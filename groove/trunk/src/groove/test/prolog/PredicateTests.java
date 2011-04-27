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
import groove.trans.GraphGrammar;
import groove.view.FormatException;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the graph predicates
 * @author Lesley Wevers
 */
public class PredicateTests {
    private static GrooveState grooveState;

    /**
     * Initializes the test suite
     */
    @BeforeClass
    public static void initialize() throws FormatException {
        GraphGrammar grammar =
            PrologTestUtil.testGrammar("graph-a").toGrammar();
        GTS gts = new GTS(grammar);
        Exploration exploration = new Exploration();

        exploration.play(gts, null);

        grooveState =
            new GrooveState(grammar, gts, gts.startState(),
                gts.startState().getTransitionIter().next().getEvent());
    }

    /**
     * Runs all tests in test-graph.pro
     */
    @Test
    public void testGraph() {
        // Assert that the start graph is a graph
        succ("start_graph(G), is_graph(G)");

        // Assert that there are two nodes
        succ("start_graph(G), graph_node_count(G,N), =(N,3)");
        succ("start_graph(G), graph_node_set(G,S), length(S,3)");

        // Assert that some node is a node
        succ("start_graph(G), graph_node(G,N), is_node(N)");
        fail("start_graph(G), is_node(G)");
        fail("start_graph(G), graph_edge(G,E), is_node(E)");

        // Assert that there are four edges (two node type, two binary, one flag)
        succ("start_graph(G), graph_edge_count(G,N), =(N,5)");
        succ("start_graph(G), graph_edge_set(G,S), length(S,5)");

        // Assert that some edge is an edge
        succ("start_graph(G), graph_edge(G,E), is_edge(E)");
        fail("start_graph(G), is_edge(G)");
        fail("start_graph(G), graph_node(G,N), is_edge(N)");

        // Assert that there are edges labeled 'a', 'f', 'A', 'F' with the appropriate node type
        succ("start_graph(G), graph_edge(G,E), edge_label(E,'f'), edge_role_binary(E)");
        succ("start_graph(G), graph_edge(G,E), edge_label(E,'A'), edge_role_node_type(E)");
        succ("start_graph(G), graph_edge(G,E), edge_label(E,'F'), edge_role_node_type(E)");
        succ("start_graph(G), graph_edge(G,E), edge_label(E,'a'), edge_role_flag(E)");

        // Assert that the edge source and edge target of a node type edge are the same
        succ("start_graph(G), graph_edge(G,E), edge_label(E,'A'), edge_source(E,S), edge_target(E,S)");

        // Test label_edge
        succ("start_graph(G), label('f',AL), label_edge(G,AL,E), edge_label(E,'f'), edge_role_binary(E)");
        succ("start_graph(G), label('flag:a',AL), label_edge(G,AL,E), edge_label(E,'a'), edge_role_flag(E)");
        succ("start_graph(G), label('type:A',AL), label_edge(G,AL,E), edge_label(E,'A'), edge_role_node_type(E)");

        // Test label_edge_set
        succ("start_graph(G), label('f', AL), label_edge_set(G,AL,E), length(E,1)");
        succ("start_graph(G), label('flag:a', AL), label_edge_set(G,AL,E), length(E,1)");
        succ("start_graph(G), label('type:A', AL), label_edge_set(G,AL,E), length(E,1)");

        // Test node_edge
        succ("start_graph(G), graph_node(G,N), node_edge(G,N,E), edge_label(E,'f'), edge_role_binary(E)");
        succ("start_graph(G), graph_node(G,N), node_edge(G,N,E), edge_label(E,'a'), edge_role_flag(E)");
        succ("start_graph(G), graph_node(G,N), node_edge(G,N,E), edge_label(E,'A'), edge_role_node_type(E)");

        // Test node_edge_set, there should be nodes with 1, 2 and 4 edges
        succ("start_graph(G), graph_node(G,N), node_edge_set(G,N,E), length(E,1)");
        succ("start_graph(G), graph_node(G,N), node_edge_set(G,N,E), length(E,2)");
        fail("start_graph(G), graph_node(G,N), node_edge_set(G,N,E), length(E,3)");
        succ("start_graph(G), graph_node(G,N), node_edge_set(G,N,E), length(E,4)");

        // Assert that there should be nodes numbered 0 through 2
        succ("start_graph(G), graph_node(G,N), node_number(N,0)");
        succ("start_graph(G), graph_node(G,N), node_number(N,1)");
        succ("start_graph(G), graph_node(G,N), node_number(N,2)");
        fail("start_graph(G), graph_node(G,N), node_number(N,3)");

        // Test node_out_edge and node_out_edge_set
        succ("start_graph(G), graph_node(G,N), node_out_edge(G,N,E), edge_source(E,N)");
        succ("start_graph(G), graph_node(G,N), node_out_edge_set(G,N,E), length(E,0)");
        succ("start_graph(G), graph_node(G,N), node_out_edge_set(G,N,E), length(E,1)");
        fail("start_graph(G), graph_node(G,N), node_out_edge_set(G,N,E), length(E,3)");

        // Test node_self_edges
        succ("start_graph(G), graph_node(G,N), node_self_edges(G,N,E), length(E,1)");
        succ("start_graph(G), graph_node(G,N), node_self_edges(G,N,E), length(E,2)");
        fail("start_graph(G), graph_node(G,N), node_self_edges(G,N,E), length(E,3)");

        // Test node_self_edges_excl
        succ("start_graph(G), graph_node(G,N), node_self_edges_excl(G,N,E), length(E,1)");
        succ("start_graph(G), graph_node(G,N), node_self_edges_excl(G,N,E), length(E,2)");
        fail("start_graph(G), graph_node(G,N), node_self_edges_excl(G,N,E), length(E,3)");
    }

