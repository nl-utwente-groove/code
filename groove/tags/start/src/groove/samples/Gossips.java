// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: Gossips.java,v 1.1.1.2 2007-03-20 10:42:54 kastenberg Exp $
 */
package groove.samples;

import groove.calc.GraphCalculator;
import groove.calc.GraphResult;
import groove.graph.DefaultEdge;
import groove.graph.DefaultLabel;
import groove.graph.DeltaTarget;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Node;
import groove.lts.AliasSPOApplication;
import groove.lts.DefaultGraphTransition;
import groove.lts.DerivedGraphState;
import groove.lts.GraphState;
import groove.lts.NextStateDeriver;
import groove.rel.VarNodeEdgeMap;
import groove.trans.DefaultRuleFactory;
import groove.trans.GraphGrammar;
import groove.trans.Rule;
import groove.trans.RuleApplication;
import groove.trans.RuleEvent;
import groove.trans.RuleFactory;
import groove.trans.SPOEvent;
import groove.trans.SPORule;
import groove.util.GenerateProgressMonitor;
import groove.util.Groove;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Gossips {
//    static private final String GOSSIP_GPS_NAME = "babbelaars";
    static private final String ATOMIC_GOSSIP_GPS_NAME = "babbelaars-snel";
    static private final String READY_CONDITION_NAME = "klaar";
    static private final String BASIC_GOSSIP_RULE_NAME = "basic";
    static private final DefaultLabel GIRL_EDGE_LABEL = DefaultLabel.createLabel("Meisje");
    static private final String SECRET_LABEL_TEXT = "kent";
    
    static private long startTime;
    
    static void init() {
        startTime = System.currentTimeMillis();
    }

    static void report(GraphResult result) {
        System.out.println("\nResult:\n=======");
        if (result == null) {
            System.out.println("\nNo result found");
        } else {
            printResult(result);
        }
        long currentTime = System.currentTimeMillis();
        long duration = currentTime - startTime;
        System.out.println("Time taken (ms): "+duration);
    }
    
    /** Prints the result to standard output. */
    static void printResult(GraphResult result) {
        List<Graph> trace = result.getTrace();
        List<RuleEvent> rules = new ArrayList<RuleEvent>();
        Iterator<Graph> traceIter = trace.listIterator(1);
        while (traceIter.hasNext()) {
            DerivedGraphState element = (DerivedGraphState) traceIter.next();
            rules.add(element.getEvent());
        }
        List<Integer> edgeCount = new ArrayList<Integer>();
        traceIter = trace.listIterator(0);
        while (traceIter.hasNext()) {
            GraphState element = (GraphState) traceIter.next();
            edgeCount.add(element.getGraph().edgeCount());
        }
        System.out.println("Trace of length: "+rules.size());
        System.out.println("Steps: "+rules);
        System.out.println("Edges: "+edgeCount);
    }
    
    public static void main(String[] args) {
        init();
        // set some policies
        DefaultGraphTransition.setRuleLabelled(false);
        NextStateDeriver.setUseDependencies(false);
        try {
            String startGraphName = "start7";
            if (args.length != 0) {
                startGraphName = args[0];
            }
//            GraphCalculator calc = Groove.createCalculator(GOSSIP_GPS_NAME, startGraphName);
//            calc.addGTSListener(new GenerateProgressMonitor());
//            GraphResult result = calc.getFirst(READY_CONDITION_NAME);
//            report(result);
            GraphGrammar atomic = Groove.loadGrammar(ATOMIC_GOSSIP_GPS_NAME, startGraphName);
            atomic.add(new GossipRule(Groove.loadRuleGraph(BASIC_GOSSIP_RULE_NAME).toRule(), new GossipRuleFactory()));
            GraphCalculator calc2 = Groove.createCalculator(atomic);
            calc2.addGTSListener(new GenerateProgressMonitor());
//            Collection result2 = calc2.getAllMax();
//            System.out.println("\nNumber of solutions: "+result2);
//            Iterator resultIter = result2.iterator();
//            while (resultIter.hasNext()) {
//                GraphResult result = (GraphResult) resultIter.next();
//                Graph graph = result.getGraph();
//                Collection secretEdgeSet = graph.labelEdgeSet(2, SECRET_EDGE_LABEL);
//                System.out.println("Length: "+result.getTrace().size()+", edges: "+secretEdgeSet.size()+" "+secretEdgeSet);
//            }
            GraphResult result2 = calc2.getFirst(READY_CONDITION_NAME);
            report(result2);
//            Groove.saveGraph(calc2.getGTS(), ATOMIC_GOSSIP_GPS_NAME+"+"+startGraphName);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
    
    static class GossipRule extends SPORule {
        public GossipRule(Rule basicRule, RuleFactory ruleFactory) {
            super(basicRule.getMorphism(), basicRule.getName(), ruleFactory);
            setAndNot(basicRule.getNegConjunct());
            setPriority(1);
        }

        public boolean isModifying() {
            return true;
        }
        
        public boolean hasCreators() {
            return true;
        }
        
        protected Element[] computeAnchor() {
            List<Element> result = new ArrayList<Element>();
            for (Edge lhsEdge: lhs().labelEdgeSet(2,GIRL_EDGE_LABEL)) {
                result.add(lhsEdge.source());
            }
            return result.toArray(new Element[0]);
        }

        public RuleEvent createEvent(VarNodeEdgeMap anchorMap) {
        	return getRuleFactory().createRuleEvent(this, anchorMap);
//            return new GossipEvent(this, anchorMap);
        }
    }
    
    static class GossipEvent extends SPOEvent {
        public GossipEvent(GossipRule gossipRule, VarNodeEdgeMap anchorMap, RuleFactory ruleFactory) {
            super(gossipRule, anchorMap, ruleFactory);
        }

        public RuleApplication createApplication(Graph source) {
        	return getRuleFactory().createRuleApplication(this, source);
//            return new GossipApplication(this, source);
        }
    }
    
    static class GossipApplication extends AliasSPOApplication {
        public GossipApplication(SPOEvent event, Graph source, RuleFactory ruleFactory) {
            super(event, source, ruleFactory);
        }

        protected void createEdges(DeltaTarget target) {
            boolean added = false;
            Iterator<Node> girlIter = getAnchorMap().nodeMap().values().iterator();
            Node girl1 = girlIter.next();
            Node girl2 = girlIter.next();
            for (Edge secretEdge: source.outEdgeSet(girl1)) {
                if (secretEdge.label().equals(SECRET_LABEL_TEXT)) {
                    Edge newEdge = DefaultEdge.createEdge(girl2, SECRET_LABEL_TEXT, secretEdge.opposite());
                    if (!source.containsElement(newEdge)) {
                        target.addEdge(newEdge);
                        added = true;
                    }
                }
            }
            for (Edge secretEdge: source.outEdgeSet(girl2)) {
                if (secretEdge.label().equals(SECRET_LABEL_TEXT)) {
                    Edge newEdge = DefaultEdge.createEdge(girl1, SECRET_LABEL_TEXT, secretEdge.opposite());
                    if (!source.containsElement(newEdge)) {
                        target.addEdge(newEdge);
                        added = true;
                    }
                }
            }
            assert added : "No edges added";
        }        
    }

    static class GossipRuleFactory extends DefaultRuleFactory {
    	/** This implementation returns a {@link GossipApplication}. */
    	@Override
    	public RuleApplication createRuleApplication(RuleEvent event, Graph source) {
    		return new GossipApplication((GossipEvent) event, source, this);
    	}

    	/** This implementation returns a {@link GossipEvent}. */
    	@Override
    	public RuleEvent createRuleEvent(Rule rule, VarNodeEdgeMap anchorMap) {
    		return new GossipEvent((GossipRule) rule, anchorMap, this);
    	}
    }
}