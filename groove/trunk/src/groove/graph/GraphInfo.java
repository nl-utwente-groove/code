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
 * $Id: GraphInfo.java,v 1.13 2008-01-30 09:32:57 iovka Exp $
 */
package groove.graph;

import groove.gui.layout.LayoutMap;
import groove.util.Groove;
import groove.util.Version;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that provides the keys needed for storing and retrieving data needed
 * for specific features.
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2008-01-30 09:32:57 $
 */
public class GraphInfo {

    /** Constructs a copy of an existing information object. */
    public GraphInfo(GraphInfo info) {
        this.data = new HashMap<String,Object>(info.getData());
    }

    /** Constructs an empty information object. */
    public GraphInfo() {
        this.data = new HashMap<String,Object>();
    }

    /**
     * Returns the list of format errors associated with the graph, if any.
     * @return an error list stored in the info object, or <code>null</code>
     * @see #setErrors(List)
     */
    @SuppressWarnings("unchecked")
    public List<String> getErrors() {
        return (List<String>) this.data.get(ERRORS_KEY);
    }

    /**
     * Adds a given list of errors to those already stored. If there are no
     * errors stored, creates the list.
     * @see #setErrors(List)
     */
    public void addErrors(List<String> errors) {
        List<String> myErrors = getErrors();
        if (myErrors == null) {
            setErrors(errors);
        } else {
            myErrors.addAll(errors);
        }
    }

    /**
     * Appends a list of format errors (key {@link #ERRORS_KEY}) to the existing
     * errors in this info object. The errors property is created first if
     * necessary. to a certain list. If the value is <code>null</code>, the key
     * is removed altogether.
     * @see #getErrors()
     */
    public void setErrors(List<String> errors) {
        if (errors == null) {
            this.data.remove(ERRORS_KEY);
        } else {
            this.data.put(ERRORS_KEY, new ArrayList<String>(errors));
        }
    }

    /**
     * Returns the file associated with the graph, if any.
     * @return a file stored in the info object, or <code>null</code>
     * @see #setFile(String)
     */
    public String getFile() {
        return (String) this.data.get(FILE_KEY);
    }

    /**
     * 
     * Sets the file (key {@link #FILE_KEY}) in this info object to a certain
     * value. If the value is <code>null</code>, the key is removed altogether.
     * @see #getFile()
     */
    public void setFile(String file) {
        if (file == null) {
            this.data.remove(FILE_KEY);
        } else {
            this.data.put(FILE_KEY, file);
        }
    }

    /**
     * Tests if this info object contains a layout map (with key
     * {@link #LAYOUT_KEY}).
     * @see #getLayoutMap()
     */
    public boolean hasLayoutMap() {
        return this.data.containsKey(LAYOUT_KEY);
    }

    /**
     * Returns the layout map (with key {@link #LAYOUT_KEY}) in this info
     * object, if any.
     * @see #setLayoutMap(LayoutMap)
     */
    @SuppressWarnings("unchecked")
    public LayoutMap<Node,Edge> getLayoutMap() {
        return (LayoutMap<Node,Edge>) this.data.get(LAYOUT_KEY);
    }

    /**
     * Sets the layout map (key {@link #LAYOUT_KEY}) in this info object to a
     * certain value.
     * @see #getLayoutMap()
     */
    public void setLayoutMap(LayoutMap<Node,Edge> layoutMap) {
        this.data.put(LAYOUT_KEY, layoutMap);
    }

    /** Tests if this info object has a value for the {@link #NAME_KEY}. */
    public boolean hasName() {
        return this.data.get(NAME_KEY) != null;
    }

    /**
     * Returns the name associated with the graph, if any.
     * @return a name stored in the info object, or <code>null</code>
     * @see #setName(String)
     */
    public String getName() {
        return (String) this.data.get(NAME_KEY);
    }

