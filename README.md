# <img src="https://github.com/nl-utwente-groove/code/blob/master/G.gif" width="25"> GROOVE

This repository contains the GROOVE code base.

GROOVE is a tool for graph transformation and verification, developed and maintained at the University of Twente.
For more information go to <https://nl-utwente-groove.github.io>

## Compiling

All Maven dependencies are obtained automatically; a fresh clone builds without any further setup:

```
mvn clean package
```

Most libraries come from Maven Central. The few that are not available there
(`gnuprologjava`, `ltl2buchi`, `osxadapter`, `groove-gxl`) are served from a *project-local
Maven repository* checked into this code base at `lib/repo`, which is declared as a
`<repository>` in the pom. No manual installation is needed.

### Adding or updating a library in the project-local repository

The repository at `lib/repo` follows the standard Maven layout:

```
lib/repo/<groupId as directories>/<artifactId>/<version>/
    <artifactId>-<version>.jar
    <artifactId>-<version>.pom
    <artifactId>-<version>-sources.jar   (optional)
```

To add or upgrade a library, place the jar, a minimal pom (see the existing ones for
examples) and optionally a sources jar in the corresponding directory, and commit them.

## Releasing

(Only for the GROOVE maintainers)

To prepare for a release (on SourceForge), three things need to be done:

1. Running all tests
2. Building and installing the Maven artefact
3. Generating the Javadoc

For this purpose, in Eclipse invoke the Launch configurations

- Step 1:     `GROOVE core - all JUnit tests`
- Steps 2&3:  `GROOVE core - prepare for build`

The next steps are to be taken from the groove-release projects, and are described there
(in the HOWTO)

## Deploying

(Only for the GROOVE maintainers)

To deploy GROOVE to Maven Central, invoke the following *from the command line*,
in a version of the GROOVE project where the pom has a release version (and not a SNAPSHOT version):

```
  mvn clean javadoc:aggregate-jar deploy
```

(I have not found a way to make the `javadoc:aggregate-jar` part of the deploy project, though I'm
pretty sure it can be done.)

## Support

The development of GROOVE was assisted by the use of the [Yourkit Java Profiler](http://www.yourkit.com)

![yklogo](https://github.com/user-attachments/assets/8692e3b4-e0d9-42b5-bb47-a4fdd4bae01f)

YourKit supports open source projects with innovative and intelligent tools 
for monitoring and profiling Java and .NET applications.


The **visualizations inside GROOVE** are built with **yFiles for Java (Swing)**

<img src="https://github.com/user-attachments/assets/2f7d7f21-e4fe-434d-8e86-7b110cc7805f" style="width: 4cm;" alt="yFiles logo" type="image/png">

[yFiles](https://www.yfiles.com/?utm_source=groove&utm_medium=web&utm_campaign=promo) is your superior SDK for interactive graph visualization with automatic layout algorithms, available for web, .NET, and Java platforms.
Developed by [yWorks – the diagramming experts](https://www.yworks.com/?utm_source=groove&utm_medium=web&utm_campaign=promo)
