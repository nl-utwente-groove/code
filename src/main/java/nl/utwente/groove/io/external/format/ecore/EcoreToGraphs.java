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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.type.Multiplicity;
import nl.utwente.groove.graph.GraphInfo;
import nl.utwente.groove.graph.GraphProperties;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.plain.PlainGraph;
import nl.utwente.groove.graph.plain.PlainNode;
import nl.utwente.groove.io.external.format.ecore.EcoreOptions.Ordering;
import nl.utwente.groove.util.parse.IdValidator;
import nl.utwente.groove.util.parse.StringHandler;

/**
 * Conversion of Ecore meta-models and instance models to GROOVE aspect graphs.
 * <p>
 * The conversion is direct: the Ecore objects are walked in model order and the
 * corresponding nodes and (aspect-prefixed) edge labels are added to a plain
 * graph, which is turned into an {@link AspectGraph} at the end.
 * <p>
 * The approximations the encoding makes by design — a custom
 * {@link EDataType} becomes a string, and under
 * {@link Ordering#NONE} the order and the duplicates of a many-valued feature
 * are dropped — are silent: they are documented behaviour, and the information
 * needed to reverse them is kept in the round-trip metadata. Format errors are
 * reserved for input that the encoding cannot represent at all, such as a
 * reference to a class outside the imported packages; since GROOVE has no
 * warning severity, an error keeps the resulting graph from compiling, which is
 * only appropriate for input that really is broken.
 * @author Arend Rensink
 */
@NonNullByDefault
public class EcoreToGraphs {
    /**
     * Constructs a converter for the classifiers of a given set of root packages.
     * @param roots the root packages of the meta-model
     * @param options the encoding options to be used
     */
    public EcoreToGraphs(Collection<EPackage> roots, EcoreOptions options) {
        this.names = new EcoreNames(roots);
        this.options = options;
        for (var classifier : this.names.classifiers()) {
            if (classifier instanceof EClass eClass) {
                this.classes.add(eClass);
            } else if (classifier instanceof EEnum eEnum) {
                this.enums.add(eEnum);
            }
        }
    }

    /** Returns the naming policy used by this converter. */
    public EcoreNames getNames() {
        return this.names;
    }

    private final EcoreNames names;
    private final EcoreOptions options;
    /** The classes covered by this converter. */
    private final Set<EClass> classes = new LinkedHashSet<>();
    /** The enums covered by this converter. */
    private final Set<EEnum> enums = new LinkedHashSet<>();

    // ----------------------------------------------------------------------
    // Meta-model
    // ----------------------------------------------------------------------

    /**
     * Converts the meta-model of this converter to a type graph.
     * @param name the name of the resulting graph
     */
    public AspectGraph toTypeGraph(String name) {
        PlainGraph result = new PlainGraph(name, GraphRole.TYPE);
        Map<EClass,PlainNode> classNodes = new LinkedHashMap<>();
        Map<EEnum,PlainNode> enumNodes = new LinkedHashMap<>();
        // first create the nodes for all classes, enums and enum literals
        for (var classifier : this.names.classifiers()) {
            if (classifier instanceof EClass eClass) {
                PlainNode node = addTypeNode(result, this.names.labelFor(eClass));
                if (eClass.isAbstract() || eClass.isInterface()) {
                    result.addEdge(node, ABSTRACT, node);
                }
                classNodes.put(eClass, node);
            } else if (classifier instanceof EEnum eEnum) {
                PlainNode node = addTypeNode(result, this.names.labelFor(eEnum));
                result.addEdge(node, ABSTRACT, node);
                enumNodes.put(eEnum, node);
                for (var literal : eEnum.getELiterals()) {
                    PlainNode litNode = addTypeNode(result, this.names.labelFor(literal));
                    result.addEdge(litNode, SUBTYPE, node);
                }
            }
        }
        // then add the sub-typing, attributes and references
        for (var classifier : this.names.classifiers()) {
            if (!(classifier instanceof EClass eClass)) {
                continue;
            }
            PlainNode node = classNodes.get(eClass);
            assert node != null;
            for (var superType : eClass.getESuperTypes()) {
                PlainNode superNode = classNodes.get(superType);
                if (superNode == null) {
                    result
                        .addError("Superclass '%s' of '%s' is outside the imported packages",
                                  superType.getName(), eClass.getName(), node);
                } else {
                    result.addEdge(node, SUBTYPE, superNode);
                }
            }
            for (var attribute : eClass.getEAttributes()) {
                addTypeAttribute(result, node, eClass, attribute, enumNodes);
            }
            for (var reference : eClass.getEReferences()) {
                addTypeReference(result, node, eClass, reference, classNodes);
            }
        }
        GraphInfo.setProperties(result, createMetadata());
        return AspectGraph.newInstance(result);
    }

