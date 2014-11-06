package groove.io.conceptual.lang.groove;

import groove.graph.GraphRole;
import groove.io.conceptual.Acceptor;
import groove.io.conceptual.Field;
import groove.io.conceptual.Glossary;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.configuration.schema.NullableType;
import groove.io.conceptual.configuration.schema.StringsType;
import groove.io.conceptual.graph.AbsEdge;
import groove.io.conceptual.graph.AbsNode;
import groove.io.conceptual.lang.GlossaryExportBuilder;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.CustomDataType;
import groove.io.conceptual.type.DataType;
import groove.io.conceptual.type.Enum;
import groove.io.conceptual.type.Tuple;
import groove.io.external.PortException;
import groove.util.Exceptions;

import java.util.HashMap;
import java.util.Map;

// Generates meta schema for type graph
/** Bridge from meta-type graph to GROOVE export. */
public class MetaToGroove extends GlossaryExportBuilder<GrooveExport,AbsNode> {
    /** Constructs an instance for a given glossary and export object. */
    public MetaToGroove(Glossary glos, GrooveExport export) {
        super(glos, export);
        this.m_cfg = export.getConfig();
    }

    private final Config m_cfg;

    @Override
    public void build() throws PortException {
        // Only create meta graph if config requires it
        if (this.m_cfg.getXMLConfig().getTypeModel().isMetaSchema()) {
            setupMetaModel();
            this.m_cfg.setGlossary(getGlossary());
            super.build();
        }
    }

    private GrammarGraph getGrammarGraph() {
        if (this.m_currentGraph == null) {
            this.m_currentGraph =
                getExport().getGraph(GrooveUtil.getSafeId(getGlossary().getName()) + "_meta",
                    GraphRole.TYPE);
        }
        return this.m_currentGraph;
    }

    private GrammarGraph m_currentGraph;

    @Override
    protected void setElement(Acceptor o, AbsNode n) {
        super.setElement(o, n);
        if (n != null) {
            getGrammarGraph().m_nodes.put(o, n);
        }
    }

    private enum MetaType {
        Class,
        ClassNullable,
        Enum,
        Intermediate,
        Type,
        ContainerSet,
        ContainerBag,
        ContainerSeq,
        ContainerOrd,
        DataType,
        Tuple;
    }

    private void setupMetaModel() {
        AbsNode typeNode = getMetaNode(MetaType.Type);
        AbsNode classNode = getMetaNode(MetaType.Class);
        AbsNode classNullableNode = getMetaNode(MetaType.ClassNullable);
        AbsNode enumNode = getMetaNode(MetaType.Enum);
        AbsNode dataNode = getMetaNode(MetaType.DataType);
        AbsNode tupleNode = getMetaNode(MetaType.Tuple);
        AbsNode intermediateNode = getMetaNode(MetaType.Intermediate);
        AbsNode containerNodeSet = getMetaNode(MetaType.ContainerSet);
        AbsNode containerNodeBag = getMetaNode(MetaType.ContainerBag);
        AbsNode containerNodeSeq = getMetaNode(MetaType.ContainerSeq);
        AbsNode containerNodeOrd = getMetaNode(MetaType.ContainerOrd);

        // Everything is a type
        new AbsEdge(classNode, typeNode, "sub:");
        new AbsEdge(classNullableNode, typeNode, "sub:");
        new AbsEdge(enumNode, typeNode, "sub:");
        new AbsEdge(dataNode, typeNode, "sub:");
        new AbsEdge(tupleNode, typeNode, "sub:");
        new AbsEdge(intermediateNode, typeNode, "sub:");
        new AbsEdge(containerNodeSet, intermediateNode, "sub:");
        new AbsEdge(containerNodeBag, intermediateNode, "sub:");
        new AbsEdge(containerNodeSeq, intermediateNode, "sub:");
        new AbsEdge(containerNodeOrd, intermediateNode, "sub:");
    }

    private AbsNode getMetaNode(MetaType type) {
        AbsNode result = this.m_metaNodes.get(type);
        if (result == null) {
            this.m_metaNodes.put(type, result = computeMetaNode(type));
        }
        return result;
    }

    private final Map<MetaType,AbsNode> m_metaNodes = new HashMap<MetaType,AbsNode>();

    private AbsNode computeMetaNode(MetaType type) {
        StringsType strings = this.m_cfg.getStrings();
        String name;
        switch (type) {
        case Type:
            name = strings.getMetaType();
            break;
        case Class:
            name = strings.getMetaClass();
            break;
        case ClassNullable:
            name = strings.getMetaClassNullable();
            break;
        case Enum:
            name = strings.getMetaEnum();
            break;
        case DataType:
            name = strings.getMetaDataType();
            break;
        case Tuple:
            name = strings.getMetaTuple();
            break;
        case ContainerSet:
            name = strings.getMetaContainerSet();
            break;
        case ContainerBag:
            name = strings.getMetaContainerBag();
            break;
        case ContainerSeq:
            name = strings.getMetaContainerSeq();
            break;
        case ContainerOrd:
            name = strings.getMetaContainerOrd();
            break;
        case Intermediate:
            name = strings.getMetaIntermediate();
            break;
        default:
            throw Exceptions.UNREACHABLE;
        }
        return new AbsNode("type:" + name);
    }

