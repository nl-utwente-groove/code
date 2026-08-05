# Design: the SETTINGS resource kind and the Ecore mapping schema

*2026-07-31, revised 2026-08-02 and 2026-08-05. Follow-up to the Ecore porter
(`ecore-porter-design.md`): makes the porter's representational choices
per-element and persistent. Agreed direction: one generic settings mechanism
(properties format) with the Ecore mapping as pilot client; exploration
configurations and (eventually) the system properties are foreseen follow-up
clients. The 08-02 revision (agreed with Arend) makes the schema externally
visible: it is the leading segment of the resource name, the in-file key is a
consistency check only, and the fixed-name porter lookup became
unique-of-schema. The 08-05 revision inverts the first of these: the in-file
key is authoritative and resource names are free, with the leading segment
surviving only as a fallback.*

## The mechanism: `ResourceKind.SETTINGS`

A new text-based, named-multi resource kind. A *settings resource* is a file in
Java properties syntax inside the `.gps`. Its **schema is declared by its
reserved `$schema` key** (2026-08-05); resource names are free, so
`fast.properties` and `nightly/full.properties` are perfectly good exploration
configurations. The leading segment of the qualified name — the top-level
folder, or for a top-level file its own name — is the **fallback** schema name,
used only for a text without a `$schema` entry: `test/good.properties` without
the key has schema `test`. The fallback is what keeps the mechanism extensible
to the system properties, whose existing files carry no such key; every schema
template written by GROOVE (`SettingsSchema.getNewText`,
`ExploreConfigSchema.setConfigText`, `EcoreMapping.setGlobals`) emits the
`$schema` line, so files created by the tool never rely on it.
Making the declaration authoritative rather than a consistency check was
Arend's decision: the schema is a property of the *content*, and tying it to
the name made names carry information that renaming could silently destroy
(and blocked the natural `explore1` freshening). Schemas are *not* an enum:
a `SettingsSchema` interface plus a registry, so a new settings family is a
schema class, not enum surgery.

- **Kind declaration**: `SETTINGS("Settings", "settings file",
  FileType.PROPERTY, null)` — no default name, no graph role. It reuses
  `FileType.PROPERTY`: `FileType.createExtensionMap` forbids a second FileType
  with extension `.properties`, so kind dispatch for `.properties` files is by
  *name*, not extension.
- **The name rule**: a top-level `system.properties` is the PROPERTIES
  singleton; every other `*.properties` in the `.gps` is a SETTINGS resource,
  named by its qualified path (subfolders = name segments, as for all named
  kinds). Implemented in exactly two places: `SystemStore.collectResources`
  (read side: skip the reserved top-level names when collecting SETTINGS) and
  the save path (write side: `putTexts`/`rename` reject the reserved top-level
  names for SETTINGS with an error, so `system.properties` can never be
  clobbered). Under the fallback rule, `system.properties` is no
  carve-out but the singleton form of the built-in `system` schema, handled by
  the PROPERTIES kind; the whole leading segment `system` (file *and* folder)
  is reserved, keeping open the future in which the PROPERTIES kind retires
  into a `system` schema typed by the GrammarKey vocabulary. The legacy
  `<grammarName>.properties` form is reserved only *while it actually serves
  as the properties file* (i.e. in a store without `system.properties`); the
  next properties save migrates it and frees the name — so a grammar named
  `ecore.gps` can hold the `ecore` mapping resource.
