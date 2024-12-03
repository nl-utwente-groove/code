/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.control.template;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.Call;
import nl.utwente.groove.control.CtrlVar;
import nl.utwente.groove.control.NestedCall;
import nl.utwente.groove.control.Position;
import nl.utwente.groove.control.Position.Type;
import nl.utwente.groove.control.Procedure;
import nl.utwente.groove.control.term.Derivation;
import nl.utwente.groove.control.term.DerivationAttempt;
import nl.utwente.groove.control.term.Term;
import nl.utwente.groove.grammar.Action;
import nl.utwente.groove.grammar.CheckPolicy;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.util.Pair;
import nl.utwente.groove.util.ThreadPool;
import nl.utwente.groove.util.collect.NestedIterator;

/**
 * Class for constructing control automata.
 * @author Arend Rensink
 * @version $Revision$
 */
public class TemplateBuilder {
    /** Private constructor. */
    private TemplateBuilder(List<Action> properties) {
        this.properties = properties;
    }

    /** Returns property actions to be tested at each steady location. */
    private List<Action> getProperties() {
        return this.properties;
    }

    /** Indicates if there are property actions to be tested at each steady location. */
    private boolean hasProperties() {
        return !getProperties().isEmpty();
    }

    /** The property actions to be tested at each non-internal location. */
    private final List<Action> properties;

    /**
     * Construct an automata template for a given program.
     * As a side effect, all procedure templates are also constructed.
     */
    public Template build(Program prog) {
        newBuilder(prog.getMainName(), null, prog.getMain());
        for (Procedure proc : prog.getProcs().values()) {
            Builder builder = newBuilder(null, proc, proc.getTerm());
            proc.setTemplate(builder.getResult());
        }
        for (Builder builder : this.builderMap.values()) {
            builder.buildNext();
        }
        ThreadPool threads = ThreadPool.instance();
        for (final Builder builder : this.builderMap.values()) {
            threads.start(new Runnable() {
                @Override
                public void run() {
                    builder.build();
                }
            });
        }
        threads.sync();
        //        for (final Builder builder : this.builderMap.values()) {
        //            builder.build();
        //        }
        final Queue<Quotient> normQ = new ConcurrentLinkedQueue<>();
        for (final Builder template : this.builderMap.values()) {
            threads.start(new Runnable() {
                @Override
                public void run() {
                    normQ.add(computeQuotient(template.getResult()));
                }
            });
        }
        threads.sync();
        Template result = null;
        Relocation map = new Relocation();
        for (Quotient norm : normQ) {
            Template key = norm.original();
            Template value = norm.result();
            var owner = value.getOwner();
            if (owner == null) {
                result = value;
            } else {
                owner.setTemplate(value);
            }
            map.addTemplate(key, value);
            map.putAll(norm.locMap());
        }
        map.build();
        threads.shutdown();
        return result;
    }

    private Builder newBuilder(QualName name, Procedure proc, Term init) {
        Builder result = new Builder(name, proc, init);
        this.builderMap.put(result.getResult(), result);
        return result;
    }

    /** Map from previously built templates to the builder who built them. */
    private final Map<Template,Builder> builderMap = new HashMap<>();

    /**
     * Returns the switch corresponding to a given derivation,
     * first creating it if required.
     * @param loc source location for the new switch
     * @param deriv the derivation to be added
     * @return the fresh or pre-existing control switch
     */
    private NestedSwitch getExternalSwitch(Location loc, Derivation deriv) {
        Builder builder = this.builderMap.get(loc.getTemplate().get());
        NestedSwitch result = builder.getSwitch(loc, deriv);
        assert result != null;
        return result;
    }

