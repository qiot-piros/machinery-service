package iot.qiot.piros.edge.production;

import io.quarkus.scheduler.Scheduled;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductionService {

  @Scheduled(every = "2s")
  public void produce() {
    System.out.println("produce");
  }
}
