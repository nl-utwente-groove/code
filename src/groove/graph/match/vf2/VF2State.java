package groove.graph.match.vf2;

import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.Edge;
import org.omg.CORBA.ContextList;

import java.util.Collection;

/**
 * @author iGniSz
 */
public class VF2State {
    public Graph G1;
    public Graph G2;

    private int core_1_len;
    private int core_2_len;

    private int n1;
    private int n2;

    private int tboth_1_len;
    private int tboth_2_len;

    private int tout_1_len;
    private int tout_2_len;

    private int tin_1_len;
    private int tin_2_len;

    private int prev_n1 = NULL_NODE;
    private int prev_n2 = NULL_NODE;

    private int orig_core_len;
    private int core_len;

    private int[] core_1;
    private int[] core_2;

    private long share_count;

    private int[] out_1;
    private int[] out_2;

    private int[] in_1;
    private int[] in_2;

    private static final int NULL_NODE = 0xFFFF;

    private int[] order = new int[0];

    private int added_node1;

    private Node[] nodes_1;
    private Node[] nodes_2;    

    public VF2State(Graph G1, Graph G2) {
        this.G1 = G1;
        nodes_1 = new Node[0]; 
        nodes_1 = G1.nodeSet().toArray(nodes_1);

        this.G2 = G2;
        nodes_2 = new Node[0];
        nodes_2 = G2.nodeSet().toArray(nodes_2);

        n1 = G1.nodeCount();
        n2 = G2.nodeCount();

        order = null;

        core_len = orig_core_len = 0;
        tboth_1_len = tin_1_len = tout_1_len = 0;
        tboth_2_len = tin_2_len = tout_2_len = 0;

    	added_node1 = NULL_NODE;

        core_1 = new int[n1];
        core_2 = new int[n2];

        in_1 = new int[n1];
        in_2 = new int[n2];

        out_1 = new int[n1];
        out_2 = new int[n2];

        for(int i = 0; i < n1; i++) {
            core_1[i] = NULL_NODE;
            in_1[i]   = 0;
            out_1[i]  = 0;
        }
        for(int i = 0; i < n2; i++) {
            core_2[i] = NULL_NODE;
            in_2[i]   = 0;
            out_2[i]  = 0;
        }

	    share_count = 1;
    }

    public VF2State(VF2State state) {
        G1 = state.G1;
        G2 = state.G2;

        nodes_1 = state.nodes_1;
        nodes_2 = state.nodes_2; 

        n1 = state.n1;
        n2 = state.n2;

        order = state.order;

        core_len = orig_core_len = state.core_len;
        tin_1_len = state.tin_1_len;
        tout_1_len = state.tout_1_len;
        tboth_1_len = state.tboth_1_len;
        tin_2_len = state.tin_2_len;
        tout_2_len = state.tout_2_len;
        tboth_2_len = state.tboth_2_len;

	    added_node1 = NULL_NODE;

        core_1 = state.core_1;
        core_2 = state.core_2;
        in_1 = state.in_1;
        in_2 = state.in_2;
        out_1 = state.out_1;
        out_2 = state.out_2;
        share_count = state.share_count;

	    ++share_count;
    }

    public void solution() {
        //To change body of created methods use File | Settings | File Templates.
    }

    public boolean isDead() {        
        return (n1 > n2) || (tboth_1_len > tboth_2_len) || (tout_1_len > tout_2_len) || ( tin_1_len > tin_1_len);  
        //(n1 != n2) && (tboth_1_len != tboth_2_len) || (tout_1_len != tout_2_len) || ( tin_1_len != tin_1_len);
    }

    public boolean isSolution() {
        return ((core_1_len == n1) && (core_2_len == n2));
    }

