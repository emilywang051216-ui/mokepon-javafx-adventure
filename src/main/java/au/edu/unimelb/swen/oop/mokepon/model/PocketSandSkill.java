package au.edu.unimelb.swen.oop.mokepon.model;

import java.util.List;
import java.util.Random;

/**
 * A targeted skill that blinds the target for three of their turns.
 */
public class PocketSandSkill extends Skill {

  public PocketSandSkill() {
    super("Pocket Sand", 4);
  }

  @Override
  public List<String> apply(Mokepon user, Mokepon target, Random random) {
    if (!Combat.hits(user, target, random)) {
      return List.of("But they missed.");
    }
    target.applyBlind();
    return List.of(
        "Pocket sand blinded " + target.getName() + "!",
        "Their targeted skills may miss for 3 turns.");
  }
}
