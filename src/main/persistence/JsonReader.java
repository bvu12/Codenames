package persistence;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import model.Board;

import static model.Board.RED;

import model.Card;
import model.Operative;
import model.Spymaster;
import org.json.*;

// Structure for the persistence functionality is inspired by the UBC: CPSC 210 Software Construction WorkRoomApp

// Represents a reader that reads workroom from JSON data stored in file
public class JsonReader {
    private String source;
    private JSONObject jsonObject;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // MODIFIES: this
    // EFFECTS: reads json from file
    // throws IOException if an error occurs reading data from file
    public void read() throws IOException {
        String jsonData = readFile(source);
        this.jsonObject = new JSONObject(jsonData);

    }


    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: returns a Board object from the read-in jsonObject
    public Board readBoard() {
        return parseBoard(jsonObject);
    }

    // EFFECTS: parses board from JSON object and returns it
    private Board parseBoard(JSONObject jsonObject) {

        // Fields
        String startingTeam = jsonObject.getString("startingTeam");
        String currentTeam = jsonObject.getString("currentTeam");
        String currentPlayer = jsonObject.getString("currentPlayer");
        int numRedCards = jsonObject.getInt("numRedCards");
        int numBlueCards = jsonObject.getInt("numBlueCards");

        // Cards
        Board bd = new Board(startingTeam, currentTeam, currentPlayer, numRedCards, numBlueCards);
        addCards(bd, jsonObject);

        return bd;
    }


    // MODIFIES: bd
    // EFFECTS: parses cards from JSON object and adds them to the board
    private void addCards(Board bd, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("cards");
        for (Object json : jsonArray) {
            JSONObject nextCard = (JSONObject) json;
            addCard(bd, nextCard);
        }
    }

    // MODIFIES: bd
    // EFFECTS: parses card from JSON object and adds it to the board
    private void addCard(Board bd, JSONObject jsonObject) {
        String word = jsonObject.getString("word");
        String team = jsonObject.getString("team");
        String visibleTeam = jsonObject.getString("visibleTeam");
        int index = jsonObject.getInt("index");

        Card card = new Card(word, team, visibleTeam, index);
        bd.addCards(card);
    }

    // REQUIRES: team to be "RED" or "BLUE"
    // EFFECTS: returns a Spymaster object from the read-in jsonObject
    public Spymaster readSpymaster(String team) {
        return parseSpymaster(jsonObject, team);
    }

    // EFFECTS: parses spymaster from JSON object and returns it
    private Spymaster parseSpymaster(JSONObject jsonObject, String team) {
        String teamName;
        String hint;
        int guesses;


        if (team.equals(RED)) {
            teamName = jsonObject.getString("redSpymasterTeamName");
            hint = jsonObject.getString("redSpymasterHint");
            guesses = jsonObject.getInt("redSpymasterGuesses");
        } else {
            teamName = jsonObject.getString("blueSpymasterTeamName");
            hint = jsonObject.getString("blueSpymasterHint");
            guesses = jsonObject.getInt("blueSpymasterGuesses");
        }

        return new Spymaster(teamName, hint, guesses);
    }

    // REQUIRES: team to be "RED" or "BLUE"
    // EFFECTS: returns an Operative object from the read-in jsonObject
    public Operative readOperative(String team) {
        return parseOperative(jsonObject, team);
    }

    // EFFECTS: parses operative from JSON object and returns it
    private Operative parseOperative(JSONObject jsonObject, String team) {
        String teamName;
        int score;


        if (team.equals(RED)) {
            teamName = jsonObject.getString("redOperativeTeamName");
            score = jsonObject.getInt("redOperativeScore");
        } else {
            teamName = jsonObject.getString("blueOperativeTeamName");
            score = jsonObject.getInt("blueOperativeScore");
        }

        return new Operative(teamName, score);
    }
}
