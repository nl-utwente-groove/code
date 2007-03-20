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
 * $Id: Xml.java,v 1.1.1.2 2007-03-20 10:42:51 kastenberg Exp $
 */
package groove.io;

import groove.graph.Graph;
import groove.graph.GraphShape;
import groove.graph.Node;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Interface for the conversion of graphs to and from XML documents.
 * To be implemented for particular XML formats.
 * 
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public interface Xml {
	/**
	 * Writes a graph to a file, in XML format.
	 * @param graph the graph to be marshalled
	 * @param file the output file
     * @throws XmlException if an error occurred during the conversion
     * @throws IOException if an error occurred during file output
	 */
	public void marshal(GraphShape graph, File file) throws XmlException, IOException;
	
	/**
	 * Reads a graph from an XML formatted file and returns it.
     * Also constructs a map from node identities in the XML file to graph nodes.
     * This can be used to connect with layout information.
	 * @param file the file to be read from
     * @param elementMap if not <tt>null</tt>, will be cleared and set to
     * a {@link String} to {@link groove.graph.Node} map from node identities in the XML file to graph nodes
     * @return the unmarshalled graph
	 * @throws XmlException if an error occurred during the conversion
     * @throws IOException if an error occurred during file input
	 */
	public Graph unmarshal(File file, Map<String, Node> elementMap) throws XmlException, IOException ;
    
    /**
     * Converts an XML formatted file into a graph, and returns the graph.
     * Convenience method for <code>unmarshal(file, null)</code>.
     * @param file the file to be read from
     * @return the unmarshalled graph
     * @throws XmlException if an error occurred during the conversion
     * @throws IOException if an error occurred during file input
     * @see #unmarshal(File, Map)
     */
    public Graph unmarshal(File file) throws XmlException, IOException ;
}