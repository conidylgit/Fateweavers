package fateweavers.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * One class in the tree, or an empty Tier 5 slot that hasn't been designed yet (placeholder).
 * The layout fields (angle, radius, x, y) are filled in by ClassTree and are in tree space:
 * the root sits at (0, 0) and y grows downward, like the screen.
 */
public final class ClassNode {
    public final String id;
    public final String name;          // "" for placeholders
    public final int tier;
    public final ClassPath path;
    public final ClassNode parent;     // null for the root
    public final String status;        // "locked", "check" or "empty"
    public final boolean placeholder;
    final List<ClassNode> children = new ArrayList<>();

    // Layout (tree space)
    double angle;      // radians, centre of this node's slice
    double span;       // radians, width of this node's slice
    double radius;
    float x, y;

    ClassNode(String id, String name, int tier, ClassPath path, ClassNode parent, String status, boolean placeholder) {
        this.id = id;
        this.name = name;
        this.tier = tier;
        this.path = path;
        this.parent = parent;
        this.status = status;
        this.placeholder = placeholder;
    }

    public List<ClassNode> children() { return Collections.unmodifiableList(children); }
    public double angle() { return angle; }
    public double span() { return span; }
    public double radius() { return radius; }
    public float x() { return x; }
    public float y() { return y; }

    public boolean needsCheck() { return "check".equals(status); }

    /** Display name; placeholders read "Unnamed slot". */
    public String displayName() { return placeholder ? "Unnamed slot" : name; }

    /** This node first, then its parent, up to the root. */
    public List<ClassNode> routeToRoot() {
        List<ClassNode> route = new ArrayList<>();
        for (ClassNode n = this; n != null; n = n.parent) route.add(n);
        return route;
    }
}
