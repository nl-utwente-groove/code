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
package nl.utwente.groove.control.instance;

import static nl.utwente.groove.util.LazyFactory.lazyFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.Assignment;
import nl.utwente.groove.control.Attempt;
import nl.utwente.groove.control.Call;
import nl.utwente.groove.control.CtrlVar;
import nl.utwente.groove.control.NestedCall;
import nl.utwente.groove.control.template.NestedSwitch;
import nl.utwente.groove.control.template.Switch;
import nl.utwente.groove.grammar.Callable;
import nl.utwente.groove.grammar.Recipe;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.util.LazyFactory;

/**
 * Run-time control step, instantiating a control edge.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class Step implements Attempt.Stage<Frame,Step>, Comparable<Step> {
    /**
     * Constructs a step from the given parameters.
     * @param source source frame for the step
     * @param newSwitches stack of new switches invoked from the source frame
     * @param onFinish target frame for the step
     */
    public Step(Frame source, NestedSwitch newSwitches, Frame onFinish) {
        assert newSwitches.getInner().getUnit().getKind() == Callable.Kind.RULE;
        this.swt = new NestedSwitch(newSwitches);
        this.onFinish = onFinish;
        this.source = source;
    }

    /** Returns the source frame of this step. */
    public Frame getSource() {
        return this.source;
    }

    private final Frame source;

    @Override
    public Frame onFinish() {
        return this.onFinish;
    }

    private final Frame onFinish;

    /** Returns the contextual nested switch within which this step takes place. */
    public NestedSwitch getContext() {
        return getSource().getContext();
    }

    /** Convenience method to return the top switch of this step. */
    public Switch getInnerSwitch() {
        return getSwitch().getInner();
    }

    @Override
    public Call getInnerCall() {
        return getSwitch().getInnerCall();
    }

    @Override
    public int getTransience() {
        return getSwitch().getTransience();
    }

    /** Returns the number of levels by which the call stack depth changes from source
     * to target frame. */
    public int getCallDepthChange() {
        return onFinish().getContext().size() - getContext().size();
    }

    /** Returns the stack of switches in the source frame, extended with the ones
     * entered by this step. */
    public final NestedSwitch getSwitch() {
        return this.swt;
    }

    private NestedSwitch swt;

    @Override
    public NestedCall getCall() {
        return getSwitch().getCall();
    }

    /** Indicates if this step is part of an atomic block. */
    public boolean isPartial() {
        return getSource().isTransient() || onFinish().isTransient();
    }

    /** Indicates if this step is the initial step of a recipe. */
    public boolean isInitial() {
        // if a recipe step starts in a non-recipe frame, it must be
        // the initial step of a recipe
        return isInternal() && !getSource().isInternal();
    }

    /** Indicates if this step is part of a recipe.
     * @return {@code true} if and only if {@link #getRecipe()} is non-{@code null}
     * @see #getRecipe()
     */
    public boolean isInternal() {
        return getContext().inRecipe() || getCall().inRecipe();
    }

    /**
     * Returns the outermost recipe of which this step is a part, if any.
     * @see #isInternal()
     */
    public Optional<Recipe> getRecipe() {
        var result = getContext().getRecipe();
        return result.isPresent()
            ? result
            : getCall().getRecipe();
    }

    /** Convenience method to return the called rule of this step. */
    public final Rule getRule() {
        return getInnerCall().getRule();
    }

    /** Returns the mapping of output variables to argument positions of the called unit. */
    public Map<CtrlVar,@Nullable Integer> getOutVars() {
        return getInnerCall().getOutVars();
    }

    /**
     * Indicates if the step may cause modifications in the control state.
     * This is the case if the (prime) source and target of this step differ,
     * or the call has out-parameters.
     */
    public boolean isModifying() {
        return getSource().getPrime() != onFinish() || getInnerCall().hasOutVars();
    }

    /** Returns an assignment to the parameters of the inner call of this step,
     * based on the source variables of the outer call.
     */
    public Assignment getParAssign() {
        return this.assignSource2Par.get();
    }

    /** Lazily computed assignment to the parameters of the inner call of this step,
     * based on the source variables of the outer call.
     */
    private final Supplier<Assignment> assignSource2Par
        = LazyFactory.instance(this::computeParAssign);

    /** Computes the value for {@link #assignSource2Par}. */
    private Assignment computeParAssign() {
        var switchIter = getSwitch().outIterator();
        var result = switchIter.next().assignSource2Par();
        while (switchIter.hasNext()) {
            result = result.after(switchIter.next().assignSource2Init());
        }
        return result;
    }

    /**
     * Computes the call stack change for this step that creates the initial target stack.
     * Output parameters of the outer calls as as yet unknown and hence not yet entered.
     * For all but the outer and inner switch of the step, the assignment pushes a new level onto the
     * call stack; for the outer switch, it modifies the call frame at the current top of the stack.
     * All (modified or pushed) levels are assignments to the target variables of the
     * respective switch.
     */
    public CallStackChange changeOnEnter() {
        NestedSwitch swt = getSwitch();
        Assignment[] result = new Assignment[swt.size()];
        Assignment sourceAssign = Assignment.identity(getSource().getVars());
        var iter = swt.iterator();
        int i = 0;
        while (iter.hasNext()) {
            Switch s = iter.next();
            result[i] = s.assignSource2Target().after(sourceAssign);
            if (iter.hasNext()) {
                sourceAssign = s.assignSource2Init().after(sourceAssign);
                i++;
            }
        }
        return CallStackChange.push(result);
    }

    /**
     * Returns the list of call stack changes involved in applying this step.
     * These consist of pushes due to fresh
     * procedure calls, followed by the action of this step, followed by pops due to
     * procedures explicitly exited by this step.
     */
    public List<CallStackChange> getApplyChanges() {
        return this.applyChanges.get();
    }

    private Supplier<List<CallStackChange>> applyChanges = lazyFactory(this::computeApplyChanges);

    /** Computes the value of {@link #applyChanges}. */
    private List<CallStackChange> computeApplyChanges() {
        List<CallStackChange> result = new ArrayList<>();
        // add modification and push actions for every successive call on the
        // stack of entered calls
        result.add(changeOnEnter());
        // add exit actions for the calls that are finished;
        // those might include calls from the context
        var exitCount
            = getSwitch().size() + getSource().getContext().size() - onFinish().getNestingDepth();
        var outIter = Stream
            .concat(getSwitch().outStream(), getSource().getContext().outStream())
            .iterator();
        var exit = outIter.next().onFinish();
        while (result.size() < exitCount) {
            var caller = outIter.next();
            result.add(caller.assignFinal2Target(exit).toPop());
            exit = caller.onFinish();
        }
        return result;
    }

    @Override
    public int compareTo(Step other) {
        int result = getSource().getNumber() - other.getSource().getNumber();
        if (result != 0) {
            return result;
        }
        result = getSwitch().compareTo(other.getSwitch());
        if (result != 0) {
            return result;
        }
        result = onFinish().getNumber() - other.onFinish().getNumber();
        return result;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        return this == other;
    }

    @Override
    public String toString() {
        return "Step " + this.source + "--" + this.swt + "-> " + this.onFinish;
    }
}
