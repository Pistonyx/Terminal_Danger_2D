package Testing;

import Commands.DropCommand;
import Playuh.Item;
import Playuh.Player;
import Playuh.Room;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DropTesting {

    @Test
    void dropCommandRequiresStorageRoom() {
        Player player = new Player("Test");
        player.inventory.add("Key");
        player.currentRoomIndex = 0;

        Room room = new Room();
        room.name = "Hallway";

        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(room);

        String result = new DropCommand().execute(player, rooms, new ArrayList<Item>());

        assertTrue(result.toLowerCase().contains("storage room"));
        assertEquals(1, player.inventory.size());
    }

    @Test
    void dropCommandRequiresInventoryItems() {
        Player player = new Player("Test");
        player.currentRoomIndex = 0;

        Room room = new Room();
        room.name = "Storage";
        room.storedItems = new ArrayList<>();

        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(room);

        String result = new DropCommand().execute(player, rooms, new ArrayList<Item>());

        assertTrue(result.toLowerCase().contains("nothing to drop"));
    }
}