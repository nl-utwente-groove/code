/* $Id: LayedOutGpsGrammar.java,v 1.1 2007-03-28 15:12:32 rensink Exp $ */
package groove.io;

import groove.graph.GraphFactory;
import groove.trans.RuleFactory;

/**
 * Grammar reader that reads in and constructs layout information
 * for the files.
 * @author Arend Rensink
 * @version $Revision $
 */
public class LayedOutGpsGrammar extends GpsGrammar {
	/**
	 * Constructs an instance of the grammar reader using a given
	 * default rule factory.
	 */
	public LayedOutGpsGrammar(RuleFactory ruleFactory) {
		super(ruleFactory);
	}

	/** This implementation returns a {@link LayedOutXml} graph reader. */
	@Override
	protected Xml createGraphLoader(GraphFactory graphFactory) {
		return new LayedOutXml(graphFactory);
	}
}
