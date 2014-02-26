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

import groove.grammar.Recipe;
import groove.graph.ALabel;
import groove.graph.Label;

import java.util.Map;

/**
 * A control label wraps a control call and a guard, consisting of a 
 * set of failure calls.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlLabel extends ALabel {
    /** 
     * Constructs a control label from a call and
     * a guard.
     * @param call the (non-{@code null}) control call in the label
     * @param guard the (non-{@code null}) guard of the control call
     * @param start flag indicating if this is the first call of a new action
     */
    public CtrlLabel(CtrlCall call, CtrlGuard guard, boolean start) {
        this(0, call, guard, start);
    }

    /** 
     * Constructs a control label from a call and
     * a guard.
     * @param number number of the label
     * @param call the (non-{@code null}) control call in the label
     * @param guard the (non-{@code null}) guard of the control call
     * @param start flag indicating if this is the first call of a new action
     */
    private CtrlLabel(int number, CtrlCall call, CtrlGuard guard, boolean start) {
        assert start || !call.isOmega();
        assert start || call.hasContext();
        this.call = call;
        this.recipe = call.getContext();
        this.start = start;
        this.guard.addAll(guard);
        this.number = number;
    }

    @Override
    public String text() {
        if (this.text == null) {
            this.text = computeText();
        }
        return this.text;
    }

    private String computeText() {
        StringBuilder result = new StringBuilder();
        result.append('t');
        result.append(getNumber());
        result.append(':');
        if (hasRecipe()) {
            result.append(getRecipe().getFullName());
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

    /** The text of the control label. */
    private String text;

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

    /** 
     * Flag indicating that this transition leaves a transient phase.
     */
    private final boolean start;

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

    /** Returns the label number. */
    public int getNumber() {
        return this.number;
    }

    /** The internal number of this label. */
    private final int number;

    /** Returns a renumbered copy of this label. */
    public CtrlLabel newLabel(int number) {
        return new CtrlLabel(number, getCall(), getGuard(), isStart());
    }

    @Override
    public int compareTo(Label obj) {
        if (!(obj instanceof CtrlLabel)) {
            return super.compareTo(obj);
        }
        CtrlLabel other = (CtrlLabel) obj;
        return getNumber() - other.getNumber();
    }

    /* The above equals and hashcode are correct in principle
     * but they cause too much overhead in CtrlAut.normalise.
     * As a consequence, normalisation does not minimise as much as it could
    @Override
    protected int computeHashCode() {
        final int prime = 31;
        int result = 0;
        result = prime * result + this.call.hashCode();
        result = prime * result + this.guard.hashCode();
        result =
            prime * result
                + ((this.recipe == null) ? 0 : this.recipe.hashCode());
        result = prime * result + (this.start ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CtrlLabel)) {
            return false;
        }
        CtrlLabel other = (CtrlLabel) obj;
        if (!this.call.equals(other.call)) {
            return false;
        }
        if (!this.guard.equals(other.guard)) {
            return false;
        }
        if (this.recipe == null) {
            if (other.recipe != null) {
                return false;
            }
        } else if (!this.recipe.equals(other.recipe)) {
            return false;
        }
        if (this.start != other.start) {
            return false;
        }
        return true;
    }
    */

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
        return new CtrlLabel(getNumber(), getCall(), newGuard, isStart());
    }
}
