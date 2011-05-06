/*
 * Groove Prolog Interface
 * Copyright (C) 2009 Michiel Hendriks, University of Twente
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package groove.prolog;

import gnu.prolog.database.PredicateListener;
import gnu.prolog.database.PredicateUpdatedEvent;
import gnu.prolog.database.PrologTextLoader;
import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.CompoundTerm;
import gnu.prolog.term.CompoundTermTag;
import gnu.prolog.vm.Environment;
import gnu.prolog.vm.PrologException;
import groove.prolog.builtin.GroovePredicates;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Subclass of the normal GNU Prolog Environment, contains a reference to a
 * {@link GrooveState} instance which contains the reference to various Groove
 * structures.
 * 
 * @author Michiel Hendriks
 */
public class GrooveEnvironment extends Environment {
    /**
     * Atom term "no_groove_environment"
     */
    public final static AtomTerm NO_GROOVE_ENV =
        AtomTerm.get("no_groove_environment");

    /**
     * Generic error to throw when the groove environment is missing
     */
    public static void invalidEnvironment() throws PrologException {
        throw new PrologException(new CompoundTerm(PrologException.errorTag,
            new CompoundTerm(CompoundTermTag.get("system_error", 1),
                GrooveEnvironment.NO_GROOVE_ENV, PrologException.errorAtom),
            PrologException.errorAtom), null);
    }

    /**
     * The current groove state
     */
    protected GrooveState grooveState;

    /**
      * No-args constructor
      */
    public GrooveEnvironment() {
        super();
    }

    /**
     * Constructs a groove environment with an inputstream and outputstream
     */
    public GrooveEnvironment(InputStream stdin, OutputStream stdout) {
        super(stdin, stdout);
    }

    /**
     * @return the grooveState
     */
    public GrooveState getGrooveState() {
        return this.grooveState;
    }

    /**
     * @param grooveState
     *            the grooveState to set
     */
    public void setGrooveState(GrooveState grooveState) {
        this.grooveState = grooveState;
    }

    /** 
     * Loads all derived predicates from a given class.
     * Returns a map from loaded predicates to tool tip texts. 
     */
    public Map<CompoundTermTag,String> ensureLoaded(
            Class<? extends GroovePredicates> source) {
        Map<CompoundTermTag,String> result = null;
        try {
            GroovePredicates instance = source.newInstance();
            // load the predicates
            for (Map.Entry<CompoundTermTag,String> definition : instance.getDefinitions().entrySet()) {
                ensureLoaded(source, definition.getKey(), definition.getValue());
            }
            // retrieve the tool tip map
            result = instance.getToolTipMap();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(String.format(
                "Can't load predicate class %s: %s", source.getSimpleName(),
                e.getMessage()));
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(String.format(
                "Can't load predicate class %s: %s", source.getSimpleName(),
                e.getMessage()));
        }
        return result;
    }

    /**
     * Loads the definitions from a single method in a given class.
     */
    private void ensureLoaded(Class<? extends GroovePredicates> source,
            CompoundTermTag tag, String definition) {
        DefinitionListener listener = new DefinitionListener();
        getModule().addPredicateListener(listener);
        new PrologTextLoader(getPrologTextLoaderState(), new StringReader(
            definition));
        getModule().removePredicateListener(listener);
        Set<CompoundTermTag> predicates = listener.getPredicates();
        if (!predicates.contains(tag)) {
            throw new IllegalArgumentException(String.format(
                "%s#%s_%d does not define predicate %s",
                source.getSimpleName(), tag.functor, tag.arity, tag));
        }
        predicates.remove(tag);
        if (!predicates.isEmpty()) {
            throw new IllegalArgumentException(String.format(
                "%s#%s_%d defines additional predicates %s",
                source.getSimpleName(), tag.functor, tag.arity, predicates));
        }
    }

    /** Loads Prolog declarations from a named stream. */
    public synchronized void loadStream(Reader stream, String streamName) {
        if (isInitialized()) {
            throw new IllegalStateException(
                "no files can be loaded after inializtion was run");
        }
        new PrologTextLoader(getPrologTextLoaderState(), stream, streamName);
    }

    /** Listener that collects predicate definitions. */
    private static class DefinitionListener implements PredicateListener {
        @Override
        public void predicateUpdated(PredicateUpdatedEvent evt) {
            this.predicates.add(evt.getTag());
        }

        /** Returns the predicates that this listener has been informed of. */
        public Set<CompoundTermTag> getPredicates() {
            return this.predicates;
        }

        private Set<CompoundTermTag> predicates =
            new HashSet<CompoundTermTag>();
    }
}