    /** Adds the encoding of a single attribute to the type graph. */
    private void addTypeAttribute(PlainGraph graph, PlainNode node, EClass eClass,
                                  EAttribute attribute, Map<EEnum,PlainNode> enumNodes) {
        String label = this.names.labelFor(attribute);
        if (attribute.getEType() instanceof EEnum eEnum) {
            PlainNode enumNode = enumNodes.get(eEnum);
            if (enumNode == null) {
                graph
                    .addError("Enum type '%s' of attribute '%s' is outside the imported packages",
                              eEnum.getName(), attribute.getName(), node);
            } else if (isIndexed(attribute)) {
                PlainNode interNode = addIntermediateNode(graph, node, eClass, attribute);
                graph.addEdge(interNode, "out=1" + SEP + VALUE, enumNode);
            } else {
                addFeatureEdge(graph, node, enumNode, attribute, label);
            }
            return;
        }
        if (!(attribute.getEType() instanceof EDataType dataType)) {
            graph.addError("Attribute '%s' has no data type", attribute.getName(), node);
            return;
        }
        Sort sort = sortOf(dataType);
        if (sort == null) {
            // a custom data type is approximated by a string; the data type itself
            // is recorded in the metadata, so the approximation is reversible
            sort = Sort.STRING;
        }
        if (isIndexed(attribute)) {
            PlainNode interNode = addIntermediateNode(graph, node, eClass, attribute);
            graph.addEdge(interNode, sort.getName() + SEP + VALUE, interNode);
        } else {
            graph.addEdge(node, sort.getName() + SEP + label, node);
        }
    }

    /** Adds the encoding of a single reference to the type graph. */
    private void addTypeReference(PlainGraph graph, PlainNode node, EClass eClass,
                                  EReference reference, Map<EClass,PlainNode> classNodes) {
        EClass target = reference.getEReferenceType();
        PlainNode targetNode = classNodes.get(target);
        if (targetNode == null) {
            graph
                .addError("Type '%s' of reference '%s' is outside the imported packages",
                          target.getName(), reference.getName(), node);
        } else if (isIndexed(reference)) {
            PlainNode interNode = addIntermediateNode(graph, node, eClass, reference);
            String valLabel = "out=1" + SEP + (reference.isContainment()
                ? COMPOSITE
                : "") + VALUE;
            graph.addEdge(interNode, valLabel, targetNode);
        } else {
            addFeatureEdge(graph, node, targetNode, reference, this.names.labelFor(reference));
        }
    }

    /** Adds a (multiplicity-annotated) feature edge between two type nodes. */
    private void addFeatureEdge(PlainGraph graph, PlainNode source, PlainNode target,
                                EStructuralFeature feature, String label) {
        StringBuilder text = new StringBuilder();
        String mult = multiplicityOf(feature);
        if (mult != null) {
            text.append("out=");
            text.append(mult);
            text.append(SEP);
        }
        if (feature instanceof EReference r && r.isContainment()) {
            text.append(COMPOSITE);
        }
        text.append(label);
        graph.addEdge(source, text.toString(), target);
    }

    /**
     * Adds an intermediate (nodified edge) node for a given feature, together with
     * its index attribute and the incoming edge from the feature's owner.
     */
    private PlainNode addIntermediateNode(PlainGraph graph, PlainNode owner, EClass eClass,
                                          EStructuralFeature feature) {
        String label = this.names.labelFor(feature);
        PlainNode result = addTypeNode(graph, interLabel(eClass, feature));
        graph.addEdge(result, EDGE + StringHandler.toQuoted(label, '"'), result);
        graph.addEdge(result, Sort.INT.getName() + SEP + INDEX, result);
        graph.addEdge(owner, "in=1" + SEP + label, result);
        return result;
    }

    /** Returns the type label of the intermediate node for a given feature. */
    private String interLabel(EClass eClass, EStructuralFeature feature) {
        return this.names.labelFor(eClass) + EcoreNames.SEPARATOR + this.names.labelFor(feature);
    }

