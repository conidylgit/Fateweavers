package fateweavers.logic;

/**
 * Skills crew train by doing tasks. Every full consecutive second on one task gives +1 to that skill
 * and +1 overall XP. Each skill opens up the class path(s) that use it.
 */
public enum Skill {
    MARKSMANSHIP("Marksmanship", "operating the Weapons Tower"),
    ARCANA("Arcana", "operating the Loom or the Shield"),
    CRAFT("Craft", "repairing damaged rooms"),
    MELEE("Melee", "sparring in the Training Hall");

    public final String label, howToTrain;
    Skill(String label, String howToTrain) { this.label = label; this.howToTrain = howToTrain; }

    /** Which skill a class path is built on. */
    public static Skill forPath(ClassPath p) {
        return switch (p) {
            case RANGED -> MARKSMANSHIP;
            case MAGIC -> ARCANA;
            case SUPPORTER -> CRAFT;
            case LIGHT_MELEE, HEAVY_MELEE -> MELEE;
            case NEUTRAL -> null;
        };
    }
}
