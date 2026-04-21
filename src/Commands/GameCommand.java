package Commands;
import Playuh.Player;
import Playuh.Room;
import Playuh.Item;
import java.util.ArrayList;
public interface GameCommand {
    String execute(Player p, ArrayList<Room> rooms, ArrayList<Item> items);
}