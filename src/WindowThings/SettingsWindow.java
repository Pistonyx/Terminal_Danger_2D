package WindowThings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

public class SettingsWindow extends JDialog {
    private JCheckBox fullscreenCheckBox;
    private JComboBox<String> resolutionComboBox;
    private boolean settingsApplied = false;

    private boolean currentFullscreen = false;
    private int currentWidth = 900;
    private int currentHeight = 650;

    // Map to store resolution strings and their Dimension objects
    private final Map<String, Dimension> resolutions = new LinkedHashMap<>();

    public SettingsWindow(Frame owner) {
        super(owner, "Settings", true); // Modal dialog
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 300);

        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // Initialize resolutions
        resolutions.put("800x600 (4:3)", new Dimension(800, 600));
        resolutions.put("1024x768 (4:3)", new Dimension(1024, 768));
        resolutions.put("1280x720 (16:9)", new Dimension(1280, 720));
        resolutions.put("1920x1080 (16:9)", new Dimension(1920, 1080));
        resolutions.put("900x650 (Default)", new Dimension(900, 650)); // Add default as an option

        // Main settings panel
        JPanel settingsPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        settingsPanel.setOpaque(false); // Keep transparent to show dialog background

        // Fullscreen checkbox
        fullscreenCheckBox = new JCheckBox("Fullscreen");
        fullscreenCheckBox.setFont(new Font("Monospaced", Font.PLAIN, 14));
        fullscreenCheckBox.setOpaque(false); // Keep transparent to show dialog background
        fullscreenCheckBox.setForeground(Color.BLACK); // Changed text color to BLACK
        settingsPanel.add(fullscreenCheckBox);

        // Resolution dropdown
        JPanel resolutionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        resolutionPanel.setOpaque(false); // Keep transparent to show dialog background
        JLabel resolutionLabel = new JLabel("Resolution:");
        resolutionLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resolutionLabel.setForeground(Color.BLACK); // Changed text color to BLACK
        resolutionComboBox = new JComboBox<>(resolutions.keySet().toArray(new String[0]));
        resolutionComboBox.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resolutionComboBox.setSelectedItem("900x650 (Default)"); // Set default selection
        resolutionPanel.add(resolutionLabel);
        resolutionPanel.add(resolutionComboBox);
        settingsPanel.add(resolutionPanel);

        add(settingsPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false); // Keep transparent to show dialog background

        JButton applyButton = new JButton("Apply");
        CustomButton.changeStyle(applyButton, null);
        applyButton.addActionListener(e -> {
            applySettings();
            settingsApplied = true;
            dispose(); // Close dialog after applying
        });
        buttonPanel.add(applyButton);

        JButton exitButton = new JButton("Exit");
        CustomButton.changeStyle(exitButton, null);
        exitButton.addActionListener(e -> {
            settingsApplied = false; // Indicate settings were not applied
            dispose(); // Close dialog without applying
        });
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Set dialog background to WHITE
        getContentPane().setBackground(Color.WHITE); // Changed background to WHITE
    }

    private void applySettings() {
        currentFullscreen = fullscreenCheckBox.isSelected();
        String selectedResolution = (String) resolutionComboBox.getSelectedItem();
        Dimension dim = resolutions.get(selectedResolution);
        if (dim != null) {
            currentWidth = dim.width;
            currentHeight = dim.height;
        }
    }

    public boolean isSettingsApplied() {
        return settingsApplied;
    }

    public boolean isFullscreen() {
        return currentFullscreen;
    }

    public int getWindowWidth() {
        return currentWidth;
    }

    public int getWindowHeight() {
        return currentHeight;
    }
}