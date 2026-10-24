package fateweavers.logic;

/**
 * Attunements: what a class is tuned to. Each one has:
 *  - an element it empowers: using a weapon of that element gives +10% damage and a special effect (Fire: burn);
 *  - one resistance and one weakness (take 50% less / 50% more damage of that element);
 *  - later, non-combat uses on the Loom map (Fire burns a wooden barrier, an Esotericist can talk to a scholar).
 * NEUTRAL is the Unclassed and Tier 1 attunement: it does nothing (post-game easter egg only).
 *
 * PLACEHOLDER resist/weak pairs: change them freely here.
 */
public enum Aspect {
    NEUTRAL("Neutral", null, null, null),
    FIRE("Fire", Element.FIRE, Element.FIRE, Element.WATER),
    ICE("Ice", Element.ICE, Element.ICE, Element.FIRE),
    WATER("Water", Element.WATER, Element.FIRE, Element.LIGHTNING),
    EARTH("Earth", Element.EARTH, Element.LIGHTNING, Element.WATER),
    AIR("Air", Element.AIR, Element.EARTH, Element.LIGHTNING),
    LIGHTNING("Lightning", Element.LIGHTNING, Element.LIGHTNING, Element.EARTH),
    DARK("Dark", Element.DARK, Element.DARK, Element.HOLY),
    HOLY("Holy", Element.HOLY, Element.HOLY, Element.DARK),
    BLOOD("Blood", Element.DARK, Element.POISON, Element.HOLY),
    POISON("Poison", Element.POISON, Element.POISON, Element.FIRE),
    KNOWLEDGE("Knowledge", Element.ARCANE, Element.ARCANE, Element.DARK),
    ESOTERIC("Esotericism", Element.ARCANE, Element.DARK, Element.HOLY);

    public final String label;
    public final Element empowers, resists, weakTo;

    Aspect(String label, Element empowers, Element resists, Element weakTo) {
        this.label = label;
        this.empowers = empowers;
        this.resists = resists;
        this.weakTo = weakTo;
    }
}
