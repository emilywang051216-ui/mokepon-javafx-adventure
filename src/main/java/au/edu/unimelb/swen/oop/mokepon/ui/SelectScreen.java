package au.edu.unimelb.swen.oop.mokepon.ui;

import au.edu.unimelb.swen.oop.mokepon.model.GameData;
import au.edu.unimelb.swen.oop.mokepon.model.Mokepon;
import au.edu.unimelb.swen.oop.mokepon.model.MokeponType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.application.Platform;
import javafx.scene.layout.Region;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Screen where the player selects their Mokepon before battle.
 */
public class SelectScreen extends StackPane {
    private final GameData gameData;
    private final Consumer<Mokepon> onChoose;
    private final VBox listBox = new VBox(8);
    private final StackPane portraitPanel = UiFactory.portraitPanel();
    private final ImageView portraitImage = new ImageView();
    private final ImageView typeImage = new ImageView();
    private final Label nameLabel = UiFactory.outlinedHeading("Select a Mokepon", 30);
    private final HBox nameRow = new HBox(20, nameLabel, typeImage);
    private final VBox statsBox = new VBox(10);
    private final StackPane choosePane = UiFactory.chooseButton(false);

    private Mokepon selected;
    private List<Mokepon> mokeponList;

    public SelectScreen(GameData gameData, Consumer<Mokepon> onChoose) {
        this.gameData = gameData;
        this.onChoose = onChoose;
        typeImage.setFitWidth(40);
        typeImage.setFitHeight(40);
        typeImage.setPreserveRatio(true);
        typeImage.setVisible(false);
        nameRow.setAlignment(Pos.CENTER);
        nameLabel.setTranslateX(15);
        buildLayout();
    }

    public void setMokeponList(List<Mokepon> mokeponList) {
        this.mokeponList = mokeponList;
        this.selected = null;
        rebuildList();
        updateDetails(null);
        updateChooseButton(false);
    }

    public void clearSelection() {
        selected = null;
        rebuildList();
        updateDetails(null);
        updateChooseButton(false);
    }

    private void buildLayout() {
        Label title = UiFactory.outlinedHeading("Choose your Mokepon", 34);
        title.setTranslateX(20);
        title.setTranslateY(10);
        nameRow.setTranslateX(60);

        portraitImage.setFitWidth(256);
        portraitImage.setFitHeight(256);
        portraitImage.setPreserveRatio(true);
        StackPane portraitContainer = new StackPane(portraitPanel, portraitImage);
        portraitContainer.setAlignment(Pos.CENTER);
        portraitContainer.setTranslateX(70);
        portraitContainer.setTranslateY(10);

        statsBox.setPadding(new Insets(18, 0, 18, 28));
        statsBox.setTranslateX(15);
        statsBox.setTranslateY(20);
        StackPane scrollStats = createScrollPane(statsBox, 400, 295);
        scrollStats.setTranslateX(160);
        scrollStats.setTranslateY(10);

        choosePane.setOnMouseClicked(
                event -> {
                    if (selected != null) {
                        onChoose.accept(selected);
                    }
                });
        attachPressEffect(choosePane, () -> selected != null, 0.50, 0.20);

        HBox statsRow = new HBox(20, scrollStats, choosePane);
        statsRow.setAlignment(Pos.CENTER);
        choosePane.setTranslateX(200);
        choosePane.setTranslateY(10);

        VBox rightPanel = new VBox(12, portraitContainer, nameRow, statsRow);
        rightPanel.setAlignment(Pos.TOP_CENTER);
        rightPanel.setPadding(new Insets(40, 40, 0, 0));
        rightPanel.setTranslateX(60);

        ScrollPane listScroll = new ScrollPane(listBox);
        listBox.setAlignment(Pos.TOP_CENTER);
        listScroll.setFitToWidth(true);
        listScroll.setPrefViewportWidth(280);
        listScroll.setPrefViewportHeight(610);
        listScroll.setHbarPolicy(ScrollBarPolicy.NEVER);
        listScroll.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        listScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        listBox.setStyle("-fx-background-color: transparent;");
        Platform.runLater(() -> {
            if (listScroll.lookup(".viewport") != null) {
                listScroll.lookup(".viewport")
                        .setStyle("-fx-background-color: transparent;");
            }
            if (listScroll.lookup(".scroll-bar:vertical") != null) {
                listScroll.lookup(".scroll-bar:vertical")
                        .setStyle(
                                "-fx-background-color: transparent;"
                                        + "-fx-background: transparent;");
            }
            if (listScroll.lookup(".scroll-bar:vertical .track") != null) {
                listScroll.lookup(".scroll-bar:vertical .track")
                        .setStyle(
                                "-fx-background-color: transparent;"
                                        + "-fx-background-insets: 0;");
            }
            if (listScroll.lookup(".scroll-bar:vertical .track-background") != null) {
                listScroll.lookup(".scroll-bar:vertical .track-background")
                        .setStyle("-fx-background-color: transparent;");
            }
            if (listScroll.lookup(".scroll-bar:vertical .thumb") != null) {
                listScroll.lookup(".scroll-bar:vertical .thumb")
                        .setStyle(
                                "-fx-background-color: rgba(220, 220, 220, 0.60);"
                                        + "-fx-background-radius: 999;"
                                        + "-fx-background-insets: 3 3 3 3;");
            }
        });
        listScroll.setTranslateX(20);

        VBox leftPanel = new VBox(16, title, listScroll);
        leftPanel.setPadding(new Insets(30, 0, 0, 30));

        HBox content = new HBox(40, leftPanel, rightPanel);
        content.setAlignment(Pos.TOP_LEFT);
        getChildren().add(content);
    }

