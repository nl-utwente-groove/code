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
 * $Id: Xml.java,v 1.5 2007-04-24 10:06:47 rensink Exp $
 */
package groove.io;

import groove.graph.Graph;
import groove.util.FormatException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Interface for the conversion of graphs to and from XML documents.
 * To be implemented for particular XML formats.
 * 
 * @author Arend Rensink
 * @version $Revision: 1.5 $
 */
public interface Xml<G extends Graph> {
	/**
	 * Writes a graph to a file, in XML format.
	 * @param graph the graph to be marshalled
	 * @param file the output file
     * @throws FormatException if an error occurred during the conversion
     * @throws IOException if an error occurred during file output
	 */
	public void marshalGraph(G graph, File file) throws FormatException, IOException;
	
    /**
     * Converts an XML formatted file into a graph, and returns the graph.
     * Convenience method for <code>unmarshal(file, null)</code>.
     * @param file the file to be read from
     * @return the unmarshalled graph
     * @throws FormatException if an error occurred during the conversion
     * @throws IOException if an error occurred during file input
     */
    public G unmarshalGraph(File file) throws FormatException, IOException ;
    
	/**
	 * Sets the names of properties possibly stored for the graph.
	 */
	public void setPropertyKeys(Collection<String> propertyNames);
}