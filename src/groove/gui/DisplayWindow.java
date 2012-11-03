package groove.gui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 * Independent window wrapping a {@link Display}.
 * @author Arend Rensink
 * @version $Revision $
 */
class DisplayWindow extends JFrame {
    private DisplayWindow(Kind kind, String title, ImageIcon icon) {
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
        this(Kind.DISPLAY, display.getKind().getTitle(),
            display.getKind().getTabIcon());
        this.parent = parent;
        this.display = display;
        JPanel listPanel = this.display.getListPanel();
        JComponent displayPanel = this.display.getDisplayPanel();
        JComponent content;
        if (listPanel == null) {
            content = displayPanel;
        } else {
            content =
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPanel,
                    displayPanel);
        }
        getContentPane().add(content);
        pack();
        setVisible(true);
    }

    private void doClosingAction() {
        switch (this.kind) {
        case DISPLAY:
            this.parent.attach(this.display);
            break;
        }
    }

    private final Kind kind;
    private DisplaysPanel parent;
    private Display display;
    private static final Dimension MINIMUM_SIZE = new Dimension(500, 300);

    private static enum Kind {
        DISPLAY, STATE
    }
}