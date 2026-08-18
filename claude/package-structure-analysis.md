# Package structure analysis (post-cleanup)

*Analysis of 2026-08-18 (Claude session), after the dependency cleanup
(`claude/dependency-analysis.md`), the automaton split and the FormatError
context refactoring had all landed. Method: jdeps `-verbose:package` and
`-verbose:class` over a fresh `target/classes` (`module-info` excluded),
plus per-package file and line counts over `src/main/java`. Raw data lived
in the session scratchpad; everything below is reproducible from jdeps and
`wc` alone.*

Main-tree size at the time of analysis: **982 files, 209,223 lines, 76
packages** (plus the root wrapper shims), connected by **701 directed
subpackage-level dependency edges**.

## Headline findings

1. **The cleanup worked.** On 2026-08-17, 53 of 66 subpackages were one
   strongly connected component. Now the largest SCC has 19 members, and
   those 19 are exactly the accepted rule-system cluster
   (`grammar`/`control`/`match`/`transform` and their subpackages) plus
   `io.store`/`io.graph`, which join it solely through the one whitelisted
   edge `grammar.model -> io.store`. The top-level layering holds with a
   one-entry whitelist, guarded by `LayeringTest`.
2. **Every remaining cycle is one of three accepted kinds**: (a) the honest
   rule-system knot, (b) idiomatic parent↔child pairs (`util ↔ util.parse`
   and the like), or (c) the gui's internal entanglement, which costs
   nothing since gui is a dependency leaf (decision of 2026-08-17,
   unchanged).
3. **Size problems live at file granularity, not package granularity.**
   The tree's package sizes are mostly healthy; the real outliers are
   individual files (`RuleModel.java` at 3,005 lines, `SystemStore.java`
   at 1,550). A handful of packages are worth a note (below), none worth
   restructuring on their own.

## Cycle structure

Eight SCCs of size > 1:

| size | members | verdict |
|---|---|---|
| 19 | rule-system cluster + `io.store`, `io.graph` | accepted (see below) |
| 13 | all of `gui.*` | accepted: leaf, ball of mud tolerated |
| 8 | `prolog.builtin` + its 6 per-domain children + `prolog` | parent↔child idiom |
| 4 | `util`, `util.collect`, `util.line`, `util.parse` | parent↔child idiom |
| 3 | `graph`, `graph.iso`, `graph.layout` | parent↔child idiom |
| 3 | `io.external`, `io.external.format`, `.format.ecore` | parent↔child idiom |
| 3 | `explore`, `explore.config`, `explore.util` | parent↔child idiom |
| 2 | `algebra`, `algebra.syntax` | parent↔child idiom |

The parent↔child pairs are the common Java pattern of a package and its
subpackages referring to each other (e.g. `util.Groove` using
`util.parse.FormatException` while `util.parse` uses `util.Fixable`).
Not worth policing.

### Inside the rule-system knot

Restricted to cluster-internal edges, 17 of the cluster's 18 subpackages
are one SCC (only `transform.criticalpair` sits cleanly on top). The hinge
is the `grammar` top package itself: everything depends on it, and it
depends back on `control`, `match` and `transform.oracle` through a short,
explicit list of class-level edges:

- `Grammar -> control.instance.Automaton` (a grammar contains its compiled
  control automaton)
- `Recipe -> control.Procedure`, `control.template.Template` (a recipe *is*
  a grammar action wrapping a control template)
- `Rule -> match.Prover`, `control.Binding`
- `RuleDependencies -> match.automaton.RegAut(Calculator)`
- `GrammarKey`/`GrammarProperties -> transform.oracle.*` (the value-oracle
  grammar property)
