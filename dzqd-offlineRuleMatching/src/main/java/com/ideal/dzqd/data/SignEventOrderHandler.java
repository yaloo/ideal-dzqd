package com.ideal.dzqd.data;

import com.ideal.dzqd.data.vo.SignEvent;
import com.lmax.disruptor.EventHandler;

/**
 * @TODO 这步骤在前面
 * Created by yaloo on 2017/5/29.
 */
public class SignEventOrderHandler implements EventHandler<SignEvent> {

  @Override
  public void onEvent(SignEvent event, long sequence, boolean endOfBatch) throws Exception {
    System.out.println(event.getSubSceneId());
  }
}