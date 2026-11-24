# How to build a github release

This chapter explains how to build a github release for Groove. A second chapter, below, summarises the actions to create a Maven artifact.

The github release, as meant here, consists of three dedicated zip-files (as well as the complete source code, in `.zip` and `.tar.gz` format):

- `groove-x_y_z-bin.zip`
- `groove-x_y_z-bin+doc.zip`
- `groove-x_y_z-yfiles-bin.zip`, the first of these with the yFiles backend and library added in obfuscated form, built only with the `yfiles` profile; see the second chapter

All of these contain a top-level README.md that explains their structure and how to install the tool. The first two contain nothing of yFiles and are under the Apache licence of GROOVE alone.

In addition, the release workflow builds self-contained installers (with a bundled Java runtime, so users need no Java installation) for Windows (`.msi`), macOS (`.dmg`, both Intel and Apple silicon) and Linux (`.deb` and `.rpm`); see the Installers section below.

## Preparation

Below, the _release directory_ refers to the project subdirectory (of the `code` repository) called `release`.

1. Update the version and date in the GROOVE source:

    - The version number is the `revision` property in the main `pom.xml`: a semantic version `x.y.z` with the optional suffix `-SNAPSHOT`, where `x`, `y` and `z` are natural numbers without leading zeros and `x` is below 100 (every Maven build checks this form, and the release workflow additionally rejects the suffix). The number might already be correct (it is updated in postprocessing, see below) but the changes in this revision may necessitate updating the `x` or `y` values. In any case remove the `-SNAPSHOT` suffix. (The `GROOVE_VERSION` resource file is generated from this property by resource filtering; do not edit it.)
    - The `revision` property of `yfiles/pom.xml` (the optional yFiles backend) is updated in the same commit, to keep the two equal. This is development hygiene rather than a condition for the release: every build of the backend, by hand or in a workflow, is passed the version of the main pom with `-Drevision`, so the backend gets the right version either way. The default in that pom governs Eclipse, where equal versions are what make m2e resolve the dependency from the open `groove` project instead of an installed artifact; a command-line build of the backend fails if the two have drifted apart.
    - The remaining files in `src/main/resources/nl/utwente/groove/resource/version`:
        - `GROOVE_BUILD`: the build date, in format `YYYYMMDD`. Update to the build date.
        - [Optional] `GXL_VERSION`: the name of the version of GXL currently used for the encoding of graphs. (This will rarely change.)
        - [Optional] `JAVA_VERSION`: the version of Java to be used for the build.  

2. Update the `include/CHANGES.md` file in the release directory
   to reflect all changes with respect to the previous release.

3. [Optional] Check the files in `release/github` to see if the wording is still up-to-date

4. [Optional] Update `include/groove2tikz.sty file` in the groove-release project.
   This is done by running the `TikzStyleExtractor` class in the package
   `groove.io.external.util`. Currently, the output goes to the console, and it
   can be redirected as usual with pipes '>'. Alternatively, you can just copy
   and paste the entire output on the `groove2tikz.sty` file.

## Testing

1. Run the JUnit tests on the GROOVE source project, using the
   `GROOVE - all JUnit tests` launch configuration.
   If you get errors complaining that the test packages are not in the module,
   in Eclipse do `Project -> Clean -> groove` and try again. Only proceed if all tests pass.

## Building [optional]

(You can skip this step if you want, since it will actually also be done by github upon tagging.)

2. Compile GROOVE by running Maven on the GROOVE source project
   (ensuring that all dependencies are Maven-based), using
   
    `mvn clean install`

    (the version comes from the `revision` property in the pom),
    or by running the Eclipse `GROOVE core - do all` launch configuration.

3. Package GROOVE by running Maven in the release directory, using

    `mvn clean package -Drevision=x.y.z`

    where `x.y.z` is the same version number as the `revision` property in the
    main pom (the release poms are a separate Maven reactor, so the version
    must be passed in explicitly here),
    or by invoking the "GROOVE release - zip em up" launch configuration.

The steps under Testing and Building can be combined by invoking the `GROOVE release - do all`
launch configuration.

## Deploying

Github runs the steps under Building automatically when a tag is pushed of the form `release-x_y_z`
where `x.y.z` should be the version number. This will result in a release called `release-x_y_z`
containing the ZIP artifacts. Note that the numbering of the releases and of the artifacts are controlled
in two different ways: the tag names the release, whereas the `revision` property in the pom of the tagged
commit determines the artifact names and whether the release is marked as a pre-release. The `release` job
checks that the two agree and fails before building anything if they do not; in that case, delete the tag
(see below), update the `revision` and tag again.

