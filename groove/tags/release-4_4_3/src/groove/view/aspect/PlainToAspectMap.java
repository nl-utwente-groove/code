package groove.view.aspect;

import groove.graph.DefaultEdge;
import groove.graph.DefaultNode;
import groove.graph.ElementMap;
import groove.graph.GraphRole;
import groove.view.aspect.AspectGraph.AspectFactory;

/**
 * Graph element map from a plain graph to an aspect graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public class PlainToAspectMap extends
        ElementMap<DefaultNode,DefaultEdge,AspectNode,AspectEdge> {
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