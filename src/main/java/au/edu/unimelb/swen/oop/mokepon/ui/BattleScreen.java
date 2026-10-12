package au.edu.unimelb.swen.oop.mokepon.ui;

import au.edu.unimelb.swen.oop.mokepon.model.Battle;
import au.edu.unimelb.swen.oop.mokepon.model.Inventory;
import au.edu.unimelb.swen.oop.mokepon.model.ItemType;
import au.edu.unimelb.swen.oop.mokepon.model.Mokepon;
import au.edu.unimelb.swen.oop.mokepon.model.Skill;
import au.edu.unimelb.swen.oop.mokepon.model.Terrain;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Screen where turn-based battles take place.
 */
public class BattleScreen extends AnchorPane {
    private static final Color HP_COLOR = Color.LIMEGREEN;
    private static final Color ENERGY_COLOR = Color.ROYALBLUE;

    private final Random random = new Random();

    private Inventory inventory;
    private Map<ItemType, String> itemImages;
    private Consumer<Battle.Outcome> onFinished;
    private boolean finishNotified;
    private Battle battle;

    private final Label playerNameLabel = UiFactory.plainHeading("", 28);
    private final Label enemyNameLabel = UiFactory.plainHeading("", 28);
    private final ImageView playerImage = new ImageView();
    private final ImageView enemyImage = new ImageView();
    private final Label playerHpLabel = UiFactory.monospaceBoldLabel("");
    private final Label playerEnergyLabel = UiFactory.monospaceBoldLabel("");
    private final ProgressBar playerHpBar = UiFactory.createStatBar(HP_COLOR);
    private final ProgressBar playerEnergyBar = UiFactory.createStatBar(ENERGY_COLOR);
    private final ProgressBar enemyHpBar = UiFactory.createStatBar(HP_COLOR);
    private final ProgressBar enemyEnergyBar = UiFactory.createStatBar(ENERGY_COLOR);
    private final VBox actionBox = new VBox(8);
    private final HBox itemBox = new HBox(8);
    private final TextArea battleLog = new TextArea();
    private final StackPane playerScrollPane;
    private final StackPane enemyScrollPane;
    private final StackPane playerFaintOverlay;
    private final StackPane enemyFaintOverlay;

    private final List<StackPane> actionButtons = new ArrayList<>();

    public BattleScreen() {
        playerScrollPane = createScrollPanel(500, 470);
        enemyScrollPane = createScrollPanel(330, 450);
        playerFaintOverlay = UiFactory.createFaintOverlay(500, 470);
        enemyFaintOverlay = UiFactory.createFaintOverlay(330, 450);
        buildLayout();
        setPrefSize(1280, 768);
    }

    public void startBattle(
            Mokepon player,
            Mokepon enemy,
            Terrain terrain,
            Inventory inventory,
            Map<ItemType, String> itemImages,
            Consumer<Battle.Outcome> onFinished) {
        this.inventory = inventory;
        this.itemImages = itemImages;
        this.onFinished = onFinished;
        this.finishNotified = false;
        this.battle = new Battle(player, enemy, terrain, random);
        refreshAll();
    }

    public boolean isInBattle() {
        return battle != null;
    }

    public boolean isBattleOver() {
        return battle != null && battle.isFinished();
    }

