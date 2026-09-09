# yFiles migration: plan and state of work

Tracked as gh #909. This document is the authoritative record of the initiative —
decisions, license constraints, completed work, residues, and next steps. It must be
kept self-contained: sessions on other machines have no access to session memory, so
everything needed to continue lives here or in `claude/jgraph-controller-split.md`
(the slice-by-slice record of the decoupling refactor).

**Status (2026-09-08): phase 1a (in-place decoupling) is COMPLETE; phase 0 (the
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
(rebased on master); slice 5 is merged too. Design record and slicing in
`claude/view-facade.md`. **Phase 2 is IN PROGRESS**: design in
`claude/phase-2-model-and-ownership.md`; slice 1 (the view-model split: `CellStore`,
insertion machinery and role subclasses `AspectGraphViewModel`/`LTSGraphViewModel` in
`gui.view`, the JGraph models reduced to adapters) is merged into `yworks-migration`;
slice 2 (the ownership inversion: `GraphBackend` factory discovered by `ServiceLoader`,
controller-owned canvases, neutral `GraphPanel`,
`newViewModel`/`setViewModel` on the canvas) is merged; slice 3 (the optional yFiles unit
`yfiles/`, a separate Maven project on the `release/` pattern providing `YFilesBackend` as a
service, with `GraphBackend` ranking yFiles first when present) is merged, and the unit was
moved to the private repository `nl-utwente-groove/yfiles-lib`, where it is the root
project, on 2026-09-09, see "License constraints"; slice 4 (the
yFiles canvas) is under way: 4a (neutral cell classes in `gui.view.cell` that both backends
show through items of their own, decision B in the phase-2 note) is merged; 4b (the
read-only yFiles canvas for aspect graphs in the optional unit, with three review rounds on
rendering fidelity) is merged into `yfiles-canvas-aspect`; 4c (LTS, control and plain
canvases, plus click forwarding to Swing listeners) is merged into `yfiles-canvas-aspect`;
4d (the yFiles layout algorithms in the layout palette, as partial layouts when vertices
are fixed, plus the persisted backend preference) is merged: phase 2 is complete for the
read-only views. **Phase 3 (the editor) is COMPLETE** (2026-09-08): design and slicing in
`claude/phase-3-editor.md`; slices 3a (the GROOVE-owned edit model and undo history in
`gui.view`, both backends feeding it), 3b (the editor canvas on yFiles, gestures mapped
onto yFiles' editor input mode, with Arend's six review rounds fixed: parallel edges fan
out as in JGraph through the neutral `gui.view.ParallelEdges`, labels and edge points are
draggable on yFiles, straight edges between aligned nodes run vertically or horizontally
on both backends), gh #913 (creating a cell and giving it its first label is one undo
step, without syncing the unlabelled cell; this also removed the label-tree blink on a
new edge) and 3c (the clipboard as a cell-level `GraphFragment`, with copy by Ctrl+drag
on both backends, two review rounds fixed) are all on branch `editor-edit-model` off the
rebased `yworks-migration`; the intermediate branches were folded into it. Accepted
cosmetic differences of the yFiles editor: edge curves are yFiles' corner smoothing
rather than JGraph's interpolating spline, and JGraph's arrow adornment corrections are
not ported. Node dragging in the yFiles viewer canvases, deferred since phase 2 slice 4c, is done
(2026-09-08, confirmed by Arend), and everything is folded into `yworks-migration`.
**Phase 4 (export + Imager) is COMPLETE** (2026-09-08, branch `phase-4-export`): the
exporters and the headless `Imager` were already on the canvas contract since phase 1b
slice 5, and verification against the JGraph output of every sample grammar (391 images)
found one systematic defect, the placement of loop labels on yFiles, which is fixed;
`YFilesImagerTest` runs the Imager on the yFiles backend in every format and the
Simulator test exports the LTS; the Imager gained a `-b` switch for the backend. Details
under "Phase 4" below. Phase 4 is merged into `yworks-migration` (2026-09-09). **Phase 5
(the yFiles edition) is BUILT** (2026-09-09, branch `yfiles-edition`): the release
reactor has a `yfiles` profile that obfuscates the library with yGuard and packages
`groove-x_y_z-yfiles-bin[+doc].zip` next to the standard zips, the installer script
builds a `GROOVE-yFiles` package from them, and the edition was verified from the zip,
from the app image and by the unit's own tests against the obfuscated jars; see "Phase 5"
below. **The edition was then reshaped into an add-on** (2026-09-09, branch
`yfiles-extension-loader`, four commits): yWorks confirmed the licence to be a Project
Licence, and the decision recorded in `claude/yfiles-distribution-options.md` is one
standard distribution plus a CI-built add-on zip that GROOVE loads from a user-level
extension directory and offers to install itself; see "Phase 5b" below. What remains is
on Arend's side: the private repository and secret for the release workflow, the
license correspondence with yWorks, the download page, and the merge into `master`; the
backend module split (gh #887) stays an independent decision. The architecture allowlist is empty.

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
  accepted). The SLA grants automated-build use only under a Project License;
  **yWorks confirmed on 2026-09-09 that the licence is a Project Licence with one
  seat**, so the release workflow builds the add-on, taking the plain jar from a
  private repository that only Arend and the workflow token can read (see
  `release/README.md`). The seat is a constraint on people: only Arend develops
  against the plain library.
