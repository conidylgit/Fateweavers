package fateweavers.logic;

/**
 * Levelling numbers in one place.
 *
 *  - Stats: practice raises a stat directly. The next point costs 25 XP more than the last
 *    (point 1 = 25, point 2 = 50 ... point 80 = 2000).
 *  - Total level: a separate XP bar. Level n costs 50 * n XP (50, 100, 150 ...).
 *    Every level gives (tier + 1) free points to spend anywhere, plus the class bonus:
 *    +2 to the class's strongest stat and +1 to its second strongest.
 *  - Tier caps: practice can't push a stat, and XP can't push the total level, past the tier's cap.
 *    Reaching the level cap unlocks the next classes. Free points and class bonuses ignore the cap.
 *  - Learning speed (from Tier 1 only; Tier 0 has no modifiers), added together:
 *      green arrow on the blob's highest stat +25% (ties: earlier stat in Stat order wins),
 *      red arrows on its 3 lowest: -50% / -25% / -10% (ties: the stat ranked weaker in the class ranking is lower),
 *      the class's strongest stat +25%.
 *    Luck never gets arrows. Knowledge then adds +1% per point. Test Mode (dev console) multiplies everything.
 */
public final class Progression {
    private Progression() {}

    /** Level (and practice) cap for each tier. Tier 5 gets its own band above 80. */
    public static final int[] LEVEL_CAP = {5, 20, 40, 60, 80, 100};
    /** Hard ceiling for any stat, free points included. */
    public static final int MAX_STAT = 100;
    public static final int STAT_XP_STEP = 25;
    public static final int LEVEL_XP_STEP = 45;   // was 50: reaching level 5 is ~10% faster
    /** Green arrow on the highest stat. */
    public static final float ARROW_UP = 0.25f;
    /** Red arrows on the lowest, 2nd lowest and 3rd lowest stats (these slow learning down). */
    public static final float[] ARROW_DOWN = {0.50f, 0.25f, 0.10f};
    /** Learning arrows and the class learning bonus start at this tier. */
    public static final int ARROWS_FROM_TIER = 1;
    public static final float KNOWLEDGE_LEARNING_PER_POINT = 0.01f;
    /** Test Mode learning speed (dev console). */
    public static final float TEST_MODE_MULTIPLIER = 20f;

    public static int levelCap(int tier) {
        return LEVEL_CAP[Math.max(0, Math.min(LEVEL_CAP.length - 1, tier))];
    }

    /** XP to go from 'value' to value + 1 in a stat. */
    public static float statXpFor(int value) { return STAT_XP_STEP * (value + 1f); }

    /** XP to go from 'level' to level + 1. */
    public static float levelXpFor(int level) { return LEVEL_XP_STEP * (level + 1f); }

    public static int freePointsPerLevel(int tier) { return tier + 1; }
}
