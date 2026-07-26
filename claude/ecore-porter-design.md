# Design: the Ecore porter (Phase 3 of the io refactoring)

*2026-07-26. Companion to `io-refactor-plan.md`. Sources: the retired
`io/conceptual` layer (readable at `git show f85d56128^:...`), whose encoding
this design simplifies, and the current aspect/type machinery.*

## Goals and non-goals

Import (`.ecore` → type graph, `.xmi` → host graph) first; export second.
Supported: EClasses (incl. abstract/interface), eSuperTypes, EAttributes over
the standard EMF datatypes, EReferences with multiplicities, containment,
eOpposites (structurally), EEnums. Not supported in v1: EOperations, generics,
EAnnotations (except as remarks), cross-resource proxies, `EFeatureMapEntry`;
ordering and uniqueness of many-valued features are handled only through the
`ordering` option below.

## Principles

1. **Direct mapping, no intermediate representation.** EMF objects to
   `AspectGraph`s and back, built with typed aspects (`AspectGraph` API /
   plain graphs fed to `AspectGraph.newInstance`), never by concatenating
   label strings for re-parsing.
2. **Use native aspects instead of 2012 emulations.** `out=lo..hi:` replaces
   the nullable/`Nil` machinery; `part:` expresses containment; `sub:`+`abs:`
   express enums; no meta-graph sidecar, no generated constraint rules
   (deferred to Phase 4), no intermediate nodes except for ordering.
3. **Round-trip metadata rides on the graphs, not in extra resources.**
   Package data (name, nsURI, nsPrefix), the enum/interface classification and
   the opposite pairing are recorded as graph properties (`GraphInfo`) of the
   type graph, so a re-export reproduces the metamodel without heuristics.
   (Implementation must verify these persist through GXL; if not, fall back to
   a dedicated `rem:` node with a fixed prefix.)
