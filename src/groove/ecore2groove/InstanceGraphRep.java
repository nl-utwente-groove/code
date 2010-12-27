/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id$
 */
package groove.ecore2groove;

import groove.graph.DefaultGraph;
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.GraphInfo;
import groove.graph.LabelKind;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Given a ModelHandler with an Ecore metamodel and also an instance model
 * loaded, this class generates the graph representation of the instance
 * model. 
 * @author Stefan Teijgeler
 */
public class InstanceGraphRep {

    private ModelHandler mh;

    private DefaultGraph ig = new DefaultGraph();

    private Map<EObject,DefaultNode> iClassToNodeMap =
        new HashMap<EObject,DefaultNode>();
    private Map<Triple<EObject,EStructuralFeature,EObject>,Stack<DefaultNode>> iReferenceToNodeMap =
        new HashMap<Triple<EObject,EStructuralFeature,EObject>,Stack<DefaultNode>>();
    private Set<EObject> iClassReferencesDone = new HashSet<EObject>();

    /**
     * Constructor method, given a ModelHandler with an Ecore metamodel and 
     * instance model loaded, creates an instance graph representation.
     * @param m the ModelHandler
     */
    public InstanceGraphRep(ModelHandler m) {
        this.mh = m;
        // First add instances of of classes to graph, then features
        addClasses(this.mh.getiClasses());
        addStructuralFeatures(this.mh.getiClasses());
    }

    /**
     * Add all representations for all EClass instances in the vector to
     * the instance graph.
     * @param iClasses the instances of EClasses
     */
    private void addClasses(Vector<EObject> iClasses) {
        for (EObject iClass : iClasses) {
            // Add new labeled  node to ig 
            String labelText = GraphLabels.getLabel(iClass.eClass());

            DefaultNode node = this.ig.addNode();
            this.ig.addEdge(node, labelText, node);

            // If this instance EClass is the root element, add the root flag
            if (iClass.eContainingFeature() == null) {
                this.ig.addEdge(node, "flag:root", node);
            }

            // Add map of EClass to node representing it
            this.iClassToNodeMap.put(iClass, node);
        }
    }

    /**
     * Add representations for features to the instance graph for all the 
     * instances of EClasses of the instance model.
     * @param iClasses the instances of EClasses
     * @require EClass instances must have been added to the graph 
     * representation already
     */
    @SuppressWarnings("unchecked")
    private void addStructuralFeatures(Vector<EObject> iClasses) {
        for (EObject iClass : iClasses) {
            for (EStructuralFeature feature : iClass.eClass().getEAllStructuralFeatures()) {
                if (feature.eClass().getName().equals("EReference")) {

                    if (iClass.eGet(feature) != null) {
                        // When there are multiple values of a feature,
                        // an EcoreEList contains them

                        if (feature.isMany()) {
                            EList<EObject> targets =
                                (EList<EObject>) iClass.eGet(feature, true);
                            boolean ordered = feature.isOrdered();
                            DefaultNode previous = null;
                            DefaultNode last = null;

                            for (EObject target : targets) {

                                // only add reference if target exists, not 
                                // exist at this point can happen with factory
                                if (this.iClassToNodeMap.containsKey(target)) {
                                    // Add node representing the EReference to ig
                                    last =
                                        addReference(iClass, feature, target);
                                    addOpposite(iClass, feature, target);

                                    // Add next edge if references are ordered
                                    if (ordered && previous != null) {
                                        this.ig.addEdge(previous, "next", last);
                                    }
                                    previous = last;
                                }

                            }

                            // When not an EcoreEList and also not null,
                            // then there is a single value which is an EObject
                        } else {
                            EObject target =
                                (EObject) iClass.eGet(feature, true);
                            // only add reference if target exists, not 
                            // exist at this point can happen with factory
                            if (this.iClassToNodeMap.containsKey(target)) {
                                addReference(iClass, feature, target);
                                addOpposite(iClass, feature, target);
                            }

                        }
                    }

                } else if (feature.eClass().getName().equals("EAttribute")) {
                    if (iClass.eGet(feature) != null) {
                        if (feature.isMany()) {
                            EList<Object> targets =
                                (EList<Object>) iClass.eGet(feature, true);
                            boolean ordered = feature.isOrdered();
                            DefaultNode previous = null;
                            DefaultNode last = null;

                            for (Object iTarget : targets) {

                                // Add node representing the EReference
                                last = addAttribute(iClass, feature, iTarget);

                                // Add next edge if references are ordered
                                if (ordered && previous != null) {
                                    DefaultLabel label =
                                        DefaultLabel.createLabel("next");
                                    this.ig.addEdge(previous, label, last);
                                }
                                previous = last;
                            }
                        } else {
                            Object iTarget = iClass.eGet(feature, true);
                            addAttribute(iClass, feature, iTarget);
                        }
                    }
                }
            }
            this.iClassReferencesDone.add(iClass);
        }
    }

