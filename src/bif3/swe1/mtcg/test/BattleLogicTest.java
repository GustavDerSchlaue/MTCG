package bif3.swe1.mtcg.test;

import bif3.swe1.mtcg.classes.BattleLogic;
import bif3.swe1.mtcg.classes.Monster;
import bif3.swe1.mtcg.classes.Spell;
import bif3.swe1.mtcg.enums.Element;
import bif3.swe1.mtcg.enums.Speciality;
import bif3.swe1.mtcg.interfaces.Card;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BattleLogicTest {

    @org.junit.jupiter.api.Test
    void testBattle() {
        System.out.println("--- Battle Test ---");
        System.out.println("--- Note: Due to random chance, it is possible for this unit case to fail, but that is expected ---");
        Monster mon1 = new Monster("Red Dragon", 70, Element.FIRE, Speciality.DRAGON);
        Monster mon2 = new Monster("Sea Elves", 45, Element.WATER, Speciality.ELF);
        ArrayList<Card> deck1 = new ArrayList<>();
        ArrayList<Card> deck2 = new ArrayList<>();
        deck1.add(mon1);
        deck1.add(mon1);
        deck2.add(mon2);
        deck2.add(mon2);
        assertEquals(BattleLogic.doBattle(deck1, deck2),1);
    }

    @org.junit.jupiter.api.Test
    void testBattleDraw() {
        System.out.println("--- Battle Test Draw ---");
        Monster mon1 = new Monster("Red Dragon", 70, Element.FIRE, Speciality.DRAGON);
        Monster mon2 = new Monster("Sea Elves", 70, Element.WATER, Speciality.ELF);
        ArrayList<Card> deck1 = new ArrayList<>();
        ArrayList<Card> deck2 = new ArrayList<>();
        deck1.add(mon1);
        deck1.add(mon1);
        deck2.add(mon2);
        deck2.add(mon2);
        assertEquals(BattleLogic.doBattle(deck1, deck2),0);
    }

    @org.junit.jupiter.api.Test
    void testFightMonsterVsMonster() {
        Monster mon1 = new Monster("Red Dragon", 70, Element.FIRE, Speciality.DRAGON);
        Monster mon2 = new Monster("Sea Elves", 45, Element.WATER, Speciality.ELF);
        assertEquals(BattleLogic.fight(mon1,mon2),mon1);
        assertEquals(BattleLogic.fight(mon2,mon1),mon1);
    }

    @org.junit.jupiter.api.Test
    void testFightDwarfVsOrk() {
        Monster mon1 = new Monster("Fire Slayer", 60, Element.FIRE, Speciality.DWARF);
        Monster mon2 = new Monster("Massive Ork Warboss", 70, Element.NORMAL, Speciality.ORK);
        assertEquals(BattleLogic.fight(mon1,mon2),mon1);
        assertEquals(BattleLogic.fight(mon2,mon1),mon1);
    }

    @org.junit.jupiter.api.Test
    void testFightDragonVsFireElves() {
        Monster mon1 = new Monster("Red Dragon", 70, Element.FIRE, Speciality.DRAGON);
        Monster mon2 = new Monster("Fire Elves", 35, Element.FIRE, Speciality.ELF);
        assertEquals(BattleLogic.fight(mon1,mon2),mon2);
        assertEquals(BattleLogic.fight(mon2,mon1),mon2);
    }

    @org.junit.jupiter.api.Test
    void testFightOrksVsWizard() {
        Monster mon1 = new Monster("Ork Warband", 40, Element.NORMAL, Speciality.ORK);
        Monster mon2 = new Monster("Bright Wizard", 35, Element.FIRE, Speciality.WIZARD);
        assertEquals(BattleLogic.fight(mon1,mon2),mon2);
        assertEquals(BattleLogic.fight(mon2,mon1),mon2);
    }


    @org.junit.jupiter.api.Test
    void testFightKnightVsWaterSpell() {
        Monster mon1 = new Monster("Questing Knight", 40, Element.NORMAL, Speciality.KNIGHT);
        Spell spell1 = new Spell("Water Spray", 20, Element.WATER);
        assertEquals(BattleLogic.fight(mon1,spell1),spell1);
        assertEquals(BattleLogic.fight(spell1,mon1),spell1);
    }

    @org.junit.jupiter.api.Test
    void testFightSpellVsKraken() {
        Spell spell1 = new Spell("Annihilation", 100, Element.NORMAL);
        Monster mon1 = new Monster("Sea Kraken", 60, Element.WATER, Speciality.KRAKEN);
        assertEquals(BattleLogic.fight(spell1,mon1),mon1);
        assertEquals(BattleLogic.fight(mon1,spell1),mon1);
    }

    @org.junit.jupiter.api.Test
    void testFightSpellVsDwarf() {
        Spell spell1 = new Spell("Annihilation", 100, Element.NORMAL);
        Monster mon1 = new Monster("Dwarven Royal Guard", 60, Element.NORMAL, Speciality.DWARF);
        assertEquals(BattleLogic.fight(spell1,mon1),mon1);
        assertEquals(BattleLogic.fight(mon1,spell1),mon1);
    }


    @org.junit.jupiter.api.Test
    void testFightDragonVsGoblins() {
        Monster mon1 = new Monster("Dragon Hatchling", 15, Element.NORMAL, Speciality.DRAGON);
        Monster mon2 = new Monster("Gaggle of Goblins", 20, Element.NORMAL, Speciality.GOBLIN);
        assertEquals(BattleLogic.fight(mon1,mon2),mon1);
        assertEquals(BattleLogic.fight(mon2,mon1),mon1);
    }

    @org.junit.jupiter.api.Test
    void testFightSpellVsSpell() {
        Spell spell1 = new Spell("Fireball", 50, Element.FIRE);
        Spell spell2 = new Spell("Earthquake", 80, Element.NORMAL);
        assertEquals(BattleLogic.fight(spell1,spell2),spell1);
        assertEquals(BattleLogic.fight(spell2,spell1),spell1);
    }

    @org.junit.jupiter.api.Test
    void testFightSpellVsSpell1() {
        Spell spell1 = new Spell("Water Spray", 20, Element.WATER);
        Spell spell2 = new Spell("Blizzard", 60, Element.WATER);
        assertEquals(BattleLogic.fight(spell1,spell2),spell2);
        assertEquals(BattleLogic.fight(spell2,spell1),spell2);
    }


    @org.junit.jupiter.api.Test
    void testFightSpellVsMonster() {
        Spell spell1 = new Spell("Inferno", 90, Element.FIRE);
        Monster mon1 = new Monster("Ice Dragon", 80, Element.WATER, Speciality.DRAGON);
        assertEquals(BattleLogic.fight(spell1,mon1),mon1);
        assertEquals(BattleLogic.fight(mon1,spell1),mon1);
    }

    @org.junit.jupiter.api.Test
    void testFightSpellVsMonster1() {
        Spell spell1 = new Spell("Fire Bolt", 25, Element.FIRE);
        Monster mon1 = new Monster("Goblin Warlord", 40, Element.NORMAL, Speciality.GOBLIN);
        assertEquals(BattleLogic.fight(spell1,mon1),spell1);
        assertEquals(BattleLogic.fight(mon1,spell1),spell1);
    }

    @org.junit.jupiter.api.Test
    void testFightDraw() {
        Spell spell1 = new Spell("Wind Blast", 20, Element.NORMAL);
        Monster mon1 = new Monster("Knight in Training", 20, Element.NORMAL, Speciality.KNIGHT);
        assertNull(BattleLogic.fight(spell1,mon1));
        assertNull(BattleLogic.fight(mon1,spell1));
    }

    @org.junit.jupiter.api.Test
    void testFightDraw1() {
        Spell spell1 = new Spell("Fire Bolt", 40, Element.FIRE);
        Monster mon1 = new Monster("Marine Goblin", 10, Element.WATER, Speciality.GOBLIN);
        assertNull(BattleLogic.fight(spell1,mon1));
        assertNull(BattleLogic.fight(mon1,spell1));
    }

    @org.junit.jupiter.api.Test
    void testFightDraw2() {
        Monster mon1 = new Monster("Goblin Sea Hag", 40, Element.WATER, Speciality.GOBLIN);
        Monster mon2 = new Monster("Lava Ork", 40, Element.FIRE, Speciality.ORK);
        assertNull(BattleLogic.fight(mon2,mon1));
        assertNull(BattleLogic.fight(mon1,mon2));
    }

    @org.junit.jupiter.api.Test
    void getEffectivenessModifierTest() {
        Spell spellFire = new Spell("Fire Bolt", 25, Element.FIRE);
        Spell spellWater = new Spell("Water Spray", 25, Element.WATER);
        Spell spellNormal = new Spell("Wind Blast", 25, Element.NORMAL);
        Spell spellIce = new Spell("Snowball", 25, Element.ICE);
        Spell spellEarth = new Spell("Rock Toss", 25, Element.EARTH);

        assertEquals(BattleLogic.getEffectivenessModifier(spellFire, spellWater),BattleLogic.getNotEffectiveModifier());
        assertEquals(BattleLogic.getEffectivenessModifier(spellFire, spellEarth),BattleLogic.getNotEffectiveModifier());
        assertEquals(BattleLogic.getEffectivenessModifier(spellFire, spellFire),BattleLogic.getNoEffectModifier());
        assertEquals(BattleLogic.getEffectivenessModifier(spellFire, spellNormal),BattleLogic.getEffectiveModifier());
        assertEquals(BattleLogic.getEffectivenessModifier(spellFire, spellIce),BattleLogic.getEffectiveModifier());

        assertEquals(BattleLogic.getEffectivenessModifier(spellWater, spellWater),BattleLogic.getNoEffectModifier());
        assertEquals(BattleLogic.getEffectivenessModifier(spellWater, spellFire),BattleLogic.getEffectiveModifier());
        assertEquals(BattleLogic.getEffectivenessModifier(spellWater, spellEarth),BattleLogic.getEffectiveModifier());
        assertEquals(BattleLogic.getEffectivenessModifier(spellWater, spellNormal),BattleLogic.getNotEffectiveModifier());
        assertEquals(BattleLogic.getEffectivenessModifier(spellWater, spellIce),BattleLogic.getNotEffectiveModifier());

        assertEquals(BattleLogic.getEffectivenessModifier(spellNormal, spellWater),BattleLogic.getEffectiveModifier());
        assertEquals(BattleLogic.getEffectivenessModifier(spellNormal, spellEarth),BattleLogic.getEffectiveModifier());
        assertEquals(BattleLogic.getEffectivenessModifier(spellNormal, spellFire),BattleLogic.getNotEffectiveModifier());
        assertEquals(BattleLogic.getEffectivenessModifier(spellNormal, spellIce),BattleLogic.getNotEffectiveModifier());
        assertEquals(BattleLogic.getEffectivenessModifier(spellNormal, spellNormal),BattleLogic.getNoEffectModifier());

        assertEquals(BattleLogic.getEffectivenessModifier(spellIce, spellIce),BattleLogic.getNoEffectModifier());
        assertEquals(BattleLogic.getEffectivenessModifier(spellIce, spellNormal),BattleLogic.getEffectiveModifier());
        assertEquals(BattleLogic.getEffectivenessModifier(spellIce, spellWater),BattleLogic.getEffectiveModifier());
        assertEquals(BattleLogic.getEffectivenessModifier(spellIce, spellEarth),BattleLogic.getNotEffectiveModifier());
        assertEquals(BattleLogic.getEffectivenessModifier(spellIce, spellFire),BattleLogic.getNotEffectiveModifier());

        assertEquals(BattleLogic.getEffectivenessModifier(spellEarth, spellEarth),BattleLogic.getNoEffectModifier());
        assertEquals(BattleLogic.getEffectivenessModifier(spellEarth, spellFire),BattleLogic.getEffectiveModifier());
        assertEquals(BattleLogic.getEffectivenessModifier(spellEarth, spellIce),BattleLogic.getEffectiveModifier());
        assertEquals(BattleLogic.getEffectivenessModifier(spellEarth, spellWater),BattleLogic.getNotEffectiveModifier());
        assertEquals(BattleLogic.getEffectivenessModifier(spellEarth, spellNormal),BattleLogic.getNotEffectiveModifier());

    }
}