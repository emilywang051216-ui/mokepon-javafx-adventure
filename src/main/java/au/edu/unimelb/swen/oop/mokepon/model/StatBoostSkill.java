package au.edu.unimelb.swen.oop.mokepon.model;

import java.util.List;
import java.util.Random;

/** A skill that always hits and raises one of the user's statistics. */
abstract class StatBoostSkill extends Skill {
  private final String statName;

  StatBoostSkill(String name, String statName) {
    super(name, 3);
    this.statName = statName;
  }

  @Override
  public List<String> apply(
      Mokepon user, Mokepon target, Random random) {

    int roll = random.nextInt(10) + 1;
    int gained = applyBoost(user, roll);

    return List.of(
        "They raised their "
            + statName
            + " by "
            + gained
            + " points!");
  }

  protected abstract int applyBoost(Mokepon user, int amount);
}