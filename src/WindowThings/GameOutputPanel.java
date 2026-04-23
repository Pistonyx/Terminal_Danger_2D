package WindowThings;

import javax.swing.*;
import java.awt.*;

public class GameOutputPanel extends JPanel {
    private final JTextArea gameOutput;

    /**
     * Constructs a GameOutputPanel with a text area for displaying game output.
     */
    public GameOutputPanel() {
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
        gameOutput.setBackground(Color.BLACK);
        gameOutput.setForeground(Color.GREEN);
        gameOutput.setFont(new Font("Monospaced", Font.PLAIN, 16));
        gameOutput.setLineWrap(true);
        gameOutput.setWrapStyleWord(true);
        // Adds the text area to the panel
        JScrollPane scrollPane = new JScrollPane(gameOutput);
        add(scrollPane, BorderLayout.CENTER);
    }
        // Appends a line of text to the game output area.
    public void appendLine(String text) {
        gameOutput.append(text + "\n");
        gameOutput.setCaretPosition(gameOutput.getDocument().getLength());
    }
}
