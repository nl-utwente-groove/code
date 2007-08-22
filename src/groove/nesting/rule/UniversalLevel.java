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
 * $Id: UniversalLevel.java,v 1.1 2007-08-22 09:19:47 kastenberg Exp $
 */
package groove.nesting.rule;

import groove.graph.Morphism;
import groove.rel.VarGraph;
import groove.trans.DefaultGraphCondition;
import groove.trans.NameLabel;
import groove.trans.SystemProperties;

/**
 *
 * @author J.H. Kuperus
 * @version 0.1 $Revision: 1.1 $ $Date: 2007-08-22 09:19:47 $
 */
public class UniversalLevel extends DefaultGraphCondition {

	/**
	 * Constructs a new UniversalLevel
	 * @param pattern
	 * @param name
	 * @param properties
	 */
	public UniversalLevel(Morphism pattern, NameLabel name, SystemProperties properties) {
		super(pattern, name, properties);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructs a new UniversalLevel
	 * @param pattern
	 * @param properties
	 */
	public UniversalLevel(Morphism pattern, SystemProperties properties) {
		super(pattern, properties);
		// TODO Auto-generated constructor stub
	}

}
