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
 * $Id: ExistentialLevel.java,v 1.1 2007-08-22 09:19:47 kastenberg Exp $
 */
package groove.nesting.rule;

import groove.graph.Morphism;
import groove.trans.DefaultGraphCondition;
import groove.trans.NameLabel;
import groove.trans.Rule;
import groove.trans.SystemProperties;

/**
 *
 * @author kramor
 * @version 0.1 $Revision: 1.1 $ $Date: 2007-08-22 09:19:47 $
 */
public class ExistentialLevel extends DefaultGraphCondition {

	private NestedRule rule;
	
	/**
	 * Constructs a new ExistentialLevel
	 * @param pattern
	 * @param name
	 * @param properties
	 */
	public ExistentialLevel(NestedRule rule, Morphism pattern, NameLabel name, SystemProperties properties) {
		super(pattern, name, properties);
		this.rule = rule;
	}

	/**
	 * Constructs a new ExistentialLevel
	 * @param pattern
	 * @param properties
	 */
	public ExistentialLevel(NestedRule rule, Morphism pattern, SystemProperties properties) {
		super(pattern, properties);
		this.rule = rule;
	}

	public NestedRule getRule() {
		return this.rule;
	}
	
}
