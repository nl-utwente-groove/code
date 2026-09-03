# yFiles spike (phase 0): findings

Part of the yFiles migration (gh #909, see `claude/yfiles-migration.md`). This note records
what the phase-0 rendering spike did, what it showed, and what it implies for the facade.
Written 2026-09-03 from a session driven by Arend; all yFiles knowledge below comes from the
bundled developer guide, Javadoc and demo sources (the jar was never inspected).

## Setup

- **Delivered library**: yFiles for Java (Swing) **3.6.0.1** (not 4.0 as the plan assumed),
  unpacked by Arend at `C:\Groove\yfiles\yFiles-for-Java-Swing-Complete-3.6.0.1`; the
  runtime license file `com.yworks.yfiles.java.developmentlicense.xml` sits next to it in
  `C:\Groove\yfiles`. Requirements per the readme: JDK 8+, no third-party dependencies.
- **Local Maven install**: the jar is installed in the local repository as
  `com.yworks.yfiles:yfiles-for-java-swing:3.6.0.1` (the coordinates Arend's Eclipse had
  already tried to resolve; yWorks' own Maven demo uses artifactId
  `yfiles-for-java-complete` instead — both appear in the distribution, pick one and stay
  consistent). Install command, run in the distribution's `lib` directory:
  `mvn install:install-file -Dfile=yfiles-for-java-swing.jar -DgroupId=com.yworks.yfiles -DartifactId=yfiles-for-java-swing -Dversion=3.6.0.1 -Dpackaging=jar`.
- **License mechanism** (developer guide, appendix "Licensing"): the library looks for the
  license file at the root of the classpath and loads it automatically; no API call needed.
  The spike pom declares `C:/Groove/yfiles` as a resource directory (xml only) so the file
  is copied into `target/classes` at build time; its contents never enter a session.
- **Spike project**: `C:\Groove\yfiles-spike` (outside any repository, throwaway). Maven,
  Java 21, classpath only (no `module-info`), depends on the GROOVE snapshot
  `nl.utwente.groove:groove:7.5.4-SNAPSHOT` (built and installed from this branch's tip) and
  the yFiles jar. Programs run in a separate JVM via `exec:exec`:
  `mvn -q compile exec:exec -Dspike.main=spike.<Main> -Dspike.args="..."`. Note that
  `exec:java` (in-process) fails with an `ExceptionInInitializerError` in GROOVE's Swing
  set-up; use `exec:exec`.
- Programs (`src/main/java/spike`):
  - `GrooveGraphs` — GROOVE side. Loads a grammar, builds GROOVE's own JGraph view of each
    graph resource exactly as the `Imager` does (`AspectJGraph` + `AspectJModel.loadGraph`),
    and extracts a backend-neutral visual model: node centre and text-fitted bounds
    (from `JGraph.getCellBounds`), routed edge points (from the JGraph `EdgeView`, so loops
    and bends are the ones actually drawn), label position, line style, plain label text
    (`MultiLabel` through `StringFormat`) and the cell's full `VisualMap`. Also headless
    exploration to a `GTS`.
  - `YFiles` — yFiles side. Builds an `IGraph` from that model, mapping `VisualMap` values to
    yFiles styles (`ShapeNodeStyle`, `PolylineEdgeStyle`, `Pen`/`DashStyle`, `Arrow`,
    `DefaultLabelStyle`), and exports PNG through `PixelImageExporter` on a throwaway
    `GraphComponent` (no visible window; needs a desktop session, true `java.awt.headless`
    is unverified because the docs do not address it).
  - `RenderGrammar <outRoot> <grammar.gps>...` — fidelity test: Imager (JGraph) and yFiles
    renders of every graph resource, matching file names.
  - `LayoutCompare <outRoot> <grammar.gps>...` — GROOVE Spring/Forest (rendered by JGraph)
    versus yFiles hierarchic/organic/orthogonal (rendered by yFiles).
  - `LtsScale <outRoot> <grammar.gps> [<start>|-] [hierarchic|organic|none] [-show]` —
    explores, builds the GTS as a yFiles graph, times the layout, exports a PNG, and with
    `-show` opens an interactive viewer (`GraphViewerInputMode`) for pan/zoom testing.
  - `Report <out.html> <title> <dir>...` — side-by-side HTML index of PNG directories.
  - `Dump`, `Explore` — diagnostics.
- Outputs in `C:\Groove\yfiles-spike\out`: `jgraph/<grammar>` and `yfiles/<grammar>`
  (fidelity), `layout/<variant>/<grammar>`, `lts/`, and the HTML indexes
  `fidelity-<grammar>.html`, `layout-<grammar>.html`.

## Fidelity (exact stored layout)

Grammars rendered: `ferryman`, `car-platooning`, `attributed-graphs`, `inheritance`,
`leader-election` (host graphs, rules, type graph). Result against the bar "node positions
exact, cosmetics may differ": **met**.

- Node positions and sizes are identical (the JGraph bounds are used verbatim; the PNGs of a
  graph come out with the same pixel dimensions from both backends).
- Label text is identical, including multi-line node labels (`bank\nleft`), rule-role
  colouring (creator green / eraser blue dashed / embargo red thick / nesting) and the
  data-node text (`length → 0`), because the text and colours come from GROOVE's own look
  layer (`VisualMap`), not from a reimplementation.
- Edge bends are reproduced from the routed points. Loops (e.g. the `a` loop in
  `inheritance/type.gty`) come out at the same place because the routed loop points are
  read from the JGraph edge view.
- Edge labels sit where JGraph puts them, after one correction (below).
- Cosmetic differences: yFiles arrowheads are larger filled triangles (GROOVE's `ARROW`,
  `UNFILLED`, `SUBTYPE`, `COMPOSITE`, `NESTING` ends all have counterparts in `ArrowType`
  but the sizes were not tuned); "bezier" edges are drawn as polylines with corner
  smoothing rather than true Bezier curves through the control points (yFiles has
  `BezierEdgeStyle`, not tried); rounded corners are rounder; anti-aliased text.
  Not attempted: node id/parameter adornments (`ID_ADORNMENT`, `PAR_ADORNMENT`), inner
  lines (`INNER_LINE`), error overlays, emphasis.

**Edge label semantics discovered.** GROOVE's persisted label position `(ratio‰, distance)`
is *not* relative to the source–target line: `JEdgeView.getLabelVector` anchors the label
on the **first segment** (point 0 to point 1) of the routed edge, extended by `ratio` and
offset perpendicularly by `distance`. For straight edges this coincides with the
source–target line, for bent edges and loops it does not. yFiles' `FreeEdgeLabelModel`
uses the source–target port line, so the first attempt misplaced labels on bent edges. The
spike now computes the JGraph anchor and places the label absolutely (`FreeLabelModel`).

## Automatic layout

All layouts on rule-sized graphs run in single-digit milliseconds on both sides. Judged by
eye on `ferryman/start.gst` and `car-platooning/Ztop8b.gpr` (a chain of `hon` nodes with a
long back edge):

- yFiles hierarchic (integrated edge labeling, node labels considered): clean layered
  drawing, labels placed on edges without overlap; with default distances noticeably more
  spread out than GROOVE's Forest layout.
- yFiles organic: compact and readable, edge labels placed by the generic labeling stage.
  Note: enabling the labeling stage without excluding node labels moves the *node* labels
  out of their interior model — set `GenericLabeling.setNodeLabelPlacementEnabled(false)`.
- yFiles orthogonal: tidy, but large, and orthogonal routing is not GROOVE's visual idiom.
- GROOVE Forest on `Ztop8b`: overlapping edges and labels; Spring: close to the stored
  layout. yFiles hierarchic/organic are clearly better on this input.

## LTS scale

| input | states / transitions | build IGraph | hierarchic | organic | export |
|---|---|---|---|---|---|
| `car-platooning` start-03 | 268 / 561 | 0.35 s | 0.25 s | 0.12 s | 0.4–0.5 s |
| `As-and-Bs-reg-exp-benchmark` | 8240 / 44774 | 0.9 s | 43.9 s (drawing 123 627 × 5 492) | 15.7 s (11 233 × 11 038) | 4.7 s / 7.9 s (scaled to 6000 px wide) |

(exploration itself: 0.3 s and 0.9 s.) The 8240-state hierarchic result is a very wide,
shallow layering — usable for an overview but not as an interactive default; the organic
layout (16 s, roughly square) is the candidate for large LTSs, although at overview scale
it is a hairball with visible cluster structure; readability at this size depends on
zooming and level-of-detail styling, not on the layout. Both are single-threaded default
configurations without `setMaximumDuration`. Interactive behaviour (pan/zoom responsiveness at
this size) is to be judged by Arend with `LtsScale ... -show`; the developer guide's
"Large Graph Performance" chapter recommends level-of-detail styles and label visibility
thresholds, none of which the spike enables.

## Facade implications

1. **The looks layer already is the facade's input.** Building the yFiles graph needed
   nothing from JGraph except what GROOVE stores in `VisualMap` plus three things the
   JGraph *view* computes: text-fitted node size, routed loop points, and the edge label
   anchor. The facade must own or specify these three: node sizing from label metrics
   (yFiles reports a label's preferred size; the facade can size nodes as
   `preferredSize + inset` itself), loop routing (compute default loop bends in the neutral
   layer rather than leave it to the backend), and the label-anchor convention.
2. **Persisted label positions keep JGraph's first-segment semantics.** The `.gxl` format
   is frozen; the yFiles backend must translate `(ratio, distance)` on load (anchor
   computation as in the spike, then either absolute placement or an
   `EdgeSegmentLabelModel` parameter for segment 0 from the source — the latter keeps the
   label attached to the edge during editing, but its `distance` is a model property, so one
   model instance per label) and translate back on save (label centre projected onto the
   first segment, as `LayoutIO.version2LabelPos` does). Alternatively the neutral format
   could switch to a path-ratio model in a new layout version; this is a decision for the
   facade definition.
3. **Style mapping is mechanical**: `NodeShape` → `ShapeNodeShape`, `EdgeEnd` → `ArrowType`,
   dash arrays → `DashStyle`, line width → `Pen`, `Font` style bits → `deriveFont`,
   foreground/background → paint/pen; see `YFiles.java`. Missing on the yFiles side is only
   what GROOVE draws itself (adornments, inner lines, error overlays), which become custom
   style decorators or extra labels.
4. **Image export** works from a component that is never shown; the same
   `ContextConfigurator` route gives a `Graphics2D` painting path (`exportContent(ctx)
   .paint`) for the vector/TikZ phase. SVG/PDF need third-party libraries per the guide.
5. **JPMS**: the docs do not state the jar's module name; the spike runs on the classpath.
   The multi-module restructure must find the module name via the IDE (Eclipse's
   quick-fix on a `requires` clause, which the guide itself recommends) before the
   yFiles backend can be a named module.
6. **Development license watermark**: the guide says a development license draws a
   watermark on every canvas; none is visible in the spike's exports, so the delivered
   license appears to be a full one. (Not verified by reading the file.)

## Residues and next steps

- Arend: run `LtsScale ... -show` on the 8240-state LTS to judge interaction, and browse
  the HTML indexes in `C:\Groove\yfiles-spike\out`.
- Decide the go/no-go on rendering quality; on the evidence here, rendering fidelity is
  not an obstacle and the layouts beat Spring/Forest.
- Then phase 1b: facade definition, informed by the three view-computed items above and the
  label-anchor decision; architecture test sealing `org.jgraph` inside the backend package.
