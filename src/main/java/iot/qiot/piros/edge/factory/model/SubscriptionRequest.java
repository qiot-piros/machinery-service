package iot.qiot.piros.edge.factory.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionRequest {

  public String serial;
  public String name;
  public String keyStorePassword;
}
