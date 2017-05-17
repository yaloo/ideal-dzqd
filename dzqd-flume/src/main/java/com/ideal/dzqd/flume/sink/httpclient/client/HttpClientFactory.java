package com.ideal.dzqd.flume.sink.httpclient.client;

/**
 * Intrnal Http client factory. Responsible for creating instance of Http clients
 * Created by yaloo on 2017/5/16.
 */
public class HttpClientFactory {
  public static final String WangyunClient = "wangyun";

  public HttpClient getClient(String clientType, String[] hostNames)
      throws NoSuchClientTypeException {
    if(clientType.equalsIgnoreCase(WangyunClient)){
      return new WangYunClient(hostNames);
    }

    throw new NoSuchClientTypeException();
  }
}
