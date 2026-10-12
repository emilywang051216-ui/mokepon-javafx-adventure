package au.edu.unimelb.swen.oop.mokepon.model;

/**
 * One cell of the overworld map.
 */
public enum Terrain {
  BARRIER('B', "barrier"),
  FOREST('F', "forest"),
  GRASS('G', "grass"),
  OCEAN('O', "ocean"),
  PATH('P', "path"),
  ROCKS('R', "rocks"),
  WATER('W', "water");

  private final char symbol;
  private final String imageKey;

  Terrain(char symbol, String imageKey) {
    this.symbol = symbol;
    this.imageKey = imageKey;
  }

  public String imageKey() {
    return imageKey;
  }

  public boolean isWalkable() {
    return this != BARRIER && this != OCEAN;
  }

  public static Terrain fromSymbol(char symbol) {
    for (Terrain terrain : values()) {
      if (terrain.symbol == symbol) {
        return terrain;
      }
    }
    throw new IllegalArgumentException("Unknown tile: " + symbol);
  }
}
