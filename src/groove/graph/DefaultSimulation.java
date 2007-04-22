/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: DefaultSimulation.java,v 1.5 2007-04-22 23:32:23 rensink Exp $
 */

package groove.graph;

import groove.graph.match.DefaultMatcher;
import groove.util.FilterIterator;
import groove.util.Reporter;
import groove.util.SingularIterator;
import groove.util.TransformIterator;
import groove.util.TransformMap;
import groove.util.TreeHashSet3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Default simulation class.
 * At initialisation time a maximal consistent relation is constructed.
 * Refinement consists of repeatedly selecting an image for those domain
 * elements that have multiple images.
 * @author Arend Rensink
 * @version $Revision: 1.5 $
 * @deprecated use {@link DefaultMatcher} instead
 */
@Deprecated
public class DefaultSimulation extends GenericNodeEdgeHashMap<Node,Simulation.ImageSet<Node>,Edge,Simulation.ImageSet<Edge>> implements Simulation {
    static protected final IllegalStateException emptyImageSet = new IllegalStateException();
    static protected final Iterator<NodeEdgeMap> emptyIterator = Collections.<NodeEdgeMap>emptySet().iterator();

    /**
     * Auxiliary class to implement the lazy refinement iterator.
     * @see #getRefinementIter() 
     */
    private final class RefinementIterator implements Iterator<NodeEdgeMap> {
        /**
         * First looks into the current sub-refinement iterator, if any.
         * If there is none or it is exhausted, attempts to create a sub-refinement
         * iterator by finding the next mulli-image set and creating a sub-refinement
         * iterator over it. 
         */
        public boolean hasNext() {
            while (next == null && nextMayExist) {
                if (subRefinementIter != null && subRefinementIter.hasNext()) {
                    // first continue with the current sub-refinement-iterator
                    next = subRefinementIter.next();
                } else if (currentImageIter != null && currentImageIter.hasNext()) {
                    // now try to continue with the imageSet started previously (if any)
                    Element image = currentImageIter.next();
                    if (currentImageIter.hasNext()) {
                        // there is yet another image for the current key; clone and recurse
                        try {
                            if (nestedSimulation == null) {
                                nestedSimulation = DefaultSimulation.this.clone();
                            } else {
                                nestedSimulation.restore();
                            }
                            nestedSimulation.backup();
                            nestedSimulation.select(currentKey, image);
                            subRefinementIter = nestedSimulation.getRefinementIter();
                            // better throw away the clone, it has been permanently changed
                            nestedSimulation = null;
                        } catch (IllegalStateException exc) {
                            // select made the simulation inconsistent; try the next image
                        }
                    } else {
                        // this is the last image; if it doesn't work there are no more
                        // refinements
                        try {
                            select(currentKey, image);
                            // better throw away the clone, we have progressed beyond it
                            nestedSimulation = null;
                        } catch (IllegalStateException exc) {
                            nextMayExist = false;
                        }
                    }
                } else if (keyIndex < keySchedule.size()) {
                    // investigate the next non-singular entry in this simulation, if any
                	Element key = keySchedule.get(keyIndex);
                	keyIndex++;
                    ImageSet<? extends Element> imageSet = getImageSet(key);
                    // loop here for efficiency
                    while (imageSet.isSingular() && keyIndex < keySchedule.size()) {
                    	key = keySchedule.get(keyIndex);
                    	keyIndex++;
                        imageSet = getImageSet(key);                        
                    }
                    if (!imageSet.isSingular()) {
                        // yes, it is an interesting next entry; process it
                        currentKey = imageSet.getKey();
                        currentImageIter = imageSet.iterator();
                    }
                } else {
                    // we are done; the current simulation is refined
                    assert DefaultSimulation.this.isRefined() : "The simulation should be refined by now: "
                            + DefaultSimulation.this;
                    next = DefaultSimulation.this.getSingularMap();
                    subRefinementIter = emptyIterator;
                    nextMayExist = false;
                }
            }
            return next != null;
        }

        /**
         * First calls {@link #hasNext()}, which does all the work of finding the
         * next element to return; then either returns the (surrounding) {@link DefaultSimulation}
         * or takes the next element from the sub-iterator found by {@link #hasNext()}.
         */
        public NodeEdgeMap next() {
            if (hasNext()) {
                NodeEdgeMap result = next;
                next = null;
                return result;
            } else {
                throw new NoSuchElementException();
            }
        }

        /**
         * @throws UnsupportedOperationException always
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /**
         * The schedule for matching keys, locally stored
         */
        List<Element> keySchedule = getMatchingSchedule();
        /** the key of the entry last retrieved from {@link #currentImageIter}, if any */
        Element currentKey;
        /** Iterator over the currently regarded image set. */
        Iterator<? extends Element> currentImageIter;

        NodeEdgeMap next;
        /** Flag to indicate that there may still be subsequent refinements within this simulation */
        boolean nextMayExist = true;

        /** an iterator obtained from a recursive call of {@link DefaultSimulation#getRefinementIter()}. */
        Iterator<? extends NodeEdgeMap> subRefinementIter;
        
        /** clone of this simulation, preserved for efficiency. */
        DefaultSimulation nestedSimulation;
    }

    /**
     * Filter for a given iterator that only passes edge images matching a certain
     * edge key. Matching means that the image endpoints have to be in the set of
     * known endpoint images (according to the simulation) if any. The edge label
     * is optionally checked.
     */
    protected class EdgeImageFilter extends FilterIterator<Edge> {
        /**
         * Constructs a filter without label checking.
         * @see #EdgeImageFilter(Edge, Iterator, boolean)
         */
        public EdgeImageFilter(Edge key, Iterator<? extends Edge> imageIter) {
            this(key, imageIter, false);
        }

        /**
         * Constructs a filter for a given edge key, which filters a given iterator
         * over prospective images. A boolean switch indicates .
         */
        public EdgeImageFilter(Edge key, Iterator<? extends Edge> imageIter, boolean checkLabel) {
            super(imageIter);
            this.arity = key.endCount();
            this.label = checkLabel ? key.label() : null;
            this.endImages = new Object[arity];
            this.duplicates = new byte[arity];
            for (int i = 0; i < arity; i++) {
                Node domNode = key.end(i);
                duplicates[i] = -1;
                for (byte j = 0; j < i; j++) {
                    if (key.end(j) == domNode) {
                        duplicates[i] = j;
                    }
                }
                if (duplicates[i] < 0) {
                    ImageSet<Node> images = getNode(domNode);
                    if (images != null) {
                        if (images.isSingular()) {
                            endImages[i] = images.getSingular();
                        } else {
                            endImages[i] = images;
                        }
                    }
                }
            }
        }

        /**
         * If label checking was switched on, first invokes {@link #approvesLabel(Edge)} to
         * check the label of <tt>obj</tt> (not that this has to be an {@link Edge} for this
         * purpose); subsequently checks the image ends using {@link #approvesEnds(Edge)}.
         */
        @Override
        protected boolean approves(Object obj) {
            return (label == null || approvesLabel((Edge) obj)) && approvesEnds((Edge) obj);
        }
        
        /**
         * Checks for equality of <tt>image.label()</tt> and <tt>image.endCount()</tt> with the stored label and edge arity.
         */
        protected boolean approvesLabel(Edge image) {
            return image.label().equals(label) && image.endCount() == arity;
        }
        
