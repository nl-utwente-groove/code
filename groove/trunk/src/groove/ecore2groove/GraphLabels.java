/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id$
 */
package groove.ecore2groove;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Class with static methods that return string values that are use for
 * representing Ecore elements in graphs.
 * @author Stefan Teijgeler
 */
public class GraphLabels {

    private static final String DOLLAR = "$";

    private static final String BOOL_PREFIX = "bool:";
    private static final String FLAG_PREFIX = "flag:";
    private static final String INT_PREFIX = "int:";
    private static final String REAL_PREFIX = "real:";
    private static final String STRING_PREFIX = "string:";
    private static final String TYPE_PREFIX = "type:";

    private static final Set<String> boolTypes = new HashSet<String>();
    private static final Set<String> intTypes = new HashSet<String>();
    private static final Set<String> realTypes = new HashSet<String>();
    private static final Set<String> stringTypes = new HashSet<String>();

    static {
        boolTypes.add("boolean");
        boolTypes.add("java.lang.Boolean");

        intTypes.add("int");
        intTypes.add("byte");
        intTypes.add("short");
        intTypes.add("long");
        intTypes.add("java.lang.Integer");
        intTypes.add("java.lang.Byte");
        intTypes.add("java.lang.Short");
        intTypes.add("java.lang.Long");
        intTypes.add("java.math.BigInteger");

        realTypes.add("float");
        realTypes.add("double");
        realTypes.add("java.lang.Float");
        realTypes.add("java.lang.Double");
        realTypes.add("java.math.BigDecimal");

        stringTypes.add("char");
        stringTypes.add("byte[]");
        stringTypes.add("java.lang.String");
        stringTypes.add("java.lang.Character");
        stringTypes.add("java.util.Date");
    }

    /**
     * Method to get a string for a label to represent an EClassifier
     * in a graph.
     * @return string to use for label of representation
     */
    public static String getLabel(EClassifier classifier) {
        String label;

        if (isRootEPackage(classifier.getEPackage())) {
            return TYPE_PREFIX + classifier.getName();
        } else {
            label = DOLLAR + classifier.getName();
        }

        EPackage curEPackage = classifier.getEPackage();

        while (!isRootEPackage(curEPackage)) {
            label = curEPackage.getName() + DOLLAR + label;
            curEPackage = curEPackage.getESuperPackage();
        }

        return TYPE_PREFIX + label;
    }

    /**
     * Method to get a string for a label to represent an EAtribute or
     * EReference in a graph
     * @return string to use for label of representation
     */
    public static String getLabel(EStructuralFeature feature) {
        String label;

        label = getLabel(feature.getEContainingClass());
        label = label + DOLLAR + feature.getName();

        return label;
    }

    /**
     * Method to get a string for a label representing an EDataType. Returns
     * an empty string if serialization of the EDataType is not supported.
     * @return string to use for label of representation
     */
    public static String getLabel(EDataType datatype) {
        String label;

        String type = datatype.getInstanceTypeName();
        if (intTypes.contains(type)) {
            label = TYPE_PREFIX + "int";
        } else if (boolTypes.contains(type)) {
            label = TYPE_PREFIX + "bool";
        } else if (realTypes.contains(type)) {
            label = TYPE_PREFIX + "real";
        } else if (stringTypes.contains(type)) {
            label = TYPE_PREFIX + "string";
        } else {
            label = "";
        }

        return label;
    }

    /**
     * Method to get a string for a label to represent a value of an EDataType.
     * If serialization of the EDataType is not supported, returns and empty
     * string.
     * @return string to use for label of representation
     */
    public static String getLabel(EDataType datatype, Object value) {
        String label;

        String type = datatype.getInstanceTypeName();
        if (intTypes.contains(type)) {
            if (value == null) {
                label = INT_PREFIX;
            } else {
                label = INT_PREFIX + value.toString();
            }
        } else if (boolTypes.contains(type)) {
            if (value == null) {
                label = BOOL_PREFIX;
            } else {
                label = BOOL_PREFIX + value.toString();
            }
        } else if (realTypes.contains(type)) {
            if (value == null) {
                label = REAL_PREFIX;
            } else {
                label = REAL_PREFIX + value.toString();
            }
        } else if (stringTypes.contains(type)) {
            if (value == null) {
                label = STRING_PREFIX;
            } else {
                label = STRING_PREFIX + "\"" + value.toString() + "\"";
            }
        } else if ("EEnum".equals(datatype.eClass().getName())) {
            label = getLabel((EEnum) datatype);
        } else {
            label = "";
        }

        return label;
    }

    /**
     * Method to get a string for a label to represent an EEnum. Representation
     * is same as for EClassifier, but this method is required else the
     * EDataType version catches the method call.
     * @return string to use for label of representation
     */
    public static String getLabel(EEnum aEnum) {
        return getLabel((EClassifier) aEnum);
    }

    /**
     * Returns whether or not an EPackage is the root of an Ecore model
     * @param aPackage The EPackage that is root or not
     * @return true if the EPackage is the root
     */
    private static boolean isRootEPackage(EPackage aPackage) {
        return aPackage.getESuperPackage() == null;
    }

    /**
     * Returns a string for a label to represent an EEnumLiteral
     * @return string to use for label of representation
     */
    public static String getLabel(EEnumLiteral literal) {
        return FLAG_PREFIX + literal.getLiteral();
    }

    /**
     * Method to get a string to represent an EClassifier in a graph, but 
     * without the type: prefix, so the string can be used for other
     * identification purposes.
     * @return string to use for label of representation
     */
    public static String getLabelNoType(EClassifier classifier) {
        String label;

        if (isRootEPackage(classifier.getEPackage())) {
            return classifier.getName();
        } else {
            label = DOLLAR + classifier.getName();
        }

        EPackage curEPackage = classifier.getEPackage();

        while (!isRootEPackage(curEPackage)) {
            label = curEPackage.getName() + DOLLAR + label;
            curEPackage = curEPackage.getESuperPackage();
        }

        return label;
    }

    /**
     * Method to get a string to represent an EEnum in a graph, but 
     * without the type: prefix, so the string can be used for other
     * identification purposes.
     * @return string to use for label of representation
     */
    public static String getLabelNoType(EEnum aEnum) {
        return getLabelNoType((EClassifier) aEnum);
    }

    /**
     * Method to get a string to represent an EStructuralefature in a graph,
     * but without the type: prefix, so the string can be used for other
     * identification purposes.
     * @return string to use for label of representation
     */
    public static String getLabelNoType(EStructuralFeature feature) {
        String label;

        label = getLabelNoType(feature.getEContainingClass());
        label = label + DOLLAR + feature.getName();

        return label;
    }

}
