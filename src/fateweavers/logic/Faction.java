package fateweavers.logic;

/** Who a unit fights for. Units of different factions in the same room fight automatically. */
public enum Faction {
    PLAYER("Your crew"),
    BANDITS("Bandits");

    public final String label;
    Faction(String label) { this.label = label; }

    public Faction other() { return this == PLAYER ? BANDITS : PLAYER; }
}
