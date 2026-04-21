package MainGame;

import Commands.*;
import Playuh.*;
import Playuh.Character;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * The core engine of "The Cellar Assassination."
 * This class handles game initialization, global state management,
 * resource loading, and the main input-processing loop.
 * * @author Trong Hieu Tran
 */
public class Game {

    /** Global flag indicating if access to the Cellar room is restricted. */
    public static boolean isCellarLocked = true;

    /** Registry mapping user input strings to their respective command objects. */
    public static Map<String, GameCommand> commandMap = new HashMap<>();

    /** Global scanner for capturing user input throughout the game. */
    public static Scanner sc = new Scanner(System.in);

    /** Flag indicating the mission's conclusion (triggered by the final choice). */
    public static boolean missionComplete = false;

    /** Tracks if the player utilized Leon's assistance to bypass the cellar lock. */
    public static boolean usedLeonToOpenCellar = false;

    /**
     * Entry point for the application.
     * Performs setup by loading JSON data, mapping characters to rooms,
     * initializing the command registry, and running the primary game loop.
     * * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Load external game data
        GameData data = GameData.loadGamaDataFromResources("/gamedata.json");
        ArrayList<Room> gameRooms = data.locations;

        if (gameRooms == null || gameRooms.isEmpty()) {
            System.out.println("No rooms loaded from gamedata.json");
            return;
        }

        ArrayList<Item> gameItems = data.items != null ? data.items : new ArrayList<>();

        // Map characters to their home locations defined in JSON
        if (data.characters != null) {
            for (Character c : data.characters) {
                if (c == null) continue;
                if (c.homeLocationId == null || c.homeLocationId.isBlank()) continue;

                Room home = data.findLocation(c.homeLocationId);
                home.npc = c;

                if (home.npc.dialogue == null || home.npc.dialogue.isBlank()) {
                    home.npc.dialogue = "(They don't say anything.)";
                }
            }
        }

        // Initialize Player and Command Registry
        Player timofey = new Player("Timofey Mufasa");
        initializeCommands();

        System.out.println("=== MISSION: THE CELLAR ASSASSINATION ===");

        boolean running = true;

        // Main Game Loop
        while (running) {
            // Safety check for room boundaries
            if (timofey.currentRoomIndex < 0 || timofey.currentRoomIndex >= gameRooms.size()) {
                System.out.println("Error: Player is out of bounds.");
                break;
            }

            Room current = gameRooms.get(timofey.currentRoomIndex);
            displayRoomInfo(current, timofey);

            System.out.print("Action -> ");
            String action = sc.nextLine().toLowerCase();

            if (action.equals("quit")) {
                running = false;
            } else if (commandMap.containsKey(action)) {
                clearScreen();
                commandMap.get(action).execute(timofey, gameRooms, gameItems);

                // Check if the game has reached its conclusion
                if (missionComplete) {
                    running = false;
                    System.out.println("\n=== THE END ===");
                }
            } else {
                clearScreen();
                System.out.println("Unknown command. Type 'h' for help.");
            }
        }
    }

    /**
     * Populates the commandMap with available game actions.
     */
    private static void initializeCommands() {
        commandMap.put("h", new HelpCommand("commands.txt"));
        commandMap.put("n", new MoveNextCommand());
        commandMap.put("p", new MovePrevCommand());
        commandMap.put("s", new SearchCommand());
        commandMap.put("i", new InteractCommand());
        commandMap.put("d", new DropCommand());
        commandMap.put("items", new ItemInteract());
    }

    /**
     * Displays the current room header, NPC alerts, and player status.
     * * @param current The room the player is currently in.
     * @param p       The player instance.
     */
    private static void displayRoomInfo(Room current, Player p) {
        System.out.println("");
        System.out.println("--- " + current.name + " ---");
        System.out.println("Type 'h' for help.");
        checkNPCPresence(current);
        System.out.println("Inventory: " + p.inventory);
    }

    /**
     * Checks for and announces the presence of an NPC in the room.
     * * @param r The room to inspect.
     */
    public static void checkNPCPresence(Room r) {
        if (r.npc != null) {
            System.out.println("[!] " + r.npc.name + " is here. Press 'i' to interact.");
        }
    }

    /**
     * Clears the console view by printing whitespace.
     */
    private static void clearScreen() {
        for (int i = 0; i < 25; i++) {
            System.out.println("");
        }
    }
}