package groove.io.conceptual.lang.gxl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import de.gupro.gxl.gxl_1_0.EdgeType;
import de.gupro.gxl.gxl_1_0.GraphElementType;
import de.gupro.gxl.gxl_1_0.GraphType;
import de.gupro.gxl.gxl_1_0.GxlType;
import de.gupro.gxl.gxl_1_0.NodeType;
import de.gupro.gxl.gxl_1_0.TupType;
import groove.grammar.QualName;
import groove.io.conceptual.Field;
import groove.io.conceptual.Id;
import groove.io.conceptual.Name;
import groove.io.conceptual.Timer;
import groove.io.conceptual.TypeModel;
import groove.io.conceptual.lang.ImportException;
import groove.io.conceptual.lang.Message;
import groove.io.conceptual.lang.Message.MessageType;
import groove.io.conceptual.lang.TypeImporter;
import groove.io.conceptual.lang.gxl.GxlUtil.AttrTypeEnum;
import groove.io.conceptual.lang.gxl.GxlUtil.EdgeWrapper;
import groove.io.conceptual.lang.gxl.GxlUtil.NodeWrapper;
import groove.io.conceptual.property.AbstractProperty;
import groove.io.conceptual.property.ContainmentProperty;
import groove.io.conceptual.property.DefaultValueProperty;
import groove.io.conceptual.type.BoolType;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.Container.Kind;
import groove.io.conceptual.type.Enum;
import groove.io.conceptual.type.IntType;
import groove.io.conceptual.type.RealType;
import groove.io.conceptual.type.StringType;
import groove.io.conceptual.type.Tuple;
import groove.io.conceptual.type.Type;
import groove.io.conceptual.value.ContainerValue;
import groove.io.conceptual.value.TupleValue;
import groove.io.conceptual.value.Value;

/**
 * Importer for GXL type models
 * Some limitations:
 * * Only one type graph supported. This type graph is allowed to describe multiple instance graphs however
 * * No cross references to other XML files, the xlink attributes are not supported
 * @author s0141844
 * @version $Revision $
 */
@SuppressWarnings("javadoc")
public class GxlToType extends TypeImporter {

    // GXL type graph to use to use (select the first one form the document)
    private List<GraphType> m_gxlTypeGraphs = new ArrayList<>();

    // Map to keep track of nodes and their tm objects
    private Map<NodeType,Object> m_nodeValues = new HashMap<>();

    // Because IDs are unique in the ENTIRE document, this map will suffice for all specified typegraphs
    private Map<String,Type> m_idToType = new HashMap<>();
    private Map<String,Field> m_idToField = new HashMap<>();
    private Set<String> m_complexEdgeIds = new HashSet<>();

    private Map<String,Id> m_graphNamespaces = new HashMap<>();

    private boolean m_useComplex;

    private static final Map<String,Type> g_simpleTypeMap = new HashMap<>();

    static {
        g_simpleTypeMap.put("Locator", StringType.instance());
        g_simpleTypeMap.put("Bool", BoolType.instance());
        g_simpleTypeMap.put("Float", RealType.instance());
        g_simpleTypeMap.put("Int", IntType.instance());
        g_simpleTypeMap.put("String", StringType.instance());
    }

    private static final Set<String> g_complexTypeSet = new HashSet<>();

    static {
        g_complexTypeSet.add("Bag");
        g_complexTypeSet.add("Set");
        g_complexTypeSet.add("Seq");
        g_complexTypeSet.add("Tup");
    }

    private static final Set<String> g_edgeTypes = new HashSet<>();

    static {
        g_edgeTypes.add("EdgeClass");
        g_edgeTypes.add("AggregationClass");
        g_edgeTypes.add("CompositionClass");
    }

