package com.ideal.dzqd.data;

import com.google.common.base.Joiner;
import com.ideal.dzqd.data.po.SceneChannelSale;
import com.ideal.dzqd.data.vo.SignEvent;
import com.lmax.disruptor.EventHandler;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 保存数据库
 * Created by yaloo on 2017/5/29.
 */
public class SignEventInDBHandler implements EventHandler<SignEvent>{

  private static final Joiner JOINER = Joiner.on("\t");
  @Override
  public void onEvent(SignEvent event, long sequence, boolean endOfBatch) throws Exception {
    if(event == null)
      return;

    List<SceneChannelSale> subs = event.getSceneChannelSales().stream().filter(s -> s.getSubSceneId().equals(event.getSubSceneId())).collect(
        Collectors.toList());

    for(SceneChannelSale sub : subs){
      event.setSceneTypeCode(sub.getSceneTypeCode());
      event.setSceneId(sub.getSceneId());
      event.setChannelId(sub.getChannelId());
      event.setOrderId(sub.getOrderId());
      event.setSaleId(sub.getSaleId());

      System.out.println(event);
    }

  }
}