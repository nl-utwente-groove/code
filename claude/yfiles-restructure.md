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
4. **Licence texts**. Drafted 2026-11-16, branch `yfiles-licence-texts` on top of step
   3, three commits (the restrictions and their wiring; the texts a user reads; this
   note). **The wording awaits Arend's review against the SLA**; the places are listed
   under *Decided in step 4*.
   - `release/yfiles/include/LICENSE-YFILES.txt` replaces `YFILES-ADDON.md`: the
     restrictions of a binary with yFiles, in plain text, started from the September
     edition's notice (`YFILES-EDITION.md` as reworded by Arend in a7d316d7f). The
     yFiles zip carries it at its root, and the build fails if it does not.
   - `LICENSE.txt` loses its paragraph on the add-on and says instead that it does not
     cover the yFiles library, and that a distribution with the library has
     `LICENSE-YFILES.txt` next to it.
   - An installer built with `yfiles` shows the two files as one licence, taking the
     second from the zip it packages and refusing a zip without it; its `.rpm` tag is
     `Apache-2.0 AND LicenseRef-GROOVE-yFiles-academic-non-commercial`.
   - `release/include/README.md` (in every zip), the two change logs and
     `release/github/INSTALL-NOTE.md` (the release page) describe the two forms of the
     release instead of the add-on.
   - Verified: the reactor with the profile, with `LICENSE-YFILES.txt` at the root of
     the yFiles zip only; the new check fails on a zip without the file; a yFiles `.msi`
     builds and its licence page text holds both files, paragraph by paragraph. Not
     verified: that `.msi` installed, the other installer types, and how the release
     page renders the note. The texts say that the installers contain yFiles, which
     becomes true with step 5.
5. **CI**. Built 2026-11-16, branch `yfiles-installers-job` on top of step 4, one
   commit and this note. What was left after step 3 was the `installers` job; the
   release job needed no change for its assets (its `*.zip` glob attaches the yFiles
   zip as it is, and its build step already runs under Xvfb for the test pass).
   - The five runners of the job have neither the library nor the token. Each now
     downloads `groove-x_y_z-yfiles-bin.zip` with `gh release download` from the
     release that the release job has just published, into `release/target`, and calls
     the script with `yfiles`. The job's own Maven builds of the core and of the
     release reactor are gone: the script reads the zip and files of the checkout
     only, and Maven remains for reading the version from the pom.
   - Verified locally: `gh release download` exits 1 on a release without the asset
     (tried on `release-7_8_0`); the script builds an app image with the two yFiles
     jars and both licence files in a checkout in which nothing was built, from a
     yFiles zip put into `release/target`; and, unplanned, it refused the zip that
     step 4's sabotage run had left in the step-3 worktree, which has no
     `LICENSE-YFILES.txt`. **Not verified, and not verifiable short of a tag: the
     workflow itself.** Its first run is the pre-release of step 7, which also carries
     what step 3 left unverified on a Linux runner.
6. **Website** (branch `release-8_0_0`): `installing.md` with two downloads and the
   restriction stated, the yWorks attribution, the manual's mentions of the add-on.
7. **Pre-release 99.0.9** for yWorks, with a mail saying where each confirmed point can
   be seen. Wait for their answer.
   **Reminder for that mail (Arend asked to be reminded, 2026-11-16):** put the question
   to yWorks whether the content of the library's module descriptor
   (`module-info.class` of the plain jar: the module name, its `requires` and `exports`)
   may be given to an LLM session. The ruling of 2026-09-05 admits demo code and the
   documentation only, and excludes class-file inspection; the session was given the
   module name alone, and learned the rest from compiler errors. A session that
   prepares the pre-release or the mail must bring this up.
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
  class path no longer works, and nothing runs that way. *Superseded on 2026-11-16, see
  "The backend as a named module" below:* something did run that way, the Eclipse launch
  of the Simulator with yFiles, and the library jar turned out to be a named module.
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

## The backend as a named module (2026-11-16)

Branch `yfiles-backend-module` off `master`, merged on 2026-11-16. It replaces the decision of step 2 to close
the seam by not exporting it, and supersedes the stopgap branch `yfiles-eclipse-launch`
(a class-path launch for Eclipse), which is to be dropped unmerged.