- **Academic restriction propagates (§2.4).** The clause reads: "Any software
  application developed under an Academic License may not be licensed in whole or
  in part, to a third party being a commercial institution or a party that
  commercially uses the Software." It restricts *licensing* the application, not
  only distributing binaries, while GROOVE is Apache 2.0. Consequence for the
  binaries: **one distribution plus an add-on** (decided 2026-09-09, reversing the
  dual-distribution plan under which phase 5 was first built; the analysis is in
  `claude/yfiles-distribution-options.md`) — the standard release stays JGraph-based
  and unrestricted, the yFiles backend ships as a separate add-on zip with its own
  notice, loaded from the extension directory, and **both backends stay genuinely
  maintained** (accepted; mitigated by capability tiering: optional yFiles-only
  features may degrade gracefully in the JGraph edition, but core
  view/edit/select/filter/export stays at parity). Consequence for the source
  (seen only on re-reading the SLA on 2026-09-09): **the backend unit's source is
  not public.** It is part of an application developed under the licence and was
  published under Apache 2.0 in `yfiles/` from phase 2 slice 3 until the move to the
  private repository `nl-utwente-groove/yfiles-lib` (branch `yfiles-private-move`);
  whether the public history must be purged as well is a question to yWorks.
- **Ownership and disclosure (§1).** The licensee "shall not use or disclose any
  Software technology, idea, algorithm, or information" beyond what it can document
  as "generally available for use and disclosure by the public without any charge
  or license". API names are in the public reference (docs.yworks.com); the usage
  patterns come from the bundled developer guide and demos, which §2.1(b) lists as
  non-redistributable. A second reason for keeping the backend source private; API
  names in prose (this file, `claude/yfiles-spike-findings.md`,
  `claude/phase-2-model-and-ownership.md`) are judged to be within the public
  reference.
- **Obfuscation's purpose (§2.1c)** is stated: "it shall no longer be possible to use
  the functionality of the Redistributables via their public API". Public backend
  source next to the obfuscated jars would yield the name mapping for the API subset
  GROOVE uses; the unobfuscated backend jar in the add-on leaks the same mapping less
  directly. Question (b) to yWorks.
- **The license is perpetual (§10.4a).** Non-renewal of the Subscription loses only
  upgrades/support. Still unknown: the delivered version/generation and
  Subscription status (check the license order, not the SLA).
- **No API re-exposure (§2.1d)**: applications "may not expose an API to a third
  party that will allow them to access functionality provided by the Software". The
  yFiles backend package stays unexported in `module-info`; the facade remains a
  GROOVE-internal seam. The add-on jar's public classes (service provider, canvases,
  layouter) are callable by third parties in principle; documented as internal, and
  question (g) to yWorks.
- **Project License (§2.2.3)** covers "different editions of the Authorized
  Application" and "an automated build process": CI builds and the add-on shape are
  within it.
- Nothing else in the SLA mentions the licensee's own source code. The SLA is
  `C:\Groove\yfiles\yFilesForJava-SLA-signable.pdf`; the Read tool refuses it (owner
  restriction, no user password), pypdf with an empty password reads it.
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
  For the yFiles unit the by-hand ecj run (see the practicalities at the end) is
  largely blind: `@NonNullByDefault` classes overriding methods of the unannotated
  yFiles types (`AbstractNodeStyle`, `IDragHandler`, `DropInputMode`, `IVisual`,
  `IArrow`, …) draw 63 "illegal redefinition of parameter" errors (2026-09-08
  baseline), and ecj skips null and flow analysis for a unit with compile errors, so
  `CellArrow`, `CellEdgeStyleRenderer`, `CellNodeStyle` and `YFilesAspectEditorCanvas`
  are not null-checked at all. Real coverage needs external annotations for
  `com.yworks.*` in the style of `lib/eea`; open design decision. The unit's own
  `.settings` do not enable null analysis, so Eclipse shows none of this either.
