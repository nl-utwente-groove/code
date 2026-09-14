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

import java.util.Objects;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Constant;
import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.type.Multiplicity;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.io.external.format.ecore.EcoreMapping.FeatureData;
import nl.utwente.groove.io.external.format.ecore.EcoreMapping.Kind;
import nl.utwente.groove.io.external.format.ecore.EcoreMapping.PackageData;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.IdValidator;

/**
 * Conversion of GROOVE aspect graphs back to Ecore meta-models and instance
 * models: the inverse of {@link EcoreToGraphs}.
 * <p>
 * A converter is first fed a type graph ({@link #addTypeGraph(AspectGraph)}),
 * which yields the root {@link EPackage}s and at the same time builds the
 * classifier and feature tables that an instance model needs; a host graph can
 * then be converted with {@link #toObjects(AspectGraph)}.
 * <p>
 * The Ecore declarations that the type graph does not determine — package data,
 * the enum/interface classification, the opposite pairing, the declared data
 * types and the order and uniqueness of the features — are taken from the
 * records of the {@link EcoreMapping}, which the import writes into the
 * {@code ecore} settings resource. The records are resolved against the labels
 * of the type graph in hand: an unmatched record is skipped (it may concern
 * another metamodel), and a label without a record becomes a class in the
 * default package. A type graph without any records is therefore exported by
 * the default policy of the design: a single package named after the graph, in
 * which every node type is a class.
 * <p>
 * Since an export has no graph to attach errors to, all problems are collected
 * in {@link #getErrors()}; it is up to the caller to report them.
 * @author Arend Rensink
 */
@NonNullByDefault
public class GraphsToEcore {
    /** Constructs a converter with given encoding options. */
    public GraphsToEcore(EcoreMapping options) {
        this.options = options;
    }

    private final EcoreMapping options;

    /** Returns the errors collected during the conversion. */
    public FormatErrorSet getErrors() {
        return this.errors;
    }

    private final FormatErrorSet errors = new FormatErrorSet();

    // ----------------------------------------------------------------------
    // Meta-model
    // ----------------------------------------------------------------------

    /**
     * Converts a type graph to a meta-model, and returns its root packages in
     * the order in which they were recorded.
     * @param typeGraph the type graph to be converted
     */
    public List<EPackage> addTypeGraph(AspectGraph typeGraph) {
        this.typeGraph = typeGraph;
        collectNodes(typeGraph);
        List<EPackage> result = createPackages(typeGraph.getName());
        createClassifiers();
        createFeatures();
        createOpposites();
        return prune(result);
    }

    /** The type graph passed to {@link #addTypeGraph(AspectGraph)}. */
    private @Nullable AspectGraph typeGraph;

    private AspectGraph getTypeGraph() {
        var result = this.typeGraph;
        assert result != null : "Type graph not set";
        return result;
    }

    /** Mapping from node type labels to the type graph nodes declaring them. */
    private final Map<String,@Nullable AspectNode> typeNodes = new LinkedHashMap<>();
    /** Mapping from package paths to the packages created for them. */
    private final Map<String,@Nullable EPackage> packages = new LinkedHashMap<>();
    /** The package that classifiers go into if the metadata does not say. */
    private @Nullable EPackage defaultPackage;
    /** Mapping from node type labels to the classifiers created for them. */
    private final Map<String,EClassifier> classifiers = new LinkedHashMap<>();
    /** Mapping from node type labels to the Ecore element paths they were matched to. */
    private final Map<String,String> classifierPaths = new LinkedHashMap<>();
    /** Mapping from Ecore names to the data types created for them. */
    private final Map<String,@Nullable EDataType> dataTypes = new LinkedHashMap<>();
    /** Mapping from node type labels to the enum literals created for them. */
    private final Map<String,@Nullable EEnumLiteral> literals = new LinkedHashMap<>();
    /** Mapping from node type labels to the nodified-edge data of intermediate nodes. */
    private final Map<String,Intermediate> intermediates = new LinkedHashMap<>();
    /** Mapping from {@code owner.feature} references to the created features. */
    private final Map<String,EStructuralFeature> features = new LinkedHashMap<>();
    /** Mapping from Ecore element paths to the features created for them. */
    private final Map<String,@Nullable EStructuralFeature> featurePaths = new LinkedHashMap<>();
    /** Mapping from the created features to the edge labels they came from.
     * The two differ whenever a repaired name was restored on creation, so this
     * is what a host graph's edge labels have to be resolved against. */
    private final Map<EStructuralFeature,@Nullable String> featureLabels = new LinkedHashMap<>();

    /** Collects the node type labels of a graph. */
    private void collectNodes(AspectGraph graph) {
        for (var node : graph.nodeSet()) {
            String label = labelOf(graph, node);
            if (label != null) {
                this.typeNodes.put(label, node);
                if (node.has(Category.EDGE)) {
                    this.intermediates.put(label, analyse(graph, node));
                }
            }
        }
    }

