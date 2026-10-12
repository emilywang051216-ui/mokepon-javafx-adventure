package au.edu.unimelb.swen.oop.mokepon.model;

/**
 * A defensive skill that raises the user's defence.
 */
public class BunkerSkill extends StatBoostSkill {

  public BunkerSkill() {
    super("Bunker", "defence");
  }

  @Override
  protected int applyBoost(Mokepon user, int amount) {
    return user.raiseDefence(amount);
  }
}