- **Stale test classes**: after interface-level changes (package moves, signature
  changes), run `mvn clean test` — the incremental build has produced stale
  test-class `NoSuchMethodError`s twice.
- **Eclipse after merges**: refresh only, except `module-info`/pom changes
  (Maven → Update Project).
- The GUI test suite barely exercises the display layer; every branch's real
  verification is manual Simulator use (menus, editor gestures, LTS interaction,
  filtering, export).

## Phase 4: export and Imager on yFiles (2026-09-08, branch `phase-4-export`)

Nothing in the export path needed a yFiles-specific port: `CanvasExportable`,
`RasterExporter` (via `toImage`), the vector writers (via `paintGraph`/`getGraphBounds`)
and `GraphToTikz` (from the view model) all work on the canvas contract, and
`YFilesCanvas` exports through yFiles' `PixelImageExporter` and `exportContent` on a
`ContextConfigurator` over the content rectangle, so the whole graph is painted, never
the viewport. Verification: every sample grammar in `junit/samples` was rendered to PNG
by the Imager on both backends (391 images) and compared by size and blurred pixel
difference; the vector formats were checked on the SVG (same document size on both
backends, whole graph) and the TikZ output is byte-identical, since it is generated
from the view model. The Imager picks the yFiles backend by the `GraphBackend` ranking
(the persisted preference applies as in the Simulator), or the backend named by its new
`-b` option (`GraphBackend.request`, honoured ahead of the preference; an unavailable
name falls back to the default with a warning on stdout), and runs with
`java.awt.headless=true` on both backends.

Findings and residues:

- **Loop labels** were the one systematic difference: the yFiles store put them on the
  loop's path at the stored permille ratio, ignoring the offset and measured along
  yFiles' own clipped path. JGraph (label transformation off, its default) walks the
  polyline through the stored points for every edge, which `EdgeGeometry.labelPosition`
  already reproduced for the other edges; loops now get the same absolute point, placed
  with a `FreeLabelModel` and re-placed by the canvas' `followDrag` during drags. Fixed.
- **Curve shapes** differ as accepted for the editor: yFiles' corner smoothing against
  JGraph's interpolating spline, so images of curved edges and loops differ by a few
  pixels in extent, and the TikZ output (JGraph's spline geometry, shared by both
  backends) does not match what yFiles shows for curved edges. Accepted, not fixed.
- **Text rendering**: JGraph's raster output carries LCD subpixel fringes on text,
  yFiles' is grey-antialiased. Cosmetic.
- Grammars whose start graph is empty (`empty`, `fibonacci`, `transactions`) fail the
  Imager with "Cannot export blank image" on both backends; pre-existing, not phase 4.
- The main project's `ImagerTest` stays on JGraph (the yFiles unit is not on its class
  path); `YFilesImagerTest` in the unit is its yFiles counterpart and keeps its output
  in the unit's `target/imager`; `YFilesSimulatorTest.ltsDisplayExportsRasterAndVector`
  covers the LTS canvas, which the Imager does not reach.

## Phase 5: the yFiles edition (2026-09-09, branch `yfiles-edition`)

**What was built.** The release reactor (`release/`, a separate Maven reactor receiving
the version as `-Drevision`) gained a profile `yfiles`:

- A module `release/yfiles` (artifact `yfiles-edition`, active only under the profile)
  copies the `groove-yfiles` jar and the library jar from the local Maven repository
  and runs yGuard 4.1.1 (MIT, from Central, as an Ant task through `maven-antrun-plugin`,
  the route of the yFiles deployment demo and of the developer guide's "Obfuscation"
  appendix) over the two as one `<inoutpair>` set: every name of the library is
  renamed, the backend's references are rewritten, `nl.utwente.groove.**` keeps all
  class, method and field names plus line numbers, `**/*.properties` are renamed with
  their classes (`<adjust>`), and `com.yworks.yfiles.utils.Obfuscation` is honoured as
  yWorks' own exclusion annotation. The outputs keep their original file names in
  `release/yfiles/target/lib` and the mapping is in `target/yguard.log.xml.gz`. The
  GROOVE core and its dependencies are yGuard's external class path, with JSR-305 added
  so that the unresolved-name warnings are meaningful (zero after that). Of 6131 classes
  in the obfuscated library 98 keep readable names: yWorks' annotated exclusions
  (`PointD`, `RectD`, ...) and the methods GROOVE overrides.
