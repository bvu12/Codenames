package ui;

import model.Event;
import model.*;
import org.json.JSONObject;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Integer.*;
import static model.Board.*;

public class CodenamesGUI {

    // Frame sizes
    private static int FRAME_WIDTH;
    private static int FRAME_HEIGHT;
    private static final int CARD_ROWS = 5;
    protected static int CARD_BRDR;
    protected static int CARD_HGAP;
    protected static int CARD_VGAP;
    protected static int CTRLS_BRDR;
    protected static int CTRLS_HGAP;

    // Font sizes
    private static int FONT_SCORE;
    private static int FONT_TEAM_INFO;
    private static int FONT_CONSOLE;
    protected static int FONT_CARDS;

    // Main JFrame
    protected static JFrame frame;
    private final JPanel mainPanel;

    // Team and score panels
    private JPanel topPanel;
    protected static JLabel teamLabel;
    private JLabel scoreLabel;

    // Hint and console panels that output text to the user
    private JPanel consolePanel;
    protected static JLabel hintLabel;
    protected static JLabel consoleLabel;

    // Card panels that hold the 25 cards in play
    private CardPanel cardPanel1;
    private CardPanel cardPanel2;
    private CardPanel cardPanel3;
    private CardPanel cardPanel4;
    private CardPanel cardPanel5;

    // Panels that hold the action buttons for the user
    private ActionPanel actionPanel;

//    protected static final GridLayout cardGridLayout = new GridLayout(1, CARD_COLS);
//    private final GridLayout ctrlsGridLayout = new GridLayout(1, 3);

    // Data persistence
    private static final String JSON_STORE = "./data/codenames.json";
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    // Game objects
    private Board gameBoard;
    private Spymaster redSpymaster;
    private Spymaster blueSpymaster;
    private Operative redOperative;
    private Operative blueOperative;
    private String hint;

    // Event log
    private EventLog eventLog;

