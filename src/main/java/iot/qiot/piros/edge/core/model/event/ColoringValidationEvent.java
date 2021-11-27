package iot.qiot.piros.edge.core.model.event;

import io.quarkus.runtime.annotations.RegisterForReflection;
import iot.qiot.piros.edge.core.model.production.ColorMetrics;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@RegisterForReflection
@NoArgsConstructor
public class ColoringValidationEvent extends AbstractValidationEvent {

  private ColorMetrics data;
}
