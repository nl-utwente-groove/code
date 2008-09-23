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
 * $Id: ScenarioHandlerFactory.java,v 1.5 2008/03/04 14:50:37 kastenberg Exp $
 */
package groove.explore;

import groove.explore.result.Acceptor;
import groove.explore.result.ConditionalAcceptor;
import groove.explore.result.ExploreCondition;
import groove.explore.result.Result;
import groove.explore.strategy.Boundary;
import groove.explore.strategy.BoundedModelCheckingStrategy;
import groove.explore.strategy.ModelCheckingStrategy;
import groove.explore.strategy.Strategy;
import groove.gui.BoundedModelCheckingDialog;
import groove.gui.Simulator;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.ProductGTS;


/** A factory for creating scenario handlers by composing a scenario
 * from its strategy, result and acceptor.
 * @author Iovka Boneva
 * @version $Revision: 1.5 $
 */
public class ScenarioHandlerFactory {
	/** Retrieves a scenario handler for a scenario constructed from its components.
	 * @param <T> Type of the result of the scenario.
	 * @param strategy Strategy for the scenario.
	 * @param acceptor Acceptor for the scenario.
	 * @param description A one-sentence description of the scenario.
	 * @param name A short (one or few words) description of the scenario. Is to be 
	 * used in menus, or as identification (for instance in command-line options).
	 */
	public static <T> ScenarioHandler getScenario(
			final Strategy strategy, final Acceptor acceptor, final String description, 
			final String name) {
		return new AbstractScenarioHandler(description, name) {
			@Override
			protected Scenario createScenario() {
				return new DefaultScenario(strategy, acceptor.newInstance());
			}
		};
	}

	/** Retrieves a conditional scenario handler for a scenario constructed from its components. 
	 * @param <C> Type of the condition.
	 * @param strategy Strategy for the scenario.
	 * @param acceptor Acceptor for the scenario.
	 * @param description A one-sentence description of the scenario.
	 * @param name A short (one or few words) description of the scenario. Is to be 
	 * used in menus, or as identification (for instance in command-line options).
	 * @param negated Whether the condition of the acceptor is to be negated. Is designed
	 * for the needs of the {@link groove.gui.Simulator} where the negated characteristic
	 * is taken into account in the name of the scenario.
	 */
	public static <C> ConditionalScenarioHandler<C> getConditionalScenario(
			final Strategy strategy, final ConditionalAcceptor<C> acceptor, final String description, 
			final String name, final boolean negated) {
		return new AbstractConditionalScenarioHandler<C>(description, name, null) {
			@Override
			protected Scenario createScenario() {
				return new DefaultScenario(strategy, acceptor.newInstance());
			}

			@Override
			public void setCondition(ExploreCondition<C> explCond, String name) {
				super.setCondition(explCond, name);
				explCond.setNegated(negated);
				acceptor.setCondition(explCond);
			}

			@Override
			public Class<?> getConditionType() { return getCondition().getConditionType(); }
		};
	}
	
	/** Retrieves a scenario handler for a scenario constructed from its components.
	 * @param strategy Strategy for the scenario.
	 * @param description A one-sentence description of the scenario.
	 * @param name A short (one or few words) description of the scenario. Is to be 
	 * used in menus, or as identification (for instance in command-line options).
	 */
	public static ScenarioHandler getModelCheckingScenario(
			final ModelCheckingStrategy strategy,
			final String description,
			final String name, 
			final Simulator sim) {
		return new ModelCheckingScenarioHandler(strategy, description, name, null, null) {
			@Override
			protected String getProperty() {
				groove.gui.FormulaDialog dialog = sim.getFormulaDialog();
				dialog.showDialog(sim.getFrame());
				return dialog.getProperty();
			}
		};
	}

	/**
	 * Retrieves a scenario handler for a scenario constructed from its
	 * components.
	 * 
	 * @param strategy
	 *            Strategy for the scenario.
	 * @param description
	 *            A one-sentence description of the scenario.
	 * @param name
	 *            A short (one or few words) description of the scenario. Is to
	 *            be used in menus, or as identification (for instance in
	 *            command-line options).
	 */
	public static ScenarioHandler getBoundedModelCheckingScenario(
			final BoundedModelCheckingStrategy strategy,
			final String description,
			final String name,
			final Simulator sim) {
		return new ModelCheckingScenarioHandler(strategy, description, name, null, null) {
			@Override
			protected String getProperty() {
				groove.gui.FormulaDialog dialog = sim.getFormulaDialog();
				dialog.showDialog(sim.getFrame());
				return dialog.getProperty();
			}

			@Override
			protected Boundary getBoundary() {
				BoundedModelCheckingDialog dialog = new BoundedModelCheckingDialog();
				dialog.setGrammar(getGTS().getGrammar());
				dialog.showDialog(sim.getFrame());
				return dialog.getBoundary();
			}
		};
	}
//
//	/** Constructs a conditional scenario handler based on a conditional strategy.
//	 * @param <T> The type of the result of the scenario.
//	 * @param <C> The generic type for the explore condition.
//	 * @param res The result.
//	 * @param acc The acceptor.
//	 * @param str The strategy
//	 * @param description A one sentence description of the scenario.
//	 * @param name A short name for the scenario. Is to be 
//	 * used in menus, or as identification (for instance in command-line options).
//	 * @param negated Whether the condition is to be negated (is used in the name, thus
//	 * given at construction time).
//	 */
//	public static <T,C> ConditionalScenarioHandler<C> getConditionalScenario(
//			final ConditionalStrategy str,
//			final Result<T> res,
//			final Acceptor<T> acc, 
//			final String description, final String name, final boolean negated) {
//		return new AbstractConditionalScenarioHandler<C>() {
//
//			@Override
//			public String getName() {
//				if (this.explCond == null) {
//					return name;
//				}
//				return name + 
//						(negated ? " !" : " ") +
//						"<" +
//						this.condName +
//						">";
//			}
//			
//			@Override
//			public String getDescription() { return description; }
//
//			@Override
//			public void playScenario() throws InterruptedException {
//				DefaultScenario<T> scenar = new DefaultScenario<T>();
//				scenar.setAcceptor(acc);
//				scenar.setResult(res.getFreshResult());
//				scenar.setStrategy(str);
//				str.setExploreCondition(explCond);
//				
//				scenar.setGTS(getGTS());
//				scenar.setState(getState());
//				try {
//					this.result = scenar.play();
//				} catch (InterruptedException e) {
//					this.result = scenar.getComputedResult();
//					throw e;
//				}
//			}
//
//			public void setCondition(ExploreCondition<C> explCond, String name) {
//				this.explCond = explCond;
//				this.condName = name;
//				explCond.setNegated(negated);
//			}
//			private ExploreCondition<C> explCond;
//			private String condName = "";
//			
//			@Override
//			public Class<?> resultType() { return null; }
//
//			public Class<?> getConditionType() { return explCond.getConditionType(); }
//			
//			public void setCondition(ExploreCondition<C> condition, String name, boolean negated) {
//				throw new UnsupportedOperationException();
//			}
//			
//		};
//	}
//	
	/** A default, abstract implementation of a {@link ScenarioHandler}.
	 * 
	 * @author Iovka Boneva
	 */
	public static abstract class AbstractScenarioHandler implements ScenarioHandler {
		/** Constructs an instance with a given name and description. */
		protected AbstractScenarioHandler(String description, String name) {
			this.description = description;
			this.name = name;
		}
		
