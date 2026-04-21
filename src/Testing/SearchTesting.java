package Testing;

import static org.junit.jupiter.api.Assertions.*;
import Commands.SearchCommand;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import Playuh.Player;
import Playuh.Room;
import Playuh.Item;

/**
 * Unit tests for the SearchCommand class.
 * This suite validates the dual-behavior of the search action: retrieving items
 * from specialized storage locations and discovering environmental items while
 * strictly enforcing inventory capacity limits.
 * * @author Trong Hieu Tran
 */
public class SearchTesting {

    /**
     * Verifies that items can be successfully retrieved from a storage-designated room.
     * Tests the interaction where a player selects a stored item by its index
     * and confirms the item is transferred to the player's inventory.
     */
    @Test
    void testExecute_PickUpFromStorageSuccess() {
        Player p = new Player("Test");
        p.currentRoomIndex = 0;
        p.inventory = new ArrayList<>(Arrays.asList("Flashlight"));

        Room storage = new Room();
        storage.name = "loc_storage";
        // Store flashlight into storage rooms ArrayList
        storage.storedItems = new ArrayList<>(Arrays.asList("Flashlight"));

        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(storage);

        // Simulate picking the first item in storage
        System.setIn(new ByteArrayInputStream("1\n".getBytes()));

        SearchCommand cmd = new SearchCommand();
        cmd.execute(p, rooms, new ArrayList<>());

        assertTrue(p.inventory.contains("Flashlight"), "Flashlight should be in inventory");
        assertTrue(storage.storedItems.contains("Flashlight"),"Flashlight should be stored");
    }

    /**
     * Ensures that the search command respects the player's maximum inventory capacity.
     * Verifies that if a player with 3 items attempts to pick up a discovered item,
     * the item remains in the room and the inventory count does not increase.
     */
    @Test
    void testExecute_InventoryFullLimit() {
        Player p = new Player("Test");
        // Inventory is full at 3 items
        p.inventory = new ArrayList<>(Arrays.asList("Item1", "Item2", "Item3"));
        p.currentRoomIndex = 0;

        Room room = new Room();
        room.hasItem = true;
        room.name="idk";

        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(room);

        ArrayList<Item> worldItems = new ArrayList<>();
        worldItems.add(new Item("Magnifying Glass"));

        // Simulate choosing to pick up the item
        System.setIn(new ByteArrayInputStream("y\n".getBytes()));

        SearchCommand cmd = new SearchCommand();
        cmd.execute(p, rooms, worldItems);

        assertEquals(3, p.inventory.size(), "Inventory should not exceed 3 items");
        assertFalse(p.inventory.contains("Magnifying Glass"), "New item should not be added when full");
    }
    @Test
    void testExecute_PrioritizeRoomItemOverStorage() {
        Player p = new Player("test");
        p.currentRoomIndex = 0;
        p.inventory = new ArrayList<>();

        Room storage = new Room();
        storage.hasItem = true; // Room has a hidden item
        storage.storedItems = new ArrayList<>(Arrays.asList("Old Boot"));

        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(storage);

        // Simulate input: 'n' to the room item, then '1' to the storage item
        String simulatedInput = "n\n1\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        SearchCommand cmd = new SearchCommand();
        cmd.execute(p, rooms, new ArrayList<>());

        // Assertion: Player should have the "Old Boot" from storage
        // because they declined the room item first.
        assertTrue(p.inventory.contains("Old Boot"));
    }
}