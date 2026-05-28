package Commands;

import Playuh.Item;
import Playuh.Player;
import Playuh.Room;
import Playuh.HotZoneType; // Import HotZoneType

import java.util.ArrayList;

public class MovePrevCommand implements GameCommand {

    @Override
    public String execute(Player p, ArrayList<Room> rooms, ArrayList<Item> items) {
        if (p == null || rooms == null || rooms.isEmpty()) {
            return "No rooms available.";
        }
        if (p.currentRoomIndex - 1 < 0) {
            return "You can't move backward.";
        }
        p.currentRoomIndex--;
        p.lastEntryHotZoneType = HotZoneType.PREV_ROOM; // Set entry type
        return "You moved to the previous room.";
    }
}