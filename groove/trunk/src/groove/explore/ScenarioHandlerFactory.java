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
 * $Id: ScenarioHandlerFactory.java,v 1.5 2008-03-04 14:50:37 kastenberg Exp $
 */
package groove.explore;

import groove.explore.result.Acceptor;
import groove.explore.result.ConditionalAcceptor;
import groove.explore.result.CycleAcceptor;
import groove.explore.result.ExploreCondition;
import groove.explore.result.Result;
import groove.explore.strategy.Boundary;
import groove.explore.strategy.BoundedModelCheckingStrategy;
import groove.explore.strategy.ConditionalStrategy;
import groove.explore.strategy.DefaultModelCheckingStrategy;
import groove.explore.strategy.Strategy;
import groove.gui.BoundedModelCheckingDialog;
import groove.gui.FormulaDialog;
import groove.gui.Simulator;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.ProductGTS;

import java.util.Collection;

/** A factory for creating scenario handlers by composing a scenario
 * from its strategy, result and acceptor.
 * @author Iovka Boneva
 * @version $Revision: 1.5 $
 */
public class ScenarioHandlerFactory {
		
	/** Retrieves a scenario handler for a scenario constructed from its components.
	 * @param <T> Type of the result of the scenario.
	 * @param str Strategy for the scenario.
	 * @param res Result for the scenario.
	 * @param acc Acceptor for the scenario.
	 * @param description A one-sentence description of the scenario.
	 * @param name A short (one or few words) description of the scenario. Is to be 
	 * used in menus, or as identification (for instance in command-line options).
	 * @return
	 */
	public static <T> ScenarioHandler getScenario(
			final Strategy str, final Result<T> res, final Acceptor<T> acc, 
			final String description, final String name) {
		return new AbstractScenarioHandler() {

			@Override
			public String getDescription() { return description; }

			@Override
			public String getName() { return name; }

			@Override
			public void playScenario() throws InterruptedException {
				DefaultScenario<T> scenar = new DefaultScenario<T>();
				scenar.setAcceptor(acc);
				scenar.setResult(res);
				scenar.setStrategy(str);
				
				scenar.setGTS(getGTS());
				scenar.setState(getState());
				
				Runtime runtime = Runtime.getRuntime();
				try {
					this.result = scenar.play();
				} catch (InterruptedException e) {
					this.result = scenar.getComputedResult();
					throw e;
				}

	            System.runFinalization();
	            System.gc();
	            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
	            
	            System.err.println("Memory in use: " + (usedMemory / 1024) + " kB");
			}

			@Override
			public Class<?> resultType() { return null; }
		};
	}

