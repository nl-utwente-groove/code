package groove.graph.match.vf1;

import groove.graph.match.Matcher;
import groove.graph.*;

import java.util.*;

/**
 * @author iGniSz
 */
public class VFMatcher implements Matcher {

    /**
     * The underlying morphism of this matcher *
     */
    private Morphism morphism = null;

    /**
     * indicates if a matching has been found *
     */
    private boolean found = false;
    private NodeEdgeMap singularMap = null;

    public VFMatcher(Morphism morphism) {
        this.morphism = morphism;

        s = new VFState(morphism.dom(), morphism.cod());

        System.out.println(morphism.dom());
        System.out.println(morphism.cod());
    }

    private boolean searching = true;

    // algorithm information
    private VFState s = null;    
    //private Set<Set<VF1Pair<Node, Node>>> results = new HashSet<Set<VF1Pair<Node, Node>>>();
    private Set<Set<VF1Pair<Node, Node>>> results = new HashSet<Set<VF1Pair<Node, Node>>>();
    private int index = -1;

    protected boolean find() {
        if( searching ) {
            match();
            searching = false;
        }
        // pop off results 1-by-1 and add to the singular map
        // make an iterator if necessary
        computeMapping();
        return it.hasNext();
    }

    Iterator<Set<VF1Pair<Node, Node>>> it;

    private void computeMapping() {
        if( it == null ) {
            it = results.iterator();
        } else {
            if( it.hasNext() ) {
              Set<VF1Pair<Node, Node>> next = it.next();
              //next = it.next();                
              System.out.println(next);
            }
        }
    }

    private boolean match() {
        // if M(s) covers all the nodes
        if (s.covers(dom()) ) {
            //System.out.println(s);
            Set<VF1Pair<Node, Node>> sol = s.solution();
            for( Set<VF1Pair<Node, Node>> r : results ) {
                if( r.toString().equals(sol.toString()) )
                    return true;
            }
            results.add(sol);
        } else {
            // compute set (P(s)) for inclusion (potential)
            Set<VF1Pair<Node,Node>> P = s.feasible();

            // foreach p in P(s)
            for( VF1Pair<Node,Node> p : P ) {
                // try tentatively if feasibility rules succeed
                s.add(p);

                if( s.valid(p) ) {
                    // recurse
                    match();
                }

                // else undo tentative addition
                s.remove(p);
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
                        next = VFMatcher.this.getSingularMap();
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
