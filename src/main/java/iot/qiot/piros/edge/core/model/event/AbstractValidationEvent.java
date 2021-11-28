package iot.qiot.piros.edge.core.model.event;

import io.quarkus.runtime.annotations.RegisterForReflection;
import iot.qiot.piros.edge.production.model.ProductionStage;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@RegisterForReflection
@NoArgsConstructor
public class AbstractValidationEvent {

  private String machineryId;
  private UUID productLineId;
  private int itemId;
  private ProductionStage stage;
}
