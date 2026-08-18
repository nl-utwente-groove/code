/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import nl.utwente.groove.graph.layout.LayoutMap;
import nl.utwente.groove.util.DefaultFixable;
import nl.utwente.groove.util.Properties;
import nl.utwente.groove.util.parse.FormatErrorSet;

/**
 * Class storing additional information about a graph.
 * This is delegated to save space for those graphs that do not have
 * such additional information.
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2008-01-30 09:32:57 $
 */
public class GraphInfo extends DefaultFixable {
    /** Constructs an empty information object. */
    public GraphInfo() {
        this.data = new HashMap<>();
        this.data.put(LAYOUT_KEY, new LayoutMap());
        this.data.put(ERRORS_KEY, new FormatErrorSet());
    }

    /**
     * Returns the list of format errors associated with the graph.
     * @return the non-{@code null} error set stored in the info object
     * @see #setErrors
     */
    public FormatErrorSet getErrors() {
        return (FormatErrorSet) this.data.get(ERRORS_KEY);
    }

    /**
     * Appends a list of format errors (key {@link #ERRORS_KEY}) to the existing
     * errors in this info object.
     * @see #getErrors()
     */
    void setErrors(FormatErrorSet errors) {
        this.data.put(ERRORS_KEY, errors);
    }

    /**
     * Returns the layout map (with key {@link #LAYOUT_KEY}) in this info
     * object. Note that the layout map is always non-{@code null} and modifiable
     * @return the (non-{@code null}, modifiable) layout map
     */
    public LayoutMap getLayoutMap() {
        return (LayoutMap) this.data.get(LAYOUT_KEY);
    }

    /**
     * Sets the layout map (key {@link #LAYOUT_KEY}) in this info object to a
     * certain value.
     * @see #getLayoutMap()
     */
    private void setLayoutMap(LayoutMap layoutMap) {
        getLayoutMap().load(layoutMap);
    }

    /**
     * Returns the properties map associated with the graph (key
     * {@link #PROPERTIES_KEY}). The map is stored and returned as a base
     * {@link Properties} object; the concrete subclass is determined by the
     * code that stores it (see {@link #setProperties(Properties)}).
     * @return the stored property map, or <code>null</code> if none was stored
     * @see #setProperties(Properties)
     */
    public Properties getProperties() {
        return (Properties) this.data.get(PROPERTIES_KEY);
    }

    /**
     * Stores a given property map in this info object (key
     * {@link #PROPERTIES_KEY}). The map is stored as given, without copying;
     * defensive copying is the caller's responsibility.
     * @see #getProperties()
     */
    public void setProperties(Properties properties) {
        testFixed(false);
        this.data.put(PROPERTIES_KEY, properties);
    }

    @Override
    public boolean setFixed() {
        boolean result = super.setFixed();
        if (result) {
            var properties = getProperties();
            if (properties != null) {
                properties.setFixed();
            }
            this.data = Collections.unmodifiableMap(this.data);
        }
        return result;
    }

    @Override
    public String toString() {
        return "Graph information: " + this.data;
    }

    /**
     * Map for the internally stored data.
     */
    private Map<String,Object> data;

    /**
     * Transfers all graph information from one graph to another,
     * modulo a given element map.
     * Convenience method combining {@link #transferProperties} and {@link #transferErrors}
     */
    public static void transferAll(Graph source, Graph target, GraphMap elementMap) {
        transferProperties(source, target, elementMap);
        transferErrors(source, target, elementMap);
    }

    /**
     * Transfers all graph properties and layout from one graph to another,
     * modulo a given element map. The element map may be null if the node and
     * edge identities of source and target coincide.
     * Errors are not transferred (see {@link #transferErrors(Graph, Graph, GraphMap)}.
     * @param source the graph to transfer the information from
     * @param target the graph to transfer the information to
     * @param elementMap map from the source elements to the target elements
     */
    public static void transferProperties(Graph source, Graph target, GraphMap elementMap) {
        assert !target.isFixed();
        if (source.hasInfo()) {
            // copy all the info
            GraphInfo sourceInfo = source.getInfo();
            GraphInfo targetInfo = target.getInfo();
            LayoutMap sourceLayoutMap = sourceInfo.getLayoutMap();
            if (elementMap != null) {
                // modify the layout map using the element map
                sourceLayoutMap = sourceLayoutMap.afterInverse(elementMap);
            }
            targetInfo.setLayoutMap(sourceLayoutMap);
            // copy the graph properties, if any
            var properties = sourceInfo.getProperties();
            if (properties != null) {
                targetInfo.setProperties(properties.clone());
            }
        }
    }

    /**
     * Transfers all errors from one graph to another,
     * modulo a given element map. The element map may be null if the node and
     * edge identities of source and target coincide.
     * @param source the graph to transfer the errors from
     * @param target the graph to transfer the errors to
     * @param elementMap map from the source elements to the target elements
     */
    public static void transferErrors(Graph source, Graph target, GraphMap elementMap) {
        assert !target.isFixed();
        if (source.hasErrors()) {
            // copy all the info
            var sourceErrors = source.getErrors();
            if (elementMap != null) {
                // modify the errors using the element map
                sourceErrors = elementMap.transfer(sourceErrors);
            }
            target.setErrors(sourceErrors);
        }
    }

    /**
     * Retrieves the layout map from a given graph.
     * @param graph the queried graph; non-{@code null}
     * @return an alias to the layout map of the graph,
     * or {@code null} if the graph has no associated layout map
     */
    public static LayoutMap getLayoutMap(Graph graph) {
        LayoutMap result = null;
        if (graph.hasInfo()) {
            result = graph.getInfo().getLayoutMap();
        }
        return result;
    }

    /**
     * Sets the layout map of a given graph.
     * @param graph the graph to be modified; non-{@code null}
     * @param layoutMap the new layout map; non-{@code null}
     */
    public static void setLayoutMap(Graph graph, LayoutMap layoutMap) {
        graph.getInfo().setLayoutMap(layoutMap);
    }

    /**
     * Key for error list.
     */
    private static final String ERRORS_KEY = "errors";
    /**
     * Key for graph properties.
     */
    private static final String PROPERTIES_KEY = "properties";
    /**
     * Key for layout-info.
     */
    private static final String LAYOUT_KEY = "layout";
}
