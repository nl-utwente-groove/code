// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id$
 */
package groove.grammar;

import groove.grammar.rule.Anchor;
import groove.grammar.rule.AnchorKey;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleGraph;
import groove.grammar.rule.RuleNode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * In this implementation, the anchors are the minimal set of nodes and edges
 * needed to reconstruct the transformation, but not necessarily the entire
 * matching: only mergers, eraser nodes and edges (the later only if they are
 * not incident to an eraser node) and the incident nodes of creator edges are
 * stored.
 * @author Arend Rensink
 * @version $Revision$
 */
public class DefaultAnchorFactory implements AnchorFactory {
    /** Private empty constructor to make this a singleton class. */
    private DefaultAnchorFactory() {
        // empty constructor
    }

    /**
     * This implementation assumes that the rule is an <tt>SPORule</tt>, and
     * that the rule's internal sets of <tt>lhsOnlyNodes</tt> etc. have been
     * initialised already.
     */
    @Override
    public Anchor newAnchor(Rule rule) {
        RuleGraph lhs = rule.lhs();
        Set<AnchorKey> result = new LinkedHashSet<>();
        Set<RuleNode> colorNodes = new HashSet<>(rule.getColorMap().keySet());
        colorNodes.retainAll(lhs.nodeSet());
        result.addAll(colorNodes);
        result.addAll(Arrays.asList(rule.getEraserNodes()));
        result.addAll(rule.getModifierEnds());
        Set<AnchorKey> anchorKeys = getAnchorKeys(lhs).collect(Collectors.toSet());
        // add the root elements of modifying subrules
        result.addAll(rule.getSubRules()
            .stream()
            .filter(r -> r.isModifying())
            .flatMap(r -> r.getAnchor().stream())
            .filter(a -> anchorKeys.contains(a))
            .collect(Collectors.toList()));
        // add the creator variables
        result.addAll(Arrays.asList(rule.getCreatorVars()));
        // add the eraser edges and their variables
        List<RuleEdge> eraserEdges = Arrays.asList(rule.getEraserEdges());
        result.addAll(eraserEdges);
        result.addAll(eraserEdges.stream()
            .flatMap(e -> e.getVars().stream())
            .collect(Collectors.toList()));
        // add all non-creator parameters explicitly, as they need to be in the anchors
        // to ensure they are correctly bound
        if (rule.isTop()) {
            result.addAll(rule.getHiddenPars());
            result.addAll(rule.getSignature()
                .stream()
                .filter(p -> !p.isCreator())
                .map(p -> p.getRuleNode())
                .collect(Collectors.toList()));
        }
        // remove the root elements of the rule itself
        return new Anchor(result);
    }

    /** Returns the collection of all potential anchor keys in a given rule graph. */
    private Stream<AnchorKey> getAnchorKeys(RuleGraph graph) {
        return Stream.of(graph.nodeSet().stream(),
            graph.edgeSet().stream(),
            graph.varSet().stream()).flatMap(Function.identity());
    }

    /**
     * Returns the singleton instance of this class.
     */
    static public DefaultAnchorFactory getInstance() {
        return prototype;
    }

    /** The singleton instance of this class. */
    static private DefaultAnchorFactory prototype = new DefaultAnchorFactory();
}