    private class Builder {
        Builder(@Nullable QualName name, Procedure proc, Term init) {
            assert init.getTransience() == 0 : "Can't build template from transient term";
            this.result = name == null
                ? new Template(proc)
                : new Template(name);
            // set the initial location
            TermKey initKey = new TermKey(init, new HashSet<>(), new HashSet<>(), true);
            Location start = this.result.getStart();
            Map<TermKey,Location> locMap = this.locMap = new HashMap<>();
            locMap.put(initKey, start);
            Deque<TermKey> fresh = this.freshSwitch = new LinkedList<>();
            fresh.add(initKey);
            this.freshVerdict = new LinkedList<>();
        }

        /** Returns the result template.
         * This is initially empty and only filled after a call to {@link #build()}
         */
        @NonNull
        Template getResult() {
            return this.result;
        }

        private final @NonNull Template result;

        /** Indicates whether we are building a procedure template. */
        private boolean isProcedure() {
            return getResult().hasOwner();
        }

        /**
         * Builds the next attempt, plus all reachable verdict attempts.
         * Directly after construction of the builder, this will build the start attempt.
         */
        void buildNext() {
            buildAttempt(getFresh(false).poll());
            Deque<TermKey> fresh = getFresh(true);
            while (!fresh.isEmpty()) {
                buildAttempt(fresh.poll());
            }
        }

        /**
         * Builds all remaining locations and attempts.
         */
        void build() {
            Deque<TermKey> fresh = getFresh(false);
            // do the following as long as there are fresh locations
            while (!fresh.isEmpty()) {
                buildNext();
            }
            getResult().initVars();
        }

        /**
         * Builds the attempt for a location belonging to a given term key.
         */
        private void buildAttempt(TermKey termKey) {
            Location loc = getLocMap().get(termKey);
            Term term = termKey.term();
            Type locType = term.getType();
            // property switches
            Set<NestedSwitch> switches = new LinkedHashSet<>();
            // see if we need a property test
            // start and final states of procedures are exempt
            boolean isProcStartOrFinal = (loc.isStart() || locType == Type.FINAL) && isProcedure();
            if (hasProperties() && !isProcStartOrFinal && termKey.isFirstSteady()) {
                for (Action prop : getProperties()) {
                    assert prop.isProperty() && prop instanceof Rule;
                    if (((Rule) prop).getPolicy() != CheckPolicy.OFF) {
                        NestedSwitch sw = new NestedSwitch();
                        sw.push(new Switch(loc, new Call(prop), 0, loc));
                        switches.add(sw);
                    }
                }
                if (locType != Type.TRIAL || !term.getAttempt().sameVerdict()) {
                    // we need an intermediate location to go to after the property attempt
                    Location aux = getResult().addLocation(0);
                    SwitchAttempt locAttempt = new SwitchAttempt(loc, aux, aux, switches.size(),
                        switches.size(), switches.stream());
                    loc.setType(Type.TRIAL);
                    loc.setAttempt(locAttempt);
                    loc = aux;
                    switches.clear();
                }
            }
            loc.setType(locType);
            if (locType == Type.TRIAL) {
                DerivationAttempt termAttempt = term.getAttempt();
                int propertyCount = switches.size();
                // add switches for the term derivations
                for (Derivation deriv : termAttempt) {
                    // build the (possibly nested) switch
                    switches.add(getSwitch(loc, deriv));
                }
                Location succTarget = addLocation(termAttempt.onSuccess(), termKey, null);
                Location failTarget = addLocation(termAttempt.onFailure(), termKey, null);
                SwitchAttempt locAttempt = new SwitchAttempt(loc, succTarget, failTarget,
                    switches.size(), propertyCount, switches.stream());
                loc.setAttempt(locAttempt);
            }
        }

