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
 * $Id$
 */
package groove.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.jgraph.graph.GraphConstants;

import com.jgoodies.looks.plastic.theme.DesertBlue;

import groove.grammar.model.ResourceKind;
import groove.io.Util;
import groove.io.store.EditType;
import groove.util.Groove;
import groove.util.parse.FormatException;
import groove.util.parse.StringHandler;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
public class Options implements Cloneable {
    /** Creates an initialised options object. */
    public Options() {
        for (ResourceKind resource : getOptionalTabs()) {
            addCheckbox(getShowTabOption(resource));
        }
        addCheckbox(SHOW_NODE_IDS_OPTION);
        addCheckbox(SHOW_ANCHORS_OPTION);
        addCheckbox(SHOW_ASPECTS_OPTION);
        addCheckbox(SHOW_VALUE_NODES_OPTION);
        addCheckbox(SHOW_ABSENT_STATES_OPTION);
        addCheckbox(SHOW_RECIPE_STEPS_OPTION);
        addCheckbox(SHOW_STATE_IDS_OPTION);
        addCheckbox(SHOW_STATE_STATUS_OPTION);
        addCheckbox(SHOW_CONTROL_STATE_OPTION);
        addCheckbox(SHOW_INVARIANTS_OPTION);
        addCheckbox(SHOW_UNFILTERED_EDGES_OPTION);
        addCheckbox(SHOW_ARROWS_ON_LABELS_OPTION);
        addCheckbox(SHOW_BIDIRECTIONAL_EDGES_OPTION);
        addBehaviour(DELETE_RESOURCE_OPTION, 2);
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
        boolean selected = userPrefs.getBoolean(name, boolOptionDefaults.get(name));
        boolean enabled = isEnabled(name);
        result.setSelected(selected & enabled);
        this.itemMap.put(name, result);
        if (enabled) {
            result.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    userPrefs.putBoolean(name, e.getStateChange() == ItemEvent.SELECTED);
                }
            });
        } else {
            result.setEnabled(false);
        }
        return result;
    }

    /** Tests if a given option is structurally enabled on this machine. */
    private boolean isEnabled(String name) {
        boolean result = true;
        if (SHOW_ARROWS_ON_LABELS_OPTION.equals(name)) {
            result = isSupportsLabelArrows();
        }
        return result;
    }

    /**
     * Adds a behaviour menu with a given name to the options, and returns the
     * associated (fresh) menu item.
     * @param name the name of the behaviour menu item to add
     * @return the added {@link javax.swing.JCheckBoxMenuItem}
     */
    private final BehaviourOption addBehaviour(final String name, int optionCount) {
        BehaviourOption result = new BehaviourOption(name, optionCount);
        result.setValue(userPrefs.getInt(name, intOptionDefaults.get(name)));
        result.addPropertyChangeListener(BehaviourOption.SELECTION, new PropertyChangeListener() {
            @Override
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
        return this.itemMap.get(name)
            .isSelected();
    }

    /**
     * Sets the selection of a given option.
     * @param name the name of the menu item for which to set the value
     * @param selected the new selection value of the menu item
     */
    public void setSelected(String name, boolean selected) {
        this.itemMap.get(name)
            .setSelected(selected);
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
        Map<String,Boolean> result = new HashMap<>();
        for (Map.Entry<String,JMenuItem> entry : this.itemMap.entrySet()) {
            result.put(entry.getKey(),
                entry.getValue()
                    .isSelected());
        }
        return result.toString();
    }

    /**
     * Map from option names to menu items.
     */
    private final Map<String,JMenuItem> itemMap = new LinkedHashMap<>();

    /**
     * Tests if the font used for rendering labels supports
     * the Unicode characters used for arrows-on-labels.
     */
    static public boolean isSupportsLabelArrows() {
        return SYMBOL_FONT != null;
    }

    /**
     * Callback method to determine whether a mouse event could be intended to
     * edit edge points.
     */
    static public boolean isEdgeEditEvent(MouseEvent evt) {
        return evt.getButton() == MouseEvent.BUTTON1 && evt.isAltDown();
    }

    /** Gives a button the Groove look-and-feel. */
    static public void setLAF(final AbstractButton button) {
        button.setHideActionText(true);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
            BorderFactory.createEmptyBorder(3, 2, 3, 2)));
        button.setBorderPainted(button.isEnabled());
        button.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                button.setBorderPainted(button.isEnabled());
            }
        });
    }

    /** Creates a button for a given action with the right look-and-feel. */
    static public JButton createButton(Action action) {
        JButton result = new JButton(action);
        setLAF(result);
        return result;
    }

    /** Creates a toggle button for a given action with the right look-and-feel. */
    static public JToggleButton createToggleButton(Action action) {
        JToggleButton result = new JToggleButton(action);
        setLAF(result);
        return result;
    }

    /** Creates a non-floatable tool bar of which the buttons are non-focusable. */
    public static JToolBar createToolBar() {
        JToolBar result = new JToolBar() {
            @Override
            protected JButton createActionComponent(Action a) {
                final JButton result = super.createActionComponent(a);
                setLAF(result);
                return result;
            }
        };
        result.setFloatable(false);
        result.setAlignmentX(Component.LEFT_ALIGNMENT);
        // make sure tool tips get displayed
        ToolTipManager.sharedInstance()
            .registerComponent(result);
        return result;
    }

    /**
     * Returns the action name for a resource edit.
     * A further parameter determines if the name is a description <i>before</i>
     * the action occurs, or after.
     * @param edit the edit for which the name is required
     * @param resource the kind of resource that is edited
     * @param dots if {@code true}, a ... prefix is appended
     * @return The appropriate action name
     */
    public static String getEditActionName(EditType edit, ResourceKind resource, boolean dots) {
        StringBuilder result = new StringBuilder(edit.getName());
        result.append(' ');
        result.append(resource.getName());
        if (dots) {
            result.append(" ...");
        }
        return result.toString();
    }

    /**
     * Returns the action name for resource enabling and disabling.
     * @param resource the kind of resource that is enabled/disabled
     * @param enable {@code true} if the resource is to be enabled,
     * {@code false} if it is to be disabled
     * @return The appropriate action name
     */
    public static String getEnableName(ResourceKind resource, boolean enable) {
        StringBuilder result = new StringBuilder(EditType.getEnableName(enable));
        result.append(' ');
        result.append(resource.getName());
        return result.toString();
    }

    /** Returns the initially suggested (simple) name for a new resource of
     * a given type.
     */
    public static String getNewResourceName(ResourceKind resource) {
        return resource.getDefaultName()
            .map(n -> n.toString())
            .orElse("new" + resource.getName());
    }

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
    /** New menu name */
    public static final String NEW_MENU_NAME = "New";
    /** Open Recent menu name * */
    public static final String OPEN_RECENT_MENU_NAME = "Load Recent Grammar";
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
    /** Rule tree pane title */
    public static final String RULE_TREE_PANE_TITLE = "Rule nesting";
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
    /** Animation action name */
    public static final String ANIMATE_ACTION_NAME = "Animate State Exploration";
    /** Apply transition action name */
    public static final String APPLY_MATCH_ACTION_NAME = "Apply selected match";
    /** Back action name */
    public static final String BACK_ACTION_NAME = "Step Back";
    /** Action name for cancelling an edit. */
    public static final String CANCEL_EDIT_ACTION_NAME = "Cancel Edit";
    /** Change graphs action name */
    public static final String CHANGE_GRAPHS_ACTION_NAME = "Change Graphs";
    /** Change rules action name */
    public static final String CHANGE_RULES_ACTION_NAME = "Change Rules";
    /** Action name for checking CTL on full state space */
    public static final String CHECK_CTL_FULL_ACTION_NAME = "Check CTL property (full state space)";
    /** Action name for checking CTL on current state space */
    public static final String CHECK_CTL_AS_IS_ACTION_NAME =
        "Check CTL property (current state space)";
    /** Action name for checking LTL. */
    public static final String CHECK_LTL_ACTION_NAME = "Check LTL property (full state space)";
    /** Action name for checking LTL on bounded state space */
    public static final String CHECK_LTL_BOUNDED_ACTION_NAME =
        "Check LTL property (bounded state space)";
    /** Action name for checking LTL on bounded state space */
    public static final String CHECK_LTL_POCKET_ACTION_NAME =
        "Check LTL property (bounded pocket strategy)";
    /** Action name for checking LTL on bounded state space */
    public static final String CHECK_LTL_OPTIMIZED_ACTION_NAME =
        "Check LTL property (optimised bounded state space)";
    /** Action name for checking LTL on bounded state space */
    public static final String CHECK_LTL_OPTMIZED_POCKET_ACTION_NAME =
        "Check LTL property (optimised bounded pocket strategy)";
    /** Close action name */
    public static final String CLOSE_ACTION_NAME = "Close";
    /** Close all editors action name */
    public static final String CLOSE_ALL_ACTION_NAME = "Close All Editors";
    /** Close this editor action name */
    public static final String CLOSE_THIS_ACTION_NAME = "Close This Editor";
    /** Close other editors action name */
    public static final String CLOSE_OTHER_ACTION_NAME = "Close Other Editors";
    /** Copy action name */
    public static final String COPY_ACTION_NAME = "Copy";
    /** Cut action name */
    public static final String CUT_ACTION_NAME = "Cut";
    /** Default exploration action name */
    public static final String EXPLORE_ACTION_NAME = "Explore State Space";
    /** Detach action name */
    public static final String DETACH_ACTION_NAME = "Detach";
    /** Delete action name */
    public static final String DELETE_ACTION_NAME = "Delete";
    /** Edge mode action name */
    public static final String EDIT_MODE_NAME = "Edit mode";
    /** Edit action name */
    public static final String EDIT_ACTION_NAME = "Edit ...";
    /** Edit label action name */
    static public final String EDIT_LABEL_ACTION = "Edit Label";
    /** Edit state action name */
    public static final String EDIT_STATE_ACTION_NAME = "Edit State ...";
    /** Exploration dialog action name */
    public static final String EXPLORATION_DIALOG_ACTION_NAME = "Customize Exploration ...";
    /** Exploration statistics dialog action name */
    public static final String EXPLORATION_STATS_DIALOG_ACTION_NAME =
        "Last Exploration Statistics ...";
    /** Layout dialog action name */
    public static final String LAYOUT_DIALOG_ACTION_NAME = "Customize Layout ...";
    /** Explore single state action name */
    public static final String EXPLORE_STATE_ACTION_NAME = "Explore current state";
    /** Export action name */
    public static final String EXPORT_ACTION_NAME = "Export ...";
    /** Export control action name */
    public static final String EXPORT_CONTROL_ACTION_NAME = "Export Control Automaton ...";
    /** Export rule action name */
    public static final String EXPORT_RULE_ACTION_NAME = "Export Rule ...";
    /** Export lts action name */
    public static final String EXPORT_LTS_ACTION_NAME = "Export LTS ...";
    /** Export graph action name */
    public static final String EXPORT_GRAPH_ACTION_NAME = "Export Graph ...";
    /** Export state action name */
    public static final String EXPORT_STATE_ACTION_NAME = "Export State ...";
    /** Export type action name */
    public static final String EXPORT_TYPE_ACTION_NAME = "Export Type ...";
    /** Export label filter action name */
    public static final String FILTER_ACTION_NAME = "Filter labels";
    /** Filter LTS action name */
    public static final String FILTER_LTS_ACTION_NAME = "Filter LTS";
    /** Export type-based label filter action name */
    public static final String FILTER_TYPE_ACTION_NAME = "Filter type graph";
    /** Back action name */
    public static final String FORWARD_ACTION_NAME = "Step Forward";
    /** Find a final state action name */
    public static final String GOTO_FINAL_STATE_ACTION_NAME = "Go to Final State";
    /** Goto start state action name */
    public static final String GOTO_START_STATE_ACTION_NAME = "Go to Start State";
    /** Hide LTS action name */
    public static final String HIDE_LTS_ACTION_NAME = "Hide LTS";
    /** List atomic propositions action name */
    public static final String LIST_ATOMIC_PROPOSITIONS_ACTION_NAME = "List Atom. Prop.";
    /** Load control file action name */
    public static final String LOAD_CONTROL_FILE_ACTION_NAME = "Load Control ...";
    /** Load start state action name */
    public static final String LOAD_START_STATE_ACTION_NAME = "Load External Start State ...";
    /** Import action name */
    public static final String IMPORT_ACTION_NAME = "Import ...";
    /** Load grammar action name */
    public static final String LOAD_GRAMMAR_ACTION_NAME = "Load Grammar ...";
    /** Load grammar from url action name */
    public static final String LOAD_URL_GRAMMAR_ACTION_NAME = "Load Grammar from URL ...";
    /** Name of the "Lower Priority" action. */
    public static final String LOWER_PRIORITY_ACTION_NAME = "Lower Priority";
    /** Name for the model checking action. */
    static public final String MODEL_CHECK_ACTION_NAME = "Verify";
    /** New action name */
    public static final String NEW_ACTION_NAME = "New";
    /** New grammar action name */
    public static final String NEW_GRAMMAR_ACTION_NAME = "New Grammar ...";
    /** Node mode action name */
    public static final String NODE_MODE_NAME = "Node Mode";
    /** Open action name */
    public static final String OPEN_ACTION_NAME = "Open ...";
    /** Paste action name */
    public static final String PASTE_ACTION_NAME = "Paste";
    /** Preview control action name */
    public static final String PREVIEW_CONTROL_ACTION_NAME = "Preview Control ...";
    /** Quit action name */
    public static final String QUIT_ACTION_NAME = "Quit";
    /** Name of the "First Prolog Result" action. */
    public static final String PROLOG_FIRST_ACTION_NAME = "Start Query";
    /** Name of the "Next Prolog Result" action. */
    public static final String PROLOG_NEXT_ACTION_NAME = "Next Result";
    /** Name of the "Raise Priority" action. */
    public static final String RAISE_PRIORITY_ACTION_NAME = "Raise Priority";
    /** Redo action name */
    public static final String REDO_ACTION_NAME = "Redo";
    /** Refresh grammar action name */
    public static final String REFRESH_GRAMMAR_ACTION_NAME = "Refresh Grammar";
    /**
     * Reload lts action name
     */
    public static final String RELOAD_LTS_ACTION_NAME = "Reload LTS";
    /** Find/replace action name */
    public static final String FIND_REPLACE_ACTION_NAME = "Find/Replace Label...";
    /** Replace action name */
    public static final String REPLACE_ACTION_NAME = "Replace Label";
    /** Remove point action name */
    static public final String REMOVE_POINT_ACTION = "Remove Point";
    /** Renumber action name */
    public static final String RENUMBER_ACTION_NAME = "Renumber Nodes";
    /** Reset label position action name */
    static public final String RESET_LABEL_POSITION_ACTION = "Reset Label";
    /**
     * Restart simulation action name
     */
    public static final String RESTART_ACTION_NAME = "Restart simulation";
    /** Edit properties action name */
    public static final String RULE_PROPERTIES_ACTION_NAME = "Rule Properties ...";
    /** Select colour action name */
    public static final String SELECT_COLOR_ACTION_NAME = "Select Color...";
    /** Use as start graph action name */
    public static final String START_GRAPH_ACTION_NAME = "Use as Start Graph";
    /**
     * Start simulation action name
     */
    public static final String START_SIMULATION_ACTION_NAME = "Restart simulation";

    /** To abstract mode action name */
    public static final String TOGGLE_TO_ABS_ACTION_NAME = "Enter Abstraction Mode";
    /** To concrete mode action name */
    public static final String TOGGLE_TO_CONC_ACTION_NAME = "Return to Concrete Mode";
    private static final String SAVE_NAME_TEMPLATE = "Save %s";
    private static final String SAVE_AS_NAME_TEMPLATE = "Save %s As ...";
    /**
     * Save action name
     */
    public static final String SAVE_ACTION_NAME = "Save";
    /**
     * Save-as action name
     */
    public static final String SAVE_AS_ACTION_NAME = "Save As ...";

    /** Returns the save-as action name for a given item text. */
    private static final String getSaveActionName(String item, boolean saveAs) {
        return String.format(saveAs ? SAVE_AS_NAME_TEMPLATE : SAVE_NAME_TEMPLATE, item);
    }

    /** Returns the save or save-as action name for a given resource kind. */
    public static final String getSaveActionName(ResourceKind resource, boolean saveAs) {
        return getSaveActionName(resource.getName(), saveAs);
    }

    /** Returns the save-as action name for a given graph role. */
    public static final String getSaveStateActionName(boolean saveAs) {
        return getSaveActionName("State", saveAs);
    }

    /**
     * Save grammar action name
     */
    public static final String SAVE_GRAMMAR_ACTION_NAME = "Save Grammar As ...";
    /**
     * Save lts action name
     */
    public static final String SAVE_LTS_ACTION_NAME = "Save LTS As ...";
    /**
     * Scroll to action name
     */
    static public final String SCROLL_TO_ACTION_NAME = "Scroll to current";
    /** Name of the "Set Priority" action. */
    public static final String SET_PRIORITY_ACTION_NAME = "Set Priority";
    /**
     * Show all labels action name
     */
    public static final String SHOW_ALL_LABELS_ACTION_NAME = "Show all labels";
    /**
     * Show graph labels action name
     */
    public static final String SHOW_EXISTING_LABELS_ACTION_NAME = "Show only existing labels";
    /**
     * Show subtypes action name
     */
    public static final String SHOW_SUBTYPES_ACTION_NAME = "Show subtypes";
    /**
     * Show supertypes action name
     */
    public static final String SHOW_SUPERTYPES_ACTION_NAME = "Show supertypes";
    /**
     * Collapse All action name
     */
    public static final String COLLAPSE_ALL = "Collapse All";
    /** Snap to grid action name */
    public static final String SNAP_TO_GRID_NAME = "Snap to grid";
    /** Search action name */
    public static final String SEARCH_ACTION_NAME = "Search ...";
    /**
     * Edit action name
     */
    public static final String SYSTEM_PROPERTIES_ACTION_NAME = "Grammar Properties ...";
    /** Undo action name */
    public static final String UNDO_ACTION_NAME = "Undo";
    /** Unfilter labels action name */
    public static final String UNFILTER_ACTION_NAME = "Reset label filter";
    /** Unfilter type-based labels action name */
    public static final String UNFILTER_TYPE_ACTION_NAME = "Reset type graph filter";
    /** Pan mode action name */
    public static final String PAN_MODE_NAME = "Pan and Zoom mode";
    /** Preview mode action name */
    public static final String PREVIEW_MODE_NAME = "Preview mode";
    /** Select mode action name */
    public static final String SELECT_MODE_NAME = "Selection mode";

    /** Add point keystroke. */
    public static final KeyStroke ADD_POINT_KEY =
        KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, InputEvent.ALT_DOWN_MASK);
    /**
     * Apply keystroke
     */
    static public final KeyStroke APPLY_KEY =
        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK);
    /** Back keystroke */
    public static final KeyStroke BACK_KEY = KeyStroke.getKeyStroke("alt LEFT");
    /** Cancel keystroke */
    public static final KeyStroke CANCEL_KEY = KeyStroke.getKeyStroke("ESCAPE");

    /** Cancel keystroke */
    public static final KeyStroke CLOSE_KEY = KeyStroke.getKeyStroke("control W");

    /**
     * Copy keystroke
     */
    public static final KeyStroke COPY_KEY = KeyStroke.getKeyStroke("control C");
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
    public static final KeyStroke EDIT_MODE_KEY = KeyStroke.getKeyStroke("alt E");
    /**
     * Edit keystroke
     */
    public static final KeyStroke EDIT_KEY = KeyStroke.getKeyStroke("control E");
    /**
     * Export keystroke
     */
    public static final KeyStroke EXPORT_KEY = KeyStroke.getKeyStroke("control alt S");
    /** Explore state space keystroke */
    /** Back keystroke */
    public static final KeyStroke FORWARD_KEY = KeyStroke.getKeyStroke("alt RIGHT");
    /** Find and go to final state keystroke */
    public static final KeyStroke GOTO_FINAL_STATE_KEY =
        KeyStroke.getKeyStroke("control shift END");
    /** Goto start state keystroke */
    public static final KeyStroke GOTO_START_STATE_KEY =
        KeyStroke.getKeyStroke("control shift HOME");
    /**
     * Insert keystroke
     */
    public static final KeyStroke INSERT_KEY = KeyStroke.getKeyStroke("INSERT");
    /** Last exploration keystroke */
    public static final KeyStroke LAYOUT_KEY = KeyStroke.getKeyStroke("control L");
    /** New keystroke */
    public static final KeyStroke NEW_KEY = KeyStroke.getKeyStroke("control N");
    /**
     * Node mode keystroke
     */
    public static final KeyStroke NODE_MODE_KEY = KeyStroke.getKeyStroke("alt N");
    /** Open keystroke */
    public static final KeyStroke OPEN_KEY = KeyStroke.getKeyStroke("control O");
    /** Open keystroke */
    public static final KeyStroke OPEN_URL_KEY = KeyStroke.getKeyStroke("control alt O");
    /** Open graph keystroke */
    public static final KeyStroke OPEN_GRAPH_KEY = KeyStroke.getKeyStroke("control shift O");
    /** Pan-and-zoom mode keystroke */
    public static final KeyStroke PAN_MODE_KEY = KeyStroke.getKeyStroke("alt Z");
    /**
     * Paste keystroke
     */
    public static final KeyStroke PASTE_KEY = KeyStroke.getKeyStroke("control V");
    /**
     * Preview keystroke
     */
    public static final KeyStroke PREVIEW_MODE_KEY = KeyStroke.getKeyStroke("alt P");
    /** Quit keystroke */
    public static final KeyStroke QUIT_KEY = KeyStroke.getKeyStroke("control Q");
    /** Redo keystroke */
    public static final KeyStroke REDO_KEY = KeyStroke.getKeyStroke("control Y");
    /** Refresh keystroke */
    public static final KeyStroke REFRESH_KEY = KeyStroke.getKeyStroke("F5");
    /**
     * Replace label keystroke
     */
    public static final KeyStroke RELABEL_KEY = KeyStroke.getKeyStroke("control R");
    /**
     * Edit label keystroke
     */
    public static final KeyStroke RENAME_KEY = KeyStroke.getKeyStroke("F2");
    /** Remove point keystroke. */
    public static final KeyStroke REMOVE_POINT_KEY =
        KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.ALT_DOWN_MASK);
    /** Save keystroke */
    public static final KeyStroke SAVE_KEY = KeyStroke.getKeyStroke("control S");
    /** Save keystroke */
    public static final KeyStroke SAVE_GRAMMAR_AS_KEY = KeyStroke.getKeyStroke("control shift S");
    /** Search keystroke */
    public static final KeyStroke SEARCH_KEY = KeyStroke.getKeyStroke("control F");
    /** Select mode keystroke */
    public static final KeyStroke SELECT_MODE_KEY = KeyStroke.getKeyStroke("alt S");
    /** Run keystroke */
    public static final KeyStroke START_SIMULATION_KEY = KeyStroke.getKeyStroke("F11");
    /** Toggle exploration keystroke */
    public static final KeyStroke TOGGLE_EXP_MODE_KEY = KeyStroke.getKeyStroke("control B");
    /** Undo keystroke */
    public static final KeyStroke UNDO_KEY = KeyStroke.getKeyStroke("control Z");
    /** Orthogonal line style keystroke */
    public static final KeyStroke ORTHOGONAL_LINE_STYLE_KEY = KeyStroke.getKeyStroke("alt 1");
    /** Spline line style keystroke */
    public static final KeyStroke SPLINE_LINE_STYLE_KEY = KeyStroke.getKeyStroke("alt 2");
    /** Bezier line style keystroke */
    public static final KeyStroke BEZIER_LINE_STYLE_KEY = KeyStroke.getKeyStroke("alt 3");
    /** Manhattan line style keystroke */
    public static final KeyStroke MANHATTAN_LINE_STYLE_KEY = KeyStroke.getKeyStroke("alt 4");

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
    /** Name for the imaging action. */
    static public final String IMAGE_ACTION_NAME = "Image";

    /** Returns the tab show option text for a given resource kind. */
    public static String getShowTabOption(ResourceKind kind) {
        if (showTabOptionMap == null) {
            showTabOptionMap = new EnumMap<>(ResourceKind.class);
        }
        String result = showTabOptionMap.get(kind);
        if (result == null) {
            showTabOptionMap.put(kind, result = String.format("Show %ss", kind.getDescription()));
        }
        return result;
    }

    private static Map<ResourceKind,String> showTabOptionMap;

    /** Returns the resource kinds for which the display tab is optional. */
    public static final Set<ResourceKind> getOptionalTabs() {
        return Collections.unmodifiableSet(optionalTabs);
    }

    /** Set of resource kinds for which the display tab is optional. */
    private static final Set<ResourceKind> optionalTabs = EnumSet
        .of(ResourceKind.CONTROL, ResourceKind.PROLOG, ResourceKind.TYPE, ResourceKind.GROOVY);

    /** Show anchors option */
    static public final String SHOW_ANCHORS_OPTION = "Show anchors";
    /** Show aspects in graphs and rules option */
    static public final String SHOW_ASPECTS_OPTION = "Show aspect prefixes";
    /** Show bidirectional edges. */
    static public final String SHOW_BIDIRECTIONAL_EDGES_OPTION = "Show bidirectional edges";
    /** Show node ids option */
    static public final String SHOW_NODE_IDS_OPTION = "Show node identities";
    /** Show state ids option */
    static public final String SHOW_STATE_IDS_OPTION = "Show state identities";
    /** Show state status option */
    static public final String SHOW_STATE_STATUS_OPTION = "Show state status";
    /** Show control state option */
    static public final String SHOW_CONTROL_STATE_OPTION = "Show control information";
    /** Show invariants option */
    static public final String SHOW_INVARIANTS_OPTION = "Show invariants on the states";
    /** Show absent states option */
    static public final String SHOW_ABSENT_STATES_OPTION = "Show absent states";
    /** Show recipe steps option */
    static public final String SHOW_RECIPE_STEPS_OPTION = "Show recipe steps";
    /** Show unfiltered edges to filtered nodes. */
    static public final String SHOW_UNFILTERED_EDGES_OPTION = "Show all unfiltered edges";
    /** Show data values as nodes rather than assignments. */
    static public final String SHOW_VALUE_NODES_OPTION = "Show data values as nodes";
    /** Show data values as nodes rather than assignments. */
    static public final String SHOW_ARROWS_ON_LABELS_OPTION = "Show arrows on labels";
    /** Always delete resources without confirmation. */
    static public final String DELETE_RESOURCE_OPTION = "Delete seletected resource?";
    /** Always check CTL properties on all states, rather than the initial state. */
    static public final String VERIFY_ALL_STATES_OPTION = "Check CTL on all states?";

    /** Default value map for the boolean options. */
    static private final Map<String,Boolean> boolOptionDefaults = new HashMap<>();
    /** Default value map for the behaviour options. */
    static private final Map<String,Integer> intOptionDefaults = new HashMap<>();

    static {
        for (ResourceKind optionalTab : optionalTabs) {
            boolOptionDefaults.put(getShowTabOption(optionalTab), false);
        }
        boolOptionDefaults.put(SHOW_ANCHORS_OPTION, false);
        boolOptionDefaults.put(SHOW_NODE_IDS_OPTION, false);
        boolOptionDefaults.put(SHOW_STATE_IDS_OPTION, true);
        boolOptionDefaults.put(SHOW_STATE_STATUS_OPTION, true);
        boolOptionDefaults.put(SHOW_CONTROL_STATE_OPTION, true);
        boolOptionDefaults.put(SHOW_INVARIANTS_OPTION, true);
        boolOptionDefaults.put(SHOW_RECIPE_STEPS_OPTION, true);
        boolOptionDefaults.put(SHOW_ABSENT_STATES_OPTION, true);
        boolOptionDefaults.put(SHOW_ASPECTS_OPTION, false);
        boolOptionDefaults.put(SHOW_VALUE_NODES_OPTION, false);
        boolOptionDefaults.put(SHOW_UNFILTERED_EDGES_OPTION, false);
        boolOptionDefaults.put(SHOW_ARROWS_ON_LABELS_OPTION, false);
        boolOptionDefaults.put(SHOW_BIDIRECTIONAL_EDGES_OPTION, true);
        intOptionDefaults.put(DELETE_RESOURCE_OPTION, BehaviourOption.ASK);
        intOptionDefaults.put(VERIFY_ALL_STATES_OPTION, BehaviourOption.NEVER);
    }

    /** Returns the user preferences for a given key, as a list of Strings. */
    public static String[] getUserPrefs(String key) {
        String[] result = new String[0];
        String storedValue = userPrefs.get(key, "");
        try {
            result = StringHandler.splitExpr(storedValue, ",");
        } catch (FormatException e) {
            assert false : String.format("Format error in user preference string %s: %s",
                storedValue,
                e.getMessage());
        }
        for (int i = 0; i < result.length; i++) {
            try {
                String newValue = StringHandler.toUnquoted(result[i], '"');
                assert result[i] != null : String
                    .format("User preference string %s is not correctly quoted", result[i]);
                result[i] = newValue;
            } catch (FormatException e) {
                assert false : String.format("Format error in user preference string %s: %s",
                    result[i],
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
            value = StringHandler.toQuoted(value, '"');
            result.append(value);
        }
        userPrefs.put(key, result.toString());
    }

    /** The persistently stored user preferences. */
    public static final Preferences userPrefs = Preferences.userNodeForPackage(Options.class);

    static {
        try {
            // add those default user option values that do not yet exist to the
            // preferences
            Set<String> keys = new HashSet<>(Arrays.asList(userPrefs.keys()));
            for (Map.Entry<String,Boolean> defaultsEntry : boolOptionDefaults.entrySet()) {
                if (!keys.contains(defaultsEntry.getKey())) {
                    userPrefs.putBoolean(defaultsEntry.getKey(), defaultsEntry.getValue());
                }
            }
            for (Map.Entry<String,Integer> defaultsEntry : intOptionDefaults.entrySet()) {
                if (!keys.contains(defaultsEntry.getKey())) {
                    userPrefs.putInt(defaultsEntry.getKey(), defaultsEntry.getValue());
                }
            }
        } catch (BackingStoreException exc) {
            // don't do anything
        }
    }

    /** Sets the look-and-feel. */
    public static void initLookAndFeel() {
        if (!lookAndFeelInit) {
            lookAndFeelInit = true;
            try {
                // LAF specific options that should be done before setting the LAF
                // go here
                MetalLookAndFeel.setCurrentTheme(new DesertBlue());
                // Set the look and feel
                UIManager.setLookAndFeel(new com.jgoodies.looks.plastic.PlasticLookAndFeel());
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /** Returns the default font set in the look-and-feel. */
    public static Font getDefaultFont() {
        if (DEFAULT_FONT == null) {
            initLookAndFeel();
            DEFAULT_FONT = MetalLookAndFeel.getCurrentTheme()
                .getUserTextFont();
        }
        return DEFAULT_FONT;
    }

    /** The default font set in the look-and-feel. */
    private static Font DEFAULT_FONT;

    /** Returns the default font used for node and edge labels. */
    public static Font getLabelFont() {
        if (LABEL_FONT == null) {
            initLookAndFeel();
            LABEL_FONT = GraphConstants.DEFAULTFONT;
            if (LABEL_FONT == null) {
                LABEL_FONT = UIManager.getDefaults()
                    .getFont("SansSerif");
            }
            // previously used: MetalLookAndFeel.getCurrentTheme().getUserTextFont();
        }
        return LABEL_FONT;
    }

    /** The default font used for node and edge labels. */
    private static Font LABEL_FONT;

    /** Returns the font for special (arrow-like) characters. */
    public static Font getSymbolFont() {
        if (SYMBOL_FONT == null) {
            initLookAndFeel();
            Font result = getLabelFont();
            if (!result.canDisplay(Util.DT)) {
                result = UIManager.getDefaults()
                    .getFont("SansSerif");
            }
            if (result == null || !result.canDisplay(Util.DT)) {
                result = loadFont("stixgeneralregular.ttf").deriveFont(getLabelFont().getSize2D());
            }
            SYMBOL_FONT = result;
        }
        return SYMBOL_FONT;
    }

    /** The font for special (arrow-like) characters. */
    private static Font SYMBOL_FONT;

    /** Loads in a TrueType font of a given name. */
    private static Font loadFont(String name) {
        Font result = null;
        try {
            result = Font.createFont(Font.TRUETYPE_FONT,
                Groove.getResource(name)
                    .openStream());
            result = result.deriveFont(getLabelFont().getSize2D());
        } catch (FileNotFoundException e) {
            // do nothing
        } catch (FontFormatException e) {
            // do nothing
        } catch (IOException e) {
            // do nothing
        }
        return result;
    }

    /** Flag indicating if {@link #initLookAndFeel()} has already been invoked. */
    private static boolean lookAndFeelInit;
}
