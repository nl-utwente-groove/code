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

import groove.control.Call;
import groove.control.CallStack;
import groove.control.CtrlFrame;
import groove.control.CtrlVar;
import groove.control.Position;
import groove.control.template.Location;
import groove.control.template.Switch;
import groove.control.template.SwitchAttempt;
import groove.control.template.SwitchStack;
import groove.grammar.Recipe;
import groove.util.DefaultFixable;
import groove.util.Fixable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Run-time composed control location.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Frame implements Position<Frame,Step>, Fixable, CtrlFrame {
    /**
     * Possibly nested frame instantiating a given control stage.
     */
    Frame(Automaton ctrl, Location loc, SwitchStack callStack) {
        this.aut = ctrl;
        this.nr = ctrl.getFrames().size();
        // avoid sharing
        callStack = new SwitchStack(callStack);
        // pop the call stack until we have a non-final location or empty stack
        while (loc.isFinal() && !callStack.isEmpty()) {
            loc = callStack.pop().onFinish();
        }
        this.switchStack = callStack;
        this.location = loc;
        // assume that this is a prime frame;
        // if not, setPrime should be called afterwards
        this.primeFrame = this;
        this.pastAttempts = new HashSet<CallStack>();
    }

    /** Returns the containing control automaton. */
    public Automaton getAut() {
        return this.aut;
    }

    private final Automaton aut;

    /** 
     * Returns the number of this frame.
     * After a frame has been added to the automaton, 
     * the frame number uniquely identifies the frame.
     */
    public int getNumber() {
        return this.nr;
    }

    private final int nr;

    @Override
    public boolean isStart() {
        return getAut().getStart() == this;
    }

    /** Returns the call stack giving rise to this frame. */
    public SwitchStack getSwitchStack() {
        return this.switchStack;
    }

    private final SwitchStack switchStack;

    /** Returns the top control location instantiated by this frame. */
    public Location getLocation() {
        return this.location;
    }

    private final Location location;

    /** 
     * Sets the prime frame of this frame, as well as the set of attempts
     * since that frame.
     * The prime frame is the one from which this one was derived through
     * a sequence of verdict transitions.
     * @param prime the prime frame
     * @param pastAttempts the set of attempts since the prime
     */
    private void setPrime(Frame prime, Set<CallStack> pastAttempts) {
        assert !isFixed();
        assert this.primeFrame == this;
        this.primeFrame = prime;
        this.pastAttempts.addAll(pastAttempts);
    }

    @Override
    public Frame getPrime() {
        return this.primeFrame;
    }

    /** Indicates if this frame is its own prime. */
    public boolean isPrime() {
        return getPrime() == this;
    }

    private Frame primeFrame;

    /** Returns the set of attempts made since the
     * prime frame.
     */
    @Override
    public Set<CallStack> getPastAttempts() {
        return this.pastAttempts;
    }

    private Set<CallStack> pastAttempts;

    /** Returns the set of rule calls that have been tried since the prime frame. */
    public Set<Call> getPastCalls() {
        if (this.pastCalls == null) {
            Set<Call> result = this.pastCalls = new HashSet<Call>();
            for (CallStack attempt : getPastAttempts()) {
                result.add(attempt.peek());
            }
        }
        return this.pastCalls;
    }

    private Set<Call> pastCalls;

    @Override
    public Type getType() {
        if (this.type == null) {
            this.type = getLocation().getType();
        }
        return this.type;
    }

    /** The type of this frame. */
    private Type type;

    @Override
    public boolean isDead() {
        return getType() == Type.DEAD;
    }

    @Override
    public boolean isFinal() {
        return getType() == Type.FINAL;
    }

    @Override
    public boolean isTrial() {
        return getType() == Type.TRIAL;
    }

    @Override
    public StepAttempt getAttempt() {
        assert isFixed();
        if (this.attempt == null) {
            this.attempt = computeAttempt();
        }
        return this.attempt;
    }

    private StepAttempt attempt;

    /** Computes the attempt of this frame. */
    private StepAttempt computeAttempt() {
        SwitchAttempt locAttempt = getLocation().getAttempt();
        Set<CallStack> pastAttempts = new HashSet<CallStack>(getPastAttempts());
        List<Step> steps = new ArrayList<Step>();
        for (SwitchStack locStack : locAttempt) {
            pastAttempts.add(locStack.getCallStack());
            SwitchStack targetStack = new SwitchStack();
            targetStack.addAll(getSwitchStack());
            targetStack.addAll(locStack);
            Switch topCall = targetStack.pop();
            Frame onFinish = new Frame(getAut(), topCall.onFinish(), targetStack);
            steps.add(new Step(this, locStack, onFinish.normalise()));
        }
        Frame onSuccess = newFrame(locAttempt.onSuccess(), pastAttempts);
        Frame onFailure = newFrame(locAttempt.onFailure(), pastAttempts);
        StepAttempt result = new StepAttempt(onSuccess, onFailure);
        result.addAll(steps);
        return result;
    }

    @Override
    public boolean isRecipeStage() {
        return getSwitchStack().isRecipeStep();
    }

    @Override
    public Recipe getRecipe() {
        return getSwitchStack().getCallStack().getRecipe();
    }

    @Override
    public boolean isTransient() {
        return getDepth() > 0;
    }

    @Override
    public int getDepth() {
        return getSwitchStack().getDepth() + getLocation().getDepth();
    }

    @Override
    public boolean hasVars() {
        return getLocation().hasVars();
    }

    @Override
    public List<CtrlVar> getVars() {
        return getLocation().getVars();
    }

    /**
     * Constructs a frame for a given control location,
     * with the same prime frame and call stack as this frame. 
     */
    private Frame newFrame(Location loc, Set<CallStack> pastAttempts) {
        Frame result = new Frame(getAut(), loc, getSwitchStack());
        result.setPrime(getPrime(), pastAttempts);
        return result.normalise();
    }

    /** Fixes this frame and returns its canonical representative. */
    public Frame normalise() {
        setFixed();
        return getAut().addFrame(this);
    }

    @Override
    public int hashCode() {
        assert isFixed();
        final int prime = 31;
        // use identity of prime frame as it has already been normalised
        int result = (isPrime() ? 1237 : System.identityHashCode(this.primeFrame));
        result = prime * result + this.pastAttempts.hashCode();
        result = prime * result + this.location.hashCode();
        result = prime * result + this.switchStack.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        assert isFixed();
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Frame)) {
            return false;
        }
        Frame other = (Frame) obj;
        if (isPrime() ? !other.isPrime() : this.primeFrame != other.primeFrame) {
            return false;
        }
        if (!this.pastAttempts.equals(other.pastAttempts)) {
            return false;
        }
        if (!this.location.equals(other.location)) {
            return false;
        }
        if (!this.switchStack.equals(other.switchStack)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String result = getIdString();
        if (RICH_LABELS) {
            if (getDepth() > 0) {
                result += ", d" + getDepth();
            }
            if (isFinal()) {
                result += ", final";
            } else if (isDead()) {
                result += ", dead";
            }
            if (isPrime()) {
                result += "\nPrime";
            } else {
                result += "\nPrime: " + getPrime().getIdString();
                if (VERY_RICH_LABELS) {
                    result += "\nTried:";
                    for (CallStack tried : getPastAttempts()) {
                        result += " " + tried.toString();
                    }
                }
            }
        }
        return result;
    }

    /** Returns the concatenation of the call stack locations. */
    public String getIdString() {
        StringBuilder result = new StringBuilder();
        String callerName = null;
        for (Switch swit : getSwitchStack()) {
            if (callerName == null) {
                result.append("c");
            } else {
                result.append('/');
                result.append(callerName);
                result.append('.');
            }
            result.append(swit.onFinish().getNumber());
            callerName = swit.getCall().getUnit().getLastName();
        }
        if (callerName == null) {
            result.append("c");
        } else {
            result.append('/');
            result.append(callerName);
            result.append('.');
        }
        result.append(getLocation().getNumber());
        return result.toString();
    }

    @Override
    public boolean setFixed() {
        return this.fixable.setFixed();
    }

    @Override
    public boolean isFixed() {
        return this.fixable.isFixed();
    }

    @Override
    public void testFixed(boolean fixed) {
        this.fixable.testFixed(fixed);
    }

    private final DefaultFixable fixable = new DefaultFixable();

    private final static boolean RICH_LABELS = false;
    private final static boolean VERY_RICH_LABELS = false;
}