4. **Errors via `FormatErrorSet` on the graph elements** (surfaced in the GUI
   error panel); `PortException` only for I/O-level failure (unreadable file,
   unresolvable metamodel). `FormatError`s are reserved for input the encoding
   cannot represent *at all* (a reference to a class outside the imported
   packages, an attribute without a data type). The approximations the encoding
   makes by design are **silent**: GROOVE has no warning severity, and any
   error on a graph makes the resource fail to compile
   (`TypeModel.Converter.convert` rethrows the source graph's errors), so a
   diagnostic for configured, documented behaviour would render every import of
   a normal metamodel useless.

## Metamodel encoding (.ecore → type graph)

| Ecore | GROOVE |
|---|---|
| EClass `C` | node `type:C`; `abs:` if abstract or interface |
| eSuperTypes | `sub:` edges (subtype → supertype) |
| EAttribute `a : EInt` `[1..1]` | self-loop `int:a` |
| EReference `r : D [lo..hi]` | edge `out=lo..hi:r` to `type:D` (`out=` omitted for `0..*`; `hi=-1` → `*`) |
| containment | `part:` prefix on the reference edge (native semantics: `in=0..1` + acyclicity — exactly Ecore's) |
| eOpposite | both edges emitted normally; pairing recorded in graph properties (no structural enforcement in v1) |
| EEnum `E` | `type:E` with `abs:`; literal `L` → `type:E$L` with `sub:` to `type:E` |
| EDataType (custom) | mapped to `string`; silent — the data type is recorded in the graph properties, so the approximation is reversible |

Datatype table (as the old code, plus the gaps filled): `EBoolean(Object)` →
`bool`; `EInt/ELong/EShort/EByte(+Objects)/EBigInteger` → `int`;
`EFloat/EDouble(+Objects)/EBigDecimal` → `real`; `EString/EChar(acterObject)`
→ `string`; `EDate` and anything else → `string` + warning.

**Naming.** GROOVE type labels are unqualified Java-style identifiers (`$` is
legal, `.` is not). Default: the simple EClass/EEnum name, repaired via
`IdValidator` if needed. On a cross-package collision, the colliding names are
qualified as `pkg$Name` (recursively up the package path until unique) —
deterministic, no configuration. Enum literals are `E$L` (the `$` echoes the
old encoding and keeps literals visually grouped under their enum).
Multiplicity of mandatory attributes is *not* enforced (GROOVE does not check
attribute-edge presence); noted limitation, Phase 4 territory.

**Ordering** (`ordering` option, default `none`):
- `none` — many-valued features become plain edges; order is not represented,
  and non-unique features collapse duplicates (host graphs are simple). Both
  losses are silent: this is exactly what the option selects, and `ordered` is
  the Ecore default for many-valued features, so flagging it would mean
  flagging nearly every reference of every metamodel. Choosing `index` is the
  way to keep the information.
- `index` — an ordered or non-unique feature `r` gets the old intermediate
  encoding, minus the parts that multiplicities now cover: nodified-edge node
  `type:C$r` with `edge:"r"` pattern, `in=1:r` edge from `C`, `out=1:val` to
  the target, `int:index` attribute (1-based in instances); `part:` moves to
  the `val` edge for containments.

## Instance encoding (.xmi → host graph)

- EObject → node `type:C` (the concrete class). The `useIdentifiers` option
  (default on) governs node identity: **on**, every object gets an `id:`
  aspect, taken from its `xmi:id` and falling back to the EMF URI fragment for
  objects that have none (repaired via `IdValidator`, then uniquified);
  **off**, no `id:` aspects are generated at all and the nodes are anonymous.
  The old code ignored `xmi:id`; using it is what makes instance round-trips
  stable.
- Attribute values → `let:a=<constant>` self-loops (the compact host form;
  normalisation desugars them). Strings quoted/escaped per GROOVE syntax.
- Enum values → edge `a` to a shared literal node `type:E$L` (one node per
  literal used in the graph).
- References → plain edges `r` (containment and cross-references look alike
  in the host graph; the type graph carries the distinction — as before).
- `ordering=index` → intermediates with `let:index=1..n`.
- Roots are unmarked (they are the nodes without incoming `part:`-typed edges).

Importing an `.xmi` resolves its metamodel through EMF (registered packages,
`schemaLocation`, sibling `.ecore` files); the import then yields *both* the
host graph and the regenerated type graph (deterministic naming makes this
stable), as a two-element `Set<Imported>` — the GUI already batches by kind.
Unresolvable metamodel → `PortException` advising to co-locate the `.ecore`.

## Export (type graph → .ecore, host graph → .xmi)

Reverse of the above. Package metadata comes from the recorded graph
properties; for hand-made type graphs without them, defaults are derived
(package name = graph name, nsURI `http://nl.utwente.groove/<name>`) and every
non-sort node type is a class (enums/interfaces only round-trip — no
heuristics). Host-graph export requires each object to have exactly one
containment chain to a root; violations (orphans, `part:` cycles — the latter
already excluded by GROOVE's containment checker) become `FormatError`s.
`xmi:id`s come from `id:` aspects when present.

## Options, persistence, GUI

`EcoreOptions` record with: `ordering` (`none`|`index`), `useIdentifiers`
(boolean). Persisted as two new `GrammarKey`s (`ecoreOrdering` via
`EnumParser`, `ecoreUseIdentifiers` boolean; both non-system, not notable).
The porter reads them from the `GrammarModel` passed to `doImport` — headless
paths and `Generator` see the same behaviour as the GUI.

GUI: a small modal `EcoreOptionsDialog` in `gui.dialog`, house pattern of
`FreshNameDialog` (JOptionPane + lazy getters + boolean `showDialog` + result
getter). `ImportAction`/`ExportAction` show it when the chosen file type is
ECORE/XMI, seeded from the grammar properties; on OK with changed values the
properties are updated through the store (undoable) before the port runs.
This is the successor of the never-completed 2012 `ConfigDialog`: options are
few because the encoding has one canonical form instead of 24 knobs.

## Code layout

- `io/external/format/ecore/`: `EcorePorter` (Importer + Exporter, RESOURCE
  kind; registered in `Importers.createImporters()` and
  `Exporters.createExporters()`), `EcoreToGraphs`, `GraphsToEcore`,
  `EcoreOptions`, `EcoreNames` (naming + repair).
- `FileType.ECORE(".ecore")`, `FileType.XMI(".xmi")`.
- pom: `org.eclipse.emf.ecore` 2.41.0, `org.eclipse.emf.common` 2.41.0,
  `org.eclipse.emf.ecore.xmi` 2.39.0 (Maven Central; automatic module names
  match the pre-Phase-1 `requires` lines, which return to `module-info.java`).
- `gui/dialog/EcoreOptionsDialog` + hooks in the two actions.

## Tests

New fixtures `junit/ecore/`: a small hand-written metamodel (`shop.ecore`:
classes, inheritance, abstract class, enum, containment, opposite,
multiplicities, all datatypes) + instance (`shop.xmi`), plus an
`ordered.ecore`/`ordered.xmi` pair for the `index` mode. `EcoreTest` (JUnit 4
`org.junit.Test`, fast suite):
1. `.ecore` import produces the expected type graph (compare against a
   committed `.gty` fixture via isomorphism, not node numbers);
2. `.xmi` import produces the expected host graph;
3. metamodel round-trip: import → export → re-import yields an isomorphic
   type graph (EMF-level comparison of the two `.ecore`s is too brittle);
4. instance round-trip: import → export → re-import isomorphic host graph;
   with `useIdentifiers`, `xmi:id`s survive.
