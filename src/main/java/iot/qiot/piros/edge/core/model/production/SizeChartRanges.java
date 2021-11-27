package iot.qiot.piros.edge.core.model.production;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

@Data
@RegisterForReflection
public class SizeChartRanges {

  public double chestMin;
  public double chestMax;
  public double shoulderMin;
  public double shoulderMax;
  public double backMin;
  public double backMax;
  public double waistMin;
  public double waistMax;
  public double hipMin;
  public double hipMax;
}
