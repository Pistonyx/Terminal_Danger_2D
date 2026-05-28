package Playuh;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

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

    // Constructor for default hotzones
    public HotZone(String id, HotZoneType type, int gridX, int gridY, String targetId, String label) {
        this.id = id;
        this.type = type.name();
        this.gridX = gridX;
        this.gridY = gridY;
        this.targetId = targetId;
        this.label = label;
    }
    // Constructor for custom hotzones
    public HotZone(HotZoneType type, int gridX, int gridY, String targetId, String label) {
        this(null, type, gridX, gridY, targetId, label); // Call the more complete constructor with null id
    }


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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HotZone hotZone = (HotZone) o;

        // If both IDs are non-null and non-blank, use them for comparison
        if (id != null && !id.isBlank() && hotZone.id != null && !hotZone.id.isBlank()) {
            return id.equals(hotZone.id);
        }

        // Otherwise, compare based on other identifying properties
        return gridX == hotZone.gridX &&
                gridY == hotZone.gridY &&
                Objects.equals(type, hotZone.type) &&
                Objects.equals(targetId, hotZone.targetId);
    }

    @Override
    public int hashCode() {
        if (id != null && !id.isBlank()) {
            return Objects.hash(id);
        }
        return Objects.hash(type, gridX, gridY, targetId);
    }

    @Override
    public String toString() {
        return "HotZone{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", gridX=" + gridX +
                ", gridY=" + gridY +
                ", label='" + label + '\'' +
                ", targetId='" + targetId + '\'' +
                '}';
    }
}