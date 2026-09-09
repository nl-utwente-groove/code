# How to build a github release

This chapter explains how to build a github release for Groove. A second chapter, below, summarises the actions to create a Maven artifact.

The github release, as meant here, consists of two dedicated zip-files (as well as the complete source code, in `.zip` and `.tar.gz` format):

- `groove-x_y_z-bin.zip`
- `groove-x_y_z-bin+doc.zip`

Both of these contain a top-level README.md that explains their structure and how to install the tool.

In addition, the release workflow builds self-contained installers (with a bundled Java runtime, so users need no Java installation) for Windows (`.msi`), macOS (`.dmg`, both Intel and Apple silicon) and Linux (`.deb`); see the Installers section below. A release built with the `yfiles` profile also yields the yFiles add-on, `groove-x_y_z-yfiles-addon.zip`; see the second chapter.

## Preparation

Below, the _release directory_ refers to the project subdirectory (of the `code` repository) called `release`.

1. Update the version and date in the GROOVE source:

    - The version number is the `revision` property in the main `pom.xml`: a semantic version `x.y.z` with the optional suffix `-SNAPSHOT`. The number might already be correct (it is updated in postprocessing, see below) but the changes in this revision may necessitate the `x` or `y` values. In any case remove the `-SNAPSHOT` suffix. (The `GROOVE_VERSION` resource file is generated from this property by resource filtering; do not edit it.) The `revision` property of the `pom.xml` of the private `yfiles-lib` repository (the optional yFiles backend) must be kept equal to it.
    - The remaining files in `src/main/resources/nl/utwente/groove/resource/version`:
        - `GROOVE_BUILD`: the build date, in format `YYYYMMDD`. Update to the build date.
        - [Optional] `GXL_VERSION`: the name of the version of GXL currently used for the encoding of graphs. (This will rarely change.)
        - [Optional] `JAVA_VERSION`: the version of Java to be used for the build.  

2. Update the `include/CHANGES.md` file in the release directory
   to reflect all changes with respect to the previous release.

3. [Optional] Update `include/usermanual.pdf` file in the groove-release project with the newest version of the manual.

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
in two different ways, it is up to the developer to ensure that they are identical.

1. Commit and push the entire code repository, including the changes under "Preparation".

2. Create and push a tag of the form `release-x_y_z`

If something goes wrong on github and you have to repeat the last step, you first have to delete the remote tag on the command line, like so:

`git push --delete origin release-x_y_z`

## Installers

The `installers` job of the release workflow (`.github/workflows/release.yml`) runs `jpackage/build-installer.sh` on a matrix of platform runners — jpackage can only build for the platform it runs on — and attaches the resulting installers to the same github release. The script unpacks the `-bin` zip and turns it into a native package with a bundled, jlink-trimmed Java runtime: the Simulator becomes the main launcher (which jpackage names after the application: GROOVE), the tools (Simulator, Generator, ModelChecker, Imager, Viewer) become additional launchers named after themselves, and those carry the menu entries.

To try this locally without any packaging tools, build the release as described above and then run

    bash jpackage/build-installer.sh x.y.z app-image

which produces the raw application directory (no installer) under `jpackage/target/dist`. Building the actual `.msi` locally additionally requires the WiX toolset.

## The release page

The installers are not code-signed, so Windows and macOS block them at first. The release page therefore explains how to get past that, in two places, both kept in `github`:

- `INSTALL-NOTE.md` opens the body of the release page. The `release` job builds the body with `github/release-notes.sh`, which appends this release's section of `include/CHANGES.md` (its first section) in a collapsed block, so that the note stays close to the asset list below it. Run `bash github/release-notes.sh` to preview the body.
- `IF-WINDOWS-OR-MACOS-BLOCKS-THIS-INSTALLER-READ-ME.txt` is attached to the release as an asset, with step-by-step instructions. Its name is the message, for those who read nothing but the asset list.

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
on macOS, `~/.groove/extensions` elsewhere; the system property `groove.extensions.dir`
overrides the location), from which GROOVE loads it at start-up
(`nl.utwente.groove.util.Extensions`). The zip unpacks into a subdirectory `yfiles/`
there, holding the two jars and the license notice. The yFiles license (an academic
project license held by the University of Twente) has three consequences that shape
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
    kept in `release/yfiles/target/yguard.log.xml.gz` (view it with
    `java -jar yguard.jar <log>` from the yGuard distribution) and should be kept with
    the release, in case a stack trace from a user needs translating.

The script `do-all.sh yfiles` runs the standard steps and then these two. The
installers need nothing for the add-on: the standard ones bundle a runtime that
suffices for it.

## In the release workflow

The `release` job of `.github/workflows/release.yml` builds the add-on along with the
standard zips, so that a release needs no manual step. For that it checks out the
private repository `nl-utwente-groove/yfiles-lib` next to the code checkout. That
repository holds two files in its `lib/` directory: `yfiles-for-java-swing.jar`, the plain library
jar from the `lib` directory of the licensed distribution, and the runtime license file
(the `.xml` file that `yfiles.license.dir` points to); the rest of that repository is
the source of the backend itself, its root project, which the yFiles license
does not allow to be public. The workflow installs the jar into
the runner's local Maven repository under the coordinates of that repository's
`pom.xml` (whose `yfiles.version` it reads), builds the backend from that
checkout (the license directory defaults to its `lib/` directory; tests skipped: they
open Simulator windows), and packages the release with
the `yfiles` profile; the add-on zip is then attached to the github release by the
same step as the standard zips.

The checkout authenticates with the repository secret `YFILES_LIB_TOKEN`, a
fine-grained personal access token of the licensed developer with read access to
`yfiles-lib` only (Contents: read). Under the one-seat project license, nobody but
that developer and this token may read the private repository. A new library version
means a new jar and license file there, and a new `yfiles.version` in both
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


