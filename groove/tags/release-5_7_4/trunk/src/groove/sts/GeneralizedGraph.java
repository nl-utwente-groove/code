package groove.sts;

import groove.grammar.host.DefaultHostGraph;
import groove.grammar.host.HostGraph;
import groove.graph.iso.IsoChecker;

/**
 * A graph where the data values are always of a default value. 
 * Two GeneralizedGraphs are considered equal if they are isomorphic.
 * TODO: change to using a PointAlgebra.
 * @author Vincent de Bruijn
 *
 */
public class GeneralizedGraph extends DefaultHostGraph {

    private static IsoChecker isoChecker = IsoChecker.getInstance(true);

    /**
     * Creates a new instance.
     * @param graph A generalized graph.
     */
    public GeneralizedGraph(HostGraph graph) {
        super(graph);
    }

    @Override
    public boolean equals(Object o) {
        HostGraph graph = (HostGraph) o;
        return isoChecker.areIsomorphic(this, graph);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + nodeCount();
        result = prime * result + edgeCount();
        return result;
    }
}
