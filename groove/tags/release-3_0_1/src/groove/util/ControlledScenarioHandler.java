package groove.util;

import groove.control.Location;
import groove.explore.DefaultScenario;
import groove.explore.ScenarioHandlerFactory.AbstractScenarioHandler;
import groove.explore.result.EmptyAcceptor;
import groove.explore.result.EmptyResult;
import groove.explore.result.Result;
import groove.explore.strategy.AbstractStrategy;
import groove.explore.util.ExploreCache;
import groove.explore.util.MatchesIterator;
import groove.graph.Edge;
import groove.graph.GraphShape;
import groove.graph.GraphShapeListener;
import groove.graph.Node;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.trans.Rule;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/** Allows the execution of a controlled scenario.
 * Such a scenario is controlled by a list of rules, to be executed in this order.
 * The scenario computes either a linear-shaped LTS representing a path
 * of the given list of rules, or a tree with "layers" defined by the
 * list of rules.
 * @author Iovka Boneva
 * @version $Revision $
 */
public class ControlledScenarioHandler extends AbstractScenarioHandler {

	/** */
	private String description;
	/** */
	private String name;
	private ControlledStrategy str;

	/** Creates a scenario handler from a name and a description.
	 * @param description
	 * @param name
	 */
	public ControlledScenarioHandler(String description, String name) {
		this.description = description;
		this.name = name;
	}
	
	@Override
	public String getDescription() { return this.description; }

	@Override
	public String getName() { return this.name; }

	@Override
	public void playScenario() throws InterruptedException {
		DefaultScenario<Object> scenar = new DefaultScenario<Object>();
		scenar.setAcceptor(new EmptyAcceptor());
		Result<Object> r = new EmptyResult<Object>();
		result = r;
		scenar.setResult(r);
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
	public Class<?> resultType() { return Object.class; }

	/** Sets a program for the scenario.
	 * @param program a list of rules to be executed in this order
	 * @param findAll if set to true, then the scenario computes a tree. Otherwise, it computes a path.
	 */
	public void setProgram(List<Rule> program, boolean findAll) {
		if (this.str == null) {
			this.str = new ControlledStrategy();
		}
		this.str.program = program;
		this.str.findAll = findAll;
		this.str.pc = program.iterator();
		this.str.nextRule();
	}
	
	private class ControlledStrategy extends AbstractStrategy {
		public boolean next() {
			if (getAtState() == null || this.currRule == null) {
				getGTS().removeGraphListener(toExplore);
				return false;
			}
			ExploreCache cache = new ControlledCache(this.currRule);
			MatchesIterator matchIter = new MatchesIterator(atState, cache);
			
			if (findAll) {
				while (matchIter.hasNext()) {
					getGenerator().addTransition(getAtState(), matchIter.next(), cache);
				}
			} else {
				if (matchIter.hasNext()) {
					getGenerator().addTransition(getAtState(), matchIter.next(), cache);
				}
			}
			updateAtState();
			return true;
		}

		@Override
		protected void updateAtState() {
			this.atState = currList.poll();
			if (this.atState == null) {
				nextRule();
				this.atState = currList.poll();
			}
		}
		
		void nextRule() {
			if (this.pc.hasNext()) {
				this.currRule = this.pc.next();
				LinkedList<GraphState> tmp = this.currList;
				this.currList = this.nextList;
				this.nextList = tmp;
				this.toExplore.setQueue(nextList);
			} else {
				this.currRule = null;
			}
		}
		
		@Override
		public void setGTS(GTS gts) {
			super.setGTS(gts);
			gts.addGraphListener(toExplore);
			toExplore.setQueue(nextList);
		}	
		
		
		/** */
		List<Rule> program;
		Iterator<Rule> pc;
		boolean findAll;
		Rule currRule;
		
		/** The states to be explored, in a FIFO order. */
		protected LinkedList<GraphState> currList = new LinkedList<GraphState>();
		protected LinkedList<GraphState> nextList = new LinkedList<GraphState>();
		protected ToExploreListener toExplore = new ToExploreListener();
		/** Iterator over the matches of the current state. */
		
		/** A queue with states to be explored, used as a FIFO. */
		protected class ToExploreListener implements GraphShapeListener {
			LinkedList<GraphState> queue;
			void setQueue (LinkedList<GraphState> queue) {
				this.queue = queue;
			}
			
			public void addUpdate(GraphShape graph, Node node) {
				queue.offer((GraphState) node);
			}

			public void addUpdate(GraphShape graph, Edge edge) { /* empty */ }

			public void removeUpdate(GraphShape graph, Node node) { /* empty */ }

			public void removeUpdate(GraphShape graph, Edge elem) { /* empty */ }

			@Override
			public boolean equals (Object o) {
				return this == o;
			}
		}
	}
	
	private class ControlledCache implements ExploreCache {
		private Rule rule;
		private Rule last;
		
		ControlledCache (Rule r) {
			this.rule = r;
		}
		
		public Location getTarget(Rule rule) { return null; }

		public void updateExplored(Rule rule) { /* empty */ }

		public void updateMatches(Rule rule) { /* empty */ }

		public Rule last() {
			return this.last;
		}

		public boolean hasNext() {
			return this.rule != null;
		}

		public Rule next() {
			if (this.rule == null) {
				throw new NoSuchElementException();
			}
			this.last = this.rule;
			this.rule = null;
			return this.last;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
}
