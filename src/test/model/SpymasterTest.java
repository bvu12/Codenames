package model;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static model.Board.RED;
import static model.Board.BLUE;
import static org.junit.jupiter.api.Assertions.*;

public class SpymasterTest {
    private Spymaster redSpymaster;
    private Spymaster blueSpymaster;
    private String redTeamString = RED;
    private String blueTeamString = BLUE;
    private int redGuesses = 5;
    private int blueGuesses = 1;

    @BeforeEach
    void setup() {
        redSpymaster = new Spymaster(redTeamString);
        blueSpymaster = new Spymaster(blueTeamString);
    }

    @Test
    void testConstructorForNewGame() {
        assertEquals(redTeamString, redSpymaster.getTeamName());
        assertEquals(blueTeamString, blueSpymaster.getTeamName());

        assertEquals("", redSpymaster.getHint());
        assertEquals("", blueSpymaster.getHint());

        assertEquals(0, redSpymaster.getGuesses());
        assertEquals(0, blueSpymaster.getGuesses());
    }

    @Test
    void testConstructorForLoadedGame() {
        Spymaster redSpymasterLoaded;
        Spymaster blueSpymasterLoaded;
        String aHint = "This is a hint.";
        int numGuesses = 99;

        redSpymasterLoaded = new Spymaster(RED,aHint,numGuesses);
        blueSpymasterLoaded = new Spymaster(BLUE,aHint,numGuesses);

        assertEquals(RED,redSpymasterLoaded.getTeamName());
        assertEquals(aHint,redSpymasterLoaded.getHint());
        assertEquals(numGuesses,redSpymasterLoaded.getGuesses());

        assertEquals(BLUE,blueSpymasterLoaded.getTeamName());
        assertEquals(aHint,blueSpymasterLoaded.getHint());
        assertEquals(numGuesses,blueSpymasterLoaded.getGuesses());

    }

    @Test
    void testSetHint() {
        String redHint = "This is my hint.";
        String blueHint = "This is a different hint.";

        redSpymaster.setHint(redHint);
        blueSpymaster.setHint(blueHint);

        assertEquals(redHint, redSpymaster.getHint());
        assertEquals(blueHint, blueSpymaster.getHint());
    }

    @Test
    void testSetGuesses() {
        redSpymaster.setGuesses(redGuesses);
        blueSpymaster.setGuesses(blueGuesses);

        assertEquals(redGuesses, redSpymaster.getGuesses());
        assertEquals(blueGuesses, blueSpymaster.getGuesses());
    }

    @Test
    void testDecrementGuesses() {
        redSpymaster.setGuesses(redGuesses);
        redSpymaster.decrementGuesses();
        assertEquals(redGuesses - 1, redSpymaster.getGuesses());
        redSpymaster.decrementGuesses();
        assertEquals(redGuesses - 2, redSpymaster.getGuesses());
    }

    @Test
    void testToJson() {
        JSONObject redJSON = new JSONObject();
        JSONObject blueJSON = new JSONObject();

        redJSON = redSpymaster.toJson();
        blueJSON = blueSpymaster.toJson();

        assertEquals(redTeamString,redJSON.get("redSpymasterTeamName"));
        assertEquals("",redJSON.get("redSpymasterHint"));
        assertEquals(0,redJSON.get("redSpymasterGuesses"));

        assertEquals(blueTeamString,blueJSON.get("blueSpymasterTeamName"));
        assertEquals("",blueJSON.get("blueSpymasterHint"));
        assertEquals(0,blueJSON.get("blueSpymasterGuesses"));
    }
}