    public Pair nextPair() {
        if (prev_n1 == NULL_NODE)
          prev_n1 = 0;
        if (prev_n2 == NULL_NODE)
          prev_n2 = 0;
        else
          prev_n2++;
        
        if( (tboth_1_len > core_len) && (tboth_2_len>core_len) ) {
            while (prev_n1<n1 && ( core_1[prev_n1] != NULL_NODE || out_1[prev_n1] == 0 || in_1[prev_n1] == 0) ) {
                prev_n1++;
                prev_n2=0;
              }
        } else if ((tout_1_len > core_len) && (tout_2_len>core_len) ) {
            while( (prev_n1 < n1) && ( (core_1[prev_n1] != NULL_NODE) || (out_1[prev_n1] == 0) ) ) {
                prev_n1++;
                prev_n2=0;
              }
        } else if ( (tin_1_len > core_len) && (tin_2_len>core_len) ) {
            while (prev_n1<n1 && ( (core_1[prev_n1] != NULL_NODE) || (in_1[prev_n1] == 0)) ) {
                prev_n1++;
                prev_n2=0;
            }
        } else if ( (prev_n1 == 0) && (order != null) ) {
            int i=0;
            prev_n1 = order[i];
            while( (i<n1) && (core_1[prev_n1] != NULL_NODE)) {
                i++;
                prev_n1 = order[i];
            }
            if (i == n1)
              prev_n1 = n1;
        } else {
            while( (prev_n1 < n1) && (core_1[prev_n1] != NULL_NODE) ) {
                prev_n1++;
                prev_n2= 0;
            }
        }
        if( (tboth_1_len > core_len) && (tboth_2_len > core_len) ) {
            while( (prev_n2<n2) && ( (core_2[prev_n2] != NULL_NODE) || (out_2[prev_n2] == 0) || (in_2[prev_n2] == 0 ) ) )
                prev_n2++;
        } else if( (tout_1_len > core_len) && (tout_2_len > core_len) ) {
            while( (prev_n2 < n2) && ((core_2[prev_n2] != NULL_NODE) || (out_2[prev_n2] == 0)  ) )
                prev_n2++;
        } else if( (tin_1_len > core_len) && (tin_2_len > core_len) ) {
            while( (prev_n2<n2) && ( (core_2[prev_n2] != NULL_NODE) || (in_2[prev_n2]==0) ) )
                prev_n2++;
        } else {
            while( (prev_n2 < n2) && (core_2[prev_n2] != NULL_NODE) )
                prev_n2++;
        }


        if( (prev_n1 < n1) && (prev_n2 < n2) ) {
            return new Pair(prev_n1, prev_n2);
        }

        return null;
    }

    public boolean isFeasible(Pair p) {
        int node1 = p.left;
        int node2 = p.right;

        if (!CompatibleNode(node1, node2))
            return false;

        int i, other1, other2;
        int termout1=0, termout2=0, termin1=0, termin2=0, new1=0, new2=0;
        
        // Check the 'out' edges of node1
        for( i = 0; i < OutEdgeCount_1(node1); i++) {
            other1 = GetOutEdge_1(node1, i);
            if( core_1[other1] != NULL_NODE ) {
                other2 = core_1[other1];
                if( !HasEdge_2(node2, other2) || !CompatibleEdge(node1, node2) )
                    return false;
            } else {
                if( in_1[other1] != 0)
                    termin1++;
                if( out_1[other1] != 0 )
                    termout1++;
                if( (in_1[other1] == 0) && (out_1[other1] == 0) )
                    new1++;
            }
        }

        // Check the 'in' edges of node1
        for( i = 0; i < InEdgeCount_1(node1); i++) {
            other1 = GetInEdge_1(node1, i);
            if( core_1[other1] != NULL_NODE) {
                other2 = core_1[other1];
                if( HasEdge_2(other2, node2) || !CompatibleEdge(node1, node2))
                    return false;
            } else {
                if( in_1[other1] != 0)
                    termin1++;
                if (out_1[other1] != 0)
                    termout1++;
                if ( (in_1[other1] == 0) && (out_1[other1] == 0) )
                    new1++;
            }
        }

        // Check the 'out' edges of node2
        for( i = 0; i < OutEdgeCount_2(node2); i++) {
            other2 = GetOutEdge_2(node2, i);
            if( core_2[other2] != NULL_NODE ) {
                other1 = core_2[other2];
                if( HasEdge_1(node1, other1))
                    return false;
            } else {
                if(in_2[other2] != 0)
                    termin2++;
                if(out_2[other2] != 0)
                    termout2++;
                if( (in_2[other2] == 0) && (out_2[other2] == 0) )
                    new2++;
            }
        }

        // Check the 'in' edges of node2
        for( i = 0; i < InEdgeCount_2(node2); i++) {
            other2 = GetInEdge_2(node2, i);
            if (core_2[other2] != NULL_NODE) {
                other1 = core_2[other2];
                if(HasEdge_1(other1, node1))
                    return false;
            } else {
                if(in_2[other2] != 0)
                    termin2++;
                if(out_2[other2] != 0)
                    termout2++;
                if( (in_2[other2] == 0) && (out_2[other2] == 0) )
                    new2++;
            }
        }

        return (termin1 <= termin2) && (termout1 <= termout2) && (new1 <= new2);
    }