    public GxlToType(String typeModel, boolean useComplex) throws ImportException {
        this.m_useComplex = useComplex;
        // Load the GXL
        try (FileInputStream in = new FileInputStream(typeModel)) {
            int timer = Timer.start("Load GXL");
            @SuppressWarnings("unchecked") JAXBElement<GxlType> doc =
                (JAXBElement<GxlType>) GxlUtil.g_unmarshaller.unmarshal(in);
            in.close();
            for (GraphType g : doc.getValue()
                .getGraph()) {
                String type = GxlUtil.getElemType(g);
                if ("gxl-1.0".equals(type)) {
                    this.m_gxlTypeGraphs.add(g);
                    break;
                }
            }
            Timer.stop(timer);
        } catch (JAXBException e) {
            throw new ImportException(e);
        } catch (FileNotFoundException e) {
            throw new ImportException(e);
        } catch (IOException e) {
            throw new ImportException(e);
        }

        // Preload TypeModels
        /*for (String model : m_typeGraphs.keySet()) {
            getTypeModel(model);
        }*/
        int timer = Timer.start("GXL to TM");
        buildTypeModels();
        Timer.stop(timer);
    }

    private void buildTypeModels() {
        for (GraphType graph : this.m_gxlTypeGraphs) {
            Map<NodeType,NodeWrapper> nodes = GxlUtil.wrapGraph(graph);

            TypeModel typeModel = new TypeModel(QualName.name(graph.getId()));

            // Maps all graph elements in graph to a specific GraphClass node
            Map<NodeType,Set<NodeWrapper>> graphElements = new HashMap<>();
            for (GraphElementType elem : graph.getNodeOrEdgeOrRel()) {
                if (elem instanceof NodeType) {
                    if ("GraphClass".equals(GxlUtil.getElemType(elem))) {
                        graphElements.put((NodeType) elem, new HashSet<NodeWrapper>());

                        // Grab all the contained elements
                        for (EdgeWrapper ew : nodes.get(elem)
                            .getEdges()) {
                            if (ew.getType()
                                .equals("contains")) {
                                graphElements.get(elem)
                                    .add(ew.getTarget());
                            }
                        }

                    }
                }
            }

            // Child -> Parent
            Map<NodeType,NodeType> graphHierachy = new HashMap<>();

            // Fix graph hierarchy
            for (NodeType graphNode : graphElements.keySet()) {
                for (Iterator<NodeWrapper> nwIt = graphElements.get(graphNode)
                    .iterator(); nwIt.hasNext();) {
                    NodeWrapper nw = nwIt.next();
                    String type = GxlUtil.getElemType(nw.getNode());
                    if (type == null) {
                        continue;
                    }

                    if (type.equals("NodeClass")) {
                        for (EdgeWrapper ew : nw.getEdges()) {
                            if (ew.getType()
                                .equals("hasAsComponentGraph")) {
                                if (graphHierachy.containsKey(ew.getTarget()
                                    .getNode())) {
                                    addMessage(new Message(
                                        "Graph may only be contained by one other graph"));
                                }
                                graphHierachy.put(ew.getTarget()
                                    .getNode(), nw.getNode());
                                // Node not to be treated as element in the graph any further
                                nwIt.remove();
                            }
                        }
                    }
                }
            }

            // Fix graph hierarchy namespaces
            for (NodeType graphClassNode : graphElements.keySet()) {
                getGraphId(graphClassNode, graphHierachy, this.m_graphNamespaces);
            }

            // Hierarchy done, build graphs
            for (NodeType graphNode : graphElements.keySet()) {
                Id graphNamespace = this.m_graphNamespaces.get(graphNode.getId());

                for (NodeWrapper nw : graphElements.get(graphNode)) {
                    String type = GxlUtil.getElemType(nw.getNode());
                    if (type == null) {
                        continue;
                    }

                    if (type.equals("NodeClass")) {
                        visitClass(typeModel, nw, graphNamespace);
                    } else if (g_edgeTypes.contains(type)) {
                        visitEdge(typeModel, nw, graphNamespace);
                    }
                }

                typeModel.resolve();

                // Store typegraph under ID of corresponding GraphClass node.
                this.m_typeModels.put(QualName.parse(graphNode.getId()), typeModel);

                //System.out.println("GXL elem " + count + " (" + graphNode.getId() + ")");
            }
        }

    }

