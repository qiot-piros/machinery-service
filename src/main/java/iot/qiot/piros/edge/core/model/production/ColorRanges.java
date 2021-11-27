package iot.qiot.piros.edge.core.model.production;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

@Data
@RegisterForReflection
public class ColorRanges {

  public int redMin;
  public int redMax;
  public int greenMin;
  public int greenMax;
  public int blueMin;
  public int blueMax;
}
