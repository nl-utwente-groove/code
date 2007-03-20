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
 * $Id: AttributeSimulation.java,v 1.1.1.2 2007-03-20 10:42:40 kastenberg Exp $
 */

package groove.graph;

import groove.algebra.Constant;
import groove.algebra.Operation;
import groove.algebra.Variable;
import groove.graph.algebra.AlgebraConstants;
import groove.graph.algebra.AlgebraEdge;
import groove.graph.algebra.AlgebraGraph;
import groove.graph.algebra.AttributeEdge;
import groove.graph.algebra.ProductEdge;
import groove.graph.algebra.ProductNode;
import groove.graph.algebra.ValueNode;
import groove.trans.Matching;
import groove.trans.MatchingSimulation;
import groove.trans.RuleFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Simulation that also takes attributed graphs into account.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:40 $
 */
public class AttributeSimulation extends MatchingSimulation {

    /**
     * Creates an attribute simulation.
     * @param mapping the current matching
     * @param ruleFactory the factory used for creating related things
     */
    public AttributeSimulation(Matching mapping, RuleFactory ruleFactory) {
		super(mapping, ruleFactory);
	}

    /**
     * This implementation takes special measures in case the <code>key</code> is
     * a {@link ProductNode} or a {@link ValueNode}, by calling the dedicated methods
     * {@link #getProductNodeMatches(ProductNode)} and {@link #getValueNodeMatches(ValueNode)},
     *  respectively.
     */
    protected Iterator<? extends Node> getNodeMatches(Node key) {
        // if the key is an operation-node
        if (key instanceof ProductNode) {
            return getProductNodeMatches((ProductNode) key);
        } else if (key instanceof ValueNode) {
            return getValueNodeMatches((ValueNode) key);
        } else {
            return super.getNodeMatches(key);
        }
    }

    /**
     * Checks whether the given <code>key</code> fulfills certain required properties.
     * If the <code>key</code> is a node representing a variable, than everything is
     * fine. When the <code>key</code> represents a specific algebraic data value,
     * then the image must represent exactly the same data value, otherwise the
     * matching is not consistent. We then return an empty image-set, i.e. we
     * throw an {@link IllegalStateException}. 
     * @param key the {@link groove.graph.algebra.ValueNode} for which to check
     * whether restriction to the given image still results in a consistent
     * simulation
     * @param newImage the candidate image
     * @param trigger the trigger element
     */
    protected void restrictValueNodeImages(ValueNode key, ValueNode newImage, Edge trigger) {
        Constant keyConstant = key.getConstant();
        Constant newImageConstant = newImage.getConstant();
        // if we have to do with a variable, we can just continue the refinement
        // by taking the given image as a correct image for this operation node
        // if this node explicitely specifies a constant, this has to match with
        // the constant represented by the image
        if (keyConstant instanceof Variable)
            return;
        else {
            if (!(keyConstant.equals(newImageConstant))) {
                throw emptyImageSet;
            }
        }
        // the key represents an operation with arity > 0
        // we can directly notify that the given image will be the new image of this node
    }

    /**
     * This method searches for images for a given {@link groove.graph.algebra.ProductNode}.
     * This is done by first taking the edge which determines the operation to be
     * applied on the operands, since then we know for how many operands to look.
     * The operands-search is performed by {@link #createProductNodeImageSet(ProductNode, int, int, List, Set)}.
     * Thereafter, we have to create a set containing candidate images as
     * being all combinations of images for all the nodes that represent the
     * operands.
     * @param key the {@link groove.graph.algebra.ProductNode} for which to search
     * for candidate images
     * @return an iterator of the set of candidate images
     */
    protected Iterator<? extends Node> getProductNodeMatches(ProductNode key) {
        Collection<? extends Node> oldImages = getNode(key);
        if (oldImages == null) {
            // create the set of all possibles images of the product-node
            ProductEdge productEdge = null;
            Iterator<? extends Edge> outEdgeIter = morph.dom().outEdgeSet(key).iterator();
            while (productEdge == null && outEdgeIter.hasNext()) {
                Edge nextEdge = outEdgeIter.next();
                if (nextEdge instanceof ProductEdge) {
                    productEdge = (ProductEdge) nextEdge;
                }
            }
            int arity = productEdge.getOperation().arity();
            Set<ProductNode> imageSet = new HashSet<ProductNode>();
            createProductNodeImageSet(key, arity, 0, new ArrayList<Constant>(), imageSet);
            // the following is erroneous: putting the images in the simulation
            // is not the responsibility of this method
            // putImageSet(key, imageSet);
            createProductNodeInstances(key, imageSet);
            return imageSet.iterator();
        } else {
            return oldImages.iterator();
        }
    }

