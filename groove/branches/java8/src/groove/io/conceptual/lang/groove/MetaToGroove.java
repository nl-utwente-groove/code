package groove.io.conceptual.lang.groove;

import groove.graph.GraphRole;
import groove.io.conceptual.Concept;
import groove.io.conceptual.Field;
import groove.io.conceptual.Glossary;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.configuration.schema.NullableType;
import groove.io.conceptual.configuration.schema.StringsType;
import groove.io.conceptual.graph.AbsEdge;
import groove.io.conceptual.graph.AbsNode;
import groove.io.conceptual.lang.GlossaryExportBuilder;
import groove.io.conceptual.property.AbstractProperty;
import groove.io.conceptual.property.ContainmentProperty;
import groove.io.conceptual.property.DefaultValueProperty;
import groove.io.conceptual.property.IdentityProperty;
import groove.io.conceptual.property.KeysetProperty;
import groove.io.conceptual.property.OppositeProperty;
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
        if (this.m_cfg.getTypeModel().isMetaSchema()) {
            setupMetaModel();
            this.m_cfg.setGlossary(getGlossary());
            super.build();
        }
    }

    @Override
    protected boolean put(Concept c, AbsNode n) {
        boolean result = super.put(c, n);
        if (result && n != null) {
            getGrammarGraph().addNodes(n);
        }
        return result;
    }

    private PreGraph getGrammarGraph() {
        if (this.m_currentGraph == null) {
            this.m_currentGraph =
                getExport().getGraph(GrooveUtil.getSafeId(getGlossary().getName()) + "_meta",
                    GraphRole.TYPE);
        }
        return this.m_currentGraph;
    }

    private PreGraph m_currentGraph;

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
    protected AbsNode addClass(Class c) {
        AbsNode result;
        // If not using the nullable/proper class system, don't instantiate nullable classes
        if (this.m_cfg.getXMLConfig().getGlobal().getNullable() == NullableType.NONE
            && !c.isProper()) {
            // Simply revert to the proper instance
            result = add(c.getProperClass());
            put(c, result);
        } else {
            result = new AbsNode(this.m_cfg.getName(c));
            put(c, result);

            AbsNode classMetaNode =
                getMetaNode(c.isProper() ? MetaType.Class : MetaType.ClassNullable);
            new AbsEdge(result, classMetaNode, "sub:");

            for (Field f : c.getFields()) {
                add(f);
            }
            if (c.isProper()) {
                if (this.m_cfg.getXMLConfig().getGlobal().getNullable() == NullableType.ALL) {
                    add(c.getNullableClass());
                }
            } else {
                add(c.getProperClass());
            }
        }
        return result;
    }

    @Override
    protected AbsNode addField(Field field) {
        AbsNode result = null;
        if (!(field.getType() instanceof DataType) || field.getType() instanceof CustomDataType) {
            result = add(field.getType(), this.m_cfg.getName(field));
            put(field, result);
            // Can happen if type is container and not using intermediate
            if (result == null) {
                assert (field.getType() instanceof Container);
            } else if (this.m_cfg.useIntermediate(field) && !(field.getType() instanceof Container)) {
                result = new AbsNode(this.m_cfg.getName(field));
                AbsNode fieldMetaNode = getMetaNode(MetaType.Intermediate);
                new AbsEdge(result, fieldMetaNode, "sub:");
            }
        }
        return result;
    }

    @Override
    protected AbsNode addDataType(DataType dt) {
        AbsNode result = null;
        if (dt instanceof CustomDataType) {
            result = new AbsNode(this.m_cfg.getName(dt));
            AbsNode dataMetaNode = getMetaNode(MetaType.DataType);
            new AbsEdge(result, dataMetaNode, "sub:");
        }
        put(dt, result);
        return result;
    }

    @Override
    protected AbsNode addEnum(Enum e) {
        AbsNode result = new AbsNode(this.m_cfg.getName(e), "abs:");
        put(e, result);
        AbsNode enumMetaNode = getMetaNode(MetaType.Enum);
        new AbsEdge(result, enumMetaNode, "sub:");

        return result;
    }

    @Override
    protected AbsNode addContainer(Container c, String base) {
        assert base != null;
        AbsNode result;
        if (c.getType() instanceof Container) {
            add(c.getType(), this.m_cfg.getContainerName(base, (Container) c.getType()));
        }
        if (this.m_cfg.useIntermediate(c)) {
            result = new AbsNode(base + this.m_cfg.getContainerPostfix(c));
            put(c, result);

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
            new AbsEdge(result, orderedNode, "sub:");
        } else {
            put(c, result = null);
        }
        return result;
    }

    @Override
    protected AbsNode addTuple(Tuple tuple) {
        AbsNode result = new AbsNode(this.m_cfg.getName(tuple));
        put(tuple, result);

        AbsNode tupleMetaNode = getMetaNode(MetaType.Tuple);
        new AbsEdge(result, tupleMetaNode, "sub:");

        return result;
    }

    @Override
    protected AbsNode addAbstractProp(AbstractProperty prop) {
        // this kind of property does not concern the meta-graph
        return null;
    }

    @Override
    protected AbsNode addContainmentProp(ContainmentProperty prop) {
        // this kind of property does not concern the meta-graph
        return null;
    }

    @Override
    protected AbsNode addIdentityProp(IdentityProperty prop) {
        // this kind of property does not concern the meta-graph
        return null;
    }

    @Override
    protected AbsNode addKeysetProp(KeysetProperty prop) {
        // this kind of property does not concern the meta-graph
        return null;
    }

    @Override
    protected AbsNode addOppositeProp(OppositeProperty prop) {
        // this kind of property does not concern the meta-graph
        return null;
    }

    @Override
    protected AbsNode addDefaultValueProp(DefaultValueProperty prop) {
        // this kind of property does not concern the meta-graph
        return null;
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
        String name = type.getName(strings);
        return new AbsNode("type:" + name);
    }
}
