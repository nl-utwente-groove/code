/* * GROOVE: GRaphs for Object Oriented VErification *  * Copyright 2003--2007 University of Twente *  *  *  * Licensed under the Apache License, Version 2.0 (the "License"); *  * you may not use this file except in compliance with the License. *  * You may obtain a copy of the License at *  * http://www.apache.org/licenses/LICENSE-2.0 *  *  *  * Unless required by applicable law or agreed to in writing, *  * software distributed under the License is distributed on an *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, *  * either express or implied. See the License for the specific *  * language governing permissions and limitations under the License. *  *  *  * $Id: ControlTransition.java,v 1.10 2008-01-30 11:13:57 fladder Exp $ */package groove.control;import groove.graph.AbstractEdge;import java.util.HashSet;import java.util.List;import java.util.Set;/** * Represents a transition in a control automaton. * Control transitions have pairs of guards and rule calls as labels. * A rule call is a rule with a sequence of input and output parameters. * A guard is a failure set, i.e., a set of rules that cannot be performed. * A transition is <i>virtual</i> if the rule names in the call and guard * are only given as strings, and <i>actual</i> if they are instantiated rules.  * @author Arend Rensink */public class CtrlTransition extends AbstractEdge<CtrlState,CtrlLabel,CtrlState> {    /**     * Creates a new control transition between two control states.     */    public CtrlTransition(CtrlState source, CtrlLabel label, CtrlState target) {        super(source, label, target);        this.inVars = new HashSet<CtrlVar>();        this.outVars = new HashSet<CtrlVar>();        List<CtrlPar> args = label.getCall().getArgs();        if (args != null) {            for (CtrlPar arg : args) {                if (arg instanceof CtrlPar.Var) {                    CtrlVar var = ((CtrlPar.Var) arg).getVar();                    if (arg.isInOnly()) {                        this.inVars.add(var);                    } else {                        this.outVars.add(var);                    }                }            }        }    }    /** Returns the set of variables used as input parameters in this transition. */    public Set<CtrlVar> getInVars() {        return this.inVars;    }    /** Returns the set of variables used as output parameters in this transition. */    public Set<CtrlVar> getOutVars() {        return this.outVars;    }    /** Set of variables used as input parameters. */    private final Set<CtrlVar> inVars;    /** Set of variables used as output parameters. */    private final Set<CtrlVar> outVars;}