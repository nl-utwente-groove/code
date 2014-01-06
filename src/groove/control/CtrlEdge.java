/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.control;

import groove.grammar.Action;
import groove.graph.ALabelEdge;
import groove.graph.Edge;
import groove.util.Groove;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Control automaton edge.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlEdge extends ALabelEdge<CtrlLocation> {
    /** Constructs a choice edge.
     * @param source source location of the edge
     * @param target target location of the edge
     * @param success flag indicating if this is a success or failure edge
     */
    public CtrlEdge(CtrlLocation source, CtrlLocation target, boolean success) {
        super(source, target);
        this.kind = Kind.CHOICE;
        this.success = success;
        this.name = null;
        this.unit = null;
        this.args = null;
    }

    /**
     * Constructs a control edge for a call.
     * @param source source location of the edge
     * @param target target location of the edge
     * @param unit callable unit to be invoked
     * @param args list of arguments for the call; non-{@code null}
     */
    public CtrlEdge(CtrlLocation source, CtrlLocation target, Callable unit,
            List<CtrlPar> args) {
        super(source, target);
        this.kind = unit.getKind();
        this.name = unit.getFullName();
        this.unit = unit;
        this.args = args;
        this.success = false;
    }

    /**
     * Convenience method testing if this is a choice edge.
     * @see #getKind() 
     */
    public boolean isChoice() {
        return getKind() == Kind.CHOICE;
    }

    /**
     * Returns the kind of this label.
     */
    public Kind getKind() {
        return this.kind;
    }

    private final Kind kind;

    /**
     * Returns the name of the rule, function or recipe invoked in
     * this edge.
     * Should only be called if this is not a choice edge.
     */
    public String getName() {
        assert !isChoice();
        return this.name;
    }

    private final String name;

    /** 
     * Returns the arguments of the call of this edge.
     * Should only be invoked if this is not a choice edge.
     * @return the list of arguments
     */
    public final List<CtrlPar> getArgs() {
        assert !isChoice();
        return this.args;
    }

    /** 
     * The list of arguments of the control call.
     */
    private final List<CtrlPar> args;

    /** 
     * Returns the invoked recipe of this call.
     * Should only be invoked if this is a recipe edge.
     * @see #getKind()
     */
    public final Callable getUnit() {
        assert getKind().isCallable();
        return this.unit;
    }

    /** 
     * The enclosing recipe of this call.
     * Is {@code null} if this is not a recipe call.
     */
    private final Callable unit;

    /**
     * Indicates if this transition is a success or failure edge.
     * Should only be invoked if this is a choice edge.
     * @return {@code true} if this is a success edge, {@code false} if
     * this is a failure edge.
     */
    public boolean isSuccess() {
        assert isChoice();
        return this.success;
    }

    private final boolean success;

    /** Returns the mapping of output variables to argument positions of this call. */
    public Map<CtrlVar,Integer> getOutVars() {
        if (this.outVars == null) {
            initVars();
        }
        return this.outVars;
    }

    /** Returns the mapping of input variables to argument positions of this call. */
    public Map<CtrlVar,Integer> getInVars() {
        if (this.inVars == null) {
            initVars();
        }
        return this.inVars;
    }

    /** Initialises the input and output variables of this call. */
    private void initVars() {
        assert !isChoice();
        Map<CtrlVar,Integer> outVars = new HashMap<CtrlVar,Integer>();
        Map<CtrlVar,Integer> inVars = new HashMap<CtrlVar,Integer>();
        if (getArgs() != null && !getArgs().isEmpty()) {
            int size = getArgs().size();
            for (int i = 0; i < size; i++) {
                CtrlPar arg = getArgs().get(i);
                if (arg instanceof CtrlPar.Var) {
                    CtrlVar var = ((CtrlPar.Var) arg).getVar();
                    if (arg.isInOnly()) {
                        inVars.put(var, i);
                    } else {
                        assert arg.isOutOnly();
                        outVars.put(var, i);
                    }
                }
            }
        }
        this.outVars = outVars;
        this.inVars = inVars;
    }

    private Map<CtrlVar,Integer> inVars;
    private Map<CtrlVar,Integer> outVars;

    @Override
    protected int computeLabelHash() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + getKind().hashCode();
        // don't call isSuccess here to escape kind test
        result = prime * result + (this.success ? 1231 : 1237);
        result =
            prime * result + ((this.args == null) ? 0 : this.args.hashCode());
        result =
            prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }

    @Override
    protected boolean isTypeEqual(Object obj) {
        return obj instanceof CtrlEdge;
    }

    @Override
    protected boolean isLabelEqual(Edge obj) {
        if (this == obj) {
            return true;
        }
        CtrlEdge other = (CtrlEdge) obj;
        if (getKind() != other.getKind()) {
            return false;
        }
        if (getKind() == Kind.CHOICE) {
            if (isSuccess() != other.isSuccess()) {
                return false;
            }
        } else {
            if (!getArgs().equals(other.getArgs())) {
                return false;
            }
            if (!getName().equals(other.getName())) {
                return false;
            }
        }
        return true;
    }

    public String text() {
        String result;
        if (getKind() == Kind.CHOICE) {
            result = isSuccess() ? "succ" : "fail";
        } else {
            result = getName();
            if (getArgs() != null) {
                result += Groove.toString(getArgs().toArray(), "(", ")", ",");
            }
            return result;
        }
        return result;
    }

    /** Control transition kind. */
    public static enum Kind {
        /** Rule invocation transition. */
        RULE("rule"),
        /** Function call transition. */
        FUNCTION("function"),
        /** Recipe call transition. */
        RECIPE("recipe"),
        /** Choice transition. */
        CHOICE("choice"),
        /** Legacy kind modelling final states. */
        OMEGA("omega"), ;

        private Kind(String name) {
            this.name = name;
        }

        /** 
         * Indicates if this kind of name has an associated body
         * (translated to a control automaton).
         */
        public boolean hasBody() {
            return this == FUNCTION || this == RECIPE;
        }

        /** Indicates if this kind represents a {@link Callable} unit. */
        public boolean isCallable() {
            return this == FUNCTION || this == RECIPE || this == RULE;
        }

        /** Indicates if this kind represents an {@link Action}. */
        public boolean isAction() {
            return this == RECIPE || this == RULE;
        }

        /** 
         * Returns the description of this name kind,
         * with the initial letter optionally capitalised.
         */
        public String getName(boolean upper) {
            StringBuilder result = new StringBuilder(this.name);
            if (upper) {
                result.replace(0, 1,
                    "" + Character.toUpperCase(this.name.charAt(0)));
            }
            return result.toString();
        }

        private final String name;
    }
}
