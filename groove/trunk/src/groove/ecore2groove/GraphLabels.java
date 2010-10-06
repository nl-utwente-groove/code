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

    /**
     * Method to get a string for a label to represent an EClassifier
     * in a graph.
     * @return string to use for label of representation
     */
    public static String getLabel(EClassifier classifier) {
        String label;

        if (isRootEPackage(classifier.getEPackage())) {
            return "type:" + classifier.getName();
        } else {
            label = "$" + classifier.getName();
        }

        EPackage curEPackage = classifier.getEPackage();

        while (!isRootEPackage(curEPackage)) {
            label = curEPackage.getName() + "$" + label;
            curEPackage = curEPackage.getESuperPackage();
        }

        return "type:" + label;
    }

    /**
     * Method to get a string for a label to represent an EAtribute or
     * EReference in a graph
     * @return string to use for label of representation
     */
    public static String getLabel(EStructuralFeature feature) {
        String label;

        label = getLabel(feature.getEContainingClass());
        label = label + "$" + feature.getName();

        return label;
    }

    /**
     * Method to get a string for a label representing an EDataType. Returns
     * an empty string if serialization of the EDataType is not supported.
     * @return string to use for label of representation
     */
    public static String getLabel(EDataType datatype) {
        String label = "";

        String type = datatype.getInstanceTypeName();
        if (type == "int" || type == "java.lang.Integer"
            || type == "java.math.BigInteger" || type == "byte"
            || type == "java.lang.Byte" || type == "short"
            || type == "java.lang.Short" || type == "long"
            || type == "java.lang.Long") {
            label = "type:int";
        } else if (type == "boolean" || type == "java.lang.Boolean") {
            label = "type:bool";
        } else if (type == "java.math.BigDecimal" || type == "double"
            || type == "java.lang.Double" || type == "float"
            || type == "java.lang.Float") {
            label = "type:real";
        } else if (type == "byte[]" || type == "char"
            || type == "java.lang.Character" || type == "java.util.Date"
            || type == "java.lang.String") {
            label = "type:string";
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
        String label = "";

        String type = datatype.getInstanceTypeName();
        if (type == "int" || type == "java.lang.Integer"
            || type == "java.math.BigInteger" || type == "byte"
            || type == "java.lang.Byte" || type == "short"
            || type == "java.lang.Short" || type == "long"
            || type == "java.lang.Long") {
            if (value == null) {
                label = "int:";
            } else {
                /*int intVal;
                try {
                    intVal = new Integer(value.toString());
                } catch (NumberFormatException e) {
                    System.out.println(value
                        + " out of bounds of Integer range,"
                        + " using MIN_VALUE or MAX_VALUE instead");
                    if (new Integer(value.toString().substring(0, 2)) < 0) {
                        intVal = Integer.MIN_VALUE;
                    } else {
                        intVal = Integer.MAX_VALUE;
                    }
                }
                label = "int:" + intVal;*/
                label = "int:" + value.toString();
            }
        } else if (type == "boolean" || type == "java.lang.Boolean") {
            if (value == null) {
                label = "bool:";
            } else {
                label = "bool:" + value.toString();
            }
        } else if (type == "java.math.BigDecimal" || type == "double"
            || type == "java.lang.Double" || type == "float"
            || type == "java.lang.Float") {
            if (value == null) {
                label = "real:";
            } else {
                /*float realVal;
                try {
                    realVal = new Float(value.toString());
                } catch (Exception e) {
                    System.out.println(value + " out of bounds of Float range,"
                        + " using MIN_VALUE or MAX_VALUE instead");
                    if (new Float(value.toString().substring(0, 2)) < 0) {
                        realVal = Float.MIN_VALUE;
                    } else {
                        realVal = Float.MAX_VALUE;
                    }
                }
                label = "real:" + realVal;*/
                label = "real:" + value.toString();
            }
        } else if (type == "byte[]" || type == "char"
            || type == "java.lang.Character" || type == "java.util.Date"
            || type == "java.lang.String") {
            if (value == null) {
                label = "string:";
            } else {
                label = "string:\"" + value.toString() + "\"";
            }
        } else if (datatype.eClass().getName().equals("EEnum")) {
            label = getLabel((EEnum) datatype);
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

        if (aPackage.getESuperPackage() == null) {
            return true;
        }
        return false;
    }

    /**
     * Returns a string for a label to represent an EEnumLiteral
     * @return string to use for label of representation
     */
    public static String getLabel(EEnumLiteral literal) {

        return "flag:" + literal.getLiteral();
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
            label = "$" + classifier.getName();
        }

        EPackage curEPackage = classifier.getEPackage();

        while (!isRootEPackage(curEPackage)) {
            label = curEPackage.getName() + "$" + label;
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
        label = label + "$" + feature.getName();

        return label;
    }

}
