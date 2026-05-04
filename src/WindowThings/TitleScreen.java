package WindowThings;

import javax.swing.*;
import java.awt.*;

public class TitleScreen {
    private JFrame TitleScreen;
    private TitleScreenPanel mainPanel; // Custom panel for background

    public TitleScreen(){
        TitleScreen = new JFrame("Terminal Danger");
        mainPanel = new TitleScreenPanel(); // Initialize custom panel
    }

    public void init(){
        this.TitleScreen.setSize(600, 400);
        this.TitleScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.TitleScreen.setLocationRelativeTo(null);

        // Set layout for the custom panel
        mainPanel.setLayout(new BorderLayout());

        // adds a text to the middle of the screen
        JLabel label = new JLabel("TERMINAL DANGER");
        label.setHorizontalAlignment(SwingConstants.CENTER); // Use SwingConstants for alignment
        label.setForeground(Color.WHITE); // Make text visible on potential dark background
        label.setFont(new Font("Monospaced", Font.BOLD, 30)); // Example font styling
        mainPanel.add(label, BorderLayout.CENTER);

        // adds a button to the bottom of the screen
        JButton button = new JButton("Start");
        CustomButton.changeStyle(button);
        mainPanel.add(button, BorderLayout.SOUTH);

        // Add the custom panel to the JFrame
        this.TitleScreen.add(mainPanel);

        // Load a default background image for the title screen
        mainPanel.setBackgroundImage(GameTextures.loadImage("/images/title_background.jpg")); // Assuming you'll add this image

        // adds an action to the button. In this case it closes the window.
        button.addActionListener(e ->{
            this.TitleScreen.dispose();
            // after it deletes the original window, it runs the init() method in the App class which creates a new window
            SwingUtilities.invokeLater(GameWindow::new);
        });
        this.TitleScreen.setVisible(true);
    }

    // Custom JPanel to draw the background image
    private static class TitleScreenPanel extends JPanel {
        private Image backgroundImage;

        public void setBackgroundImage(Image image) {
            this.backgroundImage = image;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                GameTextures.paintBackground(g, this, backgroundImage);
            }
        }
    }
}
