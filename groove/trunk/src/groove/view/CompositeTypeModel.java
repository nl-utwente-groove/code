package groove.view;

import static groove.trans.ResourceKind.HOST;
import static groove.trans.ResourceKind.RULE;
import static groove.trans.ResourceKind.TYPE;
import groove.algebra.SignatureKind;
import groove.graph.LabelStore;
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.graph.TypeNode;
import groove.trans.ResourceKind;
import groove.view.aspect.AspectNode;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/** Class to store the models that are used to compose the type graph. */
public class CompositeTypeModel extends ResourceModel<TypeGraph> {
    CompositeTypeModel(GrammarModel grammar) {
        super(grammar, ResourceKind.TYPE, "Composed type for "
            + grammar.getName());
    }

    @Override
    public Object getSource() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /** Returns a mapping from names to type graphs,
     * which together make up the combined type model. */
    public Map<String,TypeGraph> getTypeGraphMap() {
        synchronise();
        return hasErrors() ? null
                : Collections.unmodifiableMap(this.typeGraphMap);
    }

    @Override
    TypeGraph compute() throws FormatException {
        TypeGraph result = null;
        Collection<FormatError> errors = createErrors();
        this.typeModelMap.clear();
        this.typeGraphMap.clear();
        this.labelStore = null;
        for (ResourceModel<?> typeModel : getGrammar().getResourceSet(TYPE)) {
            if (typeModel.isEnabled()) {
                this.typeModelMap.put(typeModel.getName(),
                    (TypeModel) typeModel);
                for (FormatError error : typeModel.getErrors()) {
                    errors.add(new FormatError("Error in type '%s': %s",
                        typeModel.getName(), error, typeModel.getSource()));
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
        // first test if there is something to be done
        result = new TypeGraph("combined type");
        if (this.typeModelMap.isEmpty()) {
            TypeNode top = TypeNode.TOP_NODE;
            result.addNode(top);
            for (SignatureKind sigKind : EnumSet.allOf(SignatureKind.class)) {
                result.addNode(TypeNode.getDataType(sigKind));
            }
            LabelStore labelStore = computeLabelStore();
            for (TypeLabel label : labelStore.getLabels()) {
                if (label.isBinary()) {
                    for (TypeNode target : result.nodeSet()) {
                        result.addEdge(top, label, target);
                    }
                } else {
                    result.addEdge(top, label, top);
                }
            }
        } else {
            // There are no errors in each of the models, try to compose the
            // type graph.
            Map<TypeNode,TypeNode> importNodes =
                new HashMap<TypeNode,TypeNode>();
            Map<TypeNode,TypeModel> importModels =
                new HashMap<TypeNode,TypeModel>();
            for (TypeModel model : this.typeModelMap.values()) {
                try {
                    TypeGraph graph = model.toResource();
                    this.typeGraphMap.put(model.getName(), graph);
                    Map<TypeNode,TypeNode> map = result.add(graph);
                    for (TypeNode node : graph.getImports()) {
                        importNodes.put(node, map.get(node));
                        importModels.put(node, model);
                    }
                } catch (FormatException e) {
                    errors.addAll(e.getErrors());
                } catch (IllegalArgumentException e) {
                    errors.add(new FormatError(e.getMessage()));
                }
            }
            // test that there are no imported types left
            for (Map.Entry<TypeNode,TypeNode> importEntry : importNodes.entrySet()) {
                if (importEntry.getValue().isImported()) {
                    TypeNode origNode = importEntry.getKey();
                    TypeModel origModel = importModels.get(origNode);
                    errors.add(new FormatError(
                        "Error in type graph '%s': Unresolved type import '%s'",
                        origModel.getName(), origNode.getLabel(), getInverse(
                            origModel.getMap().nodeMap(), origNode),
                        origModel.getSource()));
                }
            }
        }
        result.setFixed();
        if (errors.isEmpty()) {
            return result;
        } else {
            throw new FormatException(errors);
        }
    }

    /** Computes the label store or retrieves it from the type graph. */
    public LabelStore getLabelStore() {
        synchronise();
        if (this.labelStore == null) {
            // construct the label store
            LabelStore result;
            if (getResource() == null) {
                result = computeLabelStore();
            } else {
                // get the labels from the type graph
                result = getResource().getLabelStore();
            }
            this.labelStore = result;
        }
        return this.labelStore;
    }

    /**
     * Computes a label store by collecting the labels from all rules and host graphs.
     */
    private LabelStore computeLabelStore() {
        LabelStore result;
        // get the labels from the rules and host graphs
        result = new LabelStore();
        for (ResourceKind kind : EnumSet.of(RULE, HOST)) {
            for (ResourceModel<?> model : getGrammar().getResourceSet(kind)) {
                result.addLabels(((GraphBasedModel<?>) model).getLabels());
            }
        }
        HostModel host = getGrammar().getStartGraphModel();
        if (host != null) {
            result.addLabels(host.getLabels());
        }
        result.setFixed();
        return result;
    }

    private AspectNode getInverse(Map<AspectNode,?> map, TypeNode image) {
        AspectNode result = null;
        for (Map.Entry<AspectNode,?> entry : map.entrySet()) {
            if (entry.getValue().equals(image)) {
                return entry.getKey();
            }
        }
        return result;
    }

    /** Mapping from active type names to corresponding type models. */
    private final Map<String,TypeModel> typeModelMap =
        new HashMap<String,TypeModel>();
    private final Map<String,TypeGraph> typeGraphMap =
        new TreeMap<String,TypeGraph>();
    /**
     * The label store, either from the type graph
     * or independently computed.
     */
    private LabelStore labelStore;
}