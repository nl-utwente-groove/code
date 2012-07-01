/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: MatrixAutomaton.java,v 1.13 2008-01-30 09:32:26 iovka Exp $
 */
package groove.rel;

import static groove.rel.Direction.FORWARD;
import groove.graph.Edge;
import groove.graph.ElementFactory;
import groove.graph.NodeSetEdgeSetGraph;
import groove.graph.TypeEdge;
import groove.graph.TypeElement;
import groove.graph.TypeGraph;
import groove.graph.TypeGuard;
import groove.graph.TypeLabel;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.RuleLabel;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of regular automata that also keeps track of the valuation
 * of the variables.
 */
public class SimpleAutomaton extends NodeSetEdgeSetGraph<RegNode,RegEdge>
        implements RegAut {
    /**
     * Creates an automaton with a given start and end node, which does not
     * accept the empty word.
     * The label store indicates which labels to expect (which is used
     * to predict the matching of wildcards).
     */
    public SimpleAutomaton(RegNode start, RegNode end, TypeGraph typeGraph) {
        super("automaton");
        this.start = start;
        this.end = end;
        this.typeGraph = typeGraph;
        assert typeGraph != null;
    }

    /** 
     * Regular automata are created to have disjoint node sets,
     * so fresh nodes should not be generated by the automaton itself.
     */
    @Override
    public RegNode addNode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RegNode getStartNode() {
        return this.start;
    }

    @Override
    public RegNode getEndNode() {
        return this.end;
    }

    @Override
    public boolean isAcceptsEmptyWord() {
        return this.acceptsEmptyWord;
    }

    @Override
    public void setAcceptsEmptyWord(boolean acceptsEmptyWord) {
        this.acceptsEmptyWord = acceptsEmptyWord;
    }

    @Override
    public void setEndNode(RegNode endNode) {
        this.end = endNode;
    }

    @Override
    public void setStartNode(RegNode startNode) {
        this.start = startNode;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer(super.toString());
        result.append("\nStart node: " + getStartNode());
        result.append("\nEnd node: " + getEndNode());
        result.append("\nAccepts empty word: " + isAcceptsEmptyWord());
        return result.toString();
    }

    @Override
    public void setFixed() {
        super.setFixed();
        // when the graph is fixed, we can initialise the auxiliary structures.
        for (Direction dir : Direction.all) {
            this.normalAuts.put(dir, createNormalAutomaton(dir));
        }
    }

    /** Creates a normalised automaton for exploration in a given direction. */
    private NormalAutomaton createNormalAutomaton(Direction dir) {
        NormalAutomaton result =
            new NormalAutomaton(dir == FORWARD ? getStartNode() : getEndNode(),
                isAcceptsEmptyWord());
        // set of unexplored states
        Set<NormalState> unexplored = new HashSet<NormalState>();
        unexplored.add(result.getStartState());
        do {
            Iterator<NormalState> iter = unexplored.iterator();
            NormalState current = iter.next();
            iter.remove();
            // mapping from type labels to target nodes, per direction
            Map<Direction,Map<TypeLabel,Set<RegNode>>> labelOutMap =
                new EnumMap<Direction,Map<TypeLabel,Set<RegNode>>>(
                    Direction.class);
            // mapping from label variables to target nodes, per direction
            Map<Direction,Map<LabelVar,Set<RegNode>>> varOutMap =
                new EnumMap<Direction,Map<LabelVar,Set<RegNode>>>(
                    Direction.class);
            // initialise the maps
            for (Direction edgeDir : Direction.all) {
                labelOutMap.put(edgeDir, new HashMap<TypeLabel,Set<RegNode>>());
                varOutMap.put(edgeDir, new HashMap<LabelVar,Set<RegNode>>());
            }
            // collect the transitions of all nodes contained in the state
            for (RegNode node : current.getNodes()) {
                for (RegEdge edge : dir == FORWARD ? outEdgeSet(node)
                        : inEdgeSet(node)) {
                    RuleLabel edgeLabel = edge.label();
                    Direction normalisedDir =
                        edgeLabel.isInv() ? dir.getInverse() : dir;
                    RegNode opposite = dir.end(edge);
                    LabelVar var = edgeLabel.getWildcardGuard().getVar();
                    if (var != null) {
                        addToImages(varOutMap.get(normalisedDir), var, opposite);
                    } else {
                        for (TypeLabel label : getMatchingLabels(edgeLabel)) {
                            addToImages(labelOutMap.get(normalisedDir), label,
                                opposite);
                        }
                    }
                }
            }
            for (Direction edgeDir : Direction.all) {
                for (Map.Entry<LabelVar,Set<RegNode>> varEntry : varOutMap.get(
                    edgeDir).entrySet()) {
                    Set<RegNode> image = varEntry.getValue();
                    NormalState target = result.getState(image);
                    if (target == null) {
                        target = createState(result, image, dir);
                        unexplored.add(target);
                    }
                    current.addSuccessor(edgeDir, varEntry.getKey(), target);
                }
                for (Map.Entry<TypeLabel,Set<RegNode>> labelEntry : labelOutMap.get(
                    edgeDir).entrySet()) {
                    Set<RegNode> image = labelEntry.getValue();
                    NormalState target = result.getState(image);
                    if (target == null) {
                        target = createState(result, image, dir);
                        unexplored.add(target);
                    }
                    current.addSuccessor(edgeDir, labelEntry.getKey(), target);
                }
            }
        } while (!unexplored.isEmpty());
        return result.getMinimised();
    }

    /** Extracts the type labels from the type elements matching a given rule label. */
    private Set<TypeLabel> getMatchingLabels(RuleLabel label) {
        Set<TypeLabel> result = new HashSet<TypeLabel>();
        for (TypeElement type : this.typeGraph.getMatches(label)) {
            result.add(type.label());
        }
        return result;
    }

    private NormalState createState(NormalAutomaton result, Set<RegNode> nodes,
            Direction dir) {
        RegNode finalNode = dir == FORWARD ? getEndNode() : getStartNode();
        return result.addState(nodes, nodes.contains(finalNode));
    }

    private <K> void addToImages(Map<K,Set<RegNode>> map, K key, RegNode node) {
        Set<RegNode> images = map.get(key);
        if (images == null) {
            map.put(key, new HashSet<RegNode>());
        }
        images.add(node);
    }

    @Override
    public ElementFactory<RegNode,RegEdge> getFactory() {
        return RegFactory.instance();
    }

    public boolean accepts(List<String> word) {
        assert isFixed();
        assert this.typeGraph.isImplicit();
        if (word.isEmpty()) {
            return isAcceptsEmptyWord();
        } else {
            // keep the set of current matches (initially the start node)
            Map<RegNode,HashMap<LabelVar,TypeElement>> matchSet =
                Collections.singletonMap(getStartNode(),
                    new HashMap<LabelVar,TypeElement>());
            boolean accepts = false;
            // go through the word
            for (int index = 0; !accepts && !matchSet.isEmpty()
                && index < word.size(); index++) {
                boolean lastIndex = index == word.size() - 1;
                TypeEdge letter = getLetter(word.get(index));
                Map<RegNode,HashMap<LabelVar,TypeElement>> newMatchSet =
                    new HashMap<RegNode,HashMap<LabelVar,TypeElement>>();
                Iterator<? extends Map.Entry<RegNode,HashMap<LabelVar,TypeElement>>> matchIter =
                    matchSet.entrySet().iterator();
                while (!accepts && matchIter.hasNext()) {
                    Map.Entry<RegNode,HashMap<LabelVar,TypeElement>> matchEntry =
                        matchIter.next();
                    RegNode match = matchEntry.getKey();
                    HashMap<LabelVar,TypeElement> idMap = matchEntry.getValue();
                    Iterator<? extends RegEdge> outEdgeIter =
                        outEdgeSet(match).iterator();
                    while (!accepts && outEdgeIter.hasNext()) {
                        RegEdge outEdge = outEdgeIter.next();
                        RuleLabel label = outEdge.label();
                        boolean labelOK;
                        if (label.isInv()) {
                            labelOK = false;
                        } else if (label.isWildcard()) {
                            TypeGuard guard = label.getWildcardGuard();
                            labelOK = guard.isSatisfied(letter);
                            if (guard.isNamed()) {
                                idMap =
                                    new HashMap<LabelVar,TypeElement>(idMap);
                                TypeElement oldIdValue =
                                    idMap.put(guard.getVar(), letter);
                                labelOK =
                                    oldIdValue == null
                                        || oldIdValue.equals(letter);
                            }
                        } else {
                            labelOK =
                                label.getTypeLabel().equals(letter.label());
                        }
                        if (labelOK) {
                            // if we're at the last index, we don't have to
                            // build the new match set
                            if (lastIndex) {
                                accepts = outEdge.target().equals(getEndNode());
                            } else {
                                newMatchSet.put(outEdge.target(), idMap);
                            }
                        }
                    }
                }
                matchSet = newMatchSet;
            }
            return accepts;
        }
    }

    private TypeEdge getLetter(String text) {
        TypeLabel label = TypeLabel.createLabel(text);
        Set<? extends TypeEdge> letters = this.typeGraph.labelEdgeSet(label);
        if (letters == null || letters.size() != 1) {
            return null;
        } else {
            return letters.iterator().next();
        }
    }

    public Set<Result> getMatches(HostGraph graph, HostNode startImage,
            HostNode endImage, Valuation valuation) {
        assert isFixed();
        if (valuation == null) {
            valuation = Valuation.EMPTY;
        }
        // TODO implement
        return null;
    }

    public Set<Result> getMatches(HostGraph graph, HostNode startImage,
            HostNode endImage) {
        return getMatches(graph, startImage, endImage, null);
    }

    @Override
    public Set<TypeElement> getAlphabet() {
        assert isFixed();
        Set<TypeElement> result = new HashSet<TypeElement>();
        for (RegEdge edge : edgeSet()) {
            result.addAll(this.typeGraph.getMatches(edge.label()));
        }
        return result;
    }

    @Override
    protected boolean isTypeCorrect(Edge edge) {
        boolean result = edge instanceof RegEdge;
        if (result) {
            RuleLabel edgeLabel = ((RegEdge) edge).label();
            if (edgeLabel.isInv()) {
                edgeLabel = edgeLabel.getInvLabel();
            }
            result =
                edgeLabel.isWildcard() || edgeLabel.isSharp()
                    || edgeLabel.isAtom();
        }
        return result;
    }

    /**
     * The start node of the automaton.
     */
    private RegNode start;
    /**
     * The end node of the automaton.
     */
    private RegNode end;
    /**
     * Flag to indicate that the automaton is to accept the empty word.
     */
    private boolean acceptsEmptyWord;
    /** Map from exploration directions to the initial state of the normalised automaton. */
    private final Map<Direction,NormalAutomaton> normalAuts =
        new EnumMap<Direction,NormalAutomaton>(Direction.class);

    public final TypeGraph getTypeGraph() {
        return this.typeGraph;
    }

    /** Label store to be matched against. */
    private final TypeGraph typeGraph;
}