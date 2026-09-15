# Release 8.0.0: preparation state

Handoff note for the release of GROOVE 8.0.0 from `yworks-migration` (merged into
`master`). Written 2026-09-14; the Maven module split (gh #887) is out of scope.

## Branches and worktrees

All off the `yworks-migration` tip 103f6b74d, worktrees under `.claude/worktrees/`:

- `release-8_0_0-prep` (worktree of the same name): everything for the code repository,
  with `changelog-8_0_0` and `release-8_0_0-usage-fix` merged in (2026-09-15): the change
  notes, PDF manual and quick reference chart dropped from the release, bundled README
  issue link fixed, the manual proposal `claude/website-manual-8_0_0.md`, the checklist
  items 4 and 8 below, the Generator `-x` usage text synchronised with `ExploreKey`
  (bare number forms `<n>` are accepted and are what `unparse` writes), this note.
- Website repository `nl-utwente-groove.github.io`: worktree `.claude/worktrees/release-8_0_0`,
  branch `release-8_0_0` off `main`, detached for review. Commits: fb02d3a `installing.md`
  (installers first, generic `x_y_z` asset names, add-on section linking `YFILES-ADDON.md`),
  `mac.md` (blocked `.dmg`), `index.md` (ModelChecker restored), `manual.md` (legacy PDF
  section gone), `manual/introduction.md` (8.0.0, installers bundle Java); 455cd12 the
  generated exploration-key reference (manual plan step 1); 2238066 the multigraph
  documentation for gh #896 (step 3, with the fixture grammar and two new figures from
  step 4); 5578456 temporal goals left out of the reference; 68cbc2f the verification
  chapter rewrite (step 2); e26b4c2 all figures re-rendered (step 4). Branch `quick-reference-drop` (worktree of the same name) is
  subsumed by this and can be deleted.
- Quick reference chart: dropped 2026-09-14 (2012 tutorial poster by Tim Molderez for
  GROOVE 4.x, source never in a repository, Simulator screenshot unrenderable). The copy in
  the usermanual repo is left in place.

## Checklist

Done: change notes; PDF manual and quick reference removal; website draft; manual
proposal with answers; release-page texts (item 4); `Version` javadoc for 3.12 (item 8);
grammar version already 3.12 (no bump needed).

Before the tag, in order:

1. Merge `yfiles-lib` branch `yworks-migration` into `main` (18 commits ahead); a
   `master` tag resolves to `main` there via `release/github/choose-backend.sh`.
2. Check `YFILES_LIB_TOKEN` has not expired (no skip guard in `release.yml`).
3. `revision` 7.5.4-SNAPSHOT -> 8.0.0 in `pom.xml` and `yfiles-lib/pom.xml`;
   `GROOVE_BUILD` (still 20260702); CHANGES.md heading "Upcoming release (8.0.0)" ->
   release form with date (underline style feeds `release-notes.sh`).
4. Full suite, GUI tests, yFiles backend tests on the merged tip.
5. Decide the version-sensitive issues: gh #911 (indeterminate operators on quantified
   levels silently accepted; reinstating the check is ~1h), gh #819 (ESC commits in the
   JGraph editor), gh #877 restriction half. Consider #917, #915, #907, #898 item 1, #558.
   Close gh #846 (fixed in 7.5.3, never closed); #851 is unreproducible as filed.
