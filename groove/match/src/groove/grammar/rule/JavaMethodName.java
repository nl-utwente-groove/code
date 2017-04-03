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
import java.util.Optional;

import groove.grammar.QualName;
import groove.grammar.host.HostGraph;
import groove.util.Exceptions;

/**
 * Method name in the Java language.
 * @author Arend Rensink
 * @version $Revision $
 */
public class JavaMethodName extends MethodName {
    /**
     * Creates a method name from a given Java qualified name.
     */
    public JavaMethodName(QualName qualName) {
        super(Language.JAVA, qualName);
    }

    /** Lazily looks up and returns the reflected method. */
    private Optional<Method> getMethod() {
        Optional<Method> result = this.method;
        if (result == null) {
            String clazName = getQualName().parent()
                .toString();
            try {
                Class<?> claz = getClass().getClassLoader()
                    .loadClass(clazName);
                Method method;
                try {
                    method =
                        claz.getMethod(getQualName().last(), HostGraph.class, RuleToHostMap.class);
                    this.parameterCount = 2;
                } catch (NoSuchMethodException exc) {
                    try {
                        method = claz.getMethod(getQualName().last(), HostGraph.class);
                        this.parameterCount = 1;
                    } catch (NoSuchMethodException exc1) {
                        method = claz.getMethod(getQualName().last());
                        this.parameterCount = 0;
                    }
                }
                boolean isStatic = Modifier.isStatic(method.getModifiers());
                if (isStatic && method.getReturnType() == boolean.class) {
                    result = Optional.of(method);
                } else {
                    result = Optional.empty();
                }
            } catch (ClassNotFoundException | SecurityException | NoSuchMethodException exc) {
                result = Optional.empty();
            }
            this.method = result;
        }
        return result;
    }

    /** The actual method corresponding to the method name, if one exists. */
    private Optional<Method> method;
    /** Number of parameters that the method takes. */
    private int parameterCount;

    @Override
    public boolean exists() {
        return getMethod().isPresent();
    }

    @Override
    public boolean invoke(HostGraph graph, RuleToHostMap anchorMap)
        throws UnsupportedOperationException, InvocationTargetException {
        Method method = getMethod().orElseThrow(() -> new UnsupportedOperationException(
            String.format("Method '%s' does not exist", getQualName())));
        try {
            switch (this.parameterCount) {
            case 0:
                return (Boolean) method.invoke(null);
            case 1:
                return (Boolean) method.invoke(null, graph);
            case 2:
                return (Boolean) method.invoke(null, graph, anchorMap);
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
