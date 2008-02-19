package groove.explore.strategy;

import java.util.Iterator;

import groove.explore.util.AliasMatchesIterator;
import groove.explore.util.ExploreCache;
import groove.explore.util.MatchesIterator;
import groove.explore.util.RandomChooserInSequence;
import groove.lts.DefaultGraphNextState;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphNextState;
import groove.lts.StateGenerator;

/** A partial (abstract) implementation of a strategy.
 * @author 
 *
 */
public abstract class AbstractStrategy implements Strategy {
	public void setGTS(GTS gts) {
		this.gts = gts;
		this.generator = gts.getRecord().getStateGenerator(gts);
	}
	
	/** The graph transition system explored by the strategy.
	 * @return The graph transition system explored by the strategy.
	 */
	protected GTS getGTS() { return this.gts; }

	/** The state where the strategy starts exploring.
	 * @return The state where the strategy starts exploring.
	 */
	protected final GraphState startState() {
		return this.startState;
	}

	public void setState(GraphState state) {
		this.startState = state;
		this.atState = state;
	}

	/** The state generator used as interface with the GTS. 
	 * Is initialised at the same time as the GTS.
	 */
	protected final StateGenerator getGenerator() {
		return this.generator;
	}
	
	
	/** Sets atState to the next state to be explored, or <code>null</code>
	 * if there are no more states to be explored.
	 * This is the place where satisfaction of the condition is to
	 * be tested. This method should be the only one who
	 * updates atState.
	 */
	protected abstract void updateAtState();
	
	/** The state that is currently explored by the strategy. 
	 * Is updated by {@link #updateAtState()}. */
	protected final GraphState getAtState() {
		return this.atState;
	}
	
	/** Closes a state.
	 * @param state
	 */
	protected void setClosed(GraphState state) {
		getGTS().setClosed(state);
	}

	/** The parent of a given state, or null if this is the start state.
	 * May be used by the backtracking strategies
	 * @param state which parent will be returned
	 * @param the start state 
	 */
	protected final GraphState parentOf(GraphState state) {
		if (state.equals(startState())) {
			return null;
		} 
		GraphNextState ngs = (GraphNextState) state;
		return ngs.source();
	}
	
	/** Returns a random open successor of a state, if any. Returns null otherwise. 
	 * Is considered as successor only a state that is a successor in the spanning tree.
	 */
	protected final GraphState getRandomOpenSuccessor(GraphState state) {
		Iterator<GraphState> sucIter = state.getNextStateIter();
		RandomChooserInSequence<GraphState>  chooser = new RandomChooserInSequence<GraphState>();
		while (sucIter.hasNext()) {
			GraphState s = sucIter.next();
			if (getGTS().getOpenStates().contains(s) &&
				 	s instanceof GraphNextState     &&
				 	((GraphNextState) s).source().equals(state)) {
				chooser.show(s);
			}
		}
		return chooser.pickRandom();
	}

	/** Returns the first open successor of a state, if any. Returns null otherwise. */
	protected final GraphState getFirstOpenSuccessor(GraphState state) {
		Iterator<GraphState> sucIter = state.getNextStateIter();
		while (sucIter.hasNext()) {
			GraphState s = sucIter.next();
			if (getGTS().getOpenStates().contains(s)) {
				return s;
			}
		}
		return null;
	}

	/** Gives a cache for atState. */
	protected ExploreCache getCache(boolean ruleInterrupted, boolean isRandomized) {
		return getGTS().getRecord().createCache(getAtState(), ruleInterrupted, isRandomized);
	}
	
	/** Gives an appropriate matches iterator for atState.
	 * This may be a {@link MatchesIterator} or an {@link AliasMatchesIterator}.
	 * @param cache
	 */
	protected MatchesIterator getMatchesIterator(ExploreCache cache) {
		// Two cases where an alias iterator may be returned : 
		// the parent is closed, or one of the successors is closed
		
		// First case : the parent is closed
		GraphState parent = parentOf(getAtState());
		if (aliasing && parent != null && parent.isClosed()) {
			DefaultGraphNextState s = (DefaultGraphNextState) getAtState();
			return new AliasMatchesIterator(s, cache, getGTS().getRecord().getEnabledRules(s.getEvent().getRule()), this.getGTS().getRecord().getDisabledRules(s.getEvent().getRule()));
		}

		// Second case : one of the successors is closed.
		// This is only considered for backtracking strategies, in 
		// which the state from which we backtrack is closed
		// and recently used matches iterators may be cached
		if (false && this instanceof AbstractBacktrackingStrategy) {
			AbstractBacktrackingStrategy str = (AbstractBacktrackingStrategy) this;
			// TODO integrate alias matches iterator constructed from a sibling or child
		}
		// in all other cases, return a "normal" matches iterator
		
		return new MatchesIterator(getAtState(), cache);
	}
	
	
	/** The graph transition system explored by the strategy.*/
	private GTS gts;
	/** The state where the strategy starts exploring.*/
	private GraphState startState;
	/** The state that will be explored by the next call of {@link #next()}. */
	protected GraphState atState;
	/** The state generator used as interface with the GTS. */
	private StateGenerator generator;
	/** Indicates whether the strategy should use aliasing or not. Default value is true. */
	// TODO this is set to false until the aliased matcher is debugged
	protected boolean aliasing = true;
	
	
}
