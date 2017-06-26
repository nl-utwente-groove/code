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
package groove.grammar.rule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import groove.grammar.QualName;
import groove.grammar.host.HostGraph;
import groove.grammar.model.GrammarModel;
import groove.grammar.rule.MethodName.Language;
import groove.util.Exceptions;
import groove.util.parse.FormatException;

/**
 * Function to check whether a given rule match satisfies a certain condition.
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
abstract public class MatchChecker {
    /** Instantiates a match checker for a given language. */
    protected MatchChecker(Language language, QualName qualName) {
        this.language = language;
        this.qualName = qualName;
    }

    /**
     * Returns the qualified name of this method.
     */
    public QualName getQualName() {
        return this.qualName;
    }

    /** The qualified name of this method. */
    private final QualName qualName;

    /** Returns the language of this method. */
    public Language getLanguage() {
        return this.language;
    }

    /** The language of this method. */
    private final Language language;

    /** Invokes this method on a given host graph and event.
     * @throws InvocationTargetException if the method invocation results in an error
     */
    abstract public boolean invoke(HostGraph graph, RuleToHostMap anchorMap)
        throws InvocationTargetException;

    @Override
    public String toString() {
        return getLanguage().getName() + ":" + getQualName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.language.hashCode();
        result = prime * result + this.qualName.hashCode();
        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MatchChecker)) {
            return false;
        }
        MatchChecker other = (MatchChecker) obj;
        if (this.language != other.language) {
            return false;
        }
        if (!this.qualName.equals(other.qualName)) {
            return false;
        }
        return true;
    }

    /**
     * Retrieves a method with the right signature from a given class.
     * @throws FormatException if no method with the right signature exists
     */
    protected Method getMethod(Class<?> claz) throws FormatException {
        String methodName = getQualName().last();
        Method method;
        try {
            method = claz.getMethod(methodName, HostGraph.class, RuleToHostMap.class);
        } catch (NoSuchMethodException exc) {
            try {
                method = claz.getMethod(methodName, HostGraph.class);
            } catch (NoSuchMethodException exc1) {
                try {
                    method = claz.getMethod(getQualName().last());
                } catch (NoSuchMethodException | SecurityException exc2) {
                    throw new FormatException(
                        "Class '%s' does not contain method '%s' with the required signature",
                        claz.getName(), methodName);
                }
            }
        }
        if (method.getReturnType() != boolean.class) {
            throw new FormatException("Method '%s' does not return a boolean", getQualName());
        }
        return method;
    }

    /** Instantiates a checker, based on a given method name and grammar model.
     * @param method the name of the method to be instantiated
     * @param grammar source of information on where to find the model
     * @throws FormatException if no method with this name can be instantiated.
     */
    public static MatchChecker createChecker(MethodName method, GrammarModel grammar)
        throws FormatException {
        switch (method.getLanguage()) {
        case JAVA:
            return new JavaMatchChecker(method.getQualName());
        case GROOVY:
            return new GroovyMatchChecker(method.getQualName(), grammar);
        default:
            throw Exceptions.UNREACHABLE;
        }
    }

}
