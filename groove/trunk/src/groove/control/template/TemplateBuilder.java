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

import groove.control.Call;
import groove.control.CallStack;
import groove.control.CtrlVarSet;
import groove.control.Position;
import groove.control.Position.Type;
import groove.control.Procedure;
import groove.control.term.Derivation;
import groove.control.term.DerivationAttempt;
import groove.control.term.Term;
import groove.grammar.Action;
import groove.grammar.CheckPolicy;
import groove.grammar.Rule;
import groove.util.Pair;
import groove.util.Quad;
import groove.util.Triple;
import groove.util.collect.NestedIterator;

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
import java.util.Set;

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

    /** The property actions to be tested at each non-internal location. */
    private final List<Action> properties;

    /**
     * Construct an automata template for a given program.
     * As a side effect, all procedure templates are also constructed.
     */
    public Template build(Program prog) {
        Template result = createTemplate(prog.getName(), null, prog.getMain());
        List<Template> templates = new ArrayList<Template>();
        templates.add(result);
        for (Procedure proc : prog.getProcs().values()) {
            Template template = createTemplate(null, proc, proc.getTerm());
            proc.setTemplate(template);
            templates.add(template);
        }
        for (Template template : templates) {
            build(template);
        }
        Relocation map = new Relocation();
        for (Template template : templates) {
            Template newTemplate = addQuotient(map, template);
            if (newTemplate.hasOwner()) {
                newTemplate.getOwner().setTemplate(newTemplate);
            } else {
                result = newTemplate;
            }
        }
        map.build();
        clearBuildData();
        return result;
    }

    /** Creates a new template, for the main program or a procedure,
     * and initialises it using a given term.
     */
    private Template createTemplate(String name, Procedure proc, Term init) {
        assert init.getTransience() == 0 : "Can't build template from transient term";
        Template result = name == null ? new Template(proc) : new Template(name);
        // set the initial location
        TermKey initKey = new TermKey(init, new HashSet<Term>(), new CtrlVarSet());
        Location start = result.getStart();
        getLocMap(result).put(initKey, start);
        getFresh(result).add(initKey);
        return result;
    }

    /**
     * Constructs a template from a term.
     * @param result the template to be built
     * @throws IllegalStateException if {@code init} contains procedure
     * calls with uninitialised templates
     */
    private void build(Template result) throws IllegalStateException {
        // initialise the auxiliary data structures
        Map<TermKey,Location> locMap = getLocMap(result);
        Deque<TermKey> fresh = getFresh(result);
        // do the following as long as there are fresh locations
        while (!fresh.isEmpty()) {
            TermKey next = fresh.poll();
            Location loc = locMap.get(next);
            assert loc.getType() == null;
            Term term = next.one();
            // the intended type after the optional property test
            Type locType = next.two().contains(term) ? Type.DEAD : term.getType();
            // property switches
            Set<SwitchStack> switches = new LinkedHashSet<SwitchStack>();
            // see if we need a property test
            // start states of procedures are exempt
            boolean isProcStartOrFinal = (loc.isStart() || term.isFinal()) && result.hasOwner();
            if (!isProcStartOrFinal && loc.getTransience() == 0 && next.two().isEmpty()
                && !this.properties.isEmpty()) {
                for (Action prop : this.properties) {
                    assert prop.isProperty() && prop instanceof Rule;
                    if (((Rule) prop).getPolicy() != CheckPolicy.OFF) {
                        SwitchStack sw = new SwitchStack();
                        sw.add(new Switch(loc, new Call(prop), 0, loc));
                        switches.add(sw);
                    }
                }
                if (locType != Type.TRIAL || !term.getAttempt().sameVerdict()) {
                    // we need an intermediate location to go to after the property test
                    Location aux = result.addLocation(0);
                    SwitchAttempt locAttempt = new SwitchAttempt(loc, aux, aux);
                    locAttempt.addAll(switches);
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
                    switches.add(addSwitch(loc, deriv));
                }
                Location succTarget = addLocation(result, termAttempt.onSuccess(), next, null);
                Location failTarget = addLocation(result, termAttempt.onFailure(), next, null);
                SwitchAttempt locAttempt = new SwitchAttempt(loc, succTarget, failTarget);
                locAttempt.addAll(switches);
                loc.setAttempt(locAttempt);
            }
        }
    }

    /**
     * Adds a control location corresponding to a given symbolic term to the
     * template and auxiliary data structures, if it does not yet exist.
     * @param template the template to which the location should be added
     * @param term the term to be added
     * @param predKey the predecessor location if this is due to a verdict; is {@code null}
     * iff {@code incoming} is non-{@code null}
     * @param incoming incoming control call leading to the location to be created;
     * may be {@code null} if there is no incoming control call but an incoming verdict
     * @return the fresh or pre-existing control location
     */
    private Location addLocation(Template template, Term term, TermKey predKey, Call incoming) {
        Map<TermKey,Location> locMap = getLocMap(template);
        CtrlVarSet vars = new CtrlVarSet();
        Set<Term> predTerms = new HashSet<Term>();
        if (incoming == null) {
            // this is due to a verdict transition
            assert predKey != null;
            predTerms.addAll(predKey.two());
            predTerms.add(predKey.one());
            // preserve the variables of the predecessor
            vars.addAll(locMap.get(predKey).getVars());
        } else {
            // this is due to a non-verdict transition
            assert predKey == null;
            vars.addAll(incoming.getOutVars().keySet());
        }
        TermKey key = new TermKey(term, predTerms, vars);
        Location result = locMap.get(key);
        if (result == null) {
            getFresh(template).add(key);
            result = template.addLocation(term.getTransience());
            locMap.put(key, result);
            result.setVars(vars);
        }
        return result;
    }

    /**
     * Returns the mapping from terms to locations for a given template.
     */
    private Map<TermKey,Location> getLocMap(Template template) {
        Map<TermKey,Location> result = this.locMapMap.get(template);
        if (result == null) {
            this.locMapMap.put(template, result = new HashMap<TermKey,Location>());
        }
        return result;
    }

    /** For each template, a mapping from terms to locations. */
    private final Map<Template,Map<TermKey,Location>> locMapMap =
        new HashMap<Template,Map<TermKey,Location>>();

    /**
     * Adds a switch corresponding to a given derivation to the
     * template and auxiliary data structures, if it does not yet exist.
     * @param source Source location for the new switch
     * @param deriv the derivation to be added
     * @return the fresh or pre-existing control switch
     * @throws IllegalStateException if {@code deriv} has a nested derivation
     * but the procedure does not have an initialised template
     */
    private SwitchStack addSwitch(Location source, Derivation deriv) throws IllegalStateException {
        Map<Derivation,SwitchStack> switchMap = getSwitchMap(source);
        SwitchStack result = switchMap.get(deriv);
        if (result == null) {
            result = new SwitchStack();
            Location target =
                addLocation(source.getTemplate(), deriv.onFinish(), null, deriv.getCall());
            result.add(new Switch(source, deriv.getCall(), deriv.getTransience(), target));
            if (deriv.hasNested()) {
                Procedure caller = (Procedure) deriv.getCall().getUnit();
                Template callerTemplate = caller.getTemplate();
                SwitchStack nested = addSwitch(callerTemplate.getStart(), deriv.getNested());
                result.addAll(nested);
            }
            switchMap.put(deriv, result);
        }
        assert result.getBottom().getSource() == source;
        return result;
    }

    /**
     * Returns the mapping from derivations to switches for a given template.
     */
    private Map<Derivation,SwitchStack> getSwitchMap(Location loc) {
        Map<Derivation,SwitchStack> result = this.switchMapMap.get(loc);
        if (result == null) {
            this.switchMapMap.put(loc, result = new HashMap<Derivation,SwitchStack>());
        }
        return result;
    }

    /** For each template, a mapping from derivations to switches. */
    private final Map<Location,Map<Derivation,SwitchStack>> switchMapMap =
        new HashMap<Location,Map<Derivation,SwitchStack>>();

    /**
     * Returns the mapping from terms to locations for a given template.
     */
    private Deque<TermKey> getFresh(Template template) {
        Deque<TermKey> result = this.freshMap.get(template);
        if (result == null) {
            this.freshMap.put(template, result = new LinkedList<TermKey>());
        }
        return result;
    }

    /** Unexplored set of symbolic locations per template. */
    private final Map<Template,Deque<TermKey>> freshMap = new HashMap<Template,Deque<TermKey>>();

    /** Clears the auxiliary data structures. */
    private void clearBuildData() {
        this.locMapMap.clear();
        this.switchMapMap.clear();
        this.freshMap.clear();
        this.recordMap.clear();
    }

    /** Computes the quotient of a given template under bisimilarity,
     * and adds the resulting location map to a given relocation.
     */
    private Template addQuotient(Relocation map, Template template) {
        Template result = map.addTemplate(template);
        // build the coarsest partition respecting the switch attempts
        Partition part = initPartition(template);
        while (!part.isSingular() && refinePartition(part)) {
            // repeat
        }
        // create map from original locations to their representatives
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
                map.put(loc, image);
            }
        }
        return result;
    }

    /**
     * Creates an initial partition for the locations in {@link #recordMap}
     * with distinguished cells for the initial location and all locations
     * of a given transient depth.
     */
    private Partition initPartition(Template template) {
        Partition result = new Partition(template);
        Map<LocationKey,Cell> cellMap = new LinkedHashMap<LocationKey,Cell>();
        this.recordMap.clear();
        for (Location loc : template.getLocations()) {
            for (int i = this.recordMap.size(); i <= loc.getNumber(); i++) {
                this.recordMap.add(null);
            }
            this.recordMap.set(loc.getNumber(), computeRecord(loc));
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
    private boolean refinePartition(Partition orig) {
        boolean result = false;
        for (Cell cell : orig.iterateMultiples()) {
            Map<Record<Cell>,Cell> split = new LinkedHashMap<Record<Cell>,Cell>();
            for (Location loc : cell) {
                Record<Cell> rec = append(this.recordMap.get(loc.getNumber()), orig);
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
        List<Location> targets = new ArrayList<Location>();
        Location onSuccess = null;
        Location onFailure = null;
        if (loc.isTrial()) {
            SwitchAttempt attempt = loc.getAttempt();
            for (SwitchStack swit : attempt) {
                targets.add(swit.getBottom().onFinish());
            }
            onSuccess = attempt.onSuccess();
            onFailure = attempt.onFailure();
        }
        return new Record<Location>(onSuccess, onFailure, targets);
    }

    /** Converts a record pointing to locations, to a record pointing to cells. */
    private Record<Cell> append(Record<Location> record, Partition part) {
        Cell success = part.getCell(record.getSuccess());
        Cell failure = part.getCell(record.getFailure());
        List<Cell> targets = new ArrayList<Cell>();
        for (Location targetLoc : record.getTargets()) {
            targets.add(part.getCell(targetLoc));
        }
        return new Record<Cell>(success, failure, targets);
    }

    /** Mapping from locations to their records, in terms of target locations. */
    private final List<Record<Location>> recordMap = new ArrayList<Record<Location>>();

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
    private static class TermKey extends Triple<Term,Set<Term>,CtrlVarSet> {
        TermKey(Term one, Set<Term> two, CtrlVarSet three) {
            super(one, two, three);
        }
    }

    /**
     * Type serving to distinguish locations in the initial partition.
     * The distinction is made on the basis of template, final status,
     * transient depth and sets of control variables.
     */
    private static class LocationKey extends Quad<Position.Type,AttemptKey,Integer,CtrlVarSet> {
        LocationKey(Location loc) {
            super(loc.getType(), loc.isTrial() ? new AttemptKey(loc.getAttempt()) : null,
                loc.getTransience(), new CtrlVarSet(loc.getVars()));
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
            List<Location> result = new ArrayList<Location>(sw.size() - 1);
            for (int i = 1; i < sw.size(); i++) {
                result.add(sw.get(i).onFinish());
            }
            return result;
        }
    }

    /** Local type for a cell of a partition of locations. */
    private static class Cell extends ArrayList<Location> {
        // empty
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
            return new NestedIterator<TemplateBuilder.Cell>(this.singles.iterator(),
                this.multiples.iterator());
        }

        /** Indicates that there are only singular cells. */
        boolean isSingular() {
            return this.multiples.isEmpty();
        }

        /** List of single-element cells. */
        private final List<Cell> singles = new ArrayList<Cell>();

        /** Returns the current list of multiples, and reinitialises the set. */
        List<Cell> iterateMultiples() {
            List<Cell> result = this.multiples;
            this.multiples = new ArrayList<Cell>(result.size());
            return result;
        }

        /** List of multiple-element cells. */
        private List<Cell> multiples = new LinkedList<Cell>();

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
