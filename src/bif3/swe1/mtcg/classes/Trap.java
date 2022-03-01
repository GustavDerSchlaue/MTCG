package bif3.swe1.mtcg.classes;

import bif3.swe1.mtcg.enums.CardType;
import bif3.swe1.mtcg.enums.Element;
import bif3.swe1.mtcg.interfaces.Card;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class Trap implements Card {
    private String name;
    private CardType cardType;
    private Integer damage;
    private Element element;

    public Trap(String name, Integer damage, Element element) {
        this.name = name;
        this.cardType = CardType.TRAP;
        this.damage = damage;
        this.element = element;
    }

    @Override
    public boolean equals(Card card)
    {
        return (this.name.equals(card.getName()) && this.cardType == card.getCardType() && this.element == card.getElement() && Objects.equals(this.damage, card.getDamage()));
    }


    @Override
    public Integer getCardId() throws SQLException {
        Store.initializeStore();
        ArrayList<Card> cards = Store.getCardList();
        int i = 1;
        for (Card c : cards) {
            if (this.equals(c)) return i;
            i++;
        }
        return 0;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public CardType getCardType() { return this.cardType; }

    @Override
    public void setDamage(Integer dmg) {
        this.damage = dmg;
    }

    @Override
    public Integer getDamage() {
        return this.damage;
    }

    @Override
    public void setElement(Element elem) {
        this.element = elem;
    }

    @Override
    public Element getElement() {
        return this.element;
    }
}
