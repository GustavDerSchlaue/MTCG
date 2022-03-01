package bif3.swe1.mtcg.classes;

        import bif3.swe1.mtcg.enums.CardType;
        import bif3.swe1.mtcg.enums.Element;
        import bif3.swe1.mtcg.enums.Speciality;
        import bif3.swe1.mtcg.interfaces.Card;

        import java.sql.*;
        import java.util.ArrayList;
        import java.util.Random;

        import static bif3.swe1.mtcg.Main.connection;

public class Store {
    private static ArrayList<Card> cardList;

    private static ArrayList<TradeOffer> tradeOfferList;
    public Store() throws SQLException {
        cardList = new ArrayList<>();
        tradeOfferList = new ArrayList<>();
        initializeStore();
    }

    public static void initializeStore() throws SQLException {
        cardList = new ArrayList<>();
        tradeOfferList = new ArrayList<>();
        Connection c = connection();
        PreparedStatement ps = c.prepareStatement("SELECT * FROM public.card ORDER BY id;");
        ResultSet rs = ps.executeQuery();
        Element elem = null;
        Speciality spec = null;
        CardType type;
        while (rs.next()) {
            elem =  Element.values()[rs.getInt("element_id")-1];
            if(rs.getInt("card_type_id") == 2)
            {
                spec =  Speciality.values()[rs.getInt("speciality_id")-1];
                Monster m = new Monster(rs.getString("name"),rs.getInt("damage"),elem,spec);
                cardList.add(m);
            } else if(rs.getInt("card_type_id") == 3) {
                Trap t = new Trap(rs.getString("name"),rs.getInt("damage"),elem);
                cardList.add(t);
            } else {
                Spell s = new Spell(rs.getString("name"),rs.getInt("damage"),elem);
                cardList.add(s);
            }
        }
        rs.close();
        ps.close();

        ps = c.prepareStatement("SELECT * FROM public.store INNER JOIN public.card ON store.card_id = card.id;");
        rs = ps.executeQuery();
        elem = null;
        spec = null;
        while (rs.next()) {
            if(rs.getInt("requirement_element_id") != 0)
                elem = Element.values()[rs.getInt("requirement_element_id")-1];
            if(rs.getInt("requirement_speciality_id") != 0)
                spec = Speciality.values()[rs.getInt("requirement_speciality_id")-1];
            type = CardType.values()[rs.getInt("requirement_card_type_id")-1];
            TradeOffer t;
            if(spec == null)
                t = new TradeOffer(rs.getInt("id"), User.getUserByID(rs.getInt("seller_id")), cardList.get(rs.getInt("card_id")-1), type, elem, rs.getInt("min_damage"));
            else
                t = new TradeOffer(rs.getInt("id"), User.getUserByID(rs.getInt("seller_id")), cardList.get(rs.getInt("card_id")-1), elem, spec, rs.getInt("min_damage"));

            tradeOfferList.add(t);

        }
        rs.close();
        ps.close();


        c.close();
        System.out.println("Operation done successfully");
    }

    public static ArrayList<Card> getCardList() {
        return cardList;
    }

    public static TradeOffer getTradeOfferByID(int id) {
        for(TradeOffer t : tradeOfferList) {
            if(t.getId() == id)
                return t;
        }
        return null;
    }

    public static ArrayList<Card> getCardPack()
    {
        Random random = new Random();
        ArrayList<Card> pack = new ArrayList<>();
        for(int i = 0; i < 5; i++)
        {
            pack.add(cardList.get(random.nextInt(cardList.size())));
        }

        return pack;
    }

    // returns all current trade offers
    public static ArrayList<TradeOffer> getTradeOffers()
    {
        return tradeOfferList;
    }

    // returns all current trade offers that offer a specified card
    public static ArrayList<TradeOffer> getTradeOffers(Card card)
    {
        ArrayList<TradeOffer> specificOffers = new ArrayList<>();
        for(TradeOffer t : tradeOfferList)
        {
            if(t.getCard().equals(card)) specificOffers.add(t);
        }
        return specificOffers;
    }

    // returns all current trade offers that offer a specified card ID
    public static ArrayList<TradeOffer> getTradeOffers(Integer cardID)
    {
        ArrayList<TradeOffer> specificOffers = new ArrayList<>();
        Card card = cardList.get(cardID-1);
        for(TradeOffer t : tradeOfferList)
        {
            if(t.getCard().equals(card)) specificOffers.add(t);
        }
        return specificOffers;
    }

