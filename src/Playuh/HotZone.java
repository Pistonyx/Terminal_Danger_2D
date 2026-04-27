package Playuh;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HotZone {
    public String id;
    public String type;
    public int gridX;
    public int gridY;
    public String label;
    public String targetId;

    public HotZone() {
    }

    // This method converts the text type from gamedata.json into a HotZoneType.
    public HotZoneType getType() {
        if (type == null) {
            return HotZoneType.CUSTOM;
        }

        try {
            return HotZoneType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return HotZoneType.CUSTOM;
        }
    }
}
