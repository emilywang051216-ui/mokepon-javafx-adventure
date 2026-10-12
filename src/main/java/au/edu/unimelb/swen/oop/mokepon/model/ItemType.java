package au.edu.unimelb.swen.oop.mokepon.model;

/**
 * A consumable collected from a mystery box.
 */
public enum ItemType {
  HEALTH("Health Tonic"),
  COFFEE("Coffee"),
  POISON("Poison");

  private final String displayName;

  ItemType(String displayName) {
    this.displayName = displayName;
  }

  public String displayName() {
    return displayName;
  }
}
