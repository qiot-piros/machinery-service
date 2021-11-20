package iot.qiot.piros.edge.factory.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "factory-api")
public interface FactoryServiceClient {

}
