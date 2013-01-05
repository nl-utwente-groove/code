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
package groove.io.ecore2groove;

import static groove.graph.GraphRole.TYPE;
import groove.graph.plain.PlainEdge;
import groove.graph.plain.PlainFactory;
import groove.graph.plain.PlainGraph;
import groove.graph.plain.PlainLabel;
import groove.graph.plain.PlainNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EReference;

/**
 * Class that, given a handle to a ModelHandler, creates a graph representation
 * of the Ecore metamodel that was loaded by the ModelHandler.
 * 
 * @author Stefan Teijgeler
 */
public class TypeGraphRep {

    private final ModelHandler mh;
    private final PlainGraph tg;
    private final PlainGraph ecoreTG;

    private PlainNode eClassNode;
    private PlainNode eReferenceNode;
    private PlainNode eAttributeNode;
    private PlainNode eEnumNode;

    private Map<EClass,PlainNode> eClassToNodeMap =
        new HashMap<EClass,PlainNode>();
    private Map<EEnum,PlainNode> eEnumToNodeMap =
        new HashMap<EEnum,PlainNode>();
    private Map<EReference,PlainNode> eReferenceToNodeMap =
        new HashMap<EReference,PlainNode>();
    private Map<EAttribute,PlainNode> eAttributeToNodeMap =
        new HashMap<EAttribute,PlainNode>();
    private Map<EDataType,PlainNode> eDataTypeToNodeMap =
        new HashMap<EDataType,PlainNode>();
    private Map<EEnumLiteral,PlainEdge> eEnumLiteralToEdgeMap =
        new HashMap<EEnumLiteral,PlainEdge>();

    /**
     * Constructor method. given a handle to a ModelHandler, create a type
     * graph representation of the Ecore meta model that was loaded by the
     * ModelHandler. 
     * @param m the ModelHandler.
     */
    public TypeGraphRep(String name, ModelHandler m) {
        this.mh = m;
        this.tg = new PlainGraph(name);
        this.tg.setRole(TYPE);
        this.ecoreTG = new PlainGraph("EcoreTypes");
        this.ecoreTG.setRole(TYPE);

        addEcoreTypes();

        addEClasses(this.mh.getEClasses());
        addEClassSubInfo(this.mh.getEClasses());
        addEReferences(this.mh.getEReferences());
        addEEnums(this.mh.getEEnums());
        addEEnumLiterals(this.mh.getEEnumLiterals());
        addEDataTypes(this.mh.getEDataTypes());
        addEAttributes(this.mh.getEAttributes());
    }

    /**
     * Create EClass and EReference nodes in the Ecore type graph that EClasses
     * and EReferences can inherit from later. Gets the safe label strings from
     * the ModelHandler loaded by the constructor. 
     */
    private void addEcoreTypes() {
        PlainLabel eClassLabel = factory.createLabel(this.mh.getEClassType());
        PlainLabel eReferenceLabel =
            factory.createLabel(this.mh.getEReferenceType());
        PlainLabel eAttributeLabel =
            factory.createLabel(this.mh.getEAttributeType());
        PlainLabel eEnumLabel = factory.createLabel(this.mh.getEEnumType());

        this.eClassNode = addTypeNode(this.ecoreTG, eClassLabel);
        this.eReferenceNode = addTypeNode(this.ecoreTG, eReferenceLabel);
        this.eAttributeNode = addTypeNode(this.ecoreTG, eAttributeLabel);
        this.eEnumNode = addTypeNode(this.ecoreTG, eEnumLabel);

        this.ecoreTG.addEdge(this.eClassNode, ROOT_LABEL, this.eClassNode);
        this.ecoreTG.addEdge(this.eReferenceNode, CONT_LABEL,
            this.eReferenceNode);
        this.ecoreTG.addEdge(this.eAttributeNode, ABS_VAL_LABEL, this.eEnumNode);
        this.ecoreTG.addEdge(this.eReferenceNode, ABS_VAL_LABEL,
            this.eClassNode);
        this.ecoreTG.addEdge(this.eClassNode, ABS_WILDCARD_LABEL,
            this.eReferenceNode);
    }

    /**
     * Method to add nodes that represent EClasses to type graph
     * @param classes The EClasses of the Ecore meta model.
     */
    private void addEClasses(Vector<EClass> classes) {
        for (EClass aClass : classes) {
            // Add new labeled type node to tg 
            String labelText = GraphLabels.getLabel(aClass);
            PlainLabel label = factory.createLabel(labelText);
            PlainNode node = addTypeNode(this.tg, label);

            // Add map of EClass to node representing it
            this.eClassToNodeMap.put(aClass, node);

            // Add as subtype to Ecore typegraph
            PlainNode ecoreNode = addTypeNode(this.ecoreTG, label);
            this.ecoreTG.addEdge(ecoreNode, SUB_LABEL, this.eClassNode);
        }
    }

