/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: Options.java,v 1.38 2008-03-05 11:29:42 rensink Exp $
 */
package groove.gui;

import groove.gui.jgraph.JAttr;
import groove.util.ExprParser;
import groove.view.FormatException;

import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.jgraph.graph.GraphConstants;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.DesertBlue;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
public class Options implements Cloneable {
    /**
     * Callback method to determine whether a mouse event could be intended to
     * edit edge points.
     */
    static public boolean isPointEditEvent(MouseEvent evt) {
        return evt.getButton() == MouseEvent.BUTTON1 && evt.isAltDown();
    }

    /** The default font set in the look-and-feel. */
    public static Font DEFAULT_FONT = null;
    // Menus
    /** Edit menu name */
    public static final String EDIT_MENU_NAME = "Edit";
    /** Edit menu mnemonic. */
    static public final int EDIT_MENU_MNEMONIC = KeyEvent.VK_E;
    /** Display menu name */
    public static final String DISPLAY_MENU_NAME = "View";
    /** Display (i.e., View) menu mnemonic. */
    static public final int DISPLAY_MENU_MNEMONIC = KeyEvent.VK_V;
    /** Explore menu name */
    public static final String EXPLORE_MENU_NAME = "Explore";
    /** Explore menu mnemonic. */
    static public final int EXPLORE_MENU_MNEMONIC = KeyEvent.VK_X;
    /** File menu name */
    public static final String FILE_MENU_NAME = "File";
    /** File menu mnemonic. */
    static public final int FILE_MENU_MNEMONIC = KeyEvent.VK_F;
    /** Open Recent menu name * */
    public static final String OPEN_RECENT_MENU_NAME = "Open Recent..";
    /** Open Recent menu mnemonic. */
    static public final int OPEN_RECENT_MENU_MNEMONIC = KeyEvent.VK_R;
    /** Help menu name */
    public static final String HELP_MENU_NAME = "Help";
    /** Help menu mnemonic. */
    static public final int HELP_MENU_MNEMONIC = KeyEvent.VK_H;
    /** Create (i.e., New) menu name. */
    static public final String CREATE_MENU_NAME = "New";
    /** Create (i.e., New) menu mnemonic. */
    static public final int CREATE_MENU_MNEMONIC = KeyEvent.VK_N;
    /** Options menu name */
    public static final String OPTIONS_MENU_NAME = "Options";
    /** Options menu mnemonic. */
    static public final int OPTIONS_MENU_MNEMONIC = KeyEvent.VK_O;
    /** Options menu name */
    public static final String PROPERTIES_MENU_NAME = "Properties";
    /** Options menu mnemonic. */
    static public final int PROPERTIES_MENU_MNEMONIC = KeyEvent.VK_P;
    /** Set line style context menu name */
    static public final String SET_LINE_STYLE_MENU = "Set Line Style";
    /** Set layout menu name */
    public static final String SET_LAYOUT_MENU_NAME = "Set layouter";
    /** Show/Hide menu name */
    static public final String SHOW_HIDE_MENU_NAME = "Show/Hide";
    /** Show/Hide menu mnemonic */
    static public final int SHOW_HIDE_MENU_MNEMONIC = KeyEvent.VK_S;
    /** Verify menu name */
    public static final String VERIFY_MENU_NAME = "Verify";
    /** Verify menu mnemonic. */
    static public final int VERIFY_MENU_MNEMONIC = KeyEvent.VK_Y;
    /** For externally contributed commands */
    public static final String EXTERNAL_MENU_NAME = "External";

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
    /** Button text to ask the user for a decision. */
    public static final String ASK_BUTTON = "Ask";

    // Titles
    /** Label pane title */
    public static final String LABEL_PANE_TITLE = "Labels";
    /** States pane title */
    public static final String STATES_PANE_TITLE = "Graphs";
    /** Rules pane title */
    public static final String RULES_PANE_TITLE = "Rules";

