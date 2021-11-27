package iot.qiot.piros.edge.core.model.production;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

@Data
@RegisterForReflection
public class PrintingRanges {

  public double min = 0.0D;
  public double max = 1.0D;
}
