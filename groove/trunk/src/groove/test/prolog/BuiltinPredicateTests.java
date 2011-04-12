/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.test.prolog;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import groove.explore.Exploration;
import groove.lts.GTS;
import groove.prolog.GrooveState;
import groove.view.GrammarView;

import org.junit.Test;

/**
 * Tests the graph predicates
 * @author Lesley Wevers
 */
public class BuiltinPredicateTests {
    /**
     * Runs all tests in test-graph.pro
     */
    @Test
    public void testGraph() {
        // Assert that we can see all graph names
        succ("graph_name('a')");
        succ("graph_name('b')");

        // Assert that graph 'b' is the start graph
        fail("graph('a',G), start_graph(G)");
        succ("graph('b',G), start_graph(G)");
        fail("start_graph_name('a')");
        succ("start_graph_name('b')");

        // Assert that the start graph is a graph
        succ("start_graph(G), is_graph(G)");

        // Assert that there are four nodes
        succ("start_graph(G), graph_node_count(G,N), =(N,4)");
        succ("start_graph(G), graph_node_set(G,S), length(S,4)");

        // Assert that some node is a node
        succ("start_graph(G), graph_node(G,N), is_node(N)");
        fail("start_graph(G), is_node(G)");
        fail("start_graph(G), graph_edge(G,E), is_node(E)");

        // Assert that there are eight edges (four node type, three binary, one flag)
        succ("start_graph(G), graph_edge_count(G,N), =(N,8)");
        succ("start_graph(G), graph_edge_set(G,S), length(S,8)");

        // Assert that some edge is an edge
        succ("start_graph(G), graph_edge(G,E), is_edge(E)");
        fail("start_graph(G), is_edge(G)");
        fail("start_graph(G), graph_node(G,N), is_edge(N)");

        // Assert that there are edges labeled 'a', 'b', 'A', 'B' with the appropriate node type
        succ("start_graph(G), graph_edge(G,E), edge_label(E,'a'), edge_role_binary(E)");
        succ("start_graph(G), graph_edge(G,E), edge_label(E,'b'), edge_role_binary(E)");
        succ("start_graph(G), graph_edge(G,E), edge_label(E,'A'), edge_role_node_type(E)");
        succ("start_graph(G), graph_edge(G,E), edge_label(E,'B'), edge_role_node_type(E)");
        succ("start_graph(G), graph_edge(G,E), edge_label(E,'a'), edge_role_flag(E)");

        // Assert that the edge source and edge target of a node type edge are the same
        succ("start_graph(G), graph_edge(G,E), edge_label(E,'A'), edge_source(E,S), edge_target(E,S)");

        // Assert that the edge source and edge target of some binary edge are not the same
        // succ("start_graph(G), graph_edge(G,E), edge_label(E,'a'), edge_source(E,S), edge_target(E,T), =\\=(S,T)");

        // Test label_edge
        succ("start_graph(G), label('a',AL), label_edge(G,AL,E), edge_label(E,'a'), edge_role_binary(E)");
        succ("start_graph(G), label('flag:a',AL), label_edge(G,AL,E), edge_label(E,'a'), edge_role_flag(E)");
        succ("start_graph(G), label('type:A',AL), label_edge(G,AL,E), edge_label(E,'A'), edge_role_node_type(E)");

        // Test label_edge_set
        succ("start_graph(G), label('a', AL), label_edge_set(G,AL,E), length(E,1)");
        succ("start_graph(G), label('flag:a', AL), label_edge_set(G,AL,E), length(E,1)");
        succ("start_graph(G), label('b', BL), label_edge_set(G,BL,E), length(E,2)");
        succ("start_graph(G), label('type:A', AL), label_edge_set(G,AL,E), length(E,2)");

        // Test node_edge
        succ("start_graph(G), graph_node(G,N), node_edge(G,N,E), edge_label(E,'a'), edge_role_binary(E)");
        succ("start_graph(G), graph_node(G,N), node_edge(G,N,E), edge_label(E,'a'), edge_role_flag(E)");
        succ("start_graph(G), graph_node(G,N), node_edge(G,N,E), edge_label(E,'A'), edge_role_node_type(E)");

        // Test node_edge_set, there should be nodes with 2, 3 and 4 edges
        fail("start_graph(G), graph_node(G,N), node_edge_set(G,N,E), length(E,1)");
        succ("start_graph(G), graph_node(G,N), node_edge_set(G,N,E), length(E,2)");
        succ("start_graph(G), graph_node(G,N), node_edge_set(G,N,E), length(E,3)");
        succ("start_graph(G), graph_node(G,N), node_edge_set(G,N,E), length(E,4)");
        fail("start_graph(G), graph_node(G,N), node_edge_set(G,N,E), length(E,5)");

        // Assert that there should be nodes numbered 0 through 3
        succ("start_graph(G), graph_node(G,N), node_number(N,0)");
        succ("start_graph(G), graph_node(G,N), node_number(N,1)");
        succ("start_graph(G), graph_node(G,N), node_number(N,2)");
        succ("start_graph(G), graph_node(G,N), node_number(N,3)");
        fail("start_graph(G), graph_node(G,N), node_number(N,4)");

        // Test node_out_edge and node_out_edge_set
        succ("start_graph(G), graph_node(G,N), node_out_edge(G,N,E), edge_source(E,N)");
        fail("start_graph(G), graph_node(G,N), node_out_edge_set(G,N,E), length(E,0)");
        succ("start_graph(G), graph_node(G,N), node_out_edge_set(G,N,E), length(E,1)");
        succ("start_graph(G), graph_node(G,N), node_out_edge_set(G,N,E), length(E,3)");
        fail("start_graph(G), graph_node(G,N), node_out_edge_set(G,N,E), length(E,4)");

        // Test node_self_edges
        succ("start_graph(G), graph_node(G,N), node_self_edges(G,N,E), length(E,1)");
        succ("start_graph(G), graph_node(G,N), node_self_edges(G,N,E), length(E,2)");
        fail("start_graph(G), graph_node(G,N), node_self_edges(G,N,E), length(E,3)");

        // TODO: test for node_self_edges_excl
    }

