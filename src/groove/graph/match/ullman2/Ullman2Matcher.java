package groove.graph.match.ullman2;

import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.graph.match.Matcher;
import groove.graph.match.ullman.BooleanMatrix;
import groove.rel.VarNodeEdgeHashMap;

import java.util.*;

/**
 * @author J. ter Hove
 */
public class Ullman2Matcher implements Matcher {
    // information for GROOVE

    /**
     * The underlying morphism of this matcher *
     */
    private Morphism morphism = null;

    /**
     * indicates if a matching has been found *
     */
    private boolean found = false;
    private NodeEdgeMap singularMap = null;

    private Vector<BooleanMatrix> MD;
    private Vector<NodeEdgeMap> solutions = new Vector<NodeEdgeMap>();

    /**
     * Constructs a matcher as an extension of a given morphism.
     * The images of the morphism are not checked for consistency.
     *
     * @param morphism the intended basis of the simulation
     * @ensure <tt>getMorphism() == morph</tt>
     */
    public Ullman2Matcher(Morphism morphism) {
        this.morphism = morphism;

        Pa = morphism.dom().nodeCount();
        A = makeAdjency(morphism.dom());

        Pb = morphism.cod().nodeCount();
        B = makeAdjency(morphism.cod());

        M0 = makeCompatibilityMatrix(morphism.dom(), morphism.cod());
        System.out.println(M0);

        M = M0.copy();

        H = new int[Pa];
        for( int j = 0; j < H.length; j++ )
            H[j] = -1;

        F = new boolean[Pb];

        MD = new Vector<BooleanMatrix>(Pa);
        for (int k = 0; k < Pa; k++)
            MD.add(null); // so we can set(index, BM) later
    }

    // algorithm information
    private int Pa;
    private int Pb;

    private BooleanMatrix A;
    private BooleanMatrix B;

    private BooleanMatrix M0;
    private BooleanMatrix M;

    // control information to encode the 'goto' based flow of the original algorithm
    private boolean searching = true;

    private int[] H;
    private boolean[] F;
    private int d = 0;

    protected boolean find() {
        if( searching ) {
            match();
            searching = false;
        }

        return !solutions.isEmpty();
    }

    int k;
    private boolean backtrack;

    private boolean match() {
        int k = 0;
        while (searching) {
            //System.out.println(M);
            //showH();
           //showF();

            if( d < Pa ) {
                k = H[d];
                MD.set(d, M.copy());

                do {
                    k++;
                    if( k == Pb ) {
                        backtrack = true;
                        break;
                    }
                } while( !M.get(d,k) || F[k] );

                if( backtrack ) {
                    backtrack = false;

                    // do column backtrack
                    H[d] = -1; // start from the left
                    d--;
                    if(d == -1) {
                        return true;
                    }                    
                    M = MD.get(d).copy();
                    F[H[d]] = false;                    
                    continue;
                }

                for( int j = 0; j < Pb; j++ ) {
                    if( j == k )
                        continue;
                    M.set(d,j, false);
                }

                H[d] = k;
                F[k] = true;
                d++;
            } else {
                if( check(M) ) {
                    showMapping(M);
                    computeMapping(M);
                    solutions.add(getSingularMap());
                    cloneSingularMap();                    
                }
                // do row 'backtrack'
                d--;
                M = MD.get(d).copy();
                F[k] = false;
            }
        }
        return false;
    }

    private void showH() {
        System.out.print("( ");
        for( int j = 0; j < H.length; j++ )
            System.out.print(H[j] + " ");
        System.out.println(")");
    }

    private void showF() {
        System.out.print("( ");
        for( int j = 0; j < F.length; j++ )
            System.out.print(F[j] + " ");
        System.out.println(")");
    }

