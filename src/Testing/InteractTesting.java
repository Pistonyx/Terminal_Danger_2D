package Testing;

import Commands.InteractCommand;
import MainGame.Game;
import Playuh.Item;
import Playuh.Player;
import Playuh.Room;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class InteractTesting {

    @Test
    void interactWithNoNpcReturnsNothingMessage() {
        Player player = new Player("Test");
        player.currentRoomIndex = 0;

        Room room = new Room();
        room.name = "Empty Room";

        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(room);

        String result = new InteractCommand().execute(player, rooms, new ArrayList<Item>());

        assertTrue(result.toLowerCase().contains("nothing"));
    }

    @Test
    void interactWithNpcReturnsBioText() {
        Player player = new Player("Test");
        player.currentRoomIndex = 0;

        Room room = new Room();
        room.name = "Some Room";
        Playuh.Character npc = new Playuh.Character();
        npc.name = "Leon";
        npc.bio = "Helpful NPC";
        npc.dialogue = "Hello there.";
        room.npc = npc;

        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(room);

        Game.missionComplete = false;
        String result = new InteractCommand().execute(player, rooms, new ArrayList<Item>());

        assertTrue(result.contains("Helpful NPC") || result.contains("Hello there."));
    }
}