# How to build a github release

This chapter explains how to build a github release for Groove. A second chapter, below, summarises the actions to create a Maven artifact.

The github release, as meant here, consists of two dedicated zip-files (as well as the complete source code, in `.zip` and `.tar.gz` format):

- `groove-x_y_z-bin.zip`
- `groove-x_y_z-bin+doc.zip`

Both of these contain a top-level README.md that explains their structure and how to install the tool.

In addition, the release workflow builds self-contained installers (with a bundled Java runtime, so users need no Java installation) for Windows (`.msi`), macOS (`.dmg`, both Intel and Apple silicon) and Linux (`.deb` and `.rpm`); see the Installers section below. A release built with the `yfiles` profile also yields the yFiles add-on, `groove-x_y_z-yfiles-addon.zip`; see the second chapter.

## Preparation

Below, the _release directory_ refers to the project subdirectory (of the `code` repository) called `release`.

1. Update the version and date in the GROOVE source:

    - The version number is the `revision` property in the main `pom.xml`: a semantic version `x.y.z` with the optional suffix `-SNAPSHOT`, where `x`, `y` and `z` are natural numbers without leading zeros and `x` is below 100 (every Maven build checks this form, and the release workflow additionally rejects the suffix). The number might already be correct (it is updated in postprocessing, see below) but the changes in this revision may necessitate updating the `x` or `y` values. In any case remove the `-SNAPSHOT` suffix. (The `GROOVE_VERSION` resource file is generated from this property by resource filtering; do not edit it.)
    - The `revision` property of the `pom.xml` of the private `yfiles-lib` repository (the optional yFiles backend) should be updated along with it, to keep the two equal. This is development hygiene rather than a condition for the release: every build of the backend, by hand or in a workflow, is passed the version of the main pom with `-Drevision`, so the add-on gets the right version either way. The default in that pom governs Eclipse, where equal versions are what make m2e resolve the dependency from the open `groove` project instead of an installed artifact; a command-line build of the backend fails if the two have drifted apart.
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

A pre-release is typically tagged on a branch, since testing the workflow before merging is the point of it. The yFiles add-on then has to come from the matching state of the private repository, which is not its default branch. The `release` job and `backend.yml` both choose that state by the rule of `release/github/choose-backend.sh`: the candidates are the branches of this repository whose tip is an ancestor of the commit built — the branch tagged or pushed and everything it was branched off — and the add-on is built from the nearest of those that `yfiles-lib` also has, falling back to `main`. Tagging a throwaway branch off `yworks-migration` therefore selects `yfiles-lib`'s `yworks-migration`, whereas a `yfiles-lib` branch named after the branch tagged, if there is one, wins over it as the nearer match. A release tagged on `master` matches nothing there, the private default branch being `main`, and is built from `main` as before.

The tagged branch must be pushed, since the rule resolves against the remote branches of this repository; so it should also be removed again to avoid clutter. Remove it locally from Eclipse by

`git branch -D test-release`

(the `-D` signifies that deletion should be carried out even though there are commits on this branch that are nowhere else); remove it remotely from `origin` just as for tags:

`git push origin --delete test-release`

## Installers

The `installers` job of the release workflow (`.github/workflows/release.yml`) runs `jpackage/build-installer.sh` on a matrix of platform runners — jpackage can only build for the platform it runs on — and attaches the resulting installers to the same github release. The job sets up JDK 25 rather than the Java 21 the code targets: jpackage bundles a runtime trimmed from the JDK it runs on, so that is the Java the installers ship. The launchers start their JVM with compact object headers (`-XX:+UseCompactObjectHeaders`, a product option since JDK 25, worth 7–10% of heap in exploration); the script adds the option only when its jpackage is 25 or newer, since an older JVM refuses to start on it.
The script unpacks the `-bin` zip and turns it into a native package with a bundled, jlink-trimmed Java runtime: the Simulator becomes the main launcher (which jpackage names after the application: GROOVE), the tools (Simulator, Generator, ModelChecker, Imager, Viewer) become additional launchers named after themselves. All of them carry menu entries, so the menu lists GROOVE next to the Simulator: jpackage offers no way to suppress the main launcher's entry that leaves those of the tools in place.

To try this locally without any packaging tools, build the release as described above and then run

    bash jpackage/build-installer.sh x.y.z app-image

which produces the raw application directory (no installer) under `jpackage/target/dist`. On Windows this must be Git Bash: in PowerShell, `bash` resolves to WSL's `C:\Windows\System32\bash.exe`, and the script then runs under Linux with the Linux jpackage, which knows no `msi` ("Invalid or unsupported type: [msi]"). Either run the script from a Git Bash window, or name Git Bash: `& "C:\Program Files\Git\bin\bash.exe" release/jpackage/build-installer.sh x.y.z msi`.

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
7. Uninstall through Settings > Apps. This also removes the yFiles add-on from the extension directory, see below.

