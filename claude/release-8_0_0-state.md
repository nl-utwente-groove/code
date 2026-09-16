# Release 8.0.0: preparation state

Handoff note for the release of GROOVE 8.0.0 from `yworks-migration` (merged into
`master`). Written 2026-09-14; the Maven module split (gh #887) is out of scope.

## Branches and worktrees

All off the `yworks-migration` tip 103f6b74d, worktrees under `.claude/worktrees/`:

- `changelog-8_0_0` (52a16ffce, detached for review): change notes brought up to date with
  the installer and add-on work; 7.5.3 date corrected to 2 July 2026.
- `release-8_0_0-prep` (9169da1b2, held by the main checkout): PDF manual and quick
  reference chart dropped from the release, bundled README issue link fixed, the manual
  proposal `claude/website-manual-8_0_0.md`, this note.
- `release-8_0_0-fixes`, off 9169da1b2 in the worktree `release-8_0_0-prep` (the worktree
  could not re-attach to the branch while the main checkout holds it): the checklist
  items 4 and 8 below, the Generator `-x` usage text synchronised with `ExploreKey`, and
  the answers to the open questions folded into the two notes (the manual note verbatim,
  this note rewritten with the answers applied). Meant to be fast-forwarded onto
  `release-8_0_0-prep`; the uncommitted answer edits in the main checkout are superseded by
  its last commit and can be discarded there first.
- Website repository `nl-utwente-groove.github.io`: worktree `.claude/worktrees/release-8_0_0`,
  branch `release-8_0_0` off `main`, committed (fb02d3a): `installing.md` (installers first,
  generic `x_y_z` asset names, add-on section linking `YFILES-ADDON.md`), `mac.md`
  (blocked `.dmg`), `index.md` (ModelChecker restored), `manual.md` (legacy PDF section
  gone), `manual/introduction.md` (8.0.0, installers bundle Java). Branch
  `quick-reference-drop` was merged into it on 2026-09-15 (no content change) and deleted,
  worktree included.
- `flatlaf-native-access` (off master, 2026-09-16, awaiting review): launcher-jar manifest
  grants native access (silences FlatLaf's JEP 472 warning on Java 24+), Surefire argLine
  grant, installers job on JDK 25; same-named branch in `yfiles-lib`.
- `java-requirement-docs` (off master, 2026-09-16): Java-requirement wording that follows
  from it: zip README states Java 21+, change notes and release-page text name the Java 25
  runtime and the native-access grant, this note and `claude/CLAUDE.md`. Same-named branch
  in the website repository off `release-8_0_0` (`installing.md`, `manual/introduction.md`).
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
4. Merge `flatlaf-native-access` (code and `yfiles-lib`) and `java-requirement-docs`; then
   full suite, GUI tests, yFiles backend tests on the merged tip.
5. Decide the version-sensitive issues: gh #911 (indeterminate operators on quantified
   levels silently accepted; reinstating the check is ~1h), gh #819 (ESC commits in the
   JGraph editor), gh #877 restriction half. Consider #917, #915, #907, #898 item 1, #558.
   Close gh #846 (fixed in 7.5.3, never closed); #851 is unreproducible as filed.
6. Website: merge `release-8_0_0` when the release is out (it names 8.0.0 as current);
   manual per the plan below (gh #896 is the minimum).
7. Post-release: bump to 8.0.1-SNAPSHOT, Maven Central deploy, close the issues in the
   change notes, download-stats SourceForge
   import and website page.

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

1. `MakeRefs.java`: replace `writeExploration()` by a page "Exploration keys" generated
   from `explore.feature.ExploreKey` and its setting kinds (name, explanation, content
   type, default marked), keeping a hand-written legacy `-s`/`-a` table at the end of the
   same page, labelled as legacy shorthand. Rename the sidebar entry and the `manual.md`
   bullet accordingly. Then regenerate all four reference pages against an 8.0.0 build
   (`make-refs.sh`; version string from `GROOVE_SRC`).
2. `verification.md`: drop the hand-written key table in favour of a link to the generated
   page (the maintainer wants to see the result before deciding), rewrite the Simulator
   exploration part for settings resources and the rebuilt dialog, add `-seed`/`-log`,
   correct the `-s`/`-a` status, remove `ModelChecker -ltl`, drop the CTL completeness
   caveat. `goal=graph:<name>` is a future extension: do not document it.
3. `basics.md`: new section on simple graphs versus multigraphs, `mult=k:`, and the
   rule-side behaviour (gh #896); `advanced.md`: new section on transformation semantics
   (`SPO-simple`, `SPO-multi`, `DPO`, DPO injectivity), the property table rows for
   `semantics`, `regExpMatching`, `matchBound`, the `exploration` row corrected, the two
   Ecore rows removed, `parallelEdges`/`ignoreRegExp` mentions replaced.
4. `manual/graphs.gps`: resave at grammar version 3.12 with `semantics=SPO-multi` (it is
   illustration material and must be able to hold parallel edges); draw the `mult=k:` and
   DPO example graphs (the unused `multiple-edges-left/right.gst` fixtures are a start);
   re-render with `make-figures.sh` using JGraph and diff the SVGs (expected changes: straight
   edges between axis-aligned nodes, filled embargo arrow heads, `!a` no longer rendered as
   a regular expression). One yFiles-rendered figure only if it shows a visible difference:
   with a stored layout the two backends differ cosmetically (fonts, arrow heads, label
   placement), so the candidate is an automatic layout such as hierarchic or organic of a
   larger graph, rendered with `Imager -b yfiles` on a machine with the add-on installed.
   Untested expectation.
5. `io.md`: settings resources row and section, Ecore rewrite (no conceptual layer, options
   in the `ecore` settings resource), headless exporter changes, Imager `-b`, one-liners
   for the C items. `control.md`: the B items of the table in §2 of the proposal.
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
