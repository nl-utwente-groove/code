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

import groove.graph.AbstractLabel;
import groove.graph.Label;
import groove.trans.Recipe;

import java.util.Map;

/**
 * A control label wraps a control call and a guard, consisting of a 
 * set of failure calls.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlLabel extends AbstractLabel {
    /** 
     * Constructs a control label from a call and
     * a guard.
     * @param call the (non-{@code null}) control call in the label
     * @param guard the (non-{@code null}) guard of the control call
     * @param recipe the (optional) recipe of which this label is part
     * @param start flag indicating if this is the first call of a new action
     */
    public CtrlLabel(CtrlCall call, CtrlGuard guard, Recipe recipe,
            boolean start) {
        this(0, call, guard, recipe, start);
    }

    /** 
     * Constructs a control label from a call and
     * a guard.
     * @param number number of the label
     * @param call the (non-{@code null}) control call in the label
     * @param guard the (non-{@code null}) guard of the control call
     * @param recipe the (optional) recipe of which this label is part
     * @param start flag indicating if this is the first call of a new action
     */
    private CtrlLabel(int number, CtrlCall call, CtrlGuard guard,
            Recipe recipe, boolean start) {
        assert start || !call.isOmega();
        assert start || recipe != null;
        this.call = call;
        this.recipe = recipe;
        this.start = start;
        this.guard.addAll(guard);
        this.number = number;
    }

    @Override
    public String text() {
        StringBuilder result = new StringBuilder();
        result.append('t');
        result.append(getNumber());
        result.append(':');
        if (hasRecipe()) {
            result.append(getRecipe());
            result.append('/');
        }
        if (!this.guard.isEmpty()) {
            result.append('[');
            boolean first = true;
            for (CtrlTransition guard : this.guard) {
                if (first) {
                    first = false;
                } else {
                    result.append(',');
                }
                result.append('t');
                result.append(guard.getNumber());
            }
            result.append(']');
        }
        result.append(getCall().toString());
        return result.toString();
    }

    /** Returns the rule wrapped into this label. */
    public final CtrlCall getCall() {
        return this.call;
    }

    /** The rule call wrapped in this control label. */
    private final CtrlCall call;

    /** Returns the set of failure transitions wrapped into this label. */
    public final CtrlGuard getGuard() {
        return this.guard;
    }

    /** Guard of this label, consisting of a list of failure transitions. */
    private final CtrlGuard guard = new CtrlGuard();

    /** 
     * Indicates whether this label starts a new action.
     */
    public boolean isStart() {
        return this.start;
    }

    /** Returns the name of the recipe of which this label is part, if any. */
    public Recipe getRecipe() {
        return this.recipe;
    }

    /** Indicates if this control label is part of a recipe. */
    public boolean hasRecipe() {
        return getRecipe() != null;
    }

    /** 
     * Name of the encompassing recipe, if any.
     */
    private final Recipe recipe;

    /** 
     * Flag indicating that this transition leaves a transient phase.
     */
    private final boolean start;

    /** Returns the label number. */
    public int getNumber() {
        return this.number;
    }

    /** The internal number of this label. */
    private final int number;

    /** Returns a renumbered copy of this label. */
    public CtrlLabel newLabel(int number) {
        return new CtrlLabel(number, getCall(), getGuard(), getRecipe(),
            isStart());
    }

    @Override
    public int compareTo(Label obj) {
        if (!(obj instanceof CtrlLabel)) {
            return super.compareTo(obj);
        }
        CtrlLabel other = (CtrlLabel) obj;
        return getNumber() - other.getNumber();
        //        int result = getCall().toString().compareTo(other.getCall().toString());
        //        if (result == 0) {
        //            result = getGuard().compareTo(other.getGuard());
        //        }
        //        if (result == 0) {
        //            int myRecipe = hasRecipe() ? 1 : 0;
        //            int hisRecipe = other.hasRecipe() ? 1 : 0;
        //            result = myRecipe - hisRecipe;
        //            if (result == 0 && hasRecipe()) {
        //                result = getRecipe().compareTo(other.getRecipe());
        //            }
        //        }
        //        if (result == 0) {
        //            int myStart = isStart() ? 1 : 0;
        //            int hisStart = other.isStart() ? 1 : 0;
        //            result = myStart - hisStart;
        //        }
        //        if (result == 0) {
        //            result = getNumber() - other.getNumber();
        //        }
        //        return result;
    }

    /** 
     * Returns a copy of this label in which the guard is
     * transformed and possibly extended.
     * @param map optional mapping under which both the guard of this label
     * and the additional guards are transformed
     * @param guard optional additional guarding transitions
     */
    public CtrlLabel newLabel(Map<CtrlTransition,CtrlTransition> map,
            CtrlGuard guard) {
        CtrlGuard newGuard = getGuard().newGuard(map);
        if (guard != null) {
            newGuard.addAll(guard);
        }
        return new CtrlLabel(getNumber(), getCall(), newGuard, getRecipe(),
            isStart());
    }
}
