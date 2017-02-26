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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import gnu.prolog.database.PredicateListener;
import gnu.prolog.database.PredicateUpdatedEvent;
import gnu.prolog.database.PrologTextLoader;
import gnu.prolog.database.PrologTextLoaderError;
import gnu.prolog.database.PrologTextLoaderState;
import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.CompoundTerm;
import gnu.prolog.term.CompoundTermTag;
import gnu.prolog.vm.Environment;
import gnu.prolog.vm.PrologCode;
import gnu.prolog.vm.PrologException;
import groove.io.HTMLConverter;
import groove.io.HTMLConverter.HTMLTag;
import groove.prolog.builtin.AlgebraPredicates;
import groove.prolog.builtin.GraphPredicates;
import groove.prolog.builtin.GroovePredicates;
import groove.prolog.builtin.LtsPredicates;
import groove.prolog.builtin.RulePredicates;
import groove.prolog.builtin.TransPredicates;
import groove.prolog.builtin.TypePredicates;
import groove.util.parse.FormatErrorSet;
import groove.util.parse.FormatException;

/**
 * Subclass of the normal GNU Prolog Environment, contains a reference to a
 * {@link GrooveState} instance which contains the reference to various Groove
 * structures.
 *
 * @author Michiel Hendriks
 */
public class GrooveEnvironment extends Environment {
    /**
     * Constructs a groove environment with an input stream and output stream.
     * If the streams are {@code null}, {@link #getDefaultInputStream()}
     * respectively {@link #getDefaultOutputStream()} are used.
     */
    public GrooveEnvironment(InputStream stdin, OutputStream stdout) {
        super(stdin, stdout);
        initBuiltins();
    }

    private void initBuiltins() {
        // also loads the built-in predicates
        this.prologTags.addAll(getModule().getPredicateTags());
        for (Class<GroovePredicates> predicates : GROOVE_PREDS) {
            this.toolTipMap.putAll(ensureLoaded(predicates));
        }
        this.grooveTags.addAll(getModule().getPredicateTags());
        this.grooveTags.removeAll(this.prologTags);
        if (PRINT_PROLOG_FUNCTORS) {
            printFunctors(this.prologTags);
        }
        if (PRINT_GROOVE_FUNCTORS) {
            printFunctors(this.grooveTags);
        }
    }

    /**
     * Prints a list of functor names on stdout, surrounded by
     * an XML "function" tag.
     * This can be pasted in the input of the {@link RSyntaxTextArea}
     * TokenMakerMaker, to allow syntax highlighting of predefined tags.
     */
    private void printFunctors(Collection<CompoundTermTag> tags) {
        TreeSet<String> functors = new TreeSet<>();
        HTMLTag functionTag = new HTMLConverter.HTMLTag("function");
        for (CompoundTermTag tag : tags) {
            String functor = tag.functor.value;
            if (Character.isJavaIdentifierStart(functor.charAt(0))) {
                functors.add(functor);
            }
        }
        for (String functor : functors) {
            System.out.println(functionTag.on(functor));
        }
    }

    /**
     * Loads all predicates defined in a given class.
     * Returns a map from loaded predicates to tool tip texts.
     */
    private Map<CompoundTermTag,String> ensureLoaded(Class<? extends GroovePredicates> source) {
        Map<CompoundTermTag,String> result = null;
        try {
            GroovePredicates instance = source.newInstance();
            // load the predicates
            for (Map.Entry<CompoundTermTag,String> definition : instance.getDefinitions()
                .entrySet()) {
                ensureLoaded(source, definition.getKey(), definition.getValue());
            }
            // retrieve the tool tip map
            result = instance.getToolTipMap();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(String.format("Can't load predicate class %s: %s",
                source.getSimpleName(),
                e.getMessage()));
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(String.format("Can't load predicate class %s: %s",
                source.getSimpleName(),
                e.getMessage()));
        }
        return result;
    }

