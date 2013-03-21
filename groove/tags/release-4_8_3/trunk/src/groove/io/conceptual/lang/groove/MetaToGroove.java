package groove.io.conceptual.lang.groove;

import groove.graph.GraphRole;
import groove.io.conceptual.Acceptor;
import groove.io.conceptual.Field;
import groove.io.conceptual.TypeModel;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.configuration.schema.NullableType;
import groove.io.conceptual.graph.AbsEdge;
import groove.io.conceptual.graph.AbsNode;
import groove.io.conceptual.lang.ExportableResource;
import groove.io.conceptual.lang.TypeExporter;
import groove.io.conceptual.type.Class;
import groove.io.conceptual.type.Container;
import groove.io.conceptual.type.CustomDataType;
import groove.io.conceptual.type.DataType;
import groove.io.conceptual.type.Enum;
import groove.io.conceptual.type.Tuple;
import groove.io.external.PortException;

import java.util.HashMap;
import java.util.Map;

// Generates meta schema for type graph
public class MetaToGroove extends TypeExporter<AbsNode> {
    private GrooveResource m_grooveResource;
    private Config m_cfg;
    private GrammarGraph m_currentGraph;
    private Map<MetaType,AbsNode> m_metaNodes = new HashMap<MetaType,AbsNode>();

    public MetaToGroove(GrooveResource grooveResource) {
        m_grooveResource = grooveResource;
        m_cfg = m_grooveResource.getConfig();
    }

    @Override
    public void addTypeModel(TypeModel typeModel) throws PortException {
        // Only create meta graph if config requires it
        if (m_cfg.getConfig().getTypeModel().isMetaSchema()) {
            m_currentGraph = m_grooveResource.getGraph(GrooveUtil.getSafeId(typeModel.getName()) + "_meta", GraphRole.TYPE);
            setupMetaModel();
            visitTypeModel(typeModel, m_cfg);
        }
    }

    @Override
    public ExportableResource getResource() {
        return m_grooveResource;
    }

    @Override
    protected void setElement(Acceptor o, AbsNode n) {
        super.setElement(o, n);
        if (n != null) {
            m_currentGraph.m_nodes.put(o, n);
        }
    }

    private enum MetaType {
        Class, ClassNullable, Enum, Intermediate, Type, ContainerSet, ContainerBag, ContainerSeq, ContainerOrd, DataType, Tuple;
    }

    private AbsNode getMetaNode(MetaType type) {
        if (m_metaNodes.containsKey(type)) {
            return m_metaNodes.get(type);
        }
        String name = "";
        switch (type) {
            case Type:
                name = m_cfg.getStrings().getMetaType();
                break;
            case Class:
                name = m_cfg.getStrings().getMetaClass();
                break;
            case ClassNullable:
                name = m_cfg.getStrings().getMetaClassNullable();
                break;
            case Enum:
                name = m_cfg.getStrings().getMetaEnum();
                break;
            case DataType:
                name = m_cfg.getStrings().getMetaDataType();
                break;
            case Tuple:
                name = m_cfg.getStrings().getMetaTuple();
                break;
            case ContainerSet:
                name = m_cfg.getStrings().getMetaContainerSet();
                break;
            case ContainerBag:
                name = m_cfg.getStrings().getMetaContainerBag();
                break;
            case ContainerSeq:
                name = m_cfg.getStrings().getMetaContainerSeq();
                break;
            case ContainerOrd:
                name = m_cfg.getStrings().getMetaContainerOrd();
                break;
            case Intermediate:
                name = m_cfg.getStrings().getMetaIntermediate();
                break;
            default:
                throw new RuntimeException();
        }
        AbsNode metaNode = new AbsNode("type:" + name);
        m_metaNodes.put(type, metaNode);
        return metaNode;
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

    @Override
    public void visit(Class c, java.lang.Object param) {
        if (hasElement(c)) {
            return;
        }

        // If not using the nullable/proper class system, don't instantiate nullable classes
        if (m_cfg.getConfig().getGlobal().getNullable() == NullableType.NONE) {
            if (!c.isProper()) {
                // Simply revert to the proper instance
                AbsNode classNode = getElement(c.getProperClass());
                if (!hasElement(c)) {
                    setElement(c, classNode);
                }
                return;
            }
        }

        AbsNode classNode = new AbsNode(m_cfg.getName(c));
        setElement(c, classNode);

        AbsNode classMetaNode = getMetaNode(c.isProper() ? MetaType.Class : MetaType.ClassNullable);
        new AbsEdge(classNode, classMetaNode, "sub:");

        for (Field f : c.getFields()) {
            getElement(f, null, true);
        }

        if (c.isProper()) {
            if (m_cfg.getConfig().getGlobal().getNullable() == NullableType.ALL) {
                getElement(c.getNullableClass());
            }
        } else {
            getElement(c.getProperClass());
        }
    }

    @Override
    public void visit(Field field, java.lang.Object param) {
        if (hasElement(field)) {
            return;
        }

        if (!(field.getType() instanceof DataType) || field.getType() instanceof CustomDataType) {
            AbsNode fieldTypeNode = getElement(field.getType(), m_cfg.getName(field));
            // Can happen if type is container and not using intermediate
            if (fieldTypeNode == null) {
                assert (field.getType() instanceof Container);
                return;
            }

            if (m_cfg.useIntermediate(field) && !(field.getType() instanceof Container)) {
                AbsNode interNode = new AbsNode(m_cfg.getName(field));
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
    public void visit(DataType dt, java.lang.Object param) {
        if (hasElement(dt)) {
            return;
        }

        if (dt instanceof CustomDataType) {
            AbsNode dataNode = new AbsNode(m_cfg.getName(dt));
            setElement(dt, dataNode);

            AbsNode dataMetaNode = getMetaNode(MetaType.DataType);
            new AbsEdge(dataNode, dataMetaNode, "sub:");
        }
    }

    @Override
    public void visit(Enum e, java.lang.Object param) {
        if (hasElement(e)) {
            return;
        }

        AbsNode enumNode = new AbsNode(m_cfg.getName(e), "abs:");
        setElement(e, enumNode);

        AbsNode enumMetaNode = getMetaNode(MetaType.Enum);
        new AbsEdge(enumNode, enumMetaNode, "sub:");

        return;
    }

    @Override
    public void visit(Container c, java.lang.Object param) {
        if (hasElement(c)) {
            return;
        }

        if (param == null || !(param instanceof String)) {
            throw new IllegalArgumentException("Container visitor requires String argument");
        }
        String containerId = (String) param;

        if (c.getType() instanceof Container) {
            getElement(c.getType(), m_cfg.getContainerName(containerId, (Container) c.getType()));
        }

        if (!m_cfg.useIntermediate(c)) {
            setElement(c, null);
            return;
        }

        AbsNode containerNode = new AbsNode(containerId + m_cfg.getContainerPostfix(c));
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
    public void visit(Tuple tuple, java.lang.Object param) {
        if (hasElement(tuple)) {
            return;
        }

        AbsNode tupleNode = new AbsNode(m_cfg.getName(tuple));
        setElement(tuple, tupleNode);

        AbsNode tupleMetaNode = getMetaNode(MetaType.Tuple);
        new AbsEdge(tupleNode, tupleMetaNode, "sub:");

        return;
    }
}
