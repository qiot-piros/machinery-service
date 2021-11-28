package iot.qiot.piros.edge.messaging;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
public class LatestProductLineRequestProducer {

  private static final Logger LOG = LoggerFactory.getLogger(LatestProductLineRequestProducer.class);

  @Inject
  ActiveMQConnectionFactory connectionFactory;

  private JMSContext jmsContext;
  private JMSProducer jmsProducer;
  private Queue queue;

  @ConfigProperty(name = "qiot.productline.request.queue-prefix")
  String queueName;

//  public LatestProductLineRequestProducer(ActiveMQConnectionFactory connectionFactory) {
//    this.connectionFactory = connectionFactory;
//  }

  @PostConstruct
  void init() {
    LOG.info("qiot.latest-product-line - Start init latest product line request producer");
    initProducer();
    LOG.info("qiot.latest-product-line - Finished init latest product line request producer");
  }

  private void initProducer() {
    if (jmsContext != null) {
      LOG.info("qiot.latest-product-line - Closing existing context");
      jmsContext.close();
    }
    jmsContext = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE);
    jmsProducer = jmsContext.createProducer();
    queue = jmsContext.createQueue(queueName);
  }

  public void requestLatestProductLine(String machineryId) {
    try {
      String messagePayload = machineryId;
      jmsProducer.send(queue, messagePayload);
    } catch (Exception e) {
      LOG.error("GENERIC ERROR", e);
    }
  }
}
