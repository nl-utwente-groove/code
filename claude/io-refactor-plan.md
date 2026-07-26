# Plan: `io` package simplification and Ecore revival

*Drawn up 2026-07-26 (Claude session, approved by Arend). This document records the
plan and its rationale; each phase is a separate branch/PR.*

## Background and findings

The `io` package totals ~29.5k lines, of which ~18k (61%) is the
`io/conceptual` layer plus its glue — a 2012 MSc project (Harold Bruijntjes,
shipped in GROOVE 4.8.0) that imports/exports Ecore models through a
metamodel-neutral intermediate representation. It has had no functional
maintenance since ~2014 and is broken end-to-end today:

- Import and export require a `ResourceKind.CONFIG` XML resource (schema
  `ConfigSchema.xsd`) in the grammar; without one, `ModelPorter` silently
  imports/exports nothing. No grammar in the repository (including all of
  `junit/` and the samples) contains such a resource, the GUI cannot create
  one, and the only editor ever written (`io/conceptual/configuration/ConfigDialog`)
  is abstract with no concrete subclass — unreachable.
- The `GraphIO` route is independently broken: `FileType.computeGraphIO()`
  passes `GXL_META` where `ECORE_META`/`ECORE_MODEL` is expected (loads return
  null), and `ConceptualIO.doSaveGraph` always throws because
  `Exportable.graph()` carries no resource model.
- `ConfigModel` registers under `ResourceKind.GROOVY` instead of `CONFIG`.
- Zero tests or fixtures; diagnostic messages collected but never surfaced;
  debug prints on stdout; ~39 TODO/hack comments.

Contrary to earlier belief, the EMF *libraries* are not the blocker: GROOVE
pins `org.eclipse.emf.{ecore,common,ecore.xmi}` at 2.7.0 (2011) from Maven
Central, but EMF is actively maintained and current versions (2.41.x) are on
Central and run on plain Java. The code uses only ~20 stable core EMF types.
What *is* gone is the GUI for choosing encoding options — that must return in
Phase 3.

The non-conceptual rest of `io` (~11.5k lines) has its own problems: dead code
(`lang/gxl` ~3.2k lines reachable only through the never-registered
`GxlModelPorter`; `PdfDialog`; unused `FileType` constants), verbatim
copy-paste (`NativeResourcePorter` duplicating its own superclass), six
distinct ways to save a graph, Swing baked into the framework root
(`Porter.setSimulator`), and `FileType` acting as a service locator that
creates the package cycle `io → io.external.format → io.conceptual`.

## Decisions

- **Delete and rewrite** rather than revive the conceptual IR. The IR's
  abstraction only paid off with multiple backends, and the only second
  backend (GXL) is already unreachable. The encoding knowledge in
  `TypeToGroove`/`ConstraintToGroove` remains available in git history as
  reference for the rewrite.
- **`ResourceKind.CONFIG` is removed** — it existed purely for the Ecore
  configuration and no grammar uses it.
- **Priorities for the new Ecore support**: Ecore→grammar (import) first,
  grammar→Ecore (export) a close second.
- **Encoding options get a proper GUI dialog** in Phase 3 (integrated with
  `gui.dialog`), replacing the config-XML machinery.

## Phase 1 — remove dead weight (branch `worktree-io-cleanup`)

Delete, with no intended behaviour change beyond `.ecore`/`.xmi` disappearing
from the import/export dropdowns (they are non-functional today):

1. `src/main/java/nl/utwente/groove/io/conceptual/**`.
2. Glue: `io/external/ModelPorter`, `io/external/format/EcorePorter`,
   `io/external/format/GxlModelPorter`, `io/graph/ConceptualIO`.
3. `module-info.java`: the `exports nl.utwente.groove.io.conceptual*` lines and
   `requires org.eclipse.emf.*` lines.
4. `pom.xml`: the three `org.eclipse.emf` dependencies and the stale
   `<resource>` block pointing at `io/conceptual/configuration/ConfigSchema.xsd`.
5. `FileType`: constants `ECORE_META`, `ECORE_MODEL`, `GXL_META`, `GXL_MODEL`,
   `CONFIG`, `KTH`; the `ECORE_*` cases in `computeGraphIO()`.
6. `Exporters`/`Importers`: the `EcorePorter` registrations.
7. `ResourceKind.CONFIG` and everything conditional on it
   (`grammar/model/ConfigModel`, cases in `GrammarModel`, special cases in
   `Simulator`/`SimulatorModel`, `isEnableable()`/`hasDisplay()` simplification).
8. `src/main/resources/nl/utwente/groove/resource/ConfigSchema.xsd`.
9. Independent dead code: `io/external/util/PdfDialog`; the three overrides in
   `NativeResourcePorter` that duplicate `AbstractResourcePorter` verbatim;
   `GraphIO.canSave()` (never called); the unused, infinitely recursive
   `Exporters.getExporter(ExportKind, String)`.

Kept: the `groove-gxl` local library and the JAXB dependencies — the native
`GxlIO` uses them independently of the conceptual layer.

## Phase 2 — straighten the remaining framework

- Remove `setSimulator` from `Porter`; registries become headless
  (`Exporters`/`Importers` split into format registry + Swing driver next to
  `gui/action`), so `Generator`/`Imager` CLI paths no longer touch Swing.
- Single save path: `GraphIO` as the one marshalling backend, porters as thin
  adapters; merge `NativeGraphExporter`/`NativeResourcePorter`; remove
  `Imager`'s JGRAPH→RESOURCE→GRAPH fallback ladder.
- Move the `FileType → GraphIO` mapping out of the enum (breaks the cycle).
- Relocate `GrooveFileChooser`, `GrooveFileView`, `HTMLConverter` to `gui`
  (isolated final commit, rename-only).

## Phase 3 — fresh Ecore support

- Modern EMF (2.41.x, same three artifacts, Maven Central).
- Direct `EPackage ⇄ TypeGraph` and XMI `EObject ⇄ HostGraph` porter in the
  Phase-2 framework; no intermediate IR. Estimated 1.5–2k lines.
- Canonical default encoding (mine the old `TypeToGroove`/`Config` defaults
  from history); genuine options exposed through a new encoding-options
  dialog in `gui.dialog` at import/export time, and persisted as
  `GrammarProperties` keys / exporter options — not as a grammar resource.
- Errors via `FormatErrorSet`; fixtures (`.ecore` + `.xmi`) under `junit/`
  with round-trip tests in the regular suite.
- Scope v1: classes, attributes over EMF core datatypes, references with
  multiplicities, containment, opposites, enums. Out of scope: generics,
  `EOperation`s, annotations beyond documentation, cross-resource proxies.
- Import (Ecore→grammar) is implemented and tested first.

## Phase 4 (optional, deferred) — constraint rules

Rules checking instance-graph constraints that type graphs cannot express
natively (keyset/identity). Multiplicity and containment are now covered by
GROOVE's type graphs, so most of the old `ConstraintToGroove` is obsolete.
