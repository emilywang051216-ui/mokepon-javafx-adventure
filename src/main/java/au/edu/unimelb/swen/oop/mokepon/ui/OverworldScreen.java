package au.edu.unimelb.swen.oop.mokepon.ui;

import au.edu.unimelb.swen.oop.mokepon.model.Adventure;
import au.edu.unimelb.swen.oop.mokepon.model.GameData;
import au.edu.unimelb.swen.oop.mokepon.model.ItemType;
import au.edu.unimelb.swen.oop.mokepon.model.MapMarker;
import au.edu.unimelb.swen.oop.mokepon.model.Mokepon;
import au.edu.unimelb.swen.oop.mokepon.model.MysteryBox;
import au.edu.unimelb.swen.oop.mokepon.model.Terrain;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Separator;

/**
 * Tile map, player statistics, movement controls, and the status line.
 */
public class OverworldScreen extends AnchorPane {
    private static final int TILE = 32;
    private static final int MAP_SIZE = TILE * 20;

    @FunctionalInterface
    public interface MoveListener {
        void move(int dx, int dy);
    }

    private final GameData data;
    private final MoveListener onMove;
    private final Pane actors = new Pane();
    private final Label statusLabel = new Label("");
    private final Label nameLabel = statLabel("");
    private final Label hpValue = quantityLabel();
    private final Label energyValue = quantityLabel();
    private final Label strengthValue = quantityLabel();
    private final Label accuracyValue = quantityLabel();
    private final Label defenceValue = quantityLabel();
    private final Label dodgeValue = quantityLabel();
    private final Label fightsValue = quantityLabel();
    private final Label healthValue = quantityLabel();
    private final Label coffeeValue = quantityLabel();
    private final Label poisonValue = quantityLabel();
    private final ImageView upButton;
    private final ImageView downButton;
    private final ImageView leftButton;
    private final ImageView rightButton;

    private Adventure adventure;

