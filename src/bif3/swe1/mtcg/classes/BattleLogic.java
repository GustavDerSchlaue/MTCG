package bif3.swe1.mtcg.classes;

import bif3.swe1.mtcg.enums.Element;
import bif3.swe1.mtcg.enums.Speciality;
import bif3.swe1.mtcg.interfaces.Card;

import java.util.ArrayList;
import java.util.Random;

public class BattleLogic {


    private static final Double effective = 2.0, notEffective = 0.5, noEffect = 1.0; //damage modifiers from Element type
    private static final Integer maxTurns = 100; //maximum number of turns

    /**
     *
     *
     * @param deckA is the deck of player 1
     * @param deckB is the deck of player 2
     * @return returns '1' if player 1 wins, returns '2' if player 2 wins and returns 0 in case of a draw
     */
    public static Integer doBattle(ArrayList<Card> deckA, ArrayList<Card> deckB)
    {
        Random random = new Random();
        Card cardA, cardB, winner;
        int cardNumberA, cardNumberB;
        for(int i = 0; i<maxTurns; i++)
        {
            cardNumberA = random.nextInt(deckA.size());
            cardNumberB = random.nextInt(deckB.size());
            cardA = deckA.get(cardNumberA);
            cardB = deckB.get(cardNumberB);
            System.out.print("Round " + (i+1) + ": ");
            if(cardA instanceof Monster && cardB instanceof Monster)
            {
                winner = fight((Monster) cardA, (Monster) cardB);
                System.out.println("win! " + cardA.getName());
            } else if(cardA instanceof Monster && cardB instanceof Spell) {
                winner = fight((Monster) cardA, (Spell) cardB);
            } else if(cardA instanceof Spell && cardB instanceof Monster) {
                winner = fight((Spell) cardA, (Monster) cardB);
            } else if(cardA instanceof Spell && cardB instanceof Spell) {
                winner = fight((Spell) cardA, (Spell) cardB);
            } else if(cardA instanceof Trap && cardB instanceof Spell) {
                winner = fight((Trap) cardA, (Spell) cardB);
            } else if(cardA instanceof Trap && cardB instanceof Monster) {
                winner = fight((Trap) cardA, (Monster) cardB);
            } else if(cardA instanceof Trap && cardB instanceof Trap) {
                winner = fight((Trap) cardA, (Trap) cardB);
            } else if(cardA instanceof Spell && cardB instanceof Trap) {
                winner = fight((Spell) cardA, (Trap) cardB);
            } else if(cardA instanceof Monster && cardB instanceof Trap) {
                winner = fight((Monster) cardA, (Trap) cardB);
            } else { winner = null; }

            if (winner != null)
            {
                System.out.println("Round " + (i+1) + " winner: " + winner.getName());
                if(winner == cardA)
                {
                    deckA.add(cardB);
                    deckB.remove(cardNumberB);
                } else {
                    deckA.remove(cardNumberA);
                    deckB.add(cardA);
                }
            } else {
                System.out.println("Round " + (i+1) + ": draw!");
            }
            if(deckB.isEmpty())
            {
                System.out.println("Player 1 won the match!");
                return 1;
            } else if(deckA.isEmpty())  {
                System.out.println("Player 2 won the match!");
                return 2;
            }
        }
        System.out.println("Draw! No winner.");
        return 0;
    }

    public static void doBattle(User playerA, User playerB)
    {

        System.out.println("Battle: " + playerA.getName() + " vs " + playerB.getName());
        int outcome = doBattle(playerA.getDeck(), playerB.getDeck());
        switch (outcome) {
            case 0:
                playerA.drawGame();
                playerB.drawGame();
                break;
            case 1:
                playerA.wonGame();
                playerB.lostGame();
                System.out.println("Congratulations " + playerA.getName() + "!");
                break;
            case 2:
                playerA.lostGame();
                playerB.wonGame();
                System.out.println("Congratulations " + playerB.getName() + "!");
                break;
        }
    }

    /****
     * @param atk is the Card of player 1
     * @param def is the Card of player 2
     * @return returns the victorious Card or null in case of a draw
     ****/

