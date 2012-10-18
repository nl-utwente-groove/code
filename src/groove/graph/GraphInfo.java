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
import groove.util.DefaultFixable;
import groove.util.Version;
import groove.view.FormatErrorSet;
import groove.view.FormatException;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that provides the keys needed for storing and retrieving data needed
 * for specific features.
 * @author Harmen Kastenberg
 * @version $Revision: 4089 $ $Date: 2008-01-30 09:32:57 $
 */
public class GraphInfo<N extends Node,E extends Edge> extends DefaultFixable
        implements Cloneable {
    /** Constructs a copy of an existing information object. */
    public GraphInfo(GraphInfo<?,?> info) {
        this.data = new HashMap<String,Object>(info.getData());
        this.data.put(PROPERTIES_KEY, new GraphProperties());
    }

    /** Constructs an empty information object. */
    public GraphInfo() {
        this.data = new HashMap<String,Object>();
        this.data.put(LAYOUT_KEY, new LayoutMap<N,E>());
    }

    /**
     * Returns the list of format errors associated with the graph, if any.
     * @return an error list stored in the info object, or <code>null</code>
     * @see #setErrors
     */
    public FormatErrorSet getErrors() {
        return (FormatErrorSet) this.data.get(ERRORS_KEY);
    }

    /**
     * Adds a given list of errors to those already stored. If there are no
     * errors stored, creates the list.
     * @see #setErrors
     */
    public void addErrors(FormatErrorSet errors) {
        FormatErrorSet myErrors = getErrors();
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
    public void setErrors(FormatErrorSet errors) {
        if (errors == null) {
            this.data.remove(ERRORS_KEY);
        } else {
            this.data.put(ERRORS_KEY, new FormatErrorSet(errors));
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
     * Returns the layout map (with key {@link #LAYOUT_KEY}) in this info
     * object. Note that the layout map is always non-{@code null} and modifiable
     * @return the (non-{@code null}, modifiable) layout map
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
    public void setLayoutMap(LayoutMap<? extends N,? extends E> layoutMap) {
        getLayoutMap().fill(layoutMap);
    }

    /**
     * Returns the graph properties map associated with the graph (key
     * {@link #PROPERTIES_KEY}).
     * @return a property map, or <code>null</code>
     * @see #setProperties(GraphProperties)
     */
    public GraphProperties getProperties() {
        return (GraphProperties) this.data.get(PROPERTIES_KEY);
    }

    /**
     * Copies the properties in a given map to this info object (key
     * {@link #PROPERTIES_KEY}).
     * @see #getProperties()
     */
    public void setProperties(GraphProperties properties) {
        testFixed(false);
        GraphProperties props = getProperties();
        props.clear();
        props.putAll(properties);
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
    public void load(GraphInfo<N,E> other) {
        for (Map.Entry<String,Object> e : other.getData().entrySet()) {
            String key = e.getKey();
            Object value = e.getValue();
            if (key.equals(PROPERTIES_KEY)) {
                // clone (and do not share) the properties object
                value = new GraphProperties((GraphProperties) value);
            } else if (key.equals(LAYOUT_KEY)) {
                // clone (and do not share) the layout map
                LayoutMap<N,E> myLayoutMap = getLayoutMap();
                @SuppressWarnings("unchecked")
                LayoutMap<N,E> otherLayoutMap = (LayoutMap<N,E>) value;
                myLayoutMap.fill(otherLayoutMap);
                value = myLayoutMap;
            }
            this.data.put(key, value);
        }
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
    private Map<String,Object> data;

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
    public static <N extends Node,E extends Edge> GraphInfo<N,E> getInfo(
            Graph<N,E> graph, boolean create) {
        GraphInfo<N,E> result = graph.getInfo();
        if (result == null && create) {
            assert !graph.isFixed();
            result = graph.setInfo(new GraphInfo<N,E>());
        }
        return result;
    }

    /**
     * Convenience method to indicate if a graph has a non-empty set of errors.
     * @see #getErrors()
     */
    public static <N extends Node,E extends Edge> boolean hasErrors(
            Graph<N,E> graph) {
        GraphInfo<N,E> graphInfo = graph.getInfo();
        return graphInfo != null && graphInfo.getErrors() != null
            && !graphInfo.getErrors().isEmpty();
    }

    /**
     * Convenience method to throw an exception if a graph has a non-empty set of errors.
     * @see #hasErrors
     */
    public static <N extends Node,E extends Edge> void throwException(
            Graph<N,E> graph) throws FormatException {
        GraphInfo<N,E> graphInfo = graph.getInfo();
        if (graphInfo != null && graphInfo.getErrors() != null) {
            graphInfo.getErrors().throwException();
        }
    }

    /**
     * Convenience method to retrieve the list of format errors of a graph.
     * @return a list of errors, of {@code null} if the errors were not initialised
     * @see #getErrors()
     */
    public static <N extends Node,E extends Edge> FormatErrorSet getErrors(
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
     * @see #addErrors(FormatErrorSet)
     */
    public static <N extends Node,E extends Edge> void addErrors(
            Graph<N,E> graph, FormatErrorSet errors) {
        getInfo(graph, true).addErrors(errors);
    }

    /**
     * Convenience method to set the list of format errors of a graph.
     * @see #setErrors
     */
    public static <N extends Node,E extends Edge> void setErrors(
            Graph<N,E> graph, FormatErrorSet errors) {
        if (errors != null) {
            assert !graph.isFixed();
            getInfo(graph, true).setErrors(errors);
        }
    }

    /**
     * Convenience method to retrieve the file of a graph.
     */
    public static <N extends Node,E extends Edge> File getFile(Graph<N,E> graph) {
        GraphInfo<N,E> graphInfo = graph.getInfo();
        return new File(graphInfo.getFile());
    }

    /**
     * Convenience method to set the file of a graph.
     */
    public static <N extends Node,E extends Edge> void setFile(
            Graph<N,E> graph, String file) {
        if (file != null) {
            assert !graph.isFixed();
            getInfo(graph, true).setFile(file);
        }
    }

    /**
     * Convenience method to retrieve the layout map from a graph.
     * @return the layout map; non-{@code null} if the graph has an info object
     */
    public static <N extends Node,E extends Edge> LayoutMap<N,E> getLayoutMap(
            Graph<?,?> graph) {
        @SuppressWarnings("unchecked")
        GraphInfo<N,E> graphInfo = (GraphInfo<N,E>) graph.getInfo();
        if (graphInfo == null) {
            return null;
        } else {
            return graphInfo.getLayoutMap();
        }
    }

    /**
     * Convenience method to set the layout map of a graph.
     */
    public static <N extends Node,E extends Edge> void setLayoutMap(
            Graph<N,E> graph, LayoutMap<N,E> layoutMap) {
        getInfo(graph, true).setLayoutMap(layoutMap);
    }

    /**
     * Convenience method to retrieve the properties map from a graph, creating
     * it is necessary.
     * @param graph the graph to retrieve the properties from
     * @param create if <code>true</code>, the properties map (and so the info
     *        object itself) should be created if not yet there. Note that this
     *        is only allowed if the graph is not yet fixed!
     * @return the properties map of <code>graph</code>, or <code>null</code>
     */
    public static <N extends Node,E extends Edge> GraphProperties getProperties(
            Graph<N,E> graph, boolean create) {
        GraphInfo<N,E> graphInfo = getInfo(graph, create);
        if (graphInfo == null) {
            return null;
        } else {
            return graphInfo.getProperties();
        }
    }

    /**
     * Convenience method to clone the properties map from a graph, creating
     * it if necessary.
     * @param graph the graph to retrieve the properties from
     * @return a clone of the properties map of <code>graph</code>, or a fresh properties
     * object if the graph has none 
     */
    public static <N extends Node,E extends Edge> GraphProperties cloneProperties(
            Graph<N,E> graph) {
        GraphProperties result;
        GraphInfo<N,E> graphInfo = getInfo(graph, false);
        if (graphInfo == null) {
            result = new GraphProperties();
        } else {
            result = graphInfo.getProperties().clone();
        }
        return result;
    }

    /**
     * Convenience method to set the graph properties of a graph. Only sets the
     * map if it is not <code>null</code> or empty.
     */
    public static <N extends Node,E extends Edge> void setProperties(
            Graph<N,E> graph, GraphProperties properties) {
        if (properties != null) {
            assert !graph.isFixed();
            getInfo(graph, true).setProperties(properties);
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
    public static <N1 extends Node,E1 extends Edge,N2 extends Node,E2 extends Edge> void transfer(
            Graph<N1,E1> source, Graph<N2,E2> target,
            ElementMap<N1,E1,N2,E2> elementMap) {
        GraphInfo<N1,E1> sourceInfo = source.getInfo();
        if (sourceInfo != null) {
            // copy all the info
            GraphInfo<N2,E2> targetInfo = target.setInfo(sourceInfo.clone());
            if (elementMap != null) {
                // modify the layout map using the element map
                LayoutMap<N1,E1> sourceLayoutMap = sourceInfo.getLayoutMap();
                targetInfo.setLayoutMap(sourceLayoutMap.afterInverse(elementMap));
                FormatErrorSet sourceErrors = sourceInfo.getErrors();
                if (sourceErrors != null) {
                    targetInfo.setErrors(sourceErrors.transfer(
                        elementMap.nodeMap()).transfer(elementMap.edgeMap()));
                }
            }
            // copy rather than clone the graph properties
            GraphProperties properties = sourceInfo.getProperties();
            assert !target.isFixed();
            targetInfo.newProperties(properties);
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
