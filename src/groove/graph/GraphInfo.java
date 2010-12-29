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
import groove.util.Version;
import groove.view.FormatError;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that provides the keys needed for storing and retrieving data needed
 * for specific features.
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2008-01-30 09:32:57 $
 */
public class GraphInfo<N extends Node,E extends Edge<N>> implements Cloneable {
    /** Constructs a copy of an existing information object. */
    public GraphInfo(GraphInfo<?,?> info) {
        this.data = new HashMap<String,Object>(info.getData());
    }

    /** Constructs an empty information object. */
    public GraphInfo() {
        this.data = new HashMap<String,Object>();
    }

    /**
     * Returns the list of format errors associated with the graph, if any.
     * @return an error list stored in the info object, or <code>null</code>
     * @see #setErrors(Collection)
     */
    @SuppressWarnings("unchecked")
    public List<FormatError> getErrors() {
        return (List<FormatError>) this.data.get(ERRORS_KEY);
    }

    /**
     * Adds a given list of errors to those already stored. If there are no
     * errors stored, creates the list.
     * @see #setErrors(Collection)
     */
    public void addErrors(List<FormatError> errors) {
        List<FormatError> myErrors = getErrors();
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
    public void setErrors(Collection<FormatError> errors) {
        if (errors == null) {
            this.data.remove(ERRORS_KEY);
        } else {
            this.data.put(ERRORS_KEY, new ArrayList<FormatError>(errors));
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
    public LayoutMap<N,E> getLayoutMap() {
        return (LayoutMap<N,E>) this.data.get(LAYOUT_KEY);
    }

    /**
     * Sets the layout map (key {@link #LAYOUT_KEY}) in this info object to a
     * certain value.
     * @see #getLayoutMap()
     */
    public void setLayoutMap(
            LayoutMap<? extends Node,? extends Edge<N>> layoutMap) {
        this.data.put(LAYOUT_KEY, layoutMap);
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

    /**
     * Copies another graph info object into this one, overwriting all existing
     * keys but preserving those that are not overwritten.
     */
    public void load(GraphInfo<?,?> other) {
        this.data.putAll(other.getData());
        // copy the properties object
        if (other.hasProperties()) {
            this.data.put(PROPERTIES_KEY,
                new GraphProperties(other.getProperties(false)));
        }
    }

    @Override
    public GraphInfo<N,E> clone() {
        GraphInfo<N,E> result = new GraphInfo<N,E>();
        result.load(this);
        return result;
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
    public static <N extends Node,E extends Edge<N>> GraphInfo<N,E> getInfo(
            Graph<N,E> graph, boolean create) {
        GraphInfo<N,E> result = graph.getInfo();
        if (result == null && create) {
            result = graph.setInfo(new GraphInfo<N,E>());
        }
        return result;
    }

    /**
     * Convenience method to indicate if a graph has a non-empty set of errors.
     * @see #getErrors()
     */
    public static <N extends Node,E extends Edge<N>> boolean hasErrors(
            Graph<N,E> graph) {
        GraphInfo<N,E> graphInfo = graph.getInfo();
        return graphInfo != null && graphInfo.getErrors() != null;
    }

    /**
     * Convenience method to retrieve the list of format errors of a graph.
     * @see #getErrors()
     */
    public static <N extends Node,E extends Edge<N>> List<FormatError> getErrors(
            Graph<N,E> graph) {
        GraphInfo<N,E> graphInfo = graph.getInfo();
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
    public static <N extends Node,E extends Edge<N>> void addErrors(
            Graph<N,E> graph, List<FormatError> errors) {
        getInfo(graph, true).addErrors(errors);
    }

    /**
     * Convenience method to set the list of format errors of a graph.
     * @see #setErrors(Collection)
     */
    public static <N extends Node,E extends Edge<N>> void setErrors(
            Graph<N,E> graph, Collection<FormatError> errors) {
        if (errors != null) {
            getInfo(graph, true).setErrors(errors);
        }
    }

    /**
     * Convenience method to retrieve the file of a graph.
     */
    public static <N extends Node,E extends Edge<N>> File getFile(
            Graph<N,E> graph) {
        GraphInfo<N,E> graphInfo = graph.getInfo();
        return new File(graphInfo.getFile());
    }

    /**
     * Convenience method to set the file of a graph.
     */
    public static <N extends Node,E extends Edge<N>> void setFile(
            Graph<N,E> graph, String file) {
        if (file != null) {
            getInfo(graph, true).setFile(file);
        }
    }

    /**
     * Convenience method to test if a graph contains layout information.
     */
    public static <N extends Node,E extends Edge<N>> boolean hasLayoutMap(
            Graph<N,E> graph) {
        GraphInfo<N,E> graphInfo = graph.getInfo();
        return graphInfo != null && graphInfo.hasLayoutMap();
    }

    /**
     * Convenience method to retrieve the layout map from a graph.
     */
    public static <N extends Node,E extends Edge<N>> LayoutMap<N,E> getLayoutMap(
            Graph<N,E> graph) {
        GraphInfo<N,E> graphInfo = graph.getInfo();
        if (graphInfo == null) {
            return null;
        } else {
            return graphInfo.getLayoutMap();
        }
    }

    /**
     * Convenience method to set the layout map of a graph.
     */
    public static <N extends Node,E extends Edge<N>> void setLayoutMap(
            Graph<N,E> graph, LayoutMap<N,E> layoutMap) {
        if (layoutMap != null) {
            getInfo(graph, true).setLayoutMap(layoutMap);
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
    public static <N extends Node,E extends Edge<N>> GraphProperties getProperties(
            Graph<N,E> graph, boolean create) {
        GraphInfo<N,E> graphInfo = getInfo(graph, create);
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
    public static <N extends Node,E extends Edge<N>> void setProperties(
            Graph<N,E> graph, GraphProperties properties) {
        if (properties != null) {
            getInfo(graph, true).setProperties(properties);
        }
    }

    /**
     * Convenience method to set the version of a graph.
     * @see Version#GXL_VERSION
     */
    public static void setVersion(Graph<?,?> graph, String version) {
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
    public static String getVersion(Graph<?,?> graph) {
        GraphProperties properties = getProperties(graph, false);
        if (properties == null) {
            return null;
        } else {
            return properties.getVersion();
        }
    }

    /**
     * Transfers all available graph information from one graph to another,
     * modulo a given element map. The element map may be null if the node and
     * edge identities of source and target coincide.
     * @param source the graph to transfer the information from
     * @param target the graph to transfer the information to
     * @param elementMap map from the source elements to the target elements
     */
    public static <N1 extends Node,E1 extends Edge<N1>,N2 extends Node,E2 extends Edge<N2>> void transfer(
            Graph<N1,E1> source, Graph<N2,E2> target,
            ElementMap<N1,E1,N2,E2> elementMap) {
        GraphInfo<N1,E1> sourceInfo = source.getInfo();
        if (sourceInfo != null) {
            // copy all the info
            GraphInfo<N2,E2> targetInfo = target.setInfo(sourceInfo);
            if (elementMap != null) {
                // modify the layout map using the element map
                LayoutMap<N1,E1> sourceLayoutMap = sourceInfo.getLayoutMap();
                if (sourceLayoutMap != null) {
                    targetInfo.setLayoutMap(sourceLayoutMap.afterInverse(elementMap));
                }
                List<FormatError> sourceErrors = sourceInfo.getErrors();
                if (sourceErrors != null) {
                    List<FormatError> targetErrors =
                        new ArrayList<FormatError>();
                    for (FormatError error : sourceErrors) {
                        targetErrors.add(error.transfer(elementMap.nodeMap()).transfer(
                            elementMap.edgeMap()));
                    }
                    targetInfo.setErrors(targetErrors);
                }
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
     * Key for graph properties.
     */
    public static final String PROPERTIES_KEY = "properties";
    /**
     * Key for layout-info.
     */
    public static final String LAYOUT_KEY = "layout";
}
