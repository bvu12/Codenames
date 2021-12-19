package ui;

import model.Board;
import model.Card;
import model.Operative;
import model.Spymaster;
import org.json.JSONObject;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static model.Board.*;


// Structure for this console application is inspired by the UBC: CPSC 210 Software Construction TellerApp
// Structure for the persistence functionality is inspired by the UBC: CPSC 210 Software Construction WorkRoomApp

public class CodenamesConsole {
    private static final String JSON_STORE = "./data/codenames.json";
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;
    private Scanner input;

    private Board gameBoard;
    private boolean gameContinue;
    private Spymaster redSpymaster;
    private Spymaster blueSpymaster;
    private Operative redOperative;
    private Operative blueOperative;

    // Get the cards per row (and total number of cards)
    int totalCards = Board.NUM_CARDS;
    double cardsPerRow = Math.sqrt(totalCards);
    private static final int CARD_MAX_LENGTH = 15;

    // EFFECTS: runs the Codenames application
    public CodenamesConsole() throws FileNotFoundException {
        input = new Scanner(System.in);
        gameContinue = true;

        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
        runCodenames();
    }

    // MODIFIES: this
    // EFFECTS: processes user input
    private void runCodenames() {
        String startingPlayer;
        input = new Scanner(System.in);
        input.useDelimiter("\n");


        // Initialize a board depending on which team starts first
        startingPlayer = getStartingPlayer();

        // If either RED or BLUE is provided then initialize a fresh game
        if (startingPlayer.equals(RED) || startingPlayer.equals(BLUE)) {
            init(startingPlayer);
        } else { // This is when the player opts to load a game state
            // DO NOTHING
        }

        // Play the game with the initialized board
        playGame();
    }

    // MODIFIES: this
    // EFFECTS: prompts the user to start as RED or BLUE (or randomly chooses)
    private String getStartingPlayer() {
        boolean keepGoing = true;
        String command;

        while (keepGoing) {
            startingPlayerMenu();
            command = input.next().toLowerCase(Locale.ROOT);

            switch (command) {
                case "r":
                    return RED;
                case "b":
                    return BLUE;
                case "c":
                    return getRandomStartingPlayer();
                case "l":
                    loadGameState();
                    return "";
                case "q":
                    keepGoing = false;
                    exitGame();
            }

            System.out.println("Invalid input...");
        }
        return "";
    }

    // EFFECTS: Prints the starting player menu to the console
    private void startingPlayerMenu() {
        System.out.println("\nSelect a team to start first:");
        System.out.println("\tr -> Red team starts");
        System.out.println("\tb -> Blue team starts");
        System.out.println("\tc -> Randomly chooses a team to start");
        System.out.println("\tl -> Load from save");
        System.out.println("\tq -> Quit");
    }



    // EFFECTS: Exits the game
    private void exitGame() {
        System.out.println("You've chosen to exit. Goodbye!");
        System.exit(0);
    }

    // MODIFIES: this
    // EFFECTS: initializes the game board, spymasters and operatives
    private void init(String startingPlayer) {
        initializeGameBoard(startingPlayer);


        gameBoard.setCurrentTeam(startingPlayer);

        redSpymaster = new Spymaster(RED);
        blueSpymaster = new Spymaster(BLUE);
        redOperative = new Operative(RED);
        blueOperative = new Operative(BLUE);
    }

    // MODIFIES: this
    // EFFECTS: randomly initializes the gameboard
    private void initializeGameBoard(String startingPlayer) {
        gameBoard = new Board(startingPlayer);
        gameBoard.initializeGameDictionary();
        gameBoard.addCards(RED);
        gameBoard.addCards(BLUE);
        gameBoard.addCards(NEUTRAL);
        gameBoard.addCards(ASSASSIN);
        gameBoard.shuffle();
        gameBoard.setBoardIndices();
    }

    // EFFECTS: Returns a randomly generated team to start the game ("RED" or "BLUE")
    private String getRandomStartingPlayer() {
        int min = 0;
        int max = 1;
        int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);

