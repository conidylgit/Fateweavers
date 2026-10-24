package fateweavers.logic;

/**
 * The five class paths plus the neutral root. Each path owns one equal 72-degree wedge of the
 * class tree, centred on wedgeCentreDegrees (0 = right, 90 = down, -90 = up, like the screen).
 */
public enum ClassPath {
    NEUTRAL("Unclassed", 0),
    HEAVY_MELEE("Heavy Melee", -90),  // top
    MAGIC("Magic", -18),              // top-right
    RANGED("Ranged", 54),             // bottom-right
    SUPPORTER("Supporter", 126),      // bottom-left
    LIGHT_MELEE("Light Melee", 198);  // top-left

    public final String title;
    public final double wedgeCentreDegrees;

    ClassPath(String title, double wedgeCentreDegrees) {
        this.title = title;
        this.wedgeCentreDegrees = wedgeCentreDegrees;
    }

    /** The five real paths, clockwise from the top. */
    public static final ClassPath[] WEDGES = {HEAVY_MELEE, MAGIC, RANGED, SUPPORTER, LIGHT_MELEE};
}