    /**
     * Test the derived graph predicates
     */
    @Test
    public void testGraphDerived() {
        // Test graph_binary, graph_flag, graph_node_type
        succ("start_graph(G), graph_binary(G,E), edge_role_binary(E)");
        succ("start_graph(G), graph_flag(G,E), edge_role_flag(E)");
        succ("start_graph(G), graph_node_type(G,E), edge_role_node_type(E)");

        // Test node_path
        succ("gts(G), start_state(S), final_state(F), node_path(G,S,F,[A,B,F])");

        // Test node_number
        succ("gts(G), node_number(G,X,0), is_node(X)");
        succ("gts(G), node_number(G,X,N), is_node(X)");
    }

    /**
     * Test the rule predicates
     */
    @Test
    public void testRule() {
        // Assert that all rule names can be retrieved
        succ("rule_name('rule-a')");
        succ("rule_name('rule-b')");
        fail("rule_name('rule-disabled')");

        // Assert that confluent rule names can be checked
        succ("rule_confluent('rule-confluent')");
        fail("rule_confluent('rule-a')");

        // Assert that rules can be retrieved
        succ("rule_name(N), rule(N,R), is_rule(R)");

        // Assert that the RHS and LHS of a rule are graphs
        succ("rule_name(N), rule(N,R), rule_lhs(R,G), is_graph(G)");
        succ("rule_name(N), rule(N,R), rule_rhs(R,G), is_graph(G)");

        // Assert that the name of the rules are 'rule-a' and 'rule-b'
        succ("rule_name(N), rule(N,R), rule('rule-a',R)");
        succ("rule_name(N), rule(N,R), rule('rule-b',R)");

        // Assert that rule_priority works
        succ("rule_name(N), rule(N,R), rule_priority(R,0)");
        succ("rule_name(N), rule(N,R), rule_priority(R,1)");
        fail("rule_name(N), rule(N,R), rule_priority(R,2)");
    }

    /**
     * Test the derived rule predicates
     */
    @Test
    public void testRuleDerived() {
        // Test confluent_rule_name
        succ("confluent_rule_name(R), rule_confluent(R)");

        // Test confluent_rule
        succ("confluent_rule(R), rule(N,R), rule_confluent(N)");
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
        succ("final_state_set(S), length(S,2)");

        // Assert that the final state is not the initial state
        fail("start_state(S), final_state(S)");
        fail("active_state(S), final_state(S)");
        succ("start_state(S), active_state(S)");

        // Assert that state gives all states
        succ("state(S), start_state(S)");
        succ("state(S), final_state(S)");

        // Assert that state_graph gives the start graph for the start state
        succ("start_state(S), state_graph(S,G), is_graph(G)");

        // Assert that the start state is closed
        succ("start_state(S), state_is_closed(S)");

        // Test state_next and final_state
        succ("start_state(S), state_next(S,N1), state_next(N1,N2), state_next(N2,N3), final_state(N3)");
        fail("start_state(S), state_next(S,N1), state_next(N1,N2), final_state(N2)");
        fail("start_state(S), state_next(S,S)");
        succ("start_state(S), state_next_set(S,N), length(N,2), member(NS,N), state_next(S,NS)");

        // Assert that the start state has a transition
        succ("start_state(S), state_transition(S,T), is_transition(T)");
        succ("start_state(S), state_transition_set(S,T), length(T,2), member(X,T), state_transition(S,X)");

        // Assert that the source and target of a transition are right
        succ("start_state(S), state_transition(S,T), transition_source(T,S), transition_target(T,N), state_next(S,N)");

        // Assert that transition_event gives a rule event
        succ("start_state(S), state_transition(S,T), transition_event(T,E), is_ruleevent(E)");

        // Test transition_match
        succ("start_state(S), state_transition(S,T), transition_match(T,M), is_rulematch(M)");

        // Assert that gts_match gives a rule event
        succ("start_state(S), state_ruleevent(S,R), is_ruleevent(R)");
    }

