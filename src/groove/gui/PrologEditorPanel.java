package groove.gui;

import groove.gui.action.ActionStore;
import groove.gui.action.CancelEditPrologAction;
import groove.gui.action.SavePrologAction;
import groove.prolog.util.PrologTokenMaker;

import java.awt.BorderLayout;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 * Data structure to keep track of the open/loaded prolog files
 * 
 * @author Michiel Hendriks
 */
public class PrologEditorPanel extends EditorPanel<PrologDisplay> {
    /** 
     * Constructs a prolog editor with a given name.
     * @param display the display on which this editor is placed.
     */
    public PrologEditorPanel(final PrologDisplay display, String name,
            String program) {
        super(display);
        this.textArea = new PrologTextArea(program);
        this.name = name;
        setBorder(null);
        setLayout(new BorderLayout());
        add(createToolBar(), BorderLayout.NORTH);
        add(new RTextScrollPane(this.textArea, true), BorderLayout.CENTER);
    }

    /**
     * Creates a tool bar for the display.
     */
    private JToolBar createToolBar() {
        JToolBar result = Options.createToolBar();
        result.add(createSaveButton());
        result.add(createCancelButton());
        result.addSeparator();
        result.add(createUndoButton());
        result.add(this.textArea.getRedoAction());
        result.addSeparator();
        result.add(this.textArea.getCopyAction());
        result.add(this.textArea.getPasteAction());
        result.add(this.textArea.getCutAction());
        result.add(this.textArea.getDeleteAction());
        return result;
    }

    /** Indicates if the editor is currently dirty. */
    @Override
    public final boolean isDirty() {
        return this.textArea.canUndo();
    }

    @Override
    public void setClean() {
        this.textArea.discardAllEdits();
        getDisplay().updateTab(this);
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
     * Creates and shows a confirmation dialog for abandoning the currently
     * edited control program.
     */
    @Override
    public boolean confirmAbandon() {
        boolean result = true;
        if (isDirty()) {
            int answer =
                JOptionPane.showConfirmDialog(this,
                    String.format("Save changes in '%s'?", getName()), null,
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                getActions().getSavePrologAction().doSave(getName(),
                    getProgram());
            } else {
                result = answer == JOptionPane.NO_OPTION;
            }
        }
        return result;
    }

    /** Removes this editor from the editor pane. */
    @Override
    public void dispose() {
        getDisplay().getEditorPane().remove(this);
    }

    /** Creates and returns a Cancel button, for use on the tool bar. */
    private JButton createUndoButton() {
        JButton result = Options.createButton(this.textArea.getUndoAction());
        result.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                getDisplay().updateTab(PrologEditorPanel.this);
            }
        });
        return result;
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
        return getDisplay().getActions();
    }

    /**
     * The name of the editor.
     */
    private final String name;
    /**
     * The associated text area.
     */
    private final PrologTextArea textArea;

    private static class PrologTextArea extends RSyntaxTextArea {
        public PrologTextArea(String text) {
            super(30, 100);
            ((RSyntaxDocument) getDocument()).setSyntaxStyle(new PrologTokenMaker());
            setFont(PrologDisplay.EDIT_FONT);
            setText(text);
            setEditable(true);
            setEnabled(true);
            setTabSize(4);
            discardAllEdits();
            getUndoAction().putValue(Action.SMALL_ICON, Icons.UNDO_ICON);
            getRedoAction().putValue(Action.SMALL_ICON, Icons.REDO_ICON);
            getCopyAction().putValue(Action.SMALL_ICON, Icons.COPY_ICON);
            getPasteAction().putValue(Action.SMALL_ICON, Icons.PASTE_ICON);
            getCutAction().putValue(Action.SMALL_ICON, Icons.CUT_ICON);
            getDeleteAction().putValue(Action.SMALL_ICON, Icons.DELETE_ICON);
        }

        /** Returns the undo action as applied to this text area. */
        public Action getUndoAction() {
            return getAction(UNDO_ACTION);
        }

        /** Returns the redo action as applied to this text area. */
        public Action getRedoAction() {
            return getAction(REDO_ACTION);
        }

        /** Returns the copy action as applied to this text area. */
        public Action getCopyAction() {
            return getAction(COPY_ACTION);
        }

        /** Returns the paste action as applied to this text area. */
        public Action getPasteAction() {
            return getAction(PASTE_ACTION);
        }

        /** Returns the cut action as applied to this text area. */
        public Action getCutAction() {
            return getAction(CUT_ACTION);
        }

        /** Returns the delete action as applied to this text area. */
        public Action getDeleteAction() {
            return getAction(DELETE_ACTION);
        }
    }
}