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

import groove.grammar.Rule;
import groove.grammar.host.HostFactory;
import groove.util.Groove;
import groove.util.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Call of a callable unit.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Call extends Pair<Callable,List<? extends CtrlPar>> implements Comparable<Call> {
    /**
     * Constructs a call of a given unit, with arguments.
     */
    public Call(Callable unit, List<? extends CtrlPar> args) {
        super(unit, args);
    }

    /**
     * Constructs a call of a given unit, without arguments.
     * This will construct wildcard arguments for the call.
     */
    public Call(Callable unit) {
        this(unit, createWildArgs(unit.getSignature()));
    }

    /** Returns the called unit. */
    public Callable getUnit() {
        return one();
    }

    /** Returns the called unit, cast to a {@link Rule}. */
    public Rule getRule() {
        return (Rule) one();
    }

    /** Returns the list of arguments. */
    public List<? extends CtrlPar> getArgs() {
        return two();
    }

    /** Indicates if this switch has any output variables. */
    public boolean hasOutVars() {
        return !getOutVars().isEmpty();
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
        Map<CtrlVar,Integer> outVars = new LinkedHashMap<CtrlVar,Integer>();
        Map<CtrlVar,Integer> inVars = new LinkedHashMap<CtrlVar,Integer>();
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

    /** Computes and inserts the host nodes to be used for constant value arguments. */
    public void initialise(HostFactory factory) {
        if (getArgs() != null) {
            for (CtrlPar arg : getArgs()) {
                arg.initialise(factory);
            }
        }
    }

    @Override
    public String toString() {
        return getUnit().getFullName() + Groove.toString(getArgs().toArray(), "(", ")", ", ");
    }

    @Override
    public int compareTo(Call o) {
        int result = getUnit().getFullName().compareTo(o.getUnit().getFullName());
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

    static private List<CtrlPar> createWildArgs(List<CtrlPar.Var> sig) {
        int count = sig.size();
        List<CtrlPar> result = new ArrayList<CtrlPar>(count);
        for (int i = 0; i < count; i++) {
            result.add(CtrlPar.wild());
        }
        return result;
    }
}
