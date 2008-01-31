package groove.explore;

import groove.explore.ScenarioHandlerFactory.AbstractConditionalScenarioHandler;
import groove.explore.ScenarioHandlerFactory.AbstractScenarioHandler;
import groove.explore.result.ConditionalAcceptor;
import groove.explore.result.EmptyAcceptor;
import groove.explore.result.EmptyResult;
import groove.explore.result.ExploreCondition;
import groove.explore.result.FinalStateAcceptor;
import groove.explore.result.Result;
import groove.explore.result.SizedResult;
import groove.explore.strategy.ConditionalStrategy;
import groove.explore.strategy.Strategy;
import groove.lts.GraphState;


/**
 * Offers factory methods for different {@link groove.simulate.GeneratorScenarioHandler}s needed
 * by the {@link groove.util.Generator}. 
 * @author Iovka Boneva
 */
public class GeneratorScenarioHandlerFactory {

	/**
	 * Creates a scenario handler with empty result and empty acceptor, and with the strategy given as
	 * parameter. 
	 * @param strategy 
	 * @param description
	 * @return
	 */
	public static ScenarioHandler getScenarioHandler(final Strategy strategy, final String description, final String name) {
		return new AbstractScenarioHandler() {
			@Override
			public String getDescription() { return description; }

			@Override
			public String getName() { return name; }

			@Override
			public void playScenario() {
				DefaultScenario<Object> scenario;
				scenario = new DefaultScenario<Object>();
				Result<Object> r = new EmptyResult<Object>();
				this.result = r;
				scenario.setResult(r);
				scenario.setAcceptor(new EmptyAcceptor());
				scenario.setStrategy(strategy);

				scenario.setGTS(getGTS());
				scenario.setState(getState());
				scenario.play();
			}

			@Override
			public Class<?> resultType() { return Object.class; }			
		};
	}

	/** Creates a scenario handler finding a final state with the strategy given as parameter.
	 * @param strategy
	 * @param description
	 * @param name
	 * @return
	 */
	public static ScenarioHandler getFinalStateScenarioHandler(final Strategy strategy, final String description, final String name) {
		return new AbstractScenarioHandler() {

			@Override
			public String getDescription() { return description; }

			@Override
			public String getName() { return name; }

			@Override
			public void playScenario() {
				DefaultScenario<GraphState> scenario;
				scenario = new DefaultScenario<GraphState>();
				Result<GraphState> r = new SizedResult<GraphState>(1);
				this.result = r;
				scenario.setResult(r);
				scenario.setAcceptor(new FinalStateAcceptor());
				scenario.setStrategy(strategy);

				scenario.setGTS(getGTS());
				scenario.setState(getState());
				scenario.play();
			}

			@Override
			public Class<?> resultType() { return GraphState.class; }			
		};	
	}
	
	/** Retrieves a conditional scenario handler for a scenario
	 * based on an conditional acceptor and with given (GraphState) result.
	 * @param <T> Type of the result of the scenario.
	 * @param <C> Type of the condition.
	 * @param str Strategy for the scenario.
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
			final Strategy str, final Class<?> type, final ConditionalAcceptor<C> acc, 
			final Result<GraphState> res,
			final String description, final String name) {
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
			public void playScenario() {
				DefaultScenario<GraphState> scenar = new DefaultScenario<GraphState>();
				this.explCond.setNegated(negated);
				acc.setCondition(this.explCond);
				scenar.setAcceptor(acc);
				this.result = res;
				scenar.setResult(res);
				scenar.setStrategy(str);
				

				scenar.setGTS(getGTS());
				scenar.setState(getState());
				this.result = scenar.play();
			}

			@Override
			public Class<?> resultType() { return null; }

			public void setCondition(ExploreCondition<C> explCond, String name, boolean negated) {
				this.explCond = explCond;
				this.condName = name;
				this.negated = negated;
				explCond.setNegated(negated);
			}
		
			private ExploreCondition<C> explCond;
			private String condName = ""; // Is it used ?
			private boolean negated;

			public Class<?> getConditionType() { return type; }

			public void setCondition(ExploreCondition<C> condition, String name) {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	/** Constructs a conditional scenario handler based on a conditional strategy and
	 * with empty acceptor and empty result.
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
	public static <C> ConditionalScenarioHandler<C> getConditionalScenario(
			final ConditionalStrategy str, final Class<?> type,
			final String description, final String name) {
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
			public void playScenario() {
				DefaultScenario<Object> scenar = new DefaultScenario<Object>();
				scenar.setAcceptor(new EmptyAcceptor());
				Result<Object> r = new EmptyResult<Object>();
				this.result = r;
				scenar.setResult(r);
				scenar.setStrategy(str);
				str.setExploreCondition(explCond);
				
				scenar.setGTS(getGTS());
				scenar.setState(getState());
				this.result = scenar.play();
			}

			public void setCondition(ExploreCondition<C> explCond, String name, boolean negated) {
				assert type.isAssignableFrom(explCond.getConditionType()) : "Incompatible types: " + explCond.getConditionType() + " and " + type;
				this.explCond = explCond;
				this.condName = name;
				this.negated = negated;
				explCond.setNegated(negated);
			}
			
			private ExploreCondition<C> explCond;
			private String condName = "";  // see how to use it
			private boolean negated;
			
			@Override
			public Class<?> resultType() { return null; }

			public Class<?> getConditionType() { return type; }

			public void setCondition(ExploreCondition<C> condition, String name) {
				throw new UnsupportedOperationException();
			}
			
		};
	}
	
}