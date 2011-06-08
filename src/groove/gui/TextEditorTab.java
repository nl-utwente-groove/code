package groove.gui;

import groove.gui.TabbedResourceDisplay.MainTab;
import groove.prolog.util.PrologTokenMaker;
import groove.trans.ResourceKind;

import java.awt.BorderLayout;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 * Display tab showing a text-based resource.
 * @author Michiel Hendriks
 */
public class TextEditorTab extends EditorTab implements MainTab {
    /** Creates an initially empty display. */
    public TextEditorTab(PrologDisplay display) {
        this(display, null, null);
    }

    /** 
     * Constructs a prolog editor with a given name.
     * @param display the display on which this editor is placed.
     */
    public TextEditorTab(final PrologDisplay display, String name,
            String program) {
        super(display);
        this.textArea = new TextArea();
        this.editing = name != null;
        this.textArea.setEditable(this.editing);
        setName(name);
        setBorder(null);
        setLayout(new BorderLayout());
        if (this.editing) {
            add(createToolBar(), BorderLayout.NORTH);
        }
        add(new RTextScrollPane(this.textArea, true), BorderLayout.CENTER);
        // add keyboard binding for Save and Cancel key
        InputMap focusedInputMap =
            getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        String actionName =
            Options.getSaveActionName(ResourceKind.PROLOG, false);
        focusedInputMap.put(Options.SAVE_KEY, actionName);
        focusedInputMap.put(Options.CANCEL_KEY, Options.CANCEL_EDIT_ACTION_NAME);
        getActionMap().put(actionName, getSaveAction());
        getActionMap().put(Options.CANCEL_EDIT_ACTION_NAME, getCancelAction());
        this.textArea.setProgram(program);
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
    public void setResource(String name) {
        String program =
            getSimulatorModel().getStore().getTexts(
                getDisplay().getResourceKind()).get(name);
        setName(name);
        this.textArea.setProgram(program);
    }

    @Override
    public boolean removeResource(String name) {
        boolean result = name.equals(getName());
        if (result) {
            this.textArea.setProgram(null);
        }
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
        updateDirty();
    }

    /** Returns the current program. */
    public final String getProgram() {
        return this.textArea.getText();
    }

    @Override
    protected boolean hasErrors() {
        return getSimulatorModel().getGrammar().getResource(
            getDisplay().getResourceKind(), getName()).hasErrors();
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

    @Override
    protected void saveResource() {
        getSaveAction().doSaveText(getName(), getProgram());
    }

    /** Removes this editor from the editor pane. */
    @Override
    public void dispose() {
        getDisplay().getTabPane().remove(this);
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
            ((RSyntaxDocument) getDocument()).setSyntaxStyle(new PrologTokenMaker());
            setFont(PrologDisplay.EDIT_FONT);
            setTabSize(4);
            discardAllEdits();
            getUndoAction().putValue(Action.SMALL_ICON, Icons.UNDO_ICON);
            getRedoAction().putValue(Action.SMALL_ICON, Icons.REDO_ICON);
            getCopyAction().putValue(Action.SMALL_ICON, Icons.COPY_ICON);
            getPasteAction().putValue(Action.SMALL_ICON, Icons.PASTE_ICON);
            getCutAction().putValue(Action.SMALL_ICON, Icons.CUT_ICON);
            getDeleteAction().putValue(Action.SMALL_ICON, Icons.DELETE_ICON);
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