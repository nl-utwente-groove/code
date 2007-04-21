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
 * $Id: ControlledStrategy.java,v 1.5 2007-04-21 07:28:43 rensink Exp $
 */
package groove.lts.explore;

import groove.lts.GraphState;
import groove.lts.LTS;
import groove.lts.State;
import groove.trans.Matching;
import groove.trans.Rule;
import groove.trans.RuleApplication;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Stack;

/**
 * Strategy that searches the state space in a depth-first fashion, using a list of rules
 * to control the search.
 * @author Arend Rensink
 * @version $Revision: 1.5 $
 */
public class ControlledStrategy extends AbstractStrategy {
    /** Name of this strategy. */
    public static final String STRATEGY_NAME = "Controlled";
    /** Short description of this strategy. */
    static public final String STRATEGY_DESCRIPTION =
        "Performs a depth-first search controlled by a sequence of rules";
    
    /**
     * Sets the program for this strategy.
     * @param program the sequence of rules
     * @param findAll flag to indicate if exploration should yield one or all reachable states
     */
    public void setProgram(List<Rule> program, boolean findAll) {        
        this.program = program;
        this.findAll = findAll;
    }
    
    /** In addition to calling the <code>super</code> method, also resets any previous exploration data. */
    @Override
    public void setLTS(LTS lts) {
        super.setLTS(lts);
        pc = 0;
        found = false;
    }

    /** 
     * The result nodes for this method are the states reached by applying
     * all the rules in the program sequentially.
     */
    public Collection<? extends State> explore() throws InterruptedException {
        Collection<State> result = new LinkedHashSet<State>();
        State found = findNextState();
        if (found != null) {
            result.add(found);
            if (findAll) {
                do {
                    found = findNextState();
                    if (found != null) {
                        result.add(found);
                    }
                } while (found != null && !Thread.interrupted());
            }
        }
        return result;
    }
    
    /** 
     * Returns the next state reached by applying all rules in the program 
     * sequentially, if there is any such next state; or <code>null</code> otherwise.
     */
    protected State findNextState() {
        assert pc == 0 || pc == program.size();
        boolean forward;
        if (found) {
            pc--;
            forward = false;
        } else {
            states = new Stack<GraphState>();
            states.push(getAtState());
            images = new Stack<Iterator<? extends Matching>>();
            forward = true;
        }
        while (pc >= 0 && pc < program.size()) {
            GraphState currentState = states.peek();
            // retrieve the current search record
            Iterator<? extends Matching> matchingIter;
            if (forward) {
                // make a new record
                matchingIter = program.get(pc).getMatchingIter(currentState.getGraph());
                images.push(matchingIter);
            } else {
                // take it from the existing records
                matchingIter = images.peek();
            }
            // find a new image 
            forward = matchingIter.hasNext();
            if (forward) {
            	Rule rule = program.get(pc);
                RuleApplication ruleApplication = getGenerator().getRecord().getApplication(rule, matchingIter.next());
                GraphState realNextState = getGenerator().addTransition(currentState, ruleApplication);
                states.push(realNextState);
                pc++;
            } else {
                images.pop();
                states.pop();
                pc--;
            }
        }
        found = pc >= 0;
        return found ? states.peek() : null;
    }

    public String getName() {
        return STRATEGY_NAME;
    }

    public String getShortDescription() {
        return STRATEGY_DESCRIPTION;
    }

    /** The sequence of rules controlling this strategy. */
    private List<Rule> program;
    /** Flag indicating if exploration should yield one or all reachable states. */
    private boolean findAll;
    /** Flag indicating that we already found a solution. */
    private boolean found;
    /** Program counter; index in {@link #program}. */
    private int pc;
    /** List of currently found image iterators for the rules in the program. */
    private Stack<Iterator<? extends Matching>> images;
    /** 
     * List of currently found intermediate states of the program. 
     * The element at index <code>i</code> is the state reached after step <code>i-1</code> of the program.
     */
    private Stack<GraphState> states;
}
