# yFiles migration: plan and state of work

Tracked as gh #909. This document is the authoritative record of the initiative —
decisions, license constraints, completed work, residues, and next steps. It must be
kept self-contained: sessions on other machines have no access to session memory, so
everything needed to continue lives here or in `claude/jgraph-controller-split.md`
(the slice-by-slice record of the decoupling refactor).

**Status (2026-09-03): phase 1a (in-place decoupling) is COMPLETE; phase 0 (the
yFiles rendering spike) is DONE** (findings in `claude/yfiles-spike-findings.md`:
fidelity bar met, yFiles layouts beat Spring/Forest, 8240-state LTS lays out in
16 s organic / 44 s hierarchic; go given by Arend). **Phase 1b (facade definition)
is COMPLETE** (2026-09-05): slice 1 (the `GraphCanvas` interface family, `JGraph`
implementing it, controllers and pure clients retargeted, the architecture test with its
allowlist), slice 2 (neutral role cell interfaces, palette and hatch overlay,
attribute-map and loop-routing welds into the backend, editable labels as a dedicated
cell field), slice 3 (all `org.jgraph` listeners and JGraph property changes outside the
backend replaced by `GraphCanvasListener`, with a structured `CellChange` and a
`graphChanged` event), slice 4 (GROOVE's layouters work on the canvas interface, the
JGraph layout library is a backend contribution behind `getBackendLayouters()`) and
slice 5 (the export path works on the canvas: `CanvasExportable`, `toImage`/`paintGraph`,
an own `InterpolatingBezier` for TikZ). Slices 1–4 are merged into `yworks-migration`
(rebased on master); slice 5 is on branch `export-seam` awaiting review. The architecture
allowlist holds 11 files, all tagged for phase 2 (canvas construction, the display/tab
accessors) or phase 3 (`GraphEditorTab`). Design record and slicing in
`claude/view-facade.md`. **Phase 2 is IN PROGRESS**: design in
`claude/phase-2-model-and-ownership.md`; slice 1 (the view-model split: `CellStore`,
insertion machinery and role subclasses `AspectGraphViewModel`/`LTSGraphViewModel` in
`gui.view`, the JGraph models reduced to adapters) on branch `view-model-split`.

## Goal and motivation

Replace JGraph 5.13 (unmaintained for ~10 years) as GROOVE's graph rendering/editing
library with yFiles for Java (Swing), current major 4.0, behind an architecture in
which the visualization backend is swappable. Priorities, in order:
rendering/interaction quality, automatic layout quality, escaping unmaintained code.
The interactive editor is in scope eventually; read-only views migrate first.
Alternatives were surveyed and re-checked: the maintained Swing graph-viz space is
empty (JGraphX archived 2020, JUNG dead, GraphStream lacks editing, GEF is
SWT-bound); yFiles is the only serious option short of a webview/yFiles-for-HTML
rework.

## License constraints (yFiles SLA, signed; Academic Single Developer License)

- **Redistribution only with obfuscation (§2.1c).** The yFiles jar may ship inside
  GROOVE only obfuscated (yGuard-style). The plain jar can never be in the public
  repo or any Maven repository; the release pipeline gains an obfuscation leg for
  the yFiles edition; no reflection over yFiles classes anywhere.
- **Per-developer seats (§2.2.2), Single Developer confirmed.** Only Arend may
  access the jar; `git clone && mvn package` must keep working without it, so the
  yFiles backend becomes an optionally-compiled unit (multi-module restructure
  accepted). The SLA grants automated-build use only under a Project License, so CI
  cannot build the yFiles edition — those releases build locally, unless the
  license is upgraded to Academic Project (3 seats + build automation; suggested to
  yWorks, outcome open).
- **Academic restriction propagates (§2.4).** A yFiles-enabled GROOVE distribution
  is non-commercial-only, while GROOVE is Apache 2.0. Consequence: **dual
  distribution** — the standard release stays JGraph-based and unrestricted, a
  separate yFiles edition ships alongside, and **both backends stay genuinely
  maintained** (accepted; mitigated by capability tiering: optional yFiles-only
  features may degrade gracefully in the JGraph edition, but core
  view/edit/select/filter/export stays at parity).
- **The license is perpetual (§10.4a).** Non-renewal of the Subscription loses only
  upgrades/support. Still unknown: the delivered version/generation and
  Subscription status (check the license order, not the SLA).
- **No API re-exposure (§2.1d).** The yFiles backend package stays unexported in
  `module-info`; the facade remains a GROOVE-internal seam.
- The `yFiles-for-Java-Swing-Complete-3.6.0.1-Evaluation` bundle in
  `C:\Program Files\Java` carries 60-day evaluation terms and is NOT to be used;
  the spike uses Arend's licensed delivery.

