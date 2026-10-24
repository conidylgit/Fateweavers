package fateweavers.logic;

/**
 * What kind of hit it is. Separate from attunement: a hit has one type AND may carry attunements
 * (fire MAGIC, fire BLUDGEONING, esoteric BLUDGEONING = a scholar bonking someone with a book).
 * Add new types at the end; armour (Armour) and later shields read this.
 */
public enum DamageType {
    BLUDGEONING("Bludgeoning"),
    PIERCING("Piercing"),
    SLASHING("Slashing"),
    MAGIC("Magic"),
    MENTAL("Mental");

    public final String label;
    DamageType(String label) { this.label = label; }

    public boolean isPhysical() { return this == BLUDGEONING || this == PIERCING || this == SLASHING; }
}
