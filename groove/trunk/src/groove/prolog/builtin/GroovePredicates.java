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
package groove.prolog.builtin;

import static groove.io.HTMLConverter.HTML_LINEBREAK;
import gnu.prolog.term.CompoundTermTag;
import groove.annotation.Help;
import groove.annotation.Signature;
import groove.annotation.ToolTipBody;
import groove.annotation.ToolTipPars;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract superclass for classes containing derived predicate declarations.
 * The predicate declarations should be added in the form of method declarations
 * annotated with {@link Signature} and optionally {@link ToolTipBody} and {@link ToolTipPars}.
 * The declarations themselves consist of a series of calls of {@link #s(String)}.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class GroovePredicates {
    /** Adds a line to the declaration currently being built up. */
    protected void s(String line) {
        this.text.append(line);
        this.text.append('\n');
    }

    /** 
     * Invokes all methods annotated by {@link Signature}, and 
     * returns the strings that have been built up by successive
     * invocations of {@link #s(String)}.
     */
    public Map<CompoundTermTag,String> getDefinitions() {
        if (this.definitions == null) {
            this.toolTipMap = new HashMap<>();
            this.definitions = new HashMap<>();
            for (Method method : getClass().getMethods()) {
                if (method.isAnnotationPresent(Signature.class)) {
                    try {
                        this.text = new StringBuilder();
                        method.invoke(this);
                        this.definitions.put(getTag(method.getName()),
                            this.text.toString());
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException(e.getMessage());
                    } catch (InvocationTargetException e) {
                        throw new IllegalStateException(e.getMessage());
                    }
                    addToolTipText(method);
                }
            }
        }
        return this.definitions;
    }

    /**
     * Returns a mapping from the derived predicates defined in this class
     * to corresponding tool tip text.
     */
    public Map<CompoundTermTag,String> getToolTipMap() {
        if (this.toolTipMap == null) {
            getDefinitions();
        }
        return this.toolTipMap;
    }

    /** 
     * Constructs the HMTL-formatted tool tip for a given predicate,
     * by trying to construct this from annotations of a given object.
     */
    private void addToolTipText(Method method) {
        CompoundTermTag tag = getTag(method.getName());
        Signature sig = method.getAnnotation(Signature.class);
        ToolTipBody tip = method.getAnnotation(ToolTipBody.class);
        ToolTipPars param = method.getAnnotation(ToolTipPars.class);
        this.toolTipMap.put(tag,
            newCreateToolTipText(tag, sig, tip, param).getTip());
    }

    /** Converts a method name into a tag. */
    private CompoundTermTag getTag(String methodName) {
        int arityPos = methodName.lastIndexOf('_');
        if (arityPos < 0) {
            throw new IllegalArgumentException(
                String.format(
                    "Predicate method name %s should end on '_i' (where i is the arity)",
                    methodName));
        }
        String functorName = methodName.substring(0, arityPos);
        int arity;
        try {
            arity = Integer.parseInt(methodName.substring(arityPos + 1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                String.format(
                    "Predicate method name %s should end on '_i' (where i is the arity)",
                    methodName));
        }
        return CompoundTermTag.get(functorName, arity);
    }

    /** The text being built up by successive invocations of {@link #s(String)}. */
    private StringBuilder text;
    /** Mapping from predicates to the definition strings in the corresponding methods. */
    private Map<CompoundTermTag,String> definitions;
    /** Mapping from predicates to tool tip text. */
    private Map<CompoundTermTag,String> toolTipMap;

    /** 
     * Constructs the HMTL-formatted tool tip for a given predicate,
     * by trying to construct this from given annotation values.
     */
    static public Help newCreateToolTipText(CompoundTermTag tag,
            Signature sigAnn, ToolTipBody toolTip, ToolTipPars param) {
        Help result = new Help();
        if (sigAnn != null) {
            String name = tag.functor.toString();
            int arity = tag.arity;
            String[] sigValue = sigAnn.value();
            if (sigValue.length <= arity) {
                throw new IllegalStateException(
                    String.format(
                        "Malformed annotation %s for %s/%s: insufficient arguments",
                        sigAnn, name, arity));
            }
            // construct the (multi-line) header
            StringBuilder header = new StringBuilder();
            for (int i = arity; i < sigValue.length; i++) {
                String io = sigValue[i];
                if (io.length() != arity) {
                    throw new IllegalStateException(
                        String.format(
                            "Malformed annodation %s for %s/%s: incorrect IO spec %s",
                            sigAnn, name, arity, io));
                }
                StringBuilder sigText = new StringBuilder();
                sigText.append(name);
                sigText.append('(');
                for (int p = 0; p < arity; p++) {
                    if (p > 0) {
                        sigText.append(", ");
                    }
                    sigText.append(Help.it(io.charAt(p) + sigValue[p]));
                }
                sigText.append(')');
                if (header.length() > 0) {
                    header.append(HTML_LINEBREAK);
                }
                header.append(sigText);
            }
            result.setHeader(header.toString());
            if (toolTip != null) {
                result.setBody(toolTip.value());
            }
            // collect the parameter names
            List<String> parNames = new ArrayList<>();
            for (int p = 0; p < arity; p++) {
                parNames.add(sigValue[p]);
            }
            result.setParNames(parNames);
            if (param != null) {
                result.setPars(param.value());
            }
        }
        return result;
    }
}
