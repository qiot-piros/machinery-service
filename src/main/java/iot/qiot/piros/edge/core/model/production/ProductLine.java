package iot.qiot.piros.edge.core.model.production;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.UUID;
import lombok.Data;

@Data
@RegisterForReflection
public class ProductLine {

  public UUID id;
  public SizeChartRanges sizeChart;
  public ColorRanges color;
  public PrintingRanges print;
  public PackagingRanges packaging;
}
