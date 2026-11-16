# Restructuring the yFiles integration after yWorks' rejection of the add-on

Companion to `yfiles-migration.md` (gh #909) and `yfiles-distribution-options.md`. The
latter's decision of 2026-09-09 (one standard distribution plus a separately downloadable
yFiles add-on, loaded from a user-level extension directory and installed by GROOVE
itself) was **rejected by yWorks on 2026-10-28 and, after a request for clarification,
again on 2026-10-30**. This note records what they object to, why, and the shape GROOVE
has to take instead. Written 2026-10-30, before any of it is built; the reply to yWorks
that sketches the new shape was sent the same day. yWorks confirmed it on 2026-11-13; the
path that follows from it, and how far it has been walked, is in the last two sections.

## What yWorks said

Pre-release 99.0.8 was offered to yWorks for inspection on 2026-10-21. Their technical
team looked at the add-on and did not approve it, "as along with it there is unfortunately
an API introduced that wraps the actual yFiles API". The second mail is more explicit:
they "want to avoid the situation where any such generic interface layer not only comes
in handy for the original application it was intended for, but suddenly also for many
other applications from third-parties", who would then use the yFiles library "at no cost
as well".

Their prescription, stated twice in the same words:

- Abandon the separate add-on. Use the yFiles API directly from the application's own
  source code.
- For the binaries, have yGuard fully obfuscate the yFiles jar *and* all calls to the
  yFiles API in the application's code. The binaries may then consist of several jars,
  one of them the obfuscated library.
- The application's source code, including the yFiles calls, may be published. Anyone
  who wants to build GROOVE from source adds a yFiles library of their own.
- The unobfuscated library must never be made available to any third party.
- Licensing (28 October): advertising GROOVE as Apache 2.0 while making clear that the
  yFiles library is not covered by it is fine; binaries containing the library must carry
  the same restriction to purely academic use as the yFiles licence.
- A new pre-release must be inspected by yWorks before any public release.

## What they actually object to

The add-on zip is, in effect, a yFiles wrapper library, however well yFiles itself is
obfuscated inside it. It holds the obfuscated library next to `groove-yfiles-*.jar`,
whose classes keep their readable names (the yGuard configuration keeps all of
`nl.utwente.groove.**` so that the service loader finds the provider) and implement
`GraphBackend`, the canvases, view models and layouters of `gui.view`: a graph
visualisation interface that is library-neutral by design, exported from the GROOVE
module, documented, and Apache-licensed in an artifact on Maven Central. The GROOVE core
even ships the loader that picks such jars up from a well-known directory. Any Swing
application could therefore depend on `groove` and the add-on and get yFiles rendering
and layout algorithms through readable interfaces without a yFiles licence.

The SLA names this directly: §2.1(d) forbids exposing "an API to a third party that will
allow them to access functionality provided by the Software", and §2.1(c) states the
purpose of obfuscation as making the functionality unusable "via their public API". The
licence-constraints section of `yfiles-migration.md` flagged exactly this as question (g)
to yWorks; the add-on shape answered it the wrong way. What the clause protects is
yWorks' revenue model, one paid licence per application that embeds yFiles: obfuscation
makes the embedded library useless outside that application, and a readable adapter over
a generic interface restores the usefulness.

The separate zip, the separate licence notice, the private source and the sentence "the
GROOVE license does not apply to this add-on" in `LICENSE.txt` all made the add-on look
like a second product with a life of its own, which, after the CTLViz episode of
September, is the pattern yWorks is watching for.

Technically the distance between the add-on and the required shape is small: the
obfuscated jars in `lib/` of an installed GROOVE are just as extractable, and the seam
between the backends remains. What changes is the artifact boundary: no published thing
*is* a component, the restricted licence covers the whole binary, and the plug-in
mechanism is gone. yWorks reasons at the artifact level, and an internal abstraction over
a library is something every one of their customers has.

## The required shape

Largely a rollback to the layout before 2026-09-09, when the backend was the `yfiles/`
unit of the public repository.

1. **Backend source back into the public repository**, as a reactor module `yfiles/`
   (JPMS module `nl.utwente.groove.yfiles`) activated by the existing `yfiles` Maven
   profile, so that `git clone && mvn package` still builds the JGraph-only GROOVE for
   contributors without a licence. It stays a separate compilation unit only for that
   reason: a single module cannot conditionally `require` the library. The private
   repository `nl-utwente-groove/yfiles-lib` shrinks to the plain jar and the licence
   files and stays private. The backend source was public until September, so there is
   no history to purge; the 2026-09-10 history rewrite that removed `yfiles/` was, in
   hindsight, unnecessary.
