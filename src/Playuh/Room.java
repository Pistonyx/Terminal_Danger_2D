package Playuh;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

/**
 * Represents a specific location within the game world.
 * Each room can contain an NPC, environmental items, and a list of adjacent
 * neighbors. This class also supports a storage mechanic where players can
 * drop and retrieve items.
 * * @author Trong Hieu Tran
 */
public class Room {

    /** Indicates if an item is currently available to be found via searching. */
    public boolean hasItem = true;

    /** The NPC (Non-Player Character) currently residing in this room. */
    public Character npc;

    /** Unique identifier for the room, used for mapping and logic triggers. */
    private String id;

    /** The display name of the room. */
    public String name;

    /** A sensory description of the room shown to the player. */
    private String description;

    /** * A list of room IDs that are physically adjacent to this room.
     * Annotated for GSON to map the "neighbours" JSON field to this variable.
     */
    @SerializedName("neighbours")
    private ArrayList<String> neighbors;

    /** A list of potential items that can be found in this room. */
    private ArrayList<String> lootTable;

    /** * Items that have been dropped or stored here by the player.
     * Specifically utilized for the "Storage Room" mechanic.
     */
    public ArrayList<String> storedItems = new ArrayList<>();

    /**
     * Retrieves the unique identifier of the room.
     * @return The room's ID string.
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the display name of the room.
     * @return The room's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a string representation of the room, including its
     * metadata and connections. Useful for debugging purposes.
     * * @return A formatted string containing the room's data.
     */
    @Override
    public String toString() {
        return "Location{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", neighbors=" + neighbors +
                ", lootTable=" + lootTable +
                '}';
    }
}