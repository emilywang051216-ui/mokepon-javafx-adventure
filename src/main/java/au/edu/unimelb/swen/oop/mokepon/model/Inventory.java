package au.edu.unimelb.swen.oop.mokepon.model;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Item counts collected during one overworld run.
 */
public class Inventory {
  private final Map<ItemType, Integer> counts = new EnumMap<>(ItemType.class);
  private final Set<ItemType> revealed = EnumSet.noneOf(ItemType.class);

  public Inventory() {
    for (ItemType type : ItemType.values()) {
      counts.put(type, 0);
    }
  }

  public void add(ItemType type) {
    counts.put(type, counts.get(type) + 1);
    revealed.add(type);
  }

  public int count(ItemType type) {
    return counts.get(type);
  }

  /**
   * Decreases the count by one when the player still has that item.
   */
  public boolean consume(ItemType type) {
    if (counts.get(type) <= 0) {
      return false;
    }
    counts.put(type, counts.get(type) - 1);
    return true;
  }

  public Set<ItemType> revealed() {
    return Set.copyOf(revealed);
  }
}
