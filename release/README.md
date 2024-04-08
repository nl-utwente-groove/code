# How to build a release

This file explains how to build a release for Groove. The release, as meant here, consists of two zip-files:

- `groove-x_y_z-bin.zip`
- `groove-x_y_z-bon+doc.zip`

Both of these contain a top-level README.md that explains their structure.

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

## Postprocessing

1. In `src/main/resources/nl/utwente/groove/resource/version`, update `GROOVE_VERSION`
   (containing the release version `x.y.z`) by increasing `z` and adding the prefix `-SNAPSHOT`.
