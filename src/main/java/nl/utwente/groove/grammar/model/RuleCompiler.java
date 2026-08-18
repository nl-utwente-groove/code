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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.Action.Role;
import nl.utwente.groove.grammar.Condition;
import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.grammar.ResourceProperties;
import nl.utwente.groove.grammar.ResourceProperties.Key;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectElement;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.aspect.NormalAspectGraph;
import nl.utwente.groove.grammar.model.GraphBasedModel.TypeModelMap;
import nl.utwente.groove.grammar.model.RuleModel.Index;
import nl.utwente.groove.grammar.model.RuleModel.RuleModelMap;
import nl.utwente.groove.grammar.rule.MatchChecker;
import nl.utwente.groove.grammar.rule.MethodName;
import nl.utwente.groove.grammar.rule.RuleEdge;
import nl.utwente.groove.grammar.rule.RuleFactory;
import nl.utwente.groove.grammar.rule.RuleGraphMorphism;
import nl.utwente.groove.grammar.rule.RuleNode;
import nl.utwente.groove.grammar.type.TypeEdge;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Compiler from a rule aspect graph to a {@link Rule}.
 * A single instance compiles a single rule; the intermediate quantification
 * level tree and the resulting element and type maps remain available after
 * compilation, also partially if compilation fails halfway.
 * <p>
 * The translation is staged: the quantification level index tree is built
 * from the nesting aspects ({@link LevelIndexTree}), the aspect elements are
 * distributed over the levels ({@link LevelDistribution}), converted to
 * untyped per-level patterns split into LHS/RHS/NACs ({@link PatternBuilder}),
 * typed ({@link PatternTyper}, producing the typed {@link LevelPattern}s),
 * and assembled into a tree of {@link Condition}s and rules
 * ({@link ConditionAssembler}); the parameter signature is extracted
 * separately ({@link SignatureExtractor}).
 * @author Arend Rensink
 * @version $Revision$
 */
class RuleCompiler {
    /**
     * Constructs a compiler for a given rule aspect graph.
     * @param grammar the grammar model to which the rule belongs
     * @param source the source aspect graph of the rule
     * @param normalSource the normalised version of {@code source}
     * @param role the (declared or inferred) action role of the rule
     */
    RuleCompiler(GrammarModel grammar, AspectGraph source, AspectGraph normalSource, Role role) {
        this.grammar = grammar;
        this.source = source;
        this.normalSource = normalSource;
        this.role = role;
        this.qualName = source.getQualName();
    }

    /**
     * Compiles the rule from the (error-free) source graph.
     * The level distribution and the model and type maps are stored in this
     * compiler as a side effect, insofar as their construction succeeded.
     * @throws FormatException if the source graph cannot be converted to a valid rule
     */
    Rule compile() throws FormatException {
        this.ruleFactory = RuleFactory.newInstance(getTypeGraph().getFactory());
        this.modelMap = new RuleModelMap(this.ruleFactory);
        LevelIndexTree indexTree = LevelIndexTree.from(getNormalSource(), getQualName());
        LevelDistribution distribution = this.distribution
            = LevelDistribution.from(this, getNormalSource(), indexTree);
        RuleModelMap untypedModelMap = new RuleModelMap();
        SortedMap<Index,LevelPattern> untypedPatterns;
        try {
            untypedPatterns = new PatternBuilder(this, untypedModelMap).build(distribution);
        } catch (FormatException e) {
            throw new FormatException(e.getErrors().applyInverse(untypedModelMap));
        }
        RuleGraphMorphism typingMap = new RuleGraphMorphism(this.ruleFactory);
        SortedMap<Index,LevelPattern> patternMap;
        try {
            patternMap = new PatternTyper(this, typingMap).type(untypedPatterns);
        } catch (FormatException e) {
            throw new FormatException(e.getErrors().applyInverse(untypedModelMap));
        }
        // compose the untyped model map with the typing morphism
        for (Map.Entry<AspectNode,RuleNode> nodeEntry : untypedModelMap.nodeMap().entrySet()) {
            RuleNode image = typingMap.getNode(nodeEntry.getValue());
            if (image != null) {
                this.modelMap.putNode(nodeEntry.getKey(), image);
            }
        }
        for (Map.Entry<AspectEdge,RuleEdge> edgeEntry : untypedModelMap.edgeMap().entrySet()) {
            RuleEdge image = typingMap.getEdge(edgeEntry.getValue());
            if (image != null) {
                this.modelMap.putEdge(edgeEntry.getKey(), image);
            }
        }
        this.typeMap = new TypeModelMap(getTypeGraph().getFactory());
        for (Map.Entry<AspectNode,RuleNode> nodeEntry : this.modelMap.nodeMap().entrySet()) {
            this.typeMap.putNode(nodeEntry.getKey(), nodeEntry.getValue().getType());
        }
        for (Map.Entry<AspectEdge,RuleEdge> edgeEntry : this.modelMap.edgeMap().entrySet()) {
            var edgeType = (@NonNull TypeEdge) edgeEntry.getValue().getType();
            this.typeMap.putEdge(edgeEntry.getKey(), edgeType);
        }
        return computeRule(patternMap);
    }

    /** Returns the qualified name of the rule being compiled. */
    QualName getQualName() {
        return this.qualName;
    }

    /** The qualified name of the rule being compiled. */
    private final QualName qualName;

    /** Returns the grammar model to which the rule belongs. */
    private GrammarModel getGrammar() {
        return this.grammar;
    }

    /** The grammar model to which the rule belongs. */
    private final GrammarModel grammar;

