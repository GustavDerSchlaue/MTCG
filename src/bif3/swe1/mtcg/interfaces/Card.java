package bif3.swe1.mtcg.interfaces;

import bif3.swe1.mtcg.enums.CardType;
import bif3.swe1.mtcg.enums.Element;

import java.sql.SQLException;

public interface Card {

    Integer getCardId() throws SQLException;

    void setName(String name);

    String getName();

    CardType getCardType();

    void setDamage(Integer dmg);

    Integer getDamage();

    void setElement(Element elem);

    Element getElement();

    boolean equals(Card card);

    String toString();
}
