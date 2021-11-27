package iot.qiot.piros.edge.production.stages;

import io.quarkus.scheduler.Scheduled;
import iot.qiot.piros.edge.core.model.production.ColorMetrics;
import iot.qiot.piros.edge.core.model.production.ProductLine;
import iot.qiot.piros.edge.core.model.production.ProductionItem;
import iot.qiot.piros.edge.core.util.NumberUtil;
import iot.qiot.piros.edge.production.model.ProductionStage;
import java.util.PrimitiveIterator;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ColoringStageService extends AbstractStageService {

  private PrimitiveIterator.OfInt redRandomNumberGenerator;
  private PrimitiveIterator.OfInt greenRandomNumberGenerator;
  private PrimitiveIterator.OfInt blueRandomNumberGenerator;

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
    return ProductionStage.COLORING;
  }

  @Override
  protected void addStageData(ProductionItem item) {
    ColorMetrics metrics = new ColorMetrics();
    metrics.setRed(redRandomNumberGenerator.nextInt());
    metrics.setGreen(greenRandomNumberGenerator.nextInt());
    metrics.setBlue(blueRandomNumberGenerator.nextInt());
    item.setColorMetrics(metrics);
  }

  @Override
  protected void initRandomNumberGenerators(ProductLine productLine) {
    redRandomNumberGenerator = NumberUtil.intRandomNumberGenerator(
        productLine.color.redMin, productLine.color.redMax);
    greenRandomNumberGenerator = NumberUtil.intRandomNumberGenerator(
        productLine.color.blueMin, productLine.color.blueMax);
    blueRandomNumberGenerator = NumberUtil.intRandomNumberGenerator(
        productLine.color.greenMin, productLine.color.greenMax);
  }
}
