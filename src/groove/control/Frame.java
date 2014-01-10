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

import groove.graph.ANode;

/**
 * Run-time composed control state, possibly with a parent frame.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Frame extends ANode {
    /**
     * Top-level frame instantiating a given control location.
     */
    public Frame(Instance ctrl, Location location) {
        this(ctrl, null, null, location);
    }

    /**
     * Nested frame instantiating a given control location.
     */
    public Frame(Instance ctrl, Frame parent, CtrlEdge call,
            Location location) {
        super(ctrl.getNextFrameNr());
        this.ctrl = ctrl;
        this.parent = parent;
        this.call = call;
        this.location = location;
        this.depth = parent == null ? 0 : parent.getDepth() + 1;
    }

    /** Returns the containing control instance. */
    public Instance getCtrl() {
        return this.ctrl;
    }

    private final Instance ctrl;

    /** Returns the control location that this frame instantiates. */
    public Location getLocation() {
        return this.location;
    }

    private final Location location;

    /** Indicates if this frame has a parent frame.
     * If it does not have a parent frame, this is a top-level frame.
     */
    public boolean hasParent() {
        return getParent() != null;
    }

    /** 
     * Returns the (possibly {@code null}) parent frame.
     */
    public Frame getParent() {
        return this.parent;
    }

    /** 
     * Returns the ancestor frame according to a given generation.
     */
    public Frame getAncestor(int generation) {
        assert generation >= 0 && generation <= getDepth();
        if (generation == 0) {
            return this;
        } else {
            return this.parent.getAncestor(generation - 1);
        }
    }

    private final Frame parent;

    /** 
     * Returns the (possibly {@code null}) control call.
     */
    public CtrlEdge getCall() {
        return this.call;
    }

    private final CtrlEdge call;

    /** Returns the recursive depth of this frame. */
    public int getDepth() {
        return this.depth;
    }

    /** The recursive depth of this frame. */
    private final int depth;

    @Override
    protected String getToStringPrefix() {
        return "c";
    }
}
