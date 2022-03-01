package bif3.swe1.rest.classes;

import bif3.swe1.mtcg.classes.*;
import bif3.swe1.mtcg.enums.CardType;
import bif3.swe1.mtcg.enums.Element;
import bif3.swe1.mtcg.enums.Speciality;
import bif3.swe1.mtcg.interfaces.Card;
import bif3.swe1.rest.enums.responseCodes;
import javafx.util.Pair;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;

public class RequestContext {

    public static Pair<ArrayList<String>, String> handleRequest(String method, String path, String body, String accept, String token, ArrayList<String> messages)
    {
        String responseCode = "";
        User user = null;
        if(accept.equals("No") && !path.equals("/register"))
        {
            return new Pair<>(messages, responseCodes.UNAUTHORIZED_ERROR.getErrorCode()+" invalid credentials");
        }
        if(!token.equals(""))
        {
            user = User.login(token);
        }

        String[] split = path.split("/");
        switch (method) {
            case "POST":
                //System.out.println("We are in POST");
                if (path.equals("/messages")) {
                    if(body.isEmpty())
                    {
                        responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"no body was received";
                        break;
                    }
                    messages.add(body);
                    System.out.println(messages.size()); // returns the ID (that being the last index of the list and thus equals .size() since we start with 1 for the IDs
                    responseCode = responseCodes.OK.getErrorCode()+"ID: "+messages.size();
                } else if(!token.equals("")) {
                    if (path.equals("/buy")) {

                        if (user.getCoins() < 5)
                            responseCode = responseCodes.CLIENT_ERROR.getErrorCode() + "no enough coins to buy a Card pack";
                        else {
                            user.buyCardPack();
                            responseCode = responseCodes.OK.getErrorCode()+"pack bought";
                        }
                    } else if (path.equals("/battle")) {
                        responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"Please specify the ID of the user you want to battle";
                    } else if (path.contains("/battle/")) {

                        int id = Integer.parseInt(split[2]);
                        User user2 = User.getUserByID(id);
                        if(user2 == null)
                            responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"Please select valid opponent";
                        else if(user.getId().equals(user2.getId()))
                            responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"You cannot battle yourself";
                        else if(user2.getDeck().size() < 4 || user.getDeck().size() < 4)
                            responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"One of the Users decks is not ready for battle.";
                        else {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            PrintStream ps = new PrintStream(baos);

                            PrintStream old = System.out;
                            System.setOut(ps);
                            BattleLogic.doBattle(user, user2);
                            System.out.flush();
                            System.setOut(old);
                            System.out.println(baos.toString());
                            responseCode = responseCodes.OK.getErrorCode() + baos;
                        }
                    } else if(path.contains("/tradeoffers/")) {
                        if(body.isEmpty())
                        {
                            responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"no body was received. please put your requirement into the body";
                            break;
                        }
                        int id = Integer.parseInt(split[2]);
                        StackEntry st = user.getStack().get(id);
                        if(st == null)
                            responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"There is no such ID in your stack.";
                        else if(!user.checkIfCardIsTradable(st.getCard()))
                            responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"This card is not tradable.";
                        else {
                            Card card = st.getCard();
                            String[] req;
                            req = body.split(":");
                            if(req.length != 4 || req[0].isEmpty()) {
                                responseCode = responseCodes.CLIENT_ERROR.getErrorCode() + "The body must be formatted properly to create a trade offer. For more details please check the documentation.";
                            } else {
                                CardType ct = null;
                                Element elem = null;
                                Speciality spec = null;
                                int minDmg = 0;
                                for(CardType c : CardType.values()) {
                                    if(c.toString().equals(req[0].toUpperCase()))
                                        ct = c;
                                }
                                if(ct == null) {
                                    responseCode = responseCodes.CLIENT_ERROR.getErrorCode() + "you must designate a valid required card type";
                                    break;
                                }

                                if(!req[1].isEmpty()) {
                                    for(Element e : Element.values())
                                    {
                                        if(e.toString().equals(req[1].toUpperCase()))
                                            elem = e;
                                    }
                                }
                                if(ct.equals(CardType.MONSTER) && !req[2].isEmpty()) {
                                    for(Speciality s : Speciality.values())
                                    {
                                        if(s.toString().equals(req[2].toUpperCase()))
                                            spec = s;
                                    }
                                }
                                if(!req[3].equals(";")) {
                                    try{
                                        minDmg = Integer.parseInt(req[3].split(";")[0]);
                                    }
                                    catch (NumberFormatException ex){
                                        ex.printStackTrace();
                                    }
                                }

                                try {
                                    Store.initializeStore();
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }

                                if(spec != null)
                                {
                                    if(elem != null && minDmg != 0)
                                        Store.createTradeOffer(user,card,elem,spec,minDmg);
                                    else if(elem != null)
                                        Store.createTradeOffer(user,card,elem,spec);
                                    else if(minDmg != 0)
                                        Store.createTradeOffer(user,card,spec,minDmg);
                                    else
                                        Store.createTradeOffer(user,card,spec);
                                } else {
                                    if(elem != null && minDmg != 0)
                                        Store.createTradeOffer(user,card,ct,elem,minDmg);
                                    else if(elem != null)
                                        Store.createTradeOffer(user,card,ct,elem);
                                    else
                                        Store.createTradeOffer(user,card,ct,minDmg);
                                }
                                responseCode = responseCodes.OK.getErrorCode()+"Trade offer created.";
                            }
                        }
                    } else if(path.contains("/trade/")) {
                        if(body.isEmpty())
                        {
                            responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"no body was received. please put the ID of your card you would like to trade in there";
                            break;
                        }

                        try {
                            Store.initializeStore();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        int paymentID = 0;
                        try{
                            paymentID = Integer.parseInt(body);
                        }
                        catch (NumberFormatException ex){
                            ex.printStackTrace();
                        }

                        int id = Integer.parseInt(split[2]);
                        StackEntry st = user.getStack().get(paymentID-1);
                        TradeOffer offer = Store.getTradeOfferByID(id);
                        if(offer == null)
                            responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"There is no such trade offer.";
                        if(offer.getSeller().getId().equals(user.getId()))
                            responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"This is your trade offer. If you want to remove it, please use the proper command.";
                        else if(!user.checkIfCardIsTradable(st.getCard()))
                            responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"This card is currently not tradable.";
                        else {
                            try {
                                if(Store.trade(user,st.getCard(),offer))
                                    responseCode = responseCodes.OK.getErrorCode()+"Trade successful.";
                                else
                                    responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"Trade offer requirements not met";
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        }
                    } else if(path.equals("/register")) {
                        String credentials = new String(Base64.getDecoder().decode(token));
                        String[] usernameAndPassword = credentials.split(":");
                        String username = usernameAndPassword[0];
                        String passw = usernameAndPassword[1];
                        if(User.getUserByName(username) != null)
                            responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"This name is already taken! Please chose another.";
                        else {
                            User newUser = User.registerNewUser(username, passw);
                            responseCode = responseCodes.OK.getErrorCode()+"Congratulations! You are now our newest member, welcome "+username+". You have " + newUser.getCoins() + " coins available to purchase card packs to fill your collection";
                        }
                    } else {
                        responseCode = responseCodes.CLIENT_ERROR.getErrorCode();
                    }
                } else if(path.equals("/register")) {
                    responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"When registering a new account, please send your chosen username and password in the Authentication header! This is how you will login as well.";
                } else {
                    responseCode = responseCodes.CLIENT_ERROR.getErrorCode();
                }
                break;
            case "GET":
                //System.out.println("We are in GET");
                if (path.equals("/messages")) {
                    responseCode = responseCodes.OK.getErrorCode()+Arrays.toString(messages.toArray());
                } else if (path.contains("/messages/")) {

                    int id = Integer.parseInt(split[2]);
                    System.out.println(id);
                    if(messages.size() < id || id < 1)
                    {
                        responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"id not found";
                        break;
                    }
                    System.out.println(messages.get(id - 1)); //since the IDs start at 1 but the indices at 0, we subtract 1
                    responseCode = responseCodes.OK.getErrorCode()+messages.get(id - 1);
                } else if(!token.equals("")) {
                    if(path.equals("/login"))
                    {
                        responseCode = responseCodes.OK.getErrorCode() + token;
                    } else if(path.equals("/stack"))
                    {

                        ArrayList<StackEntry> entries = user.getStack();
                        StringBuilder sb = new StringBuilder();
                        if(entries.isEmpty())
                            responseCode = responseCodes.OK.getErrorCode()+"Stack is Empty!";
                        else {
                            int i = 0;
                            for (StackEntry s : entries) {
                                sb.append(++i).append(". ");
                                sb.append(s.toString());
                                sb.append("\n");
                            }
                            String str = sb.toString();
                            responseCode = responseCodes.OK.getErrorCode() + str;
                        }
                    } else if (path.equals("/deck"))
                    {
                        ArrayList<Card> cards = user.getDeck();
                        if(cards.isEmpty())
                            responseCode = responseCodes.OK.getErrorCode()+"Deck is Empty!";
                        else {
                            StringBuilder sb = new StringBuilder();
                            int i = 0;
                            for (Card c : cards) {
                                sb.append(++i).append(". ");
                                sb.append(c.toString());
                                sb.append("\n");
                            }
                            String str = sb.toString();
                            responseCode = responseCodes.OK.getErrorCode() + str;
                        }
                    }
                    else if (path.equals("/players"))
                    {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        PrintStream ps = new PrintStream(baos);

                        PrintStream old = System.out;
                        System.setOut(ps);
                        user.listPlayers();
                        System.out.flush();
                        System.setOut(old);
                        System.out.println(baos.toString());
                        responseCode = responseCodes.OK.getErrorCode() + baos;
                    }
                    else if (path.equals("/scoreboard"))
                    {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        PrintStream ps = new PrintStream(baos);

                        PrintStream old = System.out;
                        System.setOut(ps);
                        user.viewScoreBoard();
                        System.out.flush();
                        System.setOut(old);
                        System.out.println(baos.toString());
                        responseCode = responseCodes.OK.getErrorCode() + baos;
                    }
                    else if (path.equals("/profile"))
                    {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        PrintStream ps = new PrintStream(baos);

                        PrintStream old = System.out;
                        System.setOut(ps);
                        user.viewProfile();
                        System.out.flush();
                        System.setOut(old);
                        System.out.println(baos.toString());
                        responseCode = responseCodes.OK.getErrorCode() + baos;
                    }
                    else if (path.equals("/ratio"))
                    {
                        double ratio = user.getWinLossRatio();
                        responseCode = responseCodes.OK.getErrorCode() + "Your Win-Loss Ratio is: "+ratio;
                    }
                    else if (path.contains("/tradeoffers"))
                    {
                        //User user = User.getUserByName(username);
                        try {
                            Store.initializeStore();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        ArrayList<TradeOffer> offers;
                        if(path.contains("/tradeoffers/"))
                        {
                            int id;
                            id = Integer.parseInt(split[2]);
                            offers = Store.getTradeOffers(id);
                        } else {
                            offers = Store.getTradeOffers();
                        }
                        if(offers.isEmpty())
                            responseCode = responseCodes.OK.getErrorCode()+"No Trade Offers currently on the Market!";
                        else {
                            StringBuilder sb = new StringBuilder();
                            //int i = 0;
                            for (TradeOffer o : offers) {
                                //sb.append((++i) + (". "));
                                sb.append(o.toString());
                                sb.append("\n");
                            }
                            String str = sb.toString();
                            responseCode = responseCodes.OK.getErrorCode() + str;
                        }
                    }
                    else {
                        responseCode = responseCodes.CLIENT_ERROR.getErrorCode();
                    }
                } else {
                    responseCode = responseCodes.CLIENT_ERROR.getErrorCode();
                }
                break;
            case "PUT":
                //System.out.println("We are in PUT");
                if (path.contains("/messages/")) {
                    if(body.isEmpty())
                    {
                        responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"no body was received";
                        break;
                    }
                    int id = Integer.parseInt(split[2]);
                    System.out.println(id);
                    if(messages.size() < id || id < 1)
                    {
                        responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"id not found";
                        break;
                    }
                    messages.set(id - 1,body); //since the IDs start at 1 but the indices at 0, we subtract 1
                    responseCode = responseCodes.OK.getErrorCode()+"Change successful";
                } else if(!token.equals("")) {
                    if (path.contains("/deck/")) {
                        int id = Integer.parseInt(split[2]);
                        if(user.addCardToDeck(id-1))
                            responseCode = responseCodes.OK.getErrorCode()+"Change successful";
                        else if(user.getDeck().size() >= 4)
                            responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"You already have 4 cards in your deck";
                        else
                            responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"Error, check ID again";
                    } else if (path.contains("/password")) {

                        if(body.isEmpty() || body.contains(" ") || body.contains("\n"))
                        {
                            responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"no body or invalid password was received. please put the new password into the body without whitespaces nor linebreaks";
                            break;
                        }
                        user.changePassword(body);
                        responseCode = responseCodes.OK.getErrorCode()+"password changed!";
                    } else {
                        responseCode = responseCodes.CLIENT_ERROR.getErrorCode();
                    }
                } else {
                    responseCode = responseCodes.CLIENT_ERROR.getErrorCode();
                }
                break;
            case "DELETE":
                //System.out.println("We are in DELETE");
                if (path.contains("/messages/")) {
                    split = path.split("/");
                    int id = Integer.parseInt(split[2]);
                    System.out.println(id);
                    if(messages.size() < id || id < 1)
                    {
                        responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"id not found";
                        break;
                    }
                    messages.remove(id - 1); //since the IDs start at 1 but the indices at 0, we subtract 1
                    responseCode = responseCodes.OK.getErrorCode()+"Removal successful";
                } else if(!token.equals("")) {
                    if (path.contains("/deck/")) {
                        int id = Integer.parseInt(split[2]);
                        if(user.removeCardFromDeck(id-1))
                            responseCode = responseCodes.OK.getErrorCode()+"Change successful";
                        else
                            responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"Error, check ID again";
                    } else if (path.contains("/tradeoffers/")) {
                        int id = Integer.parseInt(split[2]);
                        try {
                            Store.initializeStore();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                        TradeOffer t = Store.getTradeOfferByID(id);

                        if(t != null && !t.getSeller().getId().equals(user.getId()))
                            responseCode = responseCodes.OK.getErrorCode()+"You cannot retract someone else's trade offer.";
                        else if(t != null && Store.retractTradeOffer(t))
                        responseCode = responseCodes.OK.getErrorCode()+"Trade offer retracted.";
                        else
                            responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"Error, check ID again";
                    } else if (path.contains("/recycle/")) {
                        int id = Integer.parseInt(split[2]);
                        ArrayList<StackEntry> stack = user.getStack();

                        if(id <= stack.size() && user.checkIfCardIsTradable(stack.get(id - 1).getCard())) {
                            user.recycleCard(stack.get(id - 1).getCard());
                            responseCode = responseCodes.OK.getErrorCode() + "Successfully recycled card. You have gained 1 coin.";
                        } else
                            responseCode = responseCodes.CLIENT_ERROR.getErrorCode()+"Error, check ID again";
                    } else {
                        responseCode = responseCodes.CLIENT_ERROR.getErrorCode();
                    }
                } else {
                    responseCode = responseCodes.CLIENT_ERROR.getErrorCode();
                }
                break;
        }

        return new Pair<>(messages, responseCode);
    }

    public static HashMap<String,String> breakUpRequest(String request)
    {
        HashMap<String, String> headerAndBody = new HashMap<>();
        String[] requestParam = request.split("\n");
        String[] paramArr = new String[requestParam.length];
        boolean noBody = true;
        int i = 0;
        int blankLine = 0;

        // here we determine where the blank line preceding the body is and if there even is a body by checking the content-length parameter in the header
        // the 17th character is the length if it is a single digit, thus if it is a zero, the length can only be a zero
        for (String param : requestParam) {
            if (param.indexOf("\r") == 0 && blankLine == 0) {
                blankLine = i;
            } else if(param.contains("Content-Length") && !(param.contains("0") && param.length() == 17)) {
                noBody = false;
            }
            paramArr[i] = param.trim();
            i++;
        }
        String token = "";
        StringBuilder body = new StringBuilder();
        String[] methodAndPath = paramArr[0].split(" ");
        headerAndBody.put("method", methodAndPath[0]);
        headerAndBody.put("path", methodAndPath[1]);
        for (int j = 1; j < blankLine; j++)
        {
            String[] keyValuePair = paramArr[j].split(": ");
            headerAndBody.put(keyValuePair[0], keyValuePair[1]);
            if(keyValuePair[0].equals("Authorization") && keyValuePair[1].contains("Basic"))
            {
                token = keyValuePair[1].substring("Basic".length()).trim();
            }
        }
        System.out.println("token: " + token);
        if(!token.equals("")) {
            if (User.checkIfUserExists(token) || headerAndBody.get("path").equals("/register")) {
                headerAndBody.put("token", token);
            } else {
                headerAndBody.put("Accept", "No");
            }
        } else headerAndBody.put("token", "");
        System.out.println(Arrays.toString(methodAndPath));
        if(!noBody) {
            for (int j = blankLine+1; j < paramArr.length; j++)
                body.append(paramArr[j]);
        }
        headerAndBody.put("body", body.toString());
        return headerAndBody;
    }
}
