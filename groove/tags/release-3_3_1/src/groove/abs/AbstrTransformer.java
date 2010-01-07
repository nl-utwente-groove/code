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
 * $Id$
 */
package groove.abs;

import groove.graph.Node;
import groove.graph.NodeFactory;
import groove.trans.DefaultApplication;
import groove.trans.RuleApplication;
import groove.trans.RuleEvent;
import groove.trans.SPOEvent;
import groove.trans.RuleMatch;

import java.util.Collection;

/**
 * An interface for transforming an abstract graph.
 * @author Iovka Boneva
 * @version $Revision $
 */
public class AbstrTransformer {

    /**
     * Computes all transformations of a graph w.r.t. a given match.
     * @param host The abstract graph to be transformed
     * @param match The rule with the match used for transformation.
     * @param nodeFactory The node factory used for new nodes.
     * @param options The parameters of the abstract transformation.
     * @param result Contains the graphs result of the transformation.
     * @return The rule event that is the common label of all transitions.
     */
    public static RuleEvent transform(final AbstrGraph host,
            final RuleMatch match, NodeFactory nodeFactory,
            Abstraction.Parameters options, Collection<AbstrGraph> result) {
        // Compute the possible concrete parts
        ConcretePart.Typing typing = new ConcretePart.Typing() {
            public GraphPattern typeOf(Node n) {
                return host.typeOf(match.getElementMap().getNode(n));
            }
        };

        Collection<ConcretePart> ext =
            ConcretePart.extensions(match.getRule().lhs(), typing,
                host.family(), options.SYMMETRY_REDUCTION, nodeFactory);
        // OPTIM nothing allows to determine whether a given concrete part is
        // indeed possible (w.r.t. multiplicities)

        // For all concrete part, generate the set of materialisations and
        // transform
        for (ConcretePart cp : ext) {
            SetMaterialisations smat =
                new SetMaterialisations(cp, (DefaultAbstrGraph) host,
                    match.getElementMap(), options);

            // A second rule events is needed for the actual transformation
            // (with matching into the concrete part)
            RuleEvent transfEvent =
                new SPOEvent(match.getRule(),
                    smat.updateMatch(match.getElementMap()), nodeFactory, false);
            RuleApplication appl =
                new DefaultApplication(transfEvent, cp.graph());

            Collection<AbstrGraph> transformations =
                smat.transform(appl, nodeFactory);

            for (AbstrGraph g : transformations) {
                result.add(g);
            }
        }

        // This rule event is only used as a label for the transition in the GTS
        RuleEvent transitionEvent =
            new SPOEvent(match.getRule(), match.getElementMap(), nodeFactory,
                false);
        return transitionEvent;
    }

}