    public OverworldScreen(GameData data, MoveListener onMove) {
        this.data = data;
        this.onMove = onMove;
        setPrefSize(1280, 768);
        setMinSize(1280, 768);
        setMaxSize(1280, 768);

        Pane map = new Pane();
        map.setPrefSize(MAP_SIZE, MAP_SIZE);
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                Terrain terrain = data.getTiles()[y][x];
                ImageView tile = sprite(data.tileImage(terrain), TILE);
                tile.setLayoutX(x * TILE);
                tile.setLayoutY(y * TILE);
                map.getChildren().add(tile);
            }
        }
        actors.setPrefSize(MAP_SIZE, MAP_SIZE);
        actors.setMouseTransparent(true);
        StackPane mapStack = new StackPane(map, actors);
        mapStack.setPrefSize(MAP_SIZE, MAP_SIZE);
        mapStack.setMaxSize(MAP_SIZE, MAP_SIZE);
        AnchorPane.setTopAnchor(mapStack, 40.0);
        AnchorPane.setLeftAnchor(mapStack, 300.0);

        StackPane stats = statsScroll();
        AnchorPane.setTopAnchor(stats, 13.0);
        AnchorPane.setRightAnchor(stats, 50.0);

        upButton = directionButton("up", 0, -1);
        downButton = directionButton("down", 0, 1);
        leftButton = directionButton("left", -1, 0);
        rightButton = directionButton("right", 1, 0);
        StackPane upCircle = arrowCircle(upButton);
        StackPane downCircle = arrowCircle(downButton);
        StackPane leftCircle = arrowCircle(leftButton);
        StackPane rightCircle = arrowCircle(rightButton);
        Pane controls = new Pane(upCircle, downCircle, leftCircle, rightCircle);
        controls.setPrefSize(150, 110);
        upCircle.setLayoutX(50);
        upCircle.setLayoutY(0);
        leftCircle.setLayoutX(0);
        leftCircle.setLayoutY(55);
        downCircle.setLayoutX(50);
        downCircle.setLayoutY(55);
        rightCircle.setLayoutX(100);
        rightCircle.setLayoutY(55);
        AnchorPane.setRightAnchor(controls, 55.0);
        AnchorPane.setBottomAnchor(controls, 40.0);

        statusLabel.setFont(Font.font("SansSerif", 12));
        statusLabel.setTextFill(Color.BLACK);
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setMinWidth(450);
        statusLabel.setMinHeight(30);
        statusLabel.setPadding(new Insets(4, 12, 4, 12));
        statusLabel.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1;");
        AnchorPane.setBottomAnchor(statusLabel, 40.0);
        AnchorPane.setLeftAnchor(statusLabel, 400.0);

        getChildren().addAll(mapStack, stats, controls, statusLabel);
    }

    public void setAdventure(Adventure adventure) {
        this.adventure = adventure;
        refresh();
    }

    public void refresh() {
        if (adventure == null) {
            return;
        }
        actors.getChildren().clear();
        for (MysteryBox box : adventure.getBoxes()) {
            place(sprite(data.getMysteryImage(), TILE), box.x(), box.y());
        }
        for (MapMarker marker : adventure.getMarkers()) {
            ImageView view = new ImageView(ImageTint.tinted(data.markerImage(marker.kind()), data.markerColour(marker.kind())));
            view.setFitWidth(TILE);
            view.setFitHeight(TILE);
            place(view, marker.x(), marker.y());
        }
        ImageView exit =
                new ImageView(ImageTint.tinted(data.getExitImage(), data.getExitColour()));
        exit.setFitWidth(TILE);
        exit.setFitHeight(TILE);
        place(exit, data.getExitX(), data.getExitY());

        ImageView player = sprite(data.getPlayerImage(), TILE);
        place(player, adventure.getX(), adventure.getY());

        Mokepon mokepon = adventure.getPlayer();
        nameLabel.setText(mokepon.getName());
        hpValue.setText(Integer.toString(mokepon.getMaxHp()));
        energyValue.setText(Integer.toString(mokepon.getMaxEnergy()));
        strengthValue.setText(Integer.toString(mokepon.getStrength()));
        accuracyValue.setText(Integer.toString(mokepon.getAccuracy()));
        defenceValue.setText(Integer.toString(mokepon.getBaseDefence()));
        dodgeValue.setText(Integer.toString(mokepon.getDodge()));
        fightsValue.setText(Integer.toString(adventure.getFightsWon()));
        healthValue.setText(Integer.toString(adventure.getInventory().count(ItemType.HEALTH)));
        coffeeValue.setText(Integer.toString(adventure.getInventory().count(ItemType.COFFEE)));
        poisonValue.setText(Integer.toString(adventure.getInventory().count(ItemType.POISON)));
        statusLabel.setText(adventure.getStatus());
        setEnabled(upButton, adventure.canMove(0, -1));
        setEnabled(downButton, adventure.canMove(0, 1));
        setEnabled(leftButton, adventure.canMove(-1, 0));
        setEnabled(rightButton, adventure.canMove(1, 0));
    }

    private void place(ImageView view, int column, int row) {
        view.setLayoutX(column * TILE);
        view.setLayoutY(row * TILE);
        actors.getChildren().add(view);
    }

    private ImageView sprite(String path, int size) {
        ImageView view = new ImageView(GameAssets.loadImage(path));
        view.setFitWidth(size);
        view.setFitHeight(size);
        view.setPreserveRatio(true);
        return view;
    }

    private ImageView directionButton(String direction, int dx, int dy) {
        ImageView button = sprite(data.buttonImage(direction), 30);
        button.setPickOnBounds(true);
        button.setOnMouseClicked(
                event -> {
                    if (!button.isDisabled()) {
                        onMove.move(dx, dy);
                    }
                });
        return button;
    }

    private StackPane arrowCircle(ImageView arrow) {
        StackPane circle = new StackPane(arrow);
        circle.setPrefSize(48, 48);
        circle.setMinSize(48, 48);
        circle.setMaxSize(48, 48);
        circle.setStyle(
                "-fx-background-color: rgba(190, 180, 180, 0.70);" +
                        "-fx-background-radius: 24;"
        );
        return circle;
    }

    private void setEnabled(ImageView button, boolean enabled) {
        button.setDisable(!enabled);
        button.setOpacity(enabled ? 1.0 : 0.35);
    }

    private StackPane statsScroll() {
        Separator separator = new Separator();
        separator.setPrefWidth(200);
        separator.setMaxWidth(200);
        VBox content =
                new VBox(
                        6,
                        statRow("Mokepon", nameLabel),
                        statRow("HP", hpValue),
                        statRow("Energy", energyValue),
                        statRow("Strength", strengthValue),
                        statRow("Accuracy", accuracyValue),
                        statRow("Defence", defenceValue),
                        statRow("Dodge", dodgeValue),
                        statRow("Fights", fightsValue),
                        separator,
                        statLabel("Inventory"),
                        statRow("Health Tonic", healthValue),
                        statRow("Coffee", coffeeValue),
                        statRow("Poison", poisonValue));
        content.setPadding(new Insets(65, 46, 70, 47));
        ImageView scroll = new ImageView(GameAssets.loadImage(GameAssets.SCROLL_PATH));
        scroll.setFitWidth(250);
        scroll.setFitHeight(350);
        scroll.setPreserveRatio(false);
        StackPane pane = new StackPane(scroll, content);
        pane.setPrefSize(250, 350);
        pane.setMaxSize(250, 350);
        StackPane.setAlignment(content, Pos.TOP_CENTER);
        return pane;
    }

    private HBox statRow(String label, Label value) {
        Label labelText = statLabel(label + ":");
        labelText.setPrefWidth(100);
        value.setMinWidth(60);
        HBox row = new HBox(0, labelText, value);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private static Label statLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("SansSerif", FontWeight.NORMAL, 13));
        label.setTextFill(Color.BLACK);
        return label;
    }

    private static Label quantityLabel() {
        Label label = new Label("0");
        label.setFont(Font.font("Monospaced", FontWeight.NORMAL, 13));
        label.setTextFill(Color.BLACK);
        return label;
    }
}
