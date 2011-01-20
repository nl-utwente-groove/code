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
package groove.verify.ltl2ba;

import groove.verify.BuchiLabel;
import groove.verify.BuchiLocation;
import groove.verify.BuchiTransition;
import groove.verify.DefaultBuchiLocation;
import groove.verify.ModelChecking;
import groove.view.FormatException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import rwth.i2.ltl2ba4j.LTL2BA4J;
import rwth.i2.ltl2ba4j.model.IGraphProposition;
import rwth.i2.ltl2ba4j.model.IState;
import rwth.i2.ltl2ba4j.model.ITransition;

/**
 * @author Harmen Kastenberg
 * @version $Revision $
 */
public class LTL2BuchiGraph extends AbstractBuchiGraph {
    private Map<IState,BuchiLocation> state2location;

    /**
     * Default constructor.
     */
    private LTL2BuchiGraph() {
        this.state2location = new HashMap<IState,BuchiLocation>();
    }

    /**
     * Return the prototype graph of this class.
     */
    static public BuchiGraph getPrototype() {
        return new LTL2BuchiGraph();
    }

    @Override
    public boolean isEnabled(BuchiTransition transition,
            Set<String> applicableRules) {
        boolean result = true;
        BuchiLabel label = transition.label();
        assert (label instanceof LTL2BuchiLabel) : "The BuchiLabel is not of the correct type: "
            + label.getClass() + " instead of " + LTL2BuchiLabel.class;
        for (IGraphProposition gp : ((LTL2BuchiLabel) label).getLabels()) {
            if (gp.getFullLabel().equals(ModelChecking.SIGMA)) {
                continue;
            }
            boolean applicable = false;
            // only take the label of the proposition - negation will be checked
            // afterwards
            String prop = gp.getLabel();
            for (String ruleName : applicableRules) {
                if (prop.equals(ruleName)) {
                    applicable = true;
                }
            }
            boolean match = (gp.isNegated() ^ applicable);
            result = result && match;
        }
        return result;
    }

    public BuchiGraph newBuchiGraph(String formula) throws FormatException {
        try {
            final BuchiGraph result = new LTL2BuchiGraph();
            Collection<ITransition> automaton = LTL2BA4J.formulaToBA(formula);
            //        Graph prototype = GraphFactory.getInstance(BuchiAutomatonGraph.getPrototype()).newGraph();
            for (ITransition transition : automaton) {
                new Visitor(result).visitTransition(transition);
                //            IState sourceState = t.getSourceState();
                //            DefaultBuchiLocation sourceLocation;
                //            IState targetState = t.getTargetState();
                //            DefaultBuchiLocation targetLocation;
                //
                //            if (state2location.containsKey(sourceState))
                //            {
                //                sourceLocation = state2location.get(sourceState);
                //            }
                //            else
                //            {
                //                sourceLocation = new DefaultBuchiLocation();
                //                state2location.put(sourceState, sourceLocation);
                //            }
                //
                //            if (state2location.containsKey(targetState))
                //            {
                //                targetLocation = state2location.get(targetState);
                //            }
                //            else
                //            {
                //                targetLocation = new DefaultBuchiLocation();
                //                state2location.put(targetState, targetLocation);
                //            }
                //            BuchiTransition transition = new LTL2BuchiTransition(sourceLocation,
                //                new LTL2BuchiLabel(t.getLabels()),
                //                targetLocation);
                //            sourceLocation.addTransition(transition);
            }
            return result;
        } catch (IllegalArgumentException e) {
            throw new FormatException(e.getMessage());
        }
    }

    private BuchiLocation getLocation(IState state) {
        BuchiLocation result = null;
        if (this.state2location.containsKey(state)) {
            result = this.state2location.get(state);
        } else {
            result = new DefaultBuchiLocation();
            this.state2location.put(state, result);
        }
        return result;
    }

    private class Visitor {
        private BuchiGraph graph;

        public Visitor(BuchiGraph graph) {
            this.graph = graph;
        }

        public void visitState(IState state) {
            if (state.isInitial()) {
                this.graph.addInitialLocation(getLocation(state));
            }
            if (state.isFinal()) {
                getLocation(state).setAccepting();
                this.graph.addAcceptingLocation(getLocation(state));
            }
        }

        public void visitTransition(ITransition transition) {
            IState source = transition.getSourceState();
            visitState(source);
            BuchiLocation sourceLocation = getLocation(source);
            IState target = transition.getTargetState();
            visitState(target);
            BuchiLocation targetLocation = getLocation(target);

            BuchiTransition bTransition =
                new LTL2BuchiTransition(sourceLocation, new LTL2BuchiLabel(
                    transition.getLabels()), targetLocation);
            this.graph.addTransition(bTransition);
            //            sourceLocation.addTransition(bTransition);
        }
    }
}
