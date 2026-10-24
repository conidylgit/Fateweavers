package fateweavers.swing;

import fateweavers.logic.Element;

import java.awt.*;
import java.awt.geom.*;

/**
 * Candidate art for fortress devices (pedestals, mounts, Chrysalis Ward, Permeator, Loom).
 * Each method draws one variant ('A' or 'B') centred on (cx, cy) at in-game size.
 * state: 0 = off / unpowered, 1 = on / idle, 2 = active (firing, humming, weaving, ready).
 */
final class DeviceArt {
    private DeviceArt() {}

    static final Color STONE = new Color(168, 166, 158), STONE_DARK = new Color(128, 126, 118), STONE_LIGHT = new Color(198, 196, 188);
    static final Color IRON = new Color(80, 80, 84), IRON_LIGHT = new Color(120, 120, 126);

    // ------------------------------------------------------------------ pedestals (magic conduits)

    /** The pedestal alone; returns the y where the conduit should float/sit. */
    static float pedestal(Graphics2D g, char v, float cx, float baseY, Element e, int state) {
        if (v == 'A') {   // classic column: wide foot, tapered shaft, cup on top
            g.setColor(STONE_DARK);
            g.fill(new RoundRectangle2D.Float(cx - 12, baseY - 5, 24, 5, 3, 3));
            Path2D col = new Path2D.Float();
            col.moveTo(cx - 6, baseY - 5); col.lineTo(cx + 6, baseY - 5); col.lineTo(cx + 4, baseY - 18); col.lineTo(cx - 4, baseY - 18); col.closePath();
            g.setColor(STONE);
            g.fill(col);
            g.setColor(STONE_LIGHT);
            g.fill(new Rectangle2D.Float(cx + 1, baseY - 18, 2.5f, 13));
            g.setColor(STONE_DARK);
            g.fill(new Arc2D.Float(cx - 10, baseY - 24, 20, 12, 180, 180, Arc2D.CHORD));
            if (state > 0) {   // faint glow pooling in the cup
                g.setColor(Theme.elementAlpha(e, state == 2 ? 150 : 80));
                g.fill(new Ellipse2D.Float(cx - 7, baseY - 21, 14, 4));
            }
            return baseY - 32;
        } else {          // carved plinth: a block with a glowing rune band and two prongs holding the conduit
            g.setColor(STONE_DARK);
            g.fill(new RoundRectangle2D.Float(cx - 11, baseY - 6, 22, 6, 2, 2));
            g.setColor(STONE);
            g.fill(new Rectangle2D.Float(cx - 8, baseY - 20, 16, 15));
            g.setColor(state > 0 ? Theme.element(e) : STONE_DARK);
            g.fill(new Rectangle2D.Float(cx - 8, baseY - 15, 16, 3));
            g.setColor(STONE_DARK);
            Path2D l = new Path2D.Float(); l.moveTo(cx - 8, baseY - 20); l.lineTo(cx - 11, baseY - 30); l.lineTo(cx - 7, baseY - 30); l.lineTo(cx - 4, baseY - 20); l.closePath();
            Path2D r = new Path2D.Float(); r.moveTo(cx + 8, baseY - 20); r.lineTo(cx + 11, baseY - 30); r.lineTo(cx + 7, baseY - 30); r.lineTo(cx + 4, baseY - 20); r.closePath();
            g.fill(l); g.fill(r);
            return baseY - 32;
        }
    }

    /** Fire Gem floating over a pedestal; bobbing via 't'. */
    static void gemOnPedestal(Graphics2D g, char v, float cx, float baseY, Element e, int state, float t) {
        float top = pedestal(g, v, cx, baseY, e, state);
        float bob = state > 0 ? (float) Math.sin(t * 2.4) * 1.5f : 2f;
        if (state == 2) {
            g.setColor(Theme.elementAlpha(e, 70));
            g.fill(new Ellipse2D.Float(cx - 13, top + bob - 13, 26, 26));
        }
        WeaponArt.drawGem(g, cx, top + bob, 18, e, state > 0);
    }

