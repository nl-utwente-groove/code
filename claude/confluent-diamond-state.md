# Confluent-diamond shortcut: handoff state

Transient handoff note for branch `confluent-diamond` (worktree
`.claude/worktrees/confluent-diamond`, off master 6af3e730c). Delete when the branch is
merged. Background: finding 2.1 of `claude/exploration-performance.md` (on branch
`exploration-performance`).

## Done

- `8f18343e7`: `AbstractGraphState.getOutStub` compares keys with `equals` (was `==`
  against fresh `MatchResult`s, so the shortcut never fired); guards in
  `MatchApplier.apply`: collapsing on, same added nodes on both sides, simple graphs only.
- `e94178b14`: `BasicEvent.conflicts` compares by content (erased edge vs. the other rule's simple
  creator edges, via node identity and label), for simple and multigraphs alike; the
  simple-graph exclusion in `MatchApplier` is dropped; explanatory comment incl. gh #926
  (edge provenance across diamond sides); fixture `junit/samples/parallel-creators.gps`
  and `ConfluentDiamondTest` (8 states, 16 transitions, identity check per transition;
  fails as it should with the content test off). `null-check` clean; non-GUI suite incl.
  slow tests passes; validated over 32,223 shortcuts (1,070 in multigraphs), no mismatch.

## Findings

- The multigraph mismatches were all erase-vs-create of content-equal edges
  (pump/drain, trim/pump); parallel creation commutes thanks to the gh #905 pool.
- A temporary validation (normal application alongside every shortcut) is the oracle;
  its code is not on the branch. Pitfall: snapshot each graph's node/edge sets before
  touching the next graph (aliased stores are handed on in swing mode), and pass test
  JVM flags via `-DcoverageAgent=...` or hard-wire them (a plain `-D` never reaches the
  surefire fork).

## A/B timing (2026-09-25)

Master 6af3e730c vs tip, one JVM per row and build, 1 warm-up + 3 runs, two rounds with
the order swapped; mean of the two medians; counts identical everywhere.
`leader-election-14` 5.69 -> 2.64 s (-54 %), `inheritance-12` 19.10 -> 14.33 s (-25 %),
`as-and-bs-4-3` 4.96 -> 3.72 s (-25 %), `mark-unmark-18` 10.72 -> 8.35 s (-22 %),
`mergers-9-multi` 1.61 -> 1.42 s (-11 %); within noise: `mergers-9-simple`, both
`pump-12-6` rows (shortcut on about 3 % of transitions), `petrinet-pipe-9-9` and
`pacman-four-ghosts` (single run), the last two with no diamond closed at all.
Why petrinet and pacman close none (probe of the diamond branch, 2026-09-25): pacman
never reaches it (turn flags alternate `moveGhosts` and `movePacman`, and `moveGhosts`
alternatives disable each other). `petrinet-pipe-8-8`: 85,078 candidates, 92 % refused
by `!parentOut.isSymmetry()` (interchangeable tokens); the 6,862 with composite events are
all pairwise conflict-free, but half have no explored sibling and half fail the
created-node guard (`smartRule` creates token nodes). So lifting the composite
restriction of `conflicts` (sound pairwise over sub-events, given the forall handling of
`RuleDependencies`; at most 9 sub-event pairs seen, a per-event erased-content index
would bound large quantifiers) would close none here; left as is.

## Next

1. Done: A/B timing (above). Copy the figures to finding 2.1 of
   `claude/exploration-performance.md` when that branch is next touched., one JVM per row, alternating master and branch builds; harness and
   grammars borrowed from `exploration-performance` (not on this branch). Rows:
   `leader-election-14` (one round done: 5.6 s -> 2.6 s, 295,681 diamonds),
   `pacman-four-ghosts`, `petrinet-pipe-9-9`, `mergers-9-simple`, plus multigraph rows
   (`mergers-9-multi`, pump, default-semantics grammars).
2. Finding 2.2 (store the key so `getKey()` stops allocating) investigated 2026-09-25 and
   dropped: a stored `MatchResult` costs +24 to +36 bytes per retained stub (4-6 % of the
   retained heap on `leader-election-16`, 11-15 % on `pacman-four-ghosts`), while
   regenerating costs 0.6 % of CPU samples on `leader-election-16` (JFR, 1 ms sampling),
   0.16 % for the key comparison in `getOutStub`, and 2.6 % of the allocation pressure.
   Record this under finding 2.2 in `claude/exploration-performance.md` when that branch
   is next touched.
