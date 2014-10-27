package groove.abstraction.neigh.shape;

import groove.abstraction.MyHashMap;
import groove.abstraction.neigh.equiv.EquivClass;
import groove.abstraction.neigh.equiv.NodeEquivClass;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostNode;
import groove.graph.AElementBiMap;
import groove.graph.Edge;
import groove.graph.Node;
import groove.util.Fixable;

import java.util.Map;

/**
 * Element map from a host graph to a shape.
 *
 * @author Eduardo Zambon
 */
public final class HostToShapeMap extends AElementBiMap<HostNode,HostEdge,HostNode,HostEdge>
    implements Fixable {

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    /** Cache used to speed-up some morphism look-ups. */
    private Map<EquivClass<ShapeNode>,EquivClass<HostNode>> ecMap;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. */
    public HostToShapeMap(ShapeFactory factory) {
        super(factory);
    }

    // ------------------------------------------------------------------------
    // Overriden methods
    // ------------------------------------------------------------------------

    /** Specialises the return type. */
    @Override
    public MyEdgeMap edgeMap() {
        return (MyEdgeMap) super.edgeMap();
    }

    /** Specialises the return type. */
    @Override
    public MyNodeMap nodeMap() {
        return (MyNodeMap) super.nodeMap();
    }

    @Override
    protected MyNodeMap createNodeMap() {
        return new MyNodeMap();
    }

    @Override
    protected MyEdgeMap createEdgeMap() {
        return new MyEdgeMap();
    }

    @Override
    public ShapeNode putNode(HostNode key, HostNode value) {
        return nodeMap().put(key, value);
    }

    @Override
    public ShapeEdge putEdge(HostEdge key, HostEdge value) {
        return edgeMap().put(key, value);
    }

    @Override
    public ShapeNode getNode(Node key) {
        return nodeMap().get(key);
    }

    @Override
    public ShapeEdge getEdge(Edge key) {
        return edgeMap().get(key);
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /**
     * Returns the set of nodes that maps to values occurring in the given
     * equivalence class.
     */
    public EquivClass<HostNode> getPreImages(EquivClass<ShapeNode> ecS) {
        if (this.ecMap == null) {
            this.ecMap = new MyHashMap<EquivClass<ShapeNode>,EquivClass<HostNode>>();
        }
        EquivClass<HostNode> nodesG = this.ecMap.get(ecS);
        if (nodesG == null) {
            nodesG = new NodeEquivClass<HostNode>(getFactory());
            for (ShapeNode nodeS : ecS) {
                nodesG.addAll(this.getPreImages(nodeS));
            }
            this.ecMap.put(ecS, nodesG);
        }
        return nodesG;
    }

    @Override
    public ShapeFactory getFactory() {
        return (ShapeFactory) super.getFactory();
    }

    /** Specialised edge map that always returns shape edges. */
    public class MyEdgeMap extends EdgeMap {
        @Override
        public ShapeEdge get(Object key) {
            return (ShapeEdge) super.get(key);
        }

        @Override
        public ShapeEdge put(HostEdge key, HostEdge value) {
            assert value instanceof ShapeEdge;
            return (ShapeEdge) super.put(key, value);
        }
    }

    /** Specialised node map that always returns shape nodes. */
    public class MyNodeMap extends NodeMap {
        @Override
        public ShapeNode get(Object key) {
            return (ShapeNode) super.get(key);
        }

        @Override
        public ShapeNode put(HostNode key, HostNode value) {
            assert value instanceof ShapeNode;
            return (ShapeNode) super.put(key, value);
        }
    }
}