- The profile adds `groove-yfiles` as a dependency of the reactor, so the runnable jars'
  manifest class paths list `../lib/groove-yfiles-x.y.z.jar` and
  `../lib/yfiles-for-java-swing-3.6.0.1.jar`; the assembly uses `zip-yfiles.xml`
  descriptors (the standard ones plus the module's `target/lib` and `include`
  directories) and the final name `groove-x_y_z-yfiles-bin[+doc].zip`. The reactor
  order is forced by a pom-type dependency of `assembly` on `yfiles-edition`, which
  stays out of `lib/` because the descriptors' include names the artifact type
  (`nl.utwente.groove:groove:jar`). Without the type the filter admitted every
  dependency of the assembling module, which is how `runnable-1.0.pom` had been landing
  in `lib/` of the standard release all along: the assembly plugin matches an include
  as a substring, and with transitive filtering also against the dependency trail,
  whose first entry is the assembling module `nl.utwente.groove:groove-bin:pom:1.0`.
- `release/yfiles/include/YFILES-EDITION.md` is the edition's notice: what it is,
  non-commercial use only, no extraction or reverse engineering of the library, passing
  on only unchanged. It sits at the root of the edition's zips and is the license text
  of its installers. **Arend must review its wording against the SLA** before a first
  release; the download page of the website needs the same statement next to the
  edition's artifacts (website repository, not done here).
- `release/jpackage/build-installer.sh <version> <type> yfiles` builds the edition's
  installer from the `-yfiles-bin` zip as `GROOVE-yFiles` (own package identifier and
  upgrade UUID, so it installs next to the standard package), named
  `groove-x_y_z-yfiles-<os>-<arch>.<ext>`. The bundled runtime needs `jdk.xml.dom`,
  which the library uses: the script runs `jdeps` over the two edition jars as well
  and unions the module sets, since a library's needs need not show up in the core
  module's own dependences. The core jar is analysed without its module descriptor,
  because a jdeps that resolves the module graph first (JDK 26 does, JDK 21 does not)
  fails on the automatic modules the descriptor requires; before that fix a local
  installer build silently fell back to bundling `java.se`.
- `release/do-all.sh yfiles` runs the standard steps, then installs the backend (with
  its tests) and packages the edition with `-Pyfiles package`, without `clean` so the
  standard zips survive in the shared `release/target`. That sequence only works
  because the runnable module now sets `forceCreation` on the jar plugin: the jars hold
  nothing but a manifest, and without it the plugin kept the standard build's jars,
  whose class paths lack the edition entries — found because the app image silently
  fell back to JGraph.

**Rejected.** Keeping the library's public API readable and obfuscating only its
internals (the handoff's phrasing): weaker than the SLA's intent and not what yWorks'
recipe does. The `yguard-maven-plugin` 1.0.0 (on Central, wraps yGuard 5.0.0): untried,
the antrun route is the one the demos verify. Analysing every `lib/` jar with `jdeps`
for the standard installer too: a behaviour change outside this concern.

**Verification (2026-09-09).** The standard build is byte-for-byte unaffected in
layout (no yFiles entries). From the unzipped edition, `Imager -b yfiles` renders on
the obfuscated stack; the same from the app image after the module fix. The unit's own
tests run green against the obfuscated jars (65 tests: 61 pass, the 4 Robot tests skip
by assumption) with this recipe, worth scripting if it is needed again: jar
the unit's `target/test-classes`, run one yGuard pass with *three* inoutpairs (library,
backend, tests jar; same keep rules) from a scratch pom copied from
`release/yfiles/pom.xml`, then run
`org.junit.platform.console.ConsoleLauncher execute --scan-class-path <tests.jar>`
with the three obfuscated jars, the external class path and
`junit-platform-console-standalone` all on the JVM's own `-classpath` (not `-jar`: the
launcher's child loader hides `GROOVE_VERSION` from `ClassLoader.getSystemResource`),
with the in-memory preferences factory of the test tree. On the tip before this work
the full suite (892 tests, corpus directories passed) and the GUI suite (10 tests) were
green.

