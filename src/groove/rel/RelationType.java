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
package groove.rel;

/**
 * Class embodying the types of relations supported in a {@link RelationEdge}.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum RelationType {
	/** {@link RelationType} value standing for a mapping of label variables to string values */
	VALUATION("match");
	
	/**
	 * Private constructor to create a type with a given text.
	 */
	private RelationType(String text) {
		this.text = text;
	}
	
	/**
	 * Returns a string describing this relation type.
	 */
	public String getText() {
		return text;
	}
	
	/** Description of the {@link RelationType} value. */
	private final String text;
}