	/** Retrieves a conditional scenario handler for a scenario constructed from its components. 
	 * @param <T> Type of the result of the scenario.
	 * @param <C> Type of the condition.
	 * @param str Strategy for the scenario.
	 * @param res Result for the scenario.
	 * @param acc Acceptor for the scenario.
	 * @param description A one-sentence description of the scenario.
	 * @param name A short (one or few words) description of the scenario. Is to be 
	 * used in menus, or as identification (for instance in command-line options).
	 * @param negated Whether the condition of the acceptor is to be negated. Is designed
	 * for the needs of the {@link groove.gui.Simulator} where the negated characteristic
	 * is taken into account in the name of the scenario.
	 * @return
	 */
	public static <C> ConditionalScenarioHandler<C> getConditionalScenario(
			final Strategy str, final Result<GraphState> res, final ConditionalAcceptor<C> acc, 
			final String description, final String name, final boolean negated) {
		return new AbstractConditionalScenarioHandler<C>() {

			@Override
			public String getName() {
				if (this.explCond == null) {
					return name;
				}
				return name + 
						(negated ? " !" : " ") +
						"<" +
						this.condName +
						">";
			}

			@Override
			public String getDescription() { return description; }

			@Override
			public void playScenario() throws InterruptedException {
				DefaultScenario<GraphState> scenar = new DefaultScenario<GraphState>();
				this.explCond.setNegated(negated);
				acc.setCondition(this.explCond);
				scenar.setAcceptor(acc);
				scenar.setResult(res);
				scenar.setStrategy(str);

				scenar.setGTS(getGTS());
				scenar.setState(getState());
				try {
					this.result = scenar.play();
				} catch (InterruptedException e) {
					this.result = scenar.getComputedResult();
					throw e;
				}
			}

			@Override
			public Class<?> resultType() { return null; }

			public void setCondition(ExploreCondition<C> explCond, String name) {
				this.explCond = explCond;
				this.condName = name;
			}
		
			private ExploreCondition<C> explCond;
			private String condName = "";

			public Class<?> getConditionType() { return explCond.getConditionType(); }

			public void setCondition(ExploreCondition<C> condition, String name, boolean negated) {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	/** Retrieves a scenario handler for a scenario constructed from its components.
	 * @param <T> Type of the result of the scenario.
	 * @param str Strategy for the scenario.
	 * @param res Result for the scenario.
	 * @param acc Acceptor for the scenario.
	 * @param description A one-sentence description of the scenario.
	 * @param name A short (one or few words) description of the scenario. Is to be 
	 * used in menus, or as identification (for instance in command-line options).
	 * @return
	 */
	public static <T> ScenarioHandler getModelCheckingScenario(
			final DefaultModelCheckingStrategy<T> str,
			final Result<T> res,
			final Acceptor<T> acc, 
			final String description,
			final String name,
			final Simulator sim) {
		return new AbstractScenarioHandler() {

			@Override
			public String getDescription() { return description; }

			@Override
			public String getName() { return name; }

			@Override
			public void playScenario() throws InterruptedException {
				DefaultScenario<T> scenar = new DefaultScenario<T>();
				scenar.setAcceptor(acc);
				scenar.setResult(res);

				if (acc instanceof CycleAcceptor) {
					((CycleAcceptor) acc).setStrategy(str);
				}
				str.setSimulator(sim);
				str.setGTS(getGTS());
				str.setProductGTS(new ProductGTS(getGTS().getGrammar()));
				str.setResult(res);
				scenar.setStrategy(str);

//				String property = getProperty(sim);
//				if (property != null) {
//					str.setProperty(property);
//					str.setup();
//				}
				
				scenar.setGTS(getGTS());
				// set the initial property location
//				getState().setLocation(str.getInitialLocation());
//				getState().setPropertyLocation(str.getAtPropertyLocation());
				scenar.setState(getState());
				
				Runtime runtime = Runtime.getRuntime();
				try {
					this.result = scenar.play();
				} catch (InterruptedException e) {
					this.result = scenar.getComputedResult();
					throw e;
				}

				if (!res.getResult().isEmpty()) {
//					sim.notifyCounterExample((Stack<GraphState>) res.getResult().iterator().next());
				}
	            System.runFinalization();
	            System.gc();
	            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
	            
	            System.err.println("Memory in use: " + (usedMemory / 1024) + " kB");
			}

			protected String getProperty(Simulator sim) {
//		   		String message = "Do you want to perform on-the-fly LTL model checking?";
//		   		int onTheFly = JOptionPane.showConfirmDialog(sim.getFrame(), message, "Open states", JOptionPane.YES_NO_OPTION);
//		    	if (onTheFly == JOptionPane.YES_OPTION) {
		    		FormulaDialog dialog = sim.getFormulaDialog();
		    		dialog.showDialog(sim.getFrame());
		    		String property = dialog.getProperty();
		    		if (property != null) {
		    			return property;
		    		}
//		    	}
		    	return null;
			}

			@Override
			public Class<?> resultType() { return null; }
		};
	}

	/** Retrieves a scenario handler for a scenario constructed from its components.
	 * @param <T> Type of the result of the scenario.
	 * @param str Strategy for the scenario.
	 * @param res Result for the scenario.
	 * @param acc Acceptor for the scenario.
	 * @param description A one-sentence description of the scenario.
	 * @param name A short (one or few words) description of the scenario. Is to be 
	 * used in menus, or as identification (for instance in command-line options).
	 * @return
	 */
	public static <T> ScenarioHandler getBoundedModelCheckingScenario(
			final BoundedModelCheckingStrategy<T> str,
			final Result<T> res,
			final Acceptor<T> acc,
			final Boundary boundary,
			final String description,
			final String name,
			final Simulator sim) {
		return new AbstractScenarioHandler() {

			@Override
			public String getDescription() { return description; }

			@Override
			public String getName() { return name; }

			@Override
			public void playScenario() throws InterruptedException {
				DefaultScenario<T> scenar = new DefaultScenario<T>();
				scenar.setAcceptor(acc);
				scenar.setResult(res);

				if (acc instanceof CycleAcceptor) {
					((CycleAcceptor) acc).setStrategy(str);
				}

				setBoundary();
				str.setSimulator(sim);
				str.setGTS(getGTS());
				str.setProductGTS(new ProductGTS(getGTS().getGrammar()));
				str.setResult(res);
				str.setBoundary(boundary);
				scenar.setStrategy(str);

				scenar.setGTS(getGTS());
				scenar.setState(getState());
				
				Runtime runtime = Runtime.getRuntime();
				try {
					this.result = scenar.play();
				} catch (InterruptedException e) {
					this.result = scenar.getComputedResult();
					throw e;
				}

				if (!res.getResult().isEmpty()) {
//					sim.notifyCounterExample((Stack<GraphState>) res.getResult().iterator().next());
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

			protected Boundary setBoundary() {
				BoundedModelCheckingDialog dialog = new BoundedModelCheckingDialog();
				dialog.setGrammar(getGTS().getGrammar());
				dialog.showDialog(sim.getFrame());
				return dialog.getBoundary();
			}
			@Override
			public Class<?> resultType() { return null; }
		};
	}

	/** Constructs a conditional scenario handler based on a conditional strategy.
	 * @param <T> The type of the result of the scenario.
	 * @param <C> The generic type for the explore condition.
	 * @param res The result.
	 * @param acc The acceptor.
	 * @param str The strategy
	 * @param description A one sentence description of the scenario.
	 * @param name A short name for the scenario. Is to be 
	 * used in menus, or as identification (for instance in command-line options).
	 * @param negated Whether the condition is to be negated (is used in the name, thus
	 * given at construction time). 
	 * @return */
	public static <T,C> ConditionalScenarioHandler<C> getConditionalScenario(
			final ConditionalStrategy str,
			final Result<T> res,
			final Acceptor<T> acc, 
			final String description, final String name, final boolean negated) {
		return new AbstractConditionalScenarioHandler<C>() {

			@Override
			public String getName() {
				if (this.explCond == null) {
					return name;
				}
				return name + 
						(negated ? " !" : " ") +
						"<" +
						this.condName +
						">";
			}
			
			@Override
			public String getDescription() { return description; }

			@Override
			public void playScenario() throws InterruptedException {
				DefaultScenario<T> scenar = new DefaultScenario<T>();
				scenar.setAcceptor(acc);
				scenar.setResult(res);
				scenar.setStrategy(str);
				str.setExploreCondition(explCond);
				
				scenar.setGTS(getGTS());
				scenar.setState(getState());
				try {
					this.result = scenar.play();
				} catch (InterruptedException e) {
					this.result = scenar.getComputedResult();
					throw e;
				}
			}

			public void setCondition(ExploreCondition<C> explCond, String name) {
				this.explCond = explCond;
				this.condName = name;
				explCond.setNegated(negated);
			}
			private ExploreCondition<C> explCond;
			private String condName = "";
			
			@Override
			public Class<?> resultType() { return null; }

			public Class<?> getConditionType() { return explCond.getConditionType(); }
			
			public void setCondition(ExploreCondition<C> condition, String name, boolean negated) {
				throw new UnsupportedOperationException();
			}
			
		};
	}
	
	/** A default, abstract implementation of a {@link ScenarioHandler}.
	 * 
	 * @author Iovka Boneva
	 */
	public static abstract class AbstractScenarioHandler implements ScenarioHandler {
		// ---------------------------------------------------------
		// ScenarioHandler methods
		// ---------------------------------------------------------
		public abstract String getDescription();
		
		public abstract String getName(); 

		public abstract void playScenario() throws InterruptedException;

		public abstract Class<?> resultType(); 
		
		// ---------------------------------------------------------
		// FIELDS, ACCESSORS ETC.
		// ---------------------------------------------------------
		
		private GTS gts;
		private GraphState state;
		/** The result of this handler. */
		protected Result<? extends Object> result;
		/** Retrieves the graph transition system of this scenario.
		 * @return The graph transition system of this scenario.
		 */
		protected GTS getGTS () { return this.gts; }

		public void setGTS (GTS gts) { this.gts = gts; }
		/** Retrieves the start state for this scenario.
		 * @return The start state for this scenario.
		 */
		protected GraphState getState () { return this.state; }

		public void setState (GraphState state) {this.state = state; }
		
		public Collection<? extends Object> getResult() {
			return this.result.getResult();
		}
		
		@Override
		public String toString () {
			return getDescription();
		}
	}
	
	
	public static abstract class AbstractConditionalScenarioHandler<C> 
		extends AbstractScenarioHandler implements ConditionalScenarioHandler<C> {
		/* empty */
	}
	
}
