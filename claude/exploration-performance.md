# Exploration performance review

Code-reading review (2026-09-20) of the state-space exploration hot path, looking for
performance headroom at every level: algorithms, data structures, allocation, and
JIT-friendliness. Nothing here has been implemented or measured; the note is a
consolidated finding list to plan work from. One item found on the way, a null
dereference in `Proof.equals`, is already fixed on master (`9ff9fb4d5`) and is not
repeated here.

Paths below are relative to `src/main/java/nl/utwente/groove/`. Each finding carries an
expected impact (high/medium/low), an effort estimate (small/medium/large) and how
confident the reviewer is that the code is really on the hot path. All of it was
verified by reading; inferences are marked as such.

## Method and caveats

Five parallel reviews, one per area: matching (`match`, `match/plan`), isomorphism
checking (`graph/iso`, the GTS state set), LTS and state storage (`lts`,
`grammar/host` delta machinery, `util/collect`), transformation and control (`transform`,
`control/instance`, `algebra`) and the exploration driver (`explore`, listeners,
cross-cutting utilities). The highest-impact claims (dead confluent-diamond shortcut,
always-on `Reporter`, `Factory.get` monitor, `CHECK_IMAGES`, certifier array sizing,
eager certificate map, refinement loop, weak certifier reference) were re-verified in
the source by the orchestrating session.

