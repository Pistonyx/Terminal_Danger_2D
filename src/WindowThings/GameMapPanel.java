package WindowThings;

import javax.swing.*;
import java.awt.*;

/**
 * A panel that visually represents a game map grid with a player's position
 * and optional room hotspots. The map consists of a 5x5 grid where a player
 * can move and interact with specified hotspot areas.
 *
 * This class extends {@code JPanel} and includes methods to set the player's position,
 * update hotspots, and render the game map dynamically.
 */
public class GameMapPanel extends JPanel {
    private int playerX = 2;
    private int playerY = 2;

    private boolean nextRoomHotspot;
    private boolean prevRoomHotspot;

    // Constructor
    public GameMapPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(900, 300));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 180, 0)),
                "Map"
        ));
        setBackground(Color.WHITE);
    }
    // Method to set the player's position and hotspots'
    public void setPlayerPosition(int x, int y) {
        this.playerX = x;
        this.playerY = y;
        repaint();
    }
    // Method to update hotspots' visibility'
    public void setHotspots(boolean nextRoomHotspot, boolean prevRoomHotspot) {
        this.nextRoomHotspot = nextRoomHotspot;
        this.prevRoomHotspot = prevRoomHotspot;
        repaint();
    }
    // Method to move the player
    public void movePlayer(int dx, int dy) {
        int newX = playerX + dx;
        int newY = playerY + dy;

        if (newX < 0 || newX > 4 || newY < 0 || newY > 4) {
            return;
        }

        playerX = newX;
        playerY = newY;
        repaint();
    }
    // Methods to check if the player is on a hotspot
    public boolean isOnNextRoomHotspot() {
        return playerX == 4 && playerY == 2;
    }

    public boolean isOnPrevRoomHotspot() {
        return playerX == 0 && playerY == 2;
    }
    // Method to render the map
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Define the size and position of the grid
            int cellSize = 50;
            int gridWidth = cellSize * 5;
            int gridHeight = cellSize * 5;

            // Center the grid in the panel
            int startX = (getWidth() - gridWidth) / 2;
            int startY = (getHeight() - gridHeight) / 2;

            // Give the grid a background colour
            g2.setColor(new Color(25, 25, 25));
            g2.fillRect(startX - 8, startY - 8, gridWidth + 16, gridHeight + 16);

            // Draw the grid cells
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 5; x++) {
                    int px = startX + (x * cellSize);
                    int py = startY + (y * cellSize);

                    g2.setColor(new Color(40, 40, 40));
                    g2.drawRect(px, py, cellSize, cellSize);

                    // Draw hotspots if available
                    if (x == 4 && y == 2 && nextRoomHotspot) {
                        g2.setColor(new Color(0, 180, 0));
                        g2.drawString("N", px + cellSize / 2 - 4, py + cellSize / 2 + 5);
                    } else if (x == 0 && y == 2 && prevRoomHotspot) {
                        g2.setColor(new Color(0, 180, 0));
                        g2.drawString("P", px + cellSize / 2 - 4, py + cellSize / 2 + 5);
                    }
                }
            }
            // Draw the player's position'
            int playerPx = startX + (playerX * cellSize) + 16;
            int playerPy = startY + (playerY * cellSize) + 16;

            g2.setColor(new Color(0, 255, 0));
            g2.fillRect(playerPx, playerPy, 18, 18);
            g2.setColor(Color.BLACK);
            g2.drawRect(playerPx, playerPy, 18, 18);
        } finally {
            g2.dispose();
        }
    }
}
