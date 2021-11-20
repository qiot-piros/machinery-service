package iot.qiot.piros.edge.factory;

import iot.qiot.piros.edge.MachineryServiceApplication;
import javax.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class FactoryService {

  private static final Logger LOG = LoggerFactory.getLogger(MachineryServiceApplication.class);

  public void registerMachine() {
    LOG.info("qiot.machinery.factory - Registering machine");

    LOG.info("qiot.machinery.factory - Finished registering machine");
  }
}
