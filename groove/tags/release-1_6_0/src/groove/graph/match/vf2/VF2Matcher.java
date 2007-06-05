package groove.graph.match.vf2;

import groove.graph.Graph;
import groove.graph.Morphism;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.graph.match.Matcher;

import java.util.*;

/**
 * @author J. ter Hove
 */
public class VF2Matcher implements Matcher {

    /**
     * The underlying morphism of this matcher *
     */
    private Morphism morphism = null;

    /**
     * indicates if a matching has been found *
     */
    private boolean found = false;
    private NodeEdgeMap singularMap = null;

    public VF2Matcher(Morphism morphism) {
        this.morphism = morphism;

        System.out.println(morphism.dom());
        System.out.println(morphism.cod());

        s = new VF2State(morphism.dom(), morphism.cod());
    }

    private VF2State s;

    protected boolean find() {
        return match(s);
    }    

    public boolean match(VF2State s) {
        if( s.isSolution() ) {
            System.out.println("solution");
        } else {
            if(s.isDead())
                return false;

            Pair p = s.nextPair();
            while( p != null ) {
                System.out.println(p);
                if( s.isFeasible(p) ) {
                    VF2State ss = s.copy();
                    ss.add(p);

                    if( match(ss) ) {
                        ss.backtrack();
                        return true;
                    } else {
                        ss.backtrack();
                    }
                }
                
                p = s.nextPair();
            }
        }
        return false;
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
                        next = VF2Matcher.this.getSingularMap();
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
