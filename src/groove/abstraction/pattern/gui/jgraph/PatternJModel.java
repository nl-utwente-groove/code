/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.abstraction.pattern.gui.jgraph;

import groove.abstraction.MyHashMap;
import groove.abstraction.pattern.shape.AbstractPatternGraph;
import groove.abstraction.pattern.shape.AbstractPatternNode;
import groove.graph.Node;
import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JModel;
import groove.gui.jgraph.JVertex;
import groove.trans.HostEdge;
import groove.trans.HostFactory;
import groove.trans.HostGraph;
import groove.trans.HostNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgraph.graph.ParentMap;

/**
 * A JGraph model for pattern graphs.
 * 
 * @author Eduardo Zambon
 */
public class PatternJModel extends JModel<AbstractPatternGraph<?,?>> {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------
    /**
     * Map from pattern graph nodes to JGraph cells.
     */
    private Map<Node,PatternJVertex> pNodeJCellMap =
        new HashMap<Node,PatternJVertex>();

    /**
     * Map that stores the containment relation between simple graph elements
     * and pattern nodes.
     */
    private ParentMap parentMap;

    private Map<PatternJVertex,List<JCell<AbstractPatternGraph<?,?>>>> reverseParentMap;

    /**
     * Factory to create simple graph elements. We can't reuse the elements
     * from the patterns of pattern nodes because they all come from the type
     * graph and thus we may have identity clashes. For example, different
     * pattern nodes with the same pattern type will point to the same
     * pattern (simple graph).
     */
    private HostFactory hostFactory;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Creates a new jModel with the given prototypes. */
    PatternJModel(PatternJGraph jGraph) {
        super(jGraph);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public AbstractPatternGraph<?,?> getGraph() {
        return (AbstractPatternGraph<?,?>) super.getGraph();
    }

    @Override
    protected void prepareLoad(AbstractPatternGraph<?,?> graph) {
        super.prepareLoad(graph);
        this.pNodeJCellMap.clear();
        this.hostFactory = HostFactory.newInstance();
    }

    @Override
    public JVertex<AbstractPatternGraph<?,?>> getJCellForNode(Node node) {
        if (isPatternTyped(node)) {
            return this.pNodeJCellMap.get(node);
        } else {
            return super.getJCellForNode(node);
        }
    }

    @Override
    protected JVertex<AbstractPatternGraph<?,?>> addNode(Node node) {
        JVertex<AbstractPatternGraph<?,?>> result = super.addNode(node);
        if (isPatternTyped(node)) {
            this.pNodeJCellMap.put(node, (PatternJVertex) result);
        }
        return result;
    }

    @Override
    protected void prepareInsert() {
        super.prepareInsert();
        this.parentMap = new ParentMap();
        this.reverseParentMap =
            new MyHashMap<PatternJVertex,List<JCell<AbstractPatternGraph<?,?>>>>();
    }

    @Override
    protected ParentMap getParentMap() {
        return this.parentMap;
    }

    @Override
    protected JVertex<AbstractPatternGraph<?,?>> computeJVertex(Node node) {
        JVertex<AbstractPatternGraph<?,?>> result =
            super.computeJVertex(node);
        if (isPatternTyped(node)) {
            createPattern((AbstractPatternNode) node, (PatternJVertex) result);
        }
        return result;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * Returns the root nodes of the pattern graph. This is NOT the same as
     * the roots from the JModel.
     */
    public List<JCell<AbstractPatternGraph<?,?>>> getPatternRoots() {
        List<JCell<AbstractPatternGraph<?,?>>> result =
            new ArrayList<JCell<AbstractPatternGraph<?,?>>>();
        for (Node pNode : getGraph().getLayerNodes(0)) {
            result.add(this.pNodeJCellMap.get(pNode));
        }
        return result;
    }

    /** Basic getter. */
    public Map<PatternJVertex,List<JCell<AbstractPatternGraph<?,?>>>> getReverseParentMap() {
        return this.reverseParentMap;
    }

    /** Returns true if the given node is a pattern graph node. */
    private boolean isPatternTyped(Node node) {
        return node instanceof AbstractPatternNode;
    }

    /** Creates the pattern elements of the given pattern node. */
    private void createPattern(AbstractPatternNode pNode,
            PatternJVertex pJVertex) {
        HostGraph pattern = pNode.getPattern();
        Map<HostNode,HostNode> nodeMap = new MyHashMap<HostNode,HostNode>();
        for (HostNode sNode : pattern.nodeSet()) {
            HostNode newSNode = this.hostFactory.createNode();
            nodeMap.put(sNode, newSNode);
            JVertex<AbstractPatternGraph<?,?>> sJVertex =
                addNode(newSNode);
            this.parentMap.addEntry(sJVertex, pJVertex);
            addToReverseMap(pJVertex, sJVertex);
        }
        for (HostEdge sEdge : pattern.edgeSet()) {
            HostNode source = nodeMap.get(sEdge.source());
            HostNode target = nodeMap.get(sEdge.target());
            HostEdge newSEdge =
                this.hostFactory.createEdge(source, sEdge.label(), target);
            JCell<AbstractPatternGraph<?,?>> sJEdge = addEdge(newSEdge);
            this.parentMap.addEntry(sJEdge, pJVertex);
            addToReverseMap(pJVertex, sJEdge);
        }
    }

    private void addToReverseMap(PatternJVertex pJVertex,
            JCell<AbstractPatternGraph<?,?>> jCell) {
        List<JCell<AbstractPatternGraph<?,?>>> cells =
            this.reverseParentMap.get(pJVertex);
        if (cells == null) {
            cells = new ArrayList<JCell<AbstractPatternGraph<?,?>>>();
            this.reverseParentMap.put(pJVertex, cells);
        }
        cells.add(jCell);
    }

}
