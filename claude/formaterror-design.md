# FormatError context refactoring (dependency cleanup P3 + gh #885)

*Design note 2026-08-18 (Claude session). Covers the last P3 item of
`dependency-analysis.md` except the `automaton` split: the
`util.parse -> grammar/graph/lts` whitelist entries of `LayeringTest`, caused
by `FormatError`, `FormatErrorSet`, `SearchResult` and `SelectableListEntry`.
Also implements the mechanics of gh #885 (error severity), which reworks the
same construction surface.*

## Constraints found in the code

- The error family (`FormatError`, `FormatErrorSet`, `FormatException`) cannot
  move out of `util.parse`: it is used below graph level by `Parser`,
  `Fallible`, `Properties`, `QualName`, `IdValidator`. The domain knowledge
  has to leave the classes instead.
- `FormatError.getGraph()`, `getControl()`, `getProlog()` had zero external
  callers — dead API. `getState()` had one caller (`LTSDisplay`),
  `getPropertyKey()` one (`Simulator.selectDisplayPart`).
- The resource kind/names surface (`SelectableListEntry`) is consumed by gui
  navigation plus exactly one non-gui site, `CompositeControlModel` (names
  only).
- All callers of the `GraphMap`/`Element` remapping overloads
  (`apply`/`applyInverse`/`transfer`) sit at graph level or above.
- `FormatError.equals` today ignores resource names: identically worded
  errors from different resources collapse in `FormatErrorSet`'s
  `LinkedHashSet` unless distinguished by elements or other typed context.

## Design

`FormatError` keeps only layer-neutral data: message, severity, line/column
`numbers`, and one generic insertion-ordered **context list** of opaque
`Object`s. `addContext` shrinks to: flatten arrays and collections, inline
nested `FormatError`s, `Integer` → numbers, `Severity` → severity, everything
else → context. Equality, `clone`, `extend` and `transfer` work uniformly
over (message, context). Interpretation moves to where the types are known:

- **Resolver** (`grammar.model`): the former 15-branch `instanceof` chain,
  reconstituted as a derivation over the context list producing (resource
  kind, resource names, elements, property key). Gui adapts it into list
  entries; `CompositeControlModel` uses it directly. The single `GraphState`
  branch is handled by its only consumer (`LTSDisplay` scans the context
  itself).
- The `Resource` record moves to grammar (it holds `ResourceKind`); it
  travels opaquely through the context, so call sites only change imports.
- **Remapping inverts**: `FormatErrorSet` keeps generic `apply(Map)` /
  `apply(Relation)`; the `GraphMap`-typed conveniences move onto `GraphMap`.
  With opaque context, remapping no longer needs to tell elements from other
  context — the maps only ever contain elements.
- **`SelectableListEntry` → `gui.list`**, implemented by a gui-side adapter
  over the resolver, not by `FormatError`.
- **`SearchResult` → gui** entirely, together with the
  `AspectGraph.getSearchResults` loop (display search over public
  `AspectGraph` API, single caller in `SimulatorModel`). *Deviation from
  `dependency-analysis.md`, which said `SearchResult → grammar.model`: that
  placement would make grammar implement a gui interface — a new
  wrong-direction edge.*

### Severity (gh #885)

`enum Severity { ERROR, WARNING, INFO }` in `util.parse` — layer-neutral.
Construction needs no new overloads: `new FormatError("...",
Severity.WARNING, ...)`; the context chain picks the severity off like any
other argument. `throwException()` throws only if the set contains at least
one ERROR; when it does not throw, warnings stay in the set, and the
existing carriers (`Fallible`, `GraphInfo`, the resource models) keep them
alongside the objects they describe. The remaining cost of gh #885 is
auditing funnels where errors travel *only* via the exception (warnings
would silently drop); that adoption is incremental and out of scope here.
Naming caveat from the issue (a `FormatError` that is only a warning):
renaming the class is hundreds of sites of churn; kept as-is, documented.

## Rejected alternatives

- `ResourceError extends FormatError` subclass: needs a static factory hook
  inside `FormatErrorSet.add` so every layer's `add(msg, args)` produces the
  rich subclass; polymorphic clone; more moving parts than the resolver.
- Contributor interface implemented by domain types (`par.addTo(error)`):
  still needs util-visible storage for `ResourceKind`, so it collapses into
  the generic-context design anyway.
- Moving `ResourceKind` to `util`: too grammar-entangled.

## Findings from the implementation (2026-08-18)

- The old `instanceof` chain silently dropped `null` parameters and any
  unrecognized object (including plain `String` format arguments). The
  generic context keeps unrecognized objects — required, since consumers
  like `LTSDisplay` fish the `GraphState` out of the context — so format
  strings now land in the context too. Harmless: the formatted message
  already contains them, so equality is unaffected in practice.
  `null` parameters are still dropped, now explicitly (`SettingsTest`
  caught this: `OracleParser` passes a null argument).
- `Collections.unmodifiableCollection` does not implement value equality;
  `FormatError.equals` must compare the backing sets, not the
  `getContext()` views. (Bug in the first cut, fixed in the severity
  commit, covered by `FormatErrorTest`.)
- `@NonNullByDefault` on `FormatError`/`FormatErrorSet` required declaring
  the varargs as `@Nullable Object...` — hundreds of existing call sites
  legitimately pass nullable arguments.

## Known behavioural deltas

- **Dedup**: equality now includes the full context (resource-bearing
  objects included), so fewer identical messages collapse. Deliberate;
  arguably more correct.
- **Ordering**: `compareTo` loses `Node`/`EdgeComparator`; replaced by a
  generic deterministic order over context (class name, then `toString`).
  Not on the exploration path.
- **Retention**: context holds the contributing domain objects (e.g. the
  `Rule`) instead of distilled names; errors are few and mostly short-lived.