    /**
     * Check if the EReference feature from iClass to target has an opposite
     * EReference from target to iClass. If so, add opposite edges between
     * the nodes that represent these EReference instances
     * @param iClass the source EClass instance of feature
     * @param feature the EReference
     * @param target the target EClass instance of feature
     * @require feature instanceof EReference, EClass instances must be
     * represented in the graph representation already
     */
    @SuppressWarnings("unchecked")
    private void addOpposite(EObject iClass, EStructuralFeature feature,
            EObject target) {
        EReference oppositeRef = ((EReference) feature).getEOpposite();

        if (oppositeRef != null) {
            if (oppositeRef.isMany()) {
                EList<EObject> opposites =
                    (EList<EObject>) target.eGet(oppositeRef);
                for (EObject opposite : opposites) {
                    if (opposite.equals(iClass)
                        && this.iReferenceToNodeMap.containsKey(Triple.create(
                            target, oppositeRef, opposite))
                        && !this.iReferenceToNodeMap.get(
                            Triple.create(target, oppositeRef, opposite)).isEmpty()) {
                        Stack<DefaultNode> nodeStack1 =
                            this.iReferenceToNodeMap.get(Triple.create(
                                opposite, feature, target));
                        DefaultNode node1 = nodeStack1.pop();
                        Stack<DefaultNode> nodeStack2 =
                            this.iReferenceToNodeMap.get(Triple.create(target,
                                oppositeRef, opposite));
                        DefaultNode node2 = nodeStack2.pop();
                        this.ig.addEdge(node1, "opposite", node2);
                        this.ig.addEdge(node2, "opposite", node1);
                    }
                }
            } else {
                EObject opposite = ((EReference) feature).getEOpposite();
                if (opposite.equals(iClass)
                    && this.iReferenceToNodeMap.containsKey(Triple.create(
                        target, oppositeRef, opposite))
                    && !this.iReferenceToNodeMap.get(
                        Triple.create(target, oppositeRef, opposite)).isEmpty()) {
                    Stack<DefaultNode> nodeStack1 =
                        this.iReferenceToNodeMap.get(Triple.create(opposite,
                            feature, target));
                    DefaultNode node1 = nodeStack1.pop();
                    Stack<DefaultNode> nodeStack2 =
                        this.iReferenceToNodeMap.get(Triple.create(target,
                            oppositeRef, opposite));
                    DefaultNode node2 = nodeStack2.pop();
                    this.ig.addEdge(node1, "opposite", node2);
                    this.ig.addEdge(node2, "opposite", node1);
                }
            }
        }

    }

    /**
     * Adds a graph representation for an instance of the EAttribute feature 
     * with a value of target. The EAttribute is a feature of source.
     * @param source the EClass instance
     * @param feature the EAttribute
     * @param target the value 
     * @return The Node that was added to the instance graph
     * @require feature instanceof EAttribute, EClass instances must be
     * represented in the instance graph already.
     */
    private DefaultNode addAttribute(EObject source,
            EStructuralFeature feature, Object target) {
        String attributeLabel = GraphLabels.getLabel(feature);
        EDataType attributeType = ((EAttribute) feature).getEAttributeType();
        String datatypeLabel = GraphLabels.getLabel(attributeType, target);

        // Create and add a node to represent the EAttribute itself
        DefaultNode attributeNode = this.ig.addNode();
        this.ig.addEdge(attributeNode, attributeLabel, attributeNode);

        // Create and add an edge from the container EClass to the EAttribute
        DefaultNode sourceNode = this.iClassToNodeMap.get(source);
        this.ig.addEdge(sourceNode, feature.getName(), attributeNode);

        if (!datatypeLabel.isEmpty()) {
            // Create and add a node to represent that datatype value
            DefaultNode datatypeNode = this.ig.addNode();
            this.ig.addEdge(datatypeNode, datatypeLabel, datatypeNode);

            // Create and add a val edge to the value
            this.ig.addEdge(attributeNode, "val", datatypeNode);

            // When the value is an EEnum, the flag for the value needs to be
            // added as well
            if (attributeType.eClass().getName().equals("EEnum")) {
                String flagLabel =
                    LabelKind.FLAG.getPrefix() + target.toString();
                this.ig.addEdge(datatypeNode, flagLabel, datatypeNode);
            }
        }

        return attributeNode;
    }

    /**
     * Adds a graph representation for an instance of an EReference from source
     * to target, which must both be EClass instances.
     * @param source the source EClass instance
     * @param feature the EReference
     * @param target the target EClas instance
     * @return the Node that was added to the instance graph
     * @require feature instanceof EReference, EClass instances must be
     * represented in the instance graph already, source and target must be
     * EClass instances.
     */
    private DefaultNode addReference(EObject source,
            EStructuralFeature feature, EObject target) {
        String labelText = GraphLabels.getLabel(feature);

        // Create node to represent the reference and add it to ig
        DefaultNode node = this.ig.addNode();
        this.ig.addEdge(node, labelText, node);

        // If the reference is a containment reference, add flag:containment
        if (((EReference) feature).isContainment()) {
            DefaultLabel contLabel =
                DefaultLabel.createLabel("flag:containment");
            this.ig.addEdge(node, contLabel, node);
        }

        // Create and add an edge from the source of the EReference to the 
        // EReference node
        DefaultNode sourceNode = this.iClassToNodeMap.get(source);
        this.ig.addEdge(sourceNode, feature.getName(), node);

        // Create and add an edge from the EReference node to the target of the
        // EReference
        DefaultNode targetNode = this.iClassToNodeMap.get(target);
        this.ig.addEdge(node, "val", targetNode);

        // Either add the node to the set of nodes that represent the
        // iReference, or add a new set to the map
        Stack<DefaultNode> nodeStack =
            this.iReferenceToNodeMap.get(Triple.create(source, feature, target));

        if (nodeStack != null) {
            nodeStack.push(node);
        } else {
            nodeStack = new Stack<DefaultNode>();
            nodeStack.push(node);
            this.iReferenceToNodeMap.put(
                Triple.create(source, feature, target), nodeStack);

        }

        return node;
    }

    /**
     * Get method for the graph that represents the instance model
     * @return the instance graph
     */
    public DefaultGraph getInstanceGraph() {
        GraphInfo.setHostRole(this.ig);
        return this.ig;
    }

}
