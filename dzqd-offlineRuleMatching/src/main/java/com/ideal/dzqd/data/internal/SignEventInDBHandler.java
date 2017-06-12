package com.ideal.dzqd.data.internal;

import com.google.common.base.Joiner;
import com.ideal.dzqd.data.po.SceneChannelSale;
import com.ideal.dzqd.data.tools.MysqlTools;
import com.ideal.dzqd.data.vo.SignEvent;
import com.lmax.disruptor.EventHandler;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 保存数据库
 * Created by yaloo on 2017/5/29.
 */
public class SignEventInDBHandler implements EventHandler<SignEvent> {

  private static final Joiner JOINER = Joiner.on("\t");
  private static final ExecutorService service = Executors.newFixedThreadPool(100);
  @Override
  public void onEvent(SignEvent event, long sequence, boolean endOfBatch) throws Exception {
    if (event == null)
      return;

    if(endOfBatch)
      service.shutdown();

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
      //service.submit(new Batch(event, table));
    }


  }


  class Batch implements Runnable {

    private final String table;
    private final SignEvent event;

    public Batch(SignEvent event, String table){
      this.table = table;
      this.event = event;
    }

    @Override
    public void run() {
      try {
        MysqlTools.saveSceneUser(event, table, event.getCycle());
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }
}