1. Commit and push the entire code repository, including the changes under "Preparation".

2. Create and push a tag of the form `release-x_y_z`

If something goes wrong on github and you have to repeat the last step, you first have to delete the remote tag on the command line, like so:

`git push origin --delete release-x_y_z`

### Test deployment

For a test deployment, use a version number starting at 99, such as `99.0.0`: the `release` job marks any such version as a pre-release automatically, and a pre-release is not shown as the latest release of the repository. Consider increasin the patch or minor number for every further pre-release (`99.0.1`, `99.1.0`, ...): the Windows installer derives its product code from the version, so it refuses to install over an existing installation of the same version ("Another version of this product is already installed"), whereas a different version replaces it.

A pre-release is typically tagged on a branch, since testing the workflow before merging is the point of it. The yFiles backend is built from the tagged commit itself (the unit in `yfiles/`); the library it is built against comes from the private repository `yfiles-lib`, and if the branch goes with a change there, as for an upgrade of the library, from the matching state of that repository, which is not its default branch. The `release` job and `backend.yml` both choose that state by the rule of `release/github/choose-backend.sh`: the candidates are the branches of this repository whose tip is an ancestor of the commit built — the branch tagged or pushed and everything it was branched off — and the library is taken from the nearest of those that `yfiles-lib` also has, falling back to `main`. Tagging a throwaway branch off a branch `yfiles-upgrade` therefore selects `yfiles-lib`'s `yfiles-upgrade`, whereas a `yfiles-lib` branch named after the branch tagged, if there is one, wins over it as the nearer match. A release tagged on `master` matches nothing there, the private default branch being `main`, and is built from `main` as before.

The tagged branch must be pushed, since the rule resolves against the remote branches of this repository; so it should also be removed again to avoid clutter. Remove it locally from Eclipse by

`git branch -D test-release`

(the `-D` signifies that deletion should be carried out even though there are commits on this branch that are nowhere else); remove it remotely from `origin` just as for tags:

`git push origin --delete test-release`

## Installers

The `installers` job of the release workflow (`.github/workflows/release.yml`) runs `jpackage/build-installer.sh` on a matrix of platform runners — jpackage can only build for the platform it runs on — and attaches the resulting installers to the same github release. The runners of that matrix have neither the yFiles library nor the token for it, and build nothing of GROOVE themselves: each downloads `groove-x_y_z-yfiles-bin.zip` from the release that the `release` job has just published, puts it in `release/target` and calls the script with `yfiles` (see below). An installer therefore holds the very jars of the zip next to it on the release page, obfuscated once and not once per platform. If the release has no such asset the job fails; it does not fall back on the plain zip, since the release page says that the installers contain yFiles. The job sets up JDK 25 rather than the Java 21 the code targets: jpackage bundles a runtime trimmed from the JDK it runs on, so that is the Java the installers ship. The launchers start their JVM with compact object headers (`-XX:+UseCompactObjectHeaders`, a product option since JDK 25, worth 7–10% of heap in exploration); the script adds the option only when its jpackage is 25 or newer, since an older JVM refuses to start on it.
The script unpacks the `-bin` zip and turns it into a native package with a bundled, jlink-trimmed Java runtime: the Simulator becomes the main launcher (which jpackage names after the application: GROOVE), the tools (Simulator, Generator, ModelChecker, Imager, Viewer) become additional launchers named after themselves. All of them carry menu entries, so the menu lists GROOVE next to the Simulator: jpackage offers no way to suppress the main launcher's entry that leaves those of the tools in place.

To try this locally without any packaging tools, build the release as described above and then run

    bash jpackage/build-installer.sh x.y.z app-image

