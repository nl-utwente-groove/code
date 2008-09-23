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

import groove.explore.ScenarioHandlerFactory.AbstractConditionalScenarioHandler;
import groove.explore.ScenarioHandlerFactory.AbstractScenarioHandler;
import groove.explore.result.Acceptor;
import groove.explore.result.ConditionalAcceptor;
import groove.explore.result.CycleAcceptor;
import groove.explore.result.ExploreCondition;
import groove.explore.result.FinalStateAcceptor;
import groove.explore.result.Result;
import groove.explore.strategy.Boundary;
import groove.explore.strategy.BoundedModelCheckingStrategy;
import groove.explore.strategy.ConditionalStrategy;
import groove.explore.strategy.GraphNodeSizeBoundary;
import groove.explore.strategy.ModelCheckingStrategy;
import groove.explore.strategy.RuleSetBorderBoundary;
import groove.explore.strategy.RuleSetStartBoundary;
import groove.explore.strategy.Strategy;
import groove.gui.FormulaDialog;
import groove.gui.Simulator;
import groove.lts.ProductGTS;
import groove.trans.Rule;
import groove.verify.ModelChecking;

import java.util.Scanner;
import java.util.Set;


/**
 * Offers factory methods for different {@link GeneratorScenarioHandler}s needed
 * by the {@link groove.util.Generator}. 
 * @author Iovka Boneva
 */
public class GeneratorScenarioHandlerFactory {
	/**
	 * Creates a scenario handler collecting all final states, 
	 * and with the strategy given as parameter. 
	 */
	public static ScenarioHandler getScenarioHandler(final Strategy strategy, final String description, final String name) {
		return new AbstractScenarioHandler() {
			@Override
			public String getDescription() { return description; }

			@Override
			public String getName() { return name; }

			@Override
			public void playScenario() {
				Acceptor acceptor = new FinalStateAcceptor();
				playScenario(new DefaultScenario(strategy, acceptor));
			}
		};
	}

	/**
	 * Creates a scenario handler finding a single final state,
	 * with the strategy given as parameter.
	 */
	public static ScenarioHandler getFinalStateScenarioHandler(final Strategy strategy, final String description, final String name) {
		return new AbstractScenarioHandler() {
			@Override
			public String getDescription() { return description; }

			@Override
			public String getName() { return name; }

			@Override
			public void playScenario() {
				Acceptor acceptor = new FinalStateAcceptor(new Result(1));
				playScenario(new DefaultScenario(strategy, acceptor));
			}
		};	
	}
	
	/** Retrieves a conditional scenario handler for a scenario
	 * based on an conditional acceptor and with given (GraphState) result.
	 * @param <C> Type of the condition.
	 * @param str Strategy for the scenario.
	 * @param acc Acceptor for the scenario.
	 * @param description A one-sentence description of the scenario.
	 * @param name A short (one or few words) description of the scenario. Is to be 
	 * used in menus, or as identification (for instance in command-line options).
	 */
	public static <C> ConditionalScenarioHandler<C> getConditionalScenario(
			final Strategy str, final Class<?> type, final ConditionalAcceptor<C> acc, 
			final String description, final String name) {
		return new AbstractConditionalScenarioHandler<C>() {

			@Override
			public String getName() {
				if (this.explCond == null) {
					return name;
				}
				return name + 
						(explCond.isNegated() ? " !" : " ") +
						"<" +
						this.condName +
						">";
			}

			@Override
			public String getDescription() { return description; }

			@Override
			public void playScenario() {
				acc.setCondition(this.explCond);
				playScenario(new DefaultScenario(str, acc));
			}

			public void setCondition(ExploreCondition<C> explCond, String name) {
				this.explCond = explCond;
				this.condName = name;
			}
			
			private ExploreCondition<C> explCond;
			private String condName = ""; // Is it used ?

			public Class<?> getConditionType() { return type; }
		};
	}
	
	/** 
	 * Constructs a conditional scenario handler based on a conditional strategy and
	 * with empty acceptor and empty result.
	 * @param <C> The generic type for the explore condition.
	 * @param strategy The strategy
	 * @param description A one sentence description of the scenario.
	 * @param name A short name for the scenario. Is to be 
	 * used in menus, or as identification (for instance in command-line options).
	 */
	public static <C> ConditionalScenarioHandler<C> getConditionalScenario(
			final ConditionalStrategy strategy, final Class<?> type,
			final String description, final String name) {
		return new AbstractConditionalScenarioHandler<C>() {

			@Override
			public String getName() {
				if (this.explCond == null) {
					return name;
				}
				return name + 
						(explCond.isNegated() ? " !" : " ") +
						"<" +
						this.condName +
						">";
			}
			
			@Override
			public String getDescription() { return description; }

			@Override
			public void playScenario() {
				strategy.setExploreCondition(explCond);
				playScenario(new DefaultScenario(strategy, new FinalStateAcceptor()));
			}

			public Class<?> getConditionType() { return type; }

			public void setCondition(ExploreCondition<C> explCond, String name) {
				assert type.isAssignableFrom(explCond.getConditionType()) : "Incompatible types: " + explCond.getConditionType() + " and " + type;
				this.explCond = explCond;
				this.condName = name;
			}
			
			private ExploreCondition<C> explCond;
			private String condName = "";  // see how to use it			
		};
	}
	
