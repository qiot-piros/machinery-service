package iot.qiot.piros.edge.validation;

import iot.qiot.piros.edge.core.model.event.AbstractValidationEvent;
import iot.qiot.piros.edge.core.model.event.ColoringValidationEvent;
import iot.qiot.piros.edge.core.model.event.PackagingValidationEvent;
import iot.qiot.piros.edge.core.model.event.PrintingValidationEvent;
import iot.qiot.piros.edge.core.model.event.WeavingValidationEvent;
import iot.qiot.piros.edge.core.model.production.ProductionItem;
import iot.qiot.piros.edge.service.FacilityService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ValidationService {

  private static final Logger LOG = LoggerFactory.getLogger(ValidationService.class);

  private final FacilityService facilityService;
  private final Event<WeavingValidationEvent> weavingValidationEvent;
  private final Event<ColoringValidationEvent> coloringValidationEvent;
  private final Event<PrintingValidationEvent> printingValidationEvent;
  private final Event<PackagingValidationEvent> packagingValidationEvent;

  public ValidationService(FacilityService facilityService,
      Event<WeavingValidationEvent> weavingValidationEvent,
      Event<ColoringValidationEvent> coloringValidationEvent,
      Event<PrintingValidationEvent> printingValidationEvent,
      Event<PackagingValidationEvent> packagingValidationEvent) {
    this.facilityService = facilityService;
    this.weavingValidationEvent = weavingValidationEvent;
    this.coloringValidationEvent = coloringValidationEvent;
    this.printingValidationEvent = printingValidationEvent;
    this.packagingValidationEvent = packagingValidationEvent;
  }

  public void validate(ProductionItem item) {
    switch (item.getStage()) {
      case WEAVING:
        WeavingValidationEvent weavingEvent = new WeavingValidationEvent();
        addValidationData(weavingEvent, item);
        weavingEvent.setData(item.getSizeMetrics());
        weavingValidationEvent.fire(weavingEvent);
        return;
      case COLORING:
        ColoringValidationEvent coloringEvent = new ColoringValidationEvent();
        addValidationData(coloringEvent, item);
        coloringEvent.setData(item.getColorMetrics());
        coloringValidationEvent.fire(coloringEvent);
        return;
      case PRINTING:
        PrintingValidationEvent printingEvent = new PrintingValidationEvent();
        addValidationData(printingEvent, item);
        printingEvent.setData(item.getPrintingMetrics());
        printingValidationEvent.fire(printingEvent);
        return;
      case PACKAGING:
        PackagingValidationEvent packagingEvent = new PackagingValidationEvent();
        addValidationData(packagingEvent, item);
        packagingEvent.setData(item.getPackagingMetrics());
        packagingValidationEvent.fire(packagingEvent);
        return;
      default:
        LOG.info("qiot.production - Unknown production stage offered: {}", item.getStage());
        throw new UnsupportedOperationException("error.unsupported");
    }
  }

  private void addValidationData(AbstractValidationEvent event, ProductionItem item) {
    event.setMachineryId(facilityService.getMachineId());
    event.setItemId(item.getId());
    event.setProductLineId(item.getProductLineId());
    event.setStage(item.getStage());
  }
}
