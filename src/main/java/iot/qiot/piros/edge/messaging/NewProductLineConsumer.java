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
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class NewProductLineConsumer implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(NewProductLineConsumer.class);

  private final ActiveMQConnectionFactory connectionFactory;
  private final FacilityService facilityService;
  private final Event<ProductLineChangedEvent> productLineChangedEvent;
  private final ExecutorService scheduler = Executors.newSingleThreadExecutor();
  private final ObjectMapper objectMapper = new ObjectMapper();

  private JMSContext jmsContext;
  private JMSConsumer jmsConsumer;
  private Topic topic;

  @ConfigProperty(name = "qiot.productline.topic.name")
  String topicName;

  public NewProductLineConsumer(
      ActiveMQConnectionFactory connectionFactory,
      FacilityService facilityService,
      Event<ProductLineChangedEvent> productLineChangedEvent) {
    this.connectionFactory = connectionFactory;
    this.facilityService = facilityService;
    this.productLineChangedEvent = productLineChangedEvent;
  }

  void init(@Observes SubscriptionCompletedEvent event) {
    LOG.info("qiot.new-product-line - Start init new product line consumer");
    initConsumer();
    scheduler.submit(this);
    LOG.info("qiot.new-product-line - Finished init new product line consumer");
  }

  private void initConsumer() {
    if (jmsContext != null) {
      LOG.info("qiot.new-product-line - Closing existing context");
      jmsContext.close();
    }
    jmsContext = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE);
    jmsContext.setClientID(facilityService.getMachineId());
    topic = jmsContext.createTopic(topicName);
    jmsConsumer = jmsContext.createDurableConsumer(topic, facilityService.getMachineId());
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
        LOG.info(messagePayload);
        ProductLine productLine = objectMapper.readValue(messagePayload, ProductLine.class);
        ProductLineChangedEvent event = new ProductLineChangedEvent();
        event.setProductLine(productLine);
        productLineChangedEvent.fire(event);
      } catch (JMSException | IllegalStateRuntimeException e) {
        LOG.error("qiot.new-product-line - Messaging client error {}", e.getMessage(), e);
        initConsumer();
      } catch (JsonProcessingException e) {
        LOG.error("qiot.new-product-line - Object mapper error {}", e.getMessage(), e);

      }
    }
  }
}
