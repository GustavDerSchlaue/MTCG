package bif3.swe1.mtcg.classes;

import bif3.swe1.mtcg.interfaces.Card;

public class StackEntry {
    private Card card;
    private boolean isInDeck;
    private Integer tradeOfferId;



    public StackEntry(Card card) {
        this.card = card;
        this.isInDeck = false;
        this.tradeOfferId = null;
    }

    public StackEntry(Card card, boolean isInDeck) {
        this.card = card;
        this.isInDeck = isInDeck;
        this.tradeOfferId = null;
    }

    public StackEntry(Card card, boolean isInDeck, Integer tradeOfferId) {
        this.card = card;
        this.isInDeck = isInDeck;
        this.tradeOfferId = tradeOfferId;
    }

    public String toString()
    {
        String entry;
        if((tradeOfferId == null || tradeOfferId == 0) && !isInDeck)
        {
            entry = "[" + this.card.toString() + ", In Deck: " + this.isInDeck + ", Tradable]";
        }
        else if(tradeOfferId == null || tradeOfferId == 0)
        {
            entry = "[" + this.card.toString() + ", In Deck: " + this.isInDeck + ", Not Tradable]";
        } else
        {
            entry = "[" + this.card.toString() + ", In Deck: " + this.isInDeck + ", Trade Offer: " + this.tradeOfferId + "]";
        }

        return entry;
    }
    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public boolean isInDeck() {
        return isInDeck;
    }

    public void setInDeck(boolean inDeck) {
        isInDeck = inDeck;
    }

    public Integer getTradeOfferId() {
        return tradeOfferId;
    }

    public void setTradeOfferId(Integer tradeOfferId) {
        this.tradeOfferId = tradeOfferId;
    }
}
