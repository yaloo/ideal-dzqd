package com.ideal;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Created by yaloo on 2017/2/16.
 */
public class MyFlumeAppenderTest {
  private static final Logger logger = LoggerFactory.getLogger(MyFlumeAppenderTest.class);

  public static void main(String[] args) throws InterruptedException {
    String ip = null;
    try {
      ip = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    MDC.put("ip", ip);
    MDC.put("pid", ManagementFactory.getRuntimeMXBean().getName().replaceAll("@.*", ""));
    MDC.put("appName", "com.ideal.dzqd.rocketmq");
    MDC.put("operator", "yaloo");
    MDC.put("tag", "电渠能力平台RocketMQ日志收集");


    while (true){
      logger.info("test1========test2");
    }

  }
}