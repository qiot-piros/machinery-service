/**
 *
 */
package iot.qiot.piros.edge.validation.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iot.qiot.piros.edge.core.model.event.AbstractValidationEvent;
import iot.qiot.piros.edge.production.model.ProductionStage;
import iot.qiot.piros.edge.service.FacilityService;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.DeliveryMode;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

public abstract class AbstractValidationMessageProducer {

    @Inject
    ActiveMQConnectionFactory connectionFactory;
    @Inject
    FacilityService facilityService;

    ObjectMapper objectMapper = new ObjectMapper();

    protected JMSContext jmsContext;
    protected JMSProducer jmsProducer;
    protected Queue queue;

    @ConfigProperty(name = "qiot.production.validation.replyto-queue-prefix")
    protected String queuePrefix;
    protected Queue replyToQueue;

    protected void init() {
        if (jmsContext != null) {
            getLogger().info("qiot.validation - Closing existing context");
            jmsContext.close();
        }
        jmsContext = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE);
        jmsProducer = jmsContext.createProducer();
        queue = jmsContext.createQueue(getValidationQueueName());
        String replyToQueueName = queuePrefix + "." + facilityService.getMachineId();
        replyToQueue = jmsContext.createQueue(replyToQueueName);
        jmsProducer.setJMSReplyTo(replyToQueue);
    }

    protected void doRequestValidation(@Observes AbstractValidationEvent event) {
        getLogger().info("qiot.validation - {} stage validation request received", getStage());
        try {
            String payload = objectMapper.writeValueAsString(event);
            TextMessage message = jmsContext.createTextMessage();
            message.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
            message.setText(payload);
            message.setJMSReplyTo(replyToQueue);
            jmsProducer.send(queue, message);
        } catch (JMSException e) {
            getLogger().error("qiot.validation - Messaging client error {}", e.getMessage(), e);
            init();
        } catch (JsonProcessingException e) {
            getLogger().error("qiot.validation - Object mapper error {}", e.getMessage(), e);
        }
    }

    abstract protected String getValidationQueueName();

    abstract protected Logger getLogger();

    abstract protected ProductionStage getStage();

}
