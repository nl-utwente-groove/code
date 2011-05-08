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

import gnu.prolog.io.ReadOptions;
import gnu.prolog.io.TermReader;
import gnu.prolog.term.CompoundTermTag;
import gnu.prolog.term.Term;
import gnu.prolog.term.VariableTerm;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.Interpreter.Goal;
import gnu.prolog.vm.PrologException;
import groove.graph.Graph;
import groove.lts.GraphState;
import groove.prolog.builtin.AlgebraPredicates;
import groove.prolog.builtin.GraphPredicates;
import groove.prolog.builtin.GroovePredicates;
import groove.prolog.builtin.LtsPredicates;
import groove.prolog.builtin.RulePredicates;
import groove.prolog.builtin.TransPredicates;
import groove.prolog.builtin.TypePredicates;
import groove.prolog.exception.GroovePrologException;
import groove.prolog.exception.GroovePrologLoadingException;
import groove.prolog.util.TermConverter;

import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Interface to the prolog engine
 * 
 * @author Michiel Hendriks
 */
public class PrologQuery {
    /** Classes of predefined Groove predicates. */
    @SuppressWarnings("unchecked")
    public static final Class<GroovePredicates>[] GROOVE_PREDS = new Class[] {
        AlgebraPredicates.class, GraphPredicates.class, LtsPredicates.class,
        RulePredicates.class, TransPredicates.class, TypePredicates.class};

    /**
     * The graph that will be queried.
     */
    protected Graph<?,?> graph;

    /**
     * The graph state that will be queried.
     */
    protected GraphState graphState;

    /**
     * Will be true when the interface has been initialized.
     */
    protected boolean initialized;

    /**
     * The used environment
     */
    protected GrooveEnvironment env;

    /**
     * The prolog interpreter
     */
    protected Interpreter interpreter;

    /**
     * The current result of the query
     */
    protected InternalQueryResult currentResult;

    /**
     * The current "groove" state to work with
     */
    protected GrooveState grooveState;

    /**
     * The stream to use as default output stream
     */
    protected OutputStream userOutput;

    /**
     * Private no-args constructor for the singleton instance.
     */
    private PrologQuery() {
        // empty by design
    }

    /**
     * @param userOutput
     *            the userOutput to set
     */
    public void setUserOutput(OutputStream userOutput) {
        this.userOutput = userOutput;
    }

    /**
     * Sets the GrooveState
     * @param grooveState     The grooveState
     */
    public void setGrooveState(GrooveState grooveState) {
        this.grooveState = grooveState;
        if (this.env != null) {
            this.env.setGrooveState(this.grooveState);
        }
    }

    /**
     * @return the initialized
     */
    public boolean isInitialized() {
        return this.initialized;
    }

    /**
     * Initialize the environment
     */
    public void init() throws GroovePrologLoadingException {
        init(null, null);
    }

    /**
     * Initialize the environment
     * 
     * @param initStream
     *            Additional code to process during the loading of the
     *            environment. Typically used to load user code
     * @param streamName
     *            The name to use for the provided stream, is used when creating
     *            errors. It's best to use the name of a file.
     */
    public void init(Reader initStream, String streamName)
        throws GroovePrologLoadingException {
        if (this.initialized) {
            return;
        }
        this.initialized = true;
        this.currentResult = null;
        getEnvironment();
        if (initStream != null) {
            this.env.loadStream(initStream, streamName);
        }
        this.interpreter = this.env.createInterpreter();
        this.env.runInitialization(this.interpreter);

        if (!this.env.getLoadingErrors().isEmpty()) {
            throw new GroovePrologLoadingException(this.env.getLoadingErrors());
        }
    }

    /**
     * Execute a new prolog query
     */
    public QueryResult newQuery(String term) throws GroovePrologException {
        if (!this.initialized) {
            init();
        }
        if (this.currentResult != null) {
            // terminate the previous goal
            if (this.currentResult.getReturnValue() == QueryReturnValue.SUCCESS) {
                this.interpreter.stop(this.currentResult.getGoal());
            }
        }
        ReadOptions readOpts = new ReadOptions(this.env.getOperatorSet());
        readOpts.operatorSet = this.env.getOperatorSet();
        TermReader termReader =
            new TermReader(new StringReader(term), this.env);
        try {
            Term goalTerm = termReader.readTermEof(readOpts);
            Goal goal = this.interpreter.prepareGoal(goalTerm);
            this.currentResult = new InternalQueryResult(goal, term);
            this.currentResult.rawVars = readOpts.variableNames;
            return next();
        } catch (Exception e) {
            throw new GroovePrologException(e);
        }
    }

    /**
     * @return The current result of the prolog engine
     */
    public QueryResult current() {
        return this.currentResult;
    }

    /**
     * Get the next results
     * 
     * @return Null if there is no next result
     */
    public QueryResult next() throws GroovePrologException {
        if (this.currentResult == null) {
            return null;
        }
        if (this.currentResult.isLastResult()) {
            // no more results
            return null;
        }

        long startTime = System.nanoTime();
        int rc;
        try {
            rc = this.interpreter.execute(this.currentResult.goal);
        } catch (PrologException e) {
            throw new GroovePrologException(e);
        }
        long stopTime = System.nanoTime();
        if (this.currentResult.getReturnValue() != QueryReturnValue.NOT_RUN) {
            this.currentResult = new InternalQueryResult(this.currentResult);
        }
        this.currentResult.setReturnValue(QueryReturnValue.fromInt(rc));
        this.currentResult.setExecutionTime(stopTime - startTime);
        if (this.currentResult.getReturnValue() != QueryReturnValue.FAIL
            && this.currentResult.getReturnValue() != QueryReturnValue.HALT) {
            this.currentResult.setVariables(TermConverter.convert(this.currentResult.rawVars));
        }
        return this.currentResult;
    }

