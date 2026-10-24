package fateweavers.logic;

/**
 * The enemy Bandit Camp: a green column of three 2x2 rooms plus an entrance bordering No Man's Land.
 *   Top     the Shield Conduit: an open orb on a pedestal. Blocks your Fire Gem shots, one layer each
 *           (base 1 layer, plus extra layers from the Psyche of the bandit on the station).
 *   Middle  the crew quarters.
 *   Bottom  a wooden archer tower with a ballista. Its bolts ignore shields completely. The camp has
 *           MAX_BOLTS; when they run out, the bandits board your fortress.
 * When every bandit is dead the camp is cleared, and a fresh one moves in after RESET_SECONDS.
 * The rules live in Fortress (updateCamp, updateBandits); this class holds the camp's state.
 */
public class BanditCamp {
    public enum Phase {
        HOLDING("Holding the camp"), BOARDING("Boarding your fortress"), CLEARED("Cleared");
        public final String label;
        Phase(String label) { this.label = label; }
    }

    public static final int MAX_BOLTS = 10;
    public static final float BALLISTA_INTERVAL = 7f;
    public static final float WINDUP_SECONDS = 1.0f;
    public static final float FIRST_SHOT_DELAY = 5f;
    public static final float RESET_SECONDS = 8f;
    public static final int BASE_LAYERS = 1;
    public static final float LAYER_RECHARGE_SECONDS = 8f;

    public final Room shieldRoom, quarters, tower, entrance;
    public final Pos orb;        // the orb on its pedestal
    public final Pos ballista;   // where the bolts leave

    int bolts = MAX_BOLTS;
    float fireTimer = FIRST_SHOT_DELAY;
    float windup = -1f;          // >= 0 while the ballista is being wound
    Room aim = null;             // the room it's about to shoot
    Phase phase = Phase.HOLDING;
    float resetTimer = 0f;
    int layers = BASE_LAYERS;
    float recharge = 0f;
    int cleared = 0;             // camps cleared so far
    CrewMember gunner = null;    // the bandit whose job is the ballista (the GUNNER bandit, or whoever takes over)
    float unmannedTime = 0f;     // seconds with bolts left and no bandit free to man the ballista
    /** With bolts left but nobody free to man the ballista this long, the bandits board instead. */
    public static final float UNMANNED_BOARD_SECONDS = 3f;

    BanditCamp(Room shieldRoom, Room quarters, Room tower, Room entrance) {
        this.shieldRoom = shieldRoom;
        this.quarters = quarters;
        this.tower = tower;
        this.entrance = entrance;
        // both stand on their station tile (Room.placeDevice): the orb in a pedestal, the ballista on a mount
        this.orb = new Pos(shieldRoom.devicePos.x(), shieldRoom.devicePos.y() - Fortress.ORB_ABOVE_FOOT);
        this.ballista = new Pos(tower.devicePos.x() - 14, tower.devicePos.y() + 4 - 22 * Fortress.DEVICE_SCALE);   // mount faces left, at you
    }

    public int bolts() { return bolts; }
    public int cleared() { return cleared; }
    public CrewMember gunner() { return gunner; }
    public Phase phase() { return phase; }
    public Room aim() { return aim; }
    public int layers() { return layers; }
    public float rechargeProgress() { return recharge; }
    public float resetIn() { return resetTimer; }
    public int campsCleared() { return cleared; }
    /** 0..1 while winding up a shot, otherwise -1. */
    public float windupProgress() { return windup < 0 ? -1f : windup / WINDUP_SECONDS; }
    /** Seconds until the next wind-up starts. */
    public float nextShotIn() { return fireTimer; }
    public boolean contains(Room r) { return r != null && r.side == Room.Side.CAMP; }
}
