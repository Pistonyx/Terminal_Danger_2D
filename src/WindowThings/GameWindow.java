package WindowThings;

import Commands.*;
import Playuh.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is used to create the game window
 */
public class GameWindow {
    // This is the main window of the game
    private final JFrame frame;
    private final GameOutputPanel gameOutputPanel;
    private final GameInputPanel inputPanel;
    private final GameMapPanel mapPanel;

    // This stores the movement keys that are currently being held down
    private final Set<Integer> pressedMovementKeys = new HashSet<>();

    // This timer updates the map movement smoothly instead of relying on keyboard repeat delay
    private Timer movementTimer;

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
        startMovementTimer();

        initializeGame();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        SwingUtilities.invokeLater(inputPanel::focusInput);
    }

    // This method is used to set up the key bindings for smooth free map movement and interaction
    private void setupKeyBindings() {
        InputMap im = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = frame.getRootPane().getActionMap();

        bindMovementKey(im, am, KeyEvent.VK_W, "W");
        bindMovementKey(im, am, KeyEvent.VK_A, "A");
        bindMovementKey(im, am, KeyEvent.VK_S, "S");
        bindMovementKey(im, am, KeyEvent.VK_D, "D");

        // This key binding lets the player press I to interact with the hot zone they are standing on
        bindInteractKey(im, am);
    }

    // This method keeps track of when a movement key is pressed and released
    private void bindMovementKey(InputMap im, ActionMap am, int keyCode, String keyText) {
        String pressedAction = "pressed " + keyText;
        String releasedAction = "released " + keyText;

        im.put(KeyStroke.getKeyStroke(keyCode, 0, false), pressedAction);
        im.put(KeyStroke.getKeyStroke(keyCode, 0, true), releasedAction);

        am.put(pressedAction, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!inputPanel.isInputFocused()) {
                    pressedMovementKeys.add(keyCode);
                }
            }
        });

        am.put(releasedAction, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pressedMovementKeys.remove(keyCode);
            }
        });
    }

    // This method lets the player press I to interact with characters, items, and interactable spots
    private void bindInteractKey(InputMap im, ActionMap am) {
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_I, 0, false), "interactWithHotZone");

        am.put("interactWithHotZone", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!inputPanel.isInputFocused()) {
                    handleHotZoneInteraction();
                }
            }
        });
    }

    // This method moves the player many times per second, which makes movement smoother
    private void startMovementTimer() {
        movementTimer = new Timer(16, e -> updatePlayerMovement());
        movementTimer.start();
    }

    // This method calculates movement from the currently held keys
    private void updatePlayerMovement() {
        if (inputPanel.isInputFocused()) {
            pressedMovementKeys.clear();
            return;
        }

        double dx = 0;
        double dy = 0;
        double speed = 4;

        if (pressedMovementKeys.contains(KeyEvent.VK_W)) {
            dy -= speed;
        }
        if (pressedMovementKeys.contains(KeyEvent.VK_S)) {
            dy += speed;
        }
        if (pressedMovementKeys.contains(KeyEvent.VK_A)) {
            dx -= speed;
        }
        if (pressedMovementKeys.contains(KeyEvent.VK_D)) {
            dx += speed;
        }

        // This keeps diagonal movement from being faster than normal movement
        if (dx != 0 && dy != 0) {
            dx *= 0.7071;
            dy *= 0.7071;
        }

        if (dx != 0 || dy != 0) {
            mapPanel.movePlayerPixels(dx, dy);
        }
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

        String action = input.trim().toLowerCase();

        if (action.equals("quit")) {
            appendLine("Thanks for playing.");
            inputPanel.setInputEnabled(false);
            return;
        }

        // Checks if the player is in the right editable hot zone to move from room to room
        if (action.equals("n") && !mapPanel.isOnHotZone(HotZoneType.NEXT_ROOM)) {
            appendLine("You're not in the right spot.");
            return;
        }

        if (action.equals("p") && !mapPanel.isOnHotZone(HotZoneType.PREV_ROOM)) {
            appendLine("You're not in the right spot.");
            return;
        }

        // If the player types i, interact with the hot zone they are currently standing on
        if (action.equals("i")) {
            handleHotZoneInteraction();
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

            mapPanel.resetPlayerPosition();
            showCurrentRoom();
            refreshMap();
        } catch (Exception e) {
            appendLine("Error: " + e.getMessage());
        }
    }

    // This method runs interaction logic based on the hot zone the player is standing on
    private void handleHotZoneInteraction() {
        if (!canUseCurrentRoom()) {
            appendLine("You are not in a valid room.");
            return;
        }

        HotZone hotZone = getCurrentInteractableHotZone();

        if (hotZone == null) {
            appendLine("There is nothing to interact with here.");
            return;
        }

        switch (hotZone.getType()) {
            case CHARACTER -> interactWithCharacterHotZone(hotZone);
            case ITEM, SEARCH -> interactWithItemHotZone(hotZone);
            case SAFE -> interactWithSafeHotZone(hotZone);
            case WATER -> interactWithWaterHotZone(hotZone);
            case CUSTOM -> interactWithCustomHotZone(hotZone);
            default -> appendLine("This hot zone cannot be interacted with.");
        }

        refreshMap();
    }

    // This method finds the interactable hot zone the player is currently standing on
    private HotZone getCurrentInteractableHotZone() {
        HotZone characterHotZone = mapPanel.getCurrentHotZone(HotZoneType.CHARACTER);
        if (characterHotZone != null) {
            return characterHotZone;
        }

        HotZone searchHotZone = mapPanel.getCurrentHotZone(HotZoneType.SEARCH);
        if (searchHotZone != null) {
            return searchHotZone;
        }

        HotZone itemHotZone = mapPanel.getCurrentHotZone(HotZoneType.ITEM);
        if (itemHotZone != null) {
            return itemHotZone;
        }

        HotZone safeHotZone = mapPanel.getCurrentHotZone(HotZoneType.SAFE);
        if (safeHotZone != null) {
            return safeHotZone;
        }

        HotZone waterHotZone = mapPanel.getCurrentHotZone(HotZoneType.WATER);
        if (waterHotZone != null) {
            return waterHotZone;
        }

        return mapPanel.getCurrentHotZone(HotZoneType.CUSTOM);
    }

    // This method runs the normal interact command for a character hot zone
    private void interactWithCharacterHotZone(HotZone hotZone) {
        if (!canUseCurrentRoom()) {
            appendLine("You are not in a valid room.");
            return;
        }

        Room current = rooms.get(player.currentRoomIndex);

        // If the character hot zone has a targetId, use it to find the correct character
        if (hotZone.targetId != null && !hotZone.targetId.isBlank()) {
            Playuh.Character character = findCharacterById(hotZone.targetId);

            if (character == null) {
                appendLine("That character could not be found.");
                return;
            }

            current.npc = character;
        }

        GameCommand interactCommand = commandMap.get("i");

        if (interactCommand == null) {
            appendLine("No interact command is available.");
            return;
        }

        String result = interactCommand.execute(player, rooms, items);
        if (result != null && !result.isBlank()) {
            appendLine(result);
        }
    }

    // This method lets an item/search hot zone give the player an item or show item information
    private void interactWithItemHotZone(HotZone hotZone) {
        if (hotZone.targetId == null || hotZone.targetId.isBlank()) {
            appendLine("This search spot does not have a target item.");
            return;
        }

        Item item = findItemById(hotZone.targetId);

        if (item == null) {
            appendLine("That item could not be found.");
            return;
        }

        if (player.hasItem(item.name)) {
            appendLine(item.description != null && !item.description.isBlank()
                    ? item.description
                    : "You already have " + item.name + ".");
            return;
        }

        if (player.isInventoryFull()) {
            appendLine("Your inventory is full. Drop something before taking " + item.name + ".");
            return;
        }

        player.inventory.add(item.name);
        appendLine("You searched the area and found: " + item.name);
    }

    // This method runs logic for safe hot zones
    private void interactWithSafeHotZone(HotZone hotZone) {
        if (player.hasItem("Small key")) {
            appendLine("You unlock the safe with the small key.");
        } else {
            appendLine("The safe is locked. Maybe there is a small key somewhere.");
        }
    }


    // This method runs logic for water hot zones
    private void interactWithWaterHotZone(HotZone hotZone) {
        if (player.hasItem("Empty water bottle")) {
            player.replaceItem("Empty water bottle", "Full water bottle");
            appendLine("You filled the water bottle.");
        } else if (player.hasItem("Full water bottle")) {
            appendLine("Your water bottle is already full.");
        } else {
            appendLine("There is water here, but you need something to carry it.");
        }
    }

    // This method runs default logic for custom interactable hot zones
    private void interactWithCustomHotZone(HotZone hotZone) {
        if (hotZone.label != null && !hotZone.label.isBlank()) {
            appendLine("You interact with " + hotZone.label + ".");
        } else {
            appendLine("You interact with the spot.");
        }
    }


    // This method finds an item from gamedata.json by its id
    private Item findItemById(String id) {
        if (items == null) {
            return null;
        }

        for (Item item : items) {
            if (item != null && item.id != null && item.id.equals(id)) {
                return item;
            }
        }

        return null;
    }

    // This method finds a character from gamedata.json by its id
    private Playuh.Character findCharacterById(String id) {
        if (data == null || data.characters == null) {
            return null;
        }

        for (Playuh.Character character : data.characters) {
            if (character != null && character.id != null && character.id.equals(id)) {
                return character;
            }
        }

        return null;
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

        appendLine("Use WASD to move freely on the map.");
        appendLine("Press I while standing on a hot zone to interact.");
        appendLine("Type 'h' for help.");

        if (current.npc != null) {
            appendLine("[!] " + current.npc.name + " is here. Press 'i' to interact.");
        }

        appendLine("Inventory: " + player.inventory);
    }

    // This method is used to refresh the map hot zones for the current room
    private void refreshMap() {
        if (!canUseCurrentRoom()) {
            mapPanel.setHotZones(new ArrayList<>());
            return;
        }

        Room current = rooms.get(player.currentRoomIndex);
        mapPanel.setHotZones(current.getHotZones());
        mapPanel.repaint();
    }

    private void appendLine(String text) {
        gameOutputPanel.appendLine(text);
    }
}