    // Actions
    /**
     * About action name
     */
    public static final String ABOUT_ACTION_NAME = "About";
    /** Add point action name */
    public static final String ADD_POINT_ACTION = "Add Point";
    /** Apply transition action name */
    public static final String APPLY_TRANSITION_ACTION_NAME =
        "Apply active match";
    /** Back action name */
    public static final String BACK_ACTION_NAME = "Back";
    /** Action name for cancelling an edit. */
    public static final String CANCEL_EDIT_ACTION_NAME = "Cancel Edit";
    /** Close action name */
    public static final String CLOSE_ACTION_NAME = "Close";
    /** Copy action name */
    public static final String COPY_ACTION_NAME = "Copy";
    /** Name of the "Copy Control Program" action. */
    public static final String COPY_CONTROL_ACTION_NAME = "Copy Control";
    /** Name of the "Copy Graph" action. */
    public static final String COPY_GRAPH_ACTION_NAME = "Copy Graph";
    /** Copy rule action name */
    public static final String COPY_RULE_ACTION_NAME = "Copy Rule";
    /** Name of the "Copy Type Graph" action. */
    public static final String COPY_TYPE_ACTION_NAME = "Copy Type";
    /** Cut action name */
    public static final String CUT_ACTION_NAME = "Cut";
    /** Default exploration action name */
    public static final String DEFAULT_EXPLORATION_ACTION_NAME =
        "Explore State Space";
    /** Delete action name */
    public static final String DELETE_ACTION_NAME = "Delete";
    /** Delete control program action name */
    public static final String DELETE_CONTROL_ACTION_NAME = "Delete Control";
    /** Delete graph action name */
    public static final String DELETE_GRAPH_ACTION_NAME = "Delete Graph";
    /** Delete rule action name */
    public static final String DELETE_RULE_ACTION_NAME = "Delete Rule";
    /** Delete type graph action name */
    public static final String DELETE_TYPE_ACTION_NAME = "Delete Type";
    /** Name of the "Disable Control" action. */
    public static final String DISABLE_CONTROL_ACTION_NAME = "Disable Control";
    /** Name of the "Disable Rule" action. */
    public static final String DISABLE_RULE_ACTION_NAME = "Disable Rule";
    /** Name of the "Disable Type Graph" action. */
    public static final String DISABLE_TYPE_ACTION_NAME = "Disable Typing";
    /** Edge mode action name */
    public static final String EDGE_MODE_NAME = "Edge mode";
    /** Edit action name */
    public static final String EDIT_ACTION_NAME = "Edit ...";
    /** Edit control action name */
    public static final String EDIT_CONTROL_ACTION_NAME = "Edit Control ...";
    /** Edit graph action name */
    public static final String EDIT_GRAPH_ACTION_NAME = "Edit Graph ...";
    /** Edit label action name */
    static public final String EDIT_LABEL_ACTION = "Edit Label";
    /** Edit rule action name */
    public static final String EDIT_RULE_ACTION_NAME = "Edit Rule ...";
    /** Edit state action name */
    public static final String EDIT_STATE_ACTION_NAME = "Edit Graph ...";
    /** Edit type action name */
    public static final String EDIT_TYPE_ACTION_NAME = "Edit Type ...";
    /** Name of the "Enable Control Program" action */
    public static final String ENABLE_CONTROL_ACTION_NAME = "Enable Control";
    /** Name of the "Enable Rule" action */
    public static final String ENABLE_RULE_ACTION_NAME = "Enable Rule";
    /** Name of the "Enable Type Graph" action */
    public static final String ENABLE_TYPE_ACTION_NAME = "Enable Typing";
    /** Exploration dialog action name */
    public static final String EXPLORATION_DIALOG_ACTION_NAME =
        "Customize Exploration ...";
    /** Exploration statistics dialog action name */
    public static final String EXPLORATION_STATS_DIALOG_ACTION_NAME =
        "Last Exploration Statistics ...";
    /** Export action name */
    public static final String EXPORT_ACTION_NAME = "Export ...";
    /** Export control action name */
    public static final String EXPORT_CONTROL_ACTION_NAME =
        "Export Control Automaton ...";
    /** Export rule action name */
    public static final String EXPORT_RULE_ACTION_NAME = "Export Rule ...";
    /** Export lts action name */
    public static final String EXPORT_LTS_ACTION_NAME = "Export LTS ...";
    /** Export state action name */
    public static final String EXPORT_STATE_ACTION_NAME = "Export State ...";
    /** Export type action name */
    public static final String EXPORT_TYPE_ACTION_NAME = "Export Type ...";
    /** Export label filter action name */
    public static final String FILTER_ACTION_NAME = "Filter labels";
    /** Export type-based label filter action name */
    public static final String FILTER_TYPE_ACTION_NAME = "Filter type graph";
    /** Back action name */
    public static final String FORWARD_ACTION_NAME = "Forward";
    /** Goto start state action name */
    public static final String GOTO_START_STATE_ACTION_NAME =
        "Go to Start State";
    /** List atomic propositions action name */
    public static final String LIST_ATOMIC_PROPOSITIONS_ACTION_NAME =
        "List Atom. Prop.";
    /** Load control file action name */
    public static final String LOAD_CONTROL_FILE_ACTION_NAME =
        "Load Control ...";
    /** Load rule action name */
    public static final String IMPORT_RULE_ACTION_NAME = "Import Rule ...";
    /** Load start state action name */
    public static final String LOAD_START_STATE_ACTION_NAME =
        "Load Start State ...";
    /** Load grammar action name */
    public static final String LOAD_GRAMMAR_ACTION_NAME = "Load Grammar ...";
    /** Load grammar from url action name */
    public static final String LOAD_URL_GRAMMAR_ACTION_NAME =
        "Load Grammar from URL ...";