which produces the raw application directory (no installer) under `jpackage/target/dist`. A third argument `yfiles` (as in `bash jpackage/build-installer.sh x.y.z app-image yfiles`) packages `groove-x_y_z-yfiles-bin.zip` instead of the plain bin zip, so that the installer contains the obfuscated yFiles jars; the release has to have been built with the `yfiles` profile. It is the same product either way, with the same name, identifiers, upgrade UUID and file names: there is one set of installers, with yFiles, which is what the release workflow builds, and the plain form remains for builds without the licensed library. The script takes the plain zip unless told otherwise, so that it works as it stands for someone without the library, and a forgotten argument yields an installer without yFiles rather than a failure. The bundled runtime needs no extra module for the yFiles jars: the one they need beyond the core, `jdk.xml.dom`, already comes out of the analysis of the core jar, which was confirmed on 2026-11-16 by rendering with `-b yfiles` through the Imager launcher of such an app image. The licence such an installer shows is `LICENSE.txt` followed by the restrictions that come with yFiles, `LICENSE-YFILES.txt` as packaged in the zip, and the script refuses a yFiles zip without that file; its `.rpm` carries the licence tag `Apache-2.0 AND LicenseRef-GROOVE-yFiles-academic-non-commercial`, there being no SPDX identifier for those restrictions. On Windows this must be Git Bash: in PowerShell, `bash` resolves to WSL's `C:\Windows\System32\bash.exe`, and the script then runs under Linux with the Linux jpackage, which knows no `msi` ("Invalid or unsupported type: [msi]"). Either run the script from a Git Bash window, or name Git Bash: `& "C:\Program Files\Git\bin\bash.exe" release/jpackage/build-installer.sh x.y.z msi`.

### Building the Windows installer locally

1. Install WiX 3.14, the version the GitHub Windows runner has. Its installer (`wix314.exe`, from the wix3 releases on GitHub) puts it under `C:\Program Files (x86)\WiX Toolset v3.14`, where jpackage finds it without any PATH change. The `wixtoolset` package of scoop is WiX 7, the .NET `wix.exe`, which jpackage 25 accepts as a toolset but which rejects the spliced `main.wxs`: jpackage converts only its own sources to the WiX 4 syntax, not a custom one. Making the script convert the file (`wix convert`, plus the `override` keyword WiX 4 wants on the rescheduled close-applications action) does yield a working `.msi`, but the release path uses WiX 3, so the script stays with that.
2. Point `JAVA_HOME` at a JDK 25, the version the installers job uses: the runtime it bundles and the compact object headers it enables come from the jpackage JDK (see above). With an older JDK the script warns and leaves the compact headers out. The core build is unaffected: it targets Java 21 whatever JDK runs it.
3. Build the core artifact and the release zip, on the branch to be tested (the main checkout is where Eclipse builds; a worktree does as well):

        mvn -B clean install -DskipTests
        cd release; mvn -B -Drevision=x.y.z -pl '!assembly/bin+doc' clean package; cd ..

    `x.y.z` is the pom's `revision`, `-SNAPSHOT` suffix included; the script strips the suffix for the MSI version number.
4. Build the installer, from the repository root in Git Bash:

        bash release/jpackage/build-installer.sh x.y.z msi

    The log should end in `installer built: .../jpackage/target/dist/groove-x_y_z-windows-x64.msi`. A failed splice check ("cannot splice the installer additions into jpackage's main.wxs") means the `main.wxs` of this JDK differs in structure from the one the fragments in `jpackage/wix` were written against.
5. Install it by double-clicking; the installation is per user and needs no elevation. Things to look at: with another version installed, the first page says that it will be replaced; the licence page shows paragraphs, not ragged lines; the last page offers to start the Simulator; with the Simulator running, a second run of the same `.msi` first shows the Cancel/Retry/Ignore box. The installation lands in `%LOCALAPPDATA%\GROOVE`, with the launchers' JVM options in `app\<launcher>.cfg`.
6. Check the running JVM from outside, since a GUI launcher has no console. A JDK's `jcmd -l` lists the Java processes with their main jar, which is how to tell the JVM from the windowless launcher process next to it (attaching to the latter fails with "Access is denied"); then `jcmd <pid> VM.flags` shows the options in effect, `-XX:+UseCompactObjectHeaders` among them. The bundled runtime has no `jcmd`; use the one of the JDK that built the installer.
7. Uninstall through Settings > Apps.

The `.msi` does a few things that jpackage's own WiX sources know nothing about, described in the paragraphs below. jpackage takes a custom `main.wxs` from a resource directory in place of the bundled one, so the script extracts the bundled one from the running JDK and splices the fragments of `jpackage/wix` into it at build time. A checked-in copy of `main.wxs` would go stale with every JDK upgrade; the splice instead fails the build if the structure of `main.wxs` changes.

Installing the `.msi` while GROOVE is running (typically an upgrade, but a repair or an uninstall as well) first asks the user to close it; the fragment for this is `jpackage/wix/files-in-use.wxf`. The fragment adds a WiX `CloseApplication` row per launcher executable that does nothing but prompt, and schedules the check before `RemoveExistingProducts`, so that nothing has been touched yet. The prompt is Windows Installer's Cancel/Retry/Ignore box: Retry looks again, Cancel ends the installation with the installed version intact (silently, when there is no wizard to return to, as in an uninstall), Ignore continues regardless. Since the check matches process names only, the rows are conditioned on a GROOVE being installed, so that a fresh installation cannot trip over some other product's `Viewer.exe`. A silent installation gets no box and continues.

