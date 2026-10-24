package fateweavers.logic;

/** A point in fortress (world) space. Y grows downward, like the screen. */
public record Pos(float x, float y) {

    public float distanceTo(float ox, float oy) {
        return (float) Math.hypot(x - ox, y - oy);
    }
}