    /**
     * This method creates all candidate images for the given
     * {@link groove.graph.algebra.ProductNode} by traversing all images of
     * target-nodes of its adjacent edges, i.e. the target nodes of the
     * argument-edges pointing to the operands on which the operation will
     * eventually be applied. This method calls itself recursively for by
     * iterating over the argument-edges in increasing order (controlled by
     * the <code>argIndex</code> input parameter).
     * 
     * @param key the {@link groove.graph.algebra.ProductNode} for which to
     * determine the image-set
     * @param arity the arity of the operation that will eventually be applied
     * @param argIndex the index of the next argument to look for
     * @param args the list of arguments with index < argIndex - 1
     * @param imageSet the set which will eventually contain all candidate images
     */
    protected void createProductNodeImageSet(ProductNode key, int arity, int argIndex, List<Constant> args, Set<ProductNode> imageSet) {
        // get all edges with labelled "arg" suffixed with the right index
        List<Constant> argList = new LinkedList<Constant>();
        argList.addAll(args);

        // get all edges with labelled "arg" suffixed with the right index and
        // iterate over all those edges which have the given key as its source node
        Label searchLabel = DefaultLabel.createLabel(AlgebraConstants.ARGUMENT_PREFIX + argIndex);
        Collection<? extends Edge> argEdgeCol = morph.dom().labelEdgeSet(BinaryEdge.END_COUNT, searchLabel);
        for (Edge nextArgEdge: argEdgeCol) {
            // if this edge has the given key as its source node 
            if (nextArgEdge.source().equals(key)) {
                ValueNode target = (ValueNode) nextArgEdge.end(Edge.TARGET_INDEX);
                // iterate over the imageSet of the target-node
                for (Node nextValueNode: getNode(target)) {
                    Constant nextArg = ((ValueNode) nextValueNode).getConstant();
                    // if not last argument, call this method recursively but
                    // with the argument-index increased by 1
                    if (argIndex < arity - 1) {
                        argList.add(nextArg);
                        createProductNodeImageSet(key, arity, (argIndex + 1), argList, imageSet);
                        argList.remove(argList.size() - 1);
                    }
                    // if last argument, add that last argument and create an
                    // ProductNode with the list of arguments as its components
                    else {
                        ProductNode image = new ProductNode();
                        for (int i = 0; i < argList.size(); i++) {
                            Constant argument = argList.get(i);
                            image.addOperand(argument);
                        }
                        image.addOperand(nextArg);
                        imageSet.add(image);
                    }
                }
            }
        }
    }

    /**
     * This method performs the creation of image-edges for each of the adjacent
     * edges of the original key-node. That is, for each candidate ProductNode image
     * it creates the edges that then should be matched in order to make the
     * simulation complete.
     *  
     * @param key the original key for which we were looking for matching nodes
     * @param imageSet the set containing candidate images for the key
     */
    protected void createProductNodeInstances(ProductNode key, Set<ProductNode> imageSet) {
        AlgebraGraph algebraGraph = AlgebraGraph.getInstance();
        Map<Node,Set<Node>> nodeMap = new HashMap<Node,Set<Node>>();
        Map<Edge,Set<Edge>> edgeMap = new HashMap<Edge,Set<Edge>>();
//        Map<Element, Set<Element>> keyToImageMap = new HashMap<Element, Set<Element>>();
        // iterate over the candidate images
        for (ProductNode nextImage: imageSet) {
        	// iterate over the adjacent edges of the original key-node
        	for (Edge nextEdge: morph.dom().outEdgeSet(key)) {
                Edge imageEdge;
                // if the edge contains information over which algebraic operation to be applied
                if (nextEdge instanceof ProductEdge) {
                    imageEdge = getProductEdgeImage((ProductEdge) nextEdge, nextImage);
                    ValueNode imageTarget = ((ProductEdge) imageEdge).target();
                    Constant result = imageTarget.getConstant();
                    ValueNode resultKey = ((ProductEdge) nextEdge).target();
                    Constant actualConstant = resultKey.getConstant();
                    // if the value of the result-node is undefined, the node representing the result
                    // of applying the operation will be its image
                    if (actualConstant instanceof Variable || actualConstant.equals(result)) {
                        // add this image to the current image-set
                        addImage(nodeMap, resultKey, imageTarget);
                    }
                    // in case, it is specified which value the result-node SHOULD represent, this
                    // needs to match with the result of applying the operation
                    // if this does not match, the rule is not applicable
                    else {
                        notifyInconsistent();
                    }
                }
                // otherwise we are dealing with a edge pointing to one of its
                // argument nodes; in this case we have to create a new edge between
                // the image-nodes of both the current candidate and the image of the
                // target-node of the current edge
                else {
                    int argIndex = AlgebraConstants.isArgumentLabel(nextEdge.label());
                    Constant operation = nextImage.getOperand(argIndex);
                    ValueNode imageTarget = algebraGraph.getValueNode(operation);
                    imageEdge = new AlgebraEdge(nextImage, nextEdge.label(), imageTarget);
                }
                addImage(edgeMap, nextEdge, imageEdge);
            }
        }

        // for the nodes we can not do a simple call to putNode with the
        // needed arguments, since some attribute-nodes may already be
        // matched
        for (Map.Entry<Node, Set<Node>> elementEntry: nodeMap.entrySet()) {
        	Node nodeKey = elementEntry.getKey();
        	Set<? extends Element> currentImageSet = getImageSet(nodeKey);
        	// if this node does not yet have an imageset we
        	// we can call putNode to do all the things needed
        	if (currentImageSet == null) {
            	putNode(elementEntry.getKey(), elementEntry.getValue().iterator());
        	} else {
        		// if, however, there exists already an imageset for this node
        		// we need to be carefull
        		// in the case the current imageset and the new image are
        		// different, we only need to restrict to current imageset
        		Set newImageSet = elementEntry.getValue();
        		if (!(currentImageSet.containsAll(newImageSet) && currentImageSet.size() == newImageSet.size())) {
        			restrictNodeImages(nodeKey, newImageSet.iterator(), null);
        		}
        	}
        }
        for (Map.Entry<Edge, Set<Edge>> elementEntry: edgeMap.entrySet()) {
        	putEdge(elementEntry.getKey(), elementEntry.getValue().iterator());
        }
    }

