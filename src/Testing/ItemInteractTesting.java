package Testing;

import Commands.ItemInteract;
import Playuh.Item;
import Playuh.Player;
import Playuh.Room;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemInteractTesting {

    @Test
    void itemInteractWarnsWhenInventoryIsEmpty() {
        Player player = new Player("Test");

        String result = new ItemInteract().execute(player, new ArrayList<Room>(), new ArrayList<Item>());

        assertTrue(result.toLowerCase().contains("no items"));
    }

    @Test
    void itemInteractPromptsWhenItemsExist() {
        Player player = new Player("Test");
        player.inventory.add("Key");

        String result = new ItemInteract().execute(player, new ArrayList<Room>(), new ArrayList<Item>());

        assertTrue(result.toLowerCase().contains("inspect"));
    }
}