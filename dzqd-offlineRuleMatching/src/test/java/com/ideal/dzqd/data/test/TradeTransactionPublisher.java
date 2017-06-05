package com.ideal.dzqd.data.test;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Created by yaloo on 2017/5/28.
 */
public class TradeTransactionPublisher implements Runnable{
  Disruptor<TradeTransaction> disruptor;
  private CountDownLatch latch;
  private static int LOOP=1;//模拟一千万次交易的发生

  public TradeTransactionPublisher(CountDownLatch latch,Disruptor<TradeTransaction> disruptor) {
    this.disruptor=disruptor;
    this.latch=latch;
  }

  @Override
  public void run() {
    TradeTransactionEventTranslator tradeTransloator=new TradeTransactionEventTranslator();
    for(int i=0;i<LOOP;i++){
      disruptor.publishEvent(tradeTransloator);
    }
    latch.countDown();
  }

}

class TradeTransactionEventTranslator implements EventTranslator<TradeTransaction> {
  private Random random=new Random();
  @Override
  public void translateTo(TradeTransaction event, long sequence) {
    this.generateTradeTransaction(event);
  }
  private TradeTransaction generateTradeTransaction(TradeTransaction trade){
    System.out.println("i'm TradeTransactionEventTranslator");
    trade.setPrice(random.nextDouble()*9999);
    System.out.println(trade.getId());
    trade.setId("TradeTransactionEventTranslator");
    return trade;
  }
}
