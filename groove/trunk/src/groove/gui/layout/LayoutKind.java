/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
 * $Id$
 */
package groove.gui.layout;

import java.util.HashMap;
import java.util.Map;

import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.graph.JGraphSimpleLayout;
import com.jgraph.layout.organic.JGraphFastOrganicLayout;
import com.jgraph.layout.organic.JGraphOrganicLayout;
import com.jgraph.layout.organic.JGraphSelfOrganizingOrganicLayout;
import com.jgraph.layout.simple.SimpleGridLayout;
import com.jgraph.layout.tree.JGraphCompactTreeLayout;
import com.jgraph.layout.tree.JGraphRadialTreeLayout;
import com.jgraph.layout.tree.JGraphTreeLayout;
import com.jgraph.layout.tree.OrganizationalChart;

/** Enumeration of possible JGraph layout algorithms. */
public enum LayoutKind {

    /** Puts the nodes in a circle. */
    SIMPLE_CIRCLE("Simple Circle", new JGraphSimpleLayout(
        JGraphSimpleLayout.TYPE_CIRCLE)),
    /** Tilts the nodes of the graph by a few points. */
    SIMPLE_TILT("Simple Tilt", new JGraphSimpleLayout(
        JGraphSimpleLayout.TYPE_TILT)),
    /** Random placement of nodes.  */
    SIMPLE_RANDOM("Simple Randomized", new JGraphSimpleLayout(
        JGraphSimpleLayout.TYPE_RANDOM, 500, 500)),
    /** Grid alignment. */
    SIMPLE_GRID("Simple Grid", new SimpleGridLayout()),
    /** Compact tree representation. */
    COMPACT_TREE("Compact Tree", new JGraphCompactTreeLayout()),
    /** Radial tree layout. */
    RADIAL_TREE("Radial Tree", new JGraphRadialTreeLayout()),
    /** Simple tree layout. */
    BASIC_TREE("Basic Tree", new JGraphTreeLayout()),
    /** Organizational Chart ??? */
    ORGANIZ_CHAR("Organizational Chart", new OrganizationalChart()),
    /** Organic Layout (Slow) */
    ORGANIC("Organic", new JGraphOrganicLayout()),
    /** Organic Layout (Fast) */
    FAST_ORGANIC("Fast Organic", new JGraphFastOrganicLayout()),
    /** Self-organizing map. */
    SELF_ORGANIZ("Self-Organizing", new JGraphSelfOrganizingOrganicLayout());

    private String displayString;
    private JGraphLayout layout;

    private LayoutKind(String displayString, JGraphLayout layout) {
        this.displayString = displayString;
        this.layout = layout;
    }

    /** Returns the string to be shown in the GUI. */
    public String getDisplayString() {
        return this.displayString + " Layout";
    }

    /** Returns the layout algorithm. */
    public JGraphLayout getLayout() {
        return this.layout;
    }

    /** Returns the prototype instance of the menu item. */
    public static LayouterItem getLayouterItemProto(LayoutKind kind) {
        LayouterItem result = map.get(kind);
        if (result == null) {
            result = new LayouterItem(kind);
            map.put(kind, result);
        }
        return result;
    }

    private static Map<LayoutKind,LayouterItem> map =
        new HashMap<LayoutKind,LayouterItem>();
}
