package groove.io.conceptual.lang.groove;

import groove.grammar.host.HostGraph;
import groove.grammar.model.CompositeTypeModel;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.HostModel;
import groove.grammar.model.ResourceKind;
import groove.grammar.model.ResourceModel;
import groove.grammar.model.RuleModel;
import groove.grammar.model.TypeModel;
import groove.grammar.type.TypeGraph;
import groove.io.conceptual.Design;
import groove.io.conceptual.Glossary;
import groove.io.conceptual.Timer;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.lang.ImportException;
import groove.util.Pair;
import groove.util.parse.FormatException;

import java.awt.Frame;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/** Class combining the construction of a glossary and deisgn from a given grammar. */
public class GrammarVisitor {
    private Map<String,groove.grammar.model.TypeModel> m_typeMap;
    private Map<String,HostModel> m_hostMap;
    private Map<String,RuleModel> m_ruleMap;
    private Map<String,groove.grammar.model.TypeModel> m_metaMap;

    /** Constructs an instance from a given configuration and name space. */
    public GrammarVisitor(Config cfg, String namespace, String typeName, String hostName) {
        this.m_cfg = cfg;
        this.m_namespace = namespace;

        this.m_types = new GraphNodeTypes();
        this.m_useMeta = this.m_cfg.getTypeModel().isMetaSchema();
        this.m_fixedType = typeName;
        this.m_fixedInstance = hostName;
    }

    private final GraphNodeTypes m_types;
    private final Config m_cfg;
    private final String m_namespace;
    private final boolean m_useMeta;
    private final String m_fixedType;
    private final String m_fixedInstance;

    /** Tests if there is more than one type, meta-schema, or host present. */
    private boolean isAmbiguous() {
        return (this.m_typeMap.size() > 1 || this.m_metaMap.size() > 1 || this.m_hostMap.size() > 1);
    }

    /** Tests if there is enough type information present to convert the graphs. */
    private boolean isParseable() {
        if (this.m_useMeta) {
            return this.m_typeMap.size() == 1 || this.m_metaMap.size() == 1;
        } else {
            return this.m_typeMap.size() == 1;
        }
    }

    private boolean doDialog(Frame parent) {
        GrammarDialog dlg = new GrammarDialog(parent);

        dlg.setTypeGraphs(this.m_typeMap.keySet());
        if (this.m_useMeta) {
            dlg.setMetaGraphs(this.m_metaMap.keySet());
        }
        dlg.setHostGraphs(this.m_hostMap.keySet(), this.m_fixedInstance != null);

        if (!dlg.doDialog()) {
            return false;
        }

        // When finished, selection should have been made for each graph type
        if (dlg.getTypeGraph() != null && !dlg.getTypeGraph().equals("")) {
            filterMap(this.m_typeMap, dlg.getTypeGraph());
        } else {
            return false;
        }

        if (this.m_cfg.getXMLConfig().getTypeModel().isMetaSchema()) {
            if (dlg.getMetaGraph() != null && !dlg.getMetaGraph().equals("")) {
                filterMap(this.m_metaMap, dlg.getTypeGraph());
            } else {
                return false;
            }
        }

        if (dlg.getHostGraph() != null) {
            if (!dlg.getHostGraph().equals("")) {
                filterMap(this.m_hostMap, dlg.getHostGraph());
            } else {
                this.m_hostMap.clear();
            }
        } else {
            return false;
        }

        return true;
    }

