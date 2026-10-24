package fateweavers.logic;

/**
 * A door in the wall shared by two neighbouring rooms.
 *   OPEN    stays open (Aether flows through, and anyone can walk through).
 *   CLOSED  shut, but opens automatically for its own side. Enemies have to break it.
 * Doors have health: units blocked by a door that won't open for them hit it until it breaks.
 * A broken door stays open until its own side repairs it (by standing in one of its rooms).
 * Protection level starts at 1 and each Heavy Melee of the door's side standing in either of its rooms
 * adds a level; every level doubles the door's health.
 *
 * The gate in the middle of No Man's Land is a door with no fixed owner: it belongs to whichever side
 * has the higher strength total standing in No Man's Land (see Fortress.gateController).
 */
public class Door {
    public enum Mode {
        OPEN("Open", "Stays open. Anyone can walk through, and Aether flows between the two rooms."),
        CLOSED("Closed", "Opens for its own side as they walk through, but not for enemies, who have to break it.");

        public final String label, help;
        Mode(String label, String help) { this.label = label; this.help = help; }
    }

    public static final float HALF_LENGTH = 16f;
    public static final float BASE_HEALTH = 10f;    // at protection level 1
    public static final float GATE_HEALTH_MULTIPLIER = 3f;
    public static final float REPAIR_SECONDS = 8f;  // from broken to whole, with someone of its side nearby

    public final Room roomA, roomB;
    public final boolean verticalWall; // true = the wall runs up/down (rooms side by side)
    public final Pos center;
    public final Box hitbox;
    /** The side the door opens for (null for the gate, which goes to whoever controls No Man's Land). */
    public final Faction owner;
    public final boolean gate;
    Mode mode = Mode.CLOSED;
    float openAmount = 0f;     // 0 = closed, 1 = fully open
    float healthFraction = 1f; // kept as a fraction so it scales when protection changes
    boolean broken = false;
    boolean underAttack = false; // set each frame while someone is hitting it

    /** A door in the middle of the wall the two rooms share. */
    Door(Room a, Room b, boolean gate) {
        this.roomA = a;
        this.roomB = b;
        this.gate = gate;
        Faction o = a.side.owner != null ? a.side.owner : b.side.owner;
        this.owner = gate ? null : o;
        Box ba = a.bounds, bb = b.bounds;
        float ax2 = ba.x() + ba.w(), ay2 = ba.y() + ba.h(), bx2 = bb.x() + bb.w(), by2 = bb.y() + bb.h();
        if (ax2 == bb.x() || bx2 == ba.x()) {           // side by side
            verticalWall = true;
            float x = ax2 == bb.x() ? ax2 : bx2;
            float y0 = Math.max(ba.y(), bb.y()), y1 = Math.min(ay2, by2);
            center = new Pos(x, (y0 + y1) / 2f);
        } else {                                           // one above the other
            verticalWall = false;
            float y = ay2 == bb.y() ? ay2 : by2;
            float x0 = Math.max(ba.x(), bb.x()), x1 = Math.min(ax2, bx2);
            center = new Pos((x0 + x1) / 2f, y);
        }
        this.hitbox = verticalWall
                ? new Box(center.x() - 8, center.y() - 20, 16, 40)
                : new Box(center.x() - 20, center.y() - 8, 40, 16);
    }

    public Mode mode() { return mode; }
    public float openAmount() { return openAmount; }
    public boolean isOpen() { return openAmount > 0.5f; }
    public float healthFraction() { return healthFraction; }
    public boolean isBroken() { return broken; }
    public boolean isUnderAttack() { return underAttack; }

    /** Max health at a protection level: 10, 20, 40, 80... */
    public static float maxHealthAt(int level) {
        return BASE_HEALTH * (float) Math.pow(2, Math.max(0, level - 1));
    }

    public Room neighborOf(Room r) {
        if (r == roomA) return roomB;
        if (r == roomB) return roomA;
        return null;
    }
}
