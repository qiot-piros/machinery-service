package iot.qiot.piros.edge.core.model.event;

import io.quarkus.runtime.annotations.RegisterForReflection;
import iot.qiot.piros.edge.core.model.production.PrintingMetrics;
import iot.qiot.piros.edge.core.model.production.SizeMetrics;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@RegisterForReflection
@NoArgsConstructor
public class PrintingValidationEvent extends AbstractValidationEvent {

  private PrintingMetrics data;
}
