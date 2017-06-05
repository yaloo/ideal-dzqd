package com.ideal.dzqd.data.internal;

import com.google.inject.Singleton;
import com.ideal.dzqd.data.vo.SignEvent;
import com.lmax.disruptor.EventHandler;
import javax.inject.Inject;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * drools规则引擎匹配过滤
 * Created by yaloo on 2017/5/29.
 */
@Singleton
public class SignEventDroolHandler implements EventHandler<SignEvent> {

  @Inject
  private KieContainer kieContainer;

  @Override
  public void onEvent(SignEvent event, long sequence, boolean endOfBatch) throws Exception {
    if(event == null)
      return;

    KieSession session = kieContainer.newKieSession(event.getTable());
    session.insert(event);
    session.fireAllRules();
    session.destroy();
  }
}
