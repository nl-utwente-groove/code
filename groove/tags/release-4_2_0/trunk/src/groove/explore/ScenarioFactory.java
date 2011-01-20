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
 * $Id: ScenarioHandlerFactory.java,v 1.5 2008/03/04 14:50:37 kastenberg Exp $
 */
package groove.explore;

import groove.explore.result.Acceptor;
import groove.explore.strategy.BoundedModelCheckingStrategy;
import groove.explore.strategy.ModelCheckingStrategy;
import groove.explore.strategy.Strategy;
import groove.gui.Simulator;

/**
 * A factory for creating scenario handlers by composing a scenario from its
 * strategy, result and acceptor.
 * @author Iovka Boneva
 * @version $Revision$
 */
public class ScenarioFactory {
    /**
     * Retrieves a scenario handler for a scenario constructed from its
     * components.
     * @param <T> Type of the result of the scenario.
     * @param strategy Strategy for the scenario.
     * @param acceptor Acceptor for the scenario.
     * @param description A one-sentence description of the scenario.
     * @param name A short (one or few words) description of the scenario. Is to
     *        be used in menus, or as identification (for instance in
     *        command-line options).
     */
    public static <T> Scenario getScenario(final Strategy strategy,
            final Acceptor acceptor, final String description, final String name) {
        return new DefaultScenario(strategy, acceptor, name, description);
    }

    /**
     * Retrieves a scenario handler for a scenario constructed from its
     * components.
     * @param strategy Strategy for the scenario.
     * @param description A one-sentence description of the scenario.
     * @param name A short (one or few words) description of the scenario. Is to
     *        be used in menus, or as identification (for instance in
     *        command-line options).
     */
    public static Scenario getModelCheckingScenario(
            final ModelCheckingStrategy strategy, final String description,
            final String name, final Simulator sim) {
        return new ModelCheckingScenario(strategy, name, description) {
            /*
             * Moved the check below to the 'doGenerate' method of the Simulator - Maarten de Mol
            @Override
            protected String getProperty() {
                FormulaDialog dialog = sim.getFormulaDialog();
                dialog.showDialog(sim.getFrame());
                return dialog.getProperty();
            }
            */
        };
    }

    /**
     * Retrieves a scenario handler for a scenario constructed from its
     * components.
     * 
     * @param strategy Strategy for the scenario.
     * @param description A one-sentence description of the scenario.
     * @param name A short (one or few words) description of the scenario. Is to
     *        be used in menus, or as identification (for instance in
     *        command-line options).
     */
    public static Scenario getBoundedModelCheckingScenario(
            final BoundedModelCheckingStrategy strategy,
            final String description, final String name, final Simulator sim) {
        return new ModelCheckingScenario(strategy, name, description) {
            /*
             * Moved the check below to the 'doGenerate' method of the Simulator - Maarten de Mol
            @Override
            protected String getProperty() {
                FormulaDialog dialog = sim.getFormulaDialog();
                dialog.showDialog(sim.getFrame());
                return dialog.getProperty();
            }
            */

            /*
             * Moved the check below to the 'doGenerate' method of the Simulator - Maarten de Mol
            @Override
            protected Boundary getBoundary() {
                BoundedModelCheckingDialog dialog =
                    new BoundedModelCheckingDialog();
                dialog.setGrammar(sim.getGTS().getGrammar());
                dialog.showDialog(sim.getFrame());
                return dialog.getBoundary();
            }
            */
        };
    }
}
