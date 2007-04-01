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
 * $Id: Options.java,v 1.4 2007-04-01 12:50:29 rensink Exp $
 */
package groove.gui;

import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.KeyStroke;

import org.jgraph.graph.GraphConstants;

/**
 * @author Arend Rensink
 * @version $Revision: 1.4 $
 */
public class Options {
    /** Edit menu name */
    public static final String EDIT_MENU_NAME = "Edit";
    /** Display menu name */
    public static final String DISPLAY_MENU_NAME = "View";
    /** Explore menu name */
    public static final String EXPLORE_MENU_NAME = "Explore";
    /** File menu name */
    public static final String FILE_MENU_NAME = "File";
    /** Set line style context menu name */
    static public final String SET_LINE_STYLE_MENU = "Set Line Style";
    /** Show/Hide menu name */
    static public final String SHOW_HIDE_MENU_NAME = "Show/Hide";
    /** Set layout menu name */
    public static final String SET_LAYOUT_MENU_NAME = "Set layouter";
    /** Save menu name */
    public static final String SAVE_MENU_NAME = "Save";
    /** Options menu name */
    public static final String OPTIONS_MENU_NAME = "Options";
    /** Verify menu name */
    public static final String VERIFY_MENU_NAME = "Verify";
    
    // Titles
    /** Label pane title */
    public static final String LABEL_PANE_TITLE = "Labels";