        /**
         * Adds a control location corresponding to a given symbolic term to the
         * template and auxiliary data structures, if it does not yet exist.
         * @param term the term to be added
         * @param predKey the predecessor location if this is due to a verdict; is {@code null}
         * iff {@code incoming} is non-{@code null}
         * @param incoming incoming control call leading to the location to be created;
         * may be {@code null} if there is no incoming control call but an incoming verdict
         * @return the fresh or pre-existing control location
         */
        private Location addLocation(Term term, TermKey predKey, Call incoming) {
            Map<TermKey,Location> locMap = getLocMap();
            Set<Term> predTerms = new HashSet<>();
            Set<CtrlVar> vars;
            if (incoming == null) {
                // this is due to a verdict transition
                assert predKey != null;
                predTerms.addAll(predKey.preds());
                predTerms.add(predKey.term());
                if (predTerms.contains(term)) {
                    // there is a verdict loop from this term to itself
                    // this cannot give rise to new transitions, so deadlock
                    term = term.delta(term.getTransience());
                }
                // preserve the variables of the predecessor
                vars = predKey.ctrlVars();
            } else {
                // this is due to a non-verdict transition
                assert predKey == null;
                vars = incoming.getOutVars().keySet();
            }
            TermKey key = new TermKey(term, predTerms, vars, !isProcedure() || !hasProperties());
            Location result = locMap.get(key);
            if (result == null) {
                getFresh(incoming == null).add(key);
                result = getResult().addLocation(term.getTransience());
                result.addVars(vars);
                locMap.put(key, result);
            }
            return result;
        }

        /**
         * Returns the mapping from terms to locations for a given template.
         */
        private Map<TermKey,Location> getLocMap() {
            return this.locMap;
        }

        /** For each template, a mapping from terms to locations. */
        private final Map<TermKey,Location> locMap;

        /**
         * Returns the switch corresponding to a given derivation,
         * first creating it if required.
         * @param source source location for the new switch
         * @param deriv the derivation to be added
         * @return the fresh or pre-existing control switch
         */
        NestedSwitch getSwitch(Location source, Derivation deriv) {
            Map<Derivation,NestedSwitch> switchMap = getSwitchMap(source);
            NestedSwitch result = switchMap.get(deriv);
            if (result == null) {
                // only switches from this template or initial switches can be requested
                assert source.getTemplate().get() == getResult();
                var swt = result = new NestedSwitch();
                Location target = addLocation(deriv.onFinish(), null, deriv.getOuterCall());
                swt.push(new Switch(source, deriv.getOuterCall(), deriv.getTransience(), target));
                deriv.getNested().ifPresent(nd -> {
                    Procedure caller = (Procedure) deriv.getOuterCall().getUnit();
                    Template callerTemplate = caller.getTemplate();
                    NestedSwitch nested = getExternalSwitch(callerTemplate.getStart(), nd);
                    nested.forEach(swt::push);
                });
                assert swt.getOuter().getSource() == source;
                switchMap.put(deriv, swt);
            }
            return result;
        }

        /**
         * Returns the unexplored set of symbolic locations reached by
         * a verdict or a switch, depending on the parameter.
         */
        private Deque<TermKey> getFresh(boolean verdict) {
            return verdict
                ? this.freshVerdict
                : this.freshSwitch;
        }

        /** Unexplored set of symbolic locations reached by a verdict. */
        private final Deque<TermKey> freshVerdict;

        /** Unexplored set of symbolic locations reached by a switch. */
        private final Deque<TermKey> freshSwitch;

        /**
         * Returns the mapping from derivations to switches for a given location.
         */
        private Map<Derivation,NestedSwitch> getSwitchMap(Location loc) {
            if (this.switchMaps.size() <= loc.getNumber()) {
                synchronized (this.switchMaps) {
                    for (int i = this.switchMaps.size(); i <= loc.getNumber(); i++) {
                        this.switchMaps.add(new HashMap<>());
                    }
                }
            }
            return this.switchMaps.get(loc.getNumber());
        }

        /** For each template, a mapping from derivations to switches. */
        private final List<Map<Derivation,NestedSwitch>> switchMaps = new ArrayList<>();
    }

