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
import groove.control.template.Stage;
import groove.control.template.StageSwitch;
import groove.control.template.Switch;
import groove.control.template.TemplatePosition;
import groove.graph.ANode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Run-time composed control location.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Frame extends ANode implements Position<Frame> {
    /**
     * Nested frame instantiating a given control location.
     * @param parent successor frame after this terminates 
     * @param alsoFrame next frame to attempt if the call of this one succeeds
     * @param elseFrame next frame to attempt if the call of this one fails
     */
    private Frame(Instance ctrl, Stage slot, Frame parent, Frame alsoFrame,
            Frame elseFrame) {
        super(ctrl.nodeCount());
        assert (!slot.isFinal() && !slot.isDead()) || parent == null;
        this.ctrl = ctrl;
        this.stage = slot;
        this.nextFrame = parent;
        this.alsoFrame = alsoFrame;
        this.elseFrame = elseFrame;
        this.depth = (parent == null ? 0 : parent.getDepth()) + slot.getDepth();
    }

    /** Returns the containing control instance. */
    public Instance getCtrl() {
        return this.ctrl;
    }

    private final Instance ctrl;

    /** Returns the control position that this frame instantiates. */
    public TemplatePosition getPosition() {
        return getStage().getPosition();
    }

    /** Returns the control stage that this frame instantiates. */
    public Stage getStage() {
        return this.stage;
    }

    private final Stage stage;

    /** 
     * Returns the (possibly {@code null}) parent frame.
     */
    public Frame getNext() {
        return this.nextFrame;
    }

    /** 
     * Returns the ancestor frame according to a given generation.
     */
    public Frame getAncestor(int generation) {
        assert generation >= 0 && generation <= getDepth();
        if (generation == 0) {
            return this;
        } else {
            return this.nextFrame.getAncestor(generation - 1);
        }
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
    public StageSwitch getSwitch() {
        return getStage().getAttempt();
    }

    public Type getType() {
        return getStage().getType();
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
        }
        return this.attempt;
    }

    private Step computeAttempt() {
        StageSwitch sswit = getStage().getAttempt();
        List<StageSwitch> entered = new ArrayList<StageSwitch>();
        // depth of the call stack
        Stack<StageSwitch> stack = getCallStack();
        while (sswit.getCall().getUnit() instanceof Procedure) {
            Procedure proc = (Procedure) sswit.getCall().getUnit();
            entered.add(sswit);
            stack.add(sswit);
            sswit =
                proc.getTemplate().getStart().getStage(0, false).getAttempt();
        }
        Switch call = sswit.getSwitch();
        List<StageSwitch> exited = new ArrayList<StageSwitch>();
        while (sswit.target().isFinal() && !stack.isEmpty()) {
            sswit = stack.pop();
            exited.add(sswit);
        }
        Frame target =
            getCtrl().addFrame(sswit.target(), this.nextFrame, this.alsoFrame,
                this.elseFrame);
        // add frames for the entered calls that were not also exited
        for (int i = 0; i < entered.size() - exited.size(); i++) {
            sswit = entered.get(i);
            target =
                getCtrl().addFrame(sswit.target(), target, this.alsoFrame,
                    this.elseFrame);
        }
        return new Step(call, this, target, entered, exited);
    }

    private Step attempt;

    public Frame onFailure() {
        return getCtrl().addFrame(this.stage.onFailure(), this.nextFrame,
            this.alsoFrame, this.elseFrame);
    }

    public Frame onSuccess() {
        return getCtrl().addFrame(this.stage.onSuccess(), this.nextFrame,
            this.alsoFrame, this.alsoFrame);
    }

    public int getDepth() {
        return this.depth;
    }

    private final int depth;

    /** Returns the call stack for this frame. */
    public Stack<StageSwitch> getCallStack() {
        Stack<StageSwitch> result = new Stack<StageSwitch>();
        Stack<Frame> inverseCallerStack = new Stack<Frame>();
        Frame caller = getNext();
        while (caller != null) {
            inverseCallerStack.push(caller);
            caller = caller.getNext();
        }
        while (!inverseCallerStack.isEmpty()) {
            result.push(inverseCallerStack.pop().getSwitch());
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
        int result = super.hashCode();
        result = prime * result + System.identityHashCode(this.alsoFrame);
        result = prime * result + System.identityHashCode(this.elseFrame);
        result = prime * result + System.identityHashCode(this.nextFrame);
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
        return true;
    }

    /** Constructs the initial frame for a control instance. */
    public static Frame newInstance(Instance ctrl) {
        Stage firstStage = ctrl.getTemplate().getStart().getFirstStage();
        return new Frame(ctrl, firstStage, null, null, null);
    }

    /** 
     * Constructs a new frame.
     * Makes sure final and deadlock frames are only created for top-level locations.
     */
    public static Frame newInstance(Instance ctrl, Stage stage,
            Frame nextFrame, Frame alsoFrame, Frame elseFrame) {
        Frame result;
        if (stage.isDead() && elseFrame != null) {
            result = elseFrame;
        } else if (stage.isFinal() && nextFrame != null) {
            result =
                new Frame(ctrl, nextFrame.getStage(), nextFrame.getNext(),
                    alsoFrame, alsoFrame);
        } else {
            result = new Frame(ctrl, stage, nextFrame, alsoFrame, elseFrame);
        }
        return result;
    }
}
