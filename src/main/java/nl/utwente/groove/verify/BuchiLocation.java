/* * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007 * University of Twente *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not * use this file except in compliance with the License. You may obtain a copy of * the License at http://www.apache.org/licenses/LICENSE-2.0 *  * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the * License for the specific language governing permissions and limitations under * the License. *  * $Id$ */package nl.utwente.groove.verify;import java.util.HashSet;import java.util.Set;import nl.utwente.groove.graph.ANode;/** * Location (i.e., state) of a Buchi automaton. *  * @author Harmen Kastenberg * @version $Revision$ $Date: 2008-02-22 13:02:44 $ */public class BuchiLocation extends ANode {    /** Constructs a new, non-accepting location. */    public BuchiLocation(int nr) {        super(nr);    }    /** Returns the set of outgoing transitions of this location. */    public Set<BuchiTransition> outTransitions() {        if (this.transitions == null) {            this.transitions = new HashSet<>();        }        return this.transitions;    }    /** Adds a transition to the outgoing transitions of this location. */    public boolean addTransition(BuchiTransition transition) {        return outTransitions().add(transition);    }    /** Sets the Buchi location to accepting. */    public void setAccepting() {        this.accepting = true;    }    /**     * Returns whether this location is accepting     *      * @return the value of <code>accepting</code>     */    public boolean isAccepting() {        return this.accepting;    }    @Override    protected String getToStringPrefix() {        return "b";    }    /** Flag indicating if this location is accepting. */    private boolean accepting;    /** Set of outgoing transitions of this location. */    private Set<BuchiTransition> transitions;}