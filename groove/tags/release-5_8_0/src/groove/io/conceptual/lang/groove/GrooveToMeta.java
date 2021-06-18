package groove.io.conceptual.lang.groove;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeNode;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.lang.Message;
import groove.io.conceptual.lang.Message.MessageType;
import groove.io.conceptual.lang.Messenger;
import groove.io.conceptual.lang.groove.GraphNodeTypes.ModelType;

/*
 * Should only generate some map of Node types (strings) to NodeType enum in TypeGraphVisitor
 */
@SuppressWarnings("javadoc")
public class GrooveToMeta implements Messenger {
    private Config m_cfg;

    List<Message> m_messages = new ArrayList<>();

    private Map<TypeNode,MetaType> m_metaNodes = new HashMap<>();
    private GraphNodeTypes m_types;

    public GrooveToMeta(TypeGraph grooveTypeGraph, GraphNodeTypes types, Config cfg) {
        this.m_types = types;
        this.m_cfg = cfg;

        // Get all the meta nodes
        for (TypeNode node : grooveTypeGraph.nodeSet()) {
            getNodeType(node);
        }

        // Map all the other nodes
        for (TypeNode node : grooveTypeGraph.nodeSet()) {
            if (getNodeType(node) == MetaType.None) {
                Set<TypeNode> superTypes = node.getGraph()
                    .getDirectSupertypeMap()
                    .get(node);
                if (superTypes.size() > 1) {
                    addMessage(new Message(
                        "Node has multiple supertypes in meta type graph: " + node.toString(),
                        MessageType.WARNING));
                } else if (superTypes.size() == 0) {
                    addMessage(new Message("Node has no meta type: " + node.toString(),
                        MessageType.WARNING));
                } else {
                    this.m_types.addModelType(node.label()
                        .text(),
                        getModelType(getNodeType(superTypes.iterator()
                            .next())));
                }
            }
        }

        // And we're done
    }

    // The actual meta nodes in the meta graph. Other nodes inherit from these and use ModelType enum
    private enum MetaType {
        Type,
        Class,
        ClassNullable,
        Enum,
        Intermediate,
        ContainerSet,
        ContainerBag,
        ContainerSeq,
        ContainerOrd,
        DataType,
        Tuple,
        None;
    }

    private MetaType getNodeType(TypeNode node) {
        if (this.m_metaNodes.containsKey(node)) {
            return this.m_metaNodes.get(node);
        }
        String label = node.label()
            .text();
        MetaType type = MetaType.None;

        if (label.equals(this.m_cfg.getStrings()
            .getMetaType())) {
            type = MetaType.Type;
        } else if (label.equals(this.m_cfg.getStrings()
            .getMetaClass())) {
            type = MetaType.Class;
        } else if (label.equals(this.m_cfg.getStrings()
            .getMetaClassNullable())) {
            type = MetaType.ClassNullable;
        } else if (label.equals(this.m_cfg.getStrings()
            .getMetaEnum())) {
            type = MetaType.Enum;
        } else if (label.equals(this.m_cfg.getStrings()
            .getMetaDataType())) {
            type = MetaType.DataType;
        } else if (label.equals(this.m_cfg.getStrings()
            .getMetaTuple())) {
            type = MetaType.Tuple;
        } else if (label.equals(this.m_cfg.getStrings()
            .getMetaContainerSet())) {
            type = MetaType.ContainerSet;
        } else if (label.equals(this.m_cfg.getStrings()
            .getMetaContainerBag())) {
            type = MetaType.ContainerBag;
        } else if (label.equals(this.m_cfg.getStrings()
            .getMetaContainerSeq())) {
            type = MetaType.ContainerSeq;
        } else if (label.equals(this.m_cfg.getStrings()
            .getMetaContainerOrd())) {
            type = MetaType.ContainerOrd;
        } else if (label.equals(this.m_cfg.getStrings()
            .getMetaIntermediate())) {
            type = MetaType.Intermediate;
        }

        this.m_metaNodes.put(node, type);

        return type;
    }

    private ModelType getModelType(MetaType metaType) {
        switch (metaType) {
        case Class:
            return ModelType.TypeClass;
        case ClassNullable:
            return ModelType.TypeClassNullable;
        case Enum:
            return ModelType.TypeEnum;
        case Intermediate:
            return ModelType.TypeIntermediate;
        case ContainerSet:
            return ModelType.TypeContainerSet;
        case ContainerBag:
            return ModelType.TypeContainerBag;
        case ContainerSeq:
            return ModelType.TypeContainerSeq;
        case ContainerOrd:
            return ModelType.TypeContainerOrd;
        case DataType:
            return ModelType.TypeDatatype;
        case Tuple:
            return ModelType.TypeTuple;
        case Type:
        case None:
        default:
            return ModelType.TypeNone;
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

    private void addMessage(Message msg) {
        this.m_messages.add(msg);
    }
}
