package bif3.swe1.mtcg.test;

import bif3.swe1.mtcg.classes.Spell;
import bif3.swe1.mtcg.classes.StackEntry;
import bif3.swe1.mtcg.classes.Store;
import bif3.swe1.mtcg.classes.User;
import bif3.swe1.mtcg.enums.Element;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static bif3.swe1.mtcg.Main.getUserList;
import static bif3.swe1.mtcg.Main.initializeAllUsers;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void initializeUser() throws SQLException {
        User player1 = new User("maxmusterman420","420blazeit");
        assertEquals(player1.getId(),1);
    }

    @Test
    void getWinLossRatio() {
        User player1 = new User(0,"maxmusterman420","420blazeit");
        player1.wonGame();
        player1.wonGame();
        player1.lostGame();
        assertEquals(player1.getWinLossRatio(),2.0);
    }

    @Test
    void wonGame() throws SQLException {
        User player1 = new User("maxmusterman420","420blazeit");
        int games_won, games_played, elo;
        games_won = player1.getGames_won();
        games_played = player1.getGames_played();
        elo = player1.getElo();
        player1.wonGame();
        assertEquals(player1.getGames_won(),games_won+1);
        assertEquals(player1.getGames_played(),games_played+1);
        assertEquals(player1.getElo(),elo+3);
    }

    @Test
    void lostGame() throws SQLException {
        User player1 = new User("maxmusterman420","420blazeit");
        int games_lost, games_played, elo;
        games_lost = player1.getGames_lost();
        games_played = player1.getGames_played();
        elo = player1.getElo();
        player1.lostGame();
        assertEquals(player1.getGames_lost(),games_lost+1);
        assertEquals(player1.getGames_played(),games_played+1);
        assertEquals(player1.getElo(),elo-5);
    }

    @Test
    void removeFirstFreeInstanceFromStack() throws SQLException {
        initializeAllUsers();
        Store.initializeStore();
        User player1 = getUserList().get(0);
        Spell spell1 = (Spell)Store.getCardList().get(3);
        StackEntry entry1 = new StackEntry(spell1,true);
        player1.addEntryToStack(entry1);
        assertFalse(player1.removeFirstFreeInstanceFromStack(spell1));
        StackEntry entry2 = new StackEntry(spell1);
        player1.addEntryToStack(entry2);
        assertTrue(player1.removeFirstFreeInstanceFromStack(spell1));
    }

    @Test
    void checkIfCardIsTradable() throws SQLException {
        User player1 = new User("maxmusterman420","420blazeit");
        Spell spell1 = new Spell("Fire Bolt", 25, Element.FIRE);
        StackEntry entry1 = new StackEntry(spell1,true);
        player1.addEntryToStack(entry1);
        assertFalse(player1.checkIfCardIsTradable(spell1));
        StackEntry entry2 = new StackEntry(spell1);
        player1.addEntryToStack(entry2);
        assertTrue(player1.checkIfCardIsTradable(spell1));
    }

    @Test
    void recycleCard() {
        User player1 = User.registerNewUser("gamer3","gottem1234");
        Spell spell1 = (Spell)Store.getCardList().get(0);
        assertEquals(player1.getCoins(),20);
        player1.addCardToStack(spell1);
        player1.recycleCard(spell1);
        assertEquals(player1.getCoins(),21);
        player1.deleteUser();
    }

    @Test
    void buyCardPack() throws SQLException {
        Store.initializeStore();
        User player1 = User.registerNewUser("gamer4","gottem1234");
        Spell spell1 = (Spell)Store.getCardList().get(0);
        player1.addCardToStack(spell1);
        assertEquals(20,player1.getCoins());
        assertEquals(player1.getStack().size(),1);
        player1.buyCardPack();
        assertEquals(player1.getCoins(),15);
        assertEquals(player1.getStack().size(),6);
        player1.deleteUser();
    }

    @Test
    void manageDeck() throws SQLException {
        initializeAllUsers();
        User player1 = getUserList().get(1);

        assertFalse(player1.getStack().isEmpty());
        assertTrue(player1.getDeck().isEmpty());

        assertTrue(player1.addCardToDeck(0));
        assertFalse(player1.getDeck().isEmpty());

        assertFalse(player1.addCardToDeck(0));

        assertTrue(player1.removeCardFromDeck(0));
        assertTrue(player1.getDeck().isEmpty());
    }

    @Test
    void viewProfile() throws SQLException {
        initializeAllUsers();
        User player1 = getUserList().get(0);

        player1.viewProfile();
    }

    @Test
    void viewScoreBoard() throws SQLException {
        initializeAllUsers();
        User player1 = getUserList().get(1);
        User player2 = getUserList().get(2);
        player1.viewScoreBoard();
        player1.wonGame();
        player2.lostGame();
        player1.viewScoreBoard();
    }


    @Test
    void registerNewUserTest() throws SQLException {
        assertNull(User.registerNewUser("maxmusterman420","test123"));

        User newUser = User.registerNewUser("1337gamer2","gottem1234");
        initializeUser();
        ArrayList<User> ul = getUserList();
        assertEquals(newUser, ul.get(ul.size()-1));
        assertNotNull(User.getUserByName("1337gamer2"));
        assertNotNull(newUser);
        newUser.deleteUser();
        assertNull(User.getUserByName("1337gamer2"));
    }
}