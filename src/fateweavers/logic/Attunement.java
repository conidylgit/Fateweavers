package fateweavers.logic;

import java.util.List;

/**
 * Everything a class, a weapon or a hit can be tuned to. (Phase 2 merged the old Element and Aspect into this.)
 *
 * Three shapes:
 *  - plain:  FIRE, PIRATE, SLASHING ...
 *  - sub:    a "kind of" its parent (WATER under ICE, STONE under EARTH, STABBING under PIERCING). Only matches itself.
 *  - duo:    made of two others, Pokemon style (LAVA = FIRE + EARTH). A duo is one attunement with one points pool,
 *            but it matches through its two parts (see Attunements.match).
 *
 * Weapon-craft attunements (SLASHING, PIERCING, BLUDGEONING) also match any weapon of that damage type.
 * NEUTRAL is Unclassed / Tier 1: it does nothing.
 * Resist / weak pairs: only the old elemental ones (PLACEHOLDER); every new attunement has none for now.
 */
public enum Attunement {
    NEUTRAL("Neutral"),

    // ---- Elements
    FIRE("Fire"), ICE("Ice"), WATER("Water", "ICE"), EARTH("Earth"), STONE("Stone", "EARTH"), AIR("Air"),
    VOID("Void"),
    LIGHTNING("Lightning", "FIRE", "AIR"), LAVA("Lava", "FIRE", "EARTH"), SLOWNESS("Slowness", "ICE", "TIME"),
    METAL("Metal", "EARTH", "MACHINE"), SPEED("Speed", "AIR", "TIME"), SPACE("Space", "AIR", "VOID"),

    // ---- Dark and light
    DARK("Dark"), SHADOW("Shadow"), DEATH("Death"), SOUL("Soul", "DEATH"), NIGHT("Night"),
    HOLY("Holy"), LIGHT("Light"), RESTORATION("Restoration"), BLOOD("Blood"), POISON("Poison"),

    // ---- Magic and mind
    BALANCE("Balance"),   // the Elementalists: matches any elemental weapon at x1/2 (Attunements.match)
    ARCANE("Arcane"), KNOWLEDGE("Knowledge"), ESOTERIC("Esotericism"), MIND("Mind"), SPIRIT("Spirit"),
    FATE("Fate"), TIME("Time", "FATE"), CHAOS("Chaos"), LUCK("Luck", "CHAOS"),

    // ---- Nature and machines
    BEAST("Beast"), MACHINE("Machine"), GOO("Goo"),

    // ---- Weapon crafts (the first three match their damage type)
    SLASHING("Slashing"), BLADEDANCE("Bladedance", "SLASHING"), CLEAVING("Cleaving"),
    PIERCING("Piercing"), STABBING("Stabbing", "PIERCING"), IMPALING("Impaling", "PIERCING"),
    BLUDGEONING("Bludgeoning"), CRUSHING("Crushing", "BLUDGEONING"), PUGILISM("Pugilism", "BLUDGEONING"),
    DESTRUCTION("Destruction"), ARCHERY("Archery"), FIREARMS("Firearms"), EXPLOSIVES("Explosives", "FIREARMS"),
    THROWN("Thrown"),

    // ---- Families
    PIRATE("Pirate"), OUTLAW("Outlaw"), LAW("Law"), NORSE("Norse"), KI("Ki"), PERFORMER("Performer"),

    // ---- Body and fortress
    ARMOUR("Armour"), HEAVY("Heavy"), BASTION("Bastion");

    public final String label;
    private final String parentName, partA, partB;
    private Attunement parent;
    private List<Attunement> parts;
    /** PLACEHOLDER pairs (take 50% less / 50% more from hits carrying that attunement). Null = none. */
    public Attunement resists, weakTo;

    Attunement(String label) { this(label, null, null, null); }
    Attunement(String label, String parent) { this(label, parent, null, null); }
    Attunement(String label, String a, String b) { this(label, null, a, b); }
    private Attunement(String label, String parent, String a, String b) {
        this.label = label;
        this.parentName = parent;
        this.partA = a;
        this.partB = b;
    }

    static {
        for (Attunement t : values()) {
            t.parent = t.parentName == null ? null : valueOf(t.parentName);
            t.parts = t.partA == null ? List.of(t) : List.of(valueOf(t.partA), valueOf(t.partB));
        }
        pair(FIRE, FIRE, WATER);
        pair(ICE, ICE, FIRE);
        pair(WATER, FIRE, LIGHTNING);
        pair(EARTH, LIGHTNING, WATER);
        pair(AIR, EARTH, LIGHTNING);
        pair(LIGHTNING, LIGHTNING, EARTH);
        pair(DARK, DARK, HOLY);
        pair(HOLY, HOLY, DARK);
        pair(BLOOD, POISON, HOLY);
        pair(POISON, POISON, FIRE);
        pair(KNOWLEDGE, ARCANE, DARK);
        pair(ESOTERIC, DARK, HOLY);
    }

    private static void pair(Attunement a, Attunement resists, Attunement weakTo) {
        a.resists = resists;
        a.weakTo = weakTo;
    }

    /** The plain attunement this is a kind of (WATER -> ICE), or null. */
    public Attunement parent() { return parent; }
    /** What it matches through: a duo's two parts (LAVA -> FIRE, EARTH), otherwise just itself. */
    public List<Attunement> parts() { return parts; }
    public boolean isDuo() { return parts.size() == 2; }
    public boolean isNeutral() { return this == NEUTRAL; }

    /** The elements Balance matches (duos count through their parts: Lava has Fire and Earth). */
    public static final java.util.Set<Attunement> ELEMENTAL = java.util.EnumSet.of(FIRE, ICE, WATER, EARTH, STONE, AIR);

    /** The weapon-craft attunement a damage type counts as (SLASHING -> SLASHING), or null for magic/mental. */
    public static Attunement craftOf(DamageType t) {
        if (t == null) return null;
        return switch (t) {
            case SLASHING -> SLASHING;
            case PIERCING -> PIERCING;
            case BLUDGEONING -> BLUDGEONING;
            default -> null;
        };
    }
}