    private void buildLayout() {
        playerImage.setFitWidth(256);
        playerImage.setFitHeight(256);
        playerImage.setPreserveRatio(true);
        playerImage.setTranslateX(20);
        playerImage.setTranslateY(5);

        enemyImage.setFitWidth(256);
        enemyImage.setFitHeight(256);
        enemyImage.setPreserveRatio(true);
        enemyImage.setTranslateY(5);

        StackPane hpDisplay = new StackPane(playerHpBar, playerHpLabel);
        StackPane energyDisplay = new StackPane(playerEnergyBar, playerEnergyLabel);
        hpDisplay.setAlignment(Pos.CENTER);
        energyDisplay.setAlignment(Pos.CENTER);
        VBox playerStats = new VBox(8, hpDisplay, energyDisplay);
        playerStats.setAlignment(Pos.CENTER);

        playerStats.setPadding(new Insets(0, 0, 0, 8));

        Label actionsHeading = UiFactory.plainHeading("Actions", 24);
        actionBox.setPadding(new Insets(8, 0, 0, 0));
        itemBox.setAlignment(Pos.CENTER_LEFT);
        VBox actionsPanel = new VBox(8, actionsHeading, actionBox);
        actionsPanel.setAlignment(Pos.TOP_CENTER);
        actionsPanel.setTranslateX(40);
        actionsPanel.setTranslateY(75);

        VBox imageColumn = new VBox(10, playerImage, playerStats);
        imageColumn.setAlignment(Pos.CENTER);

        HBox playerContent = new HBox(20, imageColumn, actionsPanel);
        playerContent.setAlignment(Pos.CENTER_LEFT);
        playerContent.setPadding(new Insets(16));

        configureFaintOverlay(playerFaintOverlay);
        configureFaintOverlay(enemyFaintOverlay);
        playerScrollPane
                .getChildren()
                .addAll(playerFaintOverlay, playerContent, playerNameLabel);
        StackPane.setAlignment(playerNameLabel, Pos.TOP_CENTER);
        StackPane.setMargin(playerNameLabel, new Insets(40, 0, 0, 0));

        VBox enemyContent = new VBox(12, enemyImage, enemyHpBar, enemyEnergyBar);
        enemyContent.setAlignment(Pos.CENTER);
        enemyContent.setPadding(new Insets(16));
        enemyScrollPane
                .getChildren()
                .addAll(enemyFaintOverlay, enemyContent, enemyNameLabel);
        StackPane.setAlignment(enemyNameLabel, Pos.TOP_CENTER);
        StackPane.setMargin(enemyNameLabel, new Insets(40, 0, 0, 0));

        battleLog.setEditable(false);
        battleLog.setWrapText(true);
        battleLog.setPrefSize(670, 230);
        battleLog.setMaxSize(670, 230);

        battleLog.setStyle(
                "-fx-font-family: 'Monospaced';" +
                        "-fx-font-size: 14px;"
        );

        AnchorPane.setBottomAnchor(playerScrollPane, 38.0);
        AnchorPane.setLeftAnchor(playerScrollPane, 50.0);

        AnchorPane.setTopAnchor(enemyScrollPane, 40.0);
        AnchorPane.setRightAnchor(enemyScrollPane, 50.0);

        AnchorPane.setBottomAnchor(battleLog, 40.0);
        AnchorPane.setRightAnchor(battleLog, 50.0);

        AnchorPane.setTopAnchor(itemBox, 180.0);
        AnchorPane.setLeftAnchor(itemBox, 205.0);

        getChildren().addAll(
                playerScrollPane,
                enemyScrollPane,
                battleLog,
                itemBox
        );
    }

    public void handleHotkey(KeyCode code) {
        int index = hotkeyIndex(code);
        if (index >= 0) {
            useSkillAtIndex(index);
        }
    }

    private void useSkillAtIndex(int index) {
        if (battle == null || battle.isFinished() || index >= actionButtons.size()) {
            return;
        }
        Mokepon player = battle.getPlayer();
        List<Skill> skills = player.getSkills();
        if (index >= skills.size()) {
            return;
        }
        Skill skill = skills.get(index);
        if (player.canAfford(skill)) {
            battle.usePlayerSkill(skill);
            refreshAll();
        }
    }

    private int hotkeyIndex(KeyCode code) {
        return switch (code) {
            case DIGIT1 -> 0;
            case DIGIT2 -> 1;
            case DIGIT3 -> 2;
            case DIGIT4 -> 3;
            default -> -1;
        };
    }

    private StackPane createScrollPanel(double width, double height) {
        ImageView scroll = new ImageView(GameAssets.loadImage(GameAssets.SCROLL_PATH));
        scroll.setFitWidth(width);
        scroll.setFitHeight(height);
        scroll.setPreserveRatio(false);
        StackPane pane = new StackPane(scroll);
        pane.setPrefSize(width, height);
        pane.setMaxSize(width, height);
        return pane;
    }

    private void configureFaintOverlay(StackPane overlay) {
        StackPane.setAlignment(overlay, Pos.CENTER);
    }

    private void refreshAll() {
        Mokepon player = battle.getPlayer();
        Mokepon enemy = battle.getEnemy();

        playerNameLabel.setText(player.getName());
        enemyNameLabel.setText(enemy.getName());
        playerImage.setImage(GameAssets.loadImage(player.getImagePath()));
        enemyImage.setImage(GameAssets.loadImage(enemy.getImagePath()));

        updateBars(player, enemy);
        rebuildActions(player);
        rebuildItems();
        updateLog();
        updateFaintOverlays(player, enemy);
        if (battle.isFinished() && !finishNotified) {
            finishNotified = true;
            if (onFinished != null) {
                onFinished.accept(battle.getOutcome());
            }
        }
    }

