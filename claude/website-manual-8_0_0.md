# Updating the web user manual for release 8.0.0

Proposal for the `manual/` directory of the Jekyll site (`nl-utwente-groove.github.io`).
Sources: `release/include/CHANGES.md` (8.0.0 section) and `CHANGES-8_0_0.md`, gh #896, and
the 8.0.0 source tree. Nothing in the website repository was modified for this note.

## 1. How far the manual lags, and how much work this is

The manual documents 7.5.3 throughout: `manual.md` carries "7.5.3" in the *Checked against*
column of all six chapters, all four generated reference pages say "generated from the GROOVE
7.5.3 source code", and `introduction.md:65` names 7.5.3 as the current release. It is not
uniformly at 7.5.3, though: `verification.md` was already written against the parametric
exploration engine and documents the `-x` key/value vocabulary — but against an intermediate
development state, so its key table is now wrong in details (no `seed`, wrong `frontier` and
`bound` values) and its account of where an exploration is *stored* (a system property, a
transient dialog) predates settings resources entirely. `io.md` likewise already announces
Ecore/EMF, but describes the removed 2012 conceptual layer as the mechanism.

The update is substantial but bounded: roughly two new sections in `advanced.md` (transformation
semantics, the new grammar properties), one new section in `basics.md` (parallel edges and
`mult=`), a rewrite of the first third of `verification.md` and of the Ecore part of `io.md`,
factual repairs in `introduction.md`, and a decision about where GUI-level material (graph
backend, layouts, look and feel) belongs — the manual currently has no chapter about the
Simulator as an application, so the headline yFiles feature has no home. All four reference
pages must be regenerated, and one of them cannot be: `MakeRefs.java` imports
`explore.StrategyValue` and `explore.AcceptorValue`, neither of which exists in 8.0.0, so the
generator itself needs changing before `ref-exploration.md` can be produced at all. Order of
magnitude: ~1460 lines of hand-written manual today, of which perhaps 250 change or are added,
plus ~380 lines of regenerated reference pages and a figure re-render.

## 2. Changes to document

Priorities: **A** wrong or missing headline feature, **B** new option or property, **C** cosmetic.

