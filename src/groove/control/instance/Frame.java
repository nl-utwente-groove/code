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

/**
 * Run-time composed control location.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Frame extends ANode implements Position<Frame> {
    /**
     * Possibly nested frame instantiating a given control stage.
     * @param callStack switch from which the template of this frame was called
     * @param nextFrame successor frame after this stage terminates; one level higher up
     * in the call hierarchy
     * @param alsoFrame next frame if the attempt of this one succeeds; may be {@code null}
     * @param elseFrame next frame if the attempt of this one fails; may be {@code null}
     */
    Frame(Automaton ctrl, Stage stage, CallStack callStack, Frame nextFrame, Frame alsoFrame,
            Frame elseFrame) {
        super(ctrl.getNextFrameNr());
        this.aut = ctrl;
        this.stage = stage;
        this.callStack = new CallStack(callStack);
        this.nextFrame = nextFrame;
        this.alsoFrame = alsoFrame;
        this.elseFrame = elseFrame;
        this.depth = (nextFrame == null ? 0 : nextFrame.getDepth()) + stage.getDepth();
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

    /** Indicates that this is a nested frame. */
    public boolean isNested() {
        return !getCallStack().isEmpty();
    }

    /** Returns the switch from which the current template was called. */
    public CallStack getCallStack() {
        return this.callStack;
    }

    private final CallStack callStack;

    /** 
     * Returns the (possibly {@code null}) parent frame.
     */
    public Frame getNext() {
        return this.nextFrame;
    }

    private final Frame nextFrame;

    /** Returns the also-branch of this frame, if it is not a top-level frame. */
    public Frame getAlso() {
        return this.alsoFrame;
    }

    private final Frame alsoFrame;

    /** Returns the else-branch of this frame, if it is not a top-level frame. */
    public Frame getElse() {
        return this.elseFrame;
    }

    private final Frame elseFrame;

    /** 
     * Returns the (possibly {@code null}) switch of the instantiated stage.
     */
    public Switch getSwitch() {
        return getStage().getAttempt();
    }

    public Type getType() {
        // we want this to work also for non-normal frames
        if (getNext() == null) {
            return getStage().getType();
        } else {
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
        }
    }

    public boolean isDead() {
        return getType() == Type.DEAD;
    }

    public boolean isFinal() {
        return getType() == Type.FINAL;
    }

    public boolean isTrial() {
        return getType() == Type.TRIAL;
    }

    public Step getAttempt() {
        if (this.attempt == null) {
            this.attempt = computeAttempt();
            getAut().addEdge(this.attempt);
            getAut().addEdge(Step.newStep(this.attempt, true));
            getAut().addEdge(Step.newStep(this.attempt, false));
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
            elseF = getAut().newFrame(swit.onFailure(), stack, nextF, alsoF, elseF);
            alsoF = getAut().newFrame(swit.onSuccess(), stack, nextF, alsoF, alsoF);
            nextF = getAut().newFrame(swit.onFinish(), stack, nextF, null, null);
            isProcedure = swit.getKind().isProcedure();
            if (isProcedure) {
                stack.add(swit);
                Procedure proc = (Procedure) swit.getCall().getUnit();
                swit = proc.getTemplate().getStart().getFirstStage().getAttempt();
            }
        } while (isProcedure);
        Frame onSuccess = getAut().addFrame(alsoF);
        Frame onFailure = getAut().addFrame(elseF);
        Frame onFinish = getAut().addFrame(nextF);
        return new Step(this, swit, stack, onFinish, onSuccess, onFailure);
    }

    public int getDepth() {
        return this.depth;
    }

    private final int depth;

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

    /** Returns the normalised version of this frame. */
    public Frame normalise() {
        Frame result = null;
        if (getNext() == null || getStage().isTrial()) {
            result = this;
        } else {
            switch (getStage().getType()) {
            case DEAD:
                result = getElse().normalise();
                break;
            case FINAL:
                result = getNext().or(getAlso()).normalise();
                break;
            default:
                assert false;
            }
        }
        result.testNormal();
        return result;
    }

    /** Constructs the disjunction of this frame and another. */
    public Frame or(Frame other) {
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
            if (getNext() == null) {
                result = getAut().newFrame(getStage(), getCallStack(), null, other, other);
            } else {
                result =
                    getAut().newFrame(getStage(), getCallStack(), getNext(), getAlso().or(other),
                        getElse().or(other));
            }
            break;
        default:
            assert false;
            result = null;
        }
        return result;
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
        if (getNext() != null) {
            String nextString = getNext().toString();
            if (getNext().getNext() != null) {
                nextString = "(" + nextString + ")";
            }
            String alsoString = getAlso().toString();
            if (getAlso().getNext() != null) {
                alsoString = "(" + alsoString + ")";
            }
            String elseString = getElse().toString();
            if (getElse().getNext() != null) {
                elseString = "(" + elseString + ")";
            }
            result = result + "?" + nextString + "&" + alsoString + ":" + elseString;
        }
        if (this == getAut().getStart()) {
            result = "S::" + result;
        }
        return result;
    }
}