    /** Name for the model checking action. */
    static public final String MODEL_CHECK_ACTION_NAME = "Verify";
    /** New action name */
    public static final String NEW_ACTION_NAME = "New";
    /** New control action name */
    public static final String NEW_CONTROL_ACTION_NAME = "New Control";
    /** New graph action name */
    public static final String NEW_GRAPH_ACTION_NAME = "New Graph";
    /** New grammar action name */
    public static final String NEW_GRAMMAR_ACTION_NAME = "New Grammar";
    /** New rule action name */
    public static final String NEW_RULE_ACTION_NAME = "New Rule";
    /** New type action name */
    public static final String NEW_TYPE_ACTION_NAME = "New Type";
    /** Node mode action name */
    public static final String NODE_MODE_NAME = "Node Mode";
    /** Open action name */
    public static final String OPEN_ACTION_NAME = "Open ...";
    /** Paste action name */
    public static final String PASTE_ACTION_NAME = "Paste";
    /** Preview control action name */
    public static final String PREVIEW_CONTROL_ACTION_NAME =
        "Preview Control ...";
    /** Preview graph action name */
    public static final String PREVIEW_GRAPH_ACTION_NAME = "Preview Graph ...";
    /** Preview rule action name */
    public static final String PREVIEW_RULE_ACTION_NAME = "Preview Rule ...";
    /** Preview type action name */
    public static final String PREVIEW_TYPE_ACTION_NAME = "Preview Type ...";
    /** Provide ctl formula action name */
    public static final String PROVIDE_CTL_FORMULA_ACTION_NAME =
        "CTL Formula ...";
    /** Quit action name */
    public static final String QUIT_ACTION_NAME = "Quit";
    /** Redo action name */
    public static final String REDO_ACTION_NAME = "Redo";
    /** Refresh grammar action name */
    public static final String REFRESH_GRAMMAR_ACTION_NAME = "Refresh Grammar";
    /** Graph rename action name */
    public static final String RELABEL_ACTION_NAME = "Replace Label";
    /** Remove point action name */
    static public final String REMOVE_POINT_ACTION = "Remove Point";
    /** Control program rename action name */
    public static final String RENAME_CONTROL_ACTION_NAME = "Rename Control";
    /** Graph rename action name */
    public static final String RENAME_GRAPH_ACTION_NAME = "Rename Graph";
    /** Rule rename action name */
    public static final String RENAME_RULE_ACTION_NAME = "Rename Rule";
    /** Type rename action name */
    public static final String RENAME_TYPE_ACTION_NAME = "Rename Type";
    /** Renumber action name */
    public static final String RENUMBER_ACTION_NAME = "Renumber Nodes";
    /** Reset label position action name */
    static public final String RESET_LABEL_POSITION_ACTION = "Reset Label";
    /**
     * Restart simulation action name
     */
    public static final String RESTART_ACTION_NAME = "Restart simulation";
    /** Edit properties action name */
    public static final String RULE_PROPERTIES_ACTION_NAME =
        "Rule Properties ...";
    /** Use as start graph action name */
    public static final String START_GRAPH_ACTION_NAME = "Use as Start Graph";
    /**
     * Start simulation action name
     */
    public static final String START_SIMULATION_ACTION_NAME = "Clear LTS";

    /** To abstract mode action name */
    public static final String TOGGLE_TO_ABS_ACTION_NAME =
        "Enter Abstraction Mode";
    /** To concrete mode action name */
    public static final String TOGGLE_TO_CONC_ACTION_NAME =
        "Return to Concrete Mode";
    /**
     * Save action name
     */
    public static final String SAVE_ACTION_NAME = "Save ...";
    /**
     * Save-as action name
     */
    public static final String SAVE_AS_ACTION_NAME = "Save as...";
    /**
     * Save action name
     */
    public static final String SAVE_CONTROL_ACTION_NAME = "Save Control";
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
    static public final String SCROLL_TO_ACTION_NAME = "Scroll to current";

