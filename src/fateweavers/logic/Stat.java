package fateweavers.logic;

/**
 * The nine stats every blob has, in the fixed order that also breaks every tie (earlier wins the green arrow). Values run 0 to Progression.MAX_STAT (80, with room to 100 at Tier 5).
 * Practice raises them directly (see Fortress.updateTraining); what each one does lives in StatRules.
 * Luck is shared: the whole crew gains it together when something super lucky happens.
 */
public enum Stat {
    STRENGTH("Strength", "STR", "Melee damage, gate strength, breaking doors",
            "fighting"),
    VITALITY("Vitality", "VIT", "Max health, healing speed",
            "resting or healing in the Chrysalis Ward and surviving hits"),
    DEXTERITY("Dexterity", "DEX", "Walk speed, weapon load speed",
            "operating the Weapons Tower"),
    ARCANA("Arcana", "ARC", "Magic weapon damage, Loom speed, shield recharge speed",
            "operating the Loom or the Shield"),
    PSYCHE("Psyche", "PSY", "Raw mental force: the number of shield bars (+1 per tier reached: 5, 20, 40, 60, 80, 100)",
            "not decided yet (free points and class level bonuses only)"),
    KNOWLEDGE("Knowledge", "KNO", "Repair speed, learning speed",
            "repairing damaged rooms"),
    RESOLVE("Resolve", "RES", "Holding doors",
            "taking damage and spending time in No Man's Land"),
    CHARISMA("Charisma", "CHA", "Speeds up everyone else working in the same room",
            "raising morale or lowering Madness (not in the game yet)"),
    LUCK("Luck", "LCK", "Critical hits, dodging",
            "the whole crew gains it when something super lucky happens");

    public final String label, shortLabel, does, howToTrain;

    Stat(String label, String shortLabel, String does, String howToTrain) {
        this.label = label;
        this.shortLabel = shortLabel;
        this.does = does;
        this.howToTrain = howToTrain;
    }

    public static final Stat[] ALL = values();
    public static final int COUNT = ALL.length;

    /** The eight stats a blob trains for itself (everything but the shared Luck), in stat order. Learning arrows use only these. */
    public static final Stat[] PERSONAL = {STRENGTH, VITALITY, DEXTERITY, ARCANA, PSYCHE, KNOWLEDGE, RESOLVE, CHARISMA};
}