    public CodenamesGUI() {
        // Initialize the screen size
        initializeScreenSize();

        // Initialize data persistence objects
        initializeDataPersistence();

        int reply = loadGameOrNewGame();

        // If user does not want to load from save
        if (reply == JOptionPane.NO_OPTION) {
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
        topPanel = new JPanel();
        topPanel.setBorder(BorderFactory.createEmptyBorder(CTRLS_BRDR / 2,
                CTRLS_BRDR,
                CTRLS_BRDR / 2,
                CTRLS_BRDR));

        GridLayout topGridLayout = new GridLayout(1, 3);
        topPanel.setLayout(topGridLayout);
        topGridLayout.setHgap(100);

        // Label displays which team is it
        teamLabel = new JLabel(addHtmlTags(gameBoard.getCurrentTeam()
                + " " + gameBoard.getCurrentPlayer()), SwingConstants.CENTER);
        teamLabel.setFont(new Font("Calibri", Font.PLAIN, FONT_TEAM_INFO));
        setTeamLabelColour();

        // Label displays what is the score
        // SOURCE: https://stackoverflow.com/questions/6635730/how-do-i-put-html-in-a-jlabel-in-java
        String scoreLabelText = getScoreLabel();
        scoreLabel = new JLabel(scoreLabelText, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Calibri", Font.PLAIN, FONT_SCORE));

        // Blank label for styling purposes
        JLabel spacerLabel = new JLabel("", SwingConstants.CENTER);

        // Add the labels to the panel and the panel to the main panel
        topPanel.add(teamLabel);
        topPanel.add(scoreLabel);
        topPanel.add(spacerLabel);
        mainPanel.add(topPanel);
    }

    // MODIFIES: this
    // EFFECTS: Create both the hint and console labels
    private void setupConsolePanel() {
        // CONSOLE
        consolePanel = new JPanel();
        Border consoleBorder = BorderFactory.createEmptyBorder(CTRLS_BRDR / 2, CTRLS_BRDR, CTRLS_BRDR / 2, CTRLS_BRDR);
        consolePanel.setBorder(consoleBorder);
        consolePanel.setLayout(new GridLayout(2, 1));

        // Label displays the hint to the user
        hintLabel = new JLabel();
        hintLabel.setFont(new Font("Calibri", Font.PLAIN, FONT_CONSOLE));
        hintLabel.setText(hintForOperatives());

        // Label displays game information to the user
        consoleLabel = new JLabel();
        consoleLabel.setFont(new Font("Calibri", Font.BOLD, FONT_CONSOLE));
        consoleLabel.setVerticalAlignment(JLabel.CENTER);
        consoleLabel.setOpaque(true);
        consoleLabel.setBackground(Color.WHITE);
        setLabelBlank(consoleLabel);


        consolePanel.setPreferredSize(new Dimension(FRAME_WIDTH, 50));

        // Add labels to the panel and the panel to the main panel
        consolePanel.add(hintLabel);
        consolePanel.add(consoleLabel);
        mainPanel.add(consolePanel);
    }

    // MODIFIES: this
    // EFFECTS: Creates the five panels that make-up the 25 cards to be displayed
    private void setupCardPanel() {
        // Create the cards and add to the panel
        cardPanel1 = new CardPanel(this);
        cardPanel2 = new CardPanel(this);
        cardPanel3 = new CardPanel(this);
        cardPanel4 = new CardPanel(this);
        cardPanel5 = new CardPanel(this);

        mainPanel.add(cardPanel1);
        mainPanel.add(cardPanel2);
        mainPanel.add(cardPanel3);
        mainPanel.add(cardPanel4);
        mainPanel.add(cardPanel5);

        // On initialization, deactivate the cards
        revealKey("DEACTIVATE");
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
    // EFFECTS: creates the buttons associated with both Spymaster and Operative actions
    //          Spymaster - Reveal key, save game, set hint
    //          Operative - End turn
    private void setupActionPanel() {
        actionPanel = new ActionPanel(this);
        mainPanel.add(actionPanel);
    }

    // REQUIRES: action is one of "REVEAL", "CONCEAL" or "DEACTIVATE"
    // MODIFIES: this
    // EFFECTS: If action is "REVEAL", reveal the colour of each card
    //          Else if action is "CONCEAL", reset the colour of each that not yet visible
    //          Else deactivate the card
    protected void revealKey(String action) {
        revealPanel(cardPanel1, action);
        revealPanel(cardPanel2, action);
        revealPanel(cardPanel3, action);
        revealPanel(cardPanel4, action);
        revealPanel(cardPanel5, action);
    }

    // REQUIRES: action is one of "REVEAL", "CONCEAL" or "DEACTIVATE"
    // MODIFIES: this
    // EFFECTS: If action is "REVEAL", reveal the colour of each card
    //          Else if action is "CONCEAL", reset the colour of each that not yet visible
    //          Else deactivate the card
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private void revealPanel(JPanel jp, String action) {
        // SOURCE: https://stackoverflow.com/questions/18704904/swing-using-getcomponent-to-update-all-jbuttons/18705604
        // Loop through all components of jp
        for (Component component : jp.getComponents()) {
            // If it is a CardButton (i.e. a card in the game)
            if (component instanceof JButton) {
                CardButton btn = (CardButton) component;
                Card card = btn.getCard();
                // If we want to reveal cards
                if (action.equals("REVEAL")) {
                    String setText;
                    setText = "Cards with a BLACK border are not yet revealed!";
                    setText += " Provide a hint to your operatives to proceed!";
                    consoleLabel.setText(addHtmlTags(setText));

                    // If the card is NOT yet in play, add a border for enhanced visibility
                    if (!card.isVisibleTeam()) {
                        setButtonBorder(btn, 7);
                    }

                    // Reveal the correct colour of each card
                    btn.setBackground(card.getCardColor());

                    // Make sure the Spymaster can't access the card in the revealed key state
                    btn.setEnabled(false);
                } else if (action.equals("CONCEAL")) { // If we want to conceal the cards
                    consoleLabel.setText(addHtmlTags("Press on any card to reveal it!"));

                    // Remove borders
                    btn.setBorder(null);

                    // For cards not yet revealed, conceal them
                    if (!card.isVisibleTeam()) {
                        btn.setBackground(new JButton().getBackground());
                        btn.setEnabled(true);
                    }
                } else { // DEACTIVATE
                    setButtonBorder(btn, 1);
                    btn.setEnabled(false);

                    if (card.isVisibleTeam()) {
                        btn.setBackground(card.getCardColor());
                    }

                }
            }
        }
    }

    // MODIFIES: jb
    // EFFECTS: Sets the border of jb to a thickness of thickness
    private void setButtonBorder(JButton jb, int thickness) {
        jb.setBorder(BorderFactory.createLineBorder(Color.BLACK, thickness));
    }


    // MODIFIES: this
    // EFFECTS: Updates the label indicating which team/player's turn it is
    protected void setTeamLabelText() {
        teamLabel.setText(addHtmlTags(gameBoard.getCurrentTeam() + " " + gameBoard.getCurrentPlayer()));
    }

    // MODIFIES: this
    // EFFECTS: Prints which team's card has just been selected
    private void printSelectedCard(String team) {
        consoleLabel.setText(addHtmlTags("You've selected a " + team + " card!"));
    }


    // EFFECTS: Returns the score to be displayed on the JLabel
    private String getScoreLabel() {
        return "<html><FONT COLOR=RED>"
                + gameBoard.getRemainingCards(RED)
                + "</FONT> - <FONT COLOR=BLUE>"
                + gameBoard.getRemainingCards(BLUE)
                + "</FONT></html>";
    }

    // MODIFIES: this
    // EFFECTS: Changes the team label to the appropriate team's color
    private void setTeamLabelColour() {
        String currentTeam = gameBoard.getCurrentTeam();

        if (currentTeam.equals(RED)) {
            teamLabel.setForeground(Color.RED);
        } else {
            teamLabel.setForeground(Color.BLUE);
        }
    }

    // MODIFIES: this
    // EFFECTS: Turns off UI elements and thanks the user for playing
    private void thanksForPlaying() {
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

    // MODIFIES: this
    // EFFECTS: Sets the label to blank
    protected void setLabelBlank(JLabel jl) {
        jl.setText(addHtmlTags(""));
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

    // MODIFIES: this
    // EFFECTS: initializes the game board, spymasters and operatives
    private void initializeGame(String startingPlayer) {
        initializeGameBoard(startingPlayer);


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
    // EFFECTS:  allows the operative to guess and changes the game-state according to their guess
    protected void guess(CardButton btn) {
        Card card = btn.getCard();
        card.makeVisibleTeam();

        // Get the card associated with the guesses
        String selectedTeam = card.getTeam();

        // Get the current team
        String currentTeam = gameBoard.getCurrentTeam();

        // Log an event
        Event event = new Event("The " + currentTeam + " team selected a "
                + selectedTeam + " card (" + card.getWord() + ")!");
        eventLog.logEvent(event);


        // Depending on which card was selected
        if (selectedTeam.equals(ASSASSIN)) {
            guessAssassin();
        } else if (selectedTeam.equals(NEUTRAL)) {
            guessNeutral(selectedTeam);
        } else if (selectedTeam.equals(currentTeam)) {
            guessCorrect(selectedTeam);
        } else { // Selected the opposite team's card
            guessWrong(selectedTeam);
        }

    }

    // MODIFIES: this
    // EFFECTS: Write to the console that the current team loses and returns false
    private void guessAssassin() {
        String assassin = "The " + gameBoard.getCurrentTeam() + " team has selected the assassin! ";
        nextTeam(true); // Change the team to show that the OTHER team has won.
        assassin += "\nThe " + gameBoard.getCurrentTeam() + " team wins!";
        consoleLabel.setText(addHtmlTags(assassin));

        // Log event
        Event assassinPlayed = new Event(assassin);
        eventLog.logEvent(assassinPlayed);

        // Display thanks for playing message
        thanksForPlaying();
    }

    // MODIFIES: this
    // EFFECTS: Write to the console that you've selected a neutral card, switches team and returns false
    private void guessNeutral(String team) {
        printSelectedCard(team);
        nextTeam(false);
    }


    // MODIFIES: this
    // EFFECTS: increments the score for the current team, check if this is sufficient to win (and end the game)
    //          otherwise, decrement the number of available guesses
    private void guessCorrect(String team) {
        Operative operative;
        Spymaster spymaster;


        printSelectedCard(team);
        operative = selectOperative();
        operative.incrementScore(); // Increment score
        scoreLabel.setText(getScoreLabel());

        // Check if the current team has won, if yes - immediately exit
        if (checkIfGameWon()) {
            gameWonMessage();
        }

        // Reduce the number of guesses for this team
        spymaster = selectSpymaster();
        spymaster.decrementGuesses();

        String setText;
        setText = "Your hint is: " + spymaster.getHint() + ". You have " + guessesRemaining() + " guesses remaining!";
        hintLabel.setText(addHtmlTags(setText));

        if (guessesRemaining() > 0) {
            // DO NOTHING
        } else { // Stop looping for the current team and go to the next team
            nextTeam(false);
        }
    }

    // MODIFIES: this
    // EFFECTS: If you guessed the other team's card, they get a point - immediately check if they have won
    //          If not, go to their turn
    private void guessWrong(String team) {
        Operative selectedOperative;
        printSelectedCard(team);

        nextTeam(false);
        selectedOperative = selectOperative();
        selectedOperative.incrementScore();
        scoreLabel.setText(getScoreLabel());

        // Check if the game is won from this action
        if (checkIfGameWon()) {
            gameWonMessage();
        }

    }

    // MODIFIES: this
    // EFFECTS: changes which team goes next, given a false parameter print to console which team is coming next
    protected void nextTeam(boolean suppressPrint) {
//        hintLabel.setText(hintForOperatives());
        revealKey("DEACTIVATE");

        if (gameBoard.getCurrentTeam().equals(RED)) {
            gameBoard.setCurrentTeam(BLUE);
            teamLabel.setText(BLUE);
        } else {
            gameBoard.setCurrentTeam(RED);
            teamLabel.setText(RED);
        }

        updatePlayer();      // Go to next player
        setTeamLabelText();  // Update the label
        setTeamLabelColour();// Update the label's colour

        if (!suppressPrint) {
            hintLabel.setText(addHtmlTags("Switching to the " + gameBoard.getCurrentTeam() + " team's turn!"));
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
    private Operative selectOperative() {
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
        // Display who has won on the console
        setLabelBlank(hintLabel);
        String wonMessage = "The " + gameBoard.getCurrentTeam()
                + " team has WON by revealing all their agents!";
        consoleLabel.setText(addHtmlTags(wonMessage));

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
            consoleLabel.setText("Saved game state to " + JSON_STORE);
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

    // EFFECTS: Returns a string indicating that the user needs to set a hint
    private String hintForOperatives() {
        return addHtmlTags("Set a hint for your operatives!");
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