    /** Creates the recorded packages, and returns the root ones.
     * If no package is recorded, a single default package is created.
     */
    private List<EPackage> createPackages(String graphName) {
        List<EPackage> result = new ArrayList<>();
        // the entries are alphabetical, in which a sub-package may precede its
        // parent ('shop.catalog.package' before 'shop.package'); the packages
        // have to be created outside in for the nesting to be reconstructed
        List<String> paths = new ArrayList<>(this.options.packages().keySet());
        paths.sort(Comparator.comparingInt(p -> segments(p).size()));
        for (var path : paths) {
            PackageData data = this.options.packages().get(path);
            assert data != null; // the paths are the keys of the package map
            int split = path.lastIndexOf(PATH_SEP);
            String name = path.substring(split + 1);
            EPackage pkg = FACTORY.createEPackage();
            pkg.setName(name);
            pkg.setNsURI(data.nsURI());
            String prefix = data.nsPrefix();
            pkg.setNsPrefix(prefix == null
                ? name
                : prefix);
            this.packages.put(path, pkg);
            EPackage parent = split < 0
                ? null
                : this.packages.get(path.substring(0, split));
            if (parent == null) {
                result.add(pkg);
            } else {
                parent.getESubpackages().add(pkg);
            }
        }
        if (result.isEmpty()) {
            // no package is recorded: derive one from the graph name
            int split = graphName.lastIndexOf(PATH_SEP);
            String name = IdValidator.JAVA_ID_NON_RESERVED.repair(graphName.substring(split + 1));
            EPackage pkg = FACTORY.createEPackage();
            pkg.setName(name);
            pkg.setNsURI(DEFAULT_NS_URI_PREFIX + graphName);
            pkg.setNsPrefix(name);
            result.add(pkg);
        }
        this.defaultPackage = result.get(0);
        return result;
    }

    /**
     * Creates the classifiers of the meta-model, in the order of the type nodes
     * of the graph — which is the model order of an imported metamodel, so that
     * an export followed by an import reproduces the original classifier order.
     * The recorded data types, which have no type node, come last in their
     * package; the enum literals, which need their enums, come last of all.
     */
    private void createClassifiers() {
        // resolve every type label against the recorded entries
        Map<String,@Nullable Match> matches = new LinkedHashMap<>();
        for (var label : this.typeNodes.keySet()) {
            if (!this.intermediates.containsKey(label)) {
                matches.put(label, matchFor(label));
            }
        }
        // the literal types are the sub-types of the recorded enums
        Set<String> enumLabels = new LinkedHashSet<>();
        matches.forEach((label, match) -> {
            if (match != null && match.kind() == Kind.ENUM) {
                enumLabels.add(label);
            }
        });
        Set<String> literalLabels = new LinkedHashSet<>();
        for (var label : matches.keySet()) {
            AspectNode node = this.typeNodes.get(label);
            assert node != null; // the labels are the keys of the type node map
            if (superLabels(node).stream().anyMatch(enumLabels::contains)) {
                literalLabels.add(label);
            }
        }
        Set<String> usedPaths = new LinkedHashSet<>();
        for (var entry : matches.entrySet()) {
            String label = entry.getKey();
            if (literalLabels.contains(label)) {
                continue;
            }
            Match match = entry.getValue();
            // a label without a match is a class of its own name in the default
            // package: an unrecorded type is exported, not silently dropped
            String path = match == null
                ? label
                : match.path();
            Kind kind = match == null
                ? Kind.CLASS
                : match.kind();
            usedPaths.add(path);
            addClassifier(label, path, createClassifier(lastSegment(path), kind));
        }
        // the recorded data types have no type node of their own
        for (var entry : this.options.kinds().entrySet()) {
            String path = entry.getKey();
            if (entry.getValue() != Kind.DATATYPE || usedPaths.contains(path)) {
                continue;
            }
            addClassifier(null, path, createClassifier(lastSegment(path), Kind.DATATYPE));
        }
        for (var label : literalLabels) {
            createLiteral(label);
        }
    }

    /** Creates the enum literal of a given type label, in the enum of its super-type. */
    private void createLiteral(String label) {
        AspectNode node = this.typeNodes.get(label);
        assert node != null; // the literal labels are keys of the type node map
        EEnum eEnum = null;
        String enumLabel = null;
        for (var superLabel : superLabels(node)) {
            if (this.classifiers.get(superLabel) instanceof EEnum found) {
                eEnum = found;
                enumLabel = superLabel;
            }
        }
        if (eEnum == null || enumLabel == null) {
            this.errors.add("Enum literal '%s' has no enum type", label);
            return;
        }
        EEnumLiteral literal = FACTORY.createEEnumLiteral();
        literal.setName(literalNameFor(label, enumLabel));
        literal.setValue(eEnum.getELiterals().size());
        eEnum.getELiterals().add(literal);
        this.literals.put(label, literal);
    }

