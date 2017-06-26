package groove.io.conceptual.lang.groove;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import groove.grammar.QualName;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeNode;
import groove.graph.EdgeRole;
import groove.io.conceptual.Field;
import groove.io.conceptual.Id;
import groove.io.conceptual.Name;
import groove.io.conceptual.Timer;
import groove.io.conceptual.TypeModel;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.configuration.schema.EnumModeType;
import groove.io.conceptual.configuration.schema.ModeType;
import groove.io.conceptual.configuration.schema.NullableType;
import groove.io.conceptual.configuration.schema.OrderType;
import groove.io.conceptual.lang.ImportException;
import groove.io.conceptual.lang.Message;
import groove.io.conceptual.lang.Message.MessageType;
import groove.io.conceptual.lang.TypeImporter;
import groove.io.conceptual.lang.groove.GraphNodeTypes.ModelType;
import groove.io.conceptual.property.AbstractProperty;
import groove.io.conceptual.property.ContainmentProperty;
import groove.io.conceptual.type.BoolType;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.Container.Kind;
import groove.io.conceptual.type.CustomDataType;
import groove.io.conceptual.type.Enum;
import groove.io.conceptual.type.IntType;
import groove.io.conceptual.type.RealType;
import groove.io.conceptual.type.StringType;
import groove.io.conceptual.type.Tuple;
import groove.io.conceptual.type.Type;

@SuppressWarnings("javadoc")
public class GrooveToType extends TypeImporter {
    private TypeModel m_typeModel;
    private GraphNodeTypes m_types;
    private Config m_cfg;

    private static Map<Id,Type> g_primitiveIds = new HashMap<>();

    static {
        g_primitiveIds.put(Id.getId(Id.ROOT, Name.getName("bool")), BoolType.instance());
        g_primitiveIds.put(Id.getId(Id.ROOT, Name.getName("int")), IntType.instance());
        g_primitiveIds.put(Id.getId(Id.ROOT, Name.getName("real")), RealType.instance());
        g_primitiveIds.put(Id.getId(Id.ROOT, Name.getName("string")), StringType.instance());
    }

    // Map TypeNode to Id (each typenode in graph should have one)
    private Map<TypeNode,Id> m_typeIds = new HashMap<>();

    // Map graph nodes to edges
    private Map<TypeNode,Set<TypeEdge>> m_nodeEdges = new HashMap<>();

    private Map<TypeNode,Type> m_intermediateFields = new HashMap<>();

    public GrooveToType(TypeGraph grooveTypeGraph, GraphNodeTypes types, Config cfg) {
        this.m_types = types;
        this.m_cfg = cfg;

        int timer = Timer.start("GROOVE to TM");
        buildTypeModel(grooveTypeGraph);
        Timer.stop(timer);
    }

