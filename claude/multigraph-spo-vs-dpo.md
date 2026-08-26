# Multigraph semantics: SPO versus DPO

Status: analysis, 2026-08-22. Answers the open question recorded in
[aspect-parallel-edges.md](aspect-parallel-edges.md) and
[eraser-injectivity.md](eraser-injectivity.md) (user, 2026-07-27): "for
multigraphs, SPO is still under investigation as an option — DPO is current
behaviour, not a final commitment; if very costly in case distinctions,
parallelEdges=DPO is the go-to solution". No code changes; the decision
checklist is at the end. Line references are to master at d062e96f4.

## 1. What the two modes mean today

`ParallelMode` (`grammar/ParallelMode.java:29-74`) has three values. Two
predicates split them: `isMulti()` (SPO, DPO) selects multigraph
*structure*, `isDPO()` selects the DPO *semantics* machinery. `none` is
simple graphs under the classic semantics — which is SPO-style delete-wins,
so the mode name conflates the two axes: `none` = simple+SPO, `SPO` =
multi+SPO, `DPO` = multi+DPO. There is no simple+DPO.

### Common to SPO and DPO (`isMulti()`)

- Host graphs are non-simple (`HostModelMorphism.java:55`); `mult=k:` on
  host edges expands to k copies. Rule-side `mult=` is rejected
  (`MultAspectTest.java:75-81`, deferred 2026-07-31).
- Rule compilation is **mode-independent**: every role-bearing aspect edge
  gets its own parallel index, so `use:a` + `del:a` compiles to two
  distinct rule edges and `use:a` + `new:a` to a reader plus a creator
  that is always fresh (`PatternBuilder.java:777-784`; embargo edges are
  exempt). The DPO/SPO difference is confined to matching and application.
- Non-injective matching (the default) binds each rule edge independently:
  a k-bundle maps into an n-bundle in n^k ways; only `matchInjective`
  makes bundles edge-injective, via the used-edges set
  (`ParallelEdgeMatchingTest.java:107-128`,
  `PlanSearchStrategy.java:590-600`).
- Application: the erased edge set is the *set* of eraser images
  (`BasicEvent.java:481-490`), so two eraser copies collapsed onto one host
  edge erase one edge; readers contribute no delta; created copies are
  minted fresh (the `freshEdgeList` machinery) and merge redirects mint
  fresh copies (`MergeMap.java:149-155`, `RuleEffect.java:555`).
- Node deletion: eraser nodes are removed with their incident edges
  (`RuleEffect.removeNodes`), unless `checkDangling`.

### SPO = the above, full stop

Nothing in the matcher or the rule compiler looks at `isMulti() &&
!isDPO()`. "Multigraph SPO" is exactly textbook SPO over multigraphs:
matches are arbitrary (multi)graph morphisms, the effect is the pushout in
partial morphisms, i.e. everything identified with a deleted element is
deleted, plus dangling edges. All regular expressions — atoms excepted —
keep the automaton-based, ends-only semantics
(`PlanSearchEngine.java:465-473`; pinned by
`ChoiceEdgeMatchingTest.java:221-230`).

Pinned example (`junit/rules/spoErasers.gps/readerEraser`, rule `a` +
`del:a`; `RuleApplicationTest.java:124-129`):

| host | matches/events | result |
|---|---|---|
| 1 copy (`-0`) | 1: reader and eraser collapse | `-0-0`: 0 copies |
| 2 copies (`-1`) | 2 events (eraser on copy 0 / copy 1; the reader image is irrelevant and collapses) | `-1-0`, `-1-1`: 1 copy each |

So under SPO the rule "reads one, deletes one" does **not** demand two
copies: the declared count is an upper bound, and the reader is not a
survival guarantee.

### DPO = SPO + the gluing condition

Everything gated on `isDPO()`:

1. **Identification condition, edges**: static conflict pairs
   (eraser, other edge with overlapping matching types) refused at
   `putEdge` (`SearchPlan.java:81-83`, `PlanSearchStrategy.java:578-588`).
2. **Identification condition, nodes**: compile-time merge embargoes
   (`ConditionAssembler.java:416`), skipped under injective matching.
3. **Cross-level**: root extension imports ancestor conflict elements
   (`ConditionAssembler.java:93`).
