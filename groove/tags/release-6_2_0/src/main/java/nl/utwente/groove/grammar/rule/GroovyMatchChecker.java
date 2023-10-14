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
package nl.utwente.groove.grammar.rule;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.codehaus.groovy.control.CompilationFailedException;
import org.eclipse.jdt.annotation.NonNullByDefault;

import groovy.lang.GroovyClassLoader;
import nl.utwente.groove.grammar.ModuleName;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.GroovyModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.grammar.rule.MethodName.Language;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Method name in a Groovy script
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public class GroovyMatchChecker extends MatchChecker {
    /**
     * Creates a method name from a given Java qualified name.
     */
    public GroovyMatchChecker(QualName qualName, GrammarModel grammar) throws FormatException {
        super(Language.GROOVY, qualName);
        ModuleName scriptName = qualName.parent();
        if (!(scriptName instanceof QualName)) {
            throw new FormatException("Groovy method name '%s' does not include script name",
                qualName);
        }
        GroovyModel model =
            (GroovyModel) grammar.getResource(ResourceKind.GROOVY, (QualName) scriptName);
        if (model == null) {
            throw new FormatException("Groovy script '%s' does not exist");
        }
        try (GroovyClassLoader loader = new GroovyClassLoader()) {
            Class<?> scriptClass = loader.parseClass(model.getProgram());
            this.target = scriptClass.getConstructor()
                .newInstance();
            this.method = getMethod(scriptClass);
        } catch (IOException exc) {
            throw new FormatException("Error while loading Groovy script: %s", exc.getMessage());
        } catch (CompilationFailedException exc) {
            throw new FormatException("Failure to compile Groovy script '%s': %s", scriptName, exc);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException | NoSuchMethodException | SecurityException exc) {
            throw new FormatException("Groovy script '%s' defines non-instantiable class");
        } catch (FormatException exc) {
            throw new FormatException(exc.getErrors());
        }
    }

    /** Checker method. */
    private final Method method;
    /** Instance of the Groovy class on which {@link #method} should be invoked. */
    private final Object target;

    @Override
    public boolean invoke(HostGraph graph, RuleToHostMap anchorMap)
        throws InvocationTargetException {
        try {
            switch (this.method.getParameterCount()) {
            case 0:
                return (Boolean) this.method.invoke(this.target);
            case 1:
                return (Boolean) this.method.invoke(this.target, graph);
            case 2:
                return (Boolean) this.method.invoke(this.target, graph, anchorMap);
            default:
                throw Exceptions.UNREACHABLE;
            }
        } catch (IllegalAccessException | IllegalArgumentException exc) {
            throw Exceptions.UNREACHABLE;
        }
    }
}
