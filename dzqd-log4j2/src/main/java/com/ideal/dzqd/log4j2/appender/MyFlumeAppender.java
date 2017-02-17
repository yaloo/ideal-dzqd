package com.ideal.dzqd.log4j2.appender;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.Rfc5424Layout;
import org.apache.logging.log4j.core.net.Facility;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.core.util.Integers;
import org.apache.logging.log4j.flume.appender.AbstractFlumeManager;
import org.apache.logging.log4j.flume.appender.Agent;
import org.apache.logging.log4j.flume.appender.FlumeAvroManager;
import org.apache.logging.log4j.flume.appender.FlumeEmbeddedManager;
import org.apache.logging.log4j.flume.appender.FlumeEvent;
import org.apache.logging.log4j.flume.appender.FlumeEventFactory;
import org.apache.logging.log4j.flume.appender.FlumePersistentManager;

/**
 * It can distinguish between different data sources,
 * so as to create conditions for the preservation of the classification of the log
 *
 * this class extend from {@link org.apache.logging.log4j.flume.appender.FlumeAppender}
 *
 * this package needs to be integrated into your's app, and your app log4j2 configuration like this:
 * &ltconfiguration status="off" <b>package="com.ideal.dzqd.log4j2"</b> &gt
 * &ltMyFlume name="loggers" type="Avro" <b>source="test2"</b> compress="false" &gt
 * ...
 * &lt/MyFlume&gt
 *
 *
 * the bold element must be configuration.
 *
 * the server side flume config:
 * <pre>
 * agent.sources = avrosrc
 * agent.channels = memoryChannel c2 c3
 * agent.sinks = fileSink fileSink2 fileSink3
 *
 * agent.sources.avrosrc.type = avro
 * agent.sources.avrosrc.bind = 127.0.0.1
 * agent.sources.avrosrc.port = 23007
 * agent.sources.avrosrc.channels = memoryChannel c2 c3
 *
 *
 * agent.sources.avrosrc.selector.type = multiplexing
 * agent.sources.avrosrc.selector.header = flume.client.log4j2.logger.source
 * agent.sources.avrosrc.selector.mapping.test1 = memoryChannel
 * agent.sources.avrosrc.selector.mapping.test2 = c2
 * agent.sources.avrosrc.selector.default = c3
 *
 * agent.channels.memoryChannel.type = memory
 * agent.channels.memoryChannel.keep-alive = 30
 * agent.channels.memoryChannel.capacity = 10000
 * agent.channels.memoryChannel.transactionCapacity =100
 *
 * agent.sinks.fileSink.type = file_roll
 * agent.sinks.fileSink.channel = memoryChannel
 * agent.sinks.fileSink.sink.directory = /Users/yaloo/logs/test1/
 * agent.sinks.fileSink.sink.rollInterval = 300
 * agent.sinks.fileSink.sink.serializer.appendNewline = false
 *
 *
 * agent.channels.c2.type = memory
 * agent.channels.c2.keep-alive = 30
 * agent.channels.c2.capacity = 10000
 * agent.channels.c2.transactionCapacity =100
 *
 * agent.sinks.fileSink2.type = file_roll
 * agent.sinks.fileSink2.channel = c2
 * agent.sinks.fileSink2.sink.directory = /Users/yaloo/logs/test2/
 * agent.sinks.fileSink2.sink.rollInterval = 300
 * agent.sinks.fileSink2.sink.serializer.appendNewline = false
 *
 * agent.channels.c3.type = memory
 * agent.channels.c3.keep-alive = 30
 * agent.channels.c3.capacity = 10000
 * agent.channels.c3.transactionCapacity =100
 *
 * agent.sinks.fileSink3.type = file_roll
 * agent.sinks.fileSink3.channel = c3
 * agent.sinks.fileSink3.sink.directory = /Users/yaloo/logs/test3/
 * agent.sinks.fileSink3.sink.rollInterval = 300
 * agent.sinks.fileSink3.sink.serializer.appendNewline = false
 *  </pre>
 *  @see org.apache.logging.log4j.flume.appender.FlumeAppender
 * Created by yaloo on 2017/2/16.
 */
