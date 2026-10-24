package fateweavers.logic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The whole class tree (1 -> 5 -> 25 -> 100 -> 300 -> 600), loaded from
 * class_tree/fateweavers_class_tree.json, with every node given a place on a radial layout.
 *
 * Layout: the root is at (0, 0). Each tier is a ring further out (RING_RADIUS). Each path gets its
 * own 72-degree wedge, and each node's slice is split evenly between its children, so branches
 * never cross. Tier 4 classes with no Tier 5s yet get two empty placeholder slots.
 */
public final class ClassTree {
    public static final int MAX_TIER = 5;
    public static final int[] CHILDREN_PER_TIER = {5, 5, 4, 3, 2, 0};
    public static final double[] RING_RADIUS = {0, 220, 440, 680, 940, 1180};
    /** How much of its share each child's slice keeps; the rest becomes a gap between groups. */
    private static final double[] SLICE_KEEP = {1, 0.94, 0.92, 0.90, 0.85, 1.0};

    public static final String DEFAULT_FILE = "class_tree/fateweavers_class_tree.json";

    private final ClassNode root;
    private final List<ClassNode> nodes;            // root first, then tier by tier
    private final Map<String, ClassNode> byId;
    private final int designedCount, placeholderCount;

    private ClassTree(ClassNode root, List<ClassNode> nodes, Map<String, ClassNode> byId) {
        this.root = root;
        this.nodes = nodes;
        this.byId = byId;
        int p = 0;
        for (ClassNode n : nodes) if (n.placeholder) p++;
        this.placeholderCount = p;
        this.designedCount = nodes.size() - p;
    }

    public ClassNode root() { return root; }
    public List<ClassNode> nodes() { return Collections.unmodifiableList(nodes); }
    public ClassNode byId(String id) { return byId.get(id); }
    public int designedCount() { return designedCount; }
    public int placeholderCount() { return placeholderCount; }
    public double outerRadius() { return RING_RADIUS[MAX_TIER]; }

    /**
     * Where a crew member's class sits in the tree. Crew can only be Unclassed or one of the five
     * basic classes for now, which are the root and the Tier 1 classes.
     */
    public ClassNode nodeFor(CrewClass c) {
        if (c == CrewClass.UNCLASSED) return root;
        for (ClassNode n : root.children) if (n.path.name().equals(c.name())) return n;
        return root;
    }

    // =========================================================================
    // LOADING
    // =========================================================================
    public static ClassTree load(Path file) throws IOException {
        String text = Files.readString(file, StandardCharsets.UTF_8);
        return fromRecords(FlatJson.parseArray(text));
    }

    static ClassTree fromRecords(List<Map<String, Object>> records) {
        Map<String, ClassNode> byId = new HashMap<>();
        List<ClassNode> nodes = new ArrayList<>();
        ClassNode root = null;

        // The file lists parents before children, but don't rely on it: sort by tier first.
        List<Map<String, Object>> sorted = new ArrayList<>(records);
        sorted.sort((a, b) -> Integer.compare(intOf(a.get("tier")), intOf(b.get("tier"))));
        for (Map<String, Object> r : sorted) {
            String id = (String) r.get("id");
            String parentId = (String) r.get("parent_id");
            ClassNode parent = parentId == null ? null : byId.get(parentId);
            if (parentId != null && parent == null)
                throw new IllegalArgumentException("Class " + id + " has unknown parent " + parentId);
            ClassNode n = new ClassNode(id, (String) r.get("name"), intOf(r.get("tier")),
                    ClassPath.valueOf((String) r.get("path")), parent, (String) r.get("status"), false);
            if (parent == null) {
                if (root != null) throw new IllegalArgumentException("More than one root class");
                root = n;
            } else {
                parent.children.add(n);
            }
            byId.put(id, n);
            nodes.add(n);
        }
        if (root == null) throw new IllegalArgumentException("No root class");

        // Fill every missing child slot with an empty placeholder, so the tree is complete.
        List<ClassNode> added = new ArrayList<>();
        for (ClassNode n : nodes) {
            int want = CHILDREN_PER_TIER[n.tier];
            for (int i = n.children.size(); i < want; i++) {
                ClassNode slot = new ClassNode(n.id + "_SLOT" + (i + 1), "", n.tier + 1, n.path, n, "empty", true);
                n.children.add(slot);
                added.add(slot);
            }
        }
        // Placeholders below placeholders (only matters if a whole Tier 4 group were missing)
        for (int i = 0; i < added.size(); i++) {
            ClassNode n = added.get(i);
            for (int k = 0; k < CHILDREN_PER_TIER[n.tier]; k++) {
                ClassNode slot = new ClassNode(n.id + "_" + (k + 1), "", n.tier + 1, n.path, n, "empty", true);
                n.children.add(slot);
                added.add(slot);
            }
        }
        for (ClassNode a : added) byId.put(a.id, a);
        nodes.addAll(added);
        nodes.sort((a, b) -> Integer.compare(a.tier, b.tier));

        ClassTree tree = new ClassTree(root, nodes, byId);
        tree.layout();
        return tree;
    }

    private static int intOf(Object o) { return ((Number) o).intValue(); }

    // =========================================================================
    // LAYOUT
    // =========================================================================
    private void layout() {
        root.angle = Math.toRadians(-90);
        root.span = Math.PI * 2;
        root.radius = 0;
        root.x = root.y = 0;
        // Tier 1: one wedge per path, in its fixed direction.
        double wedge = Math.PI * 2 / ClassPath.WEDGES.length;
        for (ClassNode t1 : root.children) {
            place(t1, Math.toRadians(t1.path.wedgeCentreDegrees), wedge * SLICE_KEEP[1]);
        }
    }

    private void place(ClassNode n, double angle, double span) {
        n.angle = angle;
        n.span = span;
        n.radius = RING_RADIUS[n.tier];
        n.x = (float) (Math.cos(angle) * n.radius);
        n.y = (float) (Math.sin(angle) * n.radius);
        int k = n.children.size();
        if (k == 0) return;
        double share = span / k;
        for (int i = 0; i < k; i++) {
            ClassNode c = n.children.get(i);
            double a = angle - span / 2 + share * (i + 0.5);
            place(c, a, share * SLICE_KEEP[c.tier]);
        }
    }
}