Windows Installer's own handling of files in use does not work for GROOVE, which testing established the hard way. Its Restart Manager check (in InstallValidate) does find the processes exactly, but by default its dialog offers to close them, which cannot close the windowless launcher process that each jpackage launcher start leaves next to the JVM, so the dialog reports failure and returns until both processes are gone; and it would end the Simulator's session rather than close its window, which most likely leaves no chance to save grammars. Setting `MSIRESTARTMANAGERCONTROL` to `Disable` falls back to the older FilesInUse dialog, whose check matches loaded DLLs by name: it takes minutes and lists every application that loads the same runtime DLLs as the bundled Java runtime (browsers, mail clients, IDEs), so Retry never succeeds. `DisableShutdown`, which the fragment still sets for the Ignore case, only reports that a reboot will be required — twice in an upgrade, since the removal of the old version runs from the old version's own installer — and then blocks silently on the class-path jars, which the JVM opens without delete sharing, so that they can neither be replaced nor moved aside (the reboot-time replacement also needs privileges a per-user installation lacks); once GROOVE exited, the file copy failed outright (error 1304) and rolled back. Because jpackage schedules `RemoveExistingProducts` before costing, outside the installation transaction (it does so for downgrades, see JDK-8248264), that rollback leaves no GROOVE installed at all, only the files the JVM had held; the prompt up front is what keeps an installation from getting there.

Installing the `.msi` over another version of GROOVE first shows a page saying that the installed version, older or newer, will be replaced, with Continue and Cancel; the fragment is `jpackage/wix/replace-warning.wxf`. jpackage itself allows both upgrades and downgrades, and removes the other version without mentioning it. The page is shown before the licence page, when jpackage's own detection of related products has found one. That detection yields product codes; the page names the version being replaced by reading `DisplayVersion` from the uninstall registry entry of that product code, and falls back to "an older/newer version" if the entry is not found. Another build of the same version is replaced as well, with its own text on the page. jpackage derives the product code from name and version, so a rebuild of the same version would count as a minor update of the installed product, which `msiexec` refuses with error 1638 ("Another version of this product is already installed"), a message that is misleading next to this page; the script therefore gives every build its own product code, and the fragment adds an upgrade rule for exactly the same version. Running the very same `.msi` file again still opens Windows Installer's maintenance dialog (repair or remove). A silent installation shows no page.

The last page of the `.msi` installation offers to start the Simulator, through a checkbox that is ticked by default; the fragment is `jpackage/wix/launch-simulator.wxf`. It uses the optional checkbox of WiX's standard exit dialog, which the dialog shows after an installation or upgrade but not after a repair or an uninstall. Since Windows Installer cannot draw checkboxes transparently, the fragment also replaces WiX's white dialog background with `jpackage/icons/groove-dialog.bmp`, which is dialog gray where the controls are, with a green GROOVE panel beside them; the banner of the pages in between gets the same green and the G (`groove-banner.bmp`; see the README in `jpackage/icons`). A silent installation (`msiexec /qn`) shows no dialogs and so starts nothing. The Simulator is started by Windows Installer's custom-action host rather than by the installer window, and Windows does not hand the foreground to a program started that way: its window may open behind the windows already open. The other installers cannot offer this: a `.dmg` is installed by dragging the application into place, and `.deb` and `.rpm` package scripts run as root, often without a display.

The `.msi` shows the licence in a narrow box on its first page. jpackage converts a plain-text licence to RTF line by line, keeping the hard line breaks of `LICENSE.txt`, which then wrap raggedly; the script therefore writes the RTF itself at build time, one paragraph per block of `LICENSE.txt`, which stays the only copy of the text. With yFiles in the installer the same is done to `LICENSE.txt` and `LICENSE-YFILES.txt` together, which is why the latter is plain text with a blank line between its paragraphs rather than Markdown.

## The release page

The installers are not code-signed, so Windows and macOS block them at first, and rpm-based package managers may ask before installing the `.rpm` (`zypper` does, `dnf` normally does not; `apt` and `dpkg` never check local `.deb` files). The release page therefore explains how to get past that, in two places, both kept in `github`:

