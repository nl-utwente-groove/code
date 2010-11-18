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
package groove.control.parse;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

/**
 * Helper class for GCL parsing.
 * Acts as an interface between the grammar and the namespace.
 * @author Arend Rensink
 * @version $Revision $
 */
public class GCLHelper {
    /** Constructs a helper object for a given parser and namespace. */
    public GCLHelper(BaseRecognizer recogniser, NamespaceNew namespace) {
        this.recogniser = recogniser;
        this.namespace = namespace;
    }

    /** Creates a new tree with a {@link GCLNewParser#ID} token at the root,
     * of which the text is the concatenation of the children of the given tree.
     */
    CommonTree toRuleName(List<?> children) {
        String result;
        StringBuilder builder = new StringBuilder();
        for (Object token : children) {
            if (builder.length() > 0) {
                builder.append('.');
            }
            builder.append(((CommonTree) token).getText());
        }
        result = builder.toString();
        return new CommonTree(new CommonToken(GCLNewParser.ID, result));
    }

    /** 
     * Attempts to add a function declaration with a given name.
     * Checks for overlap with the previously declared functions and rules.
     * @return {@code true} if no rule or function with the name of this one was
     * already declared; {@code false} otherwise
     */
    boolean declareFunction(Tree functionTree) {
        boolean result = false;
        assert functionTree.getType() == GCLNewParser.FUNCTION
            && functionTree.getChildCount() == 2;
        String name = functionTree.getChild(0).getText();
        if (this.namespace.hasRule(name)) {
            emitErrorMessage(functionTree,
                "Duplicate name: Rule %s already defined", name);
        } else if (this.namespace.hasFunction(name)) {
            emitErrorMessage(functionTree,
                "Duplicate name: Function %s already defined", name);
        } else {
            this.namespace.addFunction(name);
            result = true;
        }
        return result;
    }

    private void emitErrorMessage(Tree marker, String message, Object... args) {
        message =
            String.format("line %d:%d %s", marker.getLine(),
                marker.getCharPositionInLine(), String.format(message, args));
        this.recogniser.emitErrorMessage(message);
        addError(message);
    }

    void addError(String message) {
        this.errors.add(message);
    }

    /**
     * Returns the (possibly empty) list of errors found during the last call of
     * the program.
     */
    List<String> getErrors() {
        return this.errors;
    }

    private final BaseRecognizer recogniser;
    /** Namespace to enter the declared functions. */
    private final NamespaceNew namespace;
    /** Flag indicating that errors were found during the current run. */
    private final List<String> errors = new ArrayList<String>();
}
