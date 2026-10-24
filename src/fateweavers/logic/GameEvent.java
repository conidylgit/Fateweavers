package fateweavers.logic;

/**
 * Something that happened during an update, for the drawing code to react to
 * (a little squash when someone starts walking, a message on screen, a damage number, and so on).
 * 'pos' is where it happened in world space, when that matters (hits, parries).
 */
public record GameEvent(Type type, CrewMember crew, String text, float value, Pos pos,
                        DamageType damageType, Attunement tint) {
    /** Most events don't carry a hit; damage numbers (UNIT_HIT, TARGET_HIT) do, for their colour and tooltip. */
    public GameEvent(Type type, CrewMember crew, String text, float value, Pos pos) {
        this(type, crew, text, value, pos, null, null);
    }

    public enum Type {
        FORTRESS_FALLEN, STARTED_WALKING, ARRIVED, CLASS_CHANGED, NOTICE, WEAPON_FIRED, TARGET_HIT, TARGET_DESTROYED,
        LOOM_READY, SLEPT,
        ENEMY_FIRED, SHIELD_ARMED, SHIELD_RAISED, SHIELD_PARRY, SHIELD_BLOCK, SHIELD_MISS, ROOM_HIT, ROOM_REPAIRED,
        CLASS_UNLOCKED, PROMOTED, CREW_DIED,
        FIRE_NOT_READY, ENEMY_SHIELD_BLOCK, ATTACK, UNIT_HIT, DOOR_BROKEN, BOARDING, CAMP_CLEARED, CAMP_RESET,
        GATE_CONTROL, ROOM_SABOTAGED,
        LEVEL_UP, STAT_UP, CRIT, DODGE, LUCKY
    }

    static GameEvent of(Type type, CrewMember crew) { return new GameEvent(type, crew, null, 0f, null); }
    static GameEvent notice(String text) { return new GameEvent(Type.NOTICE, null, text, 0f, null); }
    static GameEvent value(Type type, float value) { return new GameEvent(type, null, null, value, null); }
    static GameEvent at(Type type, Pos pos, float value) { return new GameEvent(type, null, null, value, pos); }
}
