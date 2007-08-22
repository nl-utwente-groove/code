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
 * $Id: NestingContentParser.java,v 1.1 2007-08-22 09:19:49 kastenberg Exp $
 */
package groove.nesting;

import groove.view.FormatException;
import groove.view.aspect.ContentParser;

/**
 *
 * @author kramor
 * @version 0.1 $Revision: 1.1 $ $Date: 2007-08-22 09:19:49 $
 */
public class NestingContentParser implements ContentParser<String> {

	/* (non-Javadoc)
	 * @see groove.graph.aspect.ContentParser#toContent(java.lang.String)
	 * @ensure result.length > 0
	 */
	public String toContent(String value) throws FormatException {
		return value;
	}

	/* (non-Javadoc)
	 * @see groove.graph.aspect.ContentParser#toString(java.lang.Object)
	 * @require content.length > 0
	 */
	public String toString(String content) {
		return content;
	}
	
}
