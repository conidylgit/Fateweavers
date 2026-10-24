package fateweavers.logic;

/**
 * How one stat of the person on a station boosts that device (the Deadlock-style part).
 * Each weapon lists three of these, e.g. the Fire Gem: Arcana +1% damage, Dexterity +0.5% load speed,
 * Luck +0.1% crit chance (all per point).
 *
 * perPoint is a fraction per stat point (0.01 = +1%). If 'step' > 0 the bonus comes in whole steps instead:
 * +perPoint for every 'step' points.
 * If 'tiered' is set, it's +perPoint for every tier threshold the stat has reached (5, 20, 40, 60, 80, 100),
 * e.g. +1 shield bar per Psyche tier.
 */
public record Scaling(Stat stat, float perPoint, int step, boolean tiered, String effect) {

    public static Scaling percent(Stat stat, float percentPerPoint, String effect) {
        return new Scaling(stat, percentPerPoint / 100f, 0, false, effect);
    }

    public static Scaling every(Stat stat, int step, float amount, String effect) {
        return new Scaling(stat, amount, step, false, effect);
    }

    public static Scaling perTier(Stat stat, float amount, String effect) {
        return new Scaling(stat, amount, 0, true, effect);
    }

    /** How many tier thresholds (Progression.LEVEL_CAP) a stat value has reached. */
    public static int tiersReached(int value) {
        int n = 0;
        for (int cap : Progression.LEVEL_CAP) if (value >= cap) n++;
        return n;
    }

    /** The bonus at a stat value: a fraction (0.12 = +12%) or, for stepped ones, a whole amount. */
    public float bonus(int value) {
        if (tiered) return tiersReached(value) * perPoint;
        return step > 0 ? (value / step) * perPoint : value * perPoint;
    }

    /** "+1% damage per point", "+1 shield layer per tier reached" */
    public String rule() {
        if (tiered) return "+" + fmt(perPoint) + " " + effect + " per tier reached";
        if (step > 0) return "+" + fmt(perPoint) + " " + effect + " per " + step;
        return "+" + fmt(perPoint * 100f) + "% " + effect + " per point";
    }

    /** "+12% damage", "+1 shield layer" */
    public String describe(int value) {
        float b = bonus(value);
        if (step > 0 || tiered) return "+" + fmt(b) + " " + effect + (Math.abs(b) == 1f ? "" : "s");
        return "+" + fmt(b * 100f) + "% " + effect;
    }

    static String fmt(float v) {
        if (Math.abs(v - Math.round(v)) < 0.001f) return String.valueOf(Math.round(v));
        return String.format(java.util.Locale.ROOT, "%.1f", v);
    }
}