@Plugin(name = "MyFlume", category = "Core", elementType = "appender", printObject = true)
public class MyFlumeAppender
    extends AbstractAppender implements FlumeEventFactory {

  private static final long serialVersionUID = 1L;
  private static final String[] EXCLUDED_PACKAGES = {"org.apache.flume", "org.apache.avro"};
  private static final int DEFAULT_MAX_DELAY = 60000;

  private static final int DEFAULT_LOCK_TIMEOUT_RETRY_COUNT = 5;

  private final AbstractFlumeManager manager;

  private String source;
  private final String mdcIncludes;
  private final String mdcExcludes;
  private final String mdcRequired;

  private final String eventPrefix;

  private final String mdcPrefix;

  private final boolean compressBody;

  private final FlumeEventFactory factory;

  /**
   * Which Manager will be used by the appender instance.
   */
  private enum ManagerType {
    AVRO, EMBEDDED, PERSISTENT;

    public static ManagerType getType(final String type) {
      return valueOf(type.toUpperCase(Locale.US));
    }
  }

  private MyFlumeAppender(final String name, final Filter filter,
      final Layout<? extends Serializable> layout,
      final String source,
      final boolean ignoreExceptions, final String includes, final String excludes,
      final String required, final String mdcPrefix, final String eventPrefix,
      final boolean compress, final FlumeEventFactory factory, final AbstractFlumeManager manager) {
    super(name, filter, layout, ignoreExceptions);
    this.manager = manager;
    this.mdcIncludes = includes;
    this.mdcExcludes = excludes;
    this.mdcRequired = required;
    this.eventPrefix = eventPrefix;
    this.mdcPrefix = mdcPrefix;
    this.compressBody = compress;
    this.factory = factory == null ? this : factory;
    this.source = source;
  }

  /**
   * Publish the event.
   *
   * @param event The LogEvent.
   */
  @Override
  public void append(final LogEvent event) {
    final String name = event.getLoggerName();
    if (name != null) {
      for (final String pkg : EXCLUDED_PACKAGES) {
        if (name.startsWith(pkg)) {
          return;
        }
      }
    }
    final FlumeEvent flumeEvent = factory
        .createEvent(event, mdcIncludes, mdcExcludes, mdcRequired, mdcPrefix,
            eventPrefix, compressBody);

    Map<String, String> header = flumeEvent.getHeaders();
    // 添加日志来源
    if (this.source == null || this.source.equals("")) {
      this.source = "unknown";
    }
    header.put("flume.client.log4j2.logger.source", this.source);
    flumeEvent.setHeaders(header);

    flumeEvent.setBody(getLayout().toByteArray(flumeEvent));
    manager.send(flumeEvent);
  }

  @Override
  public void stop() {
    super.stop();
    manager.release();
  }

  /**
   * Create a Flume event.
   *
   * @param event The Log4j LogEvent.
   * @param includes comma separated list of mdc elements to include.
   * @param excludes comma separated list of mdc elements to exclude.
   * @param required comma separated list of mdc elements that must be present with a value.
   * @param mdcPrefix The prefix to add to MDC key names.
   * @param eventPrefix The prefix to add to event fields.
   * @param compress If true the body will be compressed.
   * @return A Flume Event.
   */
  @Override
  public FlumeEvent createEvent(final LogEvent event, final String includes,
      final String excludes,
      final String required, final String mdcPrefix, final String eventPrefix,
      final boolean compress) {
    return new FlumeEvent(event, mdcIncludes, mdcExcludes, mdcRequired, mdcPrefix,
        eventPrefix, compressBody);
  }

  /**
   * Create a Flume Avro Appender.
   *
   * @param agents An array of Agents.
   * @param properties Properties to pass to the embedded agent.
   * @param embedded true if the embedded agent manager should be used. otherwise the Avro manager
   * will be used. <b>Note: </b><i>The embedded attribute is deprecated in favor of specifying the
   * type attribute.</i>
   * @param type Avro (default), Embedded, or Persistent.
   * @param source where is the log from
   * @param dataDir The directory where the Flume FileChannel should write its data.
   * @param connectionTimeoutMillis The amount of time in milliseconds to wait before a connection
   * times out. Minimum is 1000.
   * @param requestTimeoutMillis The amount of time in milliseconds to wait before a request times
   * out. Minimum is 1000.
   * @param agentRetries The number of times to retry an agent before failing to the next agent.
   * @param maxDelayMillis The maximum number of milliseconds to wait for a complete batch.
   * @param name The name of the Appender.
   * @param ignore If {@code "true"} (default) exceptions encountered when appending events are
   * logged; otherwise they are propagated to the caller.
   * @param excludes A comma separated list of MDC elements to exclude.
   * @param includes A comma separated list of MDC elements to include.
   * @param required A comma separated list of MDC elements that are required.
   * @param mdcPrefix The prefix to add to MDC key names.
   * @param eventPrefix The prefix to add to event key names.
   * @param compressBody If true the event body will be compressed.
   * @param batchSize Number of events to include in a batch. Defaults to 1.
   * @param lockTimeoutRetries Times to retry a lock timeout when writing to Berkeley DB.
   * @param factory The factory to use to create Flume events.
   * @param layout The layout to format the event.
   * @param filter A Filter to filter events.
   * @return A Flume Avro Appender.
   */
  @PluginFactory
  public static MyFlumeAppender createAppender(@PluginElement("Agents") Agent[] agents,
      @PluginElement("Properties") final Property[] properties,
      @PluginAttribute("embedded") final String embedded,
      @PluginAttribute("type") final String type,
      @PluginAttribute("source") final String source,
      @PluginAttribute("dataDir") final String dataDir,
      @PluginAliases("connectTimeout")
      @PluginAttribute("connectTimeoutMillis") final String connectionTimeoutMillis,
      @PluginAliases("requestTimeout")
      @PluginAttribute("requestTimeoutMillis") final String requestTimeoutMillis,
      @PluginAttribute("agentRetries") final String agentRetries,
      @PluginAliases("maxDelay") // deprecated
      @PluginAttribute("maxDelayMillis") final String maxDelayMillis,
      @PluginAttribute("name") final String name,
      @PluginAttribute("ignoreExceptions") final String ignore,
      @PluginAttribute("mdcExcludes") final String excludes,
      @PluginAttribute("mdcIncludes") final String includes,
      @PluginAttribute("mdcRequired") final String required,
      @PluginAttribute("mdcPrefix") final String mdcPrefix,
      @PluginAttribute("eventPrefix") final String eventPrefix,
      @PluginAttribute("compress") final String compressBody,
      @PluginAttribute("batchSize") final String batchSize,
      @PluginAttribute("lockTimeoutRetries") final String lockTimeoutRetries,
      @PluginElement("FlumeEventFactory") final FlumeEventFactory factory,
      @PluginElement("Layout") Layout<? extends Serializable> layout,
      @PluginElement("Filter") final Filter filter) {

    final boolean embed = embedded != null ? Boolean.parseBoolean(embedded) :
        (agents == null || agents.length == 0) && properties != null && properties.length > 0;
    final boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
    final boolean compress = Booleans.parseBoolean(compressBody, true);
    ManagerType managerType;
    if (type != null) {
      if (embed && embedded != null) {
        try {
          managerType = ManagerType.getType(type);
          LOGGER.warn("Embedded and type attributes are mutually exclusive. Using type " + type);
        } catch (final Exception ex) {
          LOGGER.warn("Embedded and type attributes are mutually exclusive and type " + type +
              " is invalid.");
          managerType = ManagerType.EMBEDDED;
        }
      } else {
        try {
          managerType = ManagerType.getType(type);
        } catch (final Exception ex) {
          LOGGER.warn("Type " + type + " is invalid.");
          managerType = ManagerType.EMBEDDED;
        }
      }
    } else if (embed) {
      managerType = ManagerType.EMBEDDED;
    } else {
      managerType = ManagerType.AVRO;
    }

    final int batchCount = Integers.parseInt(batchSize, 1);
    final int connectTimeoutMillis = Integers.parseInt(connectionTimeoutMillis, 0);
    final int reqTimeoutMillis = Integers.parseInt(requestTimeoutMillis, 0);
    final int retries = Integers.parseInt(agentRetries, 0);
    final int lockTimeoutRetryCount = Integers
        .parseInt(lockTimeoutRetries, DEFAULT_LOCK_TIMEOUT_RETRY_COUNT);
    final int delayMillis = Integers.parseInt(maxDelayMillis, DEFAULT_MAX_DELAY);

    if (layout == null) {
      final int enterpriseNumber = Rfc5424Layout.DEFAULT_ENTERPRISE_NUMBER;
      layout = Rfc5424Layout
          .createLayout(Facility.LOCAL0, null, enterpriseNumber, true,
              Rfc5424Layout.DEFAULT_MDCID,
              mdcPrefix, eventPrefix, false, null, null, null, excludes, includes, required, null,
              false, null,
              null);
    }

    if (name == null) {
      LOGGER.error("No name provided for Appender");
      return null;
    }

    AbstractFlumeManager manager;

    switch (managerType) {
      case EMBEDDED:
        manager = FlumeEmbeddedManager.getManager(name, agents, properties, batchCount, dataDir);
        break;
      case AVRO:
        if (agents == null || agents.length == 0) {
          LOGGER.debug("No agents provided, using defaults");
          agents = new Agent[]{Agent.createAgent(null, null)};
        }
        manager = FlumeAvroManager
            .getManager(name, agents, batchCount, delayMillis, retries, connectTimeoutMillis,
                reqTimeoutMillis);
        break;
      case PERSISTENT:
        if (agents == null || agents.length == 0) {
          LOGGER.debug("No agents provided, using defaults");
          agents = new Agent[]{Agent.createAgent(null, null)};
        }
        manager = FlumePersistentManager.getManager(name, agents, properties, batchCount, retries,
            connectTimeoutMillis, reqTimeoutMillis, delayMillis, lockTimeoutRetryCount, dataDir);
        break;
      default:
        LOGGER.debug("No manager type specified. Defaulting to AVRO");
        if (agents == null || agents.length == 0) {
          LOGGER.debug("No agents provided, using defaults");
          agents = new Agent[]{Agent.createAgent(null, null)};
        }
        manager = FlumeAvroManager
            .getManager(name, agents, batchCount, delayMillis, retries, connectTimeoutMillis,
                reqTimeoutMillis);
    }

    if (manager == null) {
      return null;
    }

    return new MyFlumeAppender(name, filter, layout, source, ignoreExceptions, includes,
        excludes, required, mdcPrefix, eventPrefix, compress, factory, manager);
  }
}