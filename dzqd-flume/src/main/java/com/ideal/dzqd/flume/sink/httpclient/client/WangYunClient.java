package com.ideal.dzqd.flume.sink.httpclient.client;

import static com.ideal.dzqd.flume.sink.httpclient.HttpClientSinkConstants.CLIENT_PREFIX;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.ideal.dzqd.flume.sink.httpclient.Base64;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.flume.Context;
import org.apache.flume.Event;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.Header;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicHeader;

/**
 * Wang Yun client which is responsible for sending bulks of events to
 * 网运 using Http Interface API.This is configurable, so any
 * config params required should be taken through this.
 *
 * Created by yaloo on 2017/5/16.
 */
public class WangYunClient implements HttpClient {
  private final ConcurrentLinkedQueue<WanyunData> queue = Queues.newConcurrentLinkedQueue();
  private static final Type listType = new TypeToken<ArrayList<WanyunData>>() {}.getType();
  private static final Gson gson = new Gson();
  private List<Header> headers;
  private final String[] urls;
  private WanyunData data;

  public WangYunClient(final String[] hostNames) {
    for (int i = 0; i < hostNames.length; ++i) {
      if (!hostNames[i].contains("http://") && !hostNames[i].contains("https://")) {
        hostNames[i] = "http://" + hostNames[i];
      }
    }

    this.urls = hostNames;
  }

  @Override
  public void close() {
    queue.clear();
    if(headers != null || !headers.isEmpty())
      headers.clear();
  }

  @Override
  public void addEvent(Event event) throws Exception {
    String body = new String(event.getBody());

    ArrayList<WanyunData> wanyunData = gson.fromJson(body, listType);
    for(WanyunData data : wanyunData){
      queue.offer(data);
    }
  }

  @Override
  public void execute() throws Exception {
    while (!queue.isEmpty()) {
      data = queue.peek();
      for (String url : urls) {
        Executor.newInstance().execute(
            Request.Post(url)
                .setHeaders(headers.toArray(new BasicHeader[]{}))
                .setHeader("Content-disposition", "attachment; filename=" + data.filename)
                .bodyByteArray(Base64.fromBase64(data.data))
        ).returnContent().asString();
      }
    }
  }

  @Override
  public void configure(Context context) {
    Map<String,String> map = context.getSubProperties(CLIENT_PREFIX);
    headers = Lists.newArrayList();
    Iterator<String> it = map.keySet().iterator();
    while (it.hasNext()){
      headers.add(new BasicHeader(it.next(),map.get(it.next())));
    }

    headers.add(new BasicHeader("Content-type","application/X-aims-OctetFile;charset=utf-8"));
    headers.add(new BasicHeader("EsTag","ABC09K8989KKHLJ8000HLIJUFKUJLJHU"));
  }

  class WanyunData {
    String data;
    int length;
    String filename;
  }
}
