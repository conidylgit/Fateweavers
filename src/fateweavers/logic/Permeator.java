package fateweavers.logic;

/**
 * The Permeator: fills the fortress with Aether (air permeated with mana), which the blobs need to live.
 * Like FTL's oxygen system it only needs power, not an operator.
 *
 * Aether is made in the Permeator's room and spreads room to room through doors (open doors fast,
 * shut doors only a trickle). If the Permeator is off or its room is too damaged it's DOWN: its own room
 * vents first and the rest slowly leak and drain towards it, so the fortress empties from the Permeator
 * room outward. Shutting doors slows that, like FTL's O2. A damaged Permeator restarts by itself once
 * its room is repaired (it stays switched on). PLACEHOLDER rates.
 */
public class Permeator {
    public static final float FILL_PER_SECOND = 1f / 12f;    // while running
    /** While down, the Permeator room vents this share of its Aether per second (less left = slower), so shut doors save Aether. */
    public static final float VENT_PER_SECOND = 0.12f;
    public static final float LEAK_PER_SECOND = 1f / 150f;   // every other room while down
    /** Share of the open-door flow that seeps through a shut door. */
    public static final float SHUT_DOOR_FLOW = 0.1f;

    public final String name = "Permeator";
    public final Room room;
    public final int powerCost = 1;
    boolean powered = true;   // starts running, like FTL's O2
    boolean wasRunning = true;

    Permeator(Room room) {
        this.room = room;
    }

    public boolean isPowered() { return powered; }
    public boolean isRunning() { return powered && room.health >= powerCost; }
}
