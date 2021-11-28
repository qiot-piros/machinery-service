package iot.qiot.piros.edge.productline;

import iot.qiot.piros.edge.core.model.production.ProductLine;
import iot.qiot.piros.edge.core.model.event.ProductLineChangedEvent;
import iot.qiot.piros.edge.core.model.event.SubscriptionCompletedEvent;
import iot.qiot.piros.edge.messaging.LatestProductLineRequestProducer;
import iot.qiot.piros.edge.service.FacilityService;
import iot.qiot.piros.edge.service.ProductLineService;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class ProductLineServiceImpl implements ProductLineService {

  private final LatestProductLineRequestProducer latestProductLineRequestProducer;
  private final FacilityService facilityService;

  private ProductLine currentProductLine;
  private Map<UUID, ProductLine> productLines;

  private ReadWriteLock readWriteLock;
  private final Lock readLock;
  private final Lock writeLock;

  public ProductLineServiceImpl(
      LatestProductLineRequestProducer latestProductLineRequestProducer,
      FacilityService facilityService) {
    this.latestProductLineRequestProducer = latestProductLineRequestProducer;
    this.facilityService = facilityService;

    this.readWriteLock = new ReentrantReadWriteLock();
    this.readLock = readWriteLock.readLock();
    this.writeLock = readWriteLock.writeLock();

    this.productLines = new TreeMap<>();
  }

  void init(@Observes SubscriptionCompletedEvent event) {
    latestProductLineRequestProducer.requestLatestProductLine(facilityService.getMachineId());
  }

  void handleProductLineEvent(@Observes ProductLineChangedEvent event) {
    if (event == null || event.getProductLine() == null) {
      return;
    }
    ProductLine productLine = event.getProductLine();
    currentProductLine = productLine;
    productLines.put(productLine.getId(), productLine);
  }

  @Override
  public boolean hasProductLineAvailable() {
    return currentProductLine != null;
  }

  @Override
  public boolean hasProductLineAvailable(UUID productLineId) {
    readLock.lock();
    try {
      return productLines != null && productLines.containsKey(productLineId);
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public ProductLine getCurrentProductLine() {
    return currentProductLine;
  }

  @Override
  public ProductLine getProductLineById(UUID productLineId) {
    readLock.lock();
    try {
      return productLines.get(productLineId);
    } finally {
      readLock.unlock();
    }
  }


}
