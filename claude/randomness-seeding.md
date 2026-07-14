# Design note: seedable randomness in exploration

*Status (2026-07-14): designed, not implemented — step 3 of the determinism program
(the other steps are on master: deterministic hash codes, insertion-ordered collections,
`DeterminismTest`, and the determinism/performance conventions in `claude/CLAUDE.md`).
Two design decisions are still open; see the end of this note.*

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

## Open decisions

1. **Re-seed semantics**: derive the streams afresh at each `Exploration.play()`?
   Then a fixed seed makes *every* exploration of the same grammar identical, which
   composes with `DeterminismTest` (it could then also cover the random strategies).
   If so: should the Simulator's "explore again" repeat the identical trace, or mix a
   run counter into the derivation to sample a fresh one?
2. **Discoverability**: system property only, or additionally a `-seed` option on the
   Generator command line (same underlying channel, but visible in `--help`)?
