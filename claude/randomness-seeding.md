# Design note: seedable randomness in exploration

*Status (2026-07-26): implemented on branch `explore-parametric-engine` (phase 5b
slice 1) — `util.Randomness` registry, seeded `RandomLinearStrategy` /
`RandomChooserInSequence` / `RandomOracle`, `Generator -seed`, `RandomnessTest`.
The two design decisions were resolved by Arend 2026-07-26: (1) streams are
re-derived per exploration with no run counter, so a fixed seed makes every
exploration identical (Simulator "explore again" repeats the identical trace);
(2) the seed is settable via the system property and a `-seed` Generator option.
Still open: storing the (generated) seed in the GTS info so saved LTS files
carry it — currently the generated seed is only logged to stdout.*

## The remaining nondeterministic sites

After the determinism program, the only remaining sources of irreproducible exploration
are intentional randomness, all currently unseeded:

- `explore/strategy/RandomLinearStrategy` and `ReteRandomLinearStrategy` — bare `Math.random()`;
- `explore/util/RandomChooserInSequence` — static unseeded `Random` (its comment already
  anticipates a seed);
- `transform/oracle/RandomOracle` — seedable via `RandomOracleFactory.instance(seed)`,
  but unseeded by default.

Out of reach: user operation classes calling `Math.random()` themselves; the pattern to
recommend is a fixed-seed `Random`, as in `algebra/UserOpsExample`.

## Two kinds of randomness

- **Policy randomness** (strategies, choosers) affects *which trace is sampled*, never the
  semantics. A seed here is a debugging/experiment convenience.
- **Semantic randomness** (the value oracle) affects the *content of the GTS*: different
  oracle values yield genuinely different transition systems. Its seed should therefore be
  expressible in the grammar properties (`valueOracle = random:<seed>`), so that a grammar
  can be made self-contained and reproducible.

## Design: master seed with derived per-purpose streams

Neither a single shared `Random` (consumers would be coupled through the *order of draws*:
one extra draw in a strategy would shift every subsequent oracle value) nor independent
user-managed seeds per consumer (poor UX for the common case). Instead:

- One user-facing **master seed**; a small registry (e.g. `util.Randomness`) derives a
  stream per named purpose (`EXPLORATION`, `ORACLE`, …) as `hash(masterSeed, purposeName)`.
  One number reproduces everything; consumers cannot perturb each other; per-purpose
  overrides remain possible where a natural configuration slot exists (the oracle).
- Seed resolution order: explicit setting (grammar property for the oracle) →
  system property `groove.randomSeed` → freshly generated.
- **An unrecorded seed is worthless**: when no seed is given, generate one, log it at
  exploration start, and store it in the GTS info so that saved LTS files carry the seed
  that produced them. (Precedent: SPIN's `-RS`, TLC's `-seed`, both printed in run reports.)

The system property is the right *default channel*: it reaches every entry point
(Generator, ModelChecker, Simulator, scripts) with zero plumbing. GROOVE has no other
system-property configuration, but a run-level cross-cutting knob is exactly what system
properties are for; the grammar-property and strategy-level mechanisms stay authoritative
where they exist.

## Open decisions — RESOLVED 2026-07-26

1. **Re-seed semantics**: streams are derived afresh whenever a consumer obtains its
   generator (at exploration preparation), as a pure function of master seed and
   purpose — no run counter. A fixed seed makes *every* exploration of the same
   grammar identical, composing with `DeterminismTest`; the Simulator's "explore
   again" repeats the identical trace. Users wanting variation set no seed.
2. **Discoverability**: both — the system property `groove.randomSeed` as the
   universal channel, plus the `-seed` Generator option (same channel, visible in
   `--help`). The oracle seed remains expressible in the grammar properties.

## Implementation notes (2026-07-26)

- `util.Randomness`: master seed resolved lazily (explicit `setMasterSeed` →
  system property → generated + logged); per-purpose seeds via a splitmix64 mix
  of the purpose name into the master seed. Each `newRandom` call returns a fresh
  generator drawing the same sequence — deterministic, though consumers created
  within one exploration for the same purpose draw *identical* sequences (each
  `RandomChooserInSequence` instance starts the same stream). If correlated
  choices ever matter statistically, a play-scoped draw counter can be mixed in.
- `next=random` is realised by `explore.engine.RandomPool` (uniform swap-remove
  take) under the converter keyword `random-frontier` — engine-only, no legacy
  strategy; combines only with `bound=none` (the other bounds require bfs/dfs,
  enforced by the existing converter guards).
- `successor=all-random` remains unsupported: it needs a hook in the inherited
  match-application order (`MatchCollector.canonicalise` territory), not a pool.
