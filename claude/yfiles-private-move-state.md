# Handoff: moving the yFiles backend source out of the public repository

State file, updated 2026-09-09 after the move. Branch `yfiles-private-move` off
`yfiles-extension-loader` (the add-on branch, under review by Arend). Worktree
`.claude/worktrees/yfiles-extension-loader`, detached for review; re-attach with
`git switch yfiles-private-move` there.

## Done (this branch, three commits after the handoff commit)

1. `claude/yfiles-migration.md`: the "License constraints" section now quotes §2.4, §1,
   §2.1(c)/(d) and §2.2.3 as read, and "Immediate next steps" carries the eight questions
   (a)-(h) plus the private-repository item.
2. `claude/yworks-question-2026-09.md`: the message for Arend to send.
3. The move: `yfiles/` and the Eclipse launch file are removed from the branch; every
   reference to the unit's location now points at `../yfiles-lib/groove-yfiles/pom.xml`
   (`release.yml`, `release/do-all.sh` via `YFILES_LIB`, `release/README.md`, the release
   pom comments, the Claude permission patterns, the migration and options notes). The
   unit itself, with its 50-commit history (`git subtree split`), is a local git
   repository at `C:\Groove\yfiles-lib` (branch `main`, one commit on top of the
   history): root `README.md`, `groove-yfiles/` with the pom changed to default
   `yfiles.license.dir` to the repository root and to find GROOVE through a new
   `groove.dir` property (default `../../code`). `mvn -q -f C:/Groove/yfiles-lib/groove-yfiles/pom.xml
   -Dgroove.install.skip=true -DskipTests package` passes.

## Next (Arend)

- Create the private repository `nl-utwente-groove/yfiles-lib`, push `C:\Groove\yfiles-lib`
  (`main`), commit `yfiles-for-java-swing.jar` and the runtime licence xml at its root
  (the pom's resource include takes every `*.xml` at the root, so nothing else with that
  extension may sit there), set `YFILES_LIB_TOKEN`, dry-run a release tag from a branch.
- Eclipse: delete the old nested `groove-yfiles` project from the workspace, import
  `C:\Groove\yfiles-lib\groove-yfiles` in its place; the launch configuration moved along.
- Send `claude/yworks-question-2026-09.md`; attach the notice and the first-run text.
- Merge this branch after `yfiles-extension-loader`; `yworks-migration` and `master` keep
  `yfiles/` until then. `origin/master` and `origin/yworks-migration` remain public with
  the unit until the merge is pushed.

## SLA findings (read on 2026-09-09; the PDF is the authority)

Recorded in "License constraints" of `claude/yfiles-migration.md`. The SLA is
`C:\Groove\yfiles\yFilesForJava-SLA-signable.pdf`, 12 pages; the Read tool refuses it
(owner restriction only), pypdf with an empty user password reads it:

    python -c "from pypdf import PdfReader; r=PdfReader(r'C:\Groove\yfiles\yFilesForJava-SLA-signable.pdf'); print('\n'.join(p.extract_text() for p in r.pages))"

Outside the unit, `com.yworks` names occur only in `release/yfiles/pom.xml` (artifact
coordinates and the `Obfuscation` annotation class name for yGuard) and as API names in
prose in `claude/phase-2-model-and-ownership.md`, `claude/yfiles-migration.md`,
`claude/yfiles-spike-findings.md`.

## Rewriting master and purging the old objects (estimate, for scheduling)

Only if yWorks answers question (f) with "purge". Mechanical, Opus-level work; roughly
half a day of wall-clock, little of it Fable reasoning:

1. Freeze: all branches merged or rebased as Arend wants them, all worktrees clean; the
   stale worktrees removed. Nothing else in flight.
2. Fresh clone with all branches; `git filter-repo --path yfiles --invert-paths` (also
   `--path claude/yfiles-spike-findings.md` etc. if the prose is to go; decide first).
   Every commit since phase 2 slice 3 (early September, ~150 commits over the branch
   chain) gets a new SHA.
3. Force-push every branch (`master`, `yworks-migration`, the review branches); release
   tags are older than the unit and keep their SHAs, so they are unaffected.
4. Ask GitHub support to purge unreachable objects and cached views (the old commits stay
   reachable by SHA until then, and forks would keep them; there are none known).
5. Re-clone or hard-reset every local checkout and worktree; the main checkout at
   `C:\Groove\code` must be reset by Arend himself (no session may touch it).
6. Stale SHAs in `claude/*.md` and in the memory files: leave them, note the rewrite date
   in `yfiles-migration.md`.

Schedule it after the yWorks answer and before the next release tag, in a session that
can afford to be mostly waiting. Note that `git subtree split` walked all 5856 commits
and took about 35 minutes; `git filter-repo` is faster but budget for it.
