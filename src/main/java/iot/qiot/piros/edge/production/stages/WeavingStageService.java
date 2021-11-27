package iot.qiot.piros.edge.production.stages;

import io.quarkus.scheduler.Scheduled;
import iot.qiot.piros.edge.core.model.production.ProductLine;
import iot.qiot.piros.edge.core.model.production.ProductionItem;
import iot.qiot.piros.edge.core.model.production.SizeMetrics;
import iot.qiot.piros.edge.core.util.NumberUtil;
import iot.qiot.piros.edge.production.model.ProductionStage;
import java.util.PrimitiveIterator;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WeavingStageService extends AbstractStageService {

  private PrimitiveIterator.OfDouble chestRandomNumberGenerator;
  private PrimitiveIterator.OfDouble shoulderRandomNumberGenerator;
  private PrimitiveIterator.OfDouble backRandomNumberGenerator;
  private PrimitiveIterator.OfDouble waistRandomNumberGenerator;
  private PrimitiveIterator.OfDouble hipRandomNumberGenerator;

  @PostConstruct
  void init() {
    super.init();
  }

  @Scheduled(every = "2s")
  public void handleStage() {
    super.handleStage();
  }

  @Override
  public ProductionStage getProductionStage() {
    return ProductionStage.WEAVING;
  }

  @Override
  protected void addStageData(ProductionItem item) {
    SizeMetrics sizeMetrics = new SizeMetrics();
    sizeMetrics.setChest(chestRandomNumberGenerator.nextDouble());
    sizeMetrics.setShoulder(shoulderRandomNumberGenerator.nextDouble());
    sizeMetrics.setBack(backRandomNumberGenerator.nextDouble());
    sizeMetrics.setWaist(waistRandomNumberGenerator.nextDouble());
    sizeMetrics.setHip(hipRandomNumberGenerator.nextDouble());
    item.setSizeMetrics(sizeMetrics);
  }

  @Override
  protected void initRandomNumberGenerators(ProductLine productLine) {
    chestRandomNumberGenerator = NumberUtil.doubleRandomNumberGenerator(
        productLine.sizeChart.chestMin, productLine.sizeChart.chestMax);
    shoulderRandomNumberGenerator = NumberUtil.doubleRandomNumberGenerator(
        productLine.sizeChart.shoulderMin, productLine.sizeChart.shoulderMax);
    backRandomNumberGenerator = NumberUtil.doubleRandomNumberGenerator(
        productLine.sizeChart.backMin, productLine.sizeChart.backMax);
    waistRandomNumberGenerator = NumberUtil.doubleRandomNumberGenerator(
        productLine.sizeChart.waistMin, productLine.sizeChart.waistMax);
    hipRandomNumberGenerator = NumberUtil.doubleRandomNumberGenerator(
        productLine.sizeChart.hipMin, productLine.sizeChart.hipMax);
  }
}
