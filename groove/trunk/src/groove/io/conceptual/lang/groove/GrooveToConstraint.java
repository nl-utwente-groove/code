/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
 * $Id$
 */
package groove.io.conceptual.lang.groove;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import groove.grammar.aspect.AspectEdge;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.AspectLabel;
import groove.grammar.aspect.AspectNode;
import groove.grammar.model.RuleModel;
import groove.graph.GraphRole;
import groove.io.conceptual.Id;
import groove.io.conceptual.Name;
import groove.io.conceptual.TypeModel;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.configuration.schema.EnumModeType;
import groove.io.conceptual.configuration.schema.TypeModel.Constraints;
import groove.io.conceptual.graph.AbsEdge;
import groove.io.conceptual.graph.AbsGraph;
import groove.io.conceptual.graph.AbsNode;
import groove.io.conceptual.lang.Message;
import groove.io.conceptual.lang.Messenger;
import groove.io.conceptual.lang.groove.GraphNodeTypes.ModelType;
import groove.io.conceptual.property.DefaultValueProperty;
import groove.io.conceptual.property.IdentityProperty;
import groove.io.conceptual.property.KeysetProperty;
import groove.io.conceptual.property.OppositeProperty;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.CustomDataType;
import groove.io.conceptual.type.DataType;
import groove.io.conceptual.type.Enum;
import groove.io.conceptual.type.Type;
import groove.io.conceptual.value.CustomDataValue;
import groove.io.conceptual.value.EnumValue;
import groove.io.conceptual.value.Value;

@SuppressWarnings("javadoc")
public class GrooveToConstraint implements Messenger {
    private Collection<RuleModel> m_ruleModels;
    private Config m_cfg;
    private TypeModel m_typeModel;

    List<Message> m_messages = new ArrayList<>();

    private GraphNodeTypes m_types;

    public GrooveToConstraint(Collection<RuleModel> ruleModels, GraphNodeTypes types, Config cfg,
        TypeModel typeModel) {
        this.m_ruleModels = ruleModels;
        this.m_types = types;
        this.m_cfg = cfg;
        this.m_typeModel = typeModel;

        parseRules();
    }

    private void parseRules() {
        Constraints constraints = this.m_cfg.getConfig()
            .getTypeModel()
            .getConstraints();
        for (RuleModel model : this.m_ruleModels) {
            if (!model.isEnabled() || model.hasErrors()) {
                continue;
            }
            String lastName = model.getLastName();
            if (constraints.isCheckUniqueness()
                && lastName.startsWith(ConstraintToGroove.UNIQUE_PRF)) {
                if (!this.m_cfg.getConfig()
                    .getTypeModel()
                    .getFields()
                    .getContainers()
                    .isUseTypeName()) {
                    parseUniqueRule(model);
                }
            } else if (constraints.isCheckOrdering()
                && lastName.startsWith(ConstraintToGroove.ORDERED_PRF)) {
                if (!this.m_cfg.getConfig()
                    .getTypeModel()
                    .getFields()
                    .getContainers()
                    .isUseTypeName()) {
                    parseOrderedRule(model);
                }
            } else if (constraints.isCheckIdentifier()
                && lastName.startsWith(ConstraintToGroove.IDENTITY_PRF)) {
                if (this.m_cfg.getConfig()
                    .getTypeModel()
                    .getProperties()
                    .isUseIdentity()) {
                    parseIdentityRule(model);
                }
            } else if (constraints.isCheckKeyset()
                && lastName.startsWith(ConstraintToGroove.KEYSET_PRF)) {
                if (this.m_cfg.getConfig()
                    .getTypeModel()
                    .getProperties()
                    .isUseKeyset()) {

                    parseKeysetRule(model);
                }
            } else if (constraints.isCheckOpposite()
                && lastName.startsWith(ConstraintToGroove.OPPOSITE_PRF)) {
                if (this.m_cfg.getConfig()
                    .getTypeModel()
                    .getProperties()
                    .isUseOpposite()) {
                    parseOppositeRule(model);
                }
            } else if (this.m_cfg.getConfig()
                .getTypeModel()
                .getFields()
                .getDefaults()
                .isUseRule() && lastName.startsWith(ConstraintToGroove.DEFAULT_PRF)) {
                if (this.m_cfg.getConfig()
                    .getTypeModel()
                    .getProperties()
                    .isUseDefaultValue()) {
                    parseDefaultRule(model);
                }
            }
        }
    }

