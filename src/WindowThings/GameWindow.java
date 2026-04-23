package WindowThings;

import Commands.*;
import MainGame.Game;
import Playuh.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to create the game window
 */
public class GameWindow {
    // This is the main window of the game
    private final JFrame frame;
    private final GameOutputPanel gameOutputPanel;
    private final GameInputPanel inputPanel;
    private final GameMapPanel mapPanel;

    // This is the data of the game
    private GameData data;
    private ArrayList<Room> rooms;
    private ArrayList<Item> items;
    private Player player;
    private Map<String, GameCommand> commandMap;

    private PendingAction pendingAction;

    private enum PendingAction {
        NONE,
        DROP_ITEM,
        INSPECT_ITEM
    }

    /**
     * Creates a new instance of the game window.
     */
    public GameWindow() {
        // Create the main window
        frame = new JFrame("Terminal Danger");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 650);
        frame.setLayout(new BorderLayout(8, 8));

        // Create the main panel
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        frame.setContentPane(root);

        gameOutputPanel = new GameOutputPanel();
        mapPanel = new GameMapPanel();
        inputPanel = new GameInputPanel(this::processInput);

        // Create the split pane
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                mapPanel,
                gameOutputPanel
        );
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(300);
        splitPane.setEnabled(false);

        root.add(splitPane, BorderLayout.CENTER);
        root.add(inputPanel, BorderLayout.SOUTH);

        // Unfocus the typing area when the mouse is clicked anywhere else
        MouseAdapter unfocusTyping = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                inputPanel.unfocusInput();
                root.requestFocusInWindow();
            }
        };
        // Add mouse listeners to all components
        root.addMouseListener(unfocusTyping);
        mapPanel.addMouseListener(unfocusTyping);
        gameOutputPanel.addMouseListener(unfocusTyping);
        splitPane.addMouseListener(unfocusTyping);

        setupKeyBindings();

        initializeGame();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        SwingUtilities.invokeLater(inputPanel::focusInput);
    }

    // This method is used to set up the key bindings for the map window
    private void setupKeyBindings() {
        InputMap im = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = frame.getRootPane().getActionMap();

        bindKey(im, am, "W", "moveUp", () -> {
            if (!inputPanel.isInputFocused()) mapPanel.movePlayer(0, -1);
        });
        bindKey(im, am, "S", "moveDown", () -> {
            if (!inputPanel.isInputFocused()) mapPanel.movePlayer(0, 1);
        });
        bindKey(im, am, "A", "moveLeft", () -> {
            if (!inputPanel.isInputFocused()) mapPanel.movePlayer(-1, 0);
        });
        bindKey(im, am, "D", "moveRight", () -> {
            if (!inputPanel.isInputFocused()) mapPanel.movePlayer(1, 0);
        });
    }


    private void bindKey(InputMap im, ActionMap am, String key, String actionName, Runnable action) {
        im.put(KeyStroke.getKeyStroke(key), actionName);
        am.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }

    // This method is used to initialize the game data
    private void initializeGame() {
        // Load the game data from /gamedata.json
        try {
            data = GameData.loadGamaDataFromResources("/gamedata.json");
        } catch (RuntimeException e) {
            appendLine("Failed to load game data: " + e.getMessage());
            inputPanel.setInputEnabled(false);
            return;
        }

        rooms = data.locations != null ? data.locations : new ArrayList<>();
        items = data.items != null ? data.items : new ArrayList<>();
        player = new Player("Timofey Mufasa");
        commandMap = new HashMap<>();

        commandMap.put("h", new HelpCommand("commands.txt"));
        commandMap.put("n", new MoveNextCommand());
        commandMap.put("p", new MovePrevCommand());
        commandMap.put("s", new SearchCommand());
        commandMap.put("i", new InteractCommand());

        appendLine("=== MISSION: THE CELLAR ASSASSINATION ===");
        showCurrentRoom();
        refreshMap();
    }

    // This method is used to process the input from the user
    private void processInput(String input) {
        appendLine("> " + input);

        if (pendingAction != PendingAction.NONE) {
            if (input.trim().matches("\\d+")) {
                handlePendingAction(input.trim());
                return;
            }
            pendingAction = PendingAction.NONE;
        }

        String action = input.toLowerCase();

        if (action.equals("quit")) {
            appendLine("Thanks for playing.");
            inputPanel.setInputEnabled(false);
            return;
        }

        // Cheks if the player is in the right gridx/gridy to move from room to room
        if (action.equals("n") && !mapPanel.isOnNextRoomHotspot()) {
            appendLine("You're not in the right spot.");
            return;
        }

        if (action.equals("p") && !mapPanel.isOnPrevRoomHotspot()) {
            appendLine("You're not in the right spot.");
            return;
        }

        GameCommand command = commandMap.get(action);
        if (command == null) {
            appendLine("Unknown command. Type 'h' for help.");
            return;
        }

        try {
            String result = command.execute(player, rooms, items);
            if (result != null && !result.isBlank()) {
                appendLine(result);
            }
            showCurrentRoom();
            refreshMap();
        } catch (Exception e) {
            appendLine("Error: " + e.getMessage());
        }
    }

    private void handlePendingAction(String input) {
        int index = Integer.parseInt(input);
        pendingAction = PendingAction.NONE;
        showCurrentRoom();
        refreshMap();
    }

    private boolean canUseCurrentRoom() {
        return player.currentRoomIndex >= 0 && player.currentRoomIndex < rooms.size();
    }

    // This method is used to show the current room
    private void showCurrentRoom() {
        if (!canUseCurrentRoom()) return;

        Room current = rooms.get(player.currentRoomIndex);
        appendLine("");
        appendLine("--- " + current.name + " ---");

        if (current.description != null && !current.description.isBlank()) {
            appendLine(current.description);
        }

        appendLine("Use WASD to move the cube on the map.");
        appendLine("Type 'h' for help.");

        if (current.npc != null) {
            appendLine("[!] " + current.npc.name + " is here. Press 'i' to interact.");
        }

        appendLine("Inventory: " + player.inventory);
    }

    // This method is used to refresh the map
    private void refreshMap() {
        mapPanel.setHotspots(true, true);
        mapPanel.repaint();
    }

    private void appendLine(String text) {
        gameOutputPanel.appendLine(text);
    }
}