package com.ideal.dzqd.data.vo;
/**
 * 湖南标准数据
 * Created by yaloo on 2017/5/27.
 */
public class HunanEvent extends SignEvent {

  private String iptv;
  private String overflow;
  private String bestpay;

  public HunanEvent() {

  }

  public HunanEvent(String iptv, String overflow, String bestpay, String phone) {
    this.iptv = iptv;
    this.overflow = overflow;
    this.bestpay = bestpay;
    setPhone(phone);
  }

  public String getIptv() {
    return iptv;
  }

  public void setIptv(String iptv) {
    this.iptv = iptv;
  }

  public String getOverflow() {
    return overflow;
  }

  public void setOverflow(String overflow) {
    this.overflow = overflow;
  }

  public String getBestpay() {
    return bestpay;
  }

  public void setBestpay(String bestpay) {
    this.bestpay = bestpay;
  }

}