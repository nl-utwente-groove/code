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

import groove.util.Groove;
import groove.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Call of a callable unit.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Call extends Pair<Callable,List<? extends CtrlPar>> {
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
        this(unit, createWildArgs(unit.getSignature().size()));
    }

    /** Returns the called unit. */
    public Callable getUnit() {
        return one();
    }

    /** Returns the list of arguments. */
    public List<? extends CtrlPar> getArgs() {
        return two();
    }

    @Override
    public String toString() {
        return getUnit().getFullName()
            + Groove.toString(getArgs().toArray(), "(", ")", ", ");
    }

    static private List<CtrlPar> createWildArgs(int count) {
        List<CtrlPar> result = new ArrayList<CtrlPar>(count);
        for (int i = 0; i < count; i++) {
            result.add(new CtrlPar.Wild());
        }
        return result;
    }
}
