package iot.qiot.piros.edge.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iot.qiot.piros.edge.core.model.production.ProductLine;
import iot.qiot.piros.edge.core.model.event.ProductLineChangedEvent;
import iot.qiot.piros.edge.core.model.event.SubscriptionCompletedEvent;
import iot.qiot.piros.edge.service.FacilityService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.jms.IllegalStateRuntimeException;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class LatestProductLineConsumer implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(LatestProductLineConsumer.class);

  private final ActiveMQConnectionFactory connectionFactory;
  private final FacilityService facilityService;
  private final Event<ProductLineChangedEvent> productLineChangedEvent;
  private final ExecutorService scheduler = Executors.newSingleThreadExecutor();
  private final ObjectMapper objectMapper = new ObjectMapper();

  private JMSContext jmsContext;
  private JMSConsumer jmsConsumer;

  @ConfigProperty(name = "qiot.productline.request.replyto-queue-prefix")
  String queuePrefix;

  public LatestProductLineConsumer(
      ActiveMQConnectionFactory connectionFactory,
      FacilityService facilityService,
      Event<ProductLineChangedEvent> productLineChangedEvent) {
    this.connectionFactory = connectionFactory;
    this.facilityService = facilityService;
    this.productLineChangedEvent = productLineChangedEvent;
  }

  void init(@Observes SubscriptionCompletedEvent event) {
    LOG.info("qiot.latest-product-line - Start init latest product line consumer");
    initConsumer();
    scheduler.submit(this);
    LOG.info("qiot.latest-product-line - Finished init latest product line consumer");
  }

  private void initConsumer() {
    if (jmsContext != null) {
      LOG.info("qiot.latest-product-line - Closing existing context");
      jmsContext.close();
    }
    jmsContext = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE);
    String queueName = queuePrefix + facilityService.getMachineId();
    Queue replyToQueue = jmsContext.createQueue(queueName);
    jmsConsumer = jmsContext.createConsumer(replyToQueue);
  }

  @PreDestroy
  void destroy() {
    scheduler.shutdown();
    jmsContext.close();
  }

  @Override
  public void run() {
    while (true) {
      try {
        Message message = jmsConsumer.receive();
        String messagePayload = message.getBody(String.class);
        ProductLine productLine = objectMapper.readValue(messagePayload, ProductLine.class);
        ProductLineChangedEvent event = new ProductLineChangedEvent();
        event.setProductLine(productLine);
        productLineChangedEvent.fire(event);
      } catch (JMSException | IllegalStateRuntimeException e) {
        LOG.error("qiot.latest-product-line - Messaging client error {}", e.getMessage(), e);
        initConsumer();
      } catch (JsonProcessingException e) {
        LOG.error("qiot.latest-product-line - Object mapper error {}", e.getMessage(), e);

      }
    }
  }
}