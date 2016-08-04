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
package groove.io.conceptual.lang.ecore;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;

import groove.io.conceptual.Id;
import groove.io.conceptual.Name;
import groove.io.conceptual.type.BoolType;
import groove.io.conceptual.type.IntType;
import groove.io.conceptual.type.RealType;
import groove.io.conceptual.type.StringType;
import groove.io.conceptual.type.Type;

@SuppressWarnings("javadoc")
public class EcoreUtil {
    // Static table of 'known' types in Ecore that can be mapped to the conceptual model
    public static final Map<String,Type> g_knownTypes = new HashMap<>();

    static {
        g_knownTypes.put("EBoolean", BoolType.instance());
        g_knownTypes.put("EBooleanObject", BoolType.instance());

        g_knownTypes.put("EBigInteger", IntType.instance());
        g_knownTypes.put("EInt", IntType.instance());
        g_knownTypes.put("EIntegerObject", IntType.instance());
        g_knownTypes.put("ELong", IntType.instance());
        g_knownTypes.put("ELongObject", IntType.instance());
        g_knownTypes.put("EShort", IntType.instance());
        g_knownTypes.put("EShortObject", IntType.instance());

        g_knownTypes.put("EChar", StringType.instance());
        g_knownTypes.put("ECharacterObject", StringType.instance());
        g_knownTypes.put("EString", StringType.instance());

        g_knownTypes.put("EDouble", RealType.instance());
        g_knownTypes.put("EDoubleObject", RealType.instance());
        g_knownTypes.put("EFloat", RealType.instance());
        g_knownTypes.put("EFloatObject", RealType.instance());
    }

    // Static table of 'known' instance types in Ecore that can be mapped to Ecore DataTypes
    // which can be mapped to the conceptual model (see EcoreType class)
    // Also used for default values
    public static final Map<String,String> g_knownInstanceTypes = new HashMap<>();

    static {
        g_knownInstanceTypes.put("java.lang.Boolean", "EBoolean");
        //g_knownInstanceTypes.put("java.lang.Boolean", "EBooleanObject");

        g_knownInstanceTypes.put("java.math.BigInteger", "EBigInteger");
        g_knownInstanceTypes.put("java.lang.Integer", "EInt");
        //g_knownInstanceTypes.put("java.lang.Integer", "EIntegerObject");
        g_knownInstanceTypes.put("java.lang.Long", "ELong");
        //g_knownInstanceTypes.put("java.lang.Long", "ELongObject");
        g_knownInstanceTypes.put("java.lang.Short", "EShort");
        //g_knownInstanceTypes.put("java.lang.Short", "EShortObject");

        g_knownInstanceTypes.put("java.lang.Character", "EChar");
        //g_knownInstanceTypes.put("java.lang.Character", "ECharacterObject");
        g_knownInstanceTypes.put("java.lang.String", "EString");

        g_knownInstanceTypes.put("java.lang.Double", "EDouble");
        //g_knownInstanceTypes.put("java.lang.Double", "EDoubleObject");
        g_knownInstanceTypes.put("java.lang.Float", "EFloat");
        //g_knownInstanceTypes.put("java.lang.Float", "EFloatObject");
    }

    /**
     * Generates an Id for the given Ecore package
     * @param pkg Package to generate Id for
     * @return The Id
     */
    public static Id idFromPackage(EPackage pkg) {
        Id nsId = Id.ROOT;
        if (pkg.getESuperPackage() != null) {
            nsId = idFromPackage(pkg.getESuperPackage());
        }
        return Id.getId(nsId, Name.getName(pkg.getName()));
    }

    /**
     * Generates an Id for the given Ecore classifier
     * @param eclass Ecore classifier to generate Id for
     * @return The Id
     */
    public static Id idFromClassifier(EClassifier eclass) {
        Id pkgId = idFromPackage(eclass.getEPackage());
        Name className = Name.getName(eclass.getName());

        return Id.getId(pkgId, className);
    }
}
