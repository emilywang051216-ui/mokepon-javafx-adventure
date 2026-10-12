package au.edu.unimelb.swen.oop.mokepon.model;

/**
 * Raises the user's accuracy by 1 to 10, capped at 100.
 */
public class AimSkill extends StatBoostSkill {
  public AimSkill() {
    super("Aim", "accuracy");
  }

  @Override
  protected int applyBoost(Mokepon user, int amount) {
    return user.raiseAccuracy(amount);
  }
}
