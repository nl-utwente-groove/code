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

1. Build the throughput harness, so the rest can be measured. Done 2026-09-20.
2. Fix the `Factory` user leak (3.11): a genuine unbounded leak, found by the harness,
   which also caps how long any benchmark run can be. Its own branch, since it is a
   bug fix independent of the rest.
3. Section 1 (always-on instrumentation and locks): two-line changes in the innermost
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
default capacity. *Measured 2026-09-22* (`hub-ring-1000-counted` against `-unstored`,
section "The hub field and ring rows"): in the exploration's swing mode the store, once
built, travels along the materialisation chain, so the walk runs once per chain rather
than once per erase, and a `let:` per move costs 4 µs on a 7 µs step with nothing of it
attributable to the store. Demoted to Low for exploration; the filter remains a
simplification worth making when the file is touched.

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

**3.11 `Factory` dependency tracking leaks every rule application into the `Grammar`.**
High (memory, unbounded) / small / verified by measurement. `util/Factory.java:46-55,
111-119,127-133,172-186`: `Factory.lazy` registers the factory being built in the static
`builders` set for the duration of `create()`, and every `Factory.get()` executed
meanwhile calls `addBuilders()`, which adds the builder to the read factory's strong
`users` set (and the read factory to the builder's `used` set), so that `reset()` can
propagate. `RuleApplication` wraps `match`, `morphism`, `effect` and `comatch` in
`Factory.lazy` (`transform/RuleApplication.java:296,317,436,720`), and `applyDelta`
(`:466`) and `getTarget` (`:249`) call `getRule().isModifying()` inside those builds, a
grammar-lifetime factory (`grammar/Rule.java:689`; `creatorNodes`, `creatorEdges`,
`eraserNodes`, `eraserEdges`, `mergers` likewise). So every rule application becomes a
permanent strong user of seven `Rule` factories, about 1.4 KB retained per application,
and nothing on any exploration path calls `reset()`. Measured on `generate-binary-tree`:
537 MB after one run of 409 k applications with only the `Grammar` alive, 1074 MB after
two, 1587 MB after three; 78 % of the persistent `binary-tree-dfs12` run's retained
heap is the same leak. It outlives the GTS, so the GUI, which keeps one grammar across
explorations, leaks across every Explore. Suggestion: the dependency tracking is meant
for grammar-level factories that reset each other; per-application memo fields should
not take part. Either give `RuleApplication` plain lazily-initialised fields, or add a
`Factory` variant that neither registers as a builder nor records users, and use it for
every factory whose owner is shorter-lived than the factories it reads. Also worth a
`DEBUG`-guarded size assertion on `users`, since the leak was invisible at test scale.
Related: 1.3 (the same `get()` also takes the global lock).

**3.12 `StateCache` keeps the transitive closure of every transient region.** High
(time and memory, quadratic) / medium (design) / verified by profile.
`lts/StateCache.java:455-503,560-640,651-686`. Every transient or inner state's cache
holds seven `HashSet`s of caches: `backInner`, `forwInner`, `backLaunch`, `forwTarget`,
`backTransient`, `forwTransient` and `forwTransientOpen`, and `registerOutPartial`
maintains them as full transitive closures: a new partial transition adds the target's
forward closure to every backward-reachable state and the source's backward closure to
every forward-reachable state (`:601-605,621-625`), and closing a state removes it
again from the sets of its whole backward closure (`:653,659,672,678`). A transient
region of n states with a path through it therefore costs O(n²) set entries and O(n²)
insertions plus removals. The closures serve `getAbsence()` (read on the fly by
`StateMatches.advanceFrame`, `lts/StateMatches.java:136`), fullness (`isFull`, read by
`GTSCounter`, `RecipeTransition.getSteps` and the GUI) and the launch-to-target pairing
that creates recipe transitions.

Measured on the fibonacci recipe rows, where the outer `fib(result, out result)` call
makes the whole run one recipe invocation with a near-linear chain of transient inner
states: `fib-15` (4 934 discovered states, a final GTS of 3 states) takes 23.5 s, 16.4 s
of it in the `gen` column, and allocates 4.7 GB; a JFR profile of that run puts 97 % of
the samples in `HashMap.putVal`/`removeNode`/`resize` under `registerOutPartial`
(50 %: the inner closure at `:601-605` 19 %, the transient closure at `:621-625` 28 %)
and `testSetFull` (27 %, the removal lambdas at `:672,678`) plus
`removeFromForwTransientOpen` (10 %); allocation is 97 % `HashMap$Node`,
`HashMap$KeyIterator` and `HashMap$Node[]`. This is the recipe-path superlinearity noted
under "The performance grammar set" (4.5 times a plain state at `fib-12`, 60 times at
`fib-15`, out of heap at `fib-17`), and it is why the function variant of the same
recursion is 60 times faster. Any recipe or atomic block whose body runs for many
steps pays it (a loop inside a recipe over a large graph, a recipe that builds a
structure); recipes of a few steps do not, which is why the samples never showed it.
Suggestion: replace the eager closures by propagation over direct transient
predecessor edges: a state is full when closed and all its direct transient successors
are full (notify predecessors on becoming full); absence is the minimum over direct
successors, propagated backwards on decrease (monotone, so it terminates); a newly
discovered recipe target is propagated backwards through inner predecessors to the
launches. That is O(transitions) amortised on acyclic regions. Cycles inside a
transient region (`alap`/`while` in a recipe or atomic block) defeat the local
fullness rule, so a fallback is needed: on closing a state whose direct successors are
all closed but not all full, search forward over closed non-full transient states and,
if no open state is found, mark the whole visited set full. 9495647a2 (2025-03-24,
"Resolved transience bug in recipe exploration") introduced the current closures
without a message; the bug it fixed must be identified before a redesign, since the
local rule may be what it replaced. Gates: `grammar-smoke`, the `control` and
`transactions` tests, `DeterminismTest`, and the fibonacci and `recipes` rows.

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
  Without names, `-Dgroove.bench.tier=quick|long|all` selects the tier (added
  2026-09-22, see "The long-run tier" below); `quick` is the default and excludes the
  long tier.
- `@Test smoke()`: every configuration of the `SMOKE` tier once, no warm-up; runs in
  the full suite.
- `@Test benchmark()`: inert unless `-Dgroove.bench.run=<names|true>`; the Maven route,
  `mvn -q test "-Dexcluded.test.groups=" -Dtest=ExplorationBenchmark
  -DenableAssertions=false "-Dgroove.bench.run=true" > bench.log 2>&1`. Surefire honours
  `enableAssertions=false` (the header line says `Assertions: disabled`); the pom's
  `argLine` is untouched.

The harness reaches the two management beans it uses (`RuntimeMXBean.getInputArguments`
for the JVM flags, `com.sun.management.ThreadMXBean.getThreadAllocatedBytes` for the
allocation column) reflectively, in a small helper that degrades to "n/a" and -1. The
test tree is patched into the product module, so direct use would have needed
`requires java.management; requires jdk.management;` in the product `module-info` for a
test-only purpose (tried, rejected). Reflective access needs the packages exported,
which they are, but no readability. At run time both modules resolve under Surefire and
under a plain class-path launch without flags, since the booter starts from the class
path and `java.se` is then a root; only the Eclipse launch, where the application module
is the root, carries `--add-modules=java.management,jdk.management`.

The harness loads the `SystemStore` once per configuration and builds a fresh `new
GrammarModel(store)` for every run, which `ExploreType.newGTS` compiles under the
type's overrides (the algebra family, gh #923; since 2026-09-22, before that the
harness compiled the model itself and built the `GTS` directly, which the fixed
`algebra` key rejects), outside the timed region, so each run starts without the state
that 3.11 leaks into the `Rule` objects. `GrammarModel.toGrammar()`
caches its `Grammar`, and the `RuleModel`s cache their `Rule`s for the model's lifetime,
so a fresh model is the only unit that isolates runs. Consequence: search plans, built
lazily on first match, are built inside the timed region on every run; milliseconds
against seconds, equal for all runs, accepted.

Calibration facts, single cold runs at `-Xmx2g` unless noted:

- **There is no `collapse=none` value, by design.** The collapse feature
  (`explore/feature/Collapse`) offers `grammar|equality|isomorphism|hash`, where `hash`
  is still converter-rejected as unsupported. Not collapsing is a consequence of other
  features, not a choice (feature-model decisions of 2026-07-31, see
  `claude/archive/exploration-feature-model-plan.md`): the linear strategies
  (`frontier=single successor=single`) switch the record's collapse flag off in
  `LinearStrategy.prepare`, and `persistence=none` never enters discovered states into
  the state set, so there is no revisit detection at all. `GTS.CollapseMode.COLLAPSE_NONE`
  is the internal state those two produce; `ConfiguredExploreType.stateExploration`
  throws on it only because it reconstructs a configuration from a GTS and no
  configuration expresses that mode. Consequences for the findings: `sierpinsky-11`
  (linear) runs with collapse off in the record, so `StateCache.freezeGraphs` is false
  there and 2.5 applies; `binary-tree-dfs-unstored` (`persistence=none`) explores the
  full tree unfolding, but `setPersistent` only flips the GTS storing switch and leaves
  the record's collapse flag on, so freezing stays enabled and only the chain findings
  2.6 and 2.7 apply to it.
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
  configuration per JVM. (Part of the effect measured before the fresh-`Grammar`-per-run
  change was the 3.11 leak accumulating across configurations; re-measure.)
