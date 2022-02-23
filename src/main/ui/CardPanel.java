package ui;

import model.*;
import model.Event;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

import java.awt.*;

import static model.Board.*;
import static ui.CodenamesGUI.*;


public class CardPanel extends JPanel {
    private static int cardIndex = 0;           // Counter from 0 - 24 (to associate the game's cards to a button
    private static final int CARD_COLS = 5;     // Number of cards in per panel
    private final GridLayout cardGridLayout = new GridLayout(1, CARD_COLS);

    private CodenamesGUI ui;                    // To gain access to the guess method

    public CardPanel(CodenamesGUI ui) {
        this.ui = ui;

        // Set-up the lay-out (1 row, 5 cards), borders and spacing
        // Call the method that creates buttons, associates it with a game card, and places it on the panel
        setLayout(cardGridLayout);
        setBorder(BorderFactory.createEmptyBorder(CARD_BRDR, CARD_BRDR, CARD_BRDR, CARD_BRDR));
        cardGridLayout.setHgap(CARD_HGAP);
        cardGridLayout.setVgap(CARD_VGAP);

        createCardButtons(ui.getGameBoard());
    }


    // REQUIRES: action is one of "REVEAL", "CONCEAL" or "DEACTIVATE"
    // MODIFIES: this
    // EFFECTS: If action is "REVEAL", reveal the colour of each card
    //          Else if action is "CONCEAL", reset the colour of each that not yet visible
    //          Else deactivate the card
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    public void revealPanel(String action) {
        // SOURCE: https://stackoverflow.com/questions/18704904/swing-using-getcomponent-to-update-all-jbuttons/18705604
        // Loop through all components of jp
        for (Component component : this.getComponents()) {
            // If it is a CardButton (i.e. a card in the game)
            if (component instanceof JButton) {
                CardButton btn = (CardButton) component;
                Card card = btn.getCard();
                // If we want to reveal cards
                if (action.equals("REVEAL")) {
                    String setText;
                    setText = "Cards with a BLACK border are not yet revealed!";
                    setText += " Provide a hint to your operatives to proceed!";
                    consolePanel.getConsoleLabel().setText(ui.addHtmlTags(setText));

                    // If the card is NOT yet in play, add a border for enhanced visibility
                    if (!card.isVisibleTeam()) {
                        setButtonBorder(btn, THICK_BORDER);
                    }

                    // Reveal the correct colour of each card
                    btn.setBackground(card.getCardColor());

                    // Make sure the Spymaster can't access the card in the revealed key state
                    btn.setEnabled(false);
                } else if (action.equals("CONCEAL")) { // If we want to conceal the cards
                    consolePanel.getConsoleLabel().setText(ui.addHtmlTags("Press on any card to reveal it!"));

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

    // MODIFIES: this
    // EFFECTS: Creates 5 JButtons that act as the game's cards for this particular panel
    private void createCardButtons(Board gameBoard) {
        UIManager.put("Button.disabledText", new ColorUIResource(Color.BLACK));
        for (int i = 0; i < CARD_COLS; i++) {
            Card card = gameBoard.getBoard().get(cardIndex); // 0-based index
            cardIndex++;

            // JButton
            // SOURCE: https://docs.oracle.com/javase/tutorial/uiswing/layout/grid.html
            CardButton btn = new CardButton(card);
            btn.setFont(new Font("Arial", Font.BOLD, FONT_CARDS));
            btn.setEnabled(false);

            // Create action listener's associated with each card
            cardButtonActionListeners(btn);

            // Place on the panel
            add(btn);
        }
    }

    // MODIFIES: btn
    // EFFECTS: Adds an action listener that performs an action when the button is selected
    private void cardButtonActionListeners(CardButton btn) {
        // Once a card is pressed, deactivate it and progress the game appropriately
        btn.addActionListener(e -> {
            Card card = btn.getCard();
            btn.setBackground(card.getCardColor());
            btn.setOpaque(true);
            btn.setEnabled(false);

            guess(btn);

        });
    }


    // MODIFIES: this
    // EFFECTS:  allows the operative to guess and changes the game-state according to their guess
    private void guess(CardButton btn) {
        Card card = btn.getCard();
        card.makeVisibleTeam();

        // Get the card associated with the guesses
        String selectedTeam = card.getTeam();

        // Get the current team
        String currentTeam = ui.getGameBoard().getCurrentTeam();

        // Log an event
        model.Event event = new model.Event("The " + currentTeam + " team selected a "
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
        String assassin = "The " + ui.getGameBoard().getCurrentTeam() + " team has selected the assassin! ";
        ui.nextTeam(true); // Change the team to show that the OTHER team has won.
        assassin += "\nThe " + ui.getGameBoard().getCurrentTeam() + " team wins!";
        consolePanel.getConsoleLabel().setText(ui.addHtmlTags(assassin));

        // Log event
        model.Event assassinPlayed = new Event(assassin);
        eventLog.logEvent(assassinPlayed);

        // Display thanks for playing message
        ui.thanksForPlaying();
    }

    // MODIFIES: this
    // EFFECTS: Write to the console that you've selected a neutral card, switches team and returns false
    private void guessNeutral(String team) {
        printSelectedCard(team);
        teamScorePanel.getScoreLabel().setText(ui.getGameBoard().getScoreText());
        ui.nextTeam(false);
    }


    // MODIFIES: this
    // EFFECTS: increments the score for the current team, check if this is sufficient to win (and end the game)
    //          otherwise, decrement the number of available guesses
    private void guessCorrect(String team) {
        Operative operative;
        Spymaster spymaster;


        printSelectedCard(team);
        operative = ui.selectOperative();
        operative.incrementScore(); // Increment score
        teamScorePanel.getScoreLabel().setText(ui.getGameBoard().getScoreText());

        // Check if the current team has won, if yes - immediately exit
        if (ui.getGameBoard().checkIfGameWon()) {
            ui.gameWonMessage();
        }

        // Reduce the number of guesses for this team
        spymaster = ui.selectSpymaster();
        spymaster.decrementGuesses();

        String setText;
        setText = "Your hint is: " + spymaster.getHint() + ". You have "
                + ui.guessesRemaining() + " guesses remaining!";
        consolePanel.getHintLabel().setText(ui.addHtmlTags(setText));

        if (ui.guessesRemaining() > 0) {
            // DO NOTHING
        } else { // Stop looping for the current team and go to the next team
            ui.nextTeam(false);
        }
    }

    // MODIFIES: this
    // EFFECTS: If you guessed the other team's card, they get a point - immediately check if they have won
    //          If not, go to their turn
    private void guessWrong(String team) {
        Operative selectedOperative;
        printSelectedCard(team);

        ui.nextTeam(false);
        selectedOperative = ui.selectOperative();
        selectedOperative.incrementScore();
        teamScorePanel.getScoreLabel().setText(ui.getGameBoard().getScoreText());

        // Check if the game is won from this action
        if (ui.getGameBoard().checkIfGameWon()) {
            ui.gameWonMessage();
        }
    }

    // MODIFIES: this
    // EFFECTS: Prints which team's card has just been selected
    private void printSelectedCard(String team) {
        consolePanel.getConsoleLabel().setText(ui.addHtmlTags("You've selected a " + team + " card!"));
    }

    // MODIFIES: jb
    // EFFECTS: Sets the border of jb to a thickness of thickness
    private void setButtonBorder(JButton jb, int thickness) {
        jb.setBorder(BorderFactory.createLineBorder(Color.BLACK, thickness));
    }
}