    private int InEdgeCount_2(int node2) {
        Collection<? extends Edge> edges = G1.edgeSet(nodes_2[node2]);
        int count = 0;
        for (Edge e : edges) {
            if (e.opposite() == nodes_2[node2])
                count++;
        }
        return count;
    }

    private int GetInEdge_2(int node2, int i) {
        Edge[] edges = new Edge[0];
        edges = G2.edgeSet(nodes_2[node2]).toArray(edges);

        for( int j = 0; j < nodes_2.length; j++ ) {
            if( nodes_2[j] == edges[i].source() )
                return j;
        }
        return -1;
    }

    private boolean HasEdge_1(int node1, int other1) {
        Node n = nodes_2[node1];
        Node o = nodes_2[other1];

        for( Edge e : G1.edgeSet(n) ) {
            if( e.opposite() == o )
                return true;
        }

        return false;
    }

    private int GetOutEdge_2(int node2, int i) {
        Edge[] edges = new Edge[0];
        edges = G2.edgeSet(nodes_2[node2]).toArray(edges);

        for( int j = 0; j < nodes_2.length; j++ ) {
            if( nodes_2[j] == edges[i].source() )
                return j;
        }
        return -1;
    }

    private int OutEdgeCount_2(int node2) {
        Collection<? extends Edge> edges = G2.edgeSet(nodes_2[node2]);
        int count = 0;
        for (Edge e : edges) {
            if (e.source() == nodes_2[node2])
                count++;
        }
        return count;
    }

    private int GetInEdge_1(int node1, int i) {
        Edge[] edges = new Edge[0];
        edges = G1.edgeSet(nodes_1[node1]).toArray(edges);

        for( int j = 0; j < nodes_1.length; j++ ) {
            if( nodes_1[j] == edges[i].source() )
                return j;
        }
        return -1;
    }

    private int InEdgeCount_1(int node1) {
        Collection<? extends Edge> edges = G1.edgeSet(nodes_1[node1]);
        int count = 0;
        for (Edge e : edges) {
            if (e.opposite() == nodes_1[node1])
                count++;
        }
        return count;
    }

    private boolean CompatibleEdge(int node1, int node2) {
        return true;
    }

    // check for node compatibility
    private boolean CompatibleNode(int node1, int node2) {
        return true;
    }

    // is there a path between node2 and other2 in G2
    private boolean HasEdge_2(int node2, int other2) {
        Node n = nodes_2[node2];
        Node o = nodes_2[other2];

        for( Edge e : G2.edgeSet(n) ) {
            if( e.opposite() == o )
                return true;
        }
        
        return false;
    }

    private int GetOutEdge_1(int node1, int i) {
        Edge[] edges = new Edge[G1.edgeSet(nodes_1[node1]).size()];
        edges = G1.edgeSet(nodes_1[node1]).toArray(edges);

        for( int j = 0; j < nodes_1.length; j++ ) {
            if( nodes_1[j] == edges[i].opposite() )
                return j;
        }
        return -1;

    }