    /**
     * @return True if there is a next result
     */
    public boolean hasNext() {
        return this.currentResult != null && !this.currentResult.isLastResult();
    }

    /**
     * @return The last return code
     */
    public QueryReturnValue lastReturnValue() {
        if (this.currentResult != null) {
            return this.currentResult.getReturnValue();
        }
        return QueryReturnValue.NOT_RUN;
    }

    /**
     * Create the prolog environment. This will initialize the environment in
     * the standard groove environment. It can be used when you need to make
     * changes to the environment before loading user code.
     */
    public GrooveEnvironment getEnvironment() {
        if (this.env == null) {
            this.env = new GrooveEnvironment(null, this.userOutput);
            this.env.setGrooveState(this.grooveState);
            this.prologTags.addAll(this.env.getModule().getPredicateTags());
            for (Class<GroovePredicates> predicates : GROOVE_PREDS) {
                this.toolTipMap.putAll(this.env.ensureLoaded(predicates));
            }
            this.grooveTags.addAll(this.env.getModule().getPredicateTags());
            this.grooveTags.removeAll(this.prologTags);
        }
        return this.env;
    }

    /** Returns the set of built-in Prolog predicates. */
    public Set<CompoundTermTag> getPrologTags() {
        return this.prologTags;
    }

    /** Returns the set of built-in Groove predicates. */
    public Set<CompoundTermTag> getGrooveTags() {
        return this.grooveTags;
    }

    /**
     * Retrieves the tool tip text for a given predicate from a map,
     * creating it first if necessary.
     */
    public String getToolTipText(CompoundTermTag tag) {
        return this.toolTipMap.get(tag);
    }

    /** The set of built-in Prolog predicates. */
    private final Set<CompoundTermTag> prologTags =
        new HashSet<CompoundTermTag>();

    /** The set of built-in Groove predicates. */
    private final Set<CompoundTermTag> grooveTags =
        new HashSet<CompoundTermTag>();

    /**
     * Mapping from Groove built-in predicates to 
     * corresponding tool tip text.
     */
    private final Map<CompoundTermTag,String> toolTipMap =
        new HashMap<CompoundTermTag,String>();

    /** Returns the singleton instance of this class. */
    public static PrologQuery instance() {
        return instance;
    }

    private static final PrologQuery instance = new PrologQuery();

    /**
     * The result object returned on {@link PrologQuery#newQuery(String)} and
     * {@link PrologQuery#next()}
     * 
     * @author Michiel Hendriks
     */
    protected static class InternalQueryResult implements QueryResult {
        /**
         * The goal
         */
        protected Goal goal;

        /**
         * The query
         */
        protected String query = "";

        /**
         * The return value
         */
        protected QueryReturnValue returnValue = QueryReturnValue.NOT_RUN;

        /**
         * The execution time
         */
        protected long executionTime = -1;

        /**
         * The previous result
         */
        protected InternalQueryResult previousResult;

        /**
         * The next result
         */
        protected InternalQueryResult nextResult;

        /**
         * The variables
         */
        protected Map<String,Object> variables = new HashMap<String,Object>();

        /**
         * Unprocessed variables
         */
        protected Map<String,VariableTerm> rawVars;

        /**
         * Constructs an internal query result
         */
        protected InternalQueryResult(Goal goal, String query) {
            this.goal = goal;
            this.query = query;
        }

        /**
         * Constructs an internal query result
         */
        protected InternalQueryResult(InternalQueryResult previous) {
            this.previousResult = previous;
            this.previousResult.nextResult = this;
            this.goal = this.previousResult.goal;
            this.query = this.previousResult.query;
            this.rawVars = this.previousResult.rawVars;
        }

        /**
         * Gets the goal
         */
        protected Goal getGoal() {
            return this.goal;
        }

        /**
         * @param value
         *            the executionTime to set
         */
        protected void setExecutionTime(long value) {
            this.executionTime = value;
        }

        /**
         * @param value
         *            the returnValue to set
         */
        protected void setReturnValue(QueryReturnValue value) {
            this.returnValue = value;
        }

        /*
         * (non-Javadoc)
         * @see groove.prolog.QueryResult#getExecutionTime()
         */
        public long getExecutionTime() {
            return this.executionTime;
        }

        /**
         * @param values
         *            the variables to set
         */
        public void setVariables(Map<String,Object> values) {
            this.variables = new HashMap<String,Object>(values);
        }

        /*
         * (non-Javadoc)
         * @see groove.prolog.QueryResult#getReturnValue()
         */
        public QueryReturnValue getReturnValue() {
            return this.returnValue;
        }

        /*
         * (non-Javadoc)
         * @see groove.prolog.QueryResult#getVariables()
         */
        public Map<String,Object> getVariables() {
            return Collections.unmodifiableMap(this.variables);
        }

        /*
         * (non-Javadoc)
         * @see groove.prolog.QueryResult#isLastResult()
         */
        public boolean isLastResult() {
            return this.returnValue == QueryReturnValue.SUCCESS_LAST
                || this.returnValue == QueryReturnValue.FAIL
                || this.returnValue == QueryReturnValue.HALT;
        }

        /*
         * (non-Javadoc)
         * @see groove.prolog.QueryResult#nextResult()
         */
        public QueryResult getNextResult() {
            return this.nextResult;
        }

        /*
         * (non-Javadoc)
         * @see groove.prolog.QueryResult#previousResult()
         */
        public QueryResult getPreviousResult() {
            return this.previousResult;
        }

        /*
         * (non-Javadoc)
         * @see groove.prolog.QueryResult#queryString()
         */
        public String getQuery() {
            return this.query;
        }
    }
}
