package iot.qiot.piros.edge.production.stages;

import io.quarkus.scheduler.Scheduled;
import iot.qiot.piros.edge.core.model.production.PackagingMetrics;
import iot.qiot.piros.edge.core.model.production.ProductLine;
import iot.qiot.piros.edge.core.model.production.ProductionItem;
import iot.qiot.piros.edge.core.util.NumberUtil;
import iot.qiot.piros.edge.production.model.ProductionStage;
import java.util.PrimitiveIterator;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PackagingStageService extends AbstractStageService {

  private PrimitiveIterator.OfDouble packagingRandomNumberGenerator;

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
    return ProductionStage.PACKAGING;
  }

  @Override
  protected void addStageData(ProductionItem item) {
    PackagingMetrics metrics = new PackagingMetrics();
    metrics.setPackaging(packagingRandomNumberGenerator.nextDouble());
    item.setPackagingMetrics(metrics);
  }

  @Override
  protected void initRandomNumberGenerators(ProductLine productLine) {
    packagingRandomNumberGenerator = NumberUtil.doubleRandomNumberGenerator(
        productLine.print.min,
        productLine.print.max);
  }
}