    /**
     * Sets the name (key {@link #NAME_KEY}) in this info object to a certain
     * value. If the value is <code>null</code>, the key is removed altogether.
     * @see #getName()
     */
    public void setName(String name) {
        if (name == null) {
            this.data.remove(NAME_KEY);
        } else {
            this.data.put(NAME_KEY, name);
        }
    }

    /** Tests if this info object has a value for the {@link #PROPERTIES_KEY}. */
    public boolean hasProperties() {
        return this.data.get(PROPERTIES_KEY) != null;
    }

    /**
     * Returns the graph properties map associated with the graph (key
     * {@link #PROPERTIES_KEY}). The parameter indicates if the map should be
     * created in case it is not yet there.
     * @param create if <code>true</code> and this data object does not contain
     *        properties, create and return an empty properties object
     * @return a property map, or <code>null</code>
     * @see #setProperties(GraphProperties)
     */
    public GraphProperties getProperties(boolean create) {
        GraphProperties result =
            (GraphProperties) this.data.get(PROPERTIES_KEY);
        if (create && result == null) {
            result = new GraphProperties();
            this.data.put(PROPERTIES_KEY, result);
        }
        return result;
    }

    /**
     * Copies the properties in a given map to this info object (key
     * {@link #PROPERTIES_KEY}).
     * @see #getProperties(boolean)
     */
    public void setProperties(GraphProperties properties) {
        GraphProperties currentProperties = getProperties(properties != null);
        if (currentProperties != null) {
            currentProperties.clear();
            if (properties != null) {
                currentProperties.putAll(properties);
            }
        }
    }

    /**
     * Clones a GraphProperties object and inserts(overwrites) it in the hash
     * map of the GraphInfo.
     * @param properties is the GraphProperties object to be cloned
     */
    public void newProperties(GraphProperties properties) {
        GraphProperties result = new GraphProperties(properties);
        this.data.put(PROPERTIES_KEY, result);
    }

    /** Tests if this info object has a value for the {@link #ROLE_KEY}. */
    public boolean hasRole() {
        return this.data.get(ROLE_KEY) != null;
    }

    /**
     * Returns the role of the graph, if any.
     * @return a role stored in the info object, or <code>null</code>
     * @see #setRole(String)
     */
    public String getRole() {
        return (String) this.data.get(ROLE_KEY);
    }

    /**
     * 
     * Sets the role (key {@link #ROLE_KEY}) in this info object to a certain
     * value. If the value is <code>null</code>, the key is removed altogether.
     * @see #getRole()
     */
    public void setRole(String role) {
        if (role == null) {
            this.data.remove(ROLE_KEY);
        } else {
            this.data.put(ROLE_KEY, role);
        }
    }

    /**
     * Copies another graph info object into this one, overwriting all existing
     * keys but preserving those that are not overwritten.
     */
    public void load(GraphInfo other) {
        this.data.putAll(other.getData());
        // copy the properties object
        if (other.hasProperties()) {
            this.data.put(PROPERTIES_KEY, new GraphProperties(
                other.getProperties(false)));
        }
    }

    @Override
    public String toString() {
        return "Graph information: " + this.data;
    }

    /** Returns the internally stored data. */
    public final Map<String,Object> getData() {
        return this.data;
    }

    /**
     * Map for the internally stored data.
     */
    private final Map<String,Object> data;

    /**
     * Convenience method to retrieve a {@link GraphInfo} object form a given
     * graph, creating it if necessary.
     * @param graph the graph for which the info object is to be (created and)
     *        retrieved
     * @param create if <code>true</code>, the info object should be created if
     *        not yet there
     * @return a non-<code>null</code> value which equals (afterwards)
     *         <code>graph.getInfo()</code>
     */
    public static GraphInfo getInfo(GraphShape graph, boolean create) {
        GraphInfo result = graph.getInfo();
        if (result == null && create) {
            result = graph.setInfo(new GraphInfo());
        }
        return result;
    }

