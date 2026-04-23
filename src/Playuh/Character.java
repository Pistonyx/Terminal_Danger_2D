package Playuh;

import MainGame.Texts;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Character {

    public String id;
    public String name;
    public String role;
    public String homeLocationId;
    public String bio;
    public String dialogue;

    public Character() {
    }

    public Character(String n, String b, String d) {
        this.name = n;
        this.bio = b;
        this.dialogue = d;
    }

    public String getBioText() {
        StringBuilder sb = new StringBuilder();
        sb.append(Texts.tf("npc.header", name)).append("\n");

        if (bio != null && !bio.isBlank()) {
            sb.append(bio).append("\n");
        }

        if (dialogue != null && !dialogue.isBlank()) {
            sb.append(Texts.tf("npc.dialogue", dialogue));
        } else {
            sb.append(Texts.t("npc.noDialogue"));
        }

        return sb.toString();
    }

    public void showBio() {
        System.out.println(getBioText());
    }
}