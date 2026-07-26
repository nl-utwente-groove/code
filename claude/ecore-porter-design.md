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
   Package data (name, nsURI, nsPrefix), the enum/interface classification, the
   opposite pairing and the per-feature data that the type graph does not
   determine (declared data type, order and uniqueness) are recorded as graph
   properties (`GraphInfo`) of the type graph, so a re-export reproduces the
   metamodel without heuristics.
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
→ `string`; `EDate` and anything else → `string`. All of these mappings are
silent; the declared Ecore type is recorded in the round-trip metadata whenever
it is not the default type of the sort it maps to, so the approximation is
reversible.

**Naming.** GROOVE type labels are unqualified Java-style identifiers (`$` is
legal, `.` is not). Default: the simple EClass/EEnum name, repaired via
`IdValidator` if needed. On a cross-package collision, the colliding names are
qualified as `pkg$Name` (recursively up the package path until unique) —
deterministic, no configuration. Enum literals are `E$L` (the `$` echoes the
old encoding and keeps literals visually grouped under their enum). Feature
labels are repaired the same way and, within one class, disambiguated by a
`$2`, `$3`, … suffix, so that two features whose names repair to the same
identifier do not silently merge into one type graph element; features of
*different* classes may share a label, since their source node type tells them
apart. Multiplicity of mandatory attributes is *not* enforced (GROOVE does not
check attribute-edge presence); noted limitation, Phase 4 territory.

**Ordering** (`ordering` option, default `none`):
- `none` — many-valued features become plain edges; order is not represented,
  and non-unique features collapse duplicates (host graphs are simple). Both
  losses are silent: this is exactly what the option selects, and `ordered` is
  the Ecore default for many-valued features, so flagging it would mean
  flagging nearly every reference of every metamodel. Choosing `index` is the
  way to keep the information.
- `index` — an ordered or non-unique feature `r` — attribute or reference —
  gets the old intermediate encoding, minus the parts that multiplicities now
  cover: nodified-edge node `type:C$r` with `edge:"r"` pattern, `in=1:r` edge
  from `C`, `out=1:val` to the target (or a `<sort>:val` self-loop for a data
  attribute), `int:index` attribute (1-based in instances); `part:` moves to
  the `val` edge for containments.

  A many-valued feature that is *both* unordered and unique keeps the direct
  edge encoding even in this mode. It has set semantics, which direct edges
  express exactly — there is no order and there are no duplicates to preserve,
  so nodifying it would buy nothing and cost a great deal: these graphs are
  what users write transformation rules against, and an intermediate node turns
  a one-edge rule pattern into a three-element one. Note that `ordered` is the
  Ecore *default*, so this exemption applies only to features explicitly
  declared `ordered="false" unique="true"`; a plain `[0..*]` reference is still
  nodified under `index`.

**Metadata format.** The round-trip properties are `;`-separated records of
`|`-separated fields. A field containing `;`, `|` or `\` is escaped with a
leading `\`, so that an nsURI or an original name can contain them without
fragmenting the record.

## Instance encoding (.xmi → host graph)

- EObject → node `type:C` (the concrete class). The `useIdentifiers` option
  (default on) governs node identity: **on**, every object gets an `id:`
  aspect, taken from its `xmi:id` and falling back to the EMF URI fragment for
  objects that have none (repaired via `IdValidator`, then uniquified);
  **off**, no `id:` aspects are generated at all and the nodes are anonymous.
  The old code ignored `xmi:id`; using it is what makes instance round-trips
  stable.
- Single-valued attribute values → `let:a=<constant>` self-loops (the compact
  host form; normalisation desugars them). Strings quoted/escaped per GROOVE
  syntax. A value that GROOVE's algebras cannot represent at all (`NaN`, the
  infinities) is a `FormatError`, not a substituted zero — unlike the
  approximations the encoding makes by design, this is input the encoding
  cannot express.
- Many-valued attribute values → an edge `a` per value to a shared constant
  node (one node per distinct value, as for enum literals). A `let:`-assignment
  can only carry one value of a field, so the compact form is not an option
  here; under `none`, duplicates therefore collapse, as for references.
- Enum values → edge `a` to a shared literal node `type:E$L` (one node per
  literal used in the graph).
- References → plain edges `r` (containment and cross-references look alike
  in the host graph; the type graph carries the distinction — as before).
- `ordering=index` → intermediates with `let:index=1..n` and a `val` edge to
  the target object or constant node. Since each occurrence gets its own
  intermediate, duplicates survive.
- Roots are unmarked (they are the nodes without incoming `part:`-typed edges).

Importing an `.xmi` resolves its metamodel through EMF (registered packages,
`schemaLocation`, sibling `.ecore` files); the import then yields *both* the
host graph and the regenerated type graph (deterministic naming makes this
stable), as a two-element `Set<Imported>` — the GUI already batches by kind.
Unresolvable metamodel → `PortException` advising to co-locate the `.ecore`.

## Export (type graph → .ecore, host graph → .xmi)

Reverse of the above. Package metadata comes from the recorded graph
properties; for hand-made type graphs *without* them, defaults are derived
(package name = graph name, nsURI `http://nl.utwente.groove/<name>`) and every
non-sort node type is a class (enums/interfaces only round-trip — no
heuristics). Note the asymmetry: the "every node type is a class" rule applies
only to metadata-free graphs. When classifier metadata *is* present it is the
authority, so a node type added to an imported type graph by hand is silently
omitted from the export until the metadata mentions it. That is deliberate —
guessing a package, an nsURI and a kind for a hand-added type would be exactly
the heuristics this design avoids — but it is a trap worth knowing about.

Feature bounds come from the type graph's multiplicity annotation where there
is one (it is what a user editing the graph would change) and from the recorded
bounds otherwise: attributes are self-loops and carry no multiplicity, and
neither does the intermediate encoding.

Host-graph export derives the containment tree from the `part:`-typed edges. An
object with more than one container, or on a containment cycle, is a
`FormatError`; an object with *no* container is simply an additional root of
the XMI resource, which is legal XMI and the natural reading of a host graph
with several unconnected components. Since an export has no graph to attach
errors to, they are collected in a `FormatErrorSet` and reported as the message
of a `PortException`. `xmi:id`s come from `id:` aspects when present.

**Remaining metamodel drift** (deliberate, deferred). The exported `.ecore` is
not byte-identical to the imported one, beyond feature ordering: enum literal
`value`s are renumbered 0,1,2,… and their `literal` fields are dropped;
attribute `defaultValueLiteral`s are not preserved; and the lower bound of a
single-valued mandatory attribute is lost with the multiplicity that the type
graph never carried. None of this affects XMI instance round-trips — XMI
serialises enum values by literal *name* — so it is recorded here rather than
fixed with three more metadata fields.

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