- **A long-run tier is wanted but blocked by 3.11.** Runs of two to five minutes would
  show the wrong-quantity findings (3.1, 3.3, 3.4) and average out GC and JIT noise
  better than repeated short runs; candidates are `car-platooning start-06`,
  `sierpinsky start12`, the unstored binary tree at depth 9 (about 140 s) and As-and-Bs
  under `collapse=equality` (223 k states in 60 s). But at 1.4 KB leaked per rule
  application a 3 M-application run leaks over 4 GB before it ends, so the tier is
  added once the leak is fixed, calibrated at a larger heap, with fewer repetitions.

Baseline on the desktop (the measurement machine: 20 cores, 32 GB), 2 warm-ups and 3
measured runs, OpenJDK 25.0.4.1, `-da -Xmx4g -XX:+UseParallelGC`, all sixteen
configurations in one JVM in table order, fresh grammar per run, taken 2026-09-21 at
`4f9f63bc5`, after the gh #919 fix (non-timing columns from the median run; `states` and
`trans` are the stored counts, `disc.st` and `disc.tr` the discovered ones):

```
config                  states    trans  disc.st   disc.tr   med ms   min ms   max ms  states/s   trans/s   match     iso    cert     gen    rep   confl  allocMB    retMB  fNodes   fEdges
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
inheritance                756     5374      756      5374     30.9     30.3     43.0     24494    174113       2       3       3      18      0       0     44.6      2.8       7       10
pacman                     256     1536      256      1536     33.7     30.4     35.1      7588     45528       3      15       6      25      0       0     38.4      1.2      20      228
as-and-bs                 8240    44774     8240     44774    226.7    204.8    233.5     36352    197528      28      90      43     166      3       0    480.8     34.1       6       27
sierpinsky-11               12       11       12        11    688.5    682.0    795.1        17        16      85       0       0     531      0       0    876.4    418.2  265734   841476
binary-tree-dfs12         4012    22188     4012     22188    568.8    566.9    597.6      7053     39005       9     461     333     550      0       0    547.4     16.9     239      596
append-4-list-8          31104   114008    31104    114008   1755.1   1729.9   1773.8     17722     64958     455     600     551    1191      1       0   2954.9    228.8      67      293
append-4-list-8-equality   73792   268912    73792    268912   3546.5   3520.8   3623.2     20807     75824     815       0       0    2387      7       0   5195.1    527.3      74      357
mark-unmark              24576   368640    24576    368640   2350.4   2314.4   2629.9     10456    156840     414     712     616    1711      2       0   5496.9    146.4      15       30
car-platooning-05       110366   369601   110366    369601   3212.5   3164.8   3544.5     34355    115050     953       0       0    1671      6       0   7449.7    539.9       5      215
binary-tree-dfs-unstored       9        8   409114    409113   1331.0   1320.0   1477.7    307377    307377     108       0       0     963      1       0   2719.5   1111.1    1023     2556
mark-unmark-18           48384   870912    48384    870912  11554.7  11261.0  11823.8      4187     75373    1103    7414    5695    9904     21       0  15650.7    366.4      18       36
mark-unmark-21          169344  3556224   169344   3556224  61375.0  60199.0  62614.6      2759     57943    5069   43363   33369   54135     72       0  74976.7   1277.7      21       42
as-and-bs-4-3           131505   947824   131505    947824   5724.5   5605.1   5818.8     22972    165574     868    2286    1280    4142     17       0  11029.6    569.7       7       35
inheritance-12          297212  4317133   297212   4317133  21113.4  20954.1  21234.5     14077    204473     690    5061    4016   18449     63       0  37537.0   1738.1      12       21
append-4-list-10       1077000  4008820  1077000   4008820 206111.9 205904.5 218017.8      5225     19450   30015   59844   59839  157800     49       0 123451.0    434.5     253     1599
pacman-four-ghosts      210102  7819623   210102   7819623 143143.1 141930.7 143631.3      1468     54628    3057   78126   39730  124697    196       0 226271.6   1892.5      24      441
```

The whole run took 39 minutes, 29 of them in the two long-tier rows `append-4-list-10`
and `pacman-four-ghosts` (five runs each of 206 s and 143 s); the long tier of the next
step should therefore run with fewer repetitions. Against the laptop table this
replaces (in the history of this file, before `4f9f63bc5`), three things changed:

- **The spread is gone.** The maxima of `append-4-list-8-equality`, `car-platooning-05`
  and `binary-tree-dfs-unstored`, previously two to eight times the median, are now within
  about 10 % of it; every row's `max ms` is within 12 % of `med ms`, and the ten shared
  rows are 2.5 to 4 times faster, which is the machine, not the fix.
- **Retained heap dropped by a third to two thirds on the heavy rows** (`mark-unmark`
  472 to 146 MB, `append-4-list-8-equality` 812 to 527 MB, `car-platooning-05` 817 to
  540 MB, `binary-tree-dfs-unstored` 1416 to 1111 MB), not to nothing: the remainder is
  the softly reachable caches of finding 3.6, which the unstored row's retention
  investigation below already put at about 60 % of its figure.
- **Isomorphism checking dominates the large runs**: `iso` is 70 % of `mark-unmark-21`,
  55 % of `pacman-four-ghosts` and 29 % of `append-4-list-10`, with certificates
  (`cert`) about half to two thirds of that. This puts sections 2.3, 2.4 and 4.4 ahead of
  the matching items for the large-state-space rows; `confl` is zero on every row, as
  finding 2.1 predicts.

With `persistence=none` the GTS retains only the nine states of the written-back trace,
so the harness counts discovered states and transitions through a `GTSListener`
registered before the start state materialises, reports stored and discovered side by
side, and asserts the pinned counts against the discovered ones; for the nine
persistent configurations the two are equal.

**The maxima of the last three rows of the laptop table were GC thrash, not noise.** Before
the gh #919 fix, a single run of `binary-tree-dfs-unstored` made about 1.4 GB live (the
3.11 leak plus soft caches) in a 3.6 GB heap; `-Xlog:gc` showed 48 full collections and
26 s of pause in a 40 s JVM, no single pause above a second. The fresh grammar per run
stopped the leak accumulating across runs, but did not make one run fit. The desktop
table above, taken after the fix, has the maxima back within noise.
`bound=cost:N` still terminates the run under `persistence=none`, at a pure tree unfolding
(transitions = states − 1), growing about eightfold per level: depth 8 takes 3 s, depth 9
about 140 s, so there is nothing in between.

**The 1.44 GB retained by that row** (after two GCs, GTS referenced, nine stored states)
was investigated with a three-point retention measurement and a class histogram. It is
not the frontier pool (empty after `play()`), not the event pool (511 entries), not the
created-node map of 3.4 (511 entries) and not `ExploreResult` (empty). Every one of the
409 114 discovered states is alive with its cache, plus one `RuleApplication` and
`RuleEffect` per transition. Two holders, independent of each other:

- **Soft, about 880 MB, rooted in the surviving states** (`Exploration.lastState` and the
  trace tip). `AbstractGraphState.setClosed` correctly skips storing transition stubs
  under `persistence=none`, so a closed state does not pin its successors. Its cache
  does: `StateCache.stateMatches` → `StateMatches.matcher` → `MatchCollector.parentTransMap`
  (finding 3.6) holds the parent's whole transition map, so from one surviving leaf the
  path climbs via `source`, fans out through the parent's map to every sibling, and
  descends through each sibling's cache: the entire tree. All links are soft
  (`setFull` → `setCacheCollectable`), and forcing soft-reference clearing drops the
  figure from 1416 MB to 536 MB, so this is reclaimable, but it is exactly the waste
  3.6 describes, kept for a diamond check that 2.1 shows never fires.
- **Hard, about 537 MB, rooted in the `Grammar`: finding 3.11 below.** Survives the
  GTS and the exploration; grows linearly with rule applications across every run that
  shares the `Grammar`.

Per state the two runs are alike: 2.3 KB per stored state in `binary-tree-dfs12`
(of whose 40 MB, 31 MB is the same hard leak) against 2.25 KB per discovered state
unstored. The harness's `retMB` column therefore overstates hard retention by the
softly reachable part, and the run-to-run spread of the unstored row (2.4 s to 25 s)
is the leak accumulating over seven runs on one compiled grammar; the harness now
compiles a fresh `Grammar` per run.

The confluent-diamond count is 0 in every row, as 2.1 predicts. The factory edge count
of `car-platooning-05` (215 edges minted for a 5-node factory) and `pacman` (196) is
3.3 showing. `sierpinsky-11` is the large-graph case (265 k nodes, 841 k edges in the
final graph) and is dominated by `gen`, i.e. transformation and reconstruction, not
matching.

### The performance grammar set (2026-09-21)

The harness now reads `junit/performance/`, a copy of the eight sample grammars it used,
kept apart from `junit/samples` so that the correctness fixtures and their test
expectations stay untouched and the performance copies can drift freely. Each copy holds
its default start graph, the largest one the samples had, and the generated larger ones;
the other sample start graphs were dropped. `junit/performance/generate-starts.py`
generates the larger start graphs of the four grammars whose start graphs are regular
(Mark-Unmark: a complete binary `next`-tree; As-and-Bs: complete bipartite `b` edges;
inheritance: a typed ring with chords; append: a longer list with more appenders); its
`SIZES` table is the record of what is generated.

