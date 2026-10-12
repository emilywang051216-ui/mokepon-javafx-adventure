package au.edu.unimelb.swen.oop.mokepon.model;

import java.util.List;
import java.util.Random;

/**
 * A basic attack skill with moderate energy cost.
 */
public class BiteSkill extends Skill {

  public BiteSkill() {
    super("Bite", 1);
  }

  @Override
  public List<String> apply(Mokepon user, Mokepon target, Random random) {
    if (!Combat.hits(user, target, random)) {
      return List.of("But they missed.");
    }
    int damage = Combat.damage(user, target, 1);
    target.damage(damage);
    return List.of("Hit " + target.getName() + " for " + damage + " damage!");
  }
}
