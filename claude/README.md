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
| [module-split-plan.md](module-split-plan.md) | gh #887, Maven module split. Phases 1–4 (preparatory decoupling) on master; phase 5, the split itself, not started. The yFiles add-on built the service-discovery and second-artifact machinery along a different seam; rescope proposed on the issue. |
| [module-exports-state.md](module-exports-state.md) | Hand-over state for the export work: three branches, merge order, what the Fable review of `view-controller-context` should weigh, session B next. 2026-09-21. |
| [module-exports.md](module-exports.md) | What `module-info` exports now that 8.0.0 keeps it: the export list, why each forced export is there, how the `gui.view` leak was closed by giving the controllers a host context (item 1, with the rejected interface design kept for the record), and the restructurings that would trim further. Branches `module-exports` and `view-controller-context`, 2026-09-21. |
| [io-refactor-plan.md](io-refactor-plan.md) | Four-phase `io` plan of 2026-07-26. Phases 1–3 (conceptual layer removed, io simplified, Ecore porter) on master; phase 4 (constraint rules) not started. gh #907, #558 open. |
| [gh763-810-plan.md](gh763-810-plan.md) | Composite-graph error tracing, gh #763 (gh #810 closed as duplicate). Plan only, none of its six commits done. |
| [priority-symmetric-choice.md](priority-symmetric-choice.md) | gh #880, priority-aware symmetric choice in control. Design points only, not started; interim gh #756 guard is on master. |
| [critical-pair-review.md](critical-pair-review.md) | gh #886, revival of the critical-pair package. Review and plan only, nothing implemented. |
| [fixable-builder-survey.md](fixable-builder-survey.md) | Which `Fixable` implementors should become builders. Step 1 (`AspectLabel`) done 2026-09-06; the rest open, no `Buildable` type. |
| [download-stats.md](download-stats.md) | Download statistics from GitHub releases, SourceForge-style: daily counter snapshots differenced at render time. Repository and collector live since 2026-09-13 (`nl-utwente-groove/download-stats`); SourceForge import and website page open. |
| [website-manual-8_0_0.md](website-manual-8_0_0.md) | gh #896 and the 8.0.0 release: what the web user manual (website repo, `manual/`) needs, change by change, plus the wrong statements and the broken reference-page generator. Proposal only, nothing written yet. |
| [release-8_0_0-state.md](release-8_0_0-state.md) | Handoff state for the 8.0.0 release: branches, ordered checklist before the tag, open website and manual decisions. Transient: delete after the release. |
| [factory-user-leak.md](factory-user-leak.md) | gh #919, memory leak in `util.Factory` dependency tracking: rule applications retained by the grammar (finding 3.11 of the exploration performance review). Fixed by weak user sets plus plain fields in `RuleApplication` on `factory-user-leak`, awaiting review; resettable-versus-frozen factories filed as gh #920. |
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
| [ecore-metadata-settings.md](archive/ecore-metadata-settings.md) | gh #898 item 1: the Ecore round-trip metadata moved from type-graph properties into the `ecore` settings resource, fixing the hand-added-type export trap. Merged to master 2026-09-15; a stale-entry prune is item 5 of gh #898. |

**Release and installers**

| Note | Status |
|---|---|
| [installer-files-in-use.md](archive/installer-files-in-use.md) | Windows MSI with GROOVE running: why `DisableShutdown` failed with the real app (jars not renameable, per-user, `RemoveExistingProducts` outside the transaction) and the close-GROOVE prompt that replaced it. Verified with the real installer 2026-09-14; on `installer-files-in-use`. |
