# Fixable implementors: which should become builders?

Survey of all `nl.utwente.groove.util.Fixable` implementors on master
(0493b344b), following the conversion of `AspectLabel` to a nested
`Builder` plus an immutable label (89d1715c9). The question was which
other fixables are candidates for the same treatment, and whether a
`Buildable` interface is worth establishing alongside `Fixable`.

Answers, up front: **one** class is a clean builder candidate
(`Grammar`), one more is a real but larger job (the `ATermTree`
family), and no, a `Buildable` interface is not worth having. The
larger yield lies elsewhere — four classes have a fixing phase with no
extent at all, and there the phase should be deleted rather than
replaced.

## The criterion

A `Fixable` may be replaced by a builder producing an immutable result
only if all three gates pass:

1. **Nothing references the object while it is being built.** A cyclic
   object graph rules a builder out outright: the identity cannot be
   swapped under existing references without a fixup pass that
   reintroduces the nulls the builder was meant to remove.
2. **Copying is affordable.** Objects allocated per match or per rule
   application, or which deliberately alias structures they are built
   from, fail here.
3. **The build process does not observe or expose the half-built
   object.** Self-inspection of its own fields is fine and moves into
   the builder; being queried through its own public API, or having
   behaviour while incomplete, is not.

`RuleModel → Rule` is *not* an instance of the pattern: that is a
compilation step with a real representation change, and `Rule` is
itself `Fixable`, so the split relocated the phase rather than removing
it. Using a builder purely to obtain static nullness is likewise a
misuse; where it happens to fall out (as for `Grammar`) it is a side
effect, not the motive.

## Tier 1: the phase has no extent — delete it

These four never have a meaningful mutable period. They do not need a
builder; they need their mutators folded into construction.

### `control.instance.Frame`

All three construction sites — `Automaton:43`, `Frame:361`,
`Frame:460` — fix the frame in the same statement or the next one
(`new Frame(…).normalise()`, where `normalise()` is `setFixed()`
followed by canonicalisation through the frame pool). Every field
except the lazily computed `type`, `onError` and `onRemove` is final.
The flag exists solely so that `hashCode()` can `assert isFixed()`.

### `gui.look.Values.ColorSet`

Built in a static initialiser by three `putColors` calls, and it fixes
*itself* once the third selection mode arrives. Replacing it with a
static factory taking the three foreground/background pairs removes the
phase, the guard and the self-fixing trick together.

### `util.Pair`

`setTwo` has no callers anywhere; `setOne` has exactly one, the private
`ForestLayouter.Forest`. Worse, `Pair` defines fixedness as "the hash
code has been computed" (`isFixed()` is `this.hashCode != 0`), so
*hashing a pair fixes it* — precisely the leak that decided the
`AspectLabel` conversion, here in one of the most widely used utility
classes in the tree. The fix is to give `Forest` its own fields and
make `one`/`two` final; the cached hash then becomes sound rather than
self-fixing.

### `grammar.model.RuleModel.Index`

A single mutator, `setParent(parent, nr)`, which fixes at the end of
itself; a top-level index is fixed directly. That is two constructors —
except that `LevelIndexTree` creates all indices first and assigns
parents in a subsequent breadth-first pass, so the traversal has to be
reordered to construct parents before children. Lower confidence and
more work than the other three.

## Tier 2: genuine builder candidates

### `grammar.Grammar` — the strongest case

One construction site (`GrammarModel.computeGrammar`), strictly linear.
No cycles: the type graph, start graph and rules are all *already
fixed* when they are handed in, which the setters assert. Nothing
outside the method touches the grammar before `setFixed()`, and the
only reads inside it are self-inspection. `setFixed()` itself does
almost nothing beyond fixing the properties, and `isFixed()` already
carries `assert !this.fixed || this.typeGraph != null` — that is, the
flag already means "complete", not "frozen".

Ten setters move to a `Grammar.Builder`; `Grammar` gets final fields.
The class is currently not `@NonNullByDefault` precisely because of the
late-initialised fields, so the conversion would let it be annotated —
a consequence, not the reason.

### `algebra.syntax.ATermTree` and subclasses (`ExprTree`, `Formula`)

The mutable phase serves exactly one client, `ATermTreeParser`. Trees
are acyclic and built bottom-up; the only reason children are added
before they are complete is that the parser fixes the whole tree from
the root afterwards. `Formula` already has some eighteen static factory
methods of the shape `new Formula(op, args); result.setFixed(); return
result` — it is immutable by convention already, and only inherits its
mutators. And `clone()` exists to get back to the mutable phase
(`FormulaParser:218,232` clone-then-refix), the same smell that decided
`AspectLabel`.

The wrinkles: `setFixed()` performs the arity check and error
propagation, which moves into `build()`; and `setParseString` is called
after the arguments are added, so the parse string becomes a `build()`
argument. This is also the one conversion that genuinely needs a
builder *interface* — see below.

### `grammar.aspect.Aspect.Map`

Small, an `EnumMap` and so cheap to copy, and `AspectNode`/`AspectEdge`
already replace the field with a different instance at fix time
(`this.aspects.setFixed(); this.aspects = Aspect.normalise(this.aspects)`) —
the builder shape with builder and product collapsed into one class.
Against it: the map is read through its owner during the owner's own
parse and check phases, so it is entangled with the `AspectNode` phase,
which is staying. Medium confidence; worth revisiting only if the
aspect element phases are ever revisited.

