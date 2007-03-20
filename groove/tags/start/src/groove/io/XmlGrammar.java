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
 * $Id: XmlGrammar.java,v 1.1.1.2 2007-03-20 10:42:51 kastenberg Exp $
 */
package groove.io;

import groove.trans.GraphGrammar;
import groove.trans.RuleFactory;

import java.io.File;
import java.io.IOException;

/**
 * Interface for the conversion of graph grammars to and from 
 * (sets of) XML documents.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public interface XmlGrammar {
    /**
     * The default name of the start state of a grammar.
     */
    static public final String DEFAULT_START_STATE_NAME = "start";

    /**
     * Returns the extension filter associated with this grammar loader.
     */
    public ExtensionFilter getExtensionFilter();
    
	/**
	 * Writes a graph grammar to a file or set of files, in XML format.
     * The XML format may either support multiple rules in a single document,
     * or each of the rules is stored in a separate file. 
	 * @param gg the graph grammar to be marshalled
	 * @param file the output file (if the XML format allows storing the grammar
     * in a single file) or directory (if the grammar is stored as a set of files)
     * @throws XmlException if an error occurs in the conversion
     * @throws IOException if an error occurs during file output
	 */
	public void marshal(GraphGrammar gg, File file) throws IOException;
	
    /**
     * Converts an XML formatted file or set of files into a graph grammar, 
     * and returns the graph grammar.
     * Convenience method for <code>unmarshal(file, null)</code>.
     * @see #unmarshal(File, String)
     */
    public GraphGrammar unmarshal(File file) throws XmlException, IOException;

    /**
     * Converts an XML formatted file or set of files into a graph grammar, 
     * and returns the graph grammar.
     * The start state is given explicitly by a string, which either 
     * stands for the name of the start state within the grammar, or for
     * the name of a separate file containing the start state. If <code>null</code>,
     * {@link #DEFAULT_START_STATE_NAME} is tried; if that does not exist,
     * the start state is not initialized.
     * @param file the file to be read from (if the XML format allows storing the grammar
     * in a single file) or directory (if the grammar is stored as a set of files)
     * @param startStateName the file where the start state is to be found; if <tt>null</tt>,
     * the default name {@link #DEFAULT_START_STATE_NAME} is tried.
     * @return the unmarshalled graph grammar
     * @throws XmlException if an error occurs in the conversion
     * @throws IOException if an error occurs during file input
     */
    public GraphGrammar unmarshal(File file, String startStateName) throws XmlException, IOException;

    /**
     * Returns the {@link groove.trans.RuleFactory} needed for instantiating classes for performing transformations.
     * @return the current <code>ruleFactory</code>
     */
    public RuleFactory getRuleFactory();
}
