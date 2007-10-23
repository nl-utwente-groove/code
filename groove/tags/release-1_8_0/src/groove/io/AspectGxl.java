/* $Id: AspectGxl.java,v 1.3 2007-05-14 18:52:03 rensink Exp $ */
package groove.io;

import groove.graph.Graph;
import groove.graph.GraphShape;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.IOException;

/**
 * Class to marshal and unmarshal {@link AspectGraph}s as GXL files.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AspectGxl implements Xml<AspectGraph> {
	/** Constructs a reader on top of an {@link DefaultGxl}. */
	public AspectGxl() {
		this(new DefaultGxl());
	}
	
	/** Constructs a reader on top of a given graph marshaller. */
	public AspectGxl(Xml<Graph> innerMarshaller) {
		this.marshaller = innerMarshaller;
	}

	/** 
	 * Converts the aspect graph to a plain graph and marshals it 
	 * using the inner marshaller.
	 * @see AspectGraph#toPlainGraph()
	 */
	public void marshalGraph(AspectGraph graph, File file) throws IOException {
		marshaller.marshalGraph(graph.toPlainGraph(), file);
	}

	/**
	 * Unmarshals the file using the inner marshaller 
	 * and converts the resulting graph to an {@link AspectGraph}.
	 * @see AspectGraph#fromPlainGraph(GraphShape)
	 */
	public AspectGraph unmarshalGraph(File file) throws IOException {
		return AspectGraph.getFactory().fromPlainGraph(marshaller.unmarshalGraph(file));
	}
	
	
	/** Calls {@link #deleteGraph(File)} on the internal marshaller. */
	public void deleteGraph(File file) {
		marshaller.deleteGraph(file);
	}

	/** The marshaller to get and store graphs, which are then converted to aspect graphs. */
	private final Xml<Graph> marshaller;
}
