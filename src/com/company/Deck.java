
package com.company;

import java.util.*;

public class Deck {
    private String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    private String[] suits = {"c", "d", "h", "s"};

    ArrayList<Card> deck = new ArrayList<>();

    public Deck() {
        int value = 1;
        for (int i = 0; i < ranks.length; i++) {
            for (int j = 0; j < suits.length; j++) {
                String cardName = ranks[i] + suits[j];
                Card card = new Card(cardName, value);
                deck.add(card);
                value++;
            }
        }
    }

    public Card dealCard() {
        return deck.remove(0);
    }
}
