package com.ideal.dzqd.data.hunan;

import com.ideal.dzqd.data.vo.HunanEvent;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by yaloo on 2017/5/27.
 */
public class HunanEventProducer {
  private final RingBuffer<HunanEvent> ringBuffer;
  public HunanEventProducer(RingBuffer<HunanEvent> ringBuffer) {
    this.ringBuffer = ringBuffer;
  }

  /**
   * onData用来发布事件，每调用一次就发布一次事件事件
   * 它的参数会通过事件传递给消费者
   *
   * @param line
   */public void onData(String[] line) {
    //可以把ringBuffer看做一个事件队列，那么next就是得到下面一个事件槽
    long sequence = ringBuffer.next();try {
      //用上面的索引取出一个空的事件用于填充
      HunanEvent event = ringBuffer.get(sequence);// for the sequence
      event.setPhone(line[0]);
      event.setIptv(line[1]);
      event.setOverflow(line[2]);
      event.setBestpay(line[3]);
    } finally {
      //发布事件
      ringBuffer.publish(sequence);
    }
  }

  public static void main(String[] args) throws InterruptedException, IOException {
    // Executor that will be used to construct new threads for consumers
    Executor executor = Executors.newCachedThreadPool();
    // The factory for the event
    HunanEventFactory factory = new HunanEventFactory();
    // Specify the size of the ring buffer, must be power of 2.
    int bufferSize = 1024;
    // Construct the Disruptor

    Disruptor<HunanEvent> disruptor = new Disruptor<HunanEvent>(factory, bufferSize, executor);
    // Connect the handler
    disruptor.handleEventsWith(new HunanEventHandler());
    // Start the Disruptor, starts all threads running
    disruptor.start();
    // Get the ring buffer from the Disruptor to be used for publishing.
    RingBuffer<HunanEvent> ringBuffer = disruptor.getRingBuffer();

    HunanEventProducer producer = new HunanEventProducer(ringBuffer);

    //ByteBuffer bb = ByteBuffer.allocate(8);
    BufferedReader reader = Files.newBufferedReader(Paths.get("/Users/yaloo/Downloads/20170522_19_tmuserlabelmon.DAT"), Charset.defaultCharset());
    String line;
    while (
        (line = reader.readLine()) != null) {
      String[] l = line.split("\t");
      producer.onData(l);
    }

    disruptor.shutdown();
  }
}