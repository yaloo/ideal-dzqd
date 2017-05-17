package com.ideal.dzqd.flume.sink.httpclient;

import static com.ideal.dzqd.flume.sink.httpclient.HttpClientSinkConstants.BATCH_SIZE;
import static com.ideal.dzqd.flume.sink.httpclient.HttpClientSinkConstants.CLIENT_PREFIX;
import static com.ideal.dzqd.flume.sink.httpclient.HttpClientSinkConstants.CLIENT_TYPE;
import static com.ideal.dzqd.flume.sink.httpclient.HttpClientSinkConstants.DEFAULT_CLIENT_TYPE;
import static com.ideal.dzqd.flume.sink.httpclient.HttpClientSinkConstants.URLS;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.ideal.dzqd.flume.sink.httpclient.client.HttpClient;
import com.ideal.dzqd.flume.sink.httpclient.client.HttpClientFactory;
import java.util.Iterator;
import java.util.List;
import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.CounterGroup;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.conf.Configurable;
import org.apache.flume.instrumentation.SinkCounter;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 实现向远程服务接口传送数据
 * 并记录返回结果日志
 * Created by yaloo on 2017/5/14.
 */
public class HttpClientSink extends AbstractSink implements Configurable {

  private String clientType = DEFAULT_CLIENT_TYPE;
  private static final int defaultBatchSize = 100;
  private int batchSize = defaultBatchSize;

  private HttpClient client = null;
  private Context httpClientContext = null;
  private List<String> urls = Lists.newArrayList();
  private SinkCounter sinkCounter;
  private final CounterGroup counterGroup = new CounterGroup();

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientSink.class);

  @Override
  public void configure(Context context) {

    if (!Strings.isNullOrEmpty(context.getString(URLS))) {
      Iterator<String> it = Splitter.on(",").omitEmptyStrings().trimResults()
          .split(context.getString(URLS)).iterator();
      while (it.hasNext()) {
        urls.add(it.next());
      }
    }

    Preconditions.checkState(urls.size() > 0, "Missing Param:" + URLS);

    if (!Strings.isNullOrEmpty(context.getString(BATCH_SIZE))) {
      this.batchSize = Integer.parseInt(context.getString(BATCH_SIZE));
    }

    if (!Strings.isNullOrEmpty(context.getString(CLIENT_TYPE))) {
      clientType = context.getString(CLIENT_TYPE);
    }

    httpClientContext = new Context();
    httpClientContext.putAll(context.getSubProperties(CLIENT_PREFIX));

    LOGGER.info("flume httpclient sink configured");
    if (sinkCounter == null) {
      sinkCounter = new SinkCounter(getName());
    }

    Preconditions.checkState(batchSize >= 1, BATCH_SIZE
        + " must be greater than 0");
  }

  /*
  private static final AtomicInteger PID = new AtomicInteger();
  private ExecutorService executorService = Executors.newFixedThreadPool(10, new ThreadFactory() {
    @Override
    public Thread newThread(Runnable r) {
      Thread thread = new Thread(r, "httpclient-exec-" + PID.getAndIncrement());
      thread.setDaemon(true);
      return thread;
    }
  });
*/
  @Override
  public synchronized void start() {
    HttpClientFactory clientFactory = new HttpClientFactory();

    LOGGER.info("Starting {}...", this);
    sinkCounter.start();
    try {
      client = clientFactory.getClient(clientType, urls.toArray(new String[]{}));
      client.configure(httpClientContext);
      sinkCounter.incrementConnectionCreatedCount();
    } catch (Exception e) {
      e.printStackTrace();
      sinkCounter.incrementConnectionFailedCount();
      if (client != null) {
        client.close();
        sinkCounter.incrementConnectionClosedCount();
      }
    }
    super.start();
  }

  @Override
  public Status process() throws EventDeliveryException {
    Status status = Status.READY;
    Transaction tx = null;
    try {
      Channel channel = getChannel();
      tx = channel.getTransaction();
      tx.begin();
      int count;

      for (count = 0; count < batchSize; ++count) {
        Event event = channel.take();
        if (event == null) {
          status = Status.BACKOFF;
          break;
        }
        client.addEvent(event);
      }

      if (count <= 0) {
        sinkCounter.incrementBatchEmptyCount();
        counterGroup.incrementAndGet("channel.underflow");
        status = Status.BACKOFF;
      } else {
        if (count < batchSize) {
          sinkCounter.incrementBatchUnderflowCount();
          status = Status.BACKOFF;
        } else {
          sinkCounter.incrementBatchCompleteCount();
        }
        sinkCounter.addToEventDrainAttemptCount(count);
        client.execute();
      }
      tx.commit();
      sinkCounter.addToEventDrainSuccessCount(count);
      counterGroup.incrementAndGet("transaction.success");
    } catch (Throwable ex) {
      try {
        tx.rollback();
        status = Status.BACKOFF;
        counterGroup.incrementAndGet("transaction.rollback");
      } catch (Exception ex2) {
        LOGGER.error("Exception in rollback. Rollback might not have been successful.", ex2);
      }
      if (ex instanceof Error || ex instanceof RuntimeException) {
        LOGGER.error("Failed to commit transaction. Transaction rolled back.",
            ex);
        Throwables.propagate(ex);
      } else {
        LOGGER.error("Failed to commit transaction. Transaction rolled back.", ex);
        throw new EventDeliveryException(
            "Failed to commit transaction. Transaction rolled back.", ex);
      }
    } finally {
      tx.close();
    }
    return status;
  }

  @Override
  public synchronized void stop() {
    LOGGER.info("Http Client sink {} stopping...", getName());
    if (client != null) {
      client.close();
    }
    sinkCounter.incrementConnectionClosedCount();
    sinkCounter.stop();
    super.stop();
  }
}