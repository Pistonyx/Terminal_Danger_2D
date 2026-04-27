package WindowThings;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Utility class for managing and applying textures/images to game components.
 */
public class GameTextures {

    /**
     * Loads an image from the resources folder.
     * @param path Path to the image (e.g., "/images/bg.png")
     * @return The Image object, or null if not found.
     */
    public static Image loadImage(String path) {
        URL imgURL = GameTextures.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL).getImage();
        } else {
            System.err.println("Could not find image: " + path);
            return null;
        }
    }

    /**
     * Helper method to draw a background image that fills the component.
     */
    public static void paintBackground(Graphics g, Component c, Image image) {
        if (image != null) {
            g.drawImage(image, 0, 0, c.getWidth(), c.getHeight(), c);
        }
    }
}