        /**
         * Checks if the endpoints of <tt>image</tt> are consistent with the image sets
         * stored at construction time of this filter.
         */
        protected boolean approvesEnds(Edge image) {
            boolean result = true;
            for (byte i = 0; result && i < arity; i++) {
                if (duplicates[i] < 0) {
                    Node imageEnd = image.end(i);
                    Object required = endImages[i];
                    if (required instanceof Element) {
                        result = required.equals(imageEnd);
                    } else if (required instanceof Set) {
                        result = ((Set) required).contains(imageEnd);
                    }
                } else {
                    result = image.end(i) == image.end(duplicates[i]);
                }
            }
            return result;
        }
        
        /**
         * The arity of the edge key against which we are checking
         */
        protected final int arity;
        /**
         * The label of the edge key against we are checking; <tt>null</tt> if
         * no label checkig is done.
         */
        protected final Label label;
        /**
         * For each endpoint index, the singular image (as an {@link Element})
         * or the set of images (as an {@link DefaultImageSet}) against which we need 
         * to check; or <tt>null</tt> if no check is to be done for this index, 
         * for instance because no images we known at construction of this filter.
         */
        protected final Object[] endImages;
        /**
         * For each endpoint index, a lower index at which the edge key endpoint against
         * which we check coincides with the edge key endpoint at this index; or <tt>-1</tt>
         * if there is no such lower index.
         */
        protected final byte[] duplicates;
    }
    
    /**
     * Set to store the simulation images of a single key.
     * The set guards its size, throwing <tt>IllegalStateException</tt>s if
     * the size is reduced to zero, and indicating singularity through
     * <tt>isSingular</tt> 
     */
    @Deprecated
    protected class DefaultImageSet<E extends Element> extends HashSet<E> implements Simulation.ImageSet<E> {
        /**
         * Implements an iterator over an image set.
         * In particular, <tt>remove()</tt> is extended to register changes
         * in set size, and a method <tt>latest()</tt> is added to retrieve
         * the object most recently returned by <tt>next()</tt>. 
         */
        class ImageSetIterator implements Iterator<E> {
            public E next() {
                latest = setIterator.next();
                return latest;
            }

            public boolean hasNext() {
                return setIterator.hasNext();
            }

            /**
             * Apart from tremoveing the object most recently returned by
             * <tt>next()</tt>, this method also registers changes in the 
             * set size through <tt>registerSizeChang()</tt>.
             * @see #updateSize()
             */
            public void remove() {
                if (isSingular()) {
                    throw emptyImageSet;
                }
                setIterator.remove();
                updateSize();
            }

            /**
             * Returns the object most recently returned by <tt>next()</tt>.
             * Also works after <tt>remove()</tt>.
             * @return the object most recently returned by <tt>next()</tt>
             */
            protected E latest() {
                return latest;
            }

            /** Contains the set's iterator. */
            private final Iterator<E> setIterator = DefaultImageSet.super.iterator();
            /** Contains the object most recently returned by <tt>next()</tt>. */
            private E latest;
        }

        /**
         * Constructs an image set by cloning an existing set of images
         * <tt>multiImageCount</tt> is increased if the image set is not a singleton.
         * @param imageSet the set of images
         * @throws IllegalStateException if <tt>imageSet</tt> is empty
         * @require <tt>! imageSet.isEmpty()</tt>
         * @ensure <tt>imageSet.equals(result)</tt>
         */
        protected DefaultImageSet(E key, Collection<E> imageSet) {
            this(key, imageSet.iterator());
        }

        /**
         * Constructs an image set from an iterator.
         * <tt>multiImageCount</tt> is increased if the image set is not a singleton.
         * @param imageIter iterator over the initial images
         * @throws IllegalStateException if <tt>imageSet</tt> is empty
         * @require <tt>! imageSet.isEmpty()</tt>
         * @ensure <tt>imageSet.equals(result)</tt>
         */
        protected DefaultImageSet(E key, Iterator<? extends E> imageIter) {
            this.key = key;
            E image = null;
            while (imageIter.hasNext()) {
                image = imageIter.next();
                super.add(image);
            }
            switch (size()) {
            case 0 :
                throw emptyImageSet;
            case 1 :
                setSingular(image);
                break;
            default :
            }
        }

        /**
         * Constructs a singleton image set.
         * @param image the unique image
         * @ensure <tt>getSingleImage() == image</tt>
         */
        protected DefaultImageSet(E key, E image) {
            super.add(image);
            this.key = key;
            setSingular(image);
        }

        /**
         * Returns an iterator over this image set.
         * The iterator suppoerts <tt>remove()</tt>; the method
         * may throw an <tt>IllegalStateException</tt> if the removed
         * element was the last in the image set.
         * The method is implemented by invoking <tt>newIterator()</tt>.
         */
        @Override
        public Iterator<E> iterator() {
            return new ImageSetIterator();
        }

        /**
         * Indicates whether this image set is a singleton
         * @return <tt>true</tt> if this image set is a singleton
         * @see #getSingular()
         * @ensure <tt>result == (size() == 1)</tt>
         */
        public boolean isSingular() {
            return singleImage != null;
        }

        /**
         * Returns the unique image in this set, if the set is a singleton.
         * @return the unique element inhabiting this set, or <tt>null</tt> if
         * this set is not a singleton.
         * @see #isSingular()
         */
        public E getSingular() {
            return singleImage;
        }

        /**
         * Returns the key for this image set.
         */
        public E getKey() {
            return key;
        }

        /**
         * Always throws an exception, since empty image sets are inconsistent.
         * @throws UnsupportedOperationException always
         */
        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        /**
         * Always throws an exception, since adding elements to an existing image
         * set is not supported.
         * @throws UnsupportedOperationException always
         */
        @Override
        public boolean add(E image) {
            throw new UnsupportedOperationException();
        }

        /**
         * Removes an image from this set.
         * @throws IllegalStateException if the set becomes empty thereby
         */
        @Override
        public boolean remove(Object image) {
            if (isSingular()) {
                if (singleImage.equals(image)) {
                    throw emptyImageSet;
                } else {
                    return false;
                }
            } else {
                boolean result = super.remove(image);
                if (result) {
                    updateSize();
                }
                return result;
            }
        }

        /**
         * Reduces the image set to a single image.
         * @throws IllegalStateException if the image was not in the set
         */
        public boolean retain(E image) {
            if (isSingular()) {
                if (singleImage.equals(image)) {
                    return false;
                } else {
                    throw emptyImageSet;                    
                }
            } else {
                if (contains(image)) {
                    super.clear();
                    super.add(image);
                    setSingular(image);
                    return true;
                } else {
                    throw emptyImageSet;
                }
            }
        }

        /**
         * Removes all elements from a given collection.
         * @throws IllegalStateException if set becomes empty thereby
         */
        @Override
        public boolean removeAll(Collection<?> imageSet) {
            if (isSingular()) {
                if (imageSet.contains(singleImage)) {
                    throw emptyImageSet;
                } else {
                    return false;
                }
            } else {
                // we re-implement the method to avoid giving repeated notifications
                boolean result = false;
                for (Object elem: imageSet) {
                    result |= super.remove(elem);
                }
                if (result) {
                    updateSize();
                }
                return result;
            }
        }
        
