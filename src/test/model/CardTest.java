package model;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static model.Board.*;
import static org.junit.jupiter.api.Assertions.*;

public class CardTest {
    private Card cardRed;
    private Card cardBlue;
    private String word = "TESTING";
    private String redTeamString = RED;
    private String blueTeamString = BLUE;
    private String invisibleTeamString = "?";

    @BeforeEach
    void setup() {
        cardRed = new Card(word, redTeamString);
        cardBlue = new Card(word, blueTeamString);
    }

    @Test
    void testConstructorForNewGame() {
        assertEquals(word, cardRed.getWord());
        assertEquals(word, cardBlue.getWord());

        assertEquals(redTeamString, cardRed.getTeam());
        assertEquals(blueTeamString, cardBlue.getTeam());

        assertEquals(invisibleTeamString, cardRed.getVisibleTeam());
        assertEquals(invisibleTeamString, cardBlue.getVisibleTeam());

    }

    @Test
    void testConstructorForLoadedGame() {
        String aWord = "Word";
        String aTeam = redTeamString;
        String visibleTeam = redTeamString;
        int index = 14;

        Card card = new Card(aWord,aTeam,visibleTeam,index);

        assertEquals(aWord, card.getWord());
        assertEquals(aTeam, card.getTeam());
        assertEquals(visibleTeam, card.getVisibleTeam());
        assertEquals(index, card.getIndex());
    }

    @Test
    void testMakeVisibleTeam() {
        // Red has revealed their team as RED
        cardRed.makeVisibleTeam();
        assertEquals(redTeamString,cardRed.getVisibleTeam());

        // Blue has not yet revealed their team
        assertEquals(invisibleTeamString, cardBlue.getVisibleTeam());
    }

    @Test
    void testIsVisibleTeam() {
        // Fresh cards should not be revealed yet
        assertFalse(cardBlue.isVisibleTeam());

        // Revealed cards should return true
        cardRed.makeVisibleTeam();
        assertTrue(cardRed.isVisibleTeam());
    }

    @Test
    void testGetIndexSetIndex() {
        // Default index is 0
        assertEquals(0, cardRed.getIndex());

        // Now we change it
        int myIndex = 999;
        cardRed.setIndex(999);
        assertEquals(myIndex, cardRed.getIndex());
    }

    @Test
    void testToJson() {
        JSONObject redJSON = new JSONObject();

        redJSON = cardRed.toJson();

        assertEquals(word, redJSON.get("word"));
        assertEquals(redTeamString, redJSON.get("team"));
        assertEquals(invisibleTeamString, redJSON.get("visibleTeam"));
        assertEquals(0, redJSON.get("index"));
    }

    @Test
    void testGetCardColorRed() {
        Color clr = new Color(247, 138,114);
        assertEquals(clr, cardRed.getCardColor());
    }

    @Test
    void testGetCardColorBlue() {
        Color clr = new Color(146, 184, 240);
        assertEquals(clr, cardBlue.getCardColor());
    }

    @Test
    void testGetCardColorNeutral() {
        Color clr = new Color(184, 172, 160);
        Card neutralCard = new Card("Card", NEUTRAL,"?", 5);
        assertEquals(clr, neutralCard.getCardColor());
    }

    @Test
    void testGetCardColorDefault() {
        Color clr = new Color(54, 54, 54);
        Card card = new Card("Card", ASSASSIN,"?", 5);
        assertEquals(clr, card.getCardColor());
    }
}
