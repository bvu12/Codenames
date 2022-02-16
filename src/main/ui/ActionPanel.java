package ui;

import model.Spymaster;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static ui.CodenamesGUI.*;

// This panel holds the buttons for the user to interact with the game (reveal cards, set hint, end-turn, etc.)
// This panel is mounted on the main panel
public class ActionPanel extends JPanel {

    private final GridLayout ctrlsGridLayout = new GridLayout(1, 3);
    private JButton revealKeyButton;
    private JButton saveButton;
    private JButton actionButton;

    private CodenamesGUI ui;


    public ActionPanel(CodenamesGUI ui) {
        this.ui = ui;

        // Create panel
        setBorder(BorderFactory.createEmptyBorder(CTRLS_BRDR, CTRLS_BRDR, CTRLS_BRDR, CTRLS_BRDR));
        setLayout(ctrlsGridLayout);
        ctrlsGridLayout.setHgap(CTRLS_HGAP);

        // Create JButtons
        createPanelButtons();

        // Create action listeners for the buttons
        createActionListeners();

        // Add buttons to panel and then panel to the main panel
        addToPanel();
    }

    // MODIFIES: this
    // EFFECTS: turn on the button's available to the operative
    public void updatePanelOperative() {
        revealKeyButton.setVisible(false); // Operative's do not have access to reveal key
        saveButton.setVisible(false);   // Operative's can't save the game
        actionButton.setText("End turn");
    }

    // MODIFIES: this
    // EFFECTS: turn on the button's available to the spymaster
    public void updatePanelSpymaster() {
        revealKeyButton.setVisible(true);
        saveButton.setVisible(true);
        actionButton.setText("Set hint");
    }

    // MODIFIES: this
    // EFFECTS: Creates the three action buttons
    private void createPanelButtons() {
        // Create JButtons
        revealKeyButton = new JButton("Reveal key");
        saveButton = new JButton("Save game");
        actionButton = new JButton("Set hint");
        actionButton.setBackground(new Color(131, 175, 84));
        actionButton.setOpaque(true);
    }

    // MODIFIES: this
    // EFFECTS: Implements the action listener for each button
    private void createActionListeners() {
        revealKeyButtonActionListener();
        saveButtonActionListener();
        actionButtonActionListener();
    }

    // EFFECTS: Creates an action listener for the Spymaster's reveal button
    private void revealKeyButtonActionListener() {
        revealKeyButton.addActionListener(e -> ui.revealKey("REVEAL"));
    }

    // EFFECTS: Create an action listener for the Spymaster's save button
    private void saveButtonActionListener() {
        saveButton.addActionListener(e -> ui.saveGameState());
    }

    // MODIFIES: this
    // EFFECTS: Creates the action listener for the bottom-right action button
    private void actionButtonActionListener() {
        actionButton.addActionListener(e -> {
            // Spymaster functionality
            if (teamScorePanel.getTeamLabel().getText().contains("SPYMASTER")) {
                // Ask the user for input
                String hintContext;
                hintContext = "<html>Give a hint to your operatives!<br><br>"
                        + "Specify your one-word clue and # of guesses with a single / between,"
                        + " for example:<br><em>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Clue / 3</em></html>";
                String hint = JOptionPane.showInputDialog(hintContext);

                // Tell user if the hint is invalid
                if (!validHint(hint, ui.selectSpymaster())) {
                    JOptionPane.showMessageDialog(frame,
                            "Your hint is invalid. Try again!",
                            "INVALID HINT",
                            JOptionPane.WARNING_MESSAGE);
                } else { // Valid hint provided, switch to Operative's turn
                    ui.updatePlayer();
                    teamScorePanel.setTeamLabelText();
                    ui.revealKey("CONCEAL");
                }
            } else { // Operative's action is to end their turn
                ui.nextTeam(false);
                ui.setLabelBlank(consolePanel.getConsoleLabel());
            }
        });
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

    // EFFECTS: Returns a regex matcher object (matching digits) for a given string
    private Matcher getDigits(String string) {
        String digits = "[0-9]+";

        Pattern pattern = Pattern.compile(digits);

        return pattern.matcher(string);
    }

    // MODIFIES: this
    // EFFECTS: sets the current Spymaster's hint and # of guesses
    private void setValidHint(Spymaster spymaster, String clue, int numGuesses) {
        spymaster.setHint(clue);
        spymaster.setGuesses(numGuesses);

        String setText;
        setText = "Your hint is: " + spymaster.getHint() + ". You have "
                + ui.guessesRemaining() + " guesses remaining!";
        consolePanel.getHintLabel().setText(ui.addHtmlTags(setText));
    }

    // MODIFIES: this
    // EFFECTS: mounts the buttons to the panel
    private void addToPanel() {
        add(revealKeyButton);
        add(saveButton);
        add(actionButton);
    }

}
