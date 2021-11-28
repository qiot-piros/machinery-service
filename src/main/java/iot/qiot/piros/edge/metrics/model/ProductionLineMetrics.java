package iot.qiot.piros.edge.metrics.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import iot.qiot.piros.edge.production.model.ProductionStage;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Data;

@Data
@RegisterForReflection
public class ProductionLineMetrics {

  private Map<ProductionStage, AtomicInteger> successItemsPerStage;
  private Map<ProductionStage, AtomicInteger> failedItemsPerStage;
  private int totalSuccessItems;
  private int totalFailedItems;

  public ProductionLineMetrics() {
    successItemsPerStage = new TreeMap<>();
    failedItemsPerStage = new TreeMap<>();
  }

  public int getTotalSuccessItems() {
    int total = 0;
    if (successItemsPerStage != null) {
      total = successItemsPerStage.values().stream()
          .map(AtomicInteger::intValue).reduce(0, Integer::sum);
    }
    return total;
  }

  public int getTotalFailedItems() {
    int total = 0;
    if (failedItemsPerStage != null) {
      total = failedItemsPerStage.values().stream()
          .map(AtomicInteger::intValue).reduce(0, Integer::sum);
    }
    return total;
  }
}