Uninstalling the `.msi` also removes the yFiles add-on (see the second chapter) from the user's extension directory, so that the library does not outlive the GROOVE version it was built for; the old version of an upgrade counts as uninstalled, and the new version then offers the add-on again at its first start. The other installers do not do this: a `.dmg` has no uninstall step at all. jpackage's own WiX sources know nothing about the add-on, but take a custom `main.wxs` from a resource directory in place of the bundled one, so the script extracts the bundled one from the running JDK and splices the removal (`jpackage/wix/addon-cleanup.wxf`) into it at build time. A checked-in copy of `main.wxs` would go stale with every JDK upgrade; the splice instead fails the build if the structure of `main.wxs` changes. Only the add-on directory and, if they are empty afterwards, the directories above it are removed; other extensions the user put there stay, as does the record in the Java preferences that the first-run question was asked, so a reinstallation of the same version does not repeat that question.

Installing the `.msi` while GROOVE is running (typically an upgrade, but a repair or an uninstall as well) first asks the user to close it; the script splices `jpackage/wix/files-in-use.wxf` into `main.wxs` the same way. The fragment adds a WiX `CloseApplication` row per launcher executable that does nothing but prompt, and schedules the check before `RemoveExistingProducts`, so that nothing has been touched yet. The prompt is Windows Installer's Cancel/Retry/Ignore box: Retry looks again, Cancel ends the installation with the installed version intact (silently, when there is no wizard to return to, as in an uninstall), Ignore continues regardless. Since the check matches process names only, the rows are conditioned on a GROOVE being installed, so that a fresh installation cannot trip over some other product's `Viewer.exe`. A silent installation gets no box and continues.

Windows Installer's own handling of files in use does not work for GROOVE, which testing established the hard way. Its Restart Manager check (in InstallValidate) does find the processes exactly, but by default its dialog offers to close them, which cannot close the windowless launcher process that each jpackage launcher start leaves next to the JVM, so the dialog reports failure and returns until both processes are gone; and it would end the Simulator's session rather than close its window, which most likely leaves no chance to save grammars. Setting `MSIRESTARTMANAGERCONTROL` to `Disable` falls back to the older FilesInUse dialog, whose check matches loaded DLLs by name: it takes minutes and lists every application that loads the same runtime DLLs as the bundled Java runtime (browsers, mail clients, IDEs), so Retry never succeeds. `DisableShutdown`, which the fragment still sets for the Ignore case, only reports that a reboot will be required — twice in an upgrade, since the removal of the old version runs from the old version's own installer — and then blocks silently on the class-path jars, which the JVM opens without delete sharing, so that they can neither be replaced nor moved aside (the reboot-time replacement also needs privileges a per-user installation lacks); once GROOVE exited, the file copy failed outright (error 1304) and rolled back. Because jpackage schedules `RemoveExistingProducts` before costing, outside the installation transaction (it does so for downgrades, see JDK-8248264), that rollback leaves no GROOVE installed at all, only the files the JVM had held; the prompt up front is what keeps an installation from getting there.

Installing the `.msi` over another version of GROOVE first shows a page saying that the installed version, older or newer, will be replaced, with Continue and Cancel; it is spliced into `main.wxs` the same way (`jpackage/wix/replace-warning.wxf`). jpackage itself allows both upgrades and downgrades, and removes the other version without mentioning it. The page is shown before the licence page, when jpackage's own detection of related products has found one. That detection yields product codes; the page names the version being replaced by reading `DisplayVersion` from the uninstall registry entry of that product code, and falls back to "an older/newer version" if the entry is not found. It does not check whether the yFiles add-on is present, and so says that it is removed "if installed". Another build of the same version is replaced as well, with its own text on the page. jpackage derives the product code from name and version, so a rebuild of the same version would count as a minor update of the installed product, which `msiexec` refuses with error 1638 ("Another version of this product is already installed"), a message that is misleading next to this page; the script therefore gives every build its own product code, and the fragment adds an upgrade rule for exactly the same version. Running the very same `.msi` file again still opens Windows Installer's maintenance dialog (repair or remove). A silent installation shows no page.

