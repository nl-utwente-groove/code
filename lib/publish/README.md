# Publishing the bundled libraries to Maven Central

This directory prepares the four libraries historically served from
`lib/repo` (and shaded into the GROOVE jar) for publication to Maven
Central under the `nl.utwente.groove` namespace. Once they are on
Central, the shade step in the main pom can be dropped, which fixes the
loss of `module-info.class` in the published GROOVE jar: the shade
plugin removes the module descriptor because the merged jar contains
packages from four foreign modules.

Each module unpacks the upstream binary jar from `lib/repo` unchanged,
regenerates the manifest, adds license texts, attaches the existing
`-sources` jar and a stub `-javadoc` jar (Central requires one to
exist), and is deployed with the same Central + GPG setup as the main
GROOVE artifact.

The published versions get a fourth version component
(house style, cf. `jgraph:5.13.0.0`), because the jar bytes differ from
the `lib/repo` originals — same coordinates with different bytes would
poison local repository caches:

| artifact | lib/repo version | published version | change |
|---|---|---|---|
| `gnuprologjava` | 0.2.6 | 0.2.6.1 | manifest: `Automatic-Module-Name: gnuprologjava`; adds LGPL/GPL texts |
| `ltl2buchi` | 2010.12 | 2010.12.1 | manifest: `Automatic-Module-Name: ltl2buchi`; adds NOSA text + NOTICE |
| `osxadapter` | 2.0 | 2.0.1 | manifest: `Automatic-Module-Name: osxadapter`; adds Apple license text |
| `groove-gxl` | 3.0 | 3.0.1 | manifest metadata only (has real `module-info`) |

The `Automatic-Module-Name` values equal the names that
`src/main/java/module-info.java` already requires (currently derived
from the jar file names), so no `module-info.java` change is needed and
the names survive future file renames. This also retires the risk noted
at the `-Xlint:-requires-transitive-automatic` flag in the main pom for
these three modules.

## Before deploying: license verification (one-time, manual)

Central publication is public and irrevocable. Verify before deploying:

1. **ltl2buchi (NOSA 1.3) — the gating item.** The source headers say
   the code is under the NASA Open Source Agreement 1.3 and refer to a
   file `NOSA-1.3-JPF` "at the top of the distribution directory tree"
   that is *not* present in our `-sources` jar. The bundled
   `META-INF/NOSA-1.3.txt` is the generic NOSA 1.3 template (from SPDX),
   which has blanks (government agency, copyright notice, registration
   URL) that the JPF instance filled in. Locate the actual
   `NOSA-1.3-JPF` file (early history of the Java PathFinder
   repositories, e.g. github.com/javapathfinder, or an original
   JPF/LTL2Buchi download) and replace `NOSA-1.3.txt` with it. Also
   check whether upstream later relicensed LTL2Buchi under Apache-2.0
   (jpf-core itself did); if a relicensed release matches our snapshot,
   prefer that license. NOSA obligations already handled here:
   clause 3.A.1 (license text accompanies each copy — bundled in the
   jar), 3.A.2 (source code freely available — the `-sources` jar),
   3.B (copyright notice — in `NOTICE.txt`), 3.C (modification record —
   in `NOTICE.txt`). Clause 3.F *requests* (not requires) registration
   with NASA — consider sending the point of contact a note.
2. **gnuprologjava (LGPL 3)**: no action expected; the jar now carries
   `COPYING.txt`/`COPYING.LESSER.txt` from the upstream distribution,
   and the embedded `gnu.getopt` keeps its own `COPYING.LIB`.
3. **osxadapter (Apple sample-code license)**: the header grants use,
   reproduction, modification and redistribution; the notice is retained
   in `META-INF/LICENSE.txt` and in the source header. No action
   expected.
4. **groove-gxl**: first-party GROOVE code, Apache-2.0. No action.

## Building

From this directory:

    mvn -q clean package > build.log 2>&1

Check each `<module>/target/` for the main jar (with the new manifest
and license files) and the `-javadoc` stub. The `-sources` jar is not
copied into `target/`: the existing file in `lib/repo` is attached
as-is and deployed under the new version.

## Deploying

Deploy each module *individually* (the `central-publishing-maven-plugin`
is designed to run from a build root; the aggregator pom here is
build-only and skips deployment):

    cd gnuprologjava && mvn -Pmaven clean deploy
    cd ltl2buchi && mvn -Pmaven clean deploy
    cd osxadapter && mvn -Pmaven clean deploy
    cd groove-gxl && mvn -Pmaven clean deploy

This uses the same prerequisites as a GROOVE release: the `central`
server credentials in `settings.xml` and the GPG key. Unlike the main
pom, `autoPublish` is **false**: each deployment lands in the Central
portal (https://central.sonatype.com/publishing) in state *validated*,
where it can be inspected and must be released manually. Mistakes
caught at that stage cost nothing; after release they are permanent.

## After publication (follow-up change to the main build)

In the main pom:

1. Bump the four `nl.utwente.groove` dependency versions to the
   published ones (table above).
2. Delete the `maven-shade-plugin` configuration; `groove-<version>.jar`
   then retains its `module-info.class`. (The `original-*` jar and
   `dependency-reduced-pom.xml` artifacts disappear too.)
3. Delete the `groove-project-local` `<repository>` declaration and the
   `lib/repo` directory.
4. Reconsider `-Xlint:-requires-transitive-automatic`: still needed for
   the other automatic modules (jgraph, batik, etc.), so probably keep.

Also update `claude/CLAUDE.md` (the *Dependencies* section describes the
`lib/repo` mechanism) and check that the release/distribution build does
not assume the GROOVE jar contains the Prolog/LTL classes (it never
contained the other twenty-odd dependencies, so any runnable
distribution already ships dependency jars).

This `lib/publish` directory stays in the repository afterwards as the
provenance record and rebuild recipe for the published artifacts.
