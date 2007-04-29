/* $Id: LayedOutGpsGrammar.java,v 1.3 2007-04-29 09:22:31 rensink Exp $ */
package groove.io;

import groove.graph.GraphFactory;
import groove.trans.DefaultRuleFactory;
import groove.trans.RuleFactory;
import groove.view.aspect.AspectGraph;

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