The last page of the `.msi` installation offers to start the Simulator, through a checkbox that is ticked by default; it is spliced into `main.wxs` the same way (`jpackage/wix/launch-simulator.wxf`). It uses the optional checkbox of WiX's standard exit dialog, which the dialog shows after an installation or upgrade but not after a repair or an uninstall. Since Windows Installer cannot draw checkboxes transparently, the fragment also replaces WiX's white dialog background with `jpackage/icons/groove-dialog.bmp`, which is dialog gray where the controls are, with a green GROOVE panel beside them; the banner of the pages in between gets the same green and the G (`groove-banner.bmp`; see the README in `jpackage/icons`). A silent installation (`msiexec /qn`) shows no dialogs and so starts nothing. The Simulator is started by Windows Installer's custom-action host rather than by the installer window, and Windows does not hand the foreground to a program started that way: its window may open behind the windows already open. The other installers cannot offer this: a `.dmg` is installed by dragging the application into place, and `.deb` and `.rpm` package scripts run as root, often without a display.

The `.msi` shows the licence in a narrow box on its first page. jpackage converts a plain-text licence to RTF line by line, keeping the hard line breaks of `LICENSE.txt`, which then wrap raggedly; the script therefore writes the RTF itself at build time, one paragraph per block of `LICENSE.txt`, which stays the only copy of the text.

## The release page

The installers are not code-signed, so Windows and macOS block them at first, and rpm-based package managers may ask before installing the `.rpm` (`zypper` does, `dnf` normally does not; `apt` and `dpkg` never check local `.deb` files). The release page therefore explains how to get past that, in two places, both kept in `github`:

- `INSTALL-NOTE.md` opens the body of the release page. The `release` job builds the body with `github/release-notes.sh`, which appends this release's section of `include/CHANGES.md` (its first section) in a collapsed block, so that the note stays close to the asset list below it; a later step of the job appends an invisible HTML comment naming the `yfiles-lib` commit of the add-on (see "Building" below). Run `bash github/release-notes.sh` to preview the body. To save the preview to a file from Windows PowerShell, keep the redirection inside bash, as in `bash -c "github/release-notes.sh > body.md"`: PowerShell 5.1's `>` writes UTF-16, which GitHub does not render as Markdown (in a gist, for instance).
- `IF-WINDOWS-OR-MACOS-BLOCKS-THE-INSTALLER.txt` is attached to the release as an asset, with step-by-step instructions. Its name is the message, for those who read nothing but the asset list.

Unlike the contents of `include`, neither file ends up in the zips or the installers.

## Postprocessing

1. In the main `pom.xml`, update the `revision` property (containing the release
   version `x.y.z`) by increasing `z` and adding the suffix `-SNAPSHOT`. (A
   pleasant side effect of the suffix: the Central portal rejects `-SNAPSHOT`
   versions, so an accidental `deploy` between releases cannot publish.)

# How to build the yFiles add-on

The yFiles add-on is a small second artifact of the same release: the optional yFiles
graph-visualisation backend (the root project of the private repository
`nl-utwente-groove/yfiles-lib`, see its README) and the commercial yFiles
library it runs on, zipped up as `groove-x_y_z-yfiles-addon.zip`. The standard zips
and installers do not contain it; a user unzips it into GROOVE's extension directory
(`%APPDATA%\GROOVE\extensions` on Windows, `~/Library/Application Support/GROOVE/extensions`
on macOS, `$XDG_DATA_HOME/groove/extensions`, normally `~/.local/share/groove/extensions`,
elsewhere; the system property `groove.extensions.dir` overrides the location), from
which GROOVE loads it at start-up
(`nl.utwente.groove.util.Extensions`). The zip unpacks into a subdirectory `yfiles/`
there, holding the two jars and the license notice. The yFiles license (an academic
project license held by the University of Twente) has four consequences that shape
this build:

- The library may be redistributed only in obfuscated form. The release reactor
  therefore has a module `yfiles` that runs yWorks' free obfuscator yGuard over the
  library jar together with the backend jar: all names of the library are renamed
  (except the few yWorks marks as reflectively used), and the backend's references
  to them are rewritten, while the backend's own classes and members keep their
  names since the main jar and its `ServiceLoader` registration need them.
- Only the licensed developer may develop against the plain library jar, and the
  release build may use it under the project license. It therefore exists in two
  places only: the local Maven repository of that developer, and the private
  repository the release workflow checks out (see below). It must never be uploaded
  anywhere public.
- The add-on may be used for non-commercial purposes only. `yfiles/include/YFILES-ADDON.md`
  states this and is placed in the add-on's directory; the download page must say
  the same next to the add-on.
- The license file the add-on carries must itself be redistributable. The backend
  packages the distribution license (`yfiles.license.file`, see below) and never the
  development license, whose `<distribution>false</distribution>` forbids precisely
  this. That property is meant to be overridable, for a development build with the
  development watermark, so the `yfiles` module does not trust it: the `check-license`
  execution of its `pom.xml` reads every license out of the obfuscated jars after the
  obfuscation and before the assembly, and fails the build on one marked
  non-distributable. The check exists because the public 99.0.x test releases of
  2026-09 shipped the development license, in the days before the backend build chose
  between the two.

