package com.ideal;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.Date;
import java.util.concurrent.Executors;

/**
 * Created by yaloo on 2017/5/25.
 */
public class EventBusTest {

  public static void siginalThreadConsumer(){

    EventBus bus = new EventBus("iamzhongyong");
    SFEventListener sf = new SFEventListener();
    YTOEventListener yto = new YTOEventListener();
    bus.register(sf);
    bus.register(yto);
    SignEvent sign1 = new SignEvent("SF","比熊啊",new Date());
    bus.post(sign1);
    SignEvent sign2 = new SignEvent("YTO","你妹的",new Date());
    bus.post(sign2);
  }

  public static void multiThread(){
    EventBus bus = new AsyncEventBus(Executors.newFixedThreadPool(3));
    SFEventListener sf = new SFEventListener();
    YTOEventListener yto = new YTOEventListener();
    bus.register(sf);
    bus.register(yto);
    SignEvent sign1 = new SignEvent("SF","比熊啊",new Date());
    bus.post(sign1);
    SignEvent sign2 = new SignEvent("YTO","你妹的",new Date());
    bus.post(sign2);
  }

  public static void main(String[] args) {
    EventBusTest.siginalThreadConsumer();
    EventBusTest.multiThread();
  }

  static class YTOEventListener{
    @Subscribe
    public void consign(SignEvent signEvent){
      if(signEvent.getCompanyName().equalsIgnoreCase("YTO")){
        System.out.println("YTO。。。开始发货");
        System.out.println(signEvent.getMessage());
      }
    }

    @Subscribe
    public void delivery(SignEvent signEvent){
      if(signEvent.getCompanyName().equalsIgnoreCase("YTO")){
        System.out.println("YTO。。。开始投递");
      }
    }
  }

  static class SFEventListener {
    @Subscribe
    public void consign(SignEvent signEvent){
      if(signEvent.getCompanyName().equalsIgnoreCase("SF")){
        System.out.println("SF。。。开始发货");
        System.out.println(signEvent.getMessage());
      }
    }

    @Subscribe
    public void delivery(SignEvent signEvent){
      if(signEvent.getCompanyName().equalsIgnoreCase("SF")){
        System.out.println("SF。。。开始投递");
      }
    }
  }

  static class SignEvent {
    private String companyName;
    private String signName;
    private Date signDate;
    public SignEvent(String name,String signName, Date signDate) {
      super();
      this.companyName = name;
      this.signName = signName;
      this.signDate = signDate;
    }
    public String getMessage(){
      StringBuilder sb = new StringBuilder();
      sb.append("物流公司：").append(this.companyName);
      sb.append("签收人：").append(signName).append(",签收日期：").append(signDate);
      return sb.toString();
    }

    public String getCompanyName() {
      return companyName;
    }
  }
}
