package fateweavers.logic;

/**
 * The base Shield Conduit (the Glass Orb). Shields are reactive: the person on the Shield Room's station
 * raises the shield just before an enemy shot reaches the shield bubble.
 *
 *   Raised in the last PARRY_WINDOW s before contact   -> PARRY: the shot is destroyed and a wave of
 *                                                         force sends double damage back to the attacker.
 *   Raised in the last BLOCK_WINDOW s before contact   -> BLOCK: the shot is stopped, one layer breaks.
 *   Not up at contact                                  -> the shot gets through and damages the room.
 *   Raised with no shot arriving in time               -> MISS: MISS_COOLDOWN s before it can be raised again.
 *
 * Layers: BASE_LAYERS, +1 if the station operator is a Magic User. Broken layers recharge one at a time.
 * Turning the power on starts with no layers; they charge up. Turning it off loses them (like FTL).
 */
public class ShieldConduit {
    public enum State { READY, UP, COOLDOWN }

    public static final int BASE_LAYERS = 1;
    public static final float PARRY_WINDOW = 0.15f;
    public static final float BLOCK_WINDOW = 0.6f;     // also how long the shield stays up
    public static final float MISS_COOLDOWN = 1.5f;
    public static final float LAYER_RECHARGE_SECONDS = 8f;
    public static final float PARRY_MULTIPLIER = 2f;

    public final String name = "Glass Orb";
    public final Room room;
    public final int powerCost = 1;
    public final Pos orb;              // where the orb sits in the room, world space

    boolean powered = false;
    State state = State.READY;
    float stateTime = 0f;              // UP: seconds since raised. COOLDOWN: seconds left.
    int layers = 0;
    float recharge = 0f;               // 0..1 progress on the next layer
    boolean queued = false;            // armed while paused; raises the instant you unpause

    ShieldConduit(Room room) {
        this.room = room;
        this.orb = room.devicePos != null   // resting in its pedestal's cup
                ? new Pos(room.devicePos.x(), room.devicePos.y() - Fortress.ORB_ABOVE_FOOT)
                : new Pos(room.bounds.x() + room.bounds.w() / 2f, room.bounds.y() + room.bounds.h() / 2f - 6);
    }

    public boolean isPowered() { return powered; }
    public State state() { return state; }
    public boolean isUp() { return state == State.UP; }
    public boolean isQueued() { return queued; }
    public int layers() { return layers; }
    public float rechargeProgress() { return recharge; }
    /** 0..1 through the miss cooldown (1 = done). */
    public float cooldownProgress() { return state == State.COOLDOWN ? 1f - stateTime / MISS_COOLDOWN : 1f; }
    /** 0..1 through the time the shield stays up. */
    public float upProgress() { return state == State.UP ? stateTime / BLOCK_WINDOW : 0f; }
}
