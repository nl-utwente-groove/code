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
package groove.algebra;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Enumeration of the currently supported signatures. 
 * @author Arend Rensink
 * @version $Revision $
 */
public enum SignatureKind {
    /** Signature kind of booleans. */
    BOOL("bool"),
    /** Signature kind of integers. */
    INT("int"),
    /** Signature kind of real numbers. */
    REAL("real"),
    /** Signature kind of strings. */
    STRING("string");

    /** Constructs a signature kind with a given name. */
    private SignatureKind(String name) {
        this.name = name;
    }

    /** Returns the name of this signature. */
    final public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return getName();
    }

    private final String name;

    /** Returns the signature kind for a given signature name. */
    public static SignatureKind getKind(String sigName) {
        return sigKindMap.get(sigName);
    }

    /** Returns the set of all known signature names. */
    static public Set<String> getNames() {
        return Collections.unmodifiableSet(sigKindMap.keySet());
    }

    /** Inverse mapping from signature names to signature kinds. */
    private static Map<String,SignatureKind> sigKindMap =
        new HashMap<String,SignatureKind>();

    static {
        for (SignatureKind kind : SignatureKind.values()) {
            sigKindMap.put(kind.getName(), kind);
        }
    }
}