- `INSTALL-NOTE.md` opens the body of the release page. The `release` job builds the body with `github/release-notes.sh`, which appends this release's section of `include/CHANGES.md` (its first section) in a collapsed block, so that the note stays close to the asset list below it; a later step of the job appends an invisible HTML comment naming the `yfiles-lib` commit the yFiles library was taken from (see "Building" below). Run `bash github/release-notes.sh` to preview the body. To save the preview to a file from Windows PowerShell, keep the redirection inside bash, as in `bash -c "github/release-notes.sh > body.md"`: PowerShell 5.1's `>` writes UTF-16, which GitHub does not render as Markdown (in a gist, for instance).
- `IF-WINDOWS-OR-MACOS-BLOCKS-THE-INSTALLER.txt` is attached to the release as an asset, with step-by-step instructions. Its name is the message, for those who read nothing but the asset list.

Unlike the contents of `include`, neither file ends up in the zips or the installers.

## Postprocessing

1. In the main `pom.xml`, update the `revision` property (containing the release
   version `x.y.z`) by increasing `z` and adding the suffix `-SNAPSHOT`, and
   give the `revision` of `yfiles/pom.xml` the same value in the same commit:
   a command-line build of the backend fails while the two differ. (A
   pleasant side effect of the suffix: the Central portal rejects `-SNAPSHOT`
   versions, so an accidental `deploy` between releases cannot publish.)

# How to build the obfuscated yFiles jars

The optional yFiles graph-visualisation backend (the unit in `yfiles/` of this
repository, see its README) runs on the commercial yFiles library. The two reach users
inside GROOVE's binaries, in obfuscated form, and never as a component of their own:
yWorks did not approve the separate add-on of the 99.0.x test releases (see
`claude/yfiles-restructure.md`). The release reactor's `yfiles` profile obfuscates the
two and assembles `groove-x_y_z-yfiles-bin.zip` from them: the content of the plain
`groove-x_y_z-bin.zip`, plus the two obfuscated jars in `lib/`, launchers in `bin/` whose
class path lists them, and the yFiles notice at the root. The installer script packages
that zip when asked to, and the release workflow asks it to (see Installers above). The yFiles license (an academic project license held by the University of
Twente) has four consequences that shape this build:

- The library may be redistributed only in obfuscated form, and yWorks require the same
  of the code that calls it. The release reactor therefore has a module `yfiles` that
  runs yWorks' free obfuscator yGuard over the library jar together with the backend
  jar. All names of the library are renamed (except the few yWorks marks as
  reflectively used), and so are the classes and members of the backend, except for the
  methods by which it overrides or implements something of the GROOVE core or the JDK.
  No name of the backend has to survive: the core finds the backend through the
  `ServiceLoader`, and yGuard rewrites the provider file in the backend jar to the new
  name of the provider class. The backend is a named module during development (see
  `yfiles/README.md`, *The unit as a module*); its module descriptor names the package
  and the provider class by their original names, so the module takes it out of the
  backend jar before the obfuscation (`target/in/classpath`). The binaries run on the
  class path, where the provider file is what counts.
- Only the licensed developer may develop against the plain library jar, and the
  release build may use it under the project license. It therefore exists in two
  places only: the local Maven repository of that developer, and the private
  repository the release workflow checks out (see below). It must never be uploaded
  anywhere public.
- A binary with the library in it may be used for academic, non-commercial purposes
  only, and has to say so. The restrictions are in `yfiles/include/LICENSE-YFILES.txt`,
  which is the only copy of that text: the yFiles zip carries it at its root, next to
  `LICENSE.txt`, and an installer built from that zip shows the two as one licence to
  accept. `LICENSE.txt` itself, which goes into every zip and heads the source, says
  that it does not cover the yFiles library and points to that file. The other texts a
  user meets say the same in their own words: `include/README.md` in the zips, the
  change logs, `github/INSTALL-NOTE.md` on the release page, and the notice below the
  version in the About box of the application, which is the text of
  `YFilesBackend.getLicenseNotice()` in the backend unit. A change to the
  restrictions has to reach all of them, and the download page of the website.
- The license file the backend jar carries must itself be redistributable. The backend
  packages the distribution license (`yfiles.license.file`, see below) and never the
  development license, whose `<distribution>false</distribution>` forbids precisely
  this. That property is meant to be overridable, for a development build with the
  development watermark, so the `yfiles` module does not trust it: the `check-license`
  execution of its `pom.xml` reads every license out of the obfuscated jars after the
  obfuscation, and fails the build on one marked non-distributable. The check exists
  because the public 99.0.x test releases of 2026-09 shipped the development license,
  in the days before the backend build chose between the two.

