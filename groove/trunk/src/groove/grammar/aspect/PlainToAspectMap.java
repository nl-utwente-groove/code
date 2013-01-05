package groove.grammar.aspect;

import groove.grammar.aspect.AspectGraph.AspectFactory;
import groove.graph.ElementMap;
import groove.graph.GraphRole;
import groove.graph.plain.PlainEdge;
import groove.graph.plain.PlainNode;

/**
 * Graph element map from a plain graph to an aspect graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public class PlainToAspectMap extends
        ElementMap<PlainNode,PlainEdge,AspectNode,AspectEdge> {
    /** Creates a fresh, empty map. */
    public PlainToAspectMap(GraphRole graphRole) {
        super(AspectFactory.instance(graphRole));
        this.graphRole = graphRole;
    }

    @Override
    public PlainToAspectMap newMap() {
        return new PlainToAspectMap(this.graphRole);
    }

    private final GraphRole graphRole;
}