	/** Retrieves a scenario handler for a scenario constructed from its components.
	 * @param <T> Type of the result of the scenario.
	 * @param str Strategy for the scenario.
	 * @param description A one-sentence description of the scenario.
	 * @param name A short (one or few words) description of the scenario. Is to be 
	 * used in menus, or as identification (for instance in command-line options).
	 */
	public static <T> ScenarioHandler getBoundedModelCheckingScenario(
			final BoundedModelCheckingStrategy str,
			final String description,
			final String name) {
		return new AbstractScenarioHandler() {

			@Override
			public String getDescription() { return description; }

			@Override
			public String getName() { return name; }

			@Override
			public void playScenario() {
				CycleAcceptor cycleAcc = new CycleAcceptor(new Result(1));
				cycleAcc.setStrategy(str);
				DefaultScenario scenar = new DefaultScenario(str, cycleAcc);
				Boundary boundary = new GraphNodeSizeBoundary(8,2);

				System.out.println("Enter the LTL formula to verify:");
				Scanner keyboard = new Scanner(System.in);
				String property = keyboard.nextLine();
				str.setProperty(property);
				str.setGTS(getGTS());
				str.setProductGTS(new ProductGTS(getGTS().getGrammar()));
				str.setResult(cycleAcc.getResult());
				str.setBoundary(boundary);

				Runtime runtime = Runtime.getRuntime();
				playScenario(scenar);
				if (!getResult().getValue().isEmpty()) {
					System.err.println("A counter-example of length " + getResult().getValue().size() + " has been found: " + getResult().getValue());
				}
	            System.runFinalization();
	            System.gc();
	            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
	            
	            System.err.println("Memory in use: " + (usedMemory / 1024) + " kB");
			}

			protected String getProperty(Simulator sim) {
				FormulaDialog dialog = sim.getFormulaDialog();
				dialog.showDialog(sim.getFrame());
				String property = dialog.getProperty();
				if (property != null) {
					return property;
				}
				return null;
			}
		};
	}

	public static <T> ScenarioHandler getBoundedModelCheckingScenario(
			final BoundedModelCheckingStrategy strategy,
			final String description,
			final String name,
			final int initialBound,
			final int stepSize,
			final String property) {
		return new AbstractScenarioHandler() {

			@Override
			public String getDescription() { return description; }

			@Override
			public String getName() { return name; }

			@Override
			public void playScenario() {
				CycleAcceptor acceptor = new CycleAcceptor(new Result(1));
				acceptor.setStrategy(strategy);
				DefaultScenario scenario = new DefaultScenario(strategy, acceptor);
				Boundary boundary = new GraphNodeSizeBoundary(initialBound,stepSize);

				strategy.setProperty(property);
				strategy.setGTS(getGTS());
				productGTS = new ProductGTS(getGTS().getGrammar());
				strategy.setProductGTS(productGTS);
				strategy.setResult(getResult());
				strategy.setBoundary(boundary);
				
				Runtime runtime = Runtime.getRuntime();
				playScenario(scenario);
	            System.runFinalization();
	            System.gc();
	            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
	            
	            System.err.println("Memory in use: " + (usedMemory / 1024) + " kB");
			}

			@Override
			public ProductGTS getProductGTS() {
				return productGTS;
			}
			private ProductGTS productGTS;
		};
	}

	public static <T> ScenarioHandler getBoundedModelCheckingScenario(
			final BoundedModelCheckingStrategy str,
			final String description,
			final String name,
			final Set<Rule> ruleSet,
			final String property) {
		return new AbstractScenarioHandler() {

			@Override
			public String getDescription() { return description; }

			@Override
			public String getName() { return name; }

			@Override
			public void playScenario() {
				CycleAcceptor acceptor = new CycleAcceptor(new Result(1));
				acceptor.setStrategy(str);
				DefaultScenario scenar = new DefaultScenario(str, acceptor);
				Boundary boundary;
				if (ModelChecking.START_FROM_BORDER_STATES) {
					boundary = new RuleSetBorderBoundary(ruleSet);
				} else {
					boundary = new RuleSetStartBoundary(ruleSet);
				}

				str.setProperty(property);
				str.setGTS(getGTS());
				productGTS = new ProductGTS(getGTS().getGrammar());
				str.setProductGTS(productGTS);
//				str.setProductGTS(new ProductGTS(getGTS().getGrammar()));
				str.setResult(acceptor.getResult());
				str.setBoundary(boundary);
				
				Runtime runtime = Runtime.getRuntime();
				playScenario(scenar);
	            System.runFinalization();
	            System.gc();
	            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
	            
	            System.err.println("Memory in use: " + (usedMemory / 1024) + " kB");
			}
			@Override
			public ProductGTS getProductGTS() {
				return productGTS;
			}
			private ProductGTS productGTS;
		};
	}

	public static <T> ScenarioHandler getModelCheckingScenario(
			final ModelCheckingStrategy strategy,
			final String description,
			final String name,
			final String property) {
		return new AbstractScenarioHandler() {

			@Override
			public String getDescription() { return description; }

			@Override
			public String getName() { return name; }

			@Override
			public void playScenario() {
				CycleAcceptor acceptor = new CycleAcceptor(new Result(1));
				acceptor.setStrategy(strategy);
				DefaultScenario scenar = new DefaultScenario(strategy, acceptor);

				strategy.setProperty(property);
				strategy.setGTS(getGTS());
				productGTS = new ProductGTS(getGTS().getGrammar());
				strategy.setProductGTS(productGTS);
				strategy.setResult(acceptor.getResult());
				
				Runtime runtime = Runtime.getRuntime();
				playScenario(scenar);
	            System.runFinalization();
	            System.gc();
	            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
	            
	            System.err.println("Memory in use: " + (usedMemory / 1024) + " kB");
			}
			@Override
			public ProductGTS getProductGTS() {
				return productGTS;
			}
			private ProductGTS productGTS;
		};
	}
}