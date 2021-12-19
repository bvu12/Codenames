package model;

import org.json.JSONObject;
import persistence.Writable;

import static model.Board.RED;

// Structure for the persistence functionality is inspired by the UBC: CPSC 210 Software Construction WorkRoomApp

// Represents playing as a Spymaster with associated actions
public class Spymaster implements Writable {
    private String teamName;     // Red or blue team
    private String hint;         // Hint provided by the operative
    private int guesses;         // Guesses provided by the operative


    // REQUIRES: team is either "red" or "blue" (case-insensitive)
    // MODIFIES: this
    // EFFECTS: assigns a team to the spymaster
    public Spymaster(String team) {
        this.teamName = team;
        this.hint = "";
        this.guesses = 0;
    }

    // MODIFIES: this
    // EFFECTS: constructs a new Spymaster given data from a loaded game state
    public Spymaster(String team, String hint, int guesses) {
        this.teamName = team;
        this.hint = hint;
        this.guesses = guesses;
    }

    // EFFECTS: returns the team's name
    public String getTeamName() {
        return teamName;
    }

    // MODIFIES: this
    // EFFECTS: stores the operative's hint
    public void setHint(String givenHint) {
        hint = givenHint;
    }

    // REQUIRES: givenGuesses is >= 0
    // MODIFIES: this
    // EFFECTS: stores the operative's number of guesses
    public void setGuesses(int givenGuesses) {
        guesses = givenGuesses;
    }

    // REQUIRES: guesses to be >= 1
    // EFFECTS: decrements the number of guesses remaining by one
    public void decrementGuesses() {
        this.guesses = guesses - 1;
    }

    // EFFECTS: returns the operative's hint
    public String getHint() {
        return hint;
    }

    // EFFECTS: returns the operative's number of guesses
    public int getGuesses() {
        return guesses;
    }

    // ==== PHASE 2: Data persistence:

    // EFFECTS: returns things in this board as a JSON object
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        if (teamName.equals(RED)) {
            json.put("redSpymasterTeamName", teamName);
            json.put("redSpymasterHint", hint);
            json.put("redSpymasterGuesses", guesses);
        } else {
            json.put("blueSpymasterTeamName", teamName);
            json.put("blueSpymasterHint", hint);
            json.put("blueSpymasterGuesses", guesses);
        }
        return json;
    }
}