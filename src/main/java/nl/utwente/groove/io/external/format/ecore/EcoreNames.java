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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.IdValidator;

/**
 * Naming policy of the Ecore porter.
 * <p>
 * GROOVE type labels are unqualified identifiers, whereas Ecore names are
 * qualified by their package path. The default label of a classifier is
 * therefore its simple name, repaired by {@link IdValidator} if it is not a
 * legal identifier. If two classifiers in different packages would receive the
 * same label, the colliding labels are qualified with package segments
 * ({@code pkg$Name}), taking in one more segment at a time until the collision
 * is resolved; classifiers that run out of segments are distinguished by a
 * numeric suffix. Enum literals are named {@code E$L}, after their enum.
 * <p>
 * The policy is deterministic: it only depends on the (model) order in which
 * the packages and their contents are traversed.
 * @author Arend Rensink
 */
@NonNullByDefault
public class EcoreNames {
    /**
     * Constructs a naming for the classifiers of a given set of (root) packages,
     * including their sub-packages, without mapping entries.
     */
    public EcoreNames(Collection<EPackage> roots) {
        this(roots, EcoreMapping.getDefault());
    }

    /**
     * Constructs a naming for the classifiers of a given set of (root) packages,
     * including their sub-packages, resolving the per-element entries of a
     * given mapping against them. Resolution problems are collected in
     * {@link #getErrors()}; an entry that resolves to nothing is silently
     * ignored (it may concern a metamodel other than this one).
     */
    public EcoreNames(Collection<EPackage> roots, EcoreMapping mapping) {
        this.packages = collectPackages(roots);
        this.classifiers = collectClassifiers(this.packages);
        this.labelMap = new LinkedHashMap<>();
        this.literalMap = new LinkedHashMap<>();
        this.featureMap = new LinkedHashMap<>();
        resolveOrdering(mapping);
        resolveNames(mapping);
        computeLabels();
        computeFeatureLabels();
    }

    /** Returns all packages covered by this naming, sub-packages included, in model order. */
    public List<EPackage> packages() {
        return this.packages;
    }

    private final List<EPackage> packages;

    /** Returns all classifiers covered by this naming, in model order. */
    public List<EClassifier> classifiers() {
        return this.classifiers;
    }

    private final List<EClassifier> classifiers;

    /** Returns the GROOVE type label of a given classifier.
     * @throws IllegalArgumentException if the classifier is not covered by this naming
     */
    public String labelFor(EClassifier classifier) {
        var result = this.labelMap.get(classifier);
        if (result == null) {
            throw Exceptions.illegalArg("Unknown Ecore classifier '%s'", classifier.getName());
        }
        return result;
    }

    /** Returns the GROOVE type label of a given enum literal.
     * @throws IllegalArgumentException if the literal's enum is not covered by this naming
     */
    public String labelFor(EEnumLiteral literal) {
        var result = this.literalMap.get(literal);
        if (result == null) {
            throw Exceptions.illegalArg("Unknown Ecore enum literal '%s'", literal.getName());
        }
        return result;
    }

    /** Returns the GROOVE edge label of a given structural feature.
     * Features of classes outside this naming (which can only be reached through
     * an {@code eOpposite}) are named by repair alone.
     */
    public String labelFor(EStructuralFeature feature) {
        var result = this.featureMap.get(feature);
        return result == null
            ? repair(feature)
            : result;
    }

    /**
     * Returns the repaired name of a structural feature, before disambiguation.
     * A feature label has to be usable as an attribute field name, which is a
     * Java identifier: unlike a type label, it may not contain a hyphen. A
     * hyphen is therefore mapped to an underscore, which reads better than the
     * {@code _HYPH_} the validator would insert; the mapping is not reversible,
     * but the original name is recorded in the round-trip metadata. References
     * follow the same rule although a hyphen would do them no harm: one rule is
     * easier to predict than two.
     */
    private static String repair(EStructuralFeature feature) {
        String name = feature.getName();
        if (name != null) {
            name = name.replace(HYPHEN, UNDERSCORE);
        }
        return IdValidator.JAVA_ID_NON_RESERVED.repair(name);
    }

    /** Returns the dot-separated path of a given package, relative to its root package. */
    public String pathOf(EPackage pkg) {
        return String.join(".", segmentsOf(pkg));
    }

    /** Returns the resolved ordering override of a given feature, if any. */
    public EcoreMapping.@Nullable Ordering orderingFor(EStructuralFeature feature) {
        return this.orderingMap.get(feature);
    }