Calibration on the laptop (JDK 25, `-Xmx8g -da -XX:+UseParallelGC`, headless
`Generator`, one run each, wall time including JVM start; the machine was shared with
other builds, so the times are indicative only and the desktop baseline is the one to
record):

| start graph | states | transitions | s | kept |
|---|---|---|---|---|
| Mark-Unmark `tree-18` | 48 384 | 870 912 | 19 | quick tier |
| Mark-Unmark `tree-20` | 112 896 | 2 257 920 | 55 | dropped |
| Mark-Unmark `tree-21` | 169 344 | 3 556 224 | 84 | upper quick tier |
| As-and-Bs `start-4-3` | 131 505 | 947 824 | 12 | quick tier |
| As-and-Bs `start-5-3` | > 1.7 M | > 15 M | killed at 360 | too large |
| As-and-Bs `start-4-4` | > 2.2 M | > 16 M | killed at 360 | too large |
| inheritance `start-10` | 36 193 | 418 212 | 6 | dropped |
| inheritance `start-11` | 74 868 | 939 355 | 10 | dropped |
| inheritance `start-12` | 297 212 | 4 317 133 | 40 | quick tier |
| append `append-4-list-10` | 1 077 000 | 4 008 820 | 153 | long tier |
| append `append-5-list-6` | > 1.5 M | > 6.4 M | killed at 360 | too large |
| append `append-5-list-8` | | | killed at 480 | too large |
| append `append-4-list-12` | > 1.5 M | > 5.7 M | killed at 360 | too large |
| pacman `start_four_ghosts` (hand-made, 24 nodes) | 210 102 | 7 819 623 | 203 | long tier |

Growth is steep in every family: As-and-Bs jumps from 12 s to beyond 6 minutes at the
next size in either direction, so the tiers cannot both be served from one family
without an intermediate edge density; Mark-Unmark grows about 2.3 times in states and
2.9 times in time per two levels, so `tree-22` (about 2 minutes) or `tree-23` would
serve the long tier, unmeasured. The pacman four-ghost graph at 20 positions was far
too large (247 k states and 5.2 M transitions after 3 minutes, 67 k open; about 21
transitions per state against 3.3 for `car-platooning-05`).

**leader-election (added 2026-09-21)**, the symmetric-ring case of finding 5.6. The
sample's hand-drawn `-init` start graphs, meant to skip the factorial number-picking
stage of the plain ones, carry `type:` and `flag:` prefixes that the rules do not use
(the rules and the plain graphs have unprefixed `Process` and `active` self-loops), so
they explore to a single state, in `junit/samples` as well; only `start-2` is pinned by
a test. `generate-starts.py` now produces `ring-N`: the plain start graph after number
picking, with the values assigned around the ring in a fixed pseudo-random order and the
`Numbers` pool empty. The hand-drawn graphs were dropped from the copy. Desktop
calibration, single cold runs at `-Xmx8g -da -XX:+UseParallelGC`, headless `Generator`,
exploration time as reported:

| start graph | states | transitions | s | kept |
|---|---|---|---|---|
| `ring-8` | 820 | 3 405 | 0.3 | smoke |
| `ring-10` | 3 142 | 16 491 | 0.7 | dropped |
| `ring-12` | 12 560 | 82 529 | 2.0 | dropped |
| `ring-14` | 49 620 | 386 295 | 7.7 | quick tier |
| `ring-16` | 197 404 | 1 772 291 | 31 | upper quick tier |
| `ring-18` | 787 648 | 7 737 099 | 156 | long tier |

States and time both grow about fourfold per two processes, so `ring-20` would take
some ten minutes.

**attribute-count-to-n and fibonacci (added 2026-09-21)**, the attribute path of
findings 4.2.1 to 4.2.3 and the recipe transience of 4.3.1. Arend's copies carry the
size as a `let:` attribute of the start graph, so `generate-starts.py` produces
`bound-N` (the counter at 0 with bound N) and `fib-N` (the argument node). Desktop
calibration, single cold runs, headless `Generator`, `-Xmx8g` unless noted:

| start graph | states | transitions | s | kept |
|---|---|---|---|---|
| `bound-10000` | 10 001 | 20 001 | 0.6 | smoke |
| `bound-100000` | 100 001 | 200 001 | 5.9 | quick tier |
| `bound-300000` (at `-Xmx4g`) | 300 001 | 600 001 | 34 | upper quick tier |
| `bound-1000000` | | | out of heap | too large |
| `fib-12` | 3 | 2 | 0.9 | smoke |
| `fib-15` | 3 | 2 | 19 (same at `-Xmx4g`) | quick tier |
| `fib-17` | | | out of heap at 8 GB | too large |
| `fib-20` | | | out of heap after 8 500 transient states | too large |

The counter is linear in states and cheap per state; the million is about 7 GB of live
GTS, which is the ordinary per-state cost, not a leak. Its allocation is not linear,
though: the harness reports 41 GB allocated for `bound-100000` and 352 GB for
`bound-300000`, 0.4 against 1.2 MB per state, so something on the path allocates in
proportion to the state count per step (the value-node factory, at 300 008 nodes, is the
first suspect; unmeasured). A BigInteger row was planned on the same start graph, but
**the exploration key `algebra=big` is broken**: with it the `Generator` explores
`bound-10000` to a single state, while `-D algebraFamily=big` (the grammar property)
gives the full 10 001; presumably the start graph keeps the grammar family's value nodes
and the rules' constants are re-interpreted, so nothing matches. Filed as gh #923, fixed the same
day; the rows are in (see "The long-run tier").

**The `probe-odd` rule (added 2026-09-22)** puts `ErrorValue` construction on the path
of every counter row, for 4.2.3. It tests that the value is odd by dividing 1 by
`value mod 2`, so every even state constructs an error (a division by zero: the
`ArithmeticException` thrown by the algebra plus the `ErrorValue` wrapping it, two stack
traces) and every odd state gets a self-loop, the rule modifying nothing. States are
unchanged, transitions rose by half the states (re-pinned). Cost of the probe:
`count-100000` and its BigInteger twin with and without the rule, desktop, 2 warm-ups
and 3 runs at `-Xmx8g`, one JVM per pair, the with-probe pair run before and after the
without-probe pair:

| | without | with, before | with, after |
|---|---|---|---|
| `count-100000` med ms | 5 145 | 5 518 | 5 328 |
| `count-100000` match ms | 301 | 504 | 451 |
| `count-100000` allocMB | 41 126 | 41 581 | 41 583 |
| `count-100000-big` med ms | 5 598 | 5 936 | 6 211 |
| `count-100000-big` match ms | 257 | 646 | 605 |
| `count-100000-big` allocMB | 41 157 | 41 656 | 41 655 |

