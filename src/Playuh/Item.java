package Playuh;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {

    public String id;
    public String name;
    public String type;
    public String description;

    public Item() {
    }

    public Item(String name) {
        this.name = name;
    }

    public void showDescription(){
        System.out.println("\n--- Item: " + name + " ---");

        if (description != null && !description.isBlank()) {
            System.out.println("Description: '" + description + "'");
        } else {
            System.out.println("This item doesnt have a description");
        }
    }
}