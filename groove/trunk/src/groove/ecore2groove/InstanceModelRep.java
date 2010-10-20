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

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.view.aspect.AspectGraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Given an Ecore model and a graph that represents an instance model,
 * this class creates the instance model this graph represents.
 * @author Stefan Teijgeler
 *
 */
public class InstanceModelRep {

    private Graph instanceGraph;
    private Resource instanceModel;

    private Set<Edge> classEdgeSet;
    private Set<Edge> referenceEdgeSet;
    private Set<Edge> containmentReferenceEdgeSet;
    private Set<Edge> attributeEdgeSet;

    private Edge rootEdge;

    private Map<Node,Node> nextNode;

    private Map<String,EClass> labelToClass;
    private Map<String,EReference> labelToReference;
    private Map<String,EReference> labelToContainmentReference;
    private Map<String,EAttribute> labelToAttribute;
    private Map<String,EEnum> labelToEnum; //delete
    private Map<String,EEnumLiteral> labelToLiteral; //delete

    private Map<Node,Node> referenceToVal;
    private Map<Node,String> attributeToVal;
    private Map<Node,Edge> featureToType;

    private Map<Node,EObject> nodeToObject;

    /**
     * Constructor class, given a ModelHandler and an AspectGraph, creates
     * the instance model that represents the graph
     * @param mh The ModelHandler with a Ecore model loaded
     * @param ig The instance graph to create a model for
     * @require ig must represent a model that is an instance of the Ecore
     * model loaded in mh, no constraints may be violated.
     */
    public InstanceModelRep(ModelHandler mh, AspectGraph ig) {
        this.instanceGraph = ig.toPlainGraph();

        this.classEdgeSet = new HashSet<Edge>();
        this.referenceEdgeSet = new HashSet<Edge>();
        this.containmentReferenceEdgeSet = new HashSet<Edge>();
        this.attributeEdgeSet = new HashSet<Edge>();

        this.rootEdge = null;

        this.nextNode = new HashMap<Node,Node>();

        this.labelToClass = new HashMap<String,EClass>();
        this.labelToReference = new HashMap<String,EReference>();
        this.labelToContainmentReference = new HashMap<String,EReference>();
        this.labelToAttribute = new HashMap<String,EAttribute>();
        this.labelToEnum = new HashMap<String,EEnum>(); //delete
        this.labelToLiteral = new HashMap<String,EEnumLiteral>(); //delete

        this.referenceToVal = new HashMap<Node,Node>();
        this.attributeToVal = new HashMap<Node,String>();
        this.featureToType = new HashMap<Node,Edge>();

        this.nodeToObject = new HashMap<Node,EObject>();

        // Fill maps from label text to the EObjects from the Ecore model
        for (EClass eClass : mh.getEClasses()) {
            String classLabel = GraphLabels.getLabel(eClass);
            this.labelToClass.put(classLabel, eClass);
        }

        for (EReference eReference : mh.getEReferences()) {
            String referenceLabel = GraphLabels.getLabel(eReference);
            if (eReference.isContainment()) {
                this.labelToContainmentReference.put(referenceLabel, eReference);
            } else {
                this.labelToReference.put(referenceLabel, eReference);
            }
        }

        for (EAttribute eAttribute : mh.getEAttributes()) {
            String attributeLabel = GraphLabels.getLabel(eAttribute);
            this.labelToAttribute.put(attributeLabel, eAttribute);
        }

        for (EEnum eEnum : mh.getEEnums()) {
            String enumLabel = GraphLabels.getLabel(eEnum);
            this.labelToEnum.put(enumLabel, eEnum);
        }

        for (EEnumLiteral eEnumLiteral : mh.getEEnumLiterals()) {
            String literalLabel = GraphLabels.getLabel(eEnumLiteral);
            this.labelToLiteral.put(literalLabel, eEnumLiteral);
        }

        // Fill edge sets with self edges of nodes that represent this type
        for (Edge edge : this.instanceGraph.edgeSet()) {
            if (edge.source() == edge.opposite()) {
                if (this.labelToClass.containsKey(edge.label().text())) {
                    this.classEdgeSet.add(edge);
                    this.featureToType.put(edge.source(), edge);
                } else if (this.labelToReference.containsKey(edge.label().text())) {
                    this.referenceEdgeSet.add(edge);
                    this.featureToType.put(edge.source(), edge);
                    this.referenceToVal.put(edge.source(),
                        getValue(edge.source()));
                } else if (this.labelToContainmentReference.containsKey(edge.label().text())) {
                    this.containmentReferenceEdgeSet.add(edge);
                    this.featureToType.put(edge.source(), edge);
                    this.referenceToVal.put(edge.source(),
                        getValue(edge.source()));
                } else if (this.labelToAttribute.containsKey(edge.label().text())) {
                    this.attributeEdgeSet.add(edge);
                    this.featureToType.put(edge.source(), edge);
                    Node attrVal = getValue(edge.source());
                    if (attrVal != null) {
                        for (Edge outEdge : this.instanceGraph.outEdgeSet(attrVal)) {
                            String outLabelText = outEdge.label().text();
                            if (outLabelText.startsWith("flag:")
                                || outLabelText.startsWith("int:")
                                || outLabelText.startsWith("bool:")
                                || outLabelText.startsWith("real:")) {
                                this.attributeToVal.put(
                                    edge.source(),
                                    outLabelText.substring(outLabelText.indexOf(':') + 1));
                            } else if (outLabelText.startsWith("string:")) {
                                this.attributeToVal.put(edge.source(),
                                    outLabelText.substring(
                                        outLabelText.indexOf(':') + 2,
                                        outLabelText.length() - 1));
                            }
                        }

                    }
                } else if (edge.label().text().equals("flag:root")) {
                    this.rootEdge = edge;
                }
            } else if (edge.label().text().equals("next")) {
                this.nextNode.put(edge.source(), edge.opposite());
            }
        }

        // Create a new instance model
        this.instanceModel =
            mh.createModel(this.instanceGraph.getInfo().getName());
        EList<EObject> contents = this.instanceModel.getContents();

        // Add instance of root to the contents of the instance model
        //Edge rootEdge = getRootEdge();
        if (this.rootEdge == null) {
            System.out.println("No root element!");
            System.exit(1);
        }

        String rootLabel =
            this.featureToType.get(this.rootEdge.source()).label().text();
        EClass rootClass = this.labelToClass.get(rootLabel);
        EObject rootElement =
            rootClass.getEPackage().getEFactoryInstance().create(rootClass);
        this.nodeToObject.put(this.rootEdge.source(), rootElement);
        contents.add(rootElement);

        // recursively add contained classes to the instance model 
        addContainedClasses(this.rootEdge);

        // add structural features
        addStructuralFeatures();
    }

