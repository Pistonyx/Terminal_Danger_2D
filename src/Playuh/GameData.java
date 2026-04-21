package Playuh;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Acts as the primary data container for the game's world state.
 * This class facilitates the loading of items, characters, rooms, and quests
 * from external JSON resources using the GSON library.
 * * @author Trong Hieu Tran
 */
public class GameData {

    /** List of all items defined in the game data. */
    public ArrayList<Item> items;

    /** List of all characters (NPCs) defined in the game data. */
    public ArrayList<Character> characters;

    /** List of all rooms (locations) defining the game map. */
    public ArrayList<Room> locations;

    /**
     * Loads game data from a JSON file located on the classpath.
     * Utilizes UTF-8 encoding to ensure special characters in dialogue or
     * descriptions are rendered correctly.
     *
     * @param resourcePath The path to the JSON resource file.
     * @return A GameData object populated with the parsed JSON data.
     * @throws RuntimeException If the resource is missing or parsing fails.
     */
    public static GameData loadGameDataFromResources(String resourcePath) {
        Gson gson = new Gson();

        String normalized = resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;

        try (InputStream in = GameData.class.getResourceAsStream(normalized)) {
            if (in == null) {
                throw new RuntimeException("Resource not found on classpath: " + normalized);
            }

            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                return gson.fromJson(reader, GameData.class);
            }
        } catch (Exception e) {
            throw new RuntimeException("Problem while loading json: " + e.getMessage(), e);
        }
    }

    /**
     * Alias for {@link #loadGameDataFromResources(String)}.
     * Provided for backwards compatibility with existing code calls.
     *
     * @param resourcePath The path to the JSON resource file.
     * @return A GameData object populated with the parsed JSON data.
     */
    public static GameData loadGamaDataFromResources(String resourcePath) {
        return loadGameDataFromResources(resourcePath);
    }

    /**
     * Searches for a specific room within the loaded locations based on its unique ID.
     *
     * @param id The unique string identifier for the room.
     * @return The Room object matching the provided ID.
     * @throws IllegalArgumentException If no location with the specified ID exists.
     */
    public Room findLocation(String id) {
        for (Room l : locations) {
            if (l.getId().equals(id)) {
                return l;
            }
        }
        throw new IllegalArgumentException("There doesn't exist a location with the id: " + id);
    }
}