    private void getGraphId(NodeType graphClassNode, Map<NodeType,NodeType> graphHierachy,
        Map<String,Id> graphNamespaces) {
        if (graphHierachy.containsKey(graphClassNode)) {
            getGraphId(graphHierachy.get(graphClassNode), graphHierachy, graphNamespaces);

            Id parentId = graphNamespaces.get(graphHierachy.get(graphClassNode)
                .getId());

            String graphName =
                (String) GxlUtil.getAttribute(graphClassNode, "name", AttrTypeEnum.STRING);
            graphNamespaces.put(graphClassNode.getId(),
                Id.getId(parentId, Name.getName(graphName)));
        } else {
            String graphName =
                (String) GxlUtil.getAttribute(graphClassNode, "name", AttrTypeEnum.STRING);
            graphNamespaces.put(graphClassNode.getId(), Id.getId(Id.ROOT, Name.getName(graphName)));
        }
    }

    @Override
    public TypeModel getTypeModel(QualName model) {
        if (this.m_typeModels.containsKey(model)) {
            return this.m_typeModels.get(model);
        }

        return null;
    }

    // There is a boatload of node types, each requires different handling
    /*
     * //Values
     * --<node id="BagVal">
     * --<node id="SetVal">
     * --<node id="SeqVal">
     * --<node id="TupVal">
     * --<node id="hasComponentValue">
     *
     * --<node id="LocatorVal">
     * --<node id="uri">
     *
     * --<node id="BoolVal">
     * --<node id="FloatVal">
     * --<node id="IntVal">
     * --<node id="StringVal">
     * --<node id="value">
     *
     * //Various unused stuff
     * --<node id="gxl-1.0">
     * --<node id="AttributedElementClass">
     * --<node id="GraphElementClass">
     * --<node id="isabstract"> attrib, node
     * only supported
     * --<node id="Domain">
     * --<node id="CompositeDomain">
     * --<node id="AtomicDomain">
     * --<node id="Value">
     * --<node id="CompositeVal">
     * --<node id="AtomicVal">
     *
     * x--<node id="RelationClass">
     * x--<node id="RelationEndClass">
     * --<node id="hasRelationEnd"> edge
     * --<node id="directedto"> relation
     * --<node id="role">
     * relation
     *
     * ?
     * --<node id="relatesTo"> edge, unused
     *
     * --<node id="domainBool">
     * --<node id="domainInt">
     * --<node id="domainString">
     * --<node id="domainTupIntInt">
     * --<node id="domainEnum">
     * --<node id="domainEnum2">
     *
     * --<node id="valueRelation">
     * --<node id="valueTarget">
     * --<node id="valueUndirected">
     * --<node id="valueFrom">
     * --<node id="valueTo">
     */

    // Nodes specify elements in the model, being nodes, edges, relations, attributes etc
    /**
     * Nodes (classes)
     * --<node id="NodeClass">
     * --<node id="isA"> edge, inheritance
     * --<node id="hasAttribute"> edge
     * --<node id="name"> attribute
     * ?--<node id="hasAsComponentGraph"> subgraph as element, only supported by graphs partially as package
     */
    private Class visitClass(TypeModel mm, NodeWrapper nodeWrapper, Id graphNamespace) {
        //assert ("NodeClass".equals(nodeWrapper.getType()));
        if (g_edgeTypes.contains(nodeWrapper.getType())) {
            return (Class) visitEdge(mm, nodeWrapper, graphNamespace);
        }

        NodeType node = nodeWrapper.getNode();
        if (this.m_nodeValues.containsKey(node)) {
            Object val = this.m_nodeValues.get(node);
            assert(val instanceof Class);
            return (Class) val;
        }

        String name = (String) GxlUtil.getAttribute(node, "name", GxlUtil.AttrTypeEnum.STRING);
        if (name == null) {
            addMessage(new Message("Class without name " + node.getId()));
            name = node.getId();
        }

        Name clsName = Name.getName(name);
        Id clsID = Id.getId(graphNamespace, clsName);
        Class cmClass = mm.getClass(clsID, true);

        this.m_nodeValues.put(node, cmClass);
        this.m_idToType.put(node.getId(), cmClass);

        //Run through attributes
        //Walk through inheritance chain
        for (EdgeWrapper edge : nodeWrapper.getEdges()) {
            if (edge.getType()
                .equals("hasAttribute")) {
                Field cmField = visitAttribute(mm, cmClass, edge.getTarget(), graphNamespace);
                cmClass.addField(cmField);
            } else if (edge.getType()
                .equals("isA")) {
                Class superClass = visitClass(mm, edge.getTarget(), graphNamespace);
                cmClass.addSuperClass(superClass);
            }
            // Cannot directly handle references, they will be added on their own account (as they are nodes and will be visited independently)
        }

        Boolean isAbstract = (Boolean) GxlUtil.getAttribute(node, "isabstract", AttrTypeEnum.BOOL);
        if (isAbstract != null && isAbstract) {
            mm.addProperty(new AbstractProperty(cmClass));
        }

        return cmClass;
    }

