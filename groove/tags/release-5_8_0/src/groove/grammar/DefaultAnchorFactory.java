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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import groove.grammar.rule.Anchor;
import groove.grammar.rule.AnchorKey;
import groove.grammar.rule.RuleEdge;
import groove.grammar.rule.RuleGraph;
import groove.grammar.rule.RuleNode;
import groove.util.collect.CollectionOfCollections;

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
     * This implementation assumes
     * that the rule's internal sets of <tt>lhsOnlyNodes</tt> etc. have been
     * initialised already.
     */
    @Override
    public Anchor newAnchor(Rule rule) {
        RuleGraph lhs = rule.lhs();
        Set<AnchorKey> result = new LinkedHashSet<>();
        Set<RuleNode> colorNodes = new HashSet<>(rule.getColorMap()
            .keySet());
        colorNodes.retainAll(lhs.nodeSet());
        result.addAll(colorNodes);
        result.addAll(Arrays.asList(rule.getEraserNodes()));
        result.addAll(rule.getModifierEnds());
        // add the root elements of modifying subrules
        for (Rule subrule : rule.getSubRules()) {
            if (subrule.isModifying()) {
                Anchor subruleAnchor = new Anchor(subrule.getAnchor());
                subruleAnchor.retainAll(getAnchorKeys(lhs));
                result.addAll(subruleAnchor);
            }
        }
        result.addAll(Arrays.asList(rule.getCreatorVars()));
        for (RuleEdge eraserEdge : rule.getEraserEdges()) {
            result.add(eraserEdge);
            result.addAll(eraserEdge.getVars());
        }
        // add all non-creator parameters explicitly, as they need to be in the anchors
        // to ensure they are correctly bound
        if (rule.isTop()) {
            Set<RuleNode> hiddenPars = rule.getHiddenPars();
            if (hiddenPars != null) {
                result.addAll(hiddenPars);
            }
            rule.getSignature()
                .stream()
                .filter(v -> !v.isCreator() && !v.isAsk())
                .map(v -> v.getNode())
                .forEach(n -> result.add(n));
        }
        // remove the root elements of the rule itself
        return new Anchor(result);
    }

    /** Returns the collection of all potential anchor keys in a given rule graph. */
    private Collection<Object> getAnchorKeys(RuleGraph graph) {
        return new CollectionOfCollections<>(graph.nodeSet(), graph.edgeSet(), graph.varSet());
    }

    /**
     * Returns the singleton instance of this class.
     */
    static public DefaultAnchorFactory instance() {
        return INSTANCE;
    }

    /** The singleton instance of this class. */
    static private DefaultAnchorFactory INSTANCE = new DefaultAnchorFactory();
}
