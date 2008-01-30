package groove.explore;

import groove.explore.result.Acceptor;
import groove.explore.result.Result;
import groove.explore.strategy.Strategy;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.StateGenerator;
import groove.util.Reporter;

//requirements:
//
//	MAIN REQUIREMENTS:
//	- adding states and transitions to the gts (yes or no, late or immediate)
//	- iterating over the possible ruleapplications for a certain graphstate
//	  -  should incorporate priorities and control
//	- iterating over the possible graphstates
//	- identifying when finished (a certain goal is reached)
//  - some goals may not require or even rather not have the gts filled
//    - requires stateiter to not use the gts
//    - requires the scenario to be able to store itermediate states
//    - ALTERNATITE: allow discarding "unsuccesful" results
//
//  - ISSUE: WHO IS RESPONSIBLE FOR ADDINGS STATES/TRANSITIONS AND CLOSING STATES
//
//	OTHER (sub) REQUIREMENTS:
//	- ruleapplication iteration should incorporate priorities and control
//	- the ruleappiter and stateiter together should iterate the entire statespace
//	- if an iterator depends on stuff being added to the gts, then stuff should be added to the gts
//  - do not use hasNext in iterators (or maybe dont call it iterator, because hasNext is sometimes
//      hard to compute and decreases performance, unless caching of temp results is implemented
//    CONSEQUENCE: if hasNext is not implemented, the only one able to close a state is the ruleapplication iterator itself
//
//	alternatives - iterator usage:
//	- the iterators might not be fully used, thus if caching is needed they should do their own or it should be handled by the getter method for the iterator (this is almost a solution already, but then again, using iterators is also)
//	- iterators are always completely used, so if only ONE ruleapplication (e.g. depth first) should be explored at a time, then it should only be able to execute next() once.
//
//	alternatives - application-iterator caching: 
//	- one could store an iterator for a certain graphstate
//	- one could let the iterator find the first ruleapplication that has not been added to the gts yet
//
//	dangers, prevent:
//	- continuing with an applicationiterator when a transition from a higher priority has already been found/added.

/**
 * A default implementation of a {@link groove.explore.Scenario}.
 * 
 * The two iterators combined should allow reaching the goals compatible with this Scenario.
 * 
 * @author Staijen
 * @param <T> The type of the result of this scenario.
 *
 */
public class DefaultScenario<T> implements Scenario<T> {

	/** The graph transition system on which the scenario works. */
	private GTS gts;
	/** The start state of the scenario. */
	private GraphState atState;

	/** The result of the scenario. Is responsible for collecting the result. */
	private Result<T> prototype;
	/** The acceptor of the scenario. */
	private Acceptor<T> acceptor;
	/** The strategy used by this scenario. */
	private Strategy strategy;
	
	/** Sets the acceptor for this scenario.
	 * @param acceptor the acceptor for this scenario.
	 */
	public void setAcceptor(Acceptor<T> acceptor) {
		this.acceptor = acceptor;
	}
	
	/** Sets the result for this scenario.
	 * @param prototype the result for this scenario.
	 */
	public void setResult(Result<T> prototype) {
		this.prototype = prototype;
	}
	
	/** Sets the strategy used by this scenario.
	 * @param strategy the strategy used by this scenario.
	 */
	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	public Result<T> play() {
		assert prototype != null && acceptor != null && strategy != null : 
			"The scenario is not correctly initialized with a result, a strategy and an acceptor.";
		assert(gts != null) : "The GTS of the scenario has not been initialized.";
		
		// make sure strategy and acceptor are reset and up to date
		strategy.setState(atState);
		strategy.setGTS(gts);
		
		gts.addGraphListener(acceptor);
		
		acceptor.setResult(prototype);

		reporter.start(RUNNING);
		
		// start working until done or nothing to do
		while( !prototype.done() && strategy.next() ) {
		}
		
		reporter.stop();
		
		// return result
		return prototype;
	}
	
	
	public void setGTS(GTS gts) {
		this.gts = gts;
	}

	public GTS getGTS() {
		return gts;
	}
	
	public void setState(GraphState state) {
		this.atState = state;
	}

	public String toString() {
		return (atState==null?"":" (from " + atState.toString() + ")");
	}

	
	/** Reporter for profiling information; aliased to {@link GTS#reporter}. */
    static public final Reporter reporter = Reporter.register(DefaultScenario.class);
    /** Handle for profiling {@link #getApplications()}. */
    static public final int GET_DERIVATIONS = reporter.newMethod("getDerivations(Graph)");
    /** Handle for profiling {@link #collectApplications(Rule, Set)}. */
//    static protected final int COLLECT_APPLICATIONS = reporter.newMethod("collectApplications(...)");
    static protected final int RUNNING = reporter.newMethod("playScenario()");

	public static long getTransformingTime() {
		return reporter.getTotalTime(GET_DERIVATIONS);
	}
	public static long getRunningTime() {
		return reporter.getTotalTime(RUNNING);
	}
    
}