        /**
         * Reduces the image set to a given collection.
         * @throws IllegalStateException if set becomes empty thereby
         */
        @Override
        public boolean retainAll(Collection<?> imageSet) {
            if (isSingular()) {
                if (!imageSet.contains(singleImage)) {
                    throw emptyImageSet;
                } else {
                    return false;
                }
            } else {
                // we re-implement the method to avoid giving repeated notifications
                boolean result = false;
                if (size() <= 2 * imageSet.size()) {
                    // iterate over super to avoid calling updateSize() for
                    // every removed element
                    Iterator<E> iter = super.iterator();
                    while (iter.hasNext()) {
                        E elem = iter.next();
                        if (!imageSet.contains(elem)) {
                            result = true;
                            iter.remove();
                        }
                    }
                } else {
                    // the other set is much smaller, so iterate over that instead
                    HashSet current = (HashSet) clone();
                    super.clear();
                    for (Object elem: imageSet) {
                        if (current.contains(elem)) {
                            super.add((E) elem);
                        }
                    }
                    result = true;
                }
                if (result) {
                    updateSize();
                }
                return result;
            }
        }
        
        /** Equality is delegated to the key returned by {@link #getKey()}. */
        @Override
        public boolean equals(Object o) {
            return (o instanceof DefaultImageSet) && key.equals(((DefaultImageSet) o).key);
        }

        /** The hash code is taken from the key returned by {@link #getKey()}. */
        @Override
        public int hashCode() {
            return key.hashCode();
        }
        
        /**
         * Returns the enclosing {@link Simulation} of the {@link DefaultImageSet}.
         */
        public Simulation getSimulation() {
            return DefaultSimulation.this;
        }
        
        /**
         * Updates the image set to register that the image is now singular.
         * This is only valid if it was <i>not</i> singular before!
         * Callback method; invokes {@link #notifySingular(ImageSet)} on the
         * enclosing {@link DefaultSimulation}.
         * @param image the singular element of the set
         */
        protected void setSingular(E image) {
            assert size() == 1 && contains(image) && !isSingular();
            singleImage = image;
            notifySingular(this);
        }

        /**
         * Updates the size of the set.
         * Sets the singularity property or throws an {@link IllegalStateException},
         * depending on the cardinality of the image set.
         * Callback method from those methods of {@link DefaultImageSet} that modify the set.
         */
        protected void updateSize() {
            switch (size()) {
                case 0 :
                    // if a singular image set can get inconsistent, there
                    // is a big risk that this affects the simulation from
                    // which this one was cloned, since we don't copy singular
                    // image sets
                    assert singleImage == null;
                    throw emptyImageSet;
                case 1 :
                    setSingular(super.iterator().next());
                    break;
                default :
            }
        }

        /** The unique image in this set, if the set is a singleton. */
        protected E singleImage = null;
        /** The key for whick this is an image set. */
        protected final E key;
    }
   
    /**
     * A map whose images are constructed as 
     * a view upon the simulation's image sets.
     */
    @Deprecated
    protected class SingularTransformMap<E extends Element> extends TransformMap<E,ImageSet<E>,E> {
    	protected SingularTransformMap(Map<E,ImageSet<E>> map) {
    		super(map);
    	}
    	
        @Override
    	protected E toOuter(ImageSet<E> obj) {
    		return obj.getSingular();
    	}
    }
    
    /**
     * A node-edge map whose images are constructed as 
     * views upon the simulation's node and edge image sets.
     */
    protected class MyNodeEdgeMap extends NodeEdgeHashMap {
		@Override
		protected Map<Edge, Edge> createEdgeMap() {
			return new SingularTransformMap<Edge>(DefaultSimulation.this.edgeMap());
		}

		@Override
		protected Map<Node, Node> createNodeMap() {
			return new SingularTransformMap<Node>(DefaultSimulation.this.nodeMap());
		}
		
		/**
		 * Overwrites the method to test for equality using <code>==</code>.
		 */
		@Override
		public boolean equals(Object obj) {
			return this == obj;
		}

		/**
		 * Overwrites the method to call {@link System#identityHashCode(Object)}.
		 */
		@Override
		public int hashCode() {
			return System.identityHashCode(this);
		}

        @Override
		public MyNodeEdgeMap clone() {
			return new MyNodeEdgeMap();
		}
    }
    
    /**
     * Constructs a simulation as an extension of a given morphism.
     * Initialization of the images is done in {@link #initSimulation()}.
     * @param morph the intended basis of the simulation
     * @ensure <tt>getMorphism() == morph</tt>
     */
    public DefaultSimulation(Morphism morph) {
        this.morph = morph;
        try {
            reporter.start(NEW_TOTAL);
            setInitializing(true);
            initSimulation();
        } catch (IllegalStateException exc) {
            notifyInconsistent();
        } finally {
            setInitializing(false);
            reporter.stop();
        }
    }

    public boolean isConsistent() {
        return consistent;
    }

    public boolean isRefined() {
        return isConsistent() && multiImageCount == 0;
    }
    
    /**
     * Returns a map whose node and edge sets are constructed as 
     * views upon the simulation's node and edge image sets.
     */
    public NodeEdgeMap getSingularMap() {
        return new MyNodeEdgeMap();
    }

    public Graph dom() {
        return morph.dom();
    }

    public Graph cod() {
        return morph.cod();
    }

    public Morphism getMorphism() {
		return morph;
	}

	public boolean hasRefinement() {
        return getRefinement() != null;
    }

    /**
     * Returns a refinement of this simulation that is functional,
     * i.e., in which all image sets have a single element.
     * Returns <tt>null</tt> if there is no such refinement.
     */
    public NodeEdgeMap getRefinement() {
        reporter.start(GET_REFINEMENT);
        try {
            if (REFINE_DEBUG) {
                System.out.println("Refining: " + this);
            }
            if (!isConsistent()) {
                return null;
            }
            if (isRefined()) {
                return this.getSingularMap();
            }
            List<Element> keySchedule = getMatchingSchedule();
            while (keyIndex < keySchedule.size()) {
                Element key = keySchedule.get(keyIndex);
                keyIndex++;
                ImageSet<? extends Element> imageSet = getImageSet(key);
                if (imageSet.isSingular()) {
                    continue;
                }
                Iterator<? extends Element> imageIter = imageSet.iterator();
                Element image = imageIter.next();
                DefaultSimulation nested = null;
                // we are sure there is at least one more image, so
                // we must clone to process the current one
                do {
                    if (nested == null) {
                        nested = clone();
                    } else {
                        nested.restore();
                    }
                    try {
                        nested.backup();
                        nested.select(key, image);
                        // early test if we are done
                        if (nested.isRefined()) {
                            return nested.getSingularMap();
                        }
                        NodeEdgeMap result = nested.getRefinement();
                        if (result != null) {
                            return result;
                        }
                    } catch (IllegalStateException exc) {
                        // this image was no good; proceed with next
                    }
                    image = imageIter.next();
                } while (imageIter.hasNext());
                // it's the last image for this key;
                // we don't have to clone the simulation to try it
                select(key, image);
            }
            assert isRefined() : "Simulation not refined: "+this;
            return this.getSingularMap();
        } catch (IllegalStateException exc) {
            // this simulation has become inconsistent
            notifyInconsistent();
            return null;
        } finally {
            reporter.stop();
        }
    }

    public Collection<NodeEdgeMap> getRefinementSet() {
        reporter.start(GET_REFINEMENT_SET);
        Collection<NodeEdgeMap> result = new TreeHashSet3<NodeEdgeMap>();
//        Collection result = new HashSet();
        collectRefinements(result);
        reporter.stop();
        return result;
    }

    public Iterator<? extends NodeEdgeMap> getRefinementIter() {
        reporter.start(GET_REFINEMENT_ITER);
        try {
            if (REFINE_DEBUG) {
                System.out.println("Refining: " + this);
            }
            if (!isConsistent()) {
                // returns an empty iterator
                return emptyIterator;
            } else if (isRefined()) {
                // returns an iterator containing just this
                return new SingularIterator<NodeEdgeMap>(this.getSingularMap());
            } else {
                return new RefinementIterator();
            }
        } catch (IllegalStateException exc) {
            return emptyIterator;
        } finally {
            reporter.stop();
        }
    }
    
