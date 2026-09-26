# Iso refinement: handoff state

Transient handoff note for branch `iso-refinement` (findings 2.4 and 2.3 of
`claude/exploration-performance.md`). Delete when the branch is merged.

## Goal

One commit per finding, measured A/B against master and recorded under the findings in
the note on branch `exploration-performance`, in the shape of the 2.1 and 3.2 entries.

## State as of 2026-09-26

Rebased onto master at 3624a9a02 (the `confluent-diamond` merge). One code commit left:

- 2.3 (`DeltaHostGraph`): only `hasCertifier` honours `strong`. The soft reference was
  built, measured and **rejected**: leader-election-16 2.9 times slower in both JVMs
  (soft certifiers evict the soft state caches; retained heap 1.1 GB to 0.56 GB),
  pacman-four-ghosts into the 300 s harness timeout twice. Recomputation after
  collection, counted with a probe, is 0 to 5 % of certifications. The weak reference
  carries a comment saying why it stays weak.
- 2.4 (`PartitionRefiner`): implemented, measured time-neutral (every row within 4 %,
  `cert` column unchanged) and **dropped at review**. The old final pass is not a wasted
  counting pass: its `store` argument was already false once the partition was discrete,
  and its edge terms hash adjacency under the discrete labelling into the graph
  certificate, which dropping it weakens. The uncounted first pass matches the
  `setSingular(0)` "duplicate" sentinel and guards against a premature stop. Full
  reasoning under 2.4 in the note.

Gates on the tip: full non-GUI suite incl. slow tests green on the soft variant (same
`hasCertifier` change, 930 tests, ExplorationTest counts unchanged); iso, exploration
and determinism classes plus null-check re-run on the minimal commit before the rebase.
The change is behaviour-neutral in strong mode, the mode of every isomorphism-collapsing
exploration.

A/B (2026-09-25): A = master 3624a9a02, B1 = A plus the since-dropped 2.4 commit,
B2 = B1 plus the soft 2.3; eight rows, one JVM per row and build, 1 warm-up + 3 runs
(pacman 1 run), two rounds with reversed build order; figures under 2.4 and 2.3 in the
note on `exploration-performance`. Logs were in the session scratchpad, not kept.

## Next

1. Hand over: detach this worktree's HEAD. Nothing else planned on this branch.
2. Then the note's next items: 2.5 to 2.7 (freezing and chain replay) on their own
   branch; judge them on retained heap as much as on time.

## Harness in the scratchpad (not on any branch)

`scratchpad/harness/`: `ExplorationBenchmark.java` and `junit/performance` copied from
`exploration-performance`; `bench.sh <build-dir> <label> <warmups> <runs> <rows>`
compiles the harness with JDK 25 `--release 21` against `<build-dir>/target/{classes,
test-classes}` plus `target/cp.txt` and runs with `-da -Xmx4g -XX:+UseParallelGC
-Dgroove.bench.tier=all`; `ab.sh` is the two-round loop. Builds are `git archive`
extracts built with `mvn -q test-compile dependency:build-classpath
-Dmdep.outputFile=target/cp.txt`. The PreToolUse guard refuses `bash <script>`, shell
`for` loops and heredoc-plus-git chains in tool calls; invoke scripts by path and commit
with `-F` from a file written beforehand.
