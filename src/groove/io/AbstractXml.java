// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: AbstractXml.java,v 1.8 2007-05-06 10:47:53 rensink Exp $
 */
package groove.io;

import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.Node;
import groove.util.Pair;
import groove.view.FormatException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Convenience class that brings down <tt>Xml</tt>'s methods to just two 
 * abstract methods: <tt>marshal(Graph)</tt> and <tt>unmarshal(Document,Graph)</tt>.
 * 
 * @author Arend Rensink
 * @version $Revision: 1.8 $
 */
public abstract class AbstractXml implements Xml<Graph> {
	AbstractXml(GraphFactory graphFactory) {
		this.graphFactory = graphFactory;
	}
	
    public Graph unmarshalGraph(File file) throws FormatException, IOException {
        return unmarshalGraphMap(file).first();
    }
	
	/**
	 * Reads a graph from an XML formatted file and returns it.
     * Also constructs a map from node identities in the XML file to graph nodes.
     * This can be used to connect with layout information.
	 * @param file the file to be read from
     * @return a pair consisting of the unmarshalled graph and a string-to-node map
     * from node identities in the XML file to nodes in the unmarshalled graph
	 * @throws FormatException if an error occurred during the conversion
     * @throws IOException if an error occurred during file input
	 */
	abstract protected Pair<Graph,Map<String,Node>> unmarshalGraphMap(File file) throws FormatException, IOException ;
//
//    /**
//	 * Checks if a given property key is allowed.
//	 * This is the case if either the property keys have not been set,
//	 * or <code>key</code> is within the property keys.
//	 * @see #getPropertyKeys() 
//	 */
//	public final boolean isKnownPropertyKey(String key) {
//		return propertyKeys == null || getPropertyKeys().contains(key);
//	}
//
//    /**
//	 * If the property keys have not been set, takes just the {@link #DEFAULT_PROPERTY_KEYS}.
//	 */
//	public final Set<String> getPropertyKeys() {
//		if (propertyKeys == null) {
//			propertyKeys = new HashSet<String>(DEFAULT_PROPERTY_KEYS);
//		}
//		return this.propertyKeys;
//	}
//
//	/**
//	 * Sets the property keys, after adding the {@link #DEFAULT_PROPERTY_KEYS}.
//	 */
//	public final void setPropertyKeys(Collection<String> propertyKeys) {
//		this.propertyKeys = new HashSet<String>(propertyKeys);
//		this.propertyKeys.addAll(DEFAULT_PROPERTY_KEYS);
//	}

    /**
     * Changes the graph factory used for unmarshalling.
     */
    protected final void setGraphFactory(GraphFactory factory) {
        graphFactory = factory;
    }

    /**
     * Returns the graph factory used for unmarshalling.
     */
    protected final GraphFactory getGraphFactory() {
        return graphFactory;
    }

    /** The graph factory for this marshaller. */
    private GraphFactory graphFactory;
//
//	/** The current set of graph property names recognised by this marshallar. */
//	private Set<String> propertyKeys;
//    /** 
//     * Set of default property names (which will certainly be included in the allowed
//     * graph property names).
//     */
//    static public final List<String> DEFAULT_PROPERTY_KEYS = GraphProperties.DEFAULT_KEYS;
}
