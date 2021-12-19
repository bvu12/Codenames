package model;

import org.json.JSONObject;
import persistence.Writable;

import java.awt.*;

import static model.Board.RED;
import static model.Board.BLUE;
import static model.Board.NEUTRAL;
import static model.Board.ASSASSIN;


// Structure for the persistence functionality is inspired by the UBC: CPSC 210 Software Construction WorkRoomApp

// Represents a single card in the game, with a type (Red, Blue, Neutral or Assassin) and an associated word and index
public class Card implements Writable {
    private String word;        // Word to be displayed on this card
    private String team;        // Team (Red, Blue, Neutral or Assassin)
    private String visibleTeam; // What team is visible to the operatives? (i.e. has this card been in play yet)
    private int index;       // 1-based index for the card

    // REQUIRES: givenTeam is "red", "blue", "neutral" or "assassin" (case-insensitive)
    // MODIFIES: this
    // EFFECTS:  create a card with an associated word and team, initialize visibility to "?" (not visible)
    public Card(String givenWord, String givenTeam) {
        this.word = givenWord;
        this.team = givenTeam;
        this.visibleTeam = "?";
        this.index = 0;
    }

    // MODIFIES: this
    // EFFECTS: constructs a new card given data from a loaded game state
    public Card(String word, String team, String visibleTeam, int index) {
        this.word = word;
        this.team = team;
        this.visibleTeam = visibleTeam;
        this.index = index;
    }

    // EFFECTS: return the card's associated word
    public String getWord() {
        return word;
    }

    // EFFECTS: return the card's associated team
    public String getTeam() {
        return team;
    }

    // EFFECTS: return the card's visible team
    public String getVisibleTeam() {
        return visibleTeam;
    }


    // EFFECTS: return false if the visible team is not yet revealed
    public boolean isVisibleTeam() {
        return !visibleTeam.equals("?");
    }

    // EFFECTS: return the card's index
    public int getIndex() {
        return index;
    }

    // MODIFIES: this
    // EFFECTS: set the card's index
    public void setIndex(int index) {
        this.index = index;
    }

    // MODIFIES: this
    // EFFECTS: if this card has been put in play, reveal the actual team to the operatives
    public void makeVisibleTeam() {
        this.visibleTeam = this.team;
    }

    // EFFECTS: returns a Color object associated with the given team
    public Color getCardColor() {
        if (team.equals(RED)) {
            return new Color(247, 138, 114);
        } else if (team.equals(BLUE)) {
            return new Color(146, 184, 240);
        } else if (team.equals(NEUTRAL)) {
            return new Color(184, 172, 160);
        } else {
            return new Color(54, 54, 54);
        }
    }

    @Override
    // EFFECTS: returns things in this card as a JSON object
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("word", word);
        json.put("team", team);
        json.put("visibleTeam", visibleTeam);
        json.put("index", index);
        return json;
    }


}