**Not changed.** The backend unit itself (the license file still reaches the backend jar's
root through the `yfiles.license.dir` resource, and yGuard copies it through, which is
what licenses the obfuscated library at run time); CI (`release.yml` builds the
standard release only; the edition's zips and installers are attached to the github
release by hand); the plain library jar stays in `~/.m2` only.

## Phase 5b: the yFiles add-on (2026-09-09, branch `yfiles-extension-loader`)

The edition of phase 5 was reshaped into an add-on the same day, once yWorks had
confirmed the Project Licence; the reasoning is in `claude/yfiles-distribution-options.md`.
Four commits, one per implementation slice of that note:

1. **Extension loader** (`util.Extensions`, `GraphBackend.discover`): a user-level
   extension directory (`%APPDATA%\GROOVE\extensions`, `~/Library/Application
   Support/GROOVE/extensions`, `~/.groove/extensions`; property `groove.extensions.dir`)
   whose jars, in the directory and its immediate subdirectories, go behind one
   `URLClassLoader` with the application loader as parent; backend discovery runs the
   `ServiceLoader` through that loader, deduplicating by backend name. A jar declaring
   another GROOVE version in its manifest (`GROOVE-Version`) is skipped with a warning.
   `ExtensionsTest` compiles a provider into a jar at run time with the JDK's `javac`
   tool, because a class from the test class path would be found by the parent loader
   and, living in the named module surefire patches the tests into, ignored by the
   `ServiceLoader`. The surefire configurations and the Eclipse test launch point the
   directory at an empty location under `target`.
