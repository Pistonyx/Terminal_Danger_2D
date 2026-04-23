package Commands;

import Playuh.Player;
import Playuh.Room;

import Playuh.Item;
import java.util.ArrayList;

public class SearchCommand implements GameCommand {

    @Override
    public String execute(Player p, ArrayList<Room> rooms, ArrayList<Item> items) {
        if (p == null || rooms == null || rooms.isEmpty()) {
            return "No rooms available.";
        }

        if (p.currentRoomIndex < 0 || p.currentRoomIndex >= rooms.size()) {
            return "You are not in a valid room.";
        }

        Room current = rooms.get(p.currentRoomIndex);

        StringBuilder out = new StringBuilder();

        if (current.hasItem && p.currentRoomIndex < items.size()) {
            Item foundItem = items.get(p.currentRoomIndex);

            if (foundItem == null || foundItem.name == null) {
                out.append("You search the area, but find nothing useful.");
            } else if (foundItem.name.equalsIgnoreCase("Full water bottle")) {
                out.append("You search the environment, but find nothing of interest.");
            } else {
                out.append("You found a [").append(foundItem.name).append("].");
                if (p.inventory.size() < 3) {
                    p.inventory.add(foundItem.name);
                    current.hasItem = false;
                    out.append(" Added to your inventory.");
                } else {
                    out.append(" Your inventory is full.");
                }
            }
        } else {
            out.append("You search the environment, but find nothing hidden.");
        }

        if (current.getId() != null && current.getId().equals("loc_storage")) {
            if (current.storedItems.isEmpty()) {
                out.append("\nThere is nothing stored in here.");
            } else {
                out.append("\nStored items: ").append(current.storedItems);
                out.append("\nUse the window prompt to withdraw items.");
            }
        }

        return out.toString();
    }
}