    /**
     * Test the derived LTS predicates
     */
    @Test
    public void testDerivedLts() {
        // Test state_graph
        succ("state_graph(G), is_graph(G)");

        // Test state_is_closed
        succ("closed_state(G), is_state(G)");

        // Test ruleevent
        succ("ruleevent(RE), is_ruleevent(RE)");
    }

    /**
     * Test the trans predicates
     */
    @Test
    public void testTrans() {
        // Assert that ruleevent gives a rule event
        succ("active_ruleevent(RE), is_ruleevent(RE)");

        // Assert that created_edge and created_node give a edge and node
        succ("active_ruleevent(RE), ruleevent_created_edge(RE,E), is_edge(E)");
        succ("active_ruleevent(RE), ruleevent_created_node(RE,N), is_node(N)");

        // Test ruleevent_erased_node and ruleevent_erased_edge
        succ("state(S), state_transition(S,T), transition_event(T,RE), ruleevent_erased_node(RE,N), is_node(N)");
        // succ("state(S), state_transition(S,T), transition_event(T,RE), ruleevent_erased_edge(RE,E), is_edge(E)");

        // Assert that ruleevent_rule gives rule-b
        succ("active_ruleevent(RE), ruleevent_rule(RE,R), rule('rule-a',R)");

        // Assert that ruleevent_label gives 'rule-b'
        succ("active_ruleevent(RE), ruleevent_label(RE,'rule-a')");

        // Assert that the match of a ruleevent is a rulematch
        succ("active_ruleevent(RE), ruleevent_match(RE,M), is_rulematch(M)");

        // Assert that rulematch_rule gives the executed rule
        succ("active_ruleevent(RE), ruleevent_match(RE,M), rulematch_rule(M,R), rule('rule-a',R)");
        fail("active_ruleevent(RE), ruleevent_match(RE,M), rulematch_rule(M,R), rule('rule-b',R)");

        // Assert that rulematch_edge gives an edge
        succ("active_ruleevent(RE), ruleevent_match(RE,M), rulematch_edge(M,E), is_edge(E)");

        // Assert that rulematch_node gives a node
        succ("active_ruleevent(RE), ruleevent_match(RE,M), rulematch_node(M,N), is_node(N)");

        // Test rule event transpose
        succ("active_ruleevent(RE), rule('rule-a',R), rule_rhs(R,RHS), graph_node(RHS,N), ruleevent_transpose(RE,N,T)");
        // TODO: succ("active_ruleevent(RE), rule('rule-a',R), rule_rhs(R,RHS), graph_edge(RHS,E), ruleevent_transpose(RE,E,)");
    }

    /**
     * Test the derived trans predicates
     */
    @Test
    public void testDerivedTrans() {
        // Test ruleevent_match
        succ("active_ruleevent(E), ruleevent_match(E,M)");

        // Test rulematch
        succ("rulematch(RM)");
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
        succ("composite_type_graph(G), label('type:A',A), label('type:D',D), subtype(G,D,A), direct_subtype(G,D,A)");
        succ("composite_type_graph(G), label('type:A',A), label('type:D',D), subtype(G,D,A)");
        fail("composite_type_graph(G), label('type:A',A), label('type:C',C), direct_subtype(G,C,A)");

        // Test type_label
        succ("label('a',L), label(X,L), =(X,'a')");
        succ("label('type:A',A), label(X,A), =(X,'type:A')");
    }

    /**
     * Test the derived type predicates
     */
    @Test
    public void testDerivedType() {
        // Test type_graph
        succ("type_graph(TG)");

        // Test active_type_graph
        succ("active_type_graph(TG)");

        // Test subtype_label
        succ("composite_type_graph(TG), subtype_label(TG,'type:D','type:A')");

        // Test direct_subtype_label
        succ("composite_type_graph(TG), direct_subtype_label(TG,'type:D','type:A')");
    }

    /**
     * Test the algebra predicates
     */
    @Test
    public void testAlgebra() {
        // Test is_valuenode and convert_valuenode
        succ("start_graph(G), graph_node(G,N), is_valuenode(N), convert_valuenode(N,8)");
    }

    /**
     * Test the derived algebra predicates
     */
    @Test
    public void testDerivedAlgebra() {
        // Test node_with_attribute
        succ("start_graph(G), label('num',L), node_with_attribute(G,N,L), is_node(N)");
        succ("start_graph(G), label('num',L), node_with_attribute(G,N,L,8), is_node(N)");
    }

    private void succ(String predicate) {
        assertTrue(test(predicate));
    }

    private void fail(String predicate) {
        assertFalse(test(predicate));
    }

    private boolean test(String predicate) {
        try {
            return PrologTestUtil.test(this.grooveState, predicate);
        } catch (Exception e) {
            e.printStackTrace();
            org.junit.Assert.fail(e.getMessage());
            return false;
        }
    }
}