- **Enabledness**: opted out of the *generic* mechanism, GROOVY-style, in all
  three places (`ResourceKind.isEnableable()`,
  `GrammarProperties.getActiveNames`, `GrammarModel.syncResource` all-active
  arm). Settings are read by their clients, not "enabled"; a schema that does
  need an *active* selection (the exploration configs) declares it itself,
  through the schema activation hooks (added 2026-08-05): `isActivatable()`
  (default false), `isActive(GrammarModel, QualName)` (default true:
  resources of a non-activatable schema are consulted by their client
  whenever applicable), `setActive(GrammarProperties, QualName, boolean)`
  (modifies the properties; default unsupported) and
  `getActivationText(boolean)` (the button/menu-item description).
  `SettingsModel.isActive()` delegates to the schema (unknown schema =
  inactive), which drives the list rendering (active = bold, inactive =
  parenthesised), the enable toggle button on the settings display's tool bar
  and popup menu (`ResourceDisplay.hasEnableButton`, overridden for
  SETTINGS), and the SETTINGS arm of `SimulatorModel.setEnabled`, which
  routes the toggle through `schema.setActive` and saves the properties
  (undoable). Deleting an active resource deactivates it the same way
  (`SimulatorModel.doDelete`), so no dangling reference is left in the
  properties; renaming an active resource follows the reference
  (`SimulatorModel.doRename`), which since 2026-08-05 is an unconditional
  retargeting to the new name: the schema lives in the content, so a rename
  can no longer move a resource out of its schema, and the cross-schema
  deactivation branch is gone.
- **Model**: `SettingsModel extends TextBasedModel<Settings>`. The schema name
  (`getSchemaName()`) is resolved once in the constructor — the program of a
  model instance is immutable — as the trimmed `$schema` value if the text
  parses and carries one, and the leading name segment otherwise; `compute()`
  parses the source text as `java.util.Properties`, resolves that name in the
  registry and validates the entries against the schema.
  Errors (unparseable text, unknown schema name — the message says whether it
  was declared or implied by the name —,
  keys/values rejected by the schema) surface in the resource tab's error
  panel. Since 2026-08-05, the errors of *active* settings resources moreover
  propagate into the grammar's error set (`GrammarModel.initGrammar`) and hence
  block the grammar, mirroring the treatment of prolog resources; the errors of
  inactive resources — including those of resources whose schema name is not
  registered — surface only on the resource itself. For a
  non-activatable schema every resource counts as active, so there each
  resource's errors block: an erroneous `ecore.properties` makes the grammar
  uncompilable. The compiled artifact `Settings` = (schema, properties) with
  typed accessors.
