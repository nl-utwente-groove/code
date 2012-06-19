package groove.gui;

import groove.control.parse.CtrlTokenMaker;
import groove.gui.ResourceDisplay.MainTab;
import groove.prolog.util.PrologTokenMaker;
import groove.trans.ResourceKind;
import groove.view.FormatError;
import groove.view.GrammarModel;

import java.awt.BorderLayout;
import java.util.Collection;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.TokenMaker;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 * Display tab showing a text-based resource.
 * The tab also offers the functionality to edit the resource.
 * @author Arend Rensink
 */
final public class TextTab extends ResourceTab implements MainTab {
    /** Creates an initially empty display. */
    public TextTab(ResourceDisplay display) {
        this(display, null, null);
    }

    /** 
     * Constructs a prolog editor with a given name.
     * @param display the display on which this editor is placed.
     */
    public TextTab(final ResourceDisplay display, String name, String program) {
        super(display);
        this.textArea = new TextArea();
        this.editing = name != null;
        this.textArea.setEditable(this.editing);
        setName(name);
        setBorder(null);
        setLayout(new BorderLayout());
        this.textArea.setProgram(program);
        updateErrors();
        start();
    }

    @Override
    protected JComponent getEditArea() {
        return new RTextScrollPane(this.textArea, true);
    }

    @Override
    protected Observer createErrorListener() {
        return new Observer() {
            @Override
            public void update(Observable observable, Object arg) {
                if (arg != null) {
                    FormatError error = (FormatError) arg;
                    if (error.getNumbers().size() > 1) {
                        int line = error.getNumbers().get(0);
                        int column = error.getNumbers().get(1);
                        select(line, column);
                    }
                }
            }
        };
    }

    /**
     * Creates a tool bar for the display.
     */
    @Override
    protected JToolBar createToolBar() {
        JToolBar result = super.createToolBar();
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

    @Override
    public Icon getIcon() {
        return isEditor() ? super.getIcon()
                : Icons.getMainTabIcon(getDisplay().getResourceKind());
    }

    @Override
    public boolean isEditor() {
        return this.editing;
    }

    @Override
    public boolean setResource(String name) {
        String program = null;
        if (name != null) {
            program =
                getSimulatorModel().getStore().getTexts(getResourceKind()).get(
                    name);
        }
        if (program != null) {
            setName(name);
            this.textArea.setProgram(program);
            updateErrors();
        }
        return program != null;
    }

    @Override
    public boolean removeResource(String name) {
        boolean result = name.equals(getName());
        if (result) {
            this.textArea.setProgram(null);
            updateErrors();
        }
        return result;
    }

    @Override
    public void updateGrammar(GrammarModel grammar) {
        setResource(getName());
    }

    /** Indicates if the editor is currently dirty. */
    @Override
    public final boolean isDirty() {
        return this.textArea.canUndo();
    }

    @Override
    public void setClean() {
        this.textArea.discardAllEdits();
        updateDirty();
        updateErrors();
    }

    /** Returns the current program. */
    public final String getProgram() {
        return this.textArea.getText();
    }

    @Override
    protected Collection<FormatError> getErrors() {
        String name = getName();
        if (name == null) {
            return Collections.emptySet();
        } else {
            return getDisplay().getResource(name).getErrors();
        }
    }

    /** Selects a given line in the text area. */
    public void select(int line, int column) {
        try {
            int pos = this.textArea.getLineStartOffset(line - 1);
            if (column > 0) {
                pos += column - 1;
            }
            this.textArea.select(pos, pos);
            this.textArea.requestFocusInWindow();
        } catch (BadLocationException e) {
            // do nothing
        }
    }

    @Override
    protected void saveResource() {
        getSaveAction().doSaveText(getName(), getProgram());
    }

    /** Creates a token maker for the text area of this tab. */
    protected TokenMaker createTokenMaker() {
        return getResourceKind() == ResourceKind.PROLOG
                ? new PrologTokenMaker() : new CtrlTokenMaker();
    }

    /** Creates and returns a Cancel button, for use on the tool bar. */
    private JButton createUndoButton() {
        JButton result = Options.createButton(this.textArea.getUndoAction());
        result.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateDirty();
            }
        });
        return result;
    }

    /**
     * The associated text area.
     */
    private final TextArea textArea;
    /** Flag indicating if this tab should be editing. */
    private final boolean editing;

    private class TextArea extends RSyntaxTextArea {
        public TextArea() {
            super(30, 100);
            ((RSyntaxDocument) getDocument()).setSyntaxStyle(createTokenMaker());
            setTabSize(4);
            discardAllEdits();
            getUndoAction().putValue(Action.SMALL_ICON, Icons.UNDO_ICON);
            getRedoAction().putValue(Action.SMALL_ICON, Icons.REDO_ICON);
            getCopyAction().putValue(Action.SMALL_ICON, Icons.COPY_ICON);
            getPasteAction().putValue(Action.SMALL_ICON, Icons.PASTE_ICON);
            getCutAction().putValue(Action.SMALL_ICON, Icons.CUT_ICON);
            getDeleteAction().putValue(Action.SMALL_ICON, Icons.DELETE_ICON);
            addMouseListener(new EditMouseListener());
        }

        /** 
         * Changes the edited program in the area.
         * @param program the new program; if {@code null}, the text area will
         * be disabled
         */
        public void setProgram(String program) {
            setText(program == null ? "" : program);
            setEnabled(program != null);
            discardAllEdits();
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

        @Override
        protected JPopupMenu createPopupMenu() {
            JPopupMenu result;
            if (isEditable()) {
                result = super.createPopupMenu();
                int i = 0;
                result.insert(getDisplay().getSaveAction(), i++);
                result.insert(getDisplay().getCancelEditAction(), i++);
                result.insert(new JPopupMenu.Separator(), i++);
            } else {
                result = new JPopupMenu();
                result.add(getDisplay().getEditAction());
            }
            return result;
        }
    }
}