    private void showMapping(BooleanMatrix m) {
        int rows = m.rows();
        int cols = m.cols();

        Graph lhs = dom();
        Graph rhs = cod();

        Node[] lhsnodes = new Node[lhs.nodeCount()];
        int lhsmin = 10000;
        for (Node n : lhs.nodeSet()) {
            if (nodeNumber(n) < lhsmin)
                lhsmin = nodeNumber(n);
        }
        for (Node n : lhs.nodeSet()) {
            lhsnodes[nodeNumber(n) - lhsmin] = n;
        }

        Node[] rhsnodes = new Node[rhs.nodeCount()];
        int rhsmin = 10000;
        for (Node n : rhs.nodeSet()) {
            if (nodeNumber(n) < rhsmin)
                rhsmin = nodeNumber(n);
        }
        for (Node n : rhs.nodeSet()) {
            rhsnodes[nodeNumber(n) - rhsmin] = n;
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (m.get(i, j)) {

                    System.out.println(lhsnodes[i] + " -> " + rhsnodes[j]);

                }
            }
        }
        System.out.println();
    }

    private void computeMapping(BooleanMatrix m) {
        int rows = m.rows();
        int cols = m.cols();

        Graph lhs = dom();
        Graph rhs = cod();

        Node[] lhsnodes = new Node[lhs.nodeCount()];
        int lhsmin = 10000;
        for (Node n : lhs.nodeSet()) {
            if (nodeNumber(n) < lhsmin)
                lhsmin = nodeNumber(n);
        }
        for (Node n : lhs.nodeSet()) {
            lhsnodes[nodeNumber(n) - lhsmin] = n;
        }

        Node[] rhsnodes = new Node[rhs.nodeCount()];
        int rhsmin = 10000;
        for (Node n : rhs.nodeSet()) {
            if (nodeNumber(n) < rhsmin)
                rhsmin = nodeNumber(n);
        }
        for (Node n : rhs.nodeSet()) {
            rhsnodes[nodeNumber(n) - rhsmin] = n;
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (m.get(i, j)) {

                    getSingularMap().putNode(lhsnodes[i], rhsnodes[j]);
                    for (Edge e : lhs.edgeSet(lhsnodes[i])) {
                        for (Edge ee : rhs.edgeSet(rhsnodes[j])) {
                            if (e.label() == ee.label()) {
                                getSingularMap().putEdge(e, ee);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean check(BooleanMatrix m) {
        //System.out.println(m);
        //System.out.println(B);
        //System.out.println("");
        BooleanMatrix tmp = m.mul(B);
        //System.out.println("M*B");
        //System.out.println(tmp);
        tmp = tmp.transpose();
        //System.out.println("M*B T");
        //System.out.println(tmp);
        tmp = m.mul(tmp);
        //System.out.println("C = M*((M*B)T)");
        //System.out.println(tmp);
        //System.out.println("A");
        //System.out.println(A);
        tmp = tmp.transpose();
        //System.out.println("A=>C");
        //System.out.println(tmp);
        return A.implies(tmp);
    }

    private BooleanMatrix makeCompatibilityMatrix(Graph lhs, Graph rhs) {
        System.out.println(lhs);
        System.out.println(rhs);

        BooleanMatrix bm = new BooleanMatrix(lhs.nodeCount(), rhs.nodeCount());

        int lhsmin = 10000;
        for (Node n : lhs.nodeSet()) {
            if (nodeNumber(n) < lhsmin)
                lhsmin = nodeNumber(n);
        }

        int rhsmin = 10000;
        for (Node n : rhs.nodeSet()) {
            if (nodeNumber(n) < rhsmin)
                rhsmin = nodeNumber(n);
        }

        for (Node source : lhs.nodeSet()) {
            for (Node image : rhs.nodeSet()) {
                if( (outgoing(rhs, image) >= outgoing(lhs, source)) && (ingoing(rhs, image) >= ingoing(lhs, source)) ) {
                    bm.set(nodeNumber(source) - lhsmin, nodeNumber(image) - rhsmin, true);
                }
            }
        }
        return bm;
    }

    private int ingoing(Graph g, Node node) {
        Collection<? extends Edge> edges = g.edgeSet(node);
        int count = 0;
        for (Edge e : edges) {
            if (e.opposite() == node)
                count++;
        }
        return count;
    }

    private int outgoing(Graph g, Node node) {
        Collection<? extends Edge> edges = g.edgeSet(node);
        int count = 0;
        for (Edge e : edges) {
            if (e.source() == node)
                count++;
        }
        return count;
    }

    private BooleanMatrix makeAdjency(Graph graph) {
        BooleanMatrix bm = new BooleanMatrix(graph.nodeCount(), graph.nodeCount());

        int min = 10000;
        for (Node n : graph.nodeSet()) {
            if (nodeNumber(n) < min)
                min = nodeNumber(n);
        }

        for (Node source : graph.nodeSet()) {
            for (Node target : graph.nodeSet()) {
                for (Edge e : graph.edgeSet(source)) {
                    if ((e.source() == source) && (e.opposite() == target)) {
                        bm.set(nodeNumber(source) - min, nodeNumber(target) - min, true);
                    }
                }
            }
        }
        return bm;
    }

    private int nodeNumber(Node node) {
        return ((DefaultNode) node).getNumber();
    }

    // methods for GROOVE
    public boolean hasRefinement() {
        if (found)
            return true;
        // we have either
        //    no matching, checking again takes time but is no problem
        // or
        //    we haven't checked yet -> so check
        return find();
    }

    public NodeEdgeMap getRefinement() {
        if (found)
            return getSingularMap();
        // we have either
        //    no matching, checking again takes time but is no problem
        // or
        //    we haven't checked yet -> so check
        if (find())
            return getSingularMap();

        return null;
    }

    public Collection<NodeEdgeMap> getRefinementSet() {
        Collection<NodeEdgeMap> result = new ArrayList<NodeEdgeMap>();
        //reporter.start(GET_REFINEMENT_SET);
        while (find())
            result.add(this.getSingularMap());
        //reporter.stop();
        return result;
    }

    public Iterator<? extends NodeEdgeMap> getRefinementIter() {
        Iterator<NodeEdgeMap> result;
        //reporter.start(GET_REFINEMENT_ITER);
        result = new Iterator<NodeEdgeMap>() {
            public boolean hasNext() {
                // test if there is an unreturned next or if we are done
                if (next == null && !atEnd) {
                    // search for the next solution
                    if (find()) {
                        next = Ullman2Matcher.this.getSolution();
                    } else {
                        // there is none and will be none; give up
                        atEnd = true;
                    }
                }
                return !atEnd;
            }

            public NodeEdgeMap next() {
                if (hasNext()) {
                    NodeEdgeMap result = next;
                    next = null;
                    return result;
                } else {
                    throw new NoSuchElementException();
                }
            }


            public void remove() {
                throw new UnsupportedOperationException();
            }

            /**
             * The next refinement to be returned.
             */
            private NodeEdgeMap next;
            /**
             * Flag to indicate that the last refinement has been returned,
             * so {@link #next()} henceforth will return <code>false</code>.
             */
            private boolean atEnd = false;
        };
        //reporter.stop();
        return result;
    }

    private NodeEdgeMap getSolution() {
        return solutions.remove(0);
    }

    /**
     * Returns the currently built map between the domain and codomain
     * elements.
     */
    public NodeEdgeMap getSingularMap() {
        if (singularMap == null) {
            singularMap = computeSingularMap();
        }
        return singularMap;
    }

    /**
     * Internally clones the element map,
     * so future changes to the map will not affect aliases of the current map.
     */
    protected void cloneSingularMap() {
        singularMap = getSingularMap().clone();
    }

    /**
     * Computes a fresh element map on the basis of the underlying morphism.
     */
    protected NodeEdgeMap computeSingularMap() {
        NodeEdgeMap result = createSingularMap();
        result.putAll(getMorphism().elementMap());
        return result;
    }

    /**
     * Callback factory method to create the node-edge map to store the
     * final result of the simulation.
     */
    protected NodeEdgeMap createSingularMap() {
        return new VarNodeEdgeHashMap();
    }

    public Morphism getMorphism() {
        return morphism;
    }

    public Graph dom() {
        return morphism.dom();
    }

    public Graph cod() {
        return morphism.cod();
    }

    @Deprecated
    public boolean isConsistent() {
        return true;
    }

    @Deprecated
    public boolean isRefined() {
        return getSingularMap().size() == morphism.dom().size();
    }
}
/*
        while (searching) {
            int k = H[d];
            boolean backtrack = false;

            System.out.println(M);
            MD.set(d, M.copy());

            do {
                k++;
                if( k == Pb ) {
                    backtrack = true;
                    break;
                }
            } while( !M.get(d,k) || F[k] ); // while (not 1) or (already selected in this column)

            if( backtrack ) {
                d--;
                if( d == -1 ) {
                    searching = false;
                    return true;
                }

                //for( int j = H[d]; j < k; j++ )
                    F[k] = false;
                k = H[d];
                M = MD.get(d).copy();
                continue;
            }

            for( int j = 0; j < Pb; j++ ) {
                if( j == k )
                    continue;
                M.set(d,j, false);
            }

            if( d == (Pa-1) ) {
                System.out.println(M);
                showMapping(M);
                if( check(M) ) {
                    found = true;

                    //showMapping(M);
                    computeMapping(M);

                    solutions.add(getSingularMap());
                    cloneSingularMap();
                }

                // possible mapping;

                F[k] = true;
                M = MD.get(d).copy();
                k = H[d-1];
                continue;
            }

            F[k] = true;
            H[d] = k;
            d++;
        }
        return false;
*/