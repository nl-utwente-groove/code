package groove.graph.match.vf1;

import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.Edge;
import groove.graph.DefaultNode;

import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.ArrayList;

/**
 * @author iGniSz
 */
public class VFState {
    Graph G1;
    Graph G2;


    public VFState(Graph g1, Graph g2) {
        G1 = g1;
        G2 = g2;
    }

    Set<VF1Pair<Node, Node>> mapping = new HashSet<VF1Pair<Node, Node>>();

    public boolean covers(Graph graph) {
        return (graph.nodeCount() == mapping.size());
    }

    public Set<VF1Pair<Node, Node>> feasible() {
        Set<VF1Pair<Node, Node>> result = new HashSet<VF1Pair<Node, Node>>();

        // construct T(out)
        Set<Node> To1 = T_out(G1, l(mapping));
        Set<Node> To2 = T_out(G2, r(mapping));
        if (!To1.isEmpty() && !To2.isEmpty()) {
            //Node m = min(To2);
            for (Node n : To1) {
                for (Node m : To2) {
                    result.add(new VF1Pair(n, m));
                }
                //result.add(new VF1Pair(n, m));
            }
            return result;
        }

        // construct T(in)
        Set<Node> Ti1 = T_in(G1, l(mapping));
        Set<Node> Ti2 = T_in(G2, r(mapping));
        if (!Ti1.isEmpty() && !Ti1.isEmpty()) {
            // make P(s) out of T_in_1 & T_in_2
            //Node m = min(Ti2);
            for (Node n : Ti1) {
                for (Node m : Ti2) {
                    result.add(new VF1Pair(n, m));
                }
                //result.add(new VF1Pair(n, m));
            }

            return result;
        }

        // construct N1 - M1(s)
        // construct N2 - M2(s)
        Collection<? extends Node> N1 = new ArrayList(G1.nodeSet());
        Collection<? extends Node> N2 = new ArrayList(G2.nodeSet());

        for (VF1Pair<Node, Node> p : mapping) {
            N1.remove(p.left);
            N2.remove(p.right);
        }

        //Node m = min(N2);
        for (Node n : N1) {
            for (Node m : N2) {
                result.add(new VF1Pair(n, m));
            }
            //result.add(new VF1Pair(n, m));

        }
        return result;
    }

    private Node min(Collection<? extends Node> set) {
        int min = 9999999;
        Node result = null;

        for( Node m : set ) {
            DefaultNode dn = (DefaultNode)m;
            if(dn.getNumber() < min) {
                min = dn.getNumber();
                result = m;
            }
        }
        
        return result;
    }

    private Set<Node> r(Collection<VF1Pair<Node, Node>> mapping) {
        Set<Node> result = new HashSet<Node>();

        for( VF1Pair<Node, Node> p : mapping ) {
            result.add(p.right);
        }

        return result;
    }

    private Set<Node> l(Collection<VF1Pair<Node, Node>> mapping) {
        Set<Node> result = new HashSet<Node>();

        for( VF1Pair<Node, Node> p : mapping ) {
            result.add(p.left);
        }

        return result;
    }

    private Set<Node> T_in(Graph g, Collection<Node> mapping) {
        Set<Node> map = new HashSet<Node>();

        for (Node p : mapping) {
            Collection<? extends Edge> edges = g.edgeSet(p);
            for (Edge e : edges) {
                if( e.opposite() == p ) {
                    // e is incoming
                    map.add(e.source());
                }
            }
        }

        map.removeAll(mapping);
        return map;
    }

    private Set<Node> T_out(Graph g, Collection<Node> mapping) {
        Set<Node> map = new HashSet<Node>();

        for (Node p : mapping) {
            Collection<? extends Edge> edges = g.outEdgeSet(p);
            for (Edge e : edges) {
                map.add(e.opposite());
            }
        }
        map.removeAll(mapping);
        return map;
    }

    public void remove(VF1Pair<Node, Node> p) {
        mapping.remove(p);
    }

    public void add(VF1Pair<Node, Node> p) {
        mapping.add(p);
    }

