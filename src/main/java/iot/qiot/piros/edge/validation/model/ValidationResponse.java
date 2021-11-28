package iot.qiot.piros.edge.validation.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import iot.qiot.piros.edge.production.model.ProductionStage;
import java.util.UUID;
import lombok.Data;

@Data
@RegisterForReflection
public class ValidationResponse {

  private UUID productLineId;
  private int itemId;
  private ProductionStage stage;
  private boolean valid;
}
