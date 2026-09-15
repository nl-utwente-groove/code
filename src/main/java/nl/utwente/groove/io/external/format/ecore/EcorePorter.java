/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.io.external.format.ecore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.GraphConverter;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.io.external.AbstractExporter;
import nl.utwente.groove.io.external.Exportable;
import nl.utwente.groove.io.external.Imported;
import nl.utwente.groove.io.external.Importer;
import nl.utwente.groove.io.external.PortException;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.io.FileType;

/**
 * Importer and exporter for Ecore meta-models ({@code .ecore}) and XMI instance
 * models ({@code .xmi}).
 * <p>
 * An imported meta-model yields a type graph; an imported instance model yields
 * both a host graph and the type graph of its meta-model, since the latter is
 * needed to make sense of the former (and the encoding is deterministic, so
 * importing the meta-model separately gives the same result). The meta-model of
 * an instance model is resolved through EMF, from the packages already
 * registered in the resource set — which, for a file-based import, are those
 * declared by the {@code .ecore} files next to the instance file.
 * <p>
 * Next to the graphs, an import returns the grammar's {@code ecore} settings
 * resource with the round-trip records of the metamodel merged into it: the
 * package namespaces, the classifier kinds, the label correspondence and the
 * parts of the feature declarations that the type graph does not determine.
 * The export reads them back from there. The resource is returned rather than
 * written, since importers are side-effect free; it is flagged as an update,
 * so that the action storing it does not ask about overwriting.
 * <p>
 * On the export side, a type graph resource is written as an {@code .ecore}
 * file and a host graph resource as an {@code .xmi} file. The latter needs the
 * meta-model as well, which is taken from the active type graphs of the
 * grammar the host graph belongs to; the {@code .ecore} file has to be exported
 * next to it for the result to be importable again.
 * @author Arend Rensink
 */
@NonNullByDefault
public class EcorePorter extends AbstractExporter implements Importer {
    private EcorePorter() {
        super(ExportKind.RESOURCE);
        register(FileType.ECORE);
        register(FileType.XMI);
    }