        if (randomNum == 0) {
            return RED;
        } else {
            return BLUE;
        }
    }

    // MODIFIES: this
    // EFFECTS:  play the game until user presses exit or when a winner is decided
    private void playGame() {

        while (gameContinue) {
            playSpymasterRound();
            playOperativeRound();
        }

        System.out.println("\nThanks for playing!\n");

    }

    // MODIFIES: this
    // EFFECTS: prompts user (as a spymaster) to perform associated actions and end with giving a hint
    private void playSpymasterRound() {
        boolean keepGoing = true;
        String command;

        while (keepGoing) {
            // First go to the spymasters' menu
            spymasterMenu();
            command = input.next();
            command = command.toLowerCase();

            if (command.equals("q")) {
                askToSave();
                keepGoing = false;
                exitGame();
            } else {
                // If an invalid input is returned, keep looping
                // Else, move on
                keepGoing = processSpymasterCommand(command);
            }

        }
    }

    // EFFECTS: Ask the user if they want to save the game state
    private void askToSave() {
        System.out.println("\nDo you want to save before quitting?");
        System.out.println("\ts -> Save game state");
        System.out.println("\tn -> Quit without saving");

        String command;
        command = input.next();
        command = command.toLowerCase();

        if (command.equals("s")) {
            saveGameState();
        } else if (command.equals("n")) {
            // DO NOTHING
        } else {
            System.out.println("Invalid command!");
            askToSave();
        }

    }

    // EFFECTS: displays menu of options for the Spymaster
    private void spymasterMenu() {
        System.out.println("\n[" + gameBoard.getCurrentTeam() + "] Spymasters can:");
        System.out.println("\tk -> Look at key");
        System.out.println("\th -> Give a hint");
        System.out.println("\tq -> Quit");
    }

    // MODIFIES: this
    // EFFECTS: processes user's spymaster command
    private boolean processSpymasterCommand(String command) {
        switch (command) {
            case "k":
                revealBoardAndScore(0);
                return true;
            case "h":
                provideHint();
                return false;
            default:
                System.out.println("Selection not valid...");
                return true;
        }

    }

    // REQUIRES: playerType to be 0 (spymaster) or 1 (operative)
    // EFFECTS: Print the board and score to the console
    private void revealBoardAndScore(int playerType) {
        currentScore();
        revealBoard(playerType);
    }

    // REQUIRES: playerType to be 0 (spymaster) or 1 (operative)
    // EFFECTS: Reveals the board depending on the type of player
    //          - Spymasters see all underlying information
    //          - Operators see the words or played cards only
    private void revealBoard(int playerType) {
        // Holder of individual cards
        Card card;


        // For loop helpers
        int startingIndex;
        String boundarySpacer = " || ";
        String centerSpacer;

        // Display cards
        for (int rows = 0; rows < cardsPerRow; rows++) {

            // Gives the index associated with the start of each row
            startingIndex = (int) (rows * cardsPerRow);

            // First display the top row
            printCardLine(boundarySpacer);
            String actualTeamPrintedLine = "";
            String visibleTeamPrintedLine = "";
            String dictionaryWordPrintedLine = "";
            String cardNumPrintedLine = "";

            // For each row of cards
            for (int cardIndex = startingIndex; cardIndex < startingIndex + cardsPerRow; cardIndex++) {
                // Get the card in the current game associated with "cardIndex"
                card = gameBoard.getBoard().get(cardIndex);

                // Get the spacer associated with this column
                centerSpacer = getCenterSpacer(cardIndex == startingIndex + cardsPerRow - 1);

                // Accumulate the card's data to be printed
                actualTeamPrintedLine = printCard(actualTeamPrintedLine, getActualTeam(playerType, card), centerSpacer);
                dictionaryWordPrintedLine = printCard(dictionaryWordPrintedLine, card.getWord(), centerSpacer);
                visibleTeamPrintedLine = printCard(visibleTeamPrintedLine, card.getVisibleTeam(), centerSpacer);
                cardNumPrintedLine = printCard(cardNumPrintedLine, Integer.toString(card.getIndex()), centerSpacer);

            }

            // Print the card data
            printCardData(actualTeamPrintedLine,dictionaryWordPrintedLine,visibleTeamPrintedLine,cardNumPrintedLine);

        }

        // Print out the bottom boundary
        printCardLine(boundarySpacer);
    }

    // EFFECTS: Returns an accumulation of the actual team's for this row's 5 cards
    private String printCard(String accumulating, String word, String spacer) {
        String retString = "";
        double wordLength = word.length();
        int leftWhitespace = (int) Math.ceil((CARD_MAX_LENGTH - wordLength) / 2);
        int rightWhitespace = (int) Math.floor((CARD_MAX_LENGTH - wordLength) / 2);

        for (int i = 0; i < leftWhitespace; i++) {
            retString = retString.concat(" ");
        }

        retString = retString + word;

        for (int j = 0; j < rightWhitespace; j++) {
            retString = retString.concat(" ");
        }

        return accumulating + retString + spacer;
    }

    // EFFECTS: Prints out the top/bottom part of a card
    private void printCardLine(String spacer) {
        String cardLine = "";

        for (int i = 0; i < CARD_MAX_LENGTH; i++) {
            cardLine = cardLine.concat("-");
        }

        String fullCardLine = "";
        for (int j = 1; j < cardsPerRow; j++) {
            fullCardLine = fullCardLine.concat(cardLine + spacer);
        }

        fullCardLine = fullCardLine + cardLine;
        System.out.println(fullCardLine);

    }

    // REQUIRES: playerType to be 0 (spymaster) or 1 (operative)
    // EFFECTS: Given a player type (0 for spymaster, 1 for operative)
    //              - If spymaster, return card's actual type
    //              - Else (operative), return blank - as operative's do not have access to this information
    private String getActualTeam(int playerType, Card card) {
        if (playerType == 0) {
            return card.getTeam();
        } else {
            return " ";
        }
    }

    // EFFECTS: Prints the full row of card data to the console
    private void printCardData(String actualTeam, String dictionaryWord, String visibleTeam, String cardNum) {
        // !!! Function with four inputs - these four that just prints
        System.out.println(actualTeam);
        System.out.println(dictionaryWord);
        System.out.println(visibleTeam);
        System.out.println(cardNum);
    }

    // EFFECTS: Within the row's that print out the card data, we have to know whether we are in the center
    //          or at a boundary. If we are in the center, we don't want to concatenate a spacer.
    private String getCenterSpacer(Boolean isCenter) {
        if (isCenter) { // Do not pass spacer
            return "";
        } else { // Otherwise, do pass the spacer
            return " || ";
        }
    }


    // EFFECTS: Outputs 50 blank lines to simulate clearing the console
    private void clearConsole() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    // MODIFIES: this
    // EFFECTS: prompts spymaster for a hint and saves it
    private void provideHint() {

        Spymaster selected = selectSpymaster();
        boolean keepGoing = true;
        String command;

        while (keepGoing) {
            System.out.println("\nGive a hint to your operatives!");
            System.out.println("Specify your one-word clue and # of guesses with a single / between, for example:");
            System.out.println("\tClue / 3\n");
            command = input.next();

            if (validHint(command, selected)) {
                keepGoing = false;
            } else {
                System.out.println("Invalid clue...");
            }

        }

    }

    // EFFECTS: get the Spymaster object whose turn it currently is
    private Spymaster selectSpymaster() {
        if (gameBoard.getCurrentTeam().equals(RED)) {
            return redSpymaster;
        } else {
            return blueSpymaster;
        }
    }

    // EFFECTS: Returns true if the provided hint is valid
    //          - One word at most
    //          - A non-zero integer number of guesses provided
    //          - A single "/" character between
    private boolean validHint(String hint, Spymaster selected) {

        // Check if there is a single word/clue given
        int delimiterIndex = hint.indexOf('/'); // Index of the delimiter
        if (delimiterIndex == -1) { // No delimiter found
            return false;
        }
        String cluePortion = removeTrailingSpace(hint.substring(0, delimiterIndex));
        if (!(countChar(cluePortion, ' ') == 0)) { // Multi-word hints have spaces
            return false;
        }

        // Extract the # portion of the hint
        Matcher isDigits = getDigits(hint.substring(delimiterIndex + 1));
        boolean isNumber = isDigits.find();
        int numberPortion;

        if (isNumber) {
            numberPortion = parseInt(isDigits.group(0));

            // Make sure that there are no negative guesses provided
            if (numberPortion < 0) {
                return false;
            }

            setValidHint(selected, cluePortion, numberPortion);
            return true;
        } else {
            return false;
        }
    }

    // MODIFIES: this
    // EFFECTS: sets the current Spymaster's hint and # of guesses
    private void setValidHint(Spymaster spymaster, String clue, int numGuesses) {
        spymaster.setHint(clue);
        spymaster.setGuesses(numGuesses);
    }

    // EFFECTS: returns the count of delimiter within hint
    private int countChar(String string, char delimiter) {
        int delimiterCounter = 0;

        // Count occurrences of the delimiter
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == delimiter) {
                delimiterCounter++;
            }
        }

        return delimiterCounter;
    }

    // EFFECTS: Returns a regex matcher object (matching digits) for a given string
    private Matcher getDigits(String string) {
        String digits = "[0-9]+";

        Pattern pattern = Pattern.compile(digits);

        return pattern.matcher(string);
    }

    // MODIFIES: this
    // EFFECTS: prompts user (as an operative) to perform associated actions (guess, end turn, etc.)
    private void playOperativeRound() {
        clearConsole();

        // Reveal information
        revealBoardAndScore(1); // Reveals the visible portion of the board
        guessesRemaining();      // Reveals how many guesses are remaining for this team

        // Loop functionality
        boolean keepGoing = true;
        String command;

        while (keepGoing) {
            operativeMenu();
            command = input.next();
            command = command.toLowerCase();

            if (command.equals("q")) {
                keepGoing = false;
                exitGame();
            } else {
                // If an invalid input is returned, keep looping
                // Else, move on
                keepGoing = processOperativeCommand(command);
            }

        }

    }

    // EFFECTS: Prints the current score to the console
    private void currentScore() {
        int redScore = redOperative.getTeamScore();
        int blueScore = blueOperative.getTeamScore();

        System.out.println("\n~~~~~~~ SCORE ~~~~~~~");
        System.out.println(" " + RED + " - " + redScore + " // " + BLUE + " - " + blueScore);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~");
    }

    // EFFECTS: Print the number of guesses remaining and returns it
    private int guessesRemaining() {
        Spymaster selected = selectSpymaster();
        int guesses = selected.getGuesses() + 1;
        System.out.println("You have " + guesses + " guesses remaining!");

        return guesses;
    }

    // EFFECTS: displays menu of options for the Operative
    private void operativeMenu() {
        System.out.println("\n[" + gameBoard.getCurrentTeam() + "] Operatives can:");
        System.out.println("\tk -> Look at key");
        System.out.println("\tg -> Guess");
        System.out.println("\th -> Review the hint");
        System.out.println("\te -> End your term");
        System.out.println("\tq -> Quit");
    }

    // MODIFIES: this
    // EFFECTS: processes user's operative command and returns a boolean associated with ending the loop or not
    private boolean processOperativeCommand(String command) {
        switch (command) {
            case "k":
                revealBoardAndScore(1);
                return true;
            case "g":
                boolean guessResult;
                guessResult = guess();

                return guessResult;
            case "h":
                obtainHint();
                return true;  // Do NOT break out of the loop - get context menu again

            case "e":
                nextTeam(false);
                return false; // Break out of the loop - change teams

            default:
                System.out.println("Selection not valid...");
                break;
        }

        return true; // Default is to NOT break out of the loop
    }

    // EFFECTS: print out the current team's hint to the console
    private void obtainHint() {
        Spymaster selected = selectSpymaster();
        System.out.println("Your clue is: " + selected.getHint());
    }

    // EFFECTS: changes which team goes next, given a false parameter print to console which team is coming next
    private void nextTeam(boolean suppressPrint) {

        if (gameBoard.getCurrentTeam().equals(RED)) {
            gameBoard.setCurrentTeam(BLUE);
        } else {
            gameBoard.setCurrentTeam(RED);
        }

        if (!suppressPrint) {
            System.out.println("\nSwitching to the " + gameBoard.getCurrentTeam() + " team's turn!");
        }

    }

    // MODIFIES: this
    // EFFECTS:  allows the operative to guesses and changes the game-state according to their guess
    private boolean guess() {
        int guessIndex = getGuessIndex();

        // Get the card associated with the guesses
        Card card = gameBoard.getBoard().get(guessIndex);
        String selectedTeam = card.getTeam();
        card.makeVisibleTeam();

        // Depending on which card was selected
        if (selectedTeam.equals(ASSASSIN)) {
            return guessAssassin();
        } else if (selectedTeam.equals(NEUTRAL)) {
            return guessNeutral(selectedTeam);
        } else if (selectedTeam.equals(gameBoard.getCurrentTeam())) {
            return guessCorrect(selectedTeam);
        } else { // Selected the opposite team's card
            return guessWrong(selectedTeam);
        }

    }

    // EFFECTS: prompts user to select an index associated with a card
    private int getGuessIndex() {
        boolean keepGoing = true;
        List<Card> cards;
        cards = gameBoard.getBoard();
        Card card;
        int index = 0;

        while (keepGoing) {
            System.out.println("\nSelect a card [1-25]:");
            index = input.nextInt();
            index = index - 1;

            if (index >= 0 && index <= 25 - 1) {
                card = cards.get(index); // Get the card at the provided index (-1 because 0-based indexing)
                if (!card.isVisibleTeam()) { // If this card is NOT yet revealed
                    keepGoing = false;
                } else {
                    System.out.println("This card is already revealed!");
                }
            } else {
                System.out.println("Invalid card index!");
            }

        }
        return index;
    }

    // EFFECTS: Write to the console that the current team loses and returns false
    private boolean guessAssassin() {
        nextTeam(true); // Change the team to show that the OTHER team has won.
        System.out.println("You've selected the assassin!");
        System.out.println("The " + gameBoard.getCurrentTeam() + " team wins!");
        gameContinue = false;

        return false;
    }

    // EFFECTS: Write to the console that you've selected a neutral card, switches team and returns false
    private boolean guessNeutral(String team) {
        printSelectedCard(team);
        nextTeam(false);
        return false;
    }


    // MODIFIES: this
    // EFFECTS: increments the score for the current team, check if this is sufficient to win (and end the game)
    //          otherwise, decrement the number of available guesses
    private boolean guessCorrect(String team) {
        Operative selectedOperative;
        Spymaster selectedSpymaster;


        printSelectedCard(team);
        selectedOperative = selectOperative();
        selectedOperative.incrementScore(); // Increment score

        // Check if the current team has won, if yes - immediately exit
        if (checkIfGameWon()) {
            gameWonMessage();
            return false;
        }

        // Reduce the number of guesses for this team
        selectedSpymaster = selectSpymaster();
        selectedSpymaster.decrementGuesses();

        if (guessesRemaining() > 0) {
            return true; // Keep looping
        } else { // Stop looping for the current team and go to the next team
            nextTeam(false);
            return false;
        }
    }

    // EFFECTS: If you guessed the other team's card, they get a point - immediately check if they have won
    //          If not, go to their turn
    private boolean guessWrong(String team) {
        Operative selectedOperative;
        printSelectedCard(team);

        nextTeam(false);
        selectedOperative = selectOperative();
        selectedOperative.incrementScore();

        // Check if the game is won from this action
        if (checkIfGameWon()) {
            gameWonMessage();
        }

        return false;

    }

    // EFFECTS: Prints which team's card has just been selected
    private void printSelectedCard(String team) {
        System.out.println("\nYou've selected a " + team + " card!");
    }

    // EFFECTS: get the Spymaster object whose turn it currently is
    private Operative selectOperative() {
        if (gameBoard.getCurrentTeam().equals(RED)) {
            return redOperative;
        } else {
            return blueOperative;
        }
    }

    // EFFECTS: Returns true if the current team has no more visible points
    private boolean checkIfGameWon() {

        // Respective team has revealed all their cards
        if (gameBoard.getCurrentTeam().equals(RED)) {
            return gameBoard.getRemainingCards(RED) == 0;
        } else {
            return gameBoard.getRemainingCards(BLUE) == 0;
        }
    }

    // MODIFIES: this
    // EFFECTS: Prints the winner to the console and sets gameContinue to false
    private void gameWonMessage() {
        System.out.println("The " + gameBoard.getCurrentTeam() + " team has WON by revealing all their agents!\n");
        currentScore();
        gameContinue = false;
    }

    // EFFECTS: Removes a trailing space (if it exists)
    private String removeTrailingSpace(String str) {
        int lastCharIndex = str.length() - 1;
        String lastChar = str.substring(lastCharIndex);
        if (lastChar.equals(" ")) {
            return str.substring(0, lastCharIndex);
        } else {
            return str;
        }
    }


    // ==== PHASE 2: Data persistence ====

    // MODIFIES: this, "codenames.json"
    // EFFECTS: saves the game state to file
    private void saveGameState() {
        try {
            // Create fields for each of the respective objects we need to save
            JSONObject mergedObject;
            JSONObject gameBoardObj;
            JSONObject redSpymasterObj;
            JSONObject blueSpymasterObj;
            JSONObject redOperativeObj;
            JSONObject blueOperativeObj;


            // Write fields to jsonWriter objects
            jsonWriter.open();
            gameBoardObj = jsonWriter.write(gameBoard);
            redSpymasterObj = jsonWriter.write(redSpymaster);
            blueSpymasterObj = jsonWriter.write(blueSpymaster);
            redOperativeObj =  jsonWriter.write(redOperative);
            blueOperativeObj = jsonWriter.write(blueOperative);

            // Merge into once JSON file
            mergedObject = jsonWriter.getMergedObject(gameBoardObj,redSpymasterObj,blueSpymasterObj,
                    redOperativeObj,blueOperativeObj);
            jsonWriter.write(mergedObject);

            // Clean-up and exit
            jsonWriter.close();
            System.out.println("Saved game state to " + JSON_STORE);
            exitGame();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_STORE);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads game state from file
    private void loadGameState() {
        try {
            jsonReader.read();
            gameBoard = jsonReader.readBoard();
            redSpymaster = jsonReader.readSpymaster(RED);
            blueSpymaster = jsonReader.readSpymaster(BLUE);
            redOperative = jsonReader.readOperative(RED);
            blueOperative = jsonReader.readOperative(BLUE);
            System.out.println("Loaded game state from " + JSON_STORE);
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_STORE);
        }
    }



}
