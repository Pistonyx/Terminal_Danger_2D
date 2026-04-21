package Commands;

import MainGame.Game;
import MainGame.Texts;
import Playuh.Item;
import Playuh.Player;
import Playuh.Room;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Handles complex interactions between the player, NPCs, and environmental puzzles.
 * This class manages the Garage lever fix, the Apartment 102 safe, NPC dialogues,
 * and the final moral choice in the cellar.
 * * @author Trong Hieu Tran
 */
public class InteractCommand implements GameCommand {

    /** Number of random letters generated for the lever minigame. */
    private int letters = 5;

    /** Time limit in milliseconds allowed for the lever minigame. */
    private long leverTime = 3000;

    /**
     * Executes interaction logic based on the player's current room and inventory.
     * Evaluates room-specific triggers such as puzzles, NPC presence, or item usage.
     *
     * @param p      The player performing the interaction.
     * @param rooms  The list of all rooms in the game.
     * @param items  The list of all world items.
     * @return       A String status code indicating the outcome of the interaction.
     */
    @Override
    public String execute(Player p, ArrayList<Room> rooms, ArrayList<Item> items) {
        Room current = rooms.get(p.currentRoomIndex);
        Scanner sc = new Scanner(System.in);

        // Lever puzzle: Triggered in the Garage with a broken handle
        if (current.name.contains("The garage") && p.hasItem("Broken lever handle")) {
            System.out.print(Texts.t("interact.lever.prompt") + " ");
            if (!sc.nextLine().equalsIgnoreCase("y")) {
                return "LEVER_FIX_CANCELLED";
            }

            boolean won = runLeverMinigame(sc);
            if (won) {
                p.replaceItem("Broken lever handle", "Lever handle");
                System.out.println(Texts.t("interact.lever.success"));
                return "LEVER_FIX_SUCCESS";
            } else {
                System.out.println(Texts.t("interact.lever.failed"));
                return "LEVER_FIX_FAILED";
            }
        }

        // NPC Validation: Interacting requires an NPC to be present
        if (current.npc == null) {
            System.out.println(Texts.t("interact.nothing"));
            return "INTERACT_NO_NPC";
        }

        // Always display NPC bio and dialogue upon interaction
        current.npc.showBio();

        // Final Choice: Kill or Spare the criminal in the cold cellar
        if (!Game.missionComplete && current.name.contains("The cold cellar")) {
            System.out.print(Texts.t("interact.criminal.choicePrompt"));
            String choice = sc.nextLine();

            if (choice.equalsIgnoreCase("k")) {
                System.out.println(Texts.t("interact.criminal.killed"));
                Game.missionComplete = true;
                return "CRIMINAL_KILLED";
            } else if (choice.equalsIgnoreCase("s")) {
                System.out.println(Texts.t("interact.criminal.spared"));
                Game.missionComplete = true;
                return "CRIMINAL_SPARED";
            } else {
                // Hesitation outcome: Depends on whether Leon was used to open the room
                if (!Game.usedLeonToOpenCellar) {
                    System.out.println(Texts.t("interact.criminal.hesitate.leonKills"));
                    System.out.println(Texts.t("interact.criminal.hesitate.leonKills.detail"));
                    Game.missionComplete = true;
                    return "CRIMINAL_KILLED_BY_LEON";
                } else {
                    System.out.println(Texts.t("interact.criminal.hesitate.escaped"));
                    Game.missionComplete = true;
                    return "CRIMINAL_ESCAPED";
                }
            }
        }

        // Safe Puzzle: Apartment 102 (Tobias Reviero)
        if (current.name != null
                && current.name.contains("102")
                && current.npc.name != null
                && current.npc.name.contains("Tobias Reviero")) {

            // Step 1: Discovering the safe requires the Small key
            if (!p.safeDiscovered) {
                if (!p.hasItem("Small key")) {
                    System.out.println(Texts.t("interact.safe.needsKeyToDiscover"));
                    return "SAFE_NEEDS_KEY_TO_DISCOVER";
                }

                System.out.println(Texts.t("interact.safe.discovered"));
                removeFirstIgnoreCase(p.inventory, "Small key");
                p.safeDiscovered = true;
            }

            if (p.safeSolved) {
                System.out.println(Texts.t("interact.safe.alreadySolved"));
                return "SAFE_ALREADY_SOLVED";
            }

            System.out.print(Texts.t("interact.safe.tryUnlockPrompt"));
            if (!sc.nextLine().equalsIgnoreCase("y") && !items.isEmpty()) {
                System.out.println(Texts.t("interact.safe.notAttempted"));
                return "SAFE_NOT_ATTEMPTED";
            }

            // Display hints based on puzzle progress
            if (p.safeProgress >= 0 && p.safeProgress <= 2) {
                System.out.println(Texts.t("interact.safe.hint." + p.safeProgress));
            }

            System.out.println(Texts.t("interact.safe.header"));
            System.out.println(Texts.t("interact.safe.cancel"));
            System.out.println(Texts.tf("interact.safe.inventory", p.inventory));
            System.out.print(Texts.tf("interact.safe.selectIndex", p.inventory.size()));

            int idx;
            try {
                idx = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println(Texts.t("interact.safe.invalidChoice"));
                return "SAFE_INVALID_INPUT";
            }

            if (idx == 0) return "SAFE_CANCELLED";

            if (idx < 1 || idx > p.inventory.size()) {
                System.out.println(Texts.t("interact.safe.indexOutOfBounds"));
                return "SAFE_INVALID_INDEX";
            }

            String chosen = p.inventory.get(idx - 1);
            String[] order = new String[] { "Rotating gear", "Weighted cube", "Lever handle" };
            String needed = order[p.safeProgress];

            if (!chosen.equalsIgnoreCase(needed)) {
                System.out.println(Texts.t("interact.safe.doesNotFit"));
                System.out.println(Texts.t("interact.safe.mechanismNoMove"));
                return "SAFE_WRONG_ITEM";
            }

            removeFirstIgnoreCase(p.inventory, needed);
            System.out.println(Texts.tf("interact.safe.installed", needed));
            p.safeProgress++;

            // Check for puzzle completion
            if (p.safeProgress >= order.length) {
                if (!p.inventory.contains("Code")) {
                    p.inventory.add("Code");
                }
                p.safeSolved = true;
                p.safeProgress = 0;
                System.out.println(Texts.t("interact.safe.opened"));
                System.out.println(Texts.t("interact.safe.receivedCode"));
                return "SAFE_SOLVED_CODE_RECEIVED";
            }

            return "SAFE_PROGRESS_" + p.safeProgress;
        }

        // Secondary Puzzle: Filling the Water Bottle
        if ((current.name.contains("101") || current.name.contains("102")) && p.hasItem("Empty water bottle")) {
            System.out.print(Texts.t("interact.bottle.fillPrompt"));
            if (sc.nextLine().equalsIgnoreCase("y")) {
                p.replaceItem("Empty water bottle", "Full water bottle");
                System.out.println(Texts.t("interact.bottle.filled"));
                return "BOTTLE_FILLED";
            } else {
                System.out.println(Texts.t("interact.bottle.declined"));
                return "BOTTLE_FILL_DECLINED";
            }
        }

        // Leon Interaction: Unlocking the cellar with a bottle
        else if (current.npc != null && current.npc.name != null && current.npc.name.contains("Leon") && p.hasItem("Full water bottle")) {
            System.out.println(Texts.t("interact.leon.thanks"));
            System.out.println(Texts.t("interact.leon.kicksDoor"));
            Game.isCellarLocked = false;
            Game.usedLeonToOpenCellar = true;
            p.replaceItem("Full water bottle", "Empty water bottle");
            return "CELLAR_UNLOCKED";
        }

        return "INTERACT_NO_EFFECT";
    }

