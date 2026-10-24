package fateweavers;

import fateweavers.logic.ClassGlyphs;
import fateweavers.logic.ClassTree;
import fateweavers.logic.Fortress;
import fateweavers.swing.AppIcon;
import fateweavers.swing.ClassTreeView;
import fateweavers.swing.FortressView;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;

/**
 * FATEWEAVERS - MODULE 1: FORTRESS & CHARACTER SANDBOX
 *
 * Run: double-click run.bat (or see README.md).
 *
 *   fateweavers.logic  - game rules and state. No Swing, no java.awt. Moves to LibGDX unchanged.
 *   fateweavers.swing  - drawing, the info panel and mouse/keyboard input. Gets rewritten for LibGDX.
 *
 * Two screens share the window: the fortress and the class tree. Tab (or the header button)
 * switches between them. The fortress keeps running while you look at the tree.
 */
public class Fateweavers {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Fortress fortress = new Fortress();
            FortressView fortressView = new FortressView(fortress);

            ClassTree tree = null;
            String treeError = null;
            try {
                tree = ClassTree.load(Path.of(ClassTree.DEFAULT_FILE));
            } catch (Exception e) {
                treeError = ClassTree.DEFAULT_FILE + ": " + e.getMessage();
                System.err.println("Couldn't load the class tree: " + treeError);
            }
            ClassGlyphs glyphs = ClassGlyphs.empty(); // class icons; missing file = plain path symbols
            try {
                glyphs = ClassGlyphs.load(Path.of(ClassGlyphs.DEFAULT_FILE));
                for (String w : glyphs.warnings()) System.err.println(ClassGlyphs.DEFAULT_FILE + " " + w);
            } catch (Exception e) {
                System.err.println("Couldn't load class icons (" + ClassGlyphs.DEFAULT_FILE + "): " + e.getMessage());
            }

            if (tree != null) fortress.setClassTree(tree);
            fortressView.setGlyphs(glyphs);
            CardLayout cards = new CardLayout();
            JPanel screens = new JPanel(cards);
            ClassTreeView[] treeView = new ClassTreeView[1];
            treeView[0] = new ClassTreeView(tree, glyphs, treeError, () -> {
                cards.show(screens, "fortress");
                fortressView.requestFocusInWindow();
            });
            treeView[0].setFortress(fortress);
            fortressView.setOnOpenClassTree(crew -> {
                // From Tab / the header button: home view. From a crew member: their class and next steps, lit up.
                if (crew == null) treeView[0].showHome();
                else treeView[0].showFor(crew);
                cards.show(screens, "tree");
                treeView[0].requestFocusInWindow();
            });
            screens.add(fortressView, "fortress");
            screens.add(treeView[0], "tree");

            JFrame frame = new JFrame("Fateweavers");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setIconImages(AppIcon.all());   // the spindle and red thread
            frame.setContentPane(screens);
            frame.pack();
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            fortressView.requestFocusInWindow();
            fortressView.start();
            treeView[0].start();
        });
    }
}