    // Action names
    /** Add point action name */
    public static final String ADD_POINT_ACTION = "Add Point";
    /** Apply transition action name */
    public static final String APPLY_TRANSITION_ACTION_NAME = "Apply active rule";
    /** Close action name */
    public static final String CLOSE_ACTION_NAME = "Close";
    /** Copy action name */
    public static final String COPY_ACTION_NAME = "Copy";
    /** Cut action name */
    public static final String CUT_ACTION_NAME = "Cut";
    /** Delete action name */
    public static final String DELETE_ACTION_NAME = "Delete";
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
    /** Export action name */
    public static final String EXPORT_ACTION_NAME = "Export ...";
    /** Export rule action name */
    public static final String EXPORT_RULE_ACTION_NAME = "Export Rule ...";
    /** Export lts action name */
    public static final String EXPORT_LTS_ACTION_NAME = "Export LTS ...";
    /** Export state action name */
    public static final String EXPORT_STATE_ACTION_NAME = "Export State ...";
    /** Goto start state action name */
    public static final String GOTO_START_STATE_ACTION_NAME = "Go to start state";
    /** List atomic propositions action name */
    public static final String LIST_ATOMIC_PROPOSITIONS_ACTION_NAME = "List Atom. Prop.";
    /** Load start state action name */
    public static final String LOAD_START_STATE_ACTION_NAME = "Load start state ...";
    /** Load grammar action name */
    public static final String LOAD_GRAMMAR_ACTION_NAME = "Load Grammar ...";
    /** Name for the model checking action. */
    static public final String MODEL_CHECK_ACTION_NAME = "Verify";
    /** New action name */
    public static final String NEW_ACTION_NAME = "New";
    /** Node mode action name */
    public static final String NODE_MODE_NAME = "Node mode";
    /** Open action name */
    public static final String OPEN_ACTION_NAME = "Open ...";
    /** Pares action name */
    public static final String PARSE_ACTION_NAME = "Paste";
    /** Paste action name */
    public static final String PASTE_ACTION_NAME = "Paste";
    /** Provide ctl formula action name */
    public static final String PROVIDE_CTL_FORMULA_ACTION_NAME = "CTL formula ...";
    /** Quit action name */
    public static final String QUIT_ACTION_NAME = "Quit";
    /** Save action name */
    public static final String SAVE_ACTION_NAME = "Save ...";
    /** Save grammar action name */
    public static final String SAVE_GRAMMAR_ACTION_NAME = "Save Grammar ...";
    /** Save lts action name */
    public static final String SAVE_LTS_ACTION_NAME = "Save LTS ...";
    /** Save state action name */
    public static final String SAVE_STATE_ACTION_NAME = "Save State ...";
    /** Scroll to action name */
    static public final String SCROLL_TO_ACTION_NAME = "Scroll To Current";
    /** Stop edit action name */
    public static final String STOP_EDIT_ACTION_NAME = "Close";
    /** Redo action name */
    public static final String REDO_ACTION_NAME = "Redo";
    /** Refresh grammar action name */
    public static final String REFRESH_GRAMMAR_ACTION_NAME = "Refresh Grammar";
    /** Reset label position action name */
    static public final String RESET_LABEL_POSITION_ACTION = "Reset Label";
    /** Remove point action name */
    static public final String REMOVE_POINT_ACTION = "Remove Point";
    /** View action name */
    public static final String VIEW_ACTION_NAME = "View as rule";
    /** Undo action name */
    public static final String UNDO_ACTION_NAME = "Undo";
    /** Select mode action name */
    public static final String SELECT_MODE_NAME = "Selection mode";
    /** Save keystroke */
    public static final KeyStroke SAVE_KEY = KeyStroke.getKeyStroke("control S");
    /** New keystroke */
    public static final KeyStroke NEW_KEY = KeyStroke.getKeyStroke("control N");
    /** Open keystroke */
    public static final KeyStroke OPEN_KEY = KeyStroke.getKeyStroke("control O");
    /** Quit keystroke */
    public static final KeyStroke QUIT_KEY = KeyStroke.getKeyStroke("control Q");
    /** Export keystroke */
    public static final KeyStroke EXPORT_KEY = KeyStroke.getKeyStroke("control alt S");
    /** Relabel keystroke */
    public static final KeyStroke RELABEL_KEY = KeyStroke.getKeyStroke("F2");
    /** Refresh keystroke */
    public static final KeyStroke REFRESH_KEY = KeyStroke.getKeyStroke("F5");
    /** Select mode keystroke */
    public static final KeyStroke SELECT_MODE_KEY = KeyStroke.getKeyStroke("control shift S");
    /** Node mode keystroke */
    public static final KeyStroke NODE_MODE_KEY = KeyStroke.getKeyStroke("control shift N");
    /** Edge mode keystroke */
    public static final KeyStroke EDGE_MODE_KEY = KeyStroke.getKeyStroke("control shift E");
    /** Undo keystroke */
    public static final KeyStroke UNDO_KEY = KeyStroke.getKeyStroke("control Z");
    /** Redo keystroke */
    public static final KeyStroke REDO_KEY = KeyStroke.getKeyStroke("control Y");
    /** Cut keystroke */
    public static final KeyStroke CUT_KEY = KeyStroke.getKeyStroke("control X");
    /** Copy keystroke */
    public static final KeyStroke COPY_KEY = KeyStroke.getKeyStroke("control C");
    /** Paste keystroke */
    public static final KeyStroke PASTE_KEY = KeyStroke.getKeyStroke("control V");
    /** Delete keystroke */
    public static final KeyStroke DELETE_KEY = KeyStroke.getKeyStroke("DELETE");
    /** Edit keystroke */
    public static final KeyStroke EDIT_KEY = KeyStroke.getKeyStroke("control E");
    /** Apply keystroke */
    static public final KeyStroke APPLY_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_MASK);
    /** Orthogonal line style keystroke */
    public static final KeyStroke ORTHOGONAL_LINE_STYLE_KEY = KeyStroke.getKeyStroke("control 1");
    /** Spline line style keystroke */
    public static final KeyStroke SPLINE_LINE_STYLE_KEY = KeyStroke.getKeyStroke("control 2");
    /** Bezier line style keystroke */
    public static final KeyStroke BEZIER_LINE_STYLE_KEY = KeyStroke.getKeyStroke("control 3");
    /** Edit label keystroke */
    public static final KeyStroke EDIT_LABEL_KEY = KeyStroke.getKeyStroke("F2");
    /** Goto start state keystroke */
    public static final KeyStroke GOTO_START_STATE_KEY = KeyStroke.getKeyStroke("control G");

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
    /** Parse attributed graphs option */
    static public final String PARSE_ATTRIBUTES_OPTION = "Parse as attributed graph";

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

    /** Creates an initialised options object. */ 
    public Options() {
		add(SHOW_NODE_IDS_OPTION);
		add(SHOW_ANCHORS_OPTION);
		add(SHOW_ASPECTS_OPTION);
		add(VERTEX_LABEL_OPTION);
		add(SHOW_STATE_IDS_OPTION);
		add(PARSE_ATTRIBUTES_OPTION);
	}

    /**
     * Adds an option name to the options, and returns the 
     * associated (fresh) menu item.
     * @param name the name of the checkbox menu item to add
     * @return the added {@link javax.swing.JCheckBoxMenuItem}
     */
    public final JCheckBoxMenuItem add(String name) {
    	JCheckBoxMenuItem result = new JCheckBoxMenuItem(name); 
    	itemMap.put(name, result);
    	return result;
    }
    
    /**
     * Returns the menu item associated with a given name, if any.
     * @param name the name of the checkbox item looked for
     * @return the {@link javax.swing.JCheckBoxMenuItem} with the given name
     * if it exists, or <tt>null</tt> otherwise
     */
    public JCheckBoxMenuItem getItem(String name) {
    	return itemMap.get(name);
    }
    
    /**
     * Returns the set of menu items available.
     * @return the set of menu items available
     */
    public Collection<JCheckBoxMenuItem> getItemSet() {
    	return itemMap.values();
    }
    
    /**
     * Returns the current value of a given options name.
     * @param name the name of the checkbox menu item for which to check its value
     * @return the value of the checkbox item with the given name
     */
    public boolean getValue(String name) {
    	return itemMap.get(name).isSelected();
    }

    /**
     * Map from option names to menu items.
     */
    private Map<String,JCheckBoxMenuItem> itemMap = new LinkedHashMap<String,JCheckBoxMenuItem>();
}
