package groove.gui.display;

import static javax.swing.JSplitPane.HORIZONTAL_SPLIT;
import groove.gui.Icons.Icon;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSplitPane;

/**
 * Independent window wrapping a {@link Display}.
 * @author Arend Rensink
 * @version $Revision $
 */
class DisplayWindow extends JFrame {
    private DisplayWindow(Kind kind, String title, Icon icon) {
        super(title);
        this.kind = kind;
        if (icon != null) {
            setIconImage(icon.getImage());
        }
        setAlwaysOnTop(true);
        setMinimumSize(MINIMUM_SIZE);
        getContentPane().setMinimumSize(MINIMUM_SIZE);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                doClosingAction();
                super.windowClosing(e);
            }
        });
    }

    /** Constructs an instance for a given simulator and panel. */
    public DisplayWindow(DisplaysPanel parent, final Display display) {
        this(Kind.DISPLAY, display.getKind().getTitle(), display.getKind().getTabIcon());
        this.parent = parent;
        this.display = display;
        setContentPane(getContentPanel());
        pack();
        setVisible(true);
    }

    JComponent getContentPanel() {
        if (this.contentPanel == null) {
            JSplitPane displayPanel = getDisplayInfoPanel();
            this.contentPanel =
                this.display.getListPanel()
                    .map(p -> new JSplitPane(HORIZONTAL_SPLIT, p, displayPanel))
                    .orElse(getDisplayInfoPanel());
        }
        return this.contentPanel;
    }

    JSplitPane getDisplayInfoPanel() {
        JSplitPane result = this.displayInfoPanel;
        if (result == null) {
            JComponent infoPanel = this.display.getInfoPanel();
            result = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            result.setBorder(null);
            result.setResizeWeight(0.9);
            result.setLeftComponent(this.display);
            result.setRightComponent(infoPanel);
            infoPanel.setVisible(true);
            this.displayInfoPanel = result;
        }
        return result;
    }

    private void doClosingAction() {
        switch (this.kind) {
        case DISPLAY:
            this.parent.attach(this.display);
            break;
        }
    }

    private final Kind kind;
    /** Panel holding the list panel (if it exists( and the display+info panel. */
    private JComponent contentPanel;
    /** Split pane holding the display panel and the info panel. */
    private JSplitPane displayInfoPanel;
    private DisplaysPanel parent;
    private Display display;
    private static final Dimension MINIMUM_SIZE = new Dimension(500, 300);

    private static enum Kind {
        DISPLAY,
        STATE
    }
}