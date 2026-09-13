# Assets

This release contains the following assets.

## Libraries for self-installation

The `groove-…-bin[+doc].zip` assets contain compiled sources (one of them with Javadoc) and can be used out of the box if you have the right Java version on your machine (21 or newer). Installation instructions are given in `README.md` inside the zip.

## Automatic installers

The `linux`, `macos` and `windows` assets are automatic installers for the three most common platforms. They will save you the trouble of unzipping, having the right Java version on your machine and getting the invocation correct. Note that the installers have few user options; among other things, they use a fixed, platform-default installation path. If you want more control, use the self-installation option above.

### Potential installation problems

The installers are not code-signed, so Windows and MacOS block them at initial invocation, throwing up a warning screen. Here is how to solve this:

- **Windows** (*"Windows protected your PC"*):
  Right-click the downloaded `.msi` → `Properties` → `General` → tick `Unblock` at the bottom; then press `OK` and run it again.
  
- **MacOS** (*"Apple could not verify GROOVE…"*):
  After the warning, open `System Settings` → `Privacy & Security` and click `Open Anyway`.

The `groove-…-bin.zip` needs no installer and triggers no warning, but requires Java 21 or newer. Manual installation instructions are given in `README.md` inside the zip.

More detailed instructions can be found in`IF-WINDOWS-OR-MACOS-BLOCKS-THE-INSTALLER.txt` among the assets below.

## The yFiles add-on

The `groove-…-yfiles-add-on.zip` is _not_ a stand-alone asset; instead, it is a separate backend that with a stricter license than the main tool: among other things, you may not use it for commercial purposes. This will either be loaded automatically (on demand) the first time you start up a newly released GROOVE simulator, or you can download the zip and import it into the Simulator yourself.

More precise license restrictions for the yFiles add-on can be found in `YFILES-ADDON.md` inside the zip.

## Source code

The GROOVE project (except for the yFiles addon) is open source; you can either clone the project directly from github or use one of the source archive in the assets.
