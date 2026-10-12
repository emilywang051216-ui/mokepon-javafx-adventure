package au.edu.unimelb.swen.oop.mokepon;

import au.edu.unimelb.swen.oop.mokepon.model.Adventure;
import au.edu.unimelb.swen.oop.mokepon.model.Battle;
import au.edu.unimelb.swen.oop.mokepon.model.GameData;
import au.edu.unimelb.swen.oop.mokepon.model.Mokepon;
import au.edu.unimelb.swen.oop.mokepon.ui.BackgroundPane;
import au.edu.unimelb.swen.oop.mokepon.ui.BattleScreen;
import au.edu.unimelb.swen.oop.mokepon.ui.ModalOverlay;
import au.edu.unimelb.swen.oop.mokepon.ui.OverworldScreen;
import au.edu.unimelb.swen.oop.mokepon.ui.SelectScreen;

import java.util.List;
import java.util.Random;

import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main application window managing screen states and menus.
 */
public class GameWindow extends Stage {
  private enum ScreenState {
    SELECT,
    OVERWORLD,
    BATTLE
  }

  private final GameData gameData;
  private final Random random = new Random();
  private final SelectScreen selectScreen;
  private final OverworldScreen overworldScreen;
  private final BattleScreen battleScreen;
  private final StackPane screenContainer = new StackPane();
  private final MenuItem runAwayItem;
  private final MenuItem mainMenuItem;

  private ScreenState screenState = ScreenState.SELECT;
  private Adventure adventure;
  private StackPane modal;
  private boolean modalOpen;

  public GameWindow(GameData gameData) {
    this.gameData = gameData;
    selectScreen = new SelectScreen(gameData, this::beginAdventure);
    selectScreen.setMokeponList(gameData.getMokepon());
    overworldScreen = new OverworldScreen(gameData, this::move);
    battleScreen = new BattleScreen();

    screenContainer.setMinSize(1280, 768);
    screenContainer.setPrefSize(1280, 768);
    screenContainer.setMaxSize(1280, 768);
    screenContainer.getChildren().add(selectScreen);

    BackgroundPane gamePane = new BackgroundPane(screenContainer);
    gamePane.setMinSize(1280, 768);
    gamePane.setPrefSize(1280, 768);
    gamePane.setMaxSize(1280, 768);

    MenuBar menuBar = createMenuBar();
    VBox layout = new VBox(menuBar, gamePane);
    Scene scene = new Scene(layout);
    scene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKey);
    setTitle("Mokepon");
    setResizable(false);
    setScene(scene);