    private void rebuildItems() {
        itemBox.getChildren().clear();

        if (inventory == null || itemImages == null || battle == null) {
            return;
        }

        for (ItemType type : List.of(
                ItemType.HEALTH,
                ItemType.POISON,
                ItemType.COFFEE)) {

            int count = inventory.count(type);
            boolean enabled = !battle.isFinished() && count > 0;

            ImageView icon = new ImageView(
                    GameAssets.loadImage(itemImages.get(type))
            );

            icon.setFitWidth(28);
            icon.setFitHeight(28);
            icon.setPreserveRatio(true);

            Label quantity =
                    UiFactory.plainHeading(Integer.toString(count), 24);

            VBox button = new VBox(4, icon, quantity);
            button.setAlignment(Pos.CENTER);

            button.setMinSize(58, 78);
            button.setPrefSize(58, 78);
            button.setMaxSize(58, 78);

            if (count > 0) {
                button.setOpacity(1.0);
            } else {
                button.setOpacity(0.35);
            }

            button.setStyle(
                    "-fx-background-color: rgba(210, 210, 210, 0.55);" +
                            "-fx-background-radius: 30;"
            );

            button.setOpacity(count > 0 ? 1.0 : 0.30);
            button.setDisable(!enabled);

            button.setOnMouseClicked(event -> {
                if (!button.isDisabled()) {
                    useItem(type);
                }
            });

            itemBox.getChildren().add(button);
        }
    }

    private void useItem(ItemType type) {
        if (battle == null || battle.isFinished() || inventory == null || !inventory.consume(type)) {
            return;
        }
        switch (type) {
            case HEALTH -> battle.useHealthTonic();
            case POISON -> battle.usePoison();
            case COFFEE -> battle.useCoffee();
        }
        refreshAll();
    }

    private void updateBars(Mokepon player, Mokepon enemy) {
        playerHpLabel.setText(player.getCurrentHp() + "/" + player.getMaxHp());
        playerEnergyLabel.setText(player.getCurrentEnergy() + "/" + player.getMaxEnergy());
        playerHpBar.setProgress(UiFactory.barProgress(player.getCurrentHp(), player.getMaxHp()));
        playerEnergyBar.setProgress(
                UiFactory.barProgress(player.getCurrentEnergy(), player.getMaxEnergy()));
        enemyHpBar.setProgress(UiFactory.barProgress(enemy.getCurrentHp(), enemy.getMaxHp()));
        enemyEnergyBar.setProgress(
                UiFactory.barProgress(enemy.getCurrentEnergy(), enemy.getMaxEnergy()));
    }

    private void rebuildActions(Mokepon player) {
        actionBox.getChildren().clear();
        actionButtons.clear();
        List<Skill> skills = player.getSkills();
        for (int i = 0; i < skills.size(); i++) {
            Skill skill = skills.get(i);
            boolean enabled = !battle.isFinished() && player.canAfford(skill);
            String label = skill.getName() + " [" + skill.getEnergyCost() + "]";
            StackPane button = UiFactory.roundedActionButton(label, enabled);
            button.setDisable(!enabled || battle.isFinished());
            final Skill chosen = skill;
            button.setOnMouseClicked(
                    event -> {
                        if (!battle.isFinished() && player.canAfford(chosen)) {
                            battle.usePlayerSkill(chosen);
                            refreshAll();
                        }
                    });
            attachPressEffect(button, () -> !button.isDisabled() && !battle.isFinished());
            actionBox.getChildren().add(button);
            actionButtons.add(button);
        }
    }

    private void updateLog() {
        battleLog.setText(String.join("\n", battle.getLogLines()));
        battleLog.positionCaret(battleLog.getText().length());
        battleLog.setScrollTop(Double.MAX_VALUE);
    }

    private void updateFaintOverlays(Mokepon player, Mokepon enemy) {
        playerFaintOverlay.setVisible(player.isFainted());
        enemyFaintOverlay.setVisible(enemy.isFainted());
    }

    private void attachPressEffect(StackPane pane, BooleanSupplier enabled) {
        pane.setOnMousePressed(
                event -> {
                    if (enabled.getAsBoolean()) {
                        UiFactory.setPressed(pane, true, 0.50);
                    }
                });
        pane.setOnMouseReleased(
                event -> {
                    UiFactory.setPressed(pane, false, enabled.getAsBoolean() ? 0.50 : 0.20);
                });
    }
}