    /** Creates the round-trip metadata of the meta-model. */
    private GraphProperties createMetadata() {
        GraphProperties result = new GraphProperties();
        StringBuilder packages = new StringBuilder();
        for (var pkg : this.names.packages()) {
            append(packages, this.names.pathOf(pkg), nonNull(pkg.getNsURI()),
                   nonNull(pkg.getNsPrefix()));
        }
        result.setProperty(PACKAGES_KEY, packages.toString());
        StringBuilder types = new StringBuilder();
        for (var classifier : this.names.classifiers()) {
            append(types, this.names.labelFor(classifier),
                   this.names.pathOf(classifier.getEPackage()), nonNull(classifier.getName()),
                   EcoreNames.kindOf(classifier));
            if (classifier instanceof EEnum eEnum) {
                for (var literal : eEnum.getELiterals()) {
                    append(types, this.names.labelFor(literal),
                           this.names.pathOf(eEnum.getEPackage()), nonNull(literal.getName()),
                           "literal");
                }
            }
        }
        result.setProperty(TYPES_KEY, types.toString());
        StringBuilder opposites = new StringBuilder();
        Set<EReference> seen = new LinkedHashSet<>();
        for (var eClass : this.classes) {
            for (var reference : eClass.getEReferences()) {
                var opposite = reference.getEOpposite();
                if (opposite != null && !seen.contains(opposite)) {
                    seen.add(reference);
                    append(opposites, featureRef(reference), featureRef(opposite));
                }
            }
        }
        result.setProperty(OPPOSITES_KEY, opposites.toString());
        return result;
    }

    /** Returns the {@code type.feature} reference of a given structural feature. */
    private String featureRef(EStructuralFeature feature) {
        return this.names.labelFor(feature.getEContainingClass()) + "."
            + this.names.labelFor(feature);
    }

    // ----------------------------------------------------------------------
    // Instance model
    // ----------------------------------------------------------------------

    /**
     * Converts the contents of a given instance resource to a host graph.
     * @param name the name of the resulting graph
     * @param resource the (loaded) instance resource
     */
    public AspectGraph toHostGraph(String name, Resource resource) {
        PlainGraph result = new PlainGraph(name, GraphRole.HOST);
        Map<EObject,PlainNode> nodeMap = new LinkedHashMap<>();
        Map<EEnumLiteral,PlainNode> literalNodes = new LinkedHashMap<>();
        List<EObject> objects = new ArrayList<>();
        for (var it = resource.getAllContents(); it.hasNext();) {
            objects.add(it.next());
        }
        Set<String> usedIds = new LinkedHashSet<>();
        // first create the object nodes
        for (var object : objects) {
            EClass eClass = object.eClass();
            if (!this.classes.contains(eClass)) {
                result
                    .addError("Object of class '%s' is outside the imported packages",
                              eClass.getName());
                continue;
            }
            PlainNode node = addTypeNode(result, this.names.labelFor(eClass));
            nodeMap.put(object, node);
            if (this.options.useIdentifiers()) {
                result.addEdge(node, ID + identifierOf(object, resource, usedIds), node);
            }
        }
        // then the attribute values and references
        for (var object : objects) {
            PlainNode node = nodeMap.get(object);
            if (node == null) {
                continue;
            }
            for (var feature : object.eClass().getEAllStructuralFeatures()) {
                if (feature.isDerived() || feature.isTransient() || !object.eIsSet(feature)) {
                    continue;
                }
                if (feature instanceof EAttribute attribute) {
                    addHostAttribute(result, node, object, attribute, literalNodes);
                } else if (feature instanceof EReference reference) {
                    addHostReference(result, node, object, reference, nodeMap);
                }
            }
        }
        return AspectGraph.newInstance(result);
    }

    /** Adds the values of a single attribute of an object to the host graph. */
    private void addHostAttribute(PlainGraph graph, PlainNode node, EObject object,
                                  EAttribute attribute, Map<EEnumLiteral,PlainNode> literalNodes) {
        List<Object> values = valuesOf(object, attribute);
        String label = this.names.labelFor(attribute);
        int index = 0;
        if (attribute.getEType() instanceof EEnum) {
            for (var value : values) {
                index++;
                if (!(value instanceof EEnumLiteral literal)) {
                    continue;
                }
                if (!this.enums.contains(literal.getEEnum())) {
                    graph
                        .addError("Enum literal '%s' is outside the imported packages",
                                  literal.getName(), node);
                    continue;
                }
                PlainNode litNode = literalNodes
                    .computeIfAbsent(literal, l -> addTypeNode(graph, this.names.labelFor(l)));
                if (isIndexed(attribute)) {
                    PlainNode interNode = addHostIntermediate(graph, node, attribute, index);
                    graph.addEdge(interNode, VALUE, litNode);
                } else {
                    graph.addEdge(node, label, litNode);
                }
            }
            return;
        }
        if (!(attribute.getEType() instanceof EDataType dataType)) {
            return;
        }
        Sort sort = sortOf(dataType);
        if (sort == null) {
            sort = Sort.STRING;
        }
        for (var value : values) {
            index++;
            String symbol = symbolOf(sort, dataType, value);
            if (isIndexed(attribute)) {
                PlainNode interNode = addHostIntermediate(graph, node, attribute, index);
                graph.addEdge(interNode, LET + VALUE + "=" + symbol, interNode);
            } else if (index == 1) {
                graph.addEdge(node, LET + label + "=" + symbol, node);
            }
        }
    }

