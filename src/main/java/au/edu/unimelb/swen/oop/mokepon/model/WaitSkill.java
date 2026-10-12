package au.edu.unimelb.swen.oop.mokepon.model;

import java.util.List;
import java.util.Random;

/**
 * A recovery skill that restores energy.
 */
public class WaitSkill extends Skill {
  private static final List<String> MESSAGES =
      List.of(
          "They get a little more bored.",
          "They wonder if this is a good strategy.",
          "They think about what they will have for dinner.");

  public WaitSkill() {
    super("Wait", -2);
  }

  @Override
  public List<String> apply(Mokepon user, Mokepon target, Random random) {
    return List.of(MESSAGES.get(random.nextInt(MESSAGES.size())));
  }
}
