/* $Id: LayedOutGpsGrammar.java,v 1.2 2007-03-29 09:59:50 rensink Exp $ */
package groove.io;

import groove.graph.GraphFactory;
import groove.graph.aspect.AspectGraph;
import groove.trans.DefaultRuleFactory;
import groove.trans.RuleFactory;

/**
 * Grammar marshaller that reads in and constructs layout information
 * for the files.
 * @author Arend Rensink
 * @version $Revision $
 */
public class LayedOutGpsGrammar extends AspectualGpsGrammar {
	/**
	 * Constructs an instance of the grammar marshaller using a given
	 * rule factory.
	 */
	public LayedOutGpsGrammar(RuleFactory ruleFactory) {
		super(ruleFactory);
	}

	/**
	 * Constructs an instance of the grammar marshaller using the
	 * {@link DefaultRuleFactory}.
	 */
	public LayedOutGpsGrammar() {
		super();
	}

	/** This implementation returns a {@link LayedOutXml} graph reader. */
	@Override
	protected Xml<AspectGraph> createGraphMarshaller(GraphFactory graphFactory) {
    	return new AspectualGxl(new LayedOutXml(new UntypedGxl()));
	}
}