## LLM usage ground rules (yWorks ruling, 2026-09-05 — binding for all sessions)

yWorks confirmed in writing that LLM-assisted development is permitted under these
conditions, which every Claude session working on this initiative must observe:

1. **Arend prompts directly.** No autonomous or scheduled tasks touch yFiles; all
   yFiles work happens in sessions Arend is driving.
2. **Permitted LLM inputs**: yFiles demo code, official documentation (including the
   bundled developer guide and docs.yworks.com), and GROOVE's own code. Nothing
   else from the distribution.
3. **No reverse engineering, ever**: no decompiling, no `javap` or class-file
   inspection of yFiles jars, no reflective API probing. If the documentation does
   not answer a question, stop and say so rather than probing the jar. Compiling
   against the jar and reading ordinary compiler errors is normal licensed use.
4. **Secrets stay with Arend**: he unpacks the password-protected distribution
   himself (anything Claude uses enters model context, so Claude never handles the
   password); the runtime license file is placed by Arend and referenced by path,
   its contents never read; jars never enter the repo and are never uploaded.

## Overall plan

- **Phase 0 — spike** (timeboxed, throwaway, standalone project outside the repo,
  depending on the locally built GROOVE artifact + the yFiles jar by local path).
  Rendering-fidelity test on real `junit/samples` grammars incl. exact `LayoutMap`
  positions; yFiles hierarchic/organic layout vs Spring/Forest; LTS scale test
  (thousands of states via `Generator`). Deliverables: side-by-side renders, a
  findings note in `claude/`, facade implications. Stop the initiative here if
  rendering does not clearly beat JGraph. *Fidelity bar (decided): node positions
  exact, cosmetics may differ.*
- **Phase 1 — decouple in place**: 1a (COMPLETE, see below) plus the facade
  definition itself, which deliberately waits for the spike so yFiles 4.x idioms
  (IGraph, styles, input modes) get a vote before interfaces freeze. The facade is
  derived from the consumer census, not from JGraph concepts; an architecture test
  (no `org.jgraph` import outside the backend package) seals the boundary.
- **Phase 2 — yFiles read-only views** (graph tabs, state display, LTS), yFiles
  layouts writing through the neutral `LayoutMap` (translating the frozen
  `LineStyle` codes and PERMILLE label positions), multi-module restructure
  (core + backend-jgraph + optional backend-yfiles), startup-time backend selection
  (decided: no runtime switching). This phase also inverts the ownership: panels
  construct a controller which owns a backend canvas, and the remaining delegation
  stubs and `getGraphView()` back-references are retargeted.
- **Phase 3 — editor**: input modes for the click-click edge gesture, in-place
  editing with label autocompletion, a GROOVE-owned undo/edit model (replacing the
  GraphUndoManager minor/major machinery), a GROOVE-owned clipboard format.
- **Phase 4 — export + Imager**: vector path via Java2D against the yFiles
  component; TikZ via the geometry seam; Imager rebuilt on the facade.

Key architectural facts from the original coupling analysis that remain relevant:
coupling is confined to `gui/**` (model/io/lts/explore/tests are clean); the
persisted `.gxl` layout format freezes the numeric `LineStyle` codes and
PERMILLE-unit label positions (GROOVE owns those numbers since the look-constants
branch; the JGraph backend documents its reliance on the coincidence); export
currently works by repainting the Swing component; the editor's undo classifies
JGraph edit internals; `gui/look` (`VisualKey`/`VisualMap`/`Look`) was already a
library-neutral visual model whose only JGraph bridge is `VisualAttributeMap`.

## Phase 1a: what was done (all merged or in the final merge stack)

Branch sequence, in merge order (each built on the previous; details and rationale
per slice in `claude/jgraph-controller-split.md`):

1. `look-constants` — GROOVE owns the frozen persisted constants; `gui.look`
   jgraph-free except the adapter.
2. `jgraph-actions` — cell-edit actions out of the JGraph classes into `gui.action`.
3. `jgraph-controller` (+rename) — `GraphViewController` extracted from `JGraph`
   (monomorphic state and lifecycle; strangler stubs).
4. `jgraph-controller-2` — controller hierarchy (Aspect/LTS), menus, mode chrome;
   callers retargeted instead of stubbed.
5. `jmodel-split` — `GraphViewModel` (displayed graph, layout map, element-to-cell
   index, layout sync) out of `JModel`.
6. `lts-view-semantics` — LTS display semantics (active state/transition, filter,
   traces, result queries) into `LTSGraphViewController`.
7. `controller-clients` — pure controller clients take the controller directly.
8. `jgraph-deport` — cell interfaces free of `org.jgraph` (no `GraphCell`
   inheritance, no ports); `JModel` records pending connections neutrally.
