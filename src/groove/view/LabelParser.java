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
 * $Id: LabelParser.java,v 1.2 2007-08-26 07:24:10 rensink Exp $
 */
package groove.view;

import groove.graph.Label;

/** Interface for parsers from strings to labels. */
public interface LabelParser {
	/** 
	 * Method turning a string into a label.
	 * @param text the string to be parsed into a label
	 * @return the label constructed from <code>text</code>
	 * @throws FormatException if <code>text</code> is not correctly formatted
	 * according to this parser. 
	 */
	Label parse(String text) throws FormatException;
    
    /** 
     * Method turning a label into a string from which the label can be resonstructed by this parser,
     * or <code>null</code> if no such string can be found.
     */
    String unparse(Label label);
}