    /**
     * Convenience method to indicate if a graph has a non-empty set of errors.
     * @see #getErrors()
     */
    public static boolean hasErrors(GraphShape graph) {
        GraphInfo graphInfo = graph.getInfo();
        return graphInfo != null && graphInfo.getErrors() != null;
    }

    /**
     * Convenience method to retrieve the list of format errors of a graph.
     * @see #getErrors()
     */
    public static List<String> getErrors(GraphShape graph) {
        GraphInfo graphInfo = graph.getInfo();
        if (graphInfo == null) {
            return null;
        } else {
            return graphInfo.getErrors();
        }
    }

    /**
     * Convenience method to add a list of errors to a graph.
     * @see #addErrors(List)
     */
    public static void addErrors(GraphShape graph, List<String> errors) {
        getInfo(graph, true).addErrors(errors);
    }

    /**
     * Convenience method to set the list of format errors of a graph.
     * @see #setErrors(List)
     */
    public static void setErrors(GraphShape graph, List<String> errors) {
        if (errors != null) {
            getInfo(graph, true).setErrors(errors);
        }
    }

    /**
     * Convenience method to retrieve the file of a graph.
     */
    public static File getFile(GraphShape graph) {
        GraphInfo graphInfo = graph.getInfo();
        return new File(graphInfo.getFile());
    }

    /**
     * Convenience method to set the file of a graph.
     */
    public static void setFile(GraphShape graph, String file) {
        if (file != null) {
            getInfo(graph, true).setFile(file);
        }
    }

    /**
     * Convenience method to test if a graph contains layout information.
     */
    public static boolean hasLayoutMap(GraphShape graph) {
        GraphInfo graphInfo = graph.getInfo();
        return graphInfo != null && graphInfo.hasLayoutMap();
    }

    /**
     * Convenience method to retrieve the layout map from a graph.
     */
    public static LayoutMap<Node,Edge> getLayoutMap(GraphShape graph) {
        GraphInfo graphInfo = graph.getInfo();
        if (graphInfo == null) {
            return null;
        } else {
            return graphInfo.getLayoutMap();
        }
    }

    /**
     * Convenience method to set the layout map of a graph.
     */
    public static void setLayoutMap(GraphShape graph,
            LayoutMap<Node,Edge> layoutMap) {
        if (layoutMap != null) {
            getInfo(graph, true).setLayoutMap(layoutMap);
        }
    }

    /**
     * Convenience method to retrieve the name of a graph.
     * @return the stored name of the graph; <code>null</code> if no name is
     *         stored
     * @see #getName()
     */
    public static String getName(GraphShape graph) {
        GraphInfo graphInfo = graph.getInfo();
        if (graphInfo == null) {
            return null;
        } else {
            return graphInfo.getName();
        }
    }

    /**
     * Convenience method to set the name of a graph.
     * @see #setName(String)
     */
    public static void setName(GraphShape graph, String name) {
        GraphInfo info = getInfo(graph, name != null);
        if (info != null) {
            info.setName(name);
        }
    }

    /**
     * Convenience method to retrieve the properties map from a graph, creating
     * it is necessary if any.
     * @param graph the graph to retrieve the properties from
     * @param create if <code>true</code>, the properties map (and so the info
     *        object itself) should be created if not yet there
     * @return the properties map of <code>graph</code>, or <code>null</code>
     */
    public static GraphProperties getProperties(GraphShape graph, boolean create) {
        GraphInfo graphInfo = getInfo(graph, create);
        if (graphInfo == null) {
            return null;
        } else {
            return graphInfo.getProperties(create);
        }
    }

    /**
     * Convenience method to set the graph properties of a graph. Only sets the
     * map if it is not <code>null</code> or empty.
     */
    public static void setProperties(GraphShape graph,
            GraphProperties properties) {
        if (properties != null) {
            getInfo(graph, true).setProperties(properties);
        }
    }

    /**
     * Convenience method to set the version of a graph.
     * @see Version#GXL_VERSION
     */
    public static void setVersion(GraphShape graph, String version) {
        GraphProperties properties =
            getProperties(graph, version.length() != 0);
        if (properties != null) {
            properties.setVersion(version);
        }
    }