    @Override
    public List<Message> getMessages() {
        return this.m_messages;
    }

    @Override
    public void clearMessages() {
        this.m_messages.clear();
    }

    // Determines subject of rule and sets container type to be unique
    private void parseUniqueRule(RuleModel model) {
        AbsGraph ruleGraph = parseRuleModel(model);

        for (AbsNode n : ruleGraph.getNodes()) {
            // Find root node
            if (n.getTargetEdges()
                .size() == 0 && getType(n) != null) {
                // Get field of first outgoing edge and change its type to something unique
                Class c = (Class) this.m_types.getType(getType(n));
                if (n.getEdges()
                    .size() > 0) {
                    Name fieldName = Name.getName(n.getEdges()
                        .get(0)
                        .getName());
                    if (c.getField(fieldName)
                        .getType() instanceof Container) {
                        ((Container) c.getField(fieldName)
                            .getType()).setUnique(true);
                    }
                }
            }
        }
    }

    // Determines subject of rule and sets container type to be ordered
    private void parseOrderedRule(RuleModel model) {
        AbsGraph ruleGraph = parseRuleModel(model);

        for (AbsNode n : ruleGraph.getNodes()) {
            // Find root node (NAC check is for edge roots)
            if (n.getTargetEdges()
                .size() == 0 && !isNAC(n) && getType(n) != null) {
                // Get field of first outgoing edge and change its type to something ordered
                Class c = (Class) this.m_types.getType(getType(n));
                if (n.getEdges()
                    .size() > 0) {
                    Name fieldName = Name.getName(n.getEdges()
                        .get(0)
                        .getName());
                    if (c.getField(fieldName)
                        .getType() instanceof Container) {
                        ((Container) c.getField(fieldName)
                            .getType()).setOrdered(true);
                    }
                }
            }
        }
    }

    // Determines subject of rule and creates IdentityProperty
    private void parseIdentityRule(RuleModel model) {
        AbsGraph ruleGraph = parseRuleModel(model);

        AbsNode classNode = null;
        Class idClass = null;

        outloop: for (AbsNode node : ruleGraph.getNodes()) {
            //Find the inequality edge, connects two class nodes for the identity property, with outgoing field names
            //Use incoming edges, can be only one
            for (AbsEdge edge : node.getTargetEdges()) {
                if (edge.getName()
                    .equals("!=")) {
                    classNode = edge.getSource();
                    idClass = (Class) this.m_types.getType(getType(classNode));
                    break outloop;
                }
            }
        }

        if (classNode != null) {
            assert idClass != null; // implied by classNode != null
            Set<Name> fieldNames = new HashSet<>();
            for (AbsEdge e : classNode.getEdges()) {
                Name fieldName = Name.getName(e.getName());
                if (idClass.getField(fieldName) != null) {
                    fieldNames.add(fieldName);
                }
            }
            if (fieldNames.size() > 0) {
                IdentityProperty ip =
                    new IdentityProperty(idClass, fieldNames.toArray(new Name[fieldNames.size()]));
                this.m_typeModel.addProperty(ip);
            }
        }
    }