    /** Computes the quotient of a given template under bisimilarity,
     * and returns a triple consisting of the original template, the
     * new template, and a mapping from original locations to their
     * images in the new template.
     */
    private Quotient computeQuotient(Template template) {
        Template result = template.newInstance();
        /** Mapping from locations to their records, in terms of target locations. */
        List<Record<Location>> locRecords = new ArrayList<>();
        // build the coarsest partition respecting the switch attempts
        Partition part = initPartition(template, locRecords);
        while (!part.isSingular() && refinePartition(part, locRecords)) {
            // repeat
        }
        // create map from original locations to their representatives
        Map<Location,Location> locMap = new HashMap<>();
        for (Cell cell : part) {
            // representative location of the cell
            Location repr;
            Location image;
            if (cell.contains(template.getStart())) {
                repr = template.getStart();
                image = result.getStart();
            } else {
                repr = cell.iterator().next();
                image = result.addLocation(repr.getTransience());
            }
            image.setType(repr.getType());
            for (Location loc : cell) {
                locMap.put(loc, image);
            }
        }
        return new Quotient(template, result, locMap);
    }

    /** Represents the quotient of a given template under bisimilarity,
     * consisting of the original template, the
     * new template, and a mapping from original locations to their
     * images in the new template.
     */
    private final static record Quotient(Template original, Template result,
        Map<Location,Location> locMap) {
        // empty by design
    }

    /**
     * Creates an initial partition for the locations in a given record list
     * with distinguished cells for the initial location and all locations
     * of a given transient depth.
     */
    private Partition initPartition(Template template, List<Record<Location>> locRecords) {
        Partition result = new Partition(template);
        Map<LocationKey,Cell> cellMap = new LinkedHashMap<>();
        for (Location loc : template.getLocations()) {
            // extend the locRecords list with slots up to the new location
            for (int i = locRecords.size(); i <= loc.getNumber(); i++) {
                locRecords.add(null);
            }
            locRecords.set(loc.getNumber(), computeRecord(loc));
            LocationKey key = new LocationKey(loc);
            Cell cell = cellMap.get(key);
            if (cell == null) {
                cellMap.put(key, cell = new Cell());
            }
            cell.add(loc);
        }
        result.addAll(cellMap.values());
        return result;
    }

    /**
     * Refines a given partition and returns the result.
     * The refinement is done by splitting every cell into new cells in
     * which all locations have the same success and failure targets
     * as well as the same call targets, in terms of cells of the original partition.
     */
    private boolean refinePartition(Partition orig, List<Record<Location>> locRecords) {
        boolean result = false;
        for (Cell cell : orig.iterateMultiples()) {
            Map<Record<Cell>,Cell> split = new LinkedHashMap<>();
            for (Location loc : cell) {
                Record<Cell> rec = append(locRecords.get(loc.getNumber()), orig);
                Cell locCell = split.get(rec);
                if (locCell == null) {
                    split.put(rec, locCell = new Cell());
                }
                locCell.add(loc);
            }
            result |= split.size() > 1;
            orig.addAll(split.values());
        }
        return result;
    }

    /** Computes the record of the choice and call switches for a given location. */
    private Record<Location> computeRecord(Location loc) {
        List<Location> targets = new ArrayList<>();
        Location onSuccess = null;
        Location onFailure = null;
        if (loc.isTrial()) {
            SwitchAttempt attempt = loc.getAttempt();
            for (NestedSwitch swit : attempt) {
                targets.add(swit.getOuter().onFinish());
            }
            onSuccess = attempt.onSuccess();
            onFailure = attempt.onFailure();
        }
        return new Record<>(onSuccess, onFailure, targets);
    }

    /** Converts a record pointing to locations, to a record pointing to cells. */
    private Record<Cell> append(Record<Location> record, Partition part) {
        Cell success = part.getCell(record.success());
        Cell failure = part.getCell(record.failure());
        List<Cell> targets = new ArrayList<>();
        for (Location targetLoc : record.targets()) {
            targets.add(part.getCell(targetLoc));
        }
        return new Record<>(success, failure, targets);
    }

