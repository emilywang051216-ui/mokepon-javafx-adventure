package au.edu.unimelb.swen.oop.mokepon.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * One overworld run: position, encounters, inventory, and fights won.
 */
public class Adventure {
  /**
   * Outcome of trying to step one tile.
   */
  public sealed interface MoveResult permits Blocked, Moved, Encountered, Finished {
  }

  public record Blocked() implements MoveResult {
  }

  public record Moved() implements MoveResult {
  }

  public record Encountered(Mokepon enemy, List<String> messages) implements MoveResult {
  }

  public record Finished() implements MoveResult {
  }

  private final GameData data;
  private final Mokepon player;
  private final List<MapMarker> markers = new ArrayList<>();
  private final List<MysteryBox> boxes = new ArrayList<>();
  private final Inventory inventory = new Inventory();
  private int x;
  private int y;
  private int previousX;
  private int previousY;
  private int fightsWon;
  private String status = "";
  private MapMarker pendingMarker;
  private Mokepon pendingEnemy;

  public Adventure(GameData data, Mokepon player) {
    this.data = data;
    this.player = player;
    this.x = data.getStartX();
    this.y = data.getStartY();
    this.previousX = x;
    this.previousY = y;
    markers.addAll(data.getRandomMarkers());
    markers.addAll(data.getStrongMarkers());
    markers.addAll(data.getWeakMarkers());
    boxes.addAll(data.getMysteryBoxes());
  }

  public Mokepon getPlayer() {
    return player;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getFightsWon() {
    return fightsWon;
  }

  public String getStatus() {
    return status;
  }

  public Inventory getInventory() {
    return inventory;
  }

  public List<MapMarker> getMarkers() {
    return List.copyOf(markers);
  }

  public List<MysteryBox> getBoxes() {
    return List.copyOf(boxes);
  }

  public GameData getData() {
    return data;
  }

  public Terrain currentTerrain() {
    return data.terrainAt(x, y);
  }

  public boolean canMove(int dx, int dy) {
    int nextX = x + dx;
    int nextY = y + dy;
    if (nextX < 0 || nextY < 0 || nextX >= 20 || nextY >= 20) {
      return false;
    }
    return data.terrainAt(nextX, nextY).isWalkable();
  }

  public MoveResult tryMove(int dx, int dy, Random random) {
    if (!canMove(dx, dy)) {
      return new Blocked();
    }
    previousX = x;
    previousY = y;
    x += dx;
    y += dy;
    status = "";

    MapMarker marker = markerAt(x, y);
    if (marker != null) {
      pendingMarker = marker;
      pendingEnemy = rollEnemy(marker, data.terrainAt(x, y), random);
      return new Encountered(pendingEnemy, terrainMessages(player, pendingEnemy, data.terrainAt(x, y)));
    }

    MysteryBox box = boxAt(x, y);
    if (box != null) {
      boxes.remove(box);
      inventory.add(box.type());
      status = "You picked up a " + box.type().displayName() + "!";
    }

    if (x == data.getExitX() && y == data.getExitY()) {
      return new Finished();
    }
    return new Moved();
  }

  public void retreat() {
    x = previousX;
    y = previousY;
    pendingMarker = null;
    pendingEnemy = null;
  }

  public Mokepon getPendingEnemy() {
    return pendingEnemy;
  }

  /**
   * Removes the defeated marker, records the win, and sets the status line.
   */
  public void applyVictory() {
    if (pendingMarker != null) {
      markers.remove(pendingMarker);
    }
    fightsWon++;
    status = "You beat the enemy " + pendingEnemy.getName() + "!";
    pendingMarker = null;
    pendingEnemy = null;
  }

  private MapMarker markerAt(int column, int row) {
    for (MapMarker marker : markers) {
      if (marker.x() == column && marker.y() == row) {
        return marker;
      }
    }
    return null;
  }

  private MysteryBox boxAt(int column, int row) {
    for (MysteryBox box : boxes) {
      if (box.x() == column && box.y() == row) {
        return box;
      }
    }
    return null;
  }

  private Mokepon rollEnemy(MapMarker marker, Terrain terrain, Random random) {
    List<Mokepon> pool = new ArrayList<>();
    for (Mokepon candidate : data.getMokepon()) {
      boolean include =
          switch (marker.kind()) {
            case RANDOM -> true;
            case STRONG -> candidate.getType().hasAdvantage(terrain);
            case WEAK -> candidate.getType().hasDisadvantage(terrain);
          };
      if (include) {
        pool.add(candidate);
      }
    }
    return pool.get(random.nextInt(pool.size()));
  }

  public static List<String> terrainMessages(Mokepon player, Mokepon enemy, Terrain terrain) {
    List<String> lines = new ArrayList<>();
    addTerrainLine(lines, player, terrain, true);
    addTerrainLine(lines, enemy, terrain, false);
    return lines;
  }

  private static void addTerrainLine(
      List<String> lines, Mokepon mokepon, Terrain terrain, boolean playerSide) {
    if (mokepon.getType().hasAdvantage(terrain)) {
      lines.add(
          playerSide
              ? "Your mokepon will be at an advantage here!"
              : "Your enemy will be at an advantage here!");
    } else if (mokepon.getType().hasDisadvantage(terrain)) {
      lines.add(
          playerSide
              ? "Your mokepon will be at a disadvantage here!"
              : "Your enemy will be at a disadvantage here!");
    }
  }
}
