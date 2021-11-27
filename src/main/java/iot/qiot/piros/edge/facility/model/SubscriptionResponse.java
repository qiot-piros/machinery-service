package iot.qiot.piros.edge.facility.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
public class SubscriptionResponse {

  @JsonProperty("id")
  public UUID id;
  @JsonProperty("truststore")
  public String truststore;
  @JsonProperty("keystore")
  public String keystore;
  @JsonProperty("tlsCert")
  public String tlsCert;
  @JsonProperty("tlsKey")
  public String tlsKey;
  @JsonProperty("subscribed_on")
  public Instant subscribedOn;
}
