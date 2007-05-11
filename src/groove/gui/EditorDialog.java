package groove.gui;

import groove.graph.Graph;
import groove.gui.jgraph.GraphJModel;
import groove.view.aspect.AspectGraph;

import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

/**
 * Dialog wrapping a graph editor, such that no file operations are possible.
 * @author Arend Rensink
 * @version $Revision: 1.1 $
 */
public class EditorDialog extends JDialog {
    /**
     * Constructs an instance of the dialog, for a given graph or rule.
     * @param owner the parent frame for the dialog
     * @param graph the input graph for the editor
     * @param type edit type (either #GRAPH_TYPE or #RULE_TYPE)
     * @throws HeadlessException
     */
    public EditorDialog(Frame owner, Graph graph, String type) throws HeadlessException {
        super(owner, true);
        this.type = type;
        this.editor = new Editor();
        editor.setEditType(type);
        this.editor.setModel(new GraphJModel(graph));
        JFrame editorFrame = editor.getFrame();
        // set the menu bar from the editor frame
        JMenuBar menuBar = editorFrame.getJMenuBar();
        menuBar.remove(0);
        setJMenuBar(menuBar);
//        // set the content pane from the editor frame
//        Container contentPane = editorFrame.getContentPane();
////        contentPane.remove();
////        contentPane.add(createToolBar(), BorderLayout.NORTH);
//        getContentPane().setLayout(new BorderLayout());
//        getContentPane().add(createToolBar(), BorderLayout.NORTH);
//        getContentPane().add(editor.getGraphPanel(), BorderLayout.CENTER);
//        getContentPane().add(editor.getStatusPanel(), BorderLayout.SOUTH);
        setContentPane(editor.createContentPanel(createToolBar()));
        // set the title from the editor frame
        setTitle(editorFrame.getTitle());
        // Set Close Operation to Exit
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                handleCancel();
            }
        });
        setLocationRelativeTo(owner);
        pack();
    }

    /** Returns the resulting graph of the editor. */
    public Graph toPlainGraph() {
        return editor.getModel().toPlainGraph();
    }

    /** Returns the resulting aspect graph of the editor. */
    public AspectGraph toAspectGraph() {
        return AspectGraph.getFactory().fromPlainGraph(toPlainGraph());
    }
    
    /** Indicates if the resulting aspect graph has syntax errors. */
    public boolean hasErrors() {
        return toAspectGraph().hasErrors();
    }
    
    /** Indicates if the underlying graph has been edited by the used during the dialog. */
    public boolean isModified() {
        return editor.isCurrentGraphModified();
    }

    /** 
     * Returns the user decision leading to the end of the dialog.
     * @return <code>true</code> if the user OK-ed the dialog
     */
    public boolean isOK() {
        return ok;
    }

    /**
     * Creates and returns the tool bar.
     */
    private JToolBar createToolBar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.add(createOkButton());
        toolbar.add(createCancelButton());
        toolbar.addSeparator();
        if (Editor.GRAPH_TYPE.equals(type)) {
            toolbar.add(editor.getGraphTypeButton());
        } else {
            toolbar.add(editor.getRuleTypeButton());
        }
        editor.addModeButtons(toolbar);
        editor.addUndoButtons(toolbar);
        editor.addCopyPasteButtons(toolbar);
        return toolbar;
    }
    
    /** Creates and returns a Cancel button, for use on the tool bar. */
    private JButton createCancelButton() {
        JButton result = new JButton(Options.CANCEL_BUTTON);
        result.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleCancel();
            }
        });
        return result;
    }
    
    /** Creates and returns an OK button, for use on the tool bar. */
    private JButton createOkButton() {
        JButton result = new JButton(Options.OK_BUTTON);
        result.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleOk();
            }
        });
        return result;
    }
    
    /**
     * Registers the effect of pressing the OK button.
     * Sets {@link #ok} to <code>true</code> and hides the dialog.
     */
    private void handleOk() {
        ok = true;
        setVisible(false);
    }
    
    /**
     * Registers the effect of cancelling.
     * Queries the user if he wants to abandon edits, sets the {@link #ok} field and hides the dialog.
     */
    private void handleCancel() {
        ok = false;
        if (isModified()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Use edited graph?",
                null,
                JOptionPane.YES_NO_CANCEL_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                ok = true;
                setVisible(false);
            } else if (confirm == JOptionPane.NO_OPTION) {
                setVisible(false);
            } 
        } else {
            setVisible(false);
        }
    }

    /** Flag recording the decision of the user on exit. */
    private boolean ok;
    /** The edit type of the dialog. */
    private final String type;
    /** The dialog wrapped in the editor. */
    private final Editor editor;
}