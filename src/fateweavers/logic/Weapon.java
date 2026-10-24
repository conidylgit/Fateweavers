package fateweavers.logic;

/**
 * A fortress weapon mounted in a room. It charges while powered AND the room's station is manned.
 * Turning power off drains the charge (like FTL).
 */
public class Weapon {
    public final String name;
    /** What the weapon is tuned to (the Fire Gem: Fire). A blob with a matching attunement unlocks its effects. */
    public final java.util.List<Attunement> attunements;
    public final DamageType damageType;
    /** The three stats of the person on the station that boost it (see StatRules). */
    public final java.util.List<Scaling> scalings;
    public final int powerCost;
    public final float chargeSeconds;
    public final float baseDamage;
    public final Room room;
    public final Pos muzzle;            // where shots leave from, in world space

    boolean powered = false;
    boolean autoFire = false;
    float charge = 0f;                  // 0..1
    Room target;                        // the camp room it shoots at

    Weapon(String name, java.util.List<Attunement> attunements, DamageType damageType, java.util.List<Scaling> scalings, int powerCost, float chargeSeconds,
           float baseDamage, Room room, Pos muzzle) {
        this.name = name;
        this.attunements = java.util.List.copyOf(attunements);
        this.damageType = damageType;
        this.scalings = scalings;
        this.powerCost = powerCost;
        this.chargeSeconds = chargeSeconds;
        this.baseDamage = baseDamage;
        this.room = room;
        this.muzzle = muzzle;
    }

    public boolean isPowered() { return powered; }
    public boolean isAutoFire() { return autoFire; }
    public float charge() { return charge; }
    public boolean isCharged() { return charge >= 1f; }
    public Room target() { return target; }
    public boolean isManned() { return room.stationOperator() != null; }
    public CrewMember operator() { return room.stationOperator(); }
    /** The colour attunement (first one), or null for a neutral weapon. */
    public Attunement tint() { return attunements.isEmpty() ? null : attunements.get(0); }
}
