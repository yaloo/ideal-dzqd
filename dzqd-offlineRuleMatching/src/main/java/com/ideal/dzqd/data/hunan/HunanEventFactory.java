package com.ideal.dzqd.data.hunan;

import com.ideal.dzqd.data.vo.HunanEvent;
import com.lmax.disruptor.EventFactory;

/**
 * Created by yaloo on 2017/5/27.
 */
public class HunanEventFactory implements EventFactory {

  @Override
  public Object newInstance() {
    return new HunanEvent();
  }
}
