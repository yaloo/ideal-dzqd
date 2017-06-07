package com.ideal.dzqd.data.internal;

import com.google.common.base.Joiner;
import com.ideal.dzqd.data.po.SceneChannelSale;
import com.ideal.dzqd.data.tools.MysqlTools;
import com.ideal.dzqd.data.vo.SignEvent;
import com.lmax.disruptor.EventHandler;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 保存数据库
 * Created by yaloo on 2017/5/29.
 */
public class SignEventInDBHandler implements EventHandler<SignEvent> {

  private static final Joiner JOINER = Joiner.on("\t");

  @Override
  public void onEvent(SignEvent event, long sequence, boolean endOfBatch) throws Exception {
    if (event == null)
      return;

    String table = Joiner.on('_')
        .join("tm_mkt_scene_user_res", event.getProvinceCode(), event.getCycle());
    List<SceneChannelSale> subs = event.getSceneChannelSales().stream()
        .filter(s -> s.getSubSceneId().equals(event.getSubSceneId())).collect(
            Collectors.toList());

    for (SceneChannelSale sub : subs) {
      event.setSceneTypeCode(sub.getSceneTypeCode());
      event.setSceneId(sub.getSceneId());
      event.setChannelId(sub.getChannelId());
      event.setOrderId(sub.getOrderId());
      event.setSaleId(sub.getSaleId());

      MysqlTools.saveSceneUser(event, table, event.getCycle());
    }
  }
}