    /**
     * Clones the underlying hash map. Does not clone the entry values, which will
     * therefore remain pointing to the caller of {@link #clone()} as their
     * enclosing simulation! New entries are inserted in {@link #getFreshImageSet(Node)}
     * as required, to avoid sharing and notification problems. 
     */
    @Override
    public DefaultSimulation clone() {
        reporter.start(CLONE);
        try {
            DefaultSimulation result = (DefaultSimulation) super.clone();
            result.changes = new HashMap<Element,Object>();
            return result;
        } finally {
            reporter.stop();
        }
    }
    
    /**
     * Since the images in this map are <code>ImageSet</code>s, which are considered equal if their
     * keys are equal, we need to change equality on the level of the map.
     * Otherwise, any simulation of the same domain graph would be equal!
     * We choose object identity as the notion of equality. 
     */
    @Override
    public boolean equals(Object o) {
        return this == o;
    }
    
    /**
     * Changed in correspondence with {@link #equals(Object)} to
     * {@link System#identityHashCode(java.lang.Object)}.
     */
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
    
    /**
     * Returns the current value of initializing.
     */
    protected boolean isInitializing() {
        return initializing;
    }
    
    /**
     * Sets the initializing flag.
     * Callback method, invoked once with <tt>true</tt> before the
     * invocation of {@link #initSimulation()} and once with <tt>false</tt> afterwards.
     * @param initializing The initializing to set.
     */
    protected void setInitializing(boolean initializing) {
        this.initializing = initializing;
    }
    
    /**
     * Initializes the images of this simulation. Callback method invoked from the constructor.
     * @throws IllegalStateException if the initialization results in an inconsistent state, i.e.,
     *         no image for a particular key
     */
    protected void initSimulation() {
        try {
            reporter.start(INIT_IMAGES);
            // we register preexisting mappings in the morphism
            try {
            reporter.start(INIT_MORPHISM);
            for (Map.Entry<Node,Node> morphEntry: morph.elementMap().nodeMap().entrySet()) {
                Node key = morphEntry.getKey();
                ImageSet<Node> imageSet = putNode(key, morphEntry.getValue());
                // take a chance to check for embargoes and injections
                notifyNodeChange(imageSet, null);
            }
            for (Map.Entry<Edge,Edge> morphEntry: morph.elementMap().edgeMap().entrySet()) {
                putEdge(morphEntry.getKey(), morphEntry.getValue());
            }
            } finally {
            reporter.stop();
            }
            // now we initialize the remainder of the domain elements
            for (Element key: getMatchingSchedule()) {
                if (key instanceof Node) {
                    initNodeImageSet((Node) key);
                } else {
                    initEdgeImageSet((Edge) key);
                }
            }
            // finally make sure there are no outstanding changes
            propagateChanges();
        } finally {
            reporter.stop();
        }
    }

    /**
     * Initialises the images of a given node key.
     * Apart from computing the set of possible images (using {{@link #getNodeMatches(Node)})
     * this involves propagating the resulting choices to the incident edges.
     * If this results in an inconcistency (that is, the number of images 
     * of some element becomes zero) a {@link IllegalArgumentException} is thrown.
     * @param key the key (from the domain) for which an initial
     * set of images is to be initialised.
     * @throws IllegalArgumentException if the initialisation results
     * in an inconsistent simulation
     */
    protected void initNodeImageSet(Node key) {
        reporter.start(INIT_ELEMENT);
        try {
            if (!nodeMap().containsKey(key)) {
                Iterator<? extends Node> matchIter = getNodeMatches(key);
                if (!matchIter.hasNext()) {
                    throw emptyImageSet;
                }
                ImageSet<Node> imageSet = putNode(key, matchIter);
                notifyNodeChange(imageSet, null);
                if (INIT_DEBUG) {
                    System.err.println("Result: " + key + "=" + getNode(key));
                }
            }
        } finally {
            reporter.stop();
        }
    }

    /**
     * Initialises the images of a given edge key.
     * Apart from computing the set of possible images (using {{@link #getEdgeMatches(Edge)})
     * this involves propagating the resulting choices to the end nodes.
     * If this results in an inconcistency (that is, the number of images 
     * of some element becomes zero) a {@link IllegalArgumentException} is thrown.
     * @param key the key (from the domain) for which an initial
     * set of images is to be initialised.
     * @throws IllegalArgumentException if the initialisation results
     * in an inconsistent simulation
     */
    protected void initEdgeImageSet(Edge key) {
        reporter.start(INIT_ELEMENT);
        try {
            if (!edgeMap().containsKey(key)) {
                Iterator<? extends Edge> matchIter = getEdgeMatches(key);//filterEnds(getEdgeMatches(key), key);
                if (!matchIter.hasNext()) {
                    throw emptyImageSet;
                }
                ImageSet<Edge> imageSet = putEdge(key, matchIter);
                notifyEdgeChange(imageSet, null);
                if (INIT_DEBUG) {
                    System.err.println("Result: " + key + "=" + getEdge(key));
                }
            }
        } finally {
            reporter.stop();
        }
    }
    
    /**
     * Recursively computes all refinements of this simulation, and adds them to the collection
     * passed in as a parameter.
     * @param result the collection that will hold the set of refinements after the method has
     *        returned
     */
    protected void collectRefinements(Collection<NodeEdgeMap> result) {
        try {
            if (REFINE_DEBUG) {
                System.out.println("Refining: " + this);
            }
            if (!isConsistent()) {
                return;
            }
            if (isRefined()) {
                result.add(this.getSingularMap());
                return;
            }
            List<Element> keySchedule = getMatchingSchedule();
            while (keyIndex < keySchedule.size()) {
                Element key = keySchedule.get(keyIndex);
            	keyIndex++;
                ImageSet<? extends Element> imageSet = getImageSet(key);
                if (imageSet.isSingular()) {
                    continue;
                }
                Iterator<? extends Element> imageIter = imageSet.iterator();
                Element image = imageIter.next();
                DefaultSimulation nested = null;
                // we are sure there is at least one more image, so
                // we must clone to process the current one
                do {
                    // nested is set to null if we have to clone it
                    if (nested == null) {
                        nested = clone();
                    } else {
                        nested.restore();
                    }
                    try {
                        nested.backup();
                        nested.select(key, image);
                        nested.collectRefinements(result);
                        // set to null to ensure cloning next time round
                        nested = null;
                    } catch (IllegalStateException exc) {
                        // do nothing; alles sal reg kom
                    }
                    image = imageIter.next();
                } while (imageIter.hasNext());
                // it's the last image for this key;
                // we don't have to clone the simulation to try it
                select(key, image);
            }
            boolean isNew = result.add(this.getSingularMap());
            assert isNew : "New refinement "+this+" already in set "+result;
        } catch (IllegalStateException exc) {
            // this simulation has become inconsistent
            notifyInconsistent();
        }
    }
    
    /**
     * Returns the set of elements in the codomain
     * that match a given key.
     * Takes the already existing simulation into account, so that the number
     * of elements inconsistent with existing mappings is minimized.
     * Overriding subclasses must ensure that no side effects ensue.
     * @param key the domain element to be matched
     * @return a set of codomain {@link Element}s that match <tt>key</tt>
     */
    protected Iterator<? extends Node> getNodeMatches(Node key) {
        Collection<Node> oldImages = getNode(key);
        if (oldImages == null) {
            return cod().nodeSet().iterator();
        } else {
            return oldImages.iterator();
        }
    }
    
