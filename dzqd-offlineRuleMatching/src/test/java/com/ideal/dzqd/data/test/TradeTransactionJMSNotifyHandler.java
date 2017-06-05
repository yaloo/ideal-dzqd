package com.ideal.dzqd.data.test;

import com.lmax.disruptor.EventHandler;

/**
 * Created by yaloo on 2017/5/28.
 */
public class TradeTransactionJMSNotifyHandler implements EventHandler<TradeTransaction> {

  @Override
  public void onEvent(TradeTransaction event, long sequence,
      boolean endOfBatch) throws Exception {
    //do send jms message
    System.out.println("i'm TradeTransactionJMSNotifyHandler");
    System.out.println(event.getId());
    event.setId("TradeTransactionJMSNotifyHandler");
  }
}