    /** Adds the targets of a single reference of an object to the host graph. */
    private void addHostReference(PlainGraph graph, PlainNode node, EObject object,
                                  EReference reference, Map<EObject,PlainNode> nodeMap) {
        String label = this.names.labelFor(reference);
        int index = 0;
        for (var value : valuesOf(object, reference)) {
            index++;
            if (!(value instanceof EObject target)) {
                continue;
            }
            PlainNode targetNode = nodeMap.get(target);
            if (targetNode == null) {
                graph
                    .addError("Target of reference '%s' is outside the imported resource",
                              reference.getName(), node);
            } else if (isIndexed(reference)) {
                PlainNode interNode = addHostIntermediate(graph, node, reference, index);
                graph.addEdge(interNode, VALUE, targetNode);
            } else {
                graph.addEdge(node, label, targetNode);
            }
        }
    }

    /** Adds an intermediate node for a given occurrence of a many-valued feature. */
    private PlainNode addHostIntermediate(PlainGraph graph, PlainNode owner,
                                          EStructuralFeature feature, int index) {
        EClass eClass = feature.getEContainingClass();
        PlainNode result = addTypeNode(graph, interLabel(eClass, feature));
        graph.addEdge(result, LET + INDEX + "=" + index, result);
        graph.addEdge(owner, this.names.labelFor(feature), result);
        return result;
    }

    /** Returns the (repaired and uniquified) node identifier of a given object. */
    private String identifierOf(EObject object, Resource resource, Set<String> used) {
        String id = resource instanceof XMLResource xml
            ? xml.getID(object)
            : null;
        if (id == null) {
            id = resource.getURIFragment(object);
        }
        String base = IdValidator.JAVA_ID_NON_RESERVED.repair(id);
        String result = base;
        for (int i = 2; !used.add(result); i++) {
            result = base + "_" + i;
        }
        return result;
    }

    // ----------------------------------------------------------------------
    // Shared helpers
    // ----------------------------------------------------------------------

    /** Adds a node with a given node type label to a graph. */
    private PlainNode addTypeNode(PlainGraph graph, String typeLabel) {
        PlainNode result = graph.addNode();
        graph.addEdge(result, TYPE_PREFIX + typeLabel, result);
        return result;
    }

    /** Returns the values of a given feature of an object, as a list. */
    private static List<Object> valuesOf(EObject object, EStructuralFeature feature) {
        Object value = object.eGet(feature);
        List<Object> result = new ArrayList<>();
        if (feature.isMany()) {
            if (value instanceof Collection<?> c) {
                c.forEach(result::add);
            }
        } else if (value != null) {
            result.add(value);
        }
        return result;
    }

    /** Indicates if a feature is many-valued. */
    private static boolean isMultiple(EStructuralFeature feature) {
        return feature.getUpperBound() > 1 || feature.getUpperBound() == -1;
    }

    /** Indicates if the order or the duplicates of a feature's values matter. */
    private static boolean isLossy(EStructuralFeature feature) {
        return isMultiple(feature) && (feature.isOrdered() || !feature.isUnique());
    }

    /** Indicates if a feature is to be encoded through intermediate nodes. */
    private boolean isIndexed(EStructuralFeature feature) {
        return this.options.ordering() == Ordering.INDEX && isLossy(feature);
    }

    /** Returns the multiplicity text of a feature, or {@code null} if it is the default. */
    private static @Nullable String multiplicityOf(EStructuralFeature feature) {
        int lower = Math.max(feature.getLowerBound(), 0);
        int upper = feature.getUpperBound();
        if (lower == 0 && upper < 0) {
            return null;
        }
        return new Multiplicity(lower, upper < 0
            ? Integer.MAX_VALUE
            : upper).toString();
    }

