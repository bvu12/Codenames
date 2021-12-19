package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Structure for the persistence functionality is inspired by the UBC: CPSC 210 Software Construction WorkRoomApp

// Represents the game board with 5x5 (25) Card objects
public class Board implements Writable {
    public static final int NUM_CARDS = 25;            // Number of cards
    public static final String RED = "RED";            // Name of the red cards
    public static final String BLUE = "BLUE";          // Name of the blue cards
    public static final String NEUTRAL = "NEUTRAL";    // Name of the neutral cards
    public static final String ASSASSIN = "ASSASSIN";  // Name of the assassin card
    public static final int NUM_CARDS_TEAM0 = 9;      // Number of agents for the first team
    public static final int NUM_CARDS_TEAM1 = 8;      // Number of agents for the second team
    public static final int NUM_CARDS_NEUTRAL = 7;    // Number of neutral cards
    private static final int NUM_CARDS_ASSASSIN = 1;   // Number of assassins


    private List<Card> board;   // Board for this game
    private Dictionary dictionary;  // Dictionary for this game
    private String startingTeam;    // Which team starts first
    private String currentTeam;     // Which team is it currently
    private String currentPlayer;   // Spymaster or Operative
    private int numRedCards;        // Number of RED agents
    private int numBlueCards;       // Number of BLUE agents


    // REQUIRES: startingTeam is either "red" or "blue" (case-insensitive)
    // MODIFIES: this
    // EFFECTS: initializes fields for board
    public Board(String startingTeam) {
        this.board = new ArrayList<>();
        this.dictionary = new Dictionary();


        this.startingTeam = startingTeam.toUpperCase();
        this.currentTeam = startingTeam;

        this.currentPlayer = "SPYMASTER";

        if (startingTeam.equals(RED)) {
            this.numRedCards = NUM_CARDS_TEAM0;
            this.numBlueCards = NUM_CARDS_TEAM1;
        } else {
            this.numRedCards = NUM_CARDS_TEAM1;
            this.numBlueCards = NUM_CARDS_TEAM0;
        }

    }

    // MODIFIES: this
    // EFFECTS: constructs a new board given data from a loaded game state
    public Board(String startingTeam, String currentTeam, String currentPlayer, int numRedCards, int numBlueCards) {
        this.board = new ArrayList<>();
        this.startingTeam = startingTeam;
        this.currentTeam = currentTeam;
        this.currentPlayer = currentPlayer;
        this.numRedCards = numRedCards;
        this.numBlueCards = numBlueCards;
    }

    // MODIFIES: this
    // EFFECTS: randomly selects 25 words from the dictionary and shuffles it
    public void initializeGameDictionary() {
        dictionary.initializeDictionary();
        dictionary.shuffleDictionary();
    }


    // MODIFIES: this
    // EFFECTS: loops through an adds maxIndex cards of cardType to board
    public void addCards(String cardType) {
        int numCards;
        switch (cardType) {
            case ASSASSIN:
                numCards = NUM_CARDS_ASSASSIN;
                break;
            case NEUTRAL:
                numCards = NUM_CARDS_NEUTRAL;
                break;
            case RED:
                numCards = numRedCards;
                break;
            default:
                numCards = numBlueCards;
                break;
        }


        Card makeCard;
        for (int i = 0; i < numCards; i++) {
            makeCard = new Card(dictionary.getNextWord(), cardType);
            board.add(makeCard);
        }

    }

    // MODIFIES: this
    // EFFECTS: adds a new card to board given data from a loaded game state
    public void addCards(Card card) {
        board.add(card);
    }

    // MODIFIES: this
    // EFFECTS: randomly shuffles the board
    public void shuffle() {
        Collections.shuffle(board);
    }

    // EFFECTS: Returns the number of cards remaining for team
    public int getRemainingCards(String team) {
        int totalCardCounter = 0;
        int revealedCardCounter = 0;

        for (Card c : board) {
            // Count the total number of team cards
            if (c.getTeam().equals(team)) {
                totalCardCounter += 1;
                // Subtract the revealed team cards to get the remaining number
                if (c.getVisibleTeam().equals(team)) {
                    revealedCardCounter += 1;
                }

            }
        }

        return totalCardCounter - revealedCardCounter;
    }

    // EFFECTS: Returns the board
    public List<Card> getBoard() {
        return board;
    }

    // MODIFIES: this
    // EFFECTS: gives each card an index
    public void setBoardIndices() {
        int counter = 1;
        for (Card c : board) {
            c.setIndex(counter);
            counter += 1;
        }
    }

    // EFFECTS: returns the dictionary field
    public Dictionary getDictionary() {
        return dictionary;
    }

    // EFFECTS: returns the starting team
    public String getStartingTeam() {
        return startingTeam;
    }

    // REQUIRES: team is one of "RED" or "BLUE"
    // MODIFIES: this
    // EFFECTS: Sets currentTeam to the provided team
    public void setCurrentTeam(String team) {
        this.currentTeam = team;
    }

    // EFFECTS: returns currentTeam
    public String getCurrentTeam() {
        return currentTeam;
    }

    // REQUIRES: player is one of "SPYMASTER" or "OPERATIVE"
    // MODIFIES: this
    // EFFECTS: Sets currentPlayer to the provided player
    public void setCurrentPlayer(String player) {
        this.currentPlayer = player;
    }

    // EFFECTS: returns currentPlayer
    public String getCurrentPlayer() {
        return currentPlayer;
    }

    // EFFECTS: returns numRedCards
    public int getNumOriginalRedCards() {
        return numRedCards;
    }

    // EFFECTS: returns numBlueCards
    public int getNumOriginalBlueCards() {
        return numBlueCards;
    }

    // ==== PHASE 2: Data persistence:

    // EFFECTS: returns things in this board as a JSON object
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("startingTeam", getStartingTeam());
        json.put("currentTeam", getCurrentTeam());
        json.put("currentPlayer", getCurrentPlayer());
        json.put("numRedCards", numRedCards);
        json.put("numBlueCards", numBlueCards);
//        json.put("dictionary", dictionaryToJson());
        json.put("cards", cardsToJson());
        return json;
    }

    // EFFECTS: returns cards in this board as a JSON array
    public JSONArray cardsToJson() {
        JSONArray jsonArray = new JSONArray();

        for (Card card : board) {
            jsonArray.put(card.toJson());
        }

        return jsonArray;
    }

}