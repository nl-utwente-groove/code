# Distributing GROOVE now that there are two graph backends

Companion to `yfiles-migration.md` (gh #909), whose "Phase 5" section records what was
built. This note is about a question that phase 5 answered only provisionally: **what a
GROOVE release consists of, now that a second, license-restricted edition exists**. It
states the facts, the options and their consequences, and the decision. First written
2026-09-09, after the edition was built and verified, as an open question; the decision below
was taken the same day, once yWorks had confirmed the licence terms.

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
2. Only the licensed developer may build with it; automated builds need a Project Licence
   (§2.2.2). **Confirmed by yWorks on 2026-09-09: the licence is a Project Licence with one
   seat, so CI builds are permitted.** The seat is a constraint on people, not on machines:
   only Arend may develop against the plain library, so wherever the plain jar lives for CI,
   nobody but him and the workflow token may read it.
3. A yFiles-enabled distribution is non-commercial-only (§2.4), while GROOVE is Apache 2.0.
   Hence two artefacts, whatever their shape, and a visible notice on the restricted one.
   Note that this taints whatever *contains* the library: an installer carrying the jars as
   an optional feature is a restricted artefact even for users who decline the feature.

The lifting of constraint 2 is what settled the decision. The options below are kept as
written before the answer arrived, since they record why the chosen shape is the chosen one.

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

- **For**: one set of artefacts from CI for everyone; no macOS gap; the public installers
  contain no trace of yFiles, which keeps the Apache-licensed artefact clean; the
  non-commercial notice travels with precisely the files it restricts; the download page
  gets one product plus an optional extra.
- **Against**: users must unzip a file into a directory by hand, which is worse than an
  installer; a new mechanism to write, test and document; an add-on that is not updated
  together with GROOVE becomes a support question, which the version guard turns into a
  clear message rather than a crash.
- **Reverses**: the "dual distribution" decision recorded under the licence constraints in
  `yfiles-migration.md`. That decision was taken before the edition existed and before it
  was known that the standard runtime already suffices.

## Option C — both editions from CI

With the Project Licence, CI may build the edition. The jar still cannot live in a public
repository, so it goes into a private location reached with a token from the workflow
secrets. The existing matrix then builds both editions on all four platforms and attaches
eight installers to the release, with no manual step.

This is the least work per release under option A's shape, and it removes A's manual step
and macOS gap. It keeps A's other costs: eight installers, ~137 MB per platform to carry
8.8 MB of difference, and two products to explain on the download page.

## Rejected: one installer that asks about yFiles

The obvious refinement of B — a single installer with a "do you want yFiles?" question that
drops the jars into place — does not work, for two independent reasons:

- **Licence.** For the installer to copy the jars, it must contain them, and then the whole
  installer is the restricted artefact (constraint 3 above), whether or not the user ticks
  the box. The Apache-clean installer is precisely the one without yFiles bytes.
- **Tooling.** jpackage installers are nearly unconfigurable: the `.dmg` is drag-and-drop
  with no dialogs at all, the `.deb` has none either, and only the `.msi` could gain a
  feature dialog by overriding the WiX sources through `--resource-dir`, a Windows-only and
  fragile customisation. An installer that *downloads* at install time is not on offer.

The question is right; the installer is the wrong place to ask it. GROOVE itself can ask.

## Decision: option B, built entirely by CI, with an in-app add-on installer

Taken 2026-09-09. **Implemented the same day on branch `yfiles-extension-loader`**, one
commit per slice of the "Implementation slices" below; what is left is listed under
"After implementation" at the end. The shape:

1. **Standard installers and zips from CI, as now**, with no yFiles bytes in them.
2. **CI also builds the add-on**, `groove-yfiles-addon-x_y_z.zip` (the two obfuscated jars
   plus `YFILES-EDITION.md`), under the `yfiles` profile, and attaches it to the release
   next to the zips and installers. Nothing is built by hand any more.
3. **GROOVE loads the add-on from a user-level extension directory** (option B's mechanism,
   including the version guard).
4. **GROOVE offers to install the add-on itself.** On the first start of a newly installed
   release, if no add-on for the running version is present, the Simulator asks once
   whether the user wants the yFiles backend, showing the non-commercial notice. "Yes"
   downloads the add-on for the running version from the GitHub release, checks the
   manifest version, unpacks it into the extension directory and asks for a restart
   (backend selection is fixed at start-up by design, see `GraphBackend.instance()`, so
   a restart is the honest answer rather than a hot swap). "No" records the refusal for
   this version, and the same action stays available as *Options › Install yFiles
   backend…*, next to the backend choice; there is also *Install from file…* for machines
   without network access, which takes a downloaded add-on zip. A new GROOVE version whose
   installed add-on is stale (version guard) asks again, this time phrased as an update.

   The one-time question is keyed on the GROOVE version in the user preferences
   (`Options.userPrefs`, where the backend choice already lives): shown at most once per
   version, and never when an add-on for the running version is present.

Why this and not C: C is the cheapest change to the *build* but keeps two products; B with
an in-app installer gives one product, one small add-on, the licence notice at the exact
moment the restricted files arrive, the same experience on all three platforms, and an
extension loader that will be wanted for other optional parts later. The extra work over
plain B is the download step, which is small (`java.net.http` against a fixed release URL,
~9 MB). It also reverses the "dual distribution" note in `yfiles-migration.md`, which should
be updated with the change.

### CI design for the licensed parts

- A **private repository** in the organisation (say `nl-utwente-groove/yfiles-lib`) holds
  the plain library jar and the runtime licence file. GitHub Packages was considered and
  dropped: a package inherits its repository's visibility, so it would need a private repo
  anyway, and a plain checkout is simpler. Encrypted blobs in the public repo would work
  too but put licensed bytes into a public history. Access: Arend and the workflow token
  only, per the one-seat constraint.
- `release.yml` gains, before the release reactor: `actions/checkout` of that repo with a
  fine-grained PAT (or deploy key) from the secrets, the `install:install-file` step from
  the backend's README (the `README.md` of `yfiles-lib` since 2026-09-09), and `-Dyfiles.license.dir` pointing into the checkout. Then
  `-Pyfiles` on the release reactor; yGuard is a Maven plugin and needs nothing else.
- The `yfiles` profile stays off `maven.yml`: secrets are not available to workflows run
  for pull requests from forks, and the PR build must keep working without the library, as
  it does today.
- The add-on assembly runs in the same job as the zips, so the release job attaches it with
  the same `release-action` step; the installer matrix is untouched.

### Implementation slices

Each independently mergeable, in this order:

1. **Extension loader** (`GraphBackend.Instance.discover`): extension directory resolution
   (platform default, `groove.extensions.dir` system property override), child
   `URLClassLoader`, manifest version guard with a logged skip, headless test that a backend
   jar in a temporary directory is discovered and a mismatched one is skipped. Preceded by
   the class-loader smoke test below.
2. **Add-on packaging** (`release/yfiles`): manifest entry with the GROOVE version, assembly
   producing the add-on zip; remove the `-yfiles` zips, their descriptors and the installer's
   edition branch; `release/README.md` accordingly.
3. **CI**: the private repo, the secrets, the `release.yml` steps. Verified by a dry run on a
   throwaway tag on a branch (the workflow triggers on `release-*_*_*` tags only).
4. **In-app installer**: the first-run question, the Options actions, download, unpack,
   restart prompt; a test of the unpack-and-verify path against a local zip (the download
   itself is mocked or skipped headlessly).
5. **Docs**: `yfiles-migration.md` (reverse the dual-distribution note), the web manual's
   installation page, the download page (one product plus the add-on and its notice).

### Verified before slice 1 (2026-09-09)

- The library works from a child class loader: the Imager rendered the ferryman grammar
  on the yFiles backend with the phase-5 obfuscated jars in a scratch extension directory,
  the license file found at the root of the backend jar as before.
- No JDK module beyond the bundled standard set is needed: the Imager launcher of a
  standard app image (built with the reverted installer script) rendered on the add-on
  loaded from an extension directory, without a warning.
- The macOS location is `~/Library/Application Support/GROOVE/extensions`, the platform
  convention; Linux and other systems use `~/.groove/extensions`, Windows
  `%APPDATA%\GROOVE\extensions`.

### After implementation

- **Arend**: the private repository `nl-utwente-groove/yfiles-lib` with the plain
  library jar (`yfiles-for-java-swing.jar`) and the runtime license file at its root, and
  the repository secret `YFILES_LIB_TOKEN` (a fine-grained PAT with read access to that
  repository only); then a dry run of `release.yml` on a throwaway `release-*_*_*` tag on
  a branch, checking that the add-on zip is attached and that its backend jar carries the
  `GROOVE-Version` attribute.
- **Arend**: the wording of `release/yfiles/include/YFILES-ADDON.md` and of the
  Simulator's first-run question (`gui.AddOnInstaller.confirmInstall`) against the SLA.
- **Website**: the download page (one product plus the add-on and its notice) and the
  installation page of the web manual; see "Docs" under the slices.
- The first-run question uses the download; a development version (`-SNAPSHOT`) never
  asks, since it has no release to download from, and its download action fails with a
  clear 404 message. "Install from file..." works for any version.

## Open questions

For yWorks:

- Is an add-on distributed separately from GROOVE still "your application" in the sense of
  §2.1c, given that the obfuscated library ships without the rest of the tool? Nothing
  changes technically, but the distribution shape differs from the one the SLA describes,
  and it is worth asking before the add-on goes public.

## Pointers

- `claude/yfiles-migration.md`, "Phase 5" and "Immediate next steps".
- `release/README.md`, the chapter on the yFiles edition.
- `release/yfiles/pom.xml` (obfuscation), `release/jpackage/build-installer.sh` (installers),
  `release/assembly/**/zip-yfiles.xml` (edition zips), `.github/workflows/release.yml`.
- the `README.md` of the private `yfiles-lib` repository for the backend unit itself; `gui/view/GraphBackend.java` for discovery
  and selection; `gui/Options.java` for the user preferences.
