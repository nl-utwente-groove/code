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

import static nl.utwente.groove.graph.GraphProperties.Key.ENABLED;
import static nl.utwente.groove.graph.GraphProperties.Key.INJECTIVE;
import static nl.utwente.groove.graph.GraphProperties.Key.PRIORITY;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import nl.utwente.groove.grammar.Action.Role;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.graph.GraphProperties.Key;
import nl.utwente.groove.gui.layout.LayoutMap;
import nl.utwente.groove.util.DefaultFixable;
import nl.utwente.groove.util.Properties.Entry;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

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
        this.data.put(PROPERTIES_KEY, new GraphProperties());
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
     * Returns the graph properties map associated with the graph (key
     * {@link #PROPERTIES_KEY}).
     * @return a property map, or <code>null</code>
     * @see #setProperties(GraphProperties)
     */
    private GraphProperties getProperties() {
        return (GraphProperties) this.data.get(PROPERTIES_KEY);
    }

    /**
     * Copies the properties in a given map to this info object (key
     * {@link #PROPERTIES_KEY}).
     * @see #getProperties()
     */
    private void setProperties(GraphProperties properties) {
        testFixed(false);
        this.data.put(PROPERTIES_KEY, new GraphProperties(properties));
    }

    @Override
    public boolean setFixed() {
        boolean result = super.setFixed();
        if (result) {
            getProperties().setFixed();
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
            // copy rather than clone the graph properties
            GraphProperties properties = sourceInfo.getProperties();
            targetInfo.setProperties(properties);
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
                sourceErrors = sourceErrors.transfer(elementMap);
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
     * Returns an unmodifiable copy of the properties map of a given graph.
     * @param graph the queried graph; non-{@code null}
     * @return a copy of the properties object of the queried graph, or an empty
     * properties map if the graph has no info object
     */
    public static GraphProperties getProperties(Graph graph) {
        GraphProperties result = null;
        if (graph.hasInfo()) {
            result = graph.getInfo().getProperties();
        } else {
            result = EMPTY_PROPERTIES;
        }
        return result;
    }

    /**
     * Convenience method to set the graph properties map of a given graph.
     * The graph will receive a copy of the properties passed in.
     * @param graph the graph to be modified; non-{@code null}
     * @param properties the new properties map; non-{@code null}
     */
    public static void setProperties(Graph graph, GraphProperties properties) {
        assert !graph.isFixed();
        graph.getInfo().setProperties(properties);
    }

    /**
     * Returns the priority property of a given graph. The priority is a non-negative number.
     * Yields {@link Rule#DEFAULT_PRIORITY} if the priority has not been set explicitly.
     * @param graph the queried graph; non-{@code null}
     * @return the non-negative priority of {@code graph}
     * @see Key#PRIORITY
     */
    static public int getPriority(Graph graph) {
        return getProperty(graph, PRIORITY).getInteger();
    }

    /**
     * Sets the role of a given rule graph to a certain value.
     * @param graph the graph to be modified; non-{@code null} and non-fixed
     * @param role the new role; non-{@code null}
     */
    static public void setRole(Graph graph, Role role) {
        setProperty(graph, Key.ROLE, role);
    }

    /**
     * Returns the role of a given rule graph.
     * @param graph the queried graph; non-{@code null}
     * @return the role; non-{@code null}
     * @see Key#ROLE
     */
    static public Optional<Role> getRole(Graph graph) {
        return getProperty(graph, Key.ROLE).getRole();
    }

    /**
     * Sets the priority of a given graph to a certain value.
     * @param graph the graph to be modified; non-{@code null} and non-fixed
     * @param priority the new priority value; should be non-negative
     */
    static public void setPriority(Graph graph, int priority) {
        setProperty(graph, PRIORITY, priority);
    }

    /**
     * Returns the enabledness property of a given graph.
     * Yields <code>true</code> by default.
     * @param graph the queried graph; non-{@code null}
     * @see Key#ENABLED
     */
    static public boolean isEnabled(Graph graph) {
        return getProperty(graph, ENABLED).getBoolean();
    }

    /**
     * Sets the enabledness of a given graph to a certain value.
     * @param graph the graph to be modified; non-{@code null} and non-fixed
     * @param enabled the new enabledness value
     */
    static public void setEnabled(Graph graph, boolean enabled) {
        setProperty(graph, ENABLED, enabled);
    }

    /**
     * Returns the injectivity property of a given graph.
     * Yields <code>false</code> by default.
     * @param graph the queried graph; non-{@code null}
     * @see Key#INJECTIVE
     */
    static public boolean isInjective(Graph graph) {
        return getProperty(graph, INJECTIVE).getBoolean();
    }

    /**
     * Sets the injectivity of a given graph to a certain value.
     * @param graph the graph to be modified; non-{@code null} and non-fixed
     * @param injective the new injectivity value
     */
    static public void setInjective(Graph graph, boolean injective) {
        setProperty(graph, INJECTIVE, injective);
    }

    /**
     * Returns the remark property from a given graph.
     * Yields the empty string by default.
     * @param graph the queried graph; non-{@code null}
     * @see Key#REMARK
     */
    static public String getRemark(Graph graph) {
        return getProperty(graph, Key.REMARK).getString();
    }

    /**
     * Sets the remark for a given graph to a certain value.
     * @param graph the graph to be modified; non-{@code null} and non-fixed
     * @param remark the remark for this graph; non-{@code null}
     */
    static public void setRemark(Graph graph, String remark) {
        setProperty(graph, Key.REMARK, remark);
    }

    /**
     * Returns the string format property from a given graph.
     * Yields the empty string if the graph has
     * no explicitly set format string.
     * @param graph the queried graph; non-{@code null}
     * @see Key#FORMAT
     */
    static public String getFormatString(Graph graph) {
        return getProperty(graph, Key.FORMAT).getString();
    }

    /**
     * Sets the format string for a given graph to a certain value.
     * @param graph the graph to be modified; non-{@code null} and non-fixed
     * @param formatString the format string for this graph; may be {@code null}
     */
    static public void setFormatString(Graph graph, String formatString) {
        setProperty(graph, Key.FORMAT, formatString);
    }

    /**
     * Returns the transition label of a given graph.
     * Yields the empty string if the transition label has not been set explicitly
     * @param graph the queried graph; non-{@code null}
     * @see Key#TRANSITION_LABEL
     */
    static public String getTransitionLabel(Graph graph) {
        return getProperty(graph, Key.TRANSITION_LABEL).getString();
    }

    /**
     * Convenience method to set the transition label for a given graph to a certain value.
     * @param graph the graph to be modified; non-{@code null} and non-fixed
     * @param label the transition label for this graph; may be {@code null}
     */
    static public void setTransitionLabel(Graph graph, String label) {
        setProperty(graph, Key.TRANSITION_LABEL, label);
    }

    /**
     * Returns the version property from a given graph.
     * Yields the empty string if the graph has
     * no explicitly set version.
     * @param graph the queried graph; non-{@code null}
     * @see Key#VERSION
     */
    static public String getVersion(Graph graph) {
        return getProperty(graph, Key.VERSION).getString();
    }

    /**
     * Convenience method to retrieve a graph property from a given graph.
     * Delegates to {@link GraphProperties#getProperty}
     * @param graph the queried graph; non-{@code null}
     * @return the stored or default property value for the given key;
     * non-{@code null}
     */
    private static nl.utwente.groove.util.Properties.Entry getProperty(Graph graph, Key key) {
        Entry result = null;
        try {
            if (graph.hasInfo()) {
                result = graph.getInfo().getProperties().parseProperty(key);
            }
        } catch (FormatException exc) {
            // do nothing; default value set below
        }
        if (result == null) {
            result = key.parser().getDefaultValue();
        }
        return result;
    }

    /**
     * Convenience method to change a graph property of a given graph.
     * Delegates to {@link GraphProperties#setProperty}
     * @param graph the graph to be modified; non-{@code null} and non-fixed
     */
    private static void setProperty(Graph graph, Key key, Object value) {
        GraphProperties properties = graph.getInfo().getProperties();
        properties.storeValue(key, value);
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
    /** Constant empty properties object. */
    private static final GraphProperties EMPTY_PROPERTIES;

    static {
        EMPTY_PROPERTIES = new GraphProperties();
        EMPTY_PROPERTIES.setFixed();
    }
}