## Building

Prerequisites, once: a clone of the private repository `nl-utwente-groove/yfiles-lib`
next to this one, which holds the library jar and its license files, and the library
installed in the local Maven repository from that jar, as described in
`yfiles/README.md`; yGuard itself comes from Maven Central like any plugin.

1. Build and install the core artifact and generate the javadoc as for the standard
   release, then build and install the backend against it:

    `mvn -f yfiles/pom.xml -Dgroove.install.skip=true -Drevision=x.y.z clean install`

    (from the repository root; this runs the backend's tests, which open a
    Simulator window briefly).

2. Package by running Maven in the release directory with the `yfiles` profile:

    `mvn clean package -Drevision=x.y.z -Pyfiles`

    This one pass produces the standard zips in `release/target`, exactly as without
    the profile, and next to them `groove-x_y_z-yfiles-bin.zip`. On the way, the module
    `yfiles` writes the two obfuscated jars to `release/yfiles/target/lib`, under the
    file names of their inputs, and the launchers of the yFiles zip to
    `release/yfiles/target/bin`: copies of the standard launchers, with the two jars
    added to the `Class-Path` of their manifests. The standard launchers themselves are
    not touched, which is why the profile leaves the standard zips unchanged; the
    September build of a yFiles edition, which switched the whole reactor in a second
    pass, did not. The name mapping of the obfuscation is written to
    `release/yfiles/target/yguard.log.xml.gz` (view it with
    `java -jar yguard.jar <log>` from the yGuard distribution).

**The name mapping must never be published.** It maps every obfuscated name back to
the library's own, so a public copy would undo the obfuscation the license requires.
Nothing publishes it: the release step attaches only `release/target/*.zip`, and the
workflow keeps no artifacts. Do not attach it to a release, upload it as a workflow
artifact (on a public repository any signed-in GitHub user can download those), or
commit it.

Nor does it need keeping: the renaming is deterministic, so the mapping of a release
can be regenerated when a user's stack trace needs translating. Two obfuscations of
the same inputs, under JDK 26 and JDK 21, gave logs identical except for yGuard's
timestamp and memory-usage comments (checked 2026-09-24 on Windows, when the backend
still kept its names; that a Linux runner renames the same way is expected, not
checked). Such a trace needs the mapping for its `com.yworks` frames, which carry no
line numbers, and for the frames of the backend, which carry obfuscated class and
method names with the original line numbers (and `y` for the source file, without which
the JDK prints no line number). To regenerate the mapping of release x.y.z, repeat the
two steps above from the release tag with the inputs of that release: the same
`yguard.version` and the same library jar, that of the `yfiles-lib` commit the release
was built with (its `lib/` directory keeps its history). The release workflow checks
that repository out by branch name, which does not fix the commit, so it records the
commit at the end of the release notes, as an HTML comment that the release page does
not show: `gh release view release-x_y_z --json body` prints it.

The script `do-all.sh yfiles` runs the standard steps with these two in place of the
standard packaging.

## Checks on the zips

The build checks what it is about to distribute rather than how it was configured,
each check failing the build with a message that says what is wrong:

- Every zip, right after it is assembled (the `check-zip` execution of
  `assembly/pom.xml`, declared by each assembling module after its assembly): no entry
  may look like the name mapping (a name containing `yguard`, or ending in
  `.log.xml.gz`), and a zip other than the yFiles one may have no entry whose name
  contains `yfiles` or `yworks`, nor a launcher whose manifest mentions them. The
  assembly's filter on dependencies matches leniently (see `assembly/bin/zip.xml`),
  so a mistake in a descriptor or a dependency could otherwise put a plain library jar
  into a zip unnoticed. For the same reason the yFiles zip's module depends on the
  `yfiles` module only for the order of the reactor, with all of that module's own
  dependencies excluded: the plain backend and library jars are in the dependency graph
  of no assembling module.
- The yFiles zip (the `check-yfiles-zip` execution of `assembly/yfiles-bin/pom.xml`):
  its `lib/` holds the jars of the standard bin zip and exactly the two of
  `release/yfiles/target/lib` besides, each byte for byte the obfuscated one and not
  the input of the obfuscation in `release/yfiles/target/in`, so the licence check of
  the `yfiles` module holds for the zip as well. Its root must hold
  `LICENSE-YFILES.txt`, the restrictions a binary with yFiles has to carry. Then the zip is unpacked and its Imager
  launcher renders a small grammar of `junit/samples` with `-b yfiles`, headlessly; the
  check fails unless the run succeeds and writes images without the Imager's warning
  that the backend is not available. As a control, the standard Imager launcher, run
  the same way in the same tree, must print that warning, so that the check does not go
  blind when the warning's wording changes. `-Dyfiles.smoke.skip=true` skips the run.
