package au.edu.unimelb.swen.oop.mokepon.model;

/**
 * Raises the user's dodge by 1 to 10, capped at 100.
 */
public class NimbleSkill extends StatBoostSkill {
    public NimbleSkill() {
        super("Nimble", "dodge");
    }

    @Override
    protected int applyBoost(Mokepon user, int amount) {
        return user.raiseDodge(amount);
    }
}