4. **Inter-instance**: the proof filter rejects amalgamated applications
   in which two instances claim one eraser image (`TreeMatch.java:292-293`).
5. **Dangling condition** implied (`GrammarProperties.java:340-343`,
   `GrammarKey.java:121-124`); enforced post-hoc per match in
   `Prover.java:240-242`. Edges to value nodes are exempt
   (`Prover.java:254,265`).
6. **Regexpr edge images**: `a|b`, `-a`, `?[b]|a` … bind a host edge, so
   they take part in 1 and count per witness under `forall`
   (`PlanSearchEngine.java:465-473`; `ChoiceEdgeMatchingTest.java:133-135`).
7. **`ignoreRegExp` check**: untracked composites that may traverse an
   erasable edge type are a rule error (`ConditionAssembler.java:198`).
8. Critical pairs: identification filter (`CriticalPair.java:318-320`),
   currently unreachable because `canComputePairs` rejects
   `checkDangling` grammars (`CriticalPair.java:636`; see
   [critical-pair-review.md](critical-pair-review.md)).

Pinned counterpart (`junit/rules/mult.gps/readerEraser`, same rule,
`parallelEdges=DPO`): 1 copy → no result file (inapplicable);
2 copies → `-1-0`, `-1-1`, 1 copy each. `dpoErasers.gps` pins the
quantifier cases (C, D, E) and `eraseReaderOverlap`/`eraseEraserOverlap`
the within-level ones; `parallelChoice.gps/choiceReaderConflict` (`del:a`
+ `a|b`) is inapplicable on a lone `a` and applies via `b` when both exist.

## 2. Where the semantics diverge for multigraphs

Per case: what a user would plausibly expect, what each mode does, and what
it would cost to make SPO honour the expectation. "Cost" is split into
matcher / application / compilation pipeline.

### 2.1 Reader and eraser copies on one host edge (`use:a` + `del:a`)

- *Expectation* (multiset reading, the one the disjoint allocation
  suggests): needs ≥2 copies, one survives.
- *SPO*: matches on 1 copy, deletes it (`spoErasers/readerEraser-0-0`).
  On ≥2 copies the outcome agrees with DPO.
- *DPO*: inapplicable on 1 copy.
- *Cost of the expectation under SPO*: edge-injectivity among
  same-content rule edges, i.e. the DPO conflict-pair machinery (1) or
  the used-edges set restricted to edges. Matcher: gate change only, the
  code exists. Application: none. Pipeline: the cross-level root extension
  (3) and the proof filter (4) would have to be re-gated too, or the
  guarantee holds within a level only. Conceptually this is *not* SPO any
  more: it is the edge half of the identification condition — a third
  semantics next to delete-wins and DPO ("SPO with edge injectivity"),
  with all of DPO's case distinctions for edges and none for nodes.
  **Rejected (user, 2026-08-22)**: edge-injectivity for rule edges that
  merely happen to share source and target makes no sense as a semantics.

### 2.2 Eraser/eraser overlap across quantifier instances

- *Expectation*: "delete all `a`-edges at x" deletes each once; two
  instances hitting one edge is harmless.
- *SPO*: erased set is a set (`BasicEvent.java:481-490`); both instances
  legal, the edge goes once; an instance `count` sees two instances.
- *DPO*: the amalgamated application is invalid (case E,
  `dpoErasers/eraseForallEraser`).
- *Cost*: none — SPO already gives the expectation. The only seam is the
  count-versus-deletions discrepancy, inherent to amalgamation under SPO.

### 2.3 Creators next to readers (`use:a` + `new:a`)

- *Expectation*: one fresh copy, 1 → 2.
- *SPO and DPO*: identical (`mult.gps/readerCreator-0-0`: `mult=2:a`);
  simple mode absorbs the creator instead.
