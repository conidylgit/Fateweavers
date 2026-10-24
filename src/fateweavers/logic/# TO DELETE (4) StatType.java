package fateweavers.logic;

/**
 * Stats that crew can contribute. Placeholder for the full stat system
 * (rooms and weapons will scale off specific stats, Deadlock-style). Add new stats here.
 * A 'flat' stat adds whole points ("+1 shield layer"); the rest are percentages ("+10% magic damage").
 */
public enum StatType {
    MAGIC_DAMAGE("magic damage", false),
    WEAVING_SPEED("weaving speed", false),
    WEAPON_COOLDOWN("weapon cooldown", false),
    SHIELD_LAYERS("shield layer", true),
    DOOR_PROTECTION("door protection", true),
    MOVE_SPEED("move speed", false),
    REPAIR_SPEED("repair speed", false),
    MAX_HEALTH("max health", false);

    public final String label;
    public final boolean flat;
    StatType(String label, boolean flat) { this.label = label; this.flat = flat; }
}