    /**
     * Graph mode action name
     */
    public static final String SET_GRAPH_ROLE_ACTION_NAME =
        "Set to graph editing";
    /**
     * Rule mode action name
     */
    public static final String SET_RULE_ROLE_ACTION_NAME =
        "Set to rule editing";
    /**
     * Type mode action name
     */
    public static final String SET_TYPE_ROLE_ACTION_NAME =
        "Set to type editing";
    /**
     * Show all labels action name
     */
    public static final String SHOW_ALL_LABELS_ACTION_NAME = "Show all labels";
    /**
     * Show graph labels action name
     */
    public static final String SHOW_EXISTING_LABELS_ACTION_NAME =
        "Show only existing labels";
    /**
     * Show subtypes action name
     */
    public static final String SHOW_SUBTYPES_ACTION_NAME = "Show subtypes";
    /**
     * Show supertypes action name
     */
    public static final String SHOW_SUPERTYPES_ACTION_NAME = "Show supertypes";
    /** Snap to grid action name */
    public static final String SNAP_TO_GRID_NAME = "Snap to grid";
    /**
     * Edit action name
     */
    public static final String SYSTEM_PROPERTIES_ACTION_NAME =
        "Grammar Properties ...";
    /** Undo action name */
    public static final String UNDO_ACTION_NAME = "Undo";
    /** Unfilter labels action name */
    public static final String UNFILTER_ACTION_NAME = "Reset label filter";
    /** Unfilter type-based labels action name */
    public static final String UNFILTER_TYPE_ACTION_NAME =
        "Reset type graph filter";
    /** Select mode action name */
    public static final String SELECT_MODE_NAME = "Selection mode";
    /** Preview mode action name */
    public static final String PREVIEW_MODE_NAME = "Preview mode";

    /** Add point keystroke. */
    public static final KeyStroke ADD_POINT_KEY = KeyStroke.getKeyStroke(
        KeyEvent.VK_INSERT, InputEvent.ALT_MASK);
    /**
     * Apply keystroke
     */
    static public final KeyStroke APPLY_KEY = KeyStroke.getKeyStroke(
        KeyEvent.VK_ENTER, InputEvent.CTRL_MASK);
    /** Back keystroke */
    public static final KeyStroke BACK_KEY =
        KeyStroke.getKeyStroke("control LEFT");
    /** Save keystroke */
    public static final KeyStroke CANCEL_KEY = KeyStroke.getKeyStroke("ESCAPE");

    /**
     * Copy keystroke
     */
    public static final KeyStroke COPY_KEY =
        KeyStroke.getKeyStroke("control C");
    /**
     * Cut keystroke
     */
    public static final KeyStroke CUT_KEY = KeyStroke.getKeyStroke("control X");
    /**
     * Keystroke for the 'default exploration' action.
     */
    public static final KeyStroke DEFAULT_EXPLORATION_KEY =
        KeyStroke.getKeyStroke("control shift X");
    /**
     * Delete keystroke
     */
    public static final KeyStroke DELETE_KEY = KeyStroke.getKeyStroke("DELETE");
    /**
     * Edge mode keystroke
     */
    public static final KeyStroke EDGE_MODE_KEY =
        KeyStroke.getKeyStroke("alt E");
    /**
     * Edit keystroke
     */
    public static final KeyStroke EDIT_KEY =
        KeyStroke.getKeyStroke("control E");
    /**
     * Export keystroke
     */
    public static final KeyStroke EXPORT_KEY =
        KeyStroke.getKeyStroke("control alt S");
    /** Explore state space keystroke */
    /** Back keystroke */
    public static final KeyStroke FORWARD_KEY =
        KeyStroke.getKeyStroke("control RIGHT");
    /** Goto start state keystroke */
    public static final KeyStroke GOTO_START_STATE_KEY =
        KeyStroke.getKeyStroke("control G");
    /**
     * Insert keystroke
     */
    public static final KeyStroke INSERT_KEY = KeyStroke.getKeyStroke("INSERT");
    /** Last exploration keystroke */
    public static final KeyStroke LAST_EXPLORE_KEY =
        KeyStroke.getKeyStroke("control L");
    /** New keystroke */
    public static final KeyStroke NEW_KEY = KeyStroke.getKeyStroke("control N");
    /**
     * Node mode keystroke
     */
    public static final KeyStroke NODE_MODE_KEY =
        KeyStroke.getKeyStroke("alt N");
    /** Open keystroke */
    public static final KeyStroke OPEN_KEY =
        KeyStroke.getKeyStroke("control O");
    /** Open keystroke */
    public static final KeyStroke OPEN_URL_KEY =
        KeyStroke.getKeyStroke("control alt O");
    /** Open graph keystroke */
    public static final KeyStroke OPEN_GRAPH_KEY =
        KeyStroke.getKeyStroke("control shift O");
    /**
     * Paste keystroke
     */
    public static final KeyStroke PASTE_KEY =
        KeyStroke.getKeyStroke("control V");
    /**
     * Preview keystroke
     */
    public static final KeyStroke PREVIEW_MODE_KEY =
        KeyStroke.getKeyStroke("control P");
    /** Quit keystroke */
    public static final KeyStroke QUIT_KEY =
        KeyStroke.getKeyStroke("control Q");
    /** Redo keystroke */
    public static final KeyStroke REDO_KEY =
        KeyStroke.getKeyStroke("control Y");
    /** Refresh keystroke */
    public static final KeyStroke REFRESH_KEY = KeyStroke.getKeyStroke("F5");
    /**
     * Replace label keystroke
     */
    public static final KeyStroke RELABEL_KEY =
        KeyStroke.getKeyStroke("control R");
    /**
     * Edit label keystroke
     */
    public static final KeyStroke RENAME_KEY = KeyStroke.getKeyStroke("F2");
    /** Remove point keystroke. */
    public static final KeyStroke REMOVE_POINT_KEY = KeyStroke.getKeyStroke(
        KeyEvent.VK_DELETE, InputEvent.ALT_MASK);
    /** Save keystroke */
    public static final KeyStroke SAVE_KEY =
        KeyStroke.getKeyStroke("control S");
    /** Save keystroke */
    public static final KeyStroke SAVE_GRAPH_KEY =
        KeyStroke.getKeyStroke("control shift S");
    /** Select mode keystroke */
    public static final KeyStroke SELECT_MODE_KEY =
        KeyStroke.getKeyStroke("alt S");
    /** Run keystroke */
    public static final KeyStroke START_SIMULATION_KEY =
        KeyStroke.getKeyStroke("F11");
    /** Toggle exploration keystroke */
    public static final KeyStroke TOGGLE_EXP_MODE_KEY =
        KeyStroke.getKeyStroke("control B");
    /** Undo keystroke */
    public static final KeyStroke UNDO_KEY =
        KeyStroke.getKeyStroke("control Z");
    /** Orthogonal line style keystroke */
    public static final KeyStroke ORTHOGONAL_LINE_STYLE_KEY =
        KeyStroke.getKeyStroke("alt 1");
    /** Spline line style keystroke */
    public static final KeyStroke SPLINE_LINE_STYLE_KEY =
        KeyStroke.getKeyStroke("alt 2");
    /** Bezier line style keystroke */
    public static final KeyStroke BEZIER_LINE_STYLE_KEY =
        KeyStroke.getKeyStroke("alt 3");
    /** Manhattan line style keystroke */
    public static final KeyStroke MANHATTAN_LINE_STYLE_KEY =
        KeyStroke.getKeyStroke("alt 4");