    // Determines subject of rule and creates KeysetProperty
    private void parseKeysetRule(RuleModel model) {
        AbsGraph ruleGraph = parseRuleModel(model);

        AbsNode relClassNode = null;
        AbsNode classNode = null;
        Class relClass = null;
        Class keyClass = null;

        // Find main class node for keyset relation. Node has no incoming edges
        for (AbsNode node : ruleGraph.getNodes()) {
            if (node.getTargetEdges()
                .size() == 0) {
                relClassNode = node;
                relClass = (Class) this.m_types.getType(getType(node));
                break;
            }
        }

        if (relClassNode == null || relClass == null) {
            return;
        }

        Name relField = Name.getName(relClassNode.getEdges()
            .get(0)
            .getName());

        outloop: for (AbsNode node : ruleGraph.getNodes()) {
            //Find the inequality edge, connects two class nodes for the identity property, with outgoing field names
            //Use incoming edges, can be only one
            for (AbsEdge edge : node.getTargetEdges()) {
                if (edge.getName()
                    .equals("!=")) {
                    classNode = edge.getSource();
                    keyClass = (Class) this.m_types.getType(getType(node));
                    break outloop;
                }
            }
        }

        if (classNode == null || keyClass == null) {
            return;
        }

        Set<Name> fieldNames = new HashSet<>();
        for (AbsEdge e : classNode.getEdges()) {
            Name fieldName = Name.getName(e.getName());
            if (keyClass.getField(fieldName) != null) {
                fieldNames.add(fieldName);
            }
        }
        if (fieldNames.size() > 0) {
            Class c = (Class) this.m_types.getType(getType(classNode));
            KeysetProperty kp = new KeysetProperty(relClass, relField, c,
                fieldNames.toArray(new Name[fieldNames.size()]));
            this.m_typeModel.addProperty(kp);
        }
    }

    // Determines subject of rule and creates OppositeProperty
    private void parseOppositeRule(RuleModel model) {
        AbsGraph ruleGraph = parseRuleModel(model);

        /*
         * Opposite rule always this shape (2 classes, 2 intermediate):
         *    /-I1    -\
         * C1           C2
         *    \-NAC:I2-/
         */

        Class class1 = null, class2 = null;
        Name field1 = null, field2 = null;

        for (AbsNode node : ruleGraph.getNodes()) {
            if (isNAC(node)) {
                continue;
            }
            String type = getType(node);
            if (type == null) {
                continue;
            }
            if (this.m_types.getModelType(type) == ModelType.TypeClass) {
                for (AbsEdge edge : node.getEdges()) {
                    AbsNode targetNode = edge.getTarget();
                    String targettype = getType(targetNode);
                    if (targettype == null) {
                        continue;
                    }
                    // If not class then intermediate. This should actually always hold
                    if (this.m_types.getModelType(targettype) != ModelType.TypeClass) {
                        if (!isNAC(targetNode)) {
                            class1 = (Class) this.m_types.getType(type);
                            field1 = Name.getName(edge.getName());
                        } else {
                            class2 = (Class) this.m_types.getType(type);
                            field2 = Name.getName(edge.getName());
                        }
                    }
                }
            }

        }

        OppositeProperty op = new OppositeProperty(class1, field1, class2, field2);
        this.m_typeModel.addProperty(op);
    }

    // Determines subject of rule and creates DefaultValueProperty
    private void parseDefaultRule(RuleModel model) {
        AbsGraph ruleGraph = parseRuleModel(model);

        /*
         * Default rule always this shape (1 class, 1 type, 1 value):
         *   /-NAC:T1
         * C1
         *   \-NEW:V2
         */

        for (AbsNode node : ruleGraph.getNodes()) {
            if (isNAC(node) || isNew(node)) {
                continue;
            }
            String type = getType(node);
            if (type == null) {
                continue;
            }
            if (this.m_types.getModelType(type) == ModelType.TypeClass) {//Other option is data type, ignore that as it is the actual value
                Class cmClass = (Class) this.m_types.getType(type);

                for (AbsEdge edge : node.getEdges()) {
                    AbsNode targetNode = edge.getTarget();
                    String targettype = getType(targetNode);
                    if (targettype == null) {
                        continue;
                    }
                    if (isNew(targetNode)) {
                        Name field = Name.getName(edge.getName()
                            .substring(4)); //remove the "new:" aspect
                        Value value = getNodeValue(targetNode);

                        DefaultValueProperty dp = new DefaultValueProperty(cmClass, field, value);
                        this.m_typeModel.addProperty(dp);

                        break;
                    }
                }
                break;
            }

        }

    }

    // No parser needed for enum constraint rules
    //private void parseEnumRule(RuleModel model) {}

