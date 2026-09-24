# Stray edge copies from the coanchor map in multigraphs

*Status (2026-09-24)*: fixed and merged into master as 9ec635802 (branch
`multigraph-coanchor-edge-leak`, one commit, rebased once over the factory-user-leak
archive move). Regression test `CacheReconstructionTest.testParallelPump[Spo]Rerecording`.
Explicitly left alone: the merge-map image minting described under "Not addressed".

Investigation record of the copy-mode edge surplus on the `hub-ring-1000-unstored`
benchmark row of `claude/exploration-performance.md`, 2026-09-24.

## Symptom

The row explores 200 000 steps of one token flag moving around a ring of 1 000 leaves
(grammar `junit/performance/hub.gps`, start graph `ring-1000-1`, program `chain`, on branch
`exploration-performance`). The host factory of the GTS ended with 8 007 edges in copy mode
(`Record.setRandomAccess(true)`, the Simulator's mode) against 3 007 in swing mode, with
identical state and transition counts. The start graph has 2 004 host edges and the rule
creates only token flags, so at most 3 004 distinct edge contents can occur.

## Mechanism

Two premises of the investigation brief were wrong, and correcting them is most of the
finding.

1. `hub.gps` is not a simple grammar. It has no `semantics` property, and the default
   since the 2026-08 semantics migration is `SPO-multi`, so its host factory is non-simple:
   the plain three-argument `HostFactory.createEdge` does not pool by content but mints a
   fresh parallel copy on every call (`StoreFactory.storeEdge`, non-simple branch:
   an unused edge number is registered unconditionally). The content pool is consulted
   only by the four-argument `createEdge(source, type, target, excluded)` of gh #905.
2. The surplus edges therefore have ordinary content. They are copies of token flags
   between real factory nodes, all registered from `BasicEvent.recordCreatedEdges`.

In the non-simple branch of `recordCreatedEdges`, the end nodes and type of each creator
edge image were obtained by `coanchorMap.mapEdge(creatorEdge)`, and the created edge then
resolved through the pool. `AGraphMap.mapEdge` creates and caches an image when the map
holds none, via the three-argument `createEdge`: one minted copy per computation of the
coanchor map. The map lives in the event's softly referenced cache (`AbstractRuleEvent`),
so every collection of that cache followed by another application of the event minted a
further copy, which the pooled lookup then passed over in favour of the earliest
non-excluded copy. Factory edges are strongly held (`StoreFactory.edges`,
`edgeCopies`) and never released.

Copy mode retains every state graph, which makes the soft event caches the first thing
the collector drops; hence five discarded copies per leaf in copy mode, against a single
stray in swing mode (the start leaf: its flag already existed as a start-graph edge, so
the first mint for that leaf was the only one ever passed over).

The instrumented run also showed where the remaining three edges above the naive
expectation come from: the GTS start state is a clone of the grammar's start graph under
the GTS algebra family, which produces a second value node for `0` and re-creates the
three `let` attribute edges towards it. Not a leak, and not touched.

## Fix

`recordCreatedEdges` now reads the end node images and the label from the coanchor map
without mapping the edge and draws the created edge from the pool through
`RuleEffect.addCreateEdge`, the path the complex creator edges already used. The only
other user of the mapped creator edge images, `BasicEvent.conflicts`, skips its edge
comparisons for non-simple graphs, where they could never succeed: a created copy lies
outside the source graph and edge equality is by number. `computeSimpleCreatedEdges`
asserts that it is only reached for simple graphs, so the minting path cannot return
unnoticed.

After the fix the row ends with 3 006 factory edges in both modes.

## Regression test

`CacheReconstructionTest.testParallelPump[Spo]Rerecording`: explore the grammar, then for
every state-creating transition clear the event's cache and record its effect again
against the same source graph and predefined created nodes. The factory must not grow and
the re-recorded added edges must be the identities in the target graph. Before the fix the
factory grew from 16 to 40 and from 17 to 53 edges.

A pitfall met while writing it: `DeltaHostGraph.edgeSet()` returns the graph's live edge
set, which in swing mode migrates to whichever graph of the chain was materialised last.
A set held across a call that materialises another graph shows that graph's content;
copy it first.

## Not addressed

`MergeMap.mapEdge` creates its merge-redirected images through the same three-argument
`createEdge`, once per `RuleApplication`, and `RuleApplication` substitutes them by the
recorded identities. Whether those images accumulate in the factory was not established:
a probe that cleared all state and event caches of the explored `parallel-pump` and
`parallel-pump-spo` GTSs and re-derived every graph three times showed no growth (15 and
16 edges throughout). Left as is.