    /**
     * Method to add the EClass hierarchy to the type graph between type nodes
     * that have been added previously
     * @param classes The EClasses of the Ecore meta model
     * @require EClasses of the Ecore type graph must have a representation
     * in the type graph already.
     */
    private void addEClassSubInfo(Vector<EClass> classes) {
        for (EClass aClass : classes) {
            EList<EClass> superTypes = aClass.getESuperTypes();

            for (EClass superType : superTypes) {
                PlainEdge edge =
                    factory.createEdge(this.eClassToNodeMap.get(aClass),
                        SUB_LABEL, this.eClassToNodeMap.get(superType));
                this.tg.addEdgeContext(edge);
            }
        }
    }

    /**
     * Method to add EEnum representations to the type graph
     * @param enums the EEnums of the Ecore meta model
     */
    private void addEEnums(Vector<EEnum> enums) {
        for (EEnum aEnum : enums) {
            // Add new labeled type node to tg 
            String labelText = GraphLabels.getLabel(aEnum);
            PlainLabel label = factory.createLabel(labelText);
            PlainNode node = addTypeNode(this.tg, label);

            // Add map of EClass to node representing it
            this.eEnumToNodeMap.put(aEnum, node);

            // add as subtype to Ecore typegraph
            PlainNode ecoreNode = addTypeNode(this.ecoreTG, label);
            this.ecoreTG.addEdge(ecoreNode, SUB_LABEL, this.eEnumNode);
        }
    }

    /**
     * Add flags to the type graph that represent EEnumLiterals.
     * @param literals Vector<EEnumLiteral> of literals to add
     * @require EEnums of the Ecore meta model must have a representation
     * in the type graph already.
     */
    private void addEEnumLiterals(Vector<EEnumLiteral> literals) {
        for (EEnumLiteral literal : literals) {
            // Create and add flag to the Enum representation of this literal
            String labelText = GraphLabels.getLabel(literal);
            EEnum aEnum = literal.getEEnum();
            PlainNode source = this.eEnumToNodeMap.get(aEnum);
            PlainEdge edge = factory.createEdge(source, labelText, source);
            this.tg.addEdgeContext(edge);

            // Add map of literal to the edge representing it
            this.eEnumLiteralToEdgeMap.put(literal, edge);
        }
    }

    /**
     * Add representations for EDataTypes to the type graph, but only if 
     * there is no representation yet for this type.
     * @param datatypes The EDataTypes used in the Ecore meta model
     */
    private void addEDataTypes(Vector<EDataType> datatypes) {
        for (EDataType datatype : datatypes) {
            String labelText = GraphLabels.getLabel(datatype);

            // If label is not empty and not in the graph yet, add it
            if (!labelText.isEmpty()) {

                boolean present = false;
                PlainNode node = null;

                // See if a node labeled labelText is already in the graph
                // If so, set present to true and node to this found node
                for (PlainEdge edge : this.tg.edgeSet()) {
                    if (edge.label().text().equals(labelText)) {
                        present = true;
                        node = edge.source();
                        break;
                    }
                }

                // If node is not present yet, create and add it
                if (!present) {
                    PlainLabel label = factory.createLabel(labelText);
                    node = addTypeNode(this.tg, label);
                }

                // Add map of datatype to the node representing it
                this.eDataTypeToNodeMap.put(datatype, node);
            }
        }
    }

    /**
     * Adds nodes that represent EReferences to the type graph.
     * @param references The EReferences in the Ecore meta model
     * @require All EClasses of the Ecore meta model must have a representation
     * in the type graph already
     */
    private void addEReferences(Vector<EReference> references) {
        for (EReference aReference : references) {
            // Add new labeled type node to tg to represent the reference 
            String labelText = GraphLabels.getLabel(aReference);
            PlainLabel label = factory.createLabel(labelText);
            PlainNode node = addTypeNode(this.tg, label);

            // add edges from source and to target of EReference
            PlainLabel sourceLabel =
                factory.createLabel(aReference.getName());
            PlainNode source =
                this.eClassToNodeMap.get(aReference.getEContainingClass());
            PlainNode target =
                this.eClassToNodeMap.get(aReference.getEReferenceType());
            this.tg.addEdge(source, sourceLabel, node);
            this.tg.addEdge(node, VAL_LABEL, target);

            // If EReference is ordered and many, add "next" self edge
            if (aReference.isOrdered() && aReference.isMany()) {
                this.tg.addEdge(node, NEXT_LABEL, node);
            }

            // Add opposite edges if EReference has opposite and it exists
            // in the map already
            if (aReference.getEOpposite() != null) {
                EReference opposite = aReference.getEOpposite();
                if (this.eReferenceToNodeMap.containsKey(opposite)) {
                    PlainNode oppositeNode =
                        this.eReferenceToNodeMap.get(opposite);
                    this.tg.addEdge(node, OPPOSITE_LABEL, oppositeNode);
                    this.tg.addEdge(oppositeNode, OPPOSITE_LABEL, node);
                }
            }

            // Add sub: label from the aReference to the EReference type node
            PlainNode ecoreNode = addTypeNode(this.ecoreTG, label);
            this.ecoreTG.addEdge(ecoreNode, SUB_LABEL, this.eReferenceNode);

            // Add map of EReference to node representing it
            this.eReferenceToNodeMap.put(aReference, node);
        }
    }

