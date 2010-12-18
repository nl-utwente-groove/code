package groove.view.aspect;

import groove.graph.DefaultEdge;
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.GraphToGraphMap;
import groove.view.aspect.AspectGraph.AspectFactory;

/**
 * Graph element map from a plain graph to an aspect graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public class PlainToAspectMap
        extends
        GraphToGraphMap<DefaultNode,DefaultLabel,DefaultEdge,AspectNode,DefaultLabel,AspectEdge> {
    @Override
    public AspectFactory getFactory() {
        return new AspectFactory();
    }

    @Override
    public PlainToAspectMap newMap() {
        return new PlainToAspectMap();
    }
}