    /**
     * Method to recursively add contained classes to an instance model
     * @param startEdge Edge on node to recursively add contained classes of
     */
    @SuppressWarnings("unchecked")
    private void addContainedClasses(Edge startEdge) {
        // Check all outgoing edges from the node of this the startEdge
        for (Edge outEdge : this.instanceGraph.outEdgeSet(startEdge.source())) {

            // Get type: edge of target node, and if exists check if it
            // represents a containment reference
            Edge refEdge = this.featureToType.get(outEdge.opposite());
            if (refEdge != null
                && this.containmentReferenceEdgeSet.contains(refEdge)) {
                // When not ordered, the order doesn't matter
                if (!this.labelToContainmentReference.get(
                    refEdge.label().text()).isOrdered()) {

                    Edge valueEdge =
                        this.featureToType.get(this.referenceToVal.get(refEdge.source()));
                    EClass valueEClass =
                        this.labelToClass.get(valueEdge.label().text());

                    // Create new instance of the target EClass
                    EReference valueEReference =
                        this.labelToContainmentReference.get(refEdge.label().text());
                    EObject valueInstance =
                        valueEClass.getEPackage().getEFactoryInstance().create(
                            valueEClass);

                    // if multiplicity is many, add to set of values, otherwise
                    // it is just the value
                    if (valueEReference.isMany()) {
                        ((EList<EObject>) this.nodeToObject.get(
                            startEdge.source()).eGet(valueEReference)).add(valueInstance);
                    } else {
                        this.nodeToObject.get(startEdge.source()).eSet(
                            valueEReference, valueInstance);
                    }

                    // add the new value to nodeToObject map
                    this.nodeToObject.put(valueEdge.source(), valueInstance);

                    // Now recursively continue for the node that was added
                    addContainedClasses(valueEdge);
                    // When ordered, check if it has no incoming next edges and
                    // only then add it plus add next ones
                } else {
                    if (!this.nextNode.containsValue(outEdge.opposite())) {

                        Node next = outEdge.opposite();
                        do {
                            Edge valueEdge =
                                this.featureToType.get(this.referenceToVal.get(next));
                            EClass valueEClass =
                                this.labelToClass.get(valueEdge.label().text());

                            // Create new instance of the target EClass
                            EReference valueEReference =
                                this.labelToContainmentReference.get(refEdge.label().text());
                            EObject valueInstance =
                                valueEClass.getEPackage().getEFactoryInstance().create(
                                    valueEClass);

                            // if multiplicity is many, add to set of values,
                            // otherwise it is just the value
                            if (valueEReference.isMany()) {
                                ((EList<EObject>) this.nodeToObject.get(
                                    startEdge.source()).eGet(valueEReference)).add(valueInstance);
                            } else {
                                this.nodeToObject.get(startEdge.source()).eSet(
                                    valueEReference, valueInstance);
                            }

                            // add the new value to nodeToObject map
                            this.nodeToObject.put(valueEdge.source(),
                                valueInstance);

                            // Now recursively continue for the node that was
                            // just added
                            addContainedClasses(valueEdge);

                            next = this.nextNode.get(next);

                        } while (next != null);
                    }
                }
            }

        }

    }

