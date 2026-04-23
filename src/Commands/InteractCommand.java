package Commands;

import MainGame.Game;
import MainGame.Texts;
import Playuh.Item;
import Playuh.Player;
import Playuh.Room;

import java.util.ArrayList;

public class InteractCommand implements GameCommand {

    @Override
    public String execute(Player p, ArrayList<Room> rooms, ArrayList<Item> items) {
        if (p == null || rooms == null || rooms.isEmpty()) {
            return "No rooms available.";
        }
        if (p.currentRoomIndex < 0 || p.currentRoomIndex >= rooms.size()) {
            return "You are not in a valid room.";
        }

        Room current = rooms.get(p.currentRoomIndex);

        if (current.npc == null) {
            return Texts.t("interact.nothing");
        }

        StringBuilder out = new StringBuilder();
        out.append(current.npc.getBioText());

        if (!Game.missionComplete && current.name != null && current.name.contains("The cold cellar")) {
            out.append("\n").append(Texts.t("interact.criminal.choicePrompt"));
            out.append("\nUse the window flow to choose what to do next.");
        }

        if (current.name != null && current.name.contains("102")
                && current.npc.name != null
                && current.npc.name.contains("Tobias Reviero")) {
            out.append("\nThis room contains the safe puzzle.");
        }

        if (current.name != null && current.name.contains("The garage")
                && p.hasItem("Broken lever handle")) {
            out.append("\nThis room contains the lever puzzle.");
        }

        if (current.npc.name != null
                && current.npc.name.contains("Leon")
                && p.hasItem("Full water bottle")) {
            Game.isCellarLocked = false;
            Game.usedLeonToOpenCellar = true;
            p.replaceItem("Full water bottle", "Empty water bottle");
            out.append("\nLeon helps you unlock the cellar.");
        }

        return out.toString();
    }
}