# Publishing the bundled libraries to Maven Central

This directory builds the four third-party libraries that GROOVE
republishes to Maven Central under the `nl.utwente.groove` namespace
(published 2026-08). They were historically served from a project-local
Maven repository (`lib/repo`, deleted since) and shaded into the GROOVE
jar; the shade step removed `module-info.class` from the published
GROOVE jar because the merged jar contained packages from four foreign
modules. With the libraries on Central, the main pom depends on them
like on any other library, and the module descriptor survives.

Each module unpacks the upstream binary jar from `upstream/` unchanged,
regenerates the manifest, adds license texts, attaches the existing
`-sources` jar and a stub `-javadoc` jar (Central requires one to
exist), and is deployed with the same Central + GPG setup as the main
GROOVE artifact. The `upstream/` directory holds the original jar,
sources jar and pom of each library as the permanent provenance record
and rebuild input.

The published versions get a fourth version component
(house style, cf. `jgraph:5.13.0.0`), because the jar bytes differ from
the upstream originals — same coordinates with different bytes would
poison local repository caches:

| artifact | upstream version | published version | change |
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

1. **ltl2buchi (NOSA 1.3).** The source headers say the code is under
   the NASA Open Source Agreement 1.3 and refer to a file `NOSA-1.3-JPF`
   "at the top of the distribution directory tree" that is not present
   in our `-sources` jar. That file — the NOSA instance for Java
   Pathfinder, NASA designation ARC-15388-1 — was already archived in
   this repository at `src/meta/NOSA-1.3-JPF.txt` and is bundled in the
   jar as `META-INF/NOSA-1.3-JPF.txt`. Note: JPF distributions circulate
   two revisions of this file, differing only in the point of contact
   (John Penix vs. Thomas Pressburger) and the copyright years (2005 vs.
   2005,2006); our archived copy is the earlier one, and is what we
   bundle. NOSA obligations handled here: clause 3.A.1 (license text
   accompanies each copy — bundled in the jar), 3.A.2 (source code
   freely available — the `-sources` jar), 3.B (copyright notice — in
   `NOTICE.txt`), 3.C (modification record — in `NOTICE.txt`).
   Clause 3.F *requests* (not requires) registration at
   opensource.arc.nasa.gov — consider a courtesy note if that contact
   point still exists. A possible Apache-2.0 relicensing was
   investigated (2026-08) and ruled out: jpf-core's relicensing to
   Apache-2.0 does not cover LTL2Buchi (no gov.nasa.ltl code anywhere
   in the javapathfinder or nasa GitHub organizations), the known
   mirrors of LTL2Buchi all carry NOSA headers, and no relicensing
   statement exists online. NOSA 1.3 stands.
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
copied into `target/`: the existing file in `upstream/` is attached
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

## After publication (done 2026-08)

Once the four libraries were released on Central, the main build was
flipped accordingly:

1. The four `nl.utwente.groove` dependency versions were bumped to the
   published ones (table above).
2. The `maven-shade-plugin` configuration was deleted;
   `groove-<version>.jar` retains its `module-info.class` again. (The
   `original-*` jar and `dependency-reduced-pom.xml` artifacts
   disappeared too.)
3. The `groove-project-local` `<repository>` declaration was deleted,
   and `lib/repo` with it; the upstream artifacts moved to `upstream/`
   in this directory.
4. `-Xlint:-requires-transitive-automatic` stays: it is still needed
   for the other automatic modules (jgraph, batik, etc.).

This `lib/publish` directory stays in the repository as the provenance
record and rebuild recipe for the published artifacts. Upgrading one of
these libraries means: put the new upstream jar (+ sources jar) in
`upstream/`, adjust `upstream.version` and the published version in the
module pom, redo the license check above, deploy, and bump the
dependency version in the main pom.
