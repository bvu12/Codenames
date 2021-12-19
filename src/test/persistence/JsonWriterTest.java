package persistence;

import model.Board;
import model.Operative;
import model.Spymaster;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static model.Board.*;
import static model.Board.ASSASSIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class JsonWriterTest {

    JSONObject mergedObject;
    JSONObject gameBoardObj;
    JSONObject redSpymasterObj;
    JSONObject blueSpymasterObj;
    JSONObject redOperativeObj;
    JSONObject blueOperativeObj;

    Board gameBoard;
    Spymaster redSpymaster;
    Spymaster blueSpymaster;
    Operative redOperative;
    Operative blueOperative;

    JsonWriter writer;

    @Test
    void testWriterInvalidFile() {
        try {
            writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterEmptyGamestate() {
        // Fresh objects
        gameBoard = new Board(RED);
        redSpymaster = new Spymaster(RED);
        blueSpymaster = new Spymaster(BLUE);
        redOperative = new Operative(RED);
        blueOperative = new Operative(BLUE);

        try {
            // Open writer object
            writer = new JsonWriter("./data/testWriterEmptyGamestate.json");
            writer.open();

            // Create JSON objects to be merged
            gameBoardObj = writer.write(gameBoard);
            redSpymasterObj = writer.write(redSpymaster);
            blueSpymasterObj = writer.write(blueSpymaster);
            redOperativeObj =  writer.write(redOperative);
            blueOperativeObj = writer.write(blueOperative);


            // Merge into one JSON file
            mergedObject = writer.getMergedObject(gameBoardObj,redSpymasterObj,blueSpymasterObj,
                    redOperativeObj,blueOperativeObj);
            writer.write(mergedObject);

            // Board writes 6 rows
            // Two Spymasters write 2x3 rows
            // Two Operatives write 2x2 rows
            assertEquals(16,mergedObject.length());

            // Clean-up
            writer.close();


            JsonReader reader = new JsonReader("./data/testWriterEmptyGamestate.json");
            reader.read();
            Board readGameBoard = reader.readBoard();
            Spymaster readRedSpymaster = reader.readSpymaster(RED);
            Spymaster readBlueSpymaster = reader.readSpymaster(BLUE);
            Operative readRedOperative = reader.readOperative(RED);
            Operative readBlueOperative = reader.readOperative(BLUE);

            // Fresh board
            assertEquals(RED, readGameBoard.getStartingTeam());
            assertEquals(0, readGameBoard.getBoard().size());

            // Fresh Spymasters
            assertEquals(RED, readRedSpymaster.getTeamName());
            assertEquals(BLUE, readBlueSpymaster.getTeamName());
            assertEquals(0,readBlueSpymaster.getGuesses());
            assertEquals(0,readRedSpymaster.getGuesses());


            // Fresh Operatives
            assertEquals(RED, readRedOperative.getTeamName());
            assertEquals(BLUE, readBlueOperative.getTeamName());
            assertEquals(0, readRedOperative.getTeamScore());
            assertEquals(0, readBlueOperative.getTeamScore());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralGamestate() {
        // Initialize objects
        int guesses = 5;
        String hint  = "A HINT!";

        // Board
        gameBoard = new Board(RED);
        gameBoard.initializeGameDictionary();
        gameBoard.addCards(RED);
        gameBoard.addCards(BLUE);
        gameBoard.addCards(NEUTRAL);
        gameBoard.addCards(ASSASSIN);
        gameBoard.shuffle();
        gameBoard.setBoardIndices();
        gameBoard.setCurrentTeam(RED);

        // Spymasters
        redSpymaster = new Spymaster(RED);
        redSpymaster.setGuesses(guesses);
        redSpymaster.setHint(hint);
        blueSpymaster = new Spymaster(BLUE);

        // Operatives
        redOperative = new Operative(RED);
        redOperative.incrementScore();
        redOperative.incrementScore();
        blueOperative = new Operative(BLUE);



        try {
            // Open writer object
            writer = new JsonWriter("./data/testWriterGeneralGamestate.json");
            writer.open();

            // Create JSON objects to be merged
            gameBoardObj = writer.write(gameBoard);
            redSpymasterObj = writer.write(redSpymaster);
            blueSpymasterObj = writer.write(blueSpymaster);
            redOperativeObj =  writer.write(redOperative);
            blueOperativeObj = writer.write(blueOperative);


            // Merge into one JSON file
            mergedObject = writer.getMergedObject(gameBoardObj,redSpymasterObj,blueSpymasterObj,
                    redOperativeObj,blueOperativeObj);
            writer.write(mergedObject);

            // Board writes 6 rows
            // Two Spymasters write 2x3 rows
            // Two Operatives write 2x2 rows
            assertEquals(16,mergedObject.length());

            // Clean-up
            writer.close();


            JsonReader reader = new JsonReader("./data/testWriterGeneralGamestate.json");
            reader.read();
            Board readGameBoard = reader.readBoard();
            Spymaster readRedSpymaster = reader.readSpymaster(RED);
            Spymaster readBlueSpymaster = reader.readSpymaster(BLUE);
            Operative readRedOperative = reader.readOperative(RED);
            Operative readBlueOperative = reader.readOperative(BLUE);

            // Initialized board with 25 cards
            assertEquals(RED, readGameBoard.getStartingTeam());
            assertEquals(25, readGameBoard.getBoard().size());

            // Initialized Spymasters
            assertEquals(RED, readRedSpymaster.getTeamName());
            assertEquals(BLUE, readBlueSpymaster.getTeamName());
            assertEquals(guesses,readRedSpymaster.getGuesses());
            assertEquals(0,readBlueSpymaster.getGuesses());
            assertEquals(hint,readRedSpymaster.getHint());
            assertEquals("",readBlueSpymaster.getHint());



            // Initialized Operatives
            assertEquals(RED, readRedOperative.getTeamName());
            assertEquals(BLUE, readBlueOperative.getTeamName());
            assertEquals(2, readRedOperative.getTeamScore());
            assertEquals(0, readBlueOperative.getTeamScore());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }


}