What prompted it: after step 2 the Eclipse launch `GROOVE yFiles - Launch Simulator`,
which has the core on the module path and the backend on the class path, died with an
`IllegalAccessError` on `GraphBackend`. Arend did not want the core off the module path
for the sake of the backend, and found that the library jar is a named module,
`com.yworks.yfiles.complete` (he read the name from the jar himself; under its file name
the jar could not even have been an automatic module, since the derived name
`yfiles.for.java.swing` contains a keyword).

What was built:

- **The unit is the module `nl.utwente.groove.yfiles`**: it requires the core and the
  library, provides `GraphBackend`, exports nothing. The core exports `gui.view`,
  `gui.view.cell`, `gui.look` and `gui.layout` to that module only, which is item 3 of
  *The required shape* after all, and a tighter seam than the unqualified exports
  before step 2.
- **The tests run on the module path**, as part of the module. They need six packages
  the core does not export (`gui`, `gui.action`, `gui.display`, `gui.export`,
  `gui.tree`, `io`); the unit's pom exports these for the tests only, at compilation
  and in the Surefire `argLine`. The preferences isolation moved to the `argLine` too:
  the session listener `TestIsolation` is declared in `META-INF/services`, which the
  service loader ignores for a named module; it stays for class-path runs.
- **The release build takes the descriptor out of the backend jar** before obfuscating
  it. The binaries run on the class path as before.
- **The Eclipse launch** is the old one with the backend project and the library jar
  moved from the class path to the module path entries.
- **`opens gui.layout to info.picocli` in the core**, a regression of step 2 found on
  the way: the Imager and the Viewer have `LayoutHandler` of that package as the
  converter of their layout option, which picocli instantiates reflectively. Since step
  2 any modular run of the Imager failed, the core's own `ImagerTest` included (6
  failures and an error), unnoticed because that test is a slow test outside the
  default suite and the installed application runs on the class path.

Verified: the core's fast suite (985 tests) and `ImagerTest`; the GUI tests (23 in 8
classes); the unit as a module (73 tests, the 6 Robot tests skipping); both null checks;
the Imager launched as a module with core, backend and library on the module path and
no option at all, rendering with yFiles, and falling back to JGraph with its warning
when backend and library are taken away; the release reactor with the `yfiles` profile,
where the obfuscated backend jar has no descriptor and the tests on the obfuscated
stack pass (67, 6 aborted by assumption).

