package au.edu.unimelb.swen.oop.mokepon.ui;

import javafx.scene.image.Image;
import javafx.scene.text.Font;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads shared fonts and images used across screens.
 */
public final class GameAssets {
    public static final String FONT_PATH = "assets/GrenzeGotisch-Black.ttf";
    public static final String BG_PATH = "assets/bg.png";
    public static final String SCROLL_PATH = "assets/scroll.png";

    private static String fontFamily;

    private GameAssets() {
    }

    public static Font grenzeFont(double size) {
        if (fontFamily == null) {
            try (InputStream stream = Files.newInputStream(Path.of(FONT_PATH))) {
                Font loaded = Font.loadFont(stream, 12);
                fontFamily = loaded != null ? loaded.getFamily() : "System";
            } catch (IOException exception) {
                fontFamily = "System";
            }
        }
        return Font.font(fontFamily, size);
    }

    public static Image loadImage(String path) {
        return new Image(Path.of(path).toUri().toString(), false);
    }
}