    /**
     * Returns the Ecore name of the enum literal with a given type label: the
     * last segment of a recorded {@code typeName} entry if there is one,
     * otherwise the inverse of the literal naming style — the label with the
     * enum's own label stripped off, for the qualified style, and the plain
     * label for the plain style.
     */
    private String literalNameFor(String label, String enumLabel) {
        String path = reduce(label, EcoreMapping.TYPE_NAME_KEY, namedPaths(label));
        if (path != null) {
            return lastSegment(path);
        }
        String prefix = enumLabel + EcoreNames.SEPARATOR;
        return label.startsWith(prefix)
            ? label.substring(prefix.length())
            : label;
    }

    /**
     * Returns the Ecore element path and classifier kind recorded for a given
     * type label: a {@code typeName} entry whose value is the label if there is
     * one, otherwise a {@code kind} entry named after the label. An ambiguous
     * match is an error; no match at all is not (the graph may hold types the
     * settings do not know about).
     */
    private @Nullable Match matchFor(String label) {
        List<String> named = namedPaths(label);
        String choice = named.isEmpty()
            ? EcoreMapping.KIND_KEY
            : EcoreMapping.TYPE_NAME_KEY;
        List<String> candidates = named.isEmpty()
            ? this.options
                .kinds()
                .keySet()
                .stream()
                .filter(p -> lastSegment(p).equals(label))
                .toList()
            : named;
        String path = reduce(label, choice, candidates);
        if (path == null) {
            return null;
        }
        Kind kind = this.options.kinds().get(path);
        if (kind == null) {
            // the matched entry may be a less qualified typeName override;
            // the kind (and with it the package) then comes from its counterpart
            String kindPath = reduce(label, EcoreMapping.KIND_KEY, relatedPaths(path));
            if (kindPath != null) {
                path = kindPath;
                kind = this.options.kinds().get(kindPath);
            }
        }
        return new Match(path, kind == null
            ? Kind.CLASS
            : kind);
    }

    /** Returns the paths of the {@code typeName} entries whose value is a given label. */
    private List<String> namedPaths(String label) {
        return this.options
            .typeNames()
            .entrySet()
            .stream()
            .filter(e -> e.getValue().equals(label))
            .map(Map.Entry::getKey)
            .toList();
    }

    /** Returns the paths of the {@code kind} entries that qualify a given path
     * further, or are qualified further by it. */
    private List<String> relatedPaths(String path) {
        return this.options
            .kinds()
            .keySet()
            .stream()
            .filter(p -> isSuffix(p, path) || isSuffix(path, p))
            .toList();
    }

    /**
     * Reduces a list of matching element paths to the one to be used: the most
     * qualified, if the paths all qualify one another and hence denote the same
     * Ecore element. Genuinely distinct matches are an error.
     * @return the single applicable path, or {@code null} if there is none
     */
    private @Nullable String reduce(String label, String choice, List<String> paths) {
        if (paths.size() <= 1) {
            return paths.isEmpty()
                ? null
                : paths.get(0);
        }
        String longest = paths.get(0);
        for (var path : paths) {
            if (segments(path).size() > segments(longest).size()) {
                longest = path;
            }
        }
        for (var path : paths) {
            if (!isSuffix(path, longest)) {
                this.errors
                    .add("Label '%s' matches multiple %s mapping entries: %s", label, choice,
                         String.join(" and ", paths));
                return null;
            }
        }
        return longest;
    }

    /** Creates an empty classifier of a given kind and Ecore name. */
    private EClassifier createClassifier(String name, Kind kind) {
        EClassifier result;
        switch (kind) {
        case ENUM -> {
            result = FACTORY.createEEnum();
            result.setName(name);
        }
        case DATATYPE -> {
            result = FACTORY.createEDataType();
            result.setName(name);
            // the encoding maps every custom data type to a string,
            // so that is the instance class it comes back with
            result.setInstanceClassName(String.class.getName());
        }
        default -> result = createClass(name, kind == Kind.INTERFACE);
        }
        return result;
    }

    /** Creates a class with a given name, taking its abstractness from the type graph. */
    private EClass createClass(String name, boolean isInterface) {
        EClass result = FACTORY.createEClass();
        result.setName(name);
        result.setInterface(isInterface);
        result.setAbstract(isInterface);
        return result;
    }

