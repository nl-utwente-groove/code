/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id: EditorDialog.java,v 1.15 2008-01-30 09:33:35 iovka Exp $
 */
package groove.gui;

import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.view.aspect.AspectGraph;

import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

/**
 * Dialog wrapping a graph editor, such that no file operations are possible.
 * @author Arend Rensink
 * @version $Revision: 1.15 $
 */
abstract public class EditorDialog {
    /**
     * Constructs an instance of the dialog, for a given graph or rule.
     * @param owner the parent frame for the dialog
     * @param graph the input graph for the editor
     * @throws HeadlessException
     */
    public EditorDialog(JFrame owner, Options options, Graph graph) throws HeadlessException {
        this.parent = owner;
        this.oldJMenuBar = parent.getJMenuBar();
        this.oldContentPane = parent.getContentPane();
        this.oldTitle = parent.getTitle();
        this.oldWindowListeners = parent.getWindowListeners();
//        this.oldDefaultCloseOperation = parent.getDefaultCloseOperation();
        this.options = options;
        this.editor = new Editor(parent, options) {
            @Override
            protected void doQuit() {
                handleCancel();
            }
        };
        this.editor.setPlainGraph(graph);
        this.newContentPane = editor.createContentPanel(createToolBar(GraphInfo.hasGraphRole(graph)));
    }

    /** Starts the dialog. */
    public void start() {
        parent.setJMenuBar(createMenuBar());
        parent.setContentPane(newContentPane);
        // set the title from the editor frame
        parent.setTitle(editor.getFrame().getTitle());
        for (WindowListener listener: oldWindowListeners) {
            parent.removeWindowListener(listener);
        }
        parent.validate();
    }

    /** Returns the resulting graph of the editor. */
    public Graph toPlainGraph() {
        return editor.getPlainGraph();
    }

    /** Returns the resulting aspect graph of the editor. */
    public AspectGraph toAspectGraph() {
        AspectGraph result = AspectGraph.getFactory().fromPlainGraph(toPlainGraph());
        result.setFixed();
        return result;
    }
    
    /** 
     * Returns the user decision leading to the end of the dialog.
     * @return <code>true</code> if the user OK-ed the dialog
     */
    public boolean isOK() {
        return ok;
    }

    /**
     * Creates and returns the menu bar. Requires the actions to have been initialised first.
     */
    private JMenuBar createMenuBar() {
        JMenuBar result = new JMenuBar();
        result.add(editor.createEditMenu());
        result.add(editor.createPropertiesMenu());
        result.add(editor.createDisplayMenu());
        result.add(createOptionsMenu());
        result.add(editor.createHelpMenu());
        return result;
    }

	/**
	 * Creates and returns an options menu for the menu bar.
	 */
	private JMenu createOptionsMenu() {
        JMenu result = new JMenu(Options.OPTIONS_MENU_NAME);
        result.setMnemonic(Options.OPTIONS_MENU_MNEMONIC);
        result.add(options.getItem(Options.PREVIEW_ON_CLOSE_OPTION));
        result.add(options.getItem(Options.SHOW_VALUE_NODES_OPTION));
        return result;
	}

    /**
     * Creates and returns the tool bar.
     */
    private JToolBar createToolBar(boolean graphRole) {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.add(createOkButton());
        toolbar.add(createCancelButton());
        toolbar.addSeparator();
        if (graphRole) {
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
     * Implements the effect of pressing the OK button.
     * Sets {@link #ok} to <code>true</code> and disposes the dialog.
     */
    void handleOk() {
    	if (options.isSelected(Options.PREVIEW_ON_CLOSE_OPTION) && !editor.handlePreview(null)) {
    		return;
    	} else if (hasErrors()) {
        	JOptionPane.showMessageDialog(parent, String.format("Cannot use %s with syntax errors", editor.getRole(false)));
        } else {
            ok = true;
        	dispose();
        }
    }
    
    /**
     * Implements the effect of cancelling.
     * Queries the user if he wants to abandon edits, 
     * sets the {@link #ok} field to <code>false</code> and disposes the dialog.
     */
    void handleCancel() {
        ok = false;
        if (! hasErrors() && isModified()) {
            int confirm = JOptionPane.showConfirmDialog(parent,
                String.format("Use edited %s?", editor.getRole(false)),
                null,
                JOptionPane.YES_NO_CANCEL_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                ok = true;
                dispose();
            } else if (confirm == JOptionPane.NO_OPTION) {
                dispose();
            } 
        } else {
            dispose();
        }
    }

    /** Indicates if the resulting aspect graph has syntax errors. */
    private boolean hasErrors() {
        return toAspectGraph().hasErrors();
    }
    
    /** Indicates if the underlying graph has been edited by the used during the dialog. */
    private boolean isModified() {
        return editor.isCurrentGraphModified();
    }

    /** Besides calling the super method, also disposes the editor frame. */
	private void dispose() {
		parent.setContentPane(oldContentPane);
//		parent.setDefaultCloseOperation(oldDefaultCloseOperation);
		for (WindowListener listener: parent.getWindowListeners()) {
		    parent.removeWindowListener(listener);
		}
		parent.setTitle(oldTitle);
		parent.setJMenuBar(oldJMenuBar);
		for (WindowListener listener: oldWindowListeners) {
		    parent.addWindowListener(listener);
		}
        parent.invalidate();
        parent.validate();
//        parent.pack();
        if (isOK()) {
            finish();
        }
	}
    
    /** Callback method invoked in case the editing finished with OK. */
    abstract protected void finish();

	/** Flag recording the decision of the user on exit. */
    private boolean ok;
    /** Options of this dialog. */
    private final Options options;
    /** The dialog wrapped in the editor. */
    private final Editor editor;
    private final JFrame parent;
    private final Container oldContentPane;
//    private final int oldDefaultCloseOperation;
    private final String oldTitle;
    private final JMenuBar oldJMenuBar;
    private final WindowListener[] oldWindowListeners;
    private final Container newContentPane;
//    private final WindowListener newWindowListener;
}