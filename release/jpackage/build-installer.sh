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
#   build-installer.sh <version> [<type> [<edition>]]
#
#   <version>  the GROOVE version, e.g. 7.5.4; must match the -Drevision value
#              of the release build
#   <type>     jpackage --type: msi, dmg, pkg, deb, rpm or app-image;
#              defaults to msi (Windows), dmg (macOS) or deb (Linux).
#              app-image produces the raw application directory without an
#              installer, which is useful for local testing since it needs no
#              packaging tools. Pass "" to get the default when giving an
#              edition.
#   <edition>  standard (the default) or yfiles: the yFiles edition is built
#              from the -yfiles-bin zip (see README.md) into a package named
#              GROOVE-yFiles that installs next to the standard one, with the
#              edition's license notice as its license text.
#
# The installer is placed in release/jpackage/target/dist, named
# groove-<version>[-yfiles]-<os>-<arch>.<ext>.

set -euo pipefail

VERSION=${1:?usage: build-installer.sh <version> [<type> [<edition>]]}
TYPE=${2:-}
EDITION=${3:-standard}

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

# ----------------------------------------------------------------- edition
# The two editions install side by side: distinct names, package identifiers
# and (fixed) upgrade UUIDs, the latter making a newer MSI replace an older
# install of the same edition only.
case $EDITION in
    standard)
        EDITION_SUFFIX=
        APP_NAME=GROOVE
        PACKAGE_ID=nl.utwente.groove
        UPGRADE_UUID=c8adea88-1eaa-4127-838b-7b4be5a147f3
        DESCRIPTION="GROOVE graph transformation and verification tool"
        ;;
    yfiles)
        EDITION_SUFFIX=-yfiles
        APP_NAME=GROOVE-yFiles
        PACKAGE_ID=nl.utwente.groove.yfiles
        UPGRADE_UUID=6e52c35a-1ac2-4126-b428-b35a8272213d
        DESCRIPTION="GROOVE graph transformation and verification tool, yFiles edition (non-commercial use only)"
        ;;
    *)
        echo "error: unknown edition '$EDITION' (standard or yfiles)" >&2
        exit 1
        ;;
esac

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
ZIP=$RELEASE_DIR/target/groove-$VERSION_UNDERSCORED$EDITION_SUFFIX-bin.zip
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
# charsets, zip filesystems, accessibility support) or through service
# loading: jdk.crypto.ec holds the elliptic-curve provider on Java 21 (merged
# into java.base from 22, where the module remains as an empty stub), which
# jdeps cannot see; without it TLS handshakes with github fail, and so does
# the download of the yFiles add-on (gh #909).
EXTRA_MODULES="java.instrument java.management java.naming java.scripting java.sql jdk.accessibility jdk.charsets jdk.crypto.ec jdk.unsupported jdk.zipfs"
MAIN_JAR=$INPUT/lib/groove-$VERSION.jar
# The analysis runs on the core jar's classes WITHOUT its module descriptor.
# The core jar is a named module whose "requires" clauses name the automatic
# modules of the class-path jars; a jdeps that resolves the module graph before
# analysing -- JDK 26 does, JDK 21 does not -- then stops at the first of them
# with "Module jgraph not found, required by nl.utwente.groove". That is not
# covered by --ignore-missing-deps, which ignores dependences missing from the
# analysis, not modules missing from the resolution, and putting lib/ on the
# module path only moves the failure on, since the automatic module names the
# jars derive are not consistent among themselves (commons-beanutils requires
# org.apache.commons.logging, the jar yields commons.logging). Without the
# descriptor there is no resolution to fail, and both JDKs give the same answer.
CLASSES=$WORK/classes
unzip -q "$MAIN_JAR" -x module-info.class -d "$CLASSES"
if JDEPS_OUT=$("$JDEPS" --multi-release 21 --ignore-missing-deps --print-module-deps \
        --class-path "$(native_path "$INPUT/lib")/*" "$(native_path "$CLASSES")" 2> /dev/null); then
    # jdeps may precede the module list with warnings; the list is the last
    # line that looks like comma-separated module names
    MODULES=$(grep -E '^[a-z][a-zA-Z0-9._]*(,[a-zA-Z0-9._]+)*$' <<< "$JDEPS_OUT" | tail -1)
fi
if [[ -z ${MODULES:-} ]]; then
    echo "warning: jdeps failed to compute the module list; falling back to java.se" >&2
    MODULES=java.se
