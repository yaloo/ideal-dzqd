package com.ideal.dzqd.flume.sink.httpclient.client;

import org.apache.flume.Event;
import org.apache.flume.conf.Configurable;

/**
 * Interface for  a remote http client which is responsible for sending bulks
 * of events to Remote Http Server
 * Created by yaloo on 2017/5/16.
 */
public interface HttpClient extends Configurable {

  /**
   * close connection to remote http server in client
   */
  void close();

  /**
   * add new event to the bulk
   * @param event
   * @throws Exception
   */
  public void addEvent(Event event) throws Exception;

  /**
   * sends data to the remote http server
   * @throws Exception
   */
  void execute() throws Exception;

}