So the probe costs 4 to 11 % of the row: 150 to 350 ms in the matching column for
100 000 match attempts and 50 000 errors, and 450 to 500 MB of allocation, some 9 KB
per error, which is the two stack traces. That is the size of the signal a fix of 4.2.3
can show here, a few per cent of time and half a gigabyte per 50 000 errors; the rest
of the probe's cost is the 50 000 extra transitions. Every counter figure earlier in
this note, including the `count-600000` row of the long-tier baseline, predates the
probe (`count-600000` with it: 131 s in a single cold run, transitions 1 500 001); the
quick-tier re-baseline of the state file replaces them. **Fibonacci's transience cost is a
finding; its state count is not.** The grammar computes fib(x) by the naive exponential recursion on purpose, so the
number of states grows with fib(x), about 1.6-fold per step, under any of its three
control programs. The default, `fibonacci-recipe`, wraps the recursion in a recipe: the
stored GTS has three states and all the work is in transient states, which the harness
counts as discovered states (1 164 for `fib-12`, 4 934 for `fib-15`; the `fib-N` rows
above are this program). `fibonacci-function` runs the same recursion as a function, so
the same states are plain stored states, and it is the control experiment for the
transience cost (Arend's suggestion, 2026-09-21): same rules, same start graphs, the
same state count and one transition fewer, no transience. (The third program,
`fibonacci-expressions`, hard-codes its argument.) Desktop calibration as above, single
cold runs at `-Xmx8g`, `-D controlProgram=fibonacci-function`, with the recipe program
rerun in the same session for the ratio:

| start graph | states | transitions | s | recipe s | kept |
|---|---|---|---|---|---|
| `fib-12` | 1 164 | 1 163 | 0.19 | 0.84 | |
| `fib-15` | 4 934 | 4 933 | 0.30 | 17.9 | smoke |
| `fib-17` | 12 919 | 12 918 | 0.57 | out of heap | |
| `fib-20` | 54 729 | 54 728 | 1.2 | out of heap | |
| `fib-22` | 143 284 | 143 283 | 2.6 | | quick tier |
| `fib-25` | 606 964 | 606 963 | 10.4 | | fits 8 GB only |
| `fib-27` | | | out of heap at 8 GB | | too large |

`fib-25` is out of heap at `-Xmx4g` (the harness retains 5.2 GB after the run), so the
quick-tier row is `fib-22`. The harness breakdown says where the recipe's time goes: of
`fib-15`'s 17.7 s, 13.7 s is in the `gen` column (state generation, after matching),
against 0.1 s for the function program; matching and isomorphism are under 40 ms in
either. So a transient state costs 4.5 times a plain state at `fib-12` and 60 times at `fib-15`,
and the ratio grows with the size: something on the recipe path is superlinear in the
transient prefix. The recipe's time does not change between 4 and 8 GB, so it is not
collector thrash. This was the ground of 4.3.1 (and possibly 3.6, the parent transition
map); the investigation of 2026-09-22 found it in the transient closures of
`StateCache`, finding 3.12, which must be fixed before a long-tier size exists for the
recipe family. The function family has no long-tier size either, for the ordinary reason:
`fib-27` is about 1.6 million states and, like the counter's million, does not fit 8 GB
of live GTS.

**After gh #924 (2026-09-22 evening).** The parallel session that took finding 3.12
replaced the transient closures of `StateCache` by local propagation over direct
predecessor edges (branch `statecache-transient-closures`, merged to master and into this
branch at `da54faa44`; the design and gates are in gh #924 and the commit body). The
recipe family recalibrated on the desktop, single cold runs through the harness, one JVM
per row, JDK 25.0.4.1, `-Xmx8g`, with the function program in the same session for the
ratio:

| start graph | discovered states | recipe s | function s | recipe retMB | kept |
|---|---|---|---|---|---|
| `fib-15` | 4 934 | 0.29 (was 19) | 0.30 | 42 | smoke (both) |
| `fib-17` | 12 919 | 0.52 (was out of heap) | | 111 | |
| `fib-20` | 54 729 | 1.13 | | 474 | |
| `fib-22` | 143 284 | 2.49 | 2.21 | 1 256 | quick tier (both) |
| `fib-25` | 606 964 | 12.3 | | 5 429 | fits 8 GB only |

So the transient state now costs the same as a plain state (the `gen` column of `fib-22`
is 1.7 s against 1.4 s, matching and isomorphism under 100 ms in both), and the recipe
rows mirror the function rows: `fib-15` smoke and `fib-22` quick, the `fib-12` row
dropped. Neither family has a long-tier size, for the ordinary reason above. Every
fibonacci figure earlier in this note, and the recipe rows of the quick-tier table before
the re-baseline below, predate the fix.

**hub (added 2026-09-22)**, Arend's grammar for the large-graph axis: a star of `Leaf`
nodes around one `Hub`, tokens as flags on the leaves, a `build` program that grows the
star from a proto graph carrying the wanted sizes as attributes, and `run`, which moves
a token from its leaf to any empty leaf through the hub. As built at 1 000 leaves and
10 tokens it explores to one state: the leaves are interchangeable, so all 9 900
moves yield isomorphic graphs, and the run took 295 s (linear or breadth-first alike,
the linear strategy still applying every match), of which matching was 22 ms and
certification 219 s, 22 ms per certificate of a 1 001-node graph with a 990-fold
symmetric leaf class. That is finding 5.6 measured, not the hub findings it was meant
for: with every incident edge of the hub a `to` edge in one direction there is nothing
for 5.1 to discard, the plan starts from the ten `token` flags so 5.2 never enumerates
nodes, and `run` erases no attribute edge (3.7). The 1.3 MB allocated per transition is
4.3.2, the per-node edge sets of each materialised graph.

Two shapes were kept, both generated by `generate-starts.py` (the built graph is not
checked in): the star sized down to 300 leaves and 3 tokens as the 5.6 row, and a
*chain* variant, consecutive leaves linked by `next` and a `chain` program whose
`moveNext` rule moves a token one step along the chain and never onto an occupied
leaf, so the leaves are distinguishable and the states are the token placements, n for
one token and about n²/2 for two, every state a graph of n+1 nodes: the many-states,
large-graph rows for 3.1, 4.3.2 and the certifier without symmetry. Desktop
calibration, single cold runs through the harness, `-Xmx8g`:

| row | states | transitions | s | cert ms | kept |
|---|---|---|---|---|---|
| `hub-1000-10` (as built) | 1 | 9 900 | 295 | 218 769 | not kept |
| `hub-star-300-3` | 1 | 891 | 2.4 | 1 521 | smoke |
| `hub-chain-1000-1` | 1 000 | 999 | 7.4 | 7 247 | quick tier |
| `hub-chain-200-2` | 19 900 | 39 402 | 10.9 | 10 092 | quick tier |
| `hub-chain-300-2` | 44 850 | 89 102 | 54.6 | 51 459 | next size, not kept |

Matching is 4 to 200 ms in every row; certification is the whole cost, and it is
superlinear in the graph on the chain too: 0.5 ms per certificate at 201 nodes, 1.15 ms
at 301, 7.2 ms at 1 001, about quadratic, which is the refinement running one round per
step of the chain's diameter with each round a pass over the graph. So the chain rows
measure the certifier's dependence on graph diameter, the star row its dependence on
symmetry (5.6, 2.4), and neither measures 5.1, 5.2 or 3.7; those are the field and ring
rows below.

**The hub field and ring rows (added 2026-09-22)**, the 5.1, 5.2 and 3.7 rows on
unstored runs where nothing is certified. The linear traversal admits no depth bound
(`Traversal.isSearch`), so the rows use the recipe of `binary-tree-dfs-unstored`
(`next=newest cost=uniform bound=cost:N persistence=none`) on *deterministic* systems,
one successor per state, so that the depth-first run is a single path of N steps with
collapsing off. The type graph grew a `from` edge from `Leaf` to `Hub`, a `pos` attribute
on `Leaf`, a `token` flag and a `moves` attribute on `Hub`, and a `Stub` type linked to
`Hub` by `stub` edges in both directions; the generated graphs of the earlier rows were
regenerated with `let:moves=0` on the hub (their counts are unchanged). Two new shapes:

- `field-2-100-2500`: two stars, each hub with 100 leaves linked both ways and numbered by
  `pos`, plus 2 500 stubs, half pointing at the hub and half away from it, which nothing
  ever touches; a token on the first leaf of the first hub and one on that hub. 5 202
  nodes, 10 810 edges, 2.3 MB of GXL, the largest fixture in the set. `hop` moves the leaf
  token to the leaf at `(pos + 1) % leaves`, both leaves bound to the hub by `from`; the
  plan (printed by flipping `PlanSearchEngine.PRINT`) is token, `from` n1 to hub with the
  source bound, `from` n2 to hub with the *target* bound, the NAC, then the `pos` test per
  leaf. The target-bound item enumerates the hub's whole incident set of 2 703 edges (the
  target branch never consults the label set), 100 of which carry the label, for one
  match: finding 5.1 as stated. `jump` moves the hub token to the other hub, a typed
  node without edges in the rule; the plan is token, `Find node n1:[Hub]` over the node
  set of 5 400 (nodes plus value nodes), then the NAC: finding 5.2, two hits.
- `ring-1000-1`: the chain closed, so that a single token walks for ever. `chain`
  (`moveNext`) against `counted` (`moveCounted`: the same rule plus `let:moves = moves + 1`
  on the hub, bound through `to`), the `let:` per move of 3.7.

Desktop calibration, single cold runs through the harness, one JVM per row, `-Xmx8g`:

| row | steps | s | match ms | gen ms | allocMB | retMB |
|---|---|---|---|---|---|---|
| `hub-field-hop` | 100 000 | 6.08 | 5 557 | 230 | 13 504 | 263 |
| `hub-field-jump` | 200 000 | 5.49 | 4 375 | 631 | 1 686 | 522 |
| `hub-ring-1000-unstored` | 200 000 | 1.39 | 162 | 756 | 1 651 | 523 |
| `hub-ring-1000-counted` | 200 000 | 2.21 | 581 | 1 000 | 2 733 | 1 068 |

`hop` is 61 µs per step, 91 % of it matching: 2 703 candidates for one match. Under 5.1(b)
(the size comparison restored) the label route offers 200 candidates, under (a) the 1 350
in-edges, under (c) the 100 `from` edges. The row carries a second lead: it allocates
135 KB per step, and since `jump` allocates 8 bytes per visited node, the garbage is on
the attribute path, about 1.3 KB per leaf candidate that reaches the `pos` test
(`Compute add`, `mod`, `eq` and the value-node lookups behind them). Not yet a numbered
finding. `jump` is 27 µs per step, 80 % matching, 4 ns per visited node; a type index
makes the 5 400 visits two.

The ring pair measures the `let:` at 4 µs on a 7 µs step, split evenly between matching
(the `to`, `moves` and `add` items) and generation (the erase, the fresh value node, the
factory), and none of it is 3.7: in the exploration's swing mode (`Record.copyGraphs`
false) the in-edge store, once built by the first attribute erase, moves along the
materialisation chain with the other three stores (`SwingTarget` takes over the parent's
references), so the O(V+E) walk runs once per chain, not once per erase; it recurs only
after a reconstruction from the delta chain, whose root graph has no store. In copy mode
(the Simulator, `randomAccess`) every child copies all four stores anyway. So 3.7 is
demoted to Low for exploration; the suggested filter stands as a simplification. The
counted row also retains 5 KB per step: the host factory keeps every value node and
`moves` edge it ever made (200 k and 400 k by the `fNodes`/`fEdges` columns, which are
the factory's counts, not the final graph's), which is the counter grammar's growth too.

