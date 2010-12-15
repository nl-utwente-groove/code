/* $Id: AspectGxl.java,v 1.5 2008-01-30 09:33:42 iovka Exp $ */
package groove.io;

import groove.graph.Graph;
import groove.graph.GraphInfo;
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
    public AspectGxl(Xml<Graph> innerMarshaller) {
        this.marshaller = innerMarshaller;
    }

    /**
     * Converts the aspect graph to a plain graph and marshals it using the
     * inner marshaller.
     * @see AspectGraph#toPlainGraph()
     */
    public void marshalGraph(AspectGraph graph, File file) throws IOException {
        this.marshaller.marshalGraph(graph.toPlainGraph(), file);
    }

    /** Calls {@link #deleteGraph(File)} on the internal marshaller. */
    public void deleteGraph(File file) {
        this.marshaller.deleteGraph(file);
    }

    /**
     * Unmarshals the URL using the inner marshaller and converts the resulting
     * graph to an {@link AspectGraph}. Derives the name of the graph from the
     * last part of the URL path
     * @see AspectGraph#newInstance(Graph)
     */
    public AspectGraph unmarshalGraph(URL url) throws IOException {
        Graph plainGraph = this.marshaller.unmarshalGraph(url);
        GraphInfo.setName(plainGraph, extractName(url.getPath()));
        return AspectGraph.newInstance(plainGraph);
    }

    /**
     * Unmarshals the file using the inner marshaller and converts the resulting
     * graph to an {@link AspectGraph}. Derives the name of the graph from the
     * name part of the file
     * @see AspectGraph#newInstance(Graph)
     */
    public AspectGraph unmarshalGraph(File file) throws IOException {
        Graph plainGraph = this.marshaller.unmarshalGraph(file);
        GraphInfo.setName(plainGraph, extractName(file.toString()));
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
    private final Xml<Graph> marshaller;
}