    /**
     * Remove all elements not starting with namespace in their name
     * @param map Map to filter elements from
     * @param namespace Namespace of elements to keep
     */
    private <M extends ResourceModel<?>> void filterMap(Map<String,M> map, String namespace) {
        Iterator<Entry<String,M>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String,M> entry = it.next();
            if (!entry.getKey().startsWith(namespace)) {
                it.remove();
            }
        }
    }

    /**
     * Applies this visitor to a given grammar.
     * The result can be retrieved afterwards by {@link #getGlossary()} and {@link #getDesign()}.
     */
    @SuppressWarnings("unchecked")
    public boolean doVisit(Frame parent, GrammarModel grammar) throws ImportException {
        this.m_typeMap =
            new HashMap<>((Map<String,TypeModel>) grammar.getResourceMap(ResourceKind.TYPE));
        this.m_hostMap =
            new HashMap<>((Map<String,HostModel>) grammar.getResourceMap(ResourceKind.HOST));
        this.m_ruleMap =
            new HashMap<>((Map<String,RuleModel>) grammar.getResourceMap(ResourceKind.RULE));
        this.m_metaMap = new HashMap<>();

        browseGraphs(this.m_namespace);

        if (isAmbiguous()) {
            if (parent == null || !doDialog(parent)) {
                // Nothing to do here
                return false;
            }
        }

        if (!isParseable()) {
            throw new ImportException(
                "Unable to translate graphs, some type information is missing");
        }

        // Timer is stopped and continued when actually parsing the graphs
        int timer = Timer.start("Load GROOVE grammar");

        // Parse meta graph
        if (this.m_useMeta) {
            try {
                TypeGraph metaGraph = this.m_metaMap.values().iterator().next().toResource();

                Timer.stop(timer);
                setMetaGraph(metaGraph);
                Timer.cont(timer);
            } catch (FormatException e) {
                throw new ImportException(e);
            }
        }

        Set<String> typeGraphSet = new HashSet<>(this.m_typeMap.keySet());
        Set<String> hostGraphSet = new HashSet<>(this.m_hostMap.keySet());
        Pair<TypeGraph,HostGraph> graphs =
            computeCompositeGraphs(grammar, typeGraphSet, hostGraphSet);

        // Parse type and rule graphs
        //No need to enable rule graphs (but ignore the disabled ones) as the model is still accessible

        Timer.stop(timer);
        setTypeGraph(graphs.one());
        setRuleGraphs(this.m_ruleMap.values());
        this.m_glos.resolve();
        Timer.cont(timer);

        if (!hostGraphSet.isEmpty()) {
            Timer.stop(timer);
            setDesign(graphs.two());
            Timer.cont(timer);
        }

        Timer.stop(timer);

        return true;
    }

    private void browseGraphs(String namespace) {
        filterMap(this.m_typeMap, namespace);
        filterMap(this.m_hostMap, namespace);
        filterMap(this.m_ruleMap, namespace);

        if (this.m_useMeta) {
            Iterator<Entry<String,TypeModel>> it = this.m_typeMap.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String,TypeModel> entry = it.next();
                if (entry.getKey().contains("meta")) {
                    it.remove();
                    this.m_metaMap.put(entry.getKey(), entry.getValue());
                }
            }
        }

        if (this.m_fixedType != null && this.m_typeMap.containsKey(this.m_fixedType)) {
            TypeModel keepModel = this.m_typeMap.get(this.m_fixedType);
            this.m_typeMap.clear();
            this.m_typeMap.put(this.m_fixedType, keepModel);
        }

        if (this.m_fixedInstance != null && this.m_hostMap.containsKey(this.m_fixedInstance)) {
            HostModel keepModel = this.m_hostMap.get(this.m_fixedInstance);
            this.m_hostMap.clear();
            this.m_hostMap.put(this.m_fixedInstance, keepModel);
        } else if ("".equals(this.m_fixedInstance)) {
            this.m_hostMap.clear();
        }
    }

    private void setRuleGraphs(Collection<groove.grammar.model.RuleModel> ruleModels) {
        new GrooveToConstraint(ruleModels, this.m_types, this.m_cfg, this.m_glos);
    }

    private void setMetaGraph(TypeGraph typeGraph) {
        // Adds meta model information to m_types
        new GrooveToMeta(typeGraph, this.m_types, this.m_cfg);
    }

    private void setTypeGraph(TypeGraph typeGraph) {
        GrooveToGlossary gtt = new GrooveToGlossary(this.m_types, this.m_cfg);
        this.m_glos = gtt.buildGlossary(typeGraph);
    }

    /** Returns the type model constructed by this visitor. */
    public Glossary getGlossary() {
        return this.m_glos;
    }

    private Glossary m_glos;

    private void setDesign(HostGraph hostGraph) throws ImportException {
        GrooveToDesign gti =
            new GrooveToDesign(hostGraph, this.m_types, this.m_cfg, this.m_glos).build();
        this.m_design = gti.getDesign();
    }

    /** Returns the instance model constructed by this visitor. */
    public Design getDesign() {
        return this.m_design;
    }

    private Design m_design;

    private Pair<TypeGraph,HostGraph> computeCompositeGraphs(GrammarModel grammar,
        Set<String> typeModels, Set<String> hostModels) throws ImportException {
        Set<String> localTypeNames = grammar.getLocalActiveNames(ResourceKind.TYPE);
        if (localTypeNames == null) {
            localTypeNames = grammar.getActiveNames(ResourceKind.TYPE);
        }

        Set<String> localHostNames = grammar.getLocalActiveNames(ResourceKind.HOST);
        if (localHostNames == null) {
            localHostNames = grammar.getActiveNames(ResourceKind.HOST);
        }

        grammar.setLocalActiveNames(ResourceKind.TYPE, typeModels);
        grammar.setLocalActiveNames(ResourceKind.HOST, hostModels);

        Pair<TypeGraph,HostGraph> result;

        try {
            CompositeTypeModel tm = grammar.getTypeModel();
            //hm might be null if no start graph selected
            HostModel hm = grammar.getStartGraphModel();

            TypeGraph tg = tm.getTypeGraph();
            HostGraph hg = (hm != null) ? hm.toHost() : null;

            result = new Pair<TypeGraph,HostGraph>(tg, hg);
        } catch (FormatException e) {
            throw new ImportException(e);
        } finally {
            grammar.setLocalActiveNames(ResourceKind.HOST, localHostNames);
            grammar.setLocalActiveNames(ResourceKind.TYPE, localTypeNames);
        }

        return result;
    }
}