    /** Mnemonic key for the New action. */
    public static final int NEW_MNEMONIC = KeyEvent.VK_N;
    /** Mnemonic key for the Quit action. */
    public static final int QUIT_MNEMONIC = KeyEvent.VK_Q;
    /** Mnemonic key for the Open action. */
    public static final int OPEN_MNEMONIC = KeyEvent.VK_O;
    /** Mnemonic key for the Save action. */
    public static final int SAVE_MNEMONIC = KeyEvent.VK_S;

    /** Indication for an empty label in a list of labels. */
    static public final String EMPTY_LABEL_TEXT = "(empty)";
    /** Indication for a subtype edge in a list of labels. */
    static public final String SUBTYPE_LABEL_TEXT = "(subtype)";
    /** Indication for no label in a list of labels. */
    static public final String NO_LABEL_TEXT = "(none)";
    /** The name of the Bezier line style. */
    static public final String BEZIER_LINE_STYLE_NAME = "Bezier";
    /** The name of the Spline line style. */
    static public final String SPLINE_LINE_STYLE_NAME = "Spline";
    /** The name of the Orthogonal line style. */
    static public final String ORTHOGONAL_LINE_STYLE_NAME = "Orthogonal";
    /** The name of the Manhattan line style. */
    static public final String MANHATTAN_LINE_STYLE_NAME = "Manhattan";
    /** Name for the imaging action. */
    static public final String IMAGE_ACTION_NAME = "Image";
    /** Show anchors option */
    static public final String SHOW_ANCHORS_OPTION = "Show anchors";
    /** Show node ids option */
    static public final String SHOW_NODE_IDS_OPTION = "Show node identities";
    /** Show state ids option */
    static public final String SHOW_STATE_IDS_OPTION = "Show state identities";
    /** Vertices are labels options */
    static public final String SHOW_VERTEX_LABELS_OPTION =
        "Show self edges as node labels";
    /** Show unfiltered edges to filtered nodes. */
    static public final String SHOW_UNFILTERED_EDGES_OPTION =
        "Show all unfiltered edges";
    /** Show aspects in graphs and rules option */
    static public final String SHOW_ASPECTS_OPTION = "Show aspect prefixes";
    /** Show remark nodes and edges. */
    static public final String SHOW_REMARKS_OPTION = "Show remarks";
    /** Show background colour for nodes. */
    static public final String SHOW_BACKGROUND_OPTION =
        "Show node background colour";
    /** Show data values as nodes rather than assignments. */
    static public final String SHOW_VALUE_NODES_OPTION =
        "Show data values as nodes";
    // /** Parse attributed graphs option */
    // static public final String IS_ATTRIBUTED_OPTION = "Parse as attributed
    // graph";
    /** Always delete rules without confirmation. */
    static public final String CANCEL_CONTROL_EDIT_OPTION =
        "Abandon edited control program?";
    /** Option to delete control programs. */
    static public final String DELETE_CONTROL_OPTION =
        "Delete control program?";
    /** Option to delete rules. */
    static public final String DELETE_RULE_OPTION = "Delete rule?";
    /** Always delete graphs without confirmation. */
    static public final String DELETE_GRAPH_OPTION = "Delete graph?";
    /** Option to delete type graphs. */
    static public final String DELETE_TYPE_OPTION = "Delete type graph?";
    /** Always replace edited rules. */
    static public final String REPLACE_RULE_OPTION = "Replace edited rule?";
    /** Always replace edited rules. */
    static public final String REPLACE_START_GRAPH_OPTION =
        "Replace start graph?";
    /** Always start simulation after changes. */
    static public final String START_SIMULATION_OPTION =
        "Start new simulation?";
    /** Automatically stop simulation at changes to the rule system. */
    static public final String STOP_SIMULATION_OPTION =
        "Stop current simulation?";
    /** Always replace edited rules. */
    static public final String VERIFY_ALL_STATES_OPTION = "Verify all states?";