    //Class --> hasAttribute -> Attribute -> hasDomain --> Domain
    /*
     * //Attributes (attribs)
     * --<node id="AttributeClass"> On demand, hasAttribute edge
     * --<node id="name"> attribute
     * --<node id="hasDomain">
     * --<node id="hasDefaultValue">
     */
    // Class argument to apply default value property
    //TODO: return value used by assign, or assigned in visitor itself?
    private Field visitAttribute(TypeModel tm, Class c, NodeWrapper nodeWrapper,
        Id graphNamespace) {
        assert("AttributeClass".equals(nodeWrapper.getType()));

        NodeType node = nodeWrapper.getNode();
        if (this.m_nodeValues.containsKey(node)) {
            Object val = this.m_nodeValues.get(node);
            assert(val instanceof Field);
            return (Field) val;
        }

        String name = (String) GxlUtil.getAttribute(nodeWrapper.getNode(),
            "name",
            GxlUtil.AttrTypeEnum.STRING);
        Type t = null;
        for (EdgeWrapper ew : nodeWrapper.getEdges()) {
            if (ew.getType()
                .equals("hasDomain")) {
                t = visitType(tm, ew.getTarget(), graphNamespace);
            }
        }
        assert(t != null);

        int lowerBound = 1;
        int upperBound = 1;
        if (t instanceof Container) {
            lowerBound = 0;
            upperBound = -1;
        }
        Field f = new Field(Name.getName(name), t, lowerBound, upperBound);
        this.m_idToField.put(node.getId(), f);

        //Note: actually visiting type before storing field, but not a problem since fields generally have no recursive behavior
        this.m_nodeValues.put(node, f);
        this.m_idToType.put(node.getId(), t);

        for (EdgeWrapper ew : nodeWrapper.getEdges()) {
            if (ew.getType()
                .equals("hasDefaultValue")) {
                Value v = visitValue(tm, ew.getTarget(), t, graphNamespace);
                DefaultValueProperty p = new DefaultValueProperty(c, f.getName(), v);
                tm.addProperty(p);
            }
        }

        return f;
    }

