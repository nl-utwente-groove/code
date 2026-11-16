# This is a bash script to run all maven commands for
# - building the main maven artifact
# - zipping up the release artifacts
# - optionally, with the argument "yfiles", also building the obfuscated
#   yFiles jars (see README.md; needs the licensed yFiles library in the local Maven
#   repository and a clone of the private yfiles-lib repository, which holds
#   the runtime license file, next to this repository)
# Run from the code repository main directory using launch/maven.sh

EDITION=${1:-}
if [[ -n $EDITION && $EDITION != yfiles ]]; then
    echo "usage: do-all.sh [yfiles]" >&2
    exit 1
fi

# The clone of the private repository nl-utwente-groove/yfiles-lib, which holds
# the library jar and the runtime license file; it is expected next to this
# repository. Override with YFILES_LIB=<dir>.
YFILES_LIB=${YFILES_LIB:-../yfiles-lib}

# Set the GROOVE_VERSION variable from the pom's revision property (the
# single source of truth for the version number); the release poms form
# a separate reactor that receives the version via -Drevision
GROOVE_VERSION=$(mvn -q help:evaluate -Dexpression=revision -DforceStdout)
# In Powershell, replace the above by
# $GROOVE_VERSION = mvn -q help:evaluate -D"expression=revision" -DforceStdout

# Install the main maven artifact
mvn clean install

# Generate javadoc
mvn javadoc:aggregate

# Build and install the yFiles backend against the artifact just installed
# (the license directory is passed as an absolute path, since Maven would
# resolve a relative one against the backend's own directory)
if [[ $EDITION == yfiles ]]; then
    mvn -f yfiles/pom.xml -Dgroove.install.skip=true -Drevision=$GROOVE_VERSION "-Dyfiles.license.dir=$(cd "$YFILES_LIB/lib" && pwd)" clean install
fi

# zip up the release artifacts
cd release; mvn -Drevision=$GROOVE_VERSION clean package

# obfuscate the yFiles jars (no clean: that would delete the standard zips
# of the step before)
if [[ $EDITION == yfiles ]]; then
    mvn -Drevision=$GROOVE_VERSION -Pyfiles package
fi
