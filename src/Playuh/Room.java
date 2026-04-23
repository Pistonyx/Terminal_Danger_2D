package Playuh;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Room {

    public boolean hasItem = true;
    public Character npc;

    private String id;
    public String name;
    public String description;

    @JsonProperty("neighbours")
    private ArrayList<String> neighbors;

    private ArrayList<String> lootTable;

    public ArrayList<String> storedItems = new ArrayList<>();

    public Room() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

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