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
 * $Id$
 */
package groove.control.parse;

import groove.control.ControlAutomaton;
import groove.control.ControlState;
import groove.control.ControlTransition;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.trans.RuleSystem;
import groove.view.FormatException;

/**
 * Class that provides operations to compose control automata.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ControlBuilder {
    private ControlBuilder() {
        // empty
    }

    /**
     * Constructs a control automaton consisting of a single rule invocation.
     */
    public ControlAutomaton createRule(String rulename) {
        ControlAutomaton result = new ControlAutomaton();
        ControlState start = new ControlState(result);
        ControlState end = new ControlState(result);
        end.setSuccess();
        ControlTransition trans = new ControlTransition(start, end);
        result.addState(start);
        result.addState(end);
        result.addTransition(trans);
        result.setStart(start);
        return result;
    }

    /**
     * Constructs a control automaton for the <b>any</b> keyword.
     */
    public ControlAutomaton createAny() {
        return createRule(_ANY_);
    }

    /**
     * Constructs a control automaton for the <b>other</b> keyword.
     */
    public ControlAutomaton createOther() {
        return createRule(_OTHER_);
    }

    /** 
     * Constructs a control automaton for the <b>if</b> construct.
     * @param condition automaton representing the condition
     * @param thenPart non-{@code null} automaton representing the <b>then</b>-part
     * @param elsePart possibly {@code null} automaton representing the <b>else</b>-part
     */
    public ControlAutomaton createIf(ControlAutomaton condition,
            ControlAutomaton thenPart, ControlAutomaton elsePart) {
        ControlAutomaton result = new ControlAutomaton();
        NodeEdgeMap conditionMap = copy(condition, result);
        return result;
    }

    /** 
     * Transforms a given automaton to final representation
     * by removing non-determinism and filling in the <b>any</b> and
     * <b>other</b> keywords with respect to a given rule system.
     * Also checks if all rules actually exist, and have the required
     * parameters. 
     * @param auto the automaton to be finalised
     * @param rules the rule system with respect to which the automaton is
     * finalised
     * @throws FormatException if there is an error in the automaton
     * with respect to the given rule system
     */
    public void finalize(ControlAutomaton auto, RuleSystem rules)
        throws FormatException {

    }

    private NodeEdgeMap copy(ControlAutomaton from, ControlAutomaton to) {
        NodeEdgeMap result = new NodeEdgeHashMap();
        for (ControlState state : from.nodeSet()) {
            ControlState image = new ControlState(to);
            image.setInitializedVariables(state.getInitializedVariables());
            to.addState(image);
            result.putNode(state, image);
        }
        for (ControlTransition trans : from.edgeSet()) {
            ControlTransition image =
                new ControlTransition(trans.source(), trans.target());
            image.setRule(trans.getRule());
            image.setInputParameters(trans.getInputParameters());
            image.setOutputParameters(trans.getOutputParameters());
            to.addTransition(image);
            result.putEdge(trans, image);
        }
        return result;
    }

    /** Returns the singleton instance of this class. */
    static public ControlBuilder getInstance() {
        return instance;
    }

    /** label used to identify delta in init result. */
    private static final String _DELTA_ = "/%/__DELTA__/%/";

    /** label used to identify other keyword in transition label. */
    private static final String _OTHER_ = "/%/__OTHER__/%/";

    /** label used to identify any keyword in transition label. */
    private static final String _ANY_ = "/%/__ANY__/%/";

    /** The singleton instance of this class. */
    static private final ControlBuilder instance = new ControlBuilder();
}
