/* GROOVE: GRaphs for Object Oriented VErification
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
 * $Id: GraphInfo.java,v 1.7 2007-05-09 22:53:36 rensink Exp $
 */
package groove.graph;

import groove.gui.layout.LayoutMap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that provides the keys needed for storing and retrieving data
 * needed for specific features.
 * @author Harmen Kastenberg
 * @version $Revision: 1.7 $ $Date: 2007-05-09 22:53:36 $
 */
public class GraphInfo {
	/** 
	 * Convenience method to retrieve a {@link GraphInfo} object form
	 * a given graph, creating it if necessary. 
	 * @param graph the graph for which the info object is to be (created and) retrieved
	 * @param create if <code>true</code>, the info object 
	 * should be created if not yet there
	 * @return a non-<code>null</code> value which equals (afterwards) <code>graph.getInfo()</code>
	 */
	public static GraphInfo getInfo(GraphShape graph, boolean create) {
		GraphInfo result = graph.getInfo();
		if (result == null && create) {
			result = graph.setInfo(new GraphInfo());
		}
		return result;
	}
	
	/**
	 * Convenience method to test if a graph contains layout information.
	 */
	public static boolean hasLayoutMap(GraphShape graph) {
        GraphInfo graphInfo = graph.getInfo();
        return graphInfo != null && graphInfo.hasLayoutMap();
	}

	/**
	 * Convenience method to retrieve the layout map from a graph and cast it to
	 * the correct type.
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
	public static void setLayoutMap(GraphShape graph, LayoutMap<Node,Edge> layoutMap) {
		if (layoutMap != null) {
            getInfo(graph, true).setLayoutMap(layoutMap);
		}
	}

	/**
	 * Convenience method to retrieve the file of a graph.
	 */
	public static File getFile(GraphShape graph) {
        GraphInfo graphInfo = graph.getInfo();
        if (graphInfo == null) {
            return null; 
        } else {
            return graphInfo.getFile();
        }
	}
	
	/**
	 * Convenience method to set the file of a graph.
	 */
	public static void setFile(GraphShape graph, File file) {
		if (file != null) {
            getInfo(graph, true).setFile(file);
		}
	}

	/**
	 * Convenience method to retrieve the name of a graph.
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
	 */
	public static void setName(GraphShape graph, String name) {
		GraphInfo info = getInfo(graph, name != null);
		if (info != null) {
            info.setName(name);
		}
	}
	
	/**
	 * Convenience method to retrieve the properties map from a graph,
	 * creating it is necessary if any.
	 * @param graph the graph to retrieve the properties from
	 * @param create if <code>true</code>, the properties map (and so the info object itself) 
	 * should be created if not yet there
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
	 * Convenience method to set the graph properties of a graph.
	 * Only sets the map if it is not <code>null</code> or empty.
	 */
	public static void setProperties(GraphShape graph, GraphProperties properties) {
		if (properties != null) {
            getInfo(graph, true).setProperties(properties);
		}
	}
	
	/**
	 * Transfers all available graph information from one graph to another,
	 * modulo a given element map. The element map may be null if the node
	 * and edge identities of source and target coincide.
	 * @param source the graph to transfer the information from
	 * @param target the graph to transfer the information to
	 * @param elementMap map from the source elements to the target elements
	 */
	public static void transfer(GraphShape source, GraphShape target, NodeEdgeMap elementMap) {
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
				target.getInfo().setProperties(new GraphProperties(properties));
			}
		}
	}

    /** Constructs a copy of an existing information object. */
    public GraphInfo(GraphInfo info) {
        this.data = new HashMap<String,Object>(info.getData());
    }

    /** Constructs an empty information object. */
    public GraphInfo() {
        this.data = new HashMap<String,Object>();
    }
    
    /** 
     * Tests if this info object contains a layout map (with key {@link #LAYOUT_KEY}). 
     * @see #getLayoutMap()
     */
    public boolean hasLayoutMap() {
        return data.containsKey(LAYOUT_KEY);
    }
    
    /** 
     * Returns the layout map (with key {@link #LAYOUT_KEY}) in this info object, if any.
     * @see #setLayoutMap(LayoutMap) 
     */
    public LayoutMap<Node,Edge> getLayoutMap() {
        return (LayoutMap<Node,Edge>) data.get(LAYOUT_KEY);
    }
    
    /**
     * Sets the layout map (key {@link #LAYOUT_KEY}) in this info object to a certain value.
     * @see #getLayoutMap()
     */
    public void setLayoutMap(LayoutMap<Node,Edge> layoutMap) {
        data.put(LAYOUT_KEY, layoutMap);
    }

    /** 
     * Returns the graph properties map associated with the graph (key {@link #PROPERTIES_KEY}).
     * The parameter indicates if the map should be created in case it is not yet there.
     * @param create if <code>true</code> and this data object does not contain properties,
     * create and return an empty properties object
     * @return a property map, or <code>null</code> 
     * @see #setProperties(GraphProperties)
     */
    public GraphProperties getProperties(boolean create) {
    	GraphProperties result = (GraphProperties) data.get(PROPERTIES_KEY);
    	if (create && result == null) {
    		result = new GraphProperties();
    		data.put(PROPERTIES_KEY, result);
    	}
    	return result;
    }
    
    /**
     * Copies the properties in a given map
     * to this info object (key {@link #PROPERTIES_KEY}). 
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
     * Returns the file associated with the graph, if any.
     * @return a file stored in the info object, or <code>null</code> 
     * @see #setFile(File)
     */
    public File getFile() {
    	return (File) data.get(FILE_KEY);
    }
    
    /**
     * 
     * Sets the file (key {@link #FILE_KEY}) in this info object to a certain value.
     * If the value is <code>null</code>, the key is removed altogether.
     * @see #getFile()
     */
    public void setFile(File file) {
    	if (file == null) {
    		data.remove(FILE_KEY);
    	} else {
    		data.put(FILE_KEY, file);
    	}
    }

    /** 
     * Returns the name associated with the graph, if any.
     * @return a name stored in the info object, or <code>null</code> 
     * @see #setName(String)
     */
    public String getName() {
    	return (String) data.get(NAME_KEY);
    }
    
    /**
     * 
     * Sets the name (key {@link #NAME_KEY}) in this info object to a certain value.
     * If the value is <code>null</code>, the key is removed altogether.
     * @see #getName()
     */
    public void setName(String name) {
    	if (name == null) {
    		data.remove(NAME_KEY);
    	} else {
    		data.put(NAME_KEY, name);
    	}
    }
    
    /**
     * Copies another graph info object into this one, overwriting all existing keys but preserving
     * those that are not overwritten.
     */
    public void load(GraphInfo other) {
        data.putAll(other.getData());
    }
    
    @Override
	public String toString() {
		return "Graph information: "+data;
	}

	/** Returns the internally stored data. */
    public final Map<String,Object> getData() {
        return data;
    }
    
    /**
     * Map for the internally stored data.
     */
    private final Map<String,Object> data;
	/**
	 * Key for storage file.
	 */
	public static final String NAME_KEY = "name";
	/**
	 * Key for storage file.
	 */
	public static final String FILE_KEY = "file";
	/**
	 * Key for layout-info.
	 */
	public static final String LAYOUT_KEY = "layout";
	/**
	 * Key for graph properties.
	 */
	public static final String PROPERTIES_KEY = "properties";
}
