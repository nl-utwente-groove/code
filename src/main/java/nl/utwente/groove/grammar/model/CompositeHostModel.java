package nl.utwente.groove.grammar.model;

import static nl.utwente.groove.grammar.model.ResourceKind.HOST;
import static nl.utwente.groove.grammar.model.ResourceKind.PROPERTIES;
import static nl.utwente.groove.grammar.model.ResourceKind.RULE;
import static nl.utwente.groove.grammar.model.ResourceKind.TYPE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.util.Factory;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/** Class compute the composed start graph. */
@NonNullByDefault
public class CompositeHostModel extends ResourceModel<HostGraph> {
    /**
     * Constructs a composite type model
     * @param grammar the underlying graph grammar
     */
    CompositeHostModel(GrammarModel grammar) {
        this(grammar, null);
    }

    /**
     * Constructs a composite type model from a single aspect graph.
     * @param grammar the underlying graph grammar
     * @param source the explicit source aspect graph; if {@code null}, the source
     * is implicit and derived from the active host graphs of the grammar.
     */
    CompositeHostModel(GrammarModel grammar, @Nullable AspectGraph source) {
        super(grammar, HOST);
        addDependencies(TYPE, PROPERTIES);
        this.implicit = source == null;
        this.hostModels = this.implicit
            ? null // to be initialised later
            : Collections.<HostModel>singletonList(new HostModel(grammar, source));
    }

    @Override
    public @NonNull GrammarModel getGrammar() {
        var result = super.getGrammar();
        assert result != null;
        return result;
    }

    /** Indicates if this composite model is implicit, i.e., derived from the active host graphs
     * in the grammar.
     */
    public boolean isImplicit() {
        return this.implicit;
    }

    private final boolean implicit;

    /** Indicates if there are multiple active host graphs underlying this composite one. */
    public boolean isMultiple() {
        return getHostModels().size() > 1;
    }

    @Override
    public @Nullable AspectGraph getSource() {
        return this.source.get();
    }

    /** The (lazily created) source aspect graph. */
    private final Factory<@Nullable AspectGraph> source = Factory.lazy(() -> {
        AspectGraph result = null;
        var hostModels = getHostModels();
        if (hostModels.size() == 1) {
            result = hostModels.getFirst().getSource();
        } else if (hostModels.size() > 1) {
            result = AspectGraph.mergeGraphs(getGrammar().getActiveGraphs(HOST));
        }
        return result;
    });

    /** Returns the set of type labels in the combined source graphs. */
    public Set<TypeLabel> getTypeLabels() {
        return this.typeLabels.get();
    }

    /**
     * Returns a mapping from the nodes in the model source to the corresponding
     * nodes in the resource that is constructed from it.
     * This method should only be called if the model contains no errors.
     * @return the mapping from source graph to resource elements
     */
    public HostModelMap getMap() {
        var model = getCombinedModel();
        assert model != null;
        return model.getMap();
    }

    private final Factory<Set<TypeLabel>> typeLabels = Factory.lazy(() -> {
        var result = new HashSet<TypeLabel>();
        for (var model : getHostModels()) {
            result.addAll(model.getTypeLabels());
        }
        return result;
    });

    @Override
    public String getName() {
        var source = getSource();
        return source == null
            ? ""
            : source.getName();
    }

    @Override
    boolean isShouldRebuild() {
        boolean result = super.isShouldRebuild();
        if (getGrammar().getTypeGraph().isImplicit()) {
            // this is an implicit type graph; look also at the rules
            // this is not a dependency by default, to avoid cyclic dependencies between TYPE and HOST
            result |= isStale(RULE);
        }
        return result;
    }

    @Override
    void notifyWillRebuild() {
        super.notifyWillRebuild();
        if (isImplicit()) {
            this.hostModels = null;
            this.combinedModel.reset();
            this.source.reset();
            this.typeLabels.reset();
        }
    }

    @Override
    HostGraph compute() throws FormatException {
        HostGraph result = null;
        var hostModels = getHostModels();
        FormatErrorSet errors = new FormatErrorSet();
        for (var model : hostModels) {
            for (var error : model.getErrors()) {
                errors.add("Error in graph '%s': %s", model.getName(), error, model.getSource());
            }
        }
        errors.throwException();
        var combinedModel = getCombinedModel();
        if (combinedModel == null) {
            throw new FormatException("No active start graph");
        } else {
            try {
                result = combinedModel.toHost();
            } catch (FormatException exc) {
                for (var error : exc.getErrors()) {
                    errors.add("Error in composite host graph: %s", error);
                }
                errors.throwException();
            }
        }
        assert result != null;
        return result;
    }

    /** Returns the combined host model.
     * This is either {@code null} of there is no active host model, or the singular host model, or a new
     * host model obtained from the merged sources of the active host models, if there are multiple.
     */
    private @Nullable HostModel getCombinedModel() {
        return this.combinedModel.get();
    }

    /** The combined host model.
     * @see #getCombinedModel()
     */
    private final Factory<@Nullable HostModel> combinedModel = Factory.lazy(() -> {
        HostModel result = null;
        var hostModels = getHostModels();
        if (hostModels.size() == 1) {
            result = hostModels.getFirst();
        } else if (hostModels.size() > 1) {
            result = new HostModel(getGrammar(), getSource());
        }
        return result;
    });

    /**
     * Returns the list of active host models.
     */
    private List<HostModel> getHostModels() {
        var result = this.hostModels;
        if (result == null) {
            result = new ArrayList<>();
            for (var name : getGrammar().getActiveNames(HOST)) {
                var model = getGrammar().getResource(HOST, name);
                result.add((HostModel) model);
            }
        }
        return result;
    }

    private @Nullable List<HostModel> hostModels;

    /** Fixed name for the composite host model. */
    static public final String NAME = "composite-host";
}