9. `looks-decoupling` — `isShow*` predicates into the controller hierarchy
   (+`CtrlGraphViewController`); `VisualValue` and all of `gui.look` are pure
   controller clients.
10. `viewcell-rename` — cell interfaces renamed `ViewCell`/`ViewVertex`/`ViewEdge`/
    `AspectViewCell`/`LTSViewCell` (+`AspectViewCellErrors`) and moved, with the
    controllers and `GraphViewModel`, to the new exported package
    **`nl.utwente.groove.gui.view`** — the neutral graph-view layer.
11. `cell-errors` — error API moved to `AspectViewCell` (was a latent CCE via an
    unchecked cast in `AJCell`); machinery slimmed via interface defaults.
    *Behavior note:* LTS error states now report `Severity.ERROR` from the
    un-shadowed `ViewCell` default (the error overlay may newly appear there;
    this restores documented intent).

**The end state**: `gui.view` holds the neutral layer (`GraphViewController` +
Aspect/LTS/Ctrl subclasses, `GraphViewModel`, the `ViewCell` interface family);
`gui.jgraph` holds only backend code; the component classes keep their J names
until retired. Ownership is still component→controller (the `JGraph` constructs
its controller); phase 2 inverts it.

## Decisions and conventions (binding)

- **Naming principle** (Arend, 2026-09-03): naming and comments outside `gui.jgraph`
  are free of JGraph terminology; the neutral vocabulary is "graph view". Direct
  *type* references to `JGraph`/`JModel`/backend cells remain until the facade
  replaces them.
- **Cell naming** (2026-09-05): short `ViewCell`/`ViewVertex`/`ViewEdge` rather than
  the family-consistent `GraphViewCell` — the `Graph` qualifier disambiguates
  nothing for cells (only graph views have cells) and these are the most frequently
  referenced GUI types; the controller/model keep the longer names where the
  qualifier is load-bearing.
- **Strangler pattern**: extractions leave delegating stubs where callers are many
  (slice 1, `JModel` index accessors), and retarget callers where they are few
  (everything since). Mixed clients (ZoomMenu, LayoutAction, ExportAction,
  SetLineStyleMenu, the cell-edit actions, the displays) convert to controller
  clients at the phase-2 ownership inversion, not before — passing the controller
  now would just add `getGraphView()` detours.

## Known residues (deliberate, for phase 2)

- J-flavored member names on the neutral API: `getJGraph`, `getJModel`,
  `setJModel`, `getJCell*`; plus j-cell/jgraph wording in `gui.view` javadoc.
  They change type or disappear at ownership inversion.
- ~~`JModel`'s insertion machinery physically still in `JModel`~~ — relocated into
  `GraphViewModel` in phase 2 slice 1 (2026-09-05), together with the `LTSJModel`
  orchestration that overrides it; the backend commits through `CellStore.insertCells`.
- Backend-named types still referenced outside the backend: `JGraphPanel`
  (gui.display), `JGraphMode` (gui.jgraph, used by displays), `JCellEditAction`
  family (gui.action). Renames folded into the phases that touch their seams.
- ~~`getColorMap`/`setLayoutable`/`refreshVisuals` stay on `JModel`~~ — on
  `GraphViewModel` since phase 2 slice 1; the z-ordered cells come from the
  `CellStore`.

## Practical notes

- **Null analysis**: the Maven build does not run it; use the `null-check` skill
  (per-file, or `-All` for wide changes — authoritative baseline documented in the
  skill). Every phase-1a branch was held to zero errors / zero new warnings.
- **Stale test classes**: after interface-level changes (package moves, signature
  changes), run `mvn clean test` — the incremental build has produced stale
  test-class `NoSuchMethodError`s twice.
- **Eclipse after merges**: refresh only, except `module-info`/pom changes
  (Maven → Update Project).
- The GUI test suite barely exercises the display layer; every branch's real
  verification is manual Simulator use (menus, editor gestures, LTS interaction,
  filtering, export).

## Immediate next steps

1. Arend: judge the spike output (`C:\Groove\yfiles-spike\out`, HTML indexes) and the
   interactive LTS viewer (`LtsScale ... -show`); decide go/no-go. Still open: the
   Subscription status and the Academic Project License upgrade question to yWorks.
   The delivered library is yFiles for Java (Swing) 3.6.0.1 (the plan's "4.0" was
   wrong); the license file at `C:\Groove\yfiles` is auto-loaded from the classpath
   root and shows no evaluation watermark.
2. Go given 2026-09-03. Phase 1b (branch `view-facade`): facade definition +
   architecture test, taking the view-computed items from the findings note (node
   sizing, loop routing) as first-class facade responsibilities and specifying the
   edge-label path model; then phase 2.
