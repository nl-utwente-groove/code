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
import java.lang.reflect.Modifier;

import groove.grammar.QualName;
import groove.grammar.host.HostGraph;
import groove.grammar.rule.MethodName.Language;
import groove.util.Exceptions;
import groove.util.parse.FormatException;

/**
 * Method name in the Java language.
 * @author Arend Rensink
 * @version $Revision $
 */
public class JavaMatchChecker extends MatchChecker {
    /**
     * Creates a method name from a given Java qualified name.
     * @throws FormatException if the name does not point to a method with the right signature.
     */
    public JavaMatchChecker(QualName qualName) throws FormatException {
        super(Language.JAVA, qualName);
        Method method = null;
        String clazName = getQualName().parent()
            .toString();
        try {
            Class<?> claz = getClass().getClassLoader()
                .loadClass(clazName);
            method = getMethod(claz);
            boolean isStatic = Modifier.isStatic(method.getModifiers());
            if (!isStatic) {
                throw new FormatException("Method '%s' should be declared as static",
                    getQualName());
            }
        } catch (ClassNotFoundException exc) {
            throw new FormatException("Class '%s' does not exist", clazName);
        } catch (FormatException exc) {
            throw new FormatException(exc.getErrors());
        }
        this.method = method;
    }

    /** The actual method corresponding to the method name, if one exists. */
    private final Method method;

    @Override
    public boolean invoke(HostGraph graph, RuleToHostMap anchorMap)
        throws InvocationTargetException {
        try {
            switch (this.method.getParameterCount()) {
            case 0:
                return (Boolean) this.method.invoke(null);
            case 1:
                return (Boolean) this.method.invoke(null, graph);
            case 2:
                return (Boolean) this.method.invoke(null, graph, anchorMap);
            default:
                throw Exceptions.UNREACHABLE;
            }
        } catch (IllegalAccessException | IllegalArgumentException exc) {
            throw Exceptions.UNREACHABLE;
        }
    }

    /** Example filter method, which does not allow any match. */
    public static boolean falseFilter(HostGraph h, RuleToHostMap e) {
        return false;
    }

    /** Example filter method, which does not allow any match. */
    public static boolean errorFilter(HostGraph h, RuleToHostMap e) {
        throw new UnsupportedOperationException();
    }
}
