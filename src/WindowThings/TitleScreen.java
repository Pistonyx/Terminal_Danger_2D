package WindowThings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TitleScreen {
    private JFrame TitleScreen;
    private TitleScreenPanel mainPanel; // Custom panel for background

    // Default settings
    private boolean fullscreen = false;
    private int width = 900;
    private int height = 650;

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
        label.setForeground(Color.BLACK); // Changed text color to BLACK
        label.setFont(new Font("Monospaced", Font.BOLD, 30)); // Example font styling
        mainPanel.add(label, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // Make button panel transparent
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // Stack buttons vertically

        // adds a button to the bottom of the screen
        JButton startButton = new JButton("Start");
        CustomButton.changeStyle(startButton, null);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button
        buttonPanel.add(startButton);

        // Add some space between buttons
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Add Settings button
        JButton settingsButton = new JButton("Settings");
        CustomButton.changeStyle(settingsButton, null);
        settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button
        buttonPanel.add(settingsButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add the custom panel to the JFrame
        this.TitleScreen.add(mainPanel);

        // Load a default background image for the title screen
        mainPanel.setBackgroundImage(GameTextures.loadImage("/images/title_background.jpg"));

        // adds an action to the start button. In this case it closes the window.
        startButton.addActionListener(e ->{
            this.TitleScreen.dispose();
            // after it deletes the original window, it runs the init() method in the App class which creates a new window
            SwingUtilities.invokeLater(() -> new GameWindow(fullscreen, width, height)); // Pass current settings
        });

        // Add action listener for settings button
        settingsButton.addActionListener(e -> {
            SettingsWindow settingsDialog = new SettingsWindow(TitleScreen);
            settingsDialog.setVisible(true);
            // When settings dialog closes, retrieve settings and update TitleScreen's fields
            if (settingsDialog.isSettingsApplied()) {
                fullscreen = settingsDialog.isFullscreen();
                width = settingsDialog.getWindowWidth();
                height = settingsDialog.getWindowHeight();
            }
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