package au.edu.unimelb.swen.oop.mokepon.model;

import java.util.List;
import java.util.Random;

/**
 * A heavy attack skill with higher energy cost.
 */
public class TackleSkill extends Skill {

  public TackleSkill() {
    super("Tackle", 5);
  }

  @Override
  public List<String> apply(Mokepon user, Mokepon target, Random random) {
    if (!Combat.hits(user, target, random)) {
      return List.of("But they missed.");
    }
    int damage = Combat.damage(user, target, 2);
    target.damage(damage);
    return List.of("Hit " + target.getName() + " for " + damage + " damage!");
  }
}
