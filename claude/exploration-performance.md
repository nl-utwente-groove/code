# Exploration performance review

Code-reading review (2026-09-20) of the state-space exploration hot path, looking for
performance headroom at every level: algorithms, data structures, allocation, and
JIT-friendliness, together with the throughput harness and the grammar set built to
measure it. One item found on the way, a null dereference in `Proof.equals`, is already
fixed on master (`9ff9fb4d5`) and is not repeated here.

**Status (2026-09-24).** The harness (`test/performance/ExplorationBenchmark`), the
`junit/performance` grammar set (15 grammars, 58 rows in three tiers), baselines of the
quick and long tiers in the Generator's mode and of the quick tier in the Simulator's
random-access mode, and a coverage assessment are done; the set is judged sufficient to
start fixing (see "Coverage"). Building it found three defects that were fixed on their
own branches and merged: finding 3.11 (gh #919), finding 3.12 (gh #924) and the recipe
targets on cyclic regions (gh #925). The first investigation, finding 3.13 (the
counter's generation time), traced it to 3.1, fixed on branch `certifier-node-table`;
nothing else in the finding list is implemented. The transient handoff
state (what is next, in which order) lives in `claude/exploration-performance-state.md`.

The note runs from the test cases to the numbers to the findings: the grammar set, the
harness, the runs and their outcomes, the finding list, and the coverage of the set.

## Method and caveats

Five parallel reviews, one per area: matching (`match`, `match/plan`), isomorphism
checking (`graph/iso`, the GTS state set), LTS and state storage (`lts`,
`grammar/host` delta machinery, `util/collect`), transformation and control (`transform`,
`control/instance`, `algebra`) and the exploration driver (`explore`, listeners,
cross-cutting utilities). The highest-impact claims (dead confluent-diamond shortcut,
always-on `Reporter`, `Factory.get` monitor, `CHECK_IMAGES`, certifier array sizing,
eager certificate map, refinement loop, weak certifier reference) were re-verified in
the source by the orchestrating session.

Paths in the findings are relative to `src/main/java/nl/utwente/groove/`. Each finding
carries an expected impact (high/medium/low), an effort estimate (small/medium/large)
and how confident the reviewer is that the code is really on the hot path. All of it was
verified by reading; inferences are marked as such. The impact ratings are expectations,
not measurements, except where a row of the grammar set has since measured a finding
(3.7, 3.11, 3.12, and the outcomes under "Runs and outcomes").

## The performance grammar set

`junit/performance/` holds the grammars the harness runs, kept apart from
`junit/samples` so that the correctness fixtures and their test expectations stay
untouched and the performance copies can drift freely; only the harness reads it. Each
grammar holds its default start graph and the start graphs of its rows; the other sample
start graphs were dropped. `junit/performance/generate-starts.py` generates the start
graphs of the regular families (named per grammar below); its `SIZES` table is the
record of what is generated, and regenerating overwrites those files and nothing else.

The sizes are chosen per tier (see "The harness"): smoke rows run in at most a few
seconds, quick rows up to about a minute at `-Xmx4g`, long rows two to five minutes at
`-Xmx8g`. The calibration tables below are single cold runs on the desktop (20 cores,
32 GB), through the headless `Generator` or through the harness one JVM per row, at
`-Xmx8g -da -XX:+UseParallelGC` unless noted; they list the kept sizes and the boundary
sizes that show why the next one was not kept. The timings of the kept rows are in the
baselines under "Runs and outcomes".

### As-and-Bs (`As-and-Bs-reg-exp-benchmark.gps`)

Regular-expression matching with a path cache, NACs. Generated start graphs
`start-N-M`: complete bipartite `b` edges.

| start graph | config | states | transitions | kept |
|---|---|---|---|---|
| `start` | | 8 240 | 44 774 | smoke |
| `start` | `collapse=equality` | 262 144 | 1 413 120 | quick (7.8 s) |
| `start-4-3` | | 131 505 | 947 824 | quick |
| `start-4-3` | `collapse=equality` | | | out of heap at 8 GB |
| `start-5-3` | | > 1.7 M | > 15 M | too large, stopped |
| `start-4-4` | | > 2.2 M | > 16 M | too large, stopped |

Growth is steep in both directions, so there is no long-tier size; an intermediate edge
density is untried. The equality rows cover 4.4.4.

### Mark-Unmark (`Mark-Unmark-List-regexp-benchmark.gps`)

Regular expressions with a high transition-to-state ratio. Generated `tree-N`: a
complete binary `next`-tree. States grow about 2.3-fold per two levels, but the count is
not monotonic in the size: the generator's tree shape depends on the number.

| start graph | states | transitions | kept |
|---|---|---|---|
| `start` | 24 576 | 368 640 | quick |
| `tree-18` | 48 384 | 870 912 | quick |
| `tree-21` | 169 344 | 3 556 224 | upper quick |
| `tree-22` | 338 688 | 7 451 136 | long (122 s) |
| `tree-23` | 151 704 | 3 489 192 | dropped: smaller than `tree-21` |

### append (`append.gps`)

The largest breadth-first run among the original samples, also run under equality
collapse (4.4.4). Generated `append-A-list-L`: a longer list with more appenders.

| start graph | config | states | transitions | kept |
|---|---|---|---|---|
| `append-4-list-8` | | 31 104 | 114 008 | quick |
| `append-4-list-8` | `collapse=equality` | 73 792 | 268 912 | quick |
| `append-4-list-10` | | 1 077 000 | 4 008 820 | long |
| `append-5-list-6` | | > 1.5 M | > 6.4 M | too large, stopped |
| `append-4-list-12` | | > 1.5 M | > 5.7 M | too large, stopped |

### inheritance (`inheritance.gps`)

A diamond-rich lattice of commuting rule applications, the ground for the dead
confluent-diamond shortcut (2.1): `confl` must turn non-zero when that is fixed. Also
subtyping. Generated `start-N`: a typed ring with chords.

| start graph | states | transitions | kept |
|---|---|---|---|
| `start` | 756 | 5 374 | smoke |
| `start-11` | 74 868 | 939 355 | dropped |
| `start-12` | 297 212 | 4 317 133 | quick |
| `start-13` | 552 824 | 8 148 238 | dropped: 41 s and 3.3 GB retained, between the tiers |

### pacman (`pacman.gps`)

Quantified rules over a small graph. The sample's `start_four_ghosts` explored to a
handful of states and was replaced by a hand-made maze of 24 nodes, 37 transitions per
state; a four-ghost maze of 20 positions was far too large (beyond 247 k states and
5.2 M transitions, 67 k of them open, when stopped).

| start graph | states | transitions | kept |
|---|---|---|---|
| `start` | 256 | 1 536 | smoke |
| `start_four_ghosts` | 210 102 | 7 819 623 | long |

### car-platooning (`car-platooning.gps`)

NACs and rule priorities, twenty rules (the largest rule set), isomorphism checking
switched off by the grammar (`checkIsomorphism=false`), so the rows spend their time in
matching and `gen`; the factory edge count shows the interning probe edges of 3.3.
Hand-made start graphs; `start-18` is in the directory but no row uses it.

| start graph | states | transitions | kept |
|---|---|---|---|
| `start-05` | 110 366 | 369 601 | quick |
| `start-06` | 2 988 061 | 11 929 077 | long (170 s, the largest breadth-first run) |

### sierpinsky (`sierpinsky.gps`)

Linear exploration over graphs growing to hundreds of thousands of elements: the
large-graph case for the certifier array (3.1) and the per-node edge sets (4.3.2),
dominated by `gen`. Collapse is off in the record, so 2.5 applies. Hand-made start graphs.

| start graph | states | factory nodes, edges | s | retMB | kept |
|---|---|---|---|---|---|
| `start11` | 12 | 265 734, 841 476 | | | smoke |
| `start12` | 13 | 797 176, 2 524 375 | 3.6 | 1 234 | quick |
| `start13` | 14 | | 10.8 | 3 805 | dropped: too short for long, too heavy for quick |

### generate-binary-tree (`generate-binary-tree.gps`)

Depth-first with a depth bound: long delta chains, certifying-heavy. The same unstored
(`persistence=none`) explores the full tree unfolding, transitions = states − 1, about
tenfold per level; the chain findings 2.6 and 2.7 apply. The GTS keeps only the
written-back trace (9 to 11 states), so the discovered counts describe the work, and the
retained heap is the softly reachable state caches of 3.6. Default start graph only.

| row | depth | discovered states | s | retMB | kept |
|---|---|---|---|---|---|
| `binary-tree-dfs12` (stored) | 12 | 4 012 | | | smoke |
| `binary-tree-dfs-unstored` | 8 | 409 114 | | | smoke |
| `binary-tree-dfs-unstored-9` | 9 | 4 037 914 | 14 | 1 826 | dropped: collector-bound at 4 GB |
| `binary-tree-dfs-unstored-10` | 10 | 43 954 714 | 131 | 4 975 | long |

### leader-election (`leader-election.gps`)

The symmetric-ring case of finding 5.6. The sample's hand-drawn `-init` start graphs,
meant to skip the factorial number-picking stage, carry `type:` and `flag:` prefixes
that the rules do not use and explore to a single state; they were dropped from the
copy. Generated `ring-N`: the plain start graph after number picking, the values
assigned around the ring in a fixed pseudo-random order, the `Numbers` pool empty.
States and time grow about fourfold per two processes, so `ring-20` would take some ten
minutes.

| start graph | states | transitions | s | kept |
|---|---|---|---|---|
| `ring-8` | 820 | 3 405 | 0.3 | smoke |
| `ring-14` | 49 620 | 386 295 | 7.7 | quick |
| `ring-16` | 197 404 | 1 772 291 | 31 | upper quick |
| `ring-18` | 787 648 | 7 737 099 | 156 | long |

**`leader-election-14-injective` (added 2026-09-23)**, coverage gap 4: the same row with
`matchInjective=true` through the properties override. The counts are identical to the
base row, so the injectivity filter rejects nothing here and the row isolates its
per-candidate cost: 1 to 4 % of wall time and 5 to 8 % of the `match` column (1 204 to
1 250 ms against 1 110 to 1 184 ms in alternating one-JVM runs), 2 % more allocation.

### attribute-count-to-n (`attribute-count-to-n.gps`)

A counter from 0 to a bound and back, one state per value: the attribute path of
findings 4.2.1 to 4.2.3 without matching or isomorphism costs. Generated `bound-N`: the
counter at 0, the bound N as a `let:` attribute. The BigInteger rows run the same start
graphs under `algebra=big` (the exploration key was broken until gh #923; since the fix
the harness builds its GTS through `ExploreType.newGTS`). The `probe-odd` rule
(2026-09-22) puts `ErrorValue` construction on the path, for 4.2.3: it divides 1 by
`value mod 2`, so every even state constructs an error (the algebra's
`ArithmeticException` plus the `ErrorValue` wrapping it, two stack traces) and every odd
state gets a self-loop. States are unchanged, transitions rose by half the states.

| start graph | states | transitions | s | kept |
|---|---|---|---|---|
| `bound-10000` | 10 001 | 25 001 | 0.6 | smoke |
| `bound-100000` | 100 001 | 250 001 | 5.9 | quick, plain and `algebra=big` |
| `bound-300000` (at `-Xmx4g`) | 300 001 | 750 001 | 34 | quick, plain and `algebra=big` |
| `bound-600000` | 600 001 | 1 500 001 | 131 | long |
| `bound-1000000` | | | out of heap | too large |

Times except `bound-600000` predate `probe-odd`. The million is about 7 GB of live GTS,
the ordinary per-state cost, not a leak. The number of value nodes grows linearly with
the values generated, by design: every value is wrapped in a `ValueNode` once and none is
ever collected, so `fNodes` at 600 008 is expected. What is not expected is the time the
rows spend in generation and the allocation behind it: 0.4 MB per state at 100 k, 1.2 MB
at 300 k, 2.3 MB at 600 k (1.4 TB in all), for a step that creates one value node and
one edge. That is finding 3.13; its cause is 3.1, the certifier's array sized by the
factory's node numbers, which the minted value nodes grow by one per state.

Cost of the probe, `count-100000` and its BigInteger twin with and without the rule,
2 warm-ups and 3 runs at `-Xmx8g`, one JVM per pair, the with-probe pair run before and
after the without-probe pair:

| | without | with, before | with, after |
|---|---|---|---|
| `count-100000` med ms | 5 145 | 5 518 | 5 328 |
| `count-100000` match ms | 301 | 504 | 451 |
| `count-100000` allocMB | 41 126 | 41 581 | 41 583 |
| `count-100000-big` med ms | 5 598 | 5 936 | 6 211 |
| `count-100000-big` match ms | 257 | 646 | 605 |
| `count-100000-big` allocMB | 41 157 | 41 656 | 41 655 |

So the probe costs 4 to 11 % of the row, 150 to 350 ms of matching and some 9 KB of
allocation per error (the two stack traces): the size of the signal a fix of 4.2.3 can
show here. BigInteger's extra cost is small and unstable: none in the cold calibration
runs (6.4 s against 7.3 s for the plain row), 13 to 25 % in the quick baseline, all of it
in `gen` at equal allocation.

### fibonacci (`fibonacci.gps`)

The naive exponential recursion on purpose: states grow with fib(x), about 1.6-fold per
step. Generated `fib-N`, the argument as a `let:` attribute. The default program
`fibonacci-recipe` wraps the recursion in a recipe, so the stored GTS has three states
and the work is in transient states, which the harness counts as discovered;
`fibonacci-function` runs it as a function over the same rules, so the same states are
plain stored states (one transition fewer): the control for the transience cost (4.3.1).
(`fibonacci-expressions` hard-codes its argument.) The recipe path was once superlinear
in the transient prefix; that was finding 3.12, fixed as gh #924.

State counts, `-D controlProgram=fibonacci-function`:

| start graph | states | transitions | s | kept |
|---|---|---|---|---|
| `fib-15` | 4 934 | 4 933 | 0.30 | smoke |
| `fib-22` | 143 284 | 143 283 | 2.6 | quick |
| `fib-25` | 606 964 | 606 963 | 10.4 | fits 8 GB only (5.2 GB retained) |
| `fib-27` | | | out of heap at 8 GB | too large |

Recalibration after gh #924, through the harness, one JVM per row, JDK 25.0.4.1, with
the function program in the same session:

| start graph | discovered states | recipe s | function s | recipe retMB | kept |
|---|---|---|---|---|---|
| `fib-15` | 4 934 | 0.29 | 0.30 | 42 | smoke (both) |
| `fib-17` | 12 919 | 0.52 | | 111 | |
| `fib-20` | 54 729 | 1.13 | | 474 | |
| `fib-22` | 143 284 | 2.49 | 2.21 | 1 256 | quick (both) |
| `fib-25` | 606 964 | 12.3 | | 5 429 | fits 8 GB only |

A transient state costs the same as a plain state (the `gen` column of `fib-22` is 1.7 s
against 1.4 s, matching and isomorphism under 100 ms in both), and the recipe rows mirror
the function rows. Neither family has a long-tier size: `fib-27` is about 1.6 million
states and does not fit 8 GB of live GTS.

### hub (`hub.gps`)

Arend's grammar for the large-graph axis: a star of `Leaf` nodes around one `Hub`,
tokens as flags on the leaves, a `build` program growing the star from a proto graph
(`proto-1000-10`), and `run`, which moves a token from its leaf to any empty leaf through
the hub. The type graph also has `from` edges from `Leaf` to `Hub`, a `pos` attribute on
`Leaf`, a `token` flag and a `moves` attribute on `Hub`, and `Stub` nodes linked to `Hub`
both ways. The rows' start graphs are generated. As built (1 000 leaves, 10 tokens) it
explores to one state, since the leaves are interchangeable: 295 s, of which
certification 219 s, 22 ms per certificate of a 1 001-node graph with a 990-fold
symmetric leaf class, which is finding 5.6 measured; the 1.3 MB allocated per transition
is 4.3.2.

Stored rows: `star-300-3` under `run`, the symmetry row (5.6, 2.4); `chain-N-k`,
consecutive leaves linked by `next` and a `chain` program whose `moveNext` moves a token
one step along the chain, never onto an occupied leaf, so the states are the token
placements (n for one token, about n²/2 for two), each a graph of n+1 nodes: the
many-states, large-graph rows for 3.1, 4.3.2 and the certifier without symmetry.

| row | states | transitions | s | cert ms | kept |
|---|---|---|---|---|---|
| `hub-1000-10` (as built) | 1 | 9 900 | 295 | 218 769 | not kept |
| `hub-star-300-3` | 1 | 891 | 2.4 | 1 521 | smoke |
| `hub-chain-1000-1` | 1 000 | 999 | 7.4 | 7 247 | quick |
| `hub-chain-200-2` | 19 900 | 39 402 | 10.9 | 10 092 | quick |
| `hub-chain-300-2` | 44 850 | 89 102 | 54.6 | 51 459 | next size, not kept |

Matching is 4 to 200 ms in every row; certification is the whole cost, and superlinear
in the graph on the chain too: 0.5 ms per certificate at 201 nodes, 1.15 ms at 301,
7.2 ms at 1 001, about quadratic, the refinement running one round per step of the
chain's diameter with each round a pass over the graph. So the chain rows measure the
certifier's dependence on diameter, the star row its dependence on symmetry.

Unstored rows, for 5.1, 5.2 and 3.7, on deterministic systems (one successor per state)
explored `next=newest cost=uniform bound=cost:N persistence=none`, a single path of N
steps with nothing certified:

- `field-2-100-2500`: two stars, each hub with 100 leaves linked both ways and numbered
  by `pos`, plus 2 500 untouched stubs, half pointing at the hub and half away; 5 202
  nodes, 10 810 edges, the largest fixture in the set. `hop` moves the leaf token to the
  leaf at `(pos + 1) % leaves`; its plan (printed by flipping `PlanSearchEngine.PRINT`)
  binds the second `from` edge with the *target* bound, and that item enumerates the
  hub's whole incident set of 2 703 edges (the target branch never consults the label
  set), 100 of which carry the label, for one match: finding 5.1 as stated. `jump` moves
  the hub token to the other hub, a typed node without edges in the rule, found by
  `Find node n1:[Hub]` over the node set of 5 400 (nodes plus value nodes): finding 5.2,
  two hits.
- `ring-1000-1`: the chain closed, a single token walking for ever; `chain` (`moveNext`)
  against `counted` (`moveCounted`, the same plus `let:moves = moves + 1` on the hub), the
  `let:` per move of 3.7.

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
and generation, and none of it is 3.7: in the exploration's swing mode
(`Record.copyGraphs` false) the in-edge store, once built by the first attribute erase,
moves along the materialisation chain with the other stores (`SwingTarget` takes over the
parent's references), so the O(V+E) walk runs once per chain, recurring only after a
reconstruction from the delta chain. In copy mode (the Simulator, `randomAccess`) every
child copies all four stores anyway. Hence the demotion of 3.7 to Low for exploration.
The counted row also retains 5 KB per step: the host factory keeps every value node and
`moves` edge it ever made (200 k and 400 k by `fNodes`/`fEdges`), the counter grammar's
growth too.

**The wander recipe rows (added 2026-09-23)**, coverage gap 2: cyclic and wide
transient regions. `movePrev` mirrors `moveNext` (the token one step back along `next`),
and the recipe of `wander.gcp` is `moveNext; (moveNext | movePrev)*`, so on `chain-n-2`
its region is the whole placement space of the two tokens, cyclic (a step back returns to
the same graph and frame) and with every state a recipe end. The main program `wander;`
launches once from the start state (the cost of traversing the region); `wander-alap.gcp`
runs `alap wander;`, so every public state launches again, finds the existing region and
needs its recipe targets recomputed over a cyclic region, which is the launch propagation
of `StateCache` under stress, at about states² recipe transitions.
Desktop calibration after the gh #925 fix (see below), single cold runs through the
harness, one JVM per row, JDK 25.0.4.1, `-Xmx8g` (`trans` is the stored count, here the
recipe transitions; `disc.tr` adds the rule transitions inside the region):

| row | states | trans | disc.tr | s | iso ms | gen ms | allocMB | retMB | kept |
|---|---|---|---|---|---|---|---|---|---|
| `hub-wander-20` | 191 | 190 | 875 | 0.15 | 10 | 78 | 14 | 0 | |
| `hub-wander-50` | 1 226 | 1 225 | 5 930 | 0.35 | 112 | 245 | 127 | 5 | |
| `hub-wander-100` | 4 951 | 4 950 | 24 355 | 1.9 | 1 357 | 1 721 | 961 | 21 | quick |
| `hub-wander-150` | 11 176 | 11 175 | 55 280 | 7.7 | 6 689 | 7 516 | 3 276 | 52 | |
| `hub-wander-200` | 19 901 | 19 900 | 98 705 | 22.7 | 20 428 | 22 330 | 7 636 | 111 | quick |
| `hub-wander-300` | 44 851 | 44 850 | 223 055 | 115 | 106 713 | 113 849 | 27 629 | 313 | under 2 min |
| `hub-wander-400` | 79 801 | 79 800 | 397 405 | 355 | 335 051 | 352 611 | 66 283 | 660 | over 5 min |
| `hub-wander-alap-20` | 191 | 65 170 | 66 197 | 0.22 | 10 | 90 | 66 | 14 | smoke |
| `hub-wander-alap-40` | 781 | 1 156 740 | 1 161 187 | 0.97 | 58 | 586 | 914 | 230 | |
| `hub-wander-alap-60` | 1 771 | 6 058 710 | 6 068 977 | 4.4 | 292 | 2 629 | 4 584 | 1 185 | quick |
| `hub-wander-alap-80` | 3 161 | 19 475 080 | 19 493 567 | 15 | 935 | 8 068 | 14 558 | 3 809 | too heavy for 4 GB |

The states are n(n−1)/2 + 1 as predicted, the single launch of `wander` gets every one of
them as a recipe end, the region is cyclic (about four rule transitions per state, each
undone by the opposite move), and `alap` costs a constant 1.2 to 1.4 M recipe transitions
per second with about 200 bytes retained per transition. So `alap` has no long size:
`hub-wander-alap-80` already retains 3.8 GB, and at 100 leaves, about 48 M transitions,
it would need some 9 GB. Two outcomes:

- **The recipe traversal costs what its transitions cost.** `hub-wander-200` has the
  state space of `hub-chain-200-2` (the same 19 900 placements on the same graph), which
  the plain `#moveNext` program explores in 10.7 s allocating 2.9 GB; under the recipe it
  takes 22.7 s (23.6 s on a second run) and allocates 7.6 GB, of which isomorphism
  checking is 20.4 s against 10.0 s in the chain row. The region has twice the chain's
  rule transitions (78 805 against 39 402, `movePrev` doubling the moves) plus the 19 900
  recipe transitions, each checked at about the same 0.2 to 0.25 ms, which leaves about
  2 s and 111 MB retained for the transient bookkeeping; before gh #925 the row took 161 s
  and allocated 65 GB. Time grows as about n⁴ (n² states, a certificate about n² on the
  chain): 7.7 s at 150, 115 s at 300, 355 s at 400, so no size lands in the long tier's
  two to five minutes, and the row kept is `hub-wander-200` in the quick tier, next to
  `hub-chain-200-2`.
- **The rows found a master bug, gh #925 (fixed and merged 2026-09-23).** Under breadth-
  and depth-first exploration a `wander` launch got 2n − 3 of its n(n−1)/2 recipe ends (37
  of 190 at n = 20; linear exploration found all), and `RecipeCompletenessTest` failed on
  `chain-20-2`. Cause: a region state leaves the recipe through the star's same-verdict
  exit as soon as its matches are computed, so it is steady before it is closed, and the
  gh #924 bookkeeping counted a steady successor as done; predecessors became full early
  and dropped the target propagation. Waiting for steady states with an inner prime frame
  (`StateCache.isDone`) made the counts right but the backward target propagation
  quadratic on this shape (`chain-100-2` 4.4 s → 8.9 s, `chain-200-2` out of a 6 GB heap),
  so it was replaced by forward launch propagation (a `LaunchSet` per region state), which
  visits each (launch, region state) pair once. Warm bfs timings, master → fix:
  `chain-100-2` `wander` 4.4 s and 19 602 transitions → 1.5 s and 24 355; `chain-200-2`
  90 s and 79 202 → 21 s and 98 705; `chain-60-2` `wander-alap` 3.6 → 4.1 s; `fib-22`
  unchanged. Re-pinned, `hub-wander-100` went from 5.0 to 1.9 s (`gen` 4.7 to 1.75 s),
  retaining 22 MB against 845.

### petrinet (`petrinet.gps`)

Arend's copy of the sample: one rule, `smartRule`, a transition firing when every input
place holds a token, consuming one per input place and producing one per output place,
as two `forall:` levels with an `exists:` token level inside the first. Of the five
hand-drawn nets (1 to 38 states) only `start2` stays, as the readable default. Two
generated families:

- `pipe-k-n`: k transitions in a row between k+1 places, n tokens on the first. Every
  token moves forward independently, so the states are the distributions of n tokens
  over k+1 places, C(n+k, k) of them, all distinguishable since the pipeline has a
  direction, on a graph of 2k+n+1 nodes. Each token on an input place is a separate
  `exists:` match, so a place holding m tokens gives its transition m parallel
  transitions to isomorphic targets: transitions run seven to ten times the states, and
  the rows are iso-check and generation rows more than matching rows. The family grows
  about 3.8-fold per step of k = n.
- `join-f`: one transition with f input places, each holding a token, and f output
  places; a second transition fires the tokens back. Exactly one of the two is enabled
  at any time, so an unstored bounded depth-first run is a single path alternating
  them, and every step matches a universal domain of f places on each side, 2f
  sub-matches each with its own context map (4.1.2), then applies a composite event of
  2f deletions and creations (4.2.4, 4.2.6).

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

### parallel-pump (`parallel-pump.gps`)

Arend's copy under DPO semantics, the multigraph rows: `pump` turns one of the hub's
parallel `c` loops into an `a` edge to a `b`-target, `drain` deletes one, `trim` deletes
one of two parallel `a` edges to the same target and flags the hub, `fold` merges two
targets. Generated `pump-k-m`: the hub with a `mult=k:c` loop and m targets, so the
states are the distributions of the pumped edges over the remaining targets, on a graph
of at most m+1 nodes, with 24 to 51 transitions per state: parallel edges in matching,
in the deltas and in the certifier's edge bundles (gh #906). The iso check and
generation are the main costs, matching under 3 %. States grow 3.7-fold and time 4.6-fold
per step of k+2, m+1. The SPO-multi twin (a property override) differs by under one per
cent in states (the `trim` matches that identify its deleted with its preserved `a` edge,
which DPO's identification condition forbids) and by nothing in time, since no rule
erases a node: the dangling check of 4.1.6 never runs here; the mergers DPO row covers it.

| row | states | transitions | s | match ms | iso ms | gen ms | allocMB | retMB | kept |
|---|---|---|---|---|---|---|---|---|---|
| `pump-8-4` (DPO) | 2 143 | 38 891 | 0.55 | 25 | 134 | 390 | 347 | 14 | smoke |
| `pump-12-6-dpo` | 36 894 | 1 231 379 | 6.7 | 125 | 2 726 | 5 907 | 11 945 | 345 | quick |
| `pump-12-6-spo` | 37 026 | 1 267 481 | 6.9 | 163 | 2 891 | 6 108 | 12 154 | 352 | quick |
| `pump-14-7-dpo` | 136 731 | 5 731 438 | 31.4 | 621 | 13 850 | 28 253 | 59 169 | 1 502 | dropped |
| `pump-16-8-dpo` | 479 787 | 24 438 977 | 168.6 | 3 845 | 76 288 | 152 314 | 268 792 | 6 010 | long |

### mergers (`mergers.gps`)

The sample copied by hand, its `system.properties` rewritten to 3.12 with the explicit
`semantics=SPO-simple` the version conversion would give it. Generated `ring-n`: n nodes
flagged a, b, c in turn, each with an edge to its successor and every second one with a
chord to the node three further on, edges labelled by the flags of their endpoints. The
rules merge a-nodes into b- and c-nodes (`merge-a-b`, `merge-and-merge`) and delete an
a-node while merging (`merge-and-del`), so the states are the reachable quotients of the
ring, about 8.5 times more per node: the merge path (`MergeMap`, the merge branch of
4.2.13). Under SPO-multi the parallel edges that merging creates survive: 4 % more states,
5 to 10 % more time than simple, a third more distinct factory edges. Under DPO the
identification condition rules out the matches that identify a deleted with a preserved
element (presumably the bulk: `merge-and-del`'s deleted a-node with the merged one), a
tenth of the states, and the dangling check runs per candidate of `merge-and-del`.

| row | states | transitions | s | match ms | iso ms | gen ms | allocMB | retMB | kept |
|---|---|---|---|---|---|---|---|---|---|
| `start` (simple) | 66 | 143 | 0.08 | 2 | 6 | 32 | 5 | 0 | default graph |
| `mergers-6` (simple and multi alike) | 202 | 681 | 0.10 | 5 | 8 | 50 | 12 | 1 | smoke (multi) |
| `mergers-9-simple` | 25 145 | 255 596 | 2.2 | 94 | 478 | 1 793 | 2 832 | 172 | quick |
| `mergers-9-multi` | 26 217 | 259 850 | 2.4 | 123 | 605 | 1 875 | 3 015 | 181 | quick |
| `mergers-10-multi` | 222 508 | 3 320 992 | 25.2 | 875 | 7 071 | 21 932 | 38 966 | 1 791 | upper quick |
| `mergers-11-dpo` | 70 065 | 735 888 | 7.2 | 281 | 1 435 | 5 889 | 10 463 | 517 | quick |
| `mergers-11-multi` | 1 084 025 | 19 356 013 | 456 | 7 638 | 127 947 | 398 622 | 240 387 | 5 640 | too large |

The family has no long-tier size: `ring-11` under SPO-multi is a million states and 19
million transitions, 5.6 GB retained and 7.6 minutes, and under simple it did not finish
in ten minutes at fewer states, which at that heap is the collector rather than the
semantics. `ring-6` explores to the same 202 states under simple and multi, so the smoke
row runs it under multi for the code path alone.

**`mergers-9-injective` (added 2026-09-23)**, coverage gap 4: `ring-9` under
`matchInjective=true`. Here the filter rejects the non-injective merges that are the
bulk of the grammar's matches: 12 775 states and 60 718 transitions against 25 145 and
255 596, a third of the allocation, 1.0 s against 2.3 s in alternating one-JVM runs. The
row is below the quick tier's 5 s floor but pairs with `mergers-9-simple`;
`mergers-10-injective` (118 923 states, 8.7 s) would need a `mergers-10-simple` base at
23 s, and `mergers-11-injective` (54 s) retains 4.8 GB.

### All rows

62 rows: 14 smoke, 39 quick, 9 long. The explore configuration is the default
(breadth-first, full) where the column is empty; `dfs(N)` abbreviates
`next=newest cost=uniform bound=cost:N`, `unstored(N)` the same plus `persistence=none`,
`linear` is `frontier=single successor=single`.

| row | grammar | start graph | program / properties / config | tier |
|---|---|---|---|---|
| `inheritance` | inheritance | `start` | | smoke |
| `pacman` | pacman | `start` | | smoke |
| `as-and-bs` | As-and-Bs | `start` | | smoke |
| `sierpinsky-11` | sierpinsky | `start11` | linear | smoke |
| `binary-tree-dfs12` | generate-binary-tree | `start` | dfs(12) | smoke |
| `append-4-list-8` | append | `append-4-list-8` | | quick |
| `append-4-list-8-equality` | append | `append-4-list-8` | `collapse=equality` | quick |
| `mark-unmark` | Mark-Unmark | `start` | | quick |
| `car-platooning-05` | car-platooning | `start-05` | | quick |
| `binary-tree-dfs-unstored` | generate-binary-tree | `start` | unstored(8) | smoke |
| `mark-unmark-18` | Mark-Unmark | `tree-18` | | quick |
| `mark-unmark-21` | Mark-Unmark | `tree-21` | | quick |
| `as-and-bs-4-3` | As-and-Bs | `start-4-3` | | quick |
| `inheritance-12` | inheritance | `start-12` | | quick |
| `append-4-list-10` | append | `append-4-list-10` | | long |
| `pacman-four-ghosts` | pacman | `start_four_ghosts` | | long |
| `leader-election-8` | leader-election | `ring-8` | | smoke |
| `leader-election-14` | leader-election | `ring-14` | | quick |
| `leader-election-14-injective` | leader-election | `ring-14` | `matchInjective=true` | quick |
| `leader-election-16` | leader-election | `ring-16` | | quick |
| `leader-election-18` | leader-election | `ring-18` | | long |
| `count-10000` | attribute-count-to-n | `bound-10000` | | smoke |
| `count-100000` | attribute-count-to-n | `bound-100000` | | quick |
| `count-300000` | attribute-count-to-n | `bound-300000` | | quick |
| `fib-15` | fibonacci | `fib-15` | | smoke |
| `fib-22` | fibonacci | `fib-22` | | quick |
| `fib-function-15` | fibonacci | `fib-15` | program `fibonacci-function` | smoke |
| `fib-function-22` | fibonacci | `fib-22` | program `fibonacci-function` | quick |
| `count-100000-big` | attribute-count-to-n | `bound-100000` | `algebra=big` | quick |
| `count-300000-big` | attribute-count-to-n | `bound-300000` | `algebra=big` | quick |
| `as-and-bs-equality` | As-and-Bs | `start` | `collapse=equality` | quick |
| `sierpinsky-12` | sierpinsky | `start12` | linear | quick |
| `car-platooning-06` | car-platooning | `start-06` | | long |
| `binary-tree-dfs-unstored-10` | generate-binary-tree | `start` | unstored(10) | long |
| `mark-unmark-22` | Mark-Unmark | `tree-22` | | long |
| `count-600000` | attribute-count-to-n | `bound-600000` | | long |
| `hub-star-300-3` | hub | `star-300-3` | program `run` | smoke |
| `hub-chain-1000-1` | hub | `chain-1000-1` | program `chain` | quick |
| `hub-chain-200-2` | hub | `chain-200-2` | program `chain` | quick |
| `hub-field-hop` | hub | `field-2-100-2500` | program `hop`, unstored(100000) | quick |
| `hub-field-jump` | hub | `field-2-100-2500` | program `jump`, unstored(200000) | quick |
| `hub-ring-1000-unstored` | hub | `ring-1000-1` | program `chain`, unstored(200000) | quick |
| `hub-ring-1000-counted` | hub | `ring-1000-1` | program `counted`, unstored(200000) | quick |
| `hub-wander-alap-20` | hub | `chain-20-2` | program `wander-alap` | smoke |
| `hub-wander-100` | hub | `chain-100-2` | program `wander` | quick |
| `hub-wander-200` | hub | `chain-200-2` | program `wander` | quick |
| `hub-wander-alap-60` | hub | `chain-60-2` | program `wander-alap` | quick |
| `petrinet-pipe-8-8` | petrinet | `pipe-8-8` | | quick |
| `petrinet-pipe-9-9` | petrinet | `pipe-9-9` | | quick |
| `petrinet-join-100` | petrinet | `join-100` | unstored(20000) | quick |
| `petrinet-join-1000` | petrinet | `join-1000` | unstored(2000) | quick |
| `petrinet-pipe-11-11` | petrinet | `pipe-11-11` | | long |
| `pump-8-4` | parallel-pump | `pump-8-4` | | smoke |
| `pump-12-6-dpo` | parallel-pump | `pump-12-6` | | quick |
| `pump-12-6-spo` | parallel-pump | `pump-12-6` | `semantics=SPO-multi` | quick |
| `mergers-6` | mergers | `ring-6` | `semantics=SPO-multi` | smoke |
| `mergers-9-simple` | mergers | `ring-9` | | quick |
| `mergers-9-injective` | mergers | `ring-9` | `matchInjective=true` | quick |
| `mergers-9-multi` | mergers | `ring-9` | `semantics=SPO-multi` | quick |
| `mergers-10-multi` | mergers | `ring-10` | `semantics=SPO-multi` | quick |
| `mergers-11-dpo` | mergers | `ring-11` | `semantics=DPO` | quick |
| `pump-16-8-dpo` | parallel-pump | `pump-16-8` | | long |

## The harness

`test/performance/ExplorationBenchmark` (built 2026-09-20) runs the rows of the grammar
set: grammar files loaded once per row, a fresh grammar, `GTS` and `Exploration` per run,
`play()` on a watchdog-timed thread, warm-ups discarded, median/min/max wall time over the
measured runs. The pinned state and transition counts are asserted on every measured
run, so the harness doubles as a regression check. There is no JMH dependency; the
plain runner reuses the `ExplorationTest` plumbing.

### Entry points and properties

- `main`, with row names as arguments, and the Eclipse launch `GROOVE - exploration
  benchmark` (`-da -Xmx4g -XX:+UseParallelGC`). Without names, the tier property selects
  the rows.
- `@Test smoke()`: every row of the `SMOKE` tier once, no warm-up; runs in the full suite.
- `@Test benchmark()`: inert unless `-Dgroove.bench.run=<names|true>`; the Maven route,
  `mvn -q test "-Dexcluded.test.groups=" -Dtest=ExplorationBenchmark
  -DenableAssertions=false "-Dgroove.bench.run=true" > bench.log 2>&1`. Surefire honours
  `enableAssertions=false` (the header line says `Assertions: disabled`); the pom's
  `argLine` is untouched.

System properties: `groove.bench.warmups` (default 2), `groove.bench.runs` (default 5;
the baselines use 3 or 2), `groove.bench.timeout` (seconds per run, default 300),
`groove.bench.tier=quick|long|all` (default `quick`, which is everything but `LONG`),
`groove.bench.csv=<file>` (rows appended in CSV form as well), `groove.bench.run`
(the JUnit route's selector, above) and `groove.bench.randomAccess=true` (since
2026-09-23: puts every GTS record into random-access mode before the start state
materialises, as `SimulatorModel.resetGTS` does, so that each state graph is a fresh copy
(`CopyTarget`) instead of the parent's sets handed down (`SwingTarget`); the header line
`Record:` names the mode. In that mode the harness materialises the start state itself
before `newExploration`, as the GUI does after its reset, since the exploration would
otherwise re-apply the per-GTS features to a GTS that already has a record).

### What a run measures

A row is a `Config` record: `name`, `grammar` (directory under `junit/performance`),
`startGraph` and `controlProgram` (null for the grammar's defaults), `properties`
(grammar property overrides as space-separated `key=value` pairs like the `Generator`'s
`-D` option, applied to a copy of the grammar's own properties through
`GrammarModel.setProperties`, so one directory serves the DPO, SPO-multi and SPO-simple
rows), `exploreConfig` (in `ExploreConfig` text form, `""` for the default breadth-first
full exploration), the expected *discovered* state and transition counts (`-1` for
unknown, which is how a new row is calibrated) and the `Tier`.

The columns of the table: `states` and `trans`, the stored counts; `disc.st` and
`disc.tr`, the discovered ones, counted through a `GTSListener` registered before the
start state materialises (under `persistence=none` the GTS keeps only the written-back
trace, so its own counts describe the storage policy, not the work; for persistent rows
the two coincide); `med ms`, `min ms`, `max ms`;
`states/s` and `trans/s`; `match`, `iso`, `cert` (the certification part of `iso`),
`gen` (state generation after matching) and `rep` (the `Reporter`'s self-time), all in
ms, as deltas of the static `Reporter` counters that `StatisticsReporter` also prints at
`Generator -v 3` (they accumulate over the JVM's lifetime and are never reset); `confl`,
the confluent-diamond count of 2.1; `allocMB`, the per-thread allocated bytes of the
exploring thread; `retMB`, the retained heap after two GCs with the GTS still referenced;
`fNodes` and `fEdges`, the host factory's node and edge counts, which are the elements it
ever made, not the final graph's (the signal for 3.3 and for value-node growth).
Non-timing columns come from the median run.

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

The harness loads the `SystemStore` once per row and builds a fresh `new
GrammarModel(store)` for every run, which `ExploreType.newGTS` compiles under the
type's overrides (the algebra family, gh #923), outside the timed region.
`GrammarModel.toGrammar()` caches its `Grammar`, and the `RuleModel`s cache their
`Rule`s for the model's lifetime, so a fresh model is the only unit that isolates runs.
Consequence: search plans, built lazily on first match, are built inside the timed region
on every run; milliseconds against seconds, equal for all runs, accepted.

The harness found finding 3.11: every rule application was retained for the lifetime of
the `Grammar` through `Factory`'s dependency tracking, so retention grew across runs. It
is gh #919, fixed 2026-09-21; the write-up is `claude/factory-user-leak.md`. Since then,
with a fresh grammar per run and the fix together, the retained heap of a run consists of
the GTS plus the softly reachable state caches (3.6).

### Tiers and run shapes

`Tier` is three-valued: `SMOKE` within `QUICK` within everything, `LONG` apart.
`smoke()` runs the `SMOKE` rows, a plain `main` run the quick tier (smoke included), and
`-Dgroove.bench.tier=long` (or `all`) the rest, names given as arguments overriding the
tier. Quick rows run at `-Xmx4g`; long rows are sized for two to five minutes at
`-Xmx8g`.

Two run shapes, for two purposes:

- **The table run**: the whole quick tier in one JVM in table order, two warm-ups and
  three measured runs, the launch flags. It yields pinned counts, the breakdown columns
  and the order of magnitude of every row; it cannot decide whether a change of under
  about 10 % helped (see the quick baseline).
- **One JVM per row**: for decisions and for the long tier. A fix is measured on the rows
  it targets, before and after alternating per row (the A/B shape of the quick
  baseline), and the table is re-run only to refresh the breakdown. The long tier runs
  this way with one warm-up and two measured runs, which also lets a row that does not
  fit fail alone:

```
for c in $(names); do java -da -Xmx8g -XX:+UseParallelGC \
  --add-modules=java.management,jdk.management -Dgroove.bench.warmups=1 \
  -Dgroove.bench.runs=2 -Dgroove.bench.timeout=1200 -cp "<cp>" \
  nl.utwente.groove.test.performance.ExplorationBenchmark $c; done
```

Calibration of a new row uses the same loop: `-1` counts, no warm-up, one run.

Profiling a row: a JFR profile of a single run of one row (no warm-up) is how 3.12 was
located; the search plans of a row are printed by flipping the compile-time constant
`PlanSearchEngine.PRINT`, as for `hub-field-hop`. Once 1.1 lands (the `Reporter` gated on
a property), each measurement wants two runs, with the property on for the breakdown and
off for the headline number.

### Calibration facts

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
  configuration expresses that mode.
- **`persistence=none` only flips the GTS storing switch**: `setPersistent` leaves the
  record's collapse flag on, so freezing stays enabled (2.6 and 2.7 apply, 2.5 does not);
  under the linear strategies the flag is off and 2.5 applies.
- **Depth-first rejects a node bound** (`ExploreTypeConverter:168`); the only DFS-compatible
  bound is `next=newest cost=uniform bound=cost:N`. `bound=size:` is unsupported. The
  linear traversal admits no depth bound at all (`Traversal.isSearch`).
- **Unstored runs need deterministic systems with a cost bound.** `bound=cost:N`
  terminates a run under `persistence=none`; on a grammar with branching it explores the
  full tree unfolding, on a system with one successor per state a single path of N
  steps.
- **`retMB` includes the soft caches** of 3.6 and is unstable in the long tier; a row
  retaining over about 2 GB at `-Xmx4g` measures the collector (both under "Runs and
  outcomes"). A row that does not fit the heap does not reliably hit the timeout: under
  collector thrash the watchdog thread is starved too.
- **Run order and machine load.** Row position in the table run was checked and found
  innocent (`mark-unmark` measured 2 332 ms in its tier position against 2 350 on a
  different day), with one small unexplained exception (`fib-15`, see "Runs and
  outcomes"). Machine load is not innocent: a table run during an Eclipse rebuild on the
  same machine made three rows 11 to 21 % slower, which looked like a regression. Run on
  a quiet machine, and confirm any apparent change one JVM per row.

## Runs and outcomes

### Quick-tier baseline (2026-09-23)

All 53 smoke and quick rows. Desktop, JDK 25.0.4.1, the launch flags
`-da -Xmx4g -XX:+UseParallelGC`, two warm-ups and three measured runs, all rows in one
JVM in table order, fresh grammar per run, at `733a78d5c` (after the gh #925 merge, the
wander recalibration and the removal of `binary-tree-dfs-unstored-9`), on a quiet
machine, 35 minutes:

```
config                  states    trans  disc.st   disc.tr   med ms   min ms   max ms  states/s   trans/s   match     iso    cert     gen    rep   confl  allocMB    retMB  fNodes   fEdges
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
inheritance                756     5374      756      5374     45.4     41.7     46.3     16663    118445       2       5       3      24      0       0     44.3      2.8       7       10
pacman                     256     1536      256      1536     30.9     29.6     40.8      8285     49712       6      18       8      20      0       0     38.0      1.2      20      228
as-and-bs                 8240    44774     8240     44774    214.4    194.9    226.9     38427    208805      37      77      55     139      0       0    474.1     34.0       6       27
sierpinsky-11               12       11       12        11    802.3    761.8    835.2        15        14      74       0       0     649      0       0    918.9    418.2  265734   841476
binary-tree-dfs12         4012    22188     4012     22188    557.6    557.5    572.8      7195     39791       4     458     353     544      0       0    561.6     16.5     239      596
append-4-list-8          31104   114008    31104    114008   1670.5   1661.5   1716.4     18620     68248     381     599     568    1183      3       0   2975.6    228.7      67      293
append-4-list-8-equality   73792   268912    73792    268912   3419.1   3390.2   3441.9     21582     78650     894       0       0    2248      2       0   5303.6    527.4      74      357
mark-unmark              24576   368640    24576    368640   2284.7   2272.7   2302.7     10757    161354     447     751     679    1645      4       0   5596.1    143.0      15       30
car-platooning-05       110366   369601   110366    369601   3591.0   3586.3   3664.0     30734    102925    1156       0       0    1919      9       0   7508.4    539.0       5      215
binary-tree-dfs-unstored       9        8   409114    409113   1327.7   1314.6   1331.0    308143    308142     103       0       0     932      4       0   2606.8   1041.5    1023     2556
mark-unmark-18           48384   870912    48384    870912  11461.8  11361.6  11689.0      4221     75984    1207    7380    5692    9702     11       0  15741.5    367.4      18       36
mark-unmark-21          169344  3556224   169344   3556224  62483.9  61929.6  62583.1      2710     56914    5411   44358   33888   54714     79       0  74569.1   1260.1      21       42
as-and-bs-4-3           131505   947824   131505    947824   5278.1   5169.5   5331.2     24915    179576     870    2118    1260    3724     10       0  10936.9    568.9       7       35
inheritance-12          297212  4317133   297212   4317133  21369.3  21209.7  21393.5     13908    202025     706    5113    4144   18329     53       0  36920.9   1736.1      12       21
leader-election-8          820     3405      820      3405    288.2    277.1    365.0      2845     11813     108      26      26     131      1       0     87.5      4.5      39      196
leader-election-14       49620   386295    49620    386295   5732.9   5729.2   5735.7      8655     67382    1102    1614    1614    4237      4       0  11229.0    394.2      63      375
leader-election-14-injective   49620   386295    49620    386295   6450.6   6399.4   6481.8      7692     59885    1321    1631    1631    4665      8       0  11434.9    382.2      63      375
leader-election-16      197404  1772291   197404   1772291  30895.5  30351.7  31904.7      6389     57364    4888    8631    8631   24036     26       0  56920.8   1596.3      71      414
count-10000              10001    25001    10001     25001    652.8    628.0    657.5     15320     38297     280      29      29     454      1       0    725.1     79.7   10008    10005
count-100000            100001   250001   100001    250001   4008.8   3980.9   4572.5     24946     62364     358     133     133    3668      2       0  41563.6    836.3  100008   100005
count-300000            300001   750001   300001    750001  36970.9  36931.3  37290.7      8115     20286    1582     469     449   35532     10       0 354021.9   2641.5  300008   300005
fib-15                       3        2     4934      4934    469.5    440.5    485.5     10508     10508      23      15      15     305      0       0    132.1     39.2      27        2
fib-22                       3        2   143284    143284   2076.1   2042.0   2229.5     69015     69015      43      28      28    1199      2       0   3767.4   1182.3      40        2
fib-function-15           4934     4933     4934      4933     49.1     41.8     52.9    100509    100489       1       1       1      32      1       0    119.3     38.6      27        2
fib-function-22         143284   143283   143284    143283   1978.9   1914.9   2050.5     72406     72405      45      39      39    1073      3       0   3643.4   1165.6      40        2
count-100000-big        100001   250001   100001    250001   5446.5   5380.0   5519.0     18361     45901     602     150     149    4948      2       0  41647.7    840.9  100008   100005
count-300000-big        300001   750001   300001    750001  35258.2  34860.9  35412.7      8509     21272    1675     516     495   33807     12       0 354283.0   2655.2  300008   300005
as-and-bs-equality      262144  1413120   262144   1413120   8688.9   8379.5   8724.5     30170    162636    1229       0       0    6206      7       0  16513.2   1171.4       6       27
sierpinsky-12               13       12       13        12   2688.2   2629.7   2738.3         5         4     338       0       0    2137      0       0   2623.4   1233.9  797176  2524375
hub-star-300-3               1      891        1       891   2333.4   2163.4   2481.3         0       382       2    2242    1663    2323      0       0    374.0      1.3     303     1200
hub-chain-1000-1          1000      999     1000       999   7365.8   7078.0   7442.3       136       136      10    7224    7224    7333      0       0    317.4      4.6    1003     3005
hub-chain-200-2          19900    39402    19900     39402  10912.4  10813.9  10947.1      1824      3611      63   10180   10180   10774      0       0   2865.9     80.8     203      606
hub-field-hop           100001   100000   100002    100001   6404.1   6381.5   6640.2     15615     15615    5960       0       0     237      0       0  13480.3    262.4    5405     5914
hub-field-jump          200001   200000   200002    200001   7517.1   7513.8   7566.6     26606     26606    6594       0       0     501      0       0   1668.1    521.8    5404     5816
hub-ring-1000-unstored  200001   200000   200002    200001   1307.9   1199.0   1316.1    152924    152923     185       0       0     706      0       0   1687.9    521.2    1003     3007
hub-ring-1000-counted   200001   200000   200002    200001   1403.7   1388.4   1431.1    142477    142477     304       0       0     795      1       0   2742.4   1066.5  201004   402009
hub-wander-alap-20         191    65170      191     66197     40.1     34.1     49.1      4764   1651226       1       4       4      22      0       0     62.2     13.3      23       85
hub-wander-100            4951     4950     4951     24355   1412.8   1407.1   1428.9      3504     17239       5    1178    1178    1378      0       0    965.7     19.9     103      405
hub-wander-200           19901    19900    19901     98705  20546.7  20497.3  20614.5       969      4804      31   18727   18727   20337      1       0   7550.3     88.3     203      805
hub-wander-alap-60        1771  6058710     1771   6068977   4168.8   4116.4   4321.8       425   1455818      17     315     315    2350      2       0   4604.1   1184.5      63      245
petrinet-pipe-8-8        12870    91520    12870     91520   2952.4   2949.5   2973.2      4359     30999     355    1932     999    2367      2       0   4034.2     85.1      89      177
petrinet-pipe-9-9        48620   393822    48620    393822  12033.6  11977.3  12223.4      4040     32727    1299    8461    4835    9877      7       0  18061.1    340.3     109      217
petrinet-join-100        20001    20000    20002     20001   8049.5   7884.9   8192.5      2485      2485    4379       0       0    1784     15       0  23027.9    483.2     502     1202
petrinet-join-1000        2001     2000     2002      2001   9201.8   9091.1   9499.1       218       217    4827       0       0    2117     27       0  22580.1    418.4    5002    12002
pump-8-4                  2143    38891     2143     38891    851.9    200.3    865.4      2516     45655      24     344     286     747      2       0    333.3     13.5       5       82
pump-12-6-dpo            36894  1231379    36894   1231379   6357.8   6342.2   6408.5      5803    193680     153    2704    1941    5588     14       0  12100.5    344.8       7      180
pump-12-6-spo            37026  1267481    37026   1267481   6469.8   6424.1   6500.0      5723    195908     135    2791    1984    5755     14       0  12434.2    351.2       7      180
mergers-6                  202      681      202       681     42.4     31.8     50.9      4763     16056       4       6       4      27      1       0      9.2      0.8       6       83
mergers-9-simple         25145   255596    25145    255596   1644.7   1638.1   1648.0     15288    155404      98     414     294    1373      3       0   2890.7    181.4       9      245
mergers-9-injective      12775    60718    12775     60718    499.2    493.3    530.4     25591    121632      62     120      77     376      0       0    801.9     81.6       9      236
mergers-9-multi          26217   259850    26217    259850   1786.2   1764.4   1805.2     14677    145473     116     539     372    1489      3       0   3073.3    189.8       9      314
mergers-10-multi        222508  3320992   222508   3320992  27159.1  27091.8  27164.4      8193    122279    1052    7776    5585   23611     51       0  40402.5   1787.8      10      520
mergers-11-dpo           70065   735888    70065    735888   7569.2   7399.4   7617.6      9257     97221     258    1560    1560    6410     15       0  11019.7    558.7      11      208
```

Against the previous run of the table (at `dd4c851e0`, 48 rows), the four wander rows
and the two injective rows are new, so the gh #925 fix shows only against the wander
calibration above (`hub-wander-100` 1.4 s, `hub-wander-200` 20.5 s warm). Outside ±10 %
are `count-10000` (+34 %), `binary-tree-dfs-unstored` and `fib-15` (+21 %),
`sierpinsky-11` and `car-platooning-05` (+18 %), `fib-function-15` (+16 %), `inheritance`
(+14 %), `pump-8-4` (+12 %), `fib-function-22` (+11 %), `pump-12-6-spo` (−11 %) and
`mergers-6` (−51 %): the sub-second ones are the known noise, and the others measured
alone (one JVM, same shape) take 3.17 s (`car-platooning-05`), 0.87 s
(`binary-tree-dfs-unstored`) and 1.65 s (`fib-function-22`), at or below their earlier
figures, while `pump-12-6-spo` returns to the level of its DPO twin; so no row outside
the recipe family moved with the fix, and every other row is within 10 %.

Every count asserted. What the table is good for: pinned counts, the breakdown columns,
and the order of magnitude of every row; not for deciding whether a change of under about
10 % helped, since two clean runs of the table a day apart differ by that much on
individual rows (two sub-second rows by about 30 %, and `petrinet-pipe-8-8` by 22 %,
cause unknown). A fix is measured one JVM per row on the rows it targets, in the A/B
shape used for the gh #924 merge: two warm-ups and three runs at `-Xmx4g`, the pre-merge tip `2546ab571`
against `fe5a2887e`, alternating per row: `mark-unmark` 2 204 against 2 205 ms,
`car-platooning-05` 3 411 against 3 409, `binary-tree-dfs-unstored` 1 033 against 962,
`append-4-list-8` 1 702 against 1 699, `fib-15` as a recipe 16 701 against 63,
`fib-function-15` 56 against 55. So gh #924 costs nothing on the non-recipe path and the
fixed recipe path is 265 times faster at `fib-15`, level with the function twin. (A first
run of this table, during an Eclipse rebuild on the same machine, had made three rows
look 11 to 21 % slower; the A/B is what showed it was load, not the merge.)

### Long-tier baseline (2026-09-22)

Desktop, one JVM per row, `-da -Xmx8g -XX:+UseParallelGC`, one warm-up and two measured
runs, fresh grammar per run, Oracle JDK 26.0.2.1 (the machine's default `java`; the
quick-tier baseline ran on 25.0.4.1, so the two tables are not to be compared across).
44 minutes in all. `count-600000` predates the `probe-odd` rule (with it: 131 s in a
single cold run, 1 500 001 transitions); `petrinet-pipe-11-11` and `pump-16-8-dpo` were
added to the tier afterwards and have calibration runs only (249 s and 169 s, see the
grammar set).

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

- **A quick-tier figure for `append-4-list-10` was collector-bound.** Alone at 8 GB it
  takes 76 s; inside a table run at 4 GB it took 206 s, with 2.3 GB retained and the soft
  caches on top. `pacman-four-ghosts` (1.9 GB retained) moved only from 143 to 135 s, so
  the cliff sits between those two retentions: a row that retains over about 2 GB at
  `-Xmx4g` measures the collector, not the exploration. Read the quick table's `retMB`
  column next to its times.
- **Isomorphism checking dominates where it runs**: 72 % of `mark-unmark-22`, 55 % of
  `pacman-four-ghosts`, 32 % of `append-4-list-10`, 27 % of `leader-election-18`, with
  certification about a third to all of it. `car-platooning-06` (the grammar switches
  isomorphism checking off) and the unstored tree (nothing collapses) spend it in `gen`
  and matching instead.
- **`count-600000` is `gen`**: 104 of 107 s, with 1.4 TB allocated; the counter's
  allocation (finding 3.13) lives in state generation, not in matching or the state set.
- **`retMB` is not a stable figure in this tier.** `car-platooning-06` retained 6.2 GB
  here against 1.3 GB in its calibration run, and `count-600000` 0.5 GB against 2.5 GB:
  the softly reachable caches of 3.6 survive or not depending on how hard the collector
  was pressed during the run. Compare `med ms`; treat `retMB` as a lower bound on the
  live set only.
- The spread is small: every `max ms` is within 6 % of `min ms` over the two measured
  runs, which is what the tier was for.

### Random-access (Simulator) mode (2026-09-23)

The quick tier once more under `-Dgroove.bench.randomAccess=true`, otherwise as the
swing-mode baseline above (JDK 25, `-Xmx4g`, two warm-ups, three runs, one JVM, at
`0cabf1217`; about 95 minutes; the two `collapse=equality` rows reran in a JVM of their
own after the harness ordering fix). No discovered count differs from swing mode. Ratios
of the median time, allocation and retained heap, random access over swing, for the rows
that moved (the full table takes 95 minutes to regenerate):

| row | time | alloc | retained | where |
|---|---|---|---|---|
| `hub-ring-1000-counted` | 33.4 | 16.0 | 2.4 | `match` 47 of 50 s |
| `hub-ring-1000-unstored` | 22.2 | 14.8 | 1.9 | `match` 26 of 28 s |
| `hub-field-jump` | 16.8 | 77.7 | 2.9 | `match` 126 of 127 s |
| `hub-field-hop` | 10.8 | 4.4 | 3.4 | `match` 69 of 70 s |
| `leader-election-16` | 3.9 | 1.7 | 0.6 | runs 75 to 130 s: collector at 4 GB |
| `petrinet-join-100`, `-1000` | 3.0, 2.8 | 1.4 | 0.3, 0.4 | `match` 16 s against 4.4 to 4.9 |
| `count-300000`, `-big` | 1.4, 1.3 | 1.0 | 1.1 | 2.9 GB retained of 3.6 |
| `sierpinsky-11`, `-12` | 1.25, 1.3 | 1.5 | 1.1 | `gen` |
| `binary-tree-dfs-unstored` | 1.27 | 1.1 | 1.1 | |
| `hub-chain-200-2`, `-1000-1` | 1.1, 1.0 | 1.4 | 5.2, 18.4 | retained per stored state |
| `leader-election-14` | 1.2 | 1.7 | 2.1 | |
| everything else | 0.9 to 1.1 | 1.1 to 1.7 | 1.0 to 1.9 | |

- **The unstored single-path rows over large graphs are 11 to 33 times slower, all of it
  in `match`.** Each step's graph is a fresh copy of 1 000 to 5 400 nodes, and the
  `match` column includes the lazy materialisation of that graph, so what it shows is
  finding 4.3.2 at full strength: the per-node edge sets rebuilt at graph size on every
  step, where swing mode hands them down with the delta. `petrinet-join` (300 to 3 000
  nodes) shows the same at 3 times. This is the mode every Simulator user explores in,
  and the demotion of 3.7 above holds for swing mode only.
- **Stored large-graph rows keep their time but retain 5 to 18 times as much**
  (`hub-chain-200-2` 81 to 423 MB, `hub-chain-1000-1` 4.6 to 85 MB): a stored state keeps
  its own sets. `sierpinsky` (graphs of up to 800 k elements on a linear path) is 25 to
  30 % slower with 55 % more allocation.
- **Rows near the heap limit fall off the collector cliff**: `leader-election-16` at 3.9
  times with 73 % more allocation, the 300 k counter rows at 1.3 to 1.4.
- **Everything else is within 10 % in time but allocates 20 to 70 % more** and retains up
  to twice as much (the append rows 1.8 to 1.9). The recipe rows are re-measured below
  after gh #925. The few ratios below 0.9 (`mergers-6`, `pump-8-4`,
  `as-and-bs-equality`) are sub-second rows with outliers in the swing table or the
  first-position effect of the two-row rerun, not gains.

**The recipe rows after gh #925** (2026-09-23, at `733a78d5c`): one JVM per row and
mode, the modes alternating per row, two warm-ups and three runs, `-Xmx4g`, JDK 25; the
swing median and the ratios random access over swing:

| row | swing ms | time | alloc | retained |
|---|---|---|---|---|
| `fib-15` | 78 | 0.84 | 1.07 | 0.93 |
| `fib-22` | 1 857 | 1.12 | 1.07 | 0.93 |
| `fib-function-15` | 70 | 0.80 | 1.07 | 0.92 |
| `fib-function-22` | 1 646 | 1.00 | 1.07 | 0.93 |
| `hub-wander-alap-20` | 42 | 0.99 | 1.08 | 1.04 |
| `hub-wander-100` | 1 508 | 1.03 | 1.40 | 3.7 |
| `hub-wander-alap-60` | 3 221 | 1.03 | 1.02 | 1.01 |
| `hub-wander-200` | 22 607 | 1.02 | 1.24 | 3.7 |

The recipe path is mode-neutral in time: every row over a second is within 3 % but
`fib-22` at 1.12, and the sub-100 ms rows are noise. The single-launch wander rows
allocate 24 to 40 % more and retain 3.7 times as much (18 to 67 MB at 100 leaves, 119 to
435 MB at 200), the stored-state sets of `hub-chain-200-2` again; the `alap` rows, whose
retention is their transitions, do not move.

### Outcomes across both tiers

- **Isomorphism and certification dominate the large stored rows.** Certification is the
  whole of the hub chain rows (98 % of `hub-chain-1000-1` and 93 % of `hub-chain-200-2`);
  `iso` is about 70 % of `mark-unmark-21` and `mark-unmark-22`, two thirds of the petrinet
  pipeline, 55 % of `pacman-four-ghosts`, 40 % of the pump rows, and a quarter to a third
  of the append and leader-election rows. This puts sections 2.3, 2.4, 4.4 and 5.5 to 5.8
  ahead of the matching items for the large-state-space rows.
- **`gen` dominates the counter and the fibonacci rows**: 91 to 97 % of the counter rows of
  100 000 states and more (104 of 107 s at `count-600000`; finding 3.13), and about 60 % of both fibonacci families at `fib-22`, where matching and
  isomorphism are under 100 ms; also 79 % of `sierpinsky`, the large-graph linear case.
- **Matching dominates the hub field and petrinet join rows**: 93 and 87 % of
  `hub-field-hop` and `hub-field-jump` (5.1, 5.2), and over half of the petrinet join rows
  (4.1.2).
- **The collector cliff.** A row retaining over about 2 GB at `-Xmx4g` measures the
  collector. `binary-tree-dfs-unstored-9` was the quick row affected (28.9 s in the
  previous quick-tier table against 14 s in its cold calibration at 8 GB, with 1.7 GB
  retained plus the soft caches of 4 M discovered states) and was dropped from the tier
  for it; depths 8 (smoke) and 10 (long) cover the family. The other quick rows above
  1 GB retained (`count-300000` and its BigInteger twin at 2.6 GB,
  `mergers-10-multi`, `inheritance-12`, `leader-election-16`, `as-and-bs-equality`,
  `fib-22`, `sierpinsky-12`) are within 15 % of their calibration at 8 GB, so for stored
  runs the cliff sits above 2.6 GB, and lower for unstored ones, whose live set is all
  soft.
- **Confluence is always zero** (`confl` on every row of both tables), as finding 2.1
  predicts.
- **Factory edge growth** (3.3): `car-platooning-05` mints 215 edges for a 5-node factory,
  `car-platooning-06` 306, `pacman` 228 and `pacman-four-ghosts` 441.
- **One order effect, unexplained**: `fib-15` measures 388 and 470 ms in the two table
  runs against 42 and 49 ms for `fib-function-15` two rows later and 44 to 78 ms in its
  own JVM, presumably JIT state left behind by the twenty preceding rows (the recipe
  branches of the state cache are first taken there); a 0.4 s row, noted, not pursued.

## Findings

The finding list of the review, unchanged in numbering: commits, issues and other
notes refer to the numbers.

### 1. Always-on instrumentation and uncontended locks

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

### 2. Dead or broken optimisations

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

### 3. Costs that scale with the wrong quantity

**3.1 Every certifier allocates an array sized by the global node counter.** High /
small / high (verified). `graph/iso/CertificateStrategy.java:45`:
`new NodeCertificate[graph.getFactory().getMaxNodeNr() + 1]`. `getMaxNodeNr()`
(`graph/ElementFactory.java:59,71`) is a monotone high-water mark over every node the
factory ever registered, so per-state cost is O(states × total nodes created) in
allocation and zero-filling, retained as a final field for the certifier's life. The
array is scratch (3.13 is this finding at its worst: quadratic on the counter rows),
used only by `putNodeCert`/`getNodeCert` from `initCertificates`
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
the hub grammar under "The performance grammar set"): in the exploration's swing mode
the store, once built, travels along the materialisation chain, so the walk runs once per
chain rather than once per erase, and a `let:` per move costs 4 µs on a 7 µs step with
nothing of it attributable to the store. Demoted to Low for exploration; the filter
remains a simplification worth making when the file is touched.

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
Related: 1.3 (the same `get()` also takes the global lock). *Fixed 2026-09-21* as gh #919
on its own branch (weak user sets plus plain fields in `RuleApplication`); the write-up is
`claude/factory-user-leak.md`.

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
*Fixed 2026-09-22* as gh #924 (branch `statecache-transient-closures`, merged into this
branch at `da54faa44`) by local propagation over direct predecessor edges; the figures
above are pre-fix, and the fibonacci recipe rows now cost the same as their function twins.

**3.13 The counter's generation time and allocation grow faster than its state count.**
Resolved (added and located 2026-09-24): it is 3.1. `attribute-count-to-n` explores a
single line of states, each step a `let:` that mints one value node, so the host factory's
node high-water mark grows by one per state and so does the `NodeCertificate` array every
certifier allocates. Predicted allocation `states² × 4` bytes: 40 GB at 100 000 (measured
41.5), 360 GB at 300 000 (measured 354). The JFR allocation profile puts 91 % (100 k) and
96 % (300 k) of all allocation in `CertificateStrategy.<init>`; the time is the zero-fill.
The execution samples are misleading: 70 % (100 k) and 81 % (300 k) land on
`CacheReference.incFrequency` line 255 (an amortised-constant `ArrayList.set`, reached
from `GTS$StateSet.getCode` through the new state's first `getCache`), which is the
zeroing loop of the inlined array allocation attributed to a neighbouring frame even
with `-XX:+DebugNonSafepoints`. Trust `allocation-by-site` over `hot-methods` when the
two disagree. None of the three readings (a) to (c) was needed: with the array replaced
by a lookup that does not grow with the factory (branch `certifier-node-table` off
master), the rows allocate a flat 33 to 34 KB per state and take about 19 µs per state,
one JVM per row, no warm-up (figures of the first, probe-table version):

| row | before (tier) | after | allocMB before | allocMB after |
|---|---|---|---|---|
| `count-100000` | 4.2 to 5.4 s | 2.0 s | 41 504 | 3 340 |
| `count-300000` | 34 s | 5.5 s | 353 671 | 10 116 |
| `count-600000` | 107 s | 11.2 s | about 1.4 TB | 20 727 |

`gen` remains 70 to 75 % of these rows, now linear. Of the two remedies 3.1 suggests,
the probe table sized by `nodeCount()` lost to master where certification dominates and
graphs are small (`leader-election-14`: certification 12 to 25 % slower over five A/B
pairs), since every lookup became a hash probe. The reusable scratch array won: direct
indexing by node number in a per-thread array shared by all certifiers, grown on demand,
its used entries cleared when `initCertificates` ends. A/B against master, alternating
builds: `leader-election-14` level over four pairs, `as-and-bs` and `mergers-9-multi`
level, `count-100000` 4.9 to 2.1 s, `count-300000` 28.0 to 4.9 s.

### 4. Allocation on the per-state and per-match path

#### 4.1 Matching

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

#### 4.2 Events, transformation, algebra, control

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

#### 4.3 LTS and state cache

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

#### 4.4 Isomorphism

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

#### 4.5 Exploration driver

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

### 5. Structural and algorithmic

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

### 6. Assertion-only costs

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

### 7. Checked and found fine

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

### Gates when implementing

- Anything touching `match`, `graph/iso`, `lts`, `transform` or `control/instance`:
  `determinism-check` skill and `grammar-smoke` skill (state and transition counts must
  be identical). Several suggestions (5.1, 5.2, 2.4, 5.7) change which candidate set is
  iterated or what certificate values are, so match discovery order and certificate
  collisions change; `MatchCollector.canonicalise` is supposed to neutralise the former,
  but this is exactly the area of the 2026-08 rule-anchor instability.
- `null-check` skill on every touched Java file.
- Before-and-after numbers from the throughput harness, and `Generator -v 3` to confirm
  "Confluent:" becomes non-zero after 2.1.

### Suggested order of attack

1. Build the throughput harness, so the rest can be measured. Done 2026-09-20.
2. Fix the `Factory` user leak (3.11): a genuine unbounded leak, found by the harness,
   which also caps how long any benchmark run can be. Done 2026-09-21 as gh #919.
3. The transient closures of `StateCache` (3.12), found by the fibonacci rows. Done
   2026-09-22 as gh #924; its complement gh #925 (recipe targets on cyclic regions,
   found by the wander rows) done 2026-09-23.
4. Finding 3.13, the counter's generation time: an investigation first, on the rows
   `count-100000` and `count-300000`; a fix on its own branch if the cause is
   algorithmic. Chosen ahead of the constant-factor items because a cost that grows
   with the run hits every grammar with many distinct data values. Done 2026-09-24:
   the cause is 3.1, fixed on branch `certifier-node-table`.
5. Finding 4.3.2, the per-node edge sets: the whole of the Simulator-mode cost on large
   graphs (11 to 33 times on the hub rows), but inherent to copying unless the sets
   become copy-on-write, so a design discussion before a commit.
6. Section 1 (always-on instrumentation and locks): two-line changes in the innermost
   loops, no design risk; a few per cent each.
7. Section 2 (dead or broken optimisations): the confluent-diamond key fix and the
   certifier reference; each restores an optimisation that exists but does not work.
8. Section 3 (costs scaling with the wrong quantity): the certifier array, the eager
   certificate map, the interning edge probe. Each is small and independently measurable.
9. Section 4 (allocation churn) as opportunity permits, largest expected payoff first:
   `Search` reuse, NAC context maps, algebra reflection, `Valuator` lambdas, match-set
   sizing.
10. Section 5 (structural) needs design discussions, not commits.

Every change is measured one JVM per row on the rows it targets (the A/B shape under
"Runs and outcomes"), in both materialisation modes where the row is mode-sensitive.
Any change to matching or certificates must pass `DeterminismTest` and the
`grammar-smoke` state counts (see the gates above).

## Coverage

Assessed 2026-09-22 after grammar-set item 6, updated 2026-09-24 after the gaps closed
on this branch. The set is fifteen grammars and 58 rows. Mapped against the parts of
state-space generation that run per state, per match or per transition, this is what the
rows exercise and what they do not.

Covered, with the row that carries the cost:

- **Matching**: plain search (all rows); NACs (`as-and-bs`, `append`, `car-platooning`,
  `pacman`, `leader-election`, `hub`); universal quantifiers, nested and with wide
  domains (`petrinet-join`, `petrinet-pipe`, `pacman`, `sierpinsky`); regular expressions
  (`mark-unmark`); attribute tests, `let:` and operations (`count`, `hub-ring-counted`,
  `fib`), error values (`probe-odd` on every counter row), BigInteger (`count-*-big`);
  rule priorities (`leader-election`, `car-platooning`); subtyping (`inheritance`,
  `hub`); parameters in and out (`fib`); injective matching, rejecting
  (`mergers-9-injective`) and not (`leader-election-14-injective`); the candidate
  over-approximation at a hub (`hub-field-hop`, `hub-field-jump`).
- **Transformation**: creation and deletion (all); merging (`mergers`); parallel edges
  under SPO-multi and DPO (`pump`, `mergers-*-multi`); the dangling check
  (`mergers-11-dpo`); value nodes and factory growth (`count`, `hub-ring-counted`);
  composite events of quantified rules (`petrinet`); large graphs (`sierpinsky`, `hub`).
- **LTS and state cache**: many transitions per state (`pump`, `pipe`, `pacman`); delta
  chains and reconstruction on a linear or depth-first path (`sierpinsky`,
  `binary-tree-dfs12`); unstored runs (`binary-tree-dfs-unstored`, the hub field and
  ring rows, `petrinet-join`); transient regions, deep and acyclic with one end per
  launch (`fib-*`, paired with the function rows) and cyclic with every state an end,
  launched once (`hub-wander-100`, `hub-wander-200`) or from every state
  (`hub-wander-alap-*`); the confluence check (`inheritance`, dead).
- **Isomorphism**: symmetry (`leader-election`, `hub-star`); diameter (`hub-chain`); edge
  bundles (`pump`); equality collapse (`*-equality`); checking off (`car-platooning`).
- **Driver and materialisation**: breadth-first, depth-first, linear, cost-bounded,
  unstored; the Generator's swing mode (every table) and the Simulator's random-access
  copy mode (the quick tier once more under `groove.bench.randomAccess`).

Not covered. None of these hides a cost known to be large; the first is the one
realistic shape missing, the rest wait for a finding that needs them:

1. **Many rules, few applicable.** The largest rule set is `car-platooning`'s twenty;
   real grammars have hundreds, and without control every rule is tried in every state
   (the per-rule fixed cost of `MatchCollector`: matcher lookup, plan, control frame
   schedule). A generated grammar with a few hundred non-matching rules around one
   working rule (the script can write `.gpr` files as it writes `.gst` files) would
   isolate that cost. Add it when a matching fix needs it.
2. **Cache collapse under memory pressure.** Reconstruction from the delta chain
   (findings 2.5 to 2.7) is measured only where the collector happens to clear soft
   caches, which the long tier's unstable `retMB` shows it does unpredictably. A harness
   option that clears the collectable caches every N states, as `DeterminismTest` does,
   would make the reconstruction cost a controlled column instead of noise (the 3.13
   investigation turned out not to need it).
3. **Per-state acceptors and rule-condition bounds.** `goal=condition|fires|graph`,
   `bound=upto|include|nodes|size|edges` and `count` each add a check per state; no row
   uses any of them.
4. **Randomised and restricted frontiers.** `next=random`, `successor=all-random`,
   `frontier=beam` and `heuristic` (the seed machinery of gh #897) have no row; their
   per-state cost is a shuffle or a pool operation, probably small, but unmeasured.
5. **LTL model checking.** The nested depth-first strategies of `explore/verify` build
   the product with the Büchi automaton during exploration and share nothing with the
   frontier strategies; unmeasured. CTL checking runs over a finished GTS and is outside
   the harness's scope.
6. **Regular-expression variants.** `regExpMatching=sloppy` (gh #900) on the
   `mark-unmark` rows is a one-line variant row.

Closed on this branch 2026-09-23: the Simulator's mode, by the `groove.bench.randomAccess`
switch and the random-access table under "Runs and outcomes" (the unstored large-graph
rows are 11 to 33 times slower there, all in `match`: 4.3.2 measured); cyclic and wide
transient regions, by the `wander` rows on the hub chain (which found gh #925, the recipe
targets missed under the closing strategies; fixed, merged, the family recalibrated and
`hub-wander-200` added as the quick row for the recipe traversal cost, 23 s against 10.7 s
for `hub-chain-200-2`, no size of the family in the long tier); injective matching, by
`mergers-9-injective` and `leader-election-14-injective` (the filter costs 1 to 4 % where
it rejects nothing).

Section 6 (assertion-only costs) is outside the harness by construction, since it runs
with assertions off; those costs show only under `-ea`, in `ExplorationTest` and in
Eclipse launches.
