# Edge bundles in the isomorphism checker (gh #906)

Working note for the repair of gh #906. Slice 0 (measurement) is done and
recorded below; slices 1–3 are the plan it justifies.

## The defects

Two distinct defects that want the same data structure — a content-keyed
**edge-bundle index** (`(source, label, target)` → parallel copies).

1. **The equality shortcut collapses in non-simple mode.**
   `IsoChecker.areGraphEqual` short-circuits a comparison by node/edge *set*
   equality. Edge identity includes the edge number (`ANumberedEdge.equals`),
   and `StoreFactory.storeEdge` bypasses the content pool when the factory is
   non-simple, so confluent derivations mint content-equal edges under
   different numbers and the shortcut fails on nearly every comparison. Each
   failure falls through to partition-map construction plus a full morphism
   construction, and sometimes to the backtracking search.

2. **Parallel edges disable the certificate fast path.** `hasDiscreteCerts`
   requires the edge partition map to be one-to-one; parallel copies have
   equal certificates by construction (`PartitionRefiner.MyEdge2Cert.equals`
   compares endpoint certificates and label only). Every comparison of a graph
   carrying a bundle therefore falls into the backtracking search, which
   additionally enumerates the k! interchangeable assignments of a k-bundle
   before failing elsewhere.

## Slice 0: measurement (2026-08-29, master c4c7957e1)

Method: `Generator -v 2` prints per-phase timings and isomorphism counters;
`-D semantics=SPO-multi` selects the mode without copying the grammar (the
pre-3.12 conversion pins the stored grammars to `SPO-simple`, the override
happens after). Counter runs used a temporary `Measurement` collector in
`IsoChecker` (reverted; it classified each comparison beyond the equality
shortcut, and the certificate discreteness of each codomain). Timings are from
the uninstrumented build, three runs each, all within ±3%.

### `append`, start graph `append-4-list-8`, 31104 states in both modes

| | simple | multi |
|---|---|---|
| total | 2608 ms | 4052 ms (+55%) |
| iso checking | 653 | 2228 |
|   certifying (incl. partition maps) | 589 | 1030 |
|   equals check | 196 | 100 |
|   cert check | 36 | 800 |
|   sim check | 6 | 232 |
| equality shortcut hits | 80297 | 4256 |
| answered by certificates | 2546 | 71006 |
| answered by search | 62 | 7643 |

The whole +1444 ms sits in isomorphism checking (+1575 ms; other phases are
marginally faster in multi mode). Of the 78649 comparisons that get past the
equality shortcut in multi mode:

- **44219 (56%) would be answered by the proposed second-line test** — node
  sets equal, edge-bundle multisets equal. These are the confluent-derivation
  comparisons that simple mode answers by set equality.
- 33851 (43%) have **differing node sets** — see the event-reuse finding below.
- 579 differ in their bundle multisets.

Certificate discreteness in multi mode: 71006 discrete, 7643 with clashing
*node* certificates, and **zero** with clashing edge certificates. So `append`
contains no parallel edges at all, confirming the parenthetical in the issue
text: it measures defect 1 only. *Confirmed in slice 2:* under bundle
certificates every `-v 2` counter on `append` is bit-identical to the
per-copy baseline (equal certificates 31965, equal simulation 2465), so no
comparison changed paths — every bundle of every compared state is singular.
(A slice-2 draft of this note briefly claimed the opposite, from comparing
these 2465 positive simulation answers against the 7643 above; the two are
different measures — the 7643 count comparisons *entering* the search,
negative outcomes included.)

### `As-and-Bs-reg-exp-benchmark`, `-s bfs:9`, SPO-multi, 30674 states

This grammar's `AaB` rule creates `new:a` with no guard against the existing
copy, so every application mints another parallel copy — it diverges under
SPO-multi, hence the depth bound. It is the benchmark for defect 2.

| | multi, bfs:9 |
|---|---|
| total | 1622 ms |
| iso checking | 456 |
|   certifying | 192 |
|   equals check | 87 |
|   cert check | 9 |
|   sim check | 226 |

At `bfs:8` (12839 states), of the 13778 comparisons beyond the equality
shortcut: 1917 discrete, 1262 with clashing node certificates, and **10599
(77%) one-to-one modulo bundles** — i.e. in the search only because parallel
copies share their certificates. Those are exactly what slice 2 converts to
the fast path; the search is ~50% of iso time and ~14% of total runtime here.

The second-line test is worthless on this grammar (all 13778 comparisons have
equal node sets but genuinely different bundle multisets, and the 11861 that
are nevertheless isomorphic are so under a *non-identity* node map). The two
benchmarks are therefore complementary: `append` measures slice 1, `As-and-Bs`
measures slice 2.

### Two findings that change the surrounding picture