    /** Default value map for the boolean options. */
    static private final Map<String,Boolean> boolOptionDefaults =
        new HashMap<String,Boolean>();
    /** Default value map for the behaviour options. */
    static private final Map<String,Integer> intOptionDefaults =
        new HashMap<String,Integer>();

    static {
        boolOptionDefaults.put(SHOW_ANCHORS_OPTION, false);
        boolOptionDefaults.put(SHOW_NODE_IDS_OPTION, false);
        boolOptionDefaults.put(SHOW_STATE_IDS_OPTION, true);
        boolOptionDefaults.put(SHOW_VERTEX_LABELS_OPTION, true);
        boolOptionDefaults.put(SHOW_ASPECTS_OPTION, false);
        boolOptionDefaults.put(SHOW_REMARKS_OPTION, true);
        boolOptionDefaults.put(SHOW_BACKGROUND_OPTION, true);
        boolOptionDefaults.put(SHOW_VALUE_NODES_OPTION, false);
        boolOptionDefaults.put(SHOW_UNFILTERED_EDGES_OPTION, false);
        intOptionDefaults.put(CANCEL_CONTROL_EDIT_OPTION, BehaviourOption.ASK);
        intOptionDefaults.put(DELETE_CONTROL_OPTION, BehaviourOption.ASK);
        intOptionDefaults.put(DELETE_GRAPH_OPTION, BehaviourOption.ASK);
        intOptionDefaults.put(DELETE_RULE_OPTION, BehaviourOption.ASK);
        intOptionDefaults.put(DELETE_TYPE_OPTION, BehaviourOption.ASK);
        intOptionDefaults.put(REPLACE_RULE_OPTION, BehaviourOption.ASK);
        intOptionDefaults.put(REPLACE_START_GRAPH_OPTION, BehaviourOption.ASK);
        intOptionDefaults.put(START_SIMULATION_OPTION, BehaviourOption.ALWAYS);
        intOptionDefaults.put(STOP_SIMULATION_OPTION, BehaviourOption.ASK);
        intOptionDefaults.put(VERIFY_ALL_STATES_OPTION, BehaviourOption.NEVER);
    }

    /** Returns the user preferences for a given key, as a list of Strings. */
    public static String[] getUserPrefs(String key) {
        String[] result = new String[0];
        String storedValue = userPrefs.get(key, "");
        try {
            result = ExprParser.splitExpr(storedValue, ",");
        } catch (FormatException e) {
            assert false : String.format(
                "Format error in user preference string %s: %s", storedValue,
                e.getMessage());
        }
        for (int i = 0; i < result.length; i++) {
            try {
                String newValue = ExprParser.toUnquoted(result[i], '"');
                assert result[i] != null : String.format(
                    "User preference string %s is not correctly quoted",
                    result[i]);
                result[i] = newValue;
            } catch (FormatException e) {
                assert false : String.format(
                    "Format error in user preference string %s: %s", result[i],
                    e.getMessage());
            }
        }
        return result;
    }

