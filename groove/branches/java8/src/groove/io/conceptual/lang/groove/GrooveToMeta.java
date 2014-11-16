package groove.io.conceptual.lang.groove;

import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeNode;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.lang.Message;
import groove.io.conceptual.lang.Message.MessageType;
import groove.io.conceptual.lang.Messenger;
import groove.io.conceptual.lang.groove.GraphNodeTypes.ModelType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * Should only generate some map of Node types (strings) to NodeType enum in TypeGraphVisitor
 */
/** Bridge from GROOVE type meta-information to a {@link GraphNodeTypes} object. */
public class GrooveToMeta implements Messenger {
    /** Constructs an instance for a given type graph and node types object. */
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
                Set<TypeNode> superTypes = node.getGraph().getDirectSupertypeMap().get(node);
                if (superTypes.size() > 1) {
                    addMessage(new Message("Node has multiple supertypes in meta type graph: "
                        + node.toString(), MessageType.WARNING));
                } else if (superTypes.size() == 0) {
                    addMessage(new Message("Node has no meta type: " + node.toString(),
                        MessageType.WARNING));
                } else {
                    this.m_types.addModelType(node.label().text(),
                        getModelType(getNodeType(superTypes.iterator().next())));
                }
            }
        }

        // And we're done
    }

    private final Config m_cfg;
    private final GraphNodeTypes m_types;

    private MetaType getNodeType(TypeNode node) {
        MetaType result = this.m_metaNodes.get(node);
        if (result == null) {
            String label = node.label().text();
            result = getMetaType(label);
            if (result != MetaType.None) {
                this.m_metaNodes.put(node, result);
            }
        }
        return result;
    }

    private final Map<TypeNode,MetaType> m_metaNodes = new HashMap<TypeNode,MetaType>();

    /** Returns the meta-type for a given type name. */
    private MetaType getMetaType(String name) {
        if (this.metaTypeMap == null) {
            this.metaTypeMap = MetaType.createMap(this.m_cfg.getMeta());
        }
        MetaType result = this.metaTypeMap.get(name);
        if (result == null) {
            result = MetaType.None;
        }
        return result;
    }

    private Map<String,MetaType> metaTypeMap;

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

    private final List<Message> m_messages = new ArrayList<Message>();
}
