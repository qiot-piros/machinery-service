package iot.qiot.piros.edge.core.model.event;

import io.quarkus.runtime.annotations.RegisterForReflection;
import iot.qiot.piros.edge.core.model.production.PackagingMetrics;
import iot.qiot.piros.edge.core.model.production.SizeMetrics;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@RegisterForReflection
@NoArgsConstructor
public class PackagingValidationEvent extends AbstractValidationEvent {

  private PackagingMetrics data;
}
