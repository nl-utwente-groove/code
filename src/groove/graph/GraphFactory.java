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
 * $Id: GraphFactory.java,v 1.4 2007-04-29 09:22:27 rensink Exp $
 */
package groove.graph;

import groove.view.FormatException;

/**
 * Abstract Factory interface for graph-related classes.
 * Objects implementing this factory can create instances of
 * <tt>Graph</tt>, <tt>Morphism</tt> and <tt>InjectiveMorphism</tt>.   
 * @author Arend Rensink
 * @version $Revision: 1.4 $
 */
public abstract class GraphFactory {
    static public final Morphism defaultPrototypeMorphism =
        DefaultMorphism.prototype;
    static public final Graph defaultPrototypeGraph =
        NodeSetEdgeSetGraph.getPrototype();

    /**
     * Returns a graph factory based on <tt>defaultPrototypeGraph</tt>,
     * <tt>defaultMorphism</tt> and <tt>defaultInjectiveMorphism</tt>
     * for morphisms.
     * @see #defaultPrototypeGraph
     * @see #defaultPrototypeMorphism
     * @see #newGraph()
     */
    static public GraphFactory getInstance() {
        return getInstance(
            defaultPrototypeGraph,
            defaultPrototypeMorphism);
    }

    /**
     * Returns a graph factory based on a given prototype graph,
     * prototype morphism and prototype injective morphism.
     * @param prototypeGraph the source of new graphs in this factory
     * @param prototypeMorphism the source of new morphisms in this factory
     */
    static public GraphFactory getInstance(
        final Graph prototypeGraph,
        final Morphism prototypeMorphism) {
        assert prototypeMorphism != null;
        return new GraphFactory() {
        	@Override
            public Graph newGraph() {
                return prototypeGraph.newGraph();
            }

        	@Override
            public Graph newGraph(Graph graph) {
                Graph result = null;
                try {
                	result = prototypeGraph.newGraph(graph);
                } catch (FormatException gfe) {
                    result = prototypeGraph.newGraph();
                    gfe.printStackTrace();
                }
                return result;
            }

        	@Override
            public Morphism newMorphism(Graph dom, Graph cod) {
                return prototypeMorphism.createMorphism(dom, cod);
            }
        };
    }

    /**
     * Returns a graph factory based on a given prototype graph,
     * using <tt>defaultPrototypeMorphism</tt> and 
     * <tt>defaultInjectivePrototypeMorphism</tt> for morphisms.
     * @param prototypeGraph the source of new graphs in this factory
     */
    static public GraphFactory getInstance(Graph prototypeGraph) {
        return getInstance(prototypeGraph, defaultPrototypeMorphism);
    }

    /**
     * Creates a new, empty <tt>Graph</tt> instance.
     */
    public abstract Graph newGraph();

    public abstract Graph newGraph(Graph graph);

    /**
     * Creates a new, empty <tt>Morphism</tt> between two given <tt>Graph</tt> instances.
     * The method returns <tt>null</tt> if the <tt>Graph</tt> instances are of incorrect
     * type (for the factory instance).
     * @param dom the source graph of the new morphism
     * @param cod the target graph of the new morphism
     * @return a new, empty morphism from <tt>dom</tt> to <tt>cod</tt>,
     *          or <tt>null</tt> if <tt>dom</tt> or <tt>cod</tt> are incorrectly typed.
     */
    public abstract Morphism newMorphism(Graph dom, Graph cod);
}
