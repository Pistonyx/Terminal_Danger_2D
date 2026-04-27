package WindowThings;

import Playuh.HotZone;
import Playuh.HotZoneType;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A map with a player's position
 * and optional room hot zones. The map uses a 5x5 grid for hot zones,
 * but the player can move around freely instead of only moving from grid to grid.
 *
 * This class extends {@code JPanel} and includes methods to move the player,
 * update hot zones, check if the player is on a hot zone, and render the map dynamically.
 */
public class GameMapPanel extends JPanel {
    private static final int GRID_COLUMNS = 5;
    private static final int GRID_ROWS = 5;
    private static final int CELL_SIZE = 50;
    private static final int PLAYER_SIZE = 20;

    private double playerX = 2.5;
    private double playerY = 2.5;

    private List<HotZone> hotZones = new ArrayList<>();

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

    // Method to reset the player's position to the middle of the map
    public void resetPlayerPosition() {
        playerX = 2.5;
        playerY = 2.5;
        repaint();
    }

    // Method to update the hot zones for the current room
    public void setHotZones(List<HotZone> hotZones) {
        if (hotZones == null) {
            this.hotZones = new ArrayList<>();
        } else {
            this.hotZones = new ArrayList<>(hotZones);
        }

        repaint();
    }

    // Method to move the player freely instead of moving only by grid spaces
    public void movePlayerPixels(double dx, double dy) {
        double newX = playerX + (dx / CELL_SIZE);
        double newY = playerY + (dy / CELL_SIZE);

        double minPosition = 0.0;
        double maxX = GRID_COLUMNS - ((double) PLAYER_SIZE / CELL_SIZE);
        double maxY = GRID_ROWS - ((double) PLAYER_SIZE / CELL_SIZE);

        playerX = Math.max(minPosition, Math.min(maxX, newX));
        playerY = Math.max(minPosition, Math.min(maxY, newY));

        repaint();
    }

    // Method to check if the player is standing on a specific type of hot zone
    public boolean isOnHotZone(HotZoneType type) {
        return getCurrentHotZone(type) != null;
    }

    // Method to get the hot zone the player is currently standing on
    public HotZone getCurrentHotZone(HotZoneType type) {
        int playerGridX = getPlayerGridX();
        int playerGridY = getPlayerGridY();

        for (HotZone hotZone : hotZones) {
            if (hotZone == null) {
                continue;
            }

            if (hotZone.gridX == playerGridX
                    && hotZone.gridY == playerGridY
                    && hotZone.getType() == type) {
                return hotZone;
            }
        }

        return null;
    }

    // Method to convert the player's free movement X position into a grid X position
    public int getPlayerGridX() {
        return Math.max(0, Math.min(GRID_COLUMNS - 1, (int) Math.floor(playerX)));
    }

    // Method to convert the player's free movement Y position into a grid Y position
    public int getPlayerGridY() {
        return Math.max(0, Math.min(GRID_ROWS - 1, (int) Math.floor(playerY)));
    }

    // Method to choose a colour for each hot zone type
    private Color getHotZoneColor(HotZoneType type) {
        return switch (type) {
            case NEXT_ROOM, PREV_ROOM -> new Color(0, 180, 0);
            case SAFE -> new Color(230, 180, 30);
            case WATER -> new Color(30, 140, 230);
            case CHARACTER -> new Color(180, 80, 230);
            case ITEM -> new Color(230, 90, 90);
            case SEARCH -> new Color(90, 210, 120);
            case CUSTOM -> new Color(220, 220, 220);
            default -> new Color(220, 220, 220);
        };
    }

    // Method to choose the text shown inside a hot zone
    private String getHotZoneLabel(HotZone hotZone) {
        if (hotZone.label != null && !hotZone.label.isBlank()) {
            return hotZone.label;
        }

        return switch (hotZone.getType()) {
            case NEXT_ROOM -> "N";
            case PREV_ROOM -> "P";
            case SAFE -> "S";
            case WATER -> "W";
            case CHARACTER -> "C";
            case ITEM -> "I";
            case SEARCH -> "S";
            case CUSTOM -> "?";
            default -> "?";
        };
    }

    // Method to render the map
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Define the size and position of the grid
            int gridWidth = CELL_SIZE * GRID_COLUMNS;
            int gridHeight = CELL_SIZE * GRID_ROWS;

            // Center the grid in the panel
            int startX = (getWidth() - gridWidth) / 2;
            int startY = (getHeight() - gridHeight) / 2;

            // Give the grid a background colour
            g2.setColor(new Color(25, 25, 25));
            g2.fillRect(startX - 8, startY - 8, gridWidth + 16, gridHeight + 16);

            // Draw the grid cells
            for (int y = 0; y < GRID_ROWS; y++) {
                for (int x = 0; x < GRID_COLUMNS; x++) {
                    int px = startX + (x * CELL_SIZE);
                    int py = startY + (y * CELL_SIZE);

                    g2.setColor(new Color(40, 40, 40));
                    g2.drawRect(px, py, CELL_SIZE, CELL_SIZE);
                }
            }

            // Draw the hot zones from the current room
            for (HotZone hotZone : hotZones) {
                if (hotZone == null) {
                    continue;
                }

                if (hotZone.gridX < 0 || hotZone.gridX >= GRID_COLUMNS
                        || hotZone.gridY < 0 || hotZone.gridY >= GRID_ROWS) {
                    continue;
                }

                int px = startX + (hotZone.gridX * CELL_SIZE);
                int py = startY + (hotZone.gridY * CELL_SIZE);

                g2.setColor(getHotZoneColor(hotZone.getType()));
                g2.fillRoundRect(px + 6, py + 6, CELL_SIZE - 12, CELL_SIZE - 12, 10, 10);

                g2.setColor(Color.BLACK);
                g2.drawRoundRect(px + 6, py + 6, CELL_SIZE - 12, CELL_SIZE - 12, 10, 10);

                g2.setColor(Color.WHITE);
                g2.drawString(getHotZoneLabel(hotZone), px + CELL_SIZE / 2 - 4, py + CELL_SIZE / 2 + 5);
            }

            // Draw the player's position using free movement coordinates
            // The player is drawn after the hot zones so it is always visible on top.
            int playerPx = startX + (int) Math.round(playerX * CELL_SIZE);
            int playerPy = startY + (int) Math.round(playerY * CELL_SIZE);

            g2.setColor(new Color(0, 255, 80));
            g2.fillOval(playerPx, playerPy, PLAYER_SIZE, PLAYER_SIZE);

            g2.setColor(Color.BLACK);
            g2.drawOval(playerPx, playerPy, PLAYER_SIZE, PLAYER_SIZE);

            g2.setColor(Color.BLACK);
            g2.drawString("P", playerPx + 7, playerPy + 14);

            // Draw the current grid position for testing/editing hot zones
            g2.setColor(Color.WHITE);
            g2.drawString("Grid: " + getPlayerGridX() + ", " + getPlayerGridY(), startX, startY + gridHeight + 22);
        } finally {
            g2.dispose();
        }
    }
}