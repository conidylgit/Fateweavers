package fateweavers.logic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Class icons, loaded from class_tree/class_glyphs.txt (the format is explained in that file).
 *
 * Each designed class has a list of shapes on a 100 x 100 grid; together they make its whole
 * silhouette (the path symbol, reshaped). Classes without an entry show the plain path symbol.
 * Pure data: the drawing code turns the shapes into pixels.
 */
public final class ClassGlyphs {
    public static final String DEFAULT_FILE = "class_tree/class_glyphs.txt";

    public enum Kind { SYMBOL, POLY, CIRCLE, ELLIPSE, RECT, LINE, PATH }

    /**
     * One shape. cut = carve it out of what's above. stroke > 0 = use only its outline, that thick
     * (WIRE_WIDTH for "wire"). path = SVG-style path data for PATH shapes, otherwise null.
     */
    public record Shape(Kind kind, float[] args, String path, boolean cut, float stroke, boolean miter) {}

    /** A class's whole icon. Same object every time for a class, so it can be cached by identity. */
    public record Glyph(List<Shape> shapes) {}

    /** How thick "wire" outlines are: the Light Melee look. */
    public static final float WIRE_WIDTH = 11f;

    private final Map<String, Glyph> glyphs = new HashMap<>();
    private final List<String> warnings = new ArrayList<>();

    private ClassGlyphs() {}

    /** No icons at all (if the file is missing): every class shows its plain path symbol. */
    public static ClassGlyphs empty() { return new ClassGlyphs(); }

    public static ClassGlyphs load(Path file) throws IOException {
        ClassGlyphs g = new ClassGlyphs();
        g.parse(Files.readAllLines(file, StandardCharsets.UTF_8));
        return g;
    }

    /** Problems found while reading the file (unknown shape, bad numbers...), for the console. */
    public List<String> warnings() { return Collections.unmodifiableList(warnings); }

    /** The icon for a class, or null if it hasn't been designed yet (or is an empty slot). */
    public Glyph glyphFor(ClassNode n) { return n.placeholder ? null : glyphs.get(n.id); }

    /** The icon stored under any name (used by the design sheets for options like "T3_FIRE_MAGE.A"). */
    public Glyph byName(String name) { return glyphs.get(name.toUpperCase(Locale.ROOT)); }

    /** Every name in the file, in no particular order. */
    public java.util.Set<String> names() { return Collections.unmodifiableSet(glyphs.keySet()); }

    // =========================================================================
    // PARSING
    // =========================================================================
    private void parse(List<String> lines) {
        String current = null;
        List<Shape> shapes = null;
        for (int i = 0; i < lines.size(); i++) {
            String raw = lines.get(i);
            int hash = raw.indexOf('#');
            String line = (hash >= 0 ? raw.substring(0, hash) : raw).stripTrailing();
            if (line.isBlank()) continue;
            int lineNo = i + 1;
            if (!Character.isWhitespace(line.charAt(0))) {          // a class name starts a block
                if (current != null) glyphs.put(current, new Glyph(List.copyOf(shapes)));
                current = line.strip().split("\\s+")[0].toUpperCase(Locale.ROOT);
                shapes = new ArrayList<>();
            } else if (current == null) {
                warnings.add("line " + lineNo + ": shape before any class name");
            } else {
                Shape s = shape(line.strip(), lineNo);
                if (s != null) shapes.add(s);
            }
        }
        if (current != null) glyphs.put(current, new Glyph(List.copyOf(shapes)));
    }

    private Shape shape(String t, int lineNo) {
        String[] w = t.split("\\s+");
        int k = 0;
        boolean cut = false, miter = false;
        float stroke = 0;
        while (k < w.length) {
            String m = w[k].toLowerCase(Locale.ROOT);
            if (m.equals("cut")) { cut = true; k++; }
            else if (m.equals("wire")) { stroke = WIRE_WIDTH; miter = true; k++; }
            else if (m.equals("stroke") && k + 1 < w.length) {
                try { stroke = Float.parseFloat(w[k + 1]); } catch (NumberFormatException e) {
                    warnings.add("line " + lineNo + ": \"stroke\" needs a width"); return null;
                }
                k += 2;
            } else break;
        }
        if (k >= w.length) { warnings.add("line " + lineNo + ": no shape given"); return null; }
        Kind kind;
        try {
            kind = Kind.valueOf(w[k].toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            warnings.add("line " + lineNo + ": unknown shape \"" + w[k] + "\"");
            return null;
        }
        if (kind == Kind.PATH) {
            String data = t.substring(t.toLowerCase(Locale.ROOT).indexOf("path") + 4).strip();
            if (data.isEmpty()) { warnings.add("line " + lineNo + ": path with no points"); return null; }
            return new Shape(kind, new float[0], data, cut, stroke, miter);
        }
        float[] a = new float[w.length - k - 1];
        try {
            for (int i = 0; i < a.length; i++) a[i] = Float.parseFloat(w[k + 1 + i]);
        } catch (NumberFormatException e) {
            warnings.add("line " + lineNo + ": numbers expected after \"" + w[k] + "\"");
            return null;
        }
        int min = switch (kind) {
            case SYMBOL -> 0; case POLY -> 6; case CIRCLE -> 3; case ELLIPSE -> 4; case RECT -> 4; case LINE -> 4; case PATH -> 0;
        };
        if (a.length < min || (kind == Kind.POLY && a.length % 2 != 0)) {
            warnings.add("line " + lineNo + ": not enough numbers for \"" + w[k] + "\"");
            return null;
        }
        return new Shape(kind, a, null, cut, stroke, miter);
    }
}
