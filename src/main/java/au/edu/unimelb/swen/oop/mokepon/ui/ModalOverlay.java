package au.edu.unimelb.swen.oop.mokepon.ui;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Darkened modal used for encounters, battle results, and the exit door.
 */
public final class ModalOverlay {
    public record Action(String label, Runnable run) {
    }

    private ModalOverlay() {
    }

    public static StackPane create(String heading, List<String> body, List<Action> actions) {
        Label title = UiFactory.plainHeading(heading, 32);
        title.setWrapText(false);
        title.setMinWidth(330);
        title.setPrefWidth(330);
        title.setMaxWidth(330);
        title.setTextAlignment(TextAlignment.CENTER);
        title.setAlignment(Pos.CENTER);
        title.setTranslateY(-2);

        VBox dialog = new VBox(8, title);
        dialog.setAlignment(Pos.CENTER);
        dialog.setPadding(new Insets(20, 25, 20, 25));
        dialog.setMinSize(330, 160);
        dialog.setPrefSize(330, 160);
        dialog.setMaxSize(330, 160);
        dialog.setTranslateX(-18);
        dialog.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2;");

        for (String line : body) {
            Label message = new Label(line);
            message.setFont(Font.font("SansSerif", 11));
            message.setTextFill(Color.BLACK);
            message.setWrapText(true);
            message.setMinWidth(280);
            message.setPrefWidth(280);
            message.setMaxWidth(280);
            message.setAlignment(Pos.CENTER);
            message.setTextAlignment(TextAlignment.CENTER);
            dialog.getChildren().add(message);
        }

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        for (Action action : actions) {
            StackPane button = UiFactory.textButton(action.label());
            button.setOnMouseClicked(event -> action.run().run());
            buttons.getChildren().add(button);
        }
        dialog.getChildren().add(buttons);

        StackPane overlay = new StackPane(dialog);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.45);");
        overlay.setPrefSize(1280, 768);
        overlay.setMinSize(1280, 768);
        overlay.setMaxSize(1280, 768);
        overlay.setPickOnBounds(true);
        return overlay;
    }

    public static StackPane createFinish(
            String heading, List<Action> actions) {

        Label title = UiFactory.plainHeading(heading, 32);
        title.setWrapText(false);
        title.setAlignment(Pos.CENTER);
        title.setTextAlignment(TextAlignment.CENTER);
        title.setTranslateY(-10);

        HBox buttons = new HBox(2);
        buttons.setAlignment(Pos.CENTER);

        for (Action action : actions) {
            StackPane button = UiFactory.textButton(action.label());
            if (action.label().equals("Return to Main Menu")) {
                button.setMinWidth(140);
                button.setPrefWidth(140);
                button.setMaxWidth(140);
            } else if (action.label().equals("Quit")) {
                button.setMinWidth(50);
                button.setPrefWidth(50);
                button.setMaxWidth(50);
                button.setTranslateX(10);
            }
            button.setOnMouseClicked(event -> action.run().run());
            buttons.getChildren().add(button);
        }

        VBox dialog = new VBox(20, title, buttons);
        dialog.setAlignment(Pos.CENTER);
        dialog.setPadding(new Insets(20, 25, 20, 25));
        dialog.setTranslateX(-15);
        dialog.setTranslateY(-1);

        dialog.setMinSize(550, 150);
        dialog.setPrefSize(550, 150);
        dialog.setMaxSize(550, 150);

        dialog.setStyle(
                "-fx-background-color: white;"
                        + "-fx-border-color: black;"
                        + "-fx-border-width: 2;");

        StackPane overlay = new StackPane(dialog);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.45);");
        overlay.setPrefSize(1280, 768);
        overlay.setMinSize(1280, 768);
        overlay.setMaxSize(1280, 768);
        overlay.setPickOnBounds(true);

        return overlay;
    }

    public static StackPane createBattleResult(
            String heading,
            List<Action> actions) {

        Label title = UiFactory.plainHeading(heading, 32);
        title.setAlignment(Pos.CENTER);
        title.setTextAlignment(TextAlignment.CENTER);

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);

        for (Action action : actions) {
            StackPane button = UiFactory.textButton(action.label());

            button.setMinWidth(90);
            button.setPrefWidth(90);
            button.setMaxWidth(90);

            button.setOnMouseClicked(event -> action.run().run());
            buttons.getChildren().add(button);
        }

        VBox dialog = new VBox(18, title, buttons);
        dialog.setAlignment(Pos.CENTER);

        dialog.setMinSize(140, 160);
        dialog.setPrefSize(140, 160);
        dialog.setMaxSize(140, 160);

        dialog.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: black;" +
                        "-fx-border-width: 1;"
        );

        StackPane overlay = new StackPane(dialog);
        dialog.setTranslateX(-45);
        dialog.setTranslateY(-10);

        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.45);");
        overlay.setPrefSize(1280, 768);
        overlay.setMinSize(1280, 768);
        overlay.setMaxSize(1280, 768);
        overlay.setPickOnBounds(true);

        return overlay;
    }

    public static StackPane createGameOver(
            String heading, List<Action> actions) {

        Label title = UiFactory.plainHeading(heading, 32);
        title.setAlignment(Pos.CENTER);
        title.setTextAlignment(TextAlignment.CENTER);
        title.setTranslateY(-15);

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER);

        for (Action action : actions) {
            StackPane button = UiFactory.textButton(action.label());

            if (action.label().equals("Return to Main Menu")) {
                button.setMinSize(170, 45);
                button.setPrefSize(170, 45);
                button.setMaxSize(170, 45);
            } else if (action.label().equals("Quit")) {
                button.setMinSize(70, 45);
                button.setPrefSize(70, 45);
                button.setMaxSize(70, 45);
            }

            button.setScaleY(0.75);

            button.setOnMouseClicked(event -> action.run().run());
            buttons.getChildren().add(button);
        }

        VBox dialog = new VBox(15, title, buttons);
        dialog.setAlignment(Pos.CENTER);
        dialog.setPadding(new Insets(15));

        dialog.setMinSize(300, 155);
        dialog.setPrefSize(300, 155);
        dialog.setMaxSize(300, 155);

        dialog.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: black;" +
                        "-fx-border-width: 2;");

        StackPane overlay = new StackPane(dialog);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.45);");
        overlay.setPrefSize(1280, 768);
        overlay.setMinSize(1280, 768);
        overlay.setMaxSize(1280, 768);

        dialog.setTranslateX(0);
        dialog.setTranslateY(-10);

        return overlay;
    }

}
