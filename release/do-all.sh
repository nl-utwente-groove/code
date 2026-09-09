# This is a bash script to run all maven commands for
# - building the main maven artifact
# - zipping up the release artifacts
# - optionally, with the argument "yfiles", also building the yFiles add-on
#   (see README.md; needs the licensed yFiles library in the local Maven
#   repository and the license directory configured)
# Run from the code repository main directory using launch/maven.sh

EDITION=${1:-}
if [[ -n $EDITION && $EDITION != yfiles ]]; then
    echo "usage: do-all.sh [yfiles]" >&2
    exit 1
fi

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
if [[ $EDITION == yfiles ]]; then
    mvn -f yfiles/pom.xml -Dgroove.install.skip=true -Drevision=$GROOVE_VERSION clean install
fi

# zip up the release artifacts
cd release; mvn -Drevision=$GROOVE_VERSION clean package

# zip up the yFiles add-on next to them (no clean: that would delete the
# standard zips, which land in the same target directory)
if [[ $EDITION == yfiles ]]; then
    mvn -Drevision=$GROOVE_VERSION -Pyfiles package
fi
