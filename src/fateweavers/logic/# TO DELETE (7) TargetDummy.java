package fateweavers.logic;

/**
 * The training turret: a practice dummy with a cannon, standing in the enemy area.
 * You can shoot it, and depending on its mode it shoots back:
 *   PASSIVE    never fires (a plain dummy)
 *   NEUTRAL    fires back once each time you hit it
 *   AGGRESSIVE fires on its own every few seconds
 * Before every shot it winds up and picks a room to aim at, so you can see what's coming.
 * Respawns a few seconds after being destroyed.
 */
public class TargetDummy {
    public enum Mode {
        PASSIVE("Passive", "Never fires. A plain practice dummy."),
        NEUTRAL("Neutral", "Fires back once each time you hit it."),
        AGGRESSIVE("Aggressive", "Charges up and fires at a random room every few seconds.");

        public final String label, help;
        Mode(String label, String help) { this.label = label; this.help = help; }
    }

    public static final float MAX_HP = 20f;
    public static final float WINDUP_SECONDS = 0.8f;
    public static final float AGGRESSIVE_INTERVAL = 6f;
    public static final float RETALIATE_DELAY = 1.2f;
    public static final float SHOT_DAMAGE = 3f;   // used for the parry reflection; rooms lose 1 bar per hit
    private static final float RESPAWN_SECONDS = 4f;

    public final Pos pos;      // base of the post, in world space
    public final Box hitbox;
    public final Pos muzzle;   // end of the cannon, where its shots leave
    float hp = MAX_HP;
    float respawnTimer = 0f;
    Mode mode = Mode.PASSIVE;
    float fireTimer = 3f;      // aggressive: time to the next wind-up
    float windup = -1f;        // >= 0 while winding up a shot
    int retaliations = 0;      // neutral: shots owed
    Room aim = null;           // the room it's about to fire at (picked when the wind-up starts)

    TargetDummy(Pos pos) {
        this.pos = pos;
        this.hitbox = new Box(pos.x() - 32, pos.y() - 72, 64, 82);
        this.muzzle = new Pos(pos.x() - 38, pos.y() - 44);
    }

    public float hp() { return hp; }
    public boolean isAlive() { return hp > 0; }
    public float respawnIn() { return respawnTimer; }
    public Mode mode() { return mode; }
    public Room aim() { return aim; }
    /** 0..1 while winding up a shot, otherwise -1. */
    public float windupProgress() { return windup < 0 ? -1f : windup / WINDUP_SECONDS; }
    /** Seconds until the next aggressive wind-up starts (aggressive mode only). */
    public float nextShotIn() { return fireTimer; }

    /** Where your shots aim. */
    public Pos aimPoint() { return new Pos(pos.x(), pos.y() - 44); }

    /** Returns true if this hit destroyed it. */
    boolean hit(float damage) {
        if (!isAlive()) return false;
        hp = Math.max(0, hp - damage);
        if (mode == Mode.NEUTRAL && hp > 0) retaliations = Math.min(retaliations + 1, 3);
        if (hp == 0) {
            respawnTimer = RESPAWN_SECONDS;
            windup = -1f;
            aim = null;
            retaliations = 0;
            return true;
        }
        return false;
    }

    void setMode(Mode m) {
        mode = m;
        windup = -1f;
        aim = null;
        retaliations = 0;
        fireTimer = 2f;
    }

    /** True on the frame a wind-up begins (the fortress then picks a room to aim at). */
    boolean startedWindup = false;

    /** Advances timers. Returns true on the frame it fires. */
    boolean update(float dt) {
        startedWindup = false;
        if (!isAlive()) {
            respawnTimer -= dt;
            if (respawnTimer <= 0) { hp = MAX_HP; fireTimer = 3f; }
            return false;
        }
        if (windup >= 0) {
            windup += dt;
            if (windup >= WINDUP_SECONDS) { windup = -1f; return true; }
            return false;
        }
        switch (mode) {
            case AGGRESSIVE -> {
                fireTimer -= dt;
                if (fireTimer <= 0) { fireTimer = AGGRESSIVE_INTERVAL; windup = 0f; startedWindup = true; }
            }
            case NEUTRAL -> {
                if (retaliations > 0) {
                    fireTimer -= dt;
                    if (fireTimer <= 0) { retaliations--; windup = 0f; startedWindup = true; fireTimer = RETALIATE_DELAY; }
                } else {
                    fireTimer = RETALIATE_DELAY;
                }
            }
            case PASSIVE -> { }
        }
        return false;
    }
}
