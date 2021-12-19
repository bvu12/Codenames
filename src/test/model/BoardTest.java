package model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static model.Board.*;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    private Board redStartBoard;
    private Board blueStartBoard;
    private Board redAddedCards;
    private Board blueAddedCards;

    @BeforeEach
    void setup() {
        redStartBoard = new Board(RED);
        blueStartBoard = new Board(BLUE);

        redAddedCards = new Board(RED);
        redAddedCards.addCards(RED);
        redAddedCards.addCards(BLUE);
        redAddedCards.addCards(NEUTRAL);
        redAddedCards.addCards(ASSASSIN);

        blueAddedCards = new Board(BLUE);
        blueAddedCards.addCards(RED);
        blueAddedCards.addCards(BLUE);
        blueAddedCards.addCards(NEUTRAL);
        blueAddedCards.addCards(ASSASSIN);

    }

    @Test
    void testConstructorForNewGame() {
        assertTrue(redStartBoard.getBoard().isEmpty());
        assertTrue(redStartBoard.getDictionary().isEmpty());
        assertEquals(RED, redStartBoard.getStartingTeam());

        assertTrue(blueStartBoard.getBoard().isEmpty());
        assertTrue(blueStartBoard.getDictionary().isEmpty());
        assertEquals(BLUE, blueStartBoard.getStartingTeam());

    }

    @Test
    void testConstructorForLoadedGame() {
        String startingTeam = RED;
        String currentTeam = BLUE;
        String currentPlayer = "Operative";
        int numRedCards = 5;
        int numBlueCards = 8;

        Board loadedBoard = new Board(startingTeam,currentTeam,currentPlayer,numRedCards,numBlueCards);

        assertTrue(loadedBoard.getBoard().isEmpty());
        assertEquals(startingTeam,loadedBoard.getStartingTeam());
        assertEquals(currentTeam,loadedBoard.getCurrentTeam());
        assertEquals(currentPlayer,loadedBoard.getCurrentPlayer());
        assertEquals(numRedCards,loadedBoard.getNumOriginalRedCards());
        assertEquals(numBlueCards,loadedBoard.getNumOriginalBlueCards());
    }

    @Test
    void testAddCardsForNewGame() {
        assertTrue(redStartBoard.getBoard().isEmpty());
        assertEquals(0, redStartBoard.getRemainingCards(RED));
        assertEquals(0, redStartBoard.getRemainingCards(BLUE));
        assertEquals(0, redStartBoard.getRemainingCards(NEUTRAL));
        assertEquals(0, redStartBoard.getRemainingCards(ASSASSIN));


        redStartBoard.addCards(RED);
        redStartBoard.addCards(BLUE);
        redStartBoard.addCards(NEUTRAL);
        redStartBoard.addCards(ASSASSIN);

        assertEquals(9, redStartBoard.getRemainingCards(RED));
        assertEquals(8, redStartBoard.getRemainingCards(BLUE));
        assertEquals(7, redStartBoard.getRemainingCards(NEUTRAL));
        assertEquals(1, redStartBoard.getRemainingCards(ASSASSIN));

        assertEquals(8, blueAddedCards.getRemainingCards(RED));
        assertEquals(9, blueAddedCards.getRemainingCards(BLUE));
        assertEquals(7, blueAddedCards.getRemainingCards(NEUTRAL));
        assertEquals(1, blueAddedCards.getRemainingCards(ASSASSIN));

        Board bd1 = new Board(RED);
        Board bd2 = new Board(BLUE);
        Board bd3 = new Board(RED);
        Board bd4 = new Board(BLUE);
        bd1.addCards(BLUE);
        assertEquals(0, bd1.getRemainingCards(RED));
        assertEquals(8, bd1.getRemainingCards(BLUE));

        bd2.addCards(ASSASSIN);
        assertEquals(1, bd2.getRemainingCards(ASSASSIN));

        bd3.addCards("???");
        assertEquals(0, bd2.getRemainingCards("???"));

        bd4.addCards(NEUTRAL);
        assertEquals(7, bd4.getRemainingCards(NEUTRAL));

    }

    @Test
    void testAddCardsOneCardForLoadedGame() {
        Board myBoard = new Board(BLUE);
        Card aCard = new Card("WORD", BLUE);
        assertEquals(0,myBoard.getBoard().size());

        myBoard.addCards(aCard);
        assertEquals(1,myBoard.getBoard().size());
    }

    @Test
    void testGetRemainingCards() {
        assertEquals(0, redStartBoard.getRemainingCards(RED));
        redStartBoard.addCards(RED);
        assertEquals(9, redStartBoard.getRemainingCards(RED));

        // Make all RED cards visible - therefore, no more remaining red cards
        for (Card c : redStartBoard.getBoard()) {
            if (c.getTeam().equals(RED)) {
                c.makeVisibleTeam();
            }
        }
        assertEquals(0, redStartBoard.getRemainingCards(RED));

        // Set the first two BLUE cards to visible
        int counter = 0;
        for (Card c : blueAddedCards.getBoard()) {
            if (c.getTeam().equals(BLUE) && counter < 4) {
                c.makeVisibleTeam();
                counter += 1;
            }
        }

        assertEquals(5, blueAddedCards.getRemainingCards(BLUE));
        assertEquals(8, blueAddedCards.getRemainingCards(RED));
        assertEquals(7, blueAddedCards.getRemainingCards(NEUTRAL));

        // Set all seven NEUTRAL cards to visible
        for (Card c: blueAddedCards.getBoard()) {
            if (c.getTeam().equals(NEUTRAL)) {
                c.makeVisibleTeam();
            }
        }

        assertEquals(0, blueAddedCards.getRemainingCards(NEUTRAL));

        //Assassin cards
        assertEquals(1, blueAddedCards.getRemainingCards(ASSASSIN));
        for (Card c: blueAddedCards.getBoard()) {
            if (c.getTeam().equals(ASSASSIN)) {
                c.makeVisibleTeam();
                counter++;
            }
        }
        assertEquals(0, blueAddedCards.getRemainingCards(ASSASSIN));


    }

    @Test
    void testSetBoardIndices() {
        // Red not initialized
        redStartBoard.setBoardIndices();
        int counter = 1;
        for (Card c : redStartBoard.getBoard()) {
            assertEquals(counter, c.getIndex());
            counter += 1;
        }

        // Red has indices
        redAddedCards.setBoardIndices();
        counter = 1;
        for (Card c : redAddedCards.getBoard()) {
            assertEquals(counter, c.getIndex());
            counter += 1;
        }

    }

    @Test
    void testGetBoardInitialized() {
        List<Card> redAddedCardsGetBoard = redAddedCards.getBoard();
        List<Card> blueAddedCardsGetBoard = blueAddedCards.getBoard();


        assertFalse(redAddedCardsGetBoard.isEmpty());
        assertFalse(blueAddedCardsGetBoard.isEmpty());
        assertNotEquals(redAddedCardsGetBoard, blueAddedCardsGetBoard);
    }

    @Test
    void testGetBoardNotInitialized() {
        List<Card> redStartBoardGetBoard = redStartBoard.getBoard();
        List<Card> blueStartBoardGetBoard = blueStartBoard.getBoard();

        assertTrue(redStartBoardGetBoard.isEmpty());
        assertTrue(blueStartBoardGetBoard.isEmpty());
    }

    @Test
    void testShuffle() {
        Board blueShuffledBoard;
        blueShuffledBoard = new Board(BLUE);
        blueShuffledBoard.initializeGameDictionary();
        blueShuffledBoard.addCards(RED);
        blueShuffledBoard.addCards(BLUE);
        blueShuffledBoard.addCards(NEUTRAL);
        blueShuffledBoard.addCards(ASSASSIN);
        blueShuffledBoard.shuffle();

        String unshuffledWord;
        String shuffledWord;

        int commonElements = 0;
        for (int i = 0; i < NUM_CARDS; i++) {
            unshuffledWord = blueAddedCards.getBoard().get(i).getWord();
            shuffledWord = blueShuffledBoard.getBoard().get(i).getWord();

            if (unshuffledWord.equals(shuffledWord)) {
                commonElements++;
            }
        }

        // Should not match
        assertTrue(commonElements < NUM_CARDS);

    }

    @Test
    void testInitializeDictionary() {
        Dictionary myDict;
        myDict = redStartBoard.getDictionary();
        assertTrue(myDict.isEmpty());

        myDict.initializeDictionary();
        assertFalse(myDict.isEmpty());
    }

    @Test
    void testGetDictionaryEmpty() {
        Dictionary myDict;
        myDict = redStartBoard.getDictionary();
        assertTrue(myDict.isEmpty());
    }

    @Test
    void testGetDictionaryInitialized() {
        Dictionary myDict;
        myDict = redStartBoard.getDictionary();
        myDict.initializeDictionary();
        assertFalse(myDict.isEmpty());
    }

    @Test
    void testGetStartingTeam() {
        String redStartingTeam = redStartBoard.getStartingTeam();
        String blueStartingTeam = blueAddedCards.getStartingTeam();

        assertEquals(RED, redStartingTeam);
        assertEquals(BLUE, blueStartingTeam);
    }

    @Test
    void testSetCurrentTeam() {
        assertEquals(RED, redStartBoard.getCurrentTeam());
        redStartBoard.setCurrentTeam(RED);
        assertEquals(RED, redStartBoard.getCurrentTeam());
        redStartBoard.setCurrentTeam(BLUE);
        assertEquals(BLUE, redStartBoard.getCurrentTeam());

    }

    @Test
    void testGetCurrentTeam() {
        assertEquals(BLUE, blueStartBoard.getCurrentTeam());
        blueStartBoard.setCurrentTeam(BLUE);
        assertEquals(BLUE, blueStartBoard.getCurrentTeam());
        blueStartBoard.setCurrentTeam(RED);
        assertEquals(RED, blueStartBoard.getCurrentTeam());
    }

    @Test
    void testGetNumOriginalRedCards() {
        assertEquals(NUM_CARDS_TEAM0,redStartBoard.getNumOriginalRedCards());
    }

    @Test
    void testGetNumOriginalBlueCards() {
        assertEquals(NUM_CARDS_TEAM1,redStartBoard.getNumOriginalBlueCards());
    }

    @Test
    void testToJson() {
        JSONObject json = new JSONObject();
        json = redAddedCards.toJson();

        assertEquals(RED,json.get("startingTeam"));
        assertEquals(RED,json.get("currentTeam"));
        assertEquals(NUM_CARDS_TEAM0,json.get("numRedCards"));
        assertEquals(NUM_CARDS_TEAM1,json.get("numBlueCards"));
    }

    @Test
    void testCardsToJson() {
        JSONObject json = new JSONObject();
        json = redAddedCards.toJson();

        JSONArray jsonArray = new JSONArray();
        jsonArray = json.getJSONArray("cards");

        // 25 items in array
        assertEquals(25, jsonArray.length());
    }

}