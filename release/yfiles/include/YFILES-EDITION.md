GROOVE yFiles edition
=====================

This is the yFiles edition of GROOVE: the same tool set as the standard
release, with a second graph-visualisation backend built on the commercial
library [yFiles for Java (Swing)](https://www.yworks.com/products/yfiles-for-java)
by yWorks GmbH. The Simulator uses it by default when present (the choice is
in the Options menu, under "Graph backend", and takes effect at the next start),
which adds yFiles' layout algorithms to the layout menu; the Imager selects it
with its `-b` option. The standard, JGraph-based backend is included as well.

License restrictions of this edition
------------------------------------

GROOVE itself is free software under the Apache License 2.0 (see
[the GROOVE repository](https://github.com/nl-utwente-groove/code)). The
yFiles library in this edition (`lib/yfiles-for-java-swing-*.jar`, together
with `lib/groove-yfiles-*.jar`, which is built against it) is **not**: it is
included under an academic license of the University of Twente and remains the
property of yWorks GmbH. Therefore:

- This edition may be used **for non-commercial purposes only**, such as
  research, teaching and study. Any commercial use of the tool set must use the
  standard release, which carries no such restriction.
- The yFiles library is included in obfuscated form, as its license requires.
  It may not be extracted from this distribution, de-obfuscated, decompiled or
  otherwise reverse engineered, and may not be used for any purpose other than
  running GROOVE.
- This edition may be passed on unchanged and as a whole, under the same
  restrictions; it may not be repackaged or included in other software.

If in doubt about whether your use qualifies, use the standard release.
