package au.edu.unimelb.swen.oop.mokepon.ui;

import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;

/**
 * Full-screen background image pane used by every screen.
 */
public class BackgroundPane extends StackPane {
    public BackgroundPane(Pane content) {
        ImageView background = new ImageView(GameAssets.loadImage(GameAssets.BG_PATH));
        background.setFitWidth(1280);
        background.setFitHeight(768);
        background.setPreserveRatio(false);
        getChildren().addAll(background, content);
    }
}
