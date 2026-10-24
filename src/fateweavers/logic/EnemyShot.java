package fateweavers.logic;

/**
 * A shot at one of your rooms.
 *
 * Normal shots all take the same time to reach the shield bubble (APPROACH_SECONDS), so the timing is
 * learnable. At the bubble ('contact') the shield either parries or blocks it; otherwise it speeds on and
 * hits the room (IMPACT_SECONDS later).
 *
 * Shield-ignoring shots (the bandits' ballista bolts, neutral PIERCING) ignore shields completely: they fly straight from 'from'
 * to 'to' in BOLT_SECONDS and can't be blocked or parried. You can only take the hit and repair.
 */
public class EnemyShot {
    public static final float APPROACH_SECONDS = 1.6f;
    public static final float IMPACT_SECONDS = 0.3f;
    public static final float BOLT_SECONDS = 1.3f;

    public final Pos from, contact, to;
    public final Room target;
    /** Ignores shields completely (the ballista's bolts). Not the same as the PIERCING damage type. */
    public final boolean ignoresShields;
    public final DamageType damageType;
    public final java.util.List<Attunement> attunements;
    final float damage;
    float elapsed = 0f;
    boolean pastShield = false;

    EnemyShot(Pos from, Pos contact, Pos to, Room target, float damage, DamageType type, java.util.List<Attunement> attunements) {
        this(from, contact, to, target, damage, false, type, attunements);
    }

    EnemyShot(Pos from, Pos contact, Pos to, Room target, float damage, boolean ignoresShields,
              DamageType type, java.util.List<Attunement> attunements) {
        this.from = from;
        this.contact = contact;
        this.to = to;
        this.target = target;
        this.damage = damage;
        this.ignoresShields = ignoresShields;
        this.pastShield = ignoresShields;
        this.damageType = type;
        this.attunements = attunements == null ? java.util.List.of() : java.util.List.copyOf(attunements);
    }

    /** A ballista bolt: neutral PIERCING, straight to the room, ignoring shields. */
    static EnemyShot bolt(Pos from, Room target) {
        Pos to = target.center();
        return new EnemyShot(from, to, to, target, 0f, true, DamageType.PIERCING, java.util.List.of());
    }

    /** Seconds until it reaches the shield bubble (negative once past it). */
    public float timeToContact() { return APPROACH_SECONDS - elapsed; }
    public boolean isPastShield() { return pastShield; }
    /** 0..1 of the way to the room (shield-ignoring shots). */
    public float boltProgress() { return Math.min(1f, elapsed / BOLT_SECONDS); }

    boolean hasLanded() {
        return ignoresShields ? elapsed >= BOLT_SECONDS : pastShield && elapsed >= APPROACH_SECONDS + IMPACT_SECONDS;
    }

    public Pos position() {
        if (ignoresShields) return lerp(from, to, boltProgress());
        if (elapsed <= APPROACH_SECONDS) {
            float t = elapsed / APPROACH_SECONDS;
            return lerp(from, contact, t);
        }
        float t = Math.min(1f, (elapsed - APPROACH_SECONDS) / IMPACT_SECONDS);
        return lerp(contact, to, t);
    }

    private static Pos lerp(Pos a, Pos b, float t) {
        return new Pos(a.x() + (b.x() - a.x()) * t, a.y() + (b.y() - a.y()) * t);
    }
}
