package Testing;

import Commands.MovePrevCommand;
import Playuh.Item;
import Playuh.Player;
import Playuh.Room;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MovePrevTesting {

    @Test
    void movePrevDecrementsRoomIndex() {
        Player player = new Player("Test");
        player.currentRoomIndex = 1;

        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(new Room());
        rooms.add(new Room());

        String result = new MovePrevCommand().execute(player, rooms, new ArrayList<Item>());

        assertEquals(0, player.currentRoomIndex);
        assertTrue(result.toLowerCase().contains("previous room"));
    }

    @Test
    void movePrevBlockedAtStartOfRooms() {
        Player player = new Player("Test");
        player.currentRoomIndex = 0;

        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(new Room());

        String result = new MovePrevCommand().execute(player, rooms, new ArrayList<Item>());

        assertEquals(0, player.currentRoomIndex);
        assertTrue(result.toLowerCase().contains("can't move backward"));
    }
}