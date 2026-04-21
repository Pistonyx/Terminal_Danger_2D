package Testing;

import static org.junit.jupiter.api.Assertions.*;
import Commands.MovePrevCommand;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import Playuh.Player;
import Playuh.Room;
import Playuh.Item;

/**
 * Unit tests for the MovePrevCommand class.
 * This class validates the player's ability to navigate backward through the
 * game world and ensures the system prevents movement beyond the starting boundary.
 * * @author Trong Hieu Tran
 */
public class MovePrevTesting {

    /**
     * Verifies that the player correctly moves to the previous room index
     * when they are not at the beginning of the room list.
     */
    @Test
    void testExecute_MoveBackwardsSuccess() {
        // Setup player starts at index 1
        Player p = new Player("Test");
        p.currentRoomIndex = 1;

        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(new Room()); // Index 0
        rooms.add(new Room()); // Index 1

        // Execute
        MovePrevCommand cmd = new MovePrevCommand();
        cmd.execute(p, rooms, new ArrayList<>());

        // Assert: Index should decrease
        assertEquals(0, p.currentRoomIndex, "Player should have moved from index 1 back to 0.");
    }

    /**
     * Ensures that the player cannot move to a negative room index when
     * already located at the entrance (index 0).
     */
    @Test
    void testExecute_BlockedAtEntrance() {
        // Setup player is already at the entrance (index 0)
        Player p = new Player("Test");
        p.currentRoomIndex = 0;

        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(new Room());

        // Execute
        MovePrevCommand cmd = new MovePrevCommand();
        cmd.execute(p, rooms, new ArrayList<>());

        // Assert: Index remains 0
        assertEquals(0, p.currentRoomIndex, "Player should not be able to move below index 0.");
    }
}