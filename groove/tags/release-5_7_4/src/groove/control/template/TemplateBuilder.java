/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.control.template;

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

import groove.control.Call;
import groove.control.CallStack;
import groove.control.CtrlVar;
import groove.control.Position;
import groove.control.Position.Type;
import groove.control.Procedure;
import groove.control.term.Derivation;
import groove.control.term.DerivationAttempt;
import groove.control.term.Term;
import groove.grammar.Action;
import groove.grammar.CheckPolicy;
import groove.grammar.QualName;
import groove.grammar.Rule;
import groove.util.Pair;
import groove.util.Quad;
import groove.util.ThreadPool;
import groove.util.Triple;
import groove.util.collect.NestedIterator;

/**
 * Class for constructing control automata.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TemplateBuilder {
    /** Private constructor. */
    private TemplateBuilder(List<Action> properties) {
        this.properties = properties;
    }

    /** Returns property actions to be tested at each non-internal location. */
    private List<Action> getProperties() {
        return this.properties;
    }

    /** The property actions to be tested at each non-internal location. */
    private final List<Action> properties;

    /**
     * Construct an automata template for a given program.
     * As a side effect, all procedure templates are also constructed.
     */
    public Template build(Program prog) {
        newBuilder(prog.getMainName(), null, prog.getMain());
        for (Procedure proc : prog.getProcs()
            .values()) {
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
        final Queue<Triple<Template,Template,Map<Location,Location>>> normQ =
            new ConcurrentLinkedQueue<>();
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
        for (Triple<Template,Template,Map<Location,Location>> norm : normQ) {
            Template key = norm.one();
            Template value = norm.two();
            if (value.hasOwner()) {
                value.getOwner()
                    .setTemplate(value);
            } else {
                result = value;
            }
            map.addTemplate(key, value);
            map.putAll(norm.three());
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

    private final Map<Template,Builder> builderMap = new HashMap<>();

    /**
     * Returns the switch corresponding to a given derivation,
     * first creating it if required.
     * @param loc source location for the new switch
     * @param deriv the derivation to be added
     * @return the fresh or pre-existing control switch
     */
    private SwitchStack getExternalSwitch(Location loc, Derivation deriv) {
        Builder builder = this.builderMap.get(loc.getTemplate());
        SwitchStack result = builder.getSwitch(loc, deriv);
        assert result != null;
        return result;
    }

    private class Builder {
        Builder(QualName name, Procedure proc, Term init) {
            assert init.getTransience() == 0 : "Can't build template from transient term";
            this.result = name == null ? new Template(proc) : new Template(name);
            // set the initial location
            TermKey initKey = new TermKey(init, new HashSet<Term>(), new HashSet<CtrlVar>());
            Location start = this.result.getStart();
            Map<TermKey,Location> locMap = this.locMap = new HashMap<>();
            locMap.put(initKey, start);
            Deque<TermKey> fresh = this.freshSwitch = new LinkedList<>();
            fresh.add(initKey);
            this.freshVerdict = new LinkedList<>();
        }

        Template getResult() {
            return this.result;
        }

        private final Template result;

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
        }

        /**
         * Builds the attempt for a location belonging to a given term key.
         */
        private void buildAttempt(TermKey next) {
            Location loc = getLocMap().get(next);
            assert loc.getType() == null;
            Term term = next.one();
            Type locType = term.getType();
            // property switches
            Set<SwitchStack> switches = new LinkedHashSet<>();
            // see if we need a property test
            // start states of procedures are exempt
            boolean isProcStartOrFinal =
                (loc.isStart() || term.isFinal()) && getResult().hasOwner();
            if (!isProcStartOrFinal && loc.getTransience() == 0 && next.two()
                .isEmpty() && !getProperties().isEmpty()) {
                for (Action prop : getProperties()) {
                    assert prop.isProperty() && prop instanceof Rule;
                    if (((Rule) prop).getPolicy() != CheckPolicy.OFF) {
                        SwitchStack sw = new SwitchStack();
                        sw.add(new Switch(loc, new Call(prop), 0, loc));
                        switches.add(sw);
                    }
                }
                if (locType != Type.TRIAL || !term.getAttempt()
                    .sameVerdict()) {
                    // we need an intermediate location to go to after the property test
                    Location aux = getResult().addLocation(0);
                    SwitchAttempt locAttempt =
                        new SwitchAttempt(loc, aux, aux, switches.size(), switches.stream());
                    loc.setType(Type.TRIAL);
                    loc.setAttempt(locAttempt);
                    loc = aux;
                    switches.clear();
                }
            }
            loc.setType(locType);
            if (locType == Type.TRIAL) {
                DerivationAttempt termAttempt = term.getAttempt();
                // add switches for the term derivations
                for (Derivation deriv : termAttempt) {
                    // build the (possibly nested) switch
                    switches.add(getSwitch(loc, deriv));
                }
                Location succTarget = addLocation(termAttempt.onSuccess(), next, null);
                Location failTarget = addLocation(termAttempt.onFailure(), next, null);
                SwitchAttempt locAttempt = new SwitchAttempt(loc, succTarget, failTarget,
                    switches.size(), switches.stream());
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
                predTerms.addAll(predKey.two());
                predTerms.add(predKey.one());
                if (predTerms.contains(term)) {
                    // there is a verdict loop from this term to itself
                    // this cannot give rise to new transitions, so deadlock
                    term = term.delta(term.getTransience());
                }
                // preserve the variables of the predecessor
                vars = predKey.three();
            } else {
                // this is due to a non-verdict transition
                assert predKey == null;
                vars = incoming.getOutVars()
                    .keySet();
            }
            TermKey key = new TermKey(term, predTerms, vars);
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
        SwitchStack getSwitch(Location source, Derivation deriv) {
            Map<Derivation,SwitchStack> switchMap = getSwitchMap(source);
            SwitchStack result = switchMap.get(deriv);
            if (result == null) {
                // only switches from this template or initial switches can be requested
                assert source.getTemplate() == getResult();
                result = new SwitchStack();
                Location target = addLocation(deriv.onFinish(), null, deriv.getCall());
                result.add(new Switch(source, deriv.getCall(), deriv.getTransience(), target));
                if (deriv.hasNested()) {
                    Procedure caller = (Procedure) deriv.getCall()
                        .getUnit();
                    Template callerTemplate = caller.getTemplate();
                    SwitchStack nested =
                        getExternalSwitch(callerTemplate.getStart(), deriv.getNested());
                    result.addAll(nested);
                }
                assert result.getBottom()
                    .getSource() == source;
                switchMap.put(deriv, result);
            }
            return result;
        }

        /**
         * Returns the unexplored set of symbolic locations reached by
         * a verdict or a switch, depending on the parameter.
         */
        private Deque<TermKey> getFresh(boolean verdict) {
            return verdict ? this.freshVerdict : this.freshSwitch;
        }

        /** Unexplored set of symbolic locations reached by a verdict. */
        private final Deque<TermKey> freshVerdict;

        /** Unexplored set of symbolic locations reached by a switch. */
        private final Deque<TermKey> freshSwitch;

        /**
         * Returns the mapping from derivations to switches for a given location.
         */
        private Map<Derivation,SwitchStack> getSwitchMap(Location loc) {
            if (this.switchMaps.size() <= loc.getNumber()) {
                synchronized (this.switchMaps) {
                    for (int i = this.switchMaps.size(); i <= loc.getNumber(); i++) {
                        this.switchMaps.add(new HashMap<Derivation,SwitchStack>());
                    }
                }
            }
            return this.switchMaps.get(loc.getNumber());
        }

        /** For each template, a mapping from derivations to switches. */
        private final List<Map<Derivation,SwitchStack>> switchMaps = new ArrayList<>();
    }

    /** Computes the quotient of a given template under bisimilarity,
     * and returns a triple consisting of the original template, the
     * new template, and a mapping from original locations to their
     * images in the new template.
     */
    private Triple<Template,Template,Map<Location,Location>> computeQuotient(Template template) {
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
                repr = cell.iterator()
                    .next();
                image = result.addLocation(repr.getTransience());
            }
            image.setType(repr.getType());
            for (Location loc : cell) {
                locMap.put(loc, image);
            }
        }
        return Triple.newTriple(template, result, locMap);
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
            for (SwitchStack swit : attempt) {
                targets.add(swit.getBottom()
                    .onFinish());
            }
            onSuccess = attempt.onSuccess();
            onFailure = attempt.onFailure();
        }
        return new Record<>(onSuccess, onFailure, targets);
    }

    /** Converts a record pointing to locations, to a record pointing to cells. */
    private Record<Cell> append(Record<Location> record, Partition part) {
        Cell success = part.getCell(record.getSuccess());
        Cell failure = part.getCell(record.getFailure());
        List<Cell> targets = new ArrayList<>();
        for (Location targetLoc : record.getTargets()) {
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
     */
    private static class TermKey extends Triple<Term,Set<Term>,Set<CtrlVar>> {
        TermKey(Term one, Set<Term> two, Set<CtrlVar> three) {
            super(one, two, three);
        }
    }

    /**
     * Type serving to distinguish locations in the initial partition.
     * The distinction is made on the basis of template, final status,
     * transient depth and sets of control variables.
     */
    private static class LocationKey extends Quad<Position.Type,AttemptKey,Integer,List<CtrlVar>> {
        LocationKey(Location loc) {
            super(loc.getType(), loc.isTrial() ? new AttemptKey(loc.getAttempt()) : null,
                loc.getTransience(), loc.getVars());
        }
    }

    /**
     * Key for attempts in the initial partition.
     * The distinction is made on the basis of the call stacks and nested locations
     * of the switch stacks in the attempt.
     */
    private static class AttemptKey extends ArrayList<Pair<CallStack,List<Location>>> {
        AttemptKey(SwitchAttempt attempt) {
            super(attempt.size());
            for (SwitchStack sw : attempt) {
                add(Pair.newPair(sw.getCallStack(), getNested(sw)));
            }
        }

        private List<Location> getNested(SwitchStack sw) {
            List<Location> result = new ArrayList<>(sw.size() - 1);
            for (int i = 1; i < sw.size(); i++) {
                result.add(sw.get(i)
                    .onFinish());
            }
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
            return loc == null ? null : this.locCells[loc.getNumber()];
        }

        private final Cell[] locCells;
    }

    /**
     * Convenience type to collect the targets of the verdicts and call switches
     * of a given location.
     * @param <L> type of the targets
     */
    private static class Record<L> extends Triple<L,L,List<L>> {
        Record(L success, L failure, List<L> targets) {
            super(success, failure, targets);
        }

        L getSuccess() {
            return one();
        }

        L getFailure() {
            return two();
        }

        List<L> getTargets() {
            return three();
        }
    }
}
