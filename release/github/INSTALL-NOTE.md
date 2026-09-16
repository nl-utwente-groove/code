### Potential installation problems

**Note**: The installers are not code-signed, so Windows and MacOS block them at initial invocation, throwing up a warning screen, and some Linux package managers ask before installing the `.rpm`. Here is how to solve this:

- **Windows** (*"Windows protected your PC"*):
  Right-click the downloaded `.msi` → `Properties` → `General` → tick `Unblock` at the bottom; then press `OK` and run it again.
  
- **MacOS** (*"Apple could not verify GROOVE…"*):
  After the warning, open `System Settings` → `Privacy & Security` and click `Open Anyway`.

- **Linux** (*"Package is not signed!"*):
  `zypper` asks whether to continue: answer yes. `dnf` and `rpm -i` install the `.rpm` without asking; if `dnf` refuses, add `--nogpgcheck`. The `.deb` is not affected, as `apt` and `dpkg` do not check signatures of local package files.

The `groove-…-bin.zip` needs no installer and triggers no warning, but requires Java 21 or newer. Manual installation instructions are given in `README.md` inside the zip.

More detailed instructions can be found in`IF-WINDOWS-OR-MACOS-BLOCKS-THE-INSTALLER.txt` among the assets below.
