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
 * $Id: MinimalAnchorFactory.java,v 1.8 2008-02-29 11:02:20 fladder Exp $
 */
package groove.trans;

import groove.control.CtrlPar;
import groove.rel.LabelVar;
import groove.rel.VarSupport;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * In this implementation, the anchors are the minimal set of nodes and edges
 * needed to reconstruct the transformation, but not necessarily the entire
 * matching: only mergers, eraser nodes and edges (the later only if they are
 * not incident to an eraser node) and the incident nodes of creator edges are
 * stored.
 * @author Arend Rensink
 * @version $Revision$
 */
public class MinimalAnchorFactory implements AnchorFactory<Rule> {
    /** Private empty constructor to make this a singleton class. */
    private MinimalAnchorFactory() {
        // empty constructor
    }

    /**
     * This implementation assumes that the rule is an <tt>SPORule</tt>, and
     * that the rule's internal sets of <tt>lhsOnlyNodes</tt> etc. have been
     * initialised already.
     */
    public RuleGraph newAnchor(Rule rule) {
        RuleGraph result = rule.lhs().newGraph(rule.getName() + "-anchor");
        Set<RuleNode> colorNodes =
            new HashSet<RuleNode>(rule.getColorMap().keySet());
        colorNodes.retainAll(rule.lhs().nodeSet());
        result.addNodeSet(colorNodes);
        result.addNodeSet(Arrays.asList(rule.getEraserNodes()));
        if (rule.isTop()) {
            Set<RuleNode> hiddenPars = rule.getHiddenPars();
            if (hiddenPars != null) {
                result.addNodeSet(hiddenPars);
            }
            List<CtrlPar.Var> ruleSig = rule.getSignature();
            for (CtrlPar.Var rulePar : ruleSig) {
                if (!rulePar.isCreator()) {
                    result.addNode(rulePar.getRuleNode());
                }
            }
        }
        // set of variables that need to be matched by variable-binding anchor edges
        Set<LabelVar> modifierVars =
            new HashSet<LabelVar>(rule.getModifierVars());
        for (RuleEdge eraserEdge : rule.getEraserEdges()) {
            result.addEdge(eraserEdge);
            modifierVars.removeAll(VarSupport.getBoundVars(eraserEdge));
        }
        if (!modifierVars.isEmpty()) {
            for (RuleEdge edge : rule.lhs().edgeSet()) {
                if (new HashSet<LabelVar>(modifierVars).removeAll(VarSupport.getBoundVars(edge))) {
                    result.addEdge(edge);
                }
            }
        }
        result.addNodeSet(rule.getModifierEnds());
        return result;
    }

    /**
     * Returns the singleton instance of this class.
     */
    static public MinimalAnchorFactory getInstance() {
        return prototype;
    }

    /** The singleton instance of this class. */
    static private MinimalAnchorFactory prototype = new MinimalAnchorFactory();
}