    private void rebuildList() {
        listBox.getChildren().clear();
        if (mokeponList == null) {
            return;
        }
        for (Mokepon mokepon : mokeponList) {
            boolean isSelected = selected == mokepon;
            StackPane item = UiFactory.roundedListItem(mokepon.getName(), isSelected);
            item.setOnMouseClicked(
                    event -> {
                        selected = mokepon;
                        rebuildList();
                        updateDetails(mokepon);
                        updateChooseButton(true);
                    });
            listBox.getChildren().add(item);
        }
    }

    private void updateDetails(Mokepon mokepon) {
        portraitImage.setImage(null);
        typeImage.setImage(null);
        typeImage.setVisible(false);
        if (mokepon == null) {
            nameLabel.setText("Select a Mokepon");
            fillStats(null);
            return;
        }
        portraitImage.setImage(GameAssets.loadImage(mokepon.getImagePath()));
        nameLabel.setText(mokepon.getName());
        if (mokepon.getType() != MokeponType.NORMAL) {
            typeImage.setImage(GameAssets.loadImage(gameData.typeImage(mokepon.getType())));
            typeImage.setVisible(true);
        }
        fillStats(mokepon);
    }

    private void fillStats(Mokepon mokepon) {
        statsBox.getChildren().clear();
        statsBox
                .getChildren()
                .add(statLine("HP", mokepon == null ? "" : String.valueOf(mokepon.getMaxHp())));
        statsBox
                .getChildren()
                .add(
                        statLine(
                                "Energy", mokepon == null ? "" : String.valueOf(mokepon.getMaxEnergy())));
        statsBox.getChildren()
                .add(statLine("Strength", mokepon == null ? "" : String.valueOf(mokepon.getStrength())));
        statsBox.getChildren()
                .add(statLine("Accuracy", mokepon == null ? "" : String.valueOf(mokepon.getAccuracy())));
        statsBox.getChildren()
                .add(statLine("Defence", mokepon == null ? "" : String.valueOf(mokepon.getBaseDefence())));
        statsBox
                .getChildren()
                .add(statLine("Dodge", mokepon == null ? "" : String.valueOf(mokepon.getDodge())));
        statsBox.getChildren()
                .add(statLine("Skills", mokepon == null ? "" : mokepon.formatSkills()));
        Region spacer = new Region();
        spacer.setPrefHeight(7);
        statsBox.getChildren().add(spacer);
        statsBox
                .getChildren()
                .add(statLine("Type", mokepon == null ? "" : mokepon.getType().name()));
    }

    private HBox statLine(String label, String value) {
        Label labelText = new Label(label + ":");
        Label valueText = new Label(value);
        labelText.setFont(Font.font("Cambria", FontWeight.BOLD, 13));
        valueText.setFont(Font.font("Cambria", FontWeight.NORMAL, 13));
        labelText.setTextFill(Color.web("#2a1d18"));
        valueText.setTextFill(Color.web("#2a1d18"));
        labelText.setPrefWidth(60);
        HBox row = new HBox(10, labelText, valueText);
        return row;
    }

    private void updateChooseButton(boolean enabled) {
        choosePane.setBackground(UiFactory.greyRoundedBackground(enabled ? 0.50 : 0.20));
        choosePane.setDisable(!enabled);
    }

    private StackPane createScrollPane(VBox content, double width, double height) {
        ImageView scroll = new ImageView(GameAssets.loadImage(GameAssets.SCROLL_PATH));
        scroll.setFitWidth(width);
        scroll.setFitHeight(height);
        scroll.setPreserveRatio(false);
        StackPane pane = new StackPane(scroll, content);
        pane.setPrefSize(width, height);
        pane.setMaxSize(width, height);
        StackPane.setAlignment(content, Pos.CENTER);
        return pane;
    }

    private void attachPressEffect(
            StackPane pane,
            BooleanSupplier enabled,
            double enabledOpacity,
            double disabledOpacity) {
        pane.setOnMousePressed(
                event -> {
                    if (enabled.getAsBoolean()) {
                        UiFactory.setPressed(pane, true, enabledOpacity);
                    }
                });
        pane.setOnMouseReleased(
                event -> {
                    double opacity = enabled.getAsBoolean() ? enabledOpacity : disabledOpacity;
                    UiFactory.setPressed(pane, false, opacity);
                });
    }
}