    private AbsGraph parseRuleModel(RuleModel rule) {
        AspectGraph sourceGraph = rule.getSource();
        AbsGraph resultGraph = new AbsGraph(rule.getQualName(), GraphRole.RULE);

        Map<AspectNode,AbsNode> nodeMap = new HashMap<>();

        for (AspectNode node : sourceGraph.nodeSet()) {
            AbsNode aNode = new AbsNode();
            for (AspectLabel aspectLabel : node.getNodeLabels()) {
                aNode.addName(aspectLabel.toString());
            }
            nodeMap.put(node, aNode);

            resultGraph.addNode(aNode);
        }

        for (AspectNode node : sourceGraph.nodeSet()) {
            for (AspectEdge edge : sourceGraph.edgeSet(node)) {
                if (edge.source() != node) {
                    // Ignore edges of which the current node is the target, they will be treated by the source node
                    continue;
                }
                if (edge.getAspect()
                    .isForNode(GraphRole.RULE) && edge.source() == edge.target()) {
                    nodeMap.get(edge.source())
                        .addName(edge.label()
                            .toString());
                } else {
                    /*AbsEdge aEdge = */new AbsEdge(nodeMap.get(edge.source()),
                        nodeMap.get(edge.target()), edge.label()
                            .toString());
                }
            }
        }

        return resultGraph;
    }

    private String getType(AbsNode node) {
        for (String name : node.getNames()) {
            if (name.startsWith("type:")) {
                return name.substring(5);
            }
            if (name.startsWith("bool:")) {
                return "bool";
            }
            if (name.startsWith("int:")) {
                return "int";
            }
            if (name.startsWith("real:")) {
                return "real";
            }
            if (name.startsWith("string:")) {
                return "string";
            }
        }
        return null;
    }

    private boolean isNAC(AbsNode node) {
        for (String name : node.getNames()) {
            if (name.equals("not:")) {
                return true;
            }
        }
        return false;
    }

    private boolean isNew(AbsNode node) {
        for (String name : node.getNames()) {
            if (name.equals("new:")) {
                return true;
            }
        }
        return false;
    }

    private Value getNodeValue(AbsNode node) {
        Type nodeType = this.m_types.getType(getType(node));

        if (nodeType == null) {
            // Might be some intermediate node
            if (this.m_types.getModelType(getType(node)) == ModelType.TypeIntermediate) {
                String valueEdge = this.m_cfg.getStrings()
                    .getValueEdge();
                AbsNode valueNode = null;
                for (AbsEdge e : node.getEdges()) {
                    if (e.getName()
                        .equals(valueEdge)) {
                        valueNode = e.getTarget();
                    }
                }
                if (valueNode != null) {
                    return getNodeValue(valueNode);
                } else {
                    return null;
                }

            } else {
                // ERROR
                return null;
            }
        }

        Value resultValue = null;

        // Data types
        // get the value-related part of the node name
        // Enum type
        if (nodeType instanceof Enum) {
            Enum e = (Enum) nodeType;
            if (this.m_cfg.getConfig()
                .getTypeModel()
                .getEnumMode() == EnumModeType.NODE) {
                Id id = this.m_cfg.nameToId(getType(node));
                EnumValue ev = new EnumValue(e, id.getName());
                resultValue = ev;
            } else {
                for (AbsEdge enumEdge : node.getEdges()) {
                    if (enumEdge.getName()
                        .startsWith("flag:")) {
                        EnumValue ev = new EnumValue(e, Name.getName(enumEdge.getName()
                            .substring(5)));
                        resultValue = ev;
                        break;
                    }
                }
            }
        }
        // Custom data type
        else if (nodeType instanceof CustomDataType) {
            CustomDataType cdt = (CustomDataType) nodeType;
            String dataValueName = this.m_cfg.getStrings()
                .getDataValue();
            AbsNode valueNode = null;
            for (AbsEdge e : node.getEdges()) {
                if (e.getName()
                    .equals(dataValueName)) {
                    valueNode = e.getTarget();
                }
            }
            if (valueNode != null) {
                String valueString = valueNode.getNames()[0].substring(nodeType.typeString()
                    .length() + 1);
                CustomDataValue dv = new CustomDataValue(cdt, valueString);
                resultValue = dv;
            }
        }
        // regular data types
        else if (nodeType instanceof DataType) {
            String valueString = node.getNames()[0].substring(nodeType.typeString()
                .length() + 1);
            resultValue = ((DataType) nodeType).valueFromString(valueString);
        }
        // Containers & tuples
        // Not supported, used by default value only

        return resultValue;
    }
}
