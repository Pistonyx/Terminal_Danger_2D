package Commands;

import Playuh.GameData;
import Playuh.Item;
import Playuh.Player;
import Playuh.Room;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles player interaction with items currently held in their inventory.
 * This command allows players to inspect items, retrieving detailed descriptions
 * by cross-referencing inventory names with external data in gamedata.json.
 * * @author Trong Hieu Tran
 */
public class ItemInteract implements GameCommand {

    /**
     * Executes the item interaction logic.
     * Displays the player's inventory, prompts for an item selection, and
     * attempts to load descriptive data for that item from the resources folder.
     *
     * @param p      The player interacting with their inventory.
     * @param rooms  The list of rooms in the game environment.
     * @param items  The global list of items.
     * @return       An empty String as output is handled via standard console output.
     */
    @Override
    public String execute(Player p, ArrayList<Room> rooms, ArrayList<Item> items) {
        if (p.inventory.isEmpty()){
            System.out.println("You have no items to interact with.");
            return "";
        }

        System.out.println("Inventory: " + p.inventory);
        System.out.println("Enter index to interact with an item. (1-" + p.inventory.size() + "):");

        try {
            Scanner sc = new Scanner(System.in);
            int idx = Integer.parseInt(sc.nextLine());

            if (idx >= 1 && idx <= p.inventory.size()) {
                String itemName = p.inventory.get(idx - 1);

                // Use GameData to load items/descriptions from gamedata.json
                GameData data = GameData.loadGamaDataFromResources("/gamedata.json");
                Item fromJson = findItemByName(data, itemName);

                Item selected = new Item(itemName);
                if (fromJson != null && fromJson.description != null && !fromJson.description.isBlank()) {
                    selected.description = fromJson.description;
                }

                selected.showDescription();
            } else {
                System.out.println("Index out of bounds of inventory.");
            }
        } catch (Exception e) {
            System.out.println("Invalid choice.");
        }
        return "";
    }

    /**
     * Searches the provided GameData for an item matching a specific name.
     * This helper method ensures that descriptive data can be retrieved even
     * if the naming case in the inventory differs slightly from the JSON file.
     *
     * @param data     The loaded GameData object containing item definitions.
     * @param itemName The name of the item to search for.
     * @return         The Item object found in the data, or null if no match is found.
     */
    private Item findItemByName(GameData data, String itemName) {
        if (data == null || data.items == null) return null;

        for (Item it : data.items) {
            if (it == null || it.name == null) continue;
            if (it.name.equalsIgnoreCase(itemName)) {
                return it;
            }
        }
        return null;
    }
}