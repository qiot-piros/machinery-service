package iot.qiot.piros.edge.service;

import iot.qiot.piros.edge.core.model.production.ProductLine;
import java.util.UUID;

public interface ProductLineService {

  boolean hasProductLineAvailable();

  boolean hasProductLineAvailable(UUID productLineId);

  ProductLine getCurrentProductLine();

  ProductLine getProductLineById(UUID productLineId);
}
