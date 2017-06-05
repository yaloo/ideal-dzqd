package com.ideal.dzqd.data.test;

import com.lmax.disruptor.EventHandler;

/**
 * Created by yaloo on 2017/5/28.
 */
public class TradeTransactionVasConsumer implements EventHandler<TradeTransaction> {

  @Override
  public void onEvent(TradeTransaction event, long sequence,
      boolean endOfBatch) throws Exception {
    //do something....
    System.out.println("i'm TradeTransactionVasConsumer......" );
    System.out.println(event.getId());
    event.setId("TradeTransactionVasConsumer");
  }
}