    private void buildTypeModel(TypeGraph grooveTypeGraph) {
        this.m_typeModel = new TypeModel(grooveTypeGraph.getQualName());

        // Set of Nodes that need to be classified (inverse of m_nodeTypes)
        Set<? extends TypeNode> unvisitedNodes = new HashSet<TypeNode>(grooveTypeGraph.nodeSet());
        // Set of edges in the TypeModel
        Set<? extends TypeEdge> edges = grooveTypeGraph.edgeSet();
        // Set of IDs for enums. These need to be known to find their value children
        Map<Id,Enum> enumIds = new HashMap<>();

        // Map nodes to edges
        for (TypeNode n : grooveTypeGraph.nodeSet()) {
            this.m_nodeEdges.put(n, new HashSet<TypeEdge>());
        }
        for (TypeEdge e : edges) {
            this.m_nodeEdges.get(e.source())
                .add(e);
        }

        // Remove already known nodes from list (from the meta schema)
        for (Iterator<? extends TypeNode> it = unvisitedNodes.iterator(); it.hasNext();) {
            TypeNode node = it.next();
            if (this.m_types.hasModelType(node.label()
                .text())) {
                if (this.m_types.getModelType(node.label()
                    .text()) != ModelType.TypeNone) {
                    if (this.m_types.getModelType(node.label()
                        .text()) == ModelType.TypeEnum) {
                        enumIds.put(getNodeId(node), null);
                    }

                    it.remove();
                }
            }
        }

        // Check nodes by postfix, if not using metamodel
        if (!this.m_cfg.getConfig()
            .getTypeModel()
            .isMetaSchema()) {
            for (Iterator<? extends TypeNode> it = unvisitedNodes.iterator(); it.hasNext();) {
                TypeNode n = it.next();
                String nameStr = n.label()
                    .text();
                Id id = getNodeId(n);

                // Enums end with EnumPostfix
                if (this.m_cfg.getStrings()
                    .getEnumPostfix()
                    .length() > 0
                    && nameStr.endsWith(this.m_cfg.getStrings()
                        .getEnumPostfix())) {
                    enumIds.put(id, null);
                    this.m_types.addModelType(getLabel(n), ModelType.TypeEnum);
                } else if (this.m_cfg.getStrings()
                    .getProperPostfix()
                    .length() > 0
                    && nameStr.endsWith(this.m_cfg.getStrings()
                        .getProperPostfix())) {
                    this.m_types.addModelType(getLabel(n), ModelType.TypeClass);
                } else if (this.m_cfg.getStrings()
                    .getNullablePostfix()
                    .length() > 0
                    && nameStr.endsWith(this.m_cfg.getStrings()
                        .getNullablePostfix())) {
                    this.m_types.addModelType(getLabel(n), ModelType.TypeClassNullable);
                } else if (this.m_cfg.getStrings()
                    .getDataPostfix()
                    .length() > 0
                    && nameStr.endsWith(this.m_cfg.getStrings()
                        .getDataPostfix())) {
                    this.m_types.addModelType(getLabel(n), ModelType.TypeDatatype);
                } else if (this.m_cfg.getStrings()
                    .getIntermediatePostfix()
                    .length() > 0
                    && nameStr.endsWith(this.m_cfg.getStrings()
                        .getIntermediatePostfix())) { //Non-container intermediates
                    this.m_types.addModelType(getLabel(n), ModelType.TypeIntermediate);
                } else if (this.m_cfg.getStrings()
                    .getMetaContainerSet()
                    .length() > 0
                    && nameStr.endsWith(this.m_cfg.getStrings()
                        .getMetaContainerSet())) { //Container intermediates when postfix enabled
                    this.m_types.addModelType(getLabel(n), ModelType.TypeIntermediate);
                } else if (this.m_cfg.getStrings()
                    .getMetaContainerBag()
                    .length() > 0
                    && nameStr.endsWith(this.m_cfg.getStrings()
                        .getMetaContainerBag())) { //Container intermediates when postfix enabled
                    this.m_types.addModelType(getLabel(n), ModelType.TypeIntermediate);
                } else if (this.m_cfg.getStrings()
                    .getMetaContainerOrd()
                    .length() > 0
                    && nameStr.endsWith(this.m_cfg.getStrings()
                        .getMetaContainerOrd())) { //Container intermediates when postfix enabled
                    this.m_types.addModelType(getLabel(n), ModelType.TypeIntermediate);
                } else if (this.m_cfg.getStrings()
                    .getMetaContainerSeq()
                    .length() > 0
                    && nameStr.endsWith(this.m_cfg.getStrings()
                        .getMetaContainerSeq())) { //Container intermediates when postfix enabled
                    this.m_types.addModelType(getLabel(n), ModelType.TypeIntermediate);
                } else if (this.m_cfg.getStrings()
                    .getTuplePostfix()
                    .length() > 0
                    && nameStr.endsWith(this.m_cfg.getStrings()
                        .getTuplePostfix())) {
                    this.m_types.addModelType(getLabel(n), ModelType.TypeTuple);
                } else {
                    // If no actual postfix, try other options later on
                    continue;
                }
                it.remove();
            }
        }

        // Run through edges, identify intermediate nodes
        // Also map nodes to edges
        for (TypeEdge e : edges) {
            Id sourceId = getNodeId(e.source());
            Id targetId = getNodeId(e.target());

            if (!unvisitedNodes.contains(e.target())) {
                // only check the unknown nodes
                continue;
            }

            // Detected intermediate node
            if (targetId == null || targetId.getName() == null) {
                System.out.println("Target null");
                continue;
            }

            //Intermediate nodes should ALWAYS have a namespace and a name, even in Id FLAT mode
            if (targetId.getNamespace() == sourceId) {
                this.m_types.addModelType(getLabel(e.target()), ModelType.TypeIntermediate);
                unvisitedNodes.remove(e.target());
            }
        }

        // Intermediates and enums are known, now all the other nodes can be typed
        for (Iterator<? extends TypeNode> it = unvisitedNodes.iterator(); it.hasNext();) {
            TypeNode n = it.next();
            Id id = getNodeId(n);

            if (n.isDataType()) {
                // Simple data types
                this.m_types.addModelType(getLabel(n), ModelType.TypeDatatype);
                it.remove();
            } else if (enumIds.keySet()
                .contains(id.getNamespace())) {
                // Enum values
                this.m_types.addModelType(getLabel(n), ModelType.TypeEnumValue);
                it.remove();
            } else {
                // mark everything else as a class
                if (getLabel(n).equals(this.m_cfg.getStrings()
                    .getNilName())) {
                    this.m_types.addModelType(getLabel(n), ModelType.TypeNone);
                } else {
                    this.m_types.addModelType(getLabel(n), ModelType.TypeClass);
                }
                it.remove();
            }
        }

        // Ought not to happen (unless misconfigured)
        for (TypeNode n : unvisitedNodes) {
            addMessage(new Message("TypeNode unvisited: " + n + ": " + n.label()
                .text(), MessageType.WARNING));
        }

        // Now all the node types are known, create conceptual model types from these nodes, also in multiple passes

        // Keep track of tuple and intermediate nodes, for performance (used to find their edges)
        Set<TypeNode> tupleNodes = new HashSet<>();
        Set<TypeNode> interNodes = new HashSet<>();

        // Instantiate Classes, Enums
        for (TypeNode n : grooveTypeGraph.nodeSet()) {
            Id id = getNodeId(n);

            switch (this.m_types.getModelType(getLabel(n))) {
            case TypeClass:
                Class c = this.m_typeModel.getClass(id, true)
                    .getProperClass();
                setNodeType(n, c);
                if (n.isAbstract()) {
                    if (this.m_cfg.getConfig()
                        .getTypeModel()
                        .getProperties()
                        .isUseAbstract()) {
                        this.m_typeModel.addProperty(new AbstractProperty(c));
                    }
                }
                // Superclass added in next pass (when all classes are known)
                break;
            case TypeClassNullable:
                Class cNull = this.m_typeModel.getClass(id, true)
                    .getNullableClass();
                setNodeType(n, cNull);
                break;
            case TypeEnum:
                Enum e = this.m_typeModel.getEnum(id, true);
                setNodeType(n, e);
                enumIds.put(id, e);
                if (this.m_cfg.getConfig()
                    .getTypeModel()
                    .getEnumMode() == EnumModeType.FLAG) {
                    // Flags can only be determiend at this point
                    populateEnumFlags(n, e);
                }
                break;
            case TypeDatatype:
                if (n.isDataType()) {
                    // Actually just copies g_primitiveIds
                    // TODO: prefill this?
                    setNodeType(n, g_primitiveIds.get(id));
                } else {
                    CustomDataType d = this.m_typeModel.getDatatype(id, true);
                    setNodeType(n, d);
                }
                break;
            case TypeEnumValue:
                // Handled in next pass
                break;
            case TypeIntermediate:
            case TypeContainerSet:
            case TypeContainerBag:
            case TypeContainerSeq:
            case TypeContainerOrd:
                // Handled by intermediate
                interNodes.add(n);
                break;
            case TypeTuple:
                // Actual types filled in after next pass
                Tuple t = new Tuple();
                setNodeType(n, t);

                tupleNodes.add(n);
                break;
            case TypeNone:
                break;
            default:
                System.err.println("No valid type for node " + n);
                break;
            }
        }

        // Time to instantiate enum values and map superclasses
        for (TypeNode n : grooveTypeGraph.nodeSet()) {
            Id id = getNodeId(n);

            switch (this.m_types.getModelType(getLabel(n))) {
            case TypeClass:
                // Find and map super classes
                Class c = (Class) getNodeType(n);
                Set<TypeNode> superTypes = n.getGraph()
                    .getDirectSupertypeMap()
                    .get(n);
                for (TypeNode superType : superTypes) {
                    Class superClass = (Class) getNodeType(superType);
                    if (superClass.isProper()) {
                        c.addSuperClass(superClass);
                    }
                }
                break;
            case TypeEnumValue:
                // Namespace were turned off, find enum by superType node
                Enum e = null;
                if (id.getNamespace() == Id.ROOT) {
                    Set<TypeNode> superEnumTypes = n.getGraph()
                        .getDirectSupertypeMap()
                        .get(n);
                    e = (Enum) getNodeType(superEnumTypes.iterator()
                        .next());
                } else {
                    e = enumIds.get(id.getNamespace());
                }
                e.addLiteral(id.getName());
                setNodeType(n, e);
                break;
            default:
                // Nothing to do for other types
                break;
            }
        }

        // Now solve types of intermediate nodes. This is a recursive process
        // since intermediate nodes may 'contain' other intermediate nodes
        for (TypeNode interNode : interNodes) {
            resolveIntermediateType(interNode);
        }

        // Run through the edges again, now solving fields
        for (TypeEdge e : edges) {
            // Source of edge is class, target must be field (ref or attr)
            if (this.m_types.getModelType(getLabel(e.source())) == ModelType.TypeClass) {
                Class cmClass = (Class) getNodeType(e.source());
                Type targetType = null;
                if (this.m_intermediateFields.containsKey(e.target())) {
                    targetType = this.m_intermediateFields.get(e.target());
                } else {
                    targetType = getNodeType(e.target());
                }

                // Get name, type and multiplicity for field signature
                Name fieldName = Name.getName(e.label()
                    .text());

                // Default multiplicity is 0..*
                int lower = 0;
                int upper = -1;
                if (e.getOutMult() != null) {
                    lower = e.getOutMult()
                        .one();
                    upper = e.getOutMult()
                        .two();
                    if (upper == Integer.MAX_VALUE) {
                        upper = -1;
                    }
                }

                // Check if field edge is containment
                if (e.isComposite()) {
                    if (this.m_cfg.getConfig()
                        .getTypeModel()
                        .getProperties()
                        .isUseContainment()) {
                        this.m_typeModel.addProperty(new ContainmentProperty(cmClass, fieldName));
                    }
                }

                ModelType targetModelType = this.m_types.getModelType(getLabel(e.target()));

                // Intermediate for field. Clear the node type, it was set by resolveIntermediate but no longer required
                if (targetModelType == ModelType.TypeIntermediate
                    && !(targetType instanceof Container)) {
                    setNodeType(e.target(), null);
                }

                // If no intermediate node but is container type, try to resolve value node as container type
                // Is container if upper > 1, or 0..1 but not nullable class
                if ((upper > 1
                    || (lower == 0 && (targetType instanceof Class) && this.m_cfg.getConfig()
                        .getGlobal()
                        .getNullable() != NullableType.NONE))
                    && !(targetModelType == ModelType.TypeIntermediate
                        || targetModelType == ModelType.TypeContainerSet
                        || targetModelType == ModelType.TypeContainerBag
                        || targetModelType == ModelType.TypeContainerOrd
                        || targetModelType == ModelType.TypeContainerSeq)) {
                    OrderType orderType = this.m_cfg.getConfig()
                        .getTypeModel()
                        .getFields()
                        .getContainers()
                        .getOrdering()
                        .getType();
                    boolean isOrdered = false;
                    if (this.m_cfg.getConfig()
                        .getTypeModel()
                        .getFields()
                        .getContainers()
                        .getOrdering()
                        .getMode() == ModeType.PREFER_VALUE) {
                        if (orderType == OrderType.INDEX) {
                            String indexName = this.m_cfg.getStrings()
                                .getIndexEdge();
                            if (hasEdge(e.target(), indexName)) {
                                isOrdered = true;
                            }
                        } else if (orderType == OrderType.EDGE) {
                            String nextName = this.m_cfg.getStrings()
                                .getNextEdge();
                            if (hasEdge(e.target(), nextName)) {
                                isOrdered = true;
                            }
                        }
                    }

                    // Guaranteed to be unique, otherwise direct edges not possible
                    targetType = new Container(isOrdered ? Kind.ORD : Kind.SET, targetType);
                }

                //TODO: opposite edge check

                Field f = new Field(fieldName, targetType, lower, upper);
                cmClass.addField(f);
            }
        }

        // Finally, fixate types of tuples
        for (TypeNode tupleNode : tupleNodes) {
            Tuple t = (Tuple) getNodeType(tupleNode);
            Set<TypeEdge> tupleEdges = this.m_nodeEdges.get(tupleNode);
            Type[] subTypes = new Type[tupleEdges.size()];
            int index = 0;
            for (TypeEdge e : tupleEdges) {
                subTypes[index++] = getNodeType(e.target());
            }
            t.setTypes(subTypes);
        }

        // And we're done
        this.m_typeModel.resolve();
        this.m_typeModels.put(this.m_typeModel.getQualName(), this.m_typeModel);
    }

