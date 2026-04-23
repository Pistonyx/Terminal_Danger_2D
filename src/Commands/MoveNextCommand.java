package Commands;

import Playuh.Item;
import Playuh.Player;
import Playuh.Room;

import java.util.ArrayList;

public class MoveNextCommand implements GameCommand {
    @Override
    public String execute(Player p, ArrayList<Room> rooms, ArrayList<Item> items) {
        if (p == null || rooms == null || rooms.isEmpty()) {
            return "No rooms available.";
        }
        if (p.currentRoomIndex + 1 >= rooms.size()) {
            return "You can't move forward.";
        }
        p.currentRoomIndex++;
        return "You moved to the next room.";
    }
}