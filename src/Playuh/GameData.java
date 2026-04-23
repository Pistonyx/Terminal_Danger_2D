package Playuh;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameData {

    public ArrayList<Item> items;
    public ArrayList<Character> characters;
    public ArrayList<Room> locations;

    public static GameData loadGameDataFromResources(String resourcePath) {
        ObjectMapper mapper = new ObjectMapper();
        String normalized = resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;

        try (InputStream in = GameData.class.getResourceAsStream(normalized)) {
            if (in == null) {
                throw new RuntimeException("Resource not found on classpath: " + normalized);
            }

            GameData data = mapper.readValue(in, GameData.class);
            if (data.items == null) data.items = new ArrayList<>();
            if (data.characters == null) data.characters = new ArrayList<>();
            if (data.locations == null) data.locations = new ArrayList<>();
            return data;
        } catch (Exception e) {
            throw new RuntimeException("Problem while loading json: " + e.getMessage(), e);
        }
    }

    public static GameData loadGamaDataFromResources(String resourcePath) {
        return loadGameDataFromResources(resourcePath);
    }

    public Room findLocation(String id) {
        if (locations == null) {
            throw new IllegalArgumentException("No locations loaded.");
        }

        for (Room l : locations) {
            if (l != null && l.getId() != null && l.getId().equals(id)) {
                return l;
            }
        }
        throw new IllegalArgumentException("There doesn't exist a location with the id: " + id);
    }
}