    public static void trade(User buyer, Card payment, int offerIndex) throws SQLException {
        if(trade(buyer,payment,tradeOfferList.get(offerIndex)))
        {
            tradeOfferList.remove(offerIndex);
        }
    }

    public static boolean trade(User buyer, Card payment, TradeOffer offer) throws SQLException {
        if(offer.checkConditions(payment))
        {
            System.out.println("Card does not fulfill the requirements set by the seller.");
            return false;
        }

        if(!buyer.removeFirstFreeInstanceFromStack(payment))
        {
            System.out.println("Card not currently available in your stack.");
            return false;
        }

        /*ArrayList<StackEntry> stackOffer = offer.getSeller().getStack();
        StackEntry entry;
        for(int i = 0; i < stackOffer.size(); i++)
        {
            entry = stackOffer.get(i);
            if(entry.getTradeOfferId().equals(offer.getId())) {
                stackOffer.remove(i);
                break;
            }
        }
        offer.getSeller().setStack(stackOffer);*/
        offer.getSeller().addCardToStack(payment);

        offer.getSeller().updateData();

        buyer.addCardToStack(offer.getCard());

        try {
            Connection c = connection();
            PreparedStatement ps;
            ps = c.prepareStatement("DELETE FROM public.store WHERE id = ?");
            ps.setInt(1, offer.getId());
            ps.executeUpdate();
            c.commit();
            ps.close();

            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        tradeOfferList.remove(offer);

        return true;
    }


    public static boolean createTradeOffer(User seller, Card offer, CardType requiredCardType, Integer minimumDamage)
    {
        if(!seller.checkIfCardIsTradable(offer))
        {
            System.out.println("Card currently not tradable. To create a trade offer, a card cannot be in your deck or already part of another trade offer");
            return false;
        }
        int id = 0;
        Connection c = connection();
        PreparedStatement ps;
        try {
            ps = c.prepareStatement("INSERT INTO public.store (seller_id, card_id, requirement_card_type_id, min_damage) VALUES (?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1,seller.getId());
            ps.setInt(2,offer.getCardId());
            ps.setInt(3,requiredCardType.ordinal()+1);
            ps.setInt(4, minimumDamage);
            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();

            if (generatedKeys.next()) {
                id = (int)generatedKeys.getLong(1);
            }
            c.commit();
            ps.close();

            ps = c.prepareStatement("UPDATE public.stack SET trade_offer_id = ?\n" +
                    "WHERE CTID IN (SELECT CTID FROM public.stack WHERE user_id = " + seller.getId() + " AND card_id = "
                    + offer.getCardId() + " AND is_in_deck = false AND trade_offer_id IS NULL LIMIT 1);");
            ps.setInt(1, id);
            ps.executeUpdate();
            c.commit();
            ps.close();

            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        tradeOfferList.add(new TradeOffer(id, seller, offer, requiredCardType, minimumDamage));
        return true;
    }

    public static boolean createTradeOffer(User seller, Card offer, Speciality requiredSpec)
    {
        if(!seller.checkIfCardIsTradable(offer))
        {
            System.out.println("Card currently not tradable. To create a trade offer, a card cannot be in your deck or already part of another trade offer");
            return false;
        }

        int id = 0;
        Connection c = connection();
        PreparedStatement ps;
        try {
            ps = c.prepareStatement("INSERT INTO public.store (seller_id, card_id, requirement_card_type_id, requirement_speciality_id) VALUES (?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1,seller.getId());
            ps.setInt(2,offer.getCardId());
            ps.setInt(3,2);
            ps.setInt(4,requiredSpec.ordinal()+1);
            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();

            if (generatedKeys.next()) {
                id = (int)generatedKeys.getLong(1);
            }
            c.commit();
            ps.close();

            ps = c.prepareStatement("UPDATE public.stack SET trade_offer_id = ?\n" +
                    "WHERE CTID IN (SELECT CTID FROM public.stack WHERE user_id = " + seller.getId() + " AND card_id = "
                    + offer.getCardId() + " AND is_in_deck = false AND trade_offer_id IS NULL LIMIT 1);");
            ps.setInt(1, id);
            ps.executeUpdate();
            c.commit();
            ps.close();

            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        tradeOfferList.add(new TradeOffer(id, seller, offer, requiredSpec));
        return true;
    }
    public static boolean createTradeOffer(User seller, Card offer, Element requiredElem, Speciality requiredSpec)
    {
        if(!seller.checkIfCardIsTradable(offer))
        {
            System.out.println("Card currently not tradable. To create a trade offer, a card cannot be in your deck or already part of another trade offer");
            return false;
        }

        int id = 0;
        Connection c = connection();
        PreparedStatement ps;
        try {
            ps = c.prepareStatement("INSERT INTO public.store (seller_id, card_id, requirement_card_type_id,  requirement_element_id, requirement_speciality_id) VALUES (?,?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1,seller.getId());
            ps.setInt(2,offer.getCardId());
            ps.setInt(3,2);
            ps.setInt(4,requiredElem.ordinal()+1);
            ps.setInt(5,requiredSpec.ordinal()+1);
            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();

            if (generatedKeys.next()) {
                id = (int)generatedKeys.getLong(1);
            }
            c.commit();
            ps.close();

            ps = c.prepareStatement("UPDATE public.stack SET trade_offer_id = ?\n" +
                    "WHERE CTID IN (SELECT CTID FROM public.stack WHERE user_id = " + seller.getId() + " AND card_id = "
                    + offer.getCardId() + " AND is_in_deck = false AND trade_offer_id IS NULL LIMIT 1);");
            ps.setInt(1, id);
            ps.executeUpdate();
            c.commit();
            ps.close();

            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        tradeOfferList.add(new TradeOffer(id, seller, offer, requiredSpec));
        return true;
    }

    public static boolean createTradeOffer(User seller, Card offer, Speciality requiredSpec, Integer minimumDamage)
    {
        if(!seller.checkIfCardIsTradable(offer))
        {
            System.out.println("Card currently not tradable. To create a trade offer, a card cannot be in your deck or already part of another trade offer");
            return false;
        }
        int id = 0;
        Connection c = connection();
        PreparedStatement ps;
        try {
            ps = c.prepareStatement("INSERT INTO public.store (seller_id, card_id, requirement_card_type_id, requirement_speciality_id, min_damage) VALUES (?,?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1,seller.getId());
            ps.setInt(2,offer.getCardId());
            ps.setInt(3,2);
            ps.setInt(4,requiredSpec.ordinal()+1);
            ps.setInt(5, minimumDamage);
            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();

            if (generatedKeys.next()) {
                id = (int)generatedKeys.getLong(1);
            }
            c.commit();
            ps.close();

            ps = c.prepareStatement("UPDATE public.stack SET trade_offer_id = ?\n" +
                    "WHERE CTID IN (SELECT CTID FROM public.stack WHERE user_id = " + seller.getId() + " AND card_id = "
                    + offer.getCardId() + " AND is_in_deck = false AND trade_offer_id IS NULL LIMIT 1);");
            ps.setInt(1, id);
            ps.executeUpdate();
            c.commit();
            ps.close();

            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        tradeOfferList.add(new TradeOffer(id, seller, offer, requiredSpec, minimumDamage));
        return true;
    }


    public static boolean createTradeOffer(User seller, Card offer, Element requiredElement, Speciality requiredSpec, Integer minimumDamage)
    {
        if(!seller.checkIfCardIsTradable(offer))
        {
            System.out.println("Card currently not tradable. To create a trade offer, a card cannot be in your deck or already part of another trade offer");
            return false;
        }
        int id = 0;
        Connection c = connection();
        PreparedStatement ps;
        try {
            ps = c.prepareStatement("INSERT INTO public.store (seller_id, card_id, requirement_card_type_id, requirement_element_id, requirement_speciality_id, min_damage) VALUES (?,?,?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1,seller.getId());
            ps.setInt(2,offer.getCardId());
            ps.setInt(3,2);
            ps.setInt(4,requiredElement.ordinal()+1);
            ps.setInt(4,requiredSpec.ordinal()+1);
            ps.setInt(5, minimumDamage);
            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();

            if (generatedKeys.next()) {
                id = (int)generatedKeys.getLong(1);
            }
            c.commit();
            ps.close();

            ps = c.prepareStatement("UPDATE public.stack SET trade_offer_id = ?\n" +
                    "WHERE CTID IN (SELECT CTID FROM public.stack WHERE user_id = " + seller.getId() + " AND card_id = "
                    + offer.getCardId() + " AND is_in_deck = false AND trade_offer_id IS NULL LIMIT 1);");
            ps.setInt(1, id);
            ps.executeUpdate();
            c.commit();
            ps.close();

            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        tradeOfferList.add(new TradeOffer(id, seller, offer, requiredElement, requiredSpec, minimumDamage));
        return true;
    }

    public static boolean createTradeOffer(User seller, Card offer, CardType requiredCardType, Element requiredElement)
    {
        if(!seller.checkIfCardIsTradable(offer))
        {
            System.out.println("Card currently not tradable. To create a trade offer, a card cannot be in your deck or already part of another trade offer");
            return false;
        }
        int id = 0;
        Connection c = connection();
        PreparedStatement ps;
        try {
            ps = c.prepareStatement("INSERT INTO public.store (seller_id, card_id, requirement_card_type_id, requirement_element_id) VALUES (?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1,seller.getId());
            ps.setInt(2,offer.getCardId());
            ps.setInt(3,requiredCardType.ordinal()+1);
            ps.setInt(4,requiredElement.ordinal()+1);
            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();

            if (generatedKeys.next()) {
                id = (int)generatedKeys.getLong(1);
            }
            c.commit();
            ps.close();

            ps = c.prepareStatement("UPDATE public.stack SET trade_offer_id = ?\n" +
                    "WHERE CTID IN (SELECT CTID FROM public.stack WHERE user_id = " + seller.getId() + " AND card_id = "
                    + offer.getCardId() + " AND is_in_deck = false AND trade_offer_id IS NULL LIMIT 1);");
            ps.setInt(1, id);
            ps.executeUpdate();
            c.commit();
            ps.close();

            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        tradeOfferList.add(new TradeOffer(id, seller, offer, requiredCardType, requiredElement));
        return true;
    }

    public static boolean createTradeOffer(User seller, Card offer, CardType requiredCardType, Element requiredElement,Integer minimumDamage)
    {
        if(!seller.checkIfCardIsTradable(offer))
        {
            System.out.println("Card currently not tradable. To create a trade offer, a card cannot be in your deck or already part of another trade offer");
            return false;
        }
        int id = 0;
        Connection c = connection();
        PreparedStatement ps;
        try {
            ps = c.prepareStatement("INSERT INTO public.store (seller_id, card_id, requirement_card_type_id, requirement_element_id, min_damage) VALUES (?,?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1,seller.getId());
            ps.setInt(2,offer.getCardId());
            ps.setInt(3,requiredCardType.ordinal()+1);
            ps.setInt(4,requiredElement.ordinal()+1);
            ps.setInt(5, minimumDamage);
            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();

            if (generatedKeys.next()) {
                id = (int)generatedKeys.getLong(1);
            }
            c.commit();
            ps.close();

            ps = c.prepareStatement("UPDATE public.stack SET trade_offer_id = ?\n" +
                    "WHERE CTID IN (SELECT CTID FROM public.stack WHERE user_id = " + seller.getId() + " AND card_id = "
                    + offer.getCardId() + " AND is_in_deck = false AND trade_offer_id IS NULL LIMIT 1);");
            ps.setInt(1, id);
            ps.executeUpdate();
            c.commit();
            ps.close();

            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        tradeOfferList.add(new TradeOffer(id, seller, offer, requiredCardType, requiredElement, minimumDamage));
        return true;
    }

    public static boolean retractTradeOffer(TradeOffer offer)
    {
        if(!tradeOfferList.contains(offer))
            return false;
        Connection c = connection();
        PreparedStatement ps;
        try {

            ps = c.prepareStatement("UPDATE public.stack SET trade_offer_id = NULL WHERE trade_offer_id = ?;");
            ps.setInt(1,offer.getId());
            ps.executeUpdate();

            c.commit();
            ps.close();

            ps = c.prepareStatement("DELETE FROM public.store WHERE id = ?");
            ps.setInt(1,offer.getId());
            ps.executeUpdate();

            c.commit();
            ps.close();

            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        tradeOfferList.remove(offer);
        return true;
    }

}