    public boolean valid(VF1Pair<Node, Node> p) {
        // lookahead 0
        //  R_pred
        Collection<VF1Pair<Node, Node>> mp = new ArrayList<VF1Pair<Node, Node>>();
        mp = mappedPredecessors(G1, mp, p.left);
        for( VF1Pair<Node, Node> q : mp ) {
            if( !isPredecessor(G2, q.right, p.right) )
                return false;
        }

        //  R_succ
        Collection<VF1Pair<Node, Node>> ms = new ArrayList<VF1Pair<Node, Node>>();
        ms = mappedSuccessors(G1, ms, p.left);
        for( VF1Pair<Node, Node> q : ms ) {
            if( !isSuccessor(G2, q.right, p.right) )
                return false;
        }

        // lookahead 1
        //  R_termin
        Collection<Node> ips1 = new ArrayList<Node>();
        ips1 = predecessors(G1, ips1, p.left);
        
        Set<Node> Ti1 = T_in(G1, l(mapping));
        int ips1c = 0;
        for( Node n : Ti1 ) {
            if( ips1.contains(n) )
                ips1c++;
        }
        Collection<Node> ips2 = new ArrayList<Node>();
        ips2 = predecessors(G2, ips2, p.right);
        Set<Node> Ti2 = T_in(G2, r(mapping));
        int ips2c = 0;
        for( Node n : Ti2 ) {
            if( ips2.contains(n) )
                ips2c++;
        }
        if( ips1c > ips2c )
            return false;

        //  R_termout
        Collection<Node> ops1 = new ArrayList<Node>();
        ops1 = predecessors(G1, ops1, p.left);
        Set<Node> To1 = T_out(G1, l(mapping));
        int ops1c = 0;
        for( Node n : To1 ) {
            if( ops1.contains(n) )
                ops1c++;
        }
        Collection<Node> ops2 = new ArrayList<Node>();
        ops2 = predecessors(G2, ops2, p.right);
        Set<Node> To2 = T_out(G2, r(mapping));
        int ops2c = 0;
        for( Node n : To2 ) {
            if( ops2.contains(n) )
                ops2c++;
        }
        if( ops1c > ops2c )
            return false;

        // lookahead 2
        Set<Node> T1 = T_in(G1, l(mapping));
        T1.addAll(T_out(G1, l(mapping)));

        Collection<Node> ps1 = new ArrayList<Node>();
        ps1 = predecessors(G1, ps1, p.left);
        int np1 = 0;
        for( Node pred : ps1 ) {
            if( !inMapping(pred) && !T1.contains(pred) )
                np1++;
        }

        Set<Node> T2 = T_in(G2, r(mapping));
        T2.addAll(T_out(G2, r(mapping)));

        Collection<Node> ps2 = new ArrayList<Node>();
        ps2 = predecessors(G2, ps2, p.left);
        int np2 = 0;
        for( Node pred : ps1 ) {
            if( !inMapping(pred) && !T2.contains(pred) )
                np2++;
        }
        if( np1 > np2 )
            return false;

        return true;
    }

    private Collection<Node> predecessors(Graph g, Collection<Node> ps, Node n) {
        for(Node m : g.nodeSet() ) {
            if( isPredecessor(g, m, n) )
                ps.add(m);
        }
        return ps;
    }

    private boolean isSuccessor(Graph g, Node successor, Node predecessor) {
        return isPredecessor(g, predecessor, successor);
    }

    private Collection<VF1Pair<Node, Node>> mappedSuccessors(Graph g, Collection<VF1Pair<Node, Node>> mss, Node n) {
        Collection<? extends Edge> edges = g.outEdgeSet(n);
        for( Edge e : edges ) {
            // outgoing edge
            if( inMapping(e.opposite()) ) {
                // a mapped successor
                mss.add(take(e.opposite()));
                // find successors for the currently found successor
                mappedSuccessors(g, mss, e.opposite());
            }
        }
        // all predecessors have been found
        return mss;
    }

    private boolean isPredecessor(Graph g, Node predecessor, Node successor) {
        Collection<? extends Edge> successors = g.outEdgeSet(predecessor);
        for( Edge e : successors ) {
            if( e.opposite() == successor )
                return true;
            else if( isPredecessor(g, e.opposite(), successor) )
                return true;
        }

        return false;
    }

    private Collection<VF1Pair<Node, Node>> mappedPredecessors(Graph g, Collection<VF1Pair<Node, Node>> mps, Node n) {
        Collection<? extends Edge> edges = g.edgeSet(n);
        for( Edge e : edges ) {
            if( e.opposite() == n ) {
                // incoming edge
                if( inMapping(e.source()) ) {
                    // a mapped predecessor
                    mps.add(take(e.source()));
                    // find predecessors for the currently found predecessor
                    mappedPredecessors(g, mps, e.source());
                }
            }
        }
        // all predecessors have been found
        return mps;
    }

    private boolean inMapping(Node node) {
        for( VF1Pair<Node, Node> p : mapping ) {
            if( p.left == node )
                return true;
            if( p.right == node )
                return true;
        }
        return false;
    }

    private VF1Pair<Node, Node> take(Node n) {
        for( VF1Pair<Node, Node> p : mapping ) {
            if( p.left == n )
                return p;
            if( p.right == n )
                return p;
        }
        return null;
    }

    public String toString() {
        return mapping.toString();
    }

    public Set<VF1Pair<Node, Node>> solution() {
        Set<VF1Pair<Node, Node>> copy = new HashSet<VF1Pair<Node, Node>>();
        copy.addAll(mapping);
        return copy;
    }
}
