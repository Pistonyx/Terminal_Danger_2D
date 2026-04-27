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

    // These are the editable map hot zones for this room, loaded from gamedata.json.
    public ArrayList<HotZone> hotZones = new ArrayList<>();

    public Room() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // This method returns the room's hot zones.
    public ArrayList<HotZone> getHotZones() {
        if (hotZones == null) {
            hotZones = new ArrayList<>();
        }
        return hotZones;
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