    /*
     * //Edges (fields/relations)
     * --<node id="EdgeClass"> edge type
     * --<node id="AggregationClass"> edge type
     * --<node id="aggregate"> (from or to)?
     * --<node id="CompositionClass"> edge type
     * --<node id="isA"> edge, inheritance
     * --<node id="hasAttribute"> edge
     * --<node id="from"> edge
     * --<node id="to"> edge
     * --<node id="name">
     * --<node id="limits"> edge attr (from, to, relatesTo)
     * --<node id="isordered"> edge attr
     * --<node id="isdirected"> edge attr
     */
    private Object visitEdge(TypeModel tm, NodeWrapper nodeWrapper, Id graphNamespace) {
        NodeType node = nodeWrapper.getNode();
        if (this.m_nodeValues.containsKey(node)) {
            Object val = this.m_nodeValues.get(node);
            return val;
        }

        String name = (String) GxlUtil.getAttribute(nodeWrapper.getNode(),
            "name",
            GxlUtil.AttrTypeEnum.STRING);

        Class sourceClass = null;
        Class targetClass = null;
        Limits fromLimits = new Limits(), toLimits = new Limits();
        boolean fromOrdered = false, toOrdered = false;
        boolean isComplex = false;
        for (EdgeWrapper ew : nodeWrapper.getEdges()) {
            //Found source
            if (ew.getType()
                .equals("from")) {
                sourceClass = visitClass(tm, ew.getTarget(), graphNamespace);
                fromLimits = getLimits(ew.getEdge());
                if (!fromLimits.isDefault()) {
                    isComplex = true;
                }
                fromOrdered =
                    (Boolean) GxlUtil.getAttribute(ew.getEdge(), "isordered", AttrTypeEnum.BOOL);
            } else if (ew.getType()
                .equals("to")) {
                targetClass = visitClass(tm, ew.getTarget(), graphNamespace);
                toLimits = getLimits(ew.getEdge());
                toOrdered =
                    (Boolean) GxlUtil.getAttribute(ew.getEdge(), "isordered", AttrTypeEnum.BOOL);
            }
        }

        for (EdgeWrapper ew : nodeWrapper.getIncomingEdges()) {
            // If some other edge inherits from this edge, or connects to another edge, then this edge automatically becomes complex as well
            if (ew.getType()
                .equals("isA")
                || ew.getType()
                    .equals("to")
                || ew.getType()
                    .equals("from")) {
                isComplex = true;
                break;
            }
        }

        assert(sourceClass != null && targetClass != null);

        List<Class> superClasses = new ArrayList<>();
        Boolean isAbstract =
            (Boolean) GxlUtil.getAttribute(nodeWrapper.getNode(), "isabstract", AttrTypeEnum.BOOL);
        boolean hasAttributes = false;
        for (EdgeWrapper ew : nodeWrapper.getEdges()) {
            if (ew.getType()
                .equals("isA")) {
                if (this.m_useComplex) {
                    superClasses.add(visitClass(tm, ew.getTarget(), graphNamespace));
                }
            } else if (ew.getType()
                .equals("hasAttribute")) {
                hasAttributes = true;
            }
        }

        boolean isAggregate = false;
        boolean reverseAggregate = false;
        if (!nodeWrapper.getType()
            .equals("EdgeClass")) {
            // Must be aggregate or composite
            isAggregate = true;
            // read the attribute, either "from" or "to"
            String eVal = (String) GxlUtil.getAttribute(nodeWrapper.getNode(),
                "aggregate",
                GxlUtil.AttrTypeEnum.ENUM);
            if (eVal != null) {
                if (eVal.equals("to")) {
                    reverseAggregate = true;
                }
            }
        }

        isComplex = isComplex || (isAbstract != null && isAbstract) || hasAttributes
            || reverseAggregate || superClasses.size() != 0;
        // The isorderedFrom attribute also makes a edge complex technically, but the fromOrder attribute is ignored in instances
        // so ignored here as well

        if (this.m_useComplex && isComplex) {
            String edgeName = sourceClass.getId()
                .getName() + "_" + name;
            Class edgeClass = tm.getClass(Id.getId(graphNamespace, Name.getName(edgeName)), true);

            Type fromType = sourceClass;
            Type toType = targetClass;
            if (fromLimits.upper > 1 || toLimits.upper == -1) {
                if (fromOrdered) {
                    fromType = new Container(Kind.ORD, fromType);
                } else {
                    fromType = new Container(Kind.SET, fromType);
                }
            }
            if (toLimits.upper > 1 || toLimits.upper == -1) {
                if (toOrdered) {
                    toType = new Container(Kind.ORD, toType);
                } else {
                    toType = new Container(Kind.SET, toType);
                }
            }

            //Field fromField = new Field(Name.getName("from"), fromType, fromLimits.lower, fromLimits.upper);
            //Field toField = new Field(Name.getName("to"), toType, toLimits.lower, toLimits.upper);

            //TODO: edge direction is inverted, making from and to limit useless
            //OUT mult is 1..1, IN mult should be those from the edges
            Field fromField = new Field(Name.getName("from"), fromType, 1, 1);
            Field toField = new Field(Name.getName("to"), toType, 1, 1);

            edgeClass.addField(fromField);
            edgeClass.addField(toField);

            if (isAbstract != null && isAbstract) {
                tm.addProperty(new AbstractProperty(edgeClass));
            }
            for (Class superClass : superClasses) {
                edgeClass.addSuperClass(superClass);
            }
            if (hasAttributes) {
                for (EdgeWrapper ew : nodeWrapper.getEdges()) {
                    if (ew.getType()
                        .equals("hasAttribute")) {
                        Field attribField =
                            visitAttribute(tm, edgeClass, ew.getTarget(), graphNamespace);
                        edgeClass.addField(attribField);
                    }
                }
            }
            if (isAggregate) {
                tm.addProperty(new ContainmentProperty(edgeClass,
                    Name.getName(reverseAggregate ? "from" : "to")));
            }

            this.m_nodeValues.put(node, edgeClass);
            this.m_idToType.put(node.getId(), edgeClass);
            this.m_complexEdgeIds.add(node.getId());

            return edgeClass;
        }

        // From here on simple edge type
        if (isAggregate) {
            tm.addProperty(new ContainmentProperty(sourceClass, Name.getName(name)));
        }

        Type targetType = targetClass;
        if ((toLimits.upper > 1 || toLimits.upper == -1) && toOrdered) {
            targetType = new Container(Kind.ORD, targetType);
        }
        Field f = new Field(Name.getName(name), targetType, toLimits.lower, toLimits.upper);
        this.m_nodeValues.put(node, f);
        this.m_idToField.put(node.getId(), f);

        sourceClass.addField(f);

        //TODO: isdirected=false

        return f;
    }

