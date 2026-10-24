package fateweavers.logic;

/**
 * The Fateweaver Loom. While powered and someone is on its station it weaves the two strings
 * together. When fully woven you can Sleep, which ends the day (and later moves you to the next node).
 * Losing power or the operator pauses the weaving; progress is kept.
 */
public class Loom {
    public final Room room;
    public final int powerCost = 1;
    public final float chargeSeconds = 20f;   // at normal speed

    boolean powered = false;
    float charge = 0f;                        // 0..1

    Loom(Room room) {
        this.room = room;
    }

    public boolean isPowered() { return powered; }
    public float charge() { return charge; }
    public boolean isReady() { return charge >= 1f; }
    public boolean isManned() { return room.stationOperator() != null; }
    /** True while the strings are actively being woven. */
    public boolean isWeaving() { return powered && isManned() && !isReady(); }
}
