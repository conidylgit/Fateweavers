package fateweavers.swing;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
public final class IconOptions {
    public static void main(String[] a) throws Exception {
        int[] sizes = {256, 64, 48, 32, 24, 16};
        int W = 40 + 3 * 520, H = 470;
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(Theme.BG); g.fillRect(0, 0, W, H);
        g.setColor(Theme.INK); g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22)); g.drawString("App icon options", 20, 38);
        String[] cap = {"the blob guy with the red thread of fate", "a fortress tower with the thread weaving through", "a spindle wound with red thread, dark tile"};
        for (int k = 0; k < 3; k++) {
            int x0 = 20 + k * 520;
            g.setColor(Color.WHITE); g.fillRoundRect(x0, 56, 500, 400, 14, 14);
            g.setColor(Theme.INK); g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18)); g.drawString(String.valueOf((char) ('A' + k)), x0 + 14, 82);
            g.setColor(new Color(110, 110, 110)); g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12)); g.drawString(cap[k], x0 + 38, 82);
            Graphics2D gi = (Graphics2D) g.create(); gi.translate(x0 + 14, 96); DeviceArt.appIcon(gi, (char) ('A' + k), 256); gi.dispose();
            int sx = x0 + 290, sy = 96;
            for (int i = 1; i < sizes.length; i++) {
                for (int bg = 0; bg < 2; bg++) {   // on light and on a dark taskbar
                    int px = sx + bg * 100, py = sy;
                    g.setColor(bg == 0 ? new Color(240, 240, 240) : new Color(32, 32, 36)); g.fillRect(px - 6, py - 6, sizes[i] + 12, sizes[i] + 12);
                    BufferedImage ic = new BufferedImage(sizes[i], sizes[i], BufferedImage.TYPE_INT_ARGB);
                    Graphics2D gc = ic.createGraphics(); DeviceArt.appIcon(gc, (char) ('A' + k), sizes[i]); gc.dispose();
                    g.drawImage(ic, px, py, null);
                }
                sy += sizes[i] + 18;
            }
        }
        g.dispose();
        ImageIO.write(img, "png", new File(a[0]));
    }
}