    /** Returns the source aspect graph of the rule. */
    private AspectGraph getSource() {
        return this.source;
    }

    /** The source aspect graph of the rule. */
    private final AspectGraph source;

    /** Returns the normalised source aspect graph of the rule. */
    AspectGraph getNormalSource() {
        return this.normalSource;
    }

    /** The normalised source aspect graph of the rule. */
    private final AspectGraph normalSource;

    /** Returns the action role of the rule. */
    private Role getRole() {
        return this.role;
    }

    /** The action role of the rule. */
    private final Role role;

    /** Convenience method to retrieve the grammar properties. */
    GrammarProperties getGrammarProperties() {
        return getGrammar().getProperties();
    }

    /** Convenience method to retrieve the (implicit or explicit) type graph of the grammar. */
    TypeGraph getTypeGraph() {
        return getGrammar().getTypeGraph();
    }

    /**
     * Indicates if the rule is to be matched injectively.
     * @see RuleModel#isInjective()
     */
    boolean isInjective() {
        return ResourceProperties.isInjective(getSource()) || getGrammarProperties().isInjective();
    }

    boolean isRhsAsNac() {
        return getGrammarProperties().isRhsAsNac();
    }

    boolean isCheckCreatorEdges() {
        return getGrammarProperties().isCheckCreatorEdges();
    }

    /**
     * Tests if the rule may be used as a graph condition.
     * @see RuleModel#testAsProperty(AspectGraph)
     */
    private FormatError testAsProperty() {
        return RuleModel.testAsProperty(getSource());
    }

    /** Returns the mapping from source graph elements to (typed) rule elements.
     * Only valid after a successful {@link #compile()}. */
    RuleModelMap getModelMap() {
        return this.modelMap;
    }

    /** Returns the mapping from source graph elements to type elements;
     * {@code null} if compilation failed before typing. */
    TypeModelMap getTypeMap() {
        return this.typeMap;
    }

    /** Returns a mapping from rule nesting levels to sets of aspect elements
     * on that level; {@code null} if compilation failed before the level
     * distribution was built. */
    TreeMap<Index,Set<AspectElement>> getLevelTree() {
        var distribution = this.distribution;
        if (distribution == null) {
            return null;
        }
        TreeMap<Index,Set<AspectElement>> result = new TreeMap<>();
        for (Map.Entry<Index,LevelDistribution.Level> levelEntry : distribution
            .getLevelMap()
            .entrySet()) {
            Index index = levelEntry.getKey();
            LevelDistribution.Level level = levelEntry.getValue();
            Set<AspectElement> elements = new HashSet<>();
            result.put(index, elements);
            elements.addAll(level.modelNodes);
            elements.addAll(level.modelEdges);
        }
        return result;
    }

    private RuleFactory ruleFactory;
    /**
     * Mapping from the elements of the aspect graph representation to the
     * corresponding elements of the rule.
     */
    private RuleModelMap modelMap;
    /** Map from source model to types. */
    private TypeModelMap typeMap;
    /** The distribution of rule elements over the quantification levels. */
    private LevelDistribution distribution;

    /**
     * Callback method to compute a rule from the source graph. All auxiliary data
     * structures are assumed to be initialised but empty. After method return,
     * the structures are filled.
     * @throws FormatException if the model cannot be converted to a valid rule
     */
    private Rule computeRule(SortedMap<Index,LevelPattern> patternMap) throws FormatException {
        FormatErrorSet errors = createErrors();
        Rule result = new ConditionAssembler(this).assemble(patternMap, errors);
        // infer and set the role
        Role role = getRole();
        if (role.isProperty()) {
            FormatError error = testAsProperty();
            if (error != null) {
                errors.add("Rule is unsuitable as %s: %s", role, error);
            }
        }
        if (result != null) {
            ResourceProperties properties = ResourceProperties.getProperties(getSource());
            result.setProperties(properties);
            result.setCheckDangling(getGrammarProperties().isCheckDangling());
            SignatureExtractor parameters = new SignatureExtractor(this);
            result.setSignature(parameters.getSignature(), parameters.getHiddenPars());
            result.setRole(role);
            try {
                Optional<MethodName> filter
                    = properties.parseProperty(Key.FILTER).value(MethodName.VALUE_TYPE);
                if (filter.isPresent()) {
                    result.setMatchFilter(MatchChecker.createChecker(filter.get(), getGrammar()));
                }
            } catch (FormatException exc) {
                result = null;
                errors.addAll(exc.getErrors());
            }
        }
        // only fix if the rule is not null
        if (result != null) {
            try {
                result.setFixed();
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
        }
        errors.applyInverse(this.modelMap).throwException();
        assert result != null;
        return result;
    }

    /** Returns an element map from the normalised graph to the source graph. */
    private Map<AspectElement,AspectElement> normalToSourceMap() {
        var result = this.normalToSourceMap;
        if (result == null) {
            var normal = getNormalSource();
            if (normal instanceof NormalAspectGraph ng) {
                result = ng.normalToOriginalMap();
            } else {
                result = new HashMap<>();
            }
            this.normalToSourceMap = result;
        }
        return result;
    }

    /** Mapping from normalised source model to source model. */
    private @Nullable Map<AspectElement,AspectElement> normalToSourceMap;

    /** Callback factory method for a format error set, with the
     * normal-to-source element map pre-applied so that errors get
     * source-graph context. */
    FormatErrorSet createErrors() {
        var result = new FormatErrorSet();
        result.apply(normalToSourceMap());
        return result;
    }

}
