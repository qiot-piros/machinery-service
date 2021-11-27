package iot.qiot.piros.edge.production.model;

public enum ProductionStage {
  WEAVING("weaving", "coloring"),
  COLORING("coloring", "printing"),
  PRINTING("printing", "packaging"),
  PACKAGING("packaging", "");

  private final String stageName;
  private final String nextStage;

  ProductionStage(String stageName, String nextStage) {
    this.stageName = stageName;
    this.nextStage = nextStage;
  }

  public String getStageName() {
    return this.stageName;
  }

  public String getNextStage() {
    return this.nextStage;
  }
}
