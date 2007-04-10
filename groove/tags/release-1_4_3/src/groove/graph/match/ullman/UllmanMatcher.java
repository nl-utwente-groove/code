package groove.graph.match.ullman;

import groove.graph.match.Matcher;
import groove.graph.*;
import groove.rel.VarNodeEdgeHashMap;

import java.util.*;

/**
 * @author J. ter Hove
 */
public class UllmanMatcher implements Matcher {
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

    /**
     * Constructs a matcher as an extension of a given morphism.
     * The images of the morphism are not checked for consistency.
     *
     * @param morphism the intended basis of the simulation
     * @ensure <tt>getMorphism() == morph</tt>
     */
    public UllmanMatcher(Morphism morphism) {
        this.morphism = morphism;

        Pa = morphism.dom().nodeCount();
        A = makeAdjency(morphism.dom());

        Pb = morphism.cod().nodeCount();
        B = makeAdjency(morphism.cod());

        /*
        System.out.println("A Pa:" + Pa);
        System.out.println(morphism.dom());
        System.out.println(A);

        System.out.println("B Pb:" + Pb);
        System.out.println(morphism.cod());
        System.out.println(B);

        System.out.println("M0");
        */

        M0 = makeCompatibilityMatrix(morphism.dom(), morphism.cod());
        
        //System.out.println(M0);

        F = new boolean[Pb];
        H = new int[Pa];

        MD = new Vector<BooleanMatrix>(Pa);
        for (int k = 0; k < Pa; k++)
            MD.add(null); // so we can set(index, BM) later

        // step 1
        M = M0.copy();
        d = 0;
        // java initializes to 0
        H[0] = -1;
    }

    // algorithm information
    private int Pa;
    private int Pb;

    private BooleanMatrix A;
    private BooleanMatrix B;

    private BooleanMatrix M0;
    private BooleanMatrix M;

    private boolean[] F;

    private int[] H;

    private int d;

    private Vector<BooleanMatrix> MD;

    private int k;

    // control information to encode the 'goto' based flow of the original algorithm
    private boolean searching = true;
    private boolean foundMdjAndNotFj = false;
    private boolean goto5 = false;
    private boolean goto3 = false;
    private boolean goto7 = false;

    protected boolean find() {
        if (found) {
            // we already have a solution, so clone map to prevent sharing errors
            cloneSingularMap();
            found = false;
        }

        while (searching) {
            System.out.println(M);

            foundMdjAndNotFj = false; // so we only look at control flow flags

            if (!goto3 && !goto5 && !goto7) { // skip if goto3,5,7

                for (int j = 0; j < Pb; j++) { // step 2
                    if (M.get(d, j) && !F[j]) {
                        foundMdjAndNotFj = true;
                        break;
                    }
                }

            }
            boolean a = (!foundMdjAndNotFj && !goto3 && !goto5 && !goto7);
            if (!(a || goto7)) { // fall through on goto3,5 = 1 skip on f = 0 and goto7 = 1
                if (!goto3 && !goto5) {

                    // there is a value of j (step 2)
                    MD.set(d, M.copy());

                    k = -1;
                    if (d == 0)
                        k = H[0];

                }
                if (!goto5) {

                    goto3 = false;
                    do { // step 3
                        k++;
                    } while (!M.get(d, k) || F[k]);

                    for (int j = 0; j < Pb; j++) {
                        if (j != k)
                            M.set(d, j, false);
                    }

                }

                if ((d == (Pa - 1)) || goto5) { // step 4

                    if (!goto5) {
                        // check
                        System.out.println(M);
                        if (check(M)) {

                            
                            found = true;
                            goto5 = true;
                            computeMapping(M);

                            System.out.println();
                            showMapping(M);
                            
                            return found;
                        }

                    }

                    goto5 = false;
                    boolean foundMdjAndNotFj2 = false;
                    for (int j = (k + 1); j < Pb; j++) { // step 5
                        if (M0.get(d, j) && !F[j]) {
                            foundMdjAndNotFj2 = true;
                            break;
                        }
                    }
                    if (!foundMdjAndNotFj2) {
                        goto7 = true;
                        continue; // goto 7  (we set only 1 boolean)
                    }


                    M = MD.get(d).copy();
                    goto3 = true;
                    continue; // goto 3 (we set only 1 boolean)
                }

                H[d] = k; // step 6
                F[k] = true;
                d++;
                continue; // goto 2 (we set no boolean)
            }

            goto7 = false; // we jumped here
            if (d == 0) { // step 7
                found = false;
                searching = false;
                return found; // terminate the algorithm
            }

            F[k] = false;
            d--;
            M = MD.get(d).copy();

            for( int j = (k-1); j > H[d]; j-- )
                F[j] = false;

            k = H[d];
            
            
            goto5 = true; // (we set only 1 boolean) jump occurs naturally as long as searching == true
        }

        return found;
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
                if (outgoing(rhs, image) >= outgoing(lhs, source)) {
                    bm.set(nodeNumber(source) - lhsmin, nodeNumber(image) - rhsmin, true);
                }
            }
        }
        return bm;
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
                        next = UllmanMatcher.this.getSingularMap();
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
        return new NodeEdgeHashMap();
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

/**
 while(searching) {
 System.out.println(M);
 if( !goto5 ) {
 foundMdjFj = false;
 if( !goto3 ) {
 for( int j = 0; j < Pb; j++ ) {
 if( M.get(d,j) && !F[j] ) {
 foundMdjFj = true;
 break;
 }
 }
 } else {
 foundMdjFj = true;
 }
 if( foundMdjFj ) {
 if( !goto3 ) {
 MD.add(d,M.copy());

 if( d == 0 )
 k = H[0];
 else
 k = -1;
 }
 // step 3
 goto3 = false;
 do {
 k++;
 } while(!M.get(d,k) || F[k]);

 for( int j = 0; j < Pb; j++ ) {
 if( j == k )
 continue;
 M.set(d,j, false);
 }

 if( d == (Pa-1) ) {
 System.out.println(M);
 // check for iso
 if( check(M) ) {
 computeMapping(M, morphism.dom(), morphism.cod());
 found = true;
 goto5 = true;
 break;
 }

 foundMdjFj2 = false;
 for( int j = (k+1); j < Pb; j++ ) {
 if( M.get(d,j) && !F[j] ) {
 foundMdjFj2 = true;
 break;
 }
 }
 if( foundMdjFj2 ) {
 M.assign(MD.get(d));
 goto3 = true;
 continue;
 }
 } else {
 foundMdjFj2 = true;
 }
 if( foundMdjFj2 ) {
 foundMdjFj2 = false;
 H[d] = k;
 F[k] = true;
 d++;
 continue; // goto 2
 }
 }
 } else {
 goto5       = false;
 foundMdjFj2 = false;
 for( int j = (k+1); j < Pb; j++ ) {
 if( M.get(d,j) && !F[j] ) {
 foundMdjFj2 = true;
 break;
 }
 }
 if( foundMdjFj2 ) {
 M.assign(MD.get(d));
 goto3 = true;
 continue;
 }
 }
 // step 7
 if( d == 0 ) {
 found = false;
 break;
 }
 F[k] = false;
 d--;
 M.assign(MD.get(d));
 k = H[d];

 goto5 = true;

 continue;
 }
 return found;
 **/
