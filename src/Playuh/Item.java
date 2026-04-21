package Playuh;

/**
 * Represents a physical object within the game world that a player can discover,
 * collect, or interact with. Items consist of a name and an optional detailed
 * description retrieved from game data.
 * * @author Trong Hieu Tran
 */
public class Item {

    /** The display name of the item. */
    public String name;

    /** A detailed description explaining the item's appearance or purpose. */
    public String description;

    /**
     * Constructs a new Item with a specified name.
     *
     * @param name The name to be assigned to this item.
     */
    public Item(String name) {
        this.name = name;
    }

    /**
     * Displays the item's name and its description to the console.
     * If no description is available or if the description is blank,
     * a default message is displayed to the player.
     */
    public void showDescription(){
        System.out.println("\n--- Item: " + name + " ---");

        if (description != null && !description.isBlank()) {
            System.out.println("Description: '" + description + "'");
        } else {
            System.out.println("This item doesnt have a description");
        }
    }
}