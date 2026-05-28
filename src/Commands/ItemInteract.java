package Commands;

import Playuh.Item;
import Playuh.Player;
import Playuh.Room;

import java.util.ArrayList;

public class ItemInteract implements GameCommand {

    @Override
    public String execute(Player p, ArrayList<Room> rooms, ArrayList<Item> items) {
        if (p == null || p.inventory.isEmpty()) {
            return "Your inventory is empty.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("--- Your Inventory ---\n");
        for (int i = 0; i < p.inventory.size(); i++) {
            sb.append(String.format("%d. %s\n", i + 1, p.inventory.get(i)));
        }
        sb.append("----------------------\n");
        sb.append("Enter the number of an item to inspect, or 0 to exit.");

        return sb.toString();
    }
}