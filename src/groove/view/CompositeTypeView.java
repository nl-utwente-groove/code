package groove.view;

import groove.graph.TypeGraph;
import groove.graph.TypeNode;
import groove.view.aspect.AspectNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Class to store the views that are used to compose the type graph. */
public class CompositeTypeView {

    private Map<String,TypeView> typeViewMap;
    private Map<String,TypeGraph> typeGraphMap;
    private TypeGraph model;
    private List<FormatError> errors;

    CompositeTypeView(GrammarView grammarView) {
        this.typeViewMap = new HashMap<String,TypeView>();
        this.model = null;
        this.errors = new ArrayList<FormatError>();
        for (String typeName : grammarView.getTypeNames()) {
            TypeView typeView = grammarView.getTypeView(typeName);
            if (typeView != null && typeView.isEnabled()) {
                this.typeViewMap.put(typeName, typeView);
                this.errors.addAll(typeView.getErrors());
            }
        }
    }

    /**
     * @return the composite type graph from the view list, or {@code null}
     * if there are no active type views
     * @throws FormatException if any of the views has errors.
     * @throws IllegalArgumentException if the composition of types gives
     *         rise to typing cycles.
     */
    public TypeGraph toModel() throws FormatException, IllegalArgumentException {
        this.initialise();
        if (this.model == null) {
            throw new FormatException(this.getErrors());
        } else {
            if (this.typeViewMap.isEmpty()) {
                return null;
            } else {
                return this.model;
            }
        }
    }

    /**
     * @return the errors in the underlying type views.
     */
    public List<FormatError> getErrors() {
        this.initialise();
        return this.errors;
    }

    /** Returns a mapping from names to type graphs,
     * which together make up the combined type model. */
    public Map<String,TypeGraph> getTypeGraphMap() {
        this.initialise();
        return this.model == null ? null
                : Collections.unmodifiableMap(this.typeGraphMap);
    }

    /** Constructs the model and associated data structures from the view. */
    private void initialise() {
        // first test if there is something to be done
        if (this.errors.isEmpty() && this.model == null) {
            // There are no errors in each of the views, try to compose the
            // type graph.
            this.model = new TypeGraph("combined type");
            this.typeGraphMap = new TreeMap<String,TypeGraph>();
            Map<TypeNode,TypeNode> importNodes =
                new HashMap<TypeNode,TypeNode>();
            Map<TypeNode,TypeView> importViews =
                new HashMap<TypeNode,TypeView>();
            for (TypeView view : this.typeViewMap.values()) {
                try {
                    TypeGraph graph = view.toModel();
                    this.typeGraphMap.put(view.getName(), graph);
                    Map<TypeNode,TypeNode> map = this.model.add(graph);
                    for (TypeNode node : graph.getImports()) {
                        importNodes.put(node, map.get(node));
                        importViews.put(node, view);
                    }
                } catch (FormatException e) {
                    this.errors.addAll(e.getErrors());
                } catch (IllegalArgumentException e) {
                    this.errors.add(new FormatError(e.getMessage()));
                }
            }
            // test that there are no imported types left
            for (Map.Entry<TypeNode,TypeNode> importEntry : importNodes.entrySet()) {
                if (importEntry.getValue().isImported()) {
                    TypeNode origNode = importEntry.getKey();
                    TypeView origView = importViews.get(origNode);
                    this.errors.add(new FormatError(
                        "Error in type graph '%s': Unresolved type import '%s'",
                        origView.getName(), origNode.getType(), getInverse(
                            origView.getMap().nodeMap(), origNode),
                        origView.getAspectGraph()));
                }
            }
            if (this.errors.isEmpty()) {
                this.model.setFixed();
            } else {
                this.model = null;
            }
        }
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
}