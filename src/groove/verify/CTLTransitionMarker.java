/*
 * GROOVE: GRaphs for Object Oriented VErification
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
 * $Id: CTLTransitionMarker.java,v 1.7 2008-02-29 11:18:00 fladder Exp $
 */
package groove.verify;

import groove.graph.Label;
import groove.graph.WrapperLabel;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.NameLabel;
import groove.util.Reporter;

import java.util.Iterator;

/**
 * Visitor-implementation of {@link CTLFormulaMarker} using the transition-strategy.
 * This means that every transition-label can be used as a predicate. Basically, a
 * transition in a graph transition systems represents the application of a graph
 * transformation rule (GTR). A GTR is only applicable if there exists a matching
 * from its left-hand-side (LHS) to the current state (graph structure inside the
 * state). So, actually the LHS of the GTR is the property to check on.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2008-02-29 11:18:00 $
 */
public class CTLTransitionMarker extends CTLMatchingMarker {
    @Override
    public void mark(Marking marking, TemporalFormula expr, GTS gts) {
        reporter.start(MARK_T);
        super.mark(marking, expr, gts);
        reporter.stop();
    }

    @Override
    public void markAtom(Marking marking, TemporalFormula property, GTS gts) {
        reporter.start(MARK_ATOM_T);
        boolean specialAtom = markSpecialAtom(marking, property, gts);
        if (!specialAtom) {
	        String name = ((CTLStarFormula.Atom) property).predicateName();
	        Iterator<? extends GraphState> stateIter = gts.nodeSet().iterator();
	        while(stateIter.hasNext()) {
	            GraphState nextState = stateIter.next();
	            // this state satisfies the CTL-expression if it has an outgoing-transition labelled with the
	            // name of the property
	            // if no such outgoing transition exists, this state does not satisfy the CTL-expression
	            boolean satisfies = false;
	            Iterator<GraphTransition> transitionIter = gts.outEdgeSet(nextState).iterator();
	            while (transitionIter.hasNext()) {
//	                Edge nextTransition = (Edge) transitionIter.next();
	                Label label = transitionIter.next().getEvent().getLabel();
	                String transitionName = null;
	                if (!(label instanceof NameLabel)) {
	                	transitionName = ((WrapperLabel<?>)label).text();
	                	if (transitionName.startsWith("[")) {
	                		transitionName = transitionName.substring(1,transitionName.length()-1);
	                	}
	                	int index = transitionName.indexOf(",");
	                	transitionName = transitionName.substring(0,index);
//	                	System.out.println("WrapperLabel...");
	                } else {
		                transitionName = ((NameLabel) label).name();
	                }
//	                String transitionName = ((NameLabel) transitionIter.next().getEvent().getLabel()).name();
	                if (transitionName.equals(name))
	                	satisfies = true;
	            }
	            if (satisfies) {
	            	marking.set(nextState, property, true);
	            }
	            else {
	            	marking.set(nextState, property, false);
	            	property.getCounterExamples().add(nextState);
	            }
	        }
        }
        reporter.stop();
    }

    /**
     * Reporter.
     */
    static public final Reporter reporter = Reporter.register(CTLTransitionMarker.class);
    /**
     * Registration id for mark-method.
     */
    static public final int MARK_T = reporter.newMethod("mark(Marking, TemporalFormula, GTS) - transition");
    /**
     * Registration id for markAtom-method.
     */
    static public final int MARK_ATOM_T = reporter.newMethod("markAtom(Marking, TemporalFormula, GTS) - transition");
}
