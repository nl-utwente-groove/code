---
name: determinism-check
description: Verify exploration determinism before handing over a branch that touches the exploration path (lts, match, transform, control/instance, grammar/host, graph, explore, or hash codes / collections used by any of these). Combines a code checklist with the determinism test classes.
---

# Determinism check

Successive explorations of the same rule system must behave identically, including under
garbage-collection-driven cache loss. This is easy to break silently: `mvn test` stays green
while a rare flake is planted. Run this check before handover whenever a branch touches the
exploration path — states, matches, events, control frames, graph elements, caches, or any
hash code or collection used by them.

## Code checklist

Review the touched code against these rules (full background: `claude/determinism-ferryman-flake.md`):

1. **No identity-based hashes** on the exploration path: hash codes must be number- or
   content-based, never `System.identityHashCode` (house pattern: `ANode.computeHashCode`).
   This includes the disguised forms `Enum.hashCode()` (use `ordinal()`; also reached through
   `EnumSet`/`EnumMap` hash codes and records with enum components) and `Class.hashCode()` /
   `identityHashCode(X.class)` (use `getClass().getName().hashCode()`). These are constant within
   a JVM — the tests below cannot detect them — but vary between runs, reordering every plain
   `HashSet`/`HashMap` keyed by such values; only code review catches them.
2. **Iterated collections are ordered**: insertion-ordered (`LinkedHashSet`/`LinkedHashMap`/
   `ArrayList`) or sorted — plain `HashSet`/`HashMap` only if the keys' hashes are deterministic.
   `TreeHashSet.iterator()` iterates in insertion order and code relies on that.
3. **No order-bearing decisions from iteration order.** Insertion order is *not* stable under
   cache collapse: state caches are softly referenced and may be cleared mid-exploration, after
   which graphs and transition data are reconstructed in a different insertion order. Anything
   that determines exploration order must be made canonical by explicit content-based
   comparison, as `MatchCollector.canonicalise` does for the match application order.
4. **Cached hash codes only on frozen state**: every field contributing to a cached hash must be
   immutable, or the object fixed (`Fixable`) before the hash is first requested.
5. **Cache reconstruction reproduces identities**: a state graph re-derived after cache loss must
   consist of the identical node and edge identities (in multigraph mode this requires the
   added-edge identities recorded in the transition — see `CacheReconstructionTest`'s javadoc).

## Test procedure

From the root of the checkout or worktree being verified:

```
mvn -q test "-Dtest=DeterminismTest,CacheReconstructionTest" > target/determinism.log 2>&1
```

Check the exit code; on failure, details are in `target/determinism.log` and
`target/surefire-reports`. The tests themselves take seconds; the Maven build dominates the
runtime.

A pass is meaningful evidence, not just a lucky run: `DeterminismTest` perturbs identity-hash
sequences and *deterministically induces* GC-sweep-like cache collapse at several points of the
exploration (every closure, plus quarter points), which is the mechanism behind the historical
1-in-14 ferryman flake. Still, for changes deep in the hot path, finish with a full-suite run
(`mvn -q test "-Dexcluded.test.groups=" > <log> 2>&1`) — the full suite's shared-JVM heap
history is what surfaced that flake originally.

## Notes

- Delegate the test runs to an Opus sub-agent (checklist review stays with the main session):
  have it run the commands and report only failures.
- If the change *intentionally* alters exploration order (e.g. a new canonical ordering),
  `ExplorationTest`'s expected counts are unaffected (states/transitions are order-independent),
  but LTS-order-sensitive fixtures may not be — run the full suite and explain any updated
  expectations in the commit body.
