package groove.explore.chscenar;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import groove.explore.chscenar.parser.ConfigLexer;
import groove.explore.chscenar.parser.ConfigParser;
import groove.explore.result.Acceptor;
import groove.explore.result.Result;
import groove.explore.strategy.Strategy;
import groove.util.Groove;

/** Allows to check whether a combination of strategy - result - acceptor is
 * allowed for a scenario.
 * 
 * @author Iovka Boneva
 *
 */
public class ScenarioChecker {

	/** The super-class of all strategies. */
	public static final Class<?> STRATEGY_CLASS = groove.explore.strategy.Strategy.class;
	/** The super-class of all results. */
	public static final Class<?> RESULT_CLASS = groove.explore.result.Result.class;
	/** The super-class of all acceptors. */
	public static final Class<?> ACCEPTOR_CLASS = groove.explore.result.Acceptor.class;
	
	/** A list of packages where strategies should be looked for by default. */
	public static final String[] STRATEGY_PACKAGES = {"groove.explore.strategy"};
	/** A list of packages where results should be looked for by default. */
	public static final String[] RESULT_PACKAGES = {"groove.explore.result"};
	/** A list of packages where acceptors should be looked for by default. */
	public static final String[] ACCEPTOR_PACKAGES = {"groove.explore.result"};
	
	
	/** Gets a class object from a string description.
	 * The given name may be either relative to one of the strategy/result/acceptor packages, 
	 * or absolute.
	 * @throws ClassNotFoundException 
	 * @see #STRATEGY_PACKAGES
	 * @see #RESULT_PACKAGES
	 * @see #ACCEPTOR_PACKAGES
	 */
	public static Class<?> getClass (String name, Component comp) throws ClassNotFoundException {
		switch (comp) {
		case STRATEGY : return getClass(name, STRATEGY_PACKAGES);
		case RESULT : return getClass(name, RESULT_PACKAGES);
		case ACCEPTOR : return getClass(name, ACCEPTOR_PACKAGES);	
		default : return null; // never happens
		}
	}

	/** Helper method */
	private static Class<?> getClass (String name, String[] packages) throws ClassNotFoundException {
		if (name.contains(".")) {
			return Class.forName(name); 
		} else {
			for (int i = 0; i < packages.length; i++) {
				try {
					return Class.forName(packages[i] + "." + name);
				} catch (ClassNotFoundException e) {
					continue;
				}
			}
		}
		// none of the packages contains a class corresponding to name 
		throw new ClassNotFoundException(name);
	}
	
	/** Utility enum type used to factorise several methods. */
	public static enum Component {
		STRATEGY, RESULT, ACCEPTOR;
	}
	
	
	/** 
	 * @param <T> The type for the result and the acceptor (should be the same).
	 * @param str 
	 * @param res
	 * @param acc
	 * @return
	 */
	public <T> boolean isAllowed (Strategy str, Result res, Acceptor acc) {
		return allowed.isAllowedConfiguration(str.getClass(), res.getClass(), acc.getClass());
	}
	
	public boolean isAllowed (Class<?> strClass, Class<?> resClass, Class<?> accClass) {
		return allowed.isAllowedConfiguration(strClass, resClass, accClass);
	}
	
	// --------------------------------------------------------------------------
	// FIELDS, CONSTRUCTORS, STANDARD METHODS
	// --------------------------------------------------------------------------
	
	/** The rule for allowed non conditional strategies. */
	private AllowRule allowed = new AllowRuleImpl();
	
	public ScenarioChecker () {
		ConfigParser parser = null;
		try {
			URL configURL = Groove.getResource(Groove.ALLOWED_SCENARIOS_CONFIGURATION_FILE);
			// configURL will be null if the file was not found
			if (configURL == null) {
				throw new IOException();
			}
			InputStream inStream = configURL.openStream();  // IOException possible
			ConfigLexer lexer = new ConfigLexer(new ANTLRInputStream(inStream)); // IOExceptionPossible
			parser = new ConfigParser(new CommonTokenStream(lexer));
		
		} catch (IOException e) {
			System.err.println("Could not open configuration file " + Groove.ALLOWED_SCENARIOS_CONFIGURATION_FILE);
			e.printStackTrace();
		}

		try {
			// System.out.println("START PARSING");
			allowed = parser.prog().value;
			// System.out.println("STOP PARSING");
			// System.out.print("PARSE RESULT : ");
			// if (allowed == null) {
			// 	System.out.println("allowed is null");
			// }
			// System.out.println(allowed.toString());
		} catch (RecognitionException e) {
			e.printStackTrace();
			System.err.println("Please correct upon errors in the configuration file (" + Groove.ALLOWED_SCENARIOS_CONFIGURATION_FILE + ").");
			System.err.println("Aborting.");
			System.exit(1);
		} 
		
	}
	
	@Override
	public String toString () {
		return allowed.toString();
	}
	
}