    /**
     * Adds a classifier to the package its element path lies in, and registers it.
     * @param label the type label the classifier was created for, or {@code null}
     * for a data type, which has no type node
     * @param path the Ecore element path of the classifier
     */
    private void addClassifier(@Nullable String label, String path, EClassifier classifier) {
        int split = path.lastIndexOf(PATH_SEP);
        EPackage pkg = split < 0
            ? null
            : this.packages.get(path.substring(0, split));
        if (pkg == null) {
            pkg = this.defaultPackage;
        }
        assert pkg != null;
        pkg.getEClassifiers().add(classifier);
        if (classifier instanceof EDataType dataType && !(classifier instanceof EEnum)) {
            this.dataTypes.put(lastSegment(path), dataType);
        }
        if (label == null) {
            return;
        }
        this.classifiers.put(label, classifier);
        this.classifierPaths.put(label, path);
        AspectNode node = this.typeNodes.get(label);
        if (classifier instanceof EClass eClass && node != null
            && node.has(AspectKind.ABSTRACT)) {
            eClass.setAbstract(true);
        }
    }

    /** Drops the packages that hold neither a classifier nor a non-empty
     * sub-package, and returns the root packages that survive. */
    private List<EPackage> prune(List<EPackage> roots) {
        List<EPackage> result = new ArrayList<>();
        for (var pkg : roots) {
            if (retain(pkg)) {
                result.add(pkg);
            }
        }
        return result;
    }

    /** Prunes the sub-packages of a package, and tells if the package itself
     * has anything left in it. */
    private boolean retain(EPackage pkg) {
        List<EPackage> keep = new ArrayList<>();
        for (var sub : pkg.getESubpackages()) {
            if (retain(sub)) {
                keep.add(sub);
            }
        }
        pkg.getESubpackages().retainAll(keep);
        return !pkg.getEClassifiers().isEmpty() || !pkg.getESubpackages().isEmpty();
    }

    /** Creates the super-types and the structural features of all classes. */
    private void createFeatures() {
        for (var entry : this.classifiers.entrySet()) {
            if (!(entry.getValue() instanceof EClass eClass)) {
                continue;
            }
            AspectNode node = this.typeNodes.get(entry.getKey());
            if (node == null) {
                continue;
            }
            for (var superLabel : superLabels(node)) {
                if (this.classifiers.get(superLabel) instanceof EClass superClass) {
                    eClass.getESuperTypes().add(superClass);
                }
            }
            String ownerPath = this.classifierPaths.getOrDefault(entry.getKey(), entry.getKey());
            for (var descriptor : descriptorsOf(entry.getKey(), node)) {
                EStructuralFeature feature = createFeature(entry.getKey(), ownerPath, descriptor);
                if (feature != null) {
                    eClass.getEStructuralFeatures().add(feature);
                    this.features.put(entry.getKey() + FEATURE_SEP + descriptor.name(), feature);
                    this.featurePaths.put(ownerPath + FEATURE_SEP + feature.getName(), feature);
                    this.featureLabels.put(feature, descriptor.name());
                }
            }
        }
    }

    /**
     * Returns the feature descriptors of a class node, in the canonical order in
     * which the metadata records them (by feature name, ties broken by target).
     */
    private List<Descriptor> descriptorsOf(String ownerLabel, AspectNode node) {
        AspectGraph graph = getTypeGraph();
        List<Descriptor> result = new ArrayList<>();
        for (var edge : graph.outEdgeSet(node)) {
            if (edge.getRole() == EdgeRole.NODE_TYPE || edge.has(AspectKind.SUBTYPE)) {
                continue;
            }
            var sortAspect = edge.get(Category.SORT);
            Sort sort = edge.getSort();
            if (sortAspect != null && sort != null) {
                result
                    .add(new Descriptor(sortAspect.getContentString(), sort, null, false, null,
                        false));
                continue;
            }
            String name = nameOf(edge);
            if (name == null) {
                continue;
            }
            String targetLabel = labelOf(graph, edge.target());
            Intermediate inter = targetLabel == null
                ? null
                : this.intermediates.get(targetLabel);
            if (inter == null) {
                result
                    .add(new Descriptor(name, null, targetLabel, edge.has(AspectKind.COMPOSITE),
                        edge.getOutMult(), false));
            } else {
                result
                    .add(new Descriptor(name, inter.sort(), inter.targetLabel(),
                        inter.containment(), null, true));
            }
        }
        result
            .sort(Comparator
                .comparing(Descriptor::name)
                .thenComparing(d -> d.targetLabel() == null
                    ? ""
                    : d.targetLabel()));
        return result;
    }

