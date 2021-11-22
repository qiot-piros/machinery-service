package iot.qiot.piros.edge.factory.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionRequest {

  public String serial;
  public String name;
  public String keyStorePassword;
}
