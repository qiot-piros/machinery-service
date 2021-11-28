package iot.qiot.piros.edge.metrics.rest;

import iot.qiot.piros.edge.metrics.model.ProductionLineMetrics;
import iot.qiot.piros.edge.metrics.service.MetricsService;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/metrics")
public class MetricsResource {

  private final MetricsService metricsService;

  public MetricsResource(MetricsService metricsService) {
    this.metricsService = metricsService;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Map<UUID, ProductionLineMetrics> getProductLineMetrics() {
    return metricsService.getProductLineMetrics();
  }
}