    /**
     * Stores an array of string values as user preferences, under a given key.
     * The preferences can later be retrieved by {@link #getUserPrefs(String)}.
     */
    public static void storeUserPrefs(String key, String[] values) {
        StringBuilder result = new StringBuilder();
        for (String value : values) {
            if (result.length() > 0) {
                result.append(",");
            }
            value = ExprParser.toQuoted(value, '"');
            result.append(value);
        }
        userPrefs.put(key, result.toString());
    }

    /** The persistently stored user preferences. */
    public static final Preferences userPrefs =
        Preferences.userNodeForPackage(Options.class);

    static {
        try {
            // add those default user option values that do not yet exist to the
            // preferences
            Set<String> keys =
                new HashSet<String>(Arrays.asList(userPrefs.keys()));
            for (Map.Entry<String,Boolean> defaultsEntry : boolOptionDefaults.entrySet()) {
                if (!keys.contains(defaultsEntry.getKey())) {
                    userPrefs.putBoolean(defaultsEntry.getKey(),
                        defaultsEntry.getValue());
                }
            }
            for (Map.Entry<String,Integer> defaultsEntry : intOptionDefaults.entrySet()) {
                if (!keys.contains(defaultsEntry.getKey())) {
                    userPrefs.putInt(defaultsEntry.getKey(),
                        defaultsEntry.getValue());
                }
            }
        } catch (BackingStoreException exc) {
            // don't do anything
        }
    }

    /**
     * Convenience method to convert line style codes to names. The line style
     * should equal one of the styles in {@link GraphConstants}.
     * @param lineStyle the integer value representing a line style
     * @return the String representing of the corresponding line style
     * @throws IllegalArgumentException if the line style is not recognized
     * @see GraphConstants#STYLE_BEZIER
     * @see GraphConstants#STYLE_SPLINE
     * @see GraphConstants#STYLE_ORTHOGONAL
     */
    static public String getLineStyleName(int lineStyle) {
        switch (lineStyle) {
        case GraphConstants.STYLE_BEZIER:
            return BEZIER_LINE_STYLE_NAME;
        case GraphConstants.STYLE_SPLINE:
            return SPLINE_LINE_STYLE_NAME;
        case GraphConstants.STYLE_ORTHOGONAL:
            return ORTHOGONAL_LINE_STYLE_NAME;
        case JAttr.STYLE_MANHATTAN:
            return MANHATTAN_LINE_STYLE_NAME;
        default:
            throw new IllegalArgumentException("" + lineStyle
                + " is not a recognized line style");
        }
    }

    /**
     * Convenience method to convert line style codes to key strokes. The line
     * style should equal one of the styles in {@link GraphConstants}.
     * @param lineStyle the integer value representing a line style
     * @return the keystroke of the corresponding line style
     * @throws IllegalArgumentException if the line style is not recognized
     * @see GraphConstants#STYLE_BEZIER
     * @see GraphConstants#STYLE_SPLINE
     * @see GraphConstants#STYLE_ORTHOGONAL
     */
    static public KeyStroke getLineStyleKey(int lineStyle) {
        switch (lineStyle) {
        case GraphConstants.STYLE_BEZIER:
            return BEZIER_LINE_STYLE_KEY;
        case GraphConstants.STYLE_SPLINE:
            return SPLINE_LINE_STYLE_KEY;
        case GraphConstants.STYLE_ORTHOGONAL:
            return ORTHOGONAL_LINE_STYLE_KEY;
        case JAttr.STYLE_MANHATTAN:
            return MANHATTAN_LINE_STYLE_KEY;
        default:
            throw new IllegalArgumentException("" + lineStyle
                + " is not a recognized line style");
        }
    }

