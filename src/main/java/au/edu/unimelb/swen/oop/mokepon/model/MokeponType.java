package au.edu.unimelb.swen.oop.mokepon.model;

/**
 * Elemental type of a Mokepon. Normal has no type image.
 */
public enum MokeponType {
  NORMAL,
  FIRE,
  ICE,
  WATER,
  LIGHTNING;

  /**
   * Battle stat multiplier for this type on the given terrain.
   */
  public double battleFactor(Terrain terrain) {
    return switch (this) {
      case LIGHTNING -> terrain == Terrain.GRASS ? 1.5 : terrain == Terrain.FOREST ? 0.5 : 1.0;
      case WATER -> terrain == Terrain.WATER ? 1.5 : terrain == Terrain.ROCKS ? 0.5 : 1.0;
      case FIRE -> terrain == Terrain.FOREST ? 1.5 : terrain == Terrain.WATER ? 0.5 : 1.0;
      case ICE -> terrain == Terrain.ROCKS ? 1.5 : terrain == Terrain.GRASS ? 0.5 : 1.0;
      case NORMAL -> 1.0;
    };
  }

  public boolean hasAdvantage(Terrain terrain) {
    return battleFactor(terrain) > 1.0;
  }

  public boolean hasDisadvantage(Terrain terrain) {
    return battleFactor(terrain) < 1.0;
  }
}
