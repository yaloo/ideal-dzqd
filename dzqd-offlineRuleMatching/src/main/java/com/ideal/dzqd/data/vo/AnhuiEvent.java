package com.ideal.dzqd.data.vo;

/**
 * Created by yaloo on 2017/6/2.
 */
public class AnhuiEvent extends SignEvent{

  private String hdid;
  private String saleid;
  private String dayid;

  public AnhuiEvent() {

  }

  public AnhuiEvent( String phone, String hdid, String saleid, String dayid) {
    setPhone(phone);
    this.hdid = hdid;
    this.saleid = saleid;
    this.dayid = dayid;
  }


  public String getHdid() {
    return hdid;
  }

  public void setHdid(String hdid) {
    this.hdid = hdid;
  }

  public String getSaleid() {
    return saleid;
  }

  public void setSaleid(String saleid) {
    this.saleid = saleid;
  }

  public String getDayid() {
    return dayid;
  }

  public void setDayid(String dayid) {
    this.dayid = dayid;
  }
}
