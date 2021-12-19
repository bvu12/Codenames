package model;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static model.Board.RED;
import static model.Board.BLUE;

import static org.junit.jupiter.api.Assertions.*;

public class OperativeTest {
    private Operative redOperative;
    private Operative blueOperative;
    private String redTeamString = RED;
    private String blueTeamString = BLUE;

    @BeforeEach
    void setup() {
        redOperative = new Operative(redTeamString);
        blueOperative = new Operative(blueTeamString);
    }

    @Test
    void testConstructorForNewGame() {
        assertEquals(redTeamString,redOperative.getTeamName());
        assertEquals(blueTeamString,blueOperative.getTeamName());

        assertEquals(0, redOperative.getTeamScore());
        assertEquals(0, blueOperative.getTeamScore());
    }

    @Test
    void testConstructorForLoadedGame() {
        Operative redOperativeLoaded;
        Operative blueOperativeLoaded;
        int redScore = 5;
        int blueScore = 2;

        redOperativeLoaded = new Operative(redTeamString,redScore);
        blueOperativeLoaded = new Operative(blueTeamString,blueScore);

        assertEquals(redTeamString,redOperativeLoaded.getTeamName());
        assertEquals(blueTeamString,blueOperativeLoaded.getTeamName());

        assertEquals(redScore,redOperativeLoaded.getTeamScore());
        assertEquals(blueScore,blueOperativeLoaded.getTeamScore());
    }

    @Test
    void testIncrementScore() {
        redOperative.incrementScore();
        redOperative.incrementScore();

        assertEquals(2, redOperative.getTeamScore());
        assertEquals(0, blueOperative.getTeamScore());
    }

    @Test
    void testToJson() {
        JSONObject redJSON = new JSONObject();
        JSONObject blueJSON = new JSONObject();

        redJSON = redOperative.toJson();
        blueJSON = blueOperative.toJson();

        assertEquals(redTeamString,redJSON.get("redOperativeTeamName"));
        assertEquals(0,redJSON.get("redOperativeScore"));

        assertEquals(blueTeamString,blueJSON.get("blueOperativeTeamName"));
        assertEquals(0,blueJSON.get("blueOperativeScore"));
    }
}
