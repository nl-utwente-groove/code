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
2. **Remove the add-on machinery** (item 3 above). Built 2026-11-16, branch
   `remove-addon-machinery`, two commits (the core; the unit and the release build):
   - *Core*: `util.Extensions`, `util.AddOn`, `gui.AddOnInstaller` and their tests are
     gone, with the View-menu submenu, the first-run question and the two system
     properties of the test configurations. `GraphBackend` discovers its providers
     through the plain `ServiceLoader`; `BackendChooser` offers the backends of the
     session. `module-info` no longer exports `gui.view`, `gui.view.cell`, `gui.look`
     and `gui.layout` (see *Decided in step 2*) nor requires `java.net.http`, whose only
     user was the download.
   - *Backend unit and release build*: the `GROOVE-Version` manifest entry and the
     extension-directory isolation of the unit's tests are gone, as are `addon.xml`, the
     msi cleanup fragment and its splice. The module `release/yfiles` (artifact
     `yfiles-obfuscated`) now only obfuscates, into `release/yfiles/target/lib`, and
     checks the licence in the result; **no zip or installer contains the jars until
     step 3**. The `check-revision` guard of the unit's pom stays, against item 3 of the
     required shape: it guards a build against a stale core artifact, which has nothing
     to do with the add-on.
   - *yGuard renames the backend's own classes and members*, see *Decided in step 2*.

   Left for later steps, deliberately: the user-facing texts that still describe the
   add-on (`release/include/README.md`, `CHANGES.md`, `CHANGES-8_0_0.md`,
   `release/github/INSTALL-NOTE.md`) and the orphaned `release/yfiles/include/YFILES-ADDON.md`,
   the basis of the notice in the binaries: all of them are rewritten in step 4, with
   wording Arend reviews, rather than twice. The website notes
   (`website-manual-8_0_0.md`, `release-8_0_0-state.md`) are step 6.
3. **Distribution**. Built 2026-11-16, branch `yfiles-distribution`, three commits
   (the zip; the tests on an obfuscated stack; the installer script). The reference is
   the second chapter of `release/README.md`.
   - *The zip*: `mvn -Pyfiles clean package` in `release/` builds, in one pass, the
     plain zips exactly as without the profile and `groove-x_y_z-yfiles-bin.zip` next to
     them (module `assembly/yfiles-bin`): the content of the plain bin zip, the two
     obfuscated jars in `lib/`, launchers whose class path lists them, and
     `release/yfiles/include` at the root. There is no `-yfiles-bin+doc.zip`.
   - *Checks, all inside the reactor and all failing the build*: no zip holds anything
     that looks like the yGuard mapping; a plain zip holds no entry named after yFiles
     and no launcher whose manifest mentions it; the two jars in the yFiles zip are byte
     for byte those of the obfuscation and not its inputs, so that the licence check of
     step 2 carries over to the zip; the Imager of the unpacked yFiles zip renders with
     `-b yfiles`; and the unit's tests run on an obfuscated stack (see *Decided in step
     3* for what that does and does not show).
   - *Installers*: `build-installer.sh <version> <type> yfiles` packages the yFiles zip,
     as the same product under the same names. The bundled runtime needs no module for
     the jars, confirmed on an app image.
   - Verified on a display: the unit's own build and the test pass both run 73 tests,
     the 6 Robot tests skipping; from clean, the whole reactor with the profile. Each
     check was seen to fail on a sabotaged build. Not verified: anything on a Linux
     runner (`xvfb-run` around the release build, the Imager check there), the `.msi`
     and the other installer types with `yfiles` (only the app image was built), and
     whether the renaming is deterministic.
4. **Licence texts**: `LICENSE.txt`, the notice in the binaries, the installer's licence
   page, the release-page text, `CHANGES.md`. Wording to be reviewed by Arend against the
   SLA. Since step 3 the yFiles zip carries `release/yfiles/include/YFILES-ADDON.md` at
   its root, which still describes the add-on, and an installer built with `yfiles`
   shows `LICENSE.txt` and, as an `.rpm`, carries the tag `Apache-2.0`: all three are
   wrong for a binary with yFiles and are settled here (the text of the September
   edition, `YFILES-EDITION.md` at 4a0731433 as reworded in a7d316d7f, describes this
   very distribution and is the obvious start for the notice).
5. **CI**: what is left after step 3 is the `installers` job. The release job needs no
   change for its assets: its `*.zip` glob attaches the yFiles zip as it is, and its
   build step already runs under Xvfb for the test pass. The installers are built on
   five runners that have neither the library nor the token; rather than obfuscating
   five times, the job should download `groove-x_y_z-yfiles-bin.zip` from the release
   that the release job has just created, put it in `release/target` and call the
   script with `yfiles`. Until then the workflow builds plain installers.
6. **Website** (branch `release-8_0_0`): `installing.md` with two downloads and the
   restriction stated, the yWorks attribution, the manual's mentions of the add-on.
7. **Pre-release 99.0.9** for yWorks, with a mail saying where each confirmed point can
   be seen. Wait for their answer.
8. **Release 8.0.0** by `release-8_0_0-state.md`; only the core artifact goes to Maven
   Central.

