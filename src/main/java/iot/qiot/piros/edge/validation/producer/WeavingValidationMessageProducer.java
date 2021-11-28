package iot.qiot.piros.edge.validation.producer;

import iot.qiot.piros.edge.core.model.event.WeavingValidationEvent;
import iot.qiot.piros.edge.production.model.ProductionStage;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class WeavingValidationMessageProducer extends AbstractValidationMessageProducer {

  private static final Logger LOG = LoggerFactory.getLogger(WeavingValidationMessageProducer.class);

  @ConfigProperty(name = "qiot.production.validation.weaving.queue")
  String queueName;

  @PostConstruct
  void doInit() {
    super.init();
  }

  @PreDestroy
  void destroy() {
    jmsContext.close();
  }

  @Override
  protected String getValidationQueueName() {
    return queueName;
  }

  @Override
  protected Logger getLogger() {
    return LOG;
  }

  @Override
  protected ProductionStage getStage() {
    return ProductionStage.WEAVING;
  }

  public void doValidation(@Observes WeavingValidationEvent event) {
    super.doRequestValidation(event);
  }
}