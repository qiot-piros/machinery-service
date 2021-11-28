package iot.qiot.piros.edge.production;

import io.quarkus.scheduler.Scheduled;
import iot.qiot.piros.edge.core.model.event.ProductionStageCompletedEvent;
import iot.qiot.piros.edge.core.model.event.ValidationFailureEvent;
import iot.qiot.piros.edge.core.model.event.ValidationSuccessEvent;
import iot.qiot.piros.edge.core.model.production.ProductLine;
import iot.qiot.piros.edge.core.model.production.ProductionItem;
import iot.qiot.piros.edge.production.model.ProductionStage;
import iot.qiot.piros.edge.service.ProductLineService;
import iot.qiot.piros.edge.validation.ValidationService;
import java.util.Collections;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ProductionService {

  private static final Logger LOG = LoggerFactory.getLogger(ProductionService.class);

  private final ProductLineService productLineService;
  private final ValidationService validationService;

  private final Queue<ProductionItem> weavingQueue;
  private final Queue<ProductionItem> coloringQueue;
  private final Queue<ProductionItem> printingQueue;
  private final Queue<ProductionItem> packagingQueue;

  private final Map<Integer, ProductionItem> weavingValidation;
  private final Map<Integer, ProductionItem> coloringValidation;
  private final Map<Integer, ProductionItem> printingValidation;
  private final Map<Integer, ProductionItem> packagingValidation;

  private final AtomicInteger totalItems = new AtomicInteger();

  public ProductionService(
      ProductLineService productLineService,
      ValidationService validationService) {
    this.productLineService = productLineService;
    this.validationService = validationService;

    this.weavingQueue = new ConcurrentLinkedQueue<>();
    this.coloringQueue = new ConcurrentLinkedQueue<>();
    this.printingQueue = new ConcurrentLinkedQueue<>();
    this.packagingQueue = new ConcurrentLinkedQueue<>();

    this.weavingValidation = Collections.synchronizedMap(new TreeMap<>());
    this.coloringValidation = Collections.synchronizedMap(new TreeMap<>());
    this.printingValidation = Collections.synchronizedMap(new TreeMap<>());
    this.packagingValidation = Collections.synchronizedMap(new TreeMap<>());
  }


  @Scheduled(every = "30s")
  public void produce() {
    if (!productLineService.hasProductLineAvailable()) {
      LOG.warn("qiot.production - No product line available, halting production");
      return;
    }
    ProductLine productLine = productLineService.getCurrentProductLine();
    createNewItem(productLine.getId(), totalItems.incrementAndGet());
  }

  public void stageCompleted(@Observes ProductionStageCompletedEvent event) {
    ProductionItem productionItem = event.getProductionItem();
    LOG.info("qiot.production - Stage completed for item: {}", productionItem);
    addItemToValidationQueue(productionItem);
    validationService.validate(productionItem);
  }

  void onValidationSuccess(@Observes ValidationSuccessEvent event) {
    toNextStage(event.getItemId(), event.getStage());
  }

  void onValidationFailure(@Observes ValidationFailureEvent event) {
    removeItem(event.getItemId(), event.getStage());
  }

  private void createNewItem(UUID productLineId, int itemId) {
    ProductionItem productionItem = new ProductionItem();
    productionItem.setId(itemId);
    productionItem.setProductLineId(productLineId);
    productionItem.setStage(ProductionStage.WEAVING);
    weavingQueue.add(productionItem);
  }

  public ProductionItem getNextItem(ProductionStage stage) {
    switch (stage) {
      case WEAVING:
        return weavingQueue.poll();
      case COLORING:
        return coloringQueue.poll();
      case PRINTING:
        return printingQueue.poll();
      case PACKAGING:
        return packagingQueue.poll();
      default:
        LOG.info("qiot.production - Unknown production stage offered: {}", stage);
        throw new UnsupportedOperationException("error.unsupported");
    }
  }

  public ProductionItem removeItem(int itemId, ProductionStage stage) {
    switch (stage) {
      case WEAVING:
        return weavingValidation.remove(itemId);
      case COLORING:
        return coloringValidation.remove(itemId);
      case PRINTING:
        return printingValidation.remove(itemId);
      case PACKAGING:
        return packagingValidation.remove(itemId);
      default:
        LOG.info("qiot.production - Unknown production stage offered: {}", stage);
        throw new UnsupportedOperationException("error.unsupported");
    }
  }

  public void addItemToValidationQueue(ProductionItem item) {
    switch (item.getStage()) {
      case WEAVING:
        weavingValidation.put(item.getId(), item);
        break;
      case COLORING:
        coloringValidation.put(item.getId(), item);
        break;
      case PRINTING:
        printingValidation.put(item.getId(), item);
        break;
      case PACKAGING:
        packagingValidation.put(item.getId(), item);
        break;
      default:
        LOG.info("qiot.production - Unknown production stage offered: {}", item.getStage());
        throw new UnsupportedOperationException("error.unsupported");
    }
  }

  public ProductionItem toNextStage(int itemId, ProductionStage stage) {
    ProductionItem item = null;
    switch (stage) {
      case WEAVING:
        item = weavingValidation.remove(itemId);
        coloringQueue.offer(item);
        break;
      case COLORING:
        item = coloringValidation.remove(itemId);
        printingQueue.offer(item);
        break;
      case PRINTING:
        item = printingValidation.remove(itemId);
        packagingQueue.offer(item);
        break;
      case PACKAGING:
        item = packagingValidation.remove(itemId);
        break;
      default:
        LOG.info("qiot.production - Unknown production stage offered: {}", stage);
        throw new UnsupportedOperationException("error.unsupported");
    }
    return item;
  }
}
