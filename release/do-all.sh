# This is a bash script to run all maven commands for
# - building the main maven artifact
# - zipping up the release artifacts
# Run from the code repository main directory using launch/maven.sh

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

# zip up the release artifacts
cd release; mvn -Drevision=$GROOVE_VERSION clean package
