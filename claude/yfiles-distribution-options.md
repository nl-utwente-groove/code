# Distributing GROOVE now that there are two graph backends

Companion to `yfiles-migration.md` (gh #909), whose "Phase 5" section records what was
built. This note is about a question that phase 5 answered only provisionally: **what a
GROOVE release consists of, now that a second, license-restricted edition exists**. It
states the facts, the options and their consequences, and what still has to be decided.
Written 2026-09-09, after the edition was built and verified; no decision has been taken.

## Where things stand

The release as built produces two distributions:

| | standard | yFiles edition |
|---|---|---|
| built by | CI, on a `release-x_y_z` tag | Arend, locally |
| zips | `groove-x_y_z-bin[+doc].zip` | `groove-x_y_z-yfiles-bin[+doc].zip` |
| installers | 4, from the CI matrix: Windows x64 `.msi`, macOS aarch64 and x64 `.dmg`, Linux x64 `.deb` | `build-installer.sh x.y.z "" yfiles`, one per platform it is run on |
| licence | Apache 2.0 | non-commercial only (`YFILES-EDITION.md`) |
| installs as | `GROOVE` | `GROOVE-yFiles`, side by side |

Facts worth having before weighing the options (all measured on 2026-09-09, Windows,
JDK 26 unless noted):

- The edition's zip differs from the standard zip in **exactly three files**:
  `lib/yfiles-for-java-swing-3.6.0.1.jar` (8.7 MB, obfuscated), `lib/groove-yfiles-x.y.z.jar`
  (128 KB) and the notice `YFILES-EDITION.md`. Everything else is byte-identical.
- An app image is ~137 MB, of which ~91 MB is the bundled Java runtime. So shipping a
  second edition as a full distribution multiplies ~137 MB per platform to carry ~8.8 MB
  of difference.
- **The standard bundled runtime already suffices for yFiles.** `jdeps` over the two
  edition jars yields no module beyond the standard set (`jdk.xml.dom`, which the library
  needs, is already required by the core module). This was not obvious and it matters for
  option B below.
- `jpackage` builds only for the platform it runs on. There is no cross-building.
- Backend selection needs no configuration: `GraphBackend` discovers providers through
  `ServiceLoader`, ranks yFiles first when present, and the Simulator's Options menu offers
  the choice only when both are present. The mechanism was designed for optional presence.

## What the licence forces

From `yfiles-migration.md` (the SLA itself is the authority):

1. The library may be redistributed only obfuscated (§2.1c). Done, by the `yfiles` profile.
2. Only the licensed developer may build with it, and automated builds need a Project
   Licence (§2.2.2). Hence: **no CI build of anything containing yFiles today.**
3. A yFiles-enabled distribution is non-commercial-only (§2.4), while GROOVE is Apache 2.0.
   Hence two artefacts, whatever their shape, and a visible notice on the restricted one.

Constraint 2 is the one that shapes the release procedure, and it is the one that may lift:
the Academic Project upgrade is an open question with yWorks.

## Option A — two full distributions (what is built now)

Every release: 4 zips, plus 4 CI installers, plus as many edition installers as Arend can
build by hand. He can produce the Windows `.msi` on his machine and, plausibly, the Linux
`.deb` from WSL. **macOS installers of the edition cannot be produced at all** without a
licensed build on a Mac, so macOS users would get the edition only as a zip, or not at all.

- **For**: the edition is a first-class product; users install it like any other GROOVE.
  Nothing changes in the code.
- **Against**: a manual, partly impossible per-platform build step at every release; ~137 MB
  of near-duplicate download per platform; two entries per platform on the download page,
  with the licence distinction to explain at the point of download; the risk of a version
  skew between an edition built by hand and a standard release built by CI.

**Option A-lite**: ship the edition as zips only, no edition installers. This removes the
per-platform problem entirely and is a two-line change to the release procedure (a paragraph
in `release/README.md`). The cost is that the edition's users need a Java installation,
which the installers exist precisely to avoid.

## Option B — one distribution plus a yFiles add-on

Ship exactly what CI builds today, for everyone, and distribute the restricted part as a
small **add-on** (the two jars, ~8.8 MB, plus the notice) that GROOVE loads at start-up
from an extension directory.

Mechanism, in outline:

- A user-level extension directory, say `%APPDATA%\GROOVE\extensions` on Windows and
  `~/.groove/extensions` elsewhere, overridable with a system property or environment
  variable. It must be user-writable, which the installed application directory is not
  reliably (the Windows install is per-user, macOS and Linux are not).
- `GraphBackend.Instance.discover` currently calls `ServiceLoader.load(GraphBackend.class)`,
  which uses the thread context class loader, i.e. the application class path. It would
  additionally load providers through a `URLClassLoader` over the jars in that directory,
  with the application loader as parent, and merge the results. Class-path deployment keeps
  working unchanged, so Eclipse, the `yfiles/` unit's tests and any existing launch are
  unaffected.
- A **version guard** is needed. The backend jar is compiled against the `gui.view`
  interfaces of one GROOVE version; a mismatched add-on would fail at an arbitrary later
  moment with `NoSuchMethodError`. The add-on jar should carry the GROOVE version in its
  manifest, and a mismatch should be a logged skip, not a crash. `discover` already
  tolerates a provider that fails to instantiate.
- Packaging: the `release/yfiles` module already produces the two obfuscated jars; it would
  gain an assembly producing `groove-yfiles-addon-x_y_z.zip` instead of feeding the edition
  zips. The obfuscation itself does not change. The edition assembly descriptors, the
  `-yfiles` zips and the installer's edition branch would all go away.

- **For**: one set of artefacts from CI for everyone; exactly one small thing built locally
  per release; no macOS gap; the public installers contain no trace of yFiles, which keeps
  the Apache-licensed artefact clean; the non-commercial notice travels with precisely the
  files it restricts; the download page gets one product plus an optional extra.
- **Against**: users must unzip a file into a directory by hand, which is worse than an
  installer; a new mechanism to write, test and document (expect roughly a day, including a
  headless test that the add-on is discovered from the directory); an add-on that is not
  updated together with GROOVE becomes a support question, which the version guard turns
  into a clear message rather than a crash.
- **Reverses**: the "dual distribution" decision recorded under the licence constraints in
  `yfiles-migration.md`. That decision was taken before the edition existed and before it
  was known that the standard runtime already suffices.

## Option C — Academic Project Licence, both editions from CI

If yWorks grants the upgrade (3 seats, build automation), CI may build the edition. The jar
still cannot live in a public repository, so it would go into a private Maven repository
(GitHub Packages of the organisation) reached with a token from the workflow secrets, and
the runtime licence file likewise. The existing matrix then builds both editions on all four
platforms and attaches eight installers to the release, with no manual step.

This is the best outcome for users and the least work per release, and it makes option A the
natural shape. It depends entirely on yWorks' answer, and on Arend's willingness to put the
licensed jar into a private registry.

## Assessment

The decision hinges on the Project Licence, which is already on the list of questions for
yWorks:

- **If granted**: keep option A, move the edition into CI, and the whole problem disappears.
  Nothing in the current build is wasted.
- **If refused**: option A costs a manual, partly impossible step at every release, and its
  cost per release never goes away. Option B trades a one-off day of work and a slightly
  clumsier user experience for a release procedure that is one command. I would take that
  trade, and would then also drop the edition zips: a single add-on for both the zip and the
  installed application is simpler to explain than two.
- **Until the answer arrives**: option A-lite is a reasonable interim. The edition zips exist
  and are verified; the Windows installer can be built if someone asks for it.

Nothing needs to be decided to merge the current work. The edition as built is the fallback
in every branch of the decision, and options B and C are both reachable from it.

## Open questions

For yWorks (see the list in `yfiles-migration.md`, of which the first is the decisive one
here):

- Is the Academic Project upgrade available, and does it permit CI builds from a private
  registry holding the plain jar?
- Under option B, is an add-on distributed separately from GROOVE still "your application"
  in the sense of §2.1c, given that the obfuscated library ships without the rest of the
  tool? This changes nothing technically, but it is a different distribution shape from the
  one the SLA describes, and worth asking about explicitly.

Technical, if option B is pursued:

- Does the yFiles library work from a child class loader? Nothing in its documented API
  suggests otherwise, and the licence file it reads lives at the root of the backend jar,
  which the child loader would find. To be verified before committing to the design.
- Does anything in the library need a JDK module beyond the standard bundled set at
  *run* time? Static analysis says no; a smoke test of the Simulator on an add-on inside an
  installed standard app image would settle it.
- Where should the extension directory live on macOS, given the sandbox conventions
  (`~/Library/Application Support/GROOVE`)?

## Pointers

- `claude/yfiles-migration.md`, "Phase 5" and "Immediate next steps".
- `release/README.md`, the chapter on the yFiles edition.
- `release/yfiles/pom.xml` (obfuscation), `release/jpackage/build-installer.sh` (installers),
  `release/assembly/**/zip-yfiles.xml` (edition zips).
- `yfiles/README.md` for the backend unit itself.
