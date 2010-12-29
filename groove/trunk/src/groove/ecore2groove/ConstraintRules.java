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

import static groove.graph.GraphRole.RULE;
import groove.graph.DefaultGraph;
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Given a ModelHandler, this class generates constraint rules for the Ecore
 * model that is loaded by the ModelHandler. All rules are added to a set
 * that can be retrieved with a get method.
 * @author Stefan Teijgeler
 *
 */
public class ConstraintRules {

    private ModelHandler mh;
    private List<DefaultGraph> constraintRules;

    /**
     * Constructor method that calls methods to create constraint rules for
     * the Ecore model in the ModelHandler passed to it.
     * @param mh the ModelHandler 
     */
    public ConstraintRules(ModelHandler mh) {
        this.mh = mh;
        this.constraintRules = new ArrayList<DefaultGraph>();

        eClassConstraints(mh.getEClasses());
        eEnumConstraints(mh.getEEnums());
        eReferenceConstraints(mh.getEReferences());
        eAttributeConstraints(mh.getEAttributes());
    }

    /**
     * Method to create constraint rules for EClasses.
     * @param eClasses the EClasses
     */
    private void eClassConstraints(Vector<EClass> eClasses) {
        for (EClass eClass : eClasses) {
            if (eClass.isAbstract()) {
                addAbstractConstraint(eClass);
            }
        }
        if (this.mh.getEReferences().size() != 0) {
            addCyclicityConstraint();
            addOneContainerConstraint();
        }
        addRootConstraint();
    }

    /**
     * Method to create constraint rules for EEnums
     * @param eEnums the EEnums
     */
    private void eEnumConstraints(Vector<EEnum> eEnums) {
        for (EEnum eEnum : eEnums) {
            addNoLiteralsConstraint(eEnum);
            addManyLiteralsConstraint(eEnum);
            addNoIncValConstraint(eEnum);
            addManyIncValConstraint(eEnum);
        }
    }

    /**
     * Method to create constraint rules for EReference
     * @param eReferences the EReferences
     */
    private void eReferenceConstraints(Vector<EReference> eReferences) {
        for (EReference eReference : eReferences) {
            addNoValConstraint(eReference);
            addManyValConstraint(eReference);
            addNoContainerConstraint(eReference);
            addManyContainerConstraint(eReference);
            if (eReference.isMany() && eReference.isUnique()) {
                addUniqueRefConstraint(eReference);
            }
            if (eReference.getLowerBound() > 0) {
                addLowerBoundConstraint(eReference);
            }
            if (eReference.getUpperBound() != -1) {
                addUpperBoundConstraint(eReference);
            }
            if (eReference.getEOpposite() != null) {
                addOppositeConstraint(eReference);
            }
            if (!eReference.getEKeys().isEmpty()) {
                addEkeysConstraint(eReference);
            }
            if (eReference.isOrdered() && eReference.isMany()) {
                addOrderedConstraint(eReference);
            }
            if (eReference.isContainment()) {
                addContainmentConstraint(eReference);
            } else {
                addNotContainmentConstraint(eReference);
            }
        }
    }

    /**
     * Method to create constraint rules for EAttributes
     * @param eAttributes the EAttributes
     */
    private void eAttributeConstraints(Vector<EAttribute> eAttributes) {
        for (EAttribute eAttribute : eAttributes) {

            addManyValConstraint(eAttribute);
            addValuesConstraint(eAttribute);
            if (eAttribute.isMany() && eAttribute.isUnique()) {
                addUniqueAttrConstraint(eAttribute);
            }
            if (eAttribute.getLowerBound() > 0) {
                addLowerBoundConstraint(eAttribute);
            }
            if (eAttribute.getUpperBound() != -1) {
                addUpperBoundConstraint(eAttribute);
            }
            if (!eAttribute.isChangeable()) {
                if (eAttribute.getDefaultValueLiteral() != null
                    && !eAttribute.getDefaultValueLiteral().equals("")) {
                    addUnchangeableConstraint(eAttribute);
                }
            }
            if (eAttribute.isID()) {
                addIDConstraint(eAttribute);
            }
            if (eAttribute.isOrdered() && eAttribute.isMany()) {
                addOrderedConstraint(eAttribute);
            }
        }
    }

    /**
     * Method to create global constraint rule for cyclicity
     */
    private void addCyclicityConstraint() {
        // Cyclicity constraint
        String name = "constraint - global - cyclicity";
        DefaultGraph constraintRule = createRuleGraph(name);

        DefaultNode node = constraintRule.addNode();

        constraintRule.addEdge(node, this.mh.getEClassType(), node);
        constraintRule.addEdge(node, "path:(?.flag:containment.val)+", node);
    }

    /**
     * Method to create global constraint that each non-root EClass must have
     * a container
     */
    private void addOneContainerConstraint() {
        // Not zero container
        String name = "constraint - global - noContainer";
        DefaultGraph constraintRule = createRuleGraph(name);
        DefaultLabel eclassLabel =
            DefaultLabel.createLabel(this.mh.getEClassType());
        DefaultLabel notFlagLabel = DefaultLabel.createLabel("not:flag:root");
        DefaultLabel refLabel =
            DefaultLabel.createLabel(this.mh.getEReferenceType());
        DefaultNode eclassNode = constraintRule.addNode();
        DefaultNode contRefNode = constraintRule.addNode();

        constraintRule.addEdge(eclassNode, eclassLabel, eclassNode);
        constraintRule.addEdge(eclassNode, notFlagLabel, eclassNode);
        constraintRule.addEdge(contRefNode, refLabel, contRefNode);
        constraintRule.addEdge(contRefNode, "not:", contRefNode);
        constraintRule.addEdge(contRefNode, "flag:containment", contRefNode);
        constraintRule.addEdge(contRefNode, "val", eclassNode);

        // Not many containers
        constraintRule = createRuleGraph(name);
        name = "constraint - global - manyContainers";

        eclassNode = constraintRule.addNode();
        DefaultNode refNode1 = constraintRule.addNode();
        DefaultNode refNode2 = constraintRule.addNode();

        DefaultLabel regExprLabel =
            DefaultLabel.createLabel("path:flag:containment.val");
        DefaultLabel unequalLabel = DefaultLabel.createLabel("!=");

        constraintRule.addEdge(eclassNode, eclassLabel, eclassNode);
        constraintRule.addEdge(eclassNode, notFlagLabel, eclassNode);
        constraintRule.addEdge(refNode1, refLabel, refNode1);
        constraintRule.addEdge(refNode2, refLabel, refNode2);
        constraintRule.addEdge(refNode1, unequalLabel, refNode2);
        constraintRule.addEdge(refNode1, regExprLabel, eclassNode);
        constraintRule.addEdge(refNode2, regExprLabel, eclassNode);
    }

