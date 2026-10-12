package au.edu.unimelb.swen.oop.mokepon.model;

/**
 * Raises the user's strength by 1 to 10, capped at 100.
 */
public class SwoleSkill extends StatBoostSkill {
  public SwoleSkill() {
    super("Swole", "strength");
  }

  @Override
  protected int applyBoost(Mokepon user, int amount) {
    return user.raiseStrength(amount);
  }
}