    /**
     * Utility method to remove an item from a list while ignoring character case.
     *
     * @param list     The list to remove the item from.
     * @param itemName The name of the item to find and remove.
     */
    private void removeFirstIgnoreCase(ArrayList<String> list, String itemName) {
        for (int i = 0; i < list.size(); i++) {
            String v = list.get(i);
            if (v != null && v.equalsIgnoreCase(itemName)) {
                list.remove(i);
                return;
            }
        }
    }

    /**
     * Executes the lever-fixing minigame. Requires the player to type a random
     * sequence of letters within a specified time limit.
     *
     * @param sc The scanner to read player input.
     * @return true if the input matches the target within the time limit; false otherwise.
     */
    private boolean runLeverMinigame(Scanner sc) {
        String targetLetters = generateRandomLetters(letters);
        System.out.println(Texts.t("interact.leverMinigame.header"));
        System.out.println(Texts.tf("interact.leverMinigame.typeWithin", letters));
        System.out.println(Texts.tf("interact.leverMinigame.showTarget", targetLetters));
        System.out.print(Texts.t("interact.leverMinigame.enterLetters"));

        long start = System.nanoTime();
        String input = sc.nextLine();
        String inputUpperCase = input.toUpperCase();
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        if (elapsedMs > leverTime) {
            System.out.println(Texts.tf("interact.leverMinigame.tooSlow", elapsedMs));
            return false;
        }
        if (!inputUpperCase.equals(targetLetters)) {
            System.out.println(Texts.t("interact.leverMinigame.incorrect"));
            return false;
        }
        return true;
    }

    /**
     * Generates a string of random uppercase letters.
     *
     * @param len The length of the string to generate.
     * @return A String of random uppercase characters.
     */
    private String generateRandomLetters(int len) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = (char) ('A' + r.nextInt(26));
            sb.append(c);
        }
        return sb.toString();
    }
}