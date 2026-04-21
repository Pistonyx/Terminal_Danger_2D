package Commands;

import java.util.ArrayList;
import java.util.Scanner;
import Playuh.Player;
import Playuh.Room;
import Playuh.Item;
import MainGame.Game;
/**
 * Handles the logic for searching a room. This refactored version prioritizes
 * environmental discovery first, then checks for storage room interactions.
 * * @author Trong Hieu Tran
 */
public class SearchCommand implements GameCommand {
    /**
     * Executes the search logic. It first checks if the room has a native item to find.
     * After handling the discovery, it checks if the room is a storage location
     * to allow item retrieval from the player's stored item list.
     *
     * @param p      The player performing the search.
     * @param rooms  The list of rooms defining the game world.
     * @param items  The global list of hidden items mapped to room indices.
     * @return       An empty String as per the command design.
     */
    @Override
    public String execute(Player p, ArrayList<Room> rooms, ArrayList<Item> items) {
        Room current = rooms.get(p.currentRoomIndex);
        Scanner sc = new Scanner(System.in);
        // check if room has an item
        if (current.hasItem && p.currentRoomIndex < items.size()) {
            String found = items.get(p.currentRoomIndex).name;

            // Integrity Check: Prevent bypassing the water puzzle via searching
            if (found != null && found.equalsIgnoreCase("Full water bottle")) {
                System.out.println("\nYou search the environment, but find nothing of interest.");
            } else {
                System.out.print("\n[!] You found a [" + found + "]. Pick up? (y/n): ");
                if (sc.nextLine().equalsIgnoreCase("y")) {
                    if (p.inventory.size() < 3) {
                        p.inventory.add(found);
                        current.hasItem = false;
                        System.out.println(">> Added " + found + " to your inventory.");
                    } else {
                        System.out.println(">> Your inventory is full! You cannot carry the " + found + ".");
                    }
                } else {
                    System.out.println(">> You leave the " + found + " where it is.");
                }
            }
        } else {
            System.out.println("\nYou search the environment, but find nothing hidden.");
        }

        // Allows the player to drop item in storage room.
        if (current.getId() != null && current.getId().equals("loc_storage")) {
            handleStorageLogic(p, current, sc);
        }

        return "";
    }

    /**
     * Helper method to handle item retrieval from the room's storage list.
     */
    private void handleStorageLogic(Player p, Room current, Scanner sc) {
        if (current.storedItems.isEmpty()) {
            System.out.println("\nThere is nothing stored in here.");
            return;
        }

        System.out.println("\n Items currently stored: " + current.storedItems);

        if (p.inventory.size() >= 3) {
            System.out.println(">> You cannot take anything from the storage because your inventory is full.");
            return;
        }

        System.out.print("Withdraw an item? Enter index (1-" + current.storedItems.size() + "), or 0 to skip: ");
        try {
            int idx = Integer.parseInt(sc.nextLine());
            if (idx > 0 && idx <= current.storedItems.size()) {
                String picked = current.storedItems.remove(idx - 1);
                p.inventory.add(picked);
                System.out.println(">> Withdrew: " + picked);
            } else if (idx != 0) {
                System.out.println(">> Invalid choice.");
            }
        } catch (Exception e) {
            System.out.println(">> Invalid input. Skipping storage interaction.");
        }
    }
}