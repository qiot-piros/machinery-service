package iot.qiot.piros.edge.facility.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
public class SubscriptionRequest {

  public String serial;
  public String name;
  public String keyStorePassword;
}
