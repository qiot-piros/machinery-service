package iot.qiot.piros.edge.factory;

import iot.qiot.piros.edge.MachineryServiceApplication;
import iot.qiot.piros.edge.configuration.MachineryConfiguration;
import iot.qiot.piros.edge.factory.client.FactoryServiceClient;
import iot.qiot.piros.edge.factory.model.SubscriptionRequest;
import iot.qiot.piros.edge.factory.model.SubscriptionResponse;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class FactoryService {

  private static final Logger LOG = LoggerFactory.getLogger(MachineryServiceApplication.class);

  private final FactoryServiceClient factoryClient;
  private final MachineryConfiguration machineryConfiguration;

  public FactoryService(
      @RestClient FactoryServiceClient factoryClient,
      MachineryConfiguration machineryConfiguration) {
    this.factoryClient = factoryClient;
    this.machineryConfiguration = machineryConfiguration;
  }

  public void subscribeMachinery() {
    LOG.info("qiot.machinery.factory - Subscribing machinery");

    SubscriptionRequest request = buildSubscriptionRequest();
    SubscriptionResponse response = factoryClient.subscribeMachinery(request);

    LOG.info(
        "qiot.machinery.factory - Finished subscribing new machinery with id {}", response.getId());
  }

  private SubscriptionRequest buildSubscriptionRequest() {
    return SubscriptionRequest.builder()
        .serial(machineryConfiguration.serial())
        .name(machineryConfiguration.name())
        .keyStorePassword(machineryConfiguration.keyStorePassword())
        .build();
  }
}
