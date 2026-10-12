package au.edu.unimelb.swen.oop.mokepon.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * Factory methods for styled UI components required by the specification.
 */
public final class UiFactory {
    private static final Color GREY_BUTTON = Color.web("#d3d3d3");
    private static final Color OUTLINE_WHITE = Color.WHITE;
    private static final Color FAINT_RED = Color.web("#b41e1e");
    private static final double FAINT_OPACITY = 0.35;

    private static Image faintOverlayImage;

    private UiFactory() {
    }

    public static Label outlinedHeading(String text, double fontSize) {
        Label label = new Label(text);
        label.setFont(GameAssets.grenzeFont(fontSize));
        label.setTextFill(Color.BLACK);
        DropShadow shadow = new DropShadow();
        shadow.setColor(OUTLINE_WHITE);
        shadow.setRadius(2);
        shadow.setSpread(1.0);
        shadow.setOffsetX(0);
        shadow.setOffsetY(0);
        label.setEffect(shadow);
        return label;
    }

    public static Label plainHeading(String text, double fontSize) {
        Label label = new Label(text);
        label.setFont(GameAssets.grenzeFont(fontSize));
        label.setTextFill(Color.BLACK);
        return label;
    }

    public static StackPane roundedListItem(String text, boolean selected) {
        Label label = plainHeading(text, 20);
        StackPane pane = new StackPane(label);
        pane.setMaxWidth(270);
        pane.setPadding(new Insets(5.7, 16, 5.7, 16));
        pane.setBackground(greyRoundedBackground(selected ? 0.75 : 0.50));
        return pane;
    }

    public static StackPane roundedActionButton(String text, boolean enabled) {
        Label label = plainHeading(text, 18);
        StackPane pane = new StackPane(label);
        pane.setPrefSize(135, 45);
        pane.setMinSize(135, 45);
        pane.setMaxSize(135, 45);
        pane.setBackground(greyRoundedBackground(enabled ? 0.50 : 0.20));
        return pane;
    }

    public static StackPane textButton(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("SansSerif", FontWeight.NORMAL, 12));
        label.setWrapText(true);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setAlignment(Pos.CENTER);
        double width = Math.max(80, text.length() * 9.0 + 30);
        StackPane pane = new StackPane(label);
        pane.setPrefSize(width, 30);
        pane.setMinSize(width, 30);
        pane.setMaxSize(width, 30);
        pane.setBackground(greyRoundedBackground(0.9));
        return pane;
    }

    public static StackPane chooseButton(boolean enabled) {
        Label label = plainHeading("Choose Mokepon", 24);
        label.setTextFill(Color.BLACK);
        label.setStyle("-fx-opacity: 1.0;");

        label.setWrapText(true);
        label.setMaxWidth(120);
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);

        StackPane pane = new StackPane(label);
        pane.setPrefSize(150, 150);
        pane.setMinSize(150, 150);
        pane.setMaxSize(150, 150);
        pane.setBackground(greyRoundedBackground(enabled ? 0.50 : 0.20));
        return pane;
    }

    public static void setPressed(StackPane pane, boolean pressed, double enabledOpacity) {
        pane.setBackground(greyRoundedBackground(pressed ? 0.75 : enabledOpacity));
    }

    public static Background greyRoundedBackground(double opacity) {
        BackgroundFill fill =
                new BackgroundFill(
                        GREY_BUTTON.deriveColor(0, 1, 1, opacity),
                        new CornerRadii(30),
                        Insets.EMPTY);
        return new Background(fill);
    }

    public static StackPane portraitPanel() {
        Circle circle = new Circle(155);
        circle.setFill(Color.color(0.7, 0.7, 0.7, 0.50));
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);
        circle.setStrokeType(StrokeType.INSIDE);
        StackPane pane = new StackPane(circle);
        pane.setMinSize(310, 310);
        pane.setMaxSize(310, 310);
        pane.setAlignment(Pos.CENTER);
        return pane;
    }

    public static ProgressBar createStatBar(Color color) {
        ProgressBar bar = new ProgressBar(1.0);
        bar.setPrefWidth(250);
        bar.setPrefHeight(25);
        String hex = toHex(color);
        bar.setStyle(
                "-fx-accent: "
                        + hex
                        + "; -fx-control-inner-background: rgba(255,255,255,0.35);");
        return bar;
    }

    public static Label monospaceBoldLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Monospaced", FontWeight.BOLD, 13));
        label.setTextFill(Color.BLACK);
        return label;
    }

    public static double barProgress(int current, int max) {
        if (current <= 0) {
            return 0.0;
        }
        double progress = (double) current / max;
        return Math.max(progress, 0.03);
    }

    public static StackPane createFaintOverlay(double width, double height) {
        ImageView mask = new ImageView(getFaintOverlayImage());
        mask.setFitWidth(width);
        mask.setFitHeight(height);
        mask.setPreserveRatio(false);

        StackPane overlay = new StackPane(mask);
        overlay.setPrefSize(width, height);
        overlay.setMaxSize(width, height);
        overlay.setVisible(false);
        overlay.setMouseTransparent(true);
        return overlay;
    }

    private static Image getFaintOverlayImage() {
        if (faintOverlayImage != null) {
            return faintOverlayImage;
        }

        Image scroll = GameAssets.loadImage(GameAssets.SCROLL_PATH);
        int width = (int) scroll.getWidth();
        int height = (int) scroll.getHeight();
        PixelReader pixels = scroll.getPixelReader();
        WritableImage mask = new WritableImage(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double alpha = pixels.getColor(x, y).getOpacity() * FAINT_OPACITY;
                Color maskedRed =
                        new Color(FAINT_RED.getRed(), FAINT_RED.getGreen(), FAINT_RED.getBlue(), alpha);
                mask.getPixelWriter().setColor(x, y, maskedRed);
            }
        }

        faintOverlayImage = mask;
        return faintOverlayImage;
    }

    private static String toHex(Color color) {
        return String.format(
                "#%02x%02x%02x",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}
