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
import nl.utwente.groove.grammar.ResourceProperties;
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
 * round-trip metadata recorded by {@link EcoreToGraphs} in the graph
 * properties. A type graph without that metadata is exported by the default
 * policy of the design: a single package named after the graph, in which every
 * node type is a class.
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
        ResourceProperties properties = ResourceProperties.getProperties(typeGraph);
        collectNodes(typeGraph);
        collectFeatureData(properties);
        List<EPackage> result = createPackages(properties, typeGraph.getName());
        createClassifiers(properties);
        createFeatures();
        createOpposites(properties);
        return result;
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
    /** Mapping from node type labels to the enum literals created for them. */
    private final Map<String,@Nullable EEnumLiteral> literals = new LinkedHashMap<>();
    /** Mapping from node type labels to the nodified-edge data of intermediate nodes. */
    private final Map<String,Intermediate> intermediates = new LinkedHashMap<>();
    /** Mapping from {@code owner.feature} references to the recorded feature data. */
    private final Map<String,@Nullable FeatureData> featureData = new LinkedHashMap<>();
    /** Mapping from {@code owner.feature} references to the created features. */
    private final Map<String,EStructuralFeature> features = new LinkedHashMap<>();
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

    /** Collects the per-feature metadata records. */
    private void collectFeatureData(ResourceProperties properties) {
        for (var record : records(properties, EcoreToGraphs.FEATURES_KEY, 8)) {
            this.featureData
                .put(record[0] + FEATURE_SEP + record[1],
                     new FeatureData(record[2], Boolean.parseBoolean(record[3]),
                         Boolean.parseBoolean(record[4]), bound(record[5], 0), bound(record[6], 1),
                         record[7]));
        }
    }

    /** Parses a recorded multiplicity bound, falling back to a default. */
    private static int bound(String text, int fallback) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException exc) {
            return fallback;
        }
    }

    /** Creates the packages recorded in the metadata, and returns the root ones.
     * If there is no package metadata, a single default package is created.
     */
    private List<EPackage> createPackages(ResourceProperties properties, String graphName) {
        List<EPackage> result = new ArrayList<>();
        for (var record : records(properties, EcoreToGraphs.PACKAGES_KEY, 3)) {
            String path = record[0];
            int split = path.lastIndexOf(PATH_SEP);
            EPackage pkg = FACTORY.createEPackage();
            pkg.setName(path.substring(split + 1));
            pkg.setNsURI(record[1]);
            pkg.setNsPrefix(record[2]);
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
            // there is no package metadata: derive a package from the graph name
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

    /** Creates the classifiers of the meta-model, in metadata order. */
    private void createClassifiers(ResourceProperties properties) {
        var records = records(properties, EcoreToGraphs.TYPES_KEY, 4);
        if (records.isEmpty()) {
            // there is no classifier metadata: every node type is a class,
            // named by its label or the reverse of a typeName mapping override
            for (var entry : this.typeNodes.entrySet()) {
                if (!this.intermediates.containsKey(entry.getKey())) {
                    addClassifier(entry.getKey(), "",
                                  createClass(ecoreNameFor(entry.getKey()), false));
                }
            }
            return;
        }
        for (var record : records) {
            String label = record[0];
            String kind = record[3];
            if (kind.equals(EcoreNames.DATATYPE_KIND)) {
                EDataType dataType = FACTORY.createEDataType();
                dataType.setName(record[2]);
                // the encoding maps every custom data type to a string,
                // so that is the instance class it comes back with
                dataType.setInstanceClassName(String.class.getName());
                addClassifier(label, record[1], dataType);
                continue;
            }
            if (!this.typeNodes.containsKey(label) || kind.equals(EcoreNames.LITERAL_KIND)) {
                // literals are created in the pass below; classifiers whose node
                // has been removed from the type graph are no longer part of the model
                continue;
            }
            EClassifier classifier;
            if (kind.equals(EcoreNames.ENUM_KIND)) {
                classifier = FACTORY.createEEnum();
                classifier.setName(record[2]);
            } else {
                classifier = createClass(record[2], kind.equals(EcoreNames.INTERFACE_KIND));
            }
            addClassifier(label, record[1], classifier);
        }
        // now the literals, which need their enums to exist
        for (var record : records) {
            if (!record[3].equals(EcoreNames.LITERAL_KIND)) {
                continue;
            }
            String label = record[0];
            AspectNode node = this.typeNodes.get(label);
            if (node == null) {
                continue;
            }
            EEnum eEnum = null;
            for (var superLabel : superLabels(node)) {
                if (this.classifiers.get(superLabel) instanceof EEnum found) {
                    eEnum = found;
                }
            }
            if (eEnum == null) {
                this.errors.add("Enum literal '%s' has no enum type", label);
                continue;
            }
            EEnumLiteral literal = FACTORY.createEEnumLiteral();
            literal.setName(record[2]);
            literal.setValue(eEnum.getELiterals().size());
            eEnum.getELiterals().add(literal);
            this.literals.put(label, literal);
        }
    }

    /**
     * Returns the Ecore class name for a type label of a metadata-free graph:
     * the reverse of a typeName mapping override if there is exactly one whose
     * value is the label, the label itself otherwise. More than one reverse
     * match is an error.
     */
    private String ecoreNameFor(String label) {
        List<String> matches = this.options
            .typeNames()
            .entrySet()
            .stream()
            .filter(e -> e.getValue().equals(label))
            .map(Map.Entry::getKey)
            .toList();
        if (matches.size() > 1) {
            this.errors
                .add("Label '%s' matches multiple %s mapping entries: %s", label,
                     EcoreMapping.TYPE_NAME_KEY, String.join(" and ", matches));
            return label;
        }
        if (matches.isEmpty()) {
            return label;
        }
        String key = matches.get(0);
        return key.substring(key.lastIndexOf('.') + 1);
    }

    /** Creates a class with a given name, taking its abstractness from the type graph. */
    private EClass createClass(String name, boolean isInterface) {
        EClass result = FACTORY.createEClass();
        result.setName(name);
        result.setInterface(isInterface);
        result.setAbstract(isInterface);
        return result;
    }

    /** Adds a classifier to the package of a given path, and registers it. */
    private void addClassifier(String label, String path, EClassifier classifier) {
        EPackage pkg = this.packages.get(path);
        if (pkg == null) {
            pkg = this.defaultPackage;
        }
        assert pkg != null;
        pkg.getEClassifiers().add(classifier);
        this.classifiers.put(label, classifier);
        AspectNode node = this.typeNodes.get(label);
        if (classifier instanceof EClass eClass && node != null
            && node.has(AspectKind.ABSTRACT)) {
            eClass.setAbstract(true);
        }
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
            for (var descriptor : descriptorsOf(entry.getKey(), node)) {
                EStructuralFeature feature = createFeature(entry.getKey(), descriptor);
                if (feature != null) {
                    eClass.getEStructuralFeatures().add(feature);
                    this.features.put(entry.getKey() + FEATURE_SEP + descriptor.name(), feature);
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

    /** Creates the structural feature described by a given descriptor. */
    private @Nullable EStructuralFeature createFeature(String ownerLabel, Descriptor descriptor) {
        FeatureData data = this.featureData.get(ownerLabel + FEATURE_SEP + descriptor.name());
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
        // the label is the repaired name; the metadata has the original one
        result
            .setName(data == null || data.originalName().isEmpty()
                ? descriptor.name()
                : data.originalName());
        setBounds(result, descriptor, data);
        if (data != null) {
            result.setOrdered(data.ordered());
            result.setUnique(data.unique());
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
        if (mult != null) {
            feature.setLowerBound(mult.lower());
            feature.setUpperBound(mult.isUnbounded()
                ? -1
                : mult.upper());
        } else if (data != null) {
            feature.setLowerBound(data.lower());
            feature.setUpperBound(data.upper());
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
            ? ""
            : data.declaredType();
        if (!declared.isEmpty()) {
            if (this.classifiers.get(declared) instanceof EDataType custom) {
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

    /** Wires up the opposite reference pairs recorded in the metadata. */
    private void createOpposites(ResourceProperties properties) {
        for (var record : records(properties, EcoreToGraphs.OPPOSITES_KEY, 2)) {
            var one = this.features.get(record[0]);
            var two = this.features.get(record[1]);
            if (one instanceof EReference first && two instanceof EReference second) {
                first.setEOpposite(second);
                second.setEOpposite(first);
            }
        }
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

    /** Returns the records of a metadata property, restricted to those of the right arity. */
    private List<String[]> records(ResourceProperties properties, String key, int arity) {
        String text = properties.getProperty(key);
        List<String[]> result = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return result;
        }
        for (var record : EcoreToGraphs.split(text, EcoreToGraphs.RECORD_SEP_CHAR, false)) {
            var fields = EcoreToGraphs.split(record, EcoreToGraphs.FIELD_SEP_CHAR, true);
            if (fields.size() == arity) {
                result.add(fields.toArray(new String[0]));
            } else {
                this.errors.add("Malformed '%s' metadata record '%s'", key, record);
            }
        }
        return result;
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

    /** The recorded data of a single structural feature.
     * @param declaredType the name of the declared data type, or the empty string
     * @param ordered the recorded {@code ordered} flag
     * @param unique the recorded {@code unique} flag
     * @param lower the recorded lower bound
     * @param upper the recorded upper bound ({@code -1} if unbounded)
     * @param originalName the Ecore name the label was repaired from, or the
     * empty string if the label reproduces it
     */
    private static record FeatureData(String declaredType, boolean ordered, boolean unique,
        int lower, int upper, String originalName) {
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