    /**
     * Returns the feature declaration recorded for a feature of a given class:
     * the entry under the feature's own label if there is one, otherwise the
     * entry whose recorded Ecore name repairs to that label.
     */
    private @Nullable FeatureData featureDataFor(String ownerPath, String label) {
        var result = this.options.features().get(ownerPath + FEATURE_SEP + label);
        if (result != null) {
            return result;
        }
        String prefix = ownerPath + FEATURE_SEP;
        for (var entry : this.options.features().entrySet()) {
            String key = entry.getKey();
            if (!key.startsWith(prefix) || key.indexOf(PATH_SEP, prefix.length()) >= 0) {
                continue;
            }
            String name = entry.getValue().name();
            if (name != null && EcoreNames.featureLabelFor(name).equals(label)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /** Creates the structural feature described by a given descriptor. */
    private @Nullable EStructuralFeature createFeature(String ownerLabel, String ownerPath,
                                                      Descriptor descriptor) {
        FeatureData data = featureDataFor(ownerPath, descriptor.name());
        EStructuralFeature result;
        String targetLabel = descriptor.targetLabel();
        EClassifier target = targetLabel == null
            ? null
            : this.classifiers.get(targetLabel);
        if (target instanceof EClass targetClass) {
            EReference reference = FACTORY.createEReference();
            reference.setEType(targetClass);
            reference.setContainment(descriptor.containment());
            result = reference;
        } else if (target instanceof EEnum targetEnum) {
            EAttribute attribute = FACTORY.createEAttribute();
            attribute.setEType(targetEnum);
            result = attribute;
        } else if (targetLabel != null) {
            this.errors
                .add("Target type '%s' of feature '%s.%s' is not an Ecore classifier", targetLabel,
                     ownerLabel, descriptor.name());
            return null;
        } else {
            Sort sort = descriptor.sort();
            if (sort == null) {
                this.errors
                    .add("Feature '%s.%s' has no type", ownerLabel, descriptor.name());
                return null;
            }
            EAttribute attribute = FACTORY.createEAttribute();
            attribute.setEType(dataTypeOf(sort, data));
            result = attribute;
        }
        // the label is the repaired name; the record has the original one
        String name = data == null
            ? null
            : data.name();
        result.setName(name == null
            ? descriptor.name()
            : name);
        setBounds(result, descriptor, data);
        if (data != null) {
            Boolean ordered = data.ordered();
            if (ordered != null) {
                result.setOrdered(ordered);
            }
            Boolean unique = data.unique();
            if (unique != null) {
                result.setUnique(unique);
            }
        }
        return result;
    }

    /**
     * Sets the multiplicity bounds of a newly created feature.
     * The multiplicity annotation of the type graph wins, since that is what a
     * user editing the type graph would change; where there is none — for
     * attributes, which the encoding writes as self-loops, and for the
     * intermediate encoding — the recorded bounds are used.
     */
    private void setBounds(EStructuralFeature feature, Descriptor descriptor,
                           @Nullable FeatureData data) {
        Multiplicity mult = descriptor.indexed()
            ? null
            : descriptor.mult();
        var bounds = data == null
            ? null
            : data.bounds();
        if (mult != null) {
            feature.setLowerBound(mult.lower());
            feature.setUpperBound(mult.isUnbounded()
                ? -1
                : mult.upper());
        } else if (bounds != null) {
            feature.setLowerBound(bounds.lower());
            feature.setUpperBound(bounds.upper());
        } else if (data != null) {
            // the record omits the bounds, so they are the Ecore defaults
            feature.setLowerBound(0);
            feature.setUpperBound(1);
        } else {
            // an attribute self-loop stands for a single value; an unannotated
            // edge for the Ecore default 0..*
            feature.setLowerBound(0);
            feature.setUpperBound(descriptor.targetLabel() == null
                ? 1
                : -1);
        }
    }

    /** Returns the data type of an attribute of a given sort:
     * the recorded declared type if there is one, otherwise the sort's default. */
    private EDataType dataTypeOf(Sort sort, @Nullable FeatureData data) {
        String declared = data == null
            ? null
            : data.type();
        if (declared != null) {
            EDataType custom = this.dataTypes.get(declared);
            if (custom != null) {
                return custom;
            }
            if (EcorePackage.eINSTANCE.getEClassifier(declared) instanceof EDataType standard) {
                return standard;
            }
        }
        var result = EcorePackage.eINSTANCE.getEClassifier(EcoreToGraphs.defaultTypeName(sort));
        assert result instanceof EDataType;
        return (EDataType) result;
    }

    /** Wires up the recorded opposite reference pairs. */
    private void createOpposites() {
        for (var entry : this.options.opposites().entrySet()) {
            var one = featureFor(entry.getKey());
            var two = featureFor(entry.getValue());
            if (one instanceof EReference first && two instanceof EReference second) {
                first.setEOpposite(second);
                second.setEOpposite(first);
            }
        }
    }

    /** Returns the feature created for a given Ecore element path, if any.
     * A path that qualifies a created feature further, or is qualified further
     * by it, resolves to that feature as long as it does so uniquely. */
    private @Nullable EStructuralFeature featureFor(String path) {
        var result = this.featurePaths.get(path);
        if (result != null) {
            return result;
        }
        for (var entry : this.featurePaths.entrySet()) {
            String key = entry.getKey();
            if (isSuffix(path, key) || isSuffix(key, path)) {
                if (result != null) {
                    return null;
                }
                result = entry.getValue();
            }
        }
        return result;
    }

    /** Analyses an intermediate (nodified edge) node of the type graph. */
    private Intermediate analyse(AspectGraph graph, AspectNode node) {
        Sort sort = null;
        String targetLabel = null;
        boolean containment = false;
        for (var edge : graph.outEdgeSet(node)) {
            if (edge.getRole() == EdgeRole.NODE_TYPE) {
                continue;
            }
            var sortAspect = edge.get(Category.SORT);
            if (sortAspect != null) {
                if (VALUE.equals(sortAspect.getContentString())) {
                    sort = edge.getSort();
                }
            } else if (VALUE.equals(nameOf(edge))) {
                targetLabel = labelOf(graph, edge.target());
                containment = edge.has(AspectKind.COMPOSITE);
            }
        }
        return new Intermediate(sort, targetLabel, containment);
    }

    // ----------------------------------------------------------------------
    // Instance model
    // ----------------------------------------------------------------------

    /**
     * Converts a host graph to an instance model of the meta-model built by
     * {@link #addTypeGraph(AspectGraph)}, and returns its root objects.
     * @param hostGraph the host graph to be converted
     */
    public List<EObject> toObjects(AspectGraph hostGraph) {
        AspectGraph graph = hostGraph.normalise();
        if (!graph.isNormal()) {
            this.errors.add("Host graph '%s' cannot be normalised", hostGraph.getName());
            return new ArrayList<>();
        }
        Map<AspectNode,@Nullable EObject> objects = new LinkedHashMap<>();
        for (var node : graph.nodeSet()) {
            if (node.getValue() != null) {
                // this is a data value node
                continue;
            }
            String label = labelOf(graph, node);
            if (label == null) {
                this.errors.add("Node without a node type cannot be exported");
                continue;
            }
            if (this.literals.containsKey(label) || this.intermediates.containsKey(label)) {
                continue;
            }
            if (!(this.classifiers.get(label) instanceof EClass eClass)) {
                this.errors.add("Node type '%s' is not an Ecore class", label);
                continue;
            }
            if (eClass.isAbstract()) {
                this.errors.add("Node type '%s' is an abstract Ecore class", label);
                continue;
            }
            EObject object = EcoreUtil.create(eClass);
            objects.put(node, object);
            if (this.options.useIdentifiers()) {
                String id = node.getId();
                if (id != null) {
                    this.identifiers.put(object, id);
                }
            }
        }
        Map<AspectNode,@Nullable AspectNode> containers = new LinkedHashMap<>();
        for (var entry : objects.entrySet()) {
            // the map is only ever filled with non-null objects;
            // null is what a lookup of an unexportable node yields
            var object = entry.getValue();
            assert object != null;
            addValues(graph, entry.getKey(), object, objects, containers);
        }
        checkContainment(graph, objects.keySet(), containers);
        List<EObject> result = new ArrayList<>();
        for (var entry : objects.entrySet()) {
            if (!containers.containsKey(entry.getKey())) {
                var object = entry.getValue();
                assert object != null;
                result.add(object);
            }
        }
        return result;
    }

    /** Returns the {@code xmi:id}s of the objects created by {@link #toObjects(AspectGraph)}. */
    public Map<EObject,String> getIdentifiers() {
        return this.identifiers;
    }

    /** Mapping from created objects to their (optional) identifiers. */
    private final Map<EObject,String> identifiers = new LinkedHashMap<>();

    /** Adds the feature values of a single object. */
    private void addValues(AspectGraph graph, AspectNode node, EObject object,
                           Map<AspectNode,@Nullable EObject> objects,
                           Map<AspectNode,@Nullable AspectNode> containers) {
        EClass eClass = object.eClass();
        // the values of an indexed feature are collected first, and set in index order
        Map<EStructuralFeature,List<Indexed>> indexed = new LinkedHashMap<>();
        for (var edge : sortedOutEdges(graph, node)) {
            String name = nameOf(edge);
            if (name == null) {
                continue;
            }
            EStructuralFeature feature = featureOf(eClass, name);
            if (feature == null) {
                this.errors.add("Node type '%s' has no feature '%s'", eClass.getName(), name);
                continue;
            }
            AspectNode target = edge.target();
            String targetLabel = labelOf(graph, target);
            if (targetLabel != null && this.intermediates.containsKey(targetLabel)) {
                AspectNode value = valueNodeOf(graph, target);
                if (value != null) {
                    indexed
                        .computeIfAbsent(feature, f -> new ArrayList<>())
                        .add(new Indexed(indexOf(graph, target), value));
                }
                continue;
            }
            setValue(graph, object, feature, target, objects, node, containers);
        }
        for (var entry : indexed.entrySet()) {
            List<Indexed> values = new ArrayList<>(entry.getValue());
            values.sort(Comparator.comparingInt(Indexed::index));
            for (var value : values) {
                setValue(graph, object, entry.getKey(), value.node(), objects, node, containers);
            }
        }
    }

    /** Returns the feature of a class that a given edge label stands for,
     * inherited features included. The lookup goes by label rather than by
     * Ecore name, since the two differ for a restored name. */
    private @Nullable EStructuralFeature featureOf(EClass eClass, String label) {
        for (var feature : eClass.getEAllStructuralFeatures()) {
            if (label.equals(this.featureLabels.get(feature))) {
                return feature;
            }
        }
        return null;
    }

    /** Sets or adds a single feature value of an object. */
    private void setValue(AspectGraph graph, EObject object, EStructuralFeature feature,
                          AspectNode target, Map<AspectNode,@Nullable EObject> objects,
                          AspectNode node, Map<AspectNode,@Nullable AspectNode> containers) {
        Object value = valueOf(graph, feature, target, objects);
        if (value == null) {
            return;
        }
        if (feature instanceof EReference reference && reference.isContainment()) {
            AspectNode old = containers.put(target, node);
            if (old != null) {
                this.errors
                    .add("Object of type '%s' has more than one container", labelOf(graph, target));
            }
        }
        if (feature.isMany()) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) object.eGet(feature);
            // an opposite reference may have inserted the value already; this can
            // only happen for references, which are unique in Ecore anyway, so
            // for attributes every value is added, duplicates included
            if (feature instanceof EReference && list.contains(value)) {
                return;
            }
            list.add(value);
        } else {
            object.eSet(feature, value);
        }
    }

    /** Returns the Ecore value that a target node stands for. */
    private @Nullable Object valueOf(AspectGraph graph, EStructuralFeature feature,
                                     AspectNode target, Map<AspectNode,@Nullable EObject> objects) {
        Constant constant = target.getValue();
        if (constant != null) {
            if (!(feature.getEType() instanceof EDataType dataType)) {
                this.errors.add("Feature '%s' does not take a data value", feature.getName());
                return null;
            }
            return dataValueOf(dataType, constant);
        }
        String label = labelOf(graph, target);
        if (label == null) {
            return null;
        }
        EEnumLiteral literal = this.literals.get(label);
        if (literal != null) {
            return literal;
        }
        EObject result = objects.get(target);
        if (result == null) {
            this.errors.add("Target of feature '%s' is not an exportable object", feature.getName());
        }
        return result;
    }

    /** Converts a GROOVE constant to a value of a given Ecore data type. */
    private Object dataValueOf(EDataType dataType, Constant constant) {
        String text = switch (constant.getSort()) {
        case BOOL -> constant.getBoolRepr().toString();
        case INT -> constant.getIntRepr().toString();
        case REAL -> constant.getRealRepr().toPlainString();
        default -> constant.getStringRepr();
        };
        try {
            Object result = EcoreUtil.createFromString(dataType, text);
            if (result != null) {
                return result;
            }
        } catch (RuntimeException exc) {
            // fall through to the textual value
        }
        return text;
    }

    /** Reports the objects that do not have a unique containment path to a root. */
    private void checkContainment(AspectGraph graph, Set<AspectNode> objects,
                                  Map<AspectNode,@Nullable AspectNode> containers) {
        for (var node : objects) {
            Set<AspectNode> seen = new LinkedHashSet<>();
            AspectNode current = node;
            while (current != null && seen.add(current)) {
                current = containers.get(current);
            }
            if (current != null) {
                this.errors
                    .add("Object of type '%s' is on a containment cycle", labelOf(graph, node));
            }
        }
    }

    /** Returns the {@code val} target of an intermediate node in a host graph. */
    private @Nullable AspectNode valueNodeOf(AspectGraph graph, AspectNode node) {
        for (var edge : graph.outEdgeSet(node)) {
            if (!edge.isLoop() && VALUE.equals(nameOf(edge))) {
                return edge.target();
            }
        }
        this.errors.add("Intermediate node without a '%s' target", VALUE);
        return null;
    }

    /** Returns the {@code index} value of an intermediate node in a host graph. */
    private int indexOf(AspectGraph graph, AspectNode node) {
        for (var edge : graph.outEdgeSet(node)) {
            if (!edge.isLoop() && INDEX.equals(nameOf(edge))) {
                Constant value = edge.target().getValue();
                if (value != null && value.getSort() == Sort.INT) {
                    return value.getIntRepr().intValue();
                }
            }
        }
        return 0;
    }

    // ----------------------------------------------------------------------
    // Shared helpers
    // ----------------------------------------------------------------------

    /** Returns the node type label of a graph node, if it has one. */
    private static @Nullable String labelOf(AspectGraph graph, AspectNode node) {
        for (var edge : graph.outEdgeSet(node)) {
            if (edge.isLoop() && edge.getRole() == EdgeRole.NODE_TYPE) {
                var label = edge.getTypeLabel();
                if (label != null) {
                    return label.text();
                }
            }
        }
        return null;
    }

    /** Returns the label text of a binary edge, if it has one. */
    private static @Nullable String nameOf(AspectEdge edge) {
        var label = edge.getTypeLabel();
        return label == null || label.getRole() != EdgeRole.BINARY
            ? null
            : label.text();
    }

    /** Returns the node type labels of the direct super-types of a node. */
    private List<String> superLabels(AspectNode node) {
        AspectGraph graph = getTypeGraph();
        List<String> result = new ArrayList<>();
        for (var edge : graph.outEdgeSet(node)) {
            if (edge.has(AspectKind.SUBTYPE) && !edge.isLoop()) {
                String label = labelOf(graph, edge.target());
                if (label != null) {
                    result.add(label);
                }
            }
        }
        result.sort(Comparator.naturalOrder());
        return result;
    }

    /** Returns the non-self edges of a node, in a canonical order. */
    private static List<AspectEdge> sortedOutEdges(AspectGraph graph, AspectNode node) {
        List<AspectEdge> result = new ArrayList<>();
        for (var edge : graph.outEdgeSet(node)) {
            if (edge.getRole() == EdgeRole.BINARY) {
                result.add(edge);
            }
        }
        result
            .sort(Comparator
                .comparing((AspectEdge e) -> String.valueOf(nameOf(e)))
                .thenComparingInt(e -> e.target().getNumber()));
        return result;
    }

    /** Returns the last segment of an Ecore element path. */
    private static String lastSegment(String path) {
        return path.substring(path.lastIndexOf(PATH_SEP) + 1);
    }

    /** Returns the segments of an Ecore element path. */
    private static List<String> segments(String path) {
        return Arrays.asList(path.split("\\" + PATH_SEP, -1));
    }

    /** Tests if one Ecore element path qualifies another further, i.e., if the
     * segments of the second are a suffix of those of the first. */
    private static boolean isSuffix(String suffix, String full) {
        List<String> one = segments(suffix);
        List<String> two = segments(full);
        return two.size() >= one.size()
            && two.subList(two.size() - one.size(), two.size()).equals(one);
    }

    /** The Ecore element path and kind that a type label was matched to.
     * @param path the Ecore element path of the matched entry
     * @param kind the recorded kind, defaulting to {@link Kind#CLASS}
     */
    private static record Match(String path, Kind kind) {
        /** Overrides the generated hash code, which would use identity-based enum hashes. */
        @Override
        public int hashCode() {
            return Objects.hash(this.path, this.kind.ordinal());
        }
        // no additional members
    }

    /** Description of a structural feature, extracted from the type graph.
     * @param name the feature name
     * @param sort the sort of an attribute over a data type, if that is what this is
     * @param targetLabel the node type label of the target, for a reference or enum attribute
     * @param containment if {@code true}, this describes a containment reference
     * @param mult the declared out-multiplicity, if any
     * @param indexed if {@code true}, the feature is encoded through intermediate nodes
     */
    private static record Descriptor(String name, @Nullable Sort sort, @Nullable String targetLabel,
        boolean containment, @Nullable Multiplicity mult, boolean indexed) {

        /** Overrides the generated hash code, which would use identity-based enum hashes. */
        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.sort == null
                    ? -1
                    : this.sort.ordinal(), this.targetLabel, this.containment, this.mult, this.indexed);
        }
        // no additional members
    }

    /** The essentials of an intermediate (nodified edge) node.
     * @param sort the sort of the value, if the feature is a data attribute
     * @param targetLabel the node type label of the value, if the feature is not a data attribute
     * @param containment if {@code true}, the feature is a containment reference
     */
    private static record Intermediate(@Nullable Sort sort, @Nullable String targetLabel,
        boolean containment) {

        /** Overrides the generated hash code, which would use identity-based enum hashes. */
        @Override
        public int hashCode() {
            return Objects.hash(this.sort == null
                    ? -1
                    : this.sort.ordinal(), this.targetLabel, this.containment);
        }
        // no additional members
    }

    /** A single value of an indexed feature, in a host graph.
     * @param index the recorded index of the value
     * @param node the graph node holding the value
     */
    private static record Indexed(int index, AspectNode node) {
        // no additional members
    }

    /** The Ecore model factory. */
    private static final EcoreFactory FACTORY = EcoreFactory.eINSTANCE;
    /** Separator between the segments of a package path in the metadata. */
    private static final char PATH_SEP = '.';
    /** Separator between the owner and the name in a feature reference. */
    private static final String FEATURE_SEP = ".";
    /** Prefix of the namespace URI derived for a type graph without metadata. */
    private static final String DEFAULT_NS_URI_PREFIX = "http://nl.utwente.groove/";
    /** Name of the value feature of an intermediate node. */
    private static final String VALUE = "val";
    /** Name of the index attribute of an intermediate node. */
    private static final String INDEX = "index";
}