		public String getDescription() { return description; }

		public String getName() { return name; }

		public void playScenario() {
			Scenario scenario = createScenario();
			result = scenario.play(getGTS(), getState());
			interrupted = scenario.isInterrupted();
		}

		public ProductGTS getProductGTS() { return null; }

		public void setGTS (GTS gts) {
			this.gts = gts;
			setState(gts.startState());
		}
		
		/** 
		 * Retrieves the graph transition system of this scenario.
		 * @return The graph transition system of this scenario.
		 */
		protected GTS getGTS () { return this.gts; }

		public void setState (GraphState state) {this.state = state; }
		
		/**
		 * Retrieves the start state for this scenario.
		 * @return The start state for this scenario.
		 */
		protected GraphState getState () { return this.state; }
		
		public Result getResult() {
			return this.result;
		}
		
		/** Prints a report on {@link System#err} on memory usage. */
		protected void reportMemory() {
			Runtime runtime = Runtime.getRuntime();
			System.runFinalization();
			System.gc();
			long usedMemory = runtime.totalMemory() - runtime.freeMemory();
			System.err.println("Memory in use: " + (usedMemory / 1024) + " kB");
		}

		/** Callback factory method to create a scenario for this handler. */
		abstract protected Scenario createScenario();
		
		public boolean isInterrupted() {
			return interrupted;
		}

		@Override
		public String toString () {
			return getDescription();
		}

		/** The GTS of this scenario. */
		private GTS gts;
		/** The start state of the scenario. */
		private GraphState state;
		/** The result of this handler. */
		private Result result;
		/** 
		 * Flag indicating if the last call of {@link #playScenario()}
		 * was terminated by interruption.
		 */
		private boolean interrupted;

		private final String description;
		private final String name;
	}
	
	static class ModelCheckingScenarioHandler extends AbstractScenarioHandler {
		/**
		 * Constructs a handler with a given description and name.
		 */
		public ModelCheckingScenarioHandler(ModelCheckingStrategy strategy, String description, String name, String property, Boundary boundary) {
			super(description, name);
			this.strategy = strategy;
			this.property = property;
			this.boundary = boundary;
		}
		
		@Override
		protected Scenario createScenario() {
			return new ModelCheckingScenario(strategy);
		}

		@Override
		public void playScenario() {
			strategy.setProperty(getProperty());
			if (strategy instanceof BoundedModelCheckingStrategy) {
				((BoundedModelCheckingStrategy) strategy).setBoundary(getBoundary());
			}
			super.playScenario();
			reportMemory();
		}

		/** Callback method to get a property for the scenario. */
		protected String getProperty() {
			return property;
		}
		
		/** Callback method to get a boundary for the scenario. */
		protected Boundary getBoundary() {
			return boundary;
		}
		
		@Override
		public ProductGTS getProductGTS() {
			return strategy.getProductGTS();
		}

		private final ModelCheckingStrategy strategy;
		private final String property;
		private final Boundary boundary;
	}
	
	static abstract class AbstractConditionalScenarioHandler<C> 
		extends AbstractScenarioHandler implements ConditionalScenarioHandler<C> {
		/**
		 * Constructs a conditional handler with a given description and name,
		 * and a given condition type.
		 */
		public AbstractConditionalScenarioHandler(String description, String name, Class<?> type) {
			super(description, name);
			this.type = type;
		}
		
		@Override
		public String getName() {
			if (this.condition == null) {
				return super.getName();
			}
			return super.getName() + 
					(condition.isNegated() ? " !" : " ") +
					"<" +
					this.condName +
					">";
		}

		public void setCondition(ExploreCondition<C> explCond, String name) {
			this.condition = explCond;
			this.condName = name;
		}

		/** Returns the currently set exploration condition. */
		protected ExploreCondition<C> getCondition() {
			return condition;
		}
		
		public Class<?> getConditionType() { return type; }

		private ExploreCondition<C> condition;
		private String condName = "";
		private final Class<?> type;
	}	
}
