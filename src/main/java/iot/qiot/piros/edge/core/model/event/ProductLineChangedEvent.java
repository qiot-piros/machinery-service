package iot.qiot.piros.edge.core.model.event;

import io.quarkus.runtime.annotations.RegisterForReflection;
import iot.qiot.piros.edge.core.model.production.ProductLine;
import lombok.Data;

@Data
@RegisterForReflection
public class ProductLineChangedEvent {

  private ProductLine productLine;
}
