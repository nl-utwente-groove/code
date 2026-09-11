#!/usr/bin/env bash
# Writes the body of the github release page to standard output:
# INSTALL-NOTE.md, followed by this release's section of the change log in a
# collapsed block, so that the note stays close to the asset list below it.
# See the section "The release page" of release/README.md.
#
# Usage: release-notes.sh > <file>

set -euo pipefail

DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
CHANGES=$(dirname "$DIR")/include/CHANGES.md

cat "$DIR/INSTALL-NOTE.md"
echo
echo "<details><summary>Changes in this release</summary>"
echo
# This release's section is the first one of CHANGES.md, whose section titles
# are underlined with dashes. By the time the second underline is seen, the
# title line above it has already been collected, hence the k--.
awk '
    { sub(/\r$/, "") }
    /^---+[ \t]*$/ { if (++n == 2) { k--; exit }; next }
    n == 1 { line[++k] = $0 }
    END { for (i = 1; i <= k; i++) print line[i] }
' "$CHANGES"
echo
echo "</details>"
