package iot.qiot.piros.edge.messaging.util;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.Hashtable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.naming.InitialContext;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@RegisterForReflection(targets = {ActiveMQInitialContextFactory.class})
public class ConnectionFactory {

  private static final Logger LOG = LoggerFactory.getLogger(ConnectionFactory.class);

  private final Lock readLock;
  private final Lock writeLock;
  private final ReadWriteLock readWriteLock;

  InitialContext initialContext = null;

  @ConfigProperty(name = "amq.jndi")
  String jndi;

  private ActiveMQConnectionFactory connectionFactory;

  public ConnectionFactory() {
    this.readWriteLock = new ReentrantReadWriteLock();
    this.readLock = this.readWriteLock.readLock();
    this.writeLock = this.readWriteLock.writeLock();
  }

  @PostConstruct
  void init() {
    writeLock.lock();
    try {
      Hashtable<Object, Object> env = new Hashtable<>();
      env.put("java.naming.factory.initial",
          "org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory");
      env.put("connectionFactory.ConnectionFactory", jndi);
      initialContext = new InitialContext(env);
      connectionFactory = (ActiveMQConnectionFactory) initialContext.lookup("ConnectionFactory");
    } catch (Exception e) {
      LOG.error("qiot.amq - Error during amq init", e);
    } finally {
      writeLock.unlock();
    }
  }

  @Produces
  public ActiveMQConnectionFactory produceConnectionFactory() {
    LOG.info("qiot.amq - Producing AMQ connection factory");
    readLock.lock();
    try {
      return connectionFactory;
    } finally {
      readLock.unlock();
      LOG.info("qiot.amq - Finished producing AMQ connection factory");
    }
  }

  @PreDestroy
  void destroy() {
    writeLock.lock();
    try {
      connectionFactory.close();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      writeLock.unlock();
    }
  }
}
