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

import groove.graph.DefaultEdge;
import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.GraphInfo;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.TypeGraph;
import groove.graph.TypeNode;

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

    private ModelHandler mh;
    private TypeGraph tg = new TypeGraph();
    private TypeGraph ecoreTG = new TypeGraph();

    private TypeNode eClassNode;
    private TypeNode eReferenceNode;
    private TypeNode eAttributeNode;
    private TypeNode eEnumNode;

    private Map<EClass,Node> eClassToNodeMap = new HashMap<EClass,Node>();
    private Map<EEnum,Node> eEnumToNodeMap = new HashMap<EEnum,Node>();
    private Map<EReference,Node> eReferenceToNodeMap =
        new HashMap<EReference,Node>();
    private Map<EAttribute,Node> eAttributeToNodeMap =
        new HashMap<EAttribute,Node>();
    private Map<EDataType,Node> eDataTypeToNodeMap =
        new HashMap<EDataType,Node>();
    private Map<EEnumLiteral,Edge> eEnumLiteralToEdgeMap =
        new HashMap<EEnumLiteral,Edge>();

    /**
     * Constructor method. given a handle to a ModelHandler, create a type
     * graph representation of the Ecore meta model that was loaded by the
     * ModelHandler. 
     * @param m the ModelHandler.
     */
    public TypeGraphRep(ModelHandler m) {
        this.mh = m;

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

        Label eClassLabel = DefaultLabel.createLabel(this.mh.getEClassType());
        Label eReferenceLabel =
            DefaultLabel.createLabel(this.mh.getEReferenceType());
        Label eAttributeLabel =
            DefaultLabel.createLabel(this.mh.getEAttributeType());
        Label eEnumLabel = DefaultLabel.createLabel(this.mh.getEEnumType());
        Label rootLabel = DefaultLabel.createLabel("flag:root");
        Label contLabel = DefaultLabel.createLabel("flag:containment");
        Label absValLabel = DefaultLabel.createLabel("abs:val");
        Label absWildcardLabel = DefaultLabel.createLabel("abs:?");

        this.eClassNode = this.ecoreTG.addNode(eClassLabel);
        this.eReferenceNode = this.ecoreTG.addNode(eReferenceLabel);
        this.eAttributeNode = this.ecoreTG.addNode(eAttributeLabel);
        this.eEnumNode = this.ecoreTG.addNode(eEnumLabel);
        //ecoreTG.addNode(eContReferenceNode);          

        this.ecoreTG.addEdge(this.eClassNode, rootLabel, this.eClassNode);
        this.ecoreTG.addEdge(this.eReferenceNode, contLabel,
            this.eReferenceNode);
        this.ecoreTG.addEdge(this.eAttributeNode, absValLabel, this.eEnumNode);
        this.ecoreTG.addEdge(this.eReferenceNode, absValLabel, this.eClassNode);
        this.ecoreTG.addEdge(this.eClassNode, absWildcardLabel,
            this.eReferenceNode);

    }

    /**
     * Method to add nodes that represent EClasses to type graph
     * @param classes The EClasses of the Ecore meta model.
     */
    private void addEClasses(Vector<EClass> classes) {

        for (EClass aClass : classes) {
            //System.out.println(GraphLabels.getLabel(aClass));

            // Add new labeled type node to tg 
            String labelText = GraphLabels.getLabel(aClass);
            DefaultLabel label = DefaultLabel.createLabel(labelText);
            Label subLabel = DefaultLabel.createLabel("sub:");
            Node node = this.tg.addNode(label);

            // Add map of EClass to node representing it
            this.eClassToNodeMap.put(aClass, node);

            // add as subtype to Ecore typegraph
            Node ecoreNode = this.ecoreTG.addNode(label);
            this.ecoreTG.addEdge(ecoreNode, subLabel, this.eClassNode);
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
                DefaultEdge edge =
                    DefaultEdge.createEdge(this.eClassToNodeMap.get(aClass),
                        "sub:", this.eClassToNodeMap.get(superType));
                this.tg.addEdge(edge);
                /*TypeNode superNode =
                    (TypeNode) this.eClassToNodeMap.get(superType);
                TypeNode subNode = (TypeNode) this.eClassToNodeMap.get(aClass);

                try {
                    this.tg.addSubtype(superNode, subNode);
                } catch (FormatException e) {
                    e.printStackTrace();
                }*/
            }
        }

    }

    /**
     * Method to add EEnum representations to the type graph
     * @param enums the EEnums of the Ecore meta model
     */
    private void addEEnums(Vector<EEnum> enums) {

        for (EEnum aEnum : enums) {
            //System.out.println(GraphLabels.getLabel(aEnum));

            // Add new labeled type node to tg 
            String labelText = GraphLabels.getLabel(aEnum);
            DefaultLabel label = DefaultLabel.createLabel(labelText);
            Label subLabel = DefaultLabel.createLabel("sub:");
            Node node = this.tg.addNode(label);

            // Add map of EClass to node representing it
            this.eEnumToNodeMap.put(aEnum, node);

            // add as subtype to Ecore typegraph
            Node ecoreNode = this.ecoreTG.addNode(label);
            this.ecoreTG.addEdge(ecoreNode, subLabel, this.eEnumNode);
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
            //System.out.println(GraphLabels.getLabel(literal));

            // Create and add flag to the Enum representation of this literal
            String labelText = GraphLabels.getLabel(literal);
            EEnum aEnum = literal.getEEnum();
            Node source = this.eEnumToNodeMap.get(aEnum);
            DefaultEdge edge =
                DefaultEdge.createEdge(source, labelText, source);
            this.tg.addEdge(edge);

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
                Node node = null;

                // See if a node labeled labelText is already in the graph
                // If so, set present to true and node to this found node
                for (Edge edge : this.tg.edgeSet()) {
                    if (edge.label().text().equals(labelText)) {
                        present = true;
                        node = edge.source();
                        break;
                    }
                }

                // If node is not present yet, create and add it
                if (!present) {
                    DefaultLabel label = DefaultLabel.createLabel(labelText);
                    node = this.tg.addNode(label);
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
            //System.out.println(GraphLabels.getLabel(aReference));

            // Add new labeled type node to tg to represent the reference 
            String labelText = GraphLabels.getLabel(aReference);
            DefaultLabel label = DefaultLabel.createLabel(labelText);
            Node node = this.tg.addNode(label);

            // add edges from source and to target of EReference
            Label sourceLabel = DefaultLabel.createLabel(aReference.getName());
            Label targetLabel = DefaultLabel.createLabel("val");
            Node source =
                this.eClassToNodeMap.get(aReference.getEContainingClass());
            Node target =
                this.eClassToNodeMap.get(aReference.getEReferenceType());
            this.tg.addEdge(source, sourceLabel, node);
            this.tg.addEdge(node, targetLabel, target);

            // If EReference is ordered and many, add "next" self edge
            if (aReference.isOrdered() && aReference.isMany()) {
                Label nextLabel = DefaultLabel.createLabel("next");
                this.tg.addEdge(node, nextLabel, node);
            }

            // Add opposite edges if EReference has opposite and it exists
            // in the map already
            if (aReference.getEOpposite() != null) {
                EReference opposite = aReference.getEOpposite();
                if (this.eReferenceToNodeMap.containsKey(opposite)) {
                    Label oppositeLabel = DefaultLabel.createLabel("opposite");
                    Node oppositeNode = this.eReferenceToNodeMap.get(opposite);
                    this.tg.addEdge(node, oppositeLabel, oppositeNode);
                    this.tg.addEdge(oppositeNode, oppositeLabel, node);
                }
            }

            // Add sub: label from the aReference to the EReference type node
            Label subLabel = DefaultLabel.createLabel("sub:");
            Node ecoreNode = this.ecoreTG.addNode(label);
            this.ecoreTG.addEdge(ecoreNode, subLabel, this.eReferenceNode);

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
            //System.out.println(GraphLabels.getLabel(aAttribute));

            // Add new labeled type node to tg to represent the attribute 
            String labelText = GraphLabels.getLabel(aAttribute);
            DefaultLabel label = DefaultLabel.createLabel(labelText);
            Node node = this.tg.addNode(label);

            // Add sub: label from the eAttribute to the EReference type node
            Label subLabel = DefaultLabel.createLabel("sub:");
            Node ecoreNode = this.ecoreTG.addNode(label);
            this.ecoreTG.addEdge(ecoreNode, subLabel, this.eAttributeNode);

            // add edge from container EClass of EAttribute
            Node source =
                this.eClassToNodeMap.get(aAttribute.getEContainingClass());
            Label sourceLabel = DefaultLabel.createLabel(aAttribute.getName());
            this.tg.addEdge(source, sourceLabel, node);

            // If EAttribute is ordered and many, add "next" self edge
            if (aAttribute.isOrdered() && aAttribute.isMany()) {
                Label nextLabel = DefaultLabel.createLabel("next");
                this.tg.addEdge(node, nextLabel, node);
            }

            // If type is EEnum, add "val" edge
            if (aAttribute.getEAttributeType().eClass().getName().equals(
                "EEnum")) {
                Label valLabel = DefaultLabel.createLabel("val");
                Node target =
                    this.eEnumToNodeMap.get(aAttribute.getEAttributeType());
                this.tg.addEdge(node, valLabel, target);

                // Else type is EDataType, see if it was already created 
                // and if so add "val" edge to it
            } else {
                EDataType type = aAttribute.getEAttributeType();
                if (this.eDataTypeToNodeMap.containsKey(type)) {
                    Label valLabel = DefaultLabel.createLabel("val");
                    Node target = this.eDataTypeToNodeMap.get(type);
                    this.tg.addEdge(node, valLabel, target);
                }
            }

            // Add map of EAttribute to node representing it
            this.eAttributeToNodeMap.put(aAttribute, node);
        }
    }

    /**
     * Get the type graph that represents the Ecore meta model
     * @return the type graph
     */
    public TypeGraph getTypeGraph() {
        // Create aspect graph from type graph to store
        GraphInfo.setTypeRole(this.tg);
        //AspectGraph atg = AspectGraph.getFactory().fromPlainGraph(this.tg);

        return this.tg;
    }

    /**
     * Get the Ecore type graph that contains typing info which nodes types
     * represent EClasses or EReferences.
     * @return the Ecore type graph
     */
    public TypeGraph getEcoreTypeGraph() {
        // Create aspect graph from type graph to store
        GraphInfo.setTypeRole(this.ecoreTG);
        //AspectGraph atg = AspectGraph.getFactory().fromPlainGraph(this.ecoreTG);

        return this.ecoreTG;
    }

    /*
    public DefaultNode getClassNode(EClass eClass) {
    	return eClassToNodeMap.get(eClass);
    }*/

}
