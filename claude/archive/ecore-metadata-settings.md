# Design: Ecore round-trip metadata in the `ecore` settings resource

*Status (2026-09-15): implemented as proposed. The note and the vocabulary
(step 2) reached master through branch `ecore-metadata-settings` (140950da4);
steps 3 to 5 through branch `ecore-metadata-port` (36550a0f8 export from the
records, 1a697a4f9 import writes the records, 3ff54a13b removal of the
graph-property form, plus fc85132f0 and 4a21b6216 for two leftovers found on
the way), merged on 2026-09-15. Deviations from the proposals are recorded
under "Steps". An optional prune of stale recorded entries on re-import is
filed as item 5 of gh #898.*

*2026-09-14. Item 1 of gh #898, deferred by the settings-resource design
(`settings-resource-design.md`) and reversing principle 3 of the porter
design (`ecore-porter-design.md`: "round-trip metadata rides on the
graphs, not in extra resources"). Target: before release 8.0.0. The porter and its metadata landed on 2026-07-26 and
have not been released, so the graph-property format has no compatibility
obligation.*

## The problem

`EcoreToGraphs.createMetadata` records what the type graph does not determine
about an imported metamodel as four graph properties of the type graph
(`ecorePackages`, `ecoreTypes`, `ecoreFeatures`, `ecoreOpposites`), in a
private `;`/`|` record syntax. `GraphsToEcore` treats those records as the
authority when present. Consequences:

- **The trap.** A node type added by hand to an imported type graph has no
  classifier record and is silently omitted from the exported `.ecore`.
- **Invisible.** The records are shown, if at all, as opaque strings in the
  graph properties panel; nobody edits them there. The syntax is documented
  only in a javadoc comment.
- **Wrong owner.** The `ecore` settings resource already holds the user's half
  of the same correspondence (`typeName` overrides, literal styles, per-feature
  ordering). The recorded half lives elsewhere, in a different syntax.

## Decision constraints (Arend, 2026-09-14)

1. Expose the metadata through the `ecore` configuration: yes. Dozens of new
   `EcoreKey` forms: no. But this is not the time for irreversible decisions;
   Arend is not actively working on this.
2. One resource serving several metamodels has no visible use, but qualifying
   entries by type-graph name sounds expensive. Choose whatever leaves the most
   options open without future incompatibilities.
3. In before 8.0.0.
4. The porter should stop using `ResourceProperties`; judge the drawbacks.

## Proposal 1: vocabulary

Four new choice keys, in the existing key form (Ecore element path, then choice
key; resolution rules unchanged). Import writes only what the type graph does
not determine; the existing `typeName` key doubles as the record of the
label correspondence.

| Key | Value | Written by import for |
|---|---|---|
| `<package path>.package` | `nsURI=<uri> [nsPrefix=<prefix>]` | every package; `nsPrefix` only when it differs from the package name |
| `<classifier>.kind` | `class` \| `interface` \| `enum` \| `datatype` | every classifier (this is the package membership list) |
| `<class>.<feature>.feature` | `[type=<EDataType>] [ordered=<bool>] [unique=<bool>] [bounds=<lo>..<hi>] [name=<original>]` | features the type graph does not determine (many-valued, non-default data type, repaired name); only non-default fields |
| `<class>.<reference>.opposite` | `<class>.<reference>` | one entry per opposite pair |
| `<classifier>.typeName` (exists) | GROOVE label | classifiers whose label is not the plain Ecore name (collision-qualified, repaired) |
| `<enum>.<literal>.typeName` (exists) | GROOVE label | literals whose label is not what `literalStyle` derives |

Field values inside `package` and `feature` are named, space-separated,
order-free and optional. Unknown field names are schema errors.

Why this shape, given constraint 1:

- **Reversibility lives in the value syntax, not the key count.** Named fields
  can be added without touching any existing file, and a field can be retired
  by accepting and ignoring it. The alternative, one choice key per field
  (`type`, `bounds`, `ordered`, `unique`, `name`), is equally extensible but
  costs up to five lines per feature and eight new key forms; the composite
  costs one line and four forms.
- **The records are regenerable.** Every entry import writes is derived from
  the `.ecore` file, which the user keeps. Should the vocabulary change after
  8.0.0, re-importing the metamodel rewrites the entries; only hand-added
  entries need the user's attention. Import groups the entries it writes under
  a comment naming the source file, so they are recognisable as such.
- **Reusing `typeName` as record makes labels sticky**, which the settings
  design already asks for. Consequence to be aware of: once `shop.Item` and
  `shop.catalog.Item` have been qualified as `shop$Item`/`catalog$Item`, they
  stay so on re-import even after the collision disappears. Rules referencing
  the labels keep working; that is the argument for, and the only argument
  against is aesthetic.
- Enum literal `value`s and `literal` strings (the remaining metamodel drift
  noted in the porter design) fit a later `<enum>.<literal>.literal` key with
  the same field syntax; not part of this work.

## Proposal 2: scoping

No qualification. Entries are global to the resource; on export they resolve
against the labels of the type graph being exported: a type label matches a
classifier entry by explicit `typeName` value or, failing that, by the last
segment of the entry's path. Unmatched entries are skipped (as the current
reader already skips classifiers whose node was removed); an ambiguous match is
an error; a label without any match is a class in the default package. The
default package is the first root package recorded, or the graph-name-derived
one if none is recorded. Packages that end up without classifiers are dropped
from the export.

