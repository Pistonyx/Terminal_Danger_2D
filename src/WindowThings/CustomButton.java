package WindowThings;

import javax.swing.*;
import java.awt.*;
import javax.swing.plaf.basic.BasicButtonUI; // Added this import

/**
 * This class is used to change the style of the buttons in the game
 */
public class CustomButton {
    // This method changes a JButton's style, optionally applying a background image.
    public static void changeStyle(JButton button, Image backgroundImage){
        if (backgroundImage != null) {
            button.setOpaque(false); // Make the button transparent so the image can show through
            button.setContentAreaFilled(false); // Don't paint the content area
            button.setBorderPainted(false); // Don't paint the border
            button.setFocusPainted(false); // Don't paint the focus ring

            // Create a custom UI for the button to draw the background image
            button.setUI(new BasicButtonUI() {
                @Override
                public void paint(Graphics g, JComponent c) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    try {
                        GameTextures.paintBackground(g2, c, backgroundImage);
                        super.paint(g2, c); // Paint the text and icon on top
                    } finally {
                        g2.dispose();
                    }
                }
            });
        } else {
            // Default style if no background image is provided
            button.setBackground(new Color(50,20,90));
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setContentAreaFilled(true); // Ensure content area is filled for solid color
        }

        button.setForeground(Color.white);
        button.setFont(new Font("Times New Roman",Font.BOLD,14));
    }
}