    /** Returns the an instance of this class.
     * @param properties the property actions to be tested at each non-transient step
     */
    public static TemplateBuilder instance(List<Action> properties) {
        return new TemplateBuilder(properties);
    }

    /**
     * Type serving to distinguish freshly generated locations.
     * The distinction is made on the basis of underlying term,
     * set of verdict predecessor terms, and set of control variables.
     * @param term the term for which this is a key
     * @param preds predecessor terms along a verdict-only path
     * @param ctrlVars free control variables in the term
     * @param reuseStart flag to ensure distinctness from a procedure start location; set to {@code true}
     * for a procedure start location or if the program has no properties to be tested at each location
     */
    private static record TermKey(Term term, Set<Term> preds, Set<CtrlVar> ctrlVars,
        boolean reuseStart) {
        /** Checks if this is the first steady term among it and its predecessors. */
        private boolean isFirstSteady() {
            boolean result = this.term.isSteady();
            if (result) {
                result = this.preds.stream().allMatch(Term::isTransient);
            }
            return result;
        }
    }

    /**
     * Type serving to distinguish locations in the initial partition.
     * The distinction is made on the basis of template, final status,
     * transient depth and sets of control variables.
     */
    private static record LocationKey(Position.Type type, AttemptKey attempt, int transcience,
        List<CtrlVar> ctrlVars) {
        LocationKey(Location loc) {
            this(loc.getType(), loc.isTrial()
                ? new AttemptKey(loc.getAttempt())
                : null, loc.getTransience(), loc.getVars());
        }
    }

    /**
     * Key for attempts in the initial partition.
     * The distinction is made on the basis of the call stacks and nested locations
     * of the switch stacks in the attempt.
     */
    private static class AttemptKey extends ArrayList<Pair<NestedCall,List<Location>>> {
        AttemptKey(SwitchAttempt attempt) {
            super(attempt.size());
            for (NestedSwitch sw : attempt) {
                add(Pair.newPair(sw.getCall(), getNested(sw)));
            }
        }

        private List<Location> getNested(NestedSwitch sw) {
            List<Location> result = new ArrayList<>(sw.size() - 1);
            sw.stream().map(Switch::onFinish).forEach(result::add);
            return result;
        }
    }

    /** Local type for a cell of a partition of locations. */
    private static class Cell extends ArrayList<Location> {
        @Override
        public boolean equals(Object o) {
            return this == o;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }

    }

    /** Local type for a partition of locations. */
    private static class Partition implements Iterable<Cell> {
        Partition(Template template) {
            this.locCells = new Cell[template.size()];
        }

        void addAll(Iterable<Cell> cells) {
            for (Cell cell : cells) {
                add(cell);
            }
        }

        void add(Cell cell) {
            if (cell.size() == 1) {
                this.singles.add(cell);
            } else {
                this.multiples.add(cell);
            }
            for (Location loc : cell) {
                this.locCells[loc.getNumber()] = cell;
            }
        }

        @Override
        public Iterator<Cell> iterator() {
            return new NestedIterator<>(this.singles.iterator(), this.multiples.iterator());
        }

        /** Indicates that there are only singular cells. */
        boolean isSingular() {
            return this.multiples.isEmpty();
        }

        /** List of single-element cells. */
        private final List<Cell> singles = new ArrayList<>();

        /** Returns the current list of multiples, and reinitialises the set. */
        List<Cell> iterateMultiples() {
            List<Cell> result = this.multiples;
            this.multiples = new ArrayList<>(result.size());
            return result;
        }

        /** List of multiple-element cells. */
        private List<Cell> multiples = new LinkedList<>();

        Cell getCell(Location loc) {
            return loc == null
                ? null
                : this.locCells[loc.getNumber()];
        }

        private final Cell[] locCells;
    }

    /**
     * Convenience type to collect the targets of the verdicts and call switches
     * of a given location.
     * @param <L> type of the targets
     */
    private static record Record<L>(L success, L failure, List<L> targets) {
        // empty by design
    }
}
