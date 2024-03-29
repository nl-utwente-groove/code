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
package nl.utwente.groove.control;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.syntax.Variable;
import nl.utwente.groove.grammar.Callable;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.Signature;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.Pair;

/**
 * Call of a callable unit.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class Call extends Pair<Callable,List<? extends CtrlArg>> implements Comparable<Call> {
    /**
     * Constructs a call of a given unit, with arguments.
     */
    private Call(Callable unit, List<? extends CtrlArg> args, boolean explicitArgs) {
        super(unit, args);
        assert args != null;
        this.explicitArgs = explicitArgs;
    }

    /**
     * Constructs a call of a given unit, with (non-{@code null}) arguments.
     */
    public Call(Callable unit, List<? extends CtrlArg> args) {
        this(unit, args, true);
    }

    /**
     * Constructs a call of a given unit, without arguments.
     * This will construct wildcard arguments for the call.
     */
    public Call(Callable unit) {
        this(unit, createWildArgs(unit.getSignature()), false);
    }

    /** Indicates if this call has explicit arguments. */
    public boolean hasExplicitArgs() {
        return this.explicitArgs;
    }

    private final boolean explicitArgs;

    /** Returns the called unit. */
    public Callable getUnit() {
        return one();
    }

    /** Returns the called unit, cast to a {@link Rule}. */
    public Rule getRule() {
        return (Rule) one();
    }

    /** Returns the list of arguments. */
    public List<? extends CtrlArg> getArgs() {
        return two();
    }

    /** Indicates if this switch has any output variables. */
    public boolean hasOutVars() {
        return !getOutVars().isEmpty();
    }

    /** Returns the mapping of output variables to argument positions of this call. */
    public Map<CtrlVar,@Nullable Integer> getOutVars() {
        var result = this.outVars;
        if (result == null) {
            initVars();
            result = this.outVars;
            assert result != null;
        }
        return result;
    }

    private @Nullable Map<CtrlVar,@Nullable Integer> outVars;

    /** Returns the mapping of input variables to argument positions of this call. */
    public Map<CtrlVar,@Nullable Integer> getInVars() {
        var result = this.inVars;
        if (result == null) {
            initVars();
            result = this.inVars;
            assert result != null;
        }
        return result;
    }

    private @Nullable Map<CtrlVar,@Nullable Integer> inVars;

    /** Initialises the input and output variables of this call. */
    private void initVars() {
        Map<CtrlVar,@Nullable Integer> outVars = new LinkedHashMap<>();
        Map<CtrlVar,@Nullable Integer> inVars = new LinkedHashMap<>();
        int size = getArgs().size();
        for (int i = 0; i < size; i++) {
            CtrlArg arg = getArgs().get(i);
            if (arg instanceof CtrlArg.Var v) {
                CtrlVar var = v.var();
                if (arg.inOnly()) {
                    inVars.put(var, i);
                } else {
                    assert arg.outOnly();
                    outVars.put(var, i);
                }
            }
        }
        this.outVars = outVars;
        this.inVars = inVars;
    }

    /** Returns a copy of this call in which all data variables are enriched with binding information. */
    public Call bind(java.util.function.Function<Variable,Object> bindMap) {
        var newArgs = getArgs().stream().map(a -> a.bind(bindMap)).toList();
        return new Call(getUnit(), newArgs);
    }

    @Override
    public String toString() {
        return getUnit().getQualName() + Groove.toString(getArgs().toArray(), "(", ")", ", ");
    }

    @Override
    public int compareTo(Call o) {
        int result = getUnit().getQualName().compareTo(o.getUnit().getQualName());
        if (result != 0) {
            return result;
        }
        result = getArgs().size() - o.getArgs().size();
        if (result != 0) {
            return result;
        }
        for (int i = 0; i < getArgs().size(); i++) {
            result = getArgs().get(i).toString().compareTo(o.getArgs().get(i).toString());
            if (result != 0) {
                return result;
            }
        }
        return result;
    }

    private static List<CtrlArg> createWildArgs(Signature<?> sig) {
        int count = sig.size();
        List<CtrlArg> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            result.add(CtrlArg.wild());
        }
        return result;
    }
}
