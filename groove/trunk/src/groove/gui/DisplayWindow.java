package groove.gui;

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
abstract class DisplayWindow extends JFrame {
    /** Constructs an instance for a given simulator and panel. */
    public DisplayWindow(final Display panel) {
        super(panel.getKind().getName());
        this.panel = panel;
        JPanel listPanel = panel.getListPanel();
        if (listPanel == null) {
            getContentPane().add(panel.getPanel());
        } else {
            JSplitPane splitPane =
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPanel,
                    panel.getPanel());
            getContentPane().add(splitPane);
        }
        setAlwaysOnTop(true);
        ImageIcon icon = panel.getKind().getTabIcon();
        if (icon != null) {
            setIconImage(icon.getImage());
        }
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
    protected abstract void attach();

    /** Returns the simulator tab currently displayed in this window. */
    protected final Display getTab() {
        return this.panel;
    }

    private final Display panel;
}