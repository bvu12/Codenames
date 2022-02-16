package ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

import static ui.CodenamesGUI.*;

public class ConsolePanel extends JPanel {

    private JLabel hintLabel;
    private JLabel consoleLabel;

    private CodenamesGUI ui;

    public ConsolePanel(CodenamesGUI ui) {
        this.ui = ui;

        Border consoleBorder = BorderFactory.createEmptyBorder(CTRLS_BRDR / 2, CTRLS_BRDR, CTRLS_BRDR / 2, CTRLS_BRDR);
        setBorder(consoleBorder);
        setLayout(new GridLayout(2, 1));
        setPreferredSize(new Dimension(FRAME_WIDTH, 50));

        // Set up labels
        setupHintLabel();
        setupConsoleLabel();

        // Add labels to the panel
        add(hintLabel);
        add(consoleLabel);
    }

    public JLabel getHintLabel() {
        return hintLabel;
    }

    public JLabel getConsoleLabel() {
        return consoleLabel;
    }

    // MODIFIES: this
    // EFFECTS: Creates a label for this panel
    private void setupHintLabel() {
        // Label displays the hint to the user
        hintLabel = new JLabel();
        hintLabel.setFont(new Font("Calibri",Font.PLAIN, FONT_CONSOLE));
        hintLabel.setText(hintForOperatives());
    }

    // MODIFIES: this
    // EFFECTS: Creates another label for this panewl
    private void setupConsoleLabel() {
        // Label displays game information to the user
        consoleLabel = new JLabel();
        consoleLabel.setFont(new Font("Calibri", Font.BOLD, FONT_CONSOLE));
        consoleLabel.setVerticalAlignment(JLabel.CENTER);
        consoleLabel.setOpaque(true);
        consoleLabel.setBackground(Color.WHITE);
        ui.setLabelBlank(consoleLabel);
    }

    // EFFECTS: Returns a string indicating that the user needs to set a hint
    private String hintForOperatives() {
        return ui.addHtmlTags("Set a hint for your operatives!");
    }

}
