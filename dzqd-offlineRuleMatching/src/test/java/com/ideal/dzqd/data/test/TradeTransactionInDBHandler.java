package com.ideal.dzqd.data.test;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * Created by yaloo on 2017/5/29.
 */
public class TradeTransactionInDBHandler  implements EventHandler<TradeTransaction>,WorkHandler<TradeTransaction> {

  @Override
  public void onEvent(TradeTransaction event, long sequence,
      boolean endOfBatch) throws Exception {
    this.onEvent(event);
  }

  @Override
  public void onEvent(TradeTransaction event) throws Exception {
    System.out.println("i'm TradeTransactionInDBHandler......");
    System.out.println(event.getId());
    //这里做具体的消费逻辑
    event.setId("TradeTransactionInDBHandler");//简单生成下ID
  }
}