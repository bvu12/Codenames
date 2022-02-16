package ui;

import model.Event;
import model.*;
import org.json.JSONObject;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Integer.*;
import static model.Board.*;

public class CodenamesGUI {

    // Frame sizes
    protected static int FRAME_WIDTH;
    private static int FRAME_HEIGHT;
    protected static int CARD_BRDR;
    protected static int CARD_HGAP;
    protected static int CARD_VGAP;
    protected static int CTRLS_BRDR;
    protected static int CTRLS_HGAP;

    // Font sizes
    protected static int FONT_SCORE;
    protected static int FONT_TEAM_INFO;
    protected static int FONT_CONSOLE;
    protected static int FONT_CARDS;

    // Main JFrame
    protected static JFrame frame;
    private final JPanel mainPanel;

    // Team and score panels
    protected static TeamScorePanel teamScorePanel;

    // Hint and console panels that output text to the user
    protected static ConsolePanel consolePanel;

    // Card panels that hold the 25 cards in play
    private ArrayList<CardPanel> cardPanels;

    // Panels that hold the action buttons for the user
    private ActionPanel actionPanel;


    // Data persistence
    private static final String JSON_STORE = "./data/codenames.json";
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    // Game objects
    private Board gameBoard;
    protected static Spymaster redSpymaster;
    protected static Spymaster blueSpymaster;
    protected static Operative redOperative;
    protected static Operative blueOperative;

    // Event log
    protected static EventLog eventLog;