    @Override
    public void addClass(Class c) {
        if (hasElement(c)) {
            return;
        }

        // If not using the nullable/proper class system, don't instantiate nullable classes
        if (this.m_cfg.getXMLConfig().getGlobal().getNullable() == NullableType.NONE) {
            if (!c.isProper()) {
                // Simply revert to the proper instance
                AbsNode classNode = getElement(c.getProperClass());
                if (!hasElement(c)) {
                    setElement(c, classNode);
                }
                return;
            }
        }

        AbsNode classNode = new AbsNode(this.m_cfg.getName(c));
        setElement(c, classNode);

        AbsNode classMetaNode = getMetaNode(c.isProper() ? MetaType.Class : MetaType.ClassNullable);
        new AbsEdge(classNode, classMetaNode, "sub:");

        for (Field f : c.getFields()) {
            getElement(f, null, true);
        }

        if (c.isProper()) {
            if (this.m_cfg.getXMLConfig().getGlobal().getNullable() == NullableType.ALL) {
                getElement(c.getNullableClass());
            }
        } else {
            getElement(c.getProperClass());
        }
    }

    @Override
    public void addField(Field field) {
        if (hasElement(field)) {
            return;
        }

        if (!(field.getType() instanceof DataType) || field.getType() instanceof CustomDataType) {
            AbsNode fieldTypeNode = getElement(field.getType(), this.m_cfg.getName(field));
            // Can happen if type is container and not using intermediate
            if (fieldTypeNode == null) {
                assert (field.getType() instanceof Container);
                return;
            }

            if (this.m_cfg.useIntermediate(field) && !(field.getType() instanceof Container)) {
                AbsNode interNode = new AbsNode(this.m_cfg.getName(field));
                setElement(field, interNode);

                AbsNode fieldMetaNode = getMetaNode(MetaType.Intermediate);
                new AbsEdge(interNode, fieldMetaNode, "sub:");
            } else {
                setElement(field, fieldTypeNode);
            }
        } else {
            //setElement(field, null);
        }
    }

    @Override
    public void addDataType(DataType dt) {
        if (hasElement(dt)) {
            return;
        }

        if (dt instanceof CustomDataType) {
            AbsNode dataNode = new AbsNode(this.m_cfg.getName(dt));
            setElement(dt, dataNode);

            AbsNode dataMetaNode = getMetaNode(MetaType.DataType);
            new AbsEdge(dataNode, dataMetaNode, "sub:");
        }
    }

    @Override
    public void addEnum(Enum e) {
        if (hasElement(e)) {
            return;
        }

        AbsNode enumNode = new AbsNode(this.m_cfg.getName(e), "abs:");
        setElement(e, enumNode);

        AbsNode enumMetaNode = getMetaNode(MetaType.Enum);
        new AbsEdge(enumNode, enumMetaNode, "sub:");

        return;
    }

    @Override
    public void addContainer(Container c, String base) {
        if (hasElement(c)) {
            return;
        }

        assert base != null;

        if (c.getType() instanceof Container) {
            getElement(c.getType(), this.m_cfg.getContainerName(base, (Container) c.getType()));
        }

        if (!this.m_cfg.useIntermediate(c)) {
            setElement(c, null);
            return;
        }

        AbsNode containerNode = new AbsNode(base + this.m_cfg.getContainerPostfix(c));
        setElement(c, containerNode);

        AbsNode orderedNode = null;
        switch (c.getContainerType()) {
        case SET:
            orderedNode = getMetaNode(MetaType.ContainerSet);
            break;
        case BAG:
            orderedNode = getMetaNode(MetaType.ContainerBag);
            break;
        case SEQ:
            orderedNode = getMetaNode(MetaType.ContainerSeq);
            break;
        case ORD:
            orderedNode = getMetaNode(MetaType.ContainerOrd);
            break;
        }
        new AbsEdge(containerNode, orderedNode, "sub:");

        return;
    }

    @Override
    public void addTuple(Tuple tuple) {
        if (hasElement(tuple)) {
            return;
        }

        AbsNode tupleNode = new AbsNode(this.m_cfg.getName(tuple));
        setElement(tuple, tupleNode);

        AbsNode tupleMetaNode = getMetaNode(MetaType.Tuple);
        new AbsEdge(tupleNode, tupleMetaNode, "sub:");

        return;
    }
}