    /**
     * Creates a new {@link groove.graph.algebra.ProductEdge} having the given
     * {@link groove.graph.algebra.ProductNode} <code>source</code> as its
     * source. The target of this edge is determined by applying the operation
     * provided by the {@link groove.graph.algebra.ProductEdge} <code>key</code>
     * on the operands of the <code>source</code>. 
     * 
     * @param key the edge providing the operation to be applied on the operands
     * contained in the given {@link groove.graph.algebra.ProductNode} <code>source</code> 
     * @param source the {@link groove.graph.algebra.ProductNode} providing the
     * arguments from which to determine the resulting value
     * @return a fresh {@link groove.graph.algebra.ProductEdge}-instance between
     * the given {@link groove.graph.algebra.ProductNode} and the {@link groove.graph.algebra.ValueNode}
     * representing the algebraic data value obtained by applying the algebraic
     * operation (provided by <code>key</code>) on the arguments (provided by
     * <code>source</code> 
     */
    protected ProductEdge getProductEdgeImage(ProductEdge key, ProductNode source) {
        AlgebraGraph algebraGraph = AlgebraGraph.getInstance();
        Operation operation = key.getOperation();
        List<Constant> operands = source.getOperands();
        Constant result = operation.apply(operands);
        ValueNode target = algebraGraph.getValueNode(result);
        return new ProductEdge(source, target, operation);
    }

    /**
     * Get the matching nodes for OperationNode for which the representing
     * operation in specified explicitely.
     * 
     * @param key the node for which to find matching nodes.
     * @return iterator over the set of matching nodes.
     */
    protected Iterator<? extends Node> getValueNodeMatches(ValueNode key) {
        Collection<? extends Node> oldImages = getNode(key);
        if (oldImages == null) {
            // for a variable-OperationNode (i.e. the operation it represents is undefined) we may asume
            // that it is already matched through matching the attribute-edge which connects it to the
            // graph part
            Node nodeImage = null;
            Constant constant = key.getConstant();
            // when we have to do with a specified operation or constant
            if (!(constant instanceof Variable)) {
                // in case of an operation with arity 0 (i.e. a constant), there can only be one unique
                // node representing this constant, namely the current node
                nodeImage = key;
            }
            return Collections.singleton(nodeImage).iterator();
        }
        else
            return oldImages.iterator();
    }

    /* (non-Javadoc)
     * @see groove.graph.DefaultSimulation#putImageSet(groove.graph.Element, java.util.Iterator)
     */
    protected ImageSet<Edge> putEdge(Edge key, Iterator<? extends Edge> imageIter) {
        // CODE: when looking for matches of attributed graphs, in some cases we
        // have to replace an already existing imageset by a larger one. For example,
        // when a transformation rule which changes attribute value matches more
        // than once. The image sets for the algebra-edges are then created incrementally.
        // Therefore, the assert-statement in the super-method is violated.
    	if (key instanceof AttributeEdge && key.source() instanceof ProductNode) {
            ImageSet<Edge> newImageSet = createImageSet(key, imageIter);
            super.putEdge(key, newImageSet);
            increaseMultiImageCount();
            return newImageSet;
    	} else {
    		return super.putEdge(key, imageIter);
    	}
    }

    /**
     * Adds the given image to the set of images stored in the given map for the given key.
     * @param keyToImageMap a mapping from graph elements to sets of graph elements
     * @param key the graph element for which the given <code>image</code> needs
     * to be added to the image-set
     * @param image the grahp element to be added to the image-set of the
     * <code>key</code> graph element
     * @return the old image set of the given <code>key</code> graph element
     */
    private <E extends Element> Set<E> addImage(Map<E, Set<E>> keyToImageMap, E key, E image) {
    	Set<E> oldImages = keyToImageMap.get(key);
    	if (oldImages == null) {
    		Set<E> newImages = new HashSet<E>();
    		newImages.add(image);
    		keyToImageMap.put(key, newImages);
    		return newImages;
    	}
    	else {
    		oldImages.add(image);
    		keyToImageMap.put(key, oldImages);
    		return oldImages;
    	}
    }
}