- **Schema interface** (`grammar/model/SettingsSchema`, registry
  `SettingsSchemas`): `name()` (the `$schema` value), `check(Properties)` →
  `FormatErrorSet`, and per-key documentation hooks for a future table editor.
  Closed-vocabulary schemas reject unknown keys; open-vocabulary schemas (the
  Ecore mapping, whose keys embed Ecore element paths) validate key *shape* and
  values, and leave element-path resolution to the client (see below).
  Three GUI-driven extensions (added on the `settings-gui` branch):
  `isSingular()` (a singular schema admits one resource per grammar; all
  resources of an over-populated singular schema get a resource error, making
  duplicates visible immediately rather than at port time — `ecore` is
  singular; since 2026-08-05 the population of a schema is determined by the
  resources' *declared* schemas, through `SettingsSchemas.getResourceNames`,
  which is also how clients locate the resource of a singular schema),
  `getExplanation()`/`getNewText()` (the generated template of a
  new resource: purpose as comment lines, the `$schema` entry, and commented
  example lines per key form), and `getHelpMap()` (a `Help`/`HelpMap` syntax
  help map shown in the settings display's info panel). The New/Rename/Copy
  name dialog no longer constrains the name; instead `NewAction` asks for the
  schema *before* the name (the template it writes fixes the schema, and the
  name can no longer tell it), and seeds the name dialog with the schema name.
- **GUI**: `DisplayKind.SETTINGS` with a generic `ResourceDisplay` (list panel
  slot 1), `TextTab` editor with RSyntaxTextArea's
  `SYNTAX_STYLE_PROPERTIES_FILE` token maker, optional tab
  (`Options.optionalTabs`), generic New/Edit/Save/Delete/Rename/Copy actions.
  `SimulatorModel.Change.SETTINGS` for notification.
- **Porters**: `NativeResourcePorter.register(SETTINGS)` so settings files can
  be imported/exported like other text resources. Importing an external
  `system.properties` is subject to the name rule (rejected; import it as the
  grammar properties via the existing Load System Properties action instead).

## The pilot schema: `ecore`

The Ecore porter reads the **unique** settings resource of schema `ecore`,
found by its declared schema and not by its name (2026-08-05):
`EcoreMapping.candidates` delegates to `SettingsSchemas.getResourceNames`, and
`EcoreMapping.RESOURCE_NAME` (`ecore`) survives only as the name under which
the options dialog *creates* the resource on demand. Several candidates
are a port error naming them. Absence means all defaults. There is
deliberately *no* grammar property naming the mapping: the mapping is
port-scoped, and one resource serves several metamodels, since per-element
entries only take effect on the metamodel they resolve against (entries for
other models are inert). Should distinct *global* options per metamodel ever
be needed, the extension point is a resource dropdown in the (already shown)
Ecore options dialog — the unique-or-error rule is forward-compatible with
that. The two grammar keys `ecoreOrdering` / `ecoreUseIdentifiers` (one week
old, no compatibility concern) are **retired** — their accessors and dialog
write-through move to the settings resource.

### Vocabulary

Keys are parsed from the right: the last segment is the *choice key*, the
preceding segments (if any) are an *Ecore element path* — package-qualified
Ecore names, unqualified allowed when unambiguous. Choice keys never clash with
element names because the last segment is always interpreted as a choice key.
Since the `settings-gui` branch, the key forms are reified as the `EcoreKey`
enum (choice key, admissible path lengths, value check, template line), whose
constants carry `@Syntax`/`@ToolTip*` annotations feeding the settings
display's documentation panel; the `EcoreMapping` parser, the generated
template and the help map all derive from it.

| Key | Value | Meaning |
|---|---|---|
| `ordering` | `none` \| `index` (default `none`) | global default, as today |
| `useIdentifiers` | boolean (default `true`) | global, as today |
| `<class>.<feature>.ordering` | `none` \| `index` | per-feature override |
| `<classifier>.typeName` | GROOVE identifier | GROOVE type name override |
| `<enum>.literalStyle` | `qualified` \| `plain` (default `qualified`) | literal type names `E$L` vs plain `L` |
| `<enum>.<literal>.typeName` | GROOVE identifier | per-literal override (wins over `literalStyle`) |

### Semantics

- **Resolution** happens at porter run time against the metamodel in hand,
  via `EcoreNames` (which knows all packages/classifiers/features): an element
  path matches by suffix against fully qualified Ecore names. *Ambiguous* →
  `FormatError` (silent misconfiguration is worse than an error); *unresolved*
  → silently ignored (the entry may concern a metamodel not currently
  imported; this is what makes choices sticky across metamodel evolution).
  Schema-level checking (in `SettingsModel`) validates only choice keys and
  values, since no metamodel is available there.
- **Naming overrides** are injected into `EcoreNames`: overridden labels are
  claimed first, default derivation disambiguates around them. A `typeName`
  override that collides with another override is a `FormatError`; a default
  name colliding with an override gets the usual `$2` disambiguation. Export
  applies the same maps in reverse; the round-trip metadata continues to record
  the actual correspondence, so metadata-carrying graphs export as before.
- **Per-feature ordering** replaces the global test
  `isMultiple && (ordered || !unique)` gate only in mode selection: the
  effective mode of a feature = its override if present, else the global
  default. The unordered-and-unique exemption stays (nodifying a set buys
  nothing); an explicit per-feature `index` on such a feature *is* honoured
  (the user asked for it; e.g. to keep duplicates of a non-unique feature —
  moot for sets, but uniform).
- **Dialog**: `EcoreOptionsDialog` keeps its two global widgets but now reads
  from and writes to the `ecore` settings resource, creating it on demand;
  writes go through the store (undoable) as targeted line edits (only the
  `ordering`/`useIdentifiers` lines are touched, hand-written entries and
  comments survive). Per-element entries are edited in the Settings tab; a
  per-element table in the dialog is a later step.

## The second client: the `explore` schema (2026-08-02, on `explore-parametric-engine`)

An `explore` settings resource holds one exploration configuration in
properties syntax: one entry per non-default `ExploreKey`, same value syntax
as the single-line form, no quoting (every entry has its own line). The
schema check runs the full validation stack of the exploration dialog:
per-key value parsing, cross-key consistency, realisability, and — via two
generic extensions of `SettingsSchema` made for this purpose — the
*grammar-dependent* contents (rule names, condition formulas, edge labels):
`check(GrammarModel, Properties)` (default delegates to the grammar-free
check; the ecore schema keeps resolving at port time) and
`getDependencies()` (resource kinds whose changes trigger a recheck — a
resource referring to a renamed rule turns red without being touched).

The *active* configuration is selected by the `exploration` grammar
property, which is a **reference** (a qualified resource name, per Arend's
no-residual decision: the property never held inline configurations outside
the tests). Consequences: resolution lives at the `GrammarModel` level
(`getDefaultExploreType/-Config`), not in `GrammarProperties` (a properties
object cannot see sibling resources); the property checker validates the
reference only (exists, `explore` schema, error-free) — content checks live
on the resource; and the dialog writes the target resource by targeted
per-key line edits, so the resource is the sole source of truth and
hand-written comments survive. The legacy
`explorationStrategy` key stays a read-time fallback, interpreted
indefinitely; its eager version-repair conversion was dropped (a
properties-level repair cannot create a settings file).

The `explore` schema is the first *activatable* schema (see the enabledness
bullet above): the enable button in the settings display sets or clears the
`exploration` reference for the selected resource. Activating one resource
supersedes the previously active one (the reference is single-valued);
deactivating the active resource reverts the grammar to the legacy or
default exploration.

**Dialog save behaviour** (2026-08-05): the `Configuration` preview panel
is gone — an unparsed one-line rendering of what the widgets already show
told the user nothing about *where* the configuration lives. In its place a
borderless resource line states which settings resource the composition is
based on (and whether that resource exists yet), or that there is none.
Saving is correspondingly named: `Save` writes to the referenced resource,
asking for a name through a `FreshNameDialog` when the reference is unset
(no more silent creation of a singleton `explore` resource); `Save As...`
always asks. Since 2026-08-05 the name is unconstrained — the resource text
declares its schema — and the suggestion for a first configuration is the
plain `exploration` rather than the schema name; names are still not
auto-freshened, so that saving in place over the current name stays possible.
Both buttons make the target resource the
grammar's exploration, so saving under a new name doubles as activation.
`Save` stays enabled when the composition equals the saved configuration
but no resource is referenced: there is then still something to do.

Deferred to a next dialog round (agreed): a resource selector dropdown in
the exploration dialog, and the reconsideration of the transient per-run
override.

## Deliberately deferred

- Folding the round-trip metadata (`ecoreTypes` etc.) into the settings file
  (would fix the hand-added-type export trap; separate branch).
- EAnnotations in the `.ecore` as import-time defaults.
- A generic keyed table editor for settings resources (the schema interface's
  documentation hooks are its future feed).

## Commit plan

1. this design document;
2. the SETTINGS mechanism (kind, model, schema registry, store name rule, GUI
   wiring) + a trivial test schema + `SettingsTest`;
3. the `ecore-mapping` schema with global keys only: porter reads it, grammar
   keys retired, dialog rewired + tests;
4. per-feature `ordering` + tests/fixtures;
5. enum/classifier naming overrides + tests/fixtures + `junit/ecore/README.md`
   update.
