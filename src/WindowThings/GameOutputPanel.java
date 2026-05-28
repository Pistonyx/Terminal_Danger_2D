package WindowThings;

import javax.swing.*;
import java.awt.*;

public class GameOutputPanel extends JPanel {
    private final JTextArea gameOutput;
    private Image backgroundImage;

    /**
     * Constructs a GameOutputPanel with a text area for displaying game output.
     */
    public GameOutputPanel(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
        // Sets the panel's layout and border'
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 180, 0)),
                "Text Log"
        ));
        // Creates a text area for displaying game output
        gameOutput = new JTextArea();
        gameOutput.setEditable(false);
        gameOutput.setFocusable(false);
        gameOutput.setBackground(Color.BLACK); // Set to solid black
        gameOutput.setOpaque(true); // Ensure it's opaque
        gameOutput.setForeground(Color.GREEN);
        gameOutput.setFont(new Font("Monospaced", Font.PLAIN, 16));
        gameOutput.setLineWrap(true);
        gameOutput.setWrapStyleWord(true);
        gameOutput.setSelectionColor(new Color(0, 255, 0, 70)); // Semi-transparent green selection color
        gameOutput.setSelectedTextColor(Color.WHITE); // Make selected text white for contrast
        // Adds the text area to the panel
        JScrollPane scrollPane = new JScrollPane(gameOutput);
        scrollPane.setOpaque(false); // Make scroll pane transparent
        scrollPane.getViewport().setOpaque(false); // Make viewport transparent
        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        GameTextures.paintBackground(g, this, backgroundImage);
    }

    // Appends a line of text to the game output area.
    public void appendLine(String text) {
        gameOutput.append(text + "\n");
        gameOutput.setCaretPosition(gameOutput.getDocument().getLength());
    }
}