**"Certifying" is half partition maps.** `IsoChecker.getCertifyingTime`
aggregates `computeCertificates` *and* `getPartitionMap`. Certificate
computation itself is unavoidable — `GTS.StateSet.getCode` forces a graph
certificate and a certificate map for every state in both modes, and the
iteration histograms are within 1.5% of each other — but the partition maps
are built lazily, only when a comparison gets past the equality shortcut: ~2.6k
times in simple mode against ~78.6k in multi. That is the +441 ms, and it is
partly recoverable, unlike the certificate computation itself. (Cache
statistics show zero clears, collections and reconstructions in both modes,
so soft-cache pressure plays no part.) This does not make the JFR comment's
"recovers nearly all of the overhead" come out right — see the slice-1
outcome below.

**43% of the append overhead is created-node reuse, not edges (gh #905).**
The 33851 comparisons with differing node sets are the shadow of a collapse in
event reuse: `Events: 88` in simple mode against `3797` in multi, and coanchor
normalisation (`HostFactory.normalise`, reached from
`BasicEvent.getCreatedNodes`) drops from 1409 reuses over 38 distinct arrays to
116 over 1247. Anchor images in a non-simple graph are distinct edge objects
per state, so matches that were the same event in simple mode become distinct
events, and their created *node* images are no longer shared. The states are
then isomorphic but not equal, and no content-based edge test can help — the
certificate path is the only way to collapse them. This gives #905 a measured
mechanism (event and node-image reuse) rather than the allocation speculation
in its text, and makes it the other half of the `append` overhead.

## Plan

**Slice 1 — second-line content-up-to-count equality.** An `EdgeBundles`
index, computed on demand and kept with the graph's certifier.
`areGraphEqual` keeps real set equality as its first line;
on failure, and only for non-simple graphs passing the existing same-factory
guard, it compares node sets plus edge-bundle multisets (summary hash first).
Boolean path only — `getIsomorphism` untouched. Sound because equal node sets
plus equal bundle multisets give an isomorphism that is the identity on nodes.
Order-independent, so no determinism exposure. No identity-morphism hazard:
`MatchApplier.apply` already marks every collapse-onto-existing-state
transition as a symmetry when iso checking is on.
Target: the 44219 comparisons and most of the partition-map time on `append`.

**Slice 1 delivered (2026-08-29).** Net −7% on `append-4-list-8` (4052 →
3763 ms), isomorphism checking −33% (2228 → 1503 ms), 44219 comparisons
answered as predicted, state and transition counts unchanged. That is well
short of "nearly all of the overhead": half the overhead is #905's
node-identity problem, and the second line has an O(E) cost per comparison
that the equality shortcut does not — the domain of a comparison is always a
freshly derived graph, so indexing *it* is pure loss (measured: 460 ms of the
810 ms that a symmetric map-versus-map implementation cost, which was a net
wash). The implementation therefore indexes one graph and streams the other's
edges through that index, allocating one counter per bundle and nothing else;
node sets are compared by probing the codomain's certificate map, which the
equality test has already materialised. On `As-and-Bs`, where the second line
never succeeds, it costs +4%, to be repaid by slice 2.

Which graph gets indexed matters, and the obvious rule — index the codomain —
would have rested on an undocumented convention: that the domain of a
comparison is the fresh graph and the codomain the stored one. That holds
through three hops (`TreeHashSet.areEqual(newKey, oldKey)` →
`GTS.StateSet.areEqual` → `IsoChecker.areIsomorphic`), was stated nowhere and
enforced nowhere, and a caller reversing it would have silently indexed the
throw-away side. Two answers, both applied: the choice is made from what the
graphs actually carry (index one that has been indexed before, else one with a
certifier to keep an index alive, preferring the codomain), so it self-corrects
for any caller that compares the same graph twice; and the convention is now
documented where it originates, in `TreeHashSet.areEqual`, with
`GTS.StateSet.areEqual`'s parameters renamed to match. Renaming the checker's
own `dom`/`cod` was rejected: they are right for the morphism-returning half of
the class, whose callers have no new/old relation at all.

The index lives in `CertificateStrategy` rather than in `GraphCache`, so that
no further specialised accessor is added to `AGraph` (`hasCertifier`/
`getCertifier` are already there for want of an alternative), and because it is
package-visible to the checker only. Slice 2 needs the bundles inside the
certificate machinery in any case.

**Slice 2 — bundle certificates.** One `EdgeCertificate` per bundle, carrying
its multiplicity, replacing one per copy. Parallel copies provably carry
identical certificate values at every iteration, so scaling each node
contribution (and the graph-certificate accumulation) by k makes the
refactoring **outcome-identical** to the current refinement while dropping the
per-iteration edge loop from O(E) to O(#bundles). Multiplicity goes into the
certificate's `equals`, not its value, so the edge partition map becomes
genuinely one-to-one whenever each class holds a single bundle and
`hasDiscreteCerts` needs no change; `getCertEqualIsomorphism` pairs the two
bundles' copy lists in order, and gains the explicit edge-count and class-size
checks that the singleton-only code got for free. `getCertificateMap` must map
every copy to its bundle certificate. The abstract factory methods gain a
multiplicity parameter, so `Bisimulator` and both `PaigeTarjanMcKay` variants
follow mechanically. Search-plan items become bundles as a consequence, and the
per-copy `usedEdgeImages` set in `computeIsomorphism` goes: two dom bundles can
share a cod bundle only if their mapped endpoints and label coincide, which
injective node mapping plus content keying already rules out.
Gate: identical verdicts and identical graph certificates against master over
the corpus. Target: the 10599 bundle-only searches on `As-and-Bs`.

**Slice 2 delivered (2026-08-30).** Two commits: the bundle certificates
themselves, and a follow-up that stops the certificate fast path from
materialising morphisms. Same-day totals against the slice-1 tip (three runs
each, all state counts identical):

| | slice 1 | bundle certs | + node-map answer |
|---|---|---|---|
| `append` multi | 3980 ms | 4100 | 3850 |
| `As-and-Bs` bfs:9 | 1765 | 1700 | **1610 (−9%)** |
| `As-and-Bs` bfs:11 | 6110 | 6150 | 5950 (−2.7%) |

The search collapsed as planned (`As-and-Bs` sim check 190 → 42 ms at bfs:9,
565 → 60 at bfs:11), but the first commit alone was a wash: the comparisons
that used to search moved onto the certificate path, where `areCertEqual`
materialised a full morphism per answer — costing about what the search had
(bfs:11: cert check 15 → 336 ms), on top of the eager bundle build in every
non-simple certificate initialisation (certifying 741 → 1050 ms at bfs:11).
On `append` nothing moves (its bundles are all singular, see above), so its
column isolates the two overheads against the node-map saving — which there
relieves a cost the per-copy fast path had all along, its 31965 cert answers
each having built a morphism.
The second commit removes the first cost: the node map alone carries the
whole verification — edge-certificate equality implies endpoint-certificate
equality, so node-partition discreteness makes the node map injective, and
equal multiplicities make any in-order pairing of the copies of matched
bundles a valid completion; with the explicit node/edge-count check this is
also surjective, which incidentally closes a collision-sized surjectivity
hole that predates the change (the per-copy fast path never compared edge
counts). The morphism-returning `getCertEqualIsomorphism` is rebuilt on the
same node-map construction and keeps the edge pairing for its callers.

What remains on the table is the eager bundle build: every certified
non-simple graph pays one extra O(E) hash-probe scan plus five small array
allocations, whether or not it has parallel edges (≈ +280 ms of certifying at
bfs:11, ≈ +90 on `append`). Folding the bundle grouping into the certificate
initialisation scan (create a certificate on new content, bump a mutable
multiplicity on a duplicate) would halve that, at the price of spreading
mutable multiplicities through all four certifier implementations; judged not
worth the review surface for now, recorded here in case profiles later say
otherwise.

Outcome-identity of the refinement was re-derived adversarially before
implementation: per-copy contributions are `int` sums, so k separate
additions equal one addition of k·v under wraparound, and the graph
certificate accumulates in `long`, where k additions of a sign-extended `int`
equal `(long) k * v` — the widened form is what the code uses. The
`usedEdgeImages` removal argument in the plan above glossed over its own main
case: the set also guarded two copies *within* one bundle mapping to the same
cod copy, which bundling eliminates by construction (one plan item, pairwise
assignment); the cross-bundle case is the one node injectivity catches.
Found in passing: `Bisimulator.MyEdge1Cert.equals` negates its label
comparison (`!this.label.equals(...)`), a latent bug reachable only under the
`ISO_ASSERT` debug flag; preserved as found, to be fixed separately.

**Slice 3 — close-out.** Re-measured table, note updated, issues closed.

Deferred, recorded on the issue: with bundle certificates the *enumerating*
`getIsomorphism(dom, cod, IsoCheckerState)` returns one isomorphism per bundle
pairing and no longer walks intra-bundle permutations, which `ConfluenceAnalyzer`
needs (it compares full edge maps across successive isomorphisms). Restoring it
needs a permutation odometer on the enumeration path; left to gh #886, which
owns critical-pair revival.

## Verification

Per slice: `mvn -q test`; `grammar-smoke` (`ExplorationTest` state counts must
be *identical* — slice 1 can only turn certificate false negatives into
positives and slice 2 is outcome-preserving, so any count change is a bug);
`determinism-check` (slice 2 touches the search plan); `null-check` on touched
files. `IsoTest.testParallelEdges` is the standing guard that parallel copies
must not collapse onto one image — since slice 2 it additionally asserts that
such graphs are answered by the certificates rather than the search, and its
morphism assertions pin the pairwise copy mapping of the fast path;
`GraphTest.testGetPartitionMap` asserts partition sizes against edge counts on
simple graphs, so it doubles as a check that simple mode is untouched.
