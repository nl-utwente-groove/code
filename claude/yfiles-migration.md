# yFiles migration: analysis and plan

Status: analysis complete, decisions below taken 2026-09-01/02 (Arend + Claude session).
Phase 0 (spike) is the next step. This note is the reference for all work on the
`yworks-migration` effort; update it as phases complete or decisions change.

## Goal and motivation

Replace JGraph 5.13 (unmaintained for ~10 years) as GROOVE's graph rendering/editing
library with yFiles for Java (Swing), behind an architecture in which the visualization
backend is swappable — yFiles primary, JGraph retained. Priorities, in order:
rendering/interaction quality, automatic layout quality, escaping unmaintained code.
The interactive editor is in scope eventually; read-only views migrate first.

The swappable-backend requirement is driven by license risk (see below), but the
license analysis turned it from an insurance policy into a permanent operating mode.

## License analysis (yFiles SLA, signed; Academic Single Developer License)

Key clauses and their consequences:

- **Redistribution only with obfuscation (§2.1c).** The yFiles jar may ship inside
  GROOVE only after obfuscation (yGuard-style: yFiles class/member names renamed,
  GROOVE's references rewritten). The plain jar can never be in the public repo or
  any Maven repository. The release pipeline gains an obfuscation leg for the
  yFiles edition; no reflection over yFiles classes anywhere.
- **Per-developer seats (§2.2.2).** Single Developer License: only the named
  developer (Arend) may access the jar. Co-developers cannot even possess it.
  Therefore the yFiles backend must be an optionally-compiled unit and
  `git clone && mvn package` must keep working without the jar. Additionally, the
  SLA grants automated-build use only under a Project License, so CI cannot build
  the yFiles edition — those releases build locally, unless the license is upgraded
  to Academic Project (3 seats + build automation; upgrade suggested to yWorks).
- **Academic restriction propagates (§2.4).** An application developed under an
  Academic License may not be provided to commercial parties. GROOVE is Apache 2.0.
  Consequence: **dual distribution** — the standard GROOVE release stays JGraph-based
  and unrestricted; a separate yFiles edition (obfuscated jar bundled) is
  non-commercial-only. Hence the JGraph backend remains genuinely maintained as the
  backend of the primary public release, not just a compile-checked fallback.
- **The license is perpetual (§10.4a).** Non-renewal of the Subscription loses
  upgrades/support, not the right to use the licensed generation. The discretionary
  risks are extra seats and the academic scope; both are mitigated by the
  dual-backend architecture.
- **No API re-exposure (§2.1d).** GROOVE may not expose an API giving third parties
  access to yFiles functionality: the yFiles backend package stays unexported in
  `module-info`, and the facade remains a GROOVE-internal seam, never a
  general-purpose graph-viz API.

Alternatives were re-checked: the maintained-Swing-graph-viz space is empty
(JGraphX archived 2020, JUNG dead, GraphStream lacks editing, GEF is SWT).
yFiles for Java (Swing), current major 4.0, is the only serious option short of a
webview/yFiles-for-HTML rework.

## Codebase analysis (three exploration passes, 2026-09-01)

- **Coupling is confined to `gui/**`.** Nothing in `grammar`, `graph`, `io`, `lts`,
  `explore`, `SimulatorModel`, or any test imports JGraph. 34 files in `gui/jgraph`
  (~11.5k LOC), 15 files elsewhere importing `org.jgraph` (+3 fully-qualified uses),
  51 files consuming `gui.jgraph` types — all under `gui`.
- **A neutral layer already exists.** `gui/look` (`VisualKey`, `VisualMap`, `Look`,
  the `VisualValue` strategies, `MultiLabel`) is a library-independent visual model.
  Its only JGraph bridge is `VisualAttributeMap` (extends `AttributeMap`) plus ~6
  small `GraphConstants` leak sites. Layout persistence (`graph/layout.LayoutMap`)
  is fully neutral.
- **Misplaced domain logic, confirming the factoring-out suspicion.** ~22% of
  `gui/jgraph` has zero JGraph references (the concrete cell classes are pure domain
  code); another ~2.5k LOC of domain/controller logic lives inside JGraph subclasses:
  `JGraph` mixes the Swing component with the per-display controller (options, menus,
  modes, layouter wiring — ~60% of 1658 lines); `LTSJGraph`/`LTSJModel` are ~95%
  state-space semantics; `JModel` holds the element→cell index, edge-merging policy
  and layout persistence.
- **Three hard structural dependencies:** the port model (gratuitous — one dummy
  port per vertex solely for JGraph's connection machinery), visibility filtering
  implemented as `GraphLayoutCache` partiality, and the `AttributeMap` edit channel
  (undoable changes flow through `DefaultGraphModel.edit`; JGraph-internal edits
  flow back into `VisualMap`).
- **The genuinely hard migration items:** editor undo (a `GraphUndoManager` subclass
  classifies edits minor/major by inspecting JGraph edit internals; drives dirty
  tracking and grammar refresh), clipboard (JGraph's `GraphTransferHandler`, no
  GROOVE-owned format), export (PNG/EPS/PDF/SVG all repaint the live Swing
  component; the Imager is not headless — it instantiates an `AspectJGraph`;
  TikZ export is nearly neutral already), and file-format fidelity: saved `.gxl`
  layout encodes JGraph conventions (`LineStyle` codes are `GraphConstants.STYLE_*`
  values, label positions in PERMILLE-along-edge units). These numbers are frozen;
  any backend translates to them.
- **Rendering quirks with semantic weight:** HTML label metrics drive node sizes,
  which drive perimeter points and saved layouts; adornment-aware perimeter points;
  the GROOVE-invented MANHATTAN line style; parallel-edge fanning at paint time;
  LTS-scale performance hacks in `JGraphLayoutCache`/`JGraphUI`.
- **Dropping JGraph also drops JGraph Layout Pro** (11 `com.jgraph.layout.*`
  algorithms and the reflective parameter panels in `LayoutKind`). yFiles layouts
  replace the algorithms; the parameter UI is a rewrite (explicit code, not
  reflection — see obfuscation above).

## Decisions taken

1. Swappable backend, yFiles primary; **both backends stay maintained** (dual
   distribution forces this). Mitigate cost by capability tiering: optional
   yFiles-only features may degrade gracefully in the JGraph edition; core
   view/edit/select/filter/export stays at parity.
2. Multi-module Maven restructure acceptable (core + backend-jgraph +
   backend-yfiles; yFiles module optional, jar outside the repo).
3. Backend selection at startup time (flag/property), no runtime switching.
4. Fidelity bar for existing grammars under yFiles: **positions exact, cosmetics
   may differ**.
5. Facade design is derived from the consumer census (~15 operations: get/set
   displayed graph, selection get/set/listen, model-change listen, refresh, scale,
   scroll-to, gray-out, layouter geometry read/write, paint-into-Graphics2D, mode
   switching, undo events), not from JGraph's concepts — and is validated against
   the yFiles viewer before being frozen.

## Plan

- **Phase 0 — spike** (timeboxed, throwaway, standalone project outside the repo,
  depending on the local GROOVE artifact + the yFiles jar from a local path).
  Rendering-fidelity test on real `junit/samples` grammars incl. exact `LayoutMap`
  positions; yFiles hierarchic/organic layout vs Spring/Forest; LTS scale test
  (thousands of states via `Generator`). Deliverables: side-by-side renders, a
  findings note in `claude/`, facade implications. Stop the project here if
  rendering does not clearly beat JGraph.
- **Phase 1 — decouple in place** (no yFiles, no behavior change, several small
  PRs; valuable regardless of yFiles): extract the controller half of `JGraph` and
  the LTS semantics of `LTSJGraph`/`LTSJModel` into neutral classes; kill the ~6
  `GraphConstants` leaks (GROOVE-owned frozen constants); de-port the cell
  interfaces; GROOVE-owned selection/model-change listener interfaces; define the
  facade with the JGraph code as first implementation; add an architecture test
  asserting no `org.jgraph` import outside the backend package.
- **Phase 2 — yFiles read-only views**: `GraphTab`, `StateDisplay`, previews, then
  the LTS display; selection sync, filtering, gray-out, match highlighting; yFiles
  layouts writing through `LayoutMap` (with line-style/label-position translation).
  Facade revised as needed. Multi-module restructure lands at the start of this phase.
- **Phase 3 — editor**: input modes for the click-click edge gesture, in-place
  editing with label autocompletion, GROOVE-owned undo/edit model, GROOVE-owned
  clipboard format. Largest single chunk.
- **Phase 4 — export + Imager**: vector path via Java2D against the yFiles
  component; TikZ via the geometry seam; Imager rebuilt on the facade (can become
  genuinely headless).

Sizing, honestly: phase 1 touches ~60 files over multiple PRs; phases 2–4 are a
multi-month effort. Every phase leaves master shippable. Estimated permanent
overhead of dual backends: 10–30% on GUI-touching work, near zero elsewhere.

## Open items

- Delivered yFiles version/generation and Subscription status (from the license
  order/delivery, not the SLA).
- Possible upgrade Academic Single Developer → Academic Project License (3 seats,
  build automation) — query to yWorks suggested.
- Spike start: waiting on a local path (outside any repo) to the yFiles jar(s) and
  the runtime license key file, downloaded by Arend. Ground rules: the jar is never
  committed, never uploaded, never decompiled; spike code is written against the
  public API docs and demos.
