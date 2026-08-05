#!/usr/bin/env bash
# Builds a self-contained GROOVE installer (with bundled Java runtime) using
# the jpackage tool of the running JDK.
#
# Prerequisites:
# - the release zip has been built:
#     mvn -B clean install -Drevision=<version>       (repository root)
#     cd release && mvn -B clean package -Drevision=<version>
# - a JDK (>= 21) providing jpackage and jdeps, located through JAVA_HOME if
#   set, otherwise through the PATH
# - for the Windows .msi type: the WiX toolset (preinstalled on the GitHub
#   windows runners)
#
# Usage:
#   build-installer.sh <version> [<type>]
#
#   <version>  the GROOVE version, e.g. 7.5.4; must match the -Drevision value
#              of the release build
#   <type>     jpackage --type: msi, dmg, pkg, deb, rpm or app-image;
#              defaults to msi (Windows), dmg (macOS) or deb (Linux).
#              app-image produces the raw application directory without an
#              installer, which is useful for local testing since it needs no
#              packaging tools.
#
# The installer is placed in release/jpackage/target/dist, named
# groove-<version>-<os>-<arch>.<ext>.

set -euo pipefail

VERSION=${1:?usage: build-installer.sh <version> [<type>]}
TYPE=${2:-}

SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
RELEASE_DIR=$(dirname "$SCRIPT_DIR")
ROOT_DIR=$(dirname "$RELEASE_DIR")

# ----------------------------------------------------------------- platform
case "$(uname -s)" in
    MINGW* | MSYS* | CYGWIN*) OS=windows ;;
    Darwin) OS=macos ;;
    *) OS=linux ;;
esac
case "$(uname -m)" in
    x86_64 | amd64) ARCH=x64 ;;
    arm64 | aarch64) ARCH=aarch64 ;;
    *) ARCH=$(uname -m) ;;
esac
if [[ -z $TYPE ]]; then
    case $OS in
        windows) TYPE=msi ;;
        macos) TYPE=dmg ;;
        linux) TYPE=deb ;;
    esac
fi

# jpackage on Windows is a native tool: give it Windows-style paths
native_path() {
    if [[ $OS == windows ]] && command -v cygpath > /dev/null; then
        cygpath -w "$1"
    else
        printf '%s' "$1"
    fi
}

JAVA_BIN=${JAVA_HOME:+$JAVA_HOME/bin/}
JPACKAGE=${JAVA_BIN}jpackage
JDEPS=${JAVA_BIN}jdeps

# ----------------------------------------------------------------- input tree
# The bin release zip already has the right shape for jpackage: thin launcher
# jars in bin/ whose manifests put ../lib/* on the classpath, next to the
# top-level documentation files. The whole tree becomes the app content.
VERSION_UNDERSCORED=${VERSION//./_}
ZIP=$RELEASE_DIR/target/groove-$VERSION_UNDERSCORED-bin.zip
if [[ ! -f $ZIP ]]; then
    echo "error: $ZIP not found; build the release first (see header of this script)" >&2
    exit 1
fi

WORK=$SCRIPT_DIR/target
DIST=$WORK/dist
rm -rf "$WORK"
mkdir -p "$WORK" "$DIST"
unzip -q "$ZIP" -d "$WORK/input"
INPUT=$WORK/input/groove-$VERSION_UNDERSCORED

# ----------------------------------------------------------------- modules
# Compute the set of JDK modules for the bundled runtime from the static
# dependencies of the code, then add modules that are only reached
# reflectively (scripting/Groovy, JNDI, JDBC, instrumentation, extra
# charsets, zip filesystems, accessibility support).
EXTRA_MODULES="java.instrument java.management java.naming java.scripting java.sql jdk.accessibility jdk.charsets jdk.unsupported jdk.zipfs"
MAIN_JAR=$INPUT/lib/groove-$VERSION.jar
if JDEPS_OUT=$("$JDEPS" --multi-release 21 --ignore-missing-deps --print-module-deps \
        --class-path "$(native_path "$INPUT/lib")/*" "$(native_path "$MAIN_JAR")" 2> /dev/null); then
    # jdeps may precede the module list with warnings; the list is the last
    # line that looks like comma-separated module names
    MODULES=$(grep -E '^[a-z][a-zA-Z0-9._]*(,[a-zA-Z0-9._]+)*$' <<< "$JDEPS_OUT" | tail -1)
fi
if [[ -z ${MODULES:-} ]]; then
    echo "warning: jdeps failed to compute the module list; falling back to java.se" >&2
    MODULES=java.se
fi
MODULES=$(printf '%s\n' ${MODULES//,/ } $EXTRA_MODULES | sort -u | paste -sd, -)
echo "bundled runtime modules: $MODULES"

# ----------------------------------------------------------------- launchers
# The main launcher (named GROOVE) starts the Simulator; the other tools
# become additional launchers. win-console gives the command-line tools a
# console on Windows (ignored elsewhere).
LAUNCHERS_DIR=$WORK/launchers
mkdir -p "$LAUNCHERS_DIR"
add_launcher_args=()
make_launcher() { # <name> <console>
    {
        echo "main-jar=bin/$1.jar"
        echo "win-console=$2"
    } > "$LAUNCHERS_DIR/$1.properties"
    add_launcher_args+=(--add-launcher "$1=$(native_path "$LAUNCHERS_DIR/$1.properties")")
}
make_launcher Generator true
make_launcher ModelChecker true
make_launcher Imager true
make_launcher Viewer false

# ----------------------------------------------------------------- jpackage
# MSI and DMG version numbers must be plain x.y.z: strip any -SNAPSHOT suffix
APP_VERSION=${VERSION%%-*}

args=(
    --type "$TYPE"
    --name GROOVE
    --app-version "$APP_VERSION"
    --input "$(native_path "$INPUT")"
    --main-jar bin/Simulator.jar
    --add-modules "$MODULES"
    --dest "$(native_path "$DIST")"
    --vendor "University of Twente"
    --description "GROOVE graph transformation and verification tool"
    "${add_launcher_args[@]}"
)
case $OS in
    windows)
        args+=(--icon "$(native_path "$INPUT/groove-G.ico")")
        ;;
    macos)
        args+=(--icon "$(native_path "$SCRIPT_DIR/icons/groove-G.icns")"
            --mac-package-identifier nl.utwente.groove
            --mac-package-name GROOVE)
        ;;
    linux)
        args+=(--icon "$(native_path "$SCRIPT_DIR/icons/groove-G.png")")
        ;;
esac
if [[ $TYPE != app-image ]]; then
    args+=(--license-file "$(native_path "$ROOT_DIR/LICENSE.md")"
        --about-url "https://nl-utwente-groove.github.io")
    case $OS in
        windows)
            # the fixed upgrade UUID makes a newer MSI replace an older install
            args+=(--win-menu --win-menu-group GROOVE
                --win-per-user-install --win-dir-chooser
                --win-upgrade-uuid c8adea88-1eaa-4127-838b-7b4be5a147f3)
            ;;
        linux)
            args+=(--linux-menu-group Development --linux-shortcut)
            ;;
    esac
fi

echo "running: jpackage ${args[*]}"
"$JPACKAGE" "${args[@]}"

# ----------------------------------------------------------------- output
if [[ $TYPE == app-image ]]; then
    echo "application image built in $DIST"
else
    for f in "$DIST"/*; do
        target=$DIST/groove-$VERSION_UNDERSCORED-$OS-$ARCH.${f##*.}
        mv "$f" "$target"
        echo "installer built: $target"
    done
fi
