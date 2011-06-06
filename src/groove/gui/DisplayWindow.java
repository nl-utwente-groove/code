package groove.gui;


import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 * Independent window wrapping a {@link Display}.
 * @author Arend Rensink
 * @version $Revision $
 */
class DisplayWindow extends JFrame {
    /** Constructs an instance for a given simulator and panel. */
    public DisplayWindow(DisplaysPanel parent, final Display panel) {
        super(panel.getKind().getTitle());
        this.parent = parent;
        this.panel = panel;
        JPanel listPanel = panel.getListPanel();
        if (panel.getKind() == DisplayKind.RULE || listPanel == null) {
            getContentPane().add(panel.getDisplayPanel());
        } else {
            JSplitPane splitPane =
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPanel,
                    panel.getDisplayPanel());
            getContentPane().add(splitPane);
        }
        setAlwaysOnTop(true);
        ImageIcon icon = panel.getKind().getTabIcon();
        if (icon != null) {
            setIconImage(icon.getImage());
        }
        setMinimumSize(MINIMUM_SIZE);
        getContentPane().setMinimumSize(MINIMUM_SIZE);
        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                attach();
                super.windowClosing(e);
            }
        });
        setVisible(true);
    }

    /** Callback method to attach the window content back to its original container. */
    protected void attach() {
        this.parent.attach(getDisplay());
    }

    /** Returns the simulator tab currently displayed in this window. */
    protected final Display getDisplay() {
        return this.panel;
    }

    private final DisplaysPanel parent;
    private final Display panel;
    private static final Dimension MINIMUM_SIZE = new Dimension(500, 300);
}