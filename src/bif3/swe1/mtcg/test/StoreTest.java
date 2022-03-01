package bif3.swe1.mtcg.test;

import bif3.swe1.mtcg.classes.*;
import bif3.swe1.mtcg.enums.CardType;
import bif3.swe1.mtcg.enums.Speciality;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static bif3.swe1.mtcg.Main.getUserList;
import static bif3.swe1.mtcg.Main.initializeAllUsers;
import static org.junit.jupiter.api.Assertions.*;

class StoreTest {

    @Test
    void initializeStoreTest() throws SQLException {
        initializeAllUsers();
        Store.initializeStore();
        assertNotNull(Store.getCardList());
        assertNotNull(Store.getTradeOffers());
    }

    @Test
    void createTradeOfferTest() throws SQLException {
        initializeAllUsers();
        Store.initializeStore();
        User player1 = getUserList().get(0);
        Spell spell1 = (Spell) player1.getStack().get(0).getCard();
        Store.createTradeOffer(player1, spell1, Speciality.DWARF,60);
        ArrayList<TradeOffer> tl = Store.getTradeOffers();
        assertNotNull(tl);
        for(TradeOffer t : tl)
        {
            System.out.println(t.getCard().getName());
        }
    }

    @Test
    void tradeTest() throws SQLException {
        initializeAllUsers();
        User player1 = getUserList().get(0);
        User player2 = getUserList().get(1);
        Spell spell1 = (Spell) player1.getStack().get(0).getCard();
        Store.createTradeOffer(player1, spell1, Speciality.DWARF,60);
        Store.initializeStore();
        ArrayList<TradeOffer> tradeList = Store.getTradeOffers(spell1);
        TradeOffer t1 = tradeList.get(0);
        System.out.println(t1.getCard().getName() + " " + t1.getRequiredCardType() + " " + t1.getRequiredSpeciality() + " " + t1.getMinDamage());
        Monster mon1 = (Monster) player2.getStack().get(0).getCard();

        assertTrue(Store.trade(player2,mon1,t1));
    }


    @Test
    void retractTradeOffer() throws SQLException {
        initializeAllUsers();
        Store.initializeStore();
        ArrayList<TradeOffer> tradeList = Store.getTradeOffers();
        TradeOffer t1 = tradeList.get(0);
        assertTrue(Store.getTradeOffers().contains(t1));
        assertTrue(Store.retractTradeOffer(t1));
        assertFalse(Store.getTradeOffers().contains(t1));
    }
}