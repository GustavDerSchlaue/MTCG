package bif3.swe1.mtcg.classes;

        import bif3.swe1.mtcg.enums.Element;
        import bif3.swe1.mtcg.enums.Speciality;
        import bif3.swe1.mtcg.interfaces.Card;

        import java.sql.*;
        import java.util.ArrayList;
        import java.util.Base64;

        import static bif3.swe1.mtcg.Main.*;

public class User {
    private Integer id;
    private String name;
    private String password;
    private String token;
    private Integer coins;
    private Integer elo;
    private Integer games_played;
    private Integer games_won;
    private Integer games_draw;
    private Integer games_lost;
    private ArrayList<StackEntry> stack;
    private ArrayList<Card> deck;

    public User(String name, String password) throws SQLException {
        this.name = name;
        this.password = password;
        stack = new ArrayList<>();
        deck = new ArrayList<>();
        this.initializeUser();
    }

    public User(Integer id, String name, String password) {
        String usernameColonPassword = (name+":"+password);
        this.id = id;
        this.name = name;
        this.password = password;
        this.token = Base64.getEncoder().encodeToString(usernameColonPassword.getBytes());
        this.coins = 20;
        this.elo = 100;
        this.games_played = 0;
        this.games_won = 0;
        this.games_draw = 0;
        this.games_lost = 0;
        stack = new ArrayList<>();
        deck = new ArrayList<>();
    }

