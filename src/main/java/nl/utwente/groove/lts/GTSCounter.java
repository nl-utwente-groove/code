/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.lts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.utwente.groove.lts.Status.Flag;

/** Class counting various statistics of a GTS under exploration. */
public class GTSCounter implements GTSListener {
    /** Constructs an uninitialised counter. */
    public GTSCounter() {
        // empty
    }

    /** Hooks this counter onto a new GTS. */
    public void setGTS(GTS gts) {
        if (this.gts != null) {
            this.gts.removeLTSListener(this);
        }
        this.gts = gts;
        initialise();
        if (gts != null) {
            gts.addLTSListener(this);
        }
    }

    /** Initialises the counters. */
    public void initialise() {
        for (Flag flag : Flag.values()) {
            this.count[flag.ordinal()] = 0;
        }
        this.absentCount = 0;
        this.innerStateCount = 0;
        this.innerStepCount = 0;
        this.ruleTransitionCount = 0;
        this.absentTransitionCount = 0;
        this.inTransMap.clear();
        if (this.gts != null) {
            for (GraphState state : this.gts.nodeSet()) {
                register(state);
            }
            for (GraphTransition trans : this.gts.edgeSet()) {
                register(trans);
            }
        }
    }

    @Override
    public void addUpdate(GTS gts, GraphState state) {
        register(state);
    }

    private void register(GraphState state) {
        for (Flag flag : Flag.values()) {
            if (state.hasFlag(flag)) {
                this.count[flag.ordinal()]++;
            }
        }
        if (state.isAbsent()) {
            this.absentCount++;
        }
        if (!state.isFull()) {
            this.inTransMap.put(state, new ArrayList<>());
        } else if (state.isInner()) {
            this.innerStateCount++;
        }
    }

    @Override
    public void addUpdate(GTS gts, GraphTransition transition) {
        register(transition);
    }

    private void register(GraphTransition trans) {
        if (DEBUG && trans instanceof RecipeTransition) {
            System.out.printf("Registering recipe transition %s%n", trans);
        }

        if (trans.isInnerStep()) {
            this.innerStepCount++;
        }
        if (trans instanceof RuleTransition) {
            this.ruleTransitionCount++;
        }
        GraphState target = trans.target();
        if (target.isAbsent()) {
            this.absentTransitionCount++;
        } else if (trans instanceof RuleTransition rt && !target.isFull()) {
            this.inTransMap.get(target).add(rt);
        }
    }

    @Override
    public void statusUpdate(GTS gts, GraphState state, int change) {
        for (Flag flag : Flag.values()) {
            if (flag.test(change)) {
                this.count[flag.ordinal()]++;
            }
        }
        if (Flag.CLOSED.test(change) && state.isInner()) {
            this.innerStateCount++;
        }
        if (Flag.FULL.test(change)) {
            List<RuleTransition> inTrans = this.inTransMap.remove(state);
            if (state.isAbsent()) {
                this.absentTransitionCount += inTrans.size();
                this.absentCount++;
            }
        }
    }

    /** Returns the total number of states in the GTS. */
    public int getStateCount() {
        return this.gts.nodeCount();
    }

    /** Returns the number of inner states in the GTS. */
    public int getInnerStateCount() {
        return this.innerStateCount;
    }

    /** Returns the number of final states in the GTS. */
    public int getFinalStateCount() {
        return this.gts.getFinalStates().size();
    }

    /** Returns the number of incompletely explored states in the GTS. */
    public int getOpenStateCount() {
        return this.gts.getOpenStateCount();
    }

    /** Returns the number of absent states in the GTS. */
    public int getAbsentStateCount() {
        return this.absentCount;
    }

    /** Returns the total number of transitions in the GTS. */
    public int getTransitionCount() {
        return this.gts.edgeCount();
    }

    /** Returns the total number of inner transitions in the GTS. */
    public int getInnerStepCount() {
        return this.innerStepCount;
    }

    /** Returns the total number of rule transitions in the GTS. */
    public int getRuleTransitionCount() {
        return this.ruleTransitionCount;
    }

    /** Returns the total number of transitions to absent states. */
    public int getAbsentTransitionCount() {
        return this.absentTransitionCount;
    }

    /** Returns the number of states with a given status. */
    public int getStateCount(Flag flag) {
        return this.count[flag.ordinal()];
    }

    private GTS gts;
    private int absentCount;
    private final int[] count = new int[Flag.values().length];
    private int innerStateCount;
    private int innerStepCount;
    private int ruleTransitionCount;
    private int absentTransitionCount;
    private final Map<GraphState,List<RuleTransition>> inTransMap = new HashMap<>();
    private final static boolean DEBUG = false;
}
