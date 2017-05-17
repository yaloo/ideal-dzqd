package com.ideal.dzqd.flume.sink.httpclient;

/**
 * Created by yaloo on 2017/5/16.
 */
public class HttpClientSinkConstants {

  /**
   * Comma separated list of url,The Url which is connect to Remote HttpServer
   * Example:
   * <pre>
   *  http://www.baidu.com/rest,10.0.180.35:8881/rest
   * </pre>
   */
  public static final String URLS = "urls";

  /**
   * Maximum number of events the sink should take from the channel per
   * transaction, if available. Defaults to 100
   */
  public static final String BATCH_SIZE = "batchSize";


  /**
   * The client type used for sending bulks to Remote
   */
  public static final String CLIENT_TYPE = "client";


  public static final String DEFAULT_CLIENT_TYPE = "wangyun";

  private static final String CONN_URL = "conn.url";

  /**
   * The client prefix to extract the configuration that will be passed to
   * http client.
   */
  public static final String CLIENT_PREFIX = CLIENT_TYPE + ".";

}
