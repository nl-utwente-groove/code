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
 * $Id: GraphGrammar.java,v 1.19 2007-08-31 10:23:06 rensink Exp $
 */
package groove.trans;

import groove.control.ControlAutomaton;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.algebra.ValueNode;
import groove.view.FormatException;

import java.util.ArrayList;
import java.util.List;

/**
 * Default model of a graph grammar, consisting of a production rule system
 * and a default start graph.
 * Currently the grammar also keeps track of the GTS generated, which is not
 * really natural.
 * @author Arend Rensink
 * @version $Revision: 1.19 $ $Date: 2007-08-31 10:23:06 $
 */
public class GraphGrammar extends RuleSystem {   
    /**
     * Constructs an anonymous graph grammar on the basis of a given rule system and
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
        super(ruleSystem);
        this.startGraph = startGraph;
    }

    /**
     * Constructs a graph grammar on ths basis of a given production system and name.
     * The initial graph is set to empty (created using {@link GraphFactory}).
     * @param ruleSystem the underlying production system
     * @require <tt>ruleSystem != null</tt>
     * @ensure ruleSystem().equals(ruleSystem), 
     *         gts().nodeSet().size() == 1, gts().edgeSet().size() == 0,
     *         getStartGraph().isEmpty()
     */
    public GraphGrammar(RuleSystem ruleSystem) {
        this(ruleSystem, null);
    }
//    
//    /**
//     * Constructs an anonymous graph grammar on ths basis of a given production system.
//     * The initial graph is set to empty. 
//     * @param ruleSystem the underlying production system
//     * @require <tt>ruleSystem != null</tt>
//     * @ensure ruleSystem().equals(ruleSystem), 
//     *         gts().nodeSet().size() == 1, gts().edgeSet().size() == 0,
//     *         getStartGraph().isEmpty()
//     */
//    public GraphGrammar(RuleSystem ruleSystem) {
//        this(ruleSystem, (String) null);
//    }
//
//    /**
//     * Constructs a named graph grammar with a given rule factory.
//     * The rule system and start graph are initially empty.
//     */
//    public GraphGrammar(String name) {
//        this(new RuleSystem(), name);
//    }

    /**
     * Constructs a named graph grammar with empty rule system and 
     * empty start graph.
     */
    public GraphGrammar(String name) {
        this(new RuleSystem(name));
    }
//
//    /**
//     * Constructs an anonymous graph grammar with empty rule system and 
//     * empty start graph.
//     */
//    public GraphGrammar() {
//        this((String) null);
//    }
//
//    /**
//     * Returns the name of this grammar.
//     * May be <tt>null</tt> if the grammar is anonymous.
//     */
//    public String getName() {
//        return name;
//    }
//
//    /**
//     * Sets the name of this grammar.
//     * @param name the name of the grammar; <tt>null</tt> if the grammar is anonymous
//     * @ensure <tt>getName().equals(name)</tt>
//     */
//    public void setName(String name) {
//        this.name = name;
//    }

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
	 * Returns the rule system that is part of this graph grammar.
	 */
	public RuleSystem getRuleSystem() {
		return this;
	}

	/**
     * Changes or sets the start graph of this graph grammar.
     * This is only allowed if the grammar is not yet fixed, as indicated by {@link #isFixed()}.
     * @param startGraph the new start graph of this graph grammar
     * @throws IllegalStateException if the grammar is already fixed
     * @see #isFixed()
     */
    public void setStartGraph(Graph startGraph) throws IllegalStateException {
    	testFixed(false);
        this.startGraph = startGraph;
    }

    /** 
     * Sets a control automaton for this grammar. 
     * This is only allowed if the grammar is not yet fixed, as indicated by {@link #isFixed()}.
     * @throws IllegalStateException if the grammar is already fixed
     * @see #isFixed()
     */
    public void setControl(ControlAutomaton control) {
        testFixed(false);
        this.control = control;
    }
    
    /** Returns the control automaton of this grammar, or <code>null</code> if there is none. */
    public ControlAutomaton getControl() {
        return this.control;
    }
    
    /** Fixes the start graph, in addition to calling the <code>super</code> method. */
	@Override
	public void setFixed() throws FormatException {
		getStartGraph().setFixed();
		super.setFixed();
	}
	
	/** Combines the consistency errors in the rules and start graph. */
	@Override
	public void testConsistent() throws FormatException {
		List<String> errors = new ArrayList<String>();
		// collect the exception o fthe super test, if any
		try {
			super.testConsistent();
		} catch (FormatException exc) {
			errors.addAll(exc.getErrors());
		}
		// chain the consistency problems in the start graph
		if (!getProperties().isAttributed() && ValueNode.hasValueNodes(getStartGraph())) {
			String attributeKey = SystemProperties.ATTRIBUTES_KEY;
			String attributeProperty = getProperties().getProperty(attributeKey);
			if (attributeProperty == null) {
				errors.add(String.format("Start graph uses attributes, but \"%s\" not declared", attributeKey));
			} else {
				errors.add(String.format("Start graph uses attributes, violating \"%s=%s\"", attributeKey, attributeProperty));
			}
		}
		if (!errors.isEmpty()) {
			throw new FormatException(errors);
		}
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

    /**
     * The start graph of this graph grammar.
     */
    private Graph startGraph;
    /** The control automaton of this grammar; <code>null</code> if there is none. */
    private ControlAutomaton control;
}