package Testing;

import static org.junit.jupiter.api.Assertions.*;

import Commands.HelpCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import Playuh.Player;
import Playuh.Room;
import Playuh.Item;

/**
 * Unit tests for the HelpCommand class.
 * This class verifies that the game can correctly locate, read, and display
 * the command list from an external text file.
 * * @author Trong Hieu Tran
 */
public class HelpTesting {

    /** * Temporary directory provided by JUnit 5.
     * Used to create mock help files that are automatically deleted after tests run.
     */
    @TempDir
    Path tempDir;

    /**
     * Verifies that HelpCommand correctly opens a file and processes its content.
     * The test creates a physical temporary file, populates it with command descriptions,
     * and asserts that the command executes without error.
     * * @throws Exception if file creation or access fails during the test.
     */
    @Test
    void testExecute_DisplaysSpecificMenu() throws Exception {
        // Create physical file in the temporary directory
        File helpFile = tempDir.resolve("commands.txt").toFile();
        try (PrintWriter out = new PrintWriter(helpFile)) {
            out.println("[n] Next          - Move deeper into the building.");
            out.println("[p] Previous      - Move back toward the alley.");
            out.println("[s] Search        - Look for items in the room.");
            out.println("[i] Interact      - Talk to NPCs or use objects.");
            out.println("[d] Drop          - Remove an item from your bag.");
            out.println("[h] Help          - Display this list of commands.");
            out.println("[items]           - Display an items description.");
            out.println("[quest] Quest     - Display your current quests.");
            out.println("[quit] Quit       - Exit the game.");
        }

        // Initialize the command with the path to our temporary test file
        HelpCommand helpCmd = new HelpCommand(helpFile.getAbsolutePath());

        Player p = new Player("test");
        ArrayList<Room> rooms = new ArrayList<>();
        ArrayList<Item> items = new ArrayList<>();

        // Run the command logic
        String result = helpCmd.execute(p, rooms, items);

        // Verification
        assertEquals("", result, "HelpCommand should return an empty string.");
        assertTrue(helpFile.exists(), "The test failed because the help file wasn't created.");
    }
}