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

import groove.control.template.Location;
import groove.control.template.Stage;
import groove.control.template.Template;
import groove.graph.GraphRole;
import groove.graph.NodeSetEdgeSetGraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Instantiated control automaton.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Automaton extends NodeSetEdgeSetGraph<Frame,Step> {
    /**
     * Instantiates a given template.
     */
    public Automaton(Template template) {
        super(template.getName());
        this.template = template;
        this.framePool = new HashMap<Frame,Frame>();
    }

    /** Returns the template from which this control instance has been created. */
    public Template getTemplate() {
        return this.template;
    }

    private final Template template;

    /** Returns the start frame of the automaton. */
    public Frame getStart() {
        return addFrame(newFrame(getTemplate().getStart()));
    }

    /** Returns a final frame for this instance. */
    public Frame getFinal() {
        return addFrame(newFrame(getTemplate().getFinal()));
    }

    /** Returns a deadlocked frame at given transience depth for this instance. */
    public Frame getDead(int depth) {
        return addFrame(newFrame(this.template.getDead(depth)));
    }

    /** Returns the next available frame number. */
    int getNextFrameNr() {
        return nodeCount();
    }

    /** Normalises a frame, adds it to the nodes of this graph, and returns the normalised frame. */
    Frame addFrame(Frame frame) {
        Frame result = frame.normalise();
        addNode(result);
        return result;
    }

    @Override
    public GraphRole getRole() {
        return GraphRole.CTRL;
    }

    @Override
    public Automaton newGraph(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeEdge(Step edge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeNode(Frame node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Automaton clone() {
        return new Automaton(getTemplate());
    }

    /** Constructs the initial, top-level frame for a given control location. */
    Frame newFrame(Location loc) {
        Frame result = new Frame(this, loc.getFirstStage(), new CallStack(), null, null, null);
        return canonical(result);
    }

    /** 
     * Constructs a new frame.
     * Makes sure final and deadlock frames are only created for top-level locations.
     */
    Frame newFrame(Stage stage, CallStack callStack, Frame nextF, Frame alsoF, Frame elseF) {
        if (nextF != null || alsoF != null || elseF != null) {
            nextF = nextF == null ? getFinal() : nextF;
            alsoF = alsoF == null ? getDead(stage.getDepth()) : alsoF;
            elseF = elseF == null ? getDead(stage.getDepth()) : elseF;
        }
        Frame result = new Frame(this, stage, callStack, nextF, alsoF, elseF);
        return canonical(result);
    }

    /** Returns a canonical representative for a given frame from the pool of known frames. */
    private Frame canonical(Frame frame) {
        Frame result = frame;
        Frame oldResult = this.framePool.put(result, result);
        if (oldResult != null) {
            this.framePool.put(oldResult, oldResult);
            result = oldResult;
        }
        return result;
    }

    private Map<Frame,Frame> framePool;

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
            Frame onFinish = next.getAttempt().onFinish();
            if (nodes.add(onFinish)) {
                fresh.add(onFinish);
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
}