    /** Mapping from features to their resolved ordering overrides. */
    private final Map<EStructuralFeature,EcoreMapping.Ordering> orderingMap
        = new LinkedHashMap<>();

    /** Returns the errors found in resolving the mapping entries against this
     * metamodel. */
    public FormatErrorSet getErrors() {
        return this.errors;
    }

    /** The errors found in resolving the mapping entries. */
    private final FormatErrorSet errors = new FormatErrorSet();

    /** Resolves the per-feature ordering entries of a mapping. */
    private void resolveOrdering(EcoreMapping mapping) {
        for (var entry : mapping.featureOrdering().entrySet()) {
            EStructuralFeature feature
                = resolveFeature(entry.getKey() + "." + EcoreMapping.ORDERING_KEY,
                                 Arrays.asList(entry.getKey().split("\\.")));
            if (feature == null) {
                continue;
            }
            if (feature.getUpperBound() >= 0 && feature.getUpperBound() <= 1) {
                this.errors
                    .add("Mapping entry '%s.%s' refers to a single-valued feature",
                         entry.getKey(), EcoreMapping.ORDERING_KEY);
            } else {
                this.orderingMap.put(feature, entry.getValue());
            }
        }
    }

    /** Resolves the naming entries of a mapping. */
    private void resolveNames(EcoreMapping mapping) {
        for (var entry : mapping.typeNames().entrySet()) {
            resolveTypeName(entry.getKey(), entry.getValue());
        }
        for (var entry : mapping.literalStyles().entrySet()) {
            String key = entry.getKey() + "." + EcoreMapping.LITERAL_STYLE_KEY;
            var classifier
                = resolveClassifier(key, Arrays.asList(entry.getKey().split("\\.")));
            if (classifier instanceof EEnum e) {
                this.styleOverrides.put(e, entry.getValue());
            } else if (classifier != null) {
                this.errors
                    .add("Mapping entry '%s' does not refer to an enum", key);
            }
        }
    }

    /** Mapping from classifiers to their overridden type names. */
    private final Map<EClassifier,String> typeNameOverrides = new LinkedHashMap<>();
    /** Mapping from enum literals to their overridden type names. */
    private final Map<EEnumLiteral,@Nullable String> literalOverrides = new LinkedHashMap<>();
    /** Mapping from enums to their overridden literal styles. */
    private final Map<EEnum,EcoreMapping.LiteralStyle> styleOverrides = new LinkedHashMap<>();

    /**
     * Resolves a typeName entry to a classifier or an enum literal, by suffix
     * match on the raw (unrepaired) qualified Ecore names, and records the
     * override. No match is silently ignored; more than one match, of either
     * kind, is an error.
     */
    private void resolveTypeName(String pathText, String value) {
        String key = pathText + "." + EcoreMapping.TYPE_NAME_KEY;
        List<String> path = Arrays.asList(pathText.split("\\."));
        List<EClassifier> classifierMatches = new ArrayList<>();
        List<EEnumLiteral> literalMatches = new ArrayList<>();
        for (var classifier : this.classifiers) {
            List<String> full = rawSegmentsOf(classifier.getEPackage());
            full.add(classifier.getName());
            if (isSuffix(path, full)) {
                classifierMatches.add(classifier);
            }
            if (classifier instanceof EEnum e) {
                for (var literal : e.getELiterals()) {
                    List<String> litFull = new ArrayList<>(full);
                    litFull.add(literal.getName());
                    if (isSuffix(path, litFull)) {
                        literalMatches.add(literal);
                    }
                }
            }
        }
        if (classifierMatches.size() + literalMatches.size() > 1) {
            this.errors.add("Ambiguous mapping entry '%s'", key);
        } else if (classifierMatches.size() == 1) {
            this.typeNameOverrides.put(classifierMatches.get(0), value);
        } else if (literalMatches.size() == 1) {
            this.literalOverrides.put(literalMatches.get(0), value);
        }
    }

    /**
     * Resolves a dotted element path to a classifier, by suffix match on the
     * raw (unrepaired) qualified Ecore names.
     * @param key the full mapping key, for error reporting
     * @param path the element path to resolve
     * @return the unique match; {@code null} if there is none (silently) or
     * more than one (with an error)
     */
    private @Nullable EClassifier resolveClassifier(String key, List<String> path) {
        List<EClassifier> matches = new ArrayList<>();
        for (var classifier : this.classifiers) {
            List<String> full = rawSegmentsOf(classifier.getEPackage());
            full.add(classifier.getName());
            if (isSuffix(path, full)) {
                matches.add(classifier);
            }
        }
        if (matches.size() > 1) {
            this.errors.add("Ambiguous mapping entry '%s'", key);
            return null;
        }
        return matches.isEmpty()
            ? null
            : matches.get(0);
    }