The add-on is built for one GROOVE version: the manifest of the backend jar records
it (attribute `GROOVE-Version`, set by the backend's `pom.xml` from its `revision`), and a
GROOVE of another version skips the jar with a warning at start-up. Every release
therefore needs its own add-on.

## Building

Prerequisites, once: a clone of the private repository `nl-utwente-groove/yfiles-lib`
next to this one, and the library installed in the local Maven repository from the jar
in its `lib/` directory, both as described in the `README.md` there; yGuard itself comes
from Maven Central like any plugin.

1. Build and install the core artifact and generate the javadoc as for the standard
   release, then build and install the backend against it:

    `mvn -f ../yfiles-lib/pom.xml -Dgroove.install.skip=true -Drevision=x.y.z clean install`

    (from the repository root; this runs the backend's tests, which open a
    Simulator window briefly).

2. Package the add-on by running Maven in the release directory with the `yfiles`
   profile:

    `mvn clean package -Drevision=x.y.z -Pyfiles`

    This produces the standard zips and, next to them, `groove-x_y_z-yfiles-addon.zip`
    in `release/target`. The obfuscation runs in `release/yfiles`; its name mapping is
    written to `release/yfiles/target/yguard.log.xml.gz` (view it with
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
timestamp and memory-usage comments (checked 2026-09-24 on Windows; that a Linux
runner renames the same way is expected, not checked). Such a trace needs the mapping
only for its `com.yworks` frames, which carry no line numbers; the backend's own frames
keep their names and lines. To regenerate the mapping of release x.y.z, repeat the two
steps above from the release tag with the inputs of that release: the same library jar
(the `lib/` directory of `yfiles-lib` keeps its history), the same `yguard.version`,
and the `yfiles-lib` commit the release was built from. The release workflow checks
that repository out by branch name, which does not fix the commit, so it records the
commit at the end of the release notes, as an HTML comment that the release page does
not show: `gh release view release-x_y_z --json body` prints it.

The script `do-all.sh yfiles` runs the standard steps and then these two. The
installers need nothing for the add-on: the standard ones bundle a runtime that
suffices for it. The Windows installer does know of it in one respect: uninstalling
GROOVE removes the add-on directory (see the Installers section).

## In the release workflow

The `release` job of `.github/workflows/release.yml` builds the add-on along with the
standard zips, so that a release needs no manual step. For that it checks out the
private repository `nl-utwente-groove/yfiles-lib` next to the code checkout, at the
branch of the same name as the branch being released if there is one and at `main`
otherwise (see "Deploying" above, on pre-releases). That
repository holds three files in its `lib/` directory: `yfiles-for-java-swing.jar`, the plain library
jar from the `lib` directory of the licensed distribution, and the two license files
yWorks issued with it, of which the backend's build packages the distribution license
(`yfiles.license.file` in its `pom.xml`, by default `com.yworks.yfiles.java.license.xml`)
and never the development license; the rest of that repository is
the source of the backend itself, its root project, which the yFiles license
does not allow to be public. The workflow installs the jar into
the runner's local Maven repository under the coordinates of that repository's
`pom.xml` (whose `yfiles.version` it reads), builds the backend from that
checkout with its tests (the license directory defaults to its `lib/` directory; the
tests open Simulator windows, so they run under Xvfb), and packages the release with
the `yfiles` profile; the add-on zip is then attached to the github release by the
same step as the standard zips.

The checkout authenticates with the repository secret `YFILES_LIB_TOKEN`, a
fine-grained personal access token of the licensed developer with read access to
`yfiles-lib` only (Contents: read). Under the one-seat project license, nobody but
that developer and this token may read the private repository. A new library version
means a new jar and new license files there, and a new `yfiles.version` in both
the `pom.xml` of that repository and `release/yfiles/pom.xml` (here).

The pull-request build (`maven.yml`) does not use the profile: secrets are not
available to workflows run for pull requests from forks, and the standard build must
keep working without the library, as it does.

## Checking the result

The add-on zip holds `yfiles/groove-yfiles-x.y.z.jar` and
`yfiles/yfiles-for-java-swing-<v>.jar`, both obfuscated, and `yfiles/YFILES-ADDON.md`.
Unzipping it into an otherwise empty directory `<ext>` and running, from an unzipped
standard release,

    java -Dgroove.extensions.dir=<ext> -jar bin/Imager.jar -b yfiles -f png <grammar> <dir>

exercises the obfuscated library headlessly through the extension loader (without the
add-on, the Imager warns that the backend is not available and renders with JGraph).
The backend's own tests can be run against the obfuscated jars as described in
the `README.md` of the `yfiles-lib` repository.

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


