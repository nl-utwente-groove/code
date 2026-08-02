# Retirement of the STS package and remote exploration

Status: retired (user decision, 2026-08-02). The `sts` package (author
Vincent de Bruijn, 2012 MSc work) converted an explored GTS into a Symbolic
Transition System — locations are host graphs generalised over data values,
switch relations carry textual guards/updates extracted from rule structure —
and serialised it as JSON. Its only user-reachable entry point was the
`remote` exploration strategy, which POSTed that JSON to a research server
that no longer exists. Nothing had touched the package's substance since
2012; every commit since was a sweeping refactor paying to keep it compiling.
Worse, the strategy was offered to every user in the exploration dialog
(`REMOTE` was not development-only) and crashed with a NullPointerException
on any grammar not using the point algebra.

## What was removed

One commit, containing only deletions (so that a plain `git revert`
resurrects everything for as long as it still applies):

- package `nl.utwente.groove.sts` (10 classes: `STS`, `RuleInspector`,
  `Location`, `SwitchRelation`, `Gate`, `Variable`, `LocationVariable`,
  `InteractionVariable`, `GeneralizedGraph`, `STSException`) and its
  `exports` line in `module-info.java`;
- `explore.strategy.SymbolicStrategy` (built the STS during exploration;
  only instantiable through `RemoteStrategy` — there was no `symbolic`
  strategy keyword) and `explore.strategy.RemoteStrategy` (the HTTP POST,
  half commented-out);
- `explore.StrategyValue.REMOTE` (keyword `remote`) and its template;
- `explore.encode.EncodedHostName` (only used by the `REMOTE` template) and
  `explore.encode.EncodedExplorationMode` (dead remnant of the same
  feature's on-the-fly/offline mode choice, referenced nowhere);
- the `remote:host` line in the `Generator` strategy help text, and the
  "remote" mention in the `ExploreTypeConverter` javadoc;
- tests: package `test.sts` (`STSTest`, `GateTest`, `LocationTest`,
  `SwitchRelationTest`, `VariableTest`), the `{"remote", "final"}` row of
  `ExploreTypeConverterTest.testInexpressibleLegacy`, and the fixture
  grammars `junit/rules/sts/{exception,guards,testCase,updates}.gps`.

Old command lines or saved configurations using the `remote` strategy
keyword now fail with the generic unknown-strategy error.

## How to revive

- The parent of the deletion commit is tagged **`sts-final`**: the last
  mainline tree containing the package.
- While the surroundings still fit, `git revert` of the deletion commit
  restores everything, including tests, fixtures and registrations.
- After drift, restore the sources wholesale
  (`git checkout sts-final -- src/main/java/nl/utwente/groove/sts
  src/test/java/nl/utwente/groove/test/sts junit/rules/sts` plus the
  strategy and encoder classes) and re-add the registrations listed above
  by hand.

## Defect ledger

Known problems at retirement time — a revival should fix these rather than
inherit them:

- **NPE on misconfiguration**: `SymbolicStrategy.prepare` checked for the
  point algebra, printed to `System.err` and returned early, leaving
  `strategy` and `sts` null; the next `computeNextState()` then threw.
  Since `remote` sat in the standard exploration dialog, any user selecting
  it on an ordinary grammar hit this.
- **Hand-rolled JSON**: `STS.toJSON()` built output by string
  concatenation, with no escaping of guard/update text and a corrupt result
  (chopped `[`) when the STS had no switch relations. Use a real JSON
  library.
- **Non-deterministic output**: locations, gates and variables were kept in
  plain `HashMap`/`HashSet` and iterated for serialisation, so the JSON
  ordering varied between runs — against the house determinism rules
  (though not on the `DeterminismTest` path).
- **Weak identities**: `SwitchRelation.getSwitchIdentifier` was a
  collision-prone string concatenation (2012 TODO: "replace with triple");
  `GeneralizedGraph.equals` blind-cast its argument and ran a full
  isomorphism check per `HashMap` lookup, duplicating (weakly) the
  iso-collapsing the GTS already performs — an STS should be derived from
  the collapsed GTS states instead.
- **Convention debt**: no `@NonNullByDefault` anywhere; tests were JUnit-3
  `TestCase` style running via the vintage engine, with a stale javadoc
  pointing at test classes deleted over a decade ago and an empty TODO where
  the JSON well-formedness check should have been.

If STS extraction becomes interesting again (e.g. as a model-based-testing
interface), the sound shape is an `io/external` `Exporter` producing STS
output from an explored GTS — deterministic, offline, no HTTP push — keeping
only the guard/update extraction logic of `STS`/`RuleInspector` as a
starting point.
