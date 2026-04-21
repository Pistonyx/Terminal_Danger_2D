package Testing;

import static org.junit.jupiter.api.Assertions.*;

import Commands.MoveNextCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Scanner;

import Playuh.Player;
import Playuh.Room;
import Playuh.Item;
import MainGame.Game;

/**
 * Unit tests for the MoveNextCommand class.
 * This class validates standard forward navigation, boundary protection at the
 * end of the map, and the conditional logic for unlocking the cellar room via
 * a password prompt.
 * * @author Trong Hieu Tran
 */
public class MoveNextTesting {

    /**
     * Resets the global game state before each test execution to ensure
     * independence and consistency between test cases.
     */
    @BeforeEach
    void setup() {
        // Reset game state before each test
        Game.isCellarLocked = true;
    }

    /**
     * Verifies that the player correctly advances to the next room index
     * under normal circumstances.
     */
    @Test
    void testExecute_SimpleMove() {
        // Setup player at index 0, total 3 rooms
        Player p = new Player("Test");
        p.currentRoomIndex = 0;

        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(new Room()); // Index 0
        rooms.add(new Room()); // Index 1
        rooms.add(new Room()); // Index 2

        // Execute
        MoveNextCommand cmd = new MoveNextCommand();
        cmd.execute(p, rooms, new ArrayList<>());

        // Assert
        assertEquals(1, p.currentRoomIndex, "Player should have moved from index 0 to 1");
    }

    /**
     * Validates the cellar unlocking mechanism.
     * Simulates the player reaching the Balcony (Index 6) and successfully
     * entering the correct password to unlock the final room.
     */
    @Test
    void testExecute_CellarUnlockSuccess() {
        // Setup: Player at the Balcony (Index 6)
        Player p = new Player("Test");
        p.currentRoomIndex = 6;

        ArrayList<Room> rooms = new ArrayList<>();
        // Create 8 rooms to allow movement to index 7
        for(int i = 0; i < 8; i++) rooms.add(new Room());

        // Input "y" to try password, then the correct password "spsejecna"
        String input = "y\nspsejecna\n";
        Game.sc = new Scanner(new ByteArrayInputStream(input.getBytes()));

        // Execute
        MoveNextCommand cmd = new MoveNextCommand();
        cmd.execute(p, rooms, new ArrayList<>());

        // Assert
        assertFalse(Game.isCellarLocked, "Cellar should now be unlocked");
        assertEquals(7, p.currentRoomIndex, "Player should have moved to index 7");
    }

    /**
     * Ensures that the player cannot move forward once they have reached
     * the final room in the list.
     */
    @Test
    void testExecute_AtEnd() {
        // Setup player at the last room
        Player p = new Player("Test");
        p.currentRoomIndex = 1;

        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(new Room());
        rooms.add(new Room());

        // Execute
        MoveNextCommand cmd = new MoveNextCommand();
        cmd.execute(p, rooms, new ArrayList<>());

        // Assert
        assertEquals(1, p.currentRoomIndex, "Player should not move past the last room");
    }
}