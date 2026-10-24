package fateweavers.logic;

/**
 * A shot in flight from one of your weapons towards a camp room. Progress runs 0 to 1.
 * It flies in two legs: first to the edge of the camp's shield bubble ('checkShield'), where an intact
 * shield layer stops it; if it gets through, a second leg carries it on to the room.
 */
public class Projectile {
    private static final float SPEED = 420f; // world units per second

    public final Attunement tint;        // colour of the shot (null = neutral)
    public final Hit hit;               // what it does to every unit in the room it lands in
    public final Pos from, to;
    public final Room target;
    final float damage;
    final boolean checkShield;   // true = this leg ends at the camp shield
    final float burnPower;       // > 0: fired by someone Fire-attuned, sets whoever it hits burning (x this)
    private final float duration;
    float progress = 0f;

    Projectile(Hit hit, Pos from, Pos to, float damage, Room target, boolean checkShield, float burnPower) {
        this.burnPower = burnPower;
        this.hit = hit;
        this.tint = hit.tint();
        this.from = from;
        this.to = to;
        this.damage = damage;
        this.target = target;
        this.checkShield = checkShield;
        this.duration = Math.max(0.1f, to.distanceTo(from.x(), from.y()) / SPEED);
    }

    public float progress() { return progress; }

    /** Returns true when it reaches the end of this leg. */
    boolean update(float dt) {
        progress = Math.min(1f, progress + dt / duration);
        return progress >= 1f;
    }
}