    /**
     * Returns the set of elements in the codomain that match a given key. Takes the already
     * existing simulation into account, so that the number of elements inconsistent with existing
     * mappings is minimized. Overriding subclasses must ensure that no side effects ensue.
     * @param key the domain element to be matched
     * @return a set of codomain {@link Element}s that match <tt>key</tt>
     */
    protected Iterator<? extends Edge> getEdgeMatches(Edge key) {
            int arity = key.endCount();
            // check if the label exists at all
            Collection<? extends Edge> labelEdgeSet = cod().labelEdgeSet(arity, key.label());
            if (labelEdgeSet == null) {
                throw emptyImageSet;
            }
            return filterEnds(labelEdgeSet.iterator(), key);
//            return labelEdgeSet.iterator();
//            // look for an end point with singular image
//            Node singularEndImage = null;
//            for (int i = 0; singularEndImage == null && i < arity; i++) {
//                Simulation.ImageSet endImageSet = getImageSet(edgeKey.end(i)); 
//                if (endImageSet != null && endImageSet.isSingular()) {
//                    singularEndImage = (Node) endImageSet.getSingular();
//                }
//            }
//            if (singularEndImage == null) {
//                return filterEnds(labelEdgeSet.iterator(), key);
//            } else {
//                return filterLabelEnds(cod().edgeSet(singularEndImage).iterator(), edgeKey); 
//            }
    }

    /**
     * Filters a given iterator on edge images by matching the endpoints
     * against the allowed end points of the edge.
     * @param iter iterator over the (preliminary) images 
     * @param key the edge according to which we intend to filter 
     * @see #filterLabelEnds(Iterator, Edge)
     * @see EdgeImageFilter
     */
    protected Iterator<Edge> filterEnds(Iterator<? extends Edge> iter, Edge key) {
        return new EdgeImageFilter(key, iter);
    }


    /**
     * Filters a given iterator on edge images by matching the endpoints
     * and label against the allowed end points and label of the edge.
     * @param iter iterator over the (preliminary) images 
     * @param key the edge according to which we intend to filter 
     * @see #filterEnds(Iterator, Edge)
     * @see EdgeImageFilter
     */
    protected Iterator<Edge> filterLabelEnds(Iterator<Edge> iter, Edge key) {
        return new EdgeImageFilter(key, iter, true);
    }

    /**
     * Narrows the simulation, by selecting a key/image pair and then
     * stabilizing the simulation. Throws an {@link IllegalStateException} if
     * this does not succeed.
     * @param key the chosen key
     * @param image the chosen image
     * @throws IllegalStateException if the selection makes the simulation inconsistent
     */
    protected void select(Element key, Element image) {
        try {
            reporter.start(SELECT);
            if (key instanceof Edge) {
                restrictEdgeImages((Edge) key, (Edge) image);
            } else {
                restrictNodeImages((Node) key, (Node) image, null);
            }
            propagateChanges();
        } finally {
            reporter.stop();
        }
    }

    /**
     * Restricts the images of a given edge key to a particular codomain edge.
     * If the key does not yet exist, creates a simulation entry for it, with the given image.
     * Also propagates the changes to the end nodes.
     * This implementation propagates through {@link #restrictNodeImages(Node, Node, Edge)}.
     * @param key the chosen key
     * @param image the chosen image
     * @throws IllegalStateException if the simulation becomes inconsistent
     */
    protected void restrictEdgeImages(Edge key, Edge image) {
        reporter.start(RESTRICT_EDGE_IMAGES);
        try {
            ImageSet<Edge> oldImageSet = getFreshImageSet(key);
            if (oldImageSet == null) {
                notifyEdgeChange(putEdge(key, image), null);                    
            } else if (oldImageSet.retain(image)) {
                notifyEdgeChange(oldImageSet, null);                    
            }
//                int arity = key.endCount();
//                for (int i = 0; i < arity; i++) {
//                    if (!isDuplicate(key, i)) {
//                        restrictNodeImages(key.end(i), image.end(i), key);
//                    }
//                }
        } finally {
            reporter.stop();
        }
    }
    
    /**
     * Restricts the images of a given edge to those that are consistent with the images of a
     * particular (previously changed) end node.
     * If the key does not yet exists, creates an entry for it using {@link #getEdgeMatches(Edge)}
     * to determine the initial elements.
     * Also propagates the changes to the end nodes. 
     * @param key the edge whose images are to be restricted now
     * @param trigger the domain node whose image has been changed previously
     * @throws IllegalStateException if the simulation becomes inconsistent
     */
    protected void restrictEdgeImages(Edge key, Node trigger) {
        reporter.start(RESTRICT_EDGE_IMAGES);
        try {
            int endIndex = key.endIndex(trigger);
            ImageSet<Edge> oldImageSet = getFreshImageSet(key);
            if (oldImageSet == null) {
            	ImageSet<Edge> newImageSet = putEdge(key, getEdgeMatches(key));
                notifyEdgeChange(newImageSet, null);
            } else if (oldImageSet.isSingular()) {
                ImageSet<Node> triggerImages = getNode(trigger);
                Edge oldImage = oldImageSet.getSingular();
                if (triggerImages.retain(oldImage.end(endIndex))) {
                    notifyNodeChange(triggerImages, key);
                }
            } else {
                ImageSet<Node> triggerImages = getNode(trigger);
                boolean changed = false;
//                Set retainedImageEnds = new HashSet();
                Set<Node> retainedImageEnds = DefaultSimulation.retainedImageEnds;
                retainedImageEnds.clear();
//                Set retainedImageEnds = new TreeHashSet(TreeHashSet.HASHCODE_EQUATOR);
                Iterator<Edge> oldImageIter = oldImageSet.iterator();
                while (oldImageIter.hasNext()) {
                    Node oldImageEnd = oldImageIter.next().end(endIndex);
                    if (!triggerImages.contains(oldImageEnd)) {
                        oldImageIter.remove();
                        changed = true;
                    } else {
                        retainedImageEnds.add(oldImageEnd);
                    }
                }
                if (changed) {
                    //CODE: why this check?
                    if (retainedImageEnds.size() < triggerImages.size()) {
                        notifyEdgeChange(oldImageSet, null);
                    } else {
                        notifyEdgeChange(oldImageSet, trigger);
                    }
                }
            }
        } finally { reporter.stop(); }
    }
    
    static private final Set<Node> retainedImageEnds = new NodeSet();
    
    /**
     * @param key
     * @param newImage
     * @param trigger
     */
    protected void restrictNodeImages(Node key, Node newImage, Edge trigger) {
        reporter.start(RESTRICT_NODE_IMAGES);
        try {
            ImageSet<Node> oldImages = getFreshImageSet(key);
            if (oldImages == null) {
                ImageSet<Node> newImages = putNode(key, newImage);
                notifyNodeChange(newImages, trigger);
            } else if (oldImages.retain(newImage)) {
                notifyNodeChange(oldImages, trigger);
            }
        } finally {
            reporter.stop();
        }
    }