### `util.parse.FormatError`

Better framed as "make it immutable" than "give it a builder". The only
post-construction mutator is the package-private `apply(Map)` /
`apply(Relation)`, and a copying counterpart already sits next to it
(`transfer`). The phase is in any case fictional: errors live in a
`LinkedHashSet` inside `FormatErrorSet`, so they are hashed long before
anyone fixes them.

## Tier 3: the phase stays

| Class(es) | Gate that fails |
|---|---|
| `AspectNode`, `AspectEdge`, `AspectGraph` | 1 — cyclic element/graph references |
| `Rule`, `Condition` | 1 — rule↔condition and parent links; `Rule` even needs a re-entrancy flag (`fixing`) during its own fixing |
| `AGraph`/`GGraph`, `GraphCache`, `GraphInfo` | 1 — graph↔cache↔info cycle |
| `TreeMatch`, `RuleEffect` | 2 — allocated per match / per rule application |
| `Properties`, `VisualMap` | 3 — long-lived *interactive* mutable use; the builder degenerates into a mutable twin with a doubled API |
| `Program`, `PlanSearchStrategy`, `Procedure`/`Function` | 3 — see below |
| `PatternBuilder.Cell` | private inner `HashSet`; `Set.copyOf` does the same job without a phase |

Three of these fail gate 3 in a way worth naming, because it recurs:
**the fixing step is itself a collaborative build.**
`Program.setFixed()` calls `TemplateBuilder…build(this)`, handing the
half-fixed program to a collaborator that reads it back.
`PlanSearchStrategy.setFixed()` calls `item.activate(this)` on every
search item, and those items call back into `getNodeIx`/`getEdgeIx`
/`getVarIx`/`getCondIx`, which *mutate the index maps as part of
fixing*. A builder cannot express this without handing the builder
itself to the collaborators, which only renames the problem.

`Procedure` fails differently and instructively: `setTemplate` is
called by `TemplateBuilder` **after** the procedure is fixed, and has
no guard at all. That is deliberate late binding of a
procedure↔template cycle, not a build phase — no builder applies.

## On a `Buildable` interface

Recommended against. The decisive evidence is `Fixable` itself: across
the whole source tree it is used as a *type* exactly once
(`Grammar:96`, `assert action instanceof Fixable fix …`). Its value is
as shared vocabulary plus one default method, `testMutable()`. A
`Buildable<T>` would have zero type uses and no default behaviour to
carry: builders are used at their concrete type, immediately, at one
call site each.

It would also be strained from birth. `AspectLabel.Builder.build()` is
total; a `Grammar.Builder.build()` must throw `FormatException`; a
term-tree builder's terminator needs the parse string as an argument.
A common supertype would either be uselessly general or force `throws
FormatException` on every implementor. And the one existing
builder-shaped hierarchy, `util.line.LineFormat.Builder`, is not a
product builder at all but a `StringBuilder`-shaped accumulator with
`getResult()` — a universal interface either excludes it or is bent to
fit it.

There is one place where the instinct is right. `ATermTreeParser<O,X>`
is generic over tree types and uses `createTree(op)` + `addArg` as its
build protocol; making the trees immutable forces that protocol into an
explicit interface. But that is a *domain* interface in
`algebra/syntax`, parameterised over `O` and `X` — not a universal
`util.Buildable`.

What to establish instead is a convention, next to the `Fixable` guard
convention in `claude/CLAUDE.md`: a nested `public static class
Builder`, fluent mutators returning the builder, a terminator named
`build()`, and the three gates above as the test for whether to reach
for one at all.

## Defects found in passing

- `Program.setFixed()` and `Procedure.setFixed()` return the **inverse**
  of the documented `Fixable` contract: both open with
  `boolean result = this.fixed` where every other implementation writes
  `!this.fixed`, so they report `false` when the state changed and
  `true` when it did not. No caller uses the value, so this is latent.
- `ATermTree.setParseString` has no guard and remains reachable after
  the tree is fixed.
- `Procedure.setTemplate` likewise has no guard, and is in fact called
  after fixing (by design, see above) — so the guard cannot simply be
  added; the asymmetry deserves a comment instead.
- `TreeMatch` defines fixed as "hash computed", and `toProofSet()` and
  `traverseProofs()` call `setFixed()` themselves, so *reading* fixes
  the object. It stays a `Fixable` on hot-path grounds, but its guard
  is decorative.
- `Function` redundantly re-declares `implements Fixable`, which it
  already inherits from `Procedure`.

## Suggested order of work

1. `Frame`, `Values.ColorSet`, `Pair` — small, independent, one commit
   each, no design risk.
2. The inverted `setFixed()` returns and the missing/decorative guards —
   a single tidy-up commit.
3. `Grammar` → `Grammar.Builder`, on its own branch.
4. The `ATermTree` family, with its domain builder interface, if and
   when the parser is touched for other reasons.

`RuleModel.Index` and `Aspect.Map` are recorded but not recommended on
their own; both are worth doing only alongside work that is already in
their neighbourhood.
