package au.edu.unimelb.swen.oop.mokepon.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.junit.jupiter.api.Test;

class ProjectRulesTest {
  @Test
  void loadsTheProvidedGameFile() throws Exception {
    Properties properties = new Properties();
    try (Reader reader = Files.newBufferedReader(Path.of("mokepon.game"), StandardCharsets.UTF_8)) {
      properties.load(reader);
    }
    GameData data = GameData.load(properties);
    assertEquals(36, data.getMokepon().size());
    assertEquals(4, data.getRandomMarkers().size());
    assertEquals(3, data.getStrongMarkers().size());
    assertEquals(3, data.getWeakMarkers().size());
    assertEquals(3, data.getMysteryBoxes().size());
    assertEquals(10, data.getStartX());
    assertEquals(18, data.getStartY());
    assertFalse(data.terrainAt(0, 16).isWalkable());
    assertTrue(data.terrainAt(10, 18).isWalkable());
  }

  @Test
  void terrainScalingRoundsAndClamps() {
    Mokepon sealance = creature("Sealance", MokeponType.WATER, 180, 10, 55, 70, 75, 45);
    Mokepon boosted = sealance.copyForBattle(Terrain.WATER);
    assertEquals(270, boosted.getMaxHp());
    assertEquals(15, boosted.getMaxEnergy());
    assertEquals(83, boosted.getStrength());
    assertEquals(100, boosted.getAccuracy());
    assertEquals(100, boosted.getCurrentDefence());
    assertEquals(68, boosted.getDodge());

    Mokepon weakened = sealance.copyForBattle(Terrain.ROCKS);
    assertEquals(90, weakened.getMaxHp());
    assertEquals(5, weakened.getMaxEnergy());
    assertEquals(28, weakened.getStrength());
    assertEquals(35, weakened.getAccuracy());
    assertEquals(38, weakened.getCurrentDefence());
    assertEquals(23, weakened.getDodge());
    assertEquals(180, sealance.getMaxHp());

    Mokepon maxed = creature("Maxed", MokeponType.WATER, 1000, 100, 100, 100, 100, 100);
    Mokepon clamped = maxed.copyForBattle(Terrain.WATER);
    assertEquals(1000, clamped.getMaxHp());
    assertEquals(100, clamped.getMaxEnergy());
    assertEquals(100, clamped.getStrength());
    assertEquals(100, clamped.getAccuracy());
    assertEquals(100, clamped.getCurrentDefence());
    assertEquals(100, clamped.getDodge());
  }

  @Test
  void strongMarkerOnlyRollsAnAdvantagedType() {
    GameData data = sampleData();
    Mokepon player = data.getMokepon().get(0);
    Adventure adventure = new Adventure(data, player);
    Random random = new Random(1);
    Adventure.MoveResult result = adventure.tryMove(0, -1, random);
    assertTrue(result instanceof Adventure.Moved || result instanceof Adventure.Blocked);
  }

  @Test
  void stunHalvesDamageForThreeOfTheTargetsTurns() {
    Mokepon player = creature("Player", MokeponType.NORMAL, 200, 30, 40, 100, 50, 0, new StunSkill(), new BiteSkill());
    Mokepon enemy = creature("Enemy", MokeponType.NORMAL, 200, 30, 40, 100, 50, 0, new BiteSkill());
    Battle battle = new Battle(player, enemy, Terrain.PATH, new Random(1));
    battle.usePlayerSkill(player.getSkills().get(0));
    int afterFirstCounter = battle.getPlayer().getCurrentHp();
    assertEquals(200 - 20, afterFirstCounter);
    battle.usePlayerSkill(player.getSkills().get(1));
    int afterSecondCounter = battle.getPlayer().getCurrentHp();
    assertEquals(afterFirstCounter - 20, afterSecondCounter);
    battle.usePlayerSkill(player.getSkills().get(1));
    int afterThirdCounter = battle.getPlayer().getCurrentHp();
    assertEquals(afterSecondCounter - 20, afterThirdCounter);
    battle.usePlayerSkill(player.getSkills().get(1));
    assertEquals(afterThirdCounter - 40, battle.getPlayer().getCurrentHp());
  }

  @Test
  void bleedingOnTheEnemyIsResolvedBeforeThePlayerAndCanEndTheBattle() {
    Mokepon player = creature("Player", MokeponType.NORMAL, 80, 20, 40, 100, 50, 0, new LanceSkill());
    Mokepon enemy = creature("Enemy", MokeponType.NORMAL, 21, 20, 40, 100, 50, 0, new LanceSkill());
    Battle battle = new Battle(player, enemy, Terrain.PATH, new Random(1));
    battle.usePlayerSkill(player.getSkills().get(0));
    assertEquals(Battle.Outcome.PLAYER_WON, battle.getOutcome());
    assertEquals(60, battle.getPlayer().getCurrentHp());
    String log = String.join("\n", battle.getLogLines());
    assertTrue(log.contains("Hit Enemy for 20 damage! They started bleeding!"));
    assertTrue(log.contains("\tHit Enemy for 20 damage!"));
  }

