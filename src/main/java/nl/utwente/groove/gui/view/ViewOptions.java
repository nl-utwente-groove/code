/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2026 University of Twente
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
 */
package nl.utwente.groove.gui.view;

import org.eclipse.jdt.annotation.NonNullByDefault;
import nl.utwente.groove.util.AIGenerated;

/**
 * The display options as a graph view sees them: the keys of the options that
 * affect rendering, their current values, and the registration of listeners that
 * refresh a canvas when a value changes.
 * <p>
 * The menu machinery implementing this lives on the simulator side; the graph-view
 * layer and the visualization backends only ever see this interface.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Opus 5, 2026-09")
@NonNullByDefault
public interface ViewOptions {
    /**
     * Returns the current value of a given option.
     * @param option the key of the option; one of the constants of this interface
     * @return {@code true} if the option is known and currently selected
     */
    boolean isSelected(String option);

    /**
     * Indicates if a given option is currently under user control.
     * An option that is not enabled is invariably reported as unselected by
     * {@link GraphViewController#getOptionValue}.
     * @param option the key of the option; one of the constants of this interface
     */
    boolean isEnabled(String option);

    /**
     * Registers a listener to be notified when a given option changes value
     * or enabledness.
     * @param option the key of the option; one of the constants of this interface
     * @param listener the listener to be registered
     * @throws IllegalArgumentException if the option is unknown
     */
    void addOptionListener(String option, OptionRefreshListener listener);

    /**
     * Removes a listener registered through
     * {@link #addOptionListener(String, OptionRefreshListener)}.
     * @param option the key of the option; one of the constants of this interface
     * @param listener the listener to be removed
     * @throws IllegalArgumentException if the option is unknown
     */
    void removeOptionListener(String option, OptionRefreshListener listener);

    // ---------- graph show options ----------

    /** Show anchors option. */
    static final String SHOW_ANCHORS_OPTION = "Show anchors";
    /** Show aspects in graphs and rules option. */
    static final String SHOW_ASPECTS_OPTION = "Show aspect prefixes";
    /** Show bidirectional edges option. */
    static final String SHOW_BIDIRECTIONAL_EDGES_OPTION = "Show bidirectional edges";
    /** Show internal node ids option. */
    static final String SHOW_INTERNAL_NODE_IDS_OPTION = "Show internal node identities";
    /** Show user-defined node ids option. */
    static final String SHOW_USER_NODE_IDS_OPTION = "Show user-defined node identities";
    /** Show data values as nodes rather than as assignments. */
    static final String SHOW_VALUE_NODES_OPTION = "Show data values as nodes";
    /** Show arrows on labels rather than on edges. */
    static final String SHOW_ARROWS_ON_LABELS_OPTION = "Show arrows on labels";

    // ---------- LTS show options ----------

    /** Show call nesting option. */
    static final String SHOW_CALL_NESTING_OPTION = "Show call nesting on transitions";
    /** Show state ids option. */
    static final String SHOW_STATE_IDS_OPTION = "Show state identities";
    /** Show state status option. */
    static final String SHOW_STATE_STATUS_OPTION = "Show state status";
    /** Show control state option. */
    static final String SHOW_CONTROL_STATE_OPTION = "Show control information";
    /** Show system state properties option. */
    static final String SHOW_SYSTEM_STATE_PROPERTIES_OPTION
        = "Show system properties on the states";
    /** Show invariants option. */
    static final String SHOW_INVARIANTS_OPTION = "Show invariants on the states";
    /** Show absent states option. */
    static final String SHOW_ABSENT_STATES_OPTION = "Show absent states";
    /** Show recipe steps option. */
    static final String SHOW_RECIPE_STEPS_OPTION = "Show recipe steps";
}
