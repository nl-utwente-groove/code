/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.control;

import static groove.view.aspect.AspectKind.UNTYPED;
import groove.algebra.Algebras;

import java.util.HashMap;
import java.util.Map;

/**
 * Class encapsulating a control type.
 * A control type is either a node type or a data type.
 * A data type has an attached signature.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlType {
    /** Constructs a control data type from a given data signature. */
    private CtrlType(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        String result;
        if (isNodeType()) {
            result = "node";
        } else {
            result = getSignature();
        }
        return result;
    }

    /** Indicates if this control type is a node type. */
    public boolean isNodeType() {
        return this.signature == null;
    }

    /** 
     * Returns the associated data signature, if this type is a data type.
     * @return the data signature, or {@code null} if this type
     * is a node type.
     */
    public String getSignature() {
        return this.signature;
    }

    /** Data signature of this type, in case it is a data type. */
    private final String signature;

    /** Returns the node type instance. */
    public static CtrlType getNodeType() {
        return nodeTypeInstance;
    }

    /** Returns the attribute type instance. */
    public static CtrlType getAttrType() {
        return attrTypeInstance;
    }

    /** 
     * Returns a data type instance for a given signature name.
     * @throws IllegalArgumentException if there is no signature with the given name 
     */
    public static CtrlType getDataType(String name) {
        CtrlType result = dataTypeMap.get(name);
        if (result == null) {
            throw new IllegalArgumentException(String.format(
                "Unknown signature '%s'", name));
        }
        return result;
    }

    /**
     * Returns a control type instance for a given type name.
     * @param name the name of the control type; either {@value #NODE_TYPE_NAME} or a data type name.
     */
    public static CtrlType getType(String name) {
        CtrlType result;
        if (NODE_TYPE_NAME.equals(name)) {
            result = getNodeType();
        } else if (ATTR_TYPE_NAME.equals(name)) {
            result = getAttrType();
        } else {
            result = getDataType(name);
        }
        return result;
    }

    /** The name of the node type. */
    static public final String NODE_TYPE_NAME = "node";
    /** The name of the (general) attribute type. */
    static public final String ATTR_TYPE_NAME = UNTYPED.getName();
    /** The singleton node type. */
    private static final CtrlType nodeTypeInstance = new CtrlType(
        NODE_TYPE_NAME);
    private static final CtrlType attrTypeInstance = new CtrlType(
        ATTR_TYPE_NAME);
    /** Static mapping from data signatures to data types. */
    private static final Map<String,CtrlType> dataTypeMap =
        new HashMap<String,CtrlType>();
    static {
        // initialise the data type map
        for (String signature : Algebras.getSigNames()) {
            dataTypeMap.put(signature, new CtrlType(signature));
        }
    }
}
