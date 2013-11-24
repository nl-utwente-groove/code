/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.algebra;

import groove.annotation.Help;

import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Helper class for algebra manipulation.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Algebras {
    /** 
     * Checks if all generic types used in the signature declaration
     * are actually themselves signatures.
     */
    static private void checkSignatureConsistency() {
        for (SignatureKind sigKind : SignatureKind.values()) {
            for (TypeVariable<?> type : sigKind.getSignatureClass().getTypeParameters()) {
                String typeName = type.getName().toLowerCase();
                if (SignatureKind.getKind(typeName) == null) {
                    throw new IllegalArgumentException(String.format(
                        "Type '%s' not declared by any signature", typeName));
                }
            }
        }
    }

    static {
        checkSignatureConsistency();
    }

    /**
     * Returns a syntax helper mapping from syntax items
     * to (possibly {@code null}) tool tips.
     */
    public static Map<String,String> getDocMap() {
        if (docMap == null) {
            docMap = computeDocMap();
        }
        return docMap;
    }

    private static Map<String,String> computeDocMap() {
        Map<String,String> result = new TreeMap<String,String>();
        for (SignatureKind sigKind : SignatureKind.values()) {
            Map<String,String> sigMap = new HashMap<String,String>(tokenMap);
            for (Method method : sigKind.getSignatureClass().getMethods()) {
                sigMap.put("Q" + method.getName(),
                    sigKind + ":" + method.getName());
                Help help = Help.createHelp(method, sigMap);
                if (help != null) {
                    result.put(help.getItem(), help.getTip());
                }
            }
        }
        return result;
    }

    /** Syntax helper map, from syntax items to associated tool tips. */
    private static Map<String,String> docMap;
    /**
     * Mapping from keywords in syntax descriptions to corresponding text.
     */
    static private final Map<String,String> tokenMap;

    static {
        tokenMap = new HashMap<String,String>();
        tokenMap.put("LPAR", "(");
        tokenMap.put("RPAR", ")");
        tokenMap.put("COMMA", ",");
        tokenMap.put("COLON", ":");
        tokenMap.put("TRUE", "true");
        tokenMap.put("FALSE", "false");
    }
}
