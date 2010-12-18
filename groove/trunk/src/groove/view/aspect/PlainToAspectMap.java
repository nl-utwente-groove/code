package groove.view.aspect;

import groove.graph.DefaultEdge;
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.ElementMap;
import groove.view.aspect.AspectGraph.AspectFactory;

/**
 * Graph element map from a plain graph to an aspect graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public class PlainToAspectMap
        extends
        ElementMap<DefaultNode,DefaultLabel,DefaultEdge,AspectNode,DefaultLabel,AspectEdge> {
    /** Creates a fresh, empty map. */
    public PlainToAspectMap() {
        super(new AspectFactory());
    }

    @Override
    public AspectFactory getFactory() {
        return new AspectFactory();
    }

    @Override
    public PlainToAspectMap newMap() {
        return new PlainToAspectMap();
    }
}