    /**
     * Create a Type in the given TypeModel corresponding to the attribute type in the GXL type model as represented by the given NodeWrapper.
     * GXL nodes: Bag, Set, Seq, Tup, and corresponding hasComponent edge. Locator, Bool, Float, Int, String. Enum
     * This encompasses datatypes, container types, tuple types and enumerations
     * @param tm TypeModel to add to type
     * @param nodeWrapper Node representing the type
     * @return The generated type, or null on error
     */
    private Type visitType(TypeModel tm, NodeWrapper nodeWrapper, Id graphNamespace) {
        NodeType node = nodeWrapper.getNode();
        if (this.m_nodeValues.containsKey(node)) {
            Object val = this.m_nodeValues.get(node);
            assert(val instanceof Type);
            return (Type) val;
        }

        String type = nodeWrapper.getType();

        if (g_simpleTypeMap.containsKey(type)) {
            this.m_nodeValues.put(node, g_simpleTypeMap.get(type));
            this.m_idToType.put(node.getId(), g_simpleTypeMap.get(type));
            return g_simpleTypeMap.get(type);
        }

        if (g_complexTypeSet.contains(type)) {
            List<Type> components = new ArrayList<>();
            // sort the edges, as they are ordered for composite types
            nodeWrapper.sortEdges();
            //Should have hasComponent attributes
            for (EdgeWrapper edge : nodeWrapper.getEdges()) {
                if (edge.getType()
                    .equals("hasComponent")) {
                    // Make sure type is visited
                    components.add(visitType(tm, edge.getTarget(), graphNamespace));
                }
            }

            Type t = null;
            assert(components.size() > 0);

            if (type.equals("Tup")) {
                t = new Tuple(components.toArray(new Type[components.size()]));
            } else {
                assert(components.size() == 1);
                if (type.equals("Set")) {
                    t = new Container(Kind.SET, components.get(0));
                } else if (type.equals("Bag")) {
                    t = new Container(Kind.BAG, components.get(0));
                } else if (type.equals("Seq")) {
                    t = new Container(Kind.SEQ, components.get(0));
                }
            }

            this.m_nodeValues.put(node, t);
            this.m_idToType.put(node.getId(), t);
            return t;
        }

        if ("Enum".equals(type)) {
            Enum e = visitEnum(tm, nodeWrapper, graphNamespace);
            return e;
        }

        assert(false);
        return null;
    }