    private int OutEdgeCount_1(int node1) {
        Collection<? extends Edge> edges = G1.edgeSet(nodes_1[node1]);
        int count = 0;
        for (Edge e : edges) {
            if (e.source() == nodes_1[node1])
                count++;
        }
        return count;
    }

    public VF2State copy() {
        return new VF2State(this);
    }

    public void add(Pair p) {
        int node1 = p.left;
        int node2 = p.right;

        core_len++;
        added_node1 = node1;

        if( in_1[node1] == 0 ) {
            in_1[node1] = core_len;
            tin_1_len++;
            if(out_1[node1] > 0)
                tboth_1_len++;
        }
        if( out_1[node1] == 0 ) {
            out_1[node1] = core_len;
            tout_1_len++;
            if(in_1[node1] > 0)
                tboth_1_len++;
        }
        if( in_2[node2] == 0 ) {
            in_2[node2] = core_len;
            tin_2_len++;
            if(out_2[node2] > 0)
                tboth_2_len++;
        }
        if( out_2[node2] > 0 ) {
            out_2[node2] = core_len;
            tout_2_len++;
            if(in_2[node2] == 0)
              tboth_2_len++;
        }

        core_1[node1] = node2;
        core_2[node2] = node1;

        int i, other;
        for(i=0; i < InEdgeCount_1(node1); i++) {
            other = GetInEdge_1(node1, i);
            if( in_1[other] > 0 ) {
                in_1[other] = core_len;
                tin_1_len++;
                if(out_1[other] == 0)
                  tboth_1_len++;
            }
        }

        for( i = 0; i < OutEdgeCount_1(node1); i++) {
            other = GetOutEdge_1(node1, i);
            if(out_1[other] > 0) {
                out_1[other]=core_len;
                tout_1_len++;
                if(in_1[other] == 0)
                    tboth_1_len++;
            }
        }

        for( i = 0; i < InEdgeCount_2(node2); i++) {
            other = GetInEdge_2(node2, i);
            if(in_2[other] > 0) {
                in_2[other]=core_len;
                tin_2_len++;
                if(out_2[other] == 0)
                    tboth_2_len++;
            }
        }

        for( i = 0; i < OutEdgeCount_2(node2); i++) {
            other = GetOutEdge_2(node2, i);
            if(out_2[other] > 0) {
                out_2[other] = core_len;
                tout_2_len++;
                if(in_2[other] == 0)
                    tboth_2_len++;
            }
        }
    }

    public void backtrack() {
        if (orig_core_len < core_len) {
            int i, node2;

            if (in_1[added_node1] == core_len)
		        in_1[added_node1] = 0;

            for( i = 0; i < InEdgeCount_1(added_node1); i++) {
                int other = GetInEdge_1(added_node1, i);
		        if (in_1[other]==core_len)
			        in_1[other]=0;
		    }

            if (out_1[added_node1] == core_len)
		        out_1[added_node1] = 0;
	        for( i = 0; i < OutEdgeCount_1(added_node1); i++) {
                int other = GetOutEdge_1(added_node1, i);
		        if (out_1[other]==core_len)
			        out_1[other]=0;
		    }

            node2 = core_1[added_node1];

            if (in_2[node2] == core_len)
		        in_2[node2] = 0;

            for( i = 0; i < InEdgeCount_2(node2); i++) {
                int other = GetInEdge_2(node2, i);
		        if (in_2[other]==core_len)
			        in_2[other]=0;
		    }

            if (out_2[node2] == core_len)
		        out_2[node2] = 0;

            for( i = 0; i < OutEdgeCount_2( node2); i++) {
                int other = GetOutEdge_2(node2, i);
		        if (out_2[other]==core_len)
			        out_2[other]=0;
		    }

            core_1[added_node1] = NULL_NODE;
            core_2[node2] = NULL_NODE;

            core_len=orig_core_len;
            added_node1 = NULL_NODE;
        }
    }
}
