package fateweavers.logic;

/**
 * A bandit's job in the camp, separate from its class (the starting Bandit-Wannabes have no class yet).
 * SHIELD mans the camp shield, GUNNER the ballista, GUARD defends the camp and leads the boarding party.
 */
public enum BanditRole {
    SHIELD("Mans the camp shield: Psyche adds layers while on the station."),
    GUNNER("Mans the ballista. Its bolts ignore your shield."),
    GUARD("Guards the camp and leads the boarding party.");

    public final String job;
    BanditRole(String job) { this.job = job; }
}
