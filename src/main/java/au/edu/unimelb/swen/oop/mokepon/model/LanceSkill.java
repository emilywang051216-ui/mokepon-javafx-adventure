package au.edu.unimelb.swen.oop.mokepon.model;

import java.util.List;
import java.util.Random;

/**
 * A targeted attack that deals half-strength damage and causes bleeding.
 */
public class LanceSkill extends Skill {

  public LanceSkill() {
    super("Lance", 4);
  }

  @Override
  public List<String> apply(Mokepon user, Mokepon target, Random random) {
    if (!Combat.hits(user, target, random)) {
      return List.of("But they missed.");
    }
    int damage = Combat.damage(user, target, 0.5);
    target.damage(damage);
    target.applyBleed(user);
    return List.of("Hit " + target.getName() + " for " + damage + " damage! They started bleeding!");
  }
}
