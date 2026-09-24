# Copy-on-write edge stores (finding 4.3.2)

Status 2026-09-24: first implementation on branch `cow-edge-stores` (off master
`01e38fd95`), awaiting a decision on the swing-mode regression (below). Gates not yet
run: `determinism-check`, `grammar-smoke`, `null-check`, GUI tests. The fast suite passes
(890 tests).

## Problem

In copy mode (`Record.isCopyGraphs()`, forced by the Simulator's random access) every
materialised graph copied its whole `HostEdgeStore`s (`LinkedHashMap`s) and its global
edge set (`TreeHashSet`) entry by entry: O(|G|) per step, retained in the soft state
caches, most of it then paid again in GC. The per-node edge sets were already
copy-on-write (`CopyTarget`'s fresh-key sets). Diagnosis and profile in
`claude/exploration-performance.md`, finding 4.3.2 (corrected 2026-09-24, on branch
`exploration-performance`).

## Design as built

- `util/collect/ForkableHashTable` (package-private core), `ForkableHashMap`,
  `ForkableHashSet`: a hash table split into 2^b buckets by the top bits of the spread
  hash, each bucket a small open-addressing table (linear probing, at most half full,
  backward-shift deletion). b grows (never shrinks) so that the average bucket size stays
  below the bucket count, i.e. about sqrt(n) buckets of about sqrt(n) keys.
- Forking copies the bucket array only. Each bucket records the table that owns it
  (owner token); a fork gives both tables fresh tokens, so either clones a shared bucket
  on its first write. Forking writes nothing but the original's token, so reads of a
  graph that is no longer modified stay safe while it is forked.
- `HostEdgeStore` extends `ForkableHashMap`; its copy constructor forks (and, for the
  deep copy of swing mode's copy bound, re-puts cloned sets). `DeltaHostGraph.edgeSet`
  is a `ForkableHashSet`; `edgeSet()` now returns `Set<HostEdge>`.
- Iteration order of the stores and the global edge set becomes bucket/slot order
  (deterministic, history-dependent); accepted by Arend 2026-09-24. The per-node sets
  keep insertion order.

## Measurements (A/B, one JVM per row, JDK 25, master classes vs branch classes)

Random-access (copy) mode, median time, retained heap:

| row | master | branch |
|---|---|---|
| `hub-ring-1000-unstored` | 25.9 s | 1.1 s |
| `hub-field-hop` | 58.7 s | 5.6 s |
| `petrinet-join-100` | 30.4 s | 11.8 s (retained 131 MB to 2.85 GB, see open points) |
| `hub-chain-200-2` | 10.5 s, 423 MB | 10.9 s, 83 MB |

Swing (Generator) mode, after tuning (presized rehash, single-lookup put/remove, hash
pre-compare before `equals`, cached key-set view, load 0.5, direct set `hashCode`):

| row | master | branch |
|---|---|---|
| `sierpinsky-11` | 0.68 to 0.77 s | 0.84 to 0.86 s (+10 to 25 %) |
| `leader-election-14` | 5.6 to 5.7 s | 5.9 s (+3 to 5 %) |
| `hub-chain-200-2` | 10.5 to 11.2 s | 11.2 to 11.5 s (noise to +8 %) |
| `hub-ring-1000-unstored`, `petrinet-join-100`, `count-100000` | | within noise to +4 % |

The swing cost is per operation (bucket indirection, probing on insertion; sierpinsky
is nearly pure insertion into graphs of up to a million elements), all in `gen`.
Profiles show no single hot spot left to tune.

## Proposal: forkable structures in copy mode only

`DeltaHostGraph` builds forkable stores and edge set when `copyData` is set, the current
`LinkedHashMap`/`HostEdgeSet` ones otherwise; `CopyTarget` forks a forkable source and
copies a classic one as before. Swing mode is then unchanged by construction. Cost: a
store abstraction (`HostEdgeStore` delegating to one of two maps, a bimorphic call site)
and two creation paths for the global edge set. Rejected alternative: keep one
representation and accept the swing-mode regression (the Generator and most of the
benchmark set run in swing mode).

## Open points

- Remaining copy-mode cost: refresh clones of large touched per-node and per-label
  sets (`petrinet-join`: hub places, label sets of hundreds of edges). Forking those
  sets as well is a possible second step.
- `petrinet-join-100` random-access retention: presumably soft state caches surviving
  under lower GC pressure; to be confirmed with a smaller heap.