- The obfuscated jars themselves carry no non-distributable licence (the `check-license`
  execution of `yfiles/pom.xml`, see above).

## In the release workflow

The `release` job of `.github/workflows/release.yml` runs the obfuscation along with
the standard zips. For that it checks out the
private repository `nl-utwente-groove/yfiles-lib` next to the code checkout, at the
branch of the same name as the branch being released if there is one and at `main`
otherwise (see "Deploying" above, on pre-releases). That
repository holds three files in its `lib/` directory: `yfiles-for-java-swing.jar`, the plain library
jar from the `lib` directory of the licensed distribution, and the two license files
yWorks issued with it, of which the backend's build packages the distribution license
(`yfiles.license.file` in its `pom.xml`, by default `com.yworks.yfiles.java.license.xml`)
and never the development license. The workflow installs the jar into
the runner's local Maven repository under the coordinates of `yfiles/pom.xml`
(whose `yfiles.version` it reads), builds the backend from `yfiles/`
with its tests (the license directory defaults to the `lib/` directory of that checkout; the
tests open Simulator windows, so they run under Xvfb), and packages the release with
the `yfiles` profile. The yFiles zip is attached to the release with the standard
zips; the installers are still built from the standard bin zip.

The checkout authenticates with the repository secret `YFILES_LIB_TOKEN`, a
fine-grained personal access token of the licensed developer with read access to
`yfiles-lib` only (Contents: read). Under the one-seat project license, nobody but
that developer and this token may read the private repository. A new library version
means a new jar and new license files there, and a new `yfiles.version` in both
`yfiles/pom.xml` and `release/pom.xml`.

The pull-request build (`maven.yml`) does not use the profile: secrets are not
available to workflows run for pull requests from forks, and the standard build must
keep working without the library, as it does.

## Checking the result

