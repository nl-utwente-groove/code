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

import groove.control.template.Template;
import groove.graph.GraphRole;
import groove.graph.NodeSetEdgeSetGraph;
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
public class Automaton extends NodeSetEdgeSetGraph<Frame,Step> {
    /**
     * Instantiates a given template.
     */
    public Automaton(Template template) {
        super(template.getName());
        this.template = template;
        this.framePool = new Pool<Frame>();
    }

    /** Returns the template from which this control instance has been created. */
    public Template getTemplate() {
        return this.template;
    }

    private final Template template;

    /** Returns the start frame of the automaton. */
    public Frame getStart() {
        if (this.start == null) {
            Frame result = new Frame(this, getTemplate().getStart().getFirstStage());
            this.start = result.normalise(null, null);
            addNode(result);
        }
        return this.start;
    }

    private Frame start;

    /** Returns the next available frame number. */
    int getNextFrameNr() {
        return nodeCount();
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

    /** Returns the mapping from frames to themselves, used to create
     * canonical frame representations.
     */
    Pool<Frame> getFramePool() {
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
