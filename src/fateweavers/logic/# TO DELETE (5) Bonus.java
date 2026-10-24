package fateweavers.logic;

/**
 * A boost to one stat: a percentage (+10% magic damage) or, for flat stats, whole points (+1 shield layer).
 * 'stationOnly' bonuses only count while the person is on the room's station (e.g. a Mage's shield layer).
 */
public record Bonus(StatType stat, float amount, boolean stationOnly) {

    public Bonus(StatType stat, float amount) { this(stat, amount, false); }

    /** "+10% magic damage", "-10% weapon cooldown", "+1 shield layer", "+2 shield layers" */
    public String describe() {
        String num = amount == Math.rint(amount) ? String.valueOf((int) amount) : String.valueOf(amount);
        String sign = amount >= 0 ? "+" : "";
        if (stat.flat) {
            boolean plural = Math.abs(amount) != 1 && !stat.label.endsWith("protection");
            return sign + num + " " + stat.label + (plural ? "s" : "");
        }
        return sign + num + "% " + stat.label;
    }
}