    /**
     * Method to add nodes that represent EAttributes to type graph.
     * @param attributes Vector<EAttribute> of attributes to add
     * @require All EClasses and EDataTypes of the Ecore meta model must have
     * a representation in the type graph already.
     */
    private void addEAttributes(Vector<EAttribute> attributes) {
        for (EAttribute aAttribute : attributes) {
            // Add new labeled type node to tg to represent the attribute 
            String labelText = GraphLabels.getLabel(aAttribute);
            PlainLabel label = factory.createLabel(labelText);
            PlainNode node = addTypeNode(this.tg, label);

            // Add sub: label from the eAttribute to the EReference type node
            PlainNode ecoreNode = addTypeNode(this.ecoreTG, label);
            this.ecoreTG.addEdge(ecoreNode, SUB_LABEL, this.eAttributeNode);

            // add edge from container EClass of EAttribute
            PlainNode source =
                this.eClassToNodeMap.get(aAttribute.getEContainingClass());
            PlainLabel sourceLabel =
                factory.createLabel(aAttribute.getName());
            this.tg.addEdge(source, sourceLabel, node);

            // If EAttribute is ordered and many, add "next" self edge
            if (aAttribute.isOrdered() && aAttribute.isMany()) {
                this.tg.addEdge(node, NEXT_LABEL, node);
            }

            // If type is EEnum, add "val" edge
            if (aAttribute.getEAttributeType().eClass().getName().equals(
                "EEnum")) {
                PlainNode target =
                    this.eEnumToNodeMap.get(aAttribute.getEAttributeType());
                this.tg.addEdge(node, VAL_LABEL, target);

                // Else type is EDataType, see if it was already created 
                // and if so add "val" edge to it
            } else {
                EDataType type = aAttribute.getEAttributeType();
                if (this.eDataTypeToNodeMap.containsKey(type)) {
                    PlainNode target = this.eDataTypeToNodeMap.get(type);
                    this.tg.addEdge(node, VAL_LABEL, target);
                }
            }

            // Add map of EAttribute to node representing it
            this.eAttributeToNodeMap.put(aAttribute, node);
        }
    }

    private PlainNode addTypeNode(PlainGraph graph, PlainLabel label) {
        PlainNode node = graph.addNode();
        graph.addEdge(node, label, node);
        return node;
    }

    /**
     * Get the type graph that represents the Ecore meta model
     * @return the type graph
     */
    public PlainGraph getTypeGraph() {
        // Create aspect graph from type graph to store
        return this.tg;
    }

    /**
     * Get the Ecore type graph that contains typing info which nodes types
     * represent EClasses or EReferences.
     * @return the Ecore type graph
     */
    public PlainGraph getEcoreTypeGraph() {
        // Create aspect graph from type graph to store
        return this.ecoreTG;
    }

    /** Factory to create all graph elements. */
    private static final PlainFactory factory = PlainFactory.instance();
    /** The subtype label. */
    private static final PlainLabel SUB_LABEL = factory.createLabel("sub:");
    /** Root label. */
    private static final PlainLabel ROOT_LABEL =
        factory.createLabel("flag:root");
    /** Containment label. */
    private static final PlainLabel CONT_LABEL =
        factory.createLabel("flag:containment");
    /** Abstract "val" label. */
    private static final PlainLabel ABS_VAL_LABEL =
        factory.createLabel("abs:val");
    /** Abstract wildcard label. */
    private static final PlainLabel ABS_WILDCARD_LABEL =
        factory.createLabel("abs:?");
    /** Next label. */
    private static final PlainLabel NEXT_LABEL = factory.createLabel("next");
    /** Opposite label. */
    private static final PlainLabel OPPOSITE_LABEL =
        factory.createLabel("opposite");
    /** Value label. */
    private static final PlainLabel VAL_LABEL = factory.createLabel("val");
}
