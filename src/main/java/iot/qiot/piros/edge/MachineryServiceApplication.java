package iot.qiot.piros.edge;

import io.quarkus.runtime.StartupEvent;
import iot.qiot.piros.edge.factory.FactoryService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@RequiredArgsConstructor
public class MachineryServiceApplication {

  private static final Logger LOG = LoggerFactory.getLogger(MachineryServiceApplication.class);

  private final FactoryService factoryService;

  void onApplicationStart(@Observes StartupEvent event) {
    LOG.info("qiot.machinery - Starting machinery-service setup");
    factoryService.registerMachine();
    LOG.info("qiot.machinery - Finished machinery-service setup");
  }
}
