package Testing;

import static org.junit.jupiter.api.Assertions.*;

import Commands.ItemInteract;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;

import Playuh.Player;
import Playuh.Room;
import Playuh.Item;

/**
 * Unit tests for the ItemInteract command.
 * This suite ensures that the inventory inspection system correctly identifies
 * held items, handles empty inventory states gracefully, and successfully
 * references external data sources without altering the player's inventory.
 * * @author Trong Hieu Tran
 */
public class ItemInteractTesting {

    /**
     * Verifies that the player can successfully select an item from their
     * inventory to view its description.
     * This test simulates choosing the first item and confirms that the
     * command logic completes without clearing the player's inventory.
     */
    @Test
    void testExecute_ShowItemDescriptionSuccess() {
        // Setup Player with an item
        Player p = new Player("TestPlayer");
        p.inventory = new ArrayList<>(Arrays.asList("Empty water bottle"));

        ArrayList<Room> rooms = new ArrayList<>();
        ArrayList<Item> worldItems = new ArrayList<>();

        // Input: "1" to select the water bottle
        String simulatedInput = "1\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Execute
        ItemInteract command = new ItemInteract();

        // Load gamedata.json from resources and process
        String result = command.execute(p, rooms, worldItems);

        // Assertions
        assertEquals("", result, "Should return empty string as per command design");
        assertFalse(p.inventory.isEmpty(), "Inventory should not be cleared by inspecting an item");
    }

    /**
     * Ensures that the command handles cases where the player attempts to
     * interact with an empty inventory.
     * Verifies that the command terminates safely and returns the expected
     * status code without attempting to process non-existent items.
     */
    @Test
    void testExecute_EmptyInventory() {
        Player p = new Player("TestPlayer");
        p.inventory = new ArrayList<>(); // Empty

        ItemInteract command = new ItemInteract();
        String result = command.execute(p, new ArrayList<>(), new ArrayList<>());

        assertEquals("", result);
        // Console output: "You have no items to interact with."
    }
}