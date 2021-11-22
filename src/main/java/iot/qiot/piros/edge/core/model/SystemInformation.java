package iot.qiot.piros.edge.core.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SystemInformation {

  private String productName;
  private String serialNumber;
}