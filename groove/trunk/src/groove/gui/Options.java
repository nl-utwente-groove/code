/*
 * GROOVE: GRaphs for Object Oriented VErification
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
 * $Id: Options.java,v 1.22 2007-05-11 08:22:02 rensink Exp $
 */
package groove.gui;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jgraph.graph.GraphConstants;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.DesertBlue;

/**
 * @author Arend Rensink
 * @version $Revision: 1.22 $
 */
public class Options {

	/** The default font set in the look-and-feel. */
	public static Font DEFAULT_FONT = null;
	// Menus
    /** Edit menu name */
    public static final String EDIT_MENU_NAME = "Edit";
    /** Display menu name */
    public static final String DISPLAY_MENU_NAME = "View";
    /** Explore menu name */
    public static final String EXPLORE_MENU_NAME = "Explore";
    /** File menu name */
    public static final String FILE_MENU_NAME = "File";
    /** Help menu name */
    public static final String HELP_MENU_NAME = "Help";
    /** Options menu name */
    public static final String OPTIONS_MENU_NAME = "Options";
    /** Options menu name */
    public static final String PROPERTIES_MENU_NAME = "Properties";
    /** Save menu name */
    public static final String SAVE_MENU_NAME = "Save";
    /** Set line style context menu name */
    static public final String SET_LINE_STYLE_MENU = "Set Line Style";
    /** Set layout menu name */
    public static final String SET_LAYOUT_MENU_NAME = "Set layouter";
    /** Show/Hide menu name */
    static public final String SHOW_HIDE_MENU_NAME = "Show/Hide";
    /** Verify menu name */
    public static final String VERIFY_MENU_NAME = "Verify";
    
    // Button texts
    /** Button text to confirm an action. */
    public static final String OK_BUTTON = "OK";
    /** Button text to cancel an action. */
    public static final String CANCEL_BUTTON = "Cancel";
    /** Button text to repair a syntactically faulty graph. */
    public static final String USE_BUTTON = "Use";
    /** Button text to choose in favour of something. */
    public static final String YES_BUTTON = "Yes";
    /** Button text to choose against of something. */
    public static final String NO_BUTTON = "No";
    /** Button text to always choose in favour. */
    public static final String ALWAYS_BUTTON = "Always";
    /** Button text to always choose against. */
    public static final String NEVER_BUTTON = "Never";
    /** Button text to ask the user for a decisiton. */
    public static final String ASK_BUTTON = "Ask";
    
    // Titles
    /** Label pane title */
    public static final String LABEL_PANE_TITLE = "Labels";

