# GROOVE yFiles backend

The Maven project `nl.utwente.groove:groove-yfiles` (gh #909): an implementation of the
`GraphBackend` service of `nl.utwente.groove.gui.view` on
[yFiles for Java (Swing)](https://www.yworks.com/products/yfiles-for-java). It is an
**optionally compiled unit**, not a module of the main Maven build: `git clone && mvn package`
in the repository root builds GROOVE with the JGraph backend and never touches this
directory.

## Why a separate project

yFiles is a commercial library, which GROOVE uses under an academic licence (details in
`claude/yfiles-migration.md`). The source of this unit, with its calls to the yFiles API,
is public with yWorks' consent; the library is not. Consequences for the build:

- The yFiles jar and its licence files are never in this repository or in any Maven
  repository. **Building this unit takes a yFiles for Java library and licence of your
  own**; without them, everything else in the repository builds and runs as before.
  GROOVE's own copy is in the private repository `nl-utwente-groove/yfiles-lib`, readable
  by the licensed developer and the workflows only.
- The ordinary continuous-integration build of pull requests never touches this
  project; only the licensed developer and, under the project license, the workflows
  `backend.yml` and `release.yml` build with the library, which they take from that
  private repository (see `release/README.md`).
- The library may be redistributed only obfuscated, and only for non-commercial use.
- The backend package `nl.utwente.groove.gui.yfiles` must never re-expose the yFiles API.

The unit was part of this repository until 2026-09-09 and then lived in the private
repository until 2026-11, on a reading of the licence that yWorks have since corrected
(see `claude/yfiles-restructure.md`).

## What it needs from GROOVE

At compile time, the neutral seams of the main project: the `GraphBackend` service
interface, the canvas, controller, view-model and cell interfaces of `gui.view`, the look
layer (`gui.look`), the graph types and the layout map. Nothing from `gui.jgraph`. At run
time the direction reverses: the main project does not depend on this unit, the Simulator
discovers it on the class path through `ServiceLoader`.

The dependency is on the GROOVE artifact `nl.utwente.groove:groove`, at the version given
by the `revision` property of this pom, whose default is kept equal to the `revision` of
the main pom one directory up (both are updated in the same commit when the version is
bumped, see the release checklist).
A command-line build compares the two and fails when they have drifted apart, since
drift is otherwise silent: an artifact of the stale version may well still be in the
local repository, in which case the build succeeds against it while the freshness guard
below dutifully installs the current one next to it. A caller that dictates the version
with `-Drevision`, as the workflows and the release recipes do, reads it from the main
pom as well, so for those the comparison holds trivially.

- **In Eclipse** no artifact is involved: with equal versions, m2e resolves the dependency
  from the open `groove` project (workspace resolution, on by default), so changes to the
  main project are visible immediately. Import this directory, which carries the Eclipse
  project name `groove-yfiles`, as an existing Maven project, next to the `groove`
  project it is nested in.
- **On the command line** the dependency is the installed artifact in `~/.m2`, and a stale
  artifact would silently compile and test this backend against an old core. The pom
  therefore re-installs the GROOVE artifact from the enclosing checkout (tests and javadoc
  skipped) at the start of every build; the checkout is the `groove.dir` property, whose
  default is the parent directory. Pass
  `-Dgroove.install.skip=true` to skip the install when the artifact is known to be
  current. The Eclipse launch `GROOVE - build local maven artefact` installs it too, with
  tests and javadoc.

  That guard refreshes an artifact but **cannot bootstrap a missing one**: Maven resolves
  the dependency graph of a module before the first phase of its lifecycle runs, so when
  `nl.utwente.groove:groove:${revision}` is absent from the local repository the build
  fails to resolve before the guard is reached, with

      [ERROR] Could not find artifact nl.utwente.groove:groove:jar:<revision>

  The remedy is step 2 of Building below, once per machine and after every version bump.
  This is the usual cause of that error on a fresh checkout or right after a bump, while
  the same build keeps working on a machine whose local repository was populated earlier.

## Building

1. Install the yFiles library into the local Maven repository, once, from the `lib`
   directory of the licensed distribution:

       mvn install:install-file -Dfile=yfiles-for-java-swing.jar -DgroupId=com.yworks.yfiles -DartifactId=yfiles-for-java-swing -Dversion=3.6.0.1 -Dpackaging=jar

2. Install the GROOVE artifact into the local Maven repository, once per machine and
   again after every bump of `revision`, from the repository root:

       mvn -q install -DskipTests -Dmaven.javadoc.skip=true

   Only this first install has to be done by hand; from then on the freshness guard
   described above keeps the artifact current. Eclipse needs no equivalent: it resolves
   the dependency from the open `groove` project.

3. Tell the build where the licence file is, which the library loads from the root of
   the class path: the build copies it from the directory `yfiles.license.dir`. The
   default is `../../yfiles-lib/lib` as seen from this directory, that is, the `lib/`
   directory of a clone of the private repository next to the clone of this one, which
   is the layout on the licensed developer's machine and on the workflow runners. It does
   not hold in a git worktree, nor for anyone with a library of their own: there, pass
   the directory as an absolute path with `-Dyfiles.license.dir=<dir>`, or set the
   property in an active profile of `~/.m2/settings.xml`.

   yWorks issues two licence files and its developer guide (Deployment, Licensing) keeps
   them strictly apart: the distribution licence `com.yworks.yfiles.java.license.xml`,
   the only one that may be deployed (its watermark is worded differently), and the
   development licence `com.yworks.yfiles.java.developmentlicense.xml`, whose watermark
   on every canvas names it as such. The build takes the distribution licence, since
   what it packages is what a release ships, and copies that file only, so the other
   never ends up in the jar. The property `yfiles.license.file` names the file, so
   `-Dyfiles.license.file=com.yworks.yfiles.java.developmentlicense.xml` gives a build
   with the development watermark. A build whose licence file is missing fails in the
   validate phase.

4. Build this project in this directory:

       mvn -q package

   or, from the repository root:

       mvn -q -f yfiles/pom.xml package

   This re-installs the GROOVE artifact first (see above). Scripts building a release may
   pass `-Drevision=<version>` as the release reactor does.

## Contents

- `nl.utwente.groove.gui.yfiles.YFilesBackend`: the `GraphBackend` service provider,
  declared in `META-INF/services`. The main build discovers backends through
  `ServiceLoader` and ranks yFiles first whenever it is present, so putting this project
  on the class path of a GROOVE launch is all that is needed to select it.
- `YFilesAspectCanvas` on the abstract `YFilesCanvas`: the read-only canvas for aspect
  graphs (host graphs, rules, types, states), a `GraphCanvas` composed over a yFiles
  `GraphComponent`. `YFilesCellStore` shows the neutral cells of the view model as tagged
  yFiles items, behind a filtered graph for hidden cells; `CellNodeStyle`,
  `CellEdgeStyleRenderer` and `CellStyles` render the cells' visuals in the look of the
  JGraph backend, including its fan-out of parallel edges (`gui.view.ParallelEdges`). The component scrolls and zooms itself, which `GraphPanel` honours through
  `GraphCanvas.hasOwnScrolling()`.
- `YFilesLTSCanvas`, `YFilesCtrlCanvas` and `YFilesPlainCanvas`: the canvases of the other
  read-only roles, on the same base. Clicks detected by yFiles are forwarded to the Swing
  mouse listeners of the component, which yFiles' own input surface would otherwise keep
  from them.
- `YFilesLayouter` with `YFilesLayoutKind`: the backend's layout palette, seven yFiles
  layout algorithms (hierarchic, organic, orthogonal, tree, balloon, circular, radial)
  offered next to GROOVE's own Spring and Forest layouters in the layout menu and the
  layout dialog, with `LayoutSettingsPanel` holding their main settings. A layouter runs
  the algorithm on the shown yFiles graph and writes the positions and edge routes back
  through the canvas' edit funnel; vertices that are not layoutable stay where they are,
  by running the algorithm as the core of a yFiles partial layout.
- `YFilesAspectEditorCanvas`: the editor's canvas, an aspect canvas with yFiles' editor
  input mode configured to the JGraph editor's gestures (double click adds a vertex,
  click-click creates an edge, Alt-click edits edge points, dragging moves cells, edge
  points and edge ends), every gesture ending in one edit of the view model's history.
  The in-place editor is yFiles' text editor on the cell's editable labels, with the
  label completion shared with the JGraph editor (`gui.view.LabelCompletion`); the grid
  snaps vertex centres and edge points. Deletion, undo, copy and paste are the tab's
  actions on the model. With this, a launch with this project on the class path never
  touches JGraph.
- When both backends are on the class path, the Simulator's Options menu offers the
  choice (`Graph backend`), stored as a user preference and applied at the next start;
  without a stored choice yFiles is used whenever it is present.
- Export and the headless `Imager` work through the canvas contract of the main project
  (`toImage` for raster images, `paintGraph` for the vector formats, the view model for
  TikZ), so the exporters need nothing from this unit. The Imager renders on the backend
  selected as in the Simulator, or on the one named by its `-b` option (`jgraph` or
  `yfiles`), falling back with a warning if that one is not on the class path.
  `YFilesImagerTest` runs the Imager on this backend in every format and leaves its
  output in `target/imager`.
- `YFilesCanvasTest` checks the canvas headlessly and `YFilesEditorTest` the editor
  canvas' gestures through its entry points; `YFilesSimulatorTest` launches a Simulator
  on the yFiles backend (a window appears briefly) and checks the state display, a host
  graph tab, a rule tab, the LTS display including its export, and an editor tab. The popup menu and the editing
  gestures are driven by a Robot, which takes over the mouse: those two tests run only
  with `-Dgroove.test.robot=true` and are skipped otherwise. All need the licensed
  library and the license file: `mvn -q test` in this directory (in PowerShell, quote any
  `-D` arguments). The tests never touch the real user preferences, nor the real
  extension directory.

## Distribution

How this backend reaches users is being changed: yWorks did not approve the add-on
described here, which never got further than test releases, and the backend is to ship
inside the binaries instead (`claude/yfiles-restructure.md`). Until that is built, the
release build still produces the add-on: the backend's jar and the
library, obfuscated with yGuard, zipped up for GROOVE's extension directory, from which
`nl.utwente.groove.util.Extensions` loads them at start-up through a class loader of
its own. The release reactor builds the add-on under its `yfiles` profile, see
`release/README.md`. Two things in this project serve that: the jar's manifest declares
the GROOVE version it is built against (`GROOVE-Version`, from the `revision`
property), by which GROOVE recognises and skips an add-on built for another version;
and the distribution licence file is a resource at the root of the jar, where the
library finds it also when the jar is loaded through the extension loader. The
obfuscation rewrites the backend jar's references to the library and keeps every name of
`nl.utwente.groove`.

The project runs on the class path (no `module-info`): the yFiles documentation does not
state the jar's module name, and GROOVE's installed application runs from the class path
as well.

## Upgrading the library

A new yFiles version means: a new `yfiles-for-java-swing.jar` and new licence files in
`lib/` of the private repository, a new `yfiles.version` in `pom.xml`, and the same
version in `release/yfiles/pom.xml`, which obfuscates the library. If the two sides are
prepared on branches, give the branch of the private repository the name of the branch
here: the workflows take the library from the same-named branch if there is one
(`release/github/choose-backend.sh`).
