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
 * $Id: AbstractXml.java,v 1.10 2007-05-14 18:52:03 rensink Exp $
 */
package groove.io;

import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.Node;
import groove.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Convenience class that brings down <tt>Xml</tt>'s methods to just two 
 * abstract methods: <tt>marshal(Graph)</tt> and <tt>unmarshal(Document,Graph)</tt>.
 * 
 * @author Arend Rensink
 * @version $Revision: 1.10 $
 */
public abstract class AbstractXml implements Xml<Graph> {
	AbstractXml(GraphFactory graphFactory) {
		this.graphFactory = graphFactory;
	}
	
    public Graph unmarshalGraph(File file) throws IOException {
        return unmarshalGraphMap(file).first();
    }
	
	/**
	 * Reads a graph from an XML formatted file and returns it.
     * Also constructs a map from node identities in the XML file to graph nodes.
     * This can be used to connect with layout information.
	 * @param file the file to be read from
     * @return a pair consisting of the unmarshalled graph and a string-to-node map
     * from node identities in the XML file to nodes in the unmarshalled graph
	 * @throws IOException if an error occurred during file input
	 */
	abstract protected Pair<Graph,Map<String,Node>> unmarshalGraphMap(File file) throws IOException ;
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

    /** Deletes the graph file, as well as all variants with the same name but different priorities. */
    public final void deleteGraph(File file) {
        deleteFile(file);
        deleteVariants(file);
    }
    
    /** Deletes a given file, storing a graph, and possible auxiliary files. */
    protected void deleteFile(File file) {
        file.delete();
    }

    /** Deletes all variants of a given file with the same name but different (old-style) priorities. */
    protected void deleteVariants(File file) {
        File parentFile = file.getParentFile();
        if (parentFile != null) {
            // build the actual name with extension
            PriorityFileName priotityName = new PriorityFileName(file);
            String fileNameWithoutPriority = priotityName.getActualName()+priotityName.getExtension();
            // look for all files ending with this name
            ExtensionFilter filter = new ExtensionFilter(PriorityFileName.SEPARATOR+fileNameWithoutPriority);
            for (File candidate : parentFile.listFiles(filter)) {
                // delete the file if it has a valid priority part.
                String candidateName = candidate.getName();
                if (!candidate.isDirectory() && !candidateName.equals(file.getName())) {
                    try {
                        Integer.parseInt(filter.stripExtension(candidateName));
                        deleteFile(candidate);
                    } catch (NumberFormatException e) {
                        // it was not a priority, so leave be
                    }
                }
            }
        }
    }

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
