package com.ideal.dzqd.data;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractService;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.ideal.dzqd.data.conf.AppConfig;
import com.ideal.dzqd.data.vo.SignEvent;
import com.lmax.disruptor.dsl.Disruptor;
import com.netflix.governator.guice.LifecycleInjector;
import com.netflix.governator.lifecycle.LifecycleManager;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import org.aeonbits.owner.ConfigFactory;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yaloo on 2017/5/24.
 */
public final class MatchingServer extends AbstractService {

  private static final Logger LOG = LoggerFactory.getLogger(MatchingServer.class);

  private final DownloadEvent downloadEvent;

  public MatchingServer(final DownloadEvent event) {
    this.downloadEvent = event;
  }

  private Injector injector;
  @Inject
  private LifecycleManager lifecycleManager;
  @Inject
  private EventBus eventBus;

  @Inject
  private AppConfig config;
  @Inject
  private Downloading downloading;

  protected void doStart() {
    try {
      initialize();
      notifyStarted();
    } catch (Exception e) {
      notifyFailed(e);
    }
  }

  public Injector getInjector() {
    checkStart();
    return injector;
  }

  private void checkStart() {
    Preconditions.checkState(state() == State.RUNNING, "Start first");
  }

  public void start() {
    startAsync().awaitRunning();
  }

  protected void doStop() {
    try {
      lifecycleManager.close();
      notifyStopped();
    } catch (Exception e) {
      notifyFailed(e);
    }
  }

  private void initialize() throws Exception {
    injector = LifecycleInjector.builder().withModules(new ServerModule()).build().createInjector();
    injector.injectMembers(this);
    eventBus.register(injector.getInstance(EventHandler.class));
    downloading.download(eventBus, downloadEvent);

    lifecycleManager.start();
  }

  private class ServerModule extends AbstractModule {

    @Override
    protected void configure() {

    }

    @Singleton
    @Provides
    private AppConfig getConfig() {
      return ConfigFactory.newInstance().create(AppConfig.class);
    }

    @Singleton
    @Provides
    private MatchingServer getMatchingServer() {
      return MatchingServer.this;
    }

    @Singleton
    @Provides
    private KieContainer getKContainer() {
      return KieServices.Factory.get().getKieClasspathContainer();
    }

  }

  private static class EventHandler {

    @Inject
    private AppConfig config;

    @Inject
    private SignEventDroolHandler droolHandler;
    @Inject
    private SignEventOrderHandler orderHandler;
    @Inject
    private SignEventInDBHandler inDBHandler;

    // 下载完通知事件
    @Subscribe
    public void download(DownloadEvent event) throws InterruptedException, SQLException {
      System.out.println(
          Joiner.on("\t").join("===download=====", event.getProvinceCode(), event.getLocalPath()));

      int bufferSize = 1024;
      ExecutorService executor = Executors.newFixedThreadPool(4);

      Disruptor<SignEvent> disruptor = new Disruptor<>(
          new SignEventFactory(event.getProvinceCode()), 1024, Executors.defaultThreadFactory());

      disruptor.handleEventsWith(droolHandler).then(inDBHandler);
      disruptor.start();//启动
      CountDownLatch latch = new CountDownLatch(1);
      //生产者准备
      executor.submit(new SignEventPublisher(latch, disruptor, event));
      latch.await();//等待生产者完事.
      disruptor.shutdown();
      executor.shutdown();
    }
  }
}