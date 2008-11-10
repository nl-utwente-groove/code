package groove.demo;

import groove.calc.DefaultGraphCalculator;
import groove.lts.GraphState;
import groove.trans.GraphGrammar;
import groove.util.Groove;

import java.io.File;
import java.util.Collection;

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
 */

/**
 * @author Staijen
 * 
 */
public class LiveContest {

    /**
     * Starts a new runner
     * @param args not used
     */
    public static void main(String[] args) {
        // for( int i = 0; i < 10; i++ ) {
        new LiveContest();
        // }
    }

    private long time;

    public LiveContest() {
        GraphGrammar grammar = null;

        try {
            File file = new File("grabats.gps");
            if (!file.exists() || !file.isDirectory()) {
                System.out.println("Unable to find grammar for GRaBaTs Live Contest at location: ");
                System.out.println(file.getCanonicalPath());
            }
            grammar = Groove.loadGrammar(file.getAbsolutePath()).toGrammar();
        } catch (Exception fe) {
            System.out.println("Error loading grammar: " + fe.getMessage());
        }

        if (grammar != null) {
            System.out.println("GRaBaTs Live Contest grammar loaded");

            DefaultGraphCalculator calc = new DefaultGraphCalculator(grammar);
            // GraphState max = calc.getMax(new ExploreStateDFStrategy());
            startTimer();
            // uncomment the next line for a randomized linear strategy
            // GraphState max = calc.getMax(new RandomLinearStrategy());
            // uncomment the next line to get any max state (NOT RANDOM)
            Collection<GraphState> maxes = calc.getAllMax();

            reportTime();

            System.out.println("Found" + maxes.size() + " solutions");

            // for( GraphState max : maxes ) {
            //			
            // if( max != null && max instanceof GraphNextState ) {
            // // get source of last transition
            // GraphState gs = ((GraphNextState) max).source();
            // // customize this line to evaluate the final transition
            // System.out.println("Simulation result (..): " + ((GraphNextState)
            // gs).getEvent() );
            // }
            // }

        } else {
            System.out.println("Error loading the grammar");
        }
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
    public void reportTime() {
        long nowtime = System.currentTimeMillis();
        long seconds = (nowtime - this.time) / 1000;
        long milis = (nowtime - this.time) % 1000;
        System.out.println("Simulation took: " + seconds + "." + milis + "s");
    }

}
