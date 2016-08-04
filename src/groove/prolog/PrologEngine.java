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

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import gnu.prolog.database.PrologTextLoaderError;
import gnu.prolog.io.ParseException;
import gnu.prolog.io.ReadOptions;
import gnu.prolog.io.TermReader;
import gnu.prolog.term.Term;
import gnu.prolog.term.VariableTerm;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.Interpreter.Goal;
import gnu.prolog.vm.PrologException;
import groove.prolog.util.TermConverter;
import groove.util.parse.FormatErrorSet;
import groove.util.parse.FormatException;

/**
 * Interface to the prolog engine
 *
 * @author Michiel Hendriks
 */
public class PrologEngine {
    /**
     * The used environment
     */
    private final GrooveEnvironment env;

    /**
     * The prolog interpreter
     */
    private Interpreter interpreter;

    /**
     * The current result of the query
     */
    private InternalQueryResult currentResult;

    /**
     * Private no-args constructor for the singleton instance.
     */
    public PrologEngine(GrooveEnvironment environment) throws FormatException {
        this.env = environment;
        init();
    }

    /**
     * Sets the GrooveState
     * @param grooveState     The grooveState
     */
    public void setGrooveState(GrooveState grooveState) {
        getEnvironment().setGrooveState(grooveState);
    }

    /**
     * Initialises the environment, loading an initial program.
     *
     * @throws FormatException list of syntax errors discovered during initialisation
     */
    private void init() throws FormatException {
        this.currentResult = null;
        this.interpreter = getEnvironment().createInterpreter();
        getEnvironment().runInitialization(this.interpreter);

        FormatErrorSet errors = new FormatErrorSet();
        for (PrologTextLoaderError error : getEnvironment().getLoadingErrors()) {
            errors.add("%s", error.getMessage(), error.getLine(), error.getColumn());
        }
        errors.throwException();
    }

    /**
     * Execute a new prolog query
     * @throws FormatException if there was an error compiling the term
     * @throws PrologException if there was an error executing the query
     */
    public QueryResult newQuery(String term) throws FormatException, PrologException {
        if (this.currentResult != null) {
            // terminate the previous goal
            if (this.currentResult.getReturnValue() == QueryReturnValue.SUCCESS) {
                this.interpreter.stop(this.currentResult.getGoal());
            }
        }
        ReadOptions readOpts = new ReadOptions(getEnvironment().getOperatorSet());
        try (TermReader termReader = new TermReader(new StringReader(term), getEnvironment())) {
            Term goalTerm = termReader.readTermEof(readOpts);
            Goal goal = this.interpreter.prepareGoal(goalTerm);
            this.currentResult = new InternalQueryResult(goal, term);
            this.currentResult.rawVars = readOpts.variableNames;
            return next();
        } catch (ParseException e) {
            throw new FormatException("Parse error in Prolog program: %s", e.getMessage());
        } catch (IOException e) {
            throw new IllegalStateException(e);
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
     * @throws PrologException if there was an error during execution
     */
    public QueryResult next() throws PrologException {
        if (this.currentResult == null) {
            return null;
        }
        if (this.currentResult.isLastResult()) {
            // no more results
            return null;
        }

        long startTime = System.nanoTime();
        int rc;
        rc = this.interpreter.execute(this.currentResult.goal);
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
        return this.env;
    }

    /**
     * The result object returned on {@link PrologEngine#newQuery(String)} and
     * {@link PrologEngine#next()}
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
        protected Map<String,Object> variables = new HashMap<>();

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
        @Override
        public long getExecutionTime() {
            return this.executionTime;
        }

        /**
         * @param values
         *            the variables to set
         */
        public void setVariables(Map<String,Object> values) {
            this.variables = new HashMap<>(values);
        }

        /*
         * (non-Javadoc)
         * @see groove.prolog.QueryResult#getReturnValue()
         */
        @Override
        public QueryReturnValue getReturnValue() {
            return this.returnValue;
        }

        /*
         * (non-Javadoc)
         * @see groove.prolog.QueryResult#getVariables()
         */
        @Override
        public Map<String,Object> getVariables() {
            return Collections.unmodifiableMap(this.variables);
        }

        /*
         * (non-Javadoc)
         * @see groove.prolog.QueryResult#isLastResult()
         */
        @Override
        public boolean isLastResult() {
            return this.returnValue == QueryReturnValue.SUCCESS_LAST
                || this.returnValue == QueryReturnValue.FAIL
                || this.returnValue == QueryReturnValue.HALT;
        }

        /*
         * (non-Javadoc)
         * @see groove.prolog.QueryResult#nextResult()
         */
        @Override
        public QueryResult getNextResult() {
            return this.nextResult;
        }

        /*
         * (non-Javadoc)
         * @see groove.prolog.QueryResult#previousResult()
         */
        @Override
        public QueryResult getPreviousResult() {
            return this.previousResult;
        }

        /*
         * (non-Javadoc)
         * @see groove.prolog.QueryResult#queryString()
         */
        @Override
        public String getQuery() {
            return this.query;
        }
    }
}
