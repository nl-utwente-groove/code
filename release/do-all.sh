# This is a bash script to run all maven commands for
# - installing local dependencies
# - building the main maven artifact
# - zipping up the release artifacts
# Run from the code repository main directory using launch/maven.sh

# Set the GROOVE_VERSION variable from the corresponding resource file
GROOVE_VERSION=$(cat src/main/resources/nl/utwente/groove/resource/version/GROOVE_VERSION)
# In Powershell, replace the above by
# $GROOVE_VERSION = Get-Content .\src\main\resources\nl\utwente\groove\resource\version\GROOVE_VERSION

# Install the local maven dependencies
cd lib
mvn install:install-file -DpomFile=pom/gnuprolog.pom -Dfile=gnuprologjava-0.2.6.jar -Dsources=src/gnuprologjava-0.2.6-src.zip
mvn install:install-file -DpomFile=pom/groove-gxl.pom -Dfile=groove-gxl-3.0.jar -Dsources=src/groove-gxl-3.0-sources.jar
mvn install:install-file -DpomFile=pom/ltl2buchi.pom -Dfile=ltl2buchi-2010.12.jar -Dsources=src/ltl2buchi-2010.12.zip
mvn install:install-file -DpomFile=pom/osxadapter.pom -Dfile=osxadapter-2.0.jar -Dsources=src/osxadapter-2.0-src.zip
cd ..

# Install the main maven artifact
mvn -Drevision=$GROOVE_VERSION clean install

# Generate javadoc
mvn javadoc:aggregate

# zip up the release artifacts
cd release; mvn -Drevision=$GROOVE_VERSION clean package