| Change | Page / section | Action | What to write | Prio |
|---|---|---|---|---|
| `semantics` property (`SPO-simple`, `SPO-multi`, `DPO`; default `SPO-multi`; renamed from `parallelEdges`, converted on load) | `advanced.md` §System properties | new section + table row | New subsection "Transformation semantics": the three values, which is default for new grammars, that pre-3.12 grammars are pinned to `SPO-simple` and asked to resave, and that `DPO` implies `checkDangling` | A |
| `mult=k:` host aspect (gh #896) | `basics.md` §Graphs | new section | "Parallel edges": `mult=k:label` on binary edges, flags and `let:` initialisers (`mult=2:let:f=5`); constant `k >= 1`; host graphs only, not rules; only under `SPO-multi`/`DPO`; `(x2)` display suffix | A |
| Rule-side multigraph behaviour | `basics.md` §Rules | extend | Distinct role edges are distinct copies: `use:a`+`new:a` creates a second copy, `use:a`+`del:a` deletes one of two | A |
| DPO eraser injectivity / identification condition | `basics.md` §Injectivities; `advanced.md` §System properties | extend | Under DPO, eraser edges match injectively and eraser nodes may not be identified with other matched nodes, across quantification levels | A |
| Settings resources (new resource kind) | `io.md` §Grammars on disk | new section + table row | `.properties` files in a folder named after their schema (`explore/`, `ecore/`), or a top-level `<schema>.properties`; schema-checked with line-precise errors; enable/disable from the resource list, active one in bold; `system.properties` and `system/` are reserved | A |
| Exploration stored as an `explore` settings resource, referenced by the `exploration` grammar property | `verification.md` §Exploration in the Simulator | rewrite | The Simulator always runs the grammar's saved exploration; no transient per-session exploration; legacy `explorationStrategy` still read and migrated on first save; errors in a stored configuration are grammar errors | A |
| Rebuilt exploration dialog (gh #865) | `verification.md` §Exploration in the Simulator | rewrite | One row per key, selector over settings resources (selection also activates), Save/Save As/Revert, Restart/Continue enabled only for a consistent saved configuration; `collapse`, `algebra`, `persistence` may not change on Continue | A |
| Exploration key table now 14 keys | `verification.md` §The exploration configuration | rewrite table | Add `seed` (`auto*`/`value:<long>`); `frontier` is `complete*`/`single`/`beam:<n>`; `bound` gains `initial`, and is `none*`/`initial`/`cost`/`size`/`nodes`/`edges`/`upto`/`include`; `count` value form is `value:<n>` | A |
| New exploration features | `verification.md` §The exploration configuration | extend | `persistence=none` (no state storage; needs a finite unfolding or depth bound), `shape=trace`, `next=random`, `frontier=beam`, `bound=initial`; goals unified into `condition`/`fires`/`any`; formula parser has conventional precedence and accepts `a->b` | A |
| Depth-bound semantics aligned | `verification.md` §The exploration configuration | extend | Depth bounds mean the same under BFS and DFS (max depth of added states); bounded BFS reaches one level deeper than in 7.x | A |
| yFiles backend and add-on (gh #909) | new chapter or `introduction.md` §The GROOVE tool set | new section | Two interchangeable backends, yFiles default where present; add-on zip into the extension directory (`%APPDATA%\GROOVE\extensions`, `~/Library/Application Support/GROOVE/extensions`, `~/.groove/extensions`, `-Dgroove.extensions.dir`); install offer at first start of a release, View > yFiles add-on; Options > Graph backend applies at next start | A |
| Seven yFiles layout algorithms | same as above | new section | hierarchic, organic, orthogonal, tree, balloon, circular, radial join the layout menu | A |
| FlatLaf look and feel | same as above | new section | HiDPI, light and dark themes, replaces JGoodies | A |
| Platform-native installers | `introduction.md` §Downloading and installing | rewrite | `.msi`/`.dmg`/`.deb` with bundled runtime (no Java needed), start menu group; zip still needs Java 21+; installers are unsigned | A |
| Current release is 8.0.0 | `introduction.md`:65; `manual.md` table | rewrite | Version bump and *Checked against* column | A |
| `regExpMatching` (`faithful`*/`sloppy`, gh #900) | `advanced.md` §Regular expressions + property table | new section | A composite regular expression must be witnessed by a path surviving the rule's own erasures; `sloppy` restores the old behaviour; only relevant under DPO | B |
| `matchBound` (default 10000, `0` disables, gh #784) | `advanced.md` property table; `verification.md` | extend | Exceeding it halts exploration gracefully with the state flagged; Generator/ModelChecker fail cleanly instead of reporting a truncated state space as complete | B |
| Format-error severities (gh #885, #904, #908) | `basics.md` or new short section | new section | Errors block, warnings do not; warnings listed in their own colour, tabs/lists/cells decorated orange, printed by the Generator before exploring; first warnings are never-applicable rules and unknown names in `ruleEnabling` | B |
| Ecore import/export reimplemented | `io.md` §Ecore/EMF exchange | rewrite | No conceptual layer; multiplicities become `out=lo..hi:`, containment `part:`, abstract classes and enums `abs:`/`sub:`; options (`ordering`, `useIdentifiers`, per-element overrides) live in an `ecore` settings resource and are offered in a dialog at import and export; EMF 2.41 | A |
| `ecoreOrdering`/`ecoreUseIdentifiers` gone | `advanced.md` property table; `io.md`:77-78 | remove | Replaced by the `ecore` settings resource keys | A |
| Generator `-seed`, system property `groove.randomSeed`, key `seed` (gh #897) | `verification.md` §The Generator | extend | One master seed for all intentional randomness; generated seed logged, effective seed stored in the GTS and saved LTS, shown in the LTS status line | B |
| `-log level[:subsystem]` on all CLI tools (gh #891) | `verification.md` §The Generator | extend | Levels trace..error to stderr, optional subsystem (e.g. `explore.timing`), repeatable; distinct from `-l <dir>` | B |
| `-s`/`-a` status and tightenings | `verification.md`:71 | rewrite | "Legacy shorthand, kept indefinitely" rather than "deprecated"; a note prints the equivalent `-x`; `cycle` only with the LTL strategies, `none` with a result count rejected; `-r N` now also works with LTL and state explorations | B |
| Imager `-b <backend>` | `io.md` §The Imager | extend | Selects the rendering backend, falls back with a warning | B |
| Headless exporter registry | `io.md` §Importing and exporting | extend | `.fsm` and `.gxl` added; missing extension defaults to `.gxl` (LTS) or `.gst` (states); unknown extension is an error; text resources exportable; the rendering exporters (`.png`, `.tikz`, …) are GUI-side and not offered by a headless Generator | B |
| CTL always explores the full state space (gh #863) | `verification.md` §Model checking in the Simulator | rewrite | The caveat that a CTL verdict is only as complete as the exploration is obsolete; weak-until and release verdicts corrected | A |
| LTL counterexamples are lassos (gh #484); `transitionLabel` respected (gh #855) | `verification.md` §Model checking | extend | Counterexample is prefix plus cycle built from the product transitions actually taken | B |
| Control: dead location variables, expression arguments (gh #561) | `control.md` §Variables and arguments | extend | States no longer kept apart by out-parameter values that are never read again; `r(x - 1)` works | B |
| Recipe call edge cases | `control.md` §Procedures | extend | An undefined in-argument makes the call inapplicable; an out-parameter whose node is deleted later is undefined, rendered `_` | B |
| Priority/control conflict (gh #756) | `control.md` §Calls; `basics.md` §Rule properties | extend | "Explicit call of prioritised rule/recipe not allowed", but only when the grammar's actions carry more than one distinct priority | B |
| Duplicate node ids in host graphs merge (gh #780) | `basics.md` §Graphs or `advanced.md` node identifiers | extend | Nodes with the same id are merged with the union of their edges; rule graphs keep the error | B |
| Quantifier levels on `test:`/`let:` edges (gh #725) | `advanced.md` §Named nesting levels | extend | `use=q:test:expr` is honoured (it used to parse and be dropped) | B |
| `disabledRules` converted to `ruleEnabling` (gh #908) | `advanced.md` property table note | extend | Pre-7.4.0 grammars convert on load instead of being silently ignored | C |
| Determinism across JVM runs (gh #888, #894) | `verification.md` §The exploration configuration | extend | Exploration order is now identical across runs; affects the wording of `linear`/`random` | B |
| Text exports UTF-8 with LF; `.aut` quote round-trip; empty `.aut`/graphless `.gxl` reported as I/O error (gh #421) | `io.md` §Importing and exporting | extend | One sentence each | C |
| LTS export as control program (gh #861) | `io.md` export table note | extend | Node-valued in-parameters render as `_`, so the program compiles | C |
| Saving large state spaces (gh #854) | `io.md` §Saving the transition system | extend | Much lower memory; output unchanged | C |
| String escape handling | `basics.md` §Rule label syntax | extend | The escape character is itself escaped as documented, escapes processed left to right; re-check the `'\\?\''` example | C |
| Parallel remark edges merged | `basics.md` §Remarks | extend | Merged into one multi-line remark, line order preserved | C |
| Editor improvements (gh #913, #336, #701, #843, #853) | no home (no editor chapter) | new section or skip | Single undo step for create-and-label, own clipboard, offset paste, line-style/bend behaviour, bidirectional merge in preview, find/replace on untyped labels, case-only rename | C |
| Display improvements (gh #858, #867, #878, #879) | no home | new section or skip | Sub-level match emphasis, user quantifier names in the level tree, ordered label tree, type-display fixes | C |
| `valueOracle=dialog` and Prolog `show_graph` are Simulator-only | `advanced.md` `valueOracle` note; `verification.md` §Prolog | extend | Headless tools report them as unavailable instead of popping a dialog | C |
| **Removed:** `remote` strategy | `ref-exploration.md`:40 | regenerate/remove | Disappears with the page; see §4 | A |
| **Removed:** `ModelChecker -ltl` (gh #727) | `verification.md`:126 | rewrite | Remove from the option list; LTL runs through the Generator's `ltl` strategies | A |
| **Removed:** conceptual model layer | `io.md`:75 | rewrite | See Ecore row above | A |
| **Removed:** `parallelEdges`, `ignoreRegExp` | `advanced.md`:292, 325 | rewrite | Replaced by `semantics` and `regExpMatching`; both interim names are still converted on load | A |
| **Removed:** RETE engine, `minimax`, JGoodies, args4j | — | none | Verified: the manual never mentions RETE, `minimax`, a matcher-engine choice, or the look-and-feel library, so nothing to delete | — |
| API relocations | — | none | Programmatic-user material; the manual documents no Java API | — |

## 3. Statements in the current manual that are now wrong

Page:line refer to the files as they stand in `manual/`.

- `introduction.md:65` — "the current release is 7.5.3, which requires Java 21 or higher". Both
  halves need repair: 8.0.0, and the installers bundle a runtime.
- `introduction.md:41` — "determined by a selectable *exploration strategy*". It is now a
  configuration over fourteen keys, stored in the grammar.
- `introduction.md:53` — Generator "options are set through command-line parameters". Also by
  the grammar's `explore` settings resource.
- `introduction.md:61` — "available as a runnable jar in the `bin` subdirectory". Still true of
  the zip, but the installers put the five tools in a start menu group.
- `basics.md:128` — "In general, rules are *not* matched injectively". Unqualified; under
  `semantics=DPO` erasers match injectively.
- `basics.md:43-45` — the Graphs section assumes simple graphs throughout; parallel edges are
  now the default for new grammars.
- `advanced.md:292` — the property table row "`parallelEdges` | `false` | Allows parallel edges
  in host graphs (multigraphs instead of simple graphs)". The key no longer exists.
- `advanced.md:325` — "**Parallel edges.** Alternatively, setting `parallelEdges` makes host
  graphs *multigraphs*". Same.
- `advanced.md:321` — "Setting `checkDangling` mimics this behaviour [DPO]". Incomplete:
  `semantics=DPO` is the real DPO setting and implies `checkDangling`.
- `advanced.md:323` — the `checkCreatorEdges` explanation is simple-graph-only ("adding an edge
  that is already present leaves the graph unchanged"); 8.0.0 re-pinned the wording for
  multigraphs, and gh #904 adds a compile-time warning for rules that can never fire.
- `advanced.md:305` — "`exploration` | (empty) | Default exploration configuration for this
  grammar". It now holds the *name of a settings resource*, not a configuration.
- `advanced.md:312-313` — `ecoreOrdering` and `ecoreUseIdentifiers` rows. Both gone; the keys
  are `ordering` and `useIdentifiers` in an `ecore` settings resource.
- `advanced.md:285-314` — the table is missing `semantics`, `regExpMatching`, `matchBound`, and
  the note that `explorationStrategy` survives only as a deprecated fallback.
- `verification.md:27` — "the *exploration dialog* … lets you compose a custom exploration …
  A grammar can record its default exploration in the `exploration` system property, using the
  same `key=value` vocabulary". Wrong on both counts after the settings-resource rework.
- `verification.md:33-47` — the key table: no `seed` row; `frontier` values given as
  "`complete`*, `single`, *n*" where the value name is `beam`; `bound` lacks `initial`; `count`'s
  numeric form is `value:<n>`.
- `verification.md:65-69` — the Generator option list omits `-seed` and `-log`.
- `verification.md:71` — "The older options `-s` (strategy) and `-a` (acceptor) are still
  recognised but deprecated in favour of `-x`". They are kept indefinitely as legacy shorthand,
  with two tightenings.
- `verification.md:118` — "CTL checking operates on the explored state space, so its verdict is
  only as complete as the exploration". Obsolete: CTL always explores the full state space
  (gh #863). The adjacent sentence at :115 about checking "the *current* (partially explored)
  state space" needs checking against the actual Verify menu.
- `verification.md:126` — "`-ctl prop` and `-ltl prop` specify the properties to check".
  `ModelChecker -ltl` was removed (gh #727).
- `io.md:27-35` — the resource/extension table has no row for settings resources.
- `io.md:49` — layout "based on the internal representation of the JGraph rendering library".
  Layout data now lives in the backend-neutral `graph.layout`.
- `io.md:45-51` — the GXL section does not mention the `edgeids` attribute that records graph
  simplicity (simple graphs are written exactly as before).
- `io.md:75` — "The translation runs through an intermediate conceptual representation". That
  layer was removed; the porter is a fresh implementation.
- `io.md:77-78` — the `ecoreOrdering`/`ecoreUseIdentifiers` bullets.
- `io.md:66-71` — the rendering exporters are now GUI-side only; a headless Generator does not
  offer them.
- `io.md:92` — the Imager option list omits `-b`.
- `ref-exploration.md:21,23,31,40` — "generated from the GROOVE 7.5.3 source code"; "deprecated
  `-s` and `-a`"; `linear` described as "the same within one incarnation of Groove" (exploration
  is now deterministic across runs); and the `remote` strategy, which was removed.
- `manual.md:33-38` — *Checked against* says 7.5.3 for all six chapters; `manual.md:42` claims
  the reference pages "cannot drift out of date", which is only true if they are regenerated.

## 4. The generated pages

`make-refs.sh` needs `GROOVE_CLASSPATH` (a build) and `GROOVE_SRC` (the matching source tree,
read only for the version string) and runs `MakeRefs.java`, which writes four pages:

| Page | Generated from | 8.0.0 status |
|---|---|---|
| `ref-prefixes.md` | `AspectKind.getNodeDocMap`/`getEdgeDocMap` for `GraphRole` HOST/RULE/TYPE | Regenerate; picks up `mult=` automatically (help text and parameter docs are in `AspectKind`) |
| `ref-control.md` | `control.parse.CtrlDoc` item tree and tooltips | Regenerate |
| `ref-operators.md` | `algebra.Sort.getOperatorMap` / `algebra.Operator` | Regenerate |
| `ref-exploration.md` | `explore.StrategyValue`, `explore.AcceptorValue` | **Broken**: neither class exists in 8.0.0 — `MakeRefs.java` does not compile |

So three of the four pages only need a re-run against the 8.0.0 build; the fourth needs a
generator change. Two options for it:

1. Replace `writeExploration()` with a page generated from `explore.feature.ExploreKey` and the
   per-key value enums (`NextState`, `Successor`, `Frontier`, `Heuristic`, `Cost`, `Goal`,
   `Outcome`, `Shape`, `Count`, `Bound`, `Persistence`, `Collapse`, `Algebra`, `Seed`), titled
   "Reference: exploration keys". This is the natural successor page and would let
   `verification.md` stop duplicating the key table by hand — the place where it silently drifted.
2. Keep a legacy `-s`/`-a` table, but hand-written: the keywords now live in a `switch` in
   `explore/config/LegacySyntaxParser.java` with no name/description metadata, so the only
   machine-readable source is the picocli help text of `-s`/`-a` in `explore/Generator.java`.

Either way `_data/sidebars/manual_sidebar.yml:43-45` ("Strategies and acceptors") and the bullet
at `manual.md:47` need to follow. Note also that the `-x` help text in `Generator.java` is itself
out of sync with `ExploreKey` on three points (`frontier` beam naming, missing `bound=initial`,
no `seed`) — worth fixing in the code before generating anything from it.

`make-figures.sh` renders every `.gpr`, `.gst` and `.gty` in `manual/graphs.gps` twice through
the Imager (`-f svg`, plus `-e` for the edit view) into `images/manual/`; 66 SVGs today. Points
for the re-render:

- `graphs.gps/system.properties` is at `grammarVersion=3.2`, `grooveVersion=4.7.0+`, and still
  carries the pre-4.x keys `subtypes` and `enableControl`. Under 8.0.0 it is pinned to
  `semantics=SPO-simple` and asked to be resaved; a decision is needed (see §5).
- Figures will change even with the JGraph backend: edges between nodes overlapping on one axis
  are now drawn straight, embargo edges have a filled arrow head, and a negated atom such as
  `!a` is no longer rendered as a regular expression — the last affects
  `double-negation-display.svg` and `counting-display.svg` directly.
- `make-figures.sh` itself needs no change, unless the figures are to be rendered with the yFiles
  backend, which would mean adding `-b` and installing the add-on on the machine that renders.
- New figures for the multigraph section: `graphs.gps` already contains unused fixtures
  `multiple-edges-left.gst` and `multiple-edges-right.gst` (no page references them), which look
  like exactly the intended material; a `mult=k:` and a DPO example still have to be drawn.
- Dialog and menu material (exploration dialog, settings resource list, View > yFiles add-on) is
  screenshot work, outside `make-figures.sh`; `manual.md` already carries two hand-made
  screenshots, so there is precedent.

## 5. Open questions for the maintainer

1. Does 8.0.0 get a new chapter on the Simulator as an application (backend, layouts, look and
   feel, add-on installation), or do those go into `introduction.md`?
2. Resave `manual/graphs.gps` at grammar version 3.12 with `semantics=SPO-simple` (figures
   unchanged) or with `SPO-multi` (matches the new default)?
3. Render the 8.0.0 figures with JGraph or with yFiles?
4. Replace `ref-exploration.md` by a generated exploration-key reference, or keep a hand-written
   legacy `-s`/`-a` table?
5. If the key reference becomes generated, should `verification.md` drop its own key table and
   link instead?
6. Is `goal=graph:<name>` still supported and to be documented? It is in `ExploreKey` but absent
   from the goal-unification sentence of the change notes.
7. Do the add-on installation instructions belong in the manual or only on `installing.md`?
8. Should `installing.md`, `downloads.md` and `index.md` be updated in the same pass (installers,
   Java requirement, add-on)? They are outside `manual/`.
9. Add a short "Errors and warnings" section for the new severities, or fold it into an existing
   chapter?
10. `_data/glossary.yml` and `_data/definitions.yml` are still the untouched Jekyll theme samples
    (elephant, baseball). Populate with GROOVE terms, or leave them?
11. Should the manual document the extension directory and `-Dgroove.extensions.dir`, or treat it
    as installation detail?
12. Does the *Checked against* column move to 8.0.0 per chapter as each is revised, or in one go
    at release?
