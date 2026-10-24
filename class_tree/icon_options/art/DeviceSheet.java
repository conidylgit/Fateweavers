package fateweavers.swing;

import fateweavers.logic.Element;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/** Renders the device option sheet: each row = one device, A and B, each in its states, at 1x and 3x. */
public final class DeviceSheet {
    interface Draw { void at(Graphics2D g, char v, float cx, float cy, int state, float t); }

    public static void main(String[] a) throws Exception {
        String which = a[1];
        String[] names; String[][] states; Draw[] draws; String[][] caps;
        if (which.equals("devices")) {
            names = new String[]{"Pedestal + Fire Gem", "Pedestal + Glass Orb", "Mount + ballista", "Chrysalis Ward", "Permeator", "Loom"};
            caps = new String[][]{{"stone column, gem floats above", "carved plinth with a rune band"}, {"stone column", "carved plinth"},
                    {"wooden swivel turntable", "iron tripod"}, {"a crystal chrysalis hanging in an arch", "a crystal cluster on a dais"},
                    {"a floating mana crystal", "a glowing rock with veins"}, {"an upright tapestry loom", "a round fate loom"}};
            states = new String[][]{{"off", "on", "firing"}, {"off", "on", "raised"}, {"empty", "loaded", "drawn"}, {"idle", "idle", "can promote"},
                    {"down", "running", "running"}, {"off", "charging", "ready"}};
            draws = new Draw[]{
                (g, v, x, y, s, t) -> DeviceArt.gemOnPedestal(g, v, x, y + 20, Element.FIRE, s, t),
                (g, v, x, y, s, t) -> DeviceArt.orbOnPedestal(g, v, x, y + 20, s),
                (g, v, x, y, s, t) -> DeviceArt.ballistaMount(g, v, x, y + 16, s),
                (g, v, x, y, s, t) -> DeviceArt.chrysalis(g, v, x, y, s == 2 ? 2 : 1, t + s),
                (g, v, x, y, s, t) -> DeviceArt.permeator(g, v, x, y, s, t + s * 0.7f),
                (g, v, x, y, s, t) -> DeviceArt.loom(g, v, x, y, s == 0 ? 0 : (s == 1 ? 2 : 1), s == 0 ? 0.3f : (s == 1 ? 0.55f : 1f), t + 0.4f),
            };
        } else return;
        int cell = 64, big = 3, colW = 3 * cell + cell * big + 30, rowH = cell * big + 44;
        int W = 20 + 2 * colW + 190, H = 60 + names.length * rowH;
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(Theme.BG); g.fillRect(0, 0, W, H);
        g.setColor(Theme.INK); g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22)); g.drawString(a[2], 20, 38);
        for (int r = 0; r < names.length; r++) {
            int y = 60 + r * rowH;
            g.setColor(Color.WHITE); g.fillRoundRect(10, y, W - 20, rowH - 10, 14, 14);
            g.setColor(Theme.INK); g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15)); g.drawString((r + 1) + ". " + names[r], 22, y + 24);
            for (int k = 0; k < 2; k++) {
                char v = k == 0 ? 'A' : 'B';
                int x0 = 200 + k * colW;
                g.setColor(Theme.INK); g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18)); g.drawString(String.valueOf(v), x0, y + 24);
                g.setColor(new Color(110, 110, 110)); g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12)); g.drawString(caps[r][k], x0 + 22, y + 24);
                // big view of the main state
                int bx = x0, by = y + 34;
                g.setColor(new Color(248, 248, 248)); g.fillRect(bx, by, cell * big, cell * big);
                Graphics2D gb = (Graphics2D) g.create();
                gb.translate(bx, by); gb.scale(big, big);
                draws[r].at(gb, v, cell / 2f, cell / 2f, states[r].length > 2 ? 2 : 1, 1.3f);
                gb.dispose();
                // small views of every state at game size
                for (int s = 0; s < states[r].length; s++) {
                    int sx = bx + cell * big + 10 + s * (cell + 6), sy = by + 40;
                    g.setColor(new Color(248, 248, 248)); g.fillRect(sx, sy, cell, cell);
                    Graphics2D gs = (Graphics2D) g.create();
                    gs.translate(sx, sy);
                    draws[r].at(gs, v, cell / 2f, cell / 2f, s, 1.3f);
                    gs.dispose();
                    g.setColor(new Color(120, 120, 120)); g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
                    g.drawString(states[r][s], sx + 2, sy + cell + 14);
                }
            }
        }
        g.dispose();
        ImageIO.write(img, "png", new File(a[0]));
    }
}
