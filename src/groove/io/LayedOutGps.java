/* $Id: LayedOutGps.java,v 1.2 2007-10-06 11:27:51 rensink Exp $ */
package groove.io;

import groove.graph.GraphFactory;
import groove.view.aspect.AspectGraph;

/**
 * Grammar marshaller that reads in and constructs layout information
 * for the files.
 * @author Arend Rensink
 * @version $Revision $
 */
public class LayedOutGps extends AspectualViewGps {
	/**
	 * Constructs an instance of the grammar marshaller.
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
