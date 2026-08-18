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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.grammar.model.RuleModel.Index;
import nl.utwente.groove.grammar.rule.LabelVar;
import nl.utwente.groove.grammar.rule.RegExpr;
import nl.utwente.groove.grammar.rule.RuleEdge;
import nl.utwente.groove.grammar.rule.RuleFactory;
import nl.utwente.groove.grammar.rule.RuleGraph;
import nl.utwente.groove.grammar.rule.RuleGraphMorphism;
import nl.utwente.groove.grammar.rule.RuleLabel;
import nl.utwente.groove.grammar.rule.RuleNode;
import nl.utwente.groove.grammar.type.TypeEdge;
import nl.utwente.groove.grammar.type.TypeElement;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.grammar.type.TypeNode;
import nl.utwente.groove.util.Strings;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * The pass that types the level patterns of a rule: constructs, for every
 * quantification level, a typed {@link LevelPattern} from the untyped one
 * via {@link TypeGraph#analyzeRule}, chaining the typing down the level
 * tree, and checks type specialisation and merger consistency.
 * @author Arend Rensink
 * @version $Revision$
 */
class PatternTyper {
    /** Constructs a typer for a given rule compiler.
     * @param compiler the compiler providing the compilation context
     * @param globalTypeMap the (empty) rule-wide mapping from untyped to
     * typed rule elements, filled by the typer
     */
    PatternTyper(RuleCompiler compiler, RuleGraphMorphism globalTypeMap) {
        this.compiler = compiler;
        this.globalTypeMap = globalTypeMap;
    }

    /** Types the given untyped level patterns and returns the typed patterns,
     * in the tree order of the indices. */
    SortedMap<Index,LevelPattern> type(SortedMap<Index,LevelPattern> untypedPatterns) throws FormatException {
        SortedMap<Index,Level> levelMap = new TreeMap<>();
        SortedMap<Index,LevelPattern> result = new TreeMap<>();
        for (LevelPattern origin : untypedPatterns.values()) {
            Index index = origin.getIndex();
            Level parent = index.isTopLevel()
                ? null
                : levelMap.get(index.getParent());
            Level level = new Level(origin, parent, this.globalTypeMap);
            levelMap.put(index, level);
            result.put(index, level.pattern);
        }
        return result;
    }

    /** Convenience method to retrieve the grammar properties from the compiler. */
    private GrammarProperties getGrammarProperties() {
        return this.compiler.getGrammarProperties();
    }

    /** Convenience method to retrieve the type graph from the compiler. */
    private TypeGraph getTypeGraph() {
        return this.compiler.getTypeGraph();
    }

    /** Convenience method to test for injective matching. */
    private boolean isInjective() {
        return this.compiler.isInjective();
    }

    /** Convenience method to create a pre-projected error set. */
    private FormatErrorSet createErrors() {
        return this.compiler.createErrors();
    }

    /** The compiler providing the compilation context. */
    private final RuleCompiler compiler;
    /** The global, rule-wide mapping from untyped to typed rule elements. */
    private final RuleGraphMorphism globalTypeMap;

    /**
     * The typing of one level: constructs the typed pattern of the
     * level from its untyped origin pattern, or an identically typed
     * pattern if there is no type graph.
     */
    private class Level {
        public Level(LevelPattern origin, Level parent,
                      RuleGraphMorphism globalTypeMap) throws FormatException {
            this.parent = parent;
            this.factory = globalTypeMap.getFactory();
            this.globalTypeMap = globalTypeMap;
            RuleGraphMorphism parentTypeMap = parent == null
                ? new RuleGraphMorphism(this.factory)
                : parent.typeMap;
            this.typeMap = new RuleGraphMorphism(this.factory);
            RuleGraph lhs = toTypedGraph(origin.lhs, parentTypeMap, this.typeMap);
            // type the RHS taking the typing of the LHS into account
            // to allow use of the typed label variables
            RuleGraphMorphism lhsTypeMap = new RuleGraphMorphism(this.factory);
            lhsTypeMap.putAll(parentTypeMap);
            lhsTypeMap.putAll(this.typeMap);
            RuleGraph rhs = toTypedGraph(origin.rhs, lhsTypeMap, this.typeMap);
            // check against label type restrictions in RHS
            for (Map.Entry<LabelVar,Set<? extends TypeElement>> entry : lhsTypeMap
                .getVarTyping()
                .entrySet()) {
                LabelVar var = entry.getKey();
                if (!this.typeMap.getVarTyping().containsKey(var)) {
                    continue;
                }
                Set<? extends TypeElement> lhsTypes = entry.getValue();
                lhsTypes.removeAll(this.typeMap.getVarTypes(var));
                if (!lhsTypes.isEmpty()) {
                    this.errors
                        .add("Invalid %s type%s %s for creator variable %s",
                             var.getKind().getDescription(false), lhsTypes.size() == 1
                                 ? ""
                                 : "s",
                             Strings.toString(lhsTypes.toArray(), "", "", ", "), var);
                }
            }
            this.errors.throwException();
            List<RuleGraph> nacs = new ArrayList<>();
            for (RuleGraph nac : origin.nacs) {
                nacs.add(toTypedGraph(nac, this.typeMap, null));
            }
            this.pattern = new LevelPattern(origin.index, parent == null
                ? null
                : parent.pattern, lhs, rhs, nacs, origin.countNode, origin.outputNodes,
                new HashMap<>(), origin.isRule);
            // check for correct type specialisation
            // this has to be done after the NACs have been added
            try {
                Set<RuleNode> parentNodes = new HashSet<>();
                for (RuleNode origParentNode : parentTypeMap.nodeMap().keySet()) {
                    parentNodes.add(this.typeMap.getNode(origParentNode));
                }
                checkTypeSpecialisation(parentNodes, lhs, rhs);
            } catch (FormatException exc) {
                this.errors.addAll(exc.getErrors());
            }
            this.errors.throwException();
            for (Map.Entry<RuleNode,Color> colorEntry : origin.colorMap.entrySet()) {
                this.pattern.colorMap
                    .put(globalTypeMap.getNode(colorEntry.getKey()), colorEntry.getValue());
            }
        }

        /**
         * Constructs a typed version of a given rule graph.
         * {@link #globalTypeMap} is updated with all new elements.
         * @param graph the untyped input graph
         * @param parentTypeMap typing inherited from the parent level;
         * may be {@code null} if there is no parent level
         * @param typeMap typing constructed for this level;
         * may be {@code null} if this is a NAC graph of which the typing
         * should not be recorded
         * @return a typed version of the input graph
         */
        private RuleGraph toTypedGraph(RuleGraph graph, RuleGraphMorphism parentTypeMap,
                                       RuleGraphMorphism typeMap) {
            RuleGraph result = createGraph(graph.getName());
            try {
                RuleGraphMorphism typing = getTypeGraph().analyzeRule(graph, parentTypeMap);
                this.errors.applyInverse(typing);
                if (typeMap != null) {
                    typeMap.putAll(typing);
                }
                for (Map.Entry<RuleNode,RuleNode> nodeEntry : typing.nodeMap().entrySet()) {
                    RuleNode key = nodeEntry.getKey();
                    RuleNode image = nodeEntry.getValue();
                    assert image != null;
                    RuleNode globalImage = this.globalTypeMap.getNode(key);
                    if (globalImage == null) {
                        this.globalTypeMap.putNode(key, image);
                    }
                    result.addNode(image);
                }
                for (Map.Entry<RuleEdge,RuleEdge> edgeEntry : typing.edgeMap().entrySet()) {
                    RuleEdge key = edgeEntry.getKey();
                    RuleEdge image = edgeEntry.getValue();
                    assert image != null;
                    RuleEdge globalImage = this.globalTypeMap.getEdge(key);
                    if (globalImage == null) {
                        this.globalTypeMap.putEdge(key, globalImage = image);
                    }
                    result.addEdgeContext(globalImage);
                }
                result.addVarSet(graph.varSet());
            } catch (FormatException e) {
                this.errors.addAll(e.getErrors());
            }
            return result;
        }

        /**
         * If the RHS type for a reader node is changed w.r.t. the LHS type,
         * the LHS type has to be sharp and the RHS type a subtype of it.
         * @param parentNodes nodes from a higher quantification level
         * @throws FormatException if there are typing errors
         */
        private void checkTypeSpecialisation(Set<RuleNode> parentNodes, RuleGraph lhs,
                                             RuleGraph rhs) throws FormatException {
            FormatErrorSet errors = createErrors();
            for (RuleNode node : rhs.nodeSet()) {
                TypeNode nodeType = node.getType();
                if (nodeType.isAbstract() && !lhs.containsNode(node)
                    && node.getTypeGuards().isEmpty()) {
                    errors
                        .add("Creation of abstract %s-node not allowed", nodeType.label().text(),
                             node);
                }
            }
            // check for ambiguous mergers
            List<RuleEdge> mergers = new ArrayList<>();
            Set<RuleNode> mergedNodes = new HashSet<>();
            for (RuleEdge edge : rhs.edgeSet()) {
                if (isMerger(edge)) {
                    mergers.add(edge);
                    RuleNode source = edge.source();
                    TypeNode sourceType = source.getType();
                    RuleNode target = edge.target();
                    TypeNode targetType = target.getType();
                    if (!targetType.getSupertypes().containsAll(source.getMatchingTypes())) {
                        errors
                            .add("Actual type of merged %s-node may be subtype of merge target",
                                 sourceType.label().text(), edge);
                    } else if (!mergedNodes.add(source)) {
                        errors
                            .add("%s-node is merged with two distinct nodes",
                                 sourceType.label().text(), source);
                    } else if (isUniversal(target) && !haveMinType(target)) {
                        errors
                            .add("Actual target types of %s-merger may be ambiguous",
                                 sourceType.label().text(), edge);
                    } else if (!getTypeGraph().isSubtype(targetType, sourceType)) {
                        errors
                            .add("Merged %s-node must be supertype of %s",
                                 sourceType.label().text(), targetType.label().text(), source);
                    } else if (source.getType().isSort()) {
                        errors
                            .add("Primitive %s-node can't be merged", sourceType.label().text(),
                                 source);
                    }
                } else {
                    TypeEdge edgeType = edge.getType();
                    if (edgeType != null && edgeType.isAbstract() && !lhs.containsEdge(edge)) {
                        errors
                            .add("Creation of abstract %s-edge not allowed",
                                 edgeType.label().text(), edge);
                    }
                }
            }
            // check for non-injectively matched merge sources
            if (!isInjective()) {
                outer: for (RuleEdge merger1 : mergers) {
                    for (RuleEdge merger2 : mergers) {
                        // only check lower left half of matrix
                        if (merger1 == merger2) {
                            continue outer;
                        }
                        RuleNode source1 = merger1.source();
                        RuleNode source2 = merger2.source();
                        RuleNode target1 = merger1.target();
                        RuleNode target2 = merger2.target();
                        if (!injective(source1, source2) && !target1.equals(target2)
                            && !haveMinType(target1, target2)) {
                            errors
                                .add("Non-injectively matched mergers have ambiguous target types",
                                     merger1, merger2);
                        }
                    }
                }
            }
            errors.throwException();
        }

        /** Tests if a given node is matched on a universal level. */
        private boolean isUniversal(RuleNode node) {
            LevelPattern highestLevel = this.pattern;
            LevelPattern parent = highestLevel.parent;
            while (parent != null && parent.rhs.containsNode(node)) {
                highestLevel = parent;
                parent = highestLevel.parent;
            }
            return highestLevel.getIndex().isUniversal();
        }

        private boolean injective(RuleNode n1, RuleNode n2) {
            boolean result = false;
            // check for type overlap
            Set<TypeNode> types = new HashSet<>(n1.getMatchingTypes());
            types.retainAll(n2.getMatchingTypes());
            result = types.isEmpty();
            if (!result) {
                // check for != edges
                RuleLabel injection = new RuleLabel(RegExpr.empty().neg());
                for (RuleEdge edge : this.pattern.lhs.edgeSet(injection)) {
                    if (edge.source().equals(n1) && edge.target().equals(n2)
                        || edge.source().equals(n2) && edge.target().equals(n1)) {
                        result = true;
                        break;
                    }
                }
            }
            if (!result) {
                // check for NACs
                for (RuleGraph nac : this.pattern.nacs) {
                    Set<RuleNode> nacNodes = nac.nodeSet();
                    Set<RuleEdge> nacEdges = nac.edgeSet();
                    if (nacNodes.size() == 2 && nacNodes.contains(n1) && nacNodes.contains(n2)
                        && nacEdges.size() == 1 && nacEdges.iterator().next().label().isEmpty()) {
                        result = true;
                        break;
                    }
                }
            }
            if (!result && this.parent != null && this.parent.pattern.lhs.containsNode(n1)
                && this.parent.pattern.lhs.containsNode(n2)) {
                result = this.parent.injective(n1, n2);
            }
            return result;
        }

        /** Tests if the host nodes that can be matched non-injectively by
         * a given non-empty set of rule nodes are certain to have a minimum type. */
        private boolean haveMinType(RuleNode... mergeTargets) {
            assert mergeTargets.length > 0;
            boolean result = true;
            // collect the common type label variables
            Set<LabelVar> commonVars = null;
            if (mergeTargets.length == 1) {
                commonVars = mergeTargets[0].getVars();
            } else {
                for (RuleNode node : mergeTargets) {
                    if (commonVars == null) {
                        commonVars = new HashSet<>(node.getVars());
                    } else {
                        commonVars.retainAll(node.getVars());
                    }
                }
                assert commonVars != null; // because mergeTargets is not empty
            }
            // if there is a common variable, the types are fixed and equal
            if (commonVars.isEmpty()) {
                // take the union of all merge target types
                Set<TypeNode> allTypes = null;
                if (mergeTargets.length == 1) {
                    allTypes = mergeTargets[0].getMatchingTypes();
                } else {
                    for (RuleNode node : mergeTargets) {
                        if (allTypes == null) {
                            allTypes = new HashSet<>(node.getMatchingTypes());
                        } else {
                            allTypes.addAll(node.getMatchingTypes());
                        }
                    }
                    assert allTypes != null; // because mergeTargets is not empty
                }
                // check that the set of types is linearly ordered
                outer: for (TypeNode one : allTypes) {
                    for (TypeNode two : allTypes) {
                        // we only check the lower left part of the matrix
                        if (two == one) {
                            continue outer;
                        }
                        if (!one.getSubtypes().contains(two)
                            && !one.getSupertypes().contains(two)) {
                            result = false;
                            break outer;
                        }
                    }
                }
            }
            return result;
        }

        /** Tests if a given RHS edge is a merger. */
        private boolean isMerger(RuleEdge rhsEdge) {
            return !this.pattern.lhs.containsEdge(rhsEdge) && rhsEdge.label().isEmpty();
        }

        /**
         * Callback method to create an untyped graph that can serve as LHS or RHS of a rule.
         * The graph is non-simple if the grammar allows parallel edges.
         */
        private RuleGraph createGraph(String name) {
            return new RuleGraph(name, isInjective(),
                !getGrammarProperties().getParallelMode().isMulti(), this.factory);
        }

        private final Level parent;
        private final RuleFactory factory;
        /** The global, rule-wide mapping from untyped to typed rule elements. */
        private final RuleGraphMorphism globalTypeMap;
        /** Combined type map for this level. */
        private final RuleGraphMorphism typeMap;
        /** The typed pattern constructed by this pass. */
        private final LevelPattern pattern;
        /** List of typing errors. */
        private final FormatErrorSet errors = createErrors();
    }
}
