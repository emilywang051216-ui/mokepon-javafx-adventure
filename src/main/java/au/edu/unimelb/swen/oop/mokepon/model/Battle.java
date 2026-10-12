package au.edu.unimelb.swen.oop.mokepon.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manages turn-based battle state and skill resolution.
 */
public class Battle {
  public enum Outcome {
    ONGOING,
    PLAYER_WON,
    PLAYER_LOST
  }

  private final Mokepon player;
  private final Mokepon enemy;
  private final Random random;
  private Outcome outcome = Outcome.ONGOING;
  private final List<String> logLines = new ArrayList<>();

  public Battle(Mokepon playerTemplate, Mokepon enemyTemplate, Terrain terrain, Random random) {
    this.player = playerTemplate.copyForBattle(terrain);
    this.enemy = enemyTemplate.copyForBattle(terrain);
    this.random = random;
    logLines.add(
        "Player chose "
            + player.getName()
            + ". Began a fight against enemy "
            + enemy.getName()
            + "!");
  }

  public Mokepon getPlayer() {
    return player;
  }

  public Mokepon getEnemy() {
    return enemy;
  }

  public Outcome getOutcome() {
    return outcome;
  }

  public boolean isFinished() {
    return outcome != Outcome.ONGOING;
  }

  public List<String> getLogLines() {
    return List.copyOf(logLines);
  }

  public void usePlayerSkill(Skill skill) {
    if (isFinished() || !player.canAfford(skill)) {
      return;
    }
    executeTurn(true, skill);
  }

  public void useHealthTonic() {
    if (isFinished()) {
      return;
    }
    player.setCurrentHp(player.getMaxHp());
    logLines.add(
        "You used your Health Tonic on "
            + player.getName()
            + ". It healed you to max hp!");
  }

  public void useCoffee() {
    if (isFinished()) {
      return;
    }
    player.setCurrentEnergy(player.getMaxEnergy());
    logLines.add(
        "You used your Coffee on " + player.getName() + ". It restored all your energy!");
  }

  public void usePoison() {
    if (isFinished()) {
      return;
    }
    enemy.setCurrentHp(1);
    logLines.add("You used your Poison on " + enemy.getName() + ". It devastated them!");
  }

  public void useEnemyTurn() {
    if (isFinished()) {
      return;
    }
    List<Skill> affordable = enemy.getAffordableSkills();
    Skill skill = affordable.get(random.nextInt(affordable.size()));
    executeTurn(false, skill);
  }

  private void executeTurn(boolean isPlayer, Skill skill) {
    Mokepon user = isPlayer ? player : enemy;
    Mokepon target = isPlayer ? enemy : player;

    user.applyEnergyCost(skill.getEnergyCost());
    if (isPlayer) {
      logLines.add("");
      logLines.add("Player's " + user.getName() + " used " + skill.getName() + "!");
    } else {
      logLines.add("Enemy " + user.getName() + " used " + skill.getName() + "!");
    }

    for (String message : skill.apply(user, target, random)) {
      logLines.add("\t" + message);
    }
    user.tickTurnStatus();

    if (isPlayer && enemy.isFainted()) {
      finish(true);
      return;
    }

    if (!isPlayer && player.isFainted()) {
      finish(false);
      return;
    }

    if (isPlayer && !isFinished()) {
      useEnemyTurn();
      return;
    }

    if (!isPlayer && !isFinished()) {
      applyLingering();
    }
  }

  private void applyLingering() {
    if (enemy.isBleeding()) {
      for (String message : enemy.tickBleed()) {
        logLines.add("\t" + message);
      }
      if (enemy.isFainted()) {
        finish(true);
        return;
      }
    }
    if (player.isBleeding()) {
      for (String message : player.tickBleed()) {
        logLines.add("\t" + message);
      }
      if (player.isFainted()) {
        finish(false);
      }
    }
  }

  private void finish(boolean playerWon) {
    logLines.add("");
    logLines.add("");
    if (playerWon) {
      logLines.add("Enemy's " + enemy.getName() + " fainted!");
      logLines.add("You win");
      outcome = Outcome.PLAYER_WON;
    } else {
      logLines.add("Player's " + player.getName() + " fainted!");
      logLines.add("You lose");
      outcome = Outcome.PLAYER_LOST;
    }
  }
}