**petrinet (added 2026-09-22)**, Arend's copy of the sample: one rule, `smartRule`, a
transition firing when every input place holds a token, consuming one per input place
and producing one per output place, as two `forall:` levels with an `exists:` token level
inside the first. The five hand-drawn nets explore to 1 to 38 states; four were deleted
and `start2` stays as the readable default start graph. Two generated families:

- `pipe-k-n`: k transitions in a row between k+1 places, n tokens on the first. Every
  token moves forward independently, so the states are the distributions of n tokens
  over k+1 places, C(n+k, k) of them, all distinguishable since the pipeline has a
  direction, on a graph of 2k+n+1 nodes. Each token on an input place is a separate
  `exists:` match, so a place holding m tokens gives its transition m parallel
  transitions to isomorphic targets: transitions run seven to ten times the states, and
  the rows are iso-check and generation rows more than matching rows (matching is 13 %
  of `pipe-8-8`). The family grows about 3.8-fold per step of k = n.
- `join-f`: one transition with f input places, each holding a token, and f output
  places; a second transition fires the tokens back. Exactly one of the two is enabled
  at any time, so an unstored bounded depth-first run is a single path alternating
  them, and every step matches a universal domain of f places on each side, 2f
  sub-matches each with its own context map (4.1.2), then applies a composite event of
  2f deletions and creations (4.2.4, 4.2.6).

Desktop calibration, single cold runs through the harness, one JVM per row, `-Xmx8g`:

| row | states | transitions | s | match ms | iso ms | cert ms | gen ms | allocMB | retMB | kept |
|---|---|---|---|---|---|---|---|---|---|---|
| `pipe-8-8` | 12 870 | 91 520 | 3.3 | 417 | 1 906 | 906 | 2 499 | 4 029 | 85 | quick |
| `pipe-9-9` | 48 620 | 393 822 | 12.0 | 1 272 | 8 240 | 4 666 | 9 731 | 17 829 | 341 | quick |
| `pipe-10-10` | 184 756 | 1 679 600 | 58.2 | 5 546 | 42 681 | 24 075 | 49 423 | 80 646 | 1 296 | not kept |
| `pipe-11-11` | 705 432 | 7 113 106 | 248.9 | 21 122 | 188 283 | 111 583 | 214 524 | 366 535 | 5 240 | long |
| `join-100`, 20 000 steps | 20 002 | 20 001 | 9.0 | 4 827 | 0 | 0 | 1 998 | 22 765 | 484 | quick |
| `join-1000`, 2 000 steps | 2 002 | 2 001 | 10.8 | 5 747 | 0 | 0 | 2 570 | 22 316 | 419 | quick |

`join-100` is 450 µs and 1.1 MB per step for 200 sub-matches, `join-1000` 5.4 ms and
11 MB per step: linear in f, so the nested search has no superlinear term, only a heavy
constant of about 2.5 µs and 5.5 KB per sub-match, which is where 4.1.2 (a
`RuleToHostMap` per candidate) and the composite-event path will show. The pipeline's
cost per state is a constant 250 to 350 µs across the sizes, and its GTS retains 7.4 KB
per state at the long size, the transition-heavy shape.

**parallel-pump and mergers (added 2026-09-22)**, the multigraph and merging rows of
grammar-set item 6. Their variants come from a new per-row grammar-property override in
the harness: `Config.properties` takes space-separated `key=value` pairs like the
`Generator`'s `-D` option, applied to a copy of the grammar's own properties through
`GrammarModel.setProperties`, so one directory serves the DPO, SPO-multi and SPO-simple
rows where the samples keep a second copy (`parallel-pump-spo`) for the purpose.

