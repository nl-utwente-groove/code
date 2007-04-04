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
 * $Id: GraphGrammar.java,v 1.7 2007-04-04 07:04:20 rensink Exp $
 */
package groove.trans;

import groove.graph.Graph;
import groove.graph.GraphFactory;

/**
 * Default model of a graph grammar, consisting of a production rule system
 * and a default start graph.
 * Currently the grammar also keeps track of the GTS generated, which is not
 * really natural.
 * @author Arend Rensink
 * @version $Revision: 1.7 $ $Date: 2007-04-04 07:04:20 $
 */
public class GraphGrammar extends RuleSystem {   
    /**
     * Constructs a graph grammar on the basis of a given rule system,
     * start graph and name.
     * @param ruleSystem the underlying rule system
     * @param startGraph the start graph; if <code>null</code>, an empty graph is used
     * @param name the name of this grammar; may be <tt>null</tt> if the grammar is anonymous
     * @require ruleSystem != null
     * @ensure ruleSystem()==ruleSystem, 
     *         gts().nodeSet().size() == 1, gts().edgeSet().size() == 0,
     *         getStartGraph().equals(startGraph),
     *         <tt>getName().equals(name)</tt>
     */
    public GraphGrammar(RuleSystem ruleSystem, Graph startGraph, String name) {
        super(ruleSystem);
        if (startGraph != null) {
        	startGraph.setFixed();
        }
        this.startGraph = startGraph;
        this.name = name;
    }
    
    /**
     * Constructs an anonymoud graph grammar on the basis of a given rule system and
     * start graph.
     * @param ruleSystem the underlying rule system
     * @param startGraph the start graph; if <code>null</code>, an empty graph is used
     * @require ruleSystem != null
     * @ensure ruleSystem()==ruleSystem, 
     *         gts().nodeSet().size() == 1, gts().edgeSet().size() == 0,
     *         getStartGraph().equals(startGraph),
     *         <tt>getName().equals(name)</tt>
     */
    public GraphGrammar(RuleSystem ruleSystem, Graph startGraph) {
        this(ruleSystem, startGraph, null);
    }

    /**
     * Constructs a graph grammar on ths basis of a given production system and name.
     * The initial graph is set to empty (created using {@link GraphFactory}).
     * @param ruleSystem the underlying production system
     * @param name name the name of this grammar; may be <tt>null</tt> if the grammar is anonymous
     * @require <tt>ruleSystem != null</tt>
     * @ensure ruleSystem().equals(ruleSystem), 
     *         gts().nodeSet().size() == 1, gts().edgeSet().size() == 0,
     *         getStartGraph().isEmpty()
     */
    public GraphGrammar(RuleSystem ruleSystem, String name) {
        this(ruleSystem, null, name);
    }
    
    /**
     * Constructs an anonymous graph grammar on ths basis of a given production system.
     * The initial graph is set to empty. 
     * @param ruleSystem the underlying production system
     * @require <tt>ruleSystem != null</tt>
     * @ensure ruleSystem().equals(ruleSystem), 
     *         gts().nodeSet().size() == 1, gts().edgeSet().size() == 0,
     *         getStartGraph().isEmpty()
     */
    public GraphGrammar(RuleSystem ruleSystem) {
        this(ruleSystem, (String) null);
    }

    /**
     * Constructs a named graph grammar with a given rule factory.
     * The rule system and start graph are initially empty.
     */
    public GraphGrammar(RuleFactory ruleFactory, String name) {
        this(new RuleSystem(ruleFactory), name);
    }

    /**
     * Constructs a named graph grammar with empty rule system and 
     * empty start graph.
     */
    public GraphGrammar(String name) {
        this(new RuleSystem(), name);
    }

    /**
     * Constructs an anonymous graph grammar with empty rule system and 
     * empty start graph.
     */
    public GraphGrammar() {
        // empty
    }

    /**
     * Returns the name of this grammar.
     * May be <tt>null</tt> if the grammar is anonymous.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this grammar.
     * @param name the name of the grammar; <tt>null</tt> if the grammar is anonymous
     * @ensure <tt>getName().equals(name)</tt>
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the start graph of this graph grammar.
     * If the graph is not set at the time of invocation, an empty 
     * start graph is created through the graph factory.
     * @return the start graph of this GraphGrammar
     * @ensure <tt>result != null</tt>
     */
    public Graph getStartGraph() {
    	if (startGraph == null) {
    		setStartGraph(GraphFactory.getInstance().newGraph());
    	}
        return startGraph;
    }
    
    /**
     * Changes or sets the start graph of this graph grammar.
     * Note that this invalidates the previous GTS.
     * @param startGraph the new start graph of this graph grammar
     * @require <tt>startGraph != null</tt>
     * @ensure <tt>getStartGraph().equals(startGraph)</tt>
     */
    public void setStartGraph(Graph startGraph) {
        this.startGraph = startGraph;
        this.startGraph.setFixed();
    }

    /** Tests for equality of the rule system and the start graph. */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof GraphGrammar)
            && getStartGraph().equals(((GraphGrammar) obj).getStartGraph())
            && super.equals(obj);
    }

    /** Combines the hash codes of the rule system and the start graph. */
    @Override
    public int hashCode() {
        return (getStartGraph().hashCode() << 8) ^ super.hashCode();
    }

    @Override
    public String toString() {
        return "Rule system:\n    " + super.toString()
            + "\nStart graph:\n    "   + getStartGraph().toString();
    }

    public RuleSystem getRuleSystem() {
		return this;
	}

	/**
     * The start Graph of this graph grammar.
     * @invariant <tt>startGraph != null</tt>
     */
    protected Graph startGraph;
    /**
     * The name of this grammar;
     * <tt>null</tt> if the grammar is anonymous.
     */
    protected String name;
}