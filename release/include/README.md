[The GROOVE Tool Set](https://github.com/nl-utwente-groove)
=====================

Installation and use
--------------------

The bin subdirectory of the GROOVE directory (into which you unzipped the
downloaded archive) contains jar files for each of the toolkit programs:
e.g., Simulator.jar, Generator.jar. Running them requires Java 21 or newer on
your machine; the native installers offered on the release page bundle a Java
runtime of their own instead. You can use the jar files in either of the
following ways:

*  In a file manager window opened on the bin directory, double-click the jar file;

*  In a command line window, run `java -jar GROOVE_PATH/bin/Program.jar [parameters]`,
   where `GROOVE_PATH` is the groove directory and `Program` is the toolkit
   program in question. If you use `-h` as parameter, you will get information
   about the (other) command-line parameters.

See the user manual at <https://nl-utwente-groove.github.io/manual.html> for
instructions on how to use the GROOVE Tool Set.

The yFiles add-on
-----------------

GROOVE can show graphs with the commercial library yFiles for Java (Swing) by
yWorks GmbH, which adds its layout algorithms to the Simulator's layout menu.
The library is not part of this distribution: it comes as a separate add-on,
`groove-x_y_z-yfiles-addon.zip` for this version of GROOVE, licensed for
non-commercial use only (see YFILES-ADDON.md inside). The Simulator offers to
download and install it at the first start of a new version, and at any later
time under View > yFiles add-on; by hand, unzip it into GROOVE's extension
directory (`%APPDATA%\GROOVE\extensions` on Windows,
`~/Library/Application Support/GROOVE/extensions` on macOS,
`$XDG_DATA_HOME/groove/extensions`, normally `~/.local/share/groove/extensions`,
elsewhere), from which GROOVE loads it at the next start.

Features
--------

Here is a brief list of the features supported by GROOVE. For details please
consult the online user manual or the YouTube demos on
[the GROOVE web site](https://groove.cs.utwente.nl).

*  Visual editing and simulation of graphs and graph transformation rules
*  Global rule priorities or control program
*  State space exploration using customised exploration strategies
*  Model checking generated transition systems over CTL and LTL formulae
*  Regular expression matching and label wildcards in transformation rules
*  Primitive data types (int, string, bool and real) with corresponding operations
*  Nested (i.e., quantified) rules
*  Typing and inheritance through node types
*  Prolog querying of graphs and state space

Tools
-----

The installation includes the following tools:

*  `Simulator(.jar)`: a GUI-based tool that lets you construct, simulate and
   model check rule systems visually;
*  `Generator(.jar)`: a command line tool that lets you simulate and model check
   rule systems without the performance penalty of the GUI;
*  `Imager(.jar)`: a command line or GUI tool that supports conversions from
   GROOVE graphs and rules to other visual formats.
*  `Viewer(.jar)`: a stand-alone (GUI) viewer for \GROOVE graphs and rules.

Contact
-------

Any comments, bug reports and reactions are welcome, either as
[a github issue](https://github.com/nl-utwente-groove/code/issues) or through an email to

Arend Rensink
University of Twente
<https://people.utwente.nl/arend.rensink>
[arend.rensink@utwente.nl](mailto:arend.rensink@utwente.nl)