    /**
     * For all class instances in the instance model, add their structural
     * features that are represented in the instance graph. 
     * @require there must be an instance EClass in the instance model for 
     * every node representing an instance EClass
     */
    private void addStructuralFeatures() {
        // For all type edges of nodes that represent classes
        for (Edge classEdge : this.classEdgeSet) {

            // Check all outgoing edges from the node of this the classEdge
            for (Edge outEdge : this.instanceGraph.outEdgeSet(classEdge.source())) {

                // Get type: edge of target node, and if it exists 
                Edge featureEdge = this.featureToType.get(outEdge.opposite());
                if (featureEdge != null) {

                    // if it represents a non-containment and non-container ref
                    if (this.referenceEdgeSet.contains(featureEdge)
                        && !this.labelToReference.get(
                            featureEdge.label().text()).isContainer()) {
                        addReference(classEdge, featureEdge);

                        // else check it represents an attribute
                    } else if (this.attributeEdgeSet.contains(featureEdge)) {
                        addAttribute(classEdge, featureEdge);
                    }
                }
            }
        }
    }

    /**
     * For a given edge of a EClass node type and an edge of an EAttribute node
     * type, add the value for this EAttribute for this EClass instance to the
     * instance model.
     * @param containerEdge edge of the EClass node type
     * @param featureEdge edge of the EAttribute node type
     */
    @SuppressWarnings("unchecked")
    private void addAttribute(Edge containerEdge, Edge featureEdge) {
        // check if it is ordered, and when not just add it
        if (!this.labelToAttribute.get(featureEdge.label().text()).isOrdered()) {

            // create a new instance of a literal to add
            String attrValue = this.attributeToVal.get(featureEdge.source());
            EClass containerEClass =
                this.labelToClass.get(containerEdge.label().text());
            EAttribute valueEAttribute =
                this.labelToAttribute.get(featureEdge.label().text());
            EDataType attrType =
                (EDataType) this.labelToAttribute.get(
                    featureEdge.label().text()).getEType();

            EFactory factory;
            if (containerEClass.getEPackage().eContents().contains(attrType)) {
                factory = containerEClass.getEPackage().getEFactoryInstance();
            } else {
                factory =
                    containerEClass.eClass().getEPackage().getEFactoryInstance();
            }

            Object valueInstance =
                factory.createFromString(attrType, attrValue);

            // if multiplicity is many, add to set of values, otherwise it is
            // just the value
            if (valueEAttribute.isMany()) {
                EList<Object> values =
                    ((EList<Object>) this.nodeToObject.get(
                        containerEdge.source()).eGet(valueEAttribute));
                values.add(valueInstance);
            } else {
                this.nodeToObject.get(containerEdge.source()).eSet(
                    valueEAttribute, valueInstance);
            }

            // Else check if it is the first in a sequence. If not, then ignore it
        } else if (!this.nextNode.containsValue(featureEdge.opposite())) {
            Node next = featureEdge.source();
            do {
                // create a new instance of a literal to add
                String attrValue = this.attributeToVal.get(next);
                EClass containerEClass =
                    this.labelToClass.get(containerEdge.label().text());
                EAttribute valueEAttribute =
                    this.labelToAttribute.get(featureEdge.label().text());
                EDataType attrType =
                    (EDataType) this.labelToAttribute.get(
                        featureEdge.label().text()).getEType();

                EFactory factory;
                if (containerEClass.getEPackage().eContents().contains(attrType)) {
                    factory =
                        containerEClass.getEPackage().getEFactoryInstance();
                } else {
                    factory =
                        containerEClass.eClass().getEPackage().getEFactoryInstance();
                }

                Object valueInstance =
                    factory.createFromString(attrType, attrValue);

                // if multiplicity is many, add to set of values, otherwise it
                // is just the value
                if (valueEAttribute.isMany()) {
                    EList<Object> values =
                        ((EList<Object>) this.nodeToObject.get(
                            containerEdge.source()).eGet(valueEAttribute));
                    values.add(valueInstance);
                } else {
                    this.nodeToObject.get(containerEdge.source()).eSet(
                        valueEAttribute, valueInstance);
                }

                next = this.nextNode.get(next);
            } while (next != null);
        }
    }

