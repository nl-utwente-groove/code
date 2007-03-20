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
 * $Id: Simulation.java,v 1.1.1.1 2007-03-20 10:05:36 kastenberg Exp $
 */
package groove.graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Instances of this class compute and store simulations between a domain and codomain,
 * both of which are graphs.
 * Simulations are relations between graph elements of domain and codomain that are consistent
 * w.r.t. the graph structure.
 * This is implemented by a mapping from the domain elements to sets of codomain elements,
 * called image sets.
 * A simulation is called <i>consistent</i> if all image sets are nonempty, and <i>refined</i> if
 * all image sets are singular.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
public interface Simulation {
    /**
     * Set of codomain elements (of the simulation) that constitute the images of some domain element.
     * The set guards its size, throwing <tt>IllegalStateException</tt>s if
     * the size is reduced to zero, and indicating singularity through <tt>isSingular</tt>.
     * Addition to an image set is not possible, after its initialization.
     */
    interface ImageSet<E extends Element> extends Set<E>, Cloneable {
        /**
         * Returns an iterator over this image set.
         * The iterator suppoerts <tt>remove()</tt>; the method
         * may throw an <tt>IllegalStateException</tt> if the removed
         * element was the last in the image set.
         */
        public Iterator<E> iterator();

        /**
         * Indicates whether this image set is a singleton
         * @return <tt>true</tt> if this image set is a singleton
         * @see #getSingular()
         * @ensure <tt>result == (size() == 1)</tt>
         */
        public boolean isSingular();

        /**
         * Returns the unique image in this set, if the set is a singleton.
         * @return the unique element inhabiting this set, or <tt>null</tt> if
         * this set is not a singleton.
         * @see #isSingular()
         */
        public E getSingular();

        /**
         * Returns the key for this image set.
         */
        public E getKey();

        /**
         * Always throws an exception, since empty image sets are inconsistent.
         * @throws UnsupportedOperationException always
         */
        public void clear();

        /**
         * Always throws an exception, since adding elements to an existing image
         * set is not supported.
         * @throws UnsupportedOperationException always
         */
        public boolean add(E image);

        /**
         * Removes an image from this set.
         * @throws IllegalStateException if the set becomes empty thereby
         */
        public boolean remove(Object image);

        /**
         * Reduces the image set to a single image.
         * @throws IllegalStateException if the image was not in the set
         */
        public boolean retain(E image);

        /**
         * Reduces the image set to a given collection..
         * @throws IllegalStateException if set becomes empty thereby
         */
        public boolean retainAll(Collection<?> imageSet);
        
        /** Returns the simulation in which this ImageSet was created. */
        public Simulation getSimulation();
    }

    /**
     * Indicates whether the simulation has been completely refined.
     * This is the case when all image sets are singular.
     */
    boolean isRefined();

    /**
     * Indicates whether the siomulation is consistent.
     * This is the case when all image sets are nonempty.
     * Note that a simulation that is not consistent is useless for our purpose.
     */
    boolean isConsistent();

    /** Returns the domain of this simulation. */
    Graph dom();
    
    /** Returns the codomain of this simulation. */
    Graph cod();

    /** Returns the morphism that this simulation was initialised with. */
    Morphism getMorphism();
    /**
     * Returns a mapping from domain to codomain elements.
     * The mapping consists of those key-image elements that are one-to-one in this simulation;
     * this may not equal all elements as long as {@link #isRefined()}
     * does not hold.
     * @see #isRefined()
     */
    NodeEdgeMap getSingularMap();
    
    /**
     * Checks if this simulation has a (functional) refinement.
     * Convenience method for <tt>getRefinement() != null</tt>.
     * @see #getRefinement()
     */
    boolean hasRefinement();

    /**
     * Returns a (functional) refinement of this morphism, if it has any.
     * Returns <tt>null</tt> if the simulation has no refinements.
     * @see #getRefinementSet()
     */
    NodeEdgeMap getRefinement();

    /**
     * Returns the set of all (functional) refinements of this simulation.
     */
    Collection<NodeEdgeMap> getRefinementSet();

    /**
     * Returns an interator over the (functional) refinements of this simulation.
     * The result is the same as <tt>getRefinementSet().iterator()</tt>,
     * but the iterator returned by this method may operate in a lazy fashion.
     * @see #getRefinementSet()
     */
    Iterator<? extends NodeEdgeMap> getRefinementIter();
}