- **parallel-pump**, Arend's copy under DPO semantics: `pump` turns one of the hub's
  parallel `c` loops into an `a` edge to a `b`-target, `drain` deletes one, `trim` deletes
  one of two parallel `a` edges to the same target and flags the hub, `fold` merges two
  targets. `generate-starts.py` produces `pump-k-m`, the hub with a `mult=k:c` loop and
  m targets, so the states are the distributions of the pumped edges over the targets
  that are left, on a graph of at most m+1 nodes, and the transitions run 24 to 51 per
  state: parallel edges in matching, in the deltas and in the certifier's edge bundles
  (gh #906), with the iso check and generation as the main costs and matching under 3 %.
  States grow 3.7-fold and time 4.6-fold per step of k+2, m+1. The SPO-multi twin differs
  by under one per cent in states (the `trim` matches that identify its deleted with its
  preserved `a` edge, which DPO's identification condition forbids) and by nothing in
  time, since no rule erases a node: the dangling check of finding 4.1.6 never runs on
  this grammar. The mergers DPO row covers that.
- **mergers**, the sample copied by hand (its `system.properties` rewritten to 3.12 with
  the explicit `semantics=SPO-simple` that the version conversion would give it; the
  sample's `enableControl` is no longer a key), scaled to `ring-n`: n nodes flagged a, b, c
  in turn, every node with an edge to its successor and every second node with a chord to
  the node three further on, the edges labelled by the flags of their endpoints as in the
  sample. The rules merge a-nodes into b- and c-nodes (`merge-a-b`, `merge-and-merge`) and
  delete an a-node while merging (`merge-and-del`), so every step shrinks the graph and
  the states are the reachable quotients of the ring: the merge path of the rule
  application (`MergeMap`, the merge branch of 4.2.13), and about 8.5 times more states
  per node. Under SPO-multi the parallel edges that merging nodes with shared neighbours
  creates survive: 4 % more states and 5 to 10 % more time than under simple, and a third
  more distinct edges in the factory (`fEdges`). Under DPO the identification condition
  rules out the non-injective matches that identify a deleted with a preserved element
  (`merge-and-del`'s deleted a-node with the merged one, presumably the bulk), a tenth of
  the states, and the dangling check runs per candidate of `merge-and-del`.

Desktop calibration, single cold runs through the harness, one JVM per row, `-Xmx8g`
(the rows marked "shared" ran while a stray second harness loop was competing for the
machine and are indicative only):

| row | states | transitions | s | match ms | iso ms | gen ms | allocMB | retMB | kept |
|---|---|---|---|---|---|---|---|---|---|
| `pump-6-3-dpo` | 431 | 5 091 | 0.28 | 20 | 60 | 159 | 48 | 2 | dropped |
| `pump-6-3-spo` | 449 | 5 523 | 0.27 | 17 | 41 | 166 | 51 | 2 | dropped |
| `pump-8-4` (DPO) | 2 143 | 38 891 | 0.55 | 25 | 134 | 390 | 347 | 14 | smoke |
| `pump-8-4-spo` (shared) | 2 183 | 40 988 | 0.73 | 26 | 242 | 535 | 369 | 14 | dropped |
| `pump-10-5-dpo` (shared) | 9 274 | 236 314 | 1.55 | 61 | 494 | 1 251 | 2 250 | 74 | dropped |
| `pump-10-5-spo` (shared) | 9 344 | 245 191 | 1.65 | 55 | 604 | 1 357 | 2 250 | 75 | dropped |
| `pump-12-6-dpo` | 36 894 | 1 231 379 | 6.7 | 125 | 2 726 | 5 907 | 11 945 | 345 | quick |
| `pump-12-6-spo` | 37 026 | 1 267 481 | 6.9 | 163 | 2 891 | 6 108 | 12 154 | 352 | quick |
| `pump-14-7-dpo` | 136 731 | 5 731 438 | 31.4 | 621 | 13 850 | 28 253 | 59 169 | 1 502 | dropped |
| `pump-14-7-spo` | 136 941 | 5 870 069 | 32.0 | 594 | 14 454 | 28 917 | 60 505 | 1 518 | dropped |
| `pump-16-8-dpo` | 479 787 | 24 438 977 | 168.6 | 3 845 | 76 288 | 152 314 | 268 792 | 6 010 | long |
| mergers `start` (simple) | 66 | 143 | 0.08 | 2 | 6 | 32 | 5 | 0 | default graph |
| `mergers-6` (simple and multi alike) | 202 | 681 | 0.10 | 5 | 8 | 50 | 12 | 1 | smoke (multi) |
| `mergers-9-simple` | 25 145 | 255 596 | 2.2 | 94 | 478 | 1 793 | 2 832 | 172 | quick |
| `mergers-9-multi` | 26 217 | 259 850 | 2.4 | 123 | 605 | 1 875 | 3 015 | 181 | quick |
| `mergers-9-dpo` | 2 818 | 18 693 | 0.52 | 43 | 63 | 331 | 275 | 20 | dropped |
| `mergers-10-simple` | 213 582 | 3 226 347 | 23.0 | 822 | 6 132 | 19 935 | 36 009 | 1 711 | dropped |
| `mergers-10-multi` | 222 508 | 3 320 992 | 25.2 | 875 | 7 071 | 21 932 | 38 966 | 1 791 | upper quick |
| `mergers-10-dpo` | 11 085 | 81 084 | 1.1 | 69 | 159 | 826 | 1 270 | 86 | dropped |
| `mergers-11-simple` | | | timeout at 600, 977 878 states | | | | | | too large |
| `mergers-11-multi` | 1 084 025 | 19 356 013 | 456 | 7 638 | 127 947 | 398 622 | 240 387 | 5 640 | too large |
| `mergers-11-dpo` | 70 065 | 735 888 | 7.2 | 281 | 1 435 | 5 889 | 10 463 | 517 | quick |
| `mergers-12-simple` | | | timeout at 600, 1 480 776 states | | | | | | too large |

The mergers family has no long-tier size: `ring-11` is a million states and 19 million
transitions, 5.6 GB retained and 7.6 minutes under SPO-multi, and under simple it did not
finish in ten minutes at fewer states, which at that heap is the collector rather than the
semantics (the run alive at the timeout was well into its second half). So the pump at
`pump-16-8` (three minutes, 6 GB) is the item's long row, next to `pipe-11-11` the second
transition-heavy one; `ring-11-dpo` at 7 s and `ring-10-multi` at 25 s bound the quick
tier for mergers. `ring-6` explores to the same 202 states under simple and multi, so the
smoke row runs it under multi for the code path alone.

### The long-run tier (2026-09-22)

The tier the note asked for once 3.11 was fixed. `Config.smoke` became a three-valued
`Tier` (`SMOKE` within `QUICK` within everything; `LONG` apart): `smoke()` runs the
`SMOKE` rows, a plain `main` run the quick tier (that is, everything but `LONG`), and
`-Dgroove.bench.tier=long` (or `all`) the rest, names given as arguments overriding the
tier. The long rows are sized for two to five minutes at `-Xmx8g`, and are meant to run
with one warm-up, two measured runs and one row per JVM, which sidesteps the run-order
effect and lets a row that does not fit fail alone:

```
for c in $(names); do java -da -Xmx8g -XX:+UseParallelGC \
  --add-modules=java.management,jdk.management -Dgroove.bench.warmups=1 \
  -Dgroove.bench.runs=2 -Dgroove.bench.timeout=1200 -cp "<cp>" \
  nl.utwente.groove.test.performance.ExplorationBenchmark $c; done
```

Calibration on the desktop, single cold runs through the harness (no warm-up) at
`-Xmx8g`, one JVM per row; `retMB` is the harness's retained heap after the run:

| row | states | transitions | s | retMB | kept |
|---|---|---|---|---|---|
| `car-platooning-06` | 2 988 061 | 11 929 077 | 170 | 1 310 | long tier |
| `sierpinsky-12` (linear) | 13 | 12 | 3.6 | 1 234 | quick tier |
| `sierpinsky-13` (linear) | 14 | 13 | 10.8 | 3 805 | dropped: too short for long, too heavy for quick |
| `binary-tree-dfs-unstored-9` | 4 037 914 discovered | | 14 | 1 826 | quick tier |
| `binary-tree-dfs-unstored-10` | 43 954 714 discovered | | 131 | 4 975 | long tier |
| `as-and-bs-equality` (`start`) | 262 144 | 1 413 120 | 7.8 | 1 174 | quick tier |
| `as-and-bs-4-3-equality` | | | out of heap at 8 GB | | too large |
| `mark-unmark-22` | 338 688 | 7 451 136 | 122 | 2 388 | long tier |
| `mark-unmark-23` | 151 704 | 3 489 192 | 72 | 1 271 | dropped: smaller than `tree-21` |
| `count-600000` | 600 001 | 1 200 001 | 106 | 2 544 | long tier |
| `count-100000-big` | 100 001 | 200 001 | 6.4 | 834 | quick tier |
| `count-300000-big` | 300 001 | 600 001 | 38 | 2 634 | quick tier |
| `inheritance-13` | 552 824 | 8 148 238 | 41 | 3 288 | dropped: between the tiers at 3.3 GB |

With the three rows already in the set that were long-tier sized (`append-4-list-10`,
`pacman-four-ghosts`, `leader-election-18`) the long tier has seven rows. Surprises:

- The note's 140 s for the unstored tree at depth 9 was a laptop figure; here it is 14 s,
  so the tier takes depth 10. Its 5 GB retained after a run whose GTS holds 11 states is
  the softly reachable state caches of 3.6 at the scale of 44 M discovered states, and
  the reason the row needs the 8 GB heap.
- **BigInteger costs nothing extra on the counter**: 6.4 s against 7.3 s for the plain
  row in the same session. The counter's time is allocation, not arithmetic, which is
  consistent with 4.2.1 to 4.2.3 being about boxing and lookups rather than the
  operations.
- **The counter's allocation is superlinear at a third point**: 1.4 TB for
  `bound-600000`, 2.3 MB per state, after 0.4 MB at 100 k and 1.2 MB at 300 k; the total
  grows about quadratically, so something allocates in proportion to the states so far
  on every step. Unmeasured beyond that; the value-node factory is the first suspect.
- Mark-Unmark's state count is not monotonic in the tree size: `tree-23` is smaller than
  `tree-21`, the generator's tree shape depending on the number. `tree-22` is twice
  `tree-21` and serves.
- As-and-Bs still has no long-tier size: `start-4-3` under equality collapse does not
  fit 8 GB, and the intermediate edge densities of the calibration note remain untried.

**Long-tier baseline**, desktop, 2026-09-22, one JVM per row, `-da -Xmx8g
-XX:+UseParallelGC`, one warm-up and two measured runs, fresh grammar per run, Oracle JDK
26.0.2.1 (the machine's default `java`; the quick-tier baseline above ran on 25.0.4.1,
so the two tables are not to be compared across). 44 minutes in all:

```
config                  states    trans  disc.st   disc.tr   med ms   min ms   max ms  states/s   trans/s   match     iso    cert     gen    rep   confl  allocMB    retMB  fNodes   fEdges
append-4-list-10       1077000  4008820  1077000   4008820  76373.1  75683.3  76373.1     14102     52490   16248   24277   24272   53902     49       0 122216.4   2260.7     253     1599
pacman-four-ghosts      210102  7819623   210102   7819623 135428.6 135387.7 135428.6      1551     57740    3162   73996   36642  117800    170       0 216911.7   1891.5      24      441
leader-election-18      787648  7737099   787648   7737099 147697.0 146645.4 147697.0      5333     52385   20590   40043   40041  118437    105       0 277825.0   2765.6      79      472
car-platooning-06      2988061 11929077  2988061  11929077 159772.4 155247.2 159772.4     18702     74663   44930       0       0   81919    160       0 222765.0   6205.6       6      306
binary-tree-dfs-unstored-10      11       10 43954714  43954713 131190.0 125844.2 131190.0    335046    335046    7376       0       0   98668    190       0 271707.9   4974.7    4095    10236
mark-unmark-22          338688  7451136   338688   7451136 135934.8 128748.0 135934.8      2492     54814   10750   97749   76504  120865    146       0 152759.1   2333.0      22       44
count-600000            600001  1200001   600001   1200001 107110.1 106639.8 107110.1      5602     11203    1191     862     851  104407     15       0 1391868.9    461.4  600008   600005
```

What it says:

- **The quick-tier figure for `append-4-list-10` was collector-bound.** Alone at 8 GB it
  takes 76 s; inside the quick-tier run at 4 GB it took 206 s, with 2.3 GB retained and
  the soft caches on top. `pacman-four-ghosts` (1.9 GB retained) moved from 143 to
  135 s, so the cliff sits between those two retentions: a quick-tier row that retains
  over about 2 GB at `-Xmx4g` measures the collector, not the exploration. That is one
  more reason for the quick-tier re-baseline before the first fix, and for reading the
  quick table's `retMB` column next to its times.
- **Isomorphism checking dominates where it runs**: 72 % of `mark-unmark-22`, 55 % of
  `pacman-four-ghosts`, 32 % of `append-4-list-10`, 27 % of `leader-election-18`, with
  certification about a third to all of it. `car-platooning-06` (the grammar switches
  isomorphism checking off) and the unstored tree (nothing collapses) spend it in `gen`
  and matching instead.
- **`count-600000` is `gen`**: 104 of 107 s, with 1.4 TB allocated; the superlinear
  allocation of the counter lives in state generation, not in matching or the state set.
- **`retMB` is not a stable figure in this tier.** `car-platooning-06` retained 6.2 GB
  here against 1.3 GB in its calibration run, and `count-600000` 0.5 GB against 2.5 GB:
  the softly reachable caches of 3.6 survive or not depending on how hard the collector
  was pressed during the run. Compare `med ms`; treat `retMB` as a lower bound on the
  live set only.
- The spread is small: every `max ms` is within 6 % of `min ms` over the two measured
  runs, which is what the tier was for.

### Quick-tier re-baseline (2026-09-22)

The quick tier as it stands after grammar-set item 6 and the gh #924 merge, replacing
the sixteen-row table of 2026-09-21 above (whose rows it repeats in the same order, so
the two are comparable row by row; the counter rows carry the `probe-odd` transitions
now, the fibonacci rows the fixed recipe path). Desktop, JDK 25.0.4.1, the launch flags
`-da -Xmx4g -XX:+UseParallelGC`, two warm-ups and three measured runs, all rows in one
JVM in table order, fresh grammar per run, at `fe5a2887e`; 35 minutes in all:

```
config                  states    trans  disc.st   disc.tr   med ms   min ms   max ms  states/s   trans/s   match     iso    cert     gen    rep   confl  allocMB    retMB  fNodes   fEdges
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
inheritance                756     5374      756      5374     31.4     29.7     34.2     24057    171005       3       1       1      22      1       0     43.6      2.8       7       10
pacman                     256     1536      256      1536     33.7     29.8     34.4      7600     45597       5      13       7      23      0       0     38.0      1.2      20      228
as-and-bs                 8240    44774     8240     44774    224.0    210.0    229.7     36786    199887      43      74      29     145      0       0    476.9     34.0       6       27
sierpinsky-11               12       11       12        11    709.9    701.4    766.2        17        15      84       0       0     554      0       0    900.7    418.2  265734   841476
binary-tree-dfs12         4012    22188     4012     22188    555.7    553.4    566.1      7220     39932       3     455     321     540      0       0    548.1     16.5     239      596
append-4-list-8          31104   114008    31104    114008   1759.6   1740.1   1939.3     17677     64793     435     617     576    1214      3       0   2940.3    228.6      67      293
append-4-list-8-equality   73792   268912    73792    268912   3517.5   3514.9   3566.9     20979     76450     839       0       0    2385      4       0   5179.3    526.8      74      357
mark-unmark              24576   368640    24576    368640   2852.1   2827.4   2900.2      8617    129254     497     890     784    2080      2       0   5479.1    143.5      15       30
car-platooning-05       110366   369601   110366    369601   3777.6   3774.2   3800.1     29216     97840    1147       0       0    2049      6       0   7451.9    539.0       5      215
binary-tree-dfs-unstored       9        8   409114    409113   1472.2   1432.0   1569.3    277897    277897     119       0       0    1055      3       0   2657.8   1041.5    1023     2556
mark-unmark-18           48384   870912    48384    870912  12056.3  12033.8  12110.9      4013     72237    1199    7796    6018   10289     20       0  15739.1    370.9      18       36
mark-unmark-21          169344  3556224   169344   3556224  63447.8  63383.9  63521.0      2669     56050    4993   45275   34899   56201     66       0  73736.5   1276.7      21       42
as-and-bs-4-3           131505   947824   131505    947824   5606.6   5586.6   5650.8     23455    169054     910    2196    1265    4046     17       0  10936.1    568.7       7       35
inheritance-12          297212  4317133   297212   4317133  21553.3  21376.8  21962.6     13790    200300     701    5563    4526   18786     46       0  36971.2   1735.6      12       21
leader-election-8          820     3405      820      3405    417.7    387.1    436.8      1963      8151     159      48      48     183      0       0     87.1      4.5      39      196
leader-election-14       49620   386295    49620    386295   5852.5   5804.8   5903.4      8478     66005    1139    1717    1717    4318      7       0  11248.8    405.9      63      375
leader-election-16      197404  1772291   197404   1772291  30605.0  29029.8  30824.8      6450     57908    4902    8424    8423   23729     21       0  56881.8   1584.3      71      414
count-10000              10001    25001    10001     25001    556.1    498.8    630.2     17983     44955     231      16      16     398      1       0    725.2     79.7   10008    10005
count-100000            100001   250001   100001    250001   4020.8   4006.4   4046.8     24871     62177     356     133     133    3666      4       0  41576.1    836.3  100008   100005
count-300000            300001   750001   300001    750001  34231.1  33904.3  34303.9      8764     21910    1592     456     456   32689      5       0 354044.9   2641.5  300008   300005
fib-15                       3        2     4934      4934    425.7    420.1    466.4     11591     11591      33      11      11     266      0       0    111.4     41.9      27        2
fib-22                       3        2   143284    143284   2049.7   2032.0   2152.1     69904     69904      84      38      38    1207      3       0   3121.8   1256.2      40        2
fib-function-15           4934     4933     4934      4933     42.9     38.5     44.6    115115    115092       3       1       1      25      0       0     99.5     41.0      27        2
fib-function-22         143284   143283   143284    143283   1771.5   1753.9   1804.0     80885     80884     110      38      38     983      4       0   2995.3   1212.7      40        2
count-100000-big        100001   250001   100001    250001   5590.4   5033.7   5696.7     17888     44720     602     140     140    5087      6       0  41659.5    840.9  100008   100005
count-300000-big        300001   750001   300001    750001  35665.6  35451.0  36275.9      8411     21029    1841     503     481   34147     11       0 354325.9   2655.2  300008   300005
as-and-bs-equality      262144  1413120   262144   1413120   9146.2   9062.2   9220.9     28661    154503    1148       0       0    6767      7       0  16501.7   1171.4       6       27
sierpinsky-12               13       12       13        12   2773.2   2697.0   2784.4         5         4     331       0       0    2214      0       0   2570.1   1233.9  797176  2524375
binary-tree-dfs-unstored-9      10        9  4037914   4037913  29723.6  29019.8  30642.5    135849    135849    1068       0       0   23066     29       0  25514.6   1710.1    2047     5116
hub-star-300-3               1      891        1       891   2354.4   2237.5   2482.2         0       378       2    2246    1679    2326      0       0    389.3      1.3     303     1200
hub-chain-1000-1          1000      999     1000       999   7602.0   7246.4   7633.3       132       131       4    7460    7460    7561      0       0    317.2      4.6    1003     3005
hub-chain-200-2          19900    39402    19900     39402  11290.3  10798.0  11290.8      1763      3490      80   10476   10476   11130      2       0   2852.7     80.8     203      606
hub-field-hop           100001   100000   100002    100001   6628.8   6575.0   6690.9     15086     15086    6178       0       0     256      1       0  13490.4    262.4    5405     5914
hub-field-jump          200001   200000   200002    200001   7982.3   7681.8   8055.4     25056     25055    7173       0       0     407      3       0   1689.0    521.8    5404     5816
hub-ring-1000-unstored  200001   200000   200002    200001   1256.0   1252.3   1291.9    159238    159238     196       0       0     687      0       0   1673.0    521.2    1003     3007
hub-ring-1000-counted   200001   200000   200002    200001   1506.7   1429.4   1507.6    132740    132739     303       0       0     743      1       0   2713.4   1066.5  201004   402009
petrinet-pipe-8-8        12870    91520    12870     91520   2422.7   2418.9   2441.6      5312     37775     286    1673     945    1938      4       0   3948.0     85.1      89      177
petrinet-pipe-9-9        48620   393822    48620    393822  11835.0  11822.5  12628.2      4108     33276    1167    8541    4844    9869     17       0  17796.2    340.3     109      217
petrinet-join-100        20001    20000    20002     20001   8042.7   8035.2   8279.7      2487      2487    4305       0       0    1844     22       0  22942.7    483.2     502     1202
petrinet-join-1000        2001     2000     2002      2001   9504.8   9490.8   9516.6       211       211    4906       0       0    2229     17       0  22484.8    418.4    5002    12002
pump-8-4                  2143    38891     2143     38891    813.4    205.8    865.5      2635     47812      14     327     273     716      2       0    336.1     13.5       5       82
pump-12-6-dpo            36894  1231379    36894   1231379   6820.3   6716.9   6919.9      5409    180547     151    2821    2032    6055     21       0  12030.9    344.8       7      180
pump-12-6-spo            37026  1267481    37026   1267481   7625.3   7609.4   7754.6      4856    166220     144    3173    2263    6826     30       0  12108.7    351.2       7      180
mergers-6                  202      681      202       681     87.7     76.4    118.4      2304      7766      11      18      11      64      3       0      9.3      0.8       6       83
mergers-9-simple         25145   255596    25145    255596   1715.2   1711.4   1722.6     14660    149017      92     471     321    1437      3       0   2866.2    181.7       9      245
mergers-9-multi          26217   259850    26217    259850   1867.5   1853.9   1879.4     14038    139143     110     561     388    1574      5       0   3048.0    190.3       9      314
mergers-10-multi        222508  3320992   222508   3320992  28257.7  28173.8  28277.8      7874    117525     882    7953    5681   24782     88       0  40117.7   1786.3      10      520
mergers-11-dpo           70065   735888    70065    735888   7723.9   7697.2   7890.7      9071     95274     283    1550    1550    6497     17       0  10930.0    555.9      11      208
```

Read against the 2026-09-21 table and the calibration figures:

- **Eleven of the fourteen shared rows agree within 3 %**; `mark-unmark` (2 350 to
  2 852 ms), `car-platooning-05` (3 213 to 3 778 ms) and `binary-tree-dfs-unstored`
  (1 331 to 1 472 ms) are 11 to 21 % slower, the other Mark-Unmark sizes only 3 to 4 %.
  The merge in between brought gh #924, which touches `StateCache` on every state, so the
  three rows were rerun one JVM per row on the pre-merge tip `2546ab571` and on
  `fe5a2887e`, with `append-4-list-8` and the fibonacci pair as controls; see the A/B
  paragraph below.
- **Collector-bound at 4 GB**: `binary-tree-dfs-unstored-9` takes 29.7 s here against
  14 s in its cold calibration at 8 GB, with 1.7 GB retained plus the soft caches of
  4 M discovered states; it is the one quick row that measures the collector
  rather than the exploration, and belongs in the long tier or at a larger heap. The
  other rows above 1 GB retained (`count-300000` and its BigInteger twin at 2.6 GB,
  `mergers-10-multi`, `inheritance-12`, `leader-election-16`, `as-and-bs-equality`,
  `fib-22`, `sierpinsky-12`) are within 15 % of their calibration at 8 GB, so the cliff
  the long tier found sits above 2.6 GB for stored runs and lower for unstored ones,
  whose live set is all soft.
- **Run order still shows.** `fib-15` measures 426 ms here against 43 ms for
  `fib-function-15` and 285 ms in its own JVM, and it ran directly after
  `count-300000` had left 2.6 GB to collect; `pump-8-4` has a minimum of 206 ms under a
  median of 813 ms. Rows that follow a heavy row inherit its collection; compare like
  orders, or one JVM per row, as before.
- **Certification is the whole of the hub chain rows** (98 % of `hub-chain-1000-1` and
  93 % of `hub-chain-200-2`), 70 % of `mark-unmark-21` and the petrinet pipeline, and
  40 % of the pump rows; matching is the whole of the hub field rows and 60 % of the
  petrinet join rows; `gen` is the whole of the counter rows (95 %) and the fibonacci
  function rows. The section order of the note (isomorphism and reconstruction before
  matching for the large-state-space rows) stands.
- The confluence count is zero on every row, as before.

**A/B of the moved rows (2026-09-22).** The three rows and three controls, one JVM per
row, two warm-ups and three runs at `-Xmx4g`, the pre-merge tip `2546ab571` (A) against
`fe5a2887e` (B), alternating per row:

| row | A med ms | B med ms |
|---|---|---|
| `mark-unmark` | 2 204 | 2 205 |
| `car-platooning-05` | 3 411 | 3 409 |
| `binary-tree-dfs-unstored` | 1 033 | 962 |
| `append-4-list-8` | 1 702 | 1 699 |
| `fib-15` (recipe) | 16 701 | 63 |
| `fib-function-15` | 56 | 55 |

So gh #924 costs nothing on the non-recipe path and the in-table differences are run
order: `mark-unmark` alone measures 2.2 s against 2.85 s as the eighth row of the tier
(and 2.35 s in the same position on 2026-09-21), `car-platooning-05` 3.4 s against 3.8 s.
The fixed recipe path is 265 times faster at `fib-15` and level with the function twin
once warm (the 426 ms of the table is the collection inherited from `count-300000`).
The position effect is larger than the 10 % the earlier tables suggested; a fix under
20 % must be measured one JVM per row, in the shape of this A/B.

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
   hub first. (Partly covered since 2026-09-22 by the `hub` grammar: graph size and
   symmetry, not yet the hub-bound matching of 5.1 and 5.2.)
3. **Symmetry.** 5.6 (individualise-and-refine without automorphism pruning) needs
   graphs with large automorphism groups: a ring of N identical processes
   (`leader-election` with N of 8 or more), N philosophers, or a set of N identical
   unconnected components. The current `leader-election start-2` has none. (Covered
   since 2026-09-21 by the generated `ring-N` graphs of the performance grammar set.)

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
  (Covered since 2026-09-22: the counter's `probe-odd` rule, see "The performance
  grammar set".)
- **NAC-heavy matching** for 3.3 and 4.1.1: `car-platooning start-18`,
  `circular-buffer`, `ferryman`; monitor `HostFactory` edge count growth across the run
  for 3.3.
- **Recipes with transience** for 4.3.1 and 1.5: `recipes`, `transactions`.

Recommendation: a benchmark set of about ten configurations, half from existing
grammars with the larger start graphs, plus two or three new grammars under
`junit/performance/` (large graph with hubs, symmetric ring, attribute counter), each
sized for 5 to 60 seconds on the development machine. Record the baseline numbers in
this note before the first change lands.

### Coverage reassessment (2026-09-22, after grammar-set item 6 and gh #924)

The set is sixteen grammars and about forty rows. Mapped against the parts of state-space
generation that run per state, per match or per transition, this is what the rows
exercise and what they do not.

Covered, with the row that carries the cost:

- **Matching**: plain search (all rows); NACs (`as-and-bs`, `append`, `car-platooning`,
  `pacman`, `leader-election`, `hub`); universal quantifiers, nested and with wide
  domains (`petrinet-join`, `petrinet-pipe`, `pacman`, `sierpinsky`); regular expressions
  (`mark-unmark`); attribute tests, `let:` and operations (`count`, `hub-ring-counted`,
  `fib`), error values (`probe-odd` on every counter row), BigInteger (`count-*-big`);
  rule priorities (`leader-election`, `car-platooning`); subtyping (`inheritance`,
  `hub`); parameters in and out (`fib`); the candidate over-approximation at a hub
  (`hub-field-hop`, `hub-field-jump`).
- **Transformation**: creation and deletion (all); merging (`mergers`); parallel edges
  under SPO-multi and DPO (`pump`, `mergers-*-multi`); the dangling check
  (`mergers-11-dpo`); value nodes and factory growth (`count`, `hub-ring-counted`);
  composite events of quantified rules (`petrinet`); large graphs (`sierpinsky`, `hub`).
- **LTS and state cache**: many transitions per state (`pump`, `pipe`, `pacman`); delta
  chains and reconstruction on a linear or depth-first path (`sierpinsky`,
  `binary-tree-dfs12`); unstored runs (`binary-tree-dfs-unstored`, the hub field and
  ring rows, `petrinet-join`); transient states of a deep acyclic recipe recursion
  (`fib-*`, paired with the function rows); the confluence check (`inheritance`, dead).
- **Isomorphism**: symmetry (`leader-election`, `hub-star`); diameter (`hub-chain`); edge
  bundles (`pump`); equality collapse (`*-equality`); checking off (`car-platooning`).
- **Driver**: breadth-first, depth-first, linear, cost-bounded, unstored.

Not covered, ranked by how much of the generation path the gap hides and how cheap it
is to close:

1. **The Simulator's mode.** `SimulatorModel.resetGTS` sets `Record.randomAccess`, which
   makes `DeltaHostGraph` materialise every state through a `CopyTarget` (fresh node and
   edge sets per graph) instead of the `SwingTarget` that hands the parent's sets down a
   lineage. Every row runs the headless swing mode, so the harness measures the
   `Generator` and never the mode every interactive user runs in; the demotion of 3.7
   above holds for swing mode only, and 4.3.2 (per-node edge sets) is a per-state cost
   in copy mode. Closing it is a harness switch (`-Dgroove.bench.copy=true`, calling
   `setRandomAccess(true)` on the fresh GTS before the start state), no new grammar, and
   a second column of the baseline for the rows where the two modes differ.
2. **Cyclic and wide transient regions.** The fibonacci recipe is a deep, acyclic
   recursion with one recipe end per launch: the case gh #924 was measured on. The
   redesign's forward-search fallback runs on cyclic transient regions, and its open
   point (a search repeated per successor notification, bounded by the closed non-full
   region) has no row that would show it; nor is there a region with many recipe ends
   (a star-ended recipe, `r() { step; step* }`, as in the `recipes` sample's `star`
   programs) or an atomic block. A `hub` control program on `chain-200-2` with a
   backward step next to `moveNext` inside a star-ended recipe gives both shapes on an
   existing graph: the transient region is the placement space itself, revisited from
   many directions.
3. **Many rules, few applicable.** The largest rule set is `car-platooning`'s twenty;
   real grammars have hundreds, and without control every rule is tried in every state
   (the per-rule fixed cost of `MatchCollector`: matcher lookup, plan, control frame
   schedule). A generated grammar with a few hundred non-matching rules around one
   working rule (the script can write `.gpr` files as it writes `.gst` files) would
   isolate that cost; nothing in the set does.
4. **Injective matching.** No grammar in the set has `matchInjective=true`; the
   injectivity constraint is a per-candidate filter in the search plan. A
   `Config.properties` variant row on `mergers` or `as-and-bs` costs nothing to add,
   with new counts.
5. **Cache collapse under memory pressure.** Reconstruction from the delta chain
   (findings 2.5 to 2.7) is measured only where the collector happens to clear soft
   caches, which the long tier's unstable `retMB` shows it does unpredictably. A harness
   option that clears the collectable caches every N states, as `DeterminismTest` does,
   would make the reconstruction cost a controlled column instead of noise.
6. **Per-state acceptors and rule-condition bounds.** `goal=condition|fires|graph`,
   `bound=upto|include|nodes|size|edges` and `count` each add a check per state (a rule
   match, a graph size, an isomorphism test against a fixed graph); no row uses any of
   them. One `goal=condition` row on an existing grammar would show whether the check
   costs a rule's worth or more.
7. **Randomised and restricted frontiers.** `next=random`, `successor=all-random`,
   `frontier=beam` and `heuristic` (the seed machinery of gh #897) have no row; their
   per-state cost is a shuffle or a pool operation, probably small, but unmeasured.
8. **LTL model checking.** The nested depth-first strategies of `explore/verify` build
   the product with the Büchi automaton during exploration and share nothing with the
   frontier strategies; unmeasured. CTL checking runs over a finished GTS and is outside
   the harness's scope.
9. **Regular-expression variants.** `regExpMatching=sloppy` (gh #900) on the
   `mark-unmark` rows is a one-line variant row.

Items 1, 2 and 4 are the ones to close before the section 1 to 4 fixes are measured:
1 because a fix measured in swing mode only may not carry to the Simulator, 2 because
gh #924 has just rewritten that code and its own gate is a correctness test, 4 because
it is free. Items 3 and 5 are harness or generator work of an hour each; 6 to 9 can
wait for a finding that needs them.
