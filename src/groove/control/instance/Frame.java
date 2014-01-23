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

import groove.control.Position;
import groove.control.Procedure;
import groove.control.template.Location;
import groove.control.template.Stage;
import groove.control.template.Switch;
import groove.graph.ANode;
import groove.util.DefaultFixable;
import groove.util.Fixable;

/**
 * Run-time composed control location.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Frame extends ANode implements Position<Frame>, Fixable {
    /**
     * Possibly nested frame instantiating a given control stage.
     */
    Frame(Automaton ctrl, Stage stage) {
        super(ctrl.getNextFrameNr());
        this.aut = ctrl;
        this.stage = stage;
        this.callStack = new CallStack();
        this.depth = stage.getDepth();
    }

    /** Returns the containing control automaton. */
    public Automaton getAut() {
        return this.aut;
    }

    private final Automaton aut;

    /** Returns the control location that this frame instantiates. */
    public Location getLocation() {
        return getStage().getLocation();
    }

    /** Returns the control stage that this frame instantiates. */
    public Stage getStage() {
        return this.stage;
    }

    private final Stage stage;

    /** 
     * Sets the prime frame of this frame.
     * The prime frame is the one from which this one was derived through
     * a sequence of verdict transitions.
     * @param prime the prime frame; if {@code null}, this frame is its own prime
     */
    public void setPrime(Frame prime) {
        assert !isFixed();
        assert this.primeFrame == null;
        this.primeFrame = prime == null ? this : prime;
    }

    /** 
     * Returns the prime frame of this frame.
     * The prime frame is the one from which this one was derived through
     * a sequence of verdict transitions.
     */
    public Frame getPrime() {
        return this.primeFrame;
    }

    /** Indicates if this frame is its own prime. */
    public boolean isPrime() {
        return getPrime() == this;
    }

    private Frame primeFrame;

    /** Indicates if the subframes of this frame are set. */
    private boolean hasSubFrames() {
        boolean result = getNext() != null;
        assert result == (getAlso() != null);
        assert result == (getElse() != null);
        assert result || getCallStack().isEmpty();
        return result;
    }

    /** Sets the values of the next/also/else frames and call stack. */
    public void setSubFrames(Frame nextF, Frame alsoF, Frame elseF, CallStack stack) {
        assert !isFixed();
        this.nextFrame = nextF == null ? getFinal() : nextF;
        this.alsoFrame = alsoF == null ? getDead(getStage().getDepth()) : alsoF;
        this.elseFrame = elseF == null ? getDead(getStage().getDepth()) : elseF;
        this.callStack.addAll(stack);
        for (Switch swit : stack) {
            this.depth += swit.source().getDepth();
        }
    }

    /** 
     * Returns the (possibly {@code null}) parent frame.
     */
    public Frame getNext() {
        return this.nextFrame;
    }

    private Frame nextFrame;

    /** Returns the also-branch of this frame, if it is not a top-level frame. */
    public Frame getAlso() {
        return this.alsoFrame;
    }

    private Frame alsoFrame;

    /** Returns the else-branch of this frame, if it is not a top-level frame. */
    public Frame getElse() {
        return this.elseFrame;
    }

    private Frame elseFrame;

    /** Returns the switch from which the current template was called. */
    public CallStack getCallStack() {
        return this.callStack;
    }

    private final CallStack callStack;

    /** 
     * Returns the (possibly {@code null}) switch of the instantiated stage.
     */
    public Switch getSwitch() {
        return getStage().getAttempt();
    }

    @Override
    public Type getType() {
        // we want this to work also for non-normal frames
        if (hasSubFrames()) {
            switch (getStage().getType()) {
            case DEAD:
                return getElse().getType();
            case TRIAL:
                return Type.TRIAL;
            case FINAL:
                switch (getNext().getType()) {
                case DEAD:
                    return getAlso().getType();
                case FINAL:
                    if (getAlso().isDead()) {
                        return Type.FINAL;
                    } else {
                        return getAlso().getType();
                    }
                case TRIAL:
                    return Type.TRIAL;
                default:
                    assert false;
                    return null;
                }
            default:
                assert false;
                return null;
            }
        } else {
            return getStage().getType();
        }
    }

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
    public Step getAttempt() {
        assert isFixed();
        if (this.attempt == null) {
            this.attempt = computeAttempt();
            getAut().addEdgeContext(this.attempt);
            getAut().addEdgeContext(Step.newStep(this.attempt, true));
            getAut().addEdgeContext(Step.newStep(this.attempt, false));
        }
        return this.attempt;
    }

    private Step attempt;

    /** Computes the attempt of this frame. */
    private Step computeAttempt() {
        // recursively build the next/also/else frames
        Frame nextF = getNext();
        Frame alsoF = getAlso();
        Frame elseF = getElse();
        // construct the call stack
        CallStack stack = new CallStack(getCallStack());
        Switch swit = getStage().getAttempt();
        boolean isProcedure;
        do {
            // the order of the next assignments is important
            // otherwise they will interfere
            elseF = newFrame(swit.onFailure(), stack, nextF, alsoF, elseF);
            alsoF = newFrame(swit.onSuccess(), stack, nextF, alsoF, alsoF);
            nextF = newFrame(swit.onFinish(), stack, nextF, null, null);
            isProcedure = swit.getKind().isProcedure();
            if (isProcedure) {
                stack.add(swit);
                Procedure proc = (Procedure) swit.getCall().getUnit();
                swit = proc.getTemplate().getStart().getFirstStage().getAttempt();
            }
        } while (isProcedure);
        Frame onSuccess = alsoF.normalise(getPrime());
        Frame onFailure = elseF.normalise(getPrime());
        Frame onFinish = nextF.normalise(null);
        return new Step(this, swit, stack, onFinish, onSuccess, onFailure);
    }

    @Override
    public int getDepth() {
        return this.depth;
    }

    private int depth;

    /** Tests if this is a normal frame.
     * A frame is normal if the next/also/else frames are simultaneously either
     * {@code null} or non-{@code null}; in the latter case, moreover, the stage
     * is a trial stage.
     * Only normal frames should ever be constructed.
     * @throws IllegalStateException if the frame is not normal
     */
    boolean testNormal() throws IllegalStateException {
        String message = null;
        if (getNext() == null) {
            if (getAlso() != null) {
                message = "next is null but also is not";
            } else if (getElse() != null) {
                message = "next is null but else is not";
            }
        } else {
            if (getAlso() == null) {
                message = "also is null but next is not";
            } else if (getElse() == null) {
                message = "else is null but next is not";
            } else if (!getStage().isTrial()) {
                message = "nested location " + getStage().getLocation() + " is not a trial";
            }
        }
        if (message != null) {
            throw new IllegalStateException(String.format("Frame %s is not normal: %s", this,
                message));
        }
        return true;
    }

    /** Returns a canonical final frame. 
     */
    public Frame getFinal() {
        return newFrame(getAut().getTemplate().getFinal());
    }

    /** Returns a canonical deadlocked frame at given transient depth. 
     */
    public Frame getDead(int depth) {
        return newFrame(getAut().getTemplate().getDead(depth));
    }

    /**
     * Fixes this frame and returns the normalised, primed version. 
     * @param prime the prime frame for the normalised frame; if {@code null}, the new frame
     * is to be its own prime
     */
    public Frame normalise(Frame prime) {
        setFixed();
        Frame result = null;
        if (hasSubFrames()) {
            switch (getStage().getType()) {
            case TRIAL:
                result = newFrame(prime);
                break;
            case DEAD:
                result = getElse().normalise(prime);
                break;
            case FINAL:
                result = getNext().or(getAlso()).normalise(prime);
                break;
            default:
                assert false;
            }
        } else {
            // clone the frame and set the prime
            result = newFrame(prime);
        }
        result.testNormal();
        return result;
    }

    /** Constructs the disjunction of this frame and another. */
    private Frame or(Frame other) {
        assert other != null;
        assert getDepth() == other.getDepth();
        Frame result;
        switch (getType()) {
        case DEAD:
            result = other;
            break;
        case FINAL:
            if (other.isTrial()) {
                result = other.or(this);
            } else {
                result = this;
            }
            break;
        case TRIAL:
            if (hasSubFrames()) {
                result =
                    newFrame(getStage(), getCallStack(), getNext(), getAlso().or(other),
                        getElse().or(other));
            } else {
                result = newFrame(getStage(), getCallStack(), null, other, other);
            }
            break;
        default:
            assert false;
            result = null;
        }
        return result;
    }

    /** 
     * Clones this frame and sets the prime frame.
     * @param prime the prime frame for the new frame; if {@code null}, the new frame
     * is to be its own prime
     */
    private Frame newFrame(Frame prime) {
        Frame result = new Frame(getAut(), getStage());
        if (hasSubFrames()) {
            result.setSubFrames(getNext(), getAlso(), getElse(), getCallStack());
        }
        result.setPrime(prime);
        return result.canonical();
    }

    /** Constructs the initial frame for a given control location (without sub-frames). 
     */
    private Frame newFrame(Location loc) {
        Frame result = new Frame(getAut(), loc.getFirstStage());
        return result.canonical();
    }

    /** 
     * Constructs a new, primeless frame with given sub-frames.
     */
    private Frame newFrame(Stage stage, CallStack callStack, Frame nextF, Frame alsoF, Frame elseF) {
        Frame result = new Frame(getAut(), stage);
        result.setSubFrames(nextF, alsoF, elseF, callStack);
        return result.canonical();
    }

    /** Fixes this frame and returns its canonical representative. */
    private Frame canonical() {
        setFixed();
        return getAut().getFramePool().canonical(this);
    }

    @Override
    protected String getToStringPrefix() {
        return "c";
    }

    @Override
    protected int computeHashCode() {
        final int prime = 31;
        int result = System.identityHashCode(this.alsoFrame);
        result = prime * result + System.identityHashCode(this.elseFrame);
        result = prime * result + System.identityHashCode(this.nextFrame);
        result = prime * result + (isPrime() ? 1237 : System.identityHashCode(this.primeFrame));
        result = prime * result + this.callStack.hashCode();
        result = prime * result + this.stage.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Frame)) {
            return false;
        }
        Frame other = (Frame) obj;
        if (this.alsoFrame != other.alsoFrame) {
            return false;
        }
        if (this.elseFrame != other.elseFrame) {
            return false;
        }
        if (this.nextFrame != other.nextFrame) {
            return false;
        }
        if (this.primeFrame != other.primeFrame && this.isPrime() != other.isPrime()) {
            return false;
        }
        if (!this.stage.equals(other.stage)) {
            return false;
        }
        if (!this.callStack.equals(other.callStack)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return toString(false) + "\n" + getIdString();
    }

    /** Implements the functionality of {@link #toString()}.
     * @param nested if {@code true}, the string will be nested in another {@link #toString()} call.
     */
    private String toString(boolean nested) {
        String result = null;
        switch (getType()) {
        case DEAD:
            result = "D" + (getDepth() > 0 ? "" + getDepth() : "");
            break;
        case FINAL:
            result = "E";
            break;
        case TRIAL:
            result = getSwitch().getCall().toString();
            break;
        default:
            assert false;
        }
        if (hasSubFrames()) {
            String nextString = getNext().toString(true);
            String alsoString = getAlso().toString(true);
            String elseString = getElse().toString(true);
            result = result + "?" + nextString + "&" + alsoString + ":" + elseString;
        }
        if (this == getAut().getStart()) {
            result = "S::" + result;
        }
        if (nested && getNext() != null) {
            result = "(" + result + ")";
        }
        return result;
    }

    /** Returns the concatenation of the call stack locations. */
    public String getIdString() {
        StringBuilder result = new StringBuilder("loc:");
        String callerName = null;
        for (Switch swit : getCallStack()) {
            if (callerName != null) {
                result.append('/');
                result.append(callerName);
                result.append('.');
            }
            result.append(swit.source().getNumber());
            callerName = swit.getCall().getUnit().getLastName();
        }
        if (callerName != null) {
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
}
