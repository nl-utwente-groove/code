package groove.util;

import groove.control.Location;
import groove.explore.DefaultScenario;
import groove.explore.result.Acceptor;
import groove.explore.strategy.AbstractStrategy;
import groove.explore.util.ExploreCache;
import groove.graph.GraphAdapter;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.trans.Rule;
import groove.trans.RuleEvent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Allows the execution of a controlled scenario. Such a scenario is controlled
 * by a list of rules, to be executed in this order. The scenario computes
 * either a linear-shaped LTS representing a path of the given list of rules, or
 * a tree with "layers" defined by the list of rules.
 * @author Iovka Boneva
 * @version $Revision $
 */
public class ControlledScenario extends DefaultScenario {
    private ControlledStrategy str;

    /** Creates a scenario handler from a name and a description. */
    public ControlledScenario(ControlledStrategy strategy, String name,
            String description) {
        super(strategy, new Acceptor(), name, description);
    }

    /**
     * Sets a program for the scenario.
     * @param program a list of rules to be executed in this order
     * @param findAll if set to true, then the scenario computes a tree.
     *        Otherwise, it computes a path.
     */
    public void setProgram(List<Rule> program, boolean findAll) {
        if (this.str == null) {
            this.str = new ControlledStrategy();
        }
        this.str.findAll = findAll;
        this.str.pc = program.iterator();
        this.str.nextRule();
    }

    private static class ControlledStrategy extends AbstractStrategy {
        public boolean next() {
            if (getAtState() == null || this.currRule == null) {
                getGTS().removeGraphListener(this.toExplore);
                return false;
            }
            ExploreCache cache = new ControlledCache(this.currRule);
            Iterator<RuleEvent> matchIter =
                createMatchCollector(cache).getMatchSet().iterator();

            if (this.findAll) {
                while (matchIter.hasNext()) {
                    applyEvent(matchIter.next(), cache);
                }
            } else {
                if (matchIter.hasNext()) {
                    applyEvent(matchIter.next(), cache);
                }
            }
            updateAtState();
            return true;
        }

        @Override
        protected void updateAtState() {
            this.atState = this.currList.poll();
            if (this.atState == null) {
                nextRule();
                this.atState = this.currList.poll();
            }
        }

        void nextRule() {
            if (this.pc.hasNext()) {
                this.currRule = this.pc.next();
                LinkedList<GraphState> tmp = this.currList;
                this.currList = this.nextList;
                this.nextList = tmp;
                this.toExplore.setQueue(this.nextList);
            } else {
                this.currRule = null;
            }
        }

        @Override
        public void prepare(GTS gts, GraphState state) {
            super.prepare(gts, state);
            gts.addGraphListener(this.toExplore);
            this.toExplore.setQueue(this.nextList);
        }

        Iterator<Rule> pc;
        boolean findAll;
        Rule currRule;

        /** The states to be explored, in a FIFO order. */
        private LinkedList<GraphState> currList = new LinkedList<GraphState>();
        private LinkedList<GraphState> nextList = new LinkedList<GraphState>();
        private final ToExploreListener toExplore = new ToExploreListener();

        /** Iterator over the matches of the current state. */

        /** A queue with states to be explored, used as a FIFO. */
        protected class ToExploreListener extends GraphAdapter {
            LinkedList<GraphState> queue;

            void setQueue(LinkedList<GraphState> queue) {
                this.queue = queue;
            }

            @Override
            public void addUpdate(GraphShape graph, Node node) {
                this.queue.offer((GraphState) node);
            }
        }
    }

    private static class ControlledCache implements ExploreCache {
        private Rule rule;
        private Rule last;

        ControlledCache(Rule r) {
            this.rule = r;
        }

        public Location getTarget(Rule rule) {
            return null;
        }

        public void updateExplored(Rule rule) { /* empty */
        }

        public void updateMatches(Rule rule) { /* empty */
        }

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
