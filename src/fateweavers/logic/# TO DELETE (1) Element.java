package fateweavers.logic;

/**
 * Damage elements. Each gets its own accent colour in the drawing code (fire = red, arcane = blue-purple);
 * elements without a colour yet fall back to arcane. Attunements (Aspect) resist, fear and empower these.
 */
public enum Element {
    FIRE("Fire"),
    ARCANE("Arcane"),   // shields and the force a parry sends back
    ICE("Ice"),
    WATER("Water"),
    EARTH("Earth"),
    AIR("Air"),
    LIGHTNING("Lightning"),
    DARK("Dark"),
    HOLY("Holy"),
    POISON("Poison");

    public final String label;
    Element(String label) { this.label = label; }
}