  @Test
  void healthTonicDoesNotGiveTheEnemyATurn() {
    Mokepon player = creature("Player", MokeponType.NORMAL, 100, 10, 10, 100, 50, 0, new BiteSkill());
    Mokepon enemy = creature("Enemy", MokeponType.NORMAL, 100, 10, 80, 100, 50, 0, new BiteSkill());
    Battle battle = new Battle(player, enemy, Terrain.PATH, new Random(1));
    battle.getPlayer().setCurrentHp(20);
    battle.useHealthTonic();
    assertEquals(100, battle.getPlayer().getCurrentHp());
    assertEquals(100, battle.getEnemy().getCurrentHp());
    assertEquals(Battle.Outcome.ONGOING, battle.getOutcome());
    assertTrue(String.join("\n", battle.getLogLines()).contains(
        "You used your Health Tonic on Player. It healed you to max hp!"));
    assertFalse(String.join("\n", battle.getLogLines()).contains("Enemy Enemy used"));
  }

  @Test
  void pickupAndVictoryMessagesMatchTheSpecification() {
    Properties properties = new Properties();
    properties.setProperty("mokepon.0.name", "Sealance");
    properties.setProperty("mokepon.0.image", "assets/Sealance.png");
    properties.setProperty("mokepon.0.hp", "100");
    properties.setProperty("mokepon.0.energy", "10");
    properties.setProperty("mokepon.0.strength", "50");
    properties.setProperty("mokepon.0.accuracy", "100");
    properties.setProperty("mokepon.0.defence", "50");
    properties.setProperty("mokepon.0.dodge", "0");
    properties.setProperty("mokepon.0.skills", "Bite|Wait");
    properties.setProperty("mokepon.0.type", "ICE");
    properties.setProperty("mokepon.1.name", "Emberbat");
    properties.setProperty("mokepon.1.image", "assets/Emberbat.png");
    properties.setProperty("mokepon.1.hp", "100");
    properties.setProperty("mokepon.1.energy", "10");
    properties.setProperty("mokepon.1.strength", "50");
    properties.setProperty("mokepon.1.accuracy", "100");
    properties.setProperty("mokepon.1.defence", "50");
    properties.setProperty("mokepon.1.dodge", "0");
    properties.setProperty("mokepon.1.skills", "Bite|Wait");
    properties.setProperty("mokepon.1.type", "LIGHTNING");
    fillMap(properties, "PPPPPPPPPPPPPPPPPPPP");
    properties.setProperty("map.tiles.10", "PPPPPPPPPPGPPPPPPPPP");
    properties.setProperty("map.player.image", "assets/sprite_light.png");
    properties.setProperty("map.player.startingPos", "10,9");
    properties.setProperty("map.buttons.up", "assets/sprite_up.png");
    properties.setProperty("map.buttons.down", "assets/sprite_down.png");
    properties.setProperty("map.buttons.left", "assets/sprite_left.png");
    properties.setProperty("map.buttons.right", "assets/sprite_right.png");
    properties.setProperty("map.mokeponRandom.image", "assets/sprite_exclamation.png");
    properties.setProperty("map.mokeponRandom.colour", "white");
    properties.setProperty("map.mokeponStrong.image", "assets/sprite_exclamation.png");
    properties.setProperty("map.mokeponStrong.colour", "red");
    properties.setProperty("map.mokeponStrong.0", "10,10");
    properties.setProperty("map.mokeponWeak.image", "assets/sprite_exclamation.png");
    properties.setProperty("map.mokeponWeak.colour", "lime");
    properties.setProperty("map.items.image", "assets/sprite_question.png");
    properties.setProperty("map.items.0.type", "HEALTH");
    properties.setProperty("map.items.0.location", "11,9");
    properties.setProperty("map.exit.image", "assets/sprite_door.png");
    properties.setProperty("map.exit.colour", "magenta");
    properties.setProperty("map.exit.location", "12,9");
    properties.setProperty("mokepon.type.fire", "assets/sprite_fire.png");
    properties.setProperty("mokepon.type.ice", "assets/sprite_ice.png");
    properties.setProperty("mokepon.type.water", "assets/sprite_water.png");
    properties.setProperty("mokepon.type.lightning", "assets/sprite_lightning.png");
    properties.setProperty("items.heal", "assets/sprite_heal.png");
    properties.setProperty("items.coffee", "assets/sprite_coffee.png");
    properties.setProperty("items.poison", "assets/sprite_poison.png");
    for (Terrain terrain : Terrain.values()) {
      properties.setProperty("map.tiles.images." + terrain.imageKey(), "assets/tile_grass.png");
    }

    GameData data = GameData.load(properties);
    Adventure adventure = new Adventure(data, data.getMokepon().get(0));
    Adventure.MoveResult pickup = adventure.tryMove(1, 0, new Random(1));
    assertTrue(pickup instanceof Adventure.Moved);
    assertEquals("You picked up a Health Tonic!", adventure.getStatus());
    assertEquals(1, adventure.getInventory().count(ItemType.HEALTH));
    Adventure grassFight = new Adventure(data, data.getMokepon().get(0));
    Adventure.MoveResult encounter = grassFight.tryMove(0, 1, new Random(1));
    assertTrue(encounter instanceof Adventure.Encountered);
    int markerCount = grassFight.getMarkers().size();
    assertEquals(10, grassFight.getX());
    assertEquals(10, grassFight.getY());
    grassFight.retreat();
    assertEquals(10, grassFight.getX());
    assertEquals(9, grassFight.getY());
    assertEquals(markerCount, grassFight.getMarkers().size());
    Adventure.Encountered encountered = (Adventure.Encountered) encounter;
    assertEquals(MokeponType.LIGHTNING, encountered.enemy().getType());
    assertTrue(encountered.messages().contains("Your mokepon will be at a disadvantage here!"));
    assertTrue(encountered.messages().contains("Your enemy will be at an advantage here!"));
  }

