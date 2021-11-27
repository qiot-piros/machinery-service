package iot.qiot.piros.edge.core.model.production;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

@Data
@RegisterForReflection
public class SizeMetrics {

  public double chest;
  public double shoulder;
  public double back;
  public double waist;
  public double hip;
}
