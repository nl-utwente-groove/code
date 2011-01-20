/* $Id: AspectGxl.java,v 1.5 2008-01-30 09:33:42 iovka Exp $ */
package groove.io;

import groove.graph.DefaultGraph;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.IOException;
import java.net.URL;

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
    public AspectGxl(Xml<DefaultGraph> innerMarshaller) {
        this.marshaller = innerMarshaller;
    }

    /**
     * Converts the aspect graph to a plain graph and marshals it using the
     * inner marshaller.
     * @see AspectGraph#toPlainGraph()
     */
    public <N extends Node,E extends Edge<N>> void marshalGraph(
            Graph<N,E> graph, File file) throws IOException {
        assert graph instanceof AspectGraph;
        this.marshaller.marshalGraph(((AspectGraph) graph).toPlainGraph(), file);
    }

    /** Calls {@link #deleteGraph(File)} on the internal marshaller. */
    public void deleteGraph(File file) {
        this.marshaller.deleteGraph(file);
    }

    /**
     * Unmarshals the URL using the inner marshaller and converts the resulting
     * graph to an {@link AspectGraph}. Derives the name of the graph from the
     * last part of the URL path
     * @see AspectGraph#newInstance(DefaultGraph)
     */
    public AspectGraph unmarshalGraph(URL url) throws IOException {
        DefaultGraph plainGraph = this.marshaller.unmarshalGraph(url);
        plainGraph.setName(extractName(url.getPath()));
        return AspectGraph.newInstance(plainGraph);
    }

    /**
     * Unmarshals the file using the inner marshaller and converts the resulting
     * graph to an {@link AspectGraph}. Derives the name of the graph from the
     * name part of the file
     * @see AspectGraph#newInstance(DefaultGraph)
     */
    public AspectGraph unmarshalGraph(File file) throws IOException {
        DefaultGraph plainGraph = this.marshaller.unmarshalGraph(file);
        plainGraph.setName(extractName(file.toString()));
        return AspectGraph.newInstance(plainGraph);
    }

    /**
     * Extracts a graph name from a location (given as a string) by regarding
     * the string as a file and returning the name part, without extension.
     * @param location string description of the location a graph was marshalled
     *        from
     * @return graph name extracted from <code>location</code>; non-null
     */
    private String extractName(String location) {
        return ExtensionFilter.getPureName(new File(location));
    }

    /**
     * The marshaller to get and store graphs, which are then converted to
     * aspect graphs.
     */
    private final Xml<DefaultGraph> marshaller;
}
