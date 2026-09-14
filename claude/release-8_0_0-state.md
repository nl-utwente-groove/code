# Release 8.0.0: preparation state

Handoff note for the release of GROOVE 8.0.0 from `yworks-migration` (merged into
`master`). Written 2026-09-14; the Maven module split (gh #887) is out of scope.

## Branches and worktrees

All off the `yworks-migration` tip 103f6b74d, worktrees under `.claude/worktrees/`,
detached for review:

- `changelog-8_0_0` (52a16ffce): change notes brought up to date with the installer and
  add-on work; 7.5.3 date corrected to 2 July 2026.
- `release-8_0_0-prep`: PDF manual dropped from the release (`release/include/`,
  both READMEs), bundled README issue link fixed, `claude/website-manual-8_0_0.md`
  (manual proposal), this note.
- Website repository `nl-utwente-groove.github.io`: worktree
  `.claude/worktrees/release-8_0_0`, branch `release-8_0_0` off `main`, **uncommitted**:
  `installing.md` (installers, zips, add-on), `mac.md` (blocked `.dmg`),
  `manual/introduction.md` (7.5.3 -> 8.0.0). Awaiting the answers under "Open decisions".
- Quick reference chart: checked 2026-09-14 and dropped from the release (branch
  `quick-reference` off `release-8_0_0-prep`; website link removed on branch
  `quick-reference-drop` of the website repo). The PDF is a 2012 tutorial poster by Tim
  Molderez (Antwerp) showing GROOVE 4.x; its source was never in any repository, and its
  Simulator screenshot does not render in poppler or pdfium. The copy in the usermanual
  repo is left in place.

## Checklist

Done: change notes; PDF manual removal; website draft; manual proposal; grammar version
already 3.12 (no bump needed).

Before the tag, in order:

1. Merge `yfiles-lib` branch `yworks-migration` into `main` (18 commits ahead); a
   `master` tag resolves to `main` there via `release/github/choose-backend.sh`.
2. Check `YFILES_LIB_TOKEN` has not expired (no skip guard in `release.yml`).
3. `revision` 7.5.4-SNAPSHOT -> 8.0.0 in `pom.xml` and `yfiles-lib/pom.xml`;
   `GROOVE_BUILD` (still 20260702); CHANGES.md heading "Upcoming release (8.0.0)" ->
   release form with date (underline style feeds `release-notes.sh`).
4. Fix `release/github/INSTALL-NOTE.md`: asset name `yfiles-add-on.zip` -> `yfiles-addon.zip`;
   "loaded automatically" -> opt-in prompt. `release/README.md:128` names the READ-ME asset
   with an old file name. `YFILES-ADDON.md` writes the menu label as `yFiles add-on`; the code
   says `YFiles add-on`.
5. Full suite, GUI tests, yFiles backend tests on the merged tip.
6. Decide the version-sensitive issues: gh #911 (indeterminate operators on quantified
   levels silently accepted; reinstating the check is ~1h), gh #819 (ESC commits in the
   JGraph editor), gh #877 restriction half. Consider #917, #915, #907, #898 item 1, #558.
   Close gh #846 (fixed in 7.5.3, never closed); #851 is unreproducible as filed.
7. Website: commit and merge `release-8_0_0` when the release is out (it names 8.0.0 as
   current); manual per `claude/website-manual-8_0_0.md` (gh #896 is the minimum).
8. `Version` javadoc for grammar 3.12 omits `regExpMatching`, `matchBound`, the settings
   resource kind and `ecore` settings.
9. Post-release: bump to 8.0.1-SNAPSHOT, Maven Central deploy, close the issues in the
   change notes, delete `claude/yfiles-private-move-state.md`, download-stats SourceForge
   import and website page.

## Open decisions

Website draft (answer yes/no unless stated):

1. Version-explicit asset names on `installing.md` (used) or generic placeholders?
2. Installers first, zip demoted to "if you want more control"?
3. Keep the one-paragraph summary of the yFiles license terms, or only link `YFILES-ADDON.md`?
4. Maven Central paragraph still accurate for 8.0.0?
5. Should `index.md` mention 8.0.0 or the installers?
6. `index.md` comments ModelChecker out as "not available right now", while the installers
   register a ModelChecker launcher. Restore it?
7. `manual.md`: drop the "Full user manual" PDF link (usermanual repo, 2024)? (The
   quick-reference link is already gone, see above.)
8. "MAC users need to get the Open JDK" kept unchanged. Still true?

Settled without asking: add-on asset name `yfiles-addon`; menu path View > Options >
Graph backend and label `YFiles add-on`; Gatekeeper text on `mac.md`, linked from
`installing.md`; the uninstall-removes-add-on sentence added.

Manual: twelve questions at the end of `claude/website-manual-8_0_0.md`; the blocking ones
are a new Simulator chapter or not, how to resave `manual/graphs.gps`, JGraph or yFiles
for the figures, and whether `ref-exploration.md` becomes generated (`MakeRefs.java`
no longer compiles against 8.0.0).
