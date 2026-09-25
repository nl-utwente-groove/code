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

## Next

1. A/B timing (check first that no other session is timing on the machine), one JVM per row, alternating master and branch builds; harness and
   grammars borrowed from `exploration-performance` (not on this branch). Rows:
   `leader-election-14` (one round done: 5.6 s -> 2.6 s, 295,681 diamonds),
   `pacman-four-ghosts`, `petrinet-pipe-9-9`, `mergers-9-simple`, plus multigraph rows
   (`mergers-9-multi`, pump, default-semantics grammars).
2. Second commit for finding 2.2: store the key so `getKey()` stops allocating.
