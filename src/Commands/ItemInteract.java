package Commands;

import Playuh.Item;
import Playuh.Player;
import Playuh.Room;

import java.util.ArrayList;

public class ItemInteract implements GameCommand {

    @Override
    public String execute(Player p, ArrayList<Room> rooms, ArrayList<Item> items) {
        if (p == null || p.inventory.isEmpty()) {
            return "You have no items to inspect.";
        }
        return "Use the window prompt to inspect an inventory item.";
    }
}