    /** Returns the GROOVE sort corresponding to a standard Ecore data type,
     * or {@code null} if the data type is not one of the standard ones. */
    public static @Nullable Sort sortOf(EDataType dataType) {
        if (dataType.getEPackage() != EcorePackage.eINSTANCE) {
            return null;
        }
        return SORT_MAP.get(dataType.getName());
    }

    /** Returns the parsable GROOVE representation of a data value. */
    private static String symbolOf(Sort sort, EDataType dataType, @Nullable Object value) {
        String text = value == null
            ? ""
            : org.eclipse.emf.ecore.util.EcoreUtil.convertToString(dataType, value);
        if (text == null) {
            text = "";
        }
        return switch (sort) {
        case BOOL -> Boolean.toString(Boolean.parseBoolean(text));
        case INT -> toIntSymbol(text);
        case REAL -> toRealSymbol(text);
        default -> StringHandler.toQuoted(text, '"');
        };
    }

    /** Converts a textual value to an integer constant symbol. */
    private static String toIntSymbol(String text) {
        try {
            return new BigInteger(text.trim()).toString();
        } catch (NumberFormatException exc) {
            return "0";
        }
    }

    /** Converts a textual value to a real constant symbol.
     * GROOVE distinguishes reals from integers by the decimal point,
     * so one is appended if the value does not have one.
     */
    private static String toRealSymbol(String text) {
        String result;
        try {
            result = new BigDecimal(text.trim()).toPlainString();
        } catch (NumberFormatException exc) {
            result = "0.0";
        }
        if (result.indexOf('.') < 0) {
            result += ".0";
        }
        return result;
    }

    /** Appends a record of fields to a metadata property value. */
    private static void append(StringBuilder text, String... fields) {
        if (!text.isEmpty()) {
            text.append(RECORD_SEP);
        }
        text.append(String.join(FIELD_SEP, fields));
    }

    /** Returns a given string, or the empty string if it is {@code null}. */
    private static String nonNull(@Nullable String text) {
        return text == null
            ? ""
            : text;
    }

    /** Mapping from standard Ecore data type names to GROOVE sorts. */
    private static final Map<String,Sort> SORT_MAP = createSortMap();

    private static Map<String,Sort> createSortMap() {
        Map<String,Sort> result = new LinkedHashMap<>();
        for (var name : new String[] {"EBoolean", "EBooleanObject"}) {
            result.put(name, Sort.BOOL);
        }
        for (var name : new String[] {"EBigInteger", "EByte", "EByteObject", "EInt",
            "EIntegerObject", "ELong", "ELongObject", "EShort", "EShortObject"}) {
            result.put(name, Sort.INT);
        }
        for (var name : new String[] {"EBigDecimal", "EDouble", "EDoubleObject", "EFloat",
            "EFloatObject"}) {
            result.put(name, Sort.REAL);
        }
        for (var name : new String[] {"EChar", "ECharacterObject", "EString"}) {
            result.put(name, Sort.STRING);
        }
        return result;
    }

    /** Graph property key under which the package data is recorded. */
    public static final String PACKAGES_KEY = "ecorePackages";
    /** Graph property key under which the classifier data is recorded. */
    public static final String TYPES_KEY = "ecoreTypes";
    /** Graph property key under which the opposite reference pairs are recorded. */
    public static final String OPPOSITES_KEY = "ecoreOpposites";
    /** Separator between the records of a metadata property value. */
    public static final String RECORD_SEP = ";";
    /** Separator between the fields of a metadata record. */
    public static final String FIELD_SEP = "|";

    /** Separator between an aspect prefix and what follows it. */
    private static final String SEP = ":";
    /** Node type label prefix. */
    private static final String TYPE_PREFIX = "type" + SEP;
    /** Label of an abstractness self-loop. */
    private static final String ABSTRACT = "abs" + SEP;
    /** Label of a sub-typing edge. */
    private static final String SUBTYPE = "sub" + SEP;
    /** Prefix of a composite (containment) edge. */
    private static final String COMPOSITE = "part" + SEP;
    /** Prefix of a nodified edge declaration. */
    private static final String EDGE = "edge" + SEP;
    /** Prefix of a node identifier declaration. */
    private static final String ID = "id" + SEP;
    /** Prefix of an attribute assignment. */
    private static final String LET = "let" + SEP;
    /** Name of the value feature of an intermediate node. */
    private static final String VALUE = "val";
    /** Name of the index attribute of an intermediate node. */
    private static final String INDEX = "index";
}
