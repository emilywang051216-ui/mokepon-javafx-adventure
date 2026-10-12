package au.edu.unimelb.swen.oop.mokepon.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Mokepon creature with battle statistics and skills.
 */
public class Mokepon {
  private static final int HP_MIN = 1;
  private static final int HP_MAX = 1000;
  private static final int ENERGY_MIN = 1;
  private static final int ENERGY_MAX = 100;
  private static final int STAT_MIN = 1;
  private static final int STAT_MAX = 100;

  private final String name;
  private final String imagePath;
  private final MokeponType type;
  private final int speciesHp;
  private final int speciesEnergy;
  private final int speciesStrength;
  private final int speciesAccuracy;
  private final int speciesDefence;
  private final int speciesDodge;
  private final List<Skill> skills;

  private int maxHp;
  private int maxEnergy;
  private int currentHp;
  private int currentEnergy;
  private int currentStrength;
  private int currentAccuracy;
  private int currentDefence;
  private int currentDodge;
  private int bleedTurns;
  private Mokepon bleedAttacker;
  private int stunTurns;
  private int blindTurns;

  public Mokepon(
      String name,
      String imagePath,
      MokeponType type,
      int maxHp,
      int maxEnergy,
      int strength,
      int accuracy,
      int defence,
      int dodge,
      List<Skill> skills) {
    this.name = name;
    this.imagePath = imagePath;
    this.type = type;
    this.speciesHp = maxHp;
    this.speciesEnergy = maxEnergy;
    this.speciesStrength = strength;
    this.speciesAccuracy = accuracy;
    this.speciesDefence = defence;
    this.speciesDodge = dodge;
    this.skills = List.copyOf(skills);
    restoreSpeciesStats();
  }

  private Mokepon(Mokepon other) {
    this.name = other.name;
    this.imagePath = other.imagePath;
    this.type = other.type;
    this.speciesHp = other.speciesHp;
    this.speciesEnergy = other.speciesEnergy;
    this.speciesStrength = other.speciesStrength;
    this.speciesAccuracy = other.speciesAccuracy;
    this.speciesDefence = other.speciesDefence;
    this.speciesDodge = other.speciesDodge;
    this.skills = other.skills;
    restoreSpeciesStats();
  }

  public Mokepon copyForBattle() {
    return copyForBattle(null);
  }

  /**
   * A battle copy with terrain scaling applied for this fight only.
   */
  public Mokepon copyForBattle(Terrain terrain) {
    Mokepon copy = new Mokepon(this);
    if (terrain != null) {
      copy.applyTerrain(terrain);
    }
    return copy;
  }

  private void restoreSpeciesStats() {
    maxHp = speciesHp;
    maxEnergy = speciesEnergy;
    currentHp = speciesHp;
    currentEnergy = speciesEnergy;
    currentStrength = speciesStrength;
    currentAccuracy = speciesAccuracy;
    currentDefence = speciesDefence;
    currentDodge = speciesDodge;
    bleedTurns = 0;
    bleedAttacker = null;
    stunTurns = 0;
    blindTurns = 0;
  }

  private void applyTerrain(Terrain terrain) {
    double factor = type.battleFactor(terrain);
    if (factor == 1.0) {
      return;
    }
    maxHp = scale(speciesHp, factor, HP_MIN, HP_MAX);
    currentHp = maxHp;
    maxEnergy = scale(speciesEnergy, factor, ENERGY_MIN, ENERGY_MAX);
    currentEnergy = maxEnergy;
    currentStrength = scale(speciesStrength, factor, STAT_MIN, STAT_MAX);
    currentAccuracy = scale(speciesAccuracy, factor, STAT_MIN, STAT_MAX);
    currentDefence = scale(speciesDefence, factor, STAT_MIN, STAT_MAX);
    currentDodge = scale(speciesDodge, factor, STAT_MIN, STAT_MAX);
  }

  private static int scale(int value, double factor, int min, int max) {
    int scaled = (int) Math.round(value * factor);
    return Math.max(min, Math.min(max, scaled));
  }