    /**
     * For a given edge of a EClass node type and an edge of an EReference node
     * type, add the value for this EReference for this EClass instance to the
     * instance model.
     * @param containerEdge edge of the EClass node type
     * @param featureEdge edge of the EReference node type
     */
    @SuppressWarnings("unchecked")
    private void addReference(Edge containerEdge, Edge featureEdge) {
        // check if it is ordered, and when not just add it
        if (!this.labelToReference.get(featureEdge.label().text()).isOrdered()) {
            Edge valueEdge =
                this.featureToType.get(this.referenceToVal.get(featureEdge.source()));

            // Get the value of the EReference from the nodeToObject map
            EReference valueEReference =
                this.labelToReference.get(featureEdge.label().text());
            EObject valueInstance = this.nodeToObject.get(valueEdge.source());

            // if multiplicity is many, add to set of values, otherwise it is
            // just the value
            if (valueEReference.isMany()) {
                EList<EObject> values =
                    ((EList<EObject>) this.nodeToObject.get(
                        containerEdge.source()).eGet(valueEReference));
                if (!values.contains(valueInstance)) {
                    values.add(valueInstance);
                }
            } else {
                this.nodeToObject.get(containerEdge.source()).eSet(
                    valueEReference, valueInstance);
            }
            // Else check if it is the first in a sequence. If not, then ignore it
        } else if (!this.nextNode.containsValue(featureEdge.opposite())) {
            Node next = featureEdge.source();
            do {
                Edge valueEdge =
                    this.featureToType.get(this.referenceToVal.get(next));

                // Get the value of the EReference from the nodeToObject map
                EReference valueEReference =
                    this.labelToReference.get(featureEdge.label().text());
                EObject valueInstance =
                    this.nodeToObject.get(valueEdge.source());

                // if multiplicity is many, add to set of values when not
                // already in the set or when already in the set, move it to 
                // the last position
                if (valueEReference.isMany()) {
                    EList<EObject> values =
                        ((EList<EObject>) this.nodeToObject.get(
                            containerEdge.source()).eGet(valueEReference));
                    if (!values.contains(valueInstance)) {
                        values.add(valueInstance);
                    } else {
                        values.move(values.size() - 1, valueInstance);
                    }
                } else {
                    this.nodeToObject.get(containerEdge.source()).eSet(
                        valueEReference, valueInstance);
                }

                next = this.nextNode.get(next);
            } while (next != null);
        }
    }

    /**
     * For a given node that represents a feature, return the node that 
     * represents the value of this feature, or null if there is no value.
     * @param featureNode The node representing an EStructuralFeature
     * @returns The node representing the value 
     */
    private Node getValue(Node featureNode) {
        Node value = null;

        for (Edge outEdge : this.instanceGraph.outEdgeSet(featureNode)) {
            if (outEdge.label().text().equals("val")) {
                value = outEdge.opposite();
            }
        }

        return value;
    }

    /**
     * Returns the resource that has the Ecore instance model
     * @return the instance model
     */
    public Resource getInstanceModel() {
        return this.instanceModel;
    }

}
