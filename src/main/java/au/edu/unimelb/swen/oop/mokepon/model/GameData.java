package au.edu.unimelb.swen.oop.mokepon.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Immutable contents of a mokepon.game file.
 */
public final class GameData {
  private final List<Mokepon> mokepon;
  private final Map<MokeponType, String> typeImages;
  private final Terrain[][] tiles;
  private final Map<Terrain, String> tileImages;
  private final String playerImage;
  private final int startX;
  private final int startY;
  private final Map<String, String> buttonImages;
  private final List<MapMarker> randomMarkers;
  private final List<MapMarker> strongMarkers;
  private final List<MapMarker> weakMarkers;
  private final Map<MarkerKind, String> markerImages;
  private final Map<MarkerKind, String> markerColours;
  private final String mysteryImage;
  private final List<MysteryBox> mysteryBoxes;
  private final String exitImage;
  private final String exitColour;
  private final int exitX;
  private final int exitY;
  private final Map<ItemType, String> itemImages;

  private GameData(
      List<Mokepon> mokepon,
      Map<MokeponType, String> typeImages,
      Terrain[][] tiles,
      Map<Terrain, String> tileImages,
      String playerImage,
      int startX,
      int startY,
      Map<String, String> buttonImages,
      List<MapMarker> randomMarkers,
      List<MapMarker> strongMarkers,
      List<MapMarker> weakMarkers,
      Map<MarkerKind, String> markerImages,
      Map<MarkerKind, String> markerColours,
      String mysteryImage,
      List<MysteryBox> mysteryBoxes,
      String exitImage,
      String exitColour,
      int exitX,
      int exitY,
      Map<ItemType, String> itemImages) {
    this.mokepon = List.copyOf(mokepon);
    this.typeImages = Map.copyOf(typeImages);
    this.tiles = tiles;
    this.tileImages = Map.copyOf(tileImages);
    this.playerImage = playerImage;
    this.startX = startX;
    this.startY = startY;
    this.buttonImages = Map.copyOf(buttonImages);
    this.randomMarkers = List.copyOf(randomMarkers);
    this.strongMarkers = List.copyOf(strongMarkers);
    this.weakMarkers = List.copyOf(weakMarkers);
    this.markerImages = Map.copyOf(markerImages);
    this.markerColours = Map.copyOf(markerColours);
    this.mysteryImage = mysteryImage;
    this.mysteryBoxes = List.copyOf(mysteryBoxes);
    this.exitImage = exitImage;
    this.exitColour = exitColour;
    this.exitX = exitX;
    this.exitY = exitY;
    this.itemImages = Map.copyOf(itemImages);
  }

