package iot.qiot.piros.edge.facility.client;

import iot.qiot.piros.edge.facility.model.SubscriptionRequest;
import iot.qiot.piros.edge.facility.model.SubscriptionResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/v1")
@RegisterRestClient(configKey = "factory-api")
public interface FacilityServiceClient {

  @POST
  @Path("/machinery")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  SubscriptionResponse subscribeMachinery(SubscriptionRequest request);
}
