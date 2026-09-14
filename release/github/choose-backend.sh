#!/usr/bin/env bash
# Prints the branch of the private repository nl-utwente-groove/yfiles-lib
# from which the yFiles backend is built for a given commit of this repository.
#
# The candidates are the remote branches of this repository whose tip is an
# ancestor of the commit - the branch pushed or tagged and everything it was
# branched off - ordered by distance, and the choice is the nearest of those
# that the private repository also has, main if it has none. A branch of the
# private repository that follows an interface change on a branch of this
# repository must therefore carry that branch's name. A throwaway branch off
# such a branch, as for a test release, resolves to the branch it was taken
# from; a commit on master resolves to main, the private repository having no
# master. See the section "Deploying" of release/README.md, and the workflows
# backend.yml and release.yml that call this script.
#
# Usage: choose-backend.sh <commit>, from a checkout of this repository that
# has the full history of its remote branches, with a token that has read
# access to the private repository in TOKEN.

set -euo pipefail

COMMIT=$1
LIB=https://x-access-token:$TOKEN@github.com/nl-utwente-groove/yfiles-lib.git
REF=main
CANDIDATES=$(git for-each-ref --format='%(objectname) %(refname:lstrip=3)' refs/remotes/origin \
  | while read -r SHA BRANCH; do
      if [ "$BRANCH" != HEAD ] && git merge-base --is-ancestor "$SHA" "$COMMIT"; then
        echo "$(git rev-list --count "$SHA..$COMMIT") $BRANCH"
      fi
    done | sort -k1,1n -k2,2 | awk '{ print $2 }')
for BRANCH in $CANDIDATES; do
  # asked from outside the checkout: actions/checkout leaves the workflow
  # token in the checkout's git config as an authorization header for
  # github.com, which would take precedence over the private-repository token
  # in the URL and turn the private repository into "Repository not found"
  if git -C "${RUNNER_TEMP:-/tmp}" ls-remote --exit-code --heads "$LIB" "refs/heads/$BRANCH" > /dev/null; then
    REF=$BRANCH
    break
  fi
done
echo "$REF"
