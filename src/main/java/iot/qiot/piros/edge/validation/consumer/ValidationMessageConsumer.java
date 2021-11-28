package iot.qiot.piros.edge.validation.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iot.qiot.piros.edge.core.model.event.SubscriptionCompletedEvent;
import iot.qiot.piros.edge.core.model.event.ValidationFailureEvent;
import iot.qiot.piros.edge.core.model.event.ValidationSuccessEvent;
import iot.qiot.piros.edge.service.FacilityService;
import iot.qiot.piros.edge.validation.model.ValidationResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
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
public class ValidationMessageConsumer implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(ValidationMessageConsumer.class);

  private final ObjectMapper objectMapper;

  @Inject
  ActiveMQConnectionFactory connectionFactory;
  private final FacilityService facilityService;

  private final Event<ValidationSuccessEvent> successEvent;
  private final Event<ValidationFailureEvent> failureEvent;
  private final ExecutorService scheduler = Executors.newSingleThreadExecutor();
  @ConfigProperty(name = "qiot.production.validation.replyto-queue-prefix")
  String queuePrefix;
  private JMSContext jmsContext;
  private JMSConsumer jmsConsumer;

  public ValidationMessageConsumer(
      FacilityService facilityService,
      Event<ValidationSuccessEvent> successEvent,
      Event<ValidationFailureEvent> failureEvent) {
    this.facilityService = facilityService;
    this.successEvent = successEvent;
    this.failureEvent = failureEvent;

    this.objectMapper = new ObjectMapper();
  }

  void init(@Observes SubscriptionCompletedEvent event) {
    initConsumer();
    scheduler.submit(this);
  }

  @PreDestroy
  void destroy() {
    scheduler.shutdown();
    jmsContext.close();
  }

  private void initConsumer() {
    if (jmsContext != null) {
      LOG.info("qiot.latest-product-line - Closing existing context");
      jmsContext.close();
    }
    jmsContext = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE);
    String queueName = queuePrefix + "." + facilityService.getMachineId();
    Queue replyToQueue = jmsContext.createQueue(queueName);
    jmsConsumer = jmsContext.createConsumer(replyToQueue);
  }

  @Override
  public void run() {
    while (true) {
      try {
        Message message = jmsConsumer.receive();
        String messagePayload = message.getBody(String.class);
        ValidationResponse response =
            objectMapper.readValue(messagePayload, ValidationResponse.class);
        if (response.isValid()) {
          ValidationSuccessEvent event = new ValidationSuccessEvent();
          event.setProductLineId(response.getProductLineId());
          event.setItemId(response.getItemId());
          event.setStage(response.getStage());
          successEvent.fire(event);
        } else {
          ValidationFailureEvent event = new ValidationFailureEvent();
          event.setProductLineId(response.getProductLineId());
          event.setItemId(response.getItemId());
          event.setStage(response.getStage());
          failureEvent.fire(event);
        }
      } catch (JMSException | IllegalStateRuntimeException e) {
        LOG.error("qiot.validation.consumer - Messaging client error {}", e.getMessage(), e);
        initConsumer();
      } catch (JsonProcessingException e) {
        LOG.error("qiot.validation.consumer - Object mapper error {}", e.getMessage(), e);

      }
    }
  }
}