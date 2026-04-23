package Testing;

import Commands.SearchCommand;
import Playuh.Item;
import Playuh.Player;
import Playuh.Room;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SearchTesting {

    @Test
    void searchAddsItemToInventoryWhenAvailable() {
        Player player = new Player("Test");
        player.currentRoomIndex = 0;

        Room room = new Room();
        room.hasItem = true;
        room.name = "Test Room";

        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(room);

        ArrayList<Item> items = new ArrayList<>();
        Item item = new Item();
        item.name = "Key";
        items.add(item);

        String result = new SearchCommand().execute(player, rooms, items);

        assertTrue(player.inventory.contains("Key"));
        assertFalse(room.hasItem);
        assertTrue(result.toLowerCase().contains("found"));
    }

    @Test
    void searchFindsNothingWhenNoItemExists() {
        Player player = new Player("Test");
        player.currentRoomIndex = 0;

        Room room = new Room();
        room.hasItem = false;

        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(room);

        ArrayList<Item> items = new ArrayList<>();

        String result = new SearchCommand().execute(player, rooms, items);

        assertTrue(result.toLowerCase().contains("nothing hidden"));
    }
}