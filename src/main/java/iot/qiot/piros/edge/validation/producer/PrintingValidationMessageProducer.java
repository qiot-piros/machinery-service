package iot.qiot.piros.edge.validation.producer;

import iot.qiot.piros.edge.core.model.event.PrintingValidationEvent;
import iot.qiot.piros.edge.production.model.ProductionStage;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class PrintingValidationMessageProducer extends AbstractValidationMessageProducer {

  private static final Logger LOG = LoggerFactory.getLogger(
      PrintingValidationMessageProducer.class);

  @ConfigProperty(name = "qiot.production.validation.printing.queue")
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
    return ProductionStage.PRINTING;
  }

  public void doValidation(@Observes PrintingValidationEvent event) {
    super.doRequestValidation(event);
  }
}