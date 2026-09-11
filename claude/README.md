# Index of Claude notes

Notes written during Claude sessions: plans, design records, investigations and handoff
state. `CLAUDE.md` is the instruction file loaded into every session; everything else is
read only when pointed at. Live notes sit at this level; notes whose work is finished
move to `archive/`, each with a `*Status (date)*` block under its title that records what
landed where and what, if anything, was explicitly deferred. When work described by a live
note completes, add that block and `git mv` the note to `archive/` (fix links; repo-relative
`claude/<name>.md` references become `claude/archive/<name>.md`).

Status lines below are as of 2026-09-11.

## Live

| Note | Status |
|---|---|
| [yfiles-migration.md](yfiles-migration.md) | gh #909. Authoritative plan and state of the visualization-backend decoupling; license ground rules. In progress on `yworks-migration`. |
| [yfiles-spike-findings.md](yfiles-spike-findings.md) | Phase 0 of gh #909: what the rendering spike showed and what it implied for the facade. Done; kept live as part of the initiative record. |
| [jgraph-controller-split.md](jgraph-controller-split.md) | Phase 1a of gh #909: `JGraph` component/controller split, slice by slice. Done; kept live as part of the initiative record. |
| [view-facade.md](view-facade.md) | Phase 1b of gh #909: the graph-view facade behind which the backend is swappable. Done; kept live as part of the initiative record. |
| [phase-2-model-and-ownership.md](phase-2-model-and-ownership.md) | Phase 2 of gh #909: the model layer and the ownership inversion. Design record, phase closed. |
| [phase-3-editor.md](phase-3-editor.md) | Phase 3 of gh #909: the editor. Design and slice record, in progress. |
| [yfiles-distribution-options.md](yfiles-distribution-options.md) | What a release consists of now that a license-restricted yFiles edition exists: facts, options, decision. |
| [yfiles-private-move-state.md](yfiles-private-move-state.md) | Handoff state for moving the yFiles backend source out of the public repo (branch `yfiles-private-move`, under review). Transient: delete after merge. |
| [yworks-question-2026-09.md](yworks-question-2026-09.md) | Draft message to yWorks on distribution and source of the yFiles backend. Transient: delete once sent. |
| [module-split-plan.md](module-split-plan.md) | gh #887, Maven module split. Phases 1–4 (preparatory decoupling) on master; phase 5, the split itself, not started. The yFiles add-on built the service-discovery and second-artifact machinery along a different seam; rescope proposed on the issue. |
| [io-refactor-plan.md](io-refactor-plan.md) | Four-phase `io` plan of 2026-07-26. Phases 1–3 (conceptual layer removed, io simplified, Ecore porter) on master; phase 4 (constraint rules) not started. gh #907, #558 open. |
| [gh763-810-plan.md](gh763-810-plan.md) | Composite-graph error tracing, gh #763 (gh #810 closed as duplicate). Plan only, none of its six commits done. |
| [priority-symmetric-choice.md](priority-symmetric-choice.md) | gh #880, priority-aware symmetric choice in control. Design points only, not started; interim gh #756 guard is on master. |
| [critical-pair-review.md](critical-pair-review.md) | gh #886, revival of the critical-pair package. Review and plan only, nothing implemented. |
| [fixable-builder-survey.md](fixable-builder-survey.md) | Which `Fixable` implementors should become builders. Step 1 (`AspectLabel`) done 2026-09-06; the rest open, no `Buildable` type. |

## Archive

Finished work, kept for the design rationale and rejected alternatives. Grouped by theme.

**Multigraph semantics and matching**