    public CodenamesGUI() {
        // Initialize the screen size
        initializeScreenSize();

        // Initialize data persistence objects
        initializeDataPersistence();

        // If user does not want to load from save
        if (loadGameOrNewGame() == JOptionPane.NO_OPTION) {
            initializeGame(getRandomStartingPlayer());
        } else {
            loadGameState();
        }

        // Initialize the EventLog
        eventLog = EventLog.getInstance();
        logCards();

        // Initialize JFrame
        frame = new JFrame();
        frame.setTitle("Codenames");

        // Initialize main panel that holds smaller panels inside
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0, 1));
        frame.add(mainPanel);

        // TOP panel
        setupTeamAndScorePanel();
        setupConsolePanel();

        // CARDS panel
        setupCardPanel();

        // ACTIONS panel
        setupActionPanel();

        // Basic frame operations
        frameCloseBehaviour();
        frameVisibleBehaviour();
    }

    // EFFECTS: Updates the height and width variables according the users' screen size
    private void initializeScreenSize() {
        // Frame size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        FRAME_WIDTH = (int) (screenSize.width * 0.7);
        FRAME_HEIGHT =  min((int) (FRAME_WIDTH * 0.8), (int) (screenSize.height * 0.8));

        // Font
        FONT_SCORE = FRAME_WIDTH / 25;
        FONT_TEAM_INFO = FONT_SCORE * 2 / 3;
        FONT_CONSOLE = FONT_SCORE * 2 / 5;
        FONT_CARDS = FONT_SCORE / 2;

        // Frame variables
        CARD_BRDR = FRAME_WIDTH / 60;
        CARD_HGAP = FRAME_WIDTH / 60;
        CARD_VGAP = FRAME_WIDTH / 60;
        CTRLS_BRDR = FRAME_WIDTH / 50;
        CTRLS_HGAP = FRAME_WIDTH / 15;

    }

    // SOURCE: https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html
    // EFFECTS: Returns 0 if user asks to load from save, 1 otherwise
    private int loadGameOrNewGame() {
        //default icon, custom title

        return JOptionPane.showConfirmDialog(
                frame,
                "Would you like to load your game from a save file?",
                "Load game?",
                JOptionPane.YES_NO_OPTION);
    }

    // MODIFIES: this
    // EFFECTS: Create both the team and score labels
    private void setupTeamAndScorePanel() {
        // Create top panel
        teamScorePanel = new TeamScorePanel(this);
        mainPanel.add(teamScorePanel);
    }

    // MODIFIES: this
    // EFFECTS: Create both the hint and console labels
    private void setupConsolePanel() {
        // CONSOLE
        consolePanel = new ConsolePanel(this);
        mainPanel.add(consolePanel);
    }

    // MODIFIES: this
    // EFFECTS: Creates the five panels that make-up the 25 cards to be displayed
    private void setupCardPanel() {
        cardPanels = new ArrayList<>();

        // Create the cards and add to the panel
        for (int i = 0; i < 5; i++) {
            cardPanels.add(new CardPanel(this));
            mainPanel.add(cardPanels.get(i));
        }

        // On initialization, deactivate the cards
        revealKey("DEACTIVATE");
    }

    // MODIFIES: this
    // EFFECTS: creates the buttons associated with both Spymaster and Operative actions
    //          Spymaster - Reveal key, save game, set hint
    //          Operative - End turn
    private void setupActionPanel() {
        actionPanel = new ActionPanel(this);
        mainPanel.add(actionPanel);
    }

    // MODIFIES: this
    // EFFECTS: Creates custom close behaviour so that the event log is printed to the console and then closed
    // SOURCE: https://stackoverflow.com/questions/15778813/how-to-perform-a-task-before-a-window-frame-is-closed
    private void frameCloseBehaviour() {
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // The window is closing
                printEventLog();
                frame.dispose();
            }
        });

    }

    // MODIFIES:this
    // EFFECTS: Makes the JFrame visible, not resizeable and centre on the screen
    private void frameVisibleBehaviour() {
        frame.pack();
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setVisible(true);
        frame.setResizable(false);
        centreWindow();
    }

    // SOURCE: https://stackoverflow.com/questions/144892/how-to-center-a-window-in-java
    // MODIFIES: this
    // EFFECTS: centres the created JFrame on the screen
    private void centreWindow() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }

    // MODIFIES: this
    // EFFECTS: Turns off UI elements and thanks the user for playing
    protected void thanksForPlaying() {
        JOptionPane.showMessageDialog(frame,
                "See ya!",
                "THE END",
                JOptionPane.PLAIN_MESSAGE);

        displayPanelImage(mainPanel);
    }

    // MODIFIES: this
    // EFFECTS: removes all components from the main panel and replaces it with a static image
    private void displayPanelImage(JPanel jp) {
        removeComponents(jp);
        displayImage(jp);
    }

    // SOURCE: https://stackoverflow.com/questions/38349445/how-to-delete-all-components-in-a-jpanel-dynamically/38350395
    // MODIFIES: jp
    // EFFECTS: removes all the JPanel's components
    private void removeComponents(JPanel jp) {
        for (Component component : jp.getComponents()) {
            jp.remove(component);
        }
        jp.revalidate();
        jp.repaint();
    }

    // MODIFIES: this
    // EFFECTS: displays an image onto jp
    private void displayImage(JPanel jp) {
        // SOURCE: https://stackoverflow.com/questions/299495/how-to-add-an-image-to-a-jpanel
        //         https://stackoverflow.com/questions/36395762/how-can-i-display-an-image-in-a-jpanel
        //         https://stackoverflow.com/questions/672916/how-to-get-image-height-and-width-using-java
        // IMAGE SOURCE: https://soundcloud.com/thelightbird/end-credits-thanks-for-playing
        BufferedImage image;
        ImageIcon imageIcon;
        int imageWidth;
        int imageHeight;

        // Get the image
        try {
            image = ImageIO.read(new File(".\\data\\thanks-for-playing.jpg"));

            // Resize image
            imageWidth = image.getWidth() * 7 / 4;
            imageHeight = image.getHeight() * 7 / 4;
            imageIcon = new ImageIcon(image.getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH));

            // Place onto icon
            JLabel jl = new JLabel();
            jl.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
            jl.setIcon(imageIcon);
            jl.setHorizontalAlignment(JLabel.CENTER);

            // Add to jpanel
            jp.add(jl);
            jp.setBackground(Color.BLACK);

            // Print event log
            printEventLog();
        } catch (IOException ex) {
            System.out.println("Image not found!");
        }

    }

    // REQUIRES: action is one of "REVEAL", "CONCEAL" or "DEACTIVATE"
    // MODIFIES: this
    // EFFECTS: If action is "REVEAL", reveal the colour of each card
    //          Else if action is "CONCEAL", reset the colour of each that not yet visible
    //          Else deactivate the card
    protected void revealKey(String action) {
        for (CardPanel cp : cardPanels) {
            cp.revealPanel(action);
        }
    }

    // MODIFIES: this
    // EFFECTS: Sets the label to blank
    protected void setLabelBlank(JLabel jl) {
        jl.setText(addHtmlTags(""));
    }

    // MODIFIES: this
    // EFFECTS: initializes the game board, spymasters and operatives
    private void initializeGame(String startingPlayer) {
        initializeGameBoard(startingPlayer);

        // Initialize game objects
        gameBoard.setCurrentTeam(startingPlayer);
        redSpymaster = new Spymaster(RED);
        blueSpymaster = new Spymaster(BLUE);
        redOperative = new Operative(RED);
        blueOperative = new Operative(BLUE);
    }

    // MODIFIES: this
    // EFFECTS: randomly initializes the game board
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


    // MODIFIES: this
    // EFFECTS: changes which team goes next, if !supressPrint parameter then print to console which team is coming next
    protected void nextTeam(boolean suppressPrint) {
        revealKey("DEACTIVATE");

        if (gameBoard.getCurrentTeam().equals(RED)) {
            gameBoard.setCurrentTeam(BLUE);
            teamScorePanel.getTeamLabel().setText(BLUE);
        } else {
            gameBoard.setCurrentTeam(RED);
            teamScorePanel.getTeamLabel().setText(RED);
        }

        updatePlayer();      // Go to next player
        teamScorePanel.setTeamLabelText();      // Update the label
        teamScorePanel.setTeamLabelColour();    // Update the label's colour

        if (!suppressPrint) {
            consolePanel.getHintLabel().setText(addHtmlTags("Switching to the "
                    + gameBoard.getCurrentTeam() + " team's turn!"));
        }

    }

    // MODIFIES: this
    // EFFECTS: If the current player is a SPYMASTER, change to OPERATIVE and vice-versa
    protected void updatePlayer() {
        if (gameBoard.getCurrentPlayer().equals("SPYMASTER")) {
            gameBoard.setCurrentPlayer("OPERATIVE");
            actionPanel.updatePanelOperative();
        } else {
            gameBoard.setCurrentPlayer("SPYMASTER");
            actionPanel.updatePanelSpymaster();
        }
    }

    // EFFECTS: get the Spymaster object whose turn it currently is
    protected Operative selectOperative() {
        if (gameBoard.getCurrentTeam().equals(RED)) {
            return redOperative;
        } else {
            return blueOperative;
        }
    }

    // EFFECTS: get the Spymaster object whose turn it currently is
    protected Spymaster selectSpymaster() {
        if (gameBoard.getCurrentTeam().equals(RED)) {
            return redSpymaster;
        } else {
            return blueSpymaster;
        }
    }

    // MODIFIES: this
    // EFFECTS: Prints the winner to the console and sets gameContinue to false
    protected void gameWonMessage() {
        // Display who has won on the console
        setLabelBlank(consolePanel.getHintLabel());
        String wonMessage = "The " + gameBoard.getCurrentTeam()
                + " team has WON by revealing all their agents!";
        consolePanel.getConsoleLabel().setText(addHtmlTags(wonMessage));

        // Log this event
        Event gameWon = new Event(wonMessage);
        eventLog.logEvent(gameWon);

        // Show thanks for playing message
        thanksForPlaying();
    }

    // EFFECTS: Print the number of guesses remaining and returns it
    //          Note that per the game rules, we can always guess 1 additional time than the guesses provided
    protected int guessesRemaining() {
        Spymaster selected = selectSpymaster();

        return selected.getGuesses() + 1;
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

    // EFFECTS: Returns a string with html tags appended (to center within a JLabel)
    protected String addHtmlTags(String s) {
        return "<html>" + s + "</html>";
    }

    // ==== PHASE 2: Data persistence ====

    // MODIFIES: this
    // EFFECTS: initializes JSON objects to read and write
    private void initializeDataPersistence() {
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
    }

    // MODIFIES: this, "codenames.json"
    // EFFECTS: saves the game state to file
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    protected void saveGameState() {
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
            redOperativeObj = jsonWriter.write(redOperative);
            blueOperativeObj = jsonWriter.write(blueOperative);

            // Merge into once JSON file
            mergedObject = jsonWriter.getMergedObject(gameBoardObj, redSpymasterObj, blueSpymasterObj,
                    redOperativeObj, blueOperativeObj);
            jsonWriter.write(mergedObject);

            // Clean-up and exit
            jsonWriter.close();
            consolePanel.getConsoleLabel().setText("Saved game state to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(frame,
                    "Unable to write to file: " + JSON_STORE,
                    "FILE NOT FOUND",
                    JOptionPane.WARNING_MESSAGE);
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
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame,
                    "Unable to read from file: " + JSON_STORE,
                    "FILE NOT FOUND",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // EFFECTS: iterates over the event log and prints all events
    private void printEventLog() {
        for (Event next : eventLog) {
            System.out.println(next.toString() + "\n");
        }
    }

    // MODIFIES: this
    // EFFECTS: Logs the cards that have been read into the game
    private void logCards() {
        String quantifier = "";
        for (Card card:gameBoard.getBoard()) {
            // Card is overturned
            if (card.isVisibleTeam()) {
                quantifier = "A visible";
            } else {
                quantifier = "An invisible";
            }
            eventLog.logEvent(new Event(quantifier + " " + card.getTeam()
                    + " card '" + card.getWord() + "' has been added to the game board!"));
        }
    }

    // EFFECTS: Returns the Board associated with this game
    protected Board getGameBoard() {
        return gameBoard;
    }

}
