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
package groove.abstraction.pattern.trans;

import groove.abstraction.Multiplicity;
import groove.abstraction.Multiplicity.MultKind;
import groove.abstraction.MyHashMap;
import groove.abstraction.pattern.match.PreMatch;
import groove.abstraction.pattern.shape.PatternEdge;
import groove.abstraction.pattern.shape.PatternFactory;
import groove.abstraction.pattern.shape.PatternNode;
import groove.abstraction.pattern.shape.PatternShape;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Application of a matched pattern graph transformation rule.
 * 
 * @author Eduardo Zambon
 */
public final class PatternShapeRuleApplication {

    private final PatternShape pShape;
    private final PatternRule pRule;
    private final PreMatch match;

    /** Default constructor. */
    public PatternShapeRuleApplication(PatternShape pShape, PreMatch match) {
        this.pShape = pShape;
        this.match = match;
        this.pRule = match.getRule();
    }

    /** Executes the rule application and returns the result. */
    public PatternShape transform() {
        if (!this.pRule.isModifying()) {
            return this.pShape;
        } else {
            return transform(this.pShape.clone());
        }
    }

    /** Transforms and returns the given pattern shape.*/
    private PatternShape transform(PatternShape host) {
        eraseNodes(host);
        eraseEdges(host);
        createNodes(host);
        createEdges(host);
        if (!this.pRule.isClosure()) {
            // This is a normal rule application. Close the transformed graph.
            this.pRule.getTypeGraph().close(host);
        } // else do nothing otherwise we have an infinite recursion.
        return host;
    }

    private void eraseNodes(PatternShape host) {
        for (Entry<PatternNode,Multiplicity> entry : computeErasureMap(host).entrySet()) {
            PatternNode pNode = entry.getKey();
            Multiplicity origMult = host.getMult(pNode);
            Multiplicity toRemove = entry.getValue();
            Multiplicity newMult = origMult.sub(toRemove);
            host.setMult(pNode, newMult);
        }
    }

    private Map<PatternNode,Multiplicity> computeErasureMap(PatternShape host) {
        Map<PatternNode,Multiplicity> diffMap =
            new MyHashMap<PatternNode,Multiplicity>();

        // First look at the pre-match to see what are the initial differences.
        for (RuleNode rNode : this.pRule.getEraserNodes()) {
            PatternNode pNode = this.match.getNode(rNode);
            Multiplicity mult = diffMap.get(pNode);
            if (mult != null) {
                mult = mult.add(Multiplicity.ONE_NODE_MULT);
            } else {
                mult = Multiplicity.ONE_NODE_MULT;
            }
            diffMap.put(pNode, mult);
        }

        // Now iterate over the layers from top to bottom and compute the
        // final differences.
        for (int layer = 0; layer <= host.depth(); layer++) {
            for (PatternNode pNode : host.getLayerNodes(layer)) {
                Multiplicity nDiff = diffMap.get(pNode);
                if (nDiff != null) {
                    // nDiff is already the maximum difference, compute the
                    // maximum for the next layer.
                    for (PatternEdge pEdge : host.outEdgeSet(pNode)) {
                        Multiplicity eMult = host.getMult(pEdge);
                        Multiplicity newDiff = nDiff.times(eMult).toNodeKind();
                        Multiplicity oldDiff = diffMap.get(pEdge.target());
                        if (oldDiff != null) {
                            newDiff = Multiplicity.max(newDiff, oldDiff);
                        }
                        diffMap.put(pEdge.target(), newDiff);
                    }
                }
            }
        }

        return diffMap;
    }

    private void eraseEdges(PatternShape host) {
        Multiplicity zeroOne =
            Multiplicity.getMultiplicity(0, 1, MultKind.EDGE_MULT);
        for (RuleEdge rEdge : this.pRule.getEraserEdges()) {
            PatternEdge pEdge = this.match.getEdge(rEdge);
            if (host.containsEdge(pEdge)) {
                Multiplicity mult = host.getMult(pEdge);
                Multiplicity newMult = mult.sub(zeroOne);
                host.setMult(pEdge, newMult);
            } // else the edge was already removed when an end node was removed.
        }
    }

    private void createNodes(PatternShape host) {
        PatternFactory factory = host.getFactory();
        for (RuleNode rNode : this.pRule.getCreatorNodes()) {
            PatternNode newNode =
                factory.createNode(rNode.getType(), host.nodeSet());
            host.addNode(newNode);
            this.match.putNode(rNode, newNode);
        }
    }

    private void createEdges(PatternShape host) {
        PatternFactory factory = host.getFactory();
        for (RuleEdge rEdge : this.pRule.getCreatorEdges()) {
            PatternNode source = this.match.getNode(rEdge.source());
            PatternNode target = this.match.getNode(rEdge.target());
            assert source != null && target != null;
            PatternEdge newEdge =
                factory.createEdge(source, rEdge.getType(), target);
            host.addEdge(newEdge);
        }
    }
}