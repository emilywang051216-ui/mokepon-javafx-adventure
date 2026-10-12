package au.edu.unimelb.swen.oop.mokepon.model;

import java.util.List;
import java.util.Random;

/**
 * A targeted skill that halves the target's damage for three of their turns.
 */
public class StunSkill extends Skill {

  public StunSkill() {
    super("Stun", 4);
  }

  @Override
  public List<String> apply(Mokepon user, Mokepon target, Random random) {
    if (!Combat.hits(user, target, random)) {
      return List.of("But they missed.");
    }
    target.applyStun();
    return List.of(target.getName() + " was stunned! Their damage is halved for 3 turns.");
  }
}