    /**
     * Method to create constraint rules that there must be exactly one
     * root EClass 
     */
    private void addRootConstraint() {
        // Not two root elements constraint rule
        String name = "constraint - global - manyRoot";
        DefaultGraph constraintRule = createRuleGraph(name);

        DefaultNode node1 = constraintRule.addNode();
        DefaultNode node2 = constraintRule.addNode();

        DefaultLabel eclassLabel =
            DefaultLabel.createLabel(this.mh.getEClassType());
        DefaultLabel flagLabel = DefaultLabel.createLabel("flag:root");
        DefaultLabel unequalLabel = DefaultLabel.createLabel("!=");

        constraintRule.addEdge(node1, eclassLabel, node1);
        constraintRule.addEdge(node2, eclassLabel, node2);
        constraintRule.addEdge(node1, flagLabel, node1);
        constraintRule.addEdge(node2, flagLabel, node2);
        constraintRule.addEdge(node1, unequalLabel, node2);

        // Not zero root elements constraint rule
        constraintRule = createRuleGraph(name);
        name = "constraint - global - noRoot";

        DefaultNode eclassNode = constraintRule.addNode();

        DefaultLabel notLabel = DefaultLabel.createLabel("not:");

        constraintRule.addEdge(eclassNode, eclassLabel, eclassNode);
        constraintRule.addEdge(eclassNode, flagLabel, eclassNode);
        constraintRule.addEdge(eclassNode, notLabel, eclassNode);

        // No container for root constraint rule, only when there are
        // EReferences in the model
        if (this.mh.getEReferences().size() != 0) {
            constraintRule = createRuleGraph(name);
            name = "constraint - global - rootContainer";

            eclassNode = constraintRule.addNode();
            DefaultNode refNode = constraintRule.addNode();

            DefaultLabel regExprLabel =
                DefaultLabel.createLabel("path:flag:containment.val");
            DefaultLabel refLabel =
                DefaultLabel.createLabel(this.mh.getEReferenceType());

            constraintRule.addEdge(eclassNode, eclassLabel, eclassNode);
            constraintRule.addEdge(eclassNode, flagLabel, eclassNode);
            constraintRule.addEdge(refNode, refLabel, refNode);
            constraintRule.addEdge(refNode, regExprLabel, eclassNode);
        }
    }

