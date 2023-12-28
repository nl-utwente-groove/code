package nl.utwente.groove.util;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

/** Utility obtained from StackOverflow;
 * see {@code https://stackoverflow.com/questions/46699135/unicode-special-characters-appearing-in-java-console-but-not-in-swing}
 */
public class FontCheck {

    private JComponent ui = null;

    private final String text = "\u24D8";
    private JComboBox<?> fontComboBox;
    private JTextField outputField = new JTextField(this.text, 5);

    FontCheck() {
        initUI();
    }

    @SuppressWarnings("javadoc")
    public void initUI() {
        if (this.ui != null) {
            return;
        }

        this.ui = new JPanel(new BorderLayout(4, 4));
        this.ui.setBorder(new EmptyBorder(4, 4, 4, 4));

        this.ui.add(this.outputField);
        this.ui.add(getToolBar(), BorderLayout.PAGE_START);

        refreshFont();
    }

    private JToolBar getToolBar() {
        JToolBar tb = new JToolBar();
        tb.setLayout(new FlowLayout());

        String[] fonts
            = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        System.out.println(fonts.length + " font families installed");
        Vector<String> supportedFonts = new Vector<>();
        for (String fontName : fonts) {
            Font f = new Font(fontName, Font.PLAIN, 1);
            if (f.canDisplayUpTo(this.text) < 0) {
                System.out.println(fontName);
                supportedFonts.add(fontName);
            }
        }
        this.fontComboBox = new JComboBox<>(supportedFonts);
        ActionListener refreshListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                refreshFont();
            }
        };
        this.fontComboBox.addActionListener(refreshListener);

        tb.add(this.fontComboBox);
        return tb;
    }

    private void refreshFont() {
        String fontName = this.fontComboBox.getSelectedItem().toString();
        Font f = new Font(fontName, Font.PLAIN, 60);
        this.outputField.setFont(f);
    }

    @SuppressWarnings("javadoc")
    public JComponent getUI() {
        return this.ui;
    }

    @SuppressWarnings("javadoc")
    public static void main(String[] args) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception useDefault) {
                    // no action
                }
                FontCheck o = new FontCheck();

                JFrame f = new JFrame(o.getClass().getSimpleName());
                f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                f.setLocationByPlatform(true);

                f.setContentPane(o.getUI());
                f.pack();
                f.setMinimumSize(f.getSize());

                f.setVisible(true);
            }
        };
        SwingUtilities.invokeLater(r);
    }
}