    public static Card fight(Monster atk, Monster def)
    {
        double dif;
        System.out.println("Monster vs Monster");
        System.out.println(atk.getName() + "(" + atk.getElement() + "/" + atk.getSpeciality() + "/[" + atk.getDamage() + "])" + " vs " + def.getName() + "(" + def.getElement() + "/" + def.getSpeciality() + "/[" + def.getDamage() + "])");
        if(atk.getSpeciality() == Speciality.GOBLIN && def.getSpeciality() == Speciality.DRAGON) return def;
        if(def.getSpeciality() == Speciality.GOBLIN && atk.getSpeciality() == Speciality.DRAGON) return atk;
        if(atk.getSpeciality() == Speciality.ORK && def.getSpeciality() == Speciality.WIZARD) return def;
        if(def.getSpeciality() == Speciality.ORK && atk.getSpeciality() == Speciality.WIZARD) return atk;
        if(atk.getSpeciality() == Speciality.DRAGON && def.getSpeciality() == Speciality.ELF && def.getElement() == Element.FIRE) return def;
        if(def.getSpeciality() == Speciality.DRAGON && atk.getSpeciality() == Speciality.ELF && atk.getElement() == Element.FIRE) return atk;

        if(atk.getSpeciality() == Speciality.DWARF && (def.getSpeciality() == Speciality.GOBLIN || def.getSpeciality() == Speciality.ORK)) {
            dif = atk.getDamage() * effective - def.getDamage();
        } else if((atk.getSpeciality() == Speciality.GOBLIN || atk.getSpeciality() == Speciality.ORK) && def.getSpeciality() == Speciality.DWARF) {
            dif = atk.getDamage() - def.getDamage() * effective;
        } else {
            dif = atk.getDamage() - def.getDamage();
        }

        if(dif>0){return atk;} else if(dif<0) {return def;} else {return null;}
    }

    public static Card fight(Spell atk, Monster def)
    {
        double dif;
        System.out.println("Spell vs Monster");
        System.out.println(atk.getName() + "(" + atk.getElement() + "/[" + atk.getDamage() + "])" + " vs " + def.getName() + "(" + def.getElement() + "/" + def.getSpeciality() + "/[" + def.getDamage() + "])");
        if(atk.getElement() == Element.WATER && def.getSpeciality() == Speciality.KNIGHT) return atk;
        if(def.getSpeciality() == Speciality.KRAKEN) return def;

        if(def.getSpeciality() == Speciality.DWARF)
        {
            dif = (atk.getDamage() * notEffective) - (def.getDamage() * getEffectivenessModifier(def, atk));
        } else {
            dif = (atk.getDamage() * getEffectivenessModifier(atk, def)) - (def.getDamage() * getEffectivenessModifier(def, atk));
        }

        if(dif>0){return atk;} else if(dif<0) {return def;} else {return null;}
    }

    public static Card fight(Monster atk, Spell def)
    {
        double dif;
        System.out.println("Monster vs Spell");
        System.out.println(atk.getName() + "(" + atk.getElement() + "/" + atk.getSpeciality() + "/[" + atk.getDamage() + "])" + " vs " + def.getName() + "(" + def.getElement() + "/[" + def.getDamage() + "])");
        if(atk.getSpeciality() == Speciality.KNIGHT && def.getElement() == Element.WATER) return def;
        if(atk.getSpeciality() == Speciality.KRAKEN) return atk;

        if(atk.getSpeciality() == Speciality.DWARF)
        {
            dif = (atk.getDamage() * getEffectivenessModifier(atk, def)) - (def.getDamage() * notEffective);
        } else {
            dif = (atk.getDamage() * getEffectivenessModifier(atk, def)) - (def.getDamage() * getEffectivenessModifier(def, atk));
        }        if(dif>0){return atk;} else if(dif<0) {return def;} else {return null;}
    }

    public static Card fight(Spell atk, Spell def)
    {
        System.out.println("Spell vs Spell");
        System.out.println(atk.getName() + "(" + atk.getElement() + "/[" + atk.getDamage() + "])" + " vs " + def.getName() + "(" + def.getElement() + "/[" + def.getDamage() + "])");
        double dif = (atk.getDamage() * getEffectivenessModifier(atk, def)) - (def.getDamage() * getEffectivenessModifier(def, atk));
        if(dif>0){return atk;} else if(dif<0) {return def;} else {return null;}
    }

    public static Card fight(Trap atk, Spell def)
    {
        System.out.println("Trap vs Spell");
        System.out.println(atk.getName() + "(" + atk.getElement() + "/[" + atk.getDamage() + "])" + " vs " + def.getName() + "(" + def.getElement() + "/[" + def.getDamage() + "])");
        double dif = (atk.getDamage() * effective) - (def.getDamage() * getEffectivenessModifier(def, atk));
        if(dif>0){return atk;} else if(dif<0) {return def;} else {return null;}
    }

