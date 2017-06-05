package com.ideal.dzqd.data.internal;

import com.ideal.dzqd.data.po.SceneChannelSale;
import com.ideal.dzqd.data.tools.MysqlTools;
import com.ideal.dzqd.data.vo.AnhuiEvent;
import com.ideal.dzqd.data.vo.HunanEvent;
import com.ideal.dzqd.data.vo.SignEvent;
import com.lmax.disruptor.EventFactory;
import java.sql.SQLException;
import java.util.List;

/**
 * 创建SignEvent式厂类
 * Created by yaloo on 2017/5/30.
 */
public class SignEventFactory implements EventFactory<SignEvent> {

  private String provinceCode;
  List<SceneChannelSale> list;

  public SignEventFactory(String provinceCode) throws SQLException {
    this.provinceCode = provinceCode;
    list = MysqlTools.getSceneChannelSales(provinceCode);
  }

  @Override
  public SignEvent newInstance() {
    SignEvent event = null;
    if ("19".equals(provinceCode)) {
      event = new HunanEvent();
    } else if ("13".equals(provinceCode)) {
      event = new AnhuiEvent();
    }

    if (event != null) {
      event.setProvinceCode(provinceCode);
    }

    event.setSceneChannelSales(list);
    return event;
  }
}