package iot.qiot.piros.edge.factory.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
