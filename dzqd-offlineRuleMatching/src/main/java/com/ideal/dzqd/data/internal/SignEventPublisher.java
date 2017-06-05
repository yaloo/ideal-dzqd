package com.ideal.dzqd.data.internal;

import com.google.common.base.Strings;
import com.ideal.dzqd.data.vo.AnhuiEvent;
import com.ideal.dzqd.data.vo.DownloadEvent;
import com.ideal.dzqd.data.vo.HunanEvent;
import com.ideal.dzqd.data.vo.SignEvent;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 发布
 * Created by yaloo on 2017/5/29.
 */
public class SignEventPublisher  implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(SignEventPublisher.class);
  private Disruptor<SignEvent> disruptor;
  private CountDownLatch latch;
  private DownloadEvent event;

  public SignEventPublisher(CountDownLatch latch, Disruptor<SignEvent> disruptor,
      DownloadEvent event) throws SQLException {
    this.disruptor = disruptor;
    this.latch = latch;
    this.event = event;
  }

  @Override
  public void run() {
    if (Strings.isNullOrEmpty(event.getLocalPath())) {
      LOG.warn("there has no file to do !");
      return;
    }

    try {
      String cmd =
          "awk '{a[$1]=$0}END{for(i in a)print a[i]}' " + event.getLocalPath().replace(",", " ");
      Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd});
      InputStreamReader reader = new InputStreamReader(process.getInputStream());
      BufferedReader br = new BufferedReader(reader);
      String line;
      while ((line = br.readLine()) != null) {
        disruptor.publishEvent(new SignEventTranslator(line));
      }
      br.close();
      reader.close();
      process.destroy();
    } catch (IOException e) {
      e.printStackTrace();
    }
    latch.countDown();
  }

  class SignEventTranslator implements EventTranslator<SignEvent> {

    private final String line;

    public SignEventTranslator(final String line) {
      this.line = line;
    }

    @Override
    public void translateTo(SignEvent event, long sequence) {
      String[] l = line.split("\t");
      if (event instanceof HunanEvent) {
        HunanEvent e = (HunanEvent) event;
        e.setPhone(l[0]);
        e.setIptv(l[1]);
        e.setOverflow(l[2]);
        e.setBestpay(l[3]);
        e.setTable("tag_hunan");
      } else if (event instanceof AnhuiEvent) {
        AnhuiEvent e = (AnhuiEvent) event;
        e.setPhone(l[0]);
        e.setHdid(l[1]);
        e.setSaleid(l[2]);
        e.setDayid(l[3]);
        e.setTable("tag_anhui");
      }
    }
  }
}