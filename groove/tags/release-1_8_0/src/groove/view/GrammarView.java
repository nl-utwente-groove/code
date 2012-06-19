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
 * $Id: GrammarView.java,v 1.4 2007-08-26 07:24:10 rensink Exp $
 */
package groove.view;

import groove.graph.Graph;
import groove.trans.GraphGrammar;
import groove.trans.RuleNameLabel;
import groove.trans.SystemProperties;

import java.util.Map;
import java.util.Set;

/**
 * Interface encapsulating a representation of a rule system that is essentially
 * a set of rule views, available as a map from names to views.
 * The view as a whole has a name and a set of properties.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface GrammarView<GV extends View<Graph>, RV extends RuleView> extends View<GraphGrammar> {
	/** Returns the name of the rule system. */
	public String getName();
	
	/** Returns the (fixed) properties of the rule system. */
	public SystemProperties getProperties();
	
	/** 
	 * Adds a rule view, and returns the possible view previously stored under that name.
	 * @param rule the rule view to be added; non-<code>null</code> 
	 * @return a rule view previously stored under the name of <code>rule</code>
	 */
	public RV addRule(RV rule) throws FormatException;
	
	/** Returns an unmodifiable map from rule names to rule views. */
	public Map<RuleNameLabel, RV> getRuleMap();

    /**
     * Returns the rule view stored for a given rule name.
     */
    public RV getRule(RuleNameLabel name);
    
    /**
     * Returns an unmodifiable map from priorities to non-empty sets of 
     * rules in this grammar with that priority.
     */
    public Map<Integer, Set<RV>> getPriorityMap();
    
	/** Returns the start graph of this grammar view. */
	public GV getStartGraph();
	
	/** 
	 * Lazily converts the view to a fixed rule system.
	 * This may throw an exception if the view has errors.
	 * @return a rule system based on the name, properties and rules stored as views
	 * @throws FormatException
	 */
	public GraphGrammar toGrammar() throws FormatException;
}