    /**
     * Method to create a containment constraint for a given EReference
     * @param eReference the EReference
     */
    private void addContainmentConstraint(EReference eReference) {
        // There must be a containment flag for containment references
        // constraint
        String name =
            "constraint - " + GraphLabels.getLabelNoType(eReference)
                + " - noContainmentFlag";
        DefaultGraph constraintRule = createRuleGraph(name);

        DefaultNode node = constraintRule.addNode();

        DefaultLabel refLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eReference));
        DefaultLabel notContLabel =
            DefaultLabel.createLabel("not:flag:containment");

        constraintRule.addEdge(node, refLabel, node);
        constraintRule.addEdge(node, notContLabel, node);
    }

    /**
     * Method to create no containment constraint rule for a given EReference
     * @param eReference the EReference
     */
    private void addNotContainmentConstraint(EReference eReference) {

        // There must be a containment flag for not-containment references
        // constraint
        String name =
            "constraint - " + GraphLabels.getLabelNoType(eReference)
                + " - containmentFlag";
        DefaultGraph constraintRule = createRuleGraph(name);

        DefaultNode node = constraintRule.addNode();

        DefaultLabel refLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eReference));
        DefaultLabel contLabel = DefaultLabel.createLabel("flag:containment");

        constraintRule.addEdge(node, refLabel, node);
        constraintRule.addEdge(node, contLabel, node);
    }

    /**
     * Method to create ordered constraint rules for a given StructuralFeature
     * @param eFeature the StructuralFeature
     */
    private void addOrderedConstraint(EStructuralFeature eFeature) {
        // Not two tail constraint rule
        String name =
            "constraint - " + GraphLabels.getLabelNoType(eFeature)
                + " - ordered not two tail";
        DefaultGraph constraintRule = createRuleGraph(name);

        DefaultNode sourceNode = constraintRule.addNode();
        DefaultNode featureNode1 = constraintRule.addNode();
        DefaultNode featureNode2 = constraintRule.addNode();
        DefaultNode notFeatureNode1 = constraintRule.addNode();
        DefaultNode notFeatureNode2 = constraintRule.addNode();

        DefaultLabel sourceLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eFeature.getEContainingClass()));
        DefaultLabel featureLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eFeature));
        DefaultLabel nameLabel = DefaultLabel.createLabel(eFeature.getName());
        DefaultLabel unequalLabel = DefaultLabel.createLabel("!=");
        DefaultLabel nextLabel = DefaultLabel.createLabel("next");
        DefaultLabel notLabel = DefaultLabel.createLabel("not:");

        constraintRule.addEdge(sourceNode, sourceLabel, sourceNode);
        constraintRule.addEdge(featureNode1, featureLabel, featureNode1);
        constraintRule.addEdge(featureNode2, featureLabel, featureNode2);
        constraintRule.addEdge(notFeatureNode1, featureLabel, notFeatureNode1);
        constraintRule.addEdge(notFeatureNode2, featureLabel, notFeatureNode2);
        constraintRule.addEdge(notFeatureNode1, notLabel, notFeatureNode1);
        constraintRule.addEdge(notFeatureNode2, notLabel, notFeatureNode2);
        constraintRule.addEdge(sourceNode, nameLabel, featureNode1);
        constraintRule.addEdge(sourceNode, nameLabel, featureNode2);
        constraintRule.addEdge(sourceNode, nameLabel, notFeatureNode1);
        constraintRule.addEdge(sourceNode, nameLabel, notFeatureNode2);
        constraintRule.addEdge(featureNode1, unequalLabel, featureNode2);
        constraintRule.addEdge(featureNode1, nextLabel, notFeatureNode1);
        constraintRule.addEdge(featureNode2, nextLabel, notFeatureNode2);

        // Not two head constraint rule
        constraintRule = createRuleGraph(name);
        name =
            "constraint - " + GraphLabels.getLabelNoType(eFeature)
                + " - ordered not two head";

        sourceNode = constraintRule.addNode();
        featureNode1 = constraintRule.addNode();
        featureNode2 = constraintRule.addNode();
        notFeatureNode1 = constraintRule.addNode();
        notFeatureNode2 = constraintRule.addNode();

        constraintRule.addEdge(sourceNode, sourceLabel, sourceNode);
        constraintRule.addEdge(featureNode1, featureLabel, featureNode1);
        constraintRule.addEdge(featureNode2, featureLabel, featureNode2);
        constraintRule.addEdge(notFeatureNode1, featureLabel, notFeatureNode1);
        constraintRule.addEdge(notFeatureNode2, featureLabel, notFeatureNode2);
        constraintRule.addEdge(notFeatureNode1, notLabel, notFeatureNode1);
        constraintRule.addEdge(notFeatureNode2, notLabel, notFeatureNode2);
        constraintRule.addEdge(sourceNode, nameLabel, featureNode1);
        constraintRule.addEdge(sourceNode, nameLabel, featureNode2);
        constraintRule.addEdge(sourceNode, nameLabel, notFeatureNode1);
        constraintRule.addEdge(sourceNode, nameLabel, notFeatureNode2);
        constraintRule.addEdge(featureNode1, unequalLabel, featureNode2);
        constraintRule.addEdge(notFeatureNode1, nextLabel, featureNode1);
        constraintRule.addEdge(notFeatureNode2, nextLabel, featureNode2);

        // Not two in constraint rule
        constraintRule = createRuleGraph(name);
        name =
            "constraint - " + GraphLabels.getLabelNoType(eFeature)
                + " - ordered not two in";

        sourceNode = constraintRule.addNode();
        featureNode1 = constraintRule.addNode();
        featureNode2 = constraintRule.addNode();
        DefaultNode featureNode3 = constraintRule.addNode();

        constraintRule.addEdge(sourceNode, sourceLabel, sourceNode);
        constraintRule.addEdge(featureNode1, featureLabel, featureNode1);
        constraintRule.addEdge(featureNode2, featureLabel, featureNode2);
        constraintRule.addEdge(featureNode3, featureLabel, featureNode3);
        constraintRule.addEdge(sourceNode, nameLabel, featureNode1);
        constraintRule.addEdge(sourceNode, nameLabel, featureNode2);
        constraintRule.addEdge(sourceNode, nameLabel, featureNode3);
        constraintRule.addEdge(featureNode1, unequalLabel, featureNode2);
        constraintRule.addEdge(featureNode1, nextLabel, featureNode3);
        constraintRule.addEdge(featureNode2, nextLabel, featureNode3);

        // Not two out constraint rule
        constraintRule = createRuleGraph(name);
        name =
            "constraint - " + GraphLabels.getLabelNoType(eFeature)
                + " - ordered not two out";

        sourceNode = constraintRule.addNode();
        featureNode1 = constraintRule.addNode();
        featureNode2 = constraintRule.addNode();
        featureNode3 = constraintRule.addNode();

        constraintRule.addEdge(sourceNode, sourceLabel, sourceNode);
        constraintRule.addEdge(featureNode1, featureLabel, featureNode1);
        constraintRule.addEdge(featureNode2, featureLabel, featureNode2);
        constraintRule.addEdge(featureNode3, featureLabel, featureNode3);
        constraintRule.addEdge(sourceNode, nameLabel, featureNode1);
        constraintRule.addEdge(sourceNode, nameLabel, featureNode2);
        constraintRule.addEdge(sourceNode, nameLabel, featureNode3);
        constraintRule.addEdge(featureNode1, unequalLabel, featureNode2);
        constraintRule.addEdge(featureNode3, nextLabel, featureNode1);
        constraintRule.addEdge(featureNode3, nextLabel, featureNode2);

        // Not circular constraint rule
        constraintRule = createRuleGraph(name);
        name =
            "constraint - " + GraphLabels.getLabelNoType(eFeature)
                + " - ordered not circular";

        sourceNode = constraintRule.addNode();
        featureNode1 = constraintRule.addNode();

        DefaultLabel nextPlusLabel = DefaultLabel.createLabel("path:next+");

        constraintRule.addEdge(sourceNode, sourceLabel, sourceNode);
        constraintRule.addEdge(featureNode1, featureLabel, featureNode1);
        constraintRule.addEdge(sourceNode, nameLabel, featureNode1);
        constraintRule.addEdge(featureNode1, nextPlusLabel, featureNode1);
    }

    /**
     * Method to create an eKeys constraint rule for a given EReference
     * @param eReference the EReference
     */
    private void addEkeysConstraint(EReference eReference) {
        String name =
            "constraint - " + GraphLabels.getLabelNoType(eReference)
                + " - ekeys";
        DefaultGraph constraintRule = createRuleGraph(name);

        DefaultNode sourceNode = constraintRule.addNode();
        DefaultNode refNode1 = constraintRule.addNode();
        DefaultNode refNode2 = constraintRule.addNode();
        DefaultNode targetNode1 = constraintRule.addNode();
        DefaultNode targetNode2 = constraintRule.addNode();

        DefaultLabel sourceLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eReference.getEContainingClass()));
        DefaultLabel refLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eReference));
        DefaultLabel targetLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eReference.getEReferenceType()));
        DefaultLabel nameLabel = DefaultLabel.createLabel(eReference.getName());
        DefaultLabel valLabel = DefaultLabel.createLabel("val");
        DefaultLabel unequalLabel = DefaultLabel.createLabel("!=");

        constraintRule.addEdge(sourceNode, sourceLabel, sourceNode);
        constraintRule.addEdge(refNode1, refLabel, refNode1);
        constraintRule.addEdge(refNode2, refLabel, refNode2);
        constraintRule.addEdge(targetNode1, targetLabel, targetNode1);
        constraintRule.addEdge(targetNode2, targetLabel, targetNode2);
        constraintRule.addEdge(sourceNode, nameLabel, refNode1);
        constraintRule.addEdge(sourceNode, nameLabel, refNode2);
        constraintRule.addEdge(refNode1, valLabel, targetNode1);
        constraintRule.addEdge(refNode2, valLabel, targetNode2);
        constraintRule.addEdge(targetNode1, unequalLabel, targetNode2);

        for (EAttribute eAttribute : eReference.getEKeys()) {

            // If Attribute is many, it must be non ordered and unique for a
            // constraint rule to be supported
            if (eAttribute.isMany()
                && (eAttribute.isOrdered() || !eAttribute.isUnique())) {
                return;
            }

            // check if datatype is an enum
            String targetString = "";
            if (eAttribute.getEAttributeType().eClass().getName().equals(
                "EEnum")) {
                addEnumCheck(constraintRule, eAttribute, targetNode1,
                    targetNode2);

                // Else, if the datatype is not serializable return; 
            } else {
                targetString =
                    GraphLabels.getLabel(eAttribute.getEAttributeType(), null);
                if (targetString.equals("")) {
                    return;
                } else {
                    addDataTypeCheck(constraintRule, eAttribute, targetNode1,
                        targetNode2);
                }
            }

        }
    }

    /**
     * Method to add an id constraint rule for a given EAttributes
     * @param eAttribute the EAttribute
     */
    private void addIDConstraint(EAttribute eAttribute) {
        // check if datatype is an Enum
        boolean isEnum = false;
        String targetString = "";
        if (eAttribute.getEAttributeType().eClass().getName().equals("EEnum")) {
            isEnum = true;

            // Else, if the datatype is not serializable return; 
        } else {
            targetString =
                GraphLabels.getLabel(eAttribute.getEAttributeType(), null);
            if (targetString.equals("")) {
                return;
            }
        }

        // If Attribute is many, it must be non ordered and unique for a
        // constraint rule to be supported
        if (eAttribute.isMany()
            && (eAttribute.isOrdered() || !eAttribute.isUnique())) {
            return;
        }

        // If class is root class, not constraint rule is needed and iD does
        // nothing
        EClass contEClass = null;
        EReference contEReference = null;
        for (EReference eReference : this.mh.getEReferences()) {
            if (eReference.getEReferenceType().equals(
                eAttribute.getEContainingClass())
                && eReference.isContainment()) {
                contEClass = eReference.getEContainingClass();
                contEReference = eReference;
                break;
            }
        }

        if (contEClass == null) {
            return;
        }

        // Create the constraint rule now
        String name =
            "constraint - " + GraphLabels.getLabelNoType(eAttribute) + " - id";
        DefaultGraph constraintRule = createRuleGraph(name);

        // Add the common part to the constraint rule
        DefaultNode contEClassNode = constraintRule.addNode();
        DefaultNode contERefNode1 = constraintRule.addNode();
        DefaultNode contERefNode2 = constraintRule.addNode();
        DefaultNode contNode1 = constraintRule.addNode();
        DefaultNode contNode2 = constraintRule.addNode();

        DefaultLabel contEClassLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(contEClass));
        DefaultLabel contERefLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(contEReference));
        DefaultLabel contLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eAttribute.getEContainingClass()));
        DefaultLabel refNameLabel =
            DefaultLabel.createLabel(contEReference.getName());
        DefaultLabel valLabel = DefaultLabel.createLabel("val");
        DefaultLabel unequalLabel = DefaultLabel.createLabel("!=");

        constraintRule.addEdge(contEClassNode, contEClassLabel, contEClassNode);
        constraintRule.addEdge(contERefNode1, contERefLabel, contERefNode1);
        constraintRule.addEdge(contERefNode2, contERefLabel, contERefNode2);
        constraintRule.addEdge(contNode1, contLabel, contNode1);
        constraintRule.addEdge(contNode2, contLabel, contNode2);
        constraintRule.addEdge(contEClassNode, refNameLabel, contERefNode1);
        constraintRule.addEdge(contEClassNode, refNameLabel, contERefNode2);
        constraintRule.addEdge(contERefNode1, valLabel, contNode1);
        constraintRule.addEdge(contERefNode2, valLabel, contNode2);
        constraintRule.addEdge(contNode1, unequalLabel, contNode2);

        // add enum or attribute specific part to the constraint rule
        if (isEnum) {
            addEnumCheck(constraintRule, eAttribute, contNode1, contNode2);
        } else {
            addDataTypeCheck(constraintRule, eAttribute, contNode1, contNode2);
        }
    }

    /**
     * method to create a constraint rule to check valid values of an
     * EAttribute
     * @param eAttribute the EAttribute
     */
    private void addValuesConstraint(EAttribute eAttribute) {
        String lowerBound = null;
        String upperBound = null;
        String type = null;

        if (eAttribute.getEAttributeType().getName().equals("EByte")
            || eAttribute.getEAttributeType().getName().equals("EByteObject")) {
            lowerBound = String.valueOf(Byte.MIN_VALUE);
            upperBound = String.valueOf(Byte.MAX_VALUE);
            type = "int:";
        } else if (eAttribute.getEAttributeType().getName().equals("EShort")
            || eAttribute.getEAttributeType().getName().equals("EShortObject")) {
            lowerBound = String.valueOf(Short.MIN_VALUE);
            upperBound = String.valueOf(Short.MAX_VALUE);
            type = "int:";
        } else if (eAttribute.getEAttributeType().getName().equals("EChar")
            || eAttribute.getEAttributeType().getName().equals("ECharObject")) {
            lowerBound = String.valueOf(java.lang.Character.MIN_VALUE);
            upperBound = String.valueOf(java.lang.Character.MAX_VALUE);
            type = "int:";
        } else if (eAttribute.getEAttributeType().getName().equals("ELong")
            || eAttribute.getEAttributeType().getName().equals("ELongObject")) {
            lowerBound = String.valueOf(Long.MIN_VALUE);
            upperBound = String.valueOf(Long.MAX_VALUE);
            type = "int:";
        } else if (eAttribute.getEAttributeType().getName().equals("EInt")
            || eAttribute.getEAttributeType().getName().equals("EIntegerObject")) {
            if (this.mh.isBigAlgebra()) {
                lowerBound = String.valueOf(Integer.MIN_VALUE);
                upperBound = String.valueOf(Integer.MAX_VALUE);
                type = "int:";
            } else {
                return;
            }
        } else {
            return;
        }

        String name =
            "constraint - " + GraphLabels.getLabelNoType(eAttribute)
                + " - validValues";
        DefaultGraph constraintRule = createRuleGraph(name);

        DefaultNode attrNode = constraintRule.addNode();
        DefaultNode datatypeNode = constraintRule.addNode();
        DefaultNode lowerBoundNode = constraintRule.addNode();
        DefaultNode upperBoundNode = constraintRule.addNode();
        DefaultNode compare1Node = constraintRule.addNode();
        DefaultNode compare2Node = constraintRule.addNode();
        DefaultNode compare3Node = constraintRule.addNode();
        DefaultNode bool1Node = constraintRule.addNode();
        DefaultNode bool2Node = constraintRule.addNode();
        DefaultNode bool3Node = constraintRule.addNode();

        DefaultLabel attrLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eAttribute));
        DefaultLabel datatypeLabel = DefaultLabel.createLabel(type);
        DefaultLabel lowerBoundLabel =
            DefaultLabel.createLabel(type + lowerBound);
        DefaultLabel upperBoundLabel =
            DefaultLabel.createLabel(type + upperBound);
        DefaultLabel boolLabel = DefaultLabel.createLabel("bool:");
        DefaultLabel trueLabel = DefaultLabel.createLabel("bool:true");
        DefaultLabel valLabel = DefaultLabel.createLabel("val");
        DefaultLabel arg1Label = DefaultLabel.createLabel("arg:0");
        DefaultLabel arg2Label = DefaultLabel.createLabel("arg:1");
        DefaultLabel gtLabel = DefaultLabel.createLabel(type + "gt");
        DefaultLabel ltLabel = DefaultLabel.createLabel(type + "lt");
        DefaultLabel orLabel = DefaultLabel.createLabel("bool:or");

        constraintRule.addEdge(attrNode, attrLabel, attrNode);
        constraintRule.addEdge(datatypeNode, datatypeLabel, datatypeNode);
        constraintRule.addEdge(lowerBoundNode, lowerBoundLabel, lowerBoundNode);
        constraintRule.addEdge(upperBoundNode, upperBoundLabel, upperBoundNode);
        constraintRule.addEdge(bool1Node, boolLabel, bool1Node);
        constraintRule.addEdge(bool2Node, boolLabel, bool2Node);
        constraintRule.addEdge(bool3Node, trueLabel, bool3Node);
        constraintRule.addEdge(attrNode, valLabel, datatypeNode);
        constraintRule.addEdge(compare1Node, arg1Label, datatypeNode);
        constraintRule.addEdge(compare1Node, arg2Label, upperBoundNode);
        constraintRule.addEdge(compare1Node, gtLabel, bool1Node);
        constraintRule.addEdge(compare2Node, arg1Label, datatypeNode);
        constraintRule.addEdge(compare2Node, arg2Label, lowerBoundNode);
        constraintRule.addEdge(compare2Node, ltLabel, bool2Node);
        constraintRule.addEdge(compare3Node, arg1Label, bool1Node);
        constraintRule.addEdge(compare3Node, arg2Label, bool2Node);
        constraintRule.addEdge(compare3Node, orLabel, bool3Node);

    }

    /**
     * Method to add unchangeable constraint rule for a given EAttribute
     * @param eAttribute the EAttribute
     */
    private void addUnchangeableConstraint(EAttribute eAttribute) {
        String targetString =
            GraphLabels.getLabel(eAttribute.getEAttributeType(), null);
        String defaultString =
            GraphLabels.getLabel(eAttribute.getEAttributeType(),
                eAttribute.getDefaultValue());
        if (targetString.equals("") || defaultString.equals("")) {
            return;
        }

        String name =
            "constraint - " + GraphLabels.getLabelNoType(eAttribute)
                + " - unchangeable";
        DefaultGraph constraintRule = createRuleGraph(name);

        DefaultNode attrNode = constraintRule.addNode();
        DefaultNode datatypeNode = constraintRule.addNode();
        DefaultNode defaultValNode = constraintRule.addNode();
        DefaultNode compareNode = constraintRule.addNode();
        DefaultNode boolNode = constraintRule.addNode();

        DefaultLabel attrLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eAttribute));
        DefaultLabel datatypeLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(
                eAttribute.getEAttributeType(), null));
        DefaultLabel defaultValLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(
                eAttribute.getEAttributeType(), eAttribute.getDefaultValue()));
        DefaultLabel boolLabel = DefaultLabel.createLabel("bool:false");
        DefaultLabel compareLabel =
            DefaultLabel.createLabel(datatypeLabel + "eq");
        DefaultLabel valLabel = DefaultLabel.createLabel("val");
        DefaultLabel arg1Label = DefaultLabel.createLabel("arg:0");
        DefaultLabel arg2Label = DefaultLabel.createLabel("arg:1");

        constraintRule.addEdge(attrNode, attrLabel, attrNode);
        constraintRule.addEdge(datatypeNode, datatypeLabel, datatypeNode);
        constraintRule.addEdge(defaultValNode, defaultValLabel, defaultValNode);
        constraintRule.addEdge(boolNode, boolLabel, boolNode);
        constraintRule.addEdge(attrNode, valLabel, datatypeNode);
        constraintRule.addEdge(compareNode, arg1Label, datatypeNode);
        constraintRule.addEdge(compareNode, arg2Label, defaultValNode);
        constraintRule.addEdge(compareNode, compareLabel, boolNode);
    }

    /**
     * Method to add opposite constraint rule for a given EReference
     * @param eReference the EReference
     */
    private void addOppositeConstraint(EReference eReference) {
        // Add constraint that there must be an out- and incoming
        // opposite edge
        String name =
            "constraint - " + GraphLabels.getLabelNoType(eReference)
                + " - opposite";
        DefaultGraph constraintRule = createRuleGraph(name);

        DefaultNode refNode = constraintRule.addNode();

        DefaultLabel refLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eReference));
        DefaultLabel regExprLabel =
            DefaultLabel.createLabel("path:!opposite.opposite");

        constraintRule.addEdge(refNode, refLabel, refNode);
        constraintRule.addEdge(refNode, regExprLabel, refNode);

        // Add constraint that there may not be two outgoing opposite edges
        name =
            "constraint - " + GraphLabels.getLabelNoType(eReference)
                + " - not two opposite";
        constraintRule = createRuleGraph(name);

        refNode = constraintRule.addNode();
        DefaultNode erefNode1 = constraintRule.addNode();
        DefaultNode erefNode2 = constraintRule.addNode();

        EReference opposite = eReference.getEOpposite();
        DefaultLabel oppositeLabel = DefaultLabel.createLabel("opposite");
        DefaultLabel erefLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(opposite));
        //DefaultLabel erefLabel = DefaultLabel.createLabel(this.mh.getEReferenceType());
        DefaultLabel unequalLabel = DefaultLabel.createLabel("!=");

        constraintRule.addEdge(refNode, refLabel, refNode);
        constraintRule.addEdge(erefNode1, erefLabel, erefNode1);
        constraintRule.addEdge(erefNode2, erefLabel, erefNode2);
        constraintRule.addEdge(refNode, oppositeLabel, erefNode1);
        constraintRule.addEdge(refNode, oppositeLabel, erefNode2);
        constraintRule.addEdge(erefNode1, unequalLabel, erefNode2);
    }

    /**
     * Method to add lower bound constraint rule for a given EStructuralFeature
     * @param eFeature the EStructuralFeature
     */
    private void addLowerBoundConstraint(EStructuralFeature eFeature) {
        int lowerBound = eFeature.getLowerBound();
        String name =
            "constraint - " + GraphLabels.getLabelNoType(eFeature)
                + " - lowerBound";
        DefaultGraph constraintRule = createRuleGraph(name);

        DefaultNode sourceNode = constraintRule.addNode();
        Vector<DefaultNode> featureNodes = new Vector<DefaultNode>();

        DefaultLabel sourceLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eFeature.getEContainingClass()));
        DefaultLabel featureLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eFeature));
        DefaultLabel nameLabel = DefaultLabel.createLabel(eFeature.getName());
        DefaultLabel unequalLabel = DefaultLabel.createLabel("!=");
        DefaultLabel notLabel = DefaultLabel.createLabel("not:");

        constraintRule.addEdge(sourceNode, sourceLabel, sourceNode);

        // First add a lowerBound amount of featureNodes
        for (int i = 0; i < lowerBound; i++) {
            DefaultNode featureNode = constraintRule.addNode();
            constraintRule.addEdge(featureNode, featureLabel, featureNode);
            constraintRule.addEdge(featureNode, notLabel, featureNode);
            constraintRule.addEdge(sourceNode, nameLabel, featureNode);
            featureNodes.add(featureNode);
        }

        // Then add unequal edges between each combination of two	
        for (int i = 0; i < lowerBound - 1; i++) {
            for (int j = i + 1; j < lowerBound; j++) {
                constraintRule.addEdge(featureNodes.get(i), unequalLabel,
                    featureNodes.get(j));
            }
        }
    }

    /**
     * Method to add upper bound constraint rule for a given EStructuralFeature
     * @param eFeature the EStructuralFeature
     */
    private void addUpperBoundConstraint(EStructuralFeature eFeature) {
        int upperBound = eFeature.getUpperBound();
        if (upperBound == -1) {
            return;
        }
        String name =
            "constraint - " + GraphLabels.getLabelNoType(eFeature)
                + " - upperBound";
        DefaultGraph constraintRule = createRuleGraph(name);

        DefaultNode sourceNode = constraintRule.addNode();
        Vector<DefaultNode> featureNodes = new Vector<DefaultNode>();

        DefaultLabel sourceLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eFeature.getEContainingClass()));
        DefaultLabel featureLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eFeature));
        DefaultLabel nameLabel = DefaultLabel.createLabel(eFeature.getName());
        DefaultLabel unequalLabel = DefaultLabel.createLabel("!=");

        constraintRule.addEdge(sourceNode, sourceLabel, sourceNode);

        // First add a lowerBound amount of featureNodes
        for (int i = 0; i < upperBound + 1; i++) {
            DefaultNode featureNode = constraintRule.addNode();
            constraintRule.addEdge(featureNode, featureLabel, featureNode);
            constraintRule.addEdge(sourceNode, nameLabel, featureNode);
            featureNodes.add(featureNode);
        }

        // Then add unequal edges between each combination of two	
        for (int i = 0; i < upperBound; i++) {
            for (int j = i + 1; j < upperBound + 1; j++) {
                constraintRule.addEdge(featureNodes.get(i), unequalLabel,
                    featureNodes.get(j));
            }
        }
    }

    /**
     * Method to add unique constraint rule for a given EAttribute
     * @param eAttribute the EAttribute
     */
    private void addUniqueAttrConstraint(EAttribute eAttribute) {

        if (eAttribute.getEAttributeType().eClass().getName().equals("EEnum")) {
            addUniqueEnumAttrConstraint(eAttribute);
            return;
        }

        String targetString =
            GraphLabels.getLabel(eAttribute.getEAttributeType(), null);
        if (targetString.equals("")) {
            return;
        }

        String name =
            "constraint - " + GraphLabels.getLabelNoType(eAttribute)
                + " - unique";
        DefaultGraph constraintRule = createRuleGraph(name);

        DefaultNode sourceNode = constraintRule.addNode();
        DefaultNode targetNode = constraintRule.addNode();
        DefaultNode refNode1 = constraintRule.addNode();
        DefaultNode refNode2 = constraintRule.addNode();

        DefaultLabel sourceLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eAttribute.getEContainingClass()));
        DefaultLabel refLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eAttribute));
        DefaultLabel targetLabel = DefaultLabel.createLabel(targetString);
        DefaultLabel unequalLabel = DefaultLabel.createLabel("!=");
        DefaultLabel valLabel = DefaultLabel.createLabel("val");
        DefaultLabel nameLabel = DefaultLabel.createLabel(eAttribute.getName());

        constraintRule.addEdge(sourceNode, sourceLabel, sourceNode);
        constraintRule.addEdge(refNode1, refLabel, refNode1);
        constraintRule.addEdge(refNode2, refLabel, refNode2);
        constraintRule.addEdge(targetNode, targetLabel, targetNode);
        constraintRule.addEdge(sourceNode, nameLabel, refNode1);
        constraintRule.addEdge(sourceNode, nameLabel, refNode2);
        constraintRule.addEdge(refNode1, unequalLabel, refNode2);
        constraintRule.addEdge(refNode1, valLabel, targetNode);
        constraintRule.addEdge(refNode2, valLabel, targetNode);
    }

    /**
     * Method to add unique constraint rule for a given EAttribute that is of
     * and EEnum type
     * @param eAttribute the EAttribute
     * @require eAttribute.eAttributeType is EEnum
     */
    private void addUniqueEnumAttrConstraint(EAttribute eAttribute) {
        String name =
            "constraint - " + GraphLabels.getLabelNoType(eAttribute)
                + " - unique";
        DefaultGraph constraintRule = createRuleGraph(name);

        DefaultNode sourceNode = constraintRule.addNode();
        DefaultNode attrNode1 = constraintRule.addNode();
        DefaultNode attrNode2 = constraintRule.addNode();
        DefaultNode targetNode1 = constraintRule.addNode();
        DefaultNode targetNode2 = constraintRule.addNode();

        DefaultLabel sourceLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eAttribute.getEContainingClass()));
        DefaultLabel attrLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eAttribute));
        DefaultLabel targetLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel((EEnum) eAttribute.getEAttributeType()));
        DefaultLabel unequalLabel = DefaultLabel.createLabel("!=");
        DefaultLabel valLabel = DefaultLabel.createLabel("val");
        DefaultLabel nameLabel = DefaultLabel.createLabel(eAttribute.getName());
        DefaultLabel matchLabel = DefaultLabel.createLabel("flag:?x");
        DefaultLabel lookLabel = DefaultLabel.createLabel("flag:?x");

        constraintRule.addEdge(sourceNode, sourceLabel, sourceNode);
        constraintRule.addEdge(attrNode1, attrLabel, attrNode1);
        constraintRule.addEdge(attrNode2, attrLabel, attrNode2);
        constraintRule.addEdge(targetNode1, targetLabel, targetNode1);
        constraintRule.addEdge(targetNode2, targetLabel, targetNode2);
        constraintRule.addEdge(attrNode1, unequalLabel, attrNode2);
        constraintRule.addEdge(sourceNode, nameLabel, attrNode1);
        constraintRule.addEdge(sourceNode, nameLabel, attrNode2);
        constraintRule.addEdge(attrNode1, valLabel, targetNode1);
        constraintRule.addEdge(attrNode2, valLabel, targetNode2);
        constraintRule.addEdge(targetNode1, lookLabel, targetNode1);
        constraintRule.addEdge(targetNode2, matchLabel, targetNode2);

    }

    /**
     * Method to add unique constraint rule for a given EReference
     * @param eReference the EReference
     */
    private void addUniqueRefConstraint(EReference eReference) {
        String name =
            "constraint - " + GraphLabels.getLabelNoType(eReference)
                + " - unique";
        DefaultGraph constraintRule = createRuleGraph(name);

        DefaultNode sourceNode = constraintRule.addNode();
        DefaultNode targetNode = constraintRule.addNode();
        DefaultNode refNode1 = constraintRule.addNode();
        DefaultNode refNode2 = constraintRule.addNode();

        DefaultLabel sourceLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eReference.getEContainingClass()));
        DefaultLabel refLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eReference));
        DefaultLabel targetLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eReference.getEReferenceType()));
        DefaultLabel unequalLabel = DefaultLabel.createLabel("!=");
        DefaultLabel valLabel = DefaultLabel.createLabel("val");
        DefaultLabel nameLabel = DefaultLabel.createLabel(eReference.getName());

        constraintRule.addEdge(sourceNode, sourceLabel, sourceNode);
        constraintRule.addEdge(targetNode, targetLabel, targetNode);
        constraintRule.addEdge(refNode1, refLabel, refNode1);
        constraintRule.addEdge(refNode2, refLabel, refNode2);
        constraintRule.addEdge(sourceNode, nameLabel, refNode1);
        constraintRule.addEdge(sourceNode, nameLabel, refNode2);
        constraintRule.addEdge(refNode1, valLabel, targetNode);
        constraintRule.addEdge(refNode2, valLabel, targetNode);
        constraintRule.addEdge(refNode1, unequalLabel, refNode2);
    }

    /**
     * Method to add no container constraint rule for a given
     * EStructuralFeature
     * @param eStructuralFeature the EStructuralFeature
     */
    private void addNoContainerConstraint(EStructuralFeature eStructuralFeature) {
        String name =
            "constraint - " + GraphLabels.getLabelNoType(eStructuralFeature)
                + " - noContainer";
        DefaultGraph constraintRule = createRuleGraph(name);

        DefaultNode featureNode = constraintRule.addNode();
        DefaultNode containerNode = constraintRule.addNode();

        DefaultLabel featureLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eStructuralFeature));
        DefaultLabel containerLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eStructuralFeature.getEContainingClass()));
        DefaultLabel notLabel = DefaultLabel.createLabel("not:");
        DefaultLabel nameLabel =
            DefaultLabel.createLabel(eStructuralFeature.getName());

        constraintRule.addEdge(featureNode, featureLabel, featureNode);
        constraintRule.addEdge(containerNode, containerLabel, containerNode);
        constraintRule.addEdge(containerNode, notLabel, containerNode);
        constraintRule.addEdge(containerNode, nameLabel, featureNode);
    }

    /**
     * Method to add many container constraint rule for a given
     * EStructuralFeature
     * @param eStructuralFeature the EStructuralFeature
     */
    private void addManyContainerConstraint(
            EStructuralFeature eStructuralFeature) {
        String name =
            "constraint - " + GraphLabels.getLabelNoType(eStructuralFeature)
                + " - manyContainers";
        DefaultGraph constraintRule = createRuleGraph(name);

        DefaultNode featureNode = constraintRule.addNode();
        DefaultNode containerNode1 = constraintRule.addNode();
        DefaultNode containerNode2 = constraintRule.addNode();

        DefaultLabel featureLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eStructuralFeature));
        DefaultLabel containerLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eStructuralFeature.getEContainingClass()));
        DefaultLabel nameLabel =
            DefaultLabel.createLabel(eStructuralFeature.getName());
        DefaultLabel unequalLabel = DefaultLabel.createLabel("!=");

        constraintRule.addEdge(featureNode, featureLabel, featureNode);
        constraintRule.addEdge(containerNode1, containerLabel, containerNode1);
        constraintRule.addEdge(containerNode2, containerLabel, containerNode2);
        constraintRule.addEdge(containerNode1, nameLabel, featureNode);
        constraintRule.addEdge(containerNode2, nameLabel, featureNode);
        constraintRule.addEdge(containerNode1, unequalLabel, containerNode2);
    }

    /**
     * Method to add no val constraint rule for a given EStructuralFeature
     * @param eStructuralFeature the EStructuralFeature
     */
    private void addNoValConstraint(EStructuralFeature eStructuralFeature) {
        String name =
            "constraint - " + GraphLabels.getLabelNoType(eStructuralFeature)
                + " - noVal";
        DefaultGraph constraintRule = createRuleGraph(name);

        DefaultNode featureNode = constraintRule.addNode();
        DefaultNode targetNode = constraintRule.addNode();

        DefaultLabel featureLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eStructuralFeature));
        DefaultLabel targetLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eStructuralFeature.getEType()));
        DefaultLabel notLabel = DefaultLabel.createLabel("not:");
        DefaultLabel valLabel = DefaultLabel.createLabel("val");

        constraintRule.addEdge(featureNode, featureLabel, featureNode);
        constraintRule.addEdge(targetNode, targetLabel, targetNode);
        constraintRule.addEdge(targetNode, notLabel, targetNode);
        constraintRule.addEdge(featureNode, valLabel, targetNode);
    }

    /**
     * Method to add many val constraint rule for a given EStructuralFeature
     * @param eStructuralFeature the EStructuralFeature
     */
    private void addManyValConstraint(EStructuralFeature eStructuralFeature) {
        // type is different for EAttributes and EReferences
        String targetString;
        if (eStructuralFeature instanceof EAttribute) {
            targetString =
                GraphLabels.getLabel(((EAttribute) eStructuralFeature).getEAttributeType());
            if (targetString.equals("")) {
                return;
            }
        } else {
            targetString = GraphLabels.getLabel(eStructuralFeature.getEType());
        }

        String name =
            "constraint - " + GraphLabels.getLabelNoType(eStructuralFeature)
                + " - manyVals";
        DefaultGraph constraintRule = createRuleGraph(name);

        DefaultNode featureNode = constraintRule.addNode();
        DefaultNode targetNode1 = constraintRule.addNode();
        DefaultNode targetNode2 = constraintRule.addNode();

        DefaultLabel featureLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eStructuralFeature));
        DefaultLabel targetLabel = DefaultLabel.createLabel(targetString);
        DefaultLabel valLabel = DefaultLabel.createLabel("val");
        DefaultLabel unequalLabel = DefaultLabel.createLabel("!=");

        constraintRule.addEdge(featureNode, featureLabel, featureNode);
        constraintRule.addEdge(targetNode1, targetLabel, targetNode1);
        constraintRule.addEdge(targetNode2, targetLabel, targetNode2);
        constraintRule.addEdge(featureNode, valLabel, targetNode1);
        constraintRule.addEdge(featureNode, valLabel, targetNode2);
        constraintRule.addEdge(targetNode1, unequalLabel, targetNode2);
    }

    /**
     * Method to add no val constraint rule for a given EEnum
     * @param eEnum the EEnum
     */
    private void addNoIncValConstraint(EEnum eEnum) {
        String name =
            "constraint - " + GraphLabels.getLabelNoType(eEnum) + " - noIncVal";
        DefaultGraph constraintRule = createRuleGraph(name);

        DefaultNode enumNode = constraintRule.addNode();
        DefaultNode attrNode = constraintRule.addNode();

        DefaultLabel enumLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eEnum));
        DefaultLabel attrLabel =
            DefaultLabel.createLabel(this.mh.getEAttributeType());
        DefaultLabel valLabel = DefaultLabel.createLabel("val");
        DefaultLabel notLabel = DefaultLabel.createLabel("not:");

        constraintRule.addEdge(enumNode, enumLabel, enumNode);
        constraintRule.addEdge(attrNode, attrLabel, attrNode);
        constraintRule.addEdge(attrNode, notLabel, attrNode);
        constraintRule.addEdge(attrNode, valLabel, enumNode);

    }

    /**
     * Method to add many val constraint rule for a given EEnum
     * @param eEnum the EEnum
     */
    private void addManyIncValConstraint(EEnum eEnum) {
        String name =
            "constraint - " + GraphLabels.getLabelNoType(eEnum)
                + " - manyIncVal";
        DefaultGraph constraintRule = createRuleGraph(name);

        DefaultNode enumNode = constraintRule.addNode();
        DefaultNode attrNode1 = constraintRule.addNode();
        DefaultNode attrNode2 = constraintRule.addNode();

        DefaultLabel enumLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eEnum));
        DefaultLabel attrLabel =
            DefaultLabel.createLabel(this.mh.getEAttributeType());
        DefaultLabel unequalLabel = DefaultLabel.createLabel("!=");
        DefaultLabel regExprLabel =
            DefaultLabel.createLabel("path:val." + GraphLabels.getLabel(eEnum));

        constraintRule.addEdge(enumNode, enumLabel, enumNode);
        constraintRule.addEdge(attrNode1, attrLabel, attrNode1);
        constraintRule.addEdge(attrNode2, attrLabel, attrNode2);
        constraintRule.addEdge(attrNode1, unequalLabel, attrNode2);
        constraintRule.addEdge(attrNode1, regExprLabel, enumNode);
        constraintRule.addEdge(attrNode2, regExprLabel, enumNode);

    }

    /**
     * Method to add no literals constraint rule for a given EEnum
     * @param eEnum the EEnum
     */
    private void addNoLiteralsConstraint(EEnum eEnum) {
        String name =
            "constraint - " + GraphLabels.getLabelNoType(eEnum)
                + " - noLiteral";
        DefaultGraph constraintRule = createRuleGraph(name);

        DefaultNode node = addEnumNode(constraintRule, eEnum);
        for (EEnumLiteral eEnumLiteral : eEnum.getELiterals()) {
            String labelText = GraphLabels.getLabel(eEnumLiteral);
            constraintRule.addEdge(node, "not:" + labelText, node);
        }
    }

    /**
     * Method to add many literals constraint rule for a given EEnum
     * @param eEnum the EEnum
     */
    private void addManyLiteralsConstraint(EEnum eEnum) {

        if (eEnum.getELiterals().size() > 1) {
            String name =
                "constraint - " + GraphLabels.getLabelNoType(eEnum)
                    + " - manyLiterals";
            DefaultGraph constraintRule = createRuleGraph(name);

            DefaultNode node = addEnumNode(constraintRule, eEnum);
            StringBuilder regExpr = new StringBuilder();
            EList<EEnumLiteral> literals = eEnum.getELiterals();
            for (int i = 0; i < literals.size() - 1; i++) {
                regExpr.append(GraphLabels.getLabel(literals.get(i)) + ".(");
                for (int j = i + 1; j < literals.size(); j++) {
                    regExpr.append(GraphLabels.getLabel(literals.get(j)));
                    if (j < literals.size() - 1) {
                        regExpr.append("|");
                    }
                }
                regExpr.append(")");
                if (i < literals.size() - 2) {
                    regExpr.append("|");
                }
            }

            String regExprStr = "path:" + regExpr.toString();

            constraintRule.addEdge(node, regExprStr, node);
        }

    }

    /**
     * Method to add abstract constraint rule for a given EClass
     * @param eClass the EClass
     */
    private void addAbstractConstraint(EClass eClass) {
        String name =
            "constraint - " + GraphLabels.getLabelNoType(eClass)
                + " - abstract";
        DefaultGraph constraintRule = createRuleGraph(name);

        DefaultNode node = addClassNode(constraintRule, eClass);

        for (EClass otherClass : this.mh.getEClasses()) {
            if (eClass.isSuperTypeOf(otherClass) && !eClass.equals(otherClass)) {
                String subClassLabel =
                    "not:" + GraphLabels.getLabel(otherClass);
                constraintRule.addEdge(node, subClassLabel, node);
            }
        }
    }

    /**
     * Method to add a node representing an EClass to a given constraint rule
     * @param constraintRule the constraint rule
     * @param eClass the EClass
     * @return the new node representing the EClass
     */
    private DefaultNode addClassNode(DefaultGraph constraintRule, EClass eClass) {
        DefaultNode node = constraintRule.addNode();
        constraintRule.addEdge(node, GraphLabels.getLabel(eClass), node);

        return node;
    }

    /**
     * Method to add a node representing an EEnum to a given constraint rule
     * @param constraintRule the constraint rule
     * @param eEnum the EEnum
     * @return the new node representing the EEnum
     */
    private DefaultNode addEnumNode(DefaultGraph constraintRule, EEnum eEnum) {
        DefaultNode node = constraintRule.addNode();
        constraintRule.addEdge(node, GraphLabels.getLabel(eEnum), node);
        return node;
    }

    /**
     * Adds nodes and edges to a constraint rule to check that values of two 
     * EAttributes nodes of EEnum type are unique
     * @param constraintRule the constraint rule
     * @param eAttribute the EAttribute
     * @param node1 the first node representing the EAttribute
     * @param node2 the second node representing the EAttribute
     */
    private void addEnumCheck(DefaultGraph constraintRule,
            EAttribute eAttribute, DefaultNode node1, DefaultNode node2) {

        DefaultLabel attrLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eAttribute));
        DefaultLabel attrNameLabel =
            DefaultLabel.createLabel(eAttribute.getName());
        DefaultLabel enumLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel((EEnum) eAttribute.getEAttributeType()));
        DefaultLabel valLabel = DefaultLabel.createLabel("val");

        DefaultNode attrA1Node = constraintRule.addNode();
        DefaultNode attrA2Node = constraintRule.addNode();
        DefaultNode attrB1Node = constraintRule.addNode();
        DefaultNode attrB2Node = constraintRule.addNode();
        DefaultNode enumA1Node = constraintRule.addNode();
        DefaultNode enumA2Node = constraintRule.addNode();
        DefaultNode enumB1Node = constraintRule.addNode();
        DefaultNode enumB2Node = constraintRule.addNode();
        DefaultNode forallANode = constraintRule.addNode();
        DefaultNode forallBNode = constraintRule.addNode();
        DefaultNode existsANode = constraintRule.addNode();
        DefaultNode existsBNode = constraintRule.addNode();

        DefaultLabel wildcardALabel =
            DefaultLabel.createLabel("flag:?" + eAttribute.getName() + "_A");
        DefaultLabel wildcardBLabel =
            DefaultLabel.createLabel("flag:?" + eAttribute.getName() + "_B");
        DefaultLabel atLabel = DefaultLabel.createLabel("at");
        DefaultLabel inLabel = DefaultLabel.createLabel("in");
        DefaultLabel forallLabel = DefaultLabel.createLabel("forall:");
        DefaultLabel existsLabel = DefaultLabel.createLabel("exists:");

        constraintRule.addEdge(attrA1Node, attrLabel, attrA1Node);
        constraintRule.addEdge(attrA2Node, attrLabel, attrA2Node);
        constraintRule.addEdge(attrB1Node, attrLabel, attrB1Node);
        constraintRule.addEdge(attrB2Node, attrLabel, attrB2Node);
        constraintRule.addEdge(enumA1Node, enumLabel, enumA1Node);
        constraintRule.addEdge(enumA2Node, enumLabel, enumA2Node);
        constraintRule.addEdge(enumB1Node, enumLabel, enumB1Node);
        constraintRule.addEdge(enumB2Node, enumLabel, enumB2Node);
        constraintRule.addEdge(enumA1Node, wildcardALabel, enumA1Node);
        constraintRule.addEdge(enumA2Node, wildcardALabel, enumA2Node);
        constraintRule.addEdge(enumB1Node, wildcardBLabel, enumB1Node);
        constraintRule.addEdge(enumB2Node, wildcardBLabel, enumB2Node);
        constraintRule.addEdge(forallANode, forallLabel, forallANode);
        constraintRule.addEdge(forallBNode, forallLabel, forallBNode);
        constraintRule.addEdge(existsANode, existsLabel, existsANode);
        constraintRule.addEdge(existsBNode, existsLabel, existsBNode);
        constraintRule.addEdge(node1, attrNameLabel, attrA1Node);
        constraintRule.addEdge(node1, attrNameLabel, attrB2Node);
        constraintRule.addEdge(node2, attrNameLabel, attrA2Node);
        constraintRule.addEdge(node2, attrNameLabel, attrB1Node);
        constraintRule.addEdge(attrA1Node, valLabel, enumA1Node);
        constraintRule.addEdge(attrA2Node, valLabel, enumA2Node);
        constraintRule.addEdge(attrB1Node, valLabel, enumB1Node);
        constraintRule.addEdge(attrB2Node, valLabel, enumB2Node);
        constraintRule.addEdge(attrA1Node, atLabel, forallANode);
        constraintRule.addEdge(enumA1Node, atLabel, forallANode);
        constraintRule.addEdge(enumA2Node, atLabel, existsANode);
        constraintRule.addEdge(existsANode, inLabel, forallANode);
        constraintRule.addEdge(attrA2Node, atLabel, existsANode);
        constraintRule.addEdge(attrB1Node, atLabel, forallBNode);
        constraintRule.addEdge(enumB1Node, atLabel, forallBNode);
        constraintRule.addEdge(enumB2Node, atLabel, existsBNode);
        constraintRule.addEdge(existsBNode, inLabel, forallBNode);
        constraintRule.addEdge(attrB2Node, atLabel, existsBNode);
    }

    /**
     * Adds nodes and edges to a constraint rule to check that values of two 
     * EAttributes nodes not of EEnum type are unique
     * @param constraintRule the constraint rule
     * @param eAttribute the EAttribute
     * @param node1 the first node representing the EAttribute
     * @param node2 the second node representing the EAttribute
     */
    void addDataTypeCheck(DefaultGraph constraintRule, EAttribute eAttribute,
            DefaultNode node1, DefaultNode node2) {
        String datatypeString =
            GraphLabels.getLabel(eAttribute.getEAttributeType(), null);
        DefaultLabel dataTypeLabel = DefaultLabel.createLabel(datatypeString);
        DefaultLabel attrLabel =
            DefaultLabel.createLabel(GraphLabels.getLabel(eAttribute));
        DefaultLabel attrNameLabel =
            DefaultLabel.createLabel(eAttribute.getName());
        DefaultLabel valLabel = DefaultLabel.createLabel("val");

        DefaultNode attrA1Node = constraintRule.addNode();
        DefaultNode attrA2Node = constraintRule.addNode();
        DefaultNode attrB1Node = constraintRule.addNode();
        DefaultNode attrB2Node = constraintRule.addNode();
        DefaultNode dataTypeANode = constraintRule.addNode();
        DefaultNode dataTypeBNode = constraintRule.addNode();
        DefaultNode forallANode = constraintRule.addNode();
        DefaultNode forallBNode = constraintRule.addNode();
        DefaultNode existsANode = constraintRule.addNode();
        DefaultNode existsBNode = constraintRule.addNode();

        DefaultLabel atLabel = DefaultLabel.createLabel("at");
        DefaultLabel inLabel = DefaultLabel.createLabel("in");
        DefaultLabel forallLabel = DefaultLabel.createLabel("forall:");
        DefaultLabel existsLabel = DefaultLabel.createLabel("exists:");

        constraintRule.addEdge(attrA1Node, attrLabel, attrA1Node);
        constraintRule.addEdge(attrA2Node, attrLabel, attrA2Node);
        constraintRule.addEdge(attrB1Node, attrLabel, attrB1Node);
        constraintRule.addEdge(attrB2Node, attrLabel, attrB2Node);
        constraintRule.addEdge(dataTypeANode, dataTypeLabel, dataTypeANode);
        constraintRule.addEdge(dataTypeBNode, dataTypeLabel, dataTypeBNode);
        constraintRule.addEdge(forallANode, forallLabel, forallANode);
        constraintRule.addEdge(forallBNode, forallLabel, forallBNode);
        constraintRule.addEdge(existsANode, existsLabel, existsANode);
        constraintRule.addEdge(existsBNode, existsLabel, existsBNode);
        constraintRule.addEdge(node1, attrNameLabel, attrA1Node);
        constraintRule.addEdge(node1, attrNameLabel, attrB2Node);
        constraintRule.addEdge(node2, attrNameLabel, attrA2Node);
        constraintRule.addEdge(node2, attrNameLabel, attrB1Node);
        constraintRule.addEdge(attrA1Node, valLabel, dataTypeANode);
        constraintRule.addEdge(attrA2Node, valLabel, dataTypeANode);
        constraintRule.addEdge(attrB1Node, valLabel, dataTypeBNode);
        constraintRule.addEdge(attrB2Node, valLabel, dataTypeBNode);
        constraintRule.addEdge(attrA1Node, atLabel, forallANode);
        constraintRule.addEdge(dataTypeANode, atLabel, forallANode);
        constraintRule.addEdge(existsANode, inLabel, forallANode);
        constraintRule.addEdge(attrA2Node, atLabel, existsANode);
        constraintRule.addEdge(attrB1Node, atLabel, forallBNode);
        constraintRule.addEdge(dataTypeBNode, atLabel, forallBNode);
        constraintRule.addEdge(existsBNode, inLabel, forallBNode);
        constraintRule.addEdge(attrB2Node, atLabel, existsBNode);

    }

    /**
     * Get the constraint rules created by this class
     * @return the constraint rules
     */
    public List<DefaultGraph> getConstraints() {
        return this.constraintRules;
    }

    private DefaultGraph createRuleGraph(String name) {
        DefaultGraph result = new DefaultGraph(name);
        result.setRole(RULE);
        this.constraintRules.add(result);
        return result;
    }
}
