package fateweavers.logic;

/** An axis-aligned rectangle in fortress (world) space. */
public record Box(float x, float y, float w, float h) {

    public boolean contains(float px, float py) {
        return px >= x && px < x + w && py >= y && py < y + h;
    }

    public float centerX() { return x + w / 2f; }
    public float centerY() { return y + h / 2f; }
}