- `Signature`/`UnitPar -> control.CtrlType`/`CtrlVar`/`CtrlArg`
  (rule/procedure signatures speak control's type vocabulary)

Non-parent↔child mutual pairs inside the cluster:
`control ↔ grammar`, `control ↔ grammar.model`,
`control.instance ↔ grammar`, `control.template ↔ grammar`,
`control.parse ↔ grammar.model`, `control.graph ↔ control.template`,
`grammar ↔ match`, `grammar ↔ transform.oracle`,
`grammar.host ↔ grammar.rule`, `grammar.host ↔ grammar.type`,
`grammar.model ↔ grammar.rule`, `grammar.rule ↔ grammar.type`.

**Assessment: the knot is intrinsic and the one-layer model is honest.**
Rules and control are mutually recursive by design (recipes are actions;
control calls actions; rules carry the bindings the control automaton
needs), and the typed-element web `grammar.host ↔ grammar.rule ↔
grammar.type` reflects that host, rule and type elements are defined in
terms of one another. Layering the cluster internally would need half a
dozen inversions and still leave `Recipe ↔ Template` standing. Two
thinning options exist if it ever matters (e.g. for a finer module split):

- Move the control type vocabulary (`CtrlType`, `CtrlVar`, `CtrlArg`) down
  into `grammar`: signatures are a grammar-level concept, and this would
  cut the `Signature`/`UnitPar` edges plus part of `control ->
  grammar`'s counterweight. Needs a design look; the vocabulary is used in
  ~all of `control`.
- Invert `Grammar -> Automaton` and the oracle wiring the same way
  `Rule.getProver` and `RuleLabel.getAutomaton` were inverted. Mechanical
  but pointless while the cluster is one module anyway (gh #887 treats it
  as such).

Neither is recommended now.

### io in the big SCC

`io.store` and `io.graph` appear in the big SCC only because of the
accepted `grammar.model -> io.store` back-edge (GrammarModel ↔ SystemStore
editable-model/backing-store pair); `io.graph` is dragged in transitively
via `io.store -> io.graph -> grammar.*`. No new information, no action.

## Package sizes

Reference points: the median package has 8 files. The extremes:

| package | files | lines | note |
|---|---|---|---|
| `gui.jgraph` | 34 | 11,521 | largest by lines |
| `gui.display` | 24 | 11,011 | incl. two ~1.3k-line token-maker tables |
| `lts` | 40 | 8,386 | largest *flat* package (no subpackages) |
| `gui.dialog` | 26 | 8,316 | |
| `gui.tree` | 27 | 7,793 | |
| `grammar.model` | 26 | 7,534 | `RuleModel.java` alone is 3,005 lines |
| `grammar.aspect` | 12 | 7,534 | highest lines/file (~630) |
| `algebra` | 45 | 6,962 | families × sorts matrix |
| `gui.action` | 56 | 6,095 | most files; one action class each |
| `util` | 42 | 7,279 | the grab-bag, plus 9 subpackages |
| `match.plan` | 17 | 6,335 | |
| `graph.iso` | 7 | 4,993 | few large algorithmic files |

### Too large?

- **`gui.jgraph` / `gui.display` / `gui.dialog` / `gui.tree`**: the gui is
  half the codebase's bulk and already split by widget kind; the split is
  serviceable. `gui.display` carries the two machine-generated-style
  syntax-highlighting tables (`PrologTokenMaker`, `CtrlTokenMaker`, ~2.7k
  lines together) — moving them to a `gui.display.syntax` subpackage would
  be cosmetic. No action.
- **`lts`**: 40 files with no substructure, but the contents are one
  coherent domain (states, transitions, their stubs, match plumbing, GTS
  and listeners) with heavy mutual reference. A split (say `lts.stub`,
  `lts.match`) would produce parent↔child cycles without cutting real
  coupling. Leave unless it grows.
- **`gui.action`** (56 files) and **`algebra`** (45 files): high file
  counts from mechanical homogeneity — one class per Simulator action, one
  class per algebra-family × sort. Navigable by naming convention; fine.
- **`grammar.model`**: the package is fine; `RuleModel.java` is not. At
  3,005 lines it is the largest file in the codebase and contains the
  whole rule-graph → `Rule` translation. Extracting the converter tier
  (the way `TypeModel` has its `Converter` inner class, but as a separate
  class) is the single most useful size-motivated refactor available.
- **`grammar.aspect`**: four ~1k-line files (`AspectKind`, `AspectEdge`,
  `AspectContent`, `AspectGraph`); dense but each is one concept.
  Borderline; no action.
- **`util`**: the perennial grab-bag; 9 subpackages already carve out the
  coherent parts. Nothing actionable.

### Too small?

- **`resource.*`** (5 packages × one 27-line stub) and the **six root
  shims**: deliberate anchors; not real packages.
- **`io`** top level (2 files): a namespace holder for
  `io.graph`/`io.store`/`io.external`; `Groove.java` is a path/constant
  grab-bag. Harmless.
- **`gui.prolog`** (2 files, 90 lines): exists precisely so that
  GUI-dependent Prolog predicates do not put a `gui` dependency into
  `prolog`. Justified by the layering; keep.
- **`prolog.exception`** (2 files): could fold into `prolog`; purely
  cosmetic, not worth a commit.
- **`util.antlr`, `util.cache`, `util.io`, `control.graph`,
  `explore.result`, `prolog.util`, `graph.plain`** (3–6 files each): each
  has one clear job; folding them buys nothing and loses the label.
- **`io.store`** (2 files, 1,617 lines): the package is small; the file is
  big. `SystemStore.java` (1,550 lines) mixes bundle I/O, edit sessions
  and undo records — second candidate for a file-level split, after
  `RuleModel`.

## Recommendations

1. **No structural work is needed.** The layering is achieved, guarded, and
   every surviving cycle is either intrinsic or idiomatic. The whitelist is
   at its floor (one accepted entry).
2. The worthwhile follow-ups are **file splits, not package moves**:
   `RuleModel.java` first, `SystemStore.java` second. Both are independent
   of any dependency concern.
3. The rule-system knot can be thinned (CtrlType vocabulary down;
   `Grammar -> Automaton` inversion) if a finer module split is ever
   wanted; not before.
4. Optionally, `LayeringTest` could be extended to guard subpackage-level
   invariants (e.g. "only `grammar.model` may touch `io.store`"). Judged
   over-engineering for now: the top-level guard plus the shrunken SCC is
   enough to notice regressions.