The obfuscated backend jar, `release/yfiles/target/lib/groove-yfiles-x.y.z.jar`, holds
no class under `nl/`: `jar tf` lists its classes under obfuscated package names, and its
provider file `META-INF/services/nl.utwente.groove.gui.view.GraphBackend` names one of
them. That the yFiles zip renders on this stack is checked by the build itself, with
the zip's own Imager launcher (see "Checks on the zips" above); by hand, `bin/Imager.jar
-b yfiles` of the unpacked zip does the same.

The build also runs the backend's own tests on an obfuscated stack, in the `obfuscate`
execution of `yfiles/pom.xml`, after the release pass. The tests name the backend's
classes and so cannot link against the released backend jar. They do run when they are
obfuscated in the same yGuard pass as the backend and the library, so the build makes a
second pass, the test pass, with the same yGuard configuration (a macro called by both
passes, so that the two cannot drift apart) and the backend's test classes as a third
jar. That jar is the `tests` artifact of the backend, installed with it by its build, so
that tests and backend come from the same build. The test classes keep their names and
those of their members, so that the reports are readable and the JUnit console launcher
finds them; the list of names is read from the tests jar, and the build fails if a class
of it comes out under another name. Their package, the backend's, therefore keeps its
name in the test pass, unlike in the release pass. The launcher then runs the tests on
the JVM's own class path, with the three jars of the test pass, the external class path
of yGuard and `junit-platform-console-standalone`, with the JVM settings of the backend's
own Surefire run (assertions, native access, `groove.dir` and `groove.test.robot`). It
fails the build if a test fails or if it finds no tests at all. Its output is in
`release/yfiles/target/test-pass/console.txt` and its reports under `reports` there; the
test pass writes a name mapping of its own in that directory, as unpublishable as the
other.

The limitation is in the word *same*: the test pass is a second obfuscation with the
same configuration, not the released jars themselves, which differ from its output in
the names of the backend's package and of whatever the test classes reach. What runs the
released jars is the smoke test of the yFiles zip. The tests open Simulator windows, as
in the backend's own build; on a machine without a display, or to keep the windows off a
desktop in use, pass `-Dyfiles.testpass.jvmargs=-Djava.awt.headless=true`, under which
the Simulator tests skip by assumption. `-DskipTests` or `-Dyfiles.testpass.skip=true`
skips the test pass, and `-Dgroove.test.robot=true` adds the Robot-driven tests, as in the
backend's build. The counts should be those of the backend's own build: headlessly, on
2026-11-16, both ran 55 of the 73 tests, the Simulator class being skipped as a whole;
on a display both ran all 73, 6 of them Robot tests that skip without the flag (the
console launcher counts such skips as aborted, next to 67 successful).

# How to build a Maven artefact

The process is quite complicated; although largely automated, many things can go wrong.

## Central Maven staging

Invoke the Maven target using

`mvn clean deploy`

with the `revision` property in the pom set to the version number also used for the github release; see above.

This can alternatively be invoked in Eclipse using the `GROOVE - maven deploy to Central Repository` launch configuration.

The relevant plugins are `central-publishing-maven-plugin` for staging, which relies on `maven-gpg-plugin` for signing. Staging consists of signing, uploading and checking the Maven Central constraints. The subsections below should not be necessary once everything is up and running, but are included for completeness and understanding, and may be useful in case part of the configuration is lost.


### Signing

The signing phase involves many different components, all of which have to work correctly. The following may help to understand them:

- GPG key: this has to be created, published to one or more keyservers and then used.
    * To have the required functionality at hand, use `Gpg4win`, which installs itself under `C:\Program Files` in the eponymous directory, as well as a second directory `GnuPG`. The package also comes with a stand-alone GUI-based app `Kleopatra`, which helps in unserstanding which keys are currently known and can also create a key if required.
    * Since Maven runs in batch mode, the best (probably only) is to use a passphrase-less key. Any other setting results in the error message `no pinentry` during the Maven signing phase.
    * The intended key should be set as default in the gpg configuration file, which is located in `%APPDATA%\gnupg\gpg.conf`. This file should (at least) contain a line `default-key <fingerprint>`, where the fingerprint, which is the identifier of the key, can be found using (e.g.) Kleopatra.
    * To upload a key to a keyserver, use `gpg --keyserver <url> --send-keys <fingerprint>`. The Maven staging plugin looks for the key on different servers, among which are `https://keys.openpgp.org` and `http://keyserver.ubuntu.com:11371`, so it's a good idea to publish the key there straight away.
    * To avoid having to go through this again, use a key without expiration date.
    * An alternative way of dealing with default keys and passphrases, by setting them as explicit arguments in the Maven configuration, is described [here](https://central.sonatype.org/publish/publish-maven/#gpg-signed-components)

- Pinentry: This is a utility, installed in the `GnuPG` directory mentioned above, that interactively requests the passphrase. It should be circumvented by using a key without passphrase. The message `no pinentry` does not necessarily mean that the utility cannot be found, it rather seems to mean that Maven cannot invoke it in batch mode; the fact that it attempts to do this in the first place probably means that the default key it's trying to use for signing has a passphrase.

- GPG agent: this is a daemon that needs to be running, and therefore needs to be installed and findable. On windows, `gpg-agent` is contained in the `GnuPG` directory mentioned above. It is advertised as starting up automatically. There is a configuration file `%APPDATA%\gnupg\gpg-agent.conf` (settings are described [here](https://www.gnupg.org/%28it%29/documentation/manuals/gnupg/Agent-Options.html)), but it should not be necessary to use this. If you get a message `gpg: can't connect to the gpg-agent: IPC connect call failed`, the following may solve this (invoke from command line): `gpg-connect-agent reloadagent /bye`

### Authenticating

The uploading phase requires Maven to be able to log into `https://central.sonatype.com`, but using a User Token rather than the User ID. 
This User Token is be generated by the server itself (see <https://central.sonatype.org/publish/generate-portal-token/>) 
and must then be saved in the Maven `settings.xml` file, which is in the `.m2` directory in `C:\Users\<username>` --- 
with the understanding that the Server ID, which on that page is given as `${server}`, should be set to the name also used in the Maven staging plugin --- by default `central`.

Syntax for content `settings.xml` is (see also [this page](https://central.sonatype.org/publish/publish-portal-maven/)):

```
<settings>
	<servers>
		<server>
			<id>ServerID</id>
			<username>UserTokenId</username>
			<password>UserTokenPassword</password>
		</server>
	</servers>
</settings>
```

where `ServerID` is `central`, and `UserTokenId` and `UserTokenPassword` are *both* generated through <https://central.sonatype.com/usertoken>.

## Deployment

If `autoPublish` is not set in the `central-publishing-maven-plugin`, the staging must be followed by a manual deployment/release step described in <https://central.sonatype.org/publish-ea/publish-ea-guide>.


