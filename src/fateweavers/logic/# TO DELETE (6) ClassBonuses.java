package fateweavers.logic;

import java.util.*;

/**
 * Which stats each class gives, and where.
 *
 * PLACEHOLDER: a simple lookup table until the real stat system exists
 * (classes will have stats, and rooms/weapons will have multipliers per stat).
 * Current test values (all easy to change here):
 *   Magic User   +10% magic damage in the Weapons Tower
 *                +1 shield layer in the Shield Room (only while on the station)
 *   Supporter    +15% weaving speed in the Loom, +100% repair speed everywhere
 *   Ranged User  -10% weapon cooldown in the Weapons Tower
 *   Heavy Melee  +1 door protection on the doors of whatever room he's in, +50% max health (bandits too)
 *   Light Melee  +20% move speed everywhere
 */
public final class ClassBonuses {
    private ClassBonuses() {}

    /** Room id meaning "whatever room they're in" (or personal, like move speed). */
    public static final String ANY_ROOM = "*";

    private static final Map<CrewClass, Map<String, List<Bonus>>> TABLE = new EnumMap<>(CrewClass.class);

    static {
        add(CrewClass.MAGIC, "WEAPONS", new Bonus(StatType.MAGIC_DAMAGE, 10));
        add(CrewClass.MAGIC, "SHIELD", new Bonus(StatType.SHIELD_LAYERS, 1, true));
        add(CrewClass.SUPPORTER, "LOOM", new Bonus(StatType.WEAVING_SPEED, 15));
        add(CrewClass.SUPPORTER, ANY_ROOM, new Bonus(StatType.REPAIR_SPEED, 100));
        add(CrewClass.RANGED, "WEAPONS", new Bonus(StatType.WEAPON_COOLDOWN, -10));
        add(CrewClass.HEAVY_MELEE, ANY_ROOM, new Bonus(StatType.DOOR_PROTECTION, 1));
        add(CrewClass.HEAVY_MELEE, ANY_ROOM, new Bonus(StatType.MAX_HEALTH, 50));
        add(CrewClass.LIGHT_MELEE, ANY_ROOM, new Bonus(StatType.MOVE_SPEED, 20));
    }

    private static void add(CrewClass cls, String roomId, Bonus bonus) {
        TABLE.computeIfAbsent(cls, k -> new HashMap<>())
             .computeIfAbsent(roomId, k -> new ArrayList<>())
             .add(bonus);
    }

    /** Bonuses a class gives while standing in a room: that room's own plus the 'any room' ones. */
    public static List<Bonus> forRoom(CrewClass cls, Room room) {
        Map<String, List<Bonus>> byRoom = TABLE.getOrDefault(cls, Map.of());
        List<Bonus> out = new ArrayList<>(byRoom.getOrDefault(room.id, List.of()));
        out.addAll(byRoom.getOrDefault(ANY_ROOM, List.of()));
        return out;
    }

    /** Every bonus a class has, grouped by room id (ANY_ROOM = everywhere). */
    public static Map<String, List<Bonus>> byRoom(CrewClass cls) {
        return Collections.unmodifiableMap(TABLE.getOrDefault(cls, Map.of()));
    }

    /** Bonuses a class has wherever it is. */
    public static List<Bonus> everywhere(CrewClass cls) {
        return TABLE.getOrDefault(cls, Map.of()).getOrDefault(ANY_ROOM, List.of());
    }

    /**
     * Melee damage per second in auto-fights (FTL-style: hostile units in the same room hit each other).
     * PLACEHOLDER values: Heavy and Light hit hardest, Ranged medium, Magic, Supporter and Unclassed weak.
     */
    public static float meleeDps(CrewClass cls) {
        return switch (cls) {
            case HEAVY_MELEE, LIGHT_MELEE -> 10f;
            case RANGED -> 7f;
            case UNCLASSED -> 5f;
            case MAGIC, SUPPORTER -> 4f;
        };
    }

    /** How much a unit counts for holding No Man's Land (the gate): Heavy 3, Light 2, everyone else 1. */
    public static int strength(CrewClass cls) {
        return switch (cls) {
            case HEAVY_MELEE -> 3;
            case LIGHT_MELEE -> 2;
            default -> 1;
        };
    }

    /** Total of one stat a class has everywhere (e.g. move speed %). */
    public static float everywhereTotal(CrewClass cls, StatType stat) {
        float total = 0;
        for (Bonus b : everywhere(cls)) if (b.stat() == stat) total += b.amount();
        return total;
    }
}
