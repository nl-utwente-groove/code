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
 * $Id: GraphCache.java,v 1.2 2007-03-30 15:50:24 rensink Exp $
 */
package groove.graph;

import groove.graph.iso.CertificateStrategy;
import groove.util.DefaultDispenser;

/**
 * Stores graph information that can be reconstructed from the actual
 * graph, for faster access.
 * Typically, the graph will have a graph cache as a <tt>{@link java.lang.ref.Reference}</tt>.
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 */
public class GraphCache extends GraphShapeCache {
    /**
     * Constructs a dynamic graph cache for a given graph.
     * @param graph the graph for which the cache is to be created.
     * @see #GraphCache(AbstractGraph,boolean)
     */
    public GraphCache(AbstractGraph graph) {
        this(graph, true);
    }
    
    /**
     * Constructs a graph cache for a given graph, which can be either dynamic or
     * static.
     * A dynamic cache listens to graph
     * changes, and keeps its internally cached sets in sync.
     * Since the cache does so by registering itself as a graph listener,
     * this means there will be a hard reference to the cache, and any reference
     * won't be cleared, until the cache is removed from the graph listeners!
     * (This happens automatically in {@link AbstractGraph#setFixed()}).
     * A static cache does not cache dynamic information as long as the graph
     * is not fixed.
     * @param graph the graph for which the cache is to be created.
     * @param dynamic switch to indicate if caching should bbe dynamic
     */
    public GraphCache(AbstractGraph graph, boolean dynamic) {
        super(graph, dynamic);
    }

    /**
     * Keeps the cached sets in sync with changes in the graph.
     */
    @Override
    public void addUpdate(GraphShape graph, Node node) {
    	super.addUpdate(graph, node);
		DefaultDispenser nodeCounter = getNodeCounter();
        int nodeNr = DefaultNode.getNodeNr(node);
		if (nodeCounter.getCount() <= nodeNr) {
			nodeCounter.setCount(nodeNr + 1);
		}
    }
    
    /** Counter to ensure distinctness of fresh node identities. */
    protected DefaultDispenser getNodeCounter() {
    	if (nodeCounter == null) {
    		nodeCounter = new DefaultDispenser();
    	}
    	return nodeCounter;
    }

    /**
     * Returns a certificate strategy for the current state of this graph. If no strategy is
     * currently cached, it is created by calling {@link CertificateStrategy#newInstance(Graph)} on
     * {@link AbstractGraph#getCertificateFactory()}. If the underlying graph is fixed (see {@link Graph#isFixed()}, 
     * the strategy is cached.
     * @see Graph#getCertificate()
     */
    protected CertificateStrategy getCertificateStrategy() {
        CertificateStrategy result = certificateStrategy;
        if (result == null) {
            result = AbstractGraph.getCertificateFactory().newInstance(getGraph());
            if (graph.isFixed()) {
                certificateStrategy = result;
            }
        }
        return result;
    }
    
    /**
     * Returns the graph for which the cache is maintained.
     */
    @Override
    public AbstractGraph getGraph() {
        return (AbstractGraph) graph;
    }

    /** Counter for node numbers. */
    private DefaultDispenser nodeCounter;
    /**
     * The certificate strategy set for the graph.
     * Initially set to <tt>null</tt>.
     */
    private CertificateStrategy certificateStrategy;
}