    runAwayItem = findMenuItem(menuBar, "Run away");
    mainMenuItem = findMenuItem(menuBar, "Main menu");
    updateMenuState();
  }

  private MenuBar createMenuBar() {
    MenuItem runAway = new MenuItem("Run away");
    runAway.setAccelerator(KeyCombination.keyCombination("Ctrl+R"));
    runAway.setOnAction(event -> runAway());

    MenuItem mainMenu = new MenuItem("Main menu");
    mainMenu.setAccelerator(KeyCombination.keyCombination("Ctrl+B"));
    mainMenu.setOnAction(event -> returnToSelect());

    MenuItem exit = new MenuItem("Exit");
    exit.setAccelerator(KeyCombination.keyCombination("Alt+F4"));
    exit.setOnAction(event -> close());

    Menu gameMenu = new Menu("Game");
    gameMenu.getItems().addAll(runAway, mainMenu, exit);
    return new MenuBar(gameMenu);
  }

  private MenuItem findMenuItem(MenuBar menuBar, String text) {
    for (Menu menu : menuBar.getMenus()) {
      for (MenuItem item : menu.getItems()) {
        if (item.getText().equals(text)) {
          return item;
        }
      }
    }
    throw new IllegalStateException("Menu item not found: " + text);
  }

  private void handleKey(KeyEvent event) {
    if (modalOpen) {
      return;
    }
    if (screenState == ScreenState.OVERWORLD) {
      switch (event.getCode()) {
        case UP -> move(0, -1);
        case DOWN -> move(0, 1);
        case LEFT -> move(-1, 0);
        case RIGHT -> move(1, 0);
        default -> {
          return;
        }
      }
      event.consume();
    } else if (screenState == ScreenState.BATTLE) {
      battleScreen.handleHotkey(event.getCode());
    }
  }

  private void beginAdventure(Mokepon choice) {
    adventure = new Adventure(gameData, choice);
    overworldScreen.setAdventure(adventure);
    showScreen(ScreenState.OVERWORLD, overworldScreen);
  }

  private void move(int dx, int dy) {
    if (adventure == null || modalOpen || screenState != ScreenState.OVERWORLD) {
      return;
    }
    Adventure.MoveResult result = adventure.tryMove(dx, dy, random);
    overworldScreen.refresh();
    switch (result) {
      case Adventure.Encountered encountered -> showEncounter(encountered);
      case Adventure.Finished ignored -> showFinish();
      default -> {
      }
    }
  }

  private void showEncounter(Adventure.Encountered encountered) {
    showModal(
        "Encountered a " + encountered.enemy().getName(),
        encountered.messages(),
        List.of(
            new ModalOverlay.Action("Fight!", this::startBattle),
            new ModalOverlay.Action("Run Away!", this::retreatToPreviousTile)));
  }

  private void startBattle() {
    hideModal();
    battleScreen.startBattle(
        adventure.getPlayer(),
        adventure.getPendingEnemy(),
        adventure.currentTerrain(),
        adventure.getInventory(),
        gameData.getItemImages(),
        this::onBattleFinished);
    showScreen(ScreenState.BATTLE, battleScreen);
    battleScreen.requestFocus();
  }

  private void onBattleFinished(Battle.Outcome outcome) {
    if (outcome == Battle.Outcome.PLAYER_WON) {
      showBattleResult(
          "You Win",
          List.of(
              new ModalOverlay.Action(
                  "Back to it!", this::returnVictorious)));
    } else if (outcome == Battle.Outcome.PLAYER_LOST) {
      showGameOver(
          "Game Over",
          List.of(
              new ModalOverlay.Action(
                  "Return to Main Menu", this::returnToSelect),
              new ModalOverlay.Action(
                  "Quit", this::close)));
    }
  }

  private void returnVictorious() {
    adventure.applyVictory();
    hideModal();
    overworldScreen.refresh();
    showScreen(ScreenState.OVERWORLD, overworldScreen);
  }

  private void retreatToPreviousTile() {
    adventure.retreat();
    hideModal();
    overworldScreen.refresh();
  }

  private void runAway() {
    if (screenState != ScreenState.BATTLE || battleScreen.isBattleOver()) {
      return;
    }
    adventure.retreat();
    hideModal();
    overworldScreen.refresh();
    showScreen(ScreenState.OVERWORLD, overworldScreen);
  }

  private void showFinish() {
    showFinishModal(
        "You finished the game! You won " + adventure.getFightsWon() + " fights!",
        List.of(
            new ModalOverlay.Action("Return to Main Menu", this::returnToSelect),
            new ModalOverlay.Action("Quit", this::close)));
  }

  private void returnToSelect() {
    hideModal();
    adventure = null;
    selectScreen.clearSelection();
    showScreen(ScreenState.SELECT, selectScreen);
  }

  private void showScreen(ScreenState state, javafx.scene.layout.Pane screen) {
    screenState = state;
    screenContainer.getChildren().clear();
    screenContainer.getChildren().add(screen);
    if (modal != null) {
      screenContainer.getChildren().add(modal);
    }
    updateMenuState();
  }

  private void showModal(String heading, List<String> body, List<ModalOverlay.Action> actions) {
    hideModal();
    modal = ModalOverlay.create(heading, body, actions);
    modalOpen = true;
    screenContainer.getChildren().add(modal);
    updateMenuState();
  }

  private void showGameOver(
      String heading,
      List<ModalOverlay.Action> actions) {
    hideModal();
    modal = ModalOverlay.createGameOver(heading, actions);
    modalOpen = true;
    screenContainer.getChildren().add(modal);
    updateMenuState();
  }

  private void showBattleResult(
      String heading,
      List<ModalOverlay.Action> actions) {

    hideModal();
    modal = ModalOverlay.createBattleResult(heading, actions);
    modalOpen = true;
    screenContainer.getChildren().add(modal);
    updateMenuState();
  }

  private void showFinishModal(String heading, List<ModalOverlay.Action> actions) {
    hideModal();
    modal = ModalOverlay.createFinish(heading, actions);
    modalOpen = true;
    screenContainer.getChildren().add(modal);
    updateMenuState();
  }

  private void hideModal() {
    if (modal != null) {
      screenContainer.getChildren().remove(modal);
      modal = null;
    }
    modalOpen = false;
    updateMenuState();
  }

  private void updateMenuState() {
    boolean battleOngoing =
        screenState == ScreenState.BATTLE && !modalOpen && !battleScreen.isBattleOver();
    runAwayItem.setDisable(!battleOngoing);
    mainMenuItem.setDisable(screenState == ScreenState.SELECT);
  }
}