    /**
     * Create an Enum for the given TypeModel and NodeWrapper.
     * This accounts for the "Enum" and "EnumVal" nodes in the GXL type model, as well as the corresponding "containsValue" edge.
     * @param tm TypeModel to add the Enum to.
     * @param nodeWrapper NodeWrapper representing the node in the GXL type model for the enum. Should be of type "Enum"
     * @return The created Enum, or null on error
     */
    private Enum visitEnum(TypeModel tm, NodeWrapper nodeWrapper, Id graphNamespace) {
        NodeType node = nodeWrapper.getNode();
        if (this.m_nodeValues.containsKey(node)) {
            Object val = this.m_nodeValues.get(node);
            assert(val instanceof Enum);
            return (Enum) val;
        }

        //String name = (String) getAttribute(nodeWrapper.getNode(), "name", AttrTypeEnum.STRING);
        String name = nodeWrapper.getNode()
            .getId();
        Id enumId = Id.getId(graphNamespace, Name.getName(name));

        List<Name> values = new ArrayList<>();
        for (EdgeWrapper ew : nodeWrapper.getEdges()) {
            if (ew.getType()
                .equals("containsValue")) {
                NodeWrapper valueNode = ew.getTarget();
                assert("EnumVal".equals(valueNode.getType()));

                String value = (String) GxlUtil.getAttribute(valueNode.getNode(),
                    "value",
                    GxlUtil.AttrTypeEnum.STRING);
                values.add(Name.getName(value));
            }
        }

        //Note: actually visiting values before storing enum, but not a problem since enums generally have no recursive behavior
        //cmEnum = new Enum(enumId, values.toArray(new Name[values.size()]));
        Enum cmEnum = tm.getEnum(enumId, true);
        for (Name litName : values) {
            cmEnum.addLiteral(litName);
        }

        this.m_nodeValues.put(node, cmEnum);
        this.m_idToType.put(node.getId(), cmEnum);

        return cmEnum;
    }

    private Value visitValue(TypeModel tm, NodeWrapper nodeWrapper, Type type, Id graphNamespace) {
        String nodeType = nodeWrapper.getType();
        NodeType valueNode = nodeWrapper.getNode();

        // Locators treated as string
        if (nodeType.equals("LocatorVal")) {
            String value = (String) GxlUtil.getAttribute(valueNode, "uri", AttrTypeEnum.STRING);
            if (type instanceof StringType) {
                return new groove.io.conceptual.value.StringValue(value);
            } else {
                addMessage(new Message(
                    "Trying to parse locator value " + value + " while expected type is " + type,
                    MessageType.ERROR));
                return null;
            }
        } else if (nodeType.equals("BoolVal")) {
            String valueString =
                (String) GxlUtil.getAttribute(valueNode, "value", AttrTypeEnum.STRING);
            if (type instanceof BoolType) {
                return BoolType.instance()
                    .valueFromString(valueString);
            } else {
                addMessage(new Message(
                    "Trying to parse bool value " + valueString + " while expected type is " + type,
                    MessageType.ERROR));
                return null;
            }
        } else if (nodeType.equals("FloatVal")) {
            String value = (String) GxlUtil.getAttribute(valueNode, "value", AttrTypeEnum.STRING);
            try {
                if (type instanceof RealType) {
                    return new groove.io.conceptual.value.RealValue(Float.parseFloat(value));
                } else {
                    addMessage(new Message(
                        "Trying to parse real value " + value + " while expected type is " + type,
                        MessageType.ERROR));
                    return null;
                }
            } catch (NumberFormatException e) {
                addMessage(
                    new Message("Unable to parse value " + value + " as float", MessageType.ERROR));
                return null;
            }
        } else if (nodeType.equals("IntVal")) {
            String value = (String) GxlUtil.getAttribute(valueNode, "value", AttrTypeEnum.STRING);
            try {
                if (type instanceof IntType) {
                    return new groove.io.conceptual.value.IntValue(Integer.parseInt(value));
                } else {
                    addMessage(new Message(
                        "Trying to parse int value " + value + " while expected type is " + type,
                        MessageType.ERROR));
                    return null;
                }
            } catch (NumberFormatException e) {
                addMessage(new Message("Unable to parse value " + value + " as integer",
                    MessageType.ERROR));
                return null;
            }
        } else if (nodeType.equals("StringVal")) {
            String value = (String) GxlUtil.getAttribute(valueNode, "value", AttrTypeEnum.STRING);
            if (type instanceof StringType) {
                return new groove.io.conceptual.value.StringValue(value);
            } else {
                addMessage(new Message(
                    "Trying to parse string value " + value + " while expected type is " + type,
                    MessageType.ERROR));
                return null;
            }
        } else if (nodeType.equals("EnumVal")) {
            String value = (String) GxlUtil.getAttribute(valueNode, "value", AttrTypeEnum.STRING);
            if (type instanceof Enum) {
                return new groove.io.conceptual.value.EnumValue((Enum) type, Name.getName(value));
            } else {
                addMessage(new Message(
                    "Trying to parse enum value " + value + " while expected type is " + type,
                    MessageType.ERROR));
                return null;
            }
        }
        // composite types
        else if (nodeType.equals("BagVal") || nodeType.equals("SetVal")
            || nodeType.equals("SeqVal")) {
            Kind ct = Kind.BAG;
            if (nodeType.equals("BagVal")) {
                ct = Kind.BAG;
            } else if (nodeType.equals("SetVal")) {
                ct = Kind.SET;
            } else if (nodeType.equals("SeqVal")) {
                ct = Kind.SEQ;
            }
            if (type instanceof Container && ((Container) type).getContainerType() == ct) {
                ContainerValue cv = new ContainerValue((Container) type);
                for (EdgeWrapper ew : nodeWrapper.getEdges()) {
                    if (ew.getType()
                        .equals("hasComponentValue")) {
                        Value v = visitValue(tm,
                            ew.getTarget(),
                            ((Container) type).getType(),
                            graphNamespace);
                        cv.addValue(v);
                    }
                }
                return cv;
            } else {
                addMessage(new Message("Trying to parse " + nodeType
                    + " container value, while expected type is " + type, MessageType.ERROR));
                return null;
            }
        } else if (nodeType.equals("TupVal")) {
            if (type instanceof Tuple) {
                Tuple tupleType = (Tuple) type;
                List<Value> values = new ArrayList<>();
                int size = tupleType.getTypes()
                    .size();
                assert size == nodeWrapper.getEdges()
                    .size();
                for (int i = 0; i < size; i++) {
                    EdgeWrapper ew = nodeWrapper.getEdges()
                        .get(i);
                    if (ew.getType()
                        .equals("hasComponentValue")) {
                        Value v = visitValue(tm, ew.getTarget(), tupleType.getTypes()
                            .get(i), graphNamespace);
                        values.add(v);
                    }
                }
                TupleValue tv =
                    new TupleValue((Tuple) type, values.toArray(new Value[values.size()]));
                return tv;
            } else {
                addMessage(new Message("Trying to parse tuple value while expected type is " + type,
                    MessageType.ERROR));
                return null;
            }
        } else {
            addMessage(new Message("Unable to parse value node " + nodeType, MessageType.ERROR));
            return null;
        }
    }

