# Handoff: moving the yFiles backend source out of the public repository

State file for a fresh session (written 2026-09-09). Branch `yfiles-private-move` off
`yfiles-extension-loader` (the add-on branch, five commits off `yworks-migration`, all
under review by Arend). Worktree `.claude/worktrees/yfiles-extension-loader`, detached;
re-attach with `git switch yfiles-private-move` there.

## Goal

Three deliverables, in this order:

1. **Add the sharpened licence questions** to "Immediate next steps" in
   `claude/yfiles-migration.md` (item 3, the yWorks questions), and correct that file's
   "License constraints" section where it summarises the SLA too loosely (see "SLA
   findings" below; the §2.4 summary there mentions only distributions, not licensing
   of the application).
2. **Draft the message to yWorks**, as a file `claude/yworks-question-2026-09.md` for
   Arend to send: the situation (GROOVE, Apache 2.0, public on github; the optional
   backend unit; the add-on shape from `claude/yfiles-distribution-options.md`), the
   fact that the backend source has been public since it was written and will be moved
   to a private repository, and the questions listed below. Plain, short, no pleading.
3. **Move `yfiles/` into the private repository** `nl-utwente-groove/yfiles-lib` (which
   Arend still has to create; the move can be prepared as a directory ready to push).

## SLA findings (read on 2026-09-09; the PDF is the authority)

The SLA is `C:\Groove\yfiles\yFilesForJava-SLA-signable.pdf`, 12 pages. The Read tool
refuses it ("password-protected": an owner restriction only); it opens with pypdf and an
empty user password, which is installed for the user (`python -m pip install --user pypdf`):

    python -c "from pypdf import PdfReader; r=PdfReader(r'C:\Groove\yfiles\yFilesForJava-SLA-signable.pdf'); print('\n'.join(p.extract_text() for p in r.pages))"

Never handle a real password; there is none here.

- **§2.4 Academic License**: "Any software application developed under an Academic
  License may not be licensed in whole or in part, to a third party being a commercial
  institution or a party that commercially uses the Software." The `yfiles/` unit is
  part of an application developed under the licence and is published under Apache 2.0
  to everyone. This is the decisive clause; it is independent of obfuscation.
- **§1 Ownership**: the licensee "shall not use or disclose any Software technology,
  idea, algorithm, or information" except what it can document as "generally available
  for use and disclosure by the public without any charge or license". API names are in
  the public reference (docs.yworks.com); usage patterns come from the bundled developer
  guide and demos, which §2.1(b) lists as non-redistributable teaching material.
- **§2.1(c)**: obfuscation's stated purpose is that "it shall no longer be possible to
  use the functionality of the Redistributables via their public API". Public source
  next to the obfuscated jars yields the name mapping for the API subset GROOVE uses;
  the unobfuscated backend jar in the add-on leaks the same mapping less directly.
- **§2.1(d)**: applications "may not expose an API to a third party that will allow them
  to access functionality provided by the Software". The add-on jar's public classes
  (layouter, canvases) are callable by third parties in principle. Not affected by
  where the source lives; reduce the public surface where possible.
- **§2.2.3 Project License**: covers "different editions of the Authorized Application"
  and "an automated build process", so CI builds and the add-on shape are fine. One seat
  (yWorks, 2026-09-09).
- Nothing in the SLA mentions the licensee's own source code otherwise.

Already public: `origin/master` and `origin/yworks-migration` carry `yfiles/` (the unit
was added in phase 2 slice 3). Outside `yfiles/`, `com.yworks` names occur only in
`release/yfiles/pom.xml` (artifact coordinates and the `Obfuscation` annotation class
name for yGuard) and in `claude/phase-2-model-and-ownership.md`,
`claude/yfiles-migration.md`, `claude/yfiles-spike-findings.md` (API names in prose).

## Questions for yWorks (to draft in full)

(a) Subscription status and delivered generation (code is against 3.6.0.1) — carried over.
(b) Does yGuard as in the deployment demo (all library names renamed except yWorks' own
    annotated exclusions and the methods GROOVE overrides), shipped next to GROOVE's
    unobfuscated backend jar, satisfy §2.1(c), given that the backend jar's references
    reveal the renamed names of the API subset GROOVE uses?
(c) May the development licence file ship inside the add-on as the runtime licence, or
    is a deployment licence issued?
(d) Is an add-on distributed separately from GROOVE, as a public github release download
    with the non-commercial notice, still "your own software applications" under
    §2.1(c) and acceptable under §2.4?
(e) NEW, §2.4: GROOVE is Apache 2.0 and public. The backend unit that calls the yFiles
    API was public source until now and is being moved to a private repository. Is the
    rest of GROOVE (which contains no yFiles code and runs without the library)
    unaffected by "may not be licensed ... to a commercial institution"? Is the backend
    source acceptable in a private repository readable by the licensed developer and the
    build automation only?
(f) NEW, §1/§2.1(c): does yWorks require the commits that added the backend source to be
    purged from the public history, or does removal from the current branches suffice?
(g) NEW, §2.1(d): the add-on jar necessarily has public classes (the service provider,
    the canvases GROOVE calls). Is that acceptable as long as GROOVE documents them as
    internal, or must the surface be reduced?
(h) Notice wording: `release/yfiles/include/YFILES-ADDON.md` and the first-run question
    in `gui.AddOnInstaller.confirmInstall`.

## The move

- Target layout of `yfiles-lib` (private): `yfiles-for-java-swing.jar` and the runtime
  licence xml at the root (as `release.yml` already expects), plus the unit as a
  subdirectory `groove-yfiles/` (or the root; decide with the pom paths). Arend creates
  the repository; prepare the content in a local directory and a README.
- `yfiles/pom.xml`: the freshness guard runs `mvn -q install` in `${project.basedir}/..`;
  make the GROOVE checkout path a property (`groove.dir`, default `../code`), and the
  licence directory default the repository root. Eclipse: import as a sibling project;
  workspace resolution still finds the open `groove` project, nothing changes there.
- `release.yml`: after checking out `yfiles-lib`, build the backend from the checkout
  (`mvn -f ../yfiles-lib/groove-yfiles/pom.xml ...` with `-Dgroove.dir=$GITHUB_WORKSPACE/code`),
  everything else as in commit 5fe502bbb.
- `release/yfiles/pom.xml`, `release/do-all.sh`, `release/README.md`, `yfiles/README.md`
  (moves along), `claude/CLAUDE.md` (mentions `yfiles/`), the surefire argLine in
  `pom.xml` and the launch file (unchanged), `.gitignore` (`/yfiles/target/`), the
  architecture test if it references the unit (check `ArchitectureTest`).
- Remove `yfiles/` from the public branches: `yfiles-private-move` first; the earlier
  branches (`yworks-migration`, `master`) only once Arend merges, or by a rewrite (below).
- Memory: `groove-yfiles-migration.md` in the memory directory.

## Rewriting master and purging the old objects (estimate, for scheduling)

Only if yWorks answers (f) with "purge". Mechanical, Opus-level work; roughly half a day
of wall-clock, little of it Fable reasoning:

1. Freeze: all branches merged or rebased as Arend wants them, all worktrees clean; the
   four stale worktrees removed. Nothing else in flight.
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
can afford to be mostly waiting; do the move (item 3 above) before that regardless, since
it stops the exposure on the branch tips at once.