    private void populateEnumFlags(TypeNode n, Enum e) {
        for (TypeEdge edge : this.m_nodeEdges.get(n)) {
            if (edge.getRole() == EdgeRole.FLAG) {
                e.addLiteral(Name.getName(edge.label()
                    .text()));
            }
        }
    }

    @Override
    public TypeModel getTypeModel(QualName modelName) throws ImportException {
        return this.m_typeModels.get(modelName);
    }

    private Id getNodeId(TypeNode n) {
        if (this.m_typeIds.containsKey(n)) {
            return this.m_typeIds.get(n);
        }

        Id id = this.m_cfg.nameToId(n.label()
            .text());
        this.m_typeIds.put(n, id);
        return id;
    }

    private String getLabel(TypeNode node) {
        return node.label()
            .text();
    }

    // (Recursively) solve types for intermediate nodes
    // Also puts this type in typeTypes
    private Type resolveIntermediateType(TypeNode interNode) {
        //Base case
        if (getNodeType(interNode) != null) {
            return getNodeType(interNode);
        }

        String valueEdge = this.m_cfg.getStrings()
            .getValueEdge();
        TypeNode valueNode = getEdgeNode(interNode, valueEdge);
        //m_nodeEdges.get(interNode).iterator().next();

        // Resolve type of outgoing edge of intermediate node
        Type t = resolveIntermediateType(valueNode);

        // If known container type (meta model), use this information
        switch (this.m_types.getModelType(getLabel(interNode))) {
        case TypeContainerSet:
            Container cSet = new Container(Kind.SET, t);
            setNodeType(interNode, cSet);
            return cSet;
        case TypeContainerBag:
            Container cBag = new Container(Kind.BAG, t);
            setNodeType(interNode, cBag);
            return cBag;
        case TypeContainerOrd:
            Container cOrd = new Container(Kind.ORD, t);
            setNodeType(interNode, cOrd);
            return cOrd;
        case TypeContainerSeq:
            Container cSeq = new Container(Kind.SEQ, t);
            setNodeType(interNode, cSeq);
            return cSeq;
        default:
            // there is nothing in the metamodel
        }

        // No luxury of metamodel, maybe postfixes were used
        if (this.m_cfg.getConfig()
            .getTypeModel()
            .getFields()
            .getContainers()
            .isUseTypeName()) {
            Kind ct = getPostfixType(interNode);
            if (ct != null) {
                Container c = new Container(ct, t);
                setNodeType(interNode, c);

                return c;
            } else {
                // No postfix, must be regular field
                this.m_intermediateFields.put(interNode, t);
                return t;
            }
        }

        // Try to derive information from type graph
        boolean isOrdered = false;
        boolean isUnique = false;
        OrderType orderType = this.m_cfg.getConfig()
            .getTypeModel()
            .getFields()
            .getContainers()
            .getOrdering()
            .getType();
        if (orderType == OrderType.INDEX) {
            String indexName = this.m_cfg.getStrings()
                .getIndexEdge();
            if (hasEdge(interNode, indexName)) {
                isOrdered = true;
            }
            // For the last non-recursive container, the ordering edge may be on the value node instead
            if (!(t instanceof Container)) {
                if (hasEdge(valueNode, indexName)) {
                    isOrdered = true;
                }
            }
        } else if (orderType == OrderType.EDGE) {
            String nextName = this.m_cfg.getStrings()
                .getNextEdge();
            if (hasEdge(interNode, nextName)) {
                isOrdered = true;
            }
            // For the last non-recursive container, the ordering edge may be on the value node instead
            if (!(t instanceof Container)) {
                if (hasEdge(valueNode, nextName)) {
                    isOrdered = true;
                }
            }
        }

        // TODO: uniqueness constraint?
        Kind type = isUnique ? (isOrdered ? Kind.ORD : Kind.SET) : // Unique
            (isOrdered ? Kind.SEQ : Kind.BAG); // Non-unique
        Container c = new Container(type, t);
        setNodeType(interNode, c);

        //TODO: check for opposite edge if rule is disabled but edge enabled

        return c;
    }

