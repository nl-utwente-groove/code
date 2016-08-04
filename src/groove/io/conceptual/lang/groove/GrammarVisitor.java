package groove.io.conceptual.lang.groove;

import java.awt.Frame;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import groove.grammar.ModuleName;
import groove.grammar.QualName;
import groove.grammar.host.HostGraph;
import groove.grammar.model.CompositeTypeModel;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.HostModel;
import groove.grammar.model.NamedResourceModel;
import groove.grammar.model.ResourceKind;
import groove.grammar.model.RuleModel;
import groove.grammar.type.TypeGraph;
import groove.io.conceptual.InstanceModel;
import groove.io.conceptual.Timer;
import groove.io.conceptual.TypeModel;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.lang.ImportException;
import groove.util.Pair;
import groove.util.parse.FormatException;

@SuppressWarnings("javadoc")
public class GrammarVisitor {
    private GraphNodeTypes m_types;
    private Config m_cfg;
    private ModuleName m_namespace;

    private Map<QualName,groove.grammar.model.TypeModel> m_typeMap;
    private Map<QualName,HostModel> m_hostMap;
    private Map<QualName,RuleModel> m_ruleMap;
    private Map<QualName,groove.grammar.model.TypeModel> m_metaMap;

    private QualName m_fixedType;
    private QualName m_fixedMeta;
    private QualName m_fixedInstance;
    private boolean useMeta;

    private TypeModel m_typeModel;
    private InstanceModel m_instanceModel;

    public GrammarVisitor(Config cfg, ModuleName namespace) {
        this.m_cfg = cfg;
        this.m_namespace = namespace;

        this.m_types = new GraphNodeTypes();
        this.useMeta = this.m_cfg.getConfig()
            .getTypeModel()
            .isMetaSchema();
    }

    public void setFixedType(QualName fixedType) {
        this.m_fixedType = fixedType;
    }

    public void setFixedMeta(QualName fixedMeta) {
        this.m_fixedMeta = fixedMeta;
    }

    // As an exception, if string is empty (not null), then instance will be ignored
    public void setFixedInstance(QualName fixedInstance) {
        this.m_fixedInstance = fixedInstance;
    }

    public boolean isAmbiguous() {
        return (this.m_typeMap.size() > 1 || this.m_metaMap.size() > 1
            || this.m_hostMap.size() > 1);
    }

    public boolean isParseable() {
        if (this.m_cfg.getConfig()
            .getTypeModel()
            .isMetaSchema()) {
            return (this.m_typeMap.size() == 1 || this.m_metaMap.size() == 1);
        }

        return (this.m_typeMap.size() == 1);
    }

    private boolean doDialog(Frame parent) {
        GrammarDialog dlg = new GrammarDialog(parent);

        dlg.setTypeModels(this.m_typeMap.keySet());
        if (this.useMeta) {
            dlg.setMetaModels(this.m_metaMap.keySet());
        }
        dlg.setInstanceModels(this.m_hostMap.keySet(), this.m_fixedInstance != null);

        if (!dlg.doDialog()) {
            return false;
        }

        // When finished, selection should have been made for each graph type
        if (dlg.getTypeModel() != null) {
            filterMap(this.m_typeMap, dlg.getTypeModel());
        } else {
            return false;
        }

        if (this.m_cfg.getConfig()
            .getTypeModel()
            .isMetaSchema()) {
            if (dlg.getMetaModel() != null) {
                filterMap(this.m_metaMap, dlg.getTypeModel());
            } else {
                return false;
            }
        }

        if (dlg.getInstanceModel() != null) {
            filterMap(this.m_hostMap, dlg.getInstanceModel());
        } else {
            return false;
        }

        return true;
    }

    private void browseGraphs(ModuleName namespace) {
        filterMap(this.m_typeMap, namespace);
        filterMap(this.m_hostMap, namespace);
        filterMap(this.m_ruleMap, namespace);

        if (this.useMeta) {
            Iterator<Entry<QualName,groove.grammar.model.TypeModel>> it = this.m_typeMap.entrySet()
                .iterator();
            while (it.hasNext()) {
                Entry<QualName,groove.grammar.model.TypeModel> entry = it.next();
                if (entry.getKey()
                    .last()
                    .contains("meta")) {
                    it.remove();
                    this.m_metaMap.put(entry.getKey(), entry.getValue());
                    continue;
                }
            }
        }

        if (this.m_fixedType != null && this.m_typeMap.containsKey(this.m_fixedType)) {
            groove.grammar.model.TypeModel keepModel = this.m_typeMap.get(this.m_fixedType);
            this.m_typeMap.clear();
            this.m_typeMap.put(this.m_fixedType, keepModel);
        }

        if (this.m_fixedMeta != null && this.m_metaMap.containsKey(this.m_fixedMeta)) {
            groove.grammar.model.TypeModel keepModel = this.m_metaMap.get(this.m_fixedMeta);
            this.m_metaMap.clear();
            this.m_metaMap.put(this.m_fixedMeta, keepModel);
        }

        if (this.m_fixedInstance != null && this.m_hostMap.containsKey(this.m_fixedInstance)) {
            HostModel keepModel = this.m_hostMap.get(this.m_fixedInstance);
            this.m_hostMap.clear();
            this.m_hostMap.put(this.m_fixedInstance, keepModel);
        } else if (this.m_fixedInstance == null) {
            this.m_hostMap.clear();
        }
    }

