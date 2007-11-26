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
 * $Id: AntlrParser.java,v 1.2 2007-11-26 08:58:36 fladder Exp $
 */
package groove.control.parse;

/**
 * Static function to parse the grammar files. 
 * @author Arend Rensink
 * @version $Revision $
 */
public class AntlrParser {

	/**
	 * Parse the grammar files. 
	 * @param args
	 */
	public static void main(String[] args) {
		antlr.Tool.main(new String[]{"gcl.g"});
		antlr.Tool.main(new String[]{"builder.g"});
	}

}
