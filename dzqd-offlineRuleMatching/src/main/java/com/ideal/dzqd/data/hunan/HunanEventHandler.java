package com.ideal.dzqd.data.hunan;

import com.ideal.dzqd.data.vo.HunanEvent;
import com.lmax.disruptor.EventHandler;

/**
 * Created by yaloo on 2017/5/27.
 */
public class HunanEventHandler implements EventHandler<HunanEvent> {

  @Override
  public void onEvent(HunanEvent hunanEvent, long l, boolean b) throws Exception {
    System.out.println(hunanEvent.getPhone() + "\t" + hunanEvent.getIptv() + "\t" + hunanEvent.getOverflow() + "\t" + hunanEvent.getBestpay());
  }
}
