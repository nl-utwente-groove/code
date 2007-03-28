/* $Id: AspectualGxl.java,v 1.1 2007-03-28 15:12:32 rensink Exp $ */
package groove.io;

import groove.graph.Graph;
import groove.graph.GraphFormatException;
import groove.graph.GraphShape;
import groove.graph.aspect.AspectGraph;

import java.io.File;
import java.io.IOException;

/**
 * Class to marshal and unmarshal {@link AspectGraph}s as GXL files.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AspectualGxl implements Xml<AspectGraph> {
	/** Constructs a reader on top of an {@link UntypedGxl}. */
	public AspectualGxl() {
		this(new UntypedGxl());
	}
	
	/** Constructs a reader on top of a given graph marshaller. */
	public AspectualGxl(Xml<Graph> innerReader) {
		this.graphMarshaller = innerReader;
	}

	/** 
	 * Converts the aspect graph to a plain graph and marshals it 
	 * using the inner marshaller.
	 * @see AspectGraph#toPlainGraph()
	 */
	public void marshalGraph(AspectGraph graph, File file) throws XmlException,
			IOException {
		graphMarshaller.marshalGraph(graph.toPlainGraph(), file);
	}

	/**
	 * Unmarshals the file using the inner marshaller 
	 * and converts the resulting graph to an {@link AspectGraph}.
	 * @see AspectGraph#fromPlainGraph(GraphShape)
	 */
	public AspectGraph unmarshalGraph(File file) throws XmlException, IOException {
		try {
			return AspectGraph.getFactory().fromPlainGraph(graphMarshaller.unmarshalGraph(file));
		} catch (GraphFormatException exc) {
			throw new XmlException(exc.getMessage());
		}
	}

	/** The marshaller to get and store graphs, which are then converted to aspect graphs. */
	private final Xml<Graph> graphMarshaller;
}
