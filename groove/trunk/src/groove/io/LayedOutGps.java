/* $Id: LayedOutGps.java,v 1.1 2007-04-30 19:53:24 rensink Exp $ */
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
public class LayedOutGps extends AspectualViewGps {
	/**
	 * Constructs an instance of the grammar marshaller using a given
	 * rule factory.
	 */
	public LayedOutGps(RuleFactory ruleFactory) {
		super(ruleFactory);
	}

	/**
	 * Constructs an instance of the grammar marshaller using the
	 * {@link DefaultRuleFactory}.
	 */
	public LayedOutGps() {
		super();
	}

	/** This implementation returns a {@link LayedOutXml} graph reader. */
	@Override
	protected Xml<AspectGraph> createGraphMarshaller(GraphFactory graphFactory) {
    	return new AspectGxl(new LayedOutXml(new DefaultGxl()));
	}
}
