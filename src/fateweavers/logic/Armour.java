package fateweavers.logic;

import java.util.EnumMap;
import java.util.Map;

/**
 * Body armour: multiplies damage taken by damage type. Bandits only for now (Brute plate, Archer leather,
 * Mage mana); crew get an armour slot with the P4 item slots. PLACEHOLDER numbers.
 *
 *            Slash  Pierce  Blunt  Magic  Mental
 *  Plate     0.5    0.75    1.0    1.25   1.0
 *  Leather   0.75   1.0     0.75   1.0    1.0
 *  Mana      1.25   1.25    1.25   0.5    0.5
 */
public enum Armour {
    NONE("None", 1f, 1f, 1f, 1f, 1f),
    PLATE("Plate", 0.5f, 0.75f, 1f, 1.25f, 1f),
    LEATHER("Leather", 0.75f, 1f, 0.75f, 1f, 1f),
    MANA("Mana", 1.25f, 1.25f, 1.25f, 0.5f, 0.5f);

    public final String label;
    private final Map<DamageType, Float> taken = new EnumMap<>(DamageType.class);

    Armour(String label, float slash, float pierce, float blunt, float magic, float mental) {
        this.label = label;
        taken.put(DamageType.SLASHING, slash);
        taken.put(DamageType.PIERCING, pierce);
        taken.put(DamageType.BLUDGEONING, blunt);
        taken.put(DamageType.MAGIC, magic);
        taken.put(DamageType.MENTAL, mental);
    }

    /** Damage multiplier for a hit of this type (1 = no change). */
    public float taken(DamageType t) { return t == null ? 1f : taken.getOrDefault(t, 1f); }
}
