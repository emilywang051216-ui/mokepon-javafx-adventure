package au.edu.unimelb.swen.oop.mokepon;

import au.edu.unimelb.swen.oop.mokepon.model.GameData;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Main extends Application {
  @Override
  public void start(Stage stage) {
    try {
      Properties properties = loadPropertiesFromArgument();
      GameData gameData = GameData.load(properties);
      GameWindow gameWindow = new GameWindow(gameData);
      gameWindow.show();
    } catch (Exception exception) {
      String path = resolveDataPath();
      if (path == null) {
        path = "mokepon.game";
      }
      System.out.println("Error loading " + path);
      javafx.application.Platform.exit();
    }
  }

  private Properties loadPropertiesFromArgument() throws IOException {
    String dataPath = resolveDataPath();
    if (dataPath == null) {
      throw new IOException("Missing data argument");
    }

    Properties properties = new Properties();
    try (Reader reader = Files.newBufferedReader(Path.of(dataPath), StandardCharsets.UTF_8)) {
      properties.load(reader);
    }
    return properties;
  }

  private String resolveDataPath() {
    for (String argument : getParameters().getRaw()) {
      if (argument.startsWith("data=")) {
        return argument.substring("data=".length());
      }
    }
    return null;
  }

  static void main(String[] args) {
    launch(args);
  }
}