  public static GameData load(Properties properties) {
    Map<MokeponType, String> typeImages = new EnumMap<>(MokeponType.class);
    typeImages.put(MokeponType.FIRE, properties.getProperty("mokepon.type.fire"));
    typeImages.put(MokeponType.ICE, properties.getProperty("mokepon.type.ice"));
    typeImages.put(MokeponType.WATER, properties.getProperty("mokepon.type.water"));
    typeImages.put(MokeponType.LIGHTNING, properties.getProperty("mokepon.type.lightning"));

    Terrain[][] tiles = new Terrain[20][20];
    for (int y = 0; y < 20; y++) {
      String row = properties.getProperty("map.tiles." + y);
      for (int x = 0; x < 20; x++) {
        tiles[y][x] = Terrain.fromSymbol(row.charAt(x));
      }
    }

    Map<Terrain, String> tileImages = new EnumMap<>(Terrain.class);
    for (Terrain terrain : Terrain.values()) {
      tileImages.put(terrain, properties.getProperty("map.tiles.images." + terrain.imageKey()));
    }

    int[] start = parsePoint(properties.getProperty("map.player.startingPos"));
    Map<String, String> buttons = new java.util.HashMap<>();
    buttons.put("up", properties.getProperty("map.buttons.up"));
    buttons.put("down", properties.getProperty("map.buttons.down"));
    buttons.put("left", properties.getProperty("map.buttons.left"));
    buttons.put("right", properties.getProperty("map.buttons.right"));

    List<MapMarker> randomMarkers = loadMarkers(properties, "map.mokeponRandom", MarkerKind.RANDOM);
    List<MapMarker> strongMarkers = loadMarkers(properties, "map.mokeponStrong", MarkerKind.STRONG);
    List<MapMarker> weakMarkers = loadMarkers(properties, "map.mokeponWeak", MarkerKind.WEAK);

    Map<MarkerKind, String> markerImages = new EnumMap<>(MarkerKind.class);
    Map<MarkerKind, String> markerColours = new EnumMap<>(MarkerKind.class);
    markerImages.put(MarkerKind.RANDOM, properties.getProperty("map.mokeponRandom.image"));
    markerImages.put(MarkerKind.STRONG, properties.getProperty("map.mokeponStrong.image"));
    markerImages.put(MarkerKind.WEAK, properties.getProperty("map.mokeponWeak.image"));
    markerColours.put(MarkerKind.RANDOM, properties.getProperty("map.mokeponRandom.colour"));
    markerColours.put(MarkerKind.STRONG, properties.getProperty("map.mokeponStrong.colour"));
    markerColours.put(MarkerKind.WEAK, properties.getProperty("map.mokeponWeak.colour"));

    List<MysteryBox> boxes = new ArrayList<>();
    int boxIndex = 0;
    while (properties.containsKey("map.items." + boxIndex + ".type")) {
      ItemType type = ItemType.valueOf(properties.getProperty("map.items." + boxIndex + ".type"));
      int[] point = parsePoint(properties.getProperty("map.items." + boxIndex + ".location"));
      boxes.add(new MysteryBox(type, point[0], point[1]));
      boxIndex++;
    }

    int[] exit = parsePoint(properties.getProperty("map.exit.location"));
    Map<ItemType, String> itemImages = new EnumMap<>(ItemType.class);
    itemImages.put(ItemType.HEALTH, properties.getProperty("items.heal"));
    itemImages.put(ItemType.COFFEE, properties.getProperty("items.coffee"));
    itemImages.put(ItemType.POISON, properties.getProperty("items.poison"));

    return new GameData(
        MokeponLoader.load(properties),
        typeImages,
        tiles,
        tileImages,
        properties.getProperty("map.player.image"),
        start[0],
        start[1],
        buttons,
        randomMarkers,
        strongMarkers,
        weakMarkers,
        markerImages,
        markerColours,
        properties.getProperty("map.items.image"),
        boxes,
        properties.getProperty("map.exit.image"),
        properties.getProperty("map.exit.colour"),
        exit[0],
        exit[1],
        itemImages);
  }

  private static List<MapMarker> loadMarkers(Properties properties, String prefix, MarkerKind kind) {
    List<MapMarker> markers = new ArrayList<>();
    int index = 0;
    while (properties.containsKey(prefix + "." + index)) {
      int[] point = parsePoint(properties.getProperty(prefix + "." + index));
      markers.add(new MapMarker(kind, point[0], point[1]));
      index++;
    }
    return markers;
  }

  private static int[] parsePoint(String value) {
    String[] parts = value.split(",");
    return new int[]{Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim())};
  }

  public List<Mokepon> getMokepon() {
    return mokepon;
  }

  public String typeImage(MokeponType type) {
    return typeImages.get(type);
  }

  public Terrain terrainAt(int x, int y) {
    return tiles[y][x];
  }

  public String tileImage(Terrain terrain) {
    return tileImages.get(terrain);
  }

  public Terrain[][] getTiles() {
    return tiles;
  }

  public String getPlayerImage() {
    return playerImage;
  }

  public int getStartX() {
    return startX;
  }

  public int getStartY() {
    return startY;
  }

  public String buttonImage(String direction) {
    return buttonImages.get(direction);
  }

  public List<MapMarker> getRandomMarkers() {
    return randomMarkers;
  }

  public List<MapMarker> getStrongMarkers() {
    return strongMarkers;
  }

  public List<MapMarker> getWeakMarkers() {
    return weakMarkers;
  }

  public String markerImage(MarkerKind kind) {
    return markerImages.get(kind);
  }

  public String markerColour(MarkerKind kind) {
    return markerColours.get(kind);
  }

  public String getMysteryImage() {
    return mysteryImage;
  }

  public List<MysteryBox> getMysteryBoxes() {
    return mysteryBoxes;
  }

  public String getExitImage() {
    return exitImage;
  }

  public String getExitColour() {
    return exitColour;
  }

  public int getExitX() {
    return exitX;
  }

  public int getExitY() {
    return exitY;
  }

  public String itemImage(ItemType type) {
    return itemImages.get(type);
  }

  public Map<ItemType, String> getItemImages() {
    return itemImages;
  }
}