    // Actions
    /**
	 * About action name 
	 */
	public static final String ABOUT_ACTION_NAME = "About";
    /** Add point action name */
    public static final String ADD_POINT_ACTION = "Add Point";
    /** Apply transition action name */
    public static final String APPLY_TRANSITION_ACTION_NAME = "Apply active match";
    /** Close action name */
    public static final String CLOSE_ACTION_NAME = "Close";
    /** Copy action name */
    public static final String COPY_ACTION_NAME = "Copy";
    /** Copy action name */
    public static final String COPY_RULE_ACTION_NAME = "Copy Rule";
    /** Cut action name */
    public static final String CUT_ACTION_NAME = "Cut";
    /** Delete action name */
    public static final String DELETE_ACTION_NAME = "Delete";
    /** Delete action name */
    public static final String DELETE_RULE_ACTION_NAME = "Delete Rule";
    /** Edit action name */
    public static final String DISABLE_ACTION_NAME = "Disable Rule";
    /** Edit graph action name */
    public static final String EDIT_GRAPH_ACTION_NAME = "Edit Graph ...";
    /** Edit properties action name */
    public static final String EDIT_PROPERTIES_ACTION_NAME = "Edit Properties ...";
    /** Edge mode action name */
    public static final String EDGE_MODE_NAME = "Edge mode";
    /** Edit label action name */
    static public final String EDIT_LABEL_ACTION = "Edit Label";
    /** Edit rule action name */
    public static final String EDIT_RULE_ACTION_NAME = "Edit Rule ...";
    /** Edit state action name */
    public static final String EDIT_STATE_ACTION_NAME = "Edit State ...";
    /** Edit action name */
    public static final String EDIT_ACTION_NAME = "Edit ...";
    /** Edit action name */
    public static final String ENABLE_ACTION_NAME = "Enable Rule";
    /** Export action name */
    public static final String EXPORT_ACTION_NAME = "Export ...";
    /** Export rule action name */
    public static final String EXPORT_RULE_ACTION_NAME = "Export Rule ...";
    /** Export lts action name */
    public static final String EXPORT_LTS_ACTION_NAME = "Export LTS ...";
    /** Export state action name */
    public static final String EXPORT_STATE_ACTION_NAME = "Export State ...";
    /** Goto start state action name */
    public static final String GOTO_START_STATE_ACTION_NAME = "Go to Start State";
	/** List atomic propositions action name */
    public static final String LIST_ATOMIC_PROPOSITIONS_ACTION_NAME = "List Atom. Prop.";
    /** Load start state action name */
    public static final String LOAD_START_STATE_ACTION_NAME = "Load Start State ...";
    /** Load grammar action name */
    public static final String LOAD_GRAMMAR_ACTION_NAME = "Load Grammar ...";
    /** Name for the model checking action. */
    static public final String MODEL_CHECK_ACTION_NAME = "Verify";
    /** New action name */
    public static final String NEW_ACTION_NAME = "New";
    /** New action name */
    public static final String NEW_GRAPH_ACTION_NAME = "New Graph";
    /** New action name */
    public static final String NEW_RULE_ACTION_NAME = "New Rule";
    /** New action name */
    public static final String NEW_RULE_SYSTEM_ACTION_NAME = "New Rule System";
    /** Node mode action name */
    public static final String NODE_MODE_NAME = "Node Mode";
    /** Open action name */
    public static final String OPEN_ACTION_NAME = "Open ...";
    /** Paste action name */
    public static final String PASTE_ACTION_NAME = "Paste";
    /**
     * Preview action name 
     */
    public static final String PREVIEW_ACTION_NAME = "Preview ...";
    /** Provide ctl formula action name */
    public static final String PROVIDE_CTL_FORMULA_ACTION_NAME = "CTL Formula ...";
    /** Quit action name */
    public static final String QUIT_ACTION_NAME = "Quit";
    /** Redo action name */
    public static final String REDO_ACTION_NAME = "Redo";
    /** Refresh grammar action name */
    public static final String REFRESH_GRAMMAR_ACTION_NAME = "Refresh Grammar";
    /** Reset label position action name */
    static public final String RESET_LABEL_POSITION_ACTION = "Reset Label";
    /**
	 * Restart simulation action name 
	 */
	public static final String RESTART_ACTION_NAME = "Restart simulation";
	/** Remove point action name */
    static public final String REMOVE_POINT_ACTION = "Remove Point";
    /** Delete action name */
    public static final String RENAME_ACTION_NAME = "Rename";
    /** Delete action name */
    public static final String RENAME_RULE_ACTION_NAME = "Rename Rule";
    /**
	 * Start simulation action name 
	 */
	public static final String START_SIMULATION_ACTION_NAME = "Run Simulation";
	/**
	 * Save action name 
	 */
	public static final String SAVE_ACTION_NAME = "Save ...";
	/**
	 * Save grammar action name 
	 */
	public static final String SAVE_GRAMMAR_ACTION_NAME = "Save Grammar ...";
	/**
	 * Save lts action name 
	 */
	public static final String SAVE_LTS_ACTION_NAME = "Save LTS ...";
	/**
	 * Save state action name 
	 */
	public static final String SAVE_STATE_ACTION_NAME = "Save State ...";
	/**
	 * Scroll to action name 
	 */
	static public final String SCROLL_TO_ACTION_NAME = "Scroll To Current";
	/**
     * Graph mode action name 
     */
    public static final String SET_GRAPH_TYPE_ACTION_NAME = "Set graph type editing";
    /**
     * Rule mode action name 
     */
    public static final String SET_RULE_TYPE_ACTION_NAME = "Set rule type editing";
    /**
	 * Edit action name 
	 */
	public static final String SYSTEM_PROPERTIES_ACTION_NAME = "System Properties ...";
    /** Undo action name */
    public static final String UNDO_ACTION_NAME = "Undo";
    /** Select mode action name */
    public static final String SELECT_MODE_NAME = "Selection mode";
    /**
	 * Apply keystroke 
	 */
	static public final KeyStroke APPLY_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_MASK);
	/**
	 * Copy keystroke 
	 */
	public static final KeyStroke COPY_KEY = KeyStroke.getKeyStroke("control C");
	/**
	 * Cut keystroke 
	 */
	public static final KeyStroke CUT_KEY = KeyStroke.getKeyStroke("control X");
	/**
	 * Delete keystroke 
	 */
	public static final KeyStroke DELETE_KEY = KeyStroke.getKeyStroke("DELETE");
	/**
	 * Edge mode keystroke 
	 */
	public static final KeyStroke EDGE_MODE_KEY = KeyStroke.getKeyStroke("control shift E");
	/**
	 * Edit keystroke 
	 */
	public static final KeyStroke EDIT_KEY = KeyStroke.getKeyStroke("control E");
    /** Edit label keystroke */
    public static final KeyStroke EDIT_LABEL_KEY = KeyStroke.getKeyStroke("F2");
	/**
	 * Export keystroke 
	 */
	public static final KeyStroke EXPORT_KEY = KeyStroke.getKeyStroke("control alt S");
    /** Goto start state keystroke */
    public static final KeyStroke GOTO_START_STATE_KEY = KeyStroke.getKeyStroke("control G");
    /**
     * Insert keystroke 
     */
    public static final KeyStroke INSERT_KEY = KeyStroke.getKeyStroke("INSERT");
	/** New keystroke */
    public static final KeyStroke NEW_KEY = KeyStroke.getKeyStroke("control N");
    /**
	 * Node mode keystroke 
	 */
	public static final KeyStroke NODE_MODE_KEY = KeyStroke.getKeyStroke("control shift N");
	/** Open keystroke */
    public static final KeyStroke OPEN_KEY = KeyStroke.getKeyStroke("control O");
    /**
	 * Paste keystroke 
	 */
	public static final KeyStroke PASTE_KEY = KeyStroke.getKeyStroke("control V");
	/** Quit keystroke */
    public static final KeyStroke QUIT_KEY = KeyStroke.getKeyStroke("control Q");
    /** Redo keystroke  */
	public static final KeyStroke REDO_KEY = KeyStroke.getKeyStroke("control Y");
	/** Refresh keystroke  */
	public static final KeyStroke REFRESH_KEY = KeyStroke.getKeyStroke("F5");
	/** Relabel keystroke */
    public static final KeyStroke RELABEL_KEY = KeyStroke.getKeyStroke("F2");
    /** Save keystroke */
	public static final KeyStroke SAVE_KEY = KeyStroke.getKeyStroke("control S");
	/** Select mode keystroke */
    public static final KeyStroke SELECT_MODE_KEY = KeyStroke.getKeyStroke("control shift S");
    /** Run keystroke */
    public static final KeyStroke START_SIMULATION_KEY = KeyStroke.getKeyStroke("F11");
    /** Undo keystroke */
    public static final KeyStroke UNDO_KEY = KeyStroke.getKeyStroke("control Z");
    /** Orthogonal line style keystroke */
    public static final KeyStroke ORTHOGONAL_LINE_STYLE_KEY = KeyStroke.getKeyStroke("control 1");
    /** Spline line style keystroke */
    public static final KeyStroke SPLINE_LINE_STYLE_KEY = KeyStroke.getKeyStroke("control 2");
    /** Bezier line style keystroke */
    public static final KeyStroke BEZIER_LINE_STYLE_KEY = KeyStroke.getKeyStroke("control 3");

    /** Indication for an empty label in a list of labels. */
    static public final String EMPTY_LABEL_TEXT = "(empty)";
    /** Indication for no label in a list of labels. */
    static public final String NO_LABEL_TEXT = "(none)";
    /** The name of the Bezier line style. */
    static public final String BEZIER_LINE_STYLE_NAME = "Bezier";
    /** The name of the Spline line style. */
    static public final String SPLINE_LINE_STYLE_NAME = "Spline";
    /** The name of the Orthogonal line style. */
    static public final String ORTHOGONAL_LINE_STYLE_NAME = "Orthogonal";
    /** Name for the imaging action. */
    static public final String IMAGE_ACTION_NAME = "Image";

    /** Show anchors option */
    static public final String SHOW_ANCHORS_OPTION = "Show anchors";
    /** Show node ids option */
    static public final String SHOW_NODE_IDS_OPTION = "Show node identities";
    /** Show state ids option */
    static public final String SHOW_STATE_IDS_OPTION = "Show state identities";
    /** Vertices are labels options */
    static public final String VERTEX_LABEL_OPTION = "Allow node labels";
    /** Show aspects in graphs and rules option */
    static public final String SHOW_ASPECTS_OPTION = "Show aspect prefixes";
    /** Show remark nodes and edges. */
    static public final String SHOW_REMARKS_OPTION = "Show remarks";
    /** Parse attributed graphs option */
    static public final String IS_ATTRIBUTED_OPTION = "Parse as attributed graph";
    /** Always start simulation after changes. */
    static public final String START_SIMULATION_OPTION = "Start simulation?";
    /** Automatically stop simulation at changes to the rule system. */
    static public final String STOP_SIMULATION_OPTION = "Stop simulation?";
    /** Always delete rules without confirmation. */
    static public final String DELETE_RULE_OPTION = "Delete rule?";
    /** Always repair aspect errors in edited graph. */
    static public final String REPAIR_ON_SAVE_OPTION = "Repair low-level syntax errors?";
    /** Always repair aspect errors in edited graph. */
    static public final String REPLACE_BY_PREVIEW_OPTION = "Replace edited graph by preview?";
    /** Always replace edited rules. */
    static public final String REPLACE_RULE_OPTION = "Replace edited rule?";
    /** Always replace edited rules. */
    static public final String REPLACE_START_GRAPH_OPTION = "Replace start graph?";

    /**
     * Convenience method to convert line style codes to names.
     * The line style should equal one of the styles in {@link GraphConstants}.
     * @param lineStyle the integer value representing a line style
     * @return the String representing of the corresponding line style
     * @throws IllegalArgumentException if the line style is not recognized
     * @see GraphConstants#STYLE_BEZIER
     * @see GraphConstants#STYLE_SPLINE
     * @see GraphConstants#STYLE_ORTHOGONAL
     */
    static public String getLineStyleName(int lineStyle) {
        switch (lineStyle) {
        case GraphConstants.STYLE_BEZIER : return BEZIER_LINE_STYLE_NAME;
        case GraphConstants.STYLE_SPLINE : return SPLINE_LINE_STYLE_NAME;
        case GraphConstants.STYLE_ORTHOGONAL : return ORTHOGONAL_LINE_STYLE_NAME;
        default : throw new IllegalArgumentException(""+lineStyle+" is not a recognized line style");
        }
    }
    
    /**
     * Convenienct method to convert line style codes to key strokes.
     * The line style should equal one of the styles in {@link GraphConstants}.
     * @param lineStyle the integer value representing a line style
     * @return the keystroke of the corresponding line style
     * @throws IllegalArgumentException if the line style is not recognized
     * @see GraphConstants#STYLE_BEZIER
     * @see GraphConstants#STYLE_SPLINE
     * @see GraphConstants#STYLE_ORTHOGONAL
     */
    static public KeyStroke getLineStyleKey(int lineStyle) {
        switch (lineStyle) {
        case GraphConstants.STYLE_BEZIER : return BEZIER_LINE_STYLE_KEY;
        case GraphConstants.STYLE_SPLINE : return SPLINE_LINE_STYLE_KEY;
        case GraphConstants.STYLE_ORTHOGONAL : return ORTHOGONAL_LINE_STYLE_KEY;
        default : throw new IllegalArgumentException(""+lineStyle+" is not a recognized line style");
        }
    }

    /**
	 * Dummy method to force static initialisation
	 */
	public static void initLookAndFeel() {
		try
    	{
			// LAF specific options that should be done before setting the LAF go here
    		PlasticLookAndFeel.setCurrentTheme(new DesertBlue());
    		// set default font to LAF font
    		Options.DEFAULT_FONT = PlasticLookAndFeel.getCurrentTheme().getUserTextFont();
    		// Set the look and feel
    		UIManager.setLookAndFeel(new com.jgoodies.looks.plastic.PlasticLookAndFeel());
    	}
		catch(UnsupportedLookAndFeelException e) {
    		// nothing to do here except not crash on the exception
    	}
	}

	/** Creates an initialised options object. */ 
    public Options() {
		addCheckbox(SHOW_NODE_IDS_OPTION);
		addCheckbox(SHOW_ANCHORS_OPTION);
		addCheckbox(SHOW_ASPECTS_OPTION);
		addCheckbox(SHOW_REMARKS_OPTION);
		addCheckbox(VERTEX_LABEL_OPTION);
		addCheckbox(SHOW_STATE_IDS_OPTION);
		addCheckbox(IS_ATTRIBUTED_OPTION);
		addBehaviour(STOP_SIMULATION_OPTION, 2);
		addBehaviour(START_SIMULATION_OPTION, 3);
		addBehaviour(DELETE_RULE_OPTION, 2);
		addBehaviour(REPAIR_ON_SAVE_OPTION, 2);
		addBehaviour(REPLACE_BY_PREVIEW_OPTION, 2);
		addBehaviour(REPLACE_RULE_OPTION, 3);
		addBehaviour(REPLACE_START_GRAPH_OPTION, 2);
	}

    /**
     * Adds a checkbox item with a given name to the options, and returns the 
     * associated (fresh) menu item.
     * @param name the name of the checkbox menu item to add
     * @return the added {@link javax.swing.JCheckBoxMenuItem}
     */
    private final JCheckBoxMenuItem addCheckbox(String name) {
    	JCheckBoxMenuItem result = new JCheckBoxMenuItem(name); 
    	itemMap.put(name, result);
    	return result;
    }

    /**
     * Adds abehaviour menu with a given name to the options, and returns the 
     * associated (fresh) menu item.
     * @param name the name of the behaviour menu item to add
     * @return the added {@link javax.swing.JCheckBoxMenuItem}
     */
    private final BehaviourOption addBehaviour(String name, int optionCount) {
    	BehaviourOption result = new BehaviourOption(name, optionCount); 
    	itemMap.put(name, result);
    	return result;
    }
    
    /**
     * Returns the menu item associated with a given name, if any.
     * @param name the name of the checkbox item looked for
     * @return the {@link javax.swing.JCheckBoxMenuItem} with the given name
     * if it exists, or <tt>null</tt> otherwise
     */
    public JMenuItem getItem(String name) {
    	return itemMap.get(name);
    }
    
    /**
     * Returns the set of menu items available.
     * @return the set of menu items available
     */
    public Collection<JMenuItem> getItemSet() {
    	return itemMap.values();
    }

    /**
     * Returns the current selection value of a given options name.
     * @param name the name of the checkbox menu item for which to check its value
     * @return the value of the checkbox item with the given name
     */
    public boolean isSelected(String name) {
    	return itemMap.get(name).isSelected();
    }

    /**
     * Sets the selection of a given option.
     * @param name the name of the menu item for which to set the value
     * @param selected the new selection value of the menu item
     */
    public void setSelected(String name, boolean selected) {
    	itemMap.get(name).setSelected(selected);
    }

    /**
     * Returns the current value of a given options name.
     * If the option is a checkbox menu, the value is <code>0</code> for <code>false</code>
     * and <code>1</code> for <code>true</code>.
     * @param name the name of the checkbox menu item for which to get the value
     * @return the current value of the checkbox item with the given name
     */
    public int getValue(String name) {
    	JMenuItem item = itemMap.get(name);
    	if (item instanceof BehaviourOption) {
    		return ((BehaviourOption) item).getValue();
    	} else {
    		return item.isSelected() ? 1 : 0;
    	}
    }

    /**
     * Sets the value of a given option.
     * If the option is a checkbox menu item, it is set to <code>true</code> for
     * any value greater than 0.
     * @param name the name of the menu item for which to set the value
     * @param value the new value of the menu item
     */
    public void setValue(String name, int value) {
    	JMenuItem item = itemMap.get(name);
    	if (item instanceof BehaviourOption) {
    		((BehaviourOption) item).setValue(value);
    	} else {
    		item.setSelected(value > 0);
    	}
    }

    /** Returns a map from option keys to the enabled status of the option. */
    @Override
	public String toString() {
    	Map<String,Boolean> result = new HashMap<String,Boolean>();
    	for (Map.Entry<String,JMenuItem> entry: itemMap.entrySet()) {
    		result.put(entry.getKey(), entry.getValue().isSelected());
    	}
    	return result.toString();
	}

	/**
     * Map from option names to menu items.
     */
    private Map<String,JMenuItem> itemMap = new LinkedHashMap<String,JMenuItem>();
}
