package iot.qiot.piros.edge.factory.client;

import iot.qiot.piros.edge.factory.model.SubscriptionRequest;
import iot.qiot.piros.edge.factory.model.SubscriptionResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "factory-api")
public interface FactoryServiceClient {

  @POST
  @Path("/machinery")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  SubscriptionResponse subscribeMachinery(SubscriptionRequest request);
}