    /**
     * Loads a single method definition, and tests the definition.
     */
    @SuppressWarnings("null")
    private void ensureLoaded(Class<? extends GroovePredicates> source, CompoundTermTag tag,
        String definition) {
        DefinitionListener listener = new DefinitionListener();
        getModule().addPredicateListener(listener);
        new PrologTextLoader(getPrologTextLoaderState(), new StringReader(definition));
        getModule().removePredicateListener(listener);
        Set<CompoundTermTag> predicates = listener.getPredicates();
        if (!predicates.contains(tag)) {
            throw new IllegalArgumentException(
                String.format("%s#%s_%d does not define predicate %s",
                    source.getName(),
                    tag.functor,
                    tag.arity,
                    tag));
        }
        predicates.remove(tag);
        if (!predicates.isEmpty()) {
            throw new IllegalArgumentException(
                String.format("%s#%s_%d defines additional predicates %s",
                    source.getName(),
                    tag.functor,
                    tag.arity,
                    predicates));
        }
        // tests if the predicate relies on a non-existent or inappropriate class
        String className = getModule().getDefinedPredicate(tag)
            .getJavaClassName();
        if (className != null) {
            try {
                Class<?> builtInClass = Class.forName(className);
                if (!PrologCode.class.isAssignableFrom(builtInClass)) {
                    throw new IllegalArgumentException(
                        String.format("%s#%s_%d builds in class %s that does not subtype %s",
                            source.getName(),
                            tag.functor,
                            tag.arity,
                            className,
                            PrologCode.class.getName()));
                }
                Deprecated annotation = builtInClass.getAnnotation(Deprecated.class);
                if (annotation != null) {
                    throw new IllegalArgumentException(
                        String.format("%s#%s_%d builds in deprecated class %s",
                            source.getName(),
                            tag.functor,
                            tag.arity,
                            className));
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(
                    String.format("%s#%s_%d builds in non-existing class %s",
                        source.getName(),
                        tag.functor,
                        tag.arity,
                        className));
            }
        }
    }

    /** Loads Prolog declarations from a program, given as a string. */
    public void loadProgram(String program) throws FormatException {
        PrologTextLoaderState loaderState = new PrologTextLoaderState(this) {
            {
                this.module = GrooveEnvironment.this.getModule();
            }
        };
        DefinitionListener listener = new DefinitionListener();
        getModule().addPredicateListener(listener);
        try {
            new PrologTextLoader(loaderState, new StringReader(program), null);
        } catch (Exception e) {
            throw new FormatException(e.getMessage());
        }
        getModule().removePredicateListener(listener);
        this.userTags.addAll(listener.getPredicates());
        FormatErrorSet errors = new FormatErrorSet();
        for (PrologTextLoaderError error : loaderState.getErrors()) {
            errors.add("%s", error.getMessage(), error.getLine(), error.getColumn());
        }
        errors.throwException();
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

    /** Returns the set of built-in Prolog predicates. */
    public Set<CompoundTermTag> getPrologTags() {
        return this.prologTags;
    }

    /** Returns the set of built-in Groove predicates. */
    public Set<CompoundTermTag> getGrooveTags() {
        return this.grooveTags;
    }

    /** Returns the set of user-defined predicates. */
    public Set<CompoundTermTag> getUserTags() {
        return this.userTags;
    }

    /** Removes all user-defined tags from the environment. */
    public void clearUserTags() {
        for (CompoundTermTag tag : this.userTags) {
            getModule().removeDefinedPredicate(tag);
        }
        this.userTags.clear();
    }

    /**
     * Retrieves the tool tip text for a given predicate.
     */
    public String getToolTipText(CompoundTermTag tag) {
        return this.toolTipMap.get(tag);
    }

    /** The set of built-in Prolog predicates. */
    private final Set<CompoundTermTag> prologTags = new TagSet();

    /** The set of built-in Groove predicates. */
    private final Set<CompoundTermTag> grooveTags = new TagSet();

    /** The set of user-defined predicates. */
    private final Set<CompoundTermTag> userTags = new TagSet();

    /**
     * Mapping from Groove built-in predicates to
     * corresponding tool tip text.
     */
    private final Map<CompoundTermTag,String> toolTipMap = new HashMap<>();

    /**
     * The current groove state
     */
    private GrooveState grooveState;

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
     * Atom term "no_groove_environment"
     */
    public final static AtomTerm NO_GROOVE_ENV = AtomTerm.get("no_groove_environment");

    /** Classes of predefined Groove predicates. */
    @SuppressWarnings("unchecked")
    public static final Class<GroovePredicates>[] GROOVE_PREDS =
        new Class[] {AlgebraPredicates.class, GraphPredicates.class, LtsPredicates.class,
            RulePredicates.class, TransPredicates.class, TypePredicates.class};

    /**
     * Flag that causes all Prolog functor names to be printed on stdout.
     * The result can be included in the TokenMakerMaker input for the
     * {@link RSyntaxTextArea} syntax highlighting.
     */
    private static final boolean PRINT_PROLOG_FUNCTORS = false;
    /**
     * Flag that causes all Groove functor names to be printed on stdout.
     * The result can be included in the TokenMakerMaker input for the
     * {@link RSyntaxTextArea} syntax highlighting.
     */
    private static final boolean PRINT_GROOVE_FUNCTORS = false;

    /** Alphabetically and arity-wise ordered set of compound tags. */
    private static class TagSet extends TreeSet<CompoundTermTag> {
        public TagSet() {
            super(new Comparator<CompoundTermTag>() {
                @Override
                public int compare(CompoundTermTag o1, CompoundTermTag o2) {
                    int rc = o1.functor.value.compareTo(o2.functor.value);
                    if (rc == 0) {
                        rc = o1.arity - o2.arity;
                    }
                    return rc;
                }
            });
        }
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

        private Set<CompoundTermTag> predicates = new HashSet<>();
    }
}
