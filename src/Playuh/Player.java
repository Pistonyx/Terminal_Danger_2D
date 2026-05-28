package Playuh;

import java.util.ArrayList;

/**
 * Represents the human-controlled protagonist within the game.
 * The Player class manages the user's inventory, current location within the
 * world, and specific state flags required for progression through puzzles.
 * * @author Trong Hieu Tran
 */
public class Player {

    /** The name of the player character. */
    String name;

    /** The maximum amount of items the player can hold. */
    public static final int MAX_INVENTORY_SIZE = 3;

    /** * The player's collection of held items.
     * Initialized with a suggested capacity of 3 items.
     */
    public ArrayList<String> inventory = new ArrayList<>(MAX_INVENTORY_SIZE);

    /** The index of the room the player is currently occupying. */
    public int currentRoomIndex = 0;

    /** Tracks which doorway the player entered from. */
    public HotZoneType lastEntryHotZoneType = HotZoneType.NEXT_ROOM;

    /** Flag indicating if the safe in Apartment 102 has been located. */
    public boolean safeDiscovered = false;

    /** Flag indicating if the safe puzzle has been successfully completed. */
    public boolean safeSolved = false;

    /** * Tracks the current stage of the safe puzzle.
     * 0: Needs Small key, 1: Needs Rotating gear, 2: Needs Weighted cube, 3: Needs Lever handle, 4: Solved.
     */
    public int safeProgress = 0;

    /** Tracks if the cellar door has been unlocked by code or other means. */
    public boolean cellarUnlocked = false; // Added for cellar access

    /** Tracks if Leon has helped kick down the cellar door. */
    public boolean leonHelped = false; // Added for Leon's help

    /**
     * Constructs a new Player with a specified name.
     *
     * @param n The name assigned to the player.
     */
    public Player(String n) {
        this.name = n;
    }

    /**
     * Checks if a specific item exists within the player's inventory.
     *
     * @param i The name of the item to check for.
     * @return true if the item is found; false otherwise.
     */
    public boolean hasItem(String i) {
        return inventory.contains(i);
    }

    /**
     * Checks if the player's inventory is full.
     *
     * @return true if the inventory has reached the maximum size; false otherwise.
     */
    public boolean isInventoryFull() {
        return inventory.size() >= MAX_INVENTORY_SIZE;
    }

    /**
     * Removes an item from the player's inventory.
     *
     * @param itemName The name of the item to be removed.
     * @return true if the item was removed, false otherwise.
     */
    public boolean removeItem(String itemName) {
        return inventory.remove(itemName);
    }

    /**
     * Replaces an existing item in the inventory with a new one.
     * This is primarily used for state-change items, such as filling
     * a water bottle or fixing a broken component.
     *
     * @param oldInventory The name of the item to be removed.
     * @param newInventory The name of the item to be added in its place.
     */
    public void replaceItem(String oldInventory, String newInventory) {
        int idx = inventory.indexOf(oldInventory);
        if (idx != -1) {
            inventory.set(idx, newInventory);
        }
    }
}