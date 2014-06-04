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
package groove.control.instance;

import groove.control.Binding;
import groove.control.Binding.Source;
import groove.control.CtrlPar;
import groove.control.CtrlPar.Const;
import groove.control.CtrlPar.Var;
import groove.control.CtrlVar;
import groove.control.Procedure;
import groove.control.Valuator;
import groove.control.template.Location;
import groove.control.template.Switch;
import groove.control.template.SwitchStack;
import groove.grammar.Rule;
import groove.grammar.host.HostNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.sun.org.apache.bcel.internal.generic.PUSH;

/**
 * Action to be taken as part of a {@link Step}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Assignment {
    /**
     * Creates an action with all necessary parameters.
     */
    private Assignment(Kind kind, Map<CtrlVar,Binding> bindings) {
        this(kind, bindings.values());
    }

    /**
     * Creates an action with all necessary parameters.
     */
    private Assignment(Kind kind, Collection<Binding> bindings) {
        this.kind = kind;
        this.bindings = new Binding[bindings.size()];
        bindings.toArray(this.bindings);
    }

    /** Returns the kind of action. */
    public Kind getKind() {
        return this.kind;
    }

    private final Kind kind;

    /** Returns the binding of a given variable index. */
    public Binding getBinding(int index) {
        return this.bindings[index];
    }

    /** Returns the assignment for this action. */
    public Binding[] getBindings() {
        return this.bindings;
    }

    private final Binding[] bindings;

    /** Returns the number of bindings in this assignment. */
    public int size() {
        return this.bindings.length;
    }

    /**
     * Applies this assignment to a given frame valuation.
     * {@link Source#ANCHOR}
     * and {@link Source#CREATOR} are ignored, meaning that the corresponding
     * values are set to {@code null}.
     */
    public HostNode[] apply(Object[] val) {
        Binding[] bindings = getBindings();
        HostNode[] result = new HostNode[bindings.length];
        Object[] parentValues = Valuator.pop(val);
        for (int i = 0; i < bindings.length; i++) {
            Binding bind = bindings[i];
            HostNode value;
            switch (bind.getSource()) {
            case ANCHOR:
            case CREATOR:
                value = null;
                break;
            case CALLER:
                assert parentValues != null : String.format(
                    "Can't apply %s: valuation %s does not have parent level", this,
                    Valuator.toString(val));
                value = Valuator.get(parentValues, bind.getIndex());
                break;
            case CONST:
                value = bind.getValue().getNode();
                break;
            case VAR:
                value = Valuator.get(val, bind.getIndex());
                break;
            default:
                assert false;
                value = null;
            }
            result[i] = value;
        }
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(this.bindings);
        result = prime * result + this.kind.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Assignment)) {
            return false;
        }
        Assignment other = (Assignment) obj;
        if (!Arrays.equals(this.bindings, other.bindings)) {
            return false;
        }
        if (this.kind != other.kind) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.kind.name() + Arrays.toString(this.bindings);
    }

    /**
     * Returns bindings for a list of target variables of a
     * control step, using the source variables
     * combined with the output parameters of the call.
     */
    static Assignment modify(Switch swit) {
        assert swit.getKind() == Switch.Kind.RULE;
        List<Binding> result = new ArrayList<Binding>();
        List<CtrlVar> sourceVars = swit.getSourceVars();
        Map<CtrlVar,Integer> outVars = swit.getCall().getOutVars();
        for (CtrlVar var : swit.onFinish().getVars()) {
            Integer ix = outVars.get(var);
            Binding rhs;
            if (ix == null) {
                // the value comes from the source
                int pos = sourceVars.indexOf(var);
                assert pos >= 0;
                rhs = Binding.var(pos);
            } else {
                // the value is an output parameter of the rule
                Rule rule = (Rule) swit.getUnit();
                rhs = rule.getParBinding(ix);
            }
            result.add(rhs);
        }
        return call(result);
    }

    /**
     * Computes the variable assignment for the initial location of a called template
     * from the variables of the caller location and the arguments of the call.
     * @param swit the template call
     */
    static Assignment enter(Switch swit) {
        assert swit.getKind().isProcedure();
        List<Binding> result = new ArrayList<Binding>();
        List<CtrlVar> sourceVars = swit.getSourceVars();
        Procedure proc = (Procedure) swit.getUnit();
        Map<CtrlVar,Integer> sig = proc.getInPars();
        for (CtrlVar var : proc.getTemplate().getStart().getVars()) {
            // all initial state variables are formal input parameters
            Integer ix = sig.get(var);
            assert ix != null;
            // look up the corresponding argument in the call
            CtrlPar arg = swit.getArgs().get(ix);
            Binding rhs;
            if (arg instanceof Const) {
                rhs = Binding.value((Const) arg);
            } else {
                rhs = Binding.var(sourceVars.indexOf(((Var) arg).getVar()));
            }
            result.add(rhs);
        }
        return push(result);
    }

    /**
     * Computes the variable assignment for the location to where control returns
     * after a procedure call, from the variables in the final location of the
     * procedure template and the source state of the call.
     * @param top final location of the template
     * @param swit the template call
     */
    static Assignment exit(Location top, Switch swit) {
        assert swit.getKind().isProcedure();
        List<Binding> result = new ArrayList<Binding>();
        List<CtrlPar.Var> sig = swit.getUnit().getSignature();
        List<CtrlVar> callerVars = swit.getSourceVars();
        Map<CtrlVar,Integer> outVars = swit.getCall().getOutVars();
        Map<CtrlVar,Integer> finalVars = top.getVarIxMap();
        for (CtrlVar var : swit.onFinish().getVars()) {
            Integer ix = outVars.get(var);
            Binding rhs;
            if (ix == null) {
                // the value comes from the caller
                rhs = Binding.caller(callerVars.indexOf(var));
            } else {
                // the value comes from an output parameter of the call
                // find the corresponding formal parameter
                CtrlVar par = sig.get(ix).getVar();
                // look it up in the final location variables
                rhs = Binding.var(finalVars.get(par));
            }
            assert rhs != null;
            result.add(rhs);
        }
        return pop(result);
    }

    /**
     * Computes the pop actions between two frames.
     * @param stack switch stack of the source frame
     * @param top template location of the source frame
     * @param remaining number of switches in the target frame switch stack
     */
    static public List<Assignment> computePops(SwitchStack stack, Location top, int remaining) {
        List<Assignment> result = new ArrayList<Assignment>();
        for (int i = stack.size() - 1; i >= remaining; i--) {
            assert top.isFinal();
            result.add(Assignment.exit(top, stack.get(i)));
            top = stack.get(i).onFinish();
        }
        return result;
    }

    /** Creates a new {@link PUSH} action with a given assignment. */
    public static Assignment push(List<Binding> bindings) {
        return new Assignment(Kind.PUSH, bindings);
    }

    /** Creates a new {@link Kind#POP} action with a given assignment. */
    public static Assignment pop(List<Binding> bindings) {
        return new Assignment(Kind.POP, bindings);
    }

    /** Creates a new {@link Kind#MODIFY} action with a given assignment. */
    public static Assignment call(List<Binding> bindings) {
        return new Assignment(Kind.MODIFY, bindings);
    }

    /** Kind of {@link Assignment}. */
    public static enum Kind {
        /** Create and initialise a frame instance. */
        PUSH,
        /** Pop a frame instance. */
        POP,
        /** Invoke a rule. */
        MODIFY, ;
    }
}
