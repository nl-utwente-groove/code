# Copy-on-write edge stores (finding 4.3.2)

Status 2026-09-24: implemented on branch `cow-edge-stores` (off master `01e38fd95`). Opus 5.5
built the forkable structures and used them in both data modes (`442b93192`); Fable 5.1
assessed that the same day, re-measured the swing-mode regression (larger than first
reported) and then implemented the refined proposal: forkable stores and edge set in
copy mode only, behind a `HostEdgeStore` interface with two implementations, the edge
set keyed by edge number, plus copy-mode tests. Gate results and the post-implementation
measurement are under "Verification". The accidental revert that sat at the branch tip
(`f1999e60e`) has been dropped.

## Problem

In copy mode (`Record.isCopyGraphs()`, forced by the Simulator's random access) every
materialised graph copied its whole `HostEdgeStore`s (`LinkedHashMap`s) and its global
edge set (`TreeHashSet`) entry by entry: O(|G|) per step, retained in the soft state
caches, most of it then paid again in GC. The per-node edge sets were already
copy-on-write (`CopyTarget`'s fresh-key sets). Diagnosis and profile in
`claude/exploration-performance.md`, finding 4.3.2 (corrected 2026-09-24, on branch
`exploration-performance`). The assessment confirms the diagnosis: `CopyTarget` shares the
per-node sets and clones only touched keys; the stores and the global set are the flat
containers copied at graph size.

## Design as built

- `util/collect/ForkableHashTable` (package-private core), `ForkableHashMap`,
  `ForkableHashSet`: a hash table split into 2^b buckets by the top bits of the spread
  hash, each bucket a small open-addressing table (linear probing, at most half full,
  backward-shift deletion). b grows (never shrinks) so that the average bucket size stays
  below the bucket count, i.e. about sqrt(n) buckets of about sqrt(n) keys.
- Forking copies the bucket array only. Each bucket records the table that owns it
  (owner token); a fork gives both tables fresh tokens, so either clones a shared bucket
  on its first write. Forking writes nothing but the original's token, so reads of a
  graph that is no longer modified stay safe while it is forked. (A property to keep: a
  graph displayed in the Simulator is read on the event thread while the exploration
  forks it.)
- Keys are hashed and compared through a `util.Equator` (the equals-based equator of
  `TreeHashSet` by default), so that a set can identify edges by number.
- `HostEdgeStore<K>` is an interface over `Map<K,HostEdgeSet>` with the store operations
  as default methods and a `copy(deepCopy)` factory; `LinkedHostEdgeStore` (over
  `LinkedHashMap`, copied entry by entry) serves swing mode, `ForkableHostEdgeStore`
  (over `ForkableHashMap`, forked) copy mode. `DeltaHostGraph` picks by its `copyData`
  flag, for the four stores and for the global edge set, which is a `HostEdgeSet` in
  swing mode and a `ForkableHostEdgeSet` (forkable, keyed by edge number like
  `HostEdgeTreeHashSet`) in copy mode; `edgeSet()` returns `Set<HostEdge>`. The deep copy
  at the swing copy bound stays a `LinkedHashMap` copy. In a Generator JVM only the
  linked store is loaded and in a Simulator JVM only the forkable one, so the call sites
  stay monomorphic; the swing-mode residual of that indirection is under "Verification".
- In copy mode the iteration order of the stores and the global edge set is bucket/slot
  order (deterministic, history-dependent); accepted by Arend 2026-09-24. The per-node
  sets keep insertion order. Swing mode keeps its data structures.
- `ForkableHashMapTest`: randomised differential test against `HashMap`/`HashSet`,
  200 000 operations over up to 50 forks, with heavily colliding keys.

Code review (Fable 5.1, 2026-09-24): no correctness defect found in the table; probing
terminates by the half-full invariant, backward-shift deletion handles the cyclic case,
removal clones the shared bucket after locating the slot (the clone keeps the slots).
Minor: `put`/`add` of a key that is already present clones a shared bucket needlessly (no
caller does this); `indexOf` recomputes `spread(k.hashCode())` per probe, which is a cached
field read for every key type involved (`ANode`, `AEdge`, `ALabel`). The `entrySet`
iterator runs a key and a value iterator in lock-step; correct while both traverse the
same table, and only the deep copy uses it.

## Measurements

Fable 5.1, 2026-09-24, re-measured independently of the first figures: JDK 25.0.4.1,
`-da -Xmx4g -XX:+UseParallelGC`, two warm-ups and three measured runs, one JVM per row
and side, master (A: the exploration-performance tip `2d878dbcb`, whose main tree equals
master, since master lacks the harness) against branch (B: `442b93192`), the swing rows
in the order A, B, A, B. Median time per JVM, retained heap of the last.

Swing (Generator) mode:

| row | A | B | change |
|---|---|---|---|
| `sierpinsky-12` | 2.63, 2.75 s | 3.58, 3.65 s | +33 to +36 % (`gen` 2.1 to 2.9 s, `match` 0.32 to 0.49 s) |
| `hub-chain-200-2` | 11.2, 11.8 s | 12.4, 12.9 s | +9 to +10 % |
| `leader-election-14` | 5.71, 5.69 s | 6.09, 5.83 s | +3 to +7 % |
| `count-100000` | 1.51, 1.22 s | 1.31, 1.28 s | noise |

The first figures (Opus 5.5, `sierpinsky-11` +10 to 25 %, `leader-election-14` +3 to
5 %, `hub-chain-200-2` noise to +8 %) understated the regression: on the larger
`sierpinsky-12` it is a third. The cost is per operation (bucket indirection, probing
with a hash recompute per compared key, bucket clones and rehashes on insertion), all in
`gen` on insertion-heavy rows; profiles show no single hot spot left to tune. A single
representation is therefore not an option.

Random-access (copy) mode:

| row | A | B |
|---|---|---|
| `hub-ring-1000-unstored` | 27.4 s, 24.9 GB allocated, 985 MB retained | 1.00 s, 2.6 GB, 1 092 MB |
| `hub-chain-200-2` | 11.4 s, 3.9 GB, 423 MB | 10.4 s, 2.9 GB, 88 MB |
| `petrinet-join-100` at 4 GB | 27.5 s, 31.2 GB, 133 MB | 10.7 s, 30.4 GB, 2 990 MB |
| `petrinet-join-100` at 1 GB | 25.1 s, 31.0 GB, 222 MB | 22.7 s, 30.4 GB, 694 MB |

`hub-ring` is the finding at full strength: the branch brings copy mode to swing speed
(swing baseline 1.4 s) and cuts allocation tenfold. `hub-chain` confirms the retention
gain for stored states. Discovered state and transition counts agree between the sides in
every row.

`petrinet-join-100` is a different case. Its allocation does not move (swing mode: 23 GB),
and at 1 GB the gain shrinks to 10 %. A JFR profile of the branch in copy mode
(`-XX:+DebugNonSafepoints`, whole JVM including the warm-up): GC pauses 7.2 s in 98
pauses at 4 GB, 29.4 s in 799 pauses at 1 GB; of the sampled allocation about a fifth is
the refresh clones of touched per-node and per-label sets (`TreeHashSet` copy constructor
via `HostEdgeSet.newInstance` from `HostEdgeStore.addEdge`/`removeEdge`/`addKey`), a
third the matcher's maps (`AGraphMap`, `RuleToHostMap`, `TreeMatch`, `Proof`), which is
mode-independent. The remaining copy-mode cost there is the per-key clones, retained by
the soft state caches and paid as GC; the 2.99 GB retained at 4 GB is those caches
surviving under lower pressure, not a leak (694 MB at 1 GB, no `OutOfMemoryError`). Both
open points of the first version are thereby settled. CPU on that row: `Factory.get`
11 to 12 % of samples from `Rule.getCreatorNodes`/`getAnchor`/`getMergers`/`getEraserNodes`
(finding 1.3, mode-independent), `ForkableHashTable.indexOf` plus `put` 8 to 11 %.

Unexplained, master side: on `hub-ring-1000-unstored` in copy mode the factory reports
8 007 edges on master against 3 007 on the branch (nodes 1 003 on both), where the start
graph's 2 004 edges plus 1 000 token flags come to 3 004. The factory's
edge store is a strongly held `TreeHashSet`, the rule creates only flags, and both sides
run clean with `-ea`. To be understood before the merge; reproduction is the single-JVM
run above with `-Dgroove.bench.warmups=0 -Dgroove.bench.runs=1`.

## Decision: forkable structures in copy mode only

Swing mode keeps its data structures (measured residual under "Verification"). Rejected
alternatives: one representation with the swing-mode regression (the Generator and most
of the benchmark set run in swing mode; a third on large graphs is not acceptable); a
flat-until-forked table that restructures itself into buckets on its first fork (one
class for both modes, but it changes the Generator's store implementation, which then
needs its own measurement, and a restructuring fork would mutate a graph the Simulator
may be reading).

Points settled by the assessment, all implemented:

- **Shape of the abstraction.** Not a delegating `HostEdgeStore` (an extra indirection on
  every store access in swing mode) but an interface `HostEdgeStore<K> extends
  Map<K,HostEdgeSet>` carrying `addKey`/`addEdge`/`removeEdge` as default methods and a
  `copy(boolean deepCopy)` factory, with two final implementations, one extending
  `LinkedHashMap` and one extending `ForkableHashMap`. The `edgeSet` field becomes
  `Set<HostEdge>`, a `HostEdgeSet` in swing mode and a forkable set in copy mode. A
  Generator JVM then loads only the `LinkedHashMap` store and a Simulator JVM only the
  forkable one, so the call sites stay monomorphic in practice; a test JVM sees both,
  which the JIT handles as a bimorphic inline cache.
- **Edge identity.** `HostEdgeSet` (`HostEdgeTreeHashSet`) keys edges by their
  factory number and treats equal numbers as equal; `ForkableHashSet` keys by
  `hashCode`/`equals`, and `DefaultHostEdge.equals` is content-based. The two agree for
  registered edges of simple graphs, where the factory pools one instance per content,
  but not for parallel copies in multigraph mode (`semantics=SPO-multi|DPO`), which are
  content-equal instances with distinct numbers: the branch's global edge set would
  reject the second copy as a duplicate (`add` returns false, the "already occurred"
  assertion fires under `-ea`), leaving the global set inconsistent with the per-node
  sets and with `RuleEffect.excluded()`, which consults `containsEdge` when pooling
  created edges. No copy-mode multigraph test exists, so this was found by reading. The
  forkable edge set keys by number like `HostEdgeTreeHashSet`: `ForkableHashTable` takes
  an `Equator`, and `ForkableHostEdgeSet` passes one that uses the number as code and
  number equality as equality. This also removes the `equals` calls from probing.
- **`RuleApplication.computeMorphism`** copies `source.edgeSet()` through the
  `Collection` constructor once the set is no longer a `HostEdgeSet` (element-wise
  instead of the array copy). It is off the exploration path (the morphism is computed
  on request); acceptable, but worth a look when the number-keyed set exists, since a
  `HostEdgeSet` constructor from it could stay cheap.
- **Tests as the gate.** No test in `src/test` runs copy mode (`setRandomAccess` and
  `setCopyGraphs` are unused there); the benchmark harness's random-access mode is the
  only exercise. `CopyModeExplorationTest` explores five samples in both modes (two of
  them multigraphs), compares the state spaces state by state under isomorphism and
  checks each copy-mode graph's global edge set against its per-node and per-label sets;
  `DeterminismTest` has a copy-mode variant, so cache collapse and reconstruction are
  covered for the forkable stores too; `ForkableHashMapTest` covers the equator hook. In
  copy mode a test builds the GTS through `ExploreType.newGTS`, sets random access and
  materialises the start state before `newExploration`, as the Simulator and the
  benchmark harness do; a plain `new GTS` materialised early makes the exploration's
  per-GTS features (persistence) fail with "use Restart".
- **Annotation order.** `@AIGenerated` must precede `@NonNullByDefault` on the three
  collect classes: with the reverse order ecj 3.42.0 reports the `AIGenerated` import as
  never used (three warnings, reproduced in per-file and whole-project mode, absent with
  the annotations swapped or without `@NonNullByDefault`; other files carrying both
  annotations in the other order check clean, so the trigger is not understood). Fixed on
  the branch.

## Possible second step

Fork the per-key sets too: a forkable `HostEdgeSet` in copy mode would remove the
refresh clones that dominate `petrinet-join`'s remaining copy-mode allocation (large hub
places, label sets of hundreds of edges). Measure on `petrinet-join-100` at `-Xmx1g`,
where the clones show as GC. Not part of this branch.

## Verification

Gates run on the implementation (2026-09-24, by an Opus sub-agent): `null-check` clean on the
eleven touched files and at baseline in whole-project mode; the fast suite green; the eight
GUI test classes green, none skipped (the Simulator is the copy-mode user, so these are the
integration test of the change); `ExplorationTest` 25 of 25; `DeterminismTest` (including the
new copy-mode variant), `CrossJvmDeterminismTest` and `CacheReconstructionTest` green.

Post-implementation A/B, same shape as above (JDK 25, one JVM per row and side, master
side built from the exploration-performance tip). Random-access (copy) mode:

| row | master | branch |
|---|---|---|
| `hub-ring-1000-unstored` | 21.8 s, 24.9 GB allocated, 985 MB retained | 0.98 s, 2.7 GB, 1 087 MB |
| `petrinet-join-100` | 23.2 s, 31.2 GB, 140 MB | 12.2 s, 30.9 GB, 2 973 MB |
| `hub-chain-200-2` | 11.3 s, 3.9 GB, 423 MB | 11.2 s, 2.9 GB, 88 MB |

The same gains as for the interim form, now with the number-keyed edge set; state and
transition counts agree on every row, and the master-side edge-count anomaly on
`hub-ring` (8 007 against 3 007) persists unchanged, so it is not caused by the branch.

Swing (Generator) mode, two rounds of A, B, A, B (the second in the order B, A, B, A),
median per JVM:

| row | master | branch |
|---|---|---|
| `sierpinsky-12` | 2.38, 2.35, 2.57, 2.57 s | 2.53, 2.43, 2.69, 2.63 s |
| `hub-chain-200-2` | 10.6, 11.0, 11.1 s | 11.3, 10.4, 11.2 s |
| `leader-election-14` | 5.54 s | 5.57 s |

`hub-chain` and `leader-election` are within noise. `sierpinsky-12` is slower on the
branch in all four pairs, by 2 to 6 % (against a third for the interim form). Swing
mode keeps its data structures, so this residual can only come from the store and the
global edge set now being reached through interface types (`HostEdgeStore`,
`Set<HostEdge>`) where they were final classes before; the call sites are monomorphic
in a Generator JVM, so the cost is presumably JIT inlining shape rather than dispatch.
Whether to accept it or chase it with `-XX:+PrintInlining` on the sierpinsky row is
Arend's call; the row is the artificial extreme of pure insertion into graphs of a
million elements.
