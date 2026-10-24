package fateweavers.logic;

/**
 * How strong a blob's attunement is. Rolled like a loot box when the blob gains the attunement
 * (Attunements.rollRarity). The class tier sets the HIGHEST rarity it can roll:
 * T0 Common, T1 Uncommon, T2 Rare, T3 Epic, T4 Legendary, T5 Mythical. One optional reroll per promotion.
 *
 * Power: every attunement effect is multiplied by 'multiplier' (+50% a step), e.g. burn 2 / 3 / 4 / 5 / 6 / 7 dps
 * and the matching-weapon damage bonus +10% / 15% / 20% / 25% / 30% / 35%.
 */
public enum Rarity {
    COMMON("Common"), UNCOMMON("Uncommon"), RARE("Rare"), EPIC("Epic"), LEGENDARY("Legendary"), MYTHICAL("Mythical");

    public static final float STEP = 0.5f;

    public final String label;
    Rarity(String label) { this.label = label; }

    public float multiplier() { return 1f + STEP * ordinal(); }

    /** The best rarity a class of this tier can roll. */
    public static Rarity capFor(int tier) {
        return values()[Math.max(0, Math.min(values().length - 1, tier))];
    }
}
