package model;


import org.json.JSONObject;
import persistence.Writable;

import static model.Board.RED;

// Structure for the persistence functionality is inspired by the UBC: CPSC 210 Software Construction WorkRoomApp

// Represents playing as an Operative with associated actions
public class Operative implements Writable {
    private String teamName;     // Red or blue team
    private int teamScore;       // Tracker for the team's score


    // REQUIRES: team is either "red" or "blue" (case-insensitive)
    // MODIFIES: this
    // EFFECTS: assigns a team to the operative and initializes the score to 0
    public Operative(String team) {
        this.teamName = team;
        this.teamScore = 0;
    }


    // MODIFIES: this
    // EFFECTS: constructs a new Operative given data from a loaded game state
    public Operative(String team, int score) {
        this.teamName = team;
        this.teamScore = score;
    }

    // MODIFIES: this
    // EFFECTS: increments the team's score by 1
    public void incrementScore() {
        teamScore += 1;
    }

    // EFFECTS: returns the team's name
    public String getTeamName() {
        return teamName;
    }

    // EFFECTS: returns the team's score
    public int getTeamScore() {
        return teamScore;
    }

    // EFFECTS: returns things in this board as a JSON object
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        if (teamName.equals(RED)) {
            json.put("redOperativeTeamName", teamName);
            json.put("redOperativeScore", teamScore);
        } else {
            json.put("blueOperativeTeamName", teamName);
            json.put("blueOperativeScore", teamScore);
        }
        return json;
    }

}