Why this leaves the most open, given constraint 2:

- A key-prefix qualifier (`<type graph>.<package path>...`) is **not** cleanly
  addable later: the leading segments are already package paths, and a type
  graph named like a package would be ambiguous. It also costs every user a
  prefix on every line now, for a case nobody can point to.
- Resource-level scoping **is** addable later without breaking any file. The
  naming scheme already reserves the `ecore/` folder for residents of the
  schema, and the lookup already finds them; only the singularity rule
  (`EcoreMappingSchema.isSingular`) would relax, to "the resource whose entries
  resolve". A single `ecore.properties` remains a valid instance of that
  scheme. So if two metamodels in one grammar ever need separating, the answer
  is one resource per metamodel, and nothing written now stands in the way.
- The failure mode of not scoping is honest: two metamodels that both declare
  `Item`, imported separately, both yield the label `Item`, and exporting
  either type graph reports the ambiguity, which a `typeName` entry resolves.
  Implementation may improve on this by letting import claim labels already
  recorded by `kind` entries, so the second import derives `b$Item` itself;
  this is a nicety, not a design point.

## Proposal 3: before 8.0.0, no fallback

The graph-property reader and writer go, together with the four key constants
and the record helpers. No shipped release wrote the properties, so no
grammar in the wild carries them. Doing this later would mean a fallback reader
for the lifetime of 8.x; doing it now costs nothing.

## Proposal 4: `ResourceProperties` unused by the porter

`grammar.ResourceProperties` stays, of course: it carries priority, version,
enabledness and the other per-graph keys, and is persisted through GXL. What
goes is the porter's use of its free-form entries. Drawbacks, judged:

- **The type graph is no longer self-describing.** Copying an imported type
  graph into another grammar, or a grammar without its `ecore` resource,
  exports it as a metadata-free graph (all classes, derived package). Judged
  acceptable: that is the documented behaviour for hand-made type graphs, the
  settings resource is one file to copy along, and the alternative is the
  current state, where the metadata is invisible precisely because it travels
  with the graph.
- **Edits and metadata are separate undo entries.** Deleting a type node and
  the corresponding `kind` entry are two actions. Acceptable: the stale entry
  is skipped on export, so nothing breaks; it merely lingers.
- **Several active type graphs share one record set.** Export of one type
  graph among several resolves entries by its labels; the other graphs' entries
  are skipped as unmatched. This is the scoping question above and is
  acceptable for the same reasons.
- **Benefit, not drawback:** type graphs written by the import are plain
  GXL again, and the "notable properties" indicator of the graph tab no longer
  lights up for imported graphs for reasons the user cannot see.

## Import-time writing

`EcorePorter.doImport` returns, next to the graphs, an `Imported` of kind
SETTINGS holding the mapping resource text with the generated entries merged
in: an existing key is replaced in place (first occurrence, as `setGlobals`
does for the globals), new keys are appended in one group under a comment
naming the imported file; comments and hand-written entries survive. If no
mapping resource exists, one is created under the default name with the
`$schema` header; more than one candidate is already an error. Import never
removes entries.

`ImportAction` currently asks before overwriting any existing resource. A
merged settings text is an update the importer computed from the existing
resource, so the question is meaningless there. `Imported` grows a flag saying
so, and `ImportAction` stores such resources without asking. Importers stay
side-effect free; the action remains the only writer.

## Steps

1. This note and its index line. *(done)*
2. Vocabulary: the four `EcoreKey` forms with value checks and documentation
   annotations; `EcoreMapping` parses them into typed records (`packages()`,
   `kinds()`, `features()`, `opposites()`), plus a writer that renders the
   records back into entries; unit tests on parse/render round trip and on the
   schema errors. No behaviour change yet.
3. Export from settings: `GraphsToEcore` takes packages, classifiers, features
   and opposites from `EcoreMapping` instead of the graph properties, with the
   per-label default rule above (the trap fix). `EcoreTest` adapted; a
   hand-added-type export test added. *(done, branch `ecore-metadata-port`;
   deviation: several entries may match one label once the import writes a
   qualified `typeName` next to a hand-written unqualified one, so matches that
   qualify one another are collapsed to the most qualified rather than reported
   as ambiguous.)*
4. Import writes settings: `EcoreToGraphs.createMetadata` produces records
   instead of properties; `EcorePorter` merges them into the resource text and
   returns the SETTINGS `Imported`; `ImportAction` applies it without asking.
   `testMetadata` rewritten against the resource text. *(done; deviation: a
   feature whose Ecore name is not a single path segment — `unit.price` in the
   packages fixture — stands under its GROOVE label, the `name` field carrying
   the Ecore name either way.)*
5. Remove the graph-property reader, writer and constants. *(done; the
   `literal` classifier kind went with them, and the three passages of
   `junit/ecore/README.md` that showed the record syntax now show entries.)*
6. Status blocks in the two archived notes; gh #898 comment. *(done 2026-09-15.)*