    public static Card fight(Trap atk, Monster def)
    {
        System.out.println("Trap vs Monster");
        System.out.println(atk.getName() + "(" + atk.getElement()+ "/[" + atk.getDamage() + "])" + " vs " + def.getName() + "(" + def.getElement() + "/" + def.getSpeciality()+ "/[" + def.getDamage() + "])");
        double dif;
        if(def.getSpeciality() == Speciality.GOBLIN) {
            dif = (atk.getDamage() * notEffective) - (def.getDamage() * effective);
        } else {
            dif = (atk.getDamage() * notEffective) - (def.getDamage() * getEffectivenessModifier(def, atk));
        }
        if(dif>0){return atk;} else if(dif<0) {return def;} else {return null;}
    }

    public static Card fight(Trap atk, Trap def)
    {
        System.out.println("Trap vs Trap");
        System.out.println(atk.getName() + "(" + atk.getElement() + "/[" + atk.getDamage() + "])" + " vs " + def.getName() + "(" + def.getElement() + "/[" + def.getDamage() + "])");

        return null;
    }

    public static Card fight(Spell atk, Trap def)
    {
        System.out.println("Spell vs Trap");
        System.out.println(atk.getName() + "(" + atk.getElement() + "/[" + atk.getDamage() + "])" + " vs " + def.getName() + "(" + def.getElement() + "/[" + def.getDamage() + "])");
        double dif = (atk.getDamage() * getEffectivenessModifier(atk, def)) - (def.getDamage() * effective);
        if(dif>0){return atk;} else if(dif<0) {return def;} else {return null;}
    }

    public static Card fight(Monster atk, Trap def)
    {
        System.out.println("Monster vs Trap");
        System.out.println(atk.getName() + "(" + atk.getElement() + "/" + atk.getSpeciality() + "/[" + atk.getDamage() + "])" + " vs " + def.getName() + "(" + def.getElement() + "/[" + def.getDamage() + "])");
        double dif;
        if(atk.getSpeciality() == Speciality.GOBLIN) {
            dif = (atk.getDamage() * effective) - (def.getDamage() * notEffective);
        } else {
            dif = (atk.getDamage() * getEffectivenessModifier(def, atk)) - (def.getDamage() * notEffective);
        }
        if(dif>0){return atk;} else if(dif<0) {return def;} else {return null;}
    }

    public static Double getEffectivenessModifier(Card a, Card b)
    {
        if (a.getElement() == Element.WATER)
        {
            if (b.getElement() == Element.FIRE) return effective;
            if (b.getElement() == Element.EARTH) return effective;
            if (b.getElement() == Element.NORMAL) return notEffective;
            if (b.getElement() == Element.ICE) return notEffective;
            return noEffect;
        }
        else if (a.getElement() == Element.FIRE)
        {
            if (b.getElement() == Element.NORMAL) return effective;
            if (b.getElement() == Element.ICE) return effective;
            if (b.getElement() == Element.WATER) return notEffective;
            if (b.getElement() == Element.EARTH) return notEffective;
            return noEffect;
        }
        else if (a.getElement() == Element.NORMAL)
        {
            if (b.getElement() == Element.WATER) return effective;
            if (b.getElement() == Element.EARTH) return effective;
            if (b.getElement() == Element.FIRE) return notEffective;
            if (b.getElement() == Element.ICE) return notEffective;
            return noEffect;
        }
        else if (a.getElement() == Element.ICE)
        {
            if (b.getElement() == Element.WATER) return effective;
            if (b.getElement() == Element.NORMAL) return effective;
            if (b.getElement() == Element.FIRE) return notEffective;
            if (b.getElement() == Element.EARTH) return notEffective;
            return noEffect;
        }
        else if (a.getElement() == Element.EARTH)
        {
            if (b.getElement() == Element.FIRE) return effective;
            if (b.getElement() == Element.ICE) return effective;
            if (b.getElement() == Element.WATER) return notEffective;
            if (b.getElement() == Element.NORMAL) return notEffective;
            return noEffect;
        }

        //this will never be reached with the current Element Types, but is a fail-safe for if new types are added but not yet accounted for here
        return noEffect;
    }

    public static Double getEffectiveModifier() { return effective; }

    public static Double getNotEffectiveModifier() { return notEffective; }

    public static Double getNoEffectModifier() { return noEffect; }
}
