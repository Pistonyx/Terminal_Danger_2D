package Commands;

import java.util.ArrayList;
import java.util.Scanner;
import Playuh.Player;
import Playuh.Room;
import Playuh.Item;

/**
 * The DropCommand class handles the logic for a player removing an item
 * from their inventory and placing it into a room's storage.
 * In this game, dropping is restricted to specific storage locations.
 * * @author Trong Hieu Tran
 */
public class DropCommand implements GameCommand {

    /**
     * Default constructor for DropCommand.
     * Required for instantiation within the Command Map.
     */
    public DropCommand() {
    }

    /**
     * Executes the drop logic. Validates if the player is in the correct room,
     * checks inventory status, and moves the selected item from the player
     * to the room's stored items list.
     *
     * @param p      The player attempting to drop an item.
     * @param rooms  The list of all rooms in the game environment.
     * @param items  The global list of items (not used in this specific command).
     * @return       An empty String, as the command prints results directly to the console.
     */
    @Override
    public String execute(Player p, ArrayList<Room> rooms, ArrayList<Item> items) {
        Room current = rooms.get(p.currentRoomIndex);

        // Only allows dropping items while in the Storage room
        if (current.getId() == null || !current.getId().equals("loc_storage")) {
            System.out.println("You can only drop items in the Storage room.");
            return "";
        }

        if (p.inventory.isEmpty()) {
            System.out.println("Nothing to drop.");
            return "";
        }

        System.out.println("Inventory: " + p.inventory);
        System.out.print("Enter index to store (1-" + (p.inventory.size()) + "): ");

        try {
            Scanner sc = new Scanner(System.in);
            int idx = Integer.parseInt(sc.nextLine());

            if (idx > 0 && idx <= p.inventory.size()) {
                String stored = p.inventory.remove(idx - 1);
                current.storedItems.add(stored);
                System.out.println("Stored in Storage room: " + stored);
            } else {
                System.out.println("Invalid choice.");
            }
        } catch (Exception e) {
            System.out.println("Invalid choice.");
        }
        return "";
    }
}