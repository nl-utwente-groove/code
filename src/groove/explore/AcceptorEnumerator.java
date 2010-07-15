/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.explore;

import groove.explore.encode.EncodedEnabledRule;
import groove.explore.encode.EncodedRuleFormula;
import groove.explore.encode.EncodedRuleMode;
import groove.explore.encode.TemplateList;
import groove.explore.encode.Template.Template0;
import groove.explore.encode.Template.Template1;
import groove.explore.encode.Template.Template2;
import groove.explore.prettyparse.PAll;
import groove.explore.prettyparse.PIdentifier;
import groove.explore.prettyparse.POptional;
import groove.explore.prettyparse.PSequence;
import groove.explore.result.Acceptor;
import groove.explore.result.AnyStateAcceptor;
import groove.explore.result.FinalStateAcceptor;
import groove.explore.result.NoStateAcceptor;
import groove.explore.result.RuleApplicableCondition;
import groove.explore.result.RuleAppliedCondition;
import groove.explore.result.RuleFormula;
import groove.explore.result.StateAcceptor;
import groove.explore.result.TransitionAcceptor;
import groove.lts.GTS;
import groove.trans.Rule;

/**
 * <!=========================================================================>
 * AcceptorEnumerator enumerates all acceptors that are available in GROOVE.
 * With this enumeration, it is possible to create an editor for acceptors
 * (inherited method createEditor, stored results as a Serialized) and to
 * parse an acceptor from a Serialized (inherited method parse).
 * <!=========================================================================>
 * @author Maarten de Mol
 */
public class AcceptorEnumerator extends TemplateList<Acceptor> {

    private static final String ACCEPTOR_TOOLTIP =
        "<HTML>"
            + "An acceptor is a predicate that is applied each time the LTS is "
            + "updated<I>*</I>.<BR>"
            + "Information about each acceptor success is added to the result "
            + "set of the exploration.<BR>"
            + "This result set can be used to interrupt exploration.<BR>"
            + "<I>(*)<I>The LTS is updated when a transition is applied, or "
            + "when a new state is reached." + "</HTML>";

    /**
     * Enumerates the available acceptors one by one. An acceptor is defined
     * by means of a Template<Acceptor> instance.
     */
    public AcceptorEnumerator() {
        super("acceptor", ACCEPTOR_TOOLTIP);

        addTemplate(new Template0<Acceptor>("final", "Final States",
            "This acceptor succeeds when a state is added to the LTS that is "
                + "<I>final</I>. A state is final when no rule is applicable "
                + "on it (or rule application results in the same state).") {

            @Override
            public Acceptor create(GTS gts) {
                return new FinalStateAcceptor();
            }
        });

        addTemplate(new Template2<Acceptor,Rule,Boolean>("inv",
            "Check Invariant",
            "This acceptor succeeds when a state is reached in which the "
                + "indicated rule is applicable. Note that this is detected "
                + "<I>before</I> the rule has been applied.<BR> "
                + "This acceptor ignores rule priorities.", new PSequence(
                new POptional("!", "mode", EncodedRuleMode.NEGATIVE,
                    EncodedRuleMode.POSITIVE), new PIdentifier("rule")),
            "rule", new EncodedEnabledRule(), "mode", new EncodedRuleMode()) {

            @Override
            public Acceptor create(GTS gts, Rule rule, Boolean mode) {
                return new StateAcceptor(mode,
                    new RuleApplicableCondition(rule));
            }
        });

        addTemplate(new Template1<Acceptor,Rule>(
            "ruleapp",
            "Rule Application",
            "This acceptor succeeds when a transition of the indicated rule is "
                + "added to the LTS. Note that this is detected <I>after</I> "
                + "the rule has been applied (which means that rule priorities "
                + "are taken into account).", new PIdentifier("rule"), "rule",
            new EncodedEnabledRule()) {

            @Override
            public Acceptor create(GTS gts, Rule rule) {
                return new TransitionAcceptor(new RuleAppliedCondition(rule));
            }
        });

        addTemplate(new Template1<Acceptor,RuleFormula>(
            "formula",
            "Rule Formula",
            "This acceptor is a variant of Check Invariant that succeeds when a"
                + " state is reached in which an arbitrary rule <i>formula</i> "
                + "is applicable.", new PAll("formula"), "formula",
            new EncodedRuleFormula()) {

            @Override
            public Acceptor create(GTS gts, RuleFormula formula) {
                return new StateAcceptor(formula);
            }
        });

        addTemplate(new Template0<Acceptor>("any", "Any State",
            "This acceptor succeeds whenever a state is added to the LTS.") {

            @Override
            public Acceptor create(GTS gts) {
                return new AnyStateAcceptor();
            }
        });

        addTemplate(new Template0<Acceptor>("none", "No State",
            "This acceptor always fails whenever a state is added to the LTS.") {

            @Override
            public Acceptor create(GTS gts) {
                return new NoStateAcceptor();
            }
        });
    }
}