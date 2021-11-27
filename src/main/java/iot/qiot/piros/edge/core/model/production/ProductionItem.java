package iot.qiot.piros.edge.core.model.production;

import io.quarkus.runtime.annotations.RegisterForReflection;
import iot.qiot.piros.edge.production.model.ProductionStage;
import java.util.UUID;
import lombok.Data;

@Data
@RegisterForReflection
public class ProductionItem {

  private int id;
  private ProductionStage stage;
  private UUID productLineId;

  private SizeMetrics sizeMetrics;
  private ColorMetrics colorMetrics;
  private PrintingMetrics printingMetrics;
  private PackagingMetrics packagingMetrics;
}
