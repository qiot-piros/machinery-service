package iot.qiot.piros.edge.production.stages;

import io.quarkus.scheduler.Scheduled;
import iot.qiot.piros.edge.core.model.production.PrintingMetrics;
import iot.qiot.piros.edge.core.model.production.ProductLine;
import iot.qiot.piros.edge.core.model.production.ProductionItem;
import iot.qiot.piros.edge.core.util.NumberUtil;
import iot.qiot.piros.edge.production.model.ProductionStage;
import java.util.PrimitiveIterator;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PrintingStageService extends AbstractStageService {

  private PrimitiveIterator.OfDouble printingRandomNumberGenerator;

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
    return ProductionStage.PRINTING;
  }

  @Override
  protected void addStageData(ProductionItem item) {
    PrintingMetrics metrics = new PrintingMetrics();
    metrics.setPrinting(printingRandomNumberGenerator.nextDouble());
    item.setPrintingMetrics(metrics);
  }

  @Override
  protected void initRandomNumberGenerators(ProductLine productLine) {
    printingRandomNumberGenerator = NumberUtil.doubleRandomNumberGenerator(
        productLine.print.min,
        productLine.print.max);
  }
}
