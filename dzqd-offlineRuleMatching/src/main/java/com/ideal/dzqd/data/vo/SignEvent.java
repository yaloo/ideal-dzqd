package com.ideal.dzqd.data.vo;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.ideal.dzqd.data.po.SceneChannelSale;
import java.util.ArrayList;
import java.util.List;

/**
 * 最终结果数据
 * Created by yaloo on 2017/5/25.
 */
public class SignEvent {

  private static final Gson GSON = new Gson();
  private int orderId;
  private String sceneTypeCode;
  private String sceneId;
  private String subSceneId;
  private String provinceCode;
  private String channelId;
  private String saleId;
  private String phone;
  private String table;

  private String cycle;//数据周期（天）
  private List<SceneChannelSale> sceneChannelSales;
  public SignEvent() {
  }

  public SignEvent(int orderId, String sceneTypeCode, String sceneId, String subSceneId,
      String provinceCode, String channelId, String saleId) {
    this.orderId = orderId;
    this.sceneTypeCode = sceneTypeCode;
    this.sceneId = sceneId;
    this.subSceneId = subSceneId;
    this.provinceCode = provinceCode;
    this.channelId = channelId;
    this.saleId = saleId;
  }

  public int getOrderId() {
    return orderId;
  }

  public void setOrderId(int orderId) {
    this.orderId = orderId;
  }

  public String getSceneTypeCode() {
    return sceneTypeCode;
  }

  public void setSceneTypeCode(String sceneTypeCode) {
    this.sceneTypeCode = sceneTypeCode;
  }

  public String getSceneId() {
    return sceneId;
  }

  public void setSceneId(String sceneId) {
    this.sceneId = sceneId;
  }

  public String getSubSceneId() {
    return subSceneId;
  }

  public void setSubSceneId(String subSceneId) {
    this.subSceneId = subSceneId;
  }

  public String getProvinceCode() {
    return provinceCode;
  }

  public void setProvinceCode(String provinceCode) {
    this.provinceCode = provinceCode;
  }

  public String getChannelId() {
    return channelId;
  }

  public void setChannelId(String channelId) {
    this.channelId = channelId;
  }

  public String getSaleId() {
    return saleId;
  }

  public void setSaleId(String saleId) {
    this.saleId = saleId;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getTable() {
    return table;
  }

  public void setTable(String table) {
    this.table = table;
  }

  public String getCycle() {
    return cycle;
  }

  public void setCycle(String cycle) {
    this.cycle = cycle;
  }

  public List<SceneChannelSale> getSceneChannelSales() {
    return sceneChannelSales;
  }

  public void setSceneChannelSales(
      List<SceneChannelSale> sceneChannelSales) {
    this.sceneChannelSales = sceneChannelSales;
  }

  @Override
  public String toString() {
    String[] subs = this.subSceneId.split(",");

    if(subs.length < 2)
      return GSON.toJson(this);

    List<String> lists = new ArrayList<>();
    for (String sub : subs) {
      this.subSceneId = sub;
      lists.add(GSON.toJson(this));
    }

    return Joiner.on("\n").join(lists);
  }
}