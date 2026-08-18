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

import static nl.utwente.groove.grammar.aspect.AspectKind.PARAM_BI;
import static nl.utwente.groove.grammar.aspect.AspectKind.PARAM_IN;
import static nl.utwente.groove.grammar.aspect.AspectKind.Category.ROLE;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.grammar.Signature;
import nl.utwente.groove.grammar.UnitPar;
import nl.utwente.groove.grammar.aspect.AspectContent;
import nl.utwente.groove.grammar.aspect.AspectContent.IntegerContent;
import nl.utwente.groove.grammar.aspect.AspectContent.NullContent;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.rule.RuleNode;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Extractor for the parameter information of a rule: collects the parameter
 * nodes of the model graph into the rule signature and the set of hidden
 * (anchor) parameters.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
class SignatureExtractor {
    /** Extracts the signature information from the compiler's source graph.
     * @param compiler the compiler providing the compilation context
     */
    SignatureExtractor(RuleCompiler compiler) throws FormatException {
        this.compiler = compiler;
        FormatErrorSet errors = new FormatErrorSet();
        this.hiddenPars = new HashSet<>();
        // Mapping from parameter position to parameter
        Map<Integer,UnitPar.RulePar> parMap = new HashMap<>();
        int parCount = 0;
        // collect parameter nodes
        for (AspectNode node : getNormalSource().nodeSet()) {
            // check if the node is a parameter
            if (node.has(Category.PARAM)) {
                AspectContent parContent = node.getContent(Category.PARAM);
                if (parContent instanceof IntegerContent i) {
                    Integer nr = i.get();
                    parCount = Math.max(parCount, nr + 1);
                    try {
                        processNode(parMap, node, nr);
                    } catch (FormatException exc) {
                        errors.addAll(exc.getErrors());
                    }
                } else {
                    assert parContent instanceof NullContent;
                    // this is an unnumbered parameter,
                    // which serves as an explicit anchor node
                    if (!node.has(PARAM_BI)) {
                        throw new FormatException("Anchor node must be '%s'",
                            PARAM_BI.getName(), node);
                    }
                    if (!node.has(ROLE, AspectKind::inLHS)) {
                        throw new FormatException("Anchor node must be in LHS", node);
                    }
                    RuleNode nodeImage = this.compiler.getModelMap().getNode(node);
                    assert nodeImage != null;
                    this.hiddenPars.add(nodeImage);
                }
            }
        }
        errors.throwException();
        // construct the signature
        // test if parameters form a consecutive sequence
        Set<Integer> missingPars = new TreeSet<>();
        for (int i = 0; i < parCount; i++) {
            missingPars.add(i);
        }
        missingPars.removeAll(parMap.keySet());
        if (!missingPars.isEmpty()) {
            errors.add("Parameters %s missing", missingPars);
        }
        errors.throwException();
        UnitPar.RulePar[] sigArray = new UnitPar.RulePar[parCount];
        for (Map.Entry<Integer,UnitPar.RulePar> parEntry : parMap.entrySet()) {
            sigArray[parEntry.getKey()] = parEntry.getValue();
        }
        this.sig = Arrays.asList(sigArray);
    }

    private void processNode(Map<Integer,UnitPar.RulePar> parMap, AspectNode node,
                             Integer nr) throws FormatException {
        var errors = new FormatErrorSet();

        AspectKind nodeKind = node.getKind(ROLE);
        assert nodeKind != null;
        AspectKind parKind = node.getKind(Category.PARAM);
        assert parKind != null;
        RuleNode nodeImage = this.compiler.getModelMap().getNode(node);
        assert nodeImage != null;
        if (parKind == PARAM_IN && nodeKind.isCreator()) {
            errors.add("Input parameter %d cannot be creator node", nr, node);
        }
        if (nodeKind.inNAC()) {
            errors.add("Parameter '%d' may not occur in NAC", nr, node);
        }
        UnitPar.RulePar par = new UnitPar.RulePar(parKind, nodeImage, nodeKind.isCreator());
        this.parOriginMap.put(par, node);
        UnitPar.RulePar oldPar = parMap.put(nr, par);
        if (oldPar != null) {
            errors
                .add("Parameter '%d' defined more than once", nr, node,
                     this.parOriginMap.get(oldPar));
        }
        errors.throwException();
    }

    /** Mapping from parameter nodes to their aspect graph origin to enable better error highlighting. */
    private final Map<UnitPar.RulePar,AspectNode> parOriginMap = new HashMap<>();

    /** Lazily creates and returns the rule's hidden parameters. */
    public Set<RuleNode> getHiddenPars() {
        return this.hiddenPars;
    }

    /** Returns the rule signature. */
    public Signature<UnitPar.RulePar> getSignature() {
        return new Signature<>(this.sig);
    }

    /** Set of all rule parameter nodes */
    private final Set<RuleNode> hiddenPars;
    /** Signature of the rule. */
    private final List<UnitPar.RulePar> sig;

    /** Convenience method to retrieve the normalised source graph from the compiler. */
    private AspectGraph getNormalSource() {
        return this.compiler.getNormalSource();
    }

    /** The compiler providing the compilation context. */
    private final RuleCompiler compiler;
}
