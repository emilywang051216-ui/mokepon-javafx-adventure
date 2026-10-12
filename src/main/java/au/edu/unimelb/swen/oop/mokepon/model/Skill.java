package au.edu.unimelb.swen.oop.mokepon.model;

import java.util.List;
import java.util.Random;

/**
 * Base class for skills that a Mokepon can use during battle.
 */
public abstract class Skill {
  private final String name;
  private final int energyCost;

  protected Skill(String name, int energyCost) {
    this.name = name;
    this.energyCost = energyCost;
  }

  public final String getName() {
    return name;
  }

  public final int getEnergyCost() {
    return energyCost;
  }

  /**
   * Applies the skill and returns indented log messages without a leading tab.
   */
  public abstract List<String> apply(
      Mokepon user, Mokepon target, Random random);
}