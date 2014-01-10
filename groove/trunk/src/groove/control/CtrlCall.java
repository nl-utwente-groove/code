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
package groove.control;

import static groove.control.CtrlEdge.Kind.OMEGA;
import static groove.control.CtrlEdge.Kind.RULE;
import groove.control.CtrlEdge.Kind;
import groove.grammar.Action;
import groove.grammar.Recipe;
import groove.grammar.Rule;
import groove.util.Groove;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates a call of a rule or function from a control automaton.
 * The call embodies the rule or function name and a sequence of arguments.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlCall {
    /** Constructor for the singleton success call. */
    private CtrlCall() {
        this.kind = OMEGA;
        this.name = OMEGA_NAME;
        this.unit = null;
        this.args = null;
        this.context = null;
    }

    /**
     * Constructs an instantiated call for a given callable unit and list of arguments.
     * @param unit the unit to be called; non-{@code null}
     * @param args list of arguments for the call; may be {@code null}
     */
    public CtrlCall(Callable unit, List<CtrlPar> args) {
        this(unit, args, null);
    }

    /**
     * Constructs an instantiated call for a given callable unit, list of arguments
     * and contextual recipe.
     * @param unit the unit to be called; non-{@code null}
     * @param args list of arguments for the call; may be {@code null}
     * @param context enclosing recipe; may be {@code null}
     */
    private CtrlCall(Callable unit, List<CtrlPar> args, Recipe context) {
        this.kind = unit.getKind();
        this.name = unit.getFullName();
        this.args = args;
        this.unit = unit;
        this.context = context;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CtrlCall)) {
            return false;
        }
        CtrlCall other = (CtrlCall) obj;
        if (getKind() != other.getKind()) {
            return false;
        }
        if (isOmega()) {
            return other.isOmega();
        }
        if (!getName().equals(other.getName())) {
            return false;
        }
        if (getArgs() == null) {
            if (other.getArgs() != null) {
                return false;
            }
        } else if (!getArgs().equals(other.getArgs())) {
            return false;
        }
        if (getContext() == null) {
            return other.getContext() == null;
        } else {
            return getContext().equals(other.getContext());
        }
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = getKind().hashCode();
        result = prime * result + (isOmega() ? 1231 : 1237);
        if (getName() != null) {
            result = prime * result + getName().hashCode();
        }
        if (getContext() != null) {
            result = prime * result + getContext().hashCode();
        }
        if (getArgs() != null) {
            result = prime * result + getArgs().hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        String result = getName();
        if (getArgs() != null) {
            result += Groove.toString(getArgs().toArray(), "(", ")", ",");
        }
        return result;
    }

    /**
     * Indicates if this is an omega call.
     * @see #OMEGA_CALL
     */
    public boolean isOmega() {
        return getKind() == OMEGA;
    }

    /**
     * Returns a new control call, based on the rule or function of
     * this call but with replaced arguments.
     * @param args the arguments of the new call
     */
    public CtrlCall copy(List<CtrlPar> args) {
        assert args == null || args.size() == getArgs().size();
        CtrlCall result = null;
        switch (getKind()) {
        case OMEGA:
            result = this;
            break;
        case RULE:
        case FUNCTION:
        case RECIPE:
            result = new CtrlCall(getUnit(), args);
            break;
        default:
            assert false;
        }
        return result;
    }

    /** Tests if this control call modifies the input arguments of another. */
    public boolean modifies(CtrlCall other) {
        boolean result = false;
        if (!(getArgs() == null || getArgs().isEmpty()
            || other.getArgs() == null || other.getArgs().isEmpty())) {
            Map<CtrlVar,Integer> otherInVars = other.getInVars();
            for (CtrlVar outVar : getOutVars().keySet()) {
                if (otherInVars.containsKey(outVar)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

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

    /** Copies this call while embedding it into a recipe. */
    public CtrlCall embed(Recipe recipe) {
        if (isOmega()) {
            assert recipe == null;
            return this;
        } else {
            assert getRule() != null;
            return new CtrlCall(getRule(), getArgs(), recipe);
        }
    }

    private Map<CtrlVar,Integer> inVars;
    private Map<CtrlVar,Integer> outVars;

    /** Returns the kind of object being called. */
    public groove.control.CtrlEdge.Kind getKind() {
        return this.kind;
    }

    /** The kind of object being called. */
    private final groove.control.CtrlEdge.Kind kind;

    /** 
     * Returns the arguments of the call.
     * @return the list of arguments; or {@code null} if this is an omega call
     * or a parameterless call.
     * @see #OMEGA_CALL
     */
    public final List<CtrlPar> getArgs() {
        return this.args;
    }

    /** 
     * The list of arguments of the control call.
     */
    private final List<CtrlPar> args;

    /** 
     * Returns the callable unit being called.
     */
    public final Callable getUnit() {
        return this.unit;
    }

    /** 
     * Returns the rule being called.
     * @return the rule being called; or {@code null} if this is a
     * function, recipe or omega call.
     * @see #getKind()
     */
    public final Rule getRule() {
        return getKind() == RULE ? (Rule) getUnit() : null;
    }

    /** 
     * The rule being called. 
     * May be {@code null} if this is a function or omega call.
     */
    private final Callable unit;

    /** 
     * Returns the contextual recipe of this call, if any
     * @return the contextual recipe, if this is a rule call within a recipe
     * @see #getKind()
     */
    public final Recipe getContext() {
        return this.context;
    }

    /** 
     * Indicates if this call has an enclosing recipe.
     */
    public final boolean hasContext() {
        return getContext() != null;
    }

    /** 
     * The enclosing recipe of this call.
     * May be {@code null} if this is not a sub-rule call.
     */
    private final Recipe context;

    /** 
     * Returns the name of the function being called.
     * @return the name of the function being called; or {@code null} if this is a rule call or
     * omega call.
     * @see #isOmega()
     */
    public final String getName() {
        return this.name;
    }

    /** The name of the function being called; non-{@code null}. */
    private final String name;

    /** 
     * Returns the call kind of a given grammar action.
     * This is either {@link Kind#RULE} or {@link Kind#RECIPE},
     * depending on the actual action object.
     */
    public static Kind getKind(Action action) {
        switch (action.getKind()) {
        case RULE:
            return Kind.RULE;
        case RECIPE:
            return Kind.RECIPE;
        default:
            assert false;
            return null;
        }
    }

    /** Name of the omega rule (which models termination). */
    public static final String OMEGA_NAME = "\u03A9";
    /**
     * A special call, indicating that the control program is successful.
     * Can be seen as a call to a rule that always matches and makes no changes.
     */
    public static final CtrlCall OMEGA_CALL = new CtrlCall();

}