  public String getName() {
    return name;
  }

  public String getImagePath() {
    return imagePath;
  }

  public MokeponType getType() {
    return type;
  }

  public int getMaxHp() {
    return maxHp;
  }

  public int getMaxEnergy() {
    return maxEnergy;
  }

  public int getStrength() {
    return currentStrength;
  }

  public int getAccuracy() {
    return currentAccuracy;
  }

  public int getBaseDefence() {
    return speciesDefence;
  }

  public int getDodge() {
    return currentDodge;
  }

  public List<Skill> getSkills() {
    return skills;
  }

  public int getCurrentHp() {
    return currentHp;
  }

  public int getCurrentEnergy() {
    return currentEnergy;
  }

  public int getCurrentDefence() {
    return currentDefence;
  }

  public boolean isFainted() {
    return currentHp <= 0;
  }

  public boolean isStunned() {
    return stunTurns > 0;
  }

  public boolean isBlinded() {
    return blindTurns > 0;
  }

  public boolean isBleeding() {
    return bleedTurns > 0;
  }

  public void setCurrentHp(int hp) {
    this.currentHp = Math.max(0, Math.min(hp, maxHp));
  }

  public void setCurrentEnergy(int energy) {
    this.currentEnergy = Math.max(0, Math.min(energy, maxEnergy));
  }

  public void applyEnergyCost(int cost) {
    setCurrentEnergy(currentEnergy - cost);
  }

  public boolean canAfford(Skill skill) {
    return currentEnergy >= skill.getEnergyCost();
  }

  public List<Skill> getAffordableSkills() {
    List<Skill> affordable = new ArrayList<>();
    for (Skill skill : skills) {
      if (canAfford(skill)) {
        affordable.add(skill);
      }
    }
    return affordable;
  }

  public void damage(int amount) {
    setCurrentHp(currentHp - amount);
  }

  public int raiseDefence(int amount) {
    int updated = Math.min(STAT_MAX, currentDefence + amount);
    int gained = updated - currentDefence;
    currentDefence = updated;
    return gained;
  }

  public int raiseStrength(int amount) {
    int updated = Math.min(STAT_MAX, currentStrength + amount);
    int gained = updated - currentStrength;
    currentStrength = updated;
    return gained;
  }

  public int raiseAccuracy(int amount) {
    int updated = Math.min(STAT_MAX, currentAccuracy + amount);
    int gained = updated - currentAccuracy;
    currentAccuracy = updated;
    return gained;
  }

  public int raiseDodge(int amount) {
    int updated = Math.min(STAT_MAX, currentDodge + amount);
    int gained = updated - currentDodge;
    currentDodge = updated;
    return gained;
  }

  public void recoverEnergy(int amount) {
    setCurrentEnergy(currentEnergy + amount);
  }

  public void applyBleed(Mokepon attacker) {
    bleedTurns = 2;
    bleedAttacker = attacker;
  }

  public void applyStun() {
    stunTurns = 3;
  }

  public void applyBlind() {
    blindTurns = 3;
  }

  /**
   * Counts down stun and blindness after this Mokepon has acted.
   */
  public void tickTurnStatus() {
    if (stunTurns > 0) {
      stunTurns--;
    }
    if (blindTurns > 0) {
      blindTurns--;
    }
  }

  /**
   * Applies one end-of-round bleed tick. The last tick reports that bleeding stopped.
   */
  public List<String> tickBleed() {
    if (bleedTurns <= 0 || bleedAttacker == null) {
      return List.of();
    }
    int amount = Combat.damage(bleedAttacker, this, 0.5);
    damage(amount);
    bleedTurns--;
    List<String> lines = new ArrayList<>();
    lines.add("Hit " + name + " for " + amount + " damage!");
    if (bleedTurns == 0) {
      lines.add("The bleeding stopped.");
      bleedAttacker = null;
    }
    return lines;
  }

  public String formatSkills() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < skills.size(); i++) {
      if (i > 0) {
        builder.append(", ");
      }
      builder.append(skills.get(i).getName());
    }
    return builder.toString();
  }
}
