package ui;

import model.Board;
import model.Card;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

import java.awt.*;

import static ui.CodenamesGUI.*;


public class CardPanel extends JPanel {
    private static int cardIndex = 0;
    private static final int CARD_COLS = 5;
    private final GridLayout cardGridLayout = new GridLayout(1, CARD_COLS);
    private CodenamesGUI ui;

    public CardPanel(CodenamesGUI ui, Board gameBoard) {
        this.ui = ui;

        // Set-up the lay-out (1 row, 5 cards), borders and spacing
        // Call the method that creates buttons, associates it with a game card, and places it on the panel
        setLayout(cardGridLayout);
        setBorder(BorderFactory.createEmptyBorder(CARD_BRDR, CARD_BRDR, CARD_BRDR, CARD_BRDR));
        cardGridLayout.setHgap(CARD_HGAP);
        cardGridLayout.setVgap(CARD_VGAP);

        createCardButtons(gameBoard);
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

            ui.guess(btn);

        });
    }

}
