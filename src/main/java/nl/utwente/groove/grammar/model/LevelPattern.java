/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
 * $Id$
 */

package nl.utwente.groove.grammar.model;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.utwente.groove.grammar.model.RuleModel.Index;
import nl.utwente.groove.grammar.rule.RuleGraph;
import nl.utwente.groove.grammar.rule.RuleNode;
import nl.utwente.groove.grammar.rule.VariableNode;

/**
 * Per-level pattern data handed between the rule construction stages: the
 * LHS, RHS and NAC graphs of one quantification level, together with
 * the count node, the output nodes, the colour map and the rule flag
 * of that level. Patterns link to the pattern of the parent level,
 * forming a tree congruent to the index tree.
 * @author Arend Rensink
 * @version $Revision$
 */
class LevelPattern {
    /** Constructs a pattern from the given level data. */
    LevelPattern(Index index, LevelPattern parent, RuleGraph lhs, RuleGraph rhs,
                 List<RuleGraph> nacs, VariableNode countNode, Set<VariableNode> outputNodes,
                 Map<RuleNode,Color> colorMap, boolean isRule) {
        this.index = index;
        this.parent = parent;
        this.lhs = lhs;
        this.rhs = rhs;
        this.nacs = nacs;
        this.countNode = countNode;
        this.outputNodes = outputNodes;
        this.colorMap = colorMap;
        this.isRule = isRule;
    }

    /** Returns the index of this level. */
    public Index getIndex() {
        return this.index;
    }

    @Override
    public String toString() {
        return String.format("Pattern for level %s", getIndex().getName());
    }

    /** Index of this level. */
    final Index index;
    /** Pattern of the parent level; {@code null} if this is the top level. */
    final LevelPattern parent;
    /** The left hand side graph of the rule. */
    final RuleGraph lhs;
    /** The right hand side graph of the rule. */
    final RuleGraph rhs;
    /** List of NAC graphs. */
    final List<RuleGraph> nacs;
    /** The rule node registering the match count. */
    final VariableNode countNode;
    /** Output nodes of the condition. */
    final Set<VariableNode> outputNodes;
    /** Map from rule nodes to declared colours. */
    final Map<RuleNode,Color> colorMap;
    /** Flag indicating that modifiers have been found at this level. */
    final boolean isRule;
}