6. Website: merge `release-8_0_0` when the release is out (it names 8.0.0 as current);
   manual per the plan below (gh #896 is the minimum).
7. Post-release: bump to 8.0.1-SNAPSHOT, Maven Central deploy, close the issues in the
   change notes, delete `claude/yfiles-private-move-state.md`, download-stats SourceForge
   import and website page.

## Manual plan

Derived from the answers in `claude/website-manual-8_0_0.md` (§5). Work in the website
worktree `release-8_0_0`, one commit per step; the `manual.md` *Checked against* entry of a
chapter moves to 8.0.0 in the commit that revises the chapter (see the last open decision).

1. DONE (455cd12). `MakeRefs.java` generates "Reference: exploration keys" from
   `ExploreKey` and its setting kinds: overview table, one section per key with the content
   each alternative takes (descriptions mirror the hints of `ExploreConfigDialog`, which does
   not expose them; `Goal.GRAPH` omitted as a future extension), and the legacy `-s`/`-a`/`-r`
   options as the Generator's usage strings verbatim. `make-refs.sh` no longer needs
   `GROOVE_SRC`: the version comes from `Version.getCurrentGrooveVersion()` of the build
   (the source file it used to read holds the unfiltered `${revision}`). All four pages
   were regenerated from a 7.5.4-SNAPSHOT build of the candidate; regenerate at the tag
   with the release classpath so the pages say 8.0.0.
2. DONE (68cbc2f). `verification.md`: the key table is replaced by a link to the generated
   page plus a prose account of how the keys combine; settings resources and the rebuilt
   dialog, `explorationStrategy` migration, new sections on determinism/master seed and
   on `matchBound`, Generator `-seed`/`-log`/`-D exploration`, `-s`/`-a`/`-r` as shorthand
   kept indefinitely, CTL caveat dropped, LTL lassos and `transitionLabel`, `ModelChecker
   -ltl` gone, `show_graph` Simulator-only. *Checked against* moved to 8.0.0. Two
   findings: `goal=ltl`/`ctl` are rejected by `ExploreTypeConverter` ("handled by the
   model checking actions"), so they are omitted from the reference page along with
   `goal=graph`; and the legacy `-s ltl:` strategies map to `LTLExploreType`, not to a
   configuration, so the manual sends command-line LTL checking through `-s ltl:prop`.
   Not verified by running the tool: the Settings tab name and that enabling a settings
   resource in the resource list switches the `exploration` property (both taken from
   the change notes and `ResourceKind`).
3. DONE (2238066). `basics.md`: "Parallel edges" section under Graphs, a reader-plus-eraser
   paragraph under Rules, DPO eraser injectivity under Injectivities; `advanced.md`: property
   table (`semantics`, `regExpMatching`, `matchBound`, `checkDangling` implied by DPO,
   `exploration` names a settings resource, Ecore rows gone) and the explanatory bullets
   rebuilt around the transformation semantics. The *Checked against* entries stay at 7.5.3:
   both chapters still have other 8.0.0 items open (string escapes, remark merging, duplicate
   node ids, `use=q:` on `test:`/`let:`, priority/control conflict). gh #896 can be closed
   once the branch is merged; it still names the property `parallelEdges`.
4. DONE (2238066 and the figure commit after 68cbc2f): `graphs.gps/system.properties`
   hand-written at grammar version 3.12 with `semantics=SPO-multi` (obsolete
   `subtypes`/`enableControl` keys dropped); new fixtures `parallel-edges.gst` and
   `parallel-rule.gpr` (edge bend points were needed, since parallel edges between the
   same nodes are drawn on top of each other; `multiple-edges-left/right.gst` show
   differently labelled edges, not parallel copies, and stay unused). All 70 figures
   re-rendered with `make-figures.sh` against the candidate build: 53 SVGs differ, none
   in label text, only half-pixel shifts, filled embargo arrow heads and straightened
   edges between aligned nodes; the manual text needed no change (the `!a` display note
   in `basics.md` still holds: negations remain italic edges). Regenerate once more at the
   tag with the release classpath. Of the three expected changes (straight edges between
   axis-aligned nodes, filled embargo arrow heads, `!a` no longer rendered as a regular
   expression) the third did not show. One yFiles-rendered figure only if it shows a visible difference:
   with a stored layout the two backends differ cosmetically (fonts, arrow heads, label
   placement), so the candidate is an automatic layout such as hierarchic or organic of a
   larger graph, rendered with `Imager -b yfiles` on a machine with the add-on installed.
   Untested expectation.
5. `io.md` DONE (website commit after e26b4c2): settings resources (table row, paragraph
   on schemas and the `system.properties` rename), layout bullet freed of JGraph, `edgeids`
   bullet, native porter covers text resources and settings, headless versus GUI-side
   exporters and UTF-8/LF, Ecore section rewritten from `EcorePorter`/`EcoreToGraphs`
   javadoc and `EcoreKey` (options `ordering`, `useIdentifiers`, per-feature ordering,
   `typeName`, `literalStyle`), LTS export extensions and defaults, gh #854/#861, Imager
   `-b`. *Checked against* moved to 8.0.0. Not verified by running the tool: the dialog
   offered at import/export time and the Settings-tab template (from the change notes).
   `control.md`: the B items of the table in §2 of the proposal — still to do.
6. `introduction.md`: the four wrong statements of §3 of the proposal; no new Simulator
   chapter, no add-on or extension-directory material (that stays on `installing.md`); the
   format-error severities get a paragraph where errors are first mentioned, not a section.
7. `_data/glossary.yml` and `_data/definitions.yml` are referenced nowhere in
   `_includes`, `_layouts` or `_config.yml` (checked 2026-09-14): leave them.

## Open decisions

- "MacOS ships no Java of its own, so on a Mac the `zip` route requires you to get the
  Open JDK" (`installing.md`): reworded to scope the claim to the zip route, otherwise
  unverified. To find out: have a colleague on a Mac without a JDK try both the `.dmg`
  (should run without any Java) and the zip; the only open question is whether Apple's
  bundled `java` stub still misleads, which such a test settles.
- The *Checked against* column of `manual.md` records, per chapter, the tool version the
  chapter was last verified against. The question was whether to move an entry to 8.0.0 as
  soon as that chapter is revised, or to set all six to 8.0.0 at release time. Per chapter
  is honest (a chapter not yet revised still says 7.5.3) at the cost of a mixed table
  until the manual pass is complete; in one go is tidy but claims checks that were not
  done. The plan above assumes per chapter.
- Item 5 of the checklist: which of the version-sensitive issues go into 8.0.0.
