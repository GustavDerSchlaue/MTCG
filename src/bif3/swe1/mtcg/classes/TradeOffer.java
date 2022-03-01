package bif3.swe1.mtcg.classes;

import bif3.swe1.mtcg.enums.CardType;
import bif3.swe1.mtcg.enums.Element;
import bif3.swe1.mtcg.enums.Speciality;
import bif3.swe1.mtcg.interfaces.Card;

public class TradeOffer {
    private Integer id;
    private User seller;
    private Card card;
    private CardType requiredCardType;
    private Element requiredElement;
    private Speciality requiredSpeciality;
    private Integer minDamage;

    public TradeOffer(Integer id, User seller, Card card, CardType requiredCardType, Element requiredElement) {
        this.id = id;
        this.seller = seller;
        this.card = card;
        this.requiredCardType = requiredCardType;
        this.requiredElement = requiredElement;
        this.requiredSpeciality = null;
        this.minDamage = null;
    }

    public TradeOffer(Integer id, User seller, Card card, Speciality requiredSpeciality) {
        this.id = id;
        this.seller = seller;
        this.card = card;
        this.requiredCardType = CardType.MONSTER;
        this.requiredElement = null;
        this.requiredSpeciality = requiredSpeciality;
        this.minDamage = null;
    }

    public TradeOffer(Integer id, User seller, Card card, Speciality requiredSpeciality, Integer minDamage) {
        this.id = id;
        this.seller = seller;
        this.card = card;
        this.requiredCardType = CardType.MONSTER;
        this.requiredElement = null;
        this.requiredSpeciality = requiredSpeciality;
        this.minDamage = minDamage;
    }

    public TradeOffer(Integer id, User seller, Card card, CardType requiredCardType, Integer minDamage) {
        this.id = id;
        this.seller = seller;
        this.card = card;
        this.requiredCardType = requiredCardType;
        this.requiredElement = null;
        this.requiredSpeciality = null;
        this.minDamage = minDamage;
    }

    public TradeOffer(Integer id, User seller, Card card,Element requiredElement, Speciality requiredSpeciality, Integer minDamage) {
        this.id = id;
        this.seller = seller;
        this.card = card;
        this.requiredCardType = CardType.MONSTER;
        this.requiredElement = requiredElement;
        this.requiredSpeciality = requiredSpeciality;
        this.minDamage = minDamage;
    }

    public TradeOffer(Integer id, User seller, Card card, CardType requiredCardType, Element requiredElement, Integer minDamage) {
        this.id = id;
        this.seller = seller;
        this.card = card;
        this.requiredCardType = requiredCardType;
        this.requiredElement = requiredElement;
        this.requiredSpeciality = null;
        this.minDamage = minDamage;
    }


    public String toString()
    {
        String offer;
        offer = "[ ID:" + this.id + ", Seller: " + this.seller.getName() + ", Card: " + this.card.toString() + ", Requirements: " + this.requiredCardType.toString();
        if(this.requiredElement != null)
            offer += ", " + this.requiredElement.toString();
        if(this.requiredSpeciality != null)
            offer += ", " + this.requiredSpeciality.toString();
        if(this.minDamage != 0)
            offer += ", MinDamage: " + this.minDamage;

        offer += "]";
        return offer;
    }

    public Integer getId() {
        return id;
    }

    public User getSeller() {
        return seller;
    }

    public Card getCard() {
        return card;
    }

    public CardType getRequiredCardType() {
        return requiredCardType;
    }

    public Element getRequiredElement() {
        return requiredElement;
    }

    public Speciality getRequiredSpeciality() {
        return requiredSpeciality;
    }

    public Integer getMinDamage() {
        return minDamage;
    }

    public boolean checkConditions(Card c) {
        if(this.requiredCardType != c.getCardType()) return false;
        if(this.requiredElement != null && this.requiredElement.equals(c.getElement())) return false;
        if(this.requiredSpeciality != null && this.requiredSpeciality.equals(((Monster)c).getSpeciality())) return false;
        return this.minDamage == null || this.minDamage <= c.getDamage();
    }
}
