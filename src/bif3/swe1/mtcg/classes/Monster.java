package bif3.swe1.mtcg.classes;

import bif3.swe1.mtcg.enums.CardType;
import bif3.swe1.mtcg.interfaces.Card;
import bif3.swe1.mtcg.enums.Element;
import bif3.swe1.mtcg.enums.Speciality;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class Monster implements Card {
    private String name;
    private CardType cardType;
    private Integer damage;
    private Element element;
    private Speciality speciality;

    public Monster(String name, Integer damage, Element element, Speciality spec) {
        this.name = name;
        this.cardType = CardType.MONSTER;
        this.damage = damage;
        this.element = element;
        this.speciality = spec;
    }


    @Override
    public boolean equals(Card card)
    {
        return (this.name.equals(card.getName()) && this.cardType == card.getCardType() && this.element == card.getElement() && Objects.equals(this.damage, card.getDamage()) && this.speciality == ((Monster)card).getSpeciality());
    }

    @Override
    public String toString()
    {
        String card;

        card = "[" + this.name + "," + this.cardType.toString() + "," + this.damage + "," + this.element + "," + this.speciality.toString() +"]";

        return card;
    }

    @Override
    public Integer getCardId() throws SQLException {
        Store.initializeStore();
        ArrayList<Card> cards = Store.getCardList();
        int i = 1;
        for(Card c: cards)
        {
            if(this.equals(c)) return i;
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
    public void setDamage(Integer dmg) { this.damage = dmg; }

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

    public void setSpeciality(Speciality spec) {
        this.speciality = spec;
    }

    public Speciality getSpeciality() {
        return speciality;
    }
}