    /**
     * Remove all elements not starting with namespace in their name, are disabled or have errors
     * @param map Map to filter elements from
     * @param namespace Namespace of elements to keep
     */
    // Removed checks for disabled and error, graphsd are checked an enabled during export process where applicable
    private <M extends NamedResourceModel<?>> void filterMap(Map<QualName,M> map,
        ModuleName namespace) {
        Iterator<Entry<QualName,M>> it = map.entrySet()
            .iterator();
        while (it.hasNext()) {
            Entry<QualName,M> entry = it.next();
            if (!namespace.contains(entry.getKey())) {
                it.remove();
                continue;
            }
            if (!entry.getValue()
                .isEnabled()) {
                //it.remove();
                continue;
            }
            if (entry.getValue()
                .hasErrors()) {
                //it.remove();
                continue;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public boolean doVisit(Frame parent, GrammarModel grammar) throws ImportException {
        this.m_typeMap = new HashMap<>((Map<QualName,groove.grammar.model.TypeModel>) grammar
            .getResourceMap(ResourceKind.TYPE));
        this.m_hostMap =
            new HashMap<>((Map<QualName,HostModel>) grammar.getResourceMap(ResourceKind.HOST));
        this.m_ruleMap =
            new HashMap<>((Map<QualName,RuleModel>) grammar.getResourceMap(ResourceKind.RULE));
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
        if (this.m_cfg.getConfig()
            .getTypeModel()
            .isMetaSchema()) {
            try {
                TypeGraph metaGraph = this.m_metaMap.values()
                    .iterator()
                    .next()
                    .toResource();

                Timer.stop(timer);
                setMetaGraph(metaGraph);
                Timer.cont(timer);
            } catch (FormatException e) {
                throw new ImportException(e);
            }
        }

        Set<QualName> typeGraphSet = new HashSet<>(this.m_typeMap.keySet());
        Set<QualName> hostGraphSet = new HashSet<>(this.m_hostMap.keySet());
        Pair<TypeGraph,HostGraph> graphs =
            computeCompositeGraphs(grammar, typeGraphSet, hostGraphSet);

        // Parse type and rule graphs
        //No need to enable rule graphs (but ignore the disabled ones) as the model is still accessible

        Timer.stop(timer);
        setTypeGraph(graphs.one());
        setRuleGraphs(this.m_ruleMap.values());
        this.m_typeModel.resolve();
        Timer.cont(timer);

        if (!hostGraphSet.isEmpty()) {
            Timer.stop(timer);
            setInstanceGraph(graphs.two());
            Timer.cont(timer);
        }

        Timer.stop(timer);

        return true;
    }

    private void setMetaGraph(TypeGraph typeGraph) {
        // Adds meta model information to m_types
        new GrooveToMeta(typeGraph, this.m_types, this.m_cfg);
    }

    private void setTypeGraph(TypeGraph typeGraph) throws ImportException {
        GrooveToType gtt = new GrooveToType(typeGraph, this.m_types, this.m_cfg);
        this.m_typeModel = gtt.getTypeModel();
    }

    private void setRuleGraphs(Collection<groove.grammar.model.RuleModel> ruleModels) {
        /*GrooveToConstraint gtc = */new GrooveToConstraint(ruleModels, this.m_types, this.m_cfg,
            this.m_typeModel);
    }

    private void setInstanceGraph(HostGraph hostGraph) throws ImportException {
        GrooveToInstance gti =
            new GrooveToInstance(hostGraph, this.m_types, this.m_cfg, this.m_typeModel);
        this.m_instanceModel = gti.getInstanceModel();
    }

    public TypeModel getTypeModel() {
        return this.m_typeModel;
    }

    public InstanceModel getInstanceModel() {
        return this.m_instanceModel;
    }

    private Pair<TypeGraph,HostGraph> computeCompositeGraphs(GrammarModel grammar,
        Set<QualName> typeModels, Set<QualName> hostModels) throws ImportException {
        Set<QualName> localTypeNames = grammar.getLocalActiveNames(ResourceKind.TYPE);
        if (localTypeNames == null) {
            localTypeNames = grammar.getActiveNames(ResourceKind.TYPE);
        }

        Set<QualName> localHostNames = grammar.getLocalActiveNames(ResourceKind.HOST);
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

            result = new Pair<>(tg, hg);
        } catch (FormatException e) {
            throw new ImportException(e);
        } finally {
            grammar.setLocalActiveNames(ResourceKind.HOST, localHostNames);
            grammar.setLocalActiveNames(ResourceKind.TYPE, localTypeNames);
        }

        return result;
    }
}