    /** Glass Orb resting on a pedestal. */
    static void orbOnPedestal(Graphics2D g, char v, float cx, float baseY, int state) {
        float top = pedestal(g, v, cx, baseY, Element.ARCANE, state);
        orb(g, cx, top + 4, 9, state > 0, state == 2 ? 1f : 0f);
    }

    private static void orb(Graphics2D g, float cx, float cy, float r, boolean on, float glow) {
        Color base = on ? Theme.elementSoft(Element.ARCANE) : new Color(232, 232, 232);
        if (glow > 0) base = WeaponArt.blend(base, Theme.elementLight(Element.ARCANE), glow);
        if (glow > 0) { g.setColor(new Color(255, 255, 255, 170)); g.fill(new Ellipse2D.Float(cx - r - 6, cy - r - 6, r * 2 + 12, r * 2 + 12)); }
        g.setColor(on ? Theme.elementAlpha(Element.ARCANE, 90) : new Color(190, 190, 190));
        g.fill(new Ellipse2D.Float(cx - r, cy - r, r * 2, r * 2));
        float ir = r * 0.84f;
        g.setColor(base);
        g.fill(new Ellipse2D.Float(cx - ir, cy - ir, ir * 2, ir * 2));
        g.setColor(new Color(255, 255, 255, 210));
        g.fill(new Ellipse2D.Float(cx - r * 0.55f, cy - r * 0.6f, r * 0.32f, r * 0.42f));
    }

    // ------------------------------------------------------------------ mounts (siege)

