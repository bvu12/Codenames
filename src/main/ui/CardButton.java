package ui;

import model.Card;

import javax.swing.*;
import java.awt.*;

public class CardButton extends JButton {
    private Card card;

    public CardButton(Card card) {
        super(card.getWord());
        this.card = card;
    }

    // EFFECTS: Returns the card associated with this JButton
    public Card getCard() {
        return card;
    }
}
