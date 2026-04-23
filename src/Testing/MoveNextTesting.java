package Testing;

import Commands.MoveNextCommand;
import Playuh.Item;
import Playuh.Player;
import Playuh.Room;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoveNextTesting {

    @Test
    void moveNextIncrementsRoomIndex() {
        Player player = new Player("Test");
        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(new Room());
        rooms.add(new Room());
        ArrayList<Item> items = new ArrayList<>();

        String result = new MoveNextCommand().execute(player, rooms, items);

        assertEquals(1, player.currentRoomIndex);
        assertTrue(result.toLowerCase().contains("next room"));
    }

    @Test
    void moveNextBlockedAtEndOfRooms() {
        Player player = new Player("Test");
        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(new Room());
        player.currentRoomIndex = 0;

        String result = new MoveNextCommand().execute(player, rooms, new ArrayList<Item>());

        assertEquals(0, player.currentRoomIndex);
        assertTrue(result.toLowerCase().contains("can't move forward"));
    }
}