    /** A siege mount with a ballista on top, aimed right. state 2 = string drawn back. */
    static void ballistaMount(Graphics2D g, char v, float cx, float baseY, int state) {
        Stroke old = g.getStroke();
        if (v == 'A') {   // wooden swivel turntable on a stone ring
            g.setColor(STONE_DARK);
            g.fill(new Ellipse2D.Float(cx - 18, baseY - 8, 36, 10));
            g.setColor(STONE);
            g.fill(new Ellipse2D.Float(cx - 16, baseY - 10, 32, 8));
            g.setColor(Theme.WOOD_DARK);
            g.fill(new Rectangle2D.Float(cx - 3, baseY - 20, 6, 12));
            g.setColor(Theme.WOOD);
            g.fill(new Ellipse2D.Float(cx - 10, baseY - 14, 20, 6));
        } else {          // iron tripod mount with a pivot block
            g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(IRON);
            g.draw(new Line2D.Float(cx, baseY - 18, cx - 13, baseY));
            g.draw(new Line2D.Float(cx, baseY - 18, cx + 13, baseY));
            g.draw(new Line2D.Float(cx, baseY - 18, cx, baseY));
            g.setColor(IRON_LIGHT);
            g.fill(new RoundRectangle2D.Float(cx - 6, baseY - 23, 12, 7, 3, 3));
        }
        // the ballista: stock, bow, string, bolt
        float sy = baseY - 22;
        g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(Theme.WOOD);
        g.draw(new Line2D.Float(cx - 14, sy + 2, cx + 14, sy - 2));
        float bx = cx + 12, pull = state == 2 ? 13 : 5;
        g.setStroke(new BasicStroke(2.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(Theme.WOOD_DARK);
        Path2D bow = new Path2D.Float();
        bow.moveTo(bx - 4, sy - 15); bow.quadTo(bx + 6, sy - 2, bx - 4, sy + 11);
        g.draw(bow);
        g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(60, 60, 60));
        g.draw(new Line2D.Float(bx - 4, sy - 15, bx - pull, sy - 2));
        g.draw(new Line2D.Float(bx - 4, sy + 11, bx - pull, sy - 2));
        if (state > 0) {
            g.setStroke(new BasicStroke(1.6f));
            g.setColor(new Color(70, 50, 30));
            g.draw(new Line2D.Float(bx - pull, sy - 2, bx + 6, sy - 3));
        }
        g.setStroke(old);
    }

    // ------------------------------------------------------------------ Chrysalis Ward

    static void chrysalis(Graphics2D g, char v, float cx, float cy, int state, float t) {
        boolean hum = state == 2;
        float pulse = 0.5f + 0.5f * (float) Math.sin(t * 6);
        if (v == 'A') {   // a crystal chrysalis hanging from a stone arch; splits with light when someone can promote
            g.setColor(STONE_DARK);
            Stroke old = g.getStroke();
            g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Path2D arch = new Path2D.Float();
            arch.moveTo(cx - 20, cy + 24); arch.lineTo(cx - 20, cy - 12); arch.quadTo(cx - 20, cy - 26, cx, cy - 26); arch.quadTo(cx + 20, cy - 26, cx + 20, cy - 12); arch.lineTo(cx + 20, cy + 24);
            g.draw(arch);
            g.setStroke(new BasicStroke(1.4f));
            g.setColor(new Color(90, 90, 90));
            g.draw(new Line2D.Float(cx, cy - 24, cx, cy - 17));
            g.setStroke(old);
            if (hum) {
                g.setColor(new Color(255, 255, 255, Math.round(120 + 100 * pulse)));
                g.fill(new Ellipse2D.Float(cx - 14, cy - 16, 28, 34));
            }
            Path2D pod = new Path2D.Float();
            pod.moveTo(cx, cy - 18); pod.curveTo(cx + 12, cy - 12, cx + 11, cy + 8, cx, cy + 20); pod.curveTo(cx - 11, cy + 8, cx - 12, cy - 12, cx, cy - 18); pod.closePath();
            g.setColor(hum ? new Color(60, 60, 60) : new Color(196, 196, 196));
            g.fill(pod);
            Path2D facet = new Path2D.Float();
            facet.moveTo(cx, cy - 18); facet.curveTo(cx + 12, cy - 12, cx + 11, cy + 8, cx, cy + 20); facet.lineTo(cx + 2, cy); facet.closePath();
            g.setColor(hum ? new Color(100, 100, 100) : new Color(224, 224, 224));
            g.fill(facet);
            g.setColor(hum ? new Color(30, 30, 30) : new Color(160, 160, 160));
            g.setStroke(new BasicStroke(1.2f));
            for (int i = -1; i <= 1; i++) g.draw(new Line2D.Float(cx - 9, cy - 6 + i * 8, cx + 9, cy - 2 + i * 8));
            if (hum) {   // the split: a bright seam down the middle
                g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.setColor(new Color(255, 255, 255, 230));
                Path2D seam = new Path2D.Float();
                seam.moveTo(cx, cy - 14); seam.lineTo(cx - 2, cy - 4); seam.lineTo(cx + 2, cy + 4); seam.lineTo(cx, cy + 14);
                g.draw(seam);
            }
            g.setStroke(old);
            g.setColor(STONE);
            g.fill(new RoundRectangle2D.Float(cx - 24, cy + 22, 48, 5, 3, 3));
        } else {          // a crystal cluster on a stone dais, with a ring of shards orbiting while it hums
            g.setColor(STONE_DARK);
            g.fill(new Ellipse2D.Float(cx - 22, cy + 14, 44, 12));
            g.setColor(STONE);
            g.fill(new Ellipse2D.Float(cx - 20, cy + 12, 40, 10));
            Color m = hum ? new Color(70, 70, 70) : new Color(190, 190, 190), d = hum ? new Color(30, 30, 30) : new Color(160, 160, 160), l = hum ? new Color(120, 120, 120) : new Color(222, 222, 222);
            shard(g, cx - 10, cy + 16, 7, 18, -0.35f, m, d, l);
            shard(g, cx + 10, cy + 16, 7, 18, 0.35f, m, d, l);
            if (hum) { g.setColor(new Color(255, 255, 255, Math.round(90 + 80 * pulse))); g.fill(new Ellipse2D.Float(cx - 12, cy - 22, 24, 40)); }
            shard(g, cx, cy + 16, 10, 36, 0f, m, d, l);
            if (hum) {
                for (int i = 0; i < 5; i++) {
                    double a = t * 2.2 + i * Math.PI * 2 / 5;
                    float sx = cx + (float) Math.cos(a) * 22, sy = cy - 2 + (float) Math.sin(a) * 7;
                    shard(g, sx, sy + 4, 3, 8, 0f, new Color(40, 40, 40), new Color(20, 20, 20), new Color(140, 140, 140));
                }
            }
        }
    }

    private static void shard(Graphics2D g, float bx, float by, float w, float h, float tilt, Color m, Color d, Color l) {
        AffineTransform old = g.getTransform();
        g.rotate(tilt, bx, by);
        Path2D whole = new Path2D.Float();
        whole.moveTo(bx, by - h); whole.lineTo(bx + w, by - h * 0.7f); whole.lineTo(bx + w * 0.8f, by); whole.lineTo(bx - w * 0.8f, by); whole.lineTo(bx - w, by - h * 0.7f); whole.closePath();
        g.setColor(m); g.fill(whole);
        Path2D left = new Path2D.Float();
        left.moveTo(bx, by - h); left.lineTo(bx - w, by - h * 0.7f); left.lineTo(bx - w * 0.8f, by); left.lineTo(bx - w * 0.15f, by); left.closePath();
        g.setColor(d); g.fill(left);
        Path2D right = new Path2D.Float();
        right.moveTo(bx, by - h); right.lineTo(bx + w, by - h * 0.7f); right.lineTo(bx + w * 0.8f, by); right.lineTo(bx + w * 0.15f, by); right.closePath();
        g.setColor(l); g.fill(right);
        g.setTransform(old);
    }

    // ------------------------------------------------------------------ Permeator (air from mana)

    static void permeator(Graphics2D g, char v, float cx, float cy, int state, float t) {
        boolean on = state > 0;
        float pulse = 0.5f + 0.5f * (float) Math.sin(t * 3);
        if (v == 'A') {   // a crystal floating over a stone base, shedding motes of mana that rise
            g.setColor(STONE_DARK);
            g.fill(new Ellipse2D.Float(cx - 14, cy + 10, 28, 8));
            g.setColor(STONE);
            g.fill(new Ellipse2D.Float(cx - 12, cy + 8, 24, 6));
            float bob = on ? (float) Math.sin(t * 2) * 2.5f : 4f;
            if (on) {
                g.setColor(Theme.elementAlpha(Element.ARCANE, Math.round(50 + 50 * pulse)));
                g.fill(new Ellipse2D.Float(cx - 13, cy - 18 + bob, 26, 26));
                g.setColor(Theme.elementAlpha(Element.ARCANE, 60));
                g.fill(new Ellipse2D.Float(cx - 8, cy + 9, 16, 4));
                for (int i = 0; i < 4; i++) {   // rising motes
                    float k = (float) ((t * 0.6 + i * 0.25) % 1.0);
                    float mx = cx + (float) Math.sin(i * 1.9 + t) * 10, my = cy - 4 - k * 26;
                    g.setColor(Theme.elementAlpha(Element.ARCANE, Math.round(200 * (1 - k))));
                    g.fill(new Ellipse2D.Float(mx - 1.6f, my - 1.6f, 3.2f, 3.2f));
                }
            }
            Color m = on ? Theme.element(Element.ARCANE) : new Color(176, 176, 176);
            Color d = on ? Theme.elementDark(Element.ARCANE) : new Color(140, 140, 140);
            Color l = on ? Theme.elementLight(Element.ARCANE) : new Color(206, 206, 206);
            float top = cy - 16 + bob, mid = cy - 5 + bob, bot = cy + 4 + bob;
            Path2D whole = new Path2D.Float(); whole.moveTo(cx, top); whole.lineTo(cx + 7, mid); whole.lineTo(cx, bot); whole.lineTo(cx - 7, mid); whole.closePath();
            g.setColor(m); g.fill(whole);
            Path2D left = new Path2D.Float(); left.moveTo(cx, top); left.lineTo(cx - 7, mid); left.lineTo(cx, bot); left.lineTo(cx - 1.5f, mid); left.closePath();
            g.setColor(d); g.fill(left);
            Path2D right = new Path2D.Float(); right.moveTo(cx, top); right.lineTo(cx + 7, mid); right.lineTo(cx, bot); right.lineTo(cx + 1.5f, mid); right.closePath();
            g.setColor(l); g.fill(right);
        } else {          // a rough standing rock with glowing veins; breathes out soft rings
            if (on) {
                for (int i = 0; i < 2; i++) {
                    float k = (float) ((t * 0.5 + i * 0.5) % 1.0);
                    float r = 10 + k * 14;
                    g.setStroke(new BasicStroke(1.5f));
                    g.setColor(Theme.elementAlpha(Element.ARCANE, Math.round(150 * (1 - k))));
                    g.draw(new Ellipse2D.Float(cx - r, cy - 4 - r * 0.8f, r * 2, r * 1.6f));
                }
            }
            Path2D rock = new Path2D.Float();
            rock.moveTo(cx - 12, cy + 14); rock.lineTo(cx - 14, cy - 2); rock.lineTo(cx - 7, cy - 16); rock.lineTo(cx + 3, cy - 19); rock.lineTo(cx + 12, cy - 9); rock.lineTo(cx + 14, cy + 6); rock.lineTo(cx + 10, cy + 14); rock.closePath();
            g.setColor(on ? new Color(96, 92, 104) : new Color(150, 150, 150));
            g.fill(rock);
            Path2D face = new Path2D.Float();
            face.moveTo(cx + 3, cy - 19); face.lineTo(cx + 12, cy - 9); face.lineTo(cx + 14, cy + 6); face.lineTo(cx + 10, cy + 14); face.lineTo(cx + 2, cy + 14); face.lineTo(cx + 1, cy - 4); face.closePath();
            g.setColor(on ? new Color(126, 122, 136) : new Color(176, 176, 176));
            g.fill(face);
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(on ? Theme.elementAlpha(Element.ARCANE, Math.round(170 + 85 * pulse)) : new Color(120, 120, 120));
            Path2D veins = new Path2D.Float();
            veins.moveTo(cx - 6, cy - 12); veins.lineTo(cx - 2, cy - 4); veins.lineTo(cx - 7, cy + 4); veins.lineTo(cx - 3, cy + 12);
            veins.moveTo(cx - 2, cy - 4); veins.lineTo(cx + 6, cy - 6); veins.lineTo(cx + 9, cy + 2);
            g.draw(veins);
            g.setColor(STONE_DARK);
            g.fill(new RoundRectangle2D.Float(cx - 16, cy + 12, 32, 5, 3, 3));
        }
        g.setStroke(new BasicStroke(1f));
    }

    // ------------------------------------------------------------------ the Loom

    /** charge 0..1 fills the weaving; state 2 = weaving (shuttle moving), ready when charge >= 1. */
    static void loom(Graphics2D g, char v, float cx, float cy, int state, float charge, float t) {
        boolean on = state > 0, ready = charge >= 1f;
        Stroke old = g.getStroke();
        Color wood = on ? Theme.WOOD : new Color(170, 160, 150), woodDark = on ? Theme.WOOD_DARK : new Color(140, 130, 120);
        if (v == 'A') {   // an upright tapestry loom: frame, warp threads, cloth growing up from the bottom
            float w = 46, h = 40, x0 = cx - w / 2, y0 = cy - h / 2;
            if (ready) {
                float p = 0.5f + 0.5f * (float) Math.sin(t * 5);
                g.setColor(Theme.elementAlpha(Element.ARCANE, Math.round(60 + 60 * p)));
                g.fill(new RoundRectangle2D.Float(x0 - 6, y0 - 6, w + 12, h + 12, 10, 10));
            }
            // cloth
            float clothH = (h - 10) * Math.max(0.05f, Math.min(1, charge));
            g.setColor(on ? new Color(30, 30, 30) : new Color(160, 160, 160));
            g.fill(new Rectangle2D.Float(x0 + 5, y0 + h - 5 - clothH, w - 10, clothH));
            g.setColor(on ? Theme.elementAlpha(Element.ARCANE, 200) : new Color(190, 190, 190));
            for (float yy = y0 + h - 9; yy > y0 + h - 5 - clothH; yy -= 6) g.fill(new Rectangle2D.Float(x0 + 5, yy, w - 10, 1.4f));
            // warp threads
            g.setStroke(new BasicStroke(1f));
            g.setColor(on ? new Color(70, 70, 70) : new Color(170, 170, 170));
            for (int i = 0; i <= 8; i++) { float x = x0 + 6 + i * (w - 12) / 8f; g.draw(new Line2D.Float(x, y0 + 4, x, y0 + h - 5 - clothH)); }
            // frame
            g.setStroke(new BasicStroke(3.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(woodDark);
            g.draw(new Line2D.Float(x0, y0 - 2, x0, y0 + h + 6));
            g.draw(new Line2D.Float(x0 + w, y0 - 2, x0 + w, y0 + h + 6));
            g.setColor(wood);
            g.draw(new Line2D.Float(x0 - 3, y0 + 2, x0 + w + 3, y0 + 2));
            g.draw(new Line2D.Float(x0 - 3, y0 + h - 4, x0 + w + 3, y0 + h - 4));
            // shuttle
            float sx = state == 2 ? x0 + 8 + (w - 16) * (0.5f + 0.5f * (float) Math.sin(t * 4)) : x0 + w - 8;
            float sy = y0 + h - 7 - clothH;
            g.setColor(on ? Theme.element(Element.ARCANE) : new Color(150, 150, 150));
            Path2D sh = new Path2D.Float(); sh.moveTo(sx - 7, sy); sh.quadTo(sx, sy - 3.5f, sx + 7, sy); sh.quadTo(sx, sy + 3.5f, sx - 7, sy); sh.closePath();
            g.fill(sh);
        } else {          // a round fate loom: a ring frame whose threads weave inward toward a glowing centre
            float r = 22;
            if (ready) {
                float p = 0.5f + 0.5f * (float) Math.sin(t * 5);
                g.setColor(Theme.elementAlpha(Element.ARCANE, Math.round(60 + 60 * p)));
                g.fill(new Ellipse2D.Float(cx - r - 6, cy - r - 6, r * 2 + 12, r * 2 + 12));
            }
            g.setColor(woodDark);
            g.fill(new Rectangle2D.Float(cx - 3, cy + r - 2, 6, 10));
            g.fill(new RoundRectangle2D.Float(cx - 14, cy + r + 6, 28, 5, 3, 3));
            // spokes (threads)
            g.setStroke(new BasicStroke(1f));
            g.setColor(on ? new Color(70, 70, 70) : new Color(170, 170, 170));
            for (int i = 0; i < 12; i++) { double a = i * Math.PI / 6 + (state == 2 ? t * 0.6 : 0); g.draw(new Line2D.Float(cx, cy, cx + (float) Math.cos(a) * r, cy + (float) Math.sin(a) * r)); }
            // the woven spiral grows inward-out with charge
            float wr = r * Math.max(0.12f, Math.min(1, charge));
            g.setColor(on ? new Color(30, 30, 30) : new Color(160, 160, 160));
            g.fill(new Ellipse2D.Float(cx - wr, cy - wr, wr * 2, wr * 2));
            g.setColor(on ? Theme.elementAlpha(Element.ARCANE, 200) : new Color(190, 190, 190));
            g.setStroke(new BasicStroke(1.2f));
            for (float rr = 4; rr < wr; rr += 5) g.draw(new Ellipse2D.Float(cx - rr, cy - rr, rr * 2, rr * 2));
            // ring frame
            g.setStroke(new BasicStroke(4f));
            g.setColor(wood);
            g.draw(new Ellipse2D.Float(cx - r, cy - r, r * 2, r * 2));
            g.setColor(on ? Theme.element(Element.ARCANE) : new Color(150, 150, 150));
            g.fill(new Ellipse2D.Float(cx - 2.5f, cy - 2.5f, 5, 5));
        }
        g.setStroke(old);
    }

    // ------------------------------------------------------------------ app icon candidates (100x100 canvas)

    static void appIcon(Graphics2D g, char v, int size) {
        Color tile = new Color(242, 240, 236), ink = new Color(17, 17, 17), red = new Color(217, 65, 43);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform old = g.getTransform();
        g.scale(size / 100.0, size / 100.0);
        boolean tiny = size <= 24;
        float in = tiny ? 0 : 3;
        g.setColor(v == 'C' ? ink : tile);
        g.fill(new RoundRectangle2D.Float(in, in, 100 - in * 2, 100 - in * 2, 24, 24));
        float th = tiny ? 7f : size < 64 ? 5f : 3.6f;
        if (v == 'A') {   // the blob guy holding a red thread of fate
            Area blob = new Area(new Ellipse2D.Float(26, 10, 48, 46));
            blob.add(new Area(new Ellipse2D.Float(32, 44, 36, 38)));
            g.setColor(red); g.setStroke(new BasicStroke(th, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Path2D thr = new Path2D.Float(); thr.moveTo(6, 86); thr.curveTo(30, 60, 54, 96, 94, 40); g.draw(thr);
            g.setColor(Color.WHITE); g.setStroke(new BasicStroke(tiny ? 6 : 5)); g.draw(blob);
            g.setColor(ink); g.fill(blob);
            g.setColor(Color.WHITE);
            g.fill(new Ellipse2D.Float(37, 24, 8, 16)); g.fill(new Ellipse2D.Float(55, 24, 8, 16));
        } else if (v == 'B') {   // a fortress tower with the red thread weaving through it
            Area tower = new Area(new Rectangle2D.Float(28, 30, 44, 60));
            for (int i = 0; i < 3; i++) tower.add(new Area(new Rectangle2D.Float(28 + i * 16, 16, 12, 16)));
            tower.subtract(new Area(new RoundRectangle2D.Float(42, 64, 16, 26, 16, 16)));
            g.setColor(ink); g.fill(tower);
            g.setColor(red); g.setStroke(new BasicStroke(th + 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Path2D thr = new Path2D.Float(); thr.moveTo(8, 58); thr.curveTo(30, 34, 48, 70, 66, 46); thr.curveTo(76, 34, 86, 40, 94, 30); g.draw(thr);
        } else {                  // dark tile: a white spindle wound with a red thread
            Path2D sp = new Path2D.Float(); sp.moveTo(50, 8); sp.lineTo(64, 34); sp.lineTo(64, 66); sp.lineTo(50, 92); sp.lineTo(36, 66); sp.lineTo(36, 34); sp.closePath();
            g.setColor(Color.WHITE); g.fill(sp);
            g.setColor(red); g.setStroke(new BasicStroke(th + 1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 0; i < 3; i++) g.draw(new Line2D.Float(36, 40 + i * 10, 64, 36 + i * 10));
            Path2D tail = new Path2D.Float(); tail.moveTo(64, 56); tail.curveTo(80, 58, 84, 76, 94, 90); g.draw(tail);
        }
        g.setTransform(old);
    }
}
