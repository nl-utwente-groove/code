package groove.gui;

import groove.gui.action.ActionStore;
import groove.gui.action.CancelEditPrologAction;
import groove.gui.action.SavePrologAction;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 * Data structure to keep track of the open/loaded prolog files
 * 
 * @author Michiel Hendriks
 */
public class PrologEditor extends JPanel {
    /** 
     * Constructs a prolog editor with a given name.
     * @param display the display on which this editor is placed.
     */
    public PrologEditor(final PrologDisplay display, String name, String program) {
        this.display = display;
        this.name = name;
        this.textArea = new RSyntaxTextArea(25, 100);
        this.textArea.setFont(PrologDisplay.EDIT_FONT);
        this.textArea.setText(program);
        this.textArea.setEditable(true);
        this.textArea.setEnabled(true);
        this.textArea.setTabSize(4);
        this.textArea.discardAllEdits();
        setBorder(null);
        setLayout(new BorderLayout());
        add(createToolBar(), BorderLayout.NORTH);
        add(new RTextScrollPane(this.textArea, true), BorderLayout.CENTER);
        this.textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent arg0) {
                display.updateTab(PrologEditor.this);
            }

            public void insertUpdate(DocumentEvent arg0) {
                display.updateTab(PrologEditor.this);
            }

            public void removeUpdate(DocumentEvent arg0) {
                display.updateTab(PrologEditor.this);
            }
        });
    }

    /**
     * Creates a tool bar for the display.
     */
    private JToolBar createToolBar() {
        JToolBar result = Options.createToolBar();
        result.add(createSaveButton());
        result.add(createCancelButton());
        return result;
    }

    /** Indicates if the editor is currently dirty. */
    public final boolean isDirty() {
        return this.textArea.canUndo();
    }

    /** Returns the file from which the editor was loaded. */
    @Override
    public final String getName() {
        return this.name;
    }

    /** Returns the current program. */
    public final String getProgram() {
        return this.textArea.getText();
    }

    /** Selects a given line in the text area. */
    public void select(int line, int column) {
        try {
            int pos = this.textArea.getLineStartOffset(line - 1) + column - 1;
            this.textArea.select(pos, pos);
            this.textArea.requestFocusInWindow();
        } catch (BadLocationException e) {
            // do nothing
        }
    }

    /**
     * Attempts to cancel the editing action.
     * Optionally saves the content if it is dirty.
     * @param confirm indicates if the user should be asked for confirmation
     * @return if editing was indeed stopped
     */
    public boolean cancelEditing(boolean confirm) {
        boolean result = false;
        if (!confirm || confirmAbandon()) {
            dispose();
            result = true;
        }
        return result;
    }

    /**
     * Creates and shows a confirmation dialog for abandoning the currently
     * edited control program.
     */
    public boolean confirmAbandon() {
        boolean result = true;
        if (isDirty()) {
            int answer =
                JOptionPane.showConfirmDialog(this,
                    String.format("Save changes in '%s'?", getName()), null,
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                this.display.getActions().getSavePrologAction().doSave(
                    getName(), getProgram());
            } else {
                result = answer == JOptionPane.NO_OPTION;
            }
        }
        return result;
    }

    /** Removes this editor from the editor pane. */
    public void dispose() {
        this.display.getEditorPane().remove(this);
    }

    /** Discards the edit history. */
    public void discardEdits() {
        this.textArea.discardAllEdits();
        this.display.updateTab(this);
    }

    /** Creates and returns a Cancel button, for use on the tool bar. */
    private JButton createCancelButton() {
        return Options.createButton(getCancelAction());
    }

    /** Creates and returns the save action. */
    private CancelEditPrologAction getCancelAction() {
        CancelEditPrologAction result =
            getActions().getCancelEditPrologAction();
        result.refresh();
        return result;
    }

    /** Creates and returns an OK button, for use on the tool bar. */
    private JButton createSaveButton() {
        return Options.createButton(getSaveAction());
    }

    /** Creates and returns the save action. */
    private SavePrologAction getSaveAction() {
        SavePrologAction result = getActions().getSavePrologAction();
        result.refresh();
        return result;
    }

    /** Convenience method to retrieve the underlying simulator. */
    private ActionStore getActions() {
        return this.display.getActions();
    }

    /**
     * The display on which the editor is placed.
     */
    private final PrologDisplay display;
    /**
     * The name of the editor.
     */
    private final String name;
    /**
     * The associated text area.
     */
    private final RSyntaxTextArea textArea;
}