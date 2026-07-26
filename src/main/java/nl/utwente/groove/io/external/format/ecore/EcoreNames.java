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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.Exceptions;
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
     * including their sub-packages.
     */
    public EcoreNames(Collection<EPackage> roots) {
        this.packages = collectPackages(roots);
        this.classifiers = collectClassifiers(this.packages);
        this.labelMap = new LinkedHashMap<>();
        this.literalMap = new LinkedHashMap<>();
        this.featureMap = new LinkedHashMap<>();
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

    /** Returns the repaired name of a structural feature, before disambiguation. */
    private static String repair(EStructuralFeature feature) {
        return IdValidator.GROOVE_ID_NON_RESERVED.repair(feature.getName());
    }

    /** Returns the dot-separated path of a given package, relative to its root package. */
    public String pathOf(EPackage pkg) {
        return String.join(".", segmentsOf(pkg));
    }

    /** Mapping from classifiers to their type labels. */
    private final Map<EClassifier,String> labelMap;
    /** Mapping from enum literals to their type labels. */
    private final Map<EEnumLiteral,String> literalMap;
    /** Mapping from structural features to their edge labels. */
    private final Map<EStructuralFeature,String> featureMap;

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
        // number of package segments prefixed to each classifier's simple name
        Map<EClassifier,Integer> depths = new LinkedHashMap<>();
        this.classifiers.forEach(c -> depths.put(c, 0));
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
        Set<String> used = new LinkedHashSet<>();
        for (var c : this.classifiers) {
            this.labelMap.put(c, disambiguate(nameOf(c, depths.get(c)), used));
        }
        // enum literals are named after their enum
        for (var c : this.classifiers) {
            if (c instanceof EEnum e) {
                for (var literal : e.getELiterals()) {
                    String name = labelFor(e) + SEPARATOR
                        + IdValidator.JAVA_ID.repair(literal.getName());
                    this.literalMap.put(literal, disambiguate(name, used));
                }
            }
        }
    }

    /** Groups the classifiers by their candidate name at the given depths. */
    private Map<String,List<EClassifier>> groupByName(Map<EClassifier,Integer> depths) {
        Map<String,List<EClassifier>> result = new LinkedHashMap<>();
        for (var c : this.classifiers) {
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