    /**
     * Sets the look-and-feel to default (i.e., {@link PlasticLookAndFeel}).
     */
    public static void initLookAndFeel() {
        try {
            // LAF specific options that should be done before setting the LAF
            // go here
            MetalLookAndFeel.setCurrentTheme(new DesertBlue());
            // set default font to LAF font
            Options.DEFAULT_FONT =
                MetalLookAndFeel.getCurrentTheme().getUserTextFont();
            // Set the look and feel
            UIManager.setLookAndFeel(new com.jgoodies.looks.plastic.PlasticLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            // nothing to do here except not crash on the exception
        }
    }

    /** Creates an initialised options object. */
    public Options() {
        addCheckbox(SHOW_NODE_IDS_OPTION);
        addCheckbox(SHOW_ANCHORS_OPTION);
        addCheckbox(SHOW_ASPECTS_OPTION);
        addCheckbox(SHOW_REMARKS_OPTION);
        addCheckbox(SHOW_BACKGROUND_OPTION);
        addCheckbox(SHOW_VALUE_NODES_OPTION);
        addCheckbox(SHOW_VERTEX_LABELS_OPTION);
        addCheckbox(SHOW_STATE_IDS_OPTION);
        addCheckbox(SHOW_UNFILTERED_EDGES_OPTION);
        addBehaviour(CANCEL_CONTROL_EDIT_OPTION, 2);
        addBehaviour(DELETE_CONTROL_OPTION, 2);
        addBehaviour(DELETE_GRAPH_OPTION, 2);
        addBehaviour(DELETE_RULE_OPTION, 2);
        addBehaviour(DELETE_TYPE_OPTION, 2);
        addBehaviour(REPLACE_RULE_OPTION, 3);
        addBehaviour(REPLACE_START_GRAPH_OPTION, 2);
        addBehaviour(STOP_SIMULATION_OPTION, 2);
        addBehaviour(START_SIMULATION_OPTION, 3);
        addBehaviour(VERIFY_ALL_STATES_OPTION, 3);
    }

    /**
     * Adds a checkbox item with a given name to the options, and returns the
     * associated (fresh) menu item.
     * @param name the name of the checkbox menu item to add
     * @return the added {@link javax.swing.JCheckBoxMenuItem}
     */
    private final JCheckBoxMenuItem addCheckbox(final String name) {
        JCheckBoxMenuItem result = new JCheckBoxMenuItem(name);
        result.setSelected(userPrefs.getBoolean(name,
            boolOptionDefaults.get(name)));
        this.itemMap.put(name, result);
        result.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                userPrefs.putBoolean(name,
                    e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        return result;
    }

    /**
     * Adds a behaviour menu with a given name to the options, and returns the
     * associated (fresh) menu item.
     * @param name the name of the behaviour menu item to add
     * @return the added {@link javax.swing.JCheckBoxMenuItem}
     */
    private final BehaviourOption addBehaviour(final String name,
            int optionCount) {
        BehaviourOption result = new BehaviourOption(name, optionCount);
        result.setValue(userPrefs.getInt(name, intOptionDefaults.get(name)));
        result.addPropertyChangeListener(BehaviourOption.SELECTION,
            new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent e) {
                    userPrefs.putInt(name, (Integer) e.getNewValue());
                }
            });
        this.itemMap.put(name, result);
        return result;
    }

    /**
     * Returns the menu item associated with a given name, if any.
     * @param name the name of the checkbox item looked for
     * @return the {@link javax.swing.JCheckBoxMenuItem} with the given name if
     *         it exists, or <tt>null</tt> otherwise
     */
    public JMenuItem getItem(String name) {
        return this.itemMap.get(name);
    }

    /**
     * Returns the set of menu items available.
     * @return the set of menu items available
     */
    public Collection<JMenuItem> getItemSet() {
        return this.itemMap.values();
    }

    /**
     * Returns the current selection value of a given options name.
     * @param name the name of the checkbox menu item for which to check its
     *        value
     * @return the value of the checkbox item with the given name
     */
    public boolean isSelected(String name) {
        return this.itemMap.get(name).isSelected();
    }

    /**
     * Sets the selection of a given option.
     * @param name the name of the menu item for which to set the value
     * @param selected the new selection value of the menu item
     */
    public void setSelected(String name, boolean selected) {
        this.itemMap.get(name).setSelected(selected);
    }

    /**
     * Returns the current value of a given options name. If the option is a
     * checkbox menu, the value is <code>0</code> for <code>false</code> and
     * <code>1</code> for <code>true</code>.
     * @param name the name of the checkbox menu item for which to get the value
     * @return the current value of the checkbox item with the given name
     */
    public int getValue(String name) {
        JMenuItem item = this.itemMap.get(name);
        if (item instanceof BehaviourOption) {
            return ((BehaviourOption) item).getValue();
        } else {
            return item.isSelected() ? 1 : 0;
        }
    }

    /**
     * Sets the value of a given option. If the option is a checkbox menu item,
     * it is set to <code>true</code> for any value greater than 0.
     * @param name the name of the menu item for which to set the value
     * @param value the new value of the menu item
     */
    public void setValue(String name, int value) {
        JMenuItem item = this.itemMap.get(name);
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
        for (Map.Entry<String,JMenuItem> entry : this.itemMap.entrySet()) {
            result.put(entry.getKey(), entry.getValue().isSelected());
        }
        return result.toString();
    }

    /** Returns a clone of this options object. */
    @Override
    public Options clone() {
        Options result = new Options();
        result.itemMap.putAll(this.itemMap);
        return result;
    }

    /**
     * Map from option names to menu items.
     */
    private final Map<String,JMenuItem> itemMap =
        new LinkedHashMap<String,JMenuItem>();

}