    /**
     * Resolves a dotted element path to a structural feature, by suffix match
     * on the raw (unrepaired) qualified Ecore names, using the declaring class.
     * @param key the full mapping key, for error reporting
     * @param path the element path to resolve
     * @return the unique match; {@code null} if there is none (silently, the
     * entry may concern another metamodel) or more than one (with an error)
     */
    private @Nullable EStructuralFeature resolveFeature(String key, List<String> path) {
        List<EStructuralFeature> matches = new ArrayList<>();
        for (var classifier : this.classifiers) {
            if (classifier instanceof EClass eClass) {
                for (var feature : eClass.getEStructuralFeatures()) {
                    List<String> full = rawSegmentsOf(eClass.getEPackage());
                    full.add(eClass.getName());
                    full.add(feature.getName());
                    if (isSuffix(path, full)) {
                        matches.add(feature);
                    }
                }
            }
        }
        if (matches.size() > 1) {
            this.errors
                .add("Ambiguous mapping entry '%s': matches %s", key,
                     matches
                         .stream()
                         .map(f -> f.getEContainingClass().getName() + "." + f.getName())
                         .collect(Collectors.joining(" and ")));
            return null;
        }
        return matches.isEmpty()
            ? null
            : matches.get(0);
    }

    /** Tests if one list of segments is a suffix of another. */
    private static boolean isSuffix(List<String> suffix, List<String> full) {
        return full.size() >= suffix.size()
            && full.subList(full.size() - suffix.size(), full.size()).equals(suffix);
    }

    /** Returns the raw (unrepaired) name segments of a package, from outermost
     * to innermost. Mapping entries are written in Ecore names, so resolution
     * must not see the repaired forms. */
    private static List<String> rawSegmentsOf(@Nullable EPackage pkg) {
        List<String> result = new ArrayList<>();
        for (var p = pkg; p != null; p = p.getESuperPackage()) {
            result.add(0, p.getName());
        }
        return result;
    }

    /** Mapping from classifiers to their type labels. */
    private final Map<EClassifier,@Nullable String> labelMap;
    /** Mapping from enum literals to their type labels. */
    private final Map<EEnumLiteral,@Nullable String> literalMap;
    /** Mapping from structural features to their edge labels. */
    private final Map<EStructuralFeature,@Nullable String> featureMap;

    /**
     * Computes the value of {@link #featureMap}.
     * Two features of one class whose names repair to the same identifier would
     * otherwise silently merge into a single type graph element, so they are
     * disambiguated by a numeric suffix, just like the classifiers. Features of
     * different classes may share a label: they are distinguished by their
     * source node type.
     */
    private void computeFeatureLabels() {
        for (var classifier : this.classifiers) {
            if (!(classifier instanceof EClass eClass)) {
                continue;
            }
            Set<String> used = new LinkedHashSet<>();
            for (var feature : eClass.getEStructuralFeatures()) {
                this.featureMap.put(feature, disambiguate(repair(feature), used));
            }
        }
    }

    /** Computes the values of {@link #labelMap} and {@link #literalMap}. */
    private void computeLabels() {
        // overridden names are claimed first, so that the derived names
        // disambiguate around them; only override-override collisions are errors
        Set<String> used = new LinkedHashSet<>();
        for (var entry : this.typeNameOverrides.entrySet()) {
            String name = entry.getValue();
            if (used.contains(name)) {
                this.errors.add("Colliding %s mapping entries for '%s'",
                                EcoreMapping.TYPE_NAME_KEY, name);
            }
            this.labelMap.put(entry.getKey(), disambiguate(name, used));
        }
        // number of package segments prefixed to each remaining classifier's simple name
        Map<EClassifier,Integer> depths = new LinkedHashMap<>();
        this.classifiers
            .stream()
            .filter(c -> !this.typeNameOverrides.containsKey(c))
            .forEach(c -> depths.put(c, 0));
        boolean changed = true;
        while (changed) {
            changed = false;
            for (var group : groupByName(depths).values()) {
                if (group.size() > 1) {
                    for (var c : group) {
                        int depth = depths.get(c);
                        if (depth < segmentsOf(c.getEPackage()).size()) {
                            depths.put(c, depth + 1);
                            changed = true;
                        }
                    }
                }
            }
        }
        // classifiers that are still ambiguous get a numeric suffix
        for (var c : this.classifiers) {
            if (!this.typeNameOverrides.containsKey(c)) {
                this.labelMap.put(c, disambiguate(nameOf(c, depths.get(c)), used));
            }
        }
        // enum literals are named after their enum, their style or their override
        for (var c : this.classifiers) {
            if (c instanceof EEnum e) {
                var style = this.styleOverrides.getOrDefault(e, EcoreMapping.LiteralStyle.QUALIFIED);
                for (var literal : e.getELiterals()) {
                    String name = this.literalOverrides.get(literal);
                    if (name == null) {
                        name = style == EcoreMapping.LiteralStyle.PLAIN
                            ? IdValidator.JAVA_ID_NON_RESERVED.repair(literal.getName())
                            : labelFor(e) + SEPARATOR
                                + IdValidator.JAVA_ID.repair(literal.getName());
                    } else if (used.contains(name)) {
                        this.errors.add("Colliding %s mapping entries for '%s'",
                                        EcoreMapping.TYPE_NAME_KEY, name);
                    }
                    this.literalMap.put(literal, disambiguate(name, used));
                }
            }
        }
    }

