# What does this release contain? (Assets, see below)

<details>
<summary><b>Libraries for manual installation</b></summary>

The `groove-…-bin[+doc].zip` assets contain compiled sources (one of them with Javadoc) and can be used out of the box if you have the right Java version on your machine (21 or newer); `groove-…-yfiles-bin.zip` is the same with yFiles added, for academic use only (see below). Installation instructions are given in `README.md` inside the zip.

</details>

<details>
<summary><b>Automatic installers</b></summary>

The `linux`, `macos` and `windows` assets are automatic installers for the three most common platforms. They will save you the trouble of unzipping, having the right Java version on your machine (they bundle a Java 25 runtime of their own) and getting the invocation correct. Note that the installers have few customisation options; among other things, they use a fixed, platform-default installation path. If you want more control, use the manual installation route above. The installers contain yFiles and are therefore for academic, non-commercial use only (see below).

#### Potential installation problems

The installers are not code-signed, so Windows and MacOS block them at initial invocation, throwing up a warning screen, and some Linux package managers ask before installing the `.rpm`. Here is how to solve this:

- **Windows** (*"Windows protected your PC"*):
  Right-click the downloaded `.msi` → `Properties` → `General` → tick `Unblock` at the bottom; then press `OK` and run it again.
  
- **MacOS** (*"Apple could not verify GROOVE…"*):
  After the warning, open `System Settings` → `Privacy & Security` and click `Open Anyway`.

- **Linux** (*"Package is not signed!"*):
  `zypper` asks whether to continue: answer yes. `dnf` and `rpm -i` install the `.rpm` without asking; if `dnf` refuses, add `--nogpgcheck`. The `.deb` is not affected, as `apt` and `dpkg` do not check signatures of local package files.

More detailed instructions can be found in `IF-WINDOWS-OR-MACOS-BLOCKS-THE-INSTALLER.txt` among the assets. If you cannot get the installer for your platform to work, please [file an issue on github](https://github.com/nl-utwente-groove/code/issues) and use the manual installation route for now (see above).

</details>

<details>
<summary><b>With or without yFiles: academic use only, or unrestricted</b></summary>

GROOVE displays graphs using the commercial library [yFiles for Java](https://www.yworks.com/products/yfiles-for-java) by yWorks GmbH, which is licensed to us for academic, non-commercial use only. That restriction carries over to every asset that contains the library:

- The automatic installers and `groove-…-yfiles-bin.zip` contain yFiles. **You may use them for academic, non-commercial purposes only**, such as research, teaching and study. The precise restrictions are in `LICENSE-YFILES.txt` inside the zip; the installers show them for you to accept.
- `groove-…-bin.zip` and `groove-…-bin+doc.zip` do not contain yFiles and carry no such restriction: they are under the Apache License 2.0 alone. They display graphs using the JGraph-based backend of earlier releases, which lacks the yFiles layout algorithms but is otherwise the same tool.

If in doubt about whether your use qualifies as "academic and non-commercial", use one of the zips without yFiles.

</details>
<details>
<summary><b>Source code</b></summary>

The GROOVE project is open source, under the Apache License 2.0; you can either clone the project directly from github or use one of the source archives in the assets. The source includes the part of GROOVE that calls yFiles, but not the yFiles library itself, which is not open source: building that part requires a yFiles licence of your own.

</details>