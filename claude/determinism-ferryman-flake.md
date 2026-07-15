# Investigation note: rare DeterminismTest flake (ferryman, transition order)

*Status (2026-07-15): observed once, not reproduced, cause narrowed down but not found.
This documents everything known, so the investigation can be picked up on any machine.*

## The observation

One full-suite run (`mvn test -Dexcluded.test.groups=`) on the merge of branch
`claude/isochecker-node-set-equality` with master (pushed as `da76f7022`) failed
`DeterminismTest.testPlanEngineDeterminism`: two successive bfs explorations of
`junit/samples/ferryman.gps` in the same JVM enumerated the two `eat` transitions of
state s108 in opposite order. States, transitions, and all hashes were identical —
only the enumeration order flipped:

```
run 1:  s108--eat->s84 #-1570868797     run 2:  s108--eat->s78 #1893946184
        s108--eat->s78 #1893946184             s108--eat->s84 #-1570868797
```

## What it is not

- **Not caused by the IsoChecker branch.** Ferryman has no isolated nodes and no value
  nodes, so the node-set comparison added there flips no `areGraphEqual` outcome in this
  grammar; the `initData`/swing side-effect sequence of the shortcut is unchanged; and all
  equality outcomes are content-based, hence identical between the two in-JVM runs of the
  test. Both merge parents pass the test in isolation.
- **Not reproducible so far.** Statistics gathered on 2026-07-15 (Windows 11, JDK 26):
  - 14 full-suite runs on the merge: 1 failure (the observation), 13 passes;
  - 6 full-suite runs on master (`ea38b5a68`): all pass;
  - 30 isolated runs (15 merge, 15 master) with amplified GC pressure
    (`-DargLine="-Xmx40m -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+UseSerialGC"`): all pass.

## Suspected mechanism

The enumeration signature is produced by `GTS.edgeSet()`, which iterates per state over
`state.getTransitions(ANY)`. So the flipped order means the two explorations *created or
stored* the transitions of s108 in different order. The chain of order dependence:

1. Transition storage order per state = match discovery order: `lts.MatchResultSet`
   extends `TreeHashSet` (iteration = insertion order), and the stubs are frozen into
   `AbstractGraphState.transitionStubs` in that order when the state closes.
2. Match discovery order (plan engine) follows the iteration order of the state graph's
   `HostEdgeSet`s / `HostEdgeStore`s.
3. Those iteration orders depend on the `DeltaHostGraph` construction history: with
   `Record.copyGraphs = false` (the exploration default), `SwingTarget` steals the basis
   graph's structures and delta-mutates them in place, so insertion order is inherited
   along the basis chain; `ALIAS_SETS = true` exposes those live structures.
4. `StateCache` is held via `CacheReference extends SoftReference`; when a cache is
   collected, the state graph is reconstructed along a *different* basis chain
   (`DeltaHostGraph.initData`), so the insertion orders of its element sets can change.

`DeterminismTest`'s own javadoc states that enumeration order on the exploration path
must not depend on cache-driven reconstruction (its `perturbIdentityHashes()` perturbs GC
timing precisely to expose this), and the determinism program hardened the other links.
The observation implies a residual path where link 2→3→4 still leaks: a GC that collects
some `StateCache` at an unlucky moment in one exploration but not the other changes the
match order within one rule at one state.

## How to pursue it

- Chance reproduction is hopeless (~1 in 40+ runs); the lever is *deterministically induced*
  cache loss. Ideas, in increasing order of invasiveness:
  1. run the two explorations with a debug hook that clears all `StateCache` references
     (or calls `System.gc()` after nulling a chosen state's cache) at a fixed state count,
     different between run 1 and run 2 — this simulates the unlucky GC placement;
  2. instrument `AbstractGraphState.setStoredTransitionStubs` to log (state, stub order)
     and diff the two runs to find the first divergent state — the divergence site shows
     which set's iteration order leaked;
  3. audit the plan engine's search-item iteration (`match/plan`) for places that iterate
     a `HostEdgeSet`/`HostEdgeStore` where the result order reaches `MatchResultSet`
     insertion, and consider sorting matches per (rule, state) by anchor images — that
     would make link 1 immune to link 3/4 rather than chasing reconstruction determinism.
- The failing configuration, for completeness: full suite in one surefire JVM (shared heap
  history), so any reproduction attempt should either run the full suite or fake its heap
  state; `DeterminismTest` alone has too little GC activity even at `-Xmx40m`.
- Related: `claude/randomness-seeding.md` (step 3 of the determinism program; this flake
  is a residual hole in the earlier steps, independent of intentional randomness).