    /**
     * Convenience method to retrieve the version of a graph.
     * @see Version#GXL_VERSION
     */
    public static String getVersion(GraphShape graph) {
        GraphProperties properties = getProperties(graph, false);
        if (properties == null) {
            return null;
        } else {
            return properties.getVersion();
        }
    }

    /**
     * Convenience method to retrieve the role of a graph.
     * @return the stored role of the graph; <code>null</code> if no role is
     *         stored
     * @see #setRole(String)
     */
    public static String getRole(GraphShape graph) {
        GraphInfo graphInfo = graph.getInfo();
        if (graphInfo == null) {
            return null;
        } else {
            return graphInfo.getRole();
        }
    }

    /**
     * Convenience method to set the role of a graph.
     * @see #setRole(String)
     */
    public static void setRole(GraphShape graph, String role) {
        GraphInfo info = getInfo(graph, role != null);
        if (info != null) {
            info.setRole(role);
        }
    }

    /**
     * Convenience method to test whether the role of a graph is <i>rule</i>.
     * @see #getRole()
     * @see Groove#RULE_ROLE
     */
    public static boolean hasRuleRole(GraphShape graph) {
        return Groove.RULE_ROLE.equals(getRole(graph));
    }

    /**
     * Convenience method to test whether the role of a graph is <i>graph</i>.
     * @see #getRole()
     * @see Groove#GRAPH_ROLE
     */
    public static boolean hasGraphRole(GraphShape graph) {
        return Groove.GRAPH_ROLE.equals(getRole(graph));
    }

    /**
     * Convenience method to set the role of a graph to <i>rule</i>.
     * @see #setRole(String)
     * @see Groove#RULE_ROLE
     */
    public static void setRuleRole(GraphShape graph) {
        setRole(graph, Groove.RULE_ROLE);
    }

    /**
     * Convenience method to set the role of a graph to <i>graph</i>.
     * @see #setRole(String)
     * @see Groove#GRAPH_ROLE
     */
    public static void setGraphRole(GraphShape graph) {
        setRole(graph, Groove.GRAPH_ROLE);
    }

    /**
     * Transfers all available graph information from one graph to another,
     * modulo a given element map. The element map may be null if the node and
     * edge identities of source and target coincide.
     * @param source the graph to transfer the information from
     * @param target the graph to transfer the information to
     * @param elementMap map from the source elements to the target elements
     */
    public static void transfer(GraphShape source, GraphShape target,
            NodeEdgeMap elementMap) {
        GraphInfo sourceInfo = source.getInfo();
        if (sourceInfo != null) {
            // copy all the info
            target.setInfo(sourceInfo);
            // modify the layout map using the element map
            LayoutMap<Node,Edge> layoutMap = sourceInfo.getLayoutMap();
            if (layoutMap != null && elementMap != null) {
                layoutMap = layoutMap.afterInverse(elementMap);
                target.getInfo().setLayoutMap(layoutMap);
            }
            // copy rather than clone the graph properties
            GraphProperties properties = sourceInfo.getProperties(false);

            if (properties != null) {
                target.getInfo().newProperties(properties);
            }
        }
    }

    /**
     * Key for error list.
     */
    public static final String ERRORS_KEY = "errors";
    /**
     * Key for storage file.
     */
    public static final String FILE_KEY = "file";
    /**
     * Key for graph role. The value should be one of {@link Groove#GRAPH_ROLE}
     * or {@link Groove#RULE_ROLE} .
     */
    public static final String ROLE_KEY = "type";
    /**
     * Key for graph name.
     */
    public static final String NAME_KEY = "name";
    /**
     * Key for graph properties.
     */
    public static final String PROPERTIES_KEY = "properties";
    /**
     * Key for layout-info.
     */
    public static final String LAYOUT_KEY = "layout";
}
