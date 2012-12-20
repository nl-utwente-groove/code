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

import groove.graph.GraphRole;
import groove.gui.SimulatorModel;
import groove.io.conceptual.InstanceModel;
import groove.io.conceptual.TypeModel;
import groove.io.conceptual.configuration.Config;
import groove.io.conceptual.configuration.JaxFrontDialog;
import groove.io.conceptual.graph.AbsGraph;
import groove.io.conceptual.lang.ExportException;
import groove.io.conceptual.lang.ExportableResource;
import groove.io.conceptual.lang.ImportException;
import groove.io.conceptual.lang.groove.ConstraintToGroove;
import groove.io.conceptual.lang.groove.GrammarGraph;
import groove.io.conceptual.lang.groove.GrammarVisitor;
import groove.io.conceptual.lang.groove.GrooveResource;
import groove.io.conceptual.lang.groove.GrooveUtil;
import groove.io.conceptual.lang.groove.InstanceToGroove;
import groove.io.conceptual.lang.groove.MetaToGroove;
import groove.io.conceptual.lang.groove.TypeToGroove;
import groove.io.external.Exporter.Exportable;
import groove.trans.QualName;
import groove.trans.ResourceKind;
import groove.util.Pair;
import groove.view.FormatException;
import groove.view.GrammarModel;
import groove.view.ResourceModel;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Im- and exporter for conceptual model-based formats. */
public abstract class ConceptualPorter extends AbstractFormatExporter implements
        FormatImporter {
    /** 
     * Constructs a porter for a given format, with equal instance and
     * type extension.
     */
    protected ConceptualPorter(String formatName, String extension) {
        this(formatName, extension, extension);
    }

    /** Constructs a porter for a given format, with given instance format and type extensions. */
    protected ConceptualPorter(String formatName, String typeExtension,
            String instanceExtension) {
        this.instanceFormat =
            new Format(this, formatName + " instance model", instanceExtension);
        this.typeFormat =
            new Format(this, formatName + " type model", typeExtension);

        this.formats = Arrays.asList(this.instanceFormat, this.typeFormat);
    }

    /** Returns the instance format of this model porter. */
    protected Format getInstanceFormat() {
        return this.instanceFormat;
    }

    /** Returns the instance format of this model porter. */
    protected Format getTypeFormat() {
        return this.typeFormat;
    }

    @Override
    public List<Format> getSupportedFormats() {
        return this.formats;
    }

    @Override
    public Set<Resource> doImport(File file, Format format, GrammarModel grammar)
        throws PortException {
        Set<Resource> result = null;
        Pair<TypeModel,InstanceModel> models = null;
        try {
            if (format == getInstanceFormat()) {
                models = importInstanceModel(file, grammar);
            } else if (format == getTypeFormat()) {
                models = importTypeModel(file, grammar);
            }
        } catch (ImportException e) {
            throw new PortException(e);
        }
        if (models != null) {
            Config cfg = loadConfig(grammar);
            if (cfg != null) {
                result = loadModel(cfg, models.one(), models.two());
            }
        }
        return result;
    }

    /** Reads in type and instance models for an instance model import. */
    abstract protected Pair<TypeModel,InstanceModel> importInstanceModel(
            File file, GrammarModel grammar) throws ImportException;

    /** Reads in type and instance models for a type import. */
    abstract protected Pair<TypeModel,InstanceModel> importTypeModel(File file,
            GrammarModel grammar) throws ImportException;

    @Override
    public Set<Resource> doImport(String name, InputStream stream,
            Format format, GrammarModel grammar) throws PortException {
        //TODO: play nice with streams
        throw new UnsupportedOperationException();
    }

    @Override
    public void doExport(File file, Format format, Exportable exportable)
        throws PortException {
        String name = exportable.getName();
        String namespace = name;
        try {
            QualName qName = new QualName(name);
            if (qName.hasParent()) {
                namespace = qName.parent();
            }
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
        Pair<TypeModel,InstanceModel> outcome = null;
        switch (kind) {
        case HOST:
            assert format == getInstanceFormat();
            outcome = constructModels(cfg, grammar, namespace, null, name);
            break;
        case RULE:
            throw new PortException("Rules cannot be exported in this format");
        case TYPE:
            assert format == getTypeFormat();
            outcome = constructModels(cfg, grammar, namespace, name, null);
            break;
        default:
            assert false;
        }
        if (outcome == null) {
            return;
        }
        TypeModel tm = outcome.one();
        InstanceModel im = outcome.two();
        if (tm == null) {
            throw new PortException("Unable to load type model");
        }
        boolean isHost = kind == ResourceKind.HOST;
        if (isHost && im == null) {
            throw new PortException("Unable to load instance model");
        }
        try {
            getResource(file, isHost, tm, im).export();
        } catch (ExportException e) {
            throw new PortException(e);
        }
    }

    /** Callback method obtaining an exportable object in the required format.
     * @param isHost flag indicating that we want to export an instance model
     */
    abstract protected ExportableResource getResource(File file,
            boolean isHost, TypeModel tm, InstanceModel im)
        throws PortException;

    /** Opens a configuration dialog and returns the resulting configuration object. */
    private Config loadConfig(GrammarModel grammar) {
        JaxFrontDialog dlg = new JaxFrontDialog(getSimulator());
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
     * @param typeModel name of the type model to be extracted; may be {@code null}
     * @param instanceModel name of the instance model to be extracted; may be {@code null}
     */
    private Pair<TypeModel,InstanceModel> constructModels(Config cfg,
            GrammarModel grammar, String namespace, String typeModel,
            String instanceModel) throws PortException {
        GrammarVisitor visitor = new GrammarVisitor(cfg, namespace);
        visitor.setFixedType(typeModel);
        visitor.setFixedInstance(instanceModel);

        boolean success;
        try {
            success = visitor.doVisit(getParent(), grammar);
        } catch (ImportException e) {
            throw new PortException(e);
        }
        return success ? Pair.newPair(visitor.getTypeModel(),
            visitor.getInstanceModel()) : null;
    }

    /**
     * Generates graphs for grammar.
     * @param cfg Configuration to use to generate graphs
     * @param tm TypeModel to insert. May be null
     * @param im InstanceModel to insert, may be null
     * @return Graphs to insert in grammar
     * @throws PortException if an error occurred during loading
     */
    private Set<Resource> loadModel(Config cfg, TypeModel tm, InstanceModel im)
        throws PortException {
        Set<Resource> result = new HashSet<Resource>();
        SimulatorModel simulatorModel =
            getSimulator() == null ? null : getSimulator().getModel();
        GrooveResource grooveResource =
            new GrooveResource(cfg, simulatorModel, "");
        if (tm != null) {
            TypeToGroove ttg = new TypeToGroove(grooveResource);
            ttg.addTypeModel(tm);
            ConstraintToGroove ctg = new ConstraintToGroove(grooveResource);
            ctg.addTypeModel(tm);
            MetaToGroove mtg = new MetaToGroove(grooveResource);
            mtg.addTypeModel(tm);
        }

        if (im != null) {
            InstanceToGroove itg = new InstanceToGroove(grooveResource);
            itg.addInstanceModel(im);
        }

        for (Map.Entry<GraphRole,HashMap<String,GrammarGraph>> entry : grooveResource.getGraphs().entrySet()) {
            GraphRole role = entry.getKey();
            ResourceKind kind = ResourceKind.toResource(role);
            for (GrammarGraph graph : entry.getValue().values()) {
                AbsGraph absGraph = graph.getGraph();
                String name = GrooveUtil.getSafeResource(graph.getGraphName());
                AspectGraph aspectGraph = absGraph.toAspectGraph(name, role);
                Resource resource = new Resource(kind, name, aspectGraph);
                result.add(resource);
            }
        }

        return result;
    }

    private final Format instanceFormat;
    private final Format typeFormat;
    private final List<Format> formats;
}
