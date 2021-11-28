package iot.qiot.piros.edge.production.model;

public enum ProductionStage {
  WEAVING("weaving"),
  COLORING("coloring"),
  PRINTING("printing"),
  PACKAGING("packaging");

  private final String stageName;

  ProductionStage(String stageName) {
    this.stageName = stageName;
  }

  public String getStageName() {
    return this.stageName;
  }
}
