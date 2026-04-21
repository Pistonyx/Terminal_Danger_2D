package Testing;

import Commands.DropCommand;
import Playuh.Item;
import Playuh.Player;
import Playuh.Room;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the DropCommand class.
 * * @author Trong Hieu Tran
 */
public class DropTesting {

    @Test
    void testExecute_SuccessStoreItem() throws Exception {
        // Setup Data
        Player player = new Player("test");
        player.currentRoomIndex = 0;
        player.inventory = new ArrayList<>(Arrays.asList("Flashlight", "Map"));

        Room storageRoom = new Room();
        storageRoom.name = "Storage room";
        storageRoom.storedItems = new ArrayList<>();

        Field idField = Room.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(storageRoom, "loc_storage");

        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(storageRoom);

        // Simulate User Input ("1" for Flashlight)
        String input = "1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Execute
        DropCommand dropCmd = new DropCommand();
        dropCmd.execute(player, rooms, new ArrayList<>());

        // Assertions
        // If successful, inventory should go from 2 to 1
        assertEquals(1, player.inventory.size(), "Inventory should have 1 item left");

        // The command should have added the item to storage
        assertTrue(storageRoom.storedItems.contains("Flashlight"), "Storage should now contain the Flashlight");

        // The player should no longer have it
        assertFalse(player.inventory.contains("Flashlight"), "Flashlight should be removed from player");
    }
}