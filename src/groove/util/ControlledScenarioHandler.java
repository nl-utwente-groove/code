package groove.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import groove.control.Location;
import groove.explore.DefaultScenario;
import groove.explore.ScenarioHandlerFactory;
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

public class ControlledScenarioHandler extends AbstractScenarioHandler {

	/** */
	private String description;
	/** */
	private String name;
	private ControlledStrategy str;

	public ControlledScenarioHandler(String description, String name) {
		this.description = description;
		this.name = name;
	}
	
	@Override
	public String getDescription() { return this.description; }

	@Override
	public String getName() { return this.name; }

	@Override
	public void playScenario() {
		DefaultScenario<Object> scenar = new DefaultScenario<Object>();
		scenar.setAcceptor(new EmptyAcceptor());
		Result<Object> r = new EmptyResult<Object>();
		result = r;
		scenar.setResult(r);
		scenar.setStrategy(str);
		
		scenar.setGTS(getGTS());
		scenar.setState(getState());
		this.result = scenar.play();
	}

	@Override
	public Class<?> resultType() { return Object.class; }

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

		
		@Override
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
			
			
			@Override
			public void addUpdate(GraphShape graph, Node node) {
				queue.offer((GraphState) node);
			}
			@Override
			public void addUpdate(GraphShape graph, Edge edge) { /* empty */ }

			@Override
			public void removeUpdate(GraphShape graph, Node node) { /* empty */ }

			@Override
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
		
		@Override
		public Location getTarget(Rule rule) { return null; }

		@Override
		public void updateExplored(Rule rule) { /* empty */ }

		@Override
		public void updateMatches(Rule rule) { /* empty */ }

		@Override
		public Rule last() {
			return this.last;
		}

		@Override
		public boolean hasNext() {
			return this.rule != null;
		}

		@Override
		public Rule next() {
			if (this.rule == null) {
				throw new NoSuchElementException();
			}
			this.last = this.rule;
			this.rule = null;
			return this.last;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
}