    private class Limits {
        public int lower;
        public int upper;

        public Limits() {
            this.lower = 0;
            this.upper = -1;
        }

        public Limits(BigInteger lowerLimit, BigInteger upperLimit) {
            this.lower = lowerLimit.intValue();
            this.upper = upperLimit.intValue();
        }

        public boolean isDefault() {
            return this.lower == 0 && this.upper == -1;
        }
    }

    private Limits getLimits(EdgeType edge) {
        TupType limits = (TupType) GxlUtil.getAttribute(edge, "limits", GxlUtil.AttrTypeEnum.TUP);
        if (limits == null || limits.getBagOrSetOrSeq()
            .size() != 2) {
            return new Limits();
        }

        BigInteger lower = (BigInteger) limits.getBagOrSetOrSeq()
            .get(0)
            .getValue();
        BigInteger upper = (BigInteger) limits.getBagOrSetOrSeq()
            .get(1)
            .getValue();

        return new Limits(lower, upper);
    }

    /** Returns the conceptual type corresponding to a given name, if any. */
    public Type getIdType(String id) {
        return this.m_idToType.get(id);
    }

    /** Returns the conceptual field corresponding to a given name, if any. */
    public Field getIdField(String id) {
        return this.m_idToField.get(id);
    }

    /** Returns the conceptual identifier corresponding to a given graph name, if any. */
    public Id getGraphId(String graphId) {
        return this.m_graphNamespaces.get(graphId);
    }

    /** Tests if a given edge name belongs to a composte edge. */
    public boolean isComplex(String edgeId) {
        return this.m_complexEdgeIds.contains(edgeId);
    }
}
