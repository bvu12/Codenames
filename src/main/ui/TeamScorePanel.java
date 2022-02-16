package ui;

import javax.swing.*;
import java.awt.*;

import static model.Board.RED;
import static ui.CodenamesGUI.*;

public class TeamScorePanel extends JPanel {

    private JLabel teamLabel;
    private JLabel scoreLabel;
    private JLabel spacerLabel;

    private CodenamesGUI ui;

    public TeamScorePanel(CodenamesGUI ui) {
        this.ui = ui;

        // Create top panel
        setBorder(BorderFactory.createEmptyBorder(CTRLS_BRDR / 2,
                CTRLS_BRDR,
                CTRLS_BRDR / 2,
                CTRLS_BRDR));

        GridLayout topGridLayout = new GridLayout(1, 3);
        setLayout(topGridLayout);
        topGridLayout.setHgap(100);

        // Setup labels
        setupTeamLabel();
        setupScoreLabel();
        setupSpacerLabel();

        // Add the labels to the panel and the panel to the main panel
        add(teamLabel);
        add(scoreLabel);
        add(spacerLabel);
    }

    public JLabel getTeamLabel() {
        return teamLabel;
    }

    public JLabel getScoreLabel() {
        return scoreLabel;
    }

    // MODIFIES: this
    // EFFECTS: Changes the team label to the appropriate team's color
    public void setTeamLabelColour() {
        String currentTeam = ui.getGameBoard().getCurrentTeam();

        if (currentTeam.equals(RED)) {
            teamLabel.setForeground(Color.RED);
        } else {
            teamLabel.setForeground(Color.BLUE);
        }
    }

    // MODIFIES: this
    // EFFECTS: Updates the label indicating which team/player's turn it is
    public void setTeamLabelText() {
        teamLabel.setText(ui.addHtmlTags(ui.getGameBoard().getCurrentTeam()
                + " " + ui.getGameBoard().getCurrentPlayer()));
    }

    private void setupTeamLabel() {
        // Label displays which team is it
        teamLabel = new JLabel(ui.addHtmlTags(ui.getGameBoard().getCurrentTeam()
                + " " + ui.getGameBoard().getCurrentPlayer()), SwingConstants.CENTER);
        teamLabel.setFont(new Font("Calibri", Font.PLAIN, FONT_TEAM_INFO));
        setTeamLabelColour();
    }

    private void setupScoreLabel() {
        // Label displays what is the score
        // SOURCE: https://stackoverflow.com/questions/6635730/how-do-i-put-html-in-a-jlabel-in-java
        String scoreLabelText = ui.getGameBoard().getScoreText();

        scoreLabel = new JLabel(scoreLabelText, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Calibri", Font.PLAIN, FONT_SCORE));
    }

    private void setupSpacerLabel() {
        // Blank label for styling purposes
        spacerLabel = new JLabel("", SwingConstants.CENTER);
    }



}
