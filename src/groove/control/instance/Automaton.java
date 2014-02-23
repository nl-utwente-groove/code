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

import groove.control.graph.ControlGraph;
import groove.control.template.SwitchStack;
import groove.control.template.Template;
import groove.util.collect.Pool;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Instantiated control automaton.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Automaton {
    /**
     * Instantiates a given template.
     */
    public Automaton(Template template) {
        this.template = template;
        this.framePool = new Pool<Frame>();
        Frame start = new Frame(this, getTemplate().getStart(), new SwitchStack());
        start.setFixed();
        this.start = addFrame(start);
    }

    /** Returns the template from which this control instance has been created. */
    public Template getTemplate() {
        return this.template;
    }

    private final Template template;

    /** Returns the start frame of the automaton. */
    public Frame getStart() {

        return this.start;
    }

    private final Frame start;

    /** 
     * Adds the canonical version of a frame to this automaton.
     * @param frame the frame to be added; non-{@code null}
     * @return either {@code frame} or an equal copy that was already in the automaton
     */
    Frame addFrame(Frame frame) {
        assert frame.isFixed();
        assert frame.getAut() == this;
        assert frame.getNumber() == getFramePool().size();
        return getFramePool().canonical(frame);
    }

    /** Returns the next available frame number. */
    int getNextFrameNr() {
        return getFramePool().size();
    }

    /** Returns the set of frames in this automaton. */
    public Set<Frame> getFrames() {
        return getFramePool().keySet();
    }

    /** Returns the mapping from frames to themselves, used to create
     * canonical frame representations.
     */
    private Pool<Frame> getFramePool() {
        return this.framePool;
    }

    private final Pool<Frame> framePool;

    /** Fully explores this automaton. */
    public void explore() {
        Queue<Frame> fresh = new LinkedList<Frame>();
        Set<Frame> nodes = new HashSet<Frame>();
        nodes.add(getStart());
        fresh.add(getStart());
        while (!fresh.isEmpty()) {
            Frame next = fresh.poll();
            if (!next.isTrial()) {
                continue;
            }
            for (Step step : next.getAttempt()) {
                Frame onFinish = step.onFinish();
                if (nodes.add(onFinish)) {
                    fresh.add(onFinish);
                }
            }
            Frame onFailure = next.getAttempt().onFailure();
            if (nodes.add(onFailure)) {
                fresh.add(onFailure);
            }
            Frame onSuccess = next.getAttempt().onSuccess();
            if (nodes.add(onSuccess)) {
                fresh.add(onSuccess);
            }
        }
    }

    /** Returns a control graph consisting of this automaton's frames and steps. */
    public ControlGraph toGraph() {
        return ControlGraph.newGraph(this.template.getName(), this.getStart());
    }
}
