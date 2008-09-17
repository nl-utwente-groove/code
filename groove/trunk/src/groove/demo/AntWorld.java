package groove.demo;

import groove.calc.DefaultGraphCalculator;
import groove.calc.MaximalStateProperty;
import groove.explore.DefaultScenario;
import groove.explore.Scenario;
import groove.explore.result.Acceptor;
import groove.explore.result.PropertyAcceptor;
import groove.explore.result.Result;
import groove.explore.result.SizedResult;
import groove.explore.strategy.AbstractStrategy;
import groove.explore.strategy.RandomLinearStrategy;
import groove.explore.strategy.Strategy;
import groove.lts.GTS;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.LTS;
import groove.lts.State;
import groove.trans.GraphGrammar;
import groove.util.Groove;

import java.io.File;

/**
 * Runner class for the AntWorld sample, using a branching stategy to collect a random next state.
 * It will find a final state, which is enforced by the grammar using a maximum number of turns, 
 * specified in the "stop" rule.
 * 
 * Given this final state, it will print the details of the second-last transition, which should be an
 * application of the end_turn rule exporting the number of turns, number of circles and number of ants.
 * 
 * Also, the execution time is measured and printed.
 */

/**
 * @author Staijen
 *
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
	
	public AntWorld() {
		GraphGrammar grammar = null;

		try {
			File file = new File("antworld.gps");
			if( !file.exists() || !file.isDirectory() ) {
				System.out.println("Unable to find antworld at location: " + file.getCanonicalPath());
			}
			grammar = Groove.loadGrammar(file.getAbsolutePath()).toGrammar();
		} catch(Exception fe) {
			System.out.println("Error loading grammar: " + fe.getMessage());
		}
		
		if( grammar != null ) {
			System.out.println("AntWorld loaded");

			AbstractStrategy strategy = new RandomLinearStrategy();
			strategy.enableCloseExit();
			DefaultGraphCalculator calc = new DefaultGraphCalculator(grammar);
			Scenario<GraphState> sc = createScenario(calc.getGTS(), strategy, new AntworldAcceptor(), new PrintResult<GraphState>());

			startTimer();
			calc.getResult(sc);
		} else {
			System.out.println("There seems to be no grammar!!!");
		}
	}

    private Scenario<GraphState> createScenario(GTS gts, Strategy strategy, Acceptor<GraphState> acceptor, Result<GraphState> result) {
    	DefaultScenario<GraphState> scenario = new DefaultScenario<GraphState>();
    	scenario.setResult(result);
    	scenario.setAcceptor(acceptor);
    	scenario.setStrategy(strategy);
    	acceptor.setResult(result);
    	scenario.setGTS(gts);
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
		long seconds = (nowtime-this.time)/1000;
		return (nowtime-this.time);
	}
	
	class AntworldAcceptor extends Acceptor<GraphState> {
		private int counter = 1;
		
		@Override
		public void closeUpdate(LTS gts, State state) {
			if( state instanceof GraphNextState ) {
				GraphNextState s = (GraphNextState) state;
				if( s.getEvent().getRule().getName().name().compareTo("end_turn") == 0 ) {
					if( counter % 50 == 0 ) {
						this.getResult().add((GraphState)state);
					}
					counter++;
				}
			}
		}
		
	}
	
	
	class PrintResult<GraphState> extends Result<GraphState> {

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Result<GraphState> getFreshResult() {
			// TODO Auto-generated method stub
			return new PrintResult<GraphState>();
		}
		
		@Override
		public void add(GraphState t) {
			if( t instanceof GraphNextState ) {
				GraphNextState gns = (GraphNextState) t;
				System.out.println(AntWorld.this.elapsedTime() + "ms:" + gns.getEvent() + "(turns,circles,fields,ants)");
			}
		}
		
	}
	
}
