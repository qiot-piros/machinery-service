package iot.qiot.piros.edge.metrics.service;

import iot.qiot.piros.edge.core.model.event.ProductLineChangedEvent;
import iot.qiot.piros.edge.core.model.event.ValidationFailureEvent;
import iot.qiot.piros.edge.core.model.event.ValidationSuccessEvent;
import iot.qiot.piros.edge.metrics.model.ProductionLineMetrics;
import iot.qiot.piros.edge.production.model.ProductionStage;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class MetricsService {

  private Map<UUID, ProductionLineMetrics> metrics;

  public MetricsService() {
    this.metrics = new TreeMap<>();
  }

  public Map<UUID, ProductionLineMetrics> getProductLineMetrics() {
    return metrics;
  }

  public void addProductLine(@Observes ProductLineChangedEvent event) {
    if (event.getProductLine() != null && !metrics.containsKey(event.getProductLine().getId())) {
      metrics.put(event.getProductLine().getId(), new ProductionLineMetrics());
    }
  }

  public void addSuccess(UUID productLineId, ProductionStage stage) {
    if (!metrics.containsKey(productLineId)) {
      metrics.put(productLineId, new ProductionLineMetrics());
    }
    ProductionLineMetrics productionLineMetrics = metrics.get(productLineId);
    if (productionLineMetrics.getSuccessItemsPerStage().containsKey(stage)) {
      productionLineMetrics.getSuccessItemsPerStage().get(stage).getAndIncrement();
    } else {
      productionLineMetrics.getSuccessItemsPerStage().put(stage, new AtomicInteger(1));
    }
  }

  public void addFailure(UUID productLineId, ProductionStage stage) {
    if (!metrics.containsKey(productLineId)) {
      metrics.put(productLineId, new ProductionLineMetrics());
    }
    ProductionLineMetrics productionLineMetrics = metrics.get(productLineId);
    if (productionLineMetrics.getFailedItemsPerStage().containsKey(stage)) {
      productionLineMetrics.getFailedItemsPerStage().get(stage).getAndIncrement();
    } else {
      productionLineMetrics.getFailedItemsPerStage().put(stage, new AtomicInteger(1));
    }
  }
}
