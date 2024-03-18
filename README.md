# <img src="https://github.com/nl-utwente-groove/code/blob/master/G.gif" width="25"> GROOVE

This repository contains the GROOVE code base.

GROOVE is a tool for graph transformation and verification, developed and maintained at the University of Twente.
For more information go to <https://nl-utwente-groove.github.io>

## Compiling

To compile and run this project, you have to obtain the Maven dependencies first. These consist of

- Globally available libraries
- Local libraries

For the first category, you need to do nothing, as Maven will be able to get them from the central repository.
For the second category, however, you first have to install the libraries into your local Maven repository (on your machine).
Eclipse launch configurations have been provided for this, in the launch directory. Eclipse should find these automatically,
and allow you to run them from the "Run" menu.

In Eclipse, to do all local installs in one fell swoop, use

```
  GROOVE core - install local mvn-deps
```

Alternatively, you can run the required Maven commands from the command line. Instructions are included
in the `.pom`-files under `lib/pom`.

The local installs need only be done once (on any machine where you want to build GROOVE).

A somewhat more extensive explanation is given below.

## GROOVE Maven build

### Globally available libraries

Many libraries can be found automatically by Maven. For those the pom.xml need only specify

```
<dependency>
	<groupId>x</groupId>
	<artifactId>y</artifactId>
	<version>n</version>
</dependency>
```

which will ensure that package y-n.jar will be obtained from group x (which needs to be recognised by Maven)

### Local libraries

If a library cannot be found automatically by Maven, but for other purposes you want Maven to treat it in the same way as the globally available libs, you first need to add it to your local Maven repository. That is a step that needs to be taken once in your build environment, for every such library.

Adding a library to your local Maven repository is called *installing* the library, in Maven terminology. This can be done by the Maven instruction

```
mvn install:install-file
  -Dfile=path-to-jar/y.jar
  [-Dsources=path-to-sources/y-src.zip]
  -DgroupId=x 
  -DarctifactId=y 
  -Dversion=n
```

where `path-to-jar/y.jar` is the path to the actual jar file, from the working directory (and the optional `path-to-sources/y-src.zip` the path to a zip file containing sources, if they are available).

For the GROOVE local libraries, help has been provided by

* Collecting the groupId, artifactId and version (and dependencies) in a single y.pom file, stored in the subdirectory lib/pom of GROOVE
* Creating a m2 launch configuration that invokes the above install-file goal
* Adding that m2 launch configuration to the launch group "GROOVE core - install local mvn-deps"

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
