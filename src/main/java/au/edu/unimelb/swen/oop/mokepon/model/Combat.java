package au.edu.unimelb.swen.oop.mokepon.model;

import java.util.Random;

/**
 * Shared hit and damage rules for targeted skills.
 */
public final class Combat {
    private Combat() {
    }

    /**
     * Normal hit check, then an extra 50% miss chance while the user is blinded.
     * Returns false when the skill misses.
     */
    public static boolean hits(Mokepon user, Mokepon target, Random random) {
        double accuracy = user.getAccuracy();
        double dodge = target.getDodge();
        double denominator = accuracy * accuracy + dodge * dodge;
        if (denominator == 0) {
            return false;
        }
        double probability = (accuracy * accuracy) / denominator;
        if (!(probability > random.nextDouble())) {
            return false;
        }
        return !user.isBlinded() || !(random.nextDouble() < 0.5);
    }

    public static int damage(Mokepon user, Mokepon target, double strengthFactor) {
        double amount = (50.0 * user.getStrength() * strengthFactor) / target.getCurrentDefence();
        int damage = (int) amount;
        if (user.isStunned()) {
            damage = damage / 2;
        }
        return damage;
    }
}
