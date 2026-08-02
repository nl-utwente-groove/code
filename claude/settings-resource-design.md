# Design: the SETTINGS resource kind and the Ecore mapping schema

*2026-07-31, revised 2026-08-02. Follow-up to the Ecore porter
(`ecore-porter-design.md`): makes the porter's representational choices
per-element and persistent. Agreed direction: one generic settings mechanism
(properties format) with the Ecore mapping as pilot client; exploration
configurations and (eventually) the system properties are foreseen follow-up
clients. The 08-02 revision (agreed with Arend) makes the schema externally
visible: it is the leading segment of the resource name, the in-file key is a
consistency check only, and the fixed-name porter lookup became
unique-of-schema.*

## The mechanism: `ResourceKind.SETTINGS`

A new text-based, named-multi resource kind. A *settings resource* is a file in
Java properties syntax inside the `.gps`. Its **schema is the leading segment
of its qualified name** — the top-level folder, or for a top-level file its own
name (the *singleton form* of a schema): `explore/fast.properties` has schema
`explore`, a top-level `ecore.properties` has schema `ecore`. This makes files
of different families distinguishable by location alone. The optional reserved
key `$schema` may re-declare the schema for the reader's benefit; when present
it must agree with the name (a mismatch is a resource error). It is optional
rather than required because the mechanism must extend to the system
properties, whose existing files carry no such key. Schemas are *not* an enum:
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
  clobbered). Under the leading-segment rule, `system.properties` is no
  carve-out but the singleton form of the built-in `system` schema, handled by
  the PROPERTIES kind; the whole leading segment `system` (file *and* folder)
  is reserved, keeping open the future in which the PROPERTIES kind retires
  into a `system` schema typed by the GrammarKey vocabulary. The legacy
  `<grammarName>.properties` form is reserved only *while it actually serves
  as the properties file* (i.e. in a store without `system.properties`); the
  next properties save migrates it and frees the name — so a grammar named
  `ecore.gps` can hold the `ecore` mapping resource.
- **Enabledness**: opted out, GROOVY-style, in all three places
  (`ResourceKind.isEnableable()`, `GrammarProperties.getActiveNames`,
  `GrammarModel.syncResource` all-active arm). Settings are read by their
  clients, not "enabled"; if a future schema (exploration configs) needs an
  *active* selection, that is that schema's business (e.g. a designated key or
  a grammar property), not the generic mechanism's.
- **Model**: `SettingsModel extends TextBasedModel<Settings>`. `compute()`
  parses the source text as `java.util.Properties`, derives the schema from
  the leading name segment, resolves it in the registry, checks an optional
  `$schema` entry for agreement, and validates the entries against the schema.
  Errors (unparseable text, unknown schema segment, `$schema` mismatch,
  keys/values rejected by the schema) surface in the resource tab's error
  panel; settings do not contribute to `Grammar` compilation, so their errors
  do not block the grammar. The compiled artifact `Settings` = (schema,
  properties) with typed accessors.
- **Schema interface** (`grammar/model/SettingsSchema`, registry
  `SettingsSchemas`): `name()` (the `$schema` value), `check(Properties)` →
  `FormatErrorSet`, and per-key documentation hooks for a future table editor.
  Closed-vocabulary schemas reject unknown keys; open-vocabulary schemas (the
  Ecore mapping, whose keys embed Ecore element paths) validate key *shape* and
  values, and leave element-path resolution to the client (see below).
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

The Ecore porter reads the **unique** settings resource of schema `ecore` —
the singleton `ecore.properties` at top level (which the options dialog
creates on demand) or a lone member of an `ecore/` folder; several candidates
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

## Deliberately deferred

- Folding the round-trip metadata (`ecoreTypes` etc.) into the settings file
  (would fix the hand-added-type export trap; separate branch).
- EAnnotations in the `.ecore` as import-time defaults.
- Exploration-configuration schema (separate branch; named-multi and folders,
  e.g. `explore/fast.properties`, come free).
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
