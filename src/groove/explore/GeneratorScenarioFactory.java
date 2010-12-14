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
package groove.explore;

import groove.explore.result.Acceptor;
import groove.explore.result.FinalStateAcceptor;
import groove.explore.result.Result;
import groove.explore.strategy.Boundary;
import groove.explore.strategy.BoundedModelCheckingStrategy;
import groove.explore.strategy.GraphNodeSizeBoundary;
import groove.explore.strategy.ModelCheckingStrategy;
import groove.explore.strategy.RuleSetBorderBoundary;
import groove.explore.strategy.RuleSetStartBoundary;
import groove.explore.strategy.Strategy;
import groove.trans.Rule;
import groove.verify.ModelChecking;

import java.util.Scanner;
import java.util.Set;

/**
 * Offers factory methods for different {@link Scenario}s needed by the
 * {@link groove.util.Generator}.
 * @author Iovka Boneva
 */
public class GeneratorScenarioFactory {
    /**
     * Creates a scenario handler collecting all final states, and with the
     * strategy given as parameter.
     */
    public static Scenario getScenarioHandler(final Strategy strategy,
            final String description, final String name) {
        return new DefaultScenario(strategy, new FinalStateAcceptor(), name,
            description);
    }

    /**
     * Creates a scenario handler finding a single final state, with the
     * strategy given as parameter.
     */
    public static Scenario getFinalStateScenarioHandler(
            final Strategy strategy, final String description, final String name) {
        Acceptor acceptor = new FinalStateAcceptor(new Result(1));
        return new DefaultScenario(strategy, acceptor, name, description);
    }

    /**
     * Constructs a bounded model checking scenario with a fixed bound. The
     * property to be checked is obtained from the command line.
     * @param strategy Strategy for the scenario.
     * @param description A one-sentence description of the scenario.
     * @param name A short (one or few words) description of the scenario. Is to
     *        be used in menus, or as identification (for instance in
     *        command-line options).
     */
    public static ModelCheckingScenario getBoundedModelCheckingScenario(
            final BoundedModelCheckingStrategy strategy,
            final String description, final String name) {
        ModelCheckingScenario result =
            new ModelCheckingScenario(strategy, name, description) {
                @Override
                protected String getProperty() {
                    System.out.println("Enter the LTL formula to verify:");
                    Scanner keyboard = new Scanner(System.in);
                    return keyboard.nextLine();
                }
            };
        result.setBoundary(new GraphNodeSizeBoundary(8, 2));
        return result;
    }

    /**
     * Constructs a bounded model checking scenario for a given property. The
     * bound is a {@link GraphNodeSizeBoundary} with given initial bound and
     * step size.
     * @param strategy Strategy for the scenario.
     * @param description A one-sentence description of the scenario.
     * @param name A short (one or few words) description of the scenario. Is to
     *        be used in menus, or as identification (for instance in
     *        command-line options).
     * @param initialBound initial value of the boundary
     * @param stepSize step size for the boundary
     * @param property the property to be checked
     */
    public static <T> ModelCheckingScenario getBoundedModelCheckingScenario(
            final BoundedModelCheckingStrategy strategy,
            final String description, final String name,
            final int initialBound, final int stepSize, final String property) {
        ModelCheckingScenario result =
            new ModelCheckingScenario(strategy, name, description);
        result.setProperty(property);
        result.setBoundary(new GraphNodeSizeBoundary(initialBound, stepSize));
        return result;
    }

    /**
     * Constructs a bounded model checking scenario for a given property. The
     * bound is determined by rules which may not be taken.
     * @param strategy Strategy for the scenario.
     * @param description A one-sentence description of the scenario.
     * @param name A short (one or few words) description of the scenario. Is to
     *        be used in menus, or as identification (for instance in
     *        command-line options).
     * @param ruleSet set of rules which take the state space to the next step
     * @param property the property to be checked
     */
    public static <T> ModelCheckingScenario getBoundedModelCheckingScenario(
            final BoundedModelCheckingStrategy strategy,
            final String description, final String name,
            final Set<Rule> ruleSet, final String property) {
        ModelCheckingScenario result =
            new ModelCheckingScenario(strategy, name, description) {
                @Override
                protected Boundary getBoundary() {
                    Boundary result;
                    if (ModelChecking.START_FROM_BORDER_STATES) {
                        result = new RuleSetBorderBoundary(ruleSet);
                    } else {
                        result = new RuleSetStartBoundary(ruleSet);
                    }
                    return result;
                }
            };
        result.setProperty(property);
        return result;
    }

    /**
     * Constructs a model checking scenario for a given property.
     * @param strategy Strategy for the scenario.
     * @param description A one-sentence description of the scenario.
     * @param name A short (one or few words) description of the scenario. Is to
     *        be used in menus, or as identification (for instance in
     *        command-line options).
     * @param property the property to be checked
     */
    public static ModelCheckingScenario getModelCheckingScenario(
            final ModelCheckingStrategy strategy, final String description,
            final String name, final String property) {
        ModelCheckingScenario result =
            new ModelCheckingScenario(strategy, name, description);
        result.setProperty(property);
        return result;
    }
}