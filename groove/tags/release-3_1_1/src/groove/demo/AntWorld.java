package groove.demo;

import groove.calc.DefaultGraphCalculator;
import groove.explore.DefaultScenario;
import groove.explore.Scenario;
import groove.explore.result.Acceptor;
import groove.explore.result.Result;
import groove.explore.strategy.AbstractStrategy;
import groove.explore.strategy.RandomLinearStrategy;
import groove.explore.strategy.Strategy;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.LTS;
import groove.lts.State;
import groove.trans.GraphGrammar;
import groove.util.Groove;

import java.io.File;

/**
 * Runner class for the AntWorld sample, using a branching stategy to collect a
 * random next state. It will find a final state, which is enforced by the
 * grammar using a maximum number of turns, specified in the "stop" rule.
 * 
 * Given this final state, it will print the details of the second-last
 * transition, which should be an application of the end_turn rule exporting the
 * number of turns, number of circles and number of ants.
 * 
 * Also, the execution time is measured and printed.
 * 
 * @author Staijen
 */
public class AntWorld {

    /**
     * Starts a new runner
     * @param args not used
     */
    public static void main(String[] args) {
        new AntWorld();
    }

    private long time;

    /** Creates and runs the AntWorld scenario. */
    public AntWorld() {
        GraphGrammar grammar = null;

        try {
            File file = new File("antworld.gps");
            if (!file.exists() || !file.isDirectory()) {
                System.out.println("Unable to find antworld at location: "
                    + file.getCanonicalPath());
            }
            grammar = Groove.loadGrammar(file.getAbsolutePath()).toGrammar();
        } catch (Exception fe) {
            System.out.println("Error loading grammar: " + fe.getMessage());
        }

        if (grammar != null) {
            System.out.println("AntWorld loaded");

            AbstractStrategy strategy = new RandomLinearStrategy();
            strategy.enableCloseExit();
            DefaultGraphCalculator calc = new DefaultGraphCalculator(grammar);
            Scenario sc = createScenario(strategy, new AntworldAcceptor());

            startTimer();
            calc.getResult(sc);
        } else {
            System.out.println("There seems to be no grammar!!!");
        }
    }

    private Scenario createScenario(Strategy strategy, Acceptor acceptor) {
        DefaultScenario scenario = new DefaultScenario(strategy, acceptor);
        // scenario.setGTS(gts);
        return scenario;
    }

    /**
     * Stores the current system time in miliseconds
     */
    public void startTimer() {
        this.time = System.currentTimeMillis();
    }

    /**
     * Writes the elapsed time since startTime() in seconds to System.out
     */
    public long elapsedTime() {
        long nowtime = System.currentTimeMillis();
        return (nowtime - this.time);
    }

    /**
     * Acceptor that accepts every 50th closed state, and uses a
     * {@link PrintResult}.
     */
    class AntworldAcceptor extends Acceptor {
        /**
         * Creates a fresh instance.
         */
        public AntworldAcceptor() {
            super(new PrintResult());
        }

        private int counter = 1;

        @Override
        public void closeUpdate(LTS gts, State state) {
            if (state instanceof GraphNextState) {
                GraphNextState s = (GraphNextState) state;
                if (s.getEvent().getRule().getName().name().compareTo(
                    "end_turn") == 0) {
                    if (this.counter % 50 == 0) {
                        getResult().add((GraphState) state);
                    }
                    this.counter++;
                }
            }
        }

        /** This implementation returns an {@link AntworldAcceptor}. */
        @Override
        public Acceptor newInstance() {
            return new AntworldAcceptor();
        }
    }

    class PrintResult extends Result {
        @Override
        public Result newInstance() {
            return new PrintResult();
        }

        @Override
        public void add(GraphState t) {
            if (t instanceof GraphNextState) {
                GraphNextState gns = (GraphNextState) t;
                System.out.println(elapsedTime() + "ms:" + gns.getEvent()
                    + "(turns,circles,fields,ants)");
            }
        }
    }
}
