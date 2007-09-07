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
 * $Id: EditorDialog.java,v 1.9 2007-09-07 19:13:31 rensink Exp $
 */
package groove.gui;

import groove.graph.Graph;
import groove.graph.GraphInfo;
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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

/**
 * Dialog wrapping a graph editor, such that no file operations are possible.
 * @author Arend Rensink
 * @version $Revision: 1.9 $
 */
public class EditorDialog extends JDialog {
    /**
     * Constructs an instance of the dialog, for a given graph or rule.
     * @param owner the parent frame for the dialog
     * @param graph the input graph for the editor
     * @throws HeadlessException
     */
    public EditorDialog(Frame owner, Options options, Graph graph) throws HeadlessException {
        super(owner, true);
        this.options = options;
        this.editor = new Editor(options);
        this.editor.setPlainGraph(graph);
        JFrame editorFrame = editor.getFrame();
        setJMenuBar(createMenuBar());
        setContentPane(editor.createContentPanel(createToolBar(GraphInfo.hasGraphRole(graph))));
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
        pack();
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
     * Creates and returns the menu bar. Requires the actions to have been initialised first.
     */
    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(editor.createEditMenu());
        menuBar.add(editor.createPropertiesMenu());
        menuBar.add(editor.createDisplayMenu());
        menuBar.add(createOptionsMenu());
        menuBar.add(editor.createHelpMenu());
        return menuBar;
    }

	/**
	 * Creates and returns an options menu for the menu bar.
	 */
	private JMenu createOptionsMenu() {
        JMenu optionsMenu = new JMenu(Options.OPTIONS_MENU_NAME);
        optionsMenu.add(options.getItem(Options.PREVIEW_ON_CLOSE_OPTION));
        optionsMenu.add(options.getItem(Options.SHOW_VALUE_NODES_OPTION));
        return optionsMenu;
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
    private void handleOk() {
    	if (options.isSelected(Options.PREVIEW_ON_CLOSE_OPTION) && !editor.handlePreview(null)) {
    		return;
    	} else if (hasErrors()) {
        	JOptionPane.showMessageDialog(this, String.format("Cannot use %s with syntax errors", editor.getRole(false)));
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
    private void handleCancel() {
        ok = false;
        if (! hasErrors() && isModified()) {
            int confirm = JOptionPane.showConfirmDialog(this,
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
    
    /** Besides calling the super method, also disposes the editor frame. */
    @Override
	public void dispose() {
		super.dispose();
		editor.doQuit();
	}

	/** Flag recording the decision of the user on exit. */
    private boolean ok;
    /** Options of this dialog. */
    private final Options options;
    /** The dialog wrapped in the editor. */
    private final Editor editor;
}