## Decided in step 2 (2026-11-16)

- **yGuard renames the classes and members of the backend unit as well** (Arend). The
  `keep` section of `release/yfiles/pom.xml` holds no name any more; yGuard itself
  keeps what the backend overrides or implements of the core and the JDK, which it
  finds on the external class path. The provider file `META-INF/services/...GraphBackend`
  keeps its name (an interface of the core) and gets its content rewritten by an
  `adjust` element with `replaceContentPolicy="strict"` and
  `replaceContentSeparator="."`. Line numbers are kept and the source file is the
  constant `y`, so a stack trace through the backend can be translated with the
  regenerated mapping. Verified: the obfuscated backend jar has no class under `nl/`
  and its provider file names an obfuscated class; `Imager -b yfiles` renders headlessly
  with the two jars added to the class path of the unzipped standard release; the
  unit's 73 tests pass on the obfuscated stack (6 Robot skips) when obfuscated in the
  same pass. Not rechecked: that the renaming is still deterministic. Consequence for
  code in the unit: nothing may depend on one of its class names at run time
  (`yfiles/README.md`, Distribution).
- **One set of installers, with yFiles** (Arend): no yFiles-free installers and no
  choice inside an installer, which would not help anyway since the installer file
  would contain the library and so be the restricted artifact whatever the user ticks.
  The plain `-bin.zip` serves everyone else.
- **The seam is closed by not exporting it** (taken in the session, confirmed by
  Arend): the four packages are no longer exported at all. The alternative, qualified
  exports to a named backend module, needs a module name for the library jar, which its
  documentation does not state and which may not be read from the jar. The backend
  reaches the packages as class-path code, which is how the unit's tests, Eclipse and
  the installed application run; a GROOVE on the module path with the backend on the
  class path no longer works, and nothing runs that way.
- **The `check-revision` guard of the unit's pom stays** (taken in the session,
  confirmed by Arend), against item 3 of the required shape: it keeps a command-line
  build from compiling against a stale core artifact, whatever the distribution.
- **The user-facing add-on texts wait for step 4** (taken in the session, confirmed by
  Arend): `release/include/README.md`, `CHANGES.md`, `CHANGES-8_0_0.md`,
  `release/github/INSTALL-NOTE.md` and `release/yfiles/include/YFILES-ADDON.md` still
  describe the add-on, and step 3 leaves them alone as well, apart from what packaging
  the notice needs.

All gates of step 2 passed on its tip (fast suite 985 tests, GUI suite 23 tests in 8
classes, backend unit 73 tests with 6 Robot skips, both null checks).

## Decided in step 3 (2026-11-16)

Taken in the session; none of these has been put to Arend yet.

- **One pass, and the profile leaves the plain zips alone.** The September edition
  switched the whole reactor under the profile (names, descriptors, and the launchers'
  class path through a dependency on the backend) and needed a second pass without
  `clean` to have both sets of zips. Now the profile only adds: the launchers of the
  yFiles zip are copies of the plain ones, made in the `release/yfiles` module by
  merging the two jar names into the `Class-Path` of their manifests. A plain zip is
  the same whether or not the profile is active, which the build also checks.
- **No assembling module has a plain yFiles jar in its dependency graph.** The yFiles
  zip takes the two jars from the obfuscation's output directory; its module depends on
  the obfuscation module for the reactor order only, with every dependency of that
  module excluded. The assembly plugin's dependency filter is lenient (the
  `runnable-1.0.pom` episode of September), and the cost of a mistake here is a plain
  library jar in a public zip.
- **The tests run on a second obfuscation, not on the released jars.** The tests name
  the backend's classes, so they have to be renamed with it, in a pass of their own
  with the test classes as a third jar; the yGuard configuration is one macro called by
  both passes, so the tested configuration cannot drift from the released one. The
  test classes keep their names, and with them the backend's package keeps its name in
  that pass only. What this shows is that the configuration yields a working stack;
  what runs the released jars themselves is the Imager check on the yFiles zip.
  Rejected: obfuscating the tests against the released jars with yGuard's mapping
  input, which would test the very jars but rests on yGuard features nobody here has
  tried, and collides with the kept names of the test classes. The unit installs its
  test classes as a `tests` artifact for this, so that tests and backend come from the
  same build; nothing of the unit is deployed.
- **The Imager check has a negative control.** The Imager falls back to JGraph with a
  warning when the backend is missing, and still exits 0, so the check has to look for
  the warning's text. A plain launcher run in the same tree must print it; if the
  wording changes, the control fails instead of the check going blind.
- **The installer script takes `yfiles` as an argument and defaults to the plain zip.**
  The decision of step 2 stands, one set of installers with yFiles; the default is
  plain because that is what builds without the library, and because the workflow's
  `installers` job keeps working until step 5 switches it. A forgotten argument yields
  an installer without yFiles, which is harmless; the opposite default would fail for
  everyone without a licence.
- **No `-yfiles-bin+doc.zip`.** The javadoc is for developers, who can take the plain
  one.

Open for Arend: nothing blocks step 4. The two decisions he may want to overrule are
the default of the installer script and the absence of a `+doc` zip with yFiles.
