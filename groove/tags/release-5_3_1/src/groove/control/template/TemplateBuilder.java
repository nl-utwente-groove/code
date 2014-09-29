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
import groove.util.Quad;
import groove.util.Triple;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

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
        for (Procedure proc : prog.getProcs().values()) {
            build(proc.getTerm(), getTemplate(proc));
        }
        return build(prog.getName(), prog.getMain());
    }

    /**
     * Constructs an automata template for a given term.
     * As a side effect, all templates of procedures
     * recursively called from the term are also constructed.
     */
    public Template build(String name, Term init) throws IllegalStateException {
        Template result = new Template(name);
        build(init, result);
        result = normalise(result);
        for (Map.Entry<Procedure,Template> e : this.templateMap.entrySet()) {
            e.getKey().setTemplate(e.getValue());
        }
        clearBuildData();
        return result;
    }

    /**
     * Constructs a template from a term.
     * @param result the template to be built
     * @param init the term for which the template should be built
     * @throws IllegalStateException if {@code init} contains procedure
     * calls with uninitialised templates
     */
    private void build(Term init, Template result) throws IllegalStateException {
        assert init.getTransience() == 0 : "Can't build template from transient term";
        // initialise the auxiliary data structures
        Map<TermKey,Location> locMap = getLocMap(result);
        Deque<TermKey> fresh = getFresh(result);
        // set the initial location
        TermKey initKey = new TermKey(init, new HashSet<Term>(), new CtrlVarSet());
        Location start = result.getStart();
        locMap.put(initKey, start);
        this.termKeyMap.put(start, initKey);
        fresh.add(initKey);
        // do the following as long as there are fresh locations
        while (!fresh.isEmpty()) {
            TermKey next = fresh.poll();
            Location loc = locMap.get(next);
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
            this.termKeyMap.put(result, key);
        }
        return result;
    }

    /** Mapping from locations to the terms they represent. */
    private final Map<Location,TermKey> termKeyMap = new HashMap<Location,TermKey>();

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
                Template callerTemplate = getTemplate(caller);
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

    /** Returns the template being built for a given procedure. */
    private Template getTemplate(Procedure proc) {
        Template result = this.templateMap.get(proc);
        if (result == null) {
            this.templateMap.put(proc, result = new Template(proc));
        }
        return result;
    }

    /** Map from procedures to corresponding templates. */
    private final Map<Procedure,Template> templateMap = new LinkedHashMap<Procedure,Template>();

    /** Clears the auxiliary data structures. */
    private void clearBuildData() {
        this.locMapMap.clear();
        this.switchMapMap.clear();
        this.freshMap.clear();
        this.templateMap.clear();
        this.recordMap.clear();
    }

    /**
     * Computes and returns a normalised version of a given template.
     * Normalisation implies minimisation w.r.t. bisimilarity.
     */
    private Template normalise(Template orig) {
        assert orig.getStart().getTransience() == 0;
        Partition partition = computePartition(orig);
        Map<Template,Template> result = computeQuotient(partition);
        for (Template newTemplate : result.values()) {
            newTemplate.initVars();
        }
        for (Map.Entry<Procedure,Template> e : this.templateMap.entrySet()) {
            Template newTemplate = result.get(e.getValue());
            e.setValue(newTemplate);
        }
        return result.get(orig);
    }

    /** Computes a location partition for a given template,
     * as well as all templates in the template map. */
    private Partition computePartition(Template template) {
        for (Location loc : template.getLocations()) {
            this.recordMap.put(loc, computeRecord(loc));
        }
        for (Template procTemplate : this.templateMap.values()) {
            for (Location loc : procTemplate.getLocations()) {
                this.recordMap.put(loc, computeRecord(loc));
            }
        }
        Partition result = initPartition();
        int cellCount = result.size();
        int oldCellCount;
        do {
            result = refinePartition(result);
            oldCellCount = cellCount;
            cellCount = result.size();
        } while (cellCount > oldCellCount);
        return result;
    }

    /**
     * Creates an initial partition for the locations in {@link #recordMap}
     * with distinguished cells for the initial location and all locations
     * of a given transient depth.
     */
    private Partition initPartition() {
        Partition result = new Partition();
        Map<LocationKey,Cell> cellMap = new LinkedHashMap<LocationKey,Cell>();
        for (Location loc : this.recordMap.keySet()) {
            LocationKey key = new LocationKey(loc);
            Cell cell = cellMap.get(key);
            if (cell == null) {
                cellMap.put(key, cell = new Cell(loc.getTemplate()));
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
    private Partition refinePartition(Partition orig) {
        Partition result = new Partition();
        for (Cell cell : orig) {
            Map<Record<Cell>,Cell> split = new LinkedHashMap<Record<Cell>,Cell>();
            for (Location loc : cell) {
                Record<Cell> rec = append(this.recordMap.get(loc), orig);
                Cell locCell = split.get(rec);
                if (locCell == null) {
                    split.put(rec, locCell = new Cell(loc.getTemplate()));
                }
                locCell.add(loc);
            }
            result.addAll(split.values());
        }
        return result;
    }

    /** Computes the record of the choice and call switches for a given location. */
    private Record<Location> computeRecord(Location loc) {
        CallMap<Location> callMap = new CallMap<Location>();
        Location onSuccess = null;
        Location onFailure = null;
        if (loc.isTrial()) {
            SwitchAttempt attempt = loc.getAttempt();
            for (SwitchStack swit : attempt) {
                CallStack call = swit.getCallStack();
                Set<Stack<Location>> targets = callMap.get(call);
                if (targets == null) {
                    callMap.put(call, targets = new HashSet<Stack<Location>>());
                }
                Stack<Location> targetStack = new Stack<Location>();
                for (Switch sub : swit) {
                    targetStack.add(sub.onFinish());
                }
                targets.add(targetStack);
            }
            onSuccess = attempt.onSuccess();
            onFailure = attempt.onFailure();
        }
        return new Record<Location>(onSuccess, onFailure, callMap, loc.getType());
    }

    /** Converts a record pointing to locations, to a record pointing to cells. */
    private Record<Cell> append(Record<Location> record, Partition part) {
        Cell success = part.getCell(record.getSuccess());
        Cell failure = part.getCell(record.getFailure());
        CallMap<Cell> map = new CallMap<Cell>();
        for (Map.Entry<CallStack,Set<Stack<Location>>> e : record.getCallMap().entrySet()) {
            Set<Stack<Cell>> target = new HashSet<Stack<Cell>>();
            for (Stack<Location> locStack : e.getValue()) {
                Stack<Cell> cellStack = new Stack<Cell>();
                for (Location loc : locStack) {
                    cellStack.add(part.getCell(loc));
                }
                target.add(cellStack);
            }
            map.put(e.getKey(), target);
        }
        return new Record<Cell>(success, failure, map, record.getType());
    }

    /** Computes the quotient of a given template from a given partition. */
    private Map<Template,Template> computeQuotient(Partition partition) {
        Map<Template,Template> result = new HashMap<Template,Template>();
        // set of representative source locations
        Set<Location> reprSet = new HashSet<Location>();
        // canonical location map
        Map<Location,Location> locMap = new HashMap<Location,Location>();
        for (Cell cell : partition) {
            Template source = cell.getTemplate();
            Template target = getTemplate(result, source);
            // representative location of the cell
            Location repr;
            Location image;
            if (cell.contains(source.getStart())) {
                repr = source.getStart();
                image = target.getStart();
            } else {
                repr = cell.iterator().next();
                image = target.addLocation(repr.getTransience());
            }
            image.setType(repr.getType());
            reprSet.add(repr);
            for (Location loc : cell) {
                locMap.put(loc, image);
            }
        }
        // canonical switch map
        Map<Switch,Switch> switchMap = new HashMap<Switch,Switch>();
        // Add attempts to the result
        for (Location repr : reprSet) {
            if (!repr.isTrial()) {
                continue;
            }
            SwitchAttempt reprAttempt = repr.getAttempt();
            Location image = locMap.get(repr);
            Location imageSucc = locMap.get(reprAttempt.onSuccess());
            Location imageFail = locMap.get(reprAttempt.onFailure());
            SwitchAttempt imageAttempt = new SwitchAttempt(image, imageSucc, imageFail);
            for (SwitchStack reprStack : reprAttempt) {
                SwitchStack imageStack = new SwitchStack();
                for (Switch swit : reprStack) {
                    Switch switImage = getSwitch(locMap, switchMap, swit);
                    imageStack.add(switImage);
                }
                assert imageStack.getBottom().getSource() == image;
                imageAttempt.add(imageStack);
            }
            image.setAttempt(imageAttempt);
        }
        return result;
    }

    /** Mapping from locations to their records, in terms of target locations. */
    private final Map<Location,Record<Location>> recordMap =
        new LinkedHashMap<Location,Record<Location>>();

    /** Returns a canonical image for a given template. */
    private Template getTemplate(Map<Template,Template> map, Template key) {
        Template result = map.get(key);
        if (result == null) {
            result = createTemplate(key.getOwner(), key.getName());
            map.put(key, result);
        }
        return result;
    }

    /** Callback factory method for a template. */
    private Template createTemplate(Procedure owner, String name) {
        if (owner != null) {
            return new Template(owner);
        } else {
            return new Template(name);
        }
    }

    /** Returns a canonical image for a given switch. */
    private Switch getSwitch(Map<Location,Location> locMap, Map<Switch,Switch> switchMap, Switch key) {
        Switch result = switchMap.get(key);
        if (result == null) {
            Location target = locMap.get(key.onFinish());
            target.addVars(key.getCall().getOutVars().keySet());
            Location source = locMap.get(key.getSource());
            result = new Switch(source, key.getCall(), key.getTransience(), target);
            switchMap.put(key, result);
        }
        return result;
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
    private static class LocationKey extends Quad<Template,Position.Type,Integer,CtrlVarSet> {
        LocationKey(Location loc) {
            super(loc.getTemplate(), loc.getType(), loc.getTransience(), new CtrlVarSet(
                loc.getVars()));
        }
    }

    /** Local type for a cell of a partition of locations. */
    private static class Cell extends TreeSet<Location> {
        Cell(Template template) {
            this.template = template;
        }

        Template getTemplate() {
            return this.template;
        }

        private final Template template;
    }

    /** Local type for a partition of locations. */
    private static class Partition extends LinkedHashSet<Cell> {
        @Override
        public boolean add(Cell cell) {
            boolean result = super.add(cell);
            if (result) {
                for (Location loc : cell) {
                    this.cellMap.put(loc, cell);
                }
            }
            return result;
        }

        Cell getCell(Location loc) {
            return loc == null ? null : this.cellMap.get(loc);
        }

        private final Map<Location,Cell> cellMap = new TreeMap<Location,TemplateBuilder.Cell>();
    }

    /**
     * Convenience type to collect the targets of the verdicts and call switches
     * of a given location.
     * @param <L> type of the targets
     */
    private static class Record<L> extends Quad<L,L,CallMap<L>,Position.Type> {
        Record(L success, L failure, CallMap<L> callMap, Position.Type type) {
            super(success, failure, callMap, type);
        }

        L getSuccess() {
            return one();
        }

        L getFailure() {
            return two();
        }

        CallMap<L> getCallMap() {
            return three();
        }

        Type getType() {
            return four();
        }
    }

    /** Convenience type for the mapping of calls to sets of possible targets. */
    private static class CallMap<L> extends LinkedHashMap<CallStack,Set<Stack<L>>> {
        // empty
    }
}