    /** Groups the depth-mapped classifiers by their candidate name at the given depths. */
    private Map<String,List<EClassifier>> groupByName(Map<EClassifier,Integer> depths) {
        Map<String,List<EClassifier>> result = new LinkedHashMap<>();
        for (var c : depths.keySet()) {
            result.computeIfAbsent(nameOf(c, depths.get(c)), n -> new ArrayList<>()).add(c);
        }
        return result;
    }

    /** Returns the candidate label of a classifier, qualified with the innermost
     * {@code depth} segments of its package path. */
    private String nameOf(EClassifier classifier, int depth) {
        StringBuilder result = new StringBuilder();
        var segments = segmentsOf(classifier.getEPackage());
        for (var segment : segments.subList(segments.size() - depth, segments.size())) {
            result.append(segment);
            result.append(SEPARATOR);
        }
        result.append(IdValidator.JAVA_ID_NON_RESERVED.repair(classifier.getName()));
        return result.toString();
    }

    /** Adds a name to the set of used names, appending a numeric suffix if it is taken. */
    private String disambiguate(String name, Set<String> used) {
        String result = name;
        for (int i = 2; !used.add(result); i++) {
            result = name + SEPARATOR + i;
        }
        return result;
    }

    /** Returns the (repaired) name segments of a package, from outermost to innermost. */
    private List<String> segmentsOf(@Nullable EPackage pkg) {
        List<String> result = new ArrayList<>();
        for (var p = pkg; p != null; p = p.getESuperPackage()) {
            result.add(0, IdValidator.JAVA_ID_NON_RESERVED.repair(p.getName()));
        }
        return result;
    }

    /** Collects the given packages and all their sub-packages, in model order. */
    private static List<EPackage> collectPackages(Collection<EPackage> roots) {
        List<EPackage> result = new ArrayList<>();
        for (var root : roots) {
            result.add(root);
            result.addAll(collectPackages(root.getESubpackages()));
        }
        return result;
    }

    /** Collects the classifiers of the given packages, in model order. */
    private static List<EClassifier> collectClassifiers(Collection<EPackage> packages) {
        List<EClassifier> result = new ArrayList<>();
        for (var pkg : packages) {
            result.addAll(pkg.getEClassifiers());
        }
        return result;
    }

    /** Returns the kind recorded for a given classifier in the round-trip metadata. */
    public static String kindOf(EClassifier classifier) {
        if (classifier instanceof EEnum) {
            return ENUM_KIND;
        } else if (classifier instanceof EClass c) {
            return c.isInterface()
                ? INTERFACE_KIND
                : CLASS_KIND;
        } else {
            return DATATYPE_KIND;
        }
    }

    /** Separator between name fragments in a qualified type label. */
    public static final String SEPARATOR = "$";
    /** Character that a feature name may not contain. */
    private static final char HYPHEN = '-';
    /** Character that a hyphen in a feature name is replaced by. */
    private static final char UNDERSCORE = '_';
    /** Metadata kind of an ordinary class. */
    public static final String CLASS_KIND = "class";
    /** Metadata kind of an interface. */
    public static final String INTERFACE_KIND = "interface";
    /** Metadata kind of an enumeration. */
    public static final String ENUM_KIND = "enum";
    /** Metadata kind of a data type. */
    public static final String DATATYPE_KIND = "datatype";
    /** Metadata kind of an enumeration literal. */
    public static final String LITERAL_KIND = "literal";
}