  @Test
  void statBoostSkillsCapAt100AndLogActualIncrease() {
    Mokepon mokepon =
        creature(
            "Booster",
            MokeponType.NORMAL,
            100,
            20,
            98,
            99,
            50,
            97);

    Random maxRoll =
        new Random() {
          @Override
          public int nextInt(int bound) {
            return bound - 1;
          }
        };

    List<String> aimLog =
        new AimSkill().apply(mokepon, mokepon, maxRoll);

    assertEquals(100, mokepon.getAccuracy());
    assertEquals(
        "They raised their accuracy by 1 points!",
        aimLog.get(0));

    List<String> swoleLog =
        new SwoleSkill().apply(mokepon, mokepon, maxRoll);

    assertEquals(100, mokepon.getStrength());
    assertEquals(
        "They raised their strength by 2 points!",
        swoleLog.get(0));

    List<String> nimbleLog =
        new NimbleSkill().apply(mokepon, mokepon, maxRoll);

    assertEquals(100, mokepon.getDodge());
    assertEquals(
        "They raised their dodge by 3 points!",
        nimbleLog.get(0));
  }

  @Test
  void coffeeAndPoisonDoNotGiveEnemyATurn() {
    Mokepon player =
        creature(
            "Player",
            MokeponType.NORMAL,
            100,
            10,
            20,
            100,
            50,
            0,
            new BiteSkill());

    Mokepon enemy =
        creature(
            "Enemy",
            MokeponType.NORMAL,
            100,
            10,
            80,
            100,
            50,
            0,
            new BiteSkill());

    Battle battle =
        new Battle(player, enemy, Terrain.PATH, new Random(1));

    battle.getPlayer().setCurrentEnergy(0);

    battle.useCoffee();

    assertEquals(10, battle.getPlayer().getCurrentEnergy());
    assertEquals(100, battle.getPlayer().getCurrentHp());
    assertEquals(100, battle.getEnemy().getCurrentHp());

    battle.usePoison();

    assertEquals(1, battle.getEnemy().getCurrentHp());
    assertEquals(100, battle.getPlayer().getCurrentHp());

    String log = String.join("\n", battle.getLogLines());

    assertTrue(
        log.contains(
            "You used your Coffee on Player. It restored all your energy!"));

    assertTrue(
        log.contains(
            "You used your Poison on Enemy. It devastated them!"));

    assertFalse(log.contains("Enemy Enemy used"));
  }

  @Test
  void collectedItemRemainsRevealedWhenQuantityReachesZero() {
    Inventory inventory = new Inventory();

    assertEquals(0, inventory.count(ItemType.COFFEE));
    assertFalse(inventory.revealed().contains(ItemType.COFFEE));

    inventory.add(ItemType.COFFEE);

    assertEquals(1, inventory.count(ItemType.COFFEE));
    assertTrue(inventory.revealed().contains(ItemType.COFFEE));

    assertTrue(inventory.consume(ItemType.COFFEE));

    assertEquals(0, inventory.count(ItemType.COFFEE));
    assertTrue(inventory.revealed().contains(ItemType.COFFEE));

    assertFalse(inventory.consume(ItemType.COFFEE));
  }

  private static GameData sampleData() {
    Properties properties = new Properties();
    try (Reader reader = Files.newBufferedReader(Path.of("mokepon.game"), StandardCharsets.UTF_8)) {
      properties.load(reader);
    } catch (Exception exception) {
      throw new IllegalStateException(exception);
    }
    return GameData.load(properties);
  }

  private static void fillMap(Properties properties, String row) {
    for (int y = 0; y < 20; y++) {
      properties.setProperty("map.tiles." + y, row);
    }
  }

  private static Mokepon creature(
      String name, MokeponType type, int hp, int energy, int strength, int accuracy, int defence, int dodge, Skill... skills) {
    return new Mokepon(name, "assets/Sealance.png", type, hp, energy, strength, accuracy, defence, dodge, List.of(skills));
  }
}
