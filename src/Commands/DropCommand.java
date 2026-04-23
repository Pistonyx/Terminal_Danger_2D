package Commands;

import Playuh.Item;
import Playuh.Player;
import Playuh.Room;

import java.util.ArrayList;

public class DropCommand implements GameCommand {

    @Override
    public String execute(Player p, ArrayList<Room> rooms, ArrayList<Item> items) {
        if (p == null || rooms == null || rooms.isEmpty() ||
                p.currentRoomIndex < 0 || p.currentRoomIndex >= rooms.size()) {
            return "You are not in a valid room.";
        }

        Room current = rooms.get(p.currentRoomIndex);

        if (!"loc_storage".equals(current.getId())) {
            return "You can only drop items in the Storage room.";
        }

        if (p.inventory.isEmpty()) {
            return "Nothing to drop.";
        }

        return "Use the window prompt to choose an item to drop.";
    }
}