**There is no exploration benchmark in the repository.** The only timing instrument is
`util.Reporter`, whose output is printed at `Generator -v 3` only; `test/performance`
holds collection micro-benchmarks, and `ExplorationTest` asserts state counts without
timing. So every impact rating below is an expectation, not a measurement. See
[Building a throughput harness](#building-a-throughput-harness) for what the first
implementation step should be.

## Suggested order of attack

1. Build the throughput harness, so the rest can be measured.
2. Section 1 (always-on instrumentation and locks): two-line changes in the innermost
   loops, no design risk.
3. Section 2 (dead or broken optimisations): the confluent-diamond key fix and the
   certifier reference; each restores an optimisation that exists but does not work.
4. Section 3 (costs scaling with the wrong quantity): the certifier array, the eager
   certificate map, the interning edge probe. Each is small and independently measurable.
5. Section 4 (allocation churn) as opportunity permits, largest expected payoff first:
   `Search` reuse, NAC context maps, algebra reflection, `Valuator` lambdas, match-set
   sizing.
6. Section 5 (structural) needs design discussions, not commits.

Any change to matching or certificates must pass `DeterminismTest` and the
`grammar-smoke` state counts (see the gates at the end).

## 1. Always-on instrumentation and uncontended locks

Biased locking is gone since JDK 15, so an uncontended `synchronized` is a real CAS pair
and blocks inlining of the method that carries it.

**1.1 `Reporter.start()/stop()` are synchronized, read the clock twice, and are always
on.** High / small / high. `util/Reporter.java:101-150`; flags `REPORT` and
`TIME_METHODS` hard-coded `true` at `:362-368`. Call sites bracket every
`Search.find()` in the matcher (`match/plan/PlanSearchStrategy.java:447,499`, i.e. once
per match produced and once per terminating failure), every transition and every fresh
state in `lts/MatchApplier.java:68,98,100,112`, every iso comparison
(`graph/iso/IsoChecker.java:99,129,146,196,286,296,307,309`) and every certificate or
partition computation (`graph/iso/CertificateStrategy.java:137,140,331,337,363,370`).
Each `start()` takes the instance monitor, calls `currentTimeMillis()` for the
measurement and once more to charge the static `reportTime` (measuring the measuring),
and `stop()` does the same: at least eight clock reads and four monitor round trips per
generated state before any real work. The data is discarded unless `-v 3`.
Suggestion: make `REPORT` a `static final boolean` initialised from a system property
(default false; C2 still constant-folds a static final set in `<clinit>`), move
`synchronized` inside the flag check or drop it (the counters are single-threaded and
the static `reportTime` is racy anyway), and drop the second clock read.

**1.2 `CHECK_IMAGES` runs a graph containment query per candidate image.** High / small /
high. `match/plan/PlanSearchStrategy.java:530-537` and `:572-579`, flag `true` at `:765`.
The `String.format` is inside the `assert`, the `containsNode`/`containsEdge` call is
not, so it runs with assertions off. `putNode`/`putEdge` are called for every candidate
image tried and again on every rollback: the single hottest statement in the matcher.
`containsNode` on a `DeltaHostGraph` is a `LinkedHashMap` key lookup, `containsEdge` a
`TreeHashSet` probe. Suggestion: `assert !(image instanceof DefaultHostNode) ||
host.containsNode(image) : ...`, or set the flag false.

**1.3 `Factory.get()` takes a global static monitor on every read.** High / small to
medium / high. `util/Factory.java:46-55` (every call enters `synchronized (lock)` on one
static object, then `addBuilders()` reads a static and may allocate an `ArrayList`).
Hot callers: `Step.getParAssign()`/`getRecipeParAssign()` per control step per state
(`lts/MatchCollector.java:297,302`), `Step.getApplyChange()` per transition
(`lts/MatchApplier.java:244`), `Step.getPush()` (`lts/StateCache.java:782`),
`Frame.getAttempt()` per frame advance (`control/instance/Frame.java:296`), and
`transform/RuleApplication.java:296,317,436,719`. These are memoised values that never
change after grammar compilation. Suggestion: lock-free fast path `if (set) return
value;` with a volatile holder, keeping the lock for the creating path only; or, for
`Step`/`Frame`, which are fixed before exploration starts, a plain memo field.

**1.4 `IsoChecker.isStrong()` is synchronized over a final field.** Low to medium /
trivial / high. `graph/iso/IsoChecker.java:1027-1035`, called twice per `getCertifier`
(`:934-935`), which runs once in `getCode`, twice in `areGraphEqual` and twice in
`areIsomorphic`: about ten monitor pairs on the shared singleton per state added.

**1.5 `NestedSwitch.getTransience()`, `getCall()` and `pop()` are synchronized.** Medium
/ small / medium to high. `control/template/NestedSwitch.java:70,147-152,171-178,183-185`.
Reached from `Frame.getTransience()` at `lts/StateMatches.java:113,136,179,206` (inside
the per-step loop) and from `AbstractGraphState.setFrame` (`:465,473`). The values are
immutable once frames are canonicalised. Suggestion: compute in the `Frame`/`Step`
constructor into final fields.

**1.6 `java.util.Stack` (synchronized `Vector`) on per-state paths.** Low to medium /
trivial / high. `explore/engine/ClosingStrategy.java:215` (`transientStack.isEmpty()`
on every `computeNextState`, `:124`), `explore/engine/LinearStrategy.java:109` (three
calls per state), `grammar/host/DeltaHostGraph.java:388` (basis chain in `initData`),
`lts/RecipeTransition.java:159`, `util/collect/TreeHashSet.java:355,359` (also boxes
`Integer`s). `ArrayDeque` is a drop-in replacement; `BeamPool` already uses it.

**1.7 `Pool.canonical` reads a `HashMap` outside the lock it writes under.** Correctness
smell rather than perf. `util/collect/Pool.java:30-42`. Used per transition via
`RuleTransitionLabel.createLabel:227-229`. Either declare it single-threaded and drop the
lock on the miss path, or use `ConcurrentHashMap`.

## 2. Dead or broken optimisations

**2.1 The confluent-diamond shortcut can never fire.** High / small / high (verified).
`lts/AbstractGraphState.java:79-91`: `getOutStub` tests `rule.getKey(this) == match` by
identity, but `RuleTransition.getKey()` (`lts/RuleTransition.java:112`) returns `new
MatchResult(this)` and `AbstractRuleTransitionStub.getKey()` (`:82`) returns `new
MatchResult(getEvent(), getStep())`; the caller at `lts/MatchApplier.java:83` passes a
freshly minted key too. So `getOutStub` always returns null, the branch at
`MatchApplier.java:84-93` is dead, and `confluentDiamondCount` (reported as
"Confluent:" by `StatisticsReporter:256`) must always print 0. Every call still allocates
one `MatchResult` per outgoing stub. Restoring the shortcut skips a full state creation
plus iso lookup per diamond, a large share of the work on lattice-shaped state spaces.
Suggestion: store the originating `MatchResult` on transition and stub (one reference)
so `getKey()` is stable, and compare with `equals` as a fallback. `MatchResult` already
has proper `equals` and a cached hash (`lts/MatchResult.java:114-157`).

**2.2 `getKey()` allocates inside the stub set's hash and equality functions.** Medium
to high / medium / high. Consequence of 2.1: `StateCache.createStubSet`
(`lts/StateCache.java:399-418`) calls `stub.getKey(getState())` in both `getCode` and
`areEqual`; `computeTransitionMap` (`:338-351`) per element; `addTransition`
(`:74-92`) three times per transition. Each fresh `MatchResult` recomputes its hash from
`step.hashCode() + event.hashCode()`. The stored key from 2.1 fixes this for free.

**2.3 The certifier is held by a `WeakReference`.** Medium to high / small / medium on
magnitude. `grammar/host/DeltaHostGraph.java:468-483`. A weak reference is cleared at
the first GC after the referent is weakly reachable, regardless of heap pressure, and
nothing else holds the certifier between `getCode` and the later `areEqual` calls
against this state. So refinement, certificate map and both partition maps can be
recomputed on an empty heap, inside the equality shortcut. `hasCertifier(strong)`
also ignores `strong`, so `areGraphEqual` can be told "present" and then silently build
a fresh uncomputed certifier. The rest of the code uses `CacheReference`/soft references
for derived data (`util/cache/AbstractCacheHolder.java:100`; `graph/GraphCache.java:535`
keeps the certifier inside the soft cache). Suggestion: soft reference, or route through
the graph cache as `AGraph` does; honour `strong` in `hasCertifier`.

**2.4 The refinement loop always runs one pass past discreteness.** Medium / small /
high. `graph/iso/PartitionRefiner.java:95-123`. Pass 0 has `store == false` and forces
`goOn`; pass 1 stores and yields `p1`; pass 2 stores only if `p1 < N`, so a discrete
partition (the common case) still costs three full O(V+E) passes, the last of which
cannot refine anything. Fixes: store on iteration 0, and break as soon as
`nodePartitionCount == nodeCertCount` (a discrete node partition implies a discrete edge
partition, since `MyEdge2Cert.equals` at `:682-689` compares endpoint certificates).
Certificate values change, so gate with `grammar-smoke`.

**2.5 Frozen graphs are never taken when collapsing is off.** Medium / small / high.
`lts/StateCache.java:293` (`isFreezeGraph` requires `freezeGraphs`, set from
`record.isCollapse()` at `:66`). With `COLLAPSE_NONE`, used by linear and unstored
explorations and left behind by `GTS.retainTraces` (`lts/GTS.java:356`), every graph
reconstruction on a long trace walks the delta chain back to the start state. Freezing
also requires `isClosed()` (`:207`), so a deep chain of open frontier states never
freezes. The coupling to collapsing looks historical.

**2.6 Only the tip of a replayed chain is frozen.** Medium / small / medium.
`lts/StateCache.java:201-210`: after replaying `depth` deltas only the tip state gets a
frozen graph; the intermediate states whose graphs were just materialised are not
considered, though they are what makes the next sibling reconstruction expensive.
Freezing the chain midpoint halves the worst case for the subtree.

**2.7 Graph reconstruction can still recurse.** Medium / small / medium.
`lts/StateCache.java:192`: the backward walk stops at an ancestor that *has a cache*,
not one that *has a graph*; a cached-but-graphless ancestor (e.g. type policy OFF, so
`checkInitConstraints` never asked for the graph) ends the loop and `backward.getGraph()`
at `:200` re-enters `computeGraph` recursively. Use `getCache(false)` plus
`StateCache.hasGraph()` (`:128`) in the stop condition.

## 3. Costs that scale with the wrong quantity

**3.1 Every certifier allocates an array sized by the global node counter.** High /
small / high (verified). `graph/iso/CertificateStrategy.java:45`:
`new NodeCertificate[graph.getFactory().getMaxNodeNr() + 1]`. `getMaxNodeNr()`
(`graph/ElementFactory.java:59,71`) is a monotone high-water mark over every node the
factory ever registered, so per-state cost is O(states × total nodes created) in
allocation and zero-filling, retained as a final field for the certifier's life. The
array is scratch, used only by `putNodeCert`/`getNodeCert` from `initCertificates`
(`:150-176,202,214`). Suggestion: an open-addressed int-keyed probe table sized by
`nodeCount()` (the shape `EdgeBundles.java:59` already uses), or a reusable scratch
array cleared after initialisation.

**3.2 The full certificate map is built for every state added, usually for nothing.**
High / small / high (verified). `lts/GTS.java:1142-1143` passes
`certifier.getCertificateMap()` to `CallStack.hashCode`, but `NestedArrays.hashCode`
(`util/collect/NestedArrays.java:88-110`) never touches the modifier when the array is
empty, which it is for every state of a grammar without control parameters.
`getCertificateMap()` (`CertificateStrategy.java:285-307`) builds a `HashMap` with
|V|+|E| entries. Guard on `getPrimeStack().length > 0`; the map then becomes lazy and is
built only in `areGraphEqual` for states that reach a comparison. Same for the
`COLLAPSE_EQUAL` branch at `GTS.java:1134`. (`claude/archive/iso-edge-bundles.md`
already records this cost.)

**3.3 Singular edge lookups mint and permanently intern a probe edge.** High / medium /
high. `match/plan/Edge2SearchItem.java:319-333`: `getEdgeImage` calls
`host.getFactory().createEdge(sourceFind, type, targetFind)`, and `HostFactory.createEdge`
(`grammar/host/HostFactory.java:167-170`) does `newEdge` then `storeEdge`
(`graph/StoreFactory.java:233-252`), which returns the pooled edge if one exists and
otherwise registers the fresh probe in the factory's pool and numbered array forever.
This is the standard NAC shape (`NegatedSearchItem` over an `Edge2SearchItem` with both
ends bound, `NegatedSearchItem.java:148-152`), where the edge by construction usually
does *not* exist: every "no `a -l-> b`" check at every partial match at every state
mints a `HostEdge` that is never part of any graph and never released. A leak as well as
a hot allocation. Suggestion: a lookup-only `HostFactory.findEdge(source, type, target)`
that computes `getStoredCode` from the components and probes `edgeStore`
(`TreeHashSet.get(int)` exists, `TreeHashSet.java:461`) without allocating or
registering.

**3.4 `HostFactory.normalise` grows without bound.** Medium (memory) / medium / medium.
`grammar/host/HostFactory.java:272-288`: every node-creating event's created-node array
is looked up in `normalHostNodeMap`, keyed by `Arrays.asList(nodes)` (a wrapper per
call), never evicted, holding strong references to the nodes for the whole run, which
defeats the soft-reference collapse for those nodes. Suggestion: document the growth at
least; better a content-hash key with weakly held entries, or a bound.

**3.5 `GTS.edgeSet().iterator()` materialises every state's transition map up front.**
Medium to high / small / high. `lts/GTS.java:1270-1272` uses the `NestedIterator`
overload that does `iterables.map(Iterable::iterator).toList()`
(`util/collect/NestedIterator.java:88-90`), calling `iterator()` on *all* states'
transition sets eagerly, which resurrects every cache, rebuilds every stub set and
`KeySet` transition map, and pins them all via the retained list: an O(N) burst that
defeats the soft-reference policy for the whole GTS. Make the nested iterator lazy.

**3.6 `MatchCollector` pins the parent's transition map.** Medium (memory, inference) /
medium / medium. `lts/MatchCollector.java:68,390`: the collector holds
`parent.getCache().getTransitionMap()` strongly, and is itself held by the child's
`StateMatches` and `StateCache`, so while any child is unexplored the parent's map,
transitions and stubs cannot be collected. Constructing the collector also forces
`computeTransitionMap()` on the parent even when no step reuses parent matches.
Suggestion: hold the parent state and fetch the map lazily inside `collectMatches`.

**3.7 Erasing an edge into a `ValueNode` materialises the whole in-edge store.** High /
small / medium to high. `transform/RuleApplication.java:651-662`: `removeEdges` calls
`getValueNodeEdges`, which calls `source.inEdgeSet(node)`; on a `DeltaHostGraph` without
a materialised `nodeInEdgeStore` that runs `computeInEdgeStore`
(`grammar/host/DeltaHostGraph.java:242-271`), a full O(V+E) walk allocating a
`HostEdgeSet` per node, retained on the cached graph. Suggestion: filter
`source.edgeSet(node)` (served from the always-present `nodeEdgeStore`, `:351-356`) on
`e.target() == node`. Related: `computeInEdgeStore`/`computeOutEdgeStore` (`:258-311`)
use `equals` where `==` suffices for canonical host nodes, and size per-node sets at the
default capacity.

**3.8 `StatisticsReporter.GraphCounter` materialises every state's graph at add time.**
Medium / small / medium. `explore/util/StatisticsReporter.java:487-490`, registered at
`:117-120` at any verbosity above low, i.e. by default in `Generator`. It forces the
`DeltaHostGraph` of every added state just to average node/edge counts, including states
never explored and states whose cache is collapsed before exploration. Gate behind
`Verbosity.HIGH` or compute lazily at report time.

**3.9 `lts.GTSCounter` pins every non-full state (GUI only).** Medium / small / high.
`lts/GTSCounter.java:81,190`: a `HashMap<GraphState,List<RuleTransition>>` holds strong
references to each non-full state and its incoming transitions until it goes FULL;
interrupted explorations leak them for the run. Track only the count. Also
`Flag.values()` is cloned per state at `:47,72,115`; `GTS.java:1039` shows the cached
`FLAG_ARRAY` pattern.

**3.10 `Strategy.collectKnownStates` walks the whole GTS on every `play()`.** Low (CLI)
to medium (GUI) / small / high. `explore/engine/Strategy.java:80,132-136`; the GUI
re-invokes `play()` on a large GTS every time the user continues exploring. Skip when
the GTS holds only the start state, or keep a "known up to number n" watermark.

## 4. Allocation on the per-state and per-match path

### 4.1 Matching

**4.1.1 A fresh `Search` plus one `Record` and influence array per plan item, on every
traversal.** High / medium / high. `match/plan/PlanSearchStrategy.java:151-163`
(`getSearch` always does `createSearch()`, eight arrays at `:379-390`); `getRecord`
(`:507-528`) allocates a `Record` and an influence array per plan position on first use,
i.e. essentially all of them per traversal. The `private Search search` field at `:315`
is written and never read: the fossil of a dropped reuse mechanism. Worst for NACs and
quantifiers: `ConditionSearchItem.NegConditionRecord.find()` (`:438`) calls
`matcher.find(...)` once per partial match of the enclosing rule, allocating a whole
`Search` plus records per candidate. There is no threading anywhere in `explore`, `lts`,
`transform` or `match` (grepped), so the only hazard is re-entrancy. Suggestion: a
per-strategy free list or depth-indexed array of `Search` objects; `Search.initialise`
already resets everything except the image arrays.

**4.1.2 NAC/quantifier context map rebuilt per candidate from boxed index maps.** High /
medium / high. `match/plan/ConditionSearchItem.java:297-312` (`createContextMap`, called
from `:331` and `:438`): allocates a `RuleToHostMap` (itself two `LinkedHashMap`s plus a
`Valuation`, `grammar/rule/RuleToHostMap.java:47,192-200`) and fills it by iterating
three `HashMap<_,Integer>` entry sets fixed at `activate` (`:144-155`): three iterators
and N unboxings per candidate. Suggestion: flatten to parallel `RuleNode[]`/`int[]` at
activation; reuse one `RuleToHostMap` per record, cleared in place (safe because
`Search.initialise` at `:394-427` copies the seed into its own arrays).

**4.1.3 Injectivity bookkeeping uses a trie set keyed by node number.** Medium / medium
/ medium. `PlanSearchStrategy.java:556-566,582-592`: `getUsedNodes().remove(old)` and
`.add(image)` per `putNode`, on `HostNodeSet`/`HostEdgeSet` (`TreeHashSet` keyed on
`getNumber()`, `grammar/host/HostNodeTreeHashSet.java:110-114`). The key is already a
dense small int; a `BitSet` or `long[]` sized by `HostFactory.getNodeCount()` makes this
two bit operations. No enumeration of the sets' contents was found.

**4.1.4 Every successful match allocates about eight objects before the event exists.**
Medium / medium / high. `getMatch()` (`PlanSearchStrategy.java:666-694`) allocates a
`RuleToHostMap` (two `LinkedHashMap`s plus a `Valuation`, even for rules without label
variables); `new TreeMatch` (`match/TreeMatch.java:51-58`) allocates the submatch
collection and a `ProofWrapperCollector` plus the field-initialised
`ProofWrapperVisitor` (`:563`), dead weight for a flat existential rule; `Proof` eagerly
allocates a `LinkedHashSet` for its subproofs (`match/Proof.java:77`). Make `Valuation`,
the `TreeMatch` collector/wrapper (used only at `:194,276`) and `Proof.subProofs` lazy.

**4.1.5 `TreeMatch.setFixed()` forces an eager hash on every match.** Medium / small /
medium. `match/TreeMatch.java:547-559`: `isFixed()` is `hashCode != 0`, so `setFixed`
must compute the hash, and `traverseProofs` (`:147`) fixes every match.
`computeHashCode` (`:451-474`) walks the anchor doing a `RuleToHostMap.get(AnchorKey)`
per key. For the common flat rule the hash is never used. Add a separate `fixed` flag.

**4.1.6 `satisfiesDangling` builds a `HashSet` and two stream pipelines per candidate.**
Medium / small / high (DPO only). `match/Prover.java:250-273`, per candidate match per
eraser node when `isCheckDangling()`. Replace with two counting loops.

**4.1.7 Operator arguments assembled via a stream pipeline.** Medium / small / high
(attributed grammars). `match/plan/OperatorNodeSearchItem.java:313-319` (set variant
`:344-355`): `IntStream.of(argumentIxs).mapToObj(...).map(...).collect(toList())` per
evaluation inside `find()`. Fill a preallocated array of known arity. See also 4.2.1.

**4.1.8 `SearchPlan.dependencies` is a `List<Integer>` read in the backtracking loop.**
Low / small / high. `match/plan/SearchPlan.java:249-251,274`, read at
`PlanSearchStrategy.java:470,514`. Flatten to `int[]`, and the plan (an `ArrayList`
subclass, so `get(i)`/`size()` are virtual per iteration) to `AbstractSearchItem[]`
after `setFixed`.

**4.1.9 Megamorphic dispatch in the shared record loop.** Low to medium / large /
medium (inference, not profiled). `match/plan/AbstractSearchItem.java:506-519`:
`MultipleRecord.find()` is final and shared, so `imageIter.next()` (iterators of
`TreeHashSet`, `LinkedHashMap` key sets, `ArrayList`, empty set) and `write(next)`
(eight-plus implementations) are megamorphic; likewise `getRecord(current).next()` at
`PlanSearchStrategy.java:469`. Flagged as the structural reason micro-optimising
individual records has limited return, and as an argument for keeping per-candidate work
(1.2, 4.1.3) out of virtual calls.

**4.1.10 Parent-transition reuse rescans all parent transitions per step.** Medium /
small / medium. `lts/MatchCollector.java:121-136`: for every step of the attempt the
whole `parentTransMap` is iterated and filtered on `getAction().equals(rule)`, O(steps ×
parent out-degree) per state where O(out-degree) would do. Build a rule-indexed map
lazily on first use; the collector is per state.

### 4.2 Events, transformation, algebra, control

**4.2.1 Every algebra operation goes through core reflection with a boxed list.** High /
medium / high. `algebra/AlgebraFamily.java:184-188,362-369`, `algebra/Operation.java:29-46`,
`match/plan/OperatorNodeSearchItem.java:299-320`. Per operator-node evaluation during
matching (once per attempted binding, inside the backtracking loop): the stream-built
`ArrayList` of 4.1.7, one pass to test for `ErrorValue`, `args.toArray()`, then
`Method.invoke`, which re-validates and re-boxes; the target methods take boxed types
(`algebra/JavaIntAlgebra.java:40`), so every intermediate `int` round-trips through
`Integer`. Suggestion: bind each `Operation` to a `MethodHandle` or a
`LambdaMetafactory`-generated functional interface at construction, with
arity-specialised `apply(Object)`/`apply(Object,Object)` entry points; the search item
knows the arity statically.

**4.2.2 `ValueNode` creation costs two hash lookups per intermediate result.** Medium to
high / small / high. `grammar/host/HostFactory.java:123-137,145-148`,
`OperatorNodeSearchItem.java:271-272`: a `HashMap` lookup keyed by `Algebra`, then a
nested lookup keyed by the boxed value. The search item knows the result algebra
statically, so the inner map can be resolved once; a dense cache for small INT/BOOL
values removes the rest.

**4.2.3 `ErrorValue extends Exception` and is minted per failed operation.** Medium
(grammar-dependent) / small / medium. `algebra/ErrorValue.java:31-36`,
`algebra/Algebra.java:67-69`, `AlgebraFamily.java:204-210`: each construction fills in a
stack trace, caught immediately by `computeStrict`/`applyStrict`. `equals`/`hashCode` are
sort-only (`:54-72`). Return an interned per-sort instance where the cause is not
needed, or construct with `writableStackTrace = false`.

**4.2.4 An `ArrayList` plus recursive traversal per match for the single-event case.**
Medium / small / high. `transform/RuleEvent.java:103-115`: `createEvent` allocates the
list and runs `collectEvents` even when `!rule.hasSubRules()`, in which case the assert
at `:110` says the list holds exactly one element. Short-circuit for flat rules.

**4.2.5 `BasicEvent` builds its fresh-node list before hash-consing.** Medium / small /
high. `transform/BasicEvent.java:81-85,602-609`: the constructor calls
`createFreshNodeList()` (outer list plus one inner list per creator node), then
`Record.normaliseEvent` (`transform/Record.java:123-136`) discards the event if an equal
one exists, which in revisited regions is the common outcome. Initialise lazily on first
`getFreshNodes`, shared empty sentinel when there are no creator nodes.

**4.2.6 `CompositeEvent.equalsEvent` allocates a `HashSet` per comparison.** Medium
(quantified rules) / small / high. `transform/CompositeEvent.java:183`: `new
HashSet<>(Arrays.asList(myEvents))` on every content comparison, triggered on every
hash-bucket hit in `RuleEventSet`. Arrays are 2 to 5 elements; a nested loop after the
existing length check (`:180`) is allocation-free, or pre-sort at construction.

**4.2.7 `Rule.compareTo` has no identity shortcut and `ModuleName.hashCode` is
uncached.** Medium / small / medium to high. `grammar/Rule.java:569-584`,
`util/ModuleName.java:180-183`, `util/QualName.java:117-127`,
`transform/BasicEvent.java:210-211`. `BasicEvent.compareTo` starts with the rule
comparison, which descends into token-wise `QualName` string comparison even for the
same rule; `MatchCollector.canonicalise` sorts with it O(n log n) times per step, where
all matches share one rule. `ModuleName.hashCode()` recomputes `tokens().hashCode()` per
call. Add `if (this == other) return 0;` and cache the hash in the house pattern.

**4.2.8 `MatchCollector.canonicalise` allocates a comparator chain and a second set per
step.** Low to medium / small / high. `lts/MatchCollector.java:200-212`: an `ArrayList`
copy, a fresh `Comparator.comparing(...).thenComparing(...)` (stateless, should be
`static final`), and a second `MatchResultSet` re-hashing every element. The re-sort
itself is required by the determinism rule (see the class comment).

**4.2.9 `MatchResultSet` is a capacity-16 `TreeHashSet`, allocated twice per step per
state.** Medium / small / high. `lts/MatchResultSet.java:22`, instantiated at
`MatchCollector.java:107,209`; `TreeHashSet.java:53-76,136-138,1138-1147`: the no-arg
constructor eagerly allocates four arrays, roughly 330 bytes, for sets that typically
hold 0 to 3 matches; `StateMatches` (one per state) is a third. Add a small-capacity
constructor, or collect per-step results in an `ArrayList` and fold once in
`StateMatches.addAll` (`:203`).

**4.2.10 `extractBinding` allocates a `RuleToHostMap` and recomputes the call stack per
step.** Medium / small / high. `lts/MatchCollector.java:288-317`:
`createRuleToHostMap()` (three `HashMap`s, `grammar/rule/RuleToHostMap.java:45-48`,
`graph/AGraphMap.java:37-38`) unconditionally, even when both parameter assignments are
empty (the common case); `getActualStack()` (`lts/AbstractGraphState.java:497-504`)
replays every pop through `CallStackChange.apply` (array allocation plus a valuator
lambda, see 4.2.11) once per step though it is identical for all steps of the state.
Hoist the stack to the per-state collector; shared immutable empty map when both
assignments are empty (needs a check that `Prover.traverseMatches` does not mutate the
seed map, not verified).

**4.2.11 The shared `Valuator` reinstalls a fresh lambda into an `EnumMap` on every
use.** Medium to high / medium / high. `control/Valuator.java:99-135`,
`lts/MatchApplier.java:235-243`, `lts/MatchCollector.java:290-291`,
`control/instance/CallStackChange.java:128`. `setVarInfo`/`setAnchorInfo`/`setCreatorInfo`
each allocate a capturing lambda and do an `EnumMap.put`: two per transition in
`computeTargetStack`, one per step per state in `extractBinding`, one per link of a
change chain. `eval` (`:62-69`) then does an `EnumMap.get` and a `Function.apply` through
a call site that sees four or five lambda classes (megamorphic). `setAnchorInfo`/
`setCreatorInfo` take `Function<Integer,HostNode>`, so `b.index()` is autoboxed per
lookup (`:101,109`) and unboxed again as an array index. Suggestion: explicit fields
(`Object[] stack`, `AnchorValue[] anchorImages`, `HostNode[] createdNodes`, `RuleEffect
record`) and a plain `switch (bind.type())` in `eval`.

**4.2.12 Derived `Step` predicates are recomputed on every query.** Low to medium /
small / high. `control/instance/Step.java:91-93,121-137,162-164`: `isModifying()` walks
`getSource().getPrime()` and `getInnermostCall().hasOutVars()`, called at
`lts/MatchApplier.java:71,82` and `lts/MatchCollector.java:119`, i.e. per match;
`isInner()` and `getTransience()` reach the synchronized `NestedSwitch` (1.5). `Step` is
immutable after construction and already caches its hash (`:332-353`); make these final
fields.

**4.2.13 `getAddedEdges` allocations.** Low to medium / small / medium.
`transform/RuleEffect.java:624-700,754-762`: the merge branch allocates a memo `HashMap`
per call (`:639`); the non-merge branch allocates an anonymous `Iterable` plus a
`FilterIterator` (`:677-697`); `getAddedEdgeArray` then allocates an `ArrayList`, fills
it via a bound method reference and copies to an array, once per fresh target state.
Also `RuleEffect.java:77` allocates a created-node list even for rules without creator
nodes. Give `getAddedEdgeArray` a direct count-and-fill path.

**4.2.14 `HostFactory.nodes(TypeNode)` allocates a factory per created node.** Low to
medium / small / high. `grammar/host/HostFactory.java:117-120` returns `new
DefaultHostNodeFactory(type)` uncached (only the top type is cached, `:102-110`);
`BasicEvent.createNode` (`transform/BasicEvent.java:616-620`) calls it per created node.
A `Map<TypeNode,NodeFactory>` is determinism-safe (keyed lookup, never iterated).

### 4.3 LTS and state cache

**4.3.1 `StateCache.init` allocates three `HashSet`s per state unconditionally.** Medium
/ small / high. `lts/StateCache.java:485,491,492` (`backTransient`, `forwTransient`,
`forwTransientOpen`), plus `:469-480` for inner states, populated only when
`knownTransience > 0` (`:493-496`). Use the existing `EMPTY_CACHE_SET` when the grammar
has no transient behaviour and promote on first insertion.

**4.3.2 Per-node edge sets are over-provisioned by about five times.** Medium to high
(memory) / small / high. `grammar/host/HostEdgeStore.java:49-52,81-88` creates
`HostEdgeSet`s at `TreeHashSet.DEFAULT_CAPACITY = 16` (`HostEdgeTreeHashSet.java:13-15`,
`TreeHashSet.java:1138`): four arrays, about 290 bytes, per node per store, up to three
stores per node, for typical degrees of 1 to 3. Graphs swing rather than copy so few are
live at once, but the copy path (`CopyTarget`, `DeltaHostGraph.java:822-854`) allocates a
full family. A small default (2 or 4) for per-node sets.

**4.3.3 `StateMatches.advanceFrame` uses a `LinkedList` for outstanding matches.** Low
to medium / trivial / high. `lts/StateMatches.java:167`: only appended, iterated and
removed via iterator; `ArrayList` gives the same semantics. `EMPTY_MATCH_SET` (`:249`)
could avoid the allocation when the attempt yields nothing.

**4.3.4 Defensive copies of the match set.** Low / small / high.
`lts/AbstractGraphState.java:232-235` copies the cache's match set per explored state
(needed, since `applyMatch` mutates the set); `explore/engine/RandomLinearStrategy.java:55`
copies the copy (verified redundant).

**4.3.5 `DefaultDeltaApplier.applyDelta` allocates four iterators per replay step.** Low
/ small / high. `grammar/host/DefaultDeltaApplier.java:51-67` iterates four
`TreeHashSet`s unconditionally; with chain replays of up to 25 graphs that is 100
iterators per reconstruction. Guard each loop with `isEmpty()`.

**4.3.6 Graph names rebuilt inside the reconstruction loop.** Low to medium / small /
high. `lts/StateCache.java:202` calls `state.toString()` inside the forward replay loop,
producing k+1 identical strings for a chain of length k
(`AbstractGraphState.toString:397-406`), all naming the tip. Hoist, or give
`DeltaHostGraph` a lazily derived name.

**4.3.7 `LinkedList` with `add(0, ...)` for the reconstruction chain.** Low / trivial /
medium. `lts/StateCache.java:191`. `ArrayList` appended in walk order and iterated
backwards.

**4.3.8 `getTransitions(claz)` allocates a `SetView` per call with O(n) `size()`.** Low
/ small / high. `lts/StateCache.java:94-105`, `util/collect/SetView.java:59-68`;
`ClosingStrategy.doNext:84` calls `getRuleTransitions()` per state. Cache the five
`Claz` views per cache, or return the map directly when the state has no inner or
absent transitions.

**4.3.9 A capturing lambda per transition for a feature almost no grammar uses.** Low to
medium / small / medium. `lts/GTS.java:678-683` calls `trans.getOutputString()`
unconditionally; `lts/GraphTransition.java:120-139` builds a lambda capturing `this`
before `Optional.map` sees the (almost always empty) format string. Guard with a
predicate on the action.

**4.3.10 `DefaultGraphNextState` wraps hot accessors in `Optional`.** Low / small /
medium. `lts/DefaultGraphNextState.java:146-167,251-265,301-310`: `getSourceKey()`,
`getSourceAddedNodes()` and `createInTransitionStub` (with a boxed `Boolean` via
`map(...).orElse(false)`) allocate per transition added. Package-private `@Nullable` raw
accessors behind the public `Optional` facade.

**4.3.11 Boxed frequency list per cache creation.** Low / trivial / high.
`util/cache/CacheReference.java:251-259`: `frequencies.set(i, frequencies.get(i) + 1)`
on an `ArrayList<Integer>` per cache creation. Use `int[]`.

**4.3.12 Per-state field inventory.** Informational. `DefaultGraphNextState` carries
eight references plus two ints, about 56 bytes on compressed oops, plus the stub array.
`frozenGraph` (`AbstractGraphState:371`) is non-null for a tiny minority and could live
in a side map; `addedEdges` (`DefaultGraphNextState:95`) is worth checking for a shared
empty array as `addedNodes`/`callStack` have via `MatchApplier.EMPTY_NODE_ARRAY`.

### 4.4 Isomorphism

**4.4.1 `getCertEqualNodeMap` allocates an unsized `HashMap` and does 2·E puts per
positive answer.** Medium / small / high. `graph/iso/IsoChecker.java:388-414`, the path
of essentially every successful non-equal collapse. Minimum: size the map from
`nodeCount() * 2`. Better: probe `cod.getNodePartitionMap()` per dom node certificate
(V lookups) and verify per bundle that the images' endpoints match, O(V+E) with no
allocation.

**4.4.2 Graph certificates are boxed `Long`s.** Low to medium / small / high.
`CertificateStrategy.java:63` (`Object getGraphCertificate()` over a `long` field at
`:400`) autoboxes per call: `GTS.java:1138` once per state, `IsoChecker.java:277` twice
per comparison, plus `CertificateStrategy.java:288,320,352`. Add a primitive accessor for
the hot sites; keep the boxed one for the tests.

**4.4.3 The equality shortcut lacks a free edge-count filter and depends on two
certificate maps.** Medium / medium / high. `IsoChecker.java:167-182` compares
certificate-map key sets to dodge the delta-aliasing hazard documented at `:167-176`,
correct but dependent on two |V|+|E| `HashMap`s existing (not guaranteed once 3.2 is
fixed and 2.3 is not). An explicit `dom.edgeCount() != cod.edgeCount()` test before
`:167` rejects the common case without touching either map.

**4.4.4 `COLLAPSE_EQUAL` copies both node and edge sets of the new graph per
comparison.** Medium (equality mode) / small / high. `lts/GTS.java:1108-1111`: full
O(V+E) copies of the *new* graph's sets on every candidate comparison; only the
codomain needs copying, as `IsoChecker.areGraphEqual:174-175` does.

**4.4.5 Discreteness mismatch is short-circuited in one direction only.** Low / small /
high. `IsoChecker.java:285-301` returns false when the codomain is discrete and the
domain is not; the reverse falls through to `computeIsomorphism`, which re-tests at
`:599` and would NPE at `:401` via `SmallCollection.getSingleton` (`:136`) for a
non-singleton with assertions off. Believed unreachable (equal certificate counts plus a
discrete domain force a discrete codomain), but the code does not say so; make the test
symmetric.

**4.4.6 Static mutable scratch state and counters in the certifier.** Low (perf), blocks
parallelism / small. `IsoChecker.java:1178-1214` (a dozen non-atomic `static int`
counters incremented per check), `PartitionRefiner.java:344-360` (`certStore`
`TreeHashSet` and `tmpCertIxs` are statics shared by all refiner instances),
`CertificateStrategy.java:445-457` (static `iterateCountArray`). Besides cache-line
traffic this makes the certificate machinery non-reentrant: a GUI thread certifying
while a generator runs corrupts it. Instance fields or a `ThreadLocal`.

**4.4.7 Inner-class search items and per-step allocation in the iso search.** Low /
small / high. `IsoChecker.java:1247,1280`: `IsoSearchPair`/`IsoSearchItem` are non-static
inner classes; `computeIsomorphism` allocates an iterator per forward step (`:675`), a
`HashSet` (`:632`) and a `Morphism` (`:631`) per search; `computePlan` (`:789-795`) an
`ArrayList`, `HashMap`, `HashSet` and `TreeSet` per call. Per comparison, not per state.

**4.4.8 Edge bundles are built eagerly for every non-simple graph.** Medium / medium /
high (already documented). `CertificateStrategy.java:160-175`, `EdgeBundles.java:50-68,
101-119`: two full edge scans plus arrays whenever any bundle has more than one copy.
`claude/archive/iso-edge-bundles.md` measured it at about +280 ms certifying on
`As-and-Bs bfs:11` and +90 ms on `append`, and proposes folding the grouping into the
initialisation scan. Still open.

**4.4.9 `TreeHashSet` shape of the state set.** Low / small / medium. `GTS.java:1045-1057`:
resolution 2 (4-way branching), root resolution 10, initial capacity 10 000. Beyond the
root a `put` takes about log4(n/1024) dependent loads: 5 to 6 levels at a million
states. Raising `STATE_SET_RESOLUTION` to 3 or 4 halves the pointer chasing at the cost
of wider records; measure on the state set alone.

### 4.5 Exploration driver

**4.5.1 Listener notification allocates an iterator and makes megamorphic calls per
state and per transition.** Medium / medium / high. `lts/GTS.java:830-836,856,863-868,
885-890,940-943`: each fire iterates a `LinkedHashSet` (iterator allocation) over, in a
default `Generator` run, five implementations (strategy explore listener,
`ResultCollector`, `GTSCounter`, `GraphCounter`, `GenerateProgressListener`;
`explore/Generator.java:97-99`, `StatisticsReporter.java:117-120`), so `addUpdate` is a
megamorphic interface call. Keep a `GTSListener[]` snapshot rebuilt on add/remove and
iterate by index; optionally fold the always-present counters into one listener.

**4.5.2 `GenerateProgressListener` computes the open-state count 100 times more often
than it uses it.** Low / small / high. `explore/util/GenerateProgressListener.java:136-145`,
consumed at `GenerateProgressMonitor.java:37,45` only when `count % 100 == 0`. Move the
call inside the branch.

**4.5.3 Frontier pools use `LinkedList` and box depth counters.** Medium / small / high.
`explore/engine/QueuePool.java:56,86,88` (a `LinkedList` per depth level, a node per
frontier state; the only non-FIFO operation is `addFirst` at `:75`, which `ArrayDeque`
supports); `explore/engine/StackPool.java:85,90` (`Deque<Integer> levelCount` with
`push(pop() + 1)` per state, values above 127 allocate; `LinkedList` as the state stack).
`ArrayDeque` for the states, `int[]` with a top index for the level counts, reuse the
drained deque for the next depth.

**4.5.4 Anonymous `Visitor` and `Optional` filter lookup allocated per step per state.**
Low / medium / high. `lts/MatchCollector.java:144-181`: a capturing inner-class instance
per `collectMatches`, handed to `Prover.traverseMatches`. Could be a per-collector
instance with mutable fields reset per step.

## 5. Structural and algorithmic

These need a design discussion before any commit.

**5.1 No (node, label) edge index; candidate sets are over-approximated.** High / large
/ high. `match/plan/Edge2SearchItem.java:494-524`: with a bound source the item iterates
either every incident edge of that node (all labels, both directions) or every edge in
the graph with that label, discarding the rest in `write()`. `DeltaHostGraph` maintains
four incrementally updated stores (`nodeEdgeStore`, `nodeInEdgeStore`, `nodeOutEdgeStore`,
`labelEdgeStore`, `DeltaHostGraph.java:515-526`, updated at `:636-684`) but none keyed on
the pair; the in-code comment that the incidence route "does not pay off" predates the
directed stores. In increasing effort: (a) use `outEdgeSet(sourceFind)`/`inEdgeSet(targetFind)`
as `VarEdgeSearchItem.java:190-198` already does, halving candidates (memory cost: the
directed stores get built for grammars that never touched them); (b) restore the
commented-out size comparison in the `targetFind` branch (`:508-519`) so a high-degree
target does not beat a rare label; (c) a fifth store keyed on (node, label), maintained
like `labelEdgeStore`, turning the filter into a lookup.

**5.2 No host node index by type.** Medium / medium / high (grammar-dependent).
`match/plan/NodeTypeSearchItem.java:347-349` iterates `host.nodeSet()` and filters on
`matchingTypes.contains(image.getType())` (`:301`) per element; the type is a field on
`DefaultHostNode` (`grammar/host/DefaultHostNode.java:66-75`), not a self-edge, so there
is no index. `ItemTypeComparator` schedules these items last (`PlanSearchEngine.java:804-806`),
limiting the exposure to rules with genuinely unconnected typed nodes. A `TypeNode ->
HostNodeSet` store in `DeltaHostGraph`, maintained in `DataTarget.addNode/removeNode`.

**5.3 Plan ordering uses no host-graph statistics.** Medium / medium / medium.
`PlanSearchEngine.computeComparators` (`:390-405`) and the comparators at `:606-915`
order by rule structure plus the manually maintained `controlLabels`/`commonLabels`
properties; nothing consults type-graph multiplicities or the start graph's label
distribution. The plan is built once per (condition, seed), so a statistics pass is free
at run time. Seed `FrequencyComparator` from the start graph's `labelEdgeStore` sizes
when the property lists are absent. A heuristic gamble: the plan is fixed while the
graph evolves.

**5.4 Matching is not incremental across a delta.** High / large / high.
`MatchCollector` already reuses parent transitions when a rule is not disabled
(`:120-137`), uses the enabled/disabled rule sets from `Record` (`:70-71`) and does the
diamond lookup (`:341-352`). What is missing: when a rule is deemed disabled, *all* its
matches are recomputed although only matches whose images intersect the parent step's
erased or created elements can have changed. Verifying the surviving parent matches
(anchor images present, NACs still failing) is much cheaper than re-searching, and the
anchor-based `getEventMatcher` (`match/Prover.java:215-227`) is the existing machinery
for re-checking a match from its anchor. This is the ground the removed RETE engine
covered (`claude/archive/rete-retirement.md`).

**5.5 Certificates are recomputed from scratch per state.** High / large / high.
`CertificateStrategy.initCertificates` (`:150-176`) iterates the full node and edge sets
of the reconstructed graph and the refinement is a global fixpoint, though a child
differs from its parent by a `DeltaStore` of a handful of elements. Incremental colour
refinement (seed from the parent's stable colouring, re-refine the ball around the
delta) is the largest algorithmic lever on the iso side; it must stay canonical (the
fixpoint must not depend on the seeding) and interacts with 5.6.

**5.6 Symmetry breaking costs one full refinement per member of the duplicate class.**
High on symmetric grammars / large / high. `PartitionRefiner.java:126-171`: for the
smallest duplicate class, `iterateCertificates2` runs `breakSymmetry`,
`iterateCertificates1` and `rollBackCertificates` for every member (`:145-150`), plus
checkpoint and accumulate passes over all certificates (`:235-271`): individualise-and-
refine without automorphism pruning, which is where certifying time goes for grammars
with repeated identical substructures. The standard remedy is nauty-style orbit pruning.
Contained wins meanwhile: `getSmallestDuplicates` (`:274-294`) allocates a `LinkedList`
and rescans all node certificates per round; the checkpoint/rollback/accumulate passes
do a `MyCert<?>` checkcast per element. Note also that termination of the breaking loop
(`:131-164`) rests on the singular flag never being un-marked (`:205-207,561-565`) and
not being rolled back; it works, but the exit condition is implicit and deserves a
comment before anyone touches it.

**5.7 Certificate seeding mixes in/out contributions weakly.** Low to medium / medium /
medium. `PartitionRefiner.java:649-650,738`: incident edges add `mult * labelHash` to
the source and `mult * (labelHash << 1)` to the target, so a label with hash h on an
out-edge collides with one with hash h/2 on an in-edge by construction; unary edges
contribute to the source only. Distinct odd multipliers are a one-line change that could
reduce how many graphs reach 5.6 at all. Heuristic; needs measurement.

**5.8 Iso search plan ordering is weakly informed.** Medium / medium / medium.
`IsoChecker.java:1256-1264,1287-1294`: ordered by candidate-image-set size, then label,
then number of pre-matched endpoints; missing are endpoint degrees and node candidate
set sizes, the standard first-fail signals. `computePlan` re-sorts a `TreeSet` while
mutating keys of removed elements only (`:828-845`), correct but fragile.

## 6. Assertion-only costs

Not production costs, but they distort every timing taken under `-ea`, which includes
the test suite and Eclipse launches.

- `util/collect/TreeHashSet.java:166-167`: the copy constructor asserts `containsAll(other)`,
  an O(n) scan with a `contains` per element, on the copy path that `HostEdgeSet`/
  `HostNodeSet` take constantly. Gate behind the existing `DEBUG` flag (`:1262`) like
  `testConsistent()`.
- `TreeHashSet.java:835`: `newKeyIx` asserts `code == getCode(key)`, re-deriving the
  code computed three lines earlier in `put` (`:397`). For `GTS.StateSet` under
  `COLLAPSE_EQUAL` that is an O(V+E) graph hash, doubling the state-hash cost per
  insertion; under iso collapse it re-enters the certifier.
- `transform/AbstractRuleEvent.java:100`: `assert !equalsEvent(other)` runs a full
  anchor-image comparison on every unequal event comparison, i.e. on every hash-bucket
  collision in `RuleEventSet`.
- `match/plan/SearchItem.java:187-211`: `State.getNext()` allocates an `EnumSet` per
  record step, used in asserts at `AbstractSearchItem.java:327,472`; make the nine
  results `static final`.

## 7. Checked and found fine

`Frame` and `Step` are canonicalised once per automaton (`control/instance/Automaton.java:74-78`);
no `Frame` is allocated per transition. `Step`, `AbstractRuleEvent` and `MatchResult`
already cache their hashes in the house pattern. `DeltaHostGraph.edgeSet(node)` aliases
rather than copies (`ALIAS_SETS`, `DeltaHostGraph.java:543`), so the dangling-edge scan in
`RuleEffect.getRemovedEdges` costs one lookup per removed node. `AlgebraFamily.getOperation`'s
three-level lookup is resolved once at search-item construction. `RuleEffect`'s
alias-then-copy discipline (`:344-421`) avoids copies in the single-event case.
`TreeHashSet.put` is allocation-free on the lookup path and calls `getCode` once per
element. `GTS.getStateCount(Flag)` is an array read. All `DEBUG` flags on the hot path
are `static final … = false`; no `System.Logger` use under `lts`, `explore/engine`,
`match`, `transform` or `control`; no `Collections.synchronized*`, `CopyOnWrite*` or
`volatile` on the exploration path. `Thread.isInterrupted()` in the main loop is a field
read on JDK 21. `CacheReference.updateCleared` (`:182-194`) locks the holder but runs on
the exploration thread itself, uncontended.

## Gates when implementing

- Anything touching `match`, `graph/iso`, `lts`, `transform` or `control/instance`:
  `determinism-check` skill and `grammar-smoke` skill (state and transition counts must
  be identical). Several suggestions (5.1, 5.2, 2.4, 5.7) change which candidate set is
  iterated or what certificate values are, so match discovery order and certificate
  collisions change; `MatchCollector.canonicalise` is supposed to neutralise the former,
  but this is exactly the area of the 2026-08 rule-anchor instability.
- `null-check` skill on every touched Java file.
- Before-and-after numbers from the throughput harness, and `Generator -v 3` to confirm
  "Confluent:" becomes non-zero after 2.1.

## Building a throughput harness

### What exists

`ExplorationTest` drives `Exploration` directly on `junit/samples` grammars and asserts
state and transition counts; it carries `SlowTest` and runs under Surefire, i.e. with
assertions enabled, so its timings include the section 6 costs. `StatisticsReporter`
already computes a time breakdown from the static `Reporter` counters (matching from
`PlanSearchStrategy.searchFindReporter`, iso checking from `IsoChecker.getTotalTime()`,
generation from `MatchApplier.getGenerateTime()`, self-time from
`Reporter.getReportTime()`, `StatisticsReporter.java:325-350`), printed by
`Generator -v 3`. `test/performance` holds collection micro-benchmarks with hand-rolled
`currentTimeMillis` loops. There is no JMH dependency.

### The harness (built 2026-09-20)

`test/performance/ExplorationBenchmark` implements the shape described below: a fixed
list of configurations over `junit/samples`, grammar compiled once per configuration,
a fresh `GTS` and `Exploration` per run, `play()` on a watchdog-timed thread, warm-ups
discarded, median/min/max wall time over the measured runs, deltas of the static
`Reporter` counters, per-thread allocated bytes, retained heap after two GCs with the
GTS still referenced, and the per-GTS `HostFactory` node and edge counts (the signal for
3.3). Expected counts are asserted on every measured run. Three entry points:

- `main` (config names as arguments, `-Dgroove.bench.warmups`, `-Dgroove.bench.runs`,
  `-Dgroove.bench.timeout` in seconds, optional `-Dgroove.bench.csv=<file>`), with the
  Eclipse launch `GROOVE - exploration benchmark` (`-da -Xmx4g -XX:+UseParallelGC`).
- `@Test smoke()`: every `smoke` configuration once, no warm-up; runs in the full suite.
- `@Test benchmark()`: inert unless `-Dgroove.bench.run=<names|true>`; the Maven route,
  `mvn -q test "-Dexcluded.test.groups=" -Dtest=ExplorationBenchmark
  -DenableAssertions=false "-Dgroove.bench.run=true" > bench.log 2>&1`. Surefire honours
  `enableAssertions=false` (the header line says `Assertions: disabled`); the pom's
  `argLine` is untouched.

The product `module-info` gained `requires java.management; requires jdk.management;`
for the harness alone: Surefire and Eclipse patch the test tree into the module, and
there is no test-only way to add a `requires` that both accept. Nothing in `src/main`
uses either module. `requires static` plus `--add-modules` in the Surefire `argLine` and
the launch would keep the product module clean at the cost of two more moving parts;
not done, open for review.

Calibration facts, single cold runs at `-Xmx2g` unless noted:

- **`collapse=none` cannot be configured**: `ExploreKey.COLLAPSE` accepts
  `grammar|equality|isomorphism|hash`, and `ConfiguredExploreType:116` rejects
  `COLLAPSE_NONE` and `COLLAPSE_ISO_WEAK` as unrealisable. The "long delta chains without
  collapsing" case for 2.5 to 2.7 is therefore not expressible from a configuration;
  `binary-tree-dfs12` (depth-first with a depth bound) is the nearest substitute.
- **Depth-first rejects a node bound** (`ExploreTypeConverter:168`); the only DFS-compatible
  bound is `next=newest cost=uniform bound=cost:N`. `bound=size:` is unsupported.
- **Dead candidates**: every `leader-election` start graph except `start-2` (including
  all `-init` graphs), `pacman start_four_ghosts`, `recipes`, `transactions`,
  `attribute-count-to-n`, `fibonacci` and both `exploreCache` grammars explore to one to
  six states. So the attribute-heavy, symmetric and recipe-transience cases are still
  uncovered and need new grammars, as anticipated above.
- **Heap**: `car-platooning start-06`, `sierpinsky start13`, `generate-binary-tree` at
  depth 14 or more and `bound=nodes:40` thrash at 2 GB and never finish; under GC thrash
  the watchdog thread is starved too, so the timeout does not fire. The launch uses 4 GB.
- **Run order matters**: `car-platooning-05` measures 5.2 s as the first configuration in
  a JVM and 12 s as the ninth, consistent with the megamorphic dispatch of 4.1.9 being
  polluted by the preceding grammars. Compare like-for-like orderings only, or one
  configuration per JVM.

Baseline, 2 warm-ups and 3 measured runs, OpenJDK 25.0.4, `-da -Xmx4g -XX:+UseParallelGC`,
all nine configurations in one JVM in table order (non-timing columns from the median run):

```
config                  states    trans   med ms   min ms   max ms  states/s   match     iso    cert     gen  allocMB    retMB  fNodes   fEdges
inheritance                756     5374     71.1     65.7    106.5     10630       0       8       5      62     52.1      9.5       7       10
pacman                     256     1536     47.6     41.7     50.5      5380       2      19       9      33     41.9      4.5      20      196
as-and-bs                 8240    44774    615.2    497.3    623.8     13395      83     150      80     451    519.8     72.2       6       27
sierpinsky-11               12       11   1811.7   1798.4   2449.4         7     175       0       0    1497    920.2    417.3  265734   841476
binary-tree-dfs12         4012    22188   1238.4   1173.3   1325.5      3240      11     920     658    1193    583.3     39.9     239      596
append-4-list-8          31104   114008   4379.0   4364.7   4427.4      7103     979    1174    1092    3062   3141.3    351.4      67      293
append-4-list-8-equality 73792   268912  10971.6   9606.6  26068.7      6726    2030       0       0    8208   5634.6    804.4      74      357
mark-unmark              24576   368640   9552.0   8838.2   9848.2      2573    1666    2120    1898    7152   6236.6    483.1      15       30
car-platooning-05       110366   369601  12046.6  10248.2  85255.8      9162    2955       0       0    7392   7927.2    815.0       5      215
```

The confluent-diamond count is 0 in every row, as 2.1 predicts. The factory edge count
of `car-platooning-05` (215 edges minted for a 5-node factory) and `pacman` (196) is
3.3 showing. `sierpinsky-11` is the large-graph case (265 k nodes, 841 k edges in the
final graph) and is dominated by `gen`, i.e. transformation and reconstruction, not
matching.

### Shape of the harness (as designed)

A runner in the test tree, `test/performance/ExplorationBenchmark` or similar, with a
`main` so it runs outside Surefire (no `-ea`) and a JUnit entry under `SlowTest` for
Eclipse. It should:

- Load each grammar once and build a fresh `Exploration`/`GTS` per run, as
  `ExplorationTest.testExploration` does, so grammar loading and plan construction stay
  out of the measurement (plans are built per `Matcher` and survive across runs of the
  same `Grammar`; a fresh grammar per run would measure them too, which is a separate,
  much smaller number).
- Per configuration: two discarded warm-up runs, then five measured runs in the same JVM,
  reporting the median and the spread. Assert the state and transition counts on every
  run, so the harness doubles as a regression check for the section 5 changes.
- Report per configuration: wall time, states and transitions per second, the
  `Reporter` breakdown (deltas of the static counters, which accumulate across runs and
  are never reset), allocated bytes via `com.sun.management.ThreadMXBean.getThreadAllocatedBytes`
  (the cheapest allocation-rate signal and the one most of section 4 changes), and
  retained heap after an explicit GC with the GTS still referenced (the signal for 3.4,
  3.5, 3.6 and 4.3.2).
- Run with a fixed heap and GC (`-Xmx2g -XX:+UseParallelGC` or whatever is chosen, but
  the same every time) and record the JVM flags in the output. Interleave configurations
  across two JVM forks when comparing branches, since JIT profile pollution from one
  grammar affects the next in the same JVM.
- Once 1.1 lands, run each configuration twice: with the profiling property on for the
  breakdown and off for the headline number.

JMH would give the warm-up, forking and statistics for free at the cost of a test-scope
dependency and its annotation-processor build step; the plain runner reuses the
`ExplorationTest` plumbing and needs nothing new. Start plain, move to JMH if run-to-run
noise turns out to be above a few percent.

### Do the sample grammars cover the sensitive points?

Mechanism coverage is good. Across the 57 grammars in `junit/samples` (plus 44
single-feature grammars in `junit/rules`) there are NACs (about half), quantifiers
(`leader-election`, `petrinet`, `pacman`, `sierpinsky`, `subsets`, `forallCount`,
`quantifierCounter`), attributes (`fibonacci`, `attribute-count-to-n`, `attributes`,
`control2`, `leader-election`), control programs in about ten grammars (`control`,
`control2`, `attributes`, `fibonacci`, `recipes`, `transactions`, ...), recipes and
priorities (`recipes`, `recipe-priorities`, `priorities`), regular expressions
(`As-and-Bs-reg-exp-benchmark`, `regexpr`, `basic-regexp`, `Mark-Unmark-List`),
DPO and injective matching (`car-platooning`, `simpleCheckDanglingEdges`,
`simpleInjective`, `injective-nac`), multigraph semantics (`parallel-pump`,
`parallel-pump-spo`), and mergers (`mergers`).

Scale is the gap, on three axes:

1. **State-space size.** Every `ExplorationTest` configuration except
   `As-and-Bs-reg-exp-benchmark` (8 240 states, 44 774 transitions) is below 1 000
   states, where warm-up dominates and per-state fixed costs are invisible. Larger start
   graphs are already there but unused by the test: `append-4-list-8`, `car-platooning
   start-18`, `leader-election start-10-init`, `petrinet start2`, `pacman
   start_four_ghosts`, `sierpinsky start13`, `regexpr list-8`. Together with `bound`
   and depth options these should give 10^4 to 10^6 states per configuration.
2. **Graph size.** The largest start graph has 37 nodes and 83 edges (`petrinet
   start2`); nothing has hundreds of nodes or a high-degree hub. The
   wrong-quantity findings (3.1 certifier array, 3.7 in-edge store, 4.3.2 per-node
   sets) and the candidate over-approximation (5.1, 5.2) only show on graphs of
   hundreds to thousands of nodes with a few hubs. This needs one or two dedicated
   grammars, e.g. a large grid or list with a shared hub node and rules that bind the
   hub first.
3. **Symmetry.** 5.6 (individualise-and-refine without automorphism pruning) needs
   graphs with large automorphism groups: a ring of N identical processes
   (`leader-election` with N of 8 or more), N philosophers, or a set of N identical
   unconnected components. The current `leader-election start-2` has none.

Beyond scale, some individual findings need a configuration that the samples do not
exercise together:

- **Diamond-rich lattices** for 2.1: independent rules whose applications commute
  (`simple`, `inheritance` at 756 states / 5 374 transitions, `loose-nodes`, `counting`
  extended); check that "Confluent:" turns non-zero and that state creation drops.
- **Long delta chains without collapsing** for 2.5 to 2.7: DFS or linear exploration
  on `generate-binary-tree` or `append-4-list-8` with `collapse=none`, deep enough that
  reconstruction walks hundreds of deltas.
- **Equality collapse mode** for 4.4.4 and the `-ea` doubling of section 6.
- **Attribute-heavy state spaces** for 4.2.1 to 4.2.3: `attribute-count-to-n` and
  `fibonacci` bounded to large N, plus a grammar whose guards probe undefined
  operations (division, `ite` over errors) so `ErrorValue` construction is on the path.
- **NAC-heavy matching** for 3.3 and 4.1.1: `car-platooning start-18`,
  `circular-buffer`, `ferryman`; monitor `HostFactory` edge count growth across the run
  for 3.3.
- **Recipes with transience** for 4.3.1 and 1.5: `recipes`, `transactions`.

Recommendation: a benchmark set of about ten configurations, half from existing
grammars with the larger start graphs, plus two or three new grammars under
`junit/performance/` (large graph with hubs, symmetric ring, attribute counter), each
sized for 5 to 60 seconds on the development machine. Record the baseline numbers in
this note before the first change lands.
