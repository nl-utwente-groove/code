# GROOVE yFiles backend

This directory holds the yFiles graph-visualisation backend of GROOVE (gh #909): an
implementation of the `GraphBackend` service of `nl.utwente.groove.gui.view` on
[yFiles for Java (Swing)](https://www.yworks.com/products/yfiles-for-java). It is an
**optionally compiled unit**, not a module of the main Maven build: `git clone && mvn package`
in the repository root builds GROOVE with the JGraph backend and never touches this
directory.

## Why a separate project

yFiles is a commercial library under an Academic Single Developer License (details in
`claude/yfiles-migration.md`). Consequences for the build:

- The yFiles jar is never in the repository or in any Maven repository; only the licensed
  developer has it, in the local Maven repository (`~/.m2`).
- Continuous integration cannot build this project. yFiles editions of GROOVE are built
  locally.
- A yFiles edition may be redistributed only with the library obfuscated, and only for
  non-commercial use. The standard GROOVE release stays JGraph-based and Apache-licensed;
  a yFiles edition ships alongside.
- The backend package `nl.utwente.groove.gui.yfiles` must never re-expose the yFiles API.

## What it needs from GROOVE

At compile time, the neutral seams of the main project: the `GraphBackend` service
interface, the canvas, controller, view-model and cell interfaces of `gui.view`, the look
layer (`gui.look`), the graph types and the layout map. Nothing from `gui.jgraph`. At run
time the direction reverses: the main project does not depend on this unit, the Simulator
discovers it on the class path through `ServiceLoader`.

The dependency is on the GROOVE artifact `nl.utwente.groove:groove`, at the version given
by the `revision` property of this pom, whose default is kept equal to the `revision` of
the main pom (both are updated when the version is bumped, see the release checklist).

- **In Eclipse** no artifact is involved: with equal versions, m2e resolves the dependency
  from the open `groove` project (workspace resolution, on by default), so changes to the
  main project are visible immediately. Import this directory as an existing Maven
  project next to the main one.
- **On the command line** the dependency is the installed artifact in `~/.m2`, and a stale
  artifact would silently compile and test this backend against an old core. The pom
  therefore installs the GROOVE artifact from the repository root (tests and javadoc
  skipped) at the start of every build; pass `-Dgroove.install.skip=true` to skip that
  when the artifact is known to be current. The Eclipse launch
  `GROOVE - build local maven artefact` installs it too, with tests and javadoc.

## Building

1. Install the yFiles library into the local Maven repository, once, from the `lib`
   directory of the licensed distribution:

       mvn install:install-file -Dfile=yfiles-for-java-swing.jar -DgroupId=com.yworks.yfiles -DartifactId=yfiles-for-java-swing -Dversion=3.6.0.1 -Dpackaging=jar

2. Point the build at the directory holding the runtime license file, which the library
   loads from the root of the class path. Either pass `-Dyfiles.license.dir=<dir>` or set
   the property in an active profile of `~/.m2/settings.xml`; the default is
   `~/yfiles-license`.

3. Build this project from the repository root:

       mvn -q -f yfiles/pom.xml package

   This installs the GROOVE artifact first (see above). Scripts building a release may
   pass `-Drevision=<version>` as the release reactor does.

## Contents

- `nl.utwente.groove.gui.yfiles.YFilesBackend`: the `GraphBackend` service provider,
  declared in `META-INF/services`. The main build discovers backends through
  `ServiceLoader` and ranks yFiles first whenever it is present, so putting this project
  on the class path of a GROOVE launch is all that is needed to select it.
- The canvases themselves (`GraphCanvas` on a yFiles `GraphComponent`) are the next
  slice of the migration; until they exist the provider refuses to create canvases.

The project runs on the class path (no `module-info`): the yFiles documentation does not
state the jar's module name, and GROOVE's installed application runs from the class path
as well.