    /**
     * Restrict the images of a given node to those that are consistent with a given
     * set of images. If the node was not yet in the simulation, an entry is created for it. 
     * Also propagates actual changes using {@link #notifyNodeChange(ImageSet, Edge)}.
     * @param key the node whose images are to be restricted
     * @param newImageIter iterator over the intended new images
     * @param trigger edge whose images have changed at an earlier stage
     */
    protected void restrictNodeImages(Node key, Iterator<Node> newImageIter, Edge trigger) {
        reporter.start(RESTRICT_NODE_IMAGES);
        try {
            final ImageSet<Node> oldImages = getFreshImageSet(key);
            if (oldImages == null) {
                ImageSet<Node> newImages = putNode(key, newImageIter);
                notifyNodeChange(newImages, trigger);
            } else {
                // first find out which images are actually to be removed
                // one of the retained images, used in case the result set becomes singular
                Node retainedImage = null;
                boolean triggerChanged = false;
                Set<Element> removeSet = new HashSet<Element>(oldImages);
                while (newImageIter.hasNext()) {
                    Node newImage = newImageIter.next();
                    // try to remove the element
                    if (removeSet.remove(newImage)) {
                        retainedImage = newImage;
                    } else if (!oldImages.contains(newImage)) {
                        // removal did not change the remove set
                        // because it was actually not an existing image
                        // this means that the trigger images must be restricted
                        newImageIter.remove();
                        triggerChanged = true;
                    }
                }
                int removeSize = removeSet.size();
                if (removeSize > 0) {
                    boolean changed;
                    switch (oldImages.size() - removeSize) {
                    case 0:
                        throw emptyImageSet;
                    case 1:
                        changed = oldImages.retain(retainedImage);
                        assert changed : "Removing " + removeSet + " and retaining "
                                + retainedImage + " did not change " + key + "=" + oldImages;
                        notifyNodeChange(oldImages, trigger);
                        break;
                    default:
                        changed = oldImages.removeAll(removeSet);
                        assert changed : "Removing " + removeSet + " and retaining "
                                + retainedImage + " did not change " + key + "=" + oldImages;
                        notifyNodeChange(oldImages, trigger);
                    }
                }
                if (triggerChanged) {
                    notifyEdgeChange(getEdge(trigger), key);
                }
            }
        } finally {
            reporter.stop();
        }
    }

    /**
     * Registers a change previously performed on the images of an edge.
     * The <i>trigger</i> of the change to the edge (which is one of the
     * end points that was previously changed) is also passed in; this
     * end point may be skipped during propagation.
     * Registration may involve either storing the change for later
     * propagation or immediate propagation.
     * This implementation immediately propagates the change.
     * @param changed the changed edge images
     * @param trigger the trigger of the change; may be <tt>null</tt>
     * @see #propagateEdgeChange(ImageSet, Element)
     */
    protected void notifyEdgeChange(ImageSet<Edge> changed, Node trigger) {
        propagateEdgeChange(changed, trigger);
    }

    /**
     * Registers a change previously performed on the images of a node.
     * The <i>cause</i> of the change to the node (which is one of the
     * incident edges that was previously changed) is also passed in; this
     * edge may be skipped during propagation.
     * Registration may involve either storing the change for later
     * propagation or immediate propagation.
     * This implementation stores the change for later propagation.
     * @param changed the changed node images
     * @param trigger the trigger of the change; may be <tt>null</tt>
     */
    protected void notifyNodeChange(ImageSet<Node> changed, Edge trigger) {
        propagateNodeChange(changed, trigger);
    }
    
    /**
     * Registers the fact that an image set has just been set to singular.
     * The notification is done from the {@link DefaultImageSet}, and may occur
     * even before the set itself is put into the simulation.
     * This implementation adjusts the multi-image count.
     * @param changed the image set that has just been set to singular
     */
    protected void notifySingular(ImageSet<?> changed) {
    	decreaseMultiImageCount();
//        multiImageCount--;
    }
    
    protected Object addChange(Element change, Object trigger) {
        return changes.put(change, trigger);
    }
    
    /**
     * Signals if there is a stored change to be propagated.
     * Used as a callback method from {@link #propagateChanges()}.
     * The next change can be retrieved by {@link #nextChange()}.
     * @return <tt>true</tt> if {@link #nextChange()} may be invoked to get 
     * a next change
     * @see #nextChange()
     * @see #propagateChanges()
     */
    protected boolean hasNextChange() {
        boolean result = ! changes.isEmpty();
        return result;
    }
    
    /**
     * Returns the next change to be propagated, if there is a change stored.
     * The availability of a next change is signalled by {@link #hasNextChange()}.
     * Used as a callback method from {@link #propagateChanges()}.
     * @return the next change, as a <b>change</b>-<tt>cause</tt> pair.
     * @see #hasNextChange()
     * @see #propagateChanges()
     */
    protected Element nextChange() {
        Iterator<Map.Entry<Element,Object>> changeIter = changes.entrySet().iterator();
        changeEntry = changeIter.next();
        changeIter.remove();
        return changeEntry.getKey();
    }
    
    /**
     * Returns the trigger of the change last returned by {@link #nextChange()}.
     * @see #nextChange()
     */
    protected Element latestTrigger() {
        return (Element) changeEntry.getValue();
    }
    
    /**
     * Iterates over the set of <i>stored changes</i>, which has been built up by the
     * notification methods {@link #notifyNodeChange(ImageSet, Edge)} and {@link #notifyEdgeChange(ImageSet, Node)},
     * and is accessed through {@link #hasNextChange()}, {@link #nextChange()}
     * and {@link #latestTrigger()}.
     * The actual processing
     * is delegated to {@link #propagateEdgeChange(ImageSet, Node)} and {@link #propagateNodeChange(ImageSet, Edge)}.
     * @throws IllegalStateException if the simulation becomes inconsistent 
     * @see #propagateNodeChange(ImageSet, Edge)
     * @see #propagateEdgeChange(ImageSet, Node)
     */
    protected void propagateChanges() {
        while (hasNextChange()) {
            reporter.start(PROPAGATE_CHANGE);
            try {
                Element change = nextChange();
                if (change instanceof Node) {
                    propagateNodeChange(getNode((Node) change), latestTrigger());
                } else {
                    propagateEdgeChange(getEdge((Edge) change), latestTrigger());
                }
            } finally {
                reporter.stop();
            }
        }
    }
    /**
     * Propagates a change previously performed on the images of an edge
     * to ths images of its end points.
     * The <i>trigger</i> of the change to the edge (which is one of the
     * end points that was previously changed) is also passed in; this
     * end point may be skipped during propagation.
     * @param changed the image set of the changed edge
     * @param trigger the trigger of the change; may be <tt>null</tt>
     */
    protected void propagateEdgeChange(ImageSet<Edge> changed, Object trigger) {
        Edge change = changed.getKey();
        int arity = change.endCount();
        if (changed.isSingular()) {
            Edge changeImage = changed.getSingular();
            for (int i = 0; i < arity; i++) {
                Node changeEnd = change.end(i);
                if (changeEnd != trigger && !isDuplicate(change, i)) {
                    restrictNodeImages(changeEnd, changeImage.end(i), change);
                }
            }
        } else {
            for (int i = 0; i < arity; i++) {
                Node endKey = change.end(i);
                if (endKey != trigger && !isDuplicate(change, i)) {
                    final int endIndex = i;
                    Iterator<Node> endImageSet = new TransformIterator<Edge,Node>(changed) {
                        @Override
                        protected Node toOuter(Edge key) {
                            return key.end(endIndex);
                        }
                    };
                    restrictNodeImages(endKey, endImageSet, change);
                }
            }
        }
    }