| Note | Status |
|---|---|
| [aspect-parallel-edges.md](archive/aspect-parallel-edges.md) | Parallel edges at the aspect level (`MULT` aspect). Merged 2026-08-02. Rule-side use of `MULT` deferred by decision. Vocabulary predates the `parallelEdges`→`semantics` rename. |
| [eraser-injectivity.md](archive/eraser-injectivity.md) | DPO identification condition: injective matching of eraser edges and nodes. Merged 2026-08-02; its deferral became gh #900. |
| [multigraph-spo-vs-dpo.md](archive/multigraph-spo-vs-dpo.md) | SPO versus DPO for multigraphs. Decided and implemented 2026-08-24/26 (three-mode `semantics` property, SPO-multi default). gh #901, #904, #906 closed. |
| [regexpr-censored-match.md](archive/regexpr-censored-match.md) | Dynamic censored re-match of regular expressions, gh #900. Merged 2026-08-31. |
| [parallel-edge-serialisation.md](archive/parallel-edge-serialisation.md) | Parallel-edge-preserving GXL serialisation. Implemented 2026-07-18; the writer has since moved to the streaming `GxlListener`. |
| [edge-store-refactoring.md](archive/edge-store-refactoring.md) | `StoreFactory` edge store and per-factory perfect hash, gh #895. All seven steps on master; issue closed 2026-08-29. |
| [iso-edge-bundles.md](archive/iso-edge-bundles.md) | Edge bundles in the isomorphism checker, gh #906. Merged 2026-08-30. Carry-over tracked as gh #886. |
| [created-edge-pooling.md](archive/created-edge-pooling.md) | Cross-event pooling of content-equal created edges, gh #905. Merged 2026-08-31. |
| [rete-retirement.md](archive/rete-retirement.md) | RETE matching engine removed 2026-07-20; tag `rete-final` marks the last tree with it. |

**Exploration**

| Note | Status |
|---|---|
| [exploration-feature-model-plan.md](archive/exploration-feature-model-plan.md) | Feature-model-based exploration configuration: the model, phase plan, decision log. All phases merged 2026-08-11 except the heuristic dimension, deferred (design first). |
| [exploration-feature-model-state.md](archive/exploration-feature-model-state.md) | As-built state and invariants of the same programme. Merged 2026-08-11. |
| [randomness-seeding.md](archive/randomness-seeding.md) | Seedable randomness (`util.Randomness`, `-seed`, gh #897). Merged 2026-08-11, seed recording 2026-08-31, issue closed. |
| [determinism-ferryman-flake.md](archive/determinism-ferryman-flake.md) | Investigation of a `DeterminismTest` flake: cache collapse reorders reconstruction. Fixed by canonical match order, 2026-07-14/17. Explains the test's cache-clearing design. |
| [parout-liveness.md](archive/parout-liveness.md) | Liveness-based control location variables, gh #561. Merged 2026-08-31, issue closed. |
| [recipe-outpar-deletion.md](archive/recipe-outpar-deletion.md) | Recipe out-parameters vs. deletion: a reachable assert in `StateCache`. Option 1 merged 2026-08-29, repaired 2026-08-31; the assert is gone. |
| [sts-retirement.md](archive/sts-retirement.md) | STS package and remote exploration removed 2026-08-02; tag `sts-final`. |

**Structure and dependencies**

| Note | Status |
|---|---|
| [dependency-analysis.md](archive/dependency-analysis.md) | jdeps-based dependency cleanup plan, P1–P3. All merged 2026-08-18; `LayeringTest` whitelist down to one entry. |
| [package-structure-analysis.md](archive/package-structure-analysis.md) | Post-cleanup package survey. Its `RuleModel` recommendation done; gui figures have drifted under the yFiles migration. |
| [formaterror-design.md](archive/formaterror-design.md) | `FormatError` context genericisation and severity, gh #885, #904. Merged 2026-08-18 and 2026-08-31. |
| [rulemodel-split-plan.md](archive/rulemodel-split-plan.md) | `RuleModel` split into eight classes. Merged 2026-08-18; follow-up test coverage gh #893 closed. |

**I/O and settings**

| Note | Status |
|---|---|
| [ecore-porter-design.md](archive/ecore-porter-design.md) | The Ecore importer/exporter (phase 3 of the io plan). Implemented as designed; principle 4 contested by gh #907. |
| [settings-resource-design.md](archive/settings-resource-design.md) | The SETTINGS resource kind and the Ecore mapping schema. Landed 2026-07-31 to 08-05; schema registry since inverted to `ServiceLoader`. Deferred items filed as gh #898. |
