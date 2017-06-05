package com.ideal.dzqd.data.po;

/**
 * Created by yaloo on 2017/6/3.
 */
public class SceneChannelSale {

  private int orderId;
  private String sceneTypeCode;
  private String sceneId;
  private String subSceneId;
  private String provinceCode;
  private String channelId;
  private String saleId;

  public SceneChannelSale() {
  }

  public SceneChannelSale(int orderId, String sceneTypeCode, String sceneId,
      String subSceneId, String provinceCode, String channelId, String saleId) {
    this.orderId = orderId;
    this.sceneTypeCode = sceneTypeCode;
    this.sceneId = sceneId;
    this.subSceneId = subSceneId;
    this.provinceCode = provinceCode;
    this.channelId = channelId;
    this.saleId = saleId;
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

  public int getOrderId() {
    return orderId;
  }

  public void setOrderId(int orderId) {
    this.orderId = orderId;
  }
}
