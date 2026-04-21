package Commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import Playuh.*;

/**
 * The HelpCommand class is responsible for displaying the game's command list
 * to the player by reading from an external text file.
 * * @author Trong Hieu Tran
 */
public class HelpCommand implements GameCommand {

    /** The path to the text file containing the help menu. */
    private String file;

    /**
     * Constructs a new HelpCommand with a specific file path.
     *
     * @param file The file path (relative or absolute) of the help text file.
     */
    public HelpCommand(String file) {
        this.file = file;
    }

    /**
     * Executes the help command. Opens the specified text file, iterates through
     * its lines, and prints each command description to the console.
     * *
     *
     * @param p      The current player instance.
     * @param rooms  The list of rooms in the game.
     * @param items  The list of items in the game.
     * @return       An empty String, as output is printed directly to the console.
     */
    @Override
    public String execute(Player p, ArrayList<Room> rooms, ArrayList<Item> items) {
        System.out.println("\n--- AVAILABLE COMMANDS ---");

        // Reads and prints help file; reports missing file
        try {
            Scanner s = new Scanner(new File(file));
            while (s.hasNextLine()) {
                System.out.println(s.nextLine());
            }
            s.close();
        } catch (FileNotFoundException e) {
            System.out.println("Help file missing.");
        }

        System.out.println("--------------------------");
        return "";
    }
}