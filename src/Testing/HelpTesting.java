package Testing;

import Commands.HelpCommand;
import Playuh.Item;
import Playuh.Player;
import Playuh.Room;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HelpTesting {

    @Test
    void helpCommandReturnsCommandList() {
        HelpCommand command = new HelpCommand("commands.txt");

        String result = command.execute(new Player("Test"), new ArrayList<Room>(), new ArrayList<Item>());

        assertTrue(result.contains("h - help"));
        assertTrue(result.contains("n - next room"));
        assertTrue(result.contains("p - previous room"));
        assertTrue(result.contains("s - search"));
        assertTrue(result.contains("i - interact"));
        assertTrue(result.contains("d - drop item"));
        assertTrue(result.contains("items - inspect inventory item"));
        assertTrue(result.contains("quit - exit"));
    }
}