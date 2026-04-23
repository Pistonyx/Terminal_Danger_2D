package Commands;

import Playuh.Item;
import Playuh.Player;
import Playuh.Room;

import java.util.ArrayList;

public interface GameCommand {
    String execute(Player p, ArrayList<Room> rooms, ArrayList<Item> items);
}