Not verified, and not verifiable from a session: **anything in Eclipse**. Open there:
whether the launch file starts as rewritten; whether m2e, after Maven > Update Project
on `groove-yfiles`, passes the `--add-exports` of the test compilation on to JDT (if
not, the test sources show "not accessible" errors, and the exports have to be set on
the project's module dependencies by hand, or moved to the plugin-level `compilerArgs`
of the pom at the price of the backend's own code compiling with them too); and whether
a JUnit run gets the VM arguments of the `argLine`. Also not known: what yGuard leaves
of the library's own descriptor in the obfuscated library jar. It does not matter on
the class path, but the jars of the distribution must not be assumed to work as
modules.

Rejected: unqualified exports again (the state before step 2; open to any class-path
code); `--add-exports` in the launch (publishes the switches that reopen the seam, and
runs the backend as no installation does); the tests on the class path with
`useModulePath=false` (Eclipse compiles and runs the tests of a modular project as part
of the module regardless, so Maven would test another configuration than Eclipse).

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

Put to Arend on 2026-11-16 and confirmed: the default of the installer script stays
the plain zip, and there is no `+doc` zip with yFiles. The other decisions of this
section were not put to him separately.

## Decided in step 4 (2026-11-16)

Taken in the session, as a draft for review.

- **The restrictions are one plain-text file, `LICENSE-YFILES.txt`.** It is named and
  placed as a licence, next to `LICENSE.txt`, because it is one: the installer has the
  user accept it. Plain text rather than Markdown because the `.msi` turns the licence
  into RTF paragraph by paragraph, and Markdown headings and links would show as
  markup there. It is the only copy; the zip, the installers and the zip check all
  take it from `release/yfiles/include`.
- **`LICENSE.txt` stays one file for source, plain zips and binaries with yFiles.** It
  covers GROOVE and says what it does not cover; the restrictions come on top, in the
  other file, only where the library is. A second variant of `LICENSE.txt` for the
  binaries with yFiles was rejected: two texts to keep in step, and the Apache licence
  of GROOVE's own part holds in both.
- **The wording to review**, each against SLA §2.1(c), §2.1(d) and §2.4 and against
  yWorks' "the same restriction to purely academic use":
  1. `LICENSE-YFILES.txt`, restriction 1: "academic, non-commercial purposes only, such
     as research, teaching and study. It may not be used by or on behalf of a
     commercial institution, nor by anyone for a commercial purpose." September's text
     said "non-commercial purposes only"; "academic" and the sentence on commercial
     institutions are new, after §2.4 and yWorks' reply.
  2. The same file, restrictions 2 and 3 (no extraction or reverse engineering; passing
     on only unchanged and as a whole): September's wording, adapted from "edition" to
     "distribution", and now naming the backend jar as obfuscated too.
  3. The same file, last two paragraphs: where the unrestricted zip is, and that the
     source is public without the library.
  4. The new paragraph of `LICENSE.txt`.
  5. The short forms: "With or without yFiles" in `release/include/README.md`, the
     first section of `CHANGES-8_0_0.md` and the line in `CHANGES.md`, and the block of
     the same title in `INSTALL-NOTE.md`, which also tells the reader of the release
     page that the installers are for academic use only.
  6. The `.rpm` licence tag.
- **Left out of step 4, to be added** (Arend, 2026-11-16): nothing in the running
  application states the restriction. The SLA asks nothing of the kind as far as the
  notes record, and the installers have the user accept the text, but a user of the
  yFiles zip meets it only as a file at the zip's root. Arend's decision: the About
  box names yFiles and the restriction when the yFiles backend is present. Built
  2026-11-16, branch `yfiles-about-notice`, see *The About-box notice* below. The
  website's download page is step 6.

## Decided in step 5 (2026-11-16)

Put to Arend before building.

- **The installers take the yFiles zip from the release, not from a workflow
  artifact.** An installer then holds the very file published next to it, and a rerun
  of the `installers` job alone finds it as long as the release exists. An artifact
  passed between the jobs would be a second copy of the zip, kept for 90 days, and one
  more step in the release job.
- **No fallback to plain installers.** A release without the yFiles zip fails the job.
  A fallback would publish installers that the release page and `LICENSE.txt` of step
  4 describe wrongly, and would hide a release job that stopped building the zip.
- **The job no longer builds GROOVE.** The jars of the installers used to be compiled
  on each runner by JDK 25 (for Java 21); they are now those of the release job, built
  once by JDK 21. The bundled runtime is still the JDK 25 of the runner, which is what
  the choice of that JDK was for.

Consequence to remember: the release is public, with its zips, for the minutes in
which the installers are built, and stays without installers if their job fails. That
was so before; what is new is that the job depends on the published release rather
than on its own build.

## The About-box notice (2026-11-16)

Arend's decision under *Decided in step 4*; branch `yfiles-about-notice` off `master`.

- **The backend states its own terms.** `GraphBackend` has a method
  `getLicenseNotice()`, `null` by default; `YFilesBackend` returns two sentences (the
  distribution contains yFiles by yWorks, academic non-commercial use only, the file
  `LICENSE-YFILES.txt`). The About box (`VersionDialog.showAbout`) shows the notices of
  all *available* backends below the version text: the restriction holds for the
  distribution whichever backend the user has selected. Without the backend the dialog
  is what it was.
- **Rejected: the text in the core, shown when a backend named `yfiles` is found.**
  The core is the Apache artifact on Maven Central and would carry the terms of a
  library it does not contain. In the backend the notice is present exactly when the
  jar is, and survives the obfuscation: yGuard keeps the methods by which the backend
  implements an interface of the core, and does not touch string constants.
- **The wording is a draft for Arend**, a short form of restriction 1 of
  `LICENSE-YFILES.txt`. It says "this distribution", which is not quite true for a
  developer who runs the backend from source under a licence of their own; they see it
  too, since the backend cannot tell.
- One more copy of the restriction to keep in step: `release/README.md` lists it with
  the others.

Merged into `master` on 2026-11-16, after the backend module (see *The backend as a named
module*). Checked on the merged result: the unit has 74 tests and the one on the notice
passes on the module path; in the test pass of the release build it also passes on the
obfuscated jars (68 tests, 6 more aborted by assumption), which settles what the commit of
the notice left untried, that the method and its text survive the obfuscation.