    public User(Integer id, String name, String password, Integer coins, Integer elo, Integer games_played, Integer games_won, Integer games_draw, Integer games_lost, ArrayList<StackEntry> stack, ArrayList<Card> deck) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.coins = coins;
        this.elo = elo;
        this.games_played = games_played;
        this.games_won = games_won;
        this.games_draw = games_draw;
        this.games_lost = games_lost;
        this.stack = stack;
        this.deck = deck;
    }

    public void initializeUser() throws SQLException {
        // method to get data from the database into the variables
        Connection c = connection();
        PreparedStatement ps = c.prepareStatement("SELECT * FROM public.user WHERE username = '"+ this.name + "';");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            this.id = rs.getInt("id");
            this.coins = rs.getInt("coins");
            this.elo = rs.getInt("elo");
            this.games_played = rs.getInt("games_played");
            this.games_won = rs.getInt("games_won");
            this.games_draw = rs.getInt("games_draw");
            this.games_lost = rs.getInt("games_lost");
        }
        rs.close();
        ps.close();

        PreparedStatement psCards = c.prepareStatement("SELECT *\n" +
                "FROM\n" +
                "public.stack\n" +
                "    INNER JOIN public.card ON stack.card_id = card.id\n" +
                "    INNER JOIN public.element_type ON element_id = element_type.id\n" +
                "    FULL OUTER JOIN public.monster_speciality ON speciality_id = monster_speciality.id\n" +
                "    WHERE user_id = "+ this.id + ";");

        ResultSet rsCards = psCards.executeQuery();
        Element elem;
        Speciality spec;
        while (rsCards.next()) {
            elem =  Element.valueOf(rsCards.getString("element"));
            if(rsCards.getInt("card_type_id") == 1)
            {
                Spell s = new Spell(rsCards.getString("name"),rsCards.getInt("damage"),elem);
                this.addEntryToStack(new StackEntry(s,rsCards.getBoolean("is_in_deck"),rsCards.getInt("trade_offer_id")));
                if(rsCards.getBoolean("is_in_deck"))
                    this.deck.add(s);
            } else if(rsCards.getInt("card_type_id") == 3) {
                Trap t = new Trap(rsCards.getString("name"),rsCards.getInt("damage"),elem);
                this.addEntryToStack(new StackEntry(t,rsCards.getBoolean("is_in_deck"),rsCards.getInt("trade_offer_id")));
                if(rsCards.getBoolean("is_in_deck"))
                    this.deck.add(t);
            } else {
                spec = Speciality.valueOf(rsCards.getString("speciality"));
                Monster m = new Monster(rsCards.getString("name"),rsCards.getInt("damage"),elem,spec);
                this.addEntryToStack(new StackEntry(m,rsCards.getBoolean("is_in_deck"),rsCards.getInt("trade_offer_id")));
                if(rsCards.getBoolean("is_in_deck"))
                    this.deck.add(m);
            }
        }
        rsCards.close();
        psCards.close();


        c.close();
        System.out.println("Initialization successful");
    }

    public static User login(String token){
        // Decodes the token
        String credentials = new String(Base64.getDecoder().decode(token));
        String[] usernameAndPassword = credentials.split(":");
        User usr = null;
        try {
            usr = new User(usernameAndPassword[0],usernameAndPassword[1]);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return usr;
    }

    public void updateData() throws SQLException {
        Connection c = connection();
        PreparedStatement ps = c.prepareStatement("SELECT * FROM public.user WHERE username = '"+ this.name + "';");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            this.id = rs.getInt("id");
            this.coins = rs.getInt("coins");
            this.elo = rs.getInt("elo");
            this.games_played = rs.getInt("games_played");
            this.games_won = rs.getInt("games_won");
            this.games_draw = rs.getInt("games_draw");
            this.games_lost = rs.getInt("games_lost");
        }
        rs.close();
        ps.close();

        PreparedStatement psCards = c.prepareStatement("SELECT *\n" +
                "FROM\n" +
                "public.stack\n" +
                "    INNER JOIN public.card ON stack.card_id = card.id\n" +
                "    INNER JOIN public.element_type ON element_id = element_type.id\n" +
                "    FULL OUTER JOIN public.monster_speciality ON speciality_id = monster_speciality.id\n" +
                "    WHERE user_id = "+ this.id + "" +
                "    ORDER BY card_id;");

        ResultSet rsCards = psCards.executeQuery();
        Element elem;
        Speciality spec;
        while (rsCards.next()) {
            elem =  Element.valueOf(rsCards.getString("element"));
            if(rsCards.getInt("card_type_id") == 1)
            {
                Spell s = new Spell(rsCards.getString("name"),rsCards.getInt("damage"),elem);
                this.addEntryToStack(new StackEntry(s,rsCards.getBoolean("is_in_deck"),rsCards.getInt("trade_offer_id")));
                if(rsCards.getBoolean("is_in_deck"))
                    this.deck.add(s);
            } else {
                spec =  Speciality.valueOf(rsCards.getString("speciality"));
                Monster m = new Monster(rsCards.getString("name"),rsCards.getInt("damage"),elem,spec);
                this.addEntryToStack(new StackEntry(m,rsCards.getBoolean("is_in_deck"),rsCards.getInt("trade_offer_id")));
                if(rsCards.getBoolean("is_in_deck"))
                    this.deck.add(m);
            }
        }
        rsCards.close();
        psCards.close();


        c.close();
    }


    public Integer getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getCoins() {
        return coins;
    }

    public ArrayList<StackEntry> getStack() {
        return stack;
    }

    public void addCardToStack(Card card) {
        this.stack.add(new StackEntry(card));
        try {
            Connection c = connection();
            PreparedStatement ps;
            ps = c.prepareStatement("INSERT INTO public.stack VALUES (?,?);");
            ps.setInt(1,this.id);
            ps.setInt(2,card.getCardId());
            ps.executeUpdate();
            c.commit();
            ps.close();

            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addEntryToStack(StackEntry entry) { this.stack.add(entry); }

    public void removeEntryFromStack(StackEntry entry) { this.stack.remove(entry); }

    public ArrayList<Card> getDeck() { return deck; }

    public boolean addCardToDeck(Integer id) {
        StackEntry s = this.stack.get(id);
        if(this.deck.size() >= 4)
        {
            System.out.println("Deck size is limited to 4! Please remove a card first.");
            return false;
        }
        if(!this.checkIfCardIsTradable(s.getCard()))
        {
            System.out.println("This card is currently not available.");
            return false;
        }
        s.setInDeck(true);
        addCardToDeck(s.getCard());
        return true;
    }

    public void addCardToDeck(Card card) {

        this.deck.add(card);

        Connection c = connection();
        PreparedStatement ps;
        try {
            ps = c.prepareStatement("INSERT INTO public.deck VALUES (?,?);");
            ps.setInt(1,this.id);
            ps.setInt(2,card.getCardId());
            ps.executeUpdate();
            c.commit();
            ps.close();

            ps = c.prepareStatement("UPDATE public.stack SET is_in_deck = true\n" +
                    "WHERE CTID IN " +
                    "(SELECT CTID FROM public.stack WHERE user_id = ? AND card_id = ? AND is_in_deck = false AND trade_offer_id IS NULL LIMIT 1);");
            ps.setInt(1,this.id);
            ps.setInt(2,card.getCardId());
            ps.executeUpdate();
            c.commit();
            ps.close();

            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean removeCardFromDeck(Integer id)
    {
        if(this.deck.isEmpty())
            return false;
        if(this.deck.size() < id+1)
            return false;
        removeCardFromDeck(this.deck.get(id));
        return true;
    }

    public void removeCardFromDeck(Card card)
    {
        if(this.deck.isEmpty())
        {
            System.out.println("Deck is already empty!");
            return;
        }
        if(!this.deck.contains(card))
        {
            System.out.println("Deck is does not contain this card!");
            return;
        }
        this.deck.remove(card);

        for(StackEntry s : this.stack)
        {
            if(s.getCard().equals(card) && s.isInDeck()) {
                s.setInDeck(false);
                break;
            }
        }

        Connection c = connection();
        PreparedStatement ps;
        try {
            ps = c.prepareStatement("DELETE FROM public.deck\n" +
                    "WHERE CTID IN (SELECT CTID FROM public.deck WHERE user_id = " + this.id + " AND card_id = " + card.getCardId() + " LIMIT 1);\n");
            ps.executeUpdate();
            c.commit();
            ps.close();

            ps = c.prepareStatement("UPDATE public.stack SET is_in_deck = false\n" +
                    "WHERE CTID IN (SELECT CTID FROM public.stack WHERE user_id = " + this.id + " AND card_id = "
                    + card.getCardId() + " AND is_in_deck = true AND trade_offer_id IS NULL LIMIT 1);");
            ps.executeUpdate();
            c.commit();
            ps.close();

            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Integer getElo() {
        return elo;
    }

    public void setElo(Integer elo) {
        this.elo = elo;
    }

    public Integer getGames_played() {
        return games_played;
    }

    public void setGames_played(Integer games_played) {
        this.games_played = games_played;
    }

    public Integer getGames_won() {
        return games_won;
    }

    public Integer getGames_draw() {
        return games_draw;
    }

    public void setGames_draw(Integer games_draw) {
        this.games_draw = games_draw;
    }

    public Integer getGames_lost() {
        return games_lost;
    }

    public void setStack(ArrayList<StackEntry> stack) {
        this.stack = stack;
    }

    public void setDeck(ArrayList<Card> deck) {
        this.deck = deck;
    }

    public double getWinLossRatio() {
        if(this.games_lost == 0) return 1.0; //cannot divide by zero
        return (double)this.getGames_won() / (double) this.getGames_lost();
    }

    public void wonGame()
    {
        this.games_won += 1;
        this.games_played += 1;
        this.elo += 3;
        Connection c = connection();
        PreparedStatement ps;
        try {
            ps = c.prepareStatement("UPDATE public.user SET games_won = ?, games_played = ?, elo = ? WHERE id = ?;");
            ps.setInt(1,this.games_won);
            ps.setInt(2,this.games_played);
            ps.setInt(3,this.elo);
            ps.setInt(4,this.id);
            int ar = ps.executeUpdate();
            c.commit();
            ps.close();
            //System.out.println(ar + " affected Rows.");

            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void drawGame()
    {
        this.games_draw += 1;
        this.games_played += 1;
        Connection c = connection();
        PreparedStatement ps;
        try {
            ps = c.prepareStatement("UPDATE public.user " +
                    "SET games_draw = " + this.games_draw + ",\n" +
                    "games_played = " + this.games_played + "\n" +
                    "WHERE id = " + this.id + ";\n");
            ps.executeUpdate();
            c.commit();
            ps.close();

            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void lostGame()
    {
        this.games_lost += 1;
        this.games_played += 1;
        this.elo -= 5;
        Connection c = connection();
        PreparedStatement ps;
        try {
            ps = c.prepareStatement("UPDATE public.user " +
                    "SET games_lost = " + this.games_lost + ",\n" +
                    "games_played = " + this.games_played + ",\n" +
                    "elo = " + this.elo + "\n" +
                    "WHERE id = " + this.id + ";\n");
            ps.executeUpdate();
            c.commit();
            ps.close();

            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean removeFirstFreeInstanceFromStack(Card card)
    {
        StackEntry entry;
        for(int i = 0; i < stack.size(); i++)
        {
            entry = stack.get(i);
            if(entry.getCard().equals(card) && (entry.getTradeOfferId() == null || entry.getTradeOfferId() == 0) && !entry.isInDeck()) {
                stack.remove(i);
                Connection c = connection();
                PreparedStatement ps;
                try {
                    ps = c.prepareStatement("DELETE FROM public.stack\n" +
                            "WHERE CTID IN (SELECT CTID FROM public.stack WHERE user_id = " + this.id + " AND card_id = " + card.getCardId() + " AND is_in_deck = false AND trade_offer_id IS NULL LIMIT 1);\n");
                    ps.executeUpdate();
                    c.commit();
                    ps.close();

                    c.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                return true; // to make sure we do not remove all instances of the card in the stack
            }
        }
        return false;
    }

    public boolean checkIfCardIsTradable(Card card)
    {
        for(StackEntry entry : stack)
        {
            if(entry.getCard().equals(card) && (entry.getTradeOfferId() == null || entry.getTradeOfferId() == 0)&& !(entry.isInDeck())) {
                return true;
            }
        }
        return false;
    }


    /**
     /* This allows the user to recycle one of their cards to gain a coin.
     /* In case the user has duplicates, only one instance of the card will be remove from their stack
     /* You are only allowed to recycle cards that you not currently up as a trade offer nor in a deck
     **/
    public void recycleCard(Card card)
    {
        if(removeFirstFreeInstanceFromStack(card)) { coins += 1; }
        else { System.out.println("Card not currently available in your stack."); }

        Connection c = connection();
        PreparedStatement ps;
        try {
            ps = c.prepareStatement("UPDATE public.user SET coins = " + coins + "\n" +
                    "WHERE id = " + this.id + ";\n");
            ps.executeUpdate();
            c.commit();
            ps.close();

            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void buyCardPack()
    {
        if(coins < 5) { System.out.println("Insufficient number of coins. You need at least 5 coins to buy a card pack!"); }
        else {
            coins -= 5;
            try {
                Store.initializeStore();
                ArrayList<Card> pack = Store.getCardPack();

                int[] ids = new int[5];
                int i = 0;
                for(Card card : pack)
                {
                    stack.add(new StackEntry(card));
                    ids[i++] = Store.getCardList().indexOf(card)+1;
                }

                Connection c = connection();
                PreparedStatement ps;
                ps = c.prepareStatement("INSERT INTO public.stack (user_id,card_id)\n" +
                        "VALUES (?,?),(?,?),(?,?),(?,?),(?,?);");
                ps.setInt(1,this.id);
                ps.setInt(2,ids[0]);
                ps.setInt(3,this.id);
                ps.setInt(4,ids[1]);
                ps.setInt(5,this.id);
                ps.setInt(6,ids[2]);
                ps.setInt(7,this.id);
                ps.setInt(8,ids[3]);
                ps.setInt(9,this.id);
                ps.setInt(10,ids[4]);
                ps.executeUpdate();
                c.commit();
                ps.close();

                ps = c.prepareStatement("UPDATE public.user SET coins = ? WHERE id = ?;");
                ps.setInt(1,this.coins);
                ps.setInt(2,this.id);
                ps.executeUpdate();
                c.commit();
                ps.close();

                c.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void viewScoreBoard()
    {
        Connection c = connection();
        PreparedStatement ps;
        int i = 0;
        try {
            ps = c.prepareStatement("SELECT username, elo, games_played, games_won, games_draw, games_lost FROM public.user ORDER BY elo DESC;");
            ResultSet rs = ps.executeQuery();
            System.out.printf("%3s %22s %7s %9s %6s %7s %7s", "Rank", "Username", "ELO", "Played", "Won", "Draw", "Lost");
            System.out.println();
            System.out.println("--------------------------------------------------------------------------");
            while (rs.next()) {
                System.out.format("%3d %23s %7d %7d %7d %7d %7d",
                        ++i, rs.getString("username"), rs.getInt("elo"), rs.getInt("games_played"), rs.getInt("games_won"), rs.getInt("games_draw"), rs.getInt("games_lost"));
                System.out.println();
            }
            System.out.println("--------------------------------------------------------------------------");
            rs.close();
            ps.close();

            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void listPlayers()
    {
        try {
            Connection c = connection();
            PreparedStatement ps;
            ps = c.prepareStatement("SELECT id, username, elo, COUNT(card_id) as deck_size\n" +
                    "FROM public.user\n" +
                    "LEFT OUTER JOIN public.deck ON id = user_id\n" +
                    "GROUP BY id\n" +
                    "ORDER BY id;");
            ResultSet rs = ps.executeQuery();
            System.out.printf("%3s %23s %7s %13s", "ID", "Username", "ELO", "Deck ready");
            System.out.println();
            System.out.println("------------------------------------------------------------");
            while (rs.next()) {
                System.out.format("%3d %23s %7d %11b",
                        rs.getInt("id"), rs.getString("username"), rs.getInt("elo"), rs.getInt("deck_size")==4);
                System.out.println();
            }
            System.out.println("------------------------------------------------------------");
            rs.close();
            ps.close();

            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static boolean checkIfUserExists(String token)
    {
        try {
            initializeAllUsers();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        ArrayList<User> userList = getUserList();
        for(User u : userList)
        {
            if(u.getToken().equals(token))
                return true;
        }
        return false;
    }

    public static User getUserByName(String username)
    {
        try {
            initializeAllUsers();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        ArrayList<User> userList = getUserList();
        for(User u : userList)
        {
            if(u.getName().equals(username))
                return u;
        }
        return null;
    }
    public static User getUserByID(Integer id)
    {
        try {
            initializeAllUsers();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        ArrayList<User> userList = getUserList();
        for(User u : userList)
        {
            if(u.getId().equals(id))
                return u;
        }
        return null;
    }

    public static User registerNewUser(String username, String password)
    {
        if(getUserByName(username) != null)
        {
            System.out.println("Name already taken");
            return null;
        }

        String usernameColonPassword = (username+":"+password);
        String token = Base64.getEncoder().encodeToString(usernameColonPassword.getBytes());

        int id = 0;
        try {

            Connection c = connection();
            PreparedStatement ps = c.prepareStatement("INSERT INTO public.user (username, password, token) VALUES (?,?,?);",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,username);
            ps.setString(2,password);
            ps.setString(3,token);
            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();

            if (generatedKeys.next()) {
                id = (int)generatedKeys.getLong(1);
            }

            c.commit();
            ps.close();

            c.close();

            initializeAllUsers();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return getUserByID(id);
    }

    public boolean changePassword(String newPassword)
    {
        String usernameColonPassword = (this.name+":"+newPassword);
        String token = Base64.getEncoder().encodeToString(usernameColonPassword.getBytes());

        try {
            Connection c = connection();
            PreparedStatement ps = c.prepareStatement("UPDATE public.user SET password = ?, token = ? WHERE id = ?;");
            ps.setString(1,newPassword);
            ps.setString(2,token);
            ps.setInt(3,this.id);
            ps.executeUpdate();

            c.commit();
            ps.close();

            c.close();

            initializeAllUsers();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }

    public void viewProfile()
    {
        System.out.printf("%23s %13s %6s %6s %9s %6s %7s %7s", "Username", "Num of Cards", "Coins", "ELO", "Played", "Won", "Draw", "Lost");
        System.out.println();
        System.out.println("-------------------------------------------------------------------------------------");

        System.out.format("%23s %3d %14d %8d %7d %7d %7d %7d",
                this.name, this.stack.size(), this.coins, this.elo, this.games_played, this.games_won, this.games_draw, this.games_lost);
        System.out.println();
        System.out.println("-------------------------------------------------------------------------------------");
    }

    public void deleteUser()
    {
        try {
            Connection c = connection();
            PreparedStatement ps = c.prepareStatement("DELETE FROM public.user WHERE id = ?;");
            ps.setInt(1,this.id);
            ps.executeUpdate();

            c.commit();
            ps.close();

            c.close();

            initializeAllUsers();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
