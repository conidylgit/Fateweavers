package fateweavers.logic;

import java.util.List;

/**
 * One hit on a unit. Every unit damage goes through Fortress.applyHit(target, hit), so armour, resistances,
 * attunement effects and (in Phase 3) shields all read the same thing.
 *
 * @param amount      damage before armour and resistances
 * @param type        bludgeoning / piercing / slashing / magic / mental
 * @param attunements what the hit carries (empty = neutral)
 * @param source      who dealt it (null for devices, burning, the environment)
 * @param label       short "by what" text for the death message ("the Fire Gem", "burning")
 */
public record Hit(float amount, DamageType type, List<Attunement> attunements, CrewMember source, String label) {
    public Hit {
        attunements = attunements == null ? List.of() : List.copyOf(attunements);
    }

    /** The first attunement it carries, for colouring the damage number (null = neutral). */
    public Attunement tint() {
        for (Attunement a : attunements) if (!a.isNeutral()) return a;
        return null;
    }
}