- *Bug, both modes* (**confirmed by probe 2026-08-22, gh #901**): under
  `checkCreatorEdges` or `rhsIsNAC` the creator rule edge itself —
  carrying its fresh index — is put into the NAC set
  (`PatternBuilder.java:307-312`). Under non-injective NAC matching that
  embargo copy binds the reader's host image: `readerCreator` has 0
  matches on a 1-copy and on a 2-copy host in SPO and DPO alike (1 resp.
  2 without the property); under `matchInjective` it fires on 1 copy but
  not on 2. In `none` mode 0 is correct (the creation would be a no-op);
  in multigraph mode the creator is always fresh, so the NAC suppresses a
  real transformation. Orthogonal to SPO/DPO.

### 2.4 Regular expressions traversing erased edges

- *Expectation* (split): a path condition is a precondition (SPO reading)
  — or — the rule must not destroy what it relies on (DPO reading).
- *SPO*: all composites are ends-only path tests; witnesses may be erased
  without notice. `del:a` + `a|b` on a lone `a` applies and leaves nothing.
  A `forall` over `a|b` counts 1 on an `a`,`b` host
  (`ChoiceEdgeMatchingTest.java:221-230`), while a `forall` over atom `a`
  on a 2-bundle counts 2.
- *DPO*: edge-image expressions (6) enforce the identification condition
  and count per witness; the rest is statically rejected unless
  `ignoreRegExp` (7); dynamic censored re-match deferred (gh #900).
- *Cost*: letting `a|b` count under SPO is one gate
  (`PlanSearchEngine.java:465`) — rejected 2026-07-31 precisely because it
  would split SPO regexprs into counting (`a|b`) and non-counting (`a.b`)
  classes. Making `a.b` count needs witness tracking, rejected outright.
  So the SPO atom/regexpr seam (S1 in §3) cannot be closed uniformly at
  any price short of witness tracking.

### 2.5 Merging with parallel copies

- *Expectation*: merging two nodes that each carry an `a`-edge to `x`
  yields a 2-bundle.
- *SPO and DPO*: identical — redirected edges are minted fresh
  (`MergeMap.java:149-155`; `parallel-pump` sample). Merging an eraser
  node with a reader node is refused under DPO (node embargoes, 2),
  delete-wins under SPO — the ordinary node case, nothing
  multigraph-specific.
- *Cost*: none.

### 2.6 Value and `let` edges

- *Expectation*: `let:f=e` replaces the field value.
- *Both modes*: `let` normalises to one eraser plus one creator
  (`NormalAspectGraph.java:377-395`), so a same-value twin survives
  (decision 2026-07-31: assignment replaces one copy). Under DPO the
  eraser additionally may not share its image with another `f`-reader in
  the rule; dangling is not checked for value edges (`Prover.java:254,265`),
  so DPO's gluing condition is already partial for attributes.
- *Cost*: none; the erase-all-copies alternative is a universally
  quantified eraser in the normalisation, equally (un)available in both
  modes.

## 3. Seams accepted under the current design

| # | seam | mode | acceptable long-term? |
|---|---|---|---|
| S1 | `forall` over atom `a` counts parallel copies, over `a|b` (and `a.b`) it does not | SPO | Yes if SPO is documented as "atoms are edges, regexprs are path tests" (the legacy story); not closable uniformly (2.4) |
| S2 | `use:a` + `del:a` matches on one copy and deletes it; declared bundle sizes are upper bounds | SPO | Yes — it *is* SPO; unacceptable only if SPO is sold as the multiset reading |
| S3 | instance count ≠ number of deletions under overlapping forall erasers | SPO | Yes, inherent to SPO amalgamation |
| S4 | cross-instance reader vs other-instance eraser (case F): deletion wins | both | Yes, decided 2026-07-20 (undetectable deterministically) |
| S5 | value-node edges exempt from the dangling condition | DPO | Yes, consistent with value nodes being implicit |
| S6 | counting NACs: no syntax, semantics undecided | both | Moot until rule-side `mult=` returns |
| S7 | creator-NAC copy vs reader twin under `checkCreatorEdges`/`rhsIsNAC` (2.3) | both | **Confirmed bug, gh #901**; independent of this decision |

The SPO-specific seams S1–S3 share one root: the rule text suggests a
multiset (counts of copies), SPO matching delivers morphisms. Once SPO is
described as morphism semantics, none of the three is a defect.

## 4. Options

### (a) DPO canonical; SPO kept as the classic semantics on multigraphs

Document SPO as "GROOVE's classic delete-wins semantics, now on
multigraphs": morphism matching, upper-bound bundles, path-test regexprs.
Cost: manual text (grammar 3.12 section, `ParallelMode` explanations —
`ParallelMode.java:34-36` already says "deletion wins"), an SPO-mode
exploration sample (today every GTS-level multigraph test —
`parallel-pump`, `FreshCreatorEdgeTest`, `CacheReconstructionTest` — runs
DPO; SPO has only the three `spoErasers` pins and one matcher test), and
the fix for gh #901 (S7). Risk: users picking SPO with the multiset reading in mind
(S2). Mitigated by documentation and by `matchInjective`, which restores
injective bundles where a rule needs them.

### (b) Make SPO a fully specified multigraph semantics

Two readings. (b1) *Specify what exists*: textbook SPO plus "regexprs are
path predicates" is already a complete, consistent definition — (b1)
collapses into (a) plus a formal paragraph. (b2) *Make SPO multiset-aware*:
edge-injectivity for same-content rule edges (2.1). Code cost is small
(the machinery is DPO's, re-gated), but the result is a third semantics
— the edge half of the gluing condition without the node half or
dangling — with its own quantifier case matrix (which of cases A–F apply
to edges only?), its own critical-pair treatment, and no theory behind it.
This is the "very costly in case distinctions" outcome the 2026-07-27 note
anticipated.

### (c) Drop SPO for multigraphs

`ParallelMode` is unreleased (8.0.0 pending), so the enum value can go
without compatibility debt; `isMulti()` and `isDPO()` merge, the
`spoErasers` fixture and `testSpoModeCollapse` are deleted, seven gates
simplify. Cost: small and mechanical. Risk: **DPO implies
`checkDangling`**, so after (c) there is no multigraph mode with GROOVE's
most common idiom — delete a node together with whatever edges happen to
be incident. Every grammar moving from `none` to multigraphs would have to
rewrite node deletion (or the DPO⇒dangling decision would have to be
reopened, which was explicitly settled as "DPO is the full gluing
condition"). SPO is therefore not a lightweight DPO; it is the only
multigraph mode continuous with `none`.

### Adversarial check on the recommendation

Against (a): "a mode nobody exercises at GTS level is a liability; two
semantics double the documentation and the critical-pair work." True for
documentation; false for critical pairs — the multigraph overlap
enumeration (critical-pair-review, multigraph gaps) is needed for both
modes, and SPO critical pairs are the *original* setting of Welling's
code. The GTS-level coverage gap is real and is the concrete deliverable
of (a).

For (c): "users who want dangling deletion can stay on `none`." Only
until they need parallel edges, at which point (c) forces DPO and its
dangling condition on them — the upgrade path is the point of SPO.

For (b2): "the multiset reading is what the syntax promises." The syntax
(`use:a` + `del:a`) is the same syntax simple-mode users have written for
twenty years with delete-wins meaning; the multiset reading was introduced
by the disjoint allocation as a *DPO* story. Making it an SPO promise
invents a semantics; DPO already provides it for those who want it.

**Recommendation: (a).** Keep DPO as the canonical multigraph semantics
(the one with a theory and the conflict analysis); keep SPO as the
documented classic semantics on multigraphs, with S1–S3 recorded as
properties rather than seams; add SPO GTS-level coverage; fix gh #901. Do not
build (b2). Revisit only if a concrete user need for edge-injective-only
matching turns up — and then consider whether `matchInjective` per rule
already serves it.

## 5. Decision checklist

- [ ] Confirm (a): SPO stays, documented as classic delete-wins on
      multigraphs; DPO stays canonical. ((b2), the third semantics, was
      rejected by the user on 2026-08-22; (c) drop SPO remains the only
      alternative.)
- [ ] Accept S1 (atom vs regexpr counting under SPO `forall`) as
      permanent, with the "atoms are edges, regexprs are path tests"
      wording in the manual.
- [ ] Accept S2 (bundle sizes are upper bounds under SPO; `matchInjective`
      is the opt-in for injective bundles). With (b2) rejected there is no
      alternative short of (c).
- [ ] Naming and default: rename the property and make SPO-multi the
      default for new grammars — see §6 for the advice and the conditions.
- [x] SPO multigraph tests are needed (user, 2026-08-22). Plan, one
      branch `spo-multigraph-tests`: (i) an SPO twin of
      `junit/samples/parallel-pump.gps` in `ExplorationTest` and
      `CacheReconstructionTest` (fresh-edge minting and reconstruction
      have never run under SPO); (ii) `spoErasers.gps` extended with the
      delete-wins twins of the `dpoErasers` quantifier cases
      (`eraseForallEraser`, `eraseForallReader`, `eraseForallOnReader`:
      instances legal, edge erased once), a merger over parallel copies,
      and a `let` assignment next to a same-value twin; (iii) an SPO
      variant of `FreshCreatorEdgeTest`; (iv) a `ChoiceEdgeMatchingTest`
      pin that `del:a` + `a|b` applies on a lone `a` under SPO.
      `DeterminismTest` cannot host any of this (fresh element mints
      change the enumeration signature, see aspect-parallel-edges.md).
- [x] Probe S7 (`checkCreatorEdges`/`rhsIsNAC` creator NAC vs reader twin
      in multigraph mode) — confirmed, filed as gh #901; resolved
      2026-08-24 by pinning the existing semantics rather than changing it
      (see §7, slice 2): neither neutralisation nor rejection.
- [ ] Critical pairs (gh #886): slice 3 must enumerate edge
      identifications for both modes; decide whether SPO strict-confluence
      verdicts are wanted at all, given the theory is DPO's.
- [ ] gh #900 (censored re-match) remains DPO-only; confirm SPO is out of
      its scope.

## 6. Property name and default (advice, 2026-08-22)

The user finds `parallelEdges=none|SPO|DPO` misleading: the key names the
structure axis, the values the semantics axis, and neither exposes the two
dimensions. Proposal on the table: a new key with values `SPO-simple`,
`SPO-multi`, `DPO`, and — since no released grammar carries the key
(`parallelEdges` predates GitHub but was never wired, see
eraser-injectivity.md) — `SPO-multi` as the default.

**Rename: agreed.** Recommended key `semantics`; value names as
proposed, with both dimensions spelled out in every value that has a
choice: `SPO-simple`, `SPO-multi`, `DPO`. Not `DPO-multi`: the suffix
would suggest that `DPO-simple` is a missing option rather than an
excluded one. (DPO on simple graphs is a coherent semantics — the
machinery is gated on the mode, not on pattern simplicity, except the
regexpr edge images at `PlanSearchEngine.java:465` — but it has the
classic oddity that `use:a` + `del:a` can never match, since a simple host
graph has only one such edge; not worth a fourth value now. If it ever
comes, `DPO` can stay as the alias of `DPO-multi`.) Migration: a stale
`parallelEdges=true|false` from an old checkout currently yields a parse
error; with the rename the old key becomes unknown and should be dropped
on 3.12 conversion rather than carried along.

**Default SPO-multi: acceptable for new grammars, not for existing
ones.** A grammar whose graphs contain no parallel edges does *not*
behave the same under `SPO-simple` and `SPO-multi`:

1. *Idempotent creation is gone.* In simple mode `new:a` (and
   `new:flag:f`) next to an existing copy is absorbed — the "ensure this
   edge/flag exists" idiom, common in existing grammars. In multi mode
   every application adds a copy; a loop over such a rule produces an
   unbounded bundle and an infinite state space.
2. *The guard for that idiom* (`checkCreatorEdges`, `rhsIsNAC`) — see
   gh #901. [Resolved 2026-08-24, §7 slice 2: the guard works unchanged
   for the pure-creator idiom in every semantics; only the reader+creator
   shape requires injective matching. No neutralisation.]
3. *Merges* create parallel bundles instead of pooling (2.5).
4. *Cost*: non-simple host graphs carry edge numbers, per-state added-edge
   arrays, and the used-edges set under injective matching; the overhead
   on ordinary simple-graph samples has never been measured.
5. *Fixtures*: 9 grammars under `junit/` set the key; the rest, and all
   `ExplorationTest` state counts, assume the simple default.

Hence the conditions: (i) the 3.12 conversion writes `semantics=SPO-simple`
explicitly into every pre-3.12 grammar — which means dropping the
silent-load shortcut in `LoadGrammarAction.java:98-111`, since every old
grammar now needs a repair and resave — and the `junit` fixtures get the
same line mechanically; an implicit version-dependent default
(absent key + version < 3.12 ⇒ simple) would avoid the edits but hides
the semantics in the version number, which is worse; (ii) gh #901
resolved first [done 2026-08-24, §7 slice 2 — by documentation and tests,
not neutralisation]; (iii) the SPO test plan from §5 landed first;
(iv) a timing comparison of `SPO-simple` vs `SPO-multi` on two or three
standard samples before flipping the default. Given (i)–(iv), the
default for *new* grammars is a free choice and `SPO-multi` is the better
one: it is the general case, and the one place a new user meets the
difference — `new:a` where an `a` may already exist — is exactly where the
unchanged `checkCreatorEdges` guard gives them the simple-mode
"create only if absent" reading in every semantics.

## 7. Migration plan (2026-08-24)

Code analysis for the §6 migration surfaced four facts the conditions
(i)–(iv) did not account for:

1. **Injection cannot precede the default flip.** `Properties.storeEntry`
   (`Properties.java:264`) removes an entry whose value equals the key's
   default, so writing `semantics=SPO-simple` while `SPO-simple` is still
   the default stores nothing. Conversion and default flip must land as
   one atomic slice.
2. **The silent-load shortcut does not self-disable.** The rename happens
   *within* grammar version 3.12 (never released; 8.0.0 pending), so the
   current version does not move and the shortcut at
   `LoadGrammarAction.java:98-114` stays live. It must be deleted
   explicitly.
3. **A `.gps` without `system.properties` skips `repairVersion`**
   (`SystemStore.java:860-870`): it reads as version 1.0 but would get the
   new `SPO-multi` default. The no-file branch needs the same injection
   (only that — moving all of `repairVersion` there would also flip
   `useStoredNodeIds` for such grammars, out of scope).
4. **After the rename, a stale `parallelEdges` key is silently kept as a
   user property** (`Properties.java:277-278`), not rejected. So
   `repairVersion` needs an active translate-and-drop clause, not
   version-gated (dev-era grammars already stamped 3.12 carry the old
   key): `none`→`SPO-simple`, `SPO`→`SPO-multi`, `DPO`→`DPO`,
   unparsable (the old boolean) → dropped.

Fixture census, sharper than §6 item 5: exactly the grammars at version
3.12 are the ones that set `parallelEdges` (10 including the
`spo-multigraph-tests` branch); no 3.12 fixture relies on the absent-key
default, and the load-time injection covers all pre-3.12 fixtures without
on-disk edits.

**Slices** (one branch each, in order):

1. `semantics-key-rename` — pure rename, zero behaviour change. Enum
   `ParallelMode` → `Semantics` (`NONE`→`SPO_SIMPLE("SPO-simple")`,
   `SPO`→`SPO_MULTI("SPO-multi")`, `DPO`); key `parallelEdges` →
   `semantics`, default still `SPO-simple`; the translate-and-drop clause
   of fact 4; user-visible strings (`AspectKind.java:889`,
   `HostModelMorphism.java:256`, the `ignoreRegExp`/`checkDangling` key
   docs, the 3.12 javadoc in `Version.java`); the fixture keys renamed
   in-repo. Based on `spo-multigraph-tests` (whose `FreshCreatorEdgeTest`
   imports `ParallelMode` and whose `parallel-pump-spo.gps` carries the
   key), so that branch merges first.
2. `creator-nac-semantics` — the gh #901 resolution, precondition (ii).
   **Decided 2026-08-24** (branch of the same name): the guard semantics
   is *pinned, not changed*. The implicit creator NACs test for the
   absence of **any** host copy of the created edge under non-injective
   matching, and for the absence of a copy **not already used by the
   match** under injective matching — the latter is not new machinery but
   a consequence of the NAC edge being bound in the same search as the
   pattern edges (`EdgeEmbargo` → `NegatedSearchItem` inlining,
   `PlanSearchEngine.java:298`, plus the shared used-edges set,
   `PlanSearchStrategy.java:591`), the same way NAC nodes avoid
   match-used nodes under injective matching. The interpretation is
   uniform across the three semantics; the earlier distinctness-based
   "neutralisation" proposal was rejected — the non-matched-parts reading
   is coherent under injective matching only, and there it already holds.
   Consequences: the pure-creator "create only if absent" idiom works
   with either guard in every semantics and matching regime; a
   reader+creator rule with a guard never fires under non-injective
   matching (by the pinned semantics, not by accident) and needs
   injective matching to express "add a copy only if none is unread".
   Delivered as: `GrammarKey` documentation for both properties, the
   `CreatorNacTest` matrix over `junit/rules/creatorNac.gps`
   (3 semantics × 3 guard settings × 2 matching regimes × 0/1/2-copy
   hosts), and this note. Both follow-ups are settled (2026-08-24):
   gh #901 is closed with this resolution, and the static diagnosis is
   tracked as gh #904 — a *warning* (never an error, since in SPO-simple
   suppressing the no-op application is the properties' documented
   purpose), which first requires introducing a warning severity in the
   `FormatError` machinery; `EcoreToGraphs` documents the same gap and is
   a second consumer.
3. `semantics-default-flip` — atomic per fact 1: parser default →
   `SPO_MULTI`; `repairVersion` clause injecting explicit
   `semantics=SPO-simple` into pre-3.12 bundles, plus the same injection
   for the no-properties-file case (fact 3); delete the silent-load
   shortcut (fact 2); rewrite the `Version` 3.12 javadoc. Gate before
   merging: the §6 (iv) timing comparison, run as a scratchpad script
   over 2–3 sizeable samples in both modes (state counts must be
   identical; a difference is a semantics leak and a finding in itself).
   If the multi overhead on simple-graph samples is material, the
   fallback is keeping `SPO-simple` as default; slices 1–2 stand
   regardless.

**Slice 3 delivered (2026-08-25, branch `semantics-default-flip`).**
Two more canonical-default-drop consequences surfaced during
implementation: a translated legacy `parallelEdges=SPO` equals the new
default and vanishes from the store, so the injection tracks the
translation in a local variable instead of re-reading the bundle; and an
explicit `semantics=SPO-multi` in a *pre-3.12* file is indistinguishable
from an absent key after load, so the injection would wrongly pin such a
grammar to `SPO-simple` — since the key only exists from 3.12 on, the
two fixtures carrying that anachronism (`spoErasers`,
`parallel-pump-spo`, both stamped 3.0) were restamped to 3.12 rather
than complicating the repair. Fixture handling: the injection covers all
pre-3.12 fixtures; the nine 3.12-stamped fixtures *without* the key
(from the rule-compilation and settings work — among them
`checkCreatorEdges.gps` and `rhsAsNac.gps`, whose expectations are
mode-sensitive) got the explicit `SPO-simple` line, per the
"only where a test's meaning depends on it" recommendation. The
*notable*-key question remains open.

**Timing gate (§6 iv), measured 2026-08-25** — 5 interleaved
fresh-JVM Generator runs per sample per mode, medians of the reported
exploration time:

| sample | states simple/multi | simple | multi | overhead |
|---|---|---|---|---|
| `inheritance` | 756 = 756 | 249 ms | 255 ms | ≈ 0% |
| `append` (append-3-list-8) | 3888 = 3888 | 552 ms | 681 ms | +23% |
| `car-platooning` (start-05) | 110366 ≠ 130896 | 48 µs/state | 48 µs/state | ≈ 0% per state |
| `As-and-Bs-reg-exp-benchmark` | 8240 ≠ unbounded | 0.74 s | diverges | — |

Two of the four samples turned out not to be timing samples but
*semantics* evidence: `car-platooning` grows 19% more states under
`SPO-multi` (unguarded re-creation of existing edges, absorbed in simple
mode), and `As-and-Bs` diverges outright — its `AaB` rule creates
`new:a` with no guard against the existing copy, so every application
mints another parallel edge (§6 item 1 on a real sample). Both confirm
that the conversion pinning pre-3.12 grammars to `SPO-simple` is
mandatory, not cosmetic. On the samples with identical state spaces the
multi overhead is ≈ 0% (`inheritance`, and per-state on
`car-platooning`) to +23% (`append`, attribute-heavy — value-node-rich
hosts are where the non-simple edge store costs). Verdict: acceptable
for a new-grammars-only default; if +23% on attribute-heavy grammars is
judged too much at review, the fallback is reverting the one-line parser
default while keeping the conversion machinery.

External follow-ups once slice 1 merges: the web manual's grammar chapter
and the 8.0.0 release notes still say `parallelEdges`.