    @Override
    public Set<Imported> doImport(File file, FileType fileType,
                                  GrammarModel grammar) throws PortException {
        Loader loader = new Loader(EcoreMapping.of(grammar), grammar, file.getName());
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent != null) {
            loader.registerMetamodels(parent, file.getAbsoluteFile());
        }
        QualName name = QualName.name(FileType.getPureName(file));
        Resource resource = loader.load(URI.createFileURI(file.getAbsolutePath()), null, fileType);
        return loader.convert(name, resource, fileType);
    }

    @Override
    public Set<Imported> doImport(QualName name, InputStream stream, FileType fileType,
                                  GrammarModel grammar) throws PortException {
        Loader loader
            = new Loader(EcoreMapping.of(grammar), grammar, name + fileType.getExtension());
        Resource resource = loader
            .load(URI.createURI(name + fileType.getExtension()), stream, fileType);
        return loader.convert(name, resource, fileType);
    }

    /** This exporter handles type graph and host graph resources,
     * which it writes as {@code .ecore} respectively {@code .xmi} files.
     */
    @Override
    public boolean exports(Exportable exportable) {
        return !getFileTypes(exportable).isEmpty();
    }

    @Override
    public Set<FileType> getFileTypes(Exportable exportable) {
        var fileType = getFileType(exportable.getResourceKind());
        return fileType == null
            ? Collections.emptySet()
            : Collections.singleton(fileType);
    }

    /** Returns the file type in which a resource of a given kind is exported, if any. */
    private @Nullable FileType getFileType(@Nullable ResourceKind kind) {
        if (kind == null) {
            return null;
        }
        return switch (kind) {
        case TYPE -> FileType.ECORE;
        case HOST -> FileType.XMI;
        default -> null;
        };
    }

    @Override
    public void doExport(Exportable exportable, File file, FileType fileType) throws PortException {
        var model = exportable.resourceModel();
        var kind = exportable.getResourceKind();
        if (model == null || getFileType(kind) != fileType) {
            throw new PortException("'%s' cannot be exported as %s", exportable.qualName(),
                fileType.getExtension());
        }
        GrammarModel grammar = model.getGrammar();
        GraphsToEcore converter = new GraphsToEcore(EcoreMapping.of(grammar));
        List<? extends EObject> contents;
        if (kind == ResourceKind.TYPE) {
            contents = converter.addTypeGraph(GraphConverter.toAspect(exportable.graph()));
        } else {
            for (var typeGraph : getTypeGraphs(grammar)) {
                converter.addTypeGraph(typeGraph);
            }
            contents = converter.toObjects(GraphConverter.toAspect(exportable.graph()));
        }
        if (!converter.getErrors().isEmpty()) {
            StringBuilder message = new StringBuilder();
            message.append("Cannot export '");
            message.append(exportable.qualName());
            message.append("':");
            for (var error : converter.getErrors()) {
                message.append(' ');
                message.append(error.toString());
            }
            throw new PortException(message.toString());
        }
        save(file, contents, converter.getIdentifiers());
    }

    /** Returns the active type graphs of a grammar, in name order.
     * @throws PortException if the grammar (and hence the meta-model) is unavailable
     */
    private List<AspectGraph> getTypeGraphs(@Nullable GrammarModel grammar) throws PortException {
        if (grammar == null) {
            throw new PortException(
                "Cannot export an instance model without the grammar holding its meta-model");
        }
        List<AspectGraph> result = new ArrayList<>();
        for (var name : grammar.getActiveNames(ResourceKind.TYPE)) {
            AspectGraph graph = grammar.getModelGraph(ResourceKind.TYPE, name);
            if (graph != null) {
                result.add(graph);
            }
        }
        if (result.isEmpty()) {
            throw new PortException(
                "Cannot export an instance model without an enabled Ecore type graph");
        }
        return result;
    }

    /** Writes a list of EMF objects to a file, with given optional identifiers. */
    private void save(File file, List<? extends EObject> contents,
                      Map<EObject,String> identifiers) throws PortException {
        ResourceSet resourceSet = new ResourceSetImpl();
        var factories = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
        factories.put(FileType.ECORE.getExtensionName(), new EcoreResourceFactoryImpl());
        factories.put("*", new XMIResourceFactoryImpl());
        Resource resource = resourceSet.createResource(URI.createFileURI(file.getAbsolutePath()));
        resource.getContents().addAll(contents);
        if (resource instanceof XMLResource xmlResource) {
            identifiers.forEach(xmlResource::setID);
        }
        try {
            resource.save(Collections.singletonMap(XMLResource.OPTION_ENCODING, "UTF-8"));
        } catch (IOException exc) {
            throw new PortException(exc);
        }
    }

    /** Returns the singleton instance of this class. */
    public static EcorePorter instance() {
        return INSTANCE;
    }

    private static final EcorePorter INSTANCE = new EcorePorter();

    /** Helper class collecting the EMF state of a single import action. */
    private static class Loader {
        Loader(EcoreMapping options, GrammarModel grammar, String source) {
            this.options = options;
            this.grammar = grammar;
            this.source = source;
            this.resourceSet = new ResourceSetImpl();
            var factories = this.resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
            factories.put(FileType.ECORE.getExtensionName(), new EcoreResourceFactoryImpl());
            factories.put("*", new XMIResourceFactoryImpl());
        }

        private final EcoreMapping options;
        /** The grammar the import is destined for, read-only. */
        private final GrammarModel grammar;
        /** The name of the file being imported, for the recording comment. */
        private final String source;
        private final ResourceSet resourceSet;
        /** Mapping from root packages to the name of the file they were loaded from. */
        private final Map<EPackage,@Nullable String> sources = new LinkedHashMap<>();

        /**
         * Registers the packages declared by the {@code .ecore} files in a given
         * directory, so that instance models can be resolved against them.
         * @param directory the directory to be scanned
         * @param exclude the file being imported, which is loaded separately
         */
        void registerMetamodels(File directory, File exclude) {
            File[] files = directory.listFiles();
            if (files == null) {
                return;
            }
            // sort the files, so that the outcome does not depend on the file system
            List<File> ecoreFiles = new ArrayList<>();
            for (var file : files) {
                if (FileType.ECORE.hasExtension(file) && !file.equals(exclude)) {
                    ecoreFiles.add(file);
                }
            }
            Collections.sort(ecoreFiles);
            for (var file : ecoreFiles) {
                try {
                    register(load(URI.createFileURI(file.getAbsolutePath()), null, FileType.ECORE),
                             FileType.getPureName(file));
                } catch (PortException exc) {
                    // an unreadable sibling meta-model is not fatal for this import
                }
            }
        }

        /** Loads a resource from a given URI, optionally from a stream. */
        Resource load(URI uri, @Nullable InputStream stream, FileType fileType) throws PortException {
            Resource result = this.resourceSet.createResource(uri);
            try {
                if (stream == null) {
                    result.load(null);
                } else {
                    result.load(stream, null);
                }
            } catch (IOException exc) {
                throw new PortException(exc);
            }
            if (!result.getErrors().isEmpty()) {
                StringBuilder message = new StringBuilder();
                message.append("Cannot read '");
                message.append(uri.lastSegment());
                message.append("':");
                for (var error : result.getErrors()) {
                    message.append(' ');
                    message.append(error.getMessage());
                }
                if (fileType == FileType.XMI) {
                    message
                        .append(". Place the corresponding .ecore meta-model "
                            + "in the same directory as the instance model");
                }
                throw new PortException(message.toString());
            }
            org.eclipse.emf.ecore.util.EcoreUtil.resolveAll(this.resourceSet);
            return result;
        }

        /** Registers the packages of a loaded meta-model resource. */
        void register(Resource resource, String sourceName) {
            for (var content : resource.getContents()) {
                if (content instanceof EPackage pkg) {
                    this.sources.put(pkg, sourceName);
                    registerRecursively(pkg);
                }
            }
        }

        private void registerRecursively(EPackage pkg) {
            String nsURI = pkg.getNsURI();
            if (nsURI != null) {
                this.resourceSet.getPackageRegistry().put(nsURI, pkg);
            }
            pkg.getESubpackages().forEach(this::registerRecursively);
        }

        /** Converts a loaded resource to the corresponding grammar resources,
         * the updated mapping resource included. */
        Set<Imported> convert(QualName name, Resource resource,
                              FileType fileType) throws PortException {
            Set<Imported> result = new LinkedHashSet<>();
            EcoreToGraphs converter;
            if (fileType == FileType.ECORE) {
                register(resource, name.toString());
                List<EPackage> roots = rootsOf(resource);
                if (roots.isEmpty()) {
                    throw new PortException("Ecore file '%s' contains no packages", name);
                }
                converter = new EcoreToGraphs(roots, this.options);
                result
                    .add(new Imported(ResourceKind.TYPE, converter.toTypeGraph(name.toString())));
            } else {
                List<EPackage> roots = metamodelOf(resource);
                if (roots.isEmpty()) {
                    throw new PortException(
                        "Cannot determine the meta-model of instance model '%s'. "
                            + "Place the corresponding .ecore meta-model in the same directory",
                        name);
                }
                converter = new EcoreToGraphs(roots, this.options);
                String typeName = this.sources.get(roots.get(0));
                if (typeName == null) {
                    typeName = roots.get(0).getName();
                }
                AspectGraph typeGraph = converter.toTypeGraph(typeName);
                AspectGraph hostGraph = converter.toHostGraph(name.toString(), resource);
                result.add(new Imported(ResourceKind.TYPE, typeGraph));
                result.add(new Imported(ResourceKind.HOST, hostGraph));
            }
            result.add(settings(converter));
            return result;
        }

        /**
         * Returns the mapping resource of the grammar with the round-trip
         * records of a conversion merged into it. The resource is the unique
         * one of the Ecore schema, or a fresh one under the default name if the
         * grammar has none; more than one is already an error, reported by
         * {@link EcoreMapping#of}.
         */
        Imported settings(EcoreToGraphs converter) {
            List<QualName> candidates = EcoreMapping.candidates(this.grammar);
            QualName target = candidates.isEmpty()
                ? EcoreMapping.RESOURCE_QUAL_NAME
                : candidates.get(0);
            String oldText
                = this.grammar.getStore().getTexts(ResourceKind.SETTINGS).get(target);
            return new Imported(ResourceKind.SETTINGS, target,
                EcoreMapping.addEntries(oldText, converter.entryLines(), this.source), true);
        }

        /** Returns the root packages declared by a meta-model resource, in model order. */
        private List<EPackage> rootsOf(Resource resource) {
            List<EPackage> result = new ArrayList<>();
            for (var content : resource.getContents()) {
                if (content instanceof EPackage pkg) {
                    result.add(pkg);
                }
            }
            return result;
        }

        /** Returns the root packages of the classes occurring in an instance resource,
         * in the order in which the classes are first met. */
        private List<EPackage> metamodelOf(Resource resource) {
            Set<EPackage> result = new LinkedHashSet<>();
            for (var it = resource.getAllContents(); it.hasNext();) {
                EObject object = it.next();
                EPackage pkg = object.eClass().getEPackage();
                while (pkg != null && pkg.getESuperPackage() != null) {
                    pkg = pkg.getESuperPackage();
                }
                if (pkg != null) {
                    result.add(pkg);
                }
            }
            return new ArrayList<>(result);
        }
    }
}