    private TypeNode getEdgeNode(TypeNode node, String edge) {
        Set<TypeEdge> nodeEdges = this.m_nodeEdges.get(node);
        for (TypeEdge e : nodeEdges) {
            if (e.label()
                .text()
                .equals(edge)) {
                return e.target();
            }
        }
        return null;
    }

    private boolean hasEdge(TypeNode node, String edge) {
        return getEdgeNode(node, edge) != null;
    }

    private void setNodeType(TypeNode node, Type type) {
        this.m_types.addType(node.label()
            .text(), type);
    }

    private Type getNodeType(TypeNode node) {
        return this.m_types.getType(node.label()
            .text());
    }

    private Kind getPostfixType(TypeNode node) {
        String typeName = node.label()
            .text();
        if (this.m_cfg.getStrings()
            .getMetaContainerSet()
            .length() > 0
            && typeName.endsWith(this.m_cfg.getStrings()
                .getMetaContainerSet())) { //Container intermediates when postfix enabled
            return Kind.SET;
        } else if (this.m_cfg.getStrings()
            .getMetaContainerBag()
            .length() > 0
            && typeName.endsWith(this.m_cfg.getStrings()
                .getMetaContainerBag())) { //Container intermediates when postfix enabled
            return Kind.BAG;
        } else if (this.m_cfg.getStrings()
            .getMetaContainerOrd()
            .length() > 0
            && typeName.endsWith(this.m_cfg.getStrings()
                .getMetaContainerOrd())) { //Container intermediates when postfix enabled
            return Kind.ORD;
        } else if (this.m_cfg.getStrings()
            .getMetaContainerSeq()
            .length() > 0
            && typeName.endsWith(this.m_cfg.getStrings()
                .getMetaContainerSeq())) { //Container intermediates when postfix enabled
            return Kind.SEQ;
        }

        //No type detected
        return null;
    }

    // intermediate = edge with properties. Properties determined by either intermediate, or target node
}
