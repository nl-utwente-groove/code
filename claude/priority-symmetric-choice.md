# Design note: priority-aware symmetric choice (gh #880)

*Status (2026-08-05): design points collected, implementation not started. Follow-up to
gh #756 (= SF FR #195), whose interim fix — the guarded "explicit call of prioritised
action" error — lives on branch `priority-control-conflict`. When this design is
implemented, that error is dropped wholesale and gh #756 is closed.*

## Position (settled in the gh #756 discussion)

Control subsumes priorities: priorities are not global scheduling invariants, but fill in
the scheduling where the control program leaves it unspecified. Consequently they must be
honoured by *symmetric choices* — not only group calls such as `any` (which already
compile to a try-else chain by descending priority, see the `prioMap` in
`CtrlTree.toTerm`), but equally an explicit choice `a | b`. Anything else lets `a | b`
deviate from the group call over {a, b}, which is surely unexpected. Asymmetric
constructs (sequencing, `try`/`else`, `if`, loop bodies) are explicit scheduling and
remain untouched by priorities.

A starting sketch exists: `Term.getPriorityChoice()`, parked in comments by commit
0976f8ab7 — "a mapping from priorities to subterms … used to correctly build choices
between prioritised rules".

## Design points to settle

- **Scope of "symmetric".** Group calls and `|`-choice qualify; sequencing, `try`/`else`,
  `if` and loop bodies do not. Where exactly does `or` arise internally (e.g. the
  compilation of `alap`, `#`, `*`) and does each such occurrence count as a symmetric
  choice?
- **Compositionality.** Whether the priority structure needs to propagate through nested
  `or`-terms only (`(a | b) | c` re-normalising across all three), or through other
  combinators as well. This is what `getPriorityChoice()`'s priority-to-subterm map was
  meant to support.
- **Mixed choices.** Whether a choice mixing prioritised calls with non-call subterms
  (e.g. `a | { b; c }`) is meaningful, and if so, what priority the composite branch
  carries (a block has no declared priority; default 0? the priority of its first
  action?).
- **The priority attribute.** Rule priorities are declared as a graph attribute of the
  rule (the `priority` attribute in the `.gpr`, read via `GraphInfo.getPriority`),
  whereas recipe priorities are declared in the control program itself
  (`recipe r() priority 2 { … }`). Under control-subsumes-priorities the attribute is an
  input to control compilation that lives outside the control text. To settle: does the
  attribute remain the sole declaration mechanism for rules, or should control be able to
  declare (or override) rule priorities symmetrically to recipes? This also touches the
  GUI priority-change actions and `CtrlLoader.changePriority` (gh #733), which already
  rewrite recipe priorities through the control text.
