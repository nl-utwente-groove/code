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
package groove.control.graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import groove.control.Attempt;
import groove.control.Attempt.Stage;
import groove.control.Position;
import groove.control.template.Template;
import groove.grammar.QualName;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.NodeSetEdgeSetGraph;

/**
 * Graph representation of a control automaton, used for visualisation purposes.
 * Attempts are translated to individual edges for each of the calls, as well
 * as verdict edges.
 * Verdict edges to deadlocks are left out of the graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ControlGraph extends NodeSetEdgeSetGraph<ControlNode,ControlEdge> {
    /**
     * Constructs a new graph with a given name.
     */
    private ControlGraph(QualName name) {
        super(name.toString());
    }

    /** Returns the qualified name of the control automaton wrapped in this graph. */
    public QualName getQualName() {
        return QualName.parse(getName());
    }

    @Override
    public ControlNode addNode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ControlNode addNode(int nr) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ControlEdge addEdge(ControlNode source, String label, ControlNode target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ControlEdge addEdge(ControlNode source, Label label, ControlNode target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GraphRole getRole() {
        return GraphRole.CTRL;
    }

    /** Returns the (possibly {@code null}) template from which this graph was constructed. */
    public Template getTemplate() {
        return this.template;
    }

    /** Initialises the template from which this graph was constructed. */
    public void setTemplate(Template template) {
        this.template = template;
    }

    private Template template;

    /** Returns the start node. */
    public ControlNode getStart() {
        return this.start;
    }

    /** Initialises the start node. */
    private void setStart(ControlNode start) {
        assert this.start == null;
        assert start != null;
        this.start = start;
    }

    private ControlNode start;

    /** Constructs a control graph for a given template. */
    public static ControlGraph newGraph(Template template, boolean full) {
        ControlGraph result = newGraph(template.getQualName(), template.getStart(), full);
        result.setTemplate(template);
        return result;
    }

    /**
     * Constructs the control graph from a given initial position.
     * @param full if {@code true}, the full control structure is generated;
     * otherwise, only the call edges are shown
     */
    public static <P extends Position<P,A>,A extends Stage<P,A>> ControlGraph newGraph(
        QualName name, P init, boolean full) {
        ControlGraph result = new ControlGraph(name);
        Map<P,ControlNode> nodeMap = new HashMap<>();
        Queue<P> fresh = new LinkedList<>();
        addNode(result, nodeMap, init, fresh);
        while (!fresh.isEmpty()) {
            P next = fresh.poll();
            ControlNode node = nodeMap.get(next);
            if (next.isTrial()) {
                Attempt<P,A> attempt = next.getAttempt();
                P onSuccess = attempt.onSuccess();
                if (!onSuccess.isDead()) {
                    if (full) {
                        ControlNode target = addNode(result, nodeMap, onSuccess, fresh);
                        node.addVerdictEdge(target, true);
                    } else {
                        nodeMap.put(onSuccess, node);
                        fresh.add(onSuccess);
                    }
                }
                P onFailure = attempt.onFailure();
                if (!onFailure.isDead()) {
                    if (full) {
                        ControlNode target = addNode(result, nodeMap, onFailure, fresh);
                        node.addVerdictEdge(target, false);
                    } else {
                        nodeMap.put(onFailure, node);
                        fresh.add(onFailure);
                    }
                }
                for (A out : attempt) {
                    addEdge(result, nodeMap, node, out, fresh);
                }
            }
        }
        return result;
    }

    /**
     * Adds a node to the control graph under construction.
     */
    private static <P extends Position<P,A>,A extends Stage<P,A>> ControlNode addNode(
        ControlGraph graph, Map<P,ControlNode> nodeMap, P pos, Queue<P> fresh) {
        ControlNode result = nodeMap.get(pos);
        if (result == null) {
            nodeMap.put(pos, result = new ControlNode(graph, pos));
            fresh.add(pos);
            if (pos.isStart()) {
                graph.setStart(result);
            }
        }
        return result;
    }

    /**
     * Adds a call edge to the control graph under construction.
     */
    private static <P extends Position<P,A>,A extends Stage<P,A>> void addEdge(ControlGraph result,
        Map<P,ControlNode> nodeMap, ControlNode node, Stage<P,A> out, Queue<P> fresh) {
        ControlNode target;
        target = addNode(result, nodeMap, out.onFinish(), fresh);
        node.addCallEdge(target, out.getCallStack());
    }
}
