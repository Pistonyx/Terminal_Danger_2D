package Commands;

import Playuh.Item;
import Playuh.Player;
import Playuh.Room;

import java.util.ArrayList;

public class HelpCommand implements GameCommand {

    public HelpCommand(String fileName) {
    }

    @Override
    public String execute(Player p, ArrayList<Room> rooms, ArrayList<Item> items) {
        return """
Commands:
h - help
n - next room
p - previous room
s - search
i - interact
d - drop item
items - inspect inventory item
quit - exit
""";
    }
}