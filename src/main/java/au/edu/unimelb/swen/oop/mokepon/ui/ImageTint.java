package au.edu.unimelb.swen.oop.mokepon.ui;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Tints a sprite with a JavaFX colour string while keeping its transparency.
 */
public final class ImageTint {
    private static final Map<String, Image> CACHE = new HashMap<>();

    private ImageTint() {
    }

    public static Image tinted(String path, String colour) {
        return CACHE.computeIfAbsent(path + "|" + colour, key -> apply(GameAssets.loadImage(path), Color.web(colour)));
    }

    private static Image apply(Image source, Color tint) {
        int width = (int) source.getWidth();
        int height = (int) source.getHeight();
        WritableImage tinted = new WritableImage(width, height);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = tinted.getPixelWriter();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixel = reader.getColor(x, y);
                writer.setColor(
                        x,
                        y,
                        new Color(
                                pixel.getRed() * tint.getRed(),
                                pixel.getGreen() * tint.getGreen(),
                                pixel.getBlue() * tint.getBlue(),
                                pixel.getOpacity()));
            }
        }
        return tinted;
    }
}
