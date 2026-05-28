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
import java.util.Random; // Added for random sequence generation
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

    // Background images
    private final Image windowBackground;

    // Minigame state variables
    private String minigameSequence;
    private long minigameStartTime;
    private boolean leverFixed = false;

    // Storage state variables "store" or "take"
    private String storageMode = "";

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
        INSPECT_ITEM,
        MINIGAME_BROKEN_LEVER, // Broken lever mini-game
        CONFIRM_MINIGAME_BROKEN_LEVER, // Confirmation step
        CODE_ENTRY, // Cellar code entry
        STORAGE_ACTION, // Choose to store or take
        STORAGE_ITEM_SELECTION // Select item for storage action
    }

    /**
     * Creates a new instance of the game window.
     */
    public GameWindow() {
        // Load background images
        windowBackground = GameTextures.loadImage("/images/window_bg.png"); // Placeholder path
        Image mapBackground = GameTextures.loadImage("/images/map_bg.png"); // Placeholder path
        Image outputBackground = GameTextures.loadImage("/images/output_bg.png"); // Placeholder path
        Image inputBackground = GameTextures.loadImage("/images/input_bg.png"); // Placeholder path

        // Create the main window
        frame = new JFrame("Terminal Danger");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 650);
        frame.setLayout(new BorderLayout(8, 8));

        // Create the main panel
        JPanel root = new JPanel(new BorderLayout(8, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                GameTextures.paintBackground(g, this, windowBackground);
            }
        };
        root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        frame.setContentPane(root);

        gameOutputPanel = new GameOutputPanel(outputBackground);
        mapPanel = new GameMapPanel(mapBackground);
        inputPanel = new GameInputPanel(this::processInput, inputBackground);

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

    // Set up the key bindings for smooth map movement and interaction
    private void setupKeyBindings() {
        InputMap im = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = frame.getRootPane().getActionMap();

        bindMovementKey(im, am, KeyEvent.VK_W, "W");
        bindMovementKey(im, am, KeyEvent.VK_A, "A");
        bindMovementKey(im, am, KeyEvent.VK_S, "S");
        bindMovementKey(im, am, KeyEvent.VK_D, "D");

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

    // This method lets the player press I to interact with characters, items and interactable spots
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
        commandMap.put("i", new InteractCommand());
        commandMap.put("items", new ItemInteract());

        appendLine("=== MISSION: THE CELLAR ASSASSINATION ===");
        showCurrentRoom();
        refreshMap();
    }

    // This method is used to process the input from the user
    private void processInput(String input) {
        appendLine("> " + input);

        if (pendingAction != PendingAction.NONE) {
            // Handle numeric input for ItemInteract or storage selection
            if ((pendingAction == PendingAction.INSPECT_ITEM || pendingAction == PendingAction.STORAGE_ITEM_SELECTION) && input.trim().matches("\\d+")) {
                handlePendingAction(input.trim());
                return;
            }
            // Handle text input for broken lever minigame or confirm minigame or code entry or storage action
            else if (pendingAction == PendingAction.MINIGAME_BROKEN_LEVER || pendingAction == PendingAction.CONFIRM_MINIGAME_BROKEN_LEVER || pendingAction == PendingAction.CODE_ENTRY || pendingAction == PendingAction.STORAGE_ACTION) {
                handlePendingAction(input.trim());
                return;
            }
            // If input is not a number and not for minigame/confirmation/code entry/storage, clear pending action and process as normal command
            pendingAction = PendingAction.NONE;
        }

        String action = input.trim().toLowerCase();

        if (action.equals("quit")) {
            appendLine("Thanks for playing.");
            inputPanel.setInputEnabled(false);
            return;
        }

        // Cellar access logic
        // Moving from balcony (index 6) to cellar (index 7)
        if (action.equals("n") && player.currentRoomIndex == 6) {
            if (player.cellarUnlocked || player.leonHelped) {
                // Already unlocked or Leon helped, proceed normally
                GameCommand moveCommand = commandMap.get("n");
                try {
                    String result = moveCommand.execute(player, rooms, items);
                    if (result != null && !result.isBlank()) {
                        appendLine(result);
                    }
                    showCurrentRoom();
                    refreshMap(player.lastEntryHotZoneType);
                } catch (Exception e) {
                    appendLine("Error: " + e.getMessage());
                }
            } else if (player.hasItem("Code")) {
                appendLine("You use the deciphered code to unlock the cellar door.");
                player.cellarUnlocked = true;
                GameCommand moveCommand = commandMap.get("n");
                try {
                    String result = moveCommand.execute(player, rooms, items);
                    if (result != null && !result.isBlank()) {
                        appendLine(result);
                    }
                    showCurrentRoom();
                    refreshMap(player.lastEntryHotZoneType);
                } catch (Exception e) {
                    appendLine("Error: " + e.getMessage());
                }
            } else {
                appendLine("The cellar door is locked. It seems to require a code.");
                appendLine("Enter the code, or type 'cancel' to stop.");
                pendingAction = PendingAction.CODE_ENTRY;
            }
            return;
        }


        // Checks if the player is in the right hot zone to move from room to room
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

        // Handle 's' command for hotzones
        if (action.equals("s")) {
            if (!canUseCurrentRoom()) {
                appendLine("You are not in a valid room to search.");
                return;
            }

            HotZone searchHotZone = mapPanel.getCurrentHotZone(HotZoneType.SEARCH);
            HotZone itemHotZone = mapPanel.getCurrentHotZone(HotZoneType.ITEM);

            if (searchHotZone != null) {
                interactWithItemHotZone(rooms.get(player.currentRoomIndex), searchHotZone); // Pass currentRoom
            } else if (itemHotZone != null) {
                interactWithItemHotZone(rooms.get(player.currentRoomIndex), itemHotZone); // Pass currentRoom
            } else {
                appendLine("There is nothing to search here.");
            }
            return;
        }

        // Cellar final decision logic
        if (player.currentRoomIndex == 7) { // In the cellar
            if (action.equals("k")) {
                appendLine("You decide to kill the criminal. The mission is complete, but at what cost?");
                appendLine("--- GAME OVER: A Bloody End ---");
                inputPanel.setInputEnabled(false);
                return;
            } else if (action.equals("s")) {
                appendLine("You decide to spare the criminal. They flee, and you are left to ponder your choice.");
                appendLine("--- GAME OVER: A Moral Dilemma ---");
                inputPanel.setInputEnabled(false);
                return;
            }
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

            // If the command was "items", set pending action to INSPECT_ITEM
            if (command instanceof ItemInteract) {
                pendingAction = PendingAction.INSPECT_ITEM;
            } else {
                if (command instanceof MoveNextCommand || command instanceof MovePrevCommand) {
                    showCurrentRoom(); // Show new room description
                    refreshMap(player.lastEntryHotZoneType); // Pass the entry hotzone type
                } else {
                    showCurrentRoom(); // Refresh output panel
                    refreshMap(); // Just repaint map with updated hotzones
                }
            }
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
        Room currentRoom = rooms.get(player.currentRoomIndex); // Get current room here

        if (hotZone == null) {
            appendLine("There is nothing to interact with here.");
            return;
        }

        switch (hotZone.getType()) {
            case CHARACTER -> interactWithCharacterHotZone(hotZone);
            case ITEM, SEARCH -> interactWithItemHotZone(currentRoom, hotZone); // Pass currentRoom
            case SAFE -> interactWithSafeHotZone(hotZone);
            case WATER -> interactWithWaterHotZone(hotZone);
            case CUSTOM -> interactWithCustomHotZone(hotZone);
            case STORAGE -> interactWithStorageHotZone(currentRoom); // New storage interaction
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

        HotZone storageHotZone = mapPanel.getCurrentHotZone(HotZoneType.STORAGE);
        if (storageHotZone != null) {
            return storageHotZone;
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

            // Leon's interaction logic
            if ("npc_leon".equals(hotZone.targetId)) {
                if (player.leonHelped) {
                    appendLine(character.dialogue); // Leon's post-help dialogue
                } else if (player.hasItem("Full water bottle")) {
                    player.removeItem("Full water bottle");
                    player.leonHelped = true;
                    appendLine("You give Leon the full water bottle. He chugs it down.");
                } else {
                    appendLine(character.dialogue); // Leon's initial dialogue
                }
            } else {
                appendLine(character.dialogue); // Generic character dialogue
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
    private void interactWithItemHotZone(Room currentRoom, HotZone hotZone) {
        System.out.println("DEBUG: interactWithItemHotZone called for: " + hotZone);
        System.out.println("DEBUG: HotZones in currentRoom BEFORE removal: " + currentRoom.getHotZones());

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
            appendLine("You already have " + item.name + ".");
            return;
        }

        if (player.isInventoryFull()) {
            appendLine("Your inventory is full. Drop something before taking " + item.name + ".");
            return;
        }

        player.inventory.add(item.name);
        appendLine("You searched the area and found: " + item.name);

        // Remove the hotzone after the item is acquired
        boolean removed = currentRoom.getHotZones().remove(hotZone);
        System.out.println("DEBUG: HotZone removed from room's list: " + removed);
        System.out.println("DEBUG: HotZones in currentRoom AFTER removal: " + currentRoom.getHotZones());
    }

    // This method runs logic for safe hot zones
    private void interactWithSafeHotZone(HotZone hotZone) {
        if (player.safeSolved) {
            appendLine("The safe is already open and empty.");
            return;
        }

        // Initial unlock with "Small key"
        if (player.safeProgress == 0) {
            if (player.hasItem("Small key")) {
                player.removeItem("Small key");
                player.safeProgress = 1; // Safe is now initially unlocked
                appendLine("You unlock the safe with the small key. The key breaks in the lock.");
                appendLine("The safe is now open, revealing a complex mechanism. It seems to require more items.");
            } else {
                appendLine("The safe is locked. Maybe there is a small key somewhere.");
            }
            return; // Exit after handling initial unlock
        }

        // Item insertion (only if safeProgress > 0)
        switch (player.safeProgress) {
            case 1: // Needs Rotating gear
                if (player.hasItem("Rotating gear")) {
                    player.removeItem("Rotating gear");
                    player.safeProgress = 2;
                    appendLine("You insert the Rotating gear into the safe mechanism. It clicks into place.");
                } else {
                    appendLine("The safe mechanism needs a 'Rotating gear'.");
                }
                break;
            case 2: // Needs Weighted cube
                if (player.hasItem("Weighted cube")) {
                    player.removeItem("Weighted cube");
                    player.safeProgress = 3;
                    appendLine("You place the Weighted cube on the pressure plate inside. Another click.");
                } else {
                    appendLine("The safe mechanism needs a 'Weighted cube'.");
                }
                break;
            case 3: // Needs Lever handle
                if (player.hasItem("Lever handle")) {
                    player.removeItem("Lever handle");
                    player.safeProgress = 4; // All items inserted
                    player.safeSolved = true;
                    player.inventory.add("Code"); // Player receives the 'Code'
                    appendLine("You attach the Lever handle and pull it firmly. With a final, satisfying thunk, the safe door swings open!");
                    appendLine("You found: Code");
                } else {
                    appendLine("The safe is almost open, but it needs a 'Lever handle' to complete the sequence.");
                }
                break;
            case 4: // Already solved (safeProgress will be 4 if safeSolved is true)
                appendLine("The safe is already open and empty.");
                break;
            default:
                appendLine("You examine the safe, but nothing seems to happen.");
                break;
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
        // Check for the broken lever minigame
        if ("broken_lever".equals(hotZone.targetId)) {
            if (leverFixed) {
                appendLine("The lever mechanism is already fixed. You can take the 'Lever handle' if you haven't already.");
                if (!player.hasItem("Lever handle") && !player.isInventoryFull()) {
                    player.inventory.add("Lever handle");
                    appendLine("You pick up the 'Lever handle'.");
                } else if (player.isInventoryFull()) {
                    appendLine("Your inventory is full. You can't pick up the 'Lever handle'.");
                }
                return;
            }

            // Only start the mini-game if the player has the broken lever handle
            if (!player.hasItem("Broken lever handle")) {
                appendLine("You examine the broken lever mechanism. It seems you need to find the 'Broken lever handle' first.");
                return;
            }

            // If not already confirming, ask for confirmation
            if (pendingAction != PendingAction.CONFIRM_MINIGAME_BROKEN_LEVER && pendingAction != PendingAction.MINIGAME_BROKEN_LEVER) {
                appendLine("A broken lever mechanism. To fix it, type the following 5 letters within 5 seconds."); // Moved this line
                appendLine("The broken lever mechanism looks dangerous. Do you want to attempt to fix it? (yes/no)");
                pendingAction = PendingAction.CONFIRM_MINIGAME_BROKEN_LEVER;
            } else if (pendingAction == PendingAction.MINIGAME_BROKEN_LEVER) {
                appendLine("You are already attempting to fix the lever. Type the sequence: " + minigameSequence);
            }
        } else if (hotZone.label != null && !hotZone.label.isBlank()) {
            appendLine("You interact with " + hotZone.label + ".");
        } else {
            appendLine("You interact with the spot.");
        }
    }

    // This method handles interaction with a storage hot zone
    private void interactWithStorageHotZone(Room currentRoom) {
        appendLine("You found a storage unit. Do you want to 'store' or 'take' items? (type 'cancel' to exit)");
        pendingAction = PendingAction.STORAGE_ACTION;
    }

    // Method to display item description
    private void displayItemDescription(int itemIndex) {
        if (itemIndex > 0 && itemIndex <= player.inventory.size()) {
            String itemName = player.inventory.get(itemIndex - 1);
            Item item = findItemByName(itemName);
            if (item != null && item.description != null && !item.description.isBlank()) {
                appendLine("--- " + item.name + " ---");
                appendLine(item.description);
            } else {
                appendLine("You examine the " + itemName + ", but find nothing remarkable.");
            }
        } else if (itemIndex == 0) {
            appendLine("Exiting inventory inspection.");
        } else {
            appendLine("Invalid item number.");
        }

        pendingAction = PendingAction.NONE; // Clear pending action after inspection
    }


    // This method finds an item from gamedata.json by its ID
    private Item findItemById(String id) {
        if (items == null) {
            return null;
        }

        for (Item item : items) {
            if (item != null && item.id != null && item.id.equals(id)) { // Compare by ID
                return item;
            }
        }

        return null;
    }

    // This method finds an item from gamedata.json by its name
    private Item findItemByName(String name) {
        if (items == null) {
            return null;
        }

        for (Item item : items) {
            if (item != null && item.name != null && item.name.equals(name)) { // Compare by name
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
        Room currentRoom = rooms.get(player.currentRoomIndex); // Get current room for storage actions

        switch (pendingAction) {
            case INSPECT_ITEM:
                try {
                    int index = Integer.parseInt(input);
                    displayItemDescription(index);
                } catch (NumberFormatException e) {
                    appendLine("Invalid input. Please enter a number (0-" + player.inventory.size() + ").");
                    pendingAction = PendingAction.NONE; // Clear pending action on invalid input
                }
                break;
            case CONFIRM_MINIGAME_BROKEN_LEVER:
                if (input.equalsIgnoreCase("yes")) {
                    Random random = new Random();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 5; i++) {
                        sb.append((char) ('A' + random.nextInt(26)));
                    }
                    minigameSequence = sb.toString();
                    minigameStartTime = System.currentTimeMillis();
                    pendingAction = PendingAction.MINIGAME_BROKEN_LEVER;
                    appendLine(">>> " + minigameSequence + " <<<"); // Only display sequence here
                } else if (input.equalsIgnoreCase("no")) {
                    appendLine("You decide not to attempt fixing the lever for now.");
                    pendingAction = PendingAction.NONE;
                } else {
                    appendLine("Invalid input. Please answer 'yes' or 'no'.");
                }
                break;
            case MINIGAME_BROKEN_LEVER:
                long elapsedTime = System.currentTimeMillis() - minigameStartTime;
                if (elapsedTime > 5000) { // Make the minigame time 5 seconds
                    appendLine("Time's up! You failed to type the sequence in time.");
                    pendingAction = PendingAction.NONE;
                } else if (input.equalsIgnoreCase(minigameSequence)) {
                    appendLine("Success! You quickly re-aligned the mechanism.");
                    // Remove broken lever handle, add fixed one
                    player.removeItem("Broken lever handle");
                    if (!player.isInventoryFull()) {
                        player.inventory.add("Lever handle");
                        appendLine("You obtained a 'Lever handle'.");
                    } else {
                        appendLine("You fixed the lever, but your inventory is full. You couldn't pick up the 'Lever handle'.");
                    }
                    leverFixed = true; // Mark as permanently fixed
                    pendingAction = PendingAction.NONE;
                } else {
                    appendLine("Incorrect sequence. Try again next time.");
                    pendingAction = PendingAction.NONE;
                }
                break;
            case CODE_ENTRY: // Handle code entry for cellar
                if (input.equalsIgnoreCase("SPSEJECNA")) { // Deciphered code for "TQTFKFDOB" with shift 1
                    appendLine("The code is correct! The cellar door unlocks with a heavy thud.");
                    player.cellarUnlocked = true;
                    // Now perform the move to the cellar
                    GameCommand moveCommand = commandMap.get("n");
                    try {
                        String result = moveCommand.execute(player, rooms, items);
                        if (result != null && !result.isBlank()) {
                            appendLine(result);
                        }
                    } catch (Exception e) {
                        appendLine("Error moving to cellar: " + e.getMessage());
                    }
                    pendingAction = PendingAction.NONE;
                } else if (input.equalsIgnoreCase("cancel")) {
                    appendLine("Cellar access cancelled.");
                    pendingAction = PendingAction.NONE;
                } else {
                    appendLine("Incorrect code. Try again, or type 'cancel' to stop.");
                }
                break;
            case STORAGE_ACTION: // Player chooses to store or take
                if (input.equalsIgnoreCase("store")) {
                    if (player.inventory.isEmpty()) {
                        appendLine("Your inventory is empty. Nothing to store.");
                        pendingAction = PendingAction.NONE;
                    } else {
                        StringBuilder sb = new StringBuilder("Your inventory:\n");
                        for (int i = 0; i < player.inventory.size(); i++) {
                            sb.append(String.format("%d. %s\n", i + 1, player.inventory.get(i)));
                        }
                        sb.append("Enter the number of the item to store, or 0 to cancel.");
                        appendLine(sb.toString());
                        storageMode = "store";
                        pendingAction = PendingAction.STORAGE_ITEM_SELECTION;
                    }
                } else if (input.equalsIgnoreCase("take")) {
                    if (currentRoom.storedItems.isEmpty()) {
                        appendLine("The storage unit is empty. Nothing to take.");
                        pendingAction = PendingAction.NONE;
                    } else {
                        StringBuilder sb = new StringBuilder("Items in storage:\n");
                        for (int i = 0; i < currentRoom.storedItems.size(); i++) {
                            sb.append(String.format("%d. %s\n", i + 1, currentRoom.storedItems.get(i)));
                        }
                        sb.append("Enter the number of the item to take, or 0 to cancel.");
                        appendLine(sb.toString());
                        storageMode = "take";
                        pendingAction = PendingAction.STORAGE_ITEM_SELECTION;
                    }
                } else if (input.equalsIgnoreCase("cancel")) {
                    appendLine("Storage interaction cancelled.");
                    pendingAction = PendingAction.NONE;
                } else {
                    appendLine("Invalid choice. Type 'store', 'take', or 'cancel'.");
                }
                break;
            case STORAGE_ITEM_SELECTION: // Player selected an item for storage action
                try {
                    int itemNum = Integer.parseInt(input);
                    if (itemNum == 0) {
                        appendLine("Storage item selection cancelled.");
                        pendingAction = PendingAction.NONE;
                        break;
                    }

                    if ("store".equals(storageMode)) {
                        if (itemNum > 0 && itemNum <= player.inventory.size()) {
                            String itemToStore = player.inventory.remove(itemNum - 1);
                            currentRoom.storedItems.add(itemToStore);
                            appendLine("You stored the " + itemToStore + ".");
                        } else {
                            appendLine("Invalid item number.");
                        }
                    } else if ("take".equals(storageMode)) {
                        if (itemNum > 0 && itemNum <= currentRoom.storedItems.size()) {
                            if (player.isInventoryFull()) {
                                appendLine("Your inventory is full. Cannot take more items.");
                            } else {
                                String itemToTake = currentRoom.storedItems.remove(itemNum - 1);
                                player.inventory.add(itemToTake);
                                appendLine("You took the " + itemToTake + " from storage.");
                            }
                        } else {
                            appendLine("Invalid item number.");
                        }
                    }
                    pendingAction = PendingAction.NONE; // Action completed
                } catch (NumberFormatException e) {
                    appendLine("Invalid input. Please enter a number or 0 to cancel.");
                }
                break;
            // Add other pending actions here if needed
            case NONE:
            case DROP_ITEM:
            default:
                appendLine("Unhandled pending action.");
                pendingAction = PendingAction.NONE;
                break;
        }

        // Only refresh room info if the pending action has been resolved
        if (pendingAction == PendingAction.NONE) {
            showCurrentRoom(); // Refresh display after action
            refreshMap(); // Refresh map after action
        }
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
        System.out.println("DEBUG: refreshMap (no-arg) passing hotzones to mapPanel: " + current.getHotZones());
        mapPanel.setHotZones(current.getHotZones());
        mapPanel.repaint();
    }

    // Overloaded method to refresh map AND set player position based on entry hotzone
    // This should ONLY be called when the room actually changes (e.g., MoveNext/MovePrev)
    private void refreshMap(HotZoneType entryType) {
        if (!canUseCurrentRoom()) {
            mapPanel.setHotZones(new ArrayList<>());
            return;
        }

        Room current = rooms.get(player.currentRoomIndex);
        System.out.println("DEBUG: refreshMap (with-arg) passing hotzones to mapPanel: " + current.getHotZones());
        mapPanel.setHotZones(current.getHotZones());
        mapPanel.setPlayerPositionBasedOnEntry(entryType); // Set player position
        mapPanel.repaint();
    }

    private void appendLine(String text) {
        gameOutputPanel.appendLine(text);
    }
}