    /**
     * Test the rule predicates
     */
    @Test
    public void testRule() {
        // TODO
    }

    /**
     * Test the LTS predicates
     */
    @Test
    public void testLts() {
        // Assert that gts gives a gts
        succ("gts(S), is_gts(S)");

        // Assert that the active state is a graph state
        succ("active_state(S), is_state(S)");

        // Assert that the start state is a state
        succ("start_state(S), is_state(S)");

        // Assert that the final state is a graph state
        succ("final_state(S), is_state(S)");
        succ("final_state_set(S), length(S,1)");

        // Assert that the final state is not the initial state
        fail("start_state(S), final_state(S)");
        fail("active_state(S), final_state(S)");
        succ("start_state(S), active_state(S)");

        // Assert that state gives all states
        succ("state(S), start_state(S)");
        succ("state(S), final_state(S)");

        // Assert that state_graph gives the start graph for the start state
        succ("start_state(S), state_graph(S,G), is_graph(G)");
        // succ("start_state(S), state_graph(S,G), start_graph(G)");

        // Assert that the start state is closed
        succ("start_state(S), state_is_closed(S)");

        // Assert that the next state of the start state is the final state
        succ("start_state(S), state_next(S,N), final_state(N)");
        fail("start_state(S), state_next(S,S)");
        succ("start_state(S), state_next_set(S,N), length(N,1), member(F,N), final_state(F)");

        // Assert that the start state has a transition
        succ("start_state(S), state_transition(S,T), is_transition(T)");
        succ("start_state(S), state_transition_set(S,T), length(T,1), member(X,T), state_transition(S,X)");

        // Assert that the source and target of a transition are right
        succ("start_state(S), state_transition(S,T), transition_source(T,S), transition_target(T,F), final_state(F)");

        // Assert that transition_event gives a rule event
        succ("start_state(S), state_transition(S,T), transition_event(T,E), is_ruleevent(E)");

        // Test gts match
        succ("gts(G), start_state(S), gts_match(G,S,R), is_ruleevent(R)");

    }

    /**
     * Test the trans predicates
     */
    @Test
    public void testTrans() {
        // TODO
    }

    /**
     * Test the type predicates
     */
    @Test
    public void testType() {
        // Test composite_type_graph
        succ("composite_type_graph(G), is_graph(G)");

        // Test type_graph_name, active_type_graph_name, type_graph
        succ("type_graph_name(TGN), type_graph(TG), is_graph(TG)");
        succ("active_type_graph_name(TGN), type_graph(TG), is_graph(TG)");
        succ("type_graph_name('type')");
        succ("type_graph_name('test')");
        succ("active_type_graph_name('type')");
        fail("active_type_graph_name('test')");

        // Test type_label, subtype, direct_subtype
        succ("composite_type_graph(G), label('type:A',A), label('type:C',C), subtype(G,C,A), direct_subtype(G,C,A)");
        succ("composite_type_graph(G), label('type:A',A), label('type:D',D), subtype(G,D,A)");
        fail("composite_type_graph(G), label('type:A',A), label('type:D',D), direct_subtype(G,D,A)");

        // Test type_label
        succ("label('a',L), label(X,L), =(X,'a')");
        succ("label('type:A',A), label(X,A), =(X,'type:A')");
    }

    private void succ(String predicate) {
        assertTrue(test(predicate));
    }

    private void fail(String predicate) {
        assertFalse(test(predicate));
    }

    private boolean test(String predicate) {
        try {
            GrammarView gv = PrologTestUtil.testGrammar("b");
            GTS gts = new GTS(gv.toGrammar());
            Exploration exploration = new Exploration();

            exploration.play(gts, null);

            return PrologTestUtil.test(
                new GrooveState(gv, gts, gts.startState(), null), predicate);
        } catch (Exception e) {
            e.printStackTrace();
            org.junit.Assert.fail(e.getMessage());
            return false;
        }
    }
}
