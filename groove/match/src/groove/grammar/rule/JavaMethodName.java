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

import javax.xml.crypto.NoSuchMechanismException;

import groove.grammar.QualName;
import groove.grammar.host.HostGraph;
import groove.transform.RuleEvent;
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

    /** Lazily creates the reflected method. */
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
                    method = claz.getMethod(getQualName().last(), HostGraph.class, RuleEvent.class);
                } catch (NoSuchMethodException exc) {
                    try {
                        method = claz.getMethod(getQualName().last(), HostGraph.class);
                    } catch (NoSuchMethodException exc1) {
                        method = claz.getMethod(getQualName().last());
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

    private Optional<Method> method;

    @Override
    public boolean exists() {
        return getMethod().isPresent();
    }

    @Override
    public boolean invoke(HostGraph graph, RuleEvent match) throws NoSuchMethodException {
        Method method = getMethod().orElseThrow(() -> new NoSuchMechanismException(
            String.format("Method '%s' does not exist", getQualName())));
        try {
            return (Boolean) method.invoke(null, graph, match);
        } catch (IllegalAccessException | IllegalArgumentException
            | InvocationTargetException exc) {
            throw Exceptions.UNREACHABLE;
        }
    }

    /** Example filter method, which does not allow any match. */
    public static boolean falseFilter(HostGraph h, RuleEvent e) {
        return false;
    }
}
