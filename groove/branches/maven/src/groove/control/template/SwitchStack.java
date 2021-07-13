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
package groove.control.template;

import java.util.Stack;

import groove.control.Attempt;
import groove.control.Call;
import groove.control.CallStack;
import groove.grammar.Recipe;
import groove.grammar.host.HostFactory;

/**
 * Stack of switches, corresponding to nested procedure calls.
 * @author Arend Rensink
 * @version $Revision $
 */
public class SwitchStack extends Stack<Switch>
    implements Attempt.Stage<Location,SwitchStack>, Comparable<SwitchStack>, Relocatable {
    /** Constructs a copy of a given stack. */
    public SwitchStack(SwitchStack other) {
        addAll(other);
    }

    /** Constructs an initially empty stack. */
    public SwitchStack() {
        // empty
    }

    /** Returns the bottom of the stack. */
    public Switch getBottom() {
        return get(0);
    }

    /** Returns the original call, at the bottom of the stack. */
    public Call getBottomCall() {
        return getBottom().getCall();
    }

    @Override
    public Call getRuleCall() {
        return peek().getCall();
    }

    @Override
    public Location onFinish() {
        return getBottom().onFinish();
    }

    @Override
    public int getTransience() {
        if (this.depth < 0) {
            this.depth = computeDepth();
        }
        return this.depth;
    }

    private int computeDepth() {
        int result = 0;
        for (Switch swit : this) {
            this.depth += swit.getTransience();
        }
        return result;
    }

    private int depth = -1;

    /** Returns the call stack corresponding to this switch stack. */
    @Override
    public CallStack getCallStack() {
        if (this.callStack == null) {
            this.callStack = new CallStack(stream().map(s -> s.getCall()));
        }
        return this.callStack;
    }

    private CallStack callStack;

    /** Indicates if this step is part of a recipe. */
    public boolean inRecipe() {
        return getCallStack().inRecipe();
    }

    /** Returns the recipe of which this is a step, if any. */
    public Recipe getRecipe() {
        return getCallStack().getRecipe();
    }

    @Override
    public SwitchStack relocate(Relocation map) {
        SwitchStack result = new SwitchStack();
        for (int i = 0; i < size(); i++) {
            Switch newSwitch = get(i).relocate(map);
            result.add(newSwitch);
        }
        return result;
    }

    /** Computes and inserts the host nodes to be used for constant value arguments. */
    public void initialise(HostFactory factory) {
        for (Call call : getCallStack()) {
            call.initialise(factory);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SwitchStack)) {
            return false;
        }
        SwitchStack other = (SwitchStack) obj;
        if (size() != other.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (!get(i).equals(other.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        for (int i = 0; i < size(); i++) {
            result = prime * result + get(i).hashCode(false);
        }
        return result;
    }

    @Override
    public int compareTo(SwitchStack o) {
        int result = size() - o.size();
        for (int i = 0; result == 0 && i < size(); i++) {
            result = get(i).compareTo(o.get(i));
        }
        return result;
    }
}
