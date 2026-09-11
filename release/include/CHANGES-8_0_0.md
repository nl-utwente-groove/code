GROOVE 8.0.0: detailed change notes
===================================

This document expands the one-line entries of `CHANGES.md` for release 8.0.0,
which collects the changes since 7.5.3 (March 2026). It is organised by topic
rather than by date. GitHub issues are referenced as `gh #N`.

Graph visualisation backends and the yFiles add-on
-------------------------------

- The graph views of the Simulator, the editor, the Viewer and the Imager now run
  on one of two interchangeable visualisation backends (gh #909): the JGraph-based
  one of all previous releases, and a new one built on the commercial library
  yFiles for Java (Swing). Backends are discovered as services; a backend that
  fails to load is skipped, so GROOVE always starts with JGraph as fallback.
- The yFiles backend is not part of the standard release. It comes as a separate
  add-on (`groove-x_y_z-yfiles-addon.zip`, for non-commercial use only, see
  `YFILES-ADDON.md` inside), which is unzipped into GROOVE's new extension
  directory: `%APPDATA%\GROOVE\extensions` on Windows, `~/Library/Application
  Support/GROOVE/extensions` on macOS, `~/.groove/extensions` elsewhere, overridable
  with the system property `groove.extensions.dir`. Jars there are loaded at
  start-up; a jar built for another GROOVE version is skipped with a warning.
- The Simulator offers to download and install the add-on at the first start of a
  release version (once per version; never for snapshot builds; suppressed with
  `-Dgroove.addon.prompt=false`). The same is available at any time under View >
  `yFiles add-on`, together with installation from a downloaded file and removal.
  The Windows installer removes the add-on on uninstall and hence on upgrade.
- Where both backends are present, the Simulator's Options menu offers the choice
  (`Graph backend`, applied at the next start); yFiles is the default. Its seven
  layout algorithms (hierarchic, organic, orthogonal, tree, balloon, circular,
  radial) join the layout menu. The Imager's new `-b` option selects the backend
  to render with, falling back with a warning if the requested one is unavailable.
- Rendering was aligned between the backends: edges between nodes that overlap on
  one axis are drawn straight; embargo edges have a filled arrow head on both;
  edge labels honour the font visual on both, and a negated atom such as `!a` is
  no longer rendered as a regular expression. TikZ coordinates of Bezier edges may
  differ in the third decimal, as the interpolation is now GROOVE's own.

Installers and distribution
-------------------------------

- GitHub releases now carry platform-native installers built with jpackage:
  Windows `.msi`, macOS `.dmg` (Intel and Apple silicon) and Linux `.deb`, each
  with a bundled Java runtime, so no Java installation is needed. The start menu
  group lists Simulator, Generator, ModelChecker, Imager and Viewer. The
  installers are not code-signed; the release page explains how to get past the
  Windows and macOS warnings. The `-bin.zip` remains available and needs Java 21
  or newer.
- The Windows installer does not offer an install-folder chooser (the MSI would
  not remember the choice across upgrades); use the zip for a custom location.
- The zips now include the license (`LICENSE.txt`, plain text instead of
  Markdown) and no longer ship the reactor pom in `lib/`.
- The GROOVE jar is a proper Java module again (`nl.utwente.groove`): the four
  libraries that were never published by their upstreams (`gnuprologjava`,
  `ltl2buchi`, `groove-gxl`, and formerly `osxadapter`) are republished on Maven
  Central under the `nl.utwente.groove` group id, so the shaded build is gone and
  a fresh clone builds without setup.

Multigraphs and transformation semantics
-------------------------------

- Grammars can now be transformed as multigraphs, with parallel edges. The new
  grammar property `semantics` selects `SPO-simple` (the classic simple-graph
  SPO semantics of all previous releases), `SPO-multi` (multigraphs with
  delete-wins SPO) or `DPO` (multigraphs with the DPO identification and dangling
  conditions). `SPO-multi` is the default for new grammars; grammars saved before
  grammar version 3.12 are pinned to `SPO-simple` on loading and asked to be
  resaved. The interim properties `parallelEdges` and `ignoreRegExp` of
  development builds are converted automatically.
- The new host-graph aspect `mult=k:` declares an edge (or flag, or field
  initialiser such as `mult=2:let:f=5`) to stand for k parallel copies; it is only
  allowed under `SPO-multi` or `DPO`. Rules grow and shrink bundles copy by copy
  (`use:a` + `new:a` creates a second copy, `use:a` + `del:a` deletes one).
  State display and saved states show and reload parallel copies faithfully
  (`(x2)` label suffix); GXL files record graph simplicity through the native
  `edgeids` attribute, so simple graphs are written exactly as before.
- Under DPO, eraser edges match injectively and eraser nodes may not be
  identified with other matched nodes, also across quantification levels.
- The new grammar property `regExpMatching` (gh #900) controls whether a
  composite regular expression must be witnessed by a path that survives the
  rule's own erasures. Under the default `faithful`, the matcher re-matches
  against the erased edges of the same or an enclosing quantification level; under
  `sloppy`, any witness path counts, as before. The former compile-time error for
  erased witnesses is only reported for erasers on unrelated levels now.
- The implicit creator NACs of `checkCreatorEdges` and `rhsIsNAC` are
  documented and tested rather than changed (gh #901): a rule whose created edge
  has a content-equal LHS twin under these properties can never fire, and now
  gets a compile-time warning saying so (gh #904).
- String values: the escape character in quoted strings is now itself escaped as
  documented, and escapes are processed left to right. Only strings containing two
  consecutive backslashes or a backslash before a quote change meaning; values
  ending in a backslash could not be reloaded at all before.
- Parallel remark edges between the same node pair are merged into one multi-line
  remark, preserving line order (they used to be reordered alphabetically).

Exploration
-------------------------------

- The exploration is now a configuration over fourteen keys (`next`, `successor`,
  `frontier`, `heuristic`, `cost`, `goal`, `outcome`, `shape`, `count`, `bound`,
  `persistence`, `collapse`, `algebra`, `seed`), stored as a *settings resource*
  of schema `explore` inside the grammar (a properties file in the `explore/`
  folder of the `.gps`) and referenced from the new grammar property
  `exploration`. The legacy `explorationStrategy` property is still read as a
  fallback and migrated on the first save from the exploration dialog. Errors in
  a stored configuration (unknown rule, malformed formula) are reported as grammar
  errors instead of silently falling back to the default exploration.
- The exploration dialog was rebuilt around saved settings: one row per key, a
  selector to switch between settings resources (which also activates them),
  Save/Save As, Revert, and Restart/Continue buttons that are enabled only for a
  consistent, saved configuration, with in-dialog error reporting. Settings
  resources can be enabled and disabled from the resource list, and the active
  one is shown in bold; deleting or renaming it keeps the reference consistent.
- The Simulator's exploration is always the grammar's saved exploration; the
  transient per-session exploration is gone. Changing the exploration reference
  no longer resets the state space, so a partial exploration can be continued
  under new settings. The features that shape the GTS itself (`collapse`,
  `algebra`, `persistence`) may not change on Continue.
- New exploration features, reachable through the configuration only:
  `persistence=none` explores without storing states (memory is reclaimed for
  fruitless subtrees; needs a finite unfolding or a depth bound); `shape=trace`
  presents results as traces; `next=random` takes states from the frontier at
  random; `frontier=beam` bounds the frontier size; `bound=initial` explores only
  the start state; `bound=nodes`, `bound=edges`, `bound=upto` and `bound=include`
  express size and rule-condition bounds. Goals were unified into `condition`
  (a propositional formula over rule names), `fires` and `any`; the formula
  parser now has conventional operator precedence and accepts `a->b` without
  spaces. Depth bounds mean the same under BFS and DFS now (maximum depth of the
  added states, so a bounded BFS reaches one level deeper than before).
- Configuration-based exploration runs on a new parametric engine that replaced
  the legacy strategy classes; state and transition counts and exploration order
  were pinned identical to the legacy path.
- Generator: the new `-x <config>` option takes the same `key=value` form as a
  settings resource; `-D exploration=...` works as well. The `-s`, `-a` and `-r`
  options are kept indefinitely as legacy shorthand (a note prints the equivalent
  `-x`), with two tightenings: the cycle acceptor only combines with the LTL
  strategies, and `none` with a result count is rejected. A bare `-r N` now also
  works with the LTL and state explorations.
- All intentional randomness derives from one master seed, set with the new
  Generator option `-seed`, the system property `groove.randomSeed` or the new
  exploration key `seed` (gh #897). A generated seed is logged; after an
  exploration that actually drew randomness, the effective seed is stored in the
  GTS, carried into saved LTS files and shown in the Simulator's LTS status line,
  so any run can be reproduced. The LTL random successor choice draws fresh
  values per choice again (it had degenerated into a fixed function).
- Exploration is now identical across JVM runs (gh #888, gh #894): rule and event
  hash codes no longer depend on object identity, class or enum identity hashes
  were eliminated from the exploration path, the match application order is
  canonical, and a cross-JVM determinism test guards the property.
- Too many matches in one state no longer crash the tool (gh #784). The former
  hard-wired limit of 10000 is the default of the new grammar property
  `matchBound` (`0` disables it); exceeding it halts exploration gracefully with
  the offending state flagged, the Simulator keeps running, and the Generator and
  ModelChecker fail with a clean message instead of reporting a truncated state
  space as complete.
- Control: location variables are restricted to live ones, so states that differ
  only in an out-parameter value that is never read again are no longer kept
  apart, and a variable inside an expression argument such as `r(x - 1)` no
  longer crashes exploration (gh #561). A recipe called with an undefined
  in-argument is inapplicable, and a recipe out-parameter whose node is deleted by
  a later step is undefined (rendered `_`); out-values computed by the final step
  are reported again.
- The "Explicit call of prioritised rule/recipe not allowed" error is reinstated,
  but only when the grammar's actions carry more than one distinct priority
  (gh #756). `CtrlLoader.changePriority` was repaired (gh #733, GUI wiring still
  outstanding).

Grammars, rules and the editor
-------------------------------

- Host graphs may contain duplicate node ids; the nodes are merged, with the union
  of their edges (gh #780). Rule graphs keep the duplicate-id error.
- Explicit quantifier levels on role-prefixed `test:` and `let:` edges
  (`use=q:test:expr`) are honoured; they used to parse and be dropped (gh #725).
- Calls and imports of units in disabled or erroneous control programs and rules
  get informative errors instead of "Unknown unit" (gh #560).
- Format errors carry a severity (error, warning, info) and only errors block
  (gh #885). Warnings survive a successful grammar build, are listed in the
  error list in their own colour, decorate tabs, resource lists and graph cells
  in orange, and are printed by the Generator before exploring. The first
  warnings are the never-applicable rules of gh #904 and unknown rule names in
  `ruleEnabling` (gh #908).
- The legacy `disabledRules` property of grammars saved before 7.4.0 is converted
  into `ruleEnabling` on loading; until now it was silently ignored, re-enabling
  (often deliberately broken) rules whose errors then blocked the grammar
  (gh #908). Loading a grammar from before version 3.2 keeps its implied start
  graph on upgrade instead of ending with "No active start graph".
- Editor: creating a cell and giving it its first label is one undo step
  (gh #913), selection no longer produces empty undo steps, and reconnecting an
  edge end is undoable again. Cut, copy and paste use GROOVE's own clipboard, so
  a paste returns exactly what was copied; pastes land south-east of the originals
  and step further on repeat. Line-style changes add a bend only for curved
  styles, act on the selected edges only and are a single edit; Add/Remove Point
  act at the pointer and pick the nearest segment. Control-dragging a label no
  longer crashes (gh #843).
- Editor: bidirectional edges are merged in preview mode (gh #336); find/replace
  offers labels that fail to type and works when all labels are erroneous
  (gh #701); renaming a resource to a case variant of its name is allowed
  (gh #853); host-graph multiplicity and containment violations are highlighted
  in the graph.
- Display: selecting a match emphasises sub-level matches as well (gh #858);
  quantifier names given by the user are shown in the level tree; the label tree
  is ordered (types, flags, edge labels, each alphabetically) also for untyped
  grammars; subtype edges no longer show a spurious arrow label (gh #878); the
  Type display's label tree no longer shows occurrence counts (gh #879); the
  quantification-level tag of an attribute edge is placed on the edge name
  (gh #867); state labels containing a recipe render correctly.
- A click anywhere on a row of the resource lists selects the resource, and the
  list keeps keyboard focus across the display switch it triggers.

Verification
-------------------------------

- CTL checking always explores the full state space, both in the ModelChecker
  CLI and in the Simulator's "Check CTL property (full state space)" action; a
  partial exploration saved with the grammar used to give wrong verdicts, and the
  GUI action silently did nothing when open states remained (gh #863). The
  verdicts of weak-until and release formulas were wrong and are corrected.
- LTL counterexamples are recorded as an ordered lasso (prefix plus cycle) built
  from the product transitions actually taken, so the reported and highlighted
  counterexample no longer contains spurious transitions (gh #484). LTL
  propositions respect a rule's special `transitionLabel` (gh #855).
- The DFA minimisation and equivalence computation behind regular-expression
  labels was broken (states were almost never merged, dead states remained, a
  quotient bug could drop matches); it is replaced by partition refinement with
  dead-state pruning (gh #892).
- The edge-bound condition accepts `type:` and `flag:` prefixes and reports
  ambiguous bare names instead of resolving them arbitrarily (gh #732).

Import and export
-------------------------------

- Ecore import and export are back, reimplemented from scratch after the removal
  of the 2012 conceptual-model layer that had been non-functional for a decade:
  an `.ecore` metamodel imports as a type graph and an `.xmi` instance model as a
  host graph, and both export again. Multiplicities become `out=lo..hi:`,
  containment `part:`, abstract classes and enums `abs:`/`sub:`. Options
  (`ordering`, `useIdentifiers`, per-element overrides) live in an `ecore`
  settings resource and are offered in a dialog at import and export time. The
  EMF dependency is at 2.41.
- Settings resources are a new resource kind: schema-checked Java properties
  files inside the `.gps`, in folders named after their schema (`explore`,
  `ecore`), with a display, a template for new resources and line-precise
  errors. The grammar properties file is now `system.properties`; the legacy
  `<grammar name>.properties` is migrated on the next save.
- Saving a large state space needs much less memory (gh #854): the LTS is written
  through a lazy view instead of a materialised copy, the `.aut` writer keeps no
  per-node map and the GXL writer streams. On car-platooning start-05, `.gxl`
  export fits in 725 MB where it needed 1000 MB. Output is unchanged.
- Text exports (`.aut`, `.gxl`, `.fsm`, `.dot`, control programs) are written in
  UTF-8 with LF line ends on every platform. `.aut` labels containing quotes now
  survive a round trip. An empty `.aut` file or a `.gxl` file without a graph is
  reported as an I/O error instead of an exception (gh #421).
- Control programs exported from an LTS render node-valued in-parameters as `_`,
  so the exported program compiles (gh #861).
- The headless exporter registry gains `.fsm` and `.gxl` graph exporters; an
  output name without extension defaults to `.gxl` (LTS) or `.gst` (states), and
  an unknown extension is an error. Exporting a text resource (e.g. a control
  program) works; it threw a NullPointerException before. The rendering
  exporters (`.png`, `.tikz`, ...) are GUI-side and no longer offered by a
  headless Generator.

Command-line tools and logging
-------------------------------

- All command-line tools accept `-log level[:subsystem]` (repeatable) to switch on
  diagnostic logging per subsystem, through `System.Logger` (gh #891). Exploration
  timing is the subsystem `explore.timing` (formerly a compile-time flag).
- The Generator's `-l` run log carries the outcome and "saved as" lines at its
  tail, no longer writes to standard output, and no longer appends a `gc.log`
  found in the working directory (a JDK 8 remnant).
- Headless tools no longer pop Swing dialogs: `valueOracle=dialog` and the Prolog
  builtin `show_graph` are Simulator-only and reported as such; the look-and-feel
  is not initialised in headless runs; the English locale is pinned for all tools.
- `ModelChecker -ltl` is removed: it was parsed but never used (gh #727). LTL
  checking runs through the Generator's `ltl` strategies.
- Command-line parsing moved from args4j to picocli.

Simulator look and behaviour
-------------------------------

- The look-and-feel is FlatLaf (HiDPI support, light and dark themes) instead of
  the unmaintained JGoodies Looks; several FlatLaf-specific tweaks keep tabs,
  tables and check-box trees readable.
- macOS Cmd-Q runs GROOVE's quit action (save prompt, settings sync); the old
  `OSXAdapter` registered nothing on current JDKs, so the JVM exited at once and
  unsaved editors were lost. A failure while saving user settings no longer
  blocks shutdown.
- Fixed: a hang of the event thread for show/hide filters with a regular
  expression containing `+` or `*`; an NPE and stale rule tree when loading a
  second grammar while a state with matches was displayed; a crash of the state
  list on grammars whose exploration is unstored; the wrong display staying up
  after selecting a graph; various NPEs found through the JDK nullness
  annotations (gh #881).

Performance
-------------------------------

- `Rule.getProver` never cached its result, so the search plan was rebuilt for
  every state instead of once per rule; fixed.
- Multigraph mode (gh #905, gh #906): created edges are drawn from a
  content-indexed pool, so rule events are shared across applications again
  (3326 to 88 events on the append-4-list-8 sample); isomorphism checking
  recognises graphs up to parallel-edge identity and assigns certificates per
  bundle instead of per copy, avoiding a factorial backtracking search.
- Symbolic (`term`) and point algebras: int-to-string and int-to-real coercions
  built the wrong term or value; fixed.

Removed features
-------------------------------

- The RETE matching engine and its strategies (`rete`, `retelinear`,
  `reterandom`), never selectable from the dialog; the parent of the removal is
  tagged `rete-final`.
- The `remote:host` exploration strategy and the symbolic transition system it
  sent to a defunct server (tag `sts-final`), and the `minimax` game strategy
  (gh #890); both now fail with an explanatory error.
- The 2012 conceptual-model layer (`io/conceptual`) with its CONFIG resource
  kind, replaced by the new Ecore porter.
- `ModelChecker -ltl` (see above), the JGoodies, OSXAdapter, args4j, xerces and
  xml-resolver dependencies (XML parsing now uses the JDK's parser), the
  "yFiles edition" distribution that briefly existed during development, and
  assorted dead code (`graph.multi`, two unused certificate strategies, several
  `util.collect` classes).

API changes for programmatic users
-------------------------------

All relocations are without deprecation.

- `transform.Transformer` is `explore.Transformer` and lost `setStrategy` and
  `setAcceptor`; use `setExploreType`. `Acceptor` became `explore.result.ResultCollector`
  with an injected `Goal`; `ExploreType.getBound` is `getResultCount`.
- Dependency-layering cleanup: `grammar.QualName` and `grammar.ModuleName` to
  `util`; `io.FileType`, `io.FileUtils` and `io.ExtensionFilter` to `util.io`;
  `explore.ExploreResult` and `explore.util.LTSLabels` to `lts`;
  `transform.Proof` and `grammar.Prover` to `match`; `transform.Phase` to `lts`;
  the delta classes from `transform` to `grammar.host`; `explore.Verbosity` to
  `util.cli`; `graph.GraphProperties` to `grammar.ResourceProperties`; the
  static grab-bag `util.Groove` is dissolved into `io.Groove`, `util.Resources`,
  `util.Strings`, `util.Trace` and others; `io.Util` into `util.Unicode` and
  `io.FileUtils`. The `explore` package is reorganised into `explore.engine`,
  `explore.feature`, `explore.config` and `explore.verify` (formerly
  `explore.strategy`).
- The top-level `automaton` package is dissolved into `grammar.rule`,
  `grammar.type` and `match.automaton`; `RuleLabel.getAutomaton` is replaced by
  `RegAutCalculator.instance()`.
- `RuleModel` is split into a package-private `RuleCompiler` with per-phase
  classes; `Rule`'s condition-matching methods are gone in favour of
  `Rule.getProver()`.
- `FormatError` carries a severity and an opaque context set instead of typed
  fields (`getContext(Class)` replaces `getGraph`/`getControl`/`getProlog`).
- `util.Properties.ValueType` is a generic identity token; the per-type `Entry`
  accessors collapse into `value(ValueType<V>)`.
- The GUI view layer is backend-neutral: `gui.view` holds `ViewCell`/`ViewVertex`/
  `ViewEdge` (formerly `JCell` etc.), `GraphCanvas`, `GraphViewController` and
  `GraphViewModel`; `JGraphPanel` is `GraphPanel`; `ExportKind.JGRAPH` is
  `CANVAS`. Layout data lives in the Swing-free `graph.layout`.
- `Fixable.testFixed(boolean)` is replaced by `testMutable()`; `AspectLabel` and
  `Pair` are immutable. `SystemStore` no longer extends `UndoableEditSupport`.
- `module-info` now requires `java.logging`; the module exports changed with the
  package moves.

Resolved GitHub issues
-------------------------------

gh #336 (bidirectional edges in preview), #421 (`.gxl` without graph), #484 (LTL
counterexample), #560 (errors for disabled units), #561 (control variables and
expression arguments), #701 (find/replace), #725 (quantifier levels on
test/let edges), #727 (`ModelChecker -ltl`), #732 (edge-bound labels), #733
(recipe priority, partial), #756 (priority/control conflict), #780 (duplicate
node ids), #784 (match bound), #843 (label drag crash), #853 (case-only rename),
#854 (memory when saving), #855 (`transitionLabel` in LTL), #858 (sub-level match
emphasis), #861 (LTS export as control), #863 (CTL on partial explorations),
#865 (exploration dialog), #867 (level tag placement), #878 (subtype arrow
label), #879 (occurrence counts), #881 (JDK nullness annotations), #885 (error
severity), #888, #894 (determinism across runs), #890 (minimax removal), #891
(diagnostic logging), #892 (DFA minimisation), #897 (random seed), #900
(erased regular-expression witnesses), #901, #904 (creator NACs), #905, #906
(multigraph performance), #908 (`disabledRules`), #909 (visualisation backends),
#913 (editor undo).