2. **Obfuscated jars into `lib/`** of the zips and the installers' app image. The yGuard
   step of `release/yfiles` already does what yWorks ask (library renamed, the backend's
   references rewritten, mapping log unpublished); only its output moves from an add-on
   zip into the distribution. Optionally let yGuard rename the backend's own classes as
   well, since nothing outside the jar needs their names any more; the services file
   would then have to be rewritten too (yGuard's `adjust` element, untried).
3. **Remove the add-on machinery**: `util.Extensions`, `util.AddOn`,
   `gui.AddOnInstaller`, the View-menu submenu and first-run prompt, `AddOnTest` and
   `AddOnGuiTest`, `release/yfiles/addon.xml`, the msi add-on cleanup fragment, the
   version-drift guards of the backend pom and the `-Dgroove.addon.prompt` property of the
   test configurations. Replace the `gui.view`, `gui.view.cell`, `gui.look` and
   `gui.layout` exports of `module-info` by qualified exports to the backend module. This
   is a statement of intent, not protection (class-path users ignore module boundaries),
   but it is what yWorks will look for.
4. **Distribution and licence.** Installers and zips contain yFiles and carry the
   academic, non-commercial restriction: in `LICENSE.txt`, on the installer's licence
   page and on the download page. The source stays Apache 2.0. One plain `bin.zip`
   without yFiles, under Apache 2.0, is kept for non-academic users if yWorks accept
   it (asked in the reply of 2026-10-30; a strict reading of SLA §2.4 could cover a
   yFiles-free binary of an application "developed under" the licence); if they do not,
   non-academic users build from source and nothing else changes. Two full sets of
   installers (option C of the distribution note) are not worth it. A single installer
   with yFiles as an optional feature remains impossible: the installer itself would be
   the restricted artifact.
5. **CI.** `release.yml` keeps the private checkout for the jar and licence file only
   and builds the backend from this repository; `backend.yml` here and `test.yml` in the
   private repository collapse into one push-triggered workflow with the secret. Pull
   request builds from forks keep building without the profile and cannot test the
   backend, as now; the push-triggered workflow and review catch seam changes that break
   it.
6. **Website**: the yWorks attribution they asked for in September 2025.
7. **New pre-release** for yWorks' inspection before anything public.

## What a contributor without a licence can do

Everything except work on the yFiles backend itself: edit the core, run the Simulator
with the JGraph backend, run the test suite, open a pull request. The `GraphBackend`
service loader simply finds one provider. Someone wanting to work on the backend needs
the library in the local Maven repository, and yWorks have said such co-developers
should contact them for a licence. In Eclipse the `yfiles` module is imported only by
those who have the jar.


## Confirmation and decisions (2026-11-13 to 16)

Thomas Wurst's reply of 2026-11-13 confirms the sketch point by point, without amendment:

- "yFiles backend" is understood as the Java code within GROOVE that integrates the
  library and where all calls to the yFiles API happen; it "can go into the public GROOVE
  repository, no problem".
- The binaries are run through yGuard, which obfuscates the library jar and adjusts the
  uses of its API in GROOVE's code.
- "The interface in GROOVE between the two rendering backends can stay in place. An
  external add-on being a usable component on its own is what we cannot allow."
- Apache 2.0 for the source, the academic non-commercial restriction on binaries with
  yFiles in them, and a binary without yFiles under Apache 2.0 all "sound good".
- A pre-release is to be offered for inspection before any public release.

The development-key exposure reported on 2026-10-21 was not mentioned in any of the three
replies. Arend's decision (2026-11-16): leave it at that and do not raise it again.

Decided by Arend on 2026-11-16: the zip that keeps the name `groove-x_y_z-bin.zip` is the
plain Apache one; the zip with yFiles is `groove-x_y_z-yfiles-bin.zip`. An installer has
a licence page to accept and a zip has none, so the name people download by habit stays
unrestricted.

Two corrections to *The required shape*, found when starting on it:

- Item 1 speaks of a reactor module under an existing `yfiles` profile of the main build.
  There is no such profile: the main pom is a single jar project and cannot aggregate
  modules, and the `yfiles` profile is that of the release reactor. The unit is a
  standalone Maven project in `yfiles/`, as it was before 2026-09-09 and as it was in the
  private repository.
- Item 3 speaks of qualified exports to the backend module. The unit has no module
  descriptor (it runs on the class path, as the installed application does), so there is
  no module to export to. Either the unit becomes a named module, which needs a module
  name for the library jar that its documentation does not state, or the four packages
  simply stop being exported and the backend reaches them as class-path code does. To be
  settled in step 2.

## Path and state

Each step is a branch off `master`; 1 to 3 touch the same files and go in this order.

1. **Backend source into `yfiles/`.** Built 2026-11-16, branch `yfiles-backend-return`:
   a plain copy of the tree of `yfiles-lib` `main` (0012def), never its history, which
   tracks the plain jar and both licence files. Both workflows and the release scripts
   build `yfiles/pom.xml`; the private repository is the source of the jar and the
   licence only, and a same-named branch `yfiles-backend-return` there removes everything
   else from it (to be merged after this one is on `master`). The licence directory
   defaults to the clone of the private repository next to the main checkout.
2. **Remove the add-on machinery** (item 3 above), and with it the `GROOVE-Version`
   manifest entry of the unit's jar and the extension-directory isolation of its tests.
3. **Distribution**: obfuscated jars in `lib/` of `-yfiles-bin.zip` and of the
   installers' app image; most of it is the edition build of 2026-09-09 (`yfiles-edition`
   in the history, described under phase 5 of `yfiles-migration.md`). Checks: the unit's
   tests against the obfuscated jars, no yFiles entry in the plain zip, the shipped
   licence is the distribution one, the yGuard mapping in no artifact.
4. **Licence texts**: `LICENSE.txt`, the notice in the binaries, the installer's licence
   page, the release-page text, `CHANGES.md`. Wording to be reviewed by Arend against the
   SLA.
5. **CI**: what is left after step 1, chiefly the release job's assets.
6. **Website** (branch `release-8_0_0`): `installing.md` with two downloads and the
   restriction stated, the yWorks attribution, the manual's mentions of the add-on.
7. **Pre-release 99.0.9** for yWorks, with a mail saying where each confirmed point can
   be seen. Wait for their answer.
8. **Release 8.0.0** by `release-8_0_0-state.md`; only the core artifact goes to Maven
   Central.

## Open

- Whether yGuard renames the classes of the backend unit as well (item 2 of the required
  shape). The core jar stays readable either way.
- Whether there is a second, yFiles-free set of installers. One installer with yFiles as
  an option is not a way out: the installer file would contain the library and so be the
  restricted artifact whatever the user ticks.
- How step 2 closes the seam, see the second correction above.
