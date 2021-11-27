package iot.qiot.piros.edge.production.stages;

import iot.qiot.piros.edge.core.model.event.ProductionStageCompletedEvent;
import iot.qiot.piros.edge.core.model.production.ProductLine;
import iot.qiot.piros.edge.core.model.production.ProductionItem;
import iot.qiot.piros.edge.core.util.NumberUtil;
import iot.qiot.piros.edge.production.ProductionService;
import iot.qiot.piros.edge.production.model.ProductionStage;
import iot.qiot.piros.edge.service.ProductLineService;
import java.util.PrimitiveIterator;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractStageService {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractStageService.class);

  @Inject
  protected ProductionService productionService;
  @Inject
  protected ProductLineService productLineService;
  @Inject
  protected Event<ProductionStageCompletedEvent> event;
  private PrimitiveIterator.OfLong sleepGenerator;

  void init() {
    sleepGenerator = NumberUtil.longRandomNumberGenerator(1000, 3000);
  }

  public void handleStage() {
    ProductionItem item = productionService.getNextItem(getProductionStage());
    if (item == null) {
      LOG.info("qiot.stage - No item available for stage: {}", getProductionStage().getStageName());
      return;
    }
    sleep();
    initRandomNumberGenerators(productLineService.getProductLineById(item.getProductLineId()));
    addStageData(item);
    stageCompleted(item);
  }

  private void sleep() {
    long sleepTime = sleepGenerator.nextLong();
    try {
      LOG.info("qiot.stage - Sleeping for {}", sleepTime);
      Thread.sleep(sleepTime);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public void stageCompleted(ProductionItem item) {
    ProductionStageCompletedEvent completedEvent = new ProductionStageCompletedEvent();
    completedEvent.setProductionItem(item);
    event.fire(completedEvent);
  }

  protected abstract ProductionStage getProductionStage();

  protected abstract void addStageData(ProductionItem item);

  protected abstract void initRandomNumberGenerators(ProductLine productLine);
}
