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
 * $Id: GraphInfo.java,v 1.2 2007-03-30 15:50:24 rensink Exp $
 */
package groove.graph;

import java.util.HashMap;
import java.util.Map;

import groove.gui.layout.LayoutMap;

/**
 * A class that provides the keys needed for storing and retrieving data
 * needed for specific features.
 * @author Harmen Kastenberg
 * @version $Revision: 1.2 $ $Date: 2007-03-30 15:50:24 $
 */
public class GraphInfo {
	/**
	 * Key for layout-info.
	 */
	public static final String LAYOUT = "layout";
	
	/**
	 * Convenience method to test if a graph contains layout information.
	 */
	public static boolean hasLayoutMap(GraphShape graph) {
        GraphInfo graphInfo = graph.getInfo();
        if (graphInfo == null) {
            return false;
        } else {
            return graph.getInfo().hasLayoutMap();
        }
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
            GraphInfo graphInfo = graph.getInfo();
            if (graphInfo == null) {
                graph.setInfo(graphInfo = new GraphInfo());
            }
			graph.getInfo().setLayoutMap(layoutMap);
		}
	}
	
	/**
	 * Transfers all available graph information from one graph to another,
	 * modulo a given element map
	 * @param source the graph to transfer the information from
	 * @param target the graph to transfer the information to
	 * @param elementMap map from the source elements to the target elements
	 */
	public static void transfer(GraphShape source, GraphShape target, NodeEdgeMap elementMap) {
		GraphInfo sourceInfo = source.getInfo();
		if (sourceInfo != null) {
			target.setInfo(sourceInfo);
			// modify the layout map using the element map
			LayoutMap<Node,Edge> layoutMap = sourceInfo.getLayoutMap();
			target.getInfo().setLayoutMap(layoutMap.afterInverse(elementMap));
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
     * Tests if this info object contains a layout map (with key {@link #LAYOUT}). 
     * @see #getLayoutMap()
     */
    public boolean hasLayoutMap() {
        return data.containsKey(LAYOUT);
    }
    
    /** 
     * Returns the layout map (with key {@link #LAYOUT}) in this info object, if any.
     * @see #setLayoutMap(LayoutMap) 
     */
    public LayoutMap<Node,Edge> getLayoutMap() {
        return (LayoutMap<Node,Edge>) data.get(LAYOUT);
    }
    
    /**
     * Sets the layout map (key {@link #LAYOUT}) in this info object to a certain value.
     * @see #getLayoutMap()
     */
    public void setLayoutMap(LayoutMap<Node,Edge> layoutMap) {
        data.put(LAYOUT, layoutMap);
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
    protected final Map<String,Object> getData() {
        return data;
    }
    
    /**
     * Map for the internally stored data.
     */
    private final Map<String,Object> data;
}
