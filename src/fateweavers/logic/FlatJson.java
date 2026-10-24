package fateweavers.logic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A tiny JSON reader for one shape only: an array of flat objects whose values are strings,
 * numbers, true/false or null (no nested objects or arrays). Enough for the class tree file,
 * without pulling in a library.
 */
final class FlatJson {
    private final String s;
    private int i = 0;

    private FlatJson(String s) { this.s = s; }

    static List<Map<String, Object>> parseArray(String text) {
        FlatJson p = new FlatJson(text);
        List<Map<String, Object>> out = new ArrayList<>();
        p.skipSpace();
        if (p.peek() == '﻿') { p.i++; p.skipSpace(); } // byte-order mark
        p.expect('[');
        p.skipSpace();
        if (p.peek() == ']') return out;
        while (true) {
            out.add(p.object());
            p.skipSpace();
            char c = p.next();
            if (c == ']') break;
            if (c != ',') throw p.error("Expected , or ]");
        }
        return out;
    }

    private Map<String, Object> object() {
        Map<String, Object> m = new LinkedHashMap<>();
        skipSpace();
        expect('{');
        skipSpace();
        if (peek() == '}') { i++; return m; }
        while (true) {
            skipSpace();
            String key = string();
            skipSpace();
            expect(':');
            skipSpace();
            m.put(key, value());
            skipSpace();
            char c = next();
            if (c == '}') break;
            if (c != ',') throw error("Expected , or }");
        }
        return m;
    }

    private Object value() {
        char c = peek();
        if (c == '"') return string();
        if (s.startsWith("null", i)) { i += 4; return null; }
        if (s.startsWith("true", i)) { i += 4; return Boolean.TRUE; }
        if (s.startsWith("false", i)) { i += 5; return Boolean.FALSE; }
        int start = i;
        while (i < s.length() && "+-0123456789.eE".indexOf(s.charAt(i)) >= 0) i++;
        if (start == i) throw error("Unexpected character");
        String num = s.substring(start, i);
        if (num.contains(".") || num.contains("e") || num.contains("E")) return Double.parseDouble(num);
        return Long.parseLong(num);
    }

    private String string() {
        expect('"');
        StringBuilder b = new StringBuilder();
        while (true) {
            char c = next();
            if (c == '"') return b.toString();
            if (c != '\\') { b.append(c); continue; }
            char e = next();
            switch (e) {
                case '"', '\\', '/' -> b.append(e);
                case 'n' -> b.append('\n');
                case 't' -> b.append('\t');
                case 'r' -> b.append('\r');
                case 'b' -> b.append('\b');
                case 'f' -> b.append('\f');
                case 'u' -> {
                    b.append((char) Integer.parseInt(s.substring(i, i + 4), 16));
                    i += 4;
                }
                default -> throw error("Bad escape");
            }
        }
    }

    private void skipSpace() {
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
    }

    private char peek() {
        if (i >= s.length()) throw error("Unexpected end of file");
        return s.charAt(i);
    }

    private char next() {
        char c = peek();
        i++;
        return c;
    }

    private void expect(char c) {
        if (next() != c) throw error("Expected " + c);
    }

    private IllegalArgumentException error(String msg) {
        return new IllegalArgumentException(msg + " at character " + i);
    }
}
