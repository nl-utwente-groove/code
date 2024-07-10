# How to build a github release

This chapter explains how to build a github release for Groove. A second chapter, below, summarises the actions to create a Maven artifact.

The github release, as meant here, consists of two dedicated zip-files (as well as the complete source code, in `.zip` and `.tar.gz` format):

- `groove-x_y_z-bin.zip`
- `groove-x_y_z-bin+doc.zip`

Both of these contain a top-level README.md that explains their structure and how to install the tool.

## Preparation

Below, the _release directory_ refers to the project subdirectory (of the `code` repository) called `release`.

1. Update the version and date in the GROOVE source, by changing the files in
   `src/main/resources/nl/utwente/groove/resource/version`:

    - `GROOVE_BUILD`: the build date, in format `YYYYMMDD`. Update to the build date.
    - `GROOVE_VERSION`: a semantic version `x.y.z` with the optional suffix `-SNAPSHOT`. The number might already be correct (it is updated in postprocessing, see below) but the changes in this revision may necessitate the `x` or `y` values. In any case remove the `-SNAPSHOT` suffix.
    - `GXL_VERSION`: the name of the version of GXL currently used for the encoding of graphs. (This will rarely change.)
    - `JAVA_VERSION`: the version of Java to be used for the build.  

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
   `GROOVE core - all JUnit tests` launch configuration.
   If you get errors complaining that the test packages are not in the module,
   in Eclipse do `Project -> Clean -> groove` and try again. Only proceed if all tests pass.

## Building [optional]

(You can skip this step if you want, since it will actually also be done by github upon tagging.)

2. Compile GROOVE by running Maven on the GROOVE source project
   (ensuring that all dependencies are Maven-based), using
   
    `mvn clean install -Drevision=x.y.z`

    where `x.y.z` is the same version number as in `GROOVE_VERSION` (see above),
    or by running the Eclipse `GROOVE core - do all` launch configuration.

3. Package GROOVE by running Maven in the release directory, using

    `mvn clean package -Drevision=x.y.z`

    where `x.y.z` is the same version number as in `GROOVE_VERSION` (see above),
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

## Postprocessing

1. In `src/main/resources/nl/utwente/groove/resource/version`, update `GROOVE_VERSION`
   (containing the release version `x.y.z`) by increasing `z` and adding the prefix `-SNAPSHOT`.

# How to build a Maven artefact

The process is quite complicated; although largely automated, many things can go wrong.

## Nexus staging

Invoke the Maven target using

`mvn clean deploy -Drevision=x.y.z`

where `x.y.z` is the version number also used for the github release; see above.

This can alternatively be invoked in Ecliplse using the `GROOVE - maven deploy to Central Repository` launch configuration.

The relevant plugins are `nexus-staging-maven-plugin` for staging, which relies on `maven-gpg-plugin` for signing. Staging consists of signing, uploading and checking the Maven Central constraints. The subsections below should not be necessary once everything is up and running, but are included for completeness and understanding, and may be useful in case part of the configuration is lost.


### Signing

The signing phase involves many different components, all of which have to work correctly. The following may help to understand them:

- GPG key: this has to be created, published to one or more keyservers and then used.
    * To have the required functionality at hand, use `Gpg4win`, which installs itself under `C:\Program Files (x86)` in the eponymous directory, as well as a second directory `gnupg`. The package also comes with a stand-alone GUI-based app `Kleopatra`, which helps in unserstanding which keys are currently known and can also create a key if required.
    * Since Maven runs in batch mode, the best (probably only) is to use a passphrase-less key. Any other setting results in the error message `no pinentry` during the Maven signing phase.
    * The intended key should be set as default in the gpg configuration file, which is located in `%APPDATA%\gnupg\gpg.conf`. This file should (at least) contain a line `default-key <fingerprint>`, where the fingerprint, which is the identifier of the key, can be found using (e.g.) Kleopatra.
    * To upload a key to a keyserver, use `gpg --keyserver <url> --send-keys <fingerprint>`. The Maven staging plugin looks for the key on different servers, among which are `https://keys.openpgp.org` and `http://keyserver.ubuntu.com:11371`, so it's a good idea to publish the key there straight away.
    * To avoid having to go through this again, use a key without expiration date.
    * An alternative way of dealing with default keys and passphrases, by setting them as explicit arguments in the Maven configuration, is described [here](https://central.sonatype.org/publish/publish-maven/#gpg-signed-components)

- Pinentry: This is a utility, installed in the `gnupg` directory mentioned above, that interactively requests the passphrase. It should be circumvented by using a key without passphrase. The message `no pinentry` does not necessarily mean that the utility cannot be found, it rather seems to mean that Maven cannot invoke it in batch mode; the fact that it attempts to do this in the first place probably means that the default key it's trying to use for signing has a passphrase.

- GPG agent: this is a daemon that needs to be running, and therefore needs to be installed and findable. On windows, `gpg-agent` is contained in the `gnupg` directory mentioned above. It is advertised as starting up automatically. There is a configuration file `%APPDATA%\gnupg\gpg-agent.conf` (settings are described [here](https://www.gnupg.org/%28it%29/documentation/manuals/gnupg/Agent-Options.html)), but it should not be necessary to use this. If you get a message `gpg: can't connect to the gpg-agent: IPC connect call failed`, the following may solve this (invoke from command line): `gpg-connect-agent reloadagent /bye`

### Authenticating

The uploading phase requires Maven to be able to log into `https://s01.oss.sonatype.org`, but using a User Token rather than the Used ID. This User Token is be generated by the server itself (see <https://central.sonatype.org/publish/generate-token/>) and must then be saved in the Maven `settings.xml` file, which is in the `.m2` directory in `C:\Users\<username>` --- with the understanding that the Server ID, which on that page is given as `${server}`, should be set to the name also used in the stagin plugin --- by default `ossrh`.

## Deployment

The staging is followed by a manual deployment/release step described in <https://central.sonatype.org/publish/release>. (This can be changed into an automatic step by setting the `autoReleaseAfterClose` property in the Nexus staging plugin to `true`.)

