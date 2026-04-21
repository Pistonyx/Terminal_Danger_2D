package Commands;

import java.util.ArrayList;
import MainGame.Game;
import Playuh.*;

/**
 * Handles the logic for moving the player backward to the previous room.
 * This command performs boundary validation to ensure the player does not
 * attempt to move past the entrance of the game map.
 * * @author Trong Hieu Tran
 */
public class MovePrevCommand implements GameCommand {

    /**
     * Executes the backward movement logic.
     * Decrements the player's current room index if they are not already at index 0.
     *
     * @param p      The player attempting to move backward.
     * @param rooms  The list of rooms defining the game world.
     * @param items  The global list of items (not used in this command).
     * @return       An empty String, as status messages are printed to the console.
     */
    @Override
    public String execute(Player p, ArrayList<Room> rooms, ArrayList<Item> items) {
        // Moves player back a room unless they're at the start (Index 0)
        if (p.currentRoomIndex > 0) {
            p.currentRoomIndex--;
        } else {
            System.out.println("You are at the entrance.");
        }
        return "";
    }
}