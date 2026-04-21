package Commands;

import java.util.ArrayList;
import MainGame.Game;
import MainGame.Texts;
import Playuh.*;

/**
 * Handles the logic for moving the player forward to the next room in the sequence.
 * This command includes a specialized puzzle check for the "Cellar" entry,
 * which requires a password to unlock if the player is currently at the Balcony.
 * * @author Trong Hieu Tran
 */
public class MoveNextCommand implements GameCommand {

    /**
     * Executes the forward movement logic.
     * Checks if the player is at the end of the map and handles the password
     * prompt for the locked cellar if the player is at room index 6 (the balcony).
     *
     * @param p      The player attempting to move.
     * @param rooms  The list of rooms defining the game world.
     * @param items  The global list of items (not used in this command).
     * @return       An empty String, as status and messages are printed to the console.
     */
    @Override
    public String execute(Player p, ArrayList<Room> rooms, ArrayList<Item> items) {
        // Advances player unless at end or blocked
        if (p.currentRoomIndex < rooms.size() - 1) {

            // Special Logic: The Cellar Gate (Room 6 / Balcony)
            if (p.currentRoomIndex == 6 && Game.isCellarLocked) {
                System.out.println(Texts.t("move.cellar.locked"));
                System.out.println(Texts.t("move.cellar.enterPrompt"));

                String choice = Game.sc.nextLine().trim();

                if (choice.equalsIgnoreCase("y")) {
                    System.out.print(Texts.t("move.cellar.passwordPrompt") + " ");
                    String passwordAttempt = Game.sc.nextLine();

                    // Validation of the cellar password
                    if (passwordAttempt.equalsIgnoreCase("spsejecna")) {
                        Game.isCellarLocked = false;
                        System.out.println(Texts.t("move.cellar.unlocked"));
                        p.currentRoomIndex++; // Move into the cellar
                    } else {
                        System.out.println(Texts.t("move.cellar.incorrect"));
                        p.currentRoomIndex = 6; // Stay on balcony
                    }
                } else {
                    System.out.println(Texts.t("move.cellar.stay"));
                    p.currentRoomIndex = 6;
                }

            } else {
                // Standard forward movement
                p.currentRoomIndex++;
            }

        } else {
            // Boundary reached
            System.out.println(Texts.t("move.end"));
        }
        return "";
    }
}