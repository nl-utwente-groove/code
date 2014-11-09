/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.io.external;

import groove.grammar.QualName;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.GrammarModel;
import groove.grammar.model.Resource;
import groove.grammar.model.ResourceKind;
import groove.grammar.model.ResourceModel;
import groove.graph.GraphRole;
import groove.gui.SimulatorModel;
import groove.io.FileType;
import groove.io.conceptual.Design;
import groove.io.conceptual.Glossary;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.configuration.ConfigDialog;
import groove.io.conceptual.lang.Export;
import groove.io.conceptual.lang.groove.ConstraintToGroove;
import groove.io.conceptual.lang.groove.DesignToGroove;
import groove.io.conceptual.lang.groove.GlossaryToGroove;
import groove.io.conceptual.lang.groove.GrammarVisitor;
import groove.io.conceptual.lang.groove.GrooveExport;
import groove.io.conceptual.lang.groove.MetaToGroove;
import groove.io.conceptual.lang.groove.PreGraph;
import groove.util.Pair;
import groove.util.parse.FormatException;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Im- and exporter for conceptual model-based formats. */
public abstract class ConceptualPorter extends AbstractExporter implements Importer {
    /** Constructs a porter for a given format, with given instance format and type extensions. */
    protected ConceptualPorter(FileType typeFileType, FileType instanceFileType) {
        super(Kind.RESOURCE);
        register(ResourceKind.TYPE, typeFileType);
        register(ResourceKind.HOST, instanceFileType);
    }

    @Override
    public Set<Resource> doImport(Path file, FileType fileType, GrammarModel grammar)
        throws PortException {
        Set<Resource> result = Collections.emptySet();
        Pair<Glossary,Design> models = null;
        if (fileType == getFileType(ResourceKind.HOST)) {
            models = importDesign(file, grammar);
        } else if (fileType == getFileType(ResourceKind.TYPE)) {
            models = importGlossary(file, grammar);
        }
        if (models != null) {
            Config cfg = loadConfig(grammar);
            if (cfg != null) {
                result = loadModel(cfg, models.one(), models.two());
            }
        }
        return result;
    }

    /** Reads in type and instance models for an instance model import.
     * @throws PortException if an error occurred during importing */
    abstract protected Pair<Glossary,Design> importDesign(Path file, GrammarModel grammar)
        throws PortException;

    /** Reads in type and instance models for a type import.
     * @throws PortException if an error occurred during importing*/
    abstract protected Pair<Glossary,Design> importGlossary(Path file, GrammarModel grammar)
        throws PortException;

    @Override
    public void doExport(Exportable exportable, Path file, FileType fileType) throws PortException {
        String name = exportable.getName();
        String namespace = name;
        try {
            namespace = new QualName(name).parent();
        } catch (FormatException e) {
            throw new PortException(e);
        }

        ResourceModel<?> model = exportable.getModel();
        GrammarModel grammar = model.getGrammar();
        Config cfg = loadConfig(grammar);
        if (cfg == null) {
            return;
        }

        ResourceKind kind = model.getKind();
        Pair<Glossary,Design> outcome = null;
        switch (kind) {
        case HOST:
            assert fileType == getFileType(ResourceKind.HOST);
            outcome = constructModels(cfg, grammar, namespace, null, name);
            break;
        case RULE:
            throw new PortException("Rules cannot be exported in this format");
        case TYPE:
            assert fileType == getFileType(ResourceKind.TYPE);
            outcome = constructModels(cfg, grammar, namespace, name, null);
            break;
        default:
            assert false;
        }
        if (outcome == null) {
            return;
        }
        Glossary tm = outcome.one();
        Design im = outcome.two();
        if (tm == null) {
            throw new PortException("Unable to load glossary");
        }
        boolean isHost = kind == ResourceKind.HOST;
        if (isHost && im == null) {
            throw new PortException("Unable to load design");
        }
        getExport(file, isHost, tm, im).export();
    }

    /** Callback method obtaining an exportable object in the required format.
     * @param isHost flag indicating that we want to export an instance model
     */
    abstract protected Export getExport(Path file, boolean isHost, Glossary tm, Design im)
        throws PortException;

    /** Opens a configuration dialog and returns the resulting configuration object. */
    private Config loadConfig(GrammarModel grammar) {
        ConfigDialog dlg = new ConfigDialog(getSimulator());
        String cfg = dlg.getConfig();
        if (cfg != null) {
            return new Config(grammar, cfg);
        }
        return null;
    }

    /**
     * Extracts a conceptual type and/or optionally instance model from a grammar.
     * @param cfg the configuration under which the conceptual model is constructed
     * @param grammar the grammar from which the models are extracted
     * @param namespace namespace for the conceptual model
     * @param typeName name of the type model to be extracted; may be {@code null}
     * @param hostName name of the instance model to be extracted; may be {@code null}
     */
    private Pair<Glossary,Design> constructModels(Config cfg, GrammarModel grammar,
        String namespace, String typeName, String hostName) throws PortException {
        GrammarVisitor visitor = new GrammarVisitor(cfg, namespace, typeName, hostName);
        boolean success = visitor.doVisit(getParent(), grammar);
        return success ? Pair.newPair(visitor.getGlossary(), visitor.getDesign()) : null;
    }

    /**
     * Generates graphs for grammar.
     * @param cfg Configuration to use to generate graphs
     * @param glos TypeModel to insert. May be null
     * @param design InstanceModel to insert, may be null
     * @return Graphs to insert in grammar
     * @throws PortException if an error occurred during loading
     */
    private Set<Resource> loadModel(Config cfg, Glossary glos, Design design) throws PortException {
        Set<Resource> result = new HashSet<Resource>();
        SimulatorModel simulatorModel = getSimulator() == null ? null : getSimulator().getModel();
        GrooveExport export = new GrooveExport(cfg, simulatorModel, "");
        if (glos != null) {
            new GlossaryToGroove(glos, export).build();
            new ConstraintToGroove(glos, export).build();
            new MetaToGroove(glos, export).build();
        }

        if (design != null) {
            new DesignToGroove(design, export).build();
        }

        for (Map.Entry<GraphRole,HashMap<String,PreGraph>> entry : export.getGraphs().entrySet()) {
            for (PreGraph graph : entry.getValue().values()) {
                AspectGraph aspectGraph = graph.toAspectGraph();
                result.add(aspectGraph);
            }
        }

        return result;
    }
}