fi
# The yFiles edition's jars have dependencies of their own (jdk.xml.dom, at
# least) that the core jar's analysis cannot see
if [[ $EDITION == yfiles ]]; then
    edition_jars=()
    for f in "$INPUT"/lib/groove-yfiles-*.jar "$INPUT"/lib/yfiles-for-java-swing-*.jar; do
        edition_jars+=("$(native_path "$f")")
    done
    if EDITION_OUT=$("$JDEPS" --multi-release 21 --ignore-missing-deps --print-module-deps             --class-path "$(native_path "$INPUT/lib")/*" "${edition_jars[@]}" 2> /dev/null); then
        EDITION_MODULES=$(grep -E '^[a-z][a-zA-Z0-9._]*(,[a-zA-Z0-9._]+)*$' <<< "$EDITION_OUT" | tail -1)
    fi
    if [[ -z ${EDITION_MODULES:-} ]]; then
        echo "warning: jdeps failed on the edition jars; adding jdk.xml.dom only" >&2
        EDITION_MODULES=jdk.xml.dom
    fi
    MODULES=$MODULES,$EDITION_MODULES
fi
MODULES=$(printf '%s\n' ${MODULES//,/ } $EXTRA_MODULES | sort -u | paste -sd, -)
echo "bundled runtime modules: $MODULES"

# ----------------------------------------------------------------- launchers
# jpackage names the main launcher after the application (GROOVE), and it
# starts the Simulator; the tools, the Simulator included, become additional
# launchers named after themselves. The menu entries (Windows start menu,
# Linux desktop files) hang off the tool launchers rather than the main one,
# so the GROOVE menu group lists Simulator, Generator, ... and not a second
# GROOVE. win-console gives the command-line tools a console on Windows
# (ignored elsewhere).
LAUNCHERS_DIR=$WORK/launchers
mkdir -p "$LAUNCHERS_DIR"
add_launcher_args=()
make_launcher() { # <name> <console>
    {
        echo "main-jar=bin/$1.jar"
        echo "win-console=$2"
        echo "win-menu=true"
        echo "linux-shortcut=true"
    } > "$LAUNCHERS_DIR/$1.properties"
    add_launcher_args+=(--add-launcher "$1=$(native_path "$LAUNCHERS_DIR/$1.properties")")
}
make_launcher Simulator false
make_launcher Generator true
make_launcher ModelChecker true
make_launcher Imager true
make_launcher Viewer false

# ----------------------------------------------------------------- jpackage
# MSI and DMG version numbers must be plain x.y.z: strip any -SNAPSHOT suffix
APP_VERSION=${VERSION%%-*}

args=(
    --type "$TYPE"
    --name "$APP_NAME"
    --app-version "$APP_VERSION"
    --input "$(native_path "$INPUT")"
    --main-jar bin/Simulator.jar
    --add-modules "$MODULES"
    --dest "$(native_path "$DIST")"
    --vendor "University of Twente"
    --description "$DESCRIPTION"
    "${add_launcher_args[@]}"
)
case $OS in
    windows)
        args+=(--icon "$(native_path "$INPUT/groove-G.ico")")
        ;;
    macos)
        args+=(--icon "$(native_path "$SCRIPT_DIR/icons/groove-G.icns")"
            --mac-package-identifier "$PACKAGE_ID"
            --mac-package-name "$APP_NAME")
        ;;
    linux)
        args+=(--icon "$(native_path "$SCRIPT_DIR/icons/groove-G.png")")
        ;;
esac
if [[ $TYPE != app-image ]]; then
    # the yFiles edition shows its license notice, which restricts its use
    if [[ $EDITION == yfiles ]]; then
        LICENSE_FILE=$INPUT/YFILES-EDITION.md
    else
        LICENSE_FILE=$ROOT_DIR/LICENSE.md
    fi
    args+=(--license-file "$(native_path "$LICENSE_FILE")"
        --about-url "https://nl-utwente-groove.github.io")
    case $OS in
        windows)
            # the fixed upgrade UUID makes a newer MSI replace an older install
            args+=(--win-menu-group GROOVE
                --win-per-user-install --win-dir-chooser
                --win-upgrade-uuid "$UPGRADE_UUID")
            ;;
        linux)
            args+=(--linux-menu-group Development)
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
        target=$DIST/groove-$VERSION_UNDERSCORED$EDITION_SUFFIX-$OS-$ARCH.${f##*.}
        mv "$f" "$target"
        echo "installer built: $target"
    done
fi