    /**
     * Propagates a change previously performed on the images of a node
     * to ths images of its end points.
     * The <i>cause</i> of the change to the node (which is one of the
     * incident edges that was previously changed) is also passed in; this
     * edge may be skipped during propagation.
     * @param changed the changed edge images
     * @param trigger the trigger of the change; may be <tt>null</tt>
     */
    protected void propagateNodeChange(ImageSet<Node> changed, Object trigger) {
        Node changedKey = changed.getKey();
        for (Edge keyEdge: dom().edgeSet(changedKey)) {
            // it is an observation that it is more efficient to
            // propagate only if the key is already known
            if (keyEdge != trigger && nodeMap().containsKey(keyEdge)) {
                restrictEdgeImages(keyEdge, changedKey);
            }
        }
    }

    /**
     * Returns the current image according to the siulation, cast to an
     * {@link DefaultImageSet}.
     * @param key the element of which to return the image set
     * @return the image set of the given element
     */
    protected final ImageSet<? extends Element> getImageSet(Element key) {
    	if (key instanceof Node) {
    		return getNode((Node) key);
    	} else {
    		return getEdge((Edge) key);
    	}
    }

    /**
     * Retrieves an image set from this simulation, replacing it by a 
     * freshly created one if the current image set for the given key
     * does not have this simulation as an enclosing instance.
     * Thus we avoid sharing problems but also unnecessary cloning.
     * @param key the element for which the image set is requested
     * @return an image set for <tt>key</tt> that has this simulation
     * as an enclosing instance.
     */
    protected ImageSet<Node> getFreshImageSet(Node key) {
        ImageSet<Node> result = getNode(key);
        if (result != null && !result.isSingular() && result.getSimulation() != this) {
            backupSimulation.putNode(key, result);
            super.putNode(key, result = createImageSet(key, result));
        }
        return result;
    }

    /**
     * Retrieves an image set from this simulation, replacing it by a 
     * freshly created one if the current image set for the given key
     * does not have this simulation as an enclosing instance.
     * Thus we avoid sharing problems but also unnecessary cloning.
     * @param key the element for which the image set is requested
     * @return an image set for <tt>key</tt> that has this simulation
     * as an enclosing instance.
     */
    protected ImageSet<Edge> getFreshImageSet(Edge key) {
        ImageSet<Edge> result = getEdge(key);
        if (result != null && !result.isSingular() && result.getSimulation() != this) {
            backupSimulation.putEdge(key, result);
            super.putEdge(key, result = createImageSet(key, result));
        }
        return result;
    }
    
    /**
     * Establishes the current state of this simulation as the checkpoint,
     * to which the next invocation of {@link #restore()} 
     * will roll back. 
     */
    protected void backup() {
    	backupKeyIndex = keyIndex;
        backupConsistent = consistent;
        backupMultiImageCount = multiImageCount;
        backupSimulation = new GenericNodeEdgeHashMap<Node,ImageSet<Node>,Edge,ImageSet<Edge>>();
    }
    
    /**
     * Restores the backup state of this simulation, stored at the
     * previous invocation of {@link #backup()}. Unpredictable behaviour
     * may arise if {@link #backup()} has not been invoked.
     */
    protected void restore() {
        reporter.start(RESTORE);
        keyIndex = backupKeyIndex;
        putAll(backupSimulation);
        consistent = backupConsistent;
        multiImageCount = backupMultiImageCount;
        changes = new HashMap<Element,Object>();
        reporter.stop();
    }

    /**
     * Creates a new, singleton image set for a given node key and adds the (key,image set)-pair
     * to this simulation.
     * The new image set is returned as a result.
     * Also updates the multi-image count.
     * @param key the key to be inserted
     * @param image the (unique) image
     * @return the newly created image set for <tt>key</tt>
     * @ensure <tt>result.getSimgleImage() == image && get(key) == result</tt>
     */
    protected ImageSet<Node> putNode(Node key, Node image) {
        ImageSet<Node> newImageSet = createImageSet(key, image);
        ImageSet<Node> oldImageSet = super.putNode(key, newImageSet);
        assert oldImageSet == null : "Overwriting old images "+oldImageSet+" with new "+newImageSet;
        increaseMultiImageCount();
//        multiImageCount++;
        return newImageSet;
    }

    /**
     * Creates a new, singleton image set for a given edge key and adds the (key,image set)-pair
     * to this simulation.
     * The new image set is returned as a result.
     * Also updates the multi-image count.
     * @param key the key to be inserted
     * @param image the (unique) image
     * @return the newly created image set for <tt>key</tt>
     * @ensure <tt>result.getSimgleImage() == image && get(key) == result</tt>
     */
    protected ImageSet<Edge> putEdge(Edge key, Edge image) {
        ImageSet<Edge> newImageSet = createImageSet(key, image);
        ImageSet<Edge> oldImageSet = super.putEdge(key, newImageSet);
        assert oldImageSet == null : "Overwriting old images "+oldImageSet+" with new "+newImageSet;
        increaseMultiImageCount();
//        multiImageCount++;
        return newImageSet;
    }

    /**
     * Creates an image set for a given node key from a give set of nodes,
     * and adds the resulting (key, image set)-pair to this simulation.
     * Also updates the multi-image count.
     * The new image set is returned as a result.
     * Conenience method for <tt>putImageSet(key, imageSet.iterator())</tt>.
     * @param key the key to be inserted
     * @param imageSet the intended image set
     * @return the newly created image set for <tt>key</tt>
     * @require <tt>!containsKey(key)</tt>
     * @ensure <tt>result.equals(imageSet) && get(key) == result</tt>
     * @deprecated use {@link #putNode(Node, Iterator)} instead
     */
    @Deprecated
    protected final ImageSet<Node> putImageSet(Node key, Collection<Node> imageSet) {
        return putNode(key, imageSet.iterator());
    }

    /**
     * Creates an image set for a given edge key from a give set of edges,
     * and adds the resulting (key, image set)-pair to this simulation.
     * Also updates the multi-image count.
     * The new image set is returned as a result.
     * Conenience method for <tt>putImageSet(key, imageSet.iterator())</tt>.
     * @param key the key to be inserted
     * @param imageSet the intended image set
     * @return the newly created image set for <tt>key</tt>
     * @require <tt>!containsKey(key)</tt>
     * @ensure <tt>result.equals(imageSet) && get(key) == result</tt>
     * @deprecated use {@link #putEdge(Edge, Iterator)} instead
     */
    @Deprecated
    protected final ImageSet<Edge> putImageSet(Edge key, Collection<Edge> imageSet) {
        return putEdge(key, imageSet.iterator());
    }

    /**
     * Creates an image set for a given node key from an iterator,
     * and adds the resulting (key, image set)-pair to this simulation.
     * It is assumed that the key is not yet in the simulation.
     * The new image set is returned as a result.
     * @param key the key to be inserted
     * @param imageIter iterator over the initial image set
     * @return the newly created image set for <tt>key</tt>
     * @require <tt>!containsKey(key)</tt>
     */
    protected ImageSet<Node> putNode(Node key, Iterator<? extends Node> imageIter) {
        ImageSet<Node> newImageSet = createImageSet(key, imageIter);
        ImageSet<Node> oldImageSet = super.putNode(key, newImageSet);
        assert oldImageSet == null : "Overwriting old images "+oldImageSet+" with new "+newImageSet;
        increaseMultiImageCount();
//        multiImageCount++;
        return newImageSet;
    }

