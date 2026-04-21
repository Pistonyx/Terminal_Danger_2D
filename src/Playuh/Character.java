package Playuh;

import MainGame.Texts;

/**
 * Represents an entity within the game world, such as an NPC (Non-Player Character).
 * Characters can hold roles, reside in specific locations, and provide dialogue
 * or biographical information to the player.
 * * @author Trong Hieu Tran
 */
public class Character {

    /** Unique identifier for the character, typically used for mapping in GameData. */
    public String id;

    /** The display name of the character. */
    public String name;

    /** The character's occupation or significance within the story. */
    public String role;

    /** The ID of the Room where this character is primarily located. */
    public String homeLocationId;

    /** A background story or description of the character's appearance. */
    public String bio;

    /** The specific line of text the character speaks when interacted with. */
    public String dialogue;

    /**
     * Constructs a new Character with basic interaction details.
     *
     * @param n The name of the character.
     * @param b The biography/description of the character.
     * @param d The dialogue the character speaks.
     */
    public Character(String n, String b, String d) {
        this.name = n;
        this.bio = b;
        this.dialogue = d;
    }

    /**
     * Displays the character's information to the console.
     * Prints a formatted header with the name, followed by the biography
     * and dialogue if they are available.
     */
    public void showBio() {
        System.out.println(Texts.tf("npc.header", name));

        // Validate and print biography
        if (bio != null && !bio.isBlank()) {
            System.out.println(bio);
        }

        // Validate and print dialogue, or show default empty message
        if (dialogue != null && !dialogue.isBlank()) {
            System.out.println(Texts.tf("npc.dialogue", dialogue));
        } else {
            System.out.println(Texts.t("npc.noDialogue"));
        }
    }
}