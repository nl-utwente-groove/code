GROOVE yFiles add-on
====================

This is the yFiles add-on of GROOVE: a second graph-visualisation backend for
the GROOVE tool set, built on the commercial library
[yFiles for Java (Swing)](https://www.yworks.com/products/yfiles-for-java)
by yWorks GmbH. It consists of this directory, which GROOVE loads from its
extension directory at start-up: the Simulator then uses the yFiles backend by
default (the choice is in the Options menu, under "Graph backend", and takes
effect at the next start), which adds yFiles' layout algorithms to the layout
menu, and the Imager selects it with its `-b` option. The vintage JGraph-based
backend of the standard release remains available.

The add-on is built for one GROOVE version, recorded in the manifest of its
`groove-yfiles-*.jar`; another version of GROOVE skips it with a warning at
start-up. A new GROOVE version therefore needs the add-on of that version.

License restrictions of this add-on
-----------------------------------

GROOVE itself is free software under the Apache License 2.0 (see
[the GROOVE repository](https://github.com/nl-utwente-groove/code)). The
yFiles library in this add-on (`yfiles-for-java-swing-*.jar`, together with
`groove-yfiles-*.jar`, which is built against it) is **not**: it is
included under an academic license of the University of Twente and remains the
property of yWorks GmbH. Therefore:

- GROOVE with this add-on installed may be used **for non-commercial purposes
  only**, such as research, teaching and study. Any commercial use of the tool
  set must do without the add-on; the standard release carries no such
  restriction.
- The yFiles library is included in obfuscated form, as its license requires.
  It may not be extracted from this add-on, de-obfuscated, decompiled or
  otherwise reverse engineered, and may not be used for any purpose other than
  running GROOVE.
- This add-on may be passed on unchanged and as a whole, under the same
  restrictions; it may not be repackaged or included in other software.

If in doubt about whether your use qualifies, do not install the add-on.