    /**
     * Creates an image set for a given key from an iterator,
     * and adds the resulting (key, image set)-pair to this simulation.
     * It is assumed that the key is not yet in the simulation.
     * The new image set is returned as a result.
     * @param key the key to be inserted
     * @param imageIter iterator over the initial image set
     * @return the newly created image set for <tt>key</tt>
     * @require <tt>!containsKey(key)</tt>
     */
    protected ImageSet<Edge> putEdge(Edge key, Iterator<? extends Edge> imageIter) {
        ImageSet<Edge> newImageSet = createImageSet(key, imageIter);
        ImageSet<Edge> oldImageSet = super.putEdge(key, newImageSet);
        assert oldImageSet == null : "Overwriting old images "+oldImageSet+" with new "+newImageSet;
        increaseMultiImageCount();
//        multiImageCount++;
        return newImageSet;
    }

    /**
     * Factory method for singleton image sets.
     * @param image the single image
     * @return an image set initialized at <tt>image</tt>
     * @see #createImageSet(Element, Collection)
     */
    protected <E extends Element> ImageSet<E> createImageSet(E key, E image) {
        // the image set itself makes sure the multiImageCount gets readjusted again
        // (through notifySingular())
        return new DefaultImageSet<E>(key, image);
    }

    /**
     * Factory method for image sets.
     * Convenienct method for <tt>createImageSet(key, imageSet.iterator())</tt>.
     * @param imageSet the initial set of images
     * @return an image set cloning <tt>imageSet</tt>
     * @throws IllegalStateException if <tt>imageSet</tt> is empty 
     * @see #createImageSet(Element, Collection)
     */
    final protected <E extends Element> ImageSet<E> createImageSet(E key, Collection<E> imageSet) {
        return createImageSet(key, imageSet.iterator());
    }

    /**
     * Factory method for image sets.
     * @param imageIter iterator over the initial set of images
     * @return an image set containing the elements returned by <tt>imageIter</tt>
     * @throws IllegalStateException if <tt>imageSet</tt> is empty 
     * @see #createImageSet(Element, Element)
     */
    protected <E extends Element> ImageSet<E> createImageSet(E key, Iterator<? extends E> imageIter) {
        return new DefaultImageSet<E>(key, imageIter);
    }
    
    /**
     * Sets the simulation to inconsistent.
     * @see #isConsistent()
     */
    protected void notifyInconsistent() {
        consistent = false;
    }
    
    /**
     * Returns an list of the domain elements in optimal order
     * to speed up stabilisation.
     * Callback method used in initialization and refinement.
     * @return the list of elements to be matched in order of matching
     */
    protected List<Element> getMatchingSchedule() {
    	if (keySchedule == null) {
    		keySchedule = computeMatchingSchedule();
    	}
    	return keySchedule;
    }

    /**
     * Computes the key schedule for this simulation.
     * This implementation returns the edges followed by the nodes.
     * @return a fresh key schedule for this simulation
     * @see #getMatchingSchedule()
     */
    protected List<Element> computeMatchingSchedule() {
    	List<Element> result = new ArrayList<Element>();
    	result.addAll(dom().edgeSet());
    	result.addAll(dom().nodeSet());
    	return result;
    }
    
    /**
     * Tests if a given edge end is a duplicate of an end with a smaller index.
     * @param edge the edge to be tested
     * @param i the end index for which duplication is tested
     * @return <tt>true</tt> if<tt>edge.end(i))</tt> is a duplicate of some
     * <tt>edge.end(j))</tt> for <tt>j < i</tt>
     */
    private boolean isDuplicate(Edge edge, int i) {
        Node end = edge.end(i);
        for (int j = 0; j < i; j++) {
            if (end == edge.end(j)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Increments the <code>multiImageCount</code>-field.
     */
    protected void increaseMultiImageCount() {
    	multiImageCount++;
    }

    /**
     * Decrements the <code>multiImageCount</code>-field.
     */
    protected void decreaseMultiImageCount() {
    	multiImageCount--;
    }

    /**
     * The underlying morphism of this simulation.
     */
    protected final Morphism morph;
    /**
     * Map of changes to causes (where the causes may be <tt>null</tt>).
     */
    private Map<Element,Object> changes = new HashMap<Element,Object>();
    /**
     * Entry latest retrieved from {@link #changes} by {@link #nextChange()}.
     * Used to determine the return value of {@link #latestTrigger()}.
     */
    private Map.Entry<Element,Object> changeEntry;
    /**
     * Signals if this simulation is still consistent.
     */
    private boolean consistent = true;
    /**
     * The number of keys with multiple image sets.
     */
    private int multiImageCount = 0;
    /**
	 * A list of domain elements, in the order in which they are to be matched.
	 */
	private List<Element> keySchedule;
	/** 
	 * The index in {@link #keySchedule} of the first element that
	 * we have not yet simulated.
	 */
	private int keyIndex;
	/**
	 * A backup value for {@link #keyIndex}.
	 */
	private int backupKeyIndex;
	/**
     * Checked-in flag if this simulation is still consistent.
     * @see #backup()
     */
    private boolean backupConsistent = true;
    /**
     * The checked-in number of keys with multiple image sets.
     * @see #backup()
     */
    private int backupMultiImageCount = 0;
    /**
     * A map from domain elements to old image sets, whose enclosing simulation is not this one and which where therefore replaced in the course of {@link #getFreshImageSet(Node)} or {@link #getFreshImageSet(Edge)}. Used in {@link #restore()} .
     */
    private GenericNodeEdgeMap<Node,ImageSet<Node>,Edge,ImageSet<Edge>> backupSimulation = new GenericNodeEdgeHashMap<Node,ImageSet<Node>,Edge,ImageSet<Edge>>();
    /** 
     * Flag to indicate if we are in the initialization phase.
     * This may influence the store/propagate or key interation decisions.
     */
    private boolean initializing;
    
    // ------------------------- switches for debugging purposes ---------------------
    /**
     * Init debug flag
     */
    static private final boolean INIT_DEBUG = false;
    /**
     * Refine debug flag
     */
    static private final boolean REFINE_DEBUG = false;

    /**
     * Reporter
     */
    static public final Reporter reporter = Reporter.register(Simulation.class);
    /**
     * Registration id for new method
     */
    static protected final int NEW_TOTAL = reporter.newMethod("new");
    /**
     * Registration id for initImages method
     */
    static protected final int INIT_IMAGES = reporter.newMethod("initImages");
    /**
     * Registration id for init-element method
     */
    static protected final int INIT_ELEMENT = reporter.newMethod("init - element images");
    /**
     * Registration id for init morphism method
     */
    static protected final int INIT_MORPHISM = reporter.newMethod("init - morphism images");
    /**
     * Registration id for select method
     */
    static protected final int SELECT = reporter.newMethod("select(Element, Element)");
    /**
     * Registration id for restrict node images method
     */
    static protected final int RESTRICT_NODE_IMAGES = reporter.newMethod("restrictNodeImages");
    /**
     * Registration id for restrict edge images method
     */
    static protected final int RESTRICT_EDGE_IMAGES = reporter.newMethod("restrictEdgeImages");
    /**
     * Registration id for clone method
     */
    static protected final int CLONE = reporter.newMethod("clone()");
    /**
     * Registration id for restore method
     */
    static protected final int RESTORE = reporter.newMethod("restore()");
    /**
     * Registration id for propagate change method
     */
    static protected final int PROPAGATE_CHANGE = reporter.newMethod("propgate change");
    /**
     * Registration id for get refinement method
     */
    static public final int GET_REFINEMENT = reporter.newMethod("getRefinement()");
    /**
     * Registration id for get refinement-set method
     */
    static public final int GET_REFINEMENT_SET = reporter.newMethod("getRefinementSet()");
    /**
     * Registration id for get refinement-iter method
     */
    static public final int GET_REFINEMENT_ITER = reporter.newMethod("getRefinementIter()");
}
