# Release 8.0.0: preparation state

Handoff note for the release of GROOVE 8.0.0 from `yworks-migration` (merged into
`master`). Written 2026-09-14, re-checked 2026-09-15, merged with `master` on 2026-09-20;
the Maven module split (gh #887) is out of scope.

## Branches and worktrees

Originally all off the `yworks-migration` tip 103f6b74d; `master` has moved on since and
was merged back in on 2026-09-20. Worktrees under `.claude/worktrees/`:

- `release-8_0_0-prep` (worktree of the same name): everything for the code repository,
  with `changelog-8_0_0` and `release-8_0_0-usage-fix` merged in (2026-09-15): the change
  notes, PDF manual and quick reference chart dropped from the release, bundled README
  issue link fixed, the manual proposal `claude/website-manual-8_0_0.md`, the checklist
  items 4 and 8 below, the Generator `-x` usage text synchronised with `ExploreKey`
  (bare number forms `<n>` are accepted and are what `unparse` writes), this note.
- Website repository `nl-utwente-groove.github.io`: worktree `.claude/worktrees/release-8_0_0`,
  branch `release-8_0_0` off `main`. Commits: fb02d3a `installing.md`
  (installers first, generic `x_y_z` asset names, add-on section linking `YFILES-ADDON.md`),
  `mac.md` (blocked `.dmg`), `index.md` (ModelChecker restored), `manual.md` (legacy PDF
  section gone), `manual/introduction.md` (8.0.0, installers bundle Java); 455cd12 the
  generated exploration-key reference (manual plan step 1); 2238066 the multigraph
  documentation for gh #896 (step 3, with the fixture grammar and two new figures from
  step 4); 5578456 temporal goals left out of the reference; 68cbc2f the verification
  chapter rewrite (step 2); e26b4c2 all figures re-rendered (step 4); 17860f6 the basics
  and advanced chapters; 0064990 the `.rpm` row of the asset table. Tip bd30077, which
  merged and retired `quick-reference-drop`.
- Merged into `master` since (2026-09-16 to 09-20): `flatlaf-native-access` (launcher-jar
  manifest grants native access, silencing FlatLaf's JEP 472 warning on Java 24+; Surefire
  argLine grant; installers job on JDK 25; same-named branch in `yfiles-lib`),
  `java-requirement-docs` (zip README states Java 21+, change notes and release-page text
  name the Java 25 runtime and the native-access grant), `ecore-dialog-skip` (gh #558,
  #907) and `retire-transient-yfiles-notes`. The website half of `java-requirement-docs`
  (`installing.md`, `manual/introduction.md`) is in neither website branch of this clone,
  so it is presumably still on the other machine; `installing.md` still names Java 21 only.
- Quick reference chart: dropped 2026-09-14 (2012 tutorial poster by Tim Molderez for
  GROOVE 4.x, source never in a repository, Simulator screenshot unrenderable). The copy in
  the usermanual repo is left in place.

## Checklist

Done: change notes (brought up to date with master again on 2026-09-15, after the
merge of master into this branch: gh #819, #877 restriction, Ecore metadata in the
settings resource, rpm installer); PDF manual and quick reference removal; website
draft; manual pass (all six chapters checked against 8.0.0); release-page texts;
`Version` javadoc for 3.12; grammar version already 3.12 (no bump needed);
`yfiles-lib` branch `yworks-migration` merged into `main` (2026-09-14, in sync with
origin); gh #819, #846, #851 closed; gh #911 settled by reinstating the ban; gh #558 and
#907 resolved on `master` (`ecore-dialog-skip`).

Before the tag, in order:

1. Website: reword the transition-label paragraph of `manual/basics.md`, which still
   calls the syntax String.format-like (now `%s`, `%i$s`, `%%` only, gh #877), and carry
   the Java-requirement wording over from the other machine (or redo it): `installing.md`
   still gives Java 21 as the requirement and says nothing of the bundled Java 25 runtime.
   The branch pointer and the `.rpm` asset row, open in the 09-15 version of this note,
   are done (0064990, bd30077).
2. Check `YFILES_LIB_TOKEN` has not expired (no skip guard in `release.yml`).
3. `revision` 7.5.4-SNAPSHOT -> 8.0.0 in `pom.xml` and `yfiles-lib/pom.xml`;
   `GROOVE_BUILD` (still 20260702); CHANGES.md heading "Upcoming release (8.0.0)" ->
   release form with date (underline style feeds `release-notes.sh`).
4. Full suite, GUI tests, yFiles backend tests on the merged tip (not run since the
   merge of master).
5. Remaining issue decisions: #917, #915, the remainders of #877 and #898, and the old
   bugs #869 and #828 are not version-sensitive. Decide whether #909 closes with the
   release or stays open for the JGraph removal.
6. Test-release residue: tags `release-99_0_1` to `_6` locally, `_3`, `_4`, `_6` on
   origin, pre-release 99.0.6 on GitHub; delete before or after tagging.
7. Website: merge `release-8_0_0` when the release is out (it names 8.0.0 as current);
   regenerate the figures and reference pages at the tag (see the manual plan); close
   gh #896.
8. Post-release: bump to 8.0.1-SNAPSHOT, Maven Central deploy, close the issues in the
   change notes, download-stats SourceForge import (the website page is on the website's
   `main`).

## Manual plan

Derived from the answers in `claude/website-manual-8_0_0.md` (§5). Work in the website
worktree `release-8_0_0`, one commit per step; the `manual.md` *Checked against* entry of a
chapter moves to 8.0.0 in the commit that revises the chapter (see the last open decision).

Progress, 2026-09-15: all seven steps are committed on the website branch `release-8_0_0`
(tip 0064990, ten commits ahead of `main`, the last one adding the `.rpm` installer to `installing.md`); every chapter is checked against 8.0.0. Two
things remain: the figures were rendered with a 7.5.4-SNAPSHOT candidate and are to be
regenerated at the tag with the release classpath (`make-figures.sh`, then `make-refs.sh`
for the version string on the reference pages), and the optional yFiles-rendered figure of
step 4 was not made. The editor and display items of the proposal's table (prio C, no
home in the manual) were skipped as planned.

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
   `control.md` DONE (website commit after fad46f1): the priority/control conflict
   (gh #756) under Calls and expressions, mirrored in the recipe bullet and in the
   `priority` bullet of `basics.md`; variable liveness (gh #561) under Variables and
   arguments; recipe call edge cases in the recipe bullet; unavailable procedures in
   disabled or erroneous programs (gh #560). *Checked against* moved to 8.0.0. That group
   calls honour priorities is taken from `Term`'s prioritised choice, not from a run.
6. DONE (website commit after cfed4fe). `introduction.md`: exploration configuration
   instead of a selectable strategy, Generator runs the stored exploration, installers
   register the tools (zip has the `bin` jars), multigraphs in the Graph concept, the
   Simulator bullet carries the error/warning severities and a pointer to the add-on on
   `installing.md`; chapter list updated. *Checked against* moved to 8.0.0. Found on the
   way: `PrologChecker` is an entry-point class but ships neither as a `bin` jar nor as an
   installer launcher (`release/runnable`, `build-installer.sh`), so the sentence claiming
   a PrologChecker tool was taken out of `verification.md` again.

   Remaining for the manual: `basics.md` and `advanced.md` stay at 7.5.3 until their other
   8.0.0 items are done (string escapes and the `'\\?\''` example, remark merging, duplicate
   node ids merge, `use=q:` on `test:`/`let:`, `valueOracle=dialog` Simulator-only, editor
   and display items if wanted); then regenerate the reference pages and figures at the tag.
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
