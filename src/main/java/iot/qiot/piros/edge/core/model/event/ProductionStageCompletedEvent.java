package iot.qiot.piros.edge.core.model.event;

import io.quarkus.runtime.annotations.RegisterForReflection;
import iot.qiot.piros.edge.core.model.production.ProductionItem;
import lombok.Data;

@Data
@RegisterForReflection
public class ProductionStageCompletedEvent {

  private ProductionItem productionItem;
}