2. **Add-on packaging** (`release/yfiles`): the backend jar's manifest carries the GROOVE
   version (set in the backend's `pom.xml`, passed through by yGuard); the `yfiles` profile
   produces `groove-x_y_z-yfiles-addon.zip` (directory `yfiles/` with the two obfuscated
   jars and `YFILES-ADDON.md`, the rewritten notice) next to the standard zips. The
   edition zips, descriptors, the profile's manifest-class-path dependency, the
   installer's edition branch and the runnable module's `forceCreation` are gone.
3. **CI** (`release.yml`): the release job checks out the private repository
   `nl-utwente-groove/yfiles-lib` (plain jar + license file in `lib/`; since 2026-09-09 also the backend source, its root project) with `YFILES_LIB_TOKEN`,
   installs the jar, builds the backend (tests skipped on the headless runner) and
   packages with `-Pyfiles`. Not yet exercised: the repository and secret do not exist.
4. **In-app installer** (`gui.AddOnInstaller`, `util.AddOn`): the options part of the
   View menu gets a `yFiles add-on` submenu (download and install, install from file,
   remove); at the
   first start of a release version whose add-on is absent or stale, the Simulator asks
   once (recorded per version in the user preferences, suppressed for `-SNAPSHOT`
   versions and by `-Dgroove.addon.prompt=false` in the test configurations) and shows
   the license restriction. The download is `java.net.http` against the github release
   URL of the running version; installation unpacks into a dot-prefixed staging
   directory (hidden from the scan), verifies the version attribute and replaces the
   add-on directory. `AddOnTest` covers the install/verify/remove path headlessly,
   `AddOnGuiTest` drives the file installation and the removal through the menu.

**Verified**: the obfuscated jars work from the child loader (Imager on the ferryman
grammar), a stale add-on gives one warning and a JGraph fallback, and the standard app
image's Imager renders on the add-on unzipped into an extension directory, so the
standard runtime suffices. **Rejected**: keeping the edition installers next to the
add-on (two products to explain, macOS gap); a single installer with a yFiles question
(taints the installer's license, and jpackage cannot ask).

## Immediate next steps (2026-09-09, after phase 5b)

1. **Review of branch `yfiles-extension-loader`** (off `yworks-migration`, four commits):
   the loader and the installer code, the release poms, and the wording of
   `release/yfiles/include/YFILES-ADDON.md` and of the first-run question.
2. **CI set-up by Arend**: the private repository `nl-utwente-groove/yfiles-lib` holding
   `yfiles-for-java-swing.jar` and the runtime license file, the secret
   `YFILES_LIB_TOKEN`, then a dry run on a throwaway release tag from a branch.
3. **The private repository**: Arend creates `nl-utwente-groove/yfiles-lib`, pushes the
   prepared clone at `C:\Groove\yfiles-lib` (main), adds the library jar and licence file
   in its `lib/` directory, and imports the clone as the Eclipse project
   `groove-yfiles` in place of the old nested project (delete the old one from the workspace first). Until then the backend is built
   from that clone.
4. **The license questions for yWorks**, drafted as a message in
   `claude/yworks-question-2026-09.md` for Arend to send (2026-09-09). The questions,
   sharpened after re-reading the SLA (see "License constraints"):
   (a) the Subscription status and delivered generation of the license (the code is
   written against 3.6.0.1);
   (b) §2.1c: whether yGuard as in the deployment demo (all library names renamed
   except yWorks' own annotated exclusions and the methods GROOVE overrides), shipped
   next to GROOVE's unobfuscated backend jar, satisfies the clause, given that the
   backend jar's references reveal the renamed names of the API subset GROOVE uses;
   (c) whether the development license file may ship inside the add-on as the runtime
   license (it is what the library loads), or a deployment license is issued;
   (d) §2.1c/§2.4: whether an add-on distributed separately from GROOVE, as a public
   github release download with the non-commercial notice, is still "your own
   software applications" and acceptable;
   (e) §2.4: GROOVE is Apache 2.0 and public; the backend unit calling the yFiles API
   was public source and is being moved to a private repository. Is the rest of GROOVE
   (no yFiles code, runs without the library) unaffected by "may not be licensed ...
   to a commercial institution", and is the backend source acceptable in a private
   repository readable by the licensed developer and the build automation only;
   (f) §1/§2.1c: whether the commits that added the backend source must be purged
   from the public history, or removal from the current branches suffices (the purge
   procedure is estimated in `claude/yfiles-private-move-state.md`);
   (g) §2.1d: the add-on jar necessarily has public classes (the service provider,
   the canvases GROOVE calls); acceptable if documented as internal, or must the
   surface be reduced;
   (h) the wording of the notice `release/yfiles/include/YFILES-ADDON.md` and of the
   first-run question in `gui.AddOnInstaller.confirmInstall`.
5. **Merge `yworks-migration` plus this branch into `master`**: Arend's call.
6. **The website**: the download page gets the add-on next to the standard artifacts,
   with the non-commercial statement; the web manual's installation page describes the
   add-on and the extension directory, its layout section the yFiles algorithms.
7. **At the first release**: nothing by hand; keep `release/yfiles/target/yguard.log.xml.gz`
   from the workflow run (it is not attached) with the release, in case a user's stack
   trace needs translating — or add an upload step for it.
8. **The backend module split** (gh #887) remains independent and unblocked either way.

Side issues filed along the way, independent of the phases: gh #882 (mouse
interaction), gh #915 (JGraph editor grid snapping), gh #916 (popup actions in the menu
bar). Open technical residues: the null-analysis blind spot over the yFiles unit (see
"Practical notes"), the Robot tests of `YFilesSimulatorTest` that need a visible canvas,
and the accepted cosmetic differences listed under "Phase 4".

Practicalities carried over: the yFiles unit is built from the GROOVE checkout with
`mvn -q -f ../yfiles-lib/pom.xml test > <log> 2>&1` (installs the core
artifact first; `-Dgroove.install.skip=true` when it is current). The Robot tests in `YFilesSimulatorTest` skip unless
`-Dgroove.test.robot=true` and the canvas is visible on screen, but
synthetic mouse events on yFiles' input surface (the child component carrying its
mouse listeners, see `YFilesCanvasTest.drag`) do exercise the input modes headlessly.
The `null-check` script is bound to the main module: for the yFiles unit run ecj by hand with
the main `.settings` prefs and `lib/eea` against the unit's classpath, and diff the
problem list against a stashed baseline. The developer guide and javadoc are JS
bundles under `C:/Groove/yfiles/yFiles-for-Java-Swing-Complete-3.6.0.1/doc/api/assets`;
search them by member id (for example `MoveInputMode-property-HitTestable`) with a
script printing a window of text around the match, since regex tools choke on the
24 MB file. Input-mode priorities differ between the viewer and the editor mode
(viewer: click 10, marquee 30, viewport 39; editor: move 40, marquee 50): never take
the guide's editor figures for the viewer. The deployment demo
(`demos/src/deploy/obfuscation/build.xml`) and the Maven demo
(`demos/src-maven/deploy/mavendemo/pom.xml`) are the permitted references for the
obfuscation setup.
