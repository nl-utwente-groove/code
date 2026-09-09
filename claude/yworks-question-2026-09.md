# Message to yWorks: distribution and source of the GROOVE yFiles backend

Draft of 2026-09-09 for Arend to send (licence correspondence; the earlier answers on
LLM use of 2026-09-05 and on the Project Licence of 2026-09-09 are the thread this
continues). Adjust the salutation and the licence order reference; the rest is meant
to go as it stands. Questions (a) to (d) were announced earlier; (e) to (h) came out
of re-reading the SLA.

---

Subject: GROOVE yFiles backend: distribution shape and source code

Dear ...,

Thank you for confirming that our licence is a Project Licence with one seat. Before
we publish anything that contains yFiles, I want to check the shape we have arrived
at against the SLA, and correct one thing on our side.

**The situation.** GROOVE (https://github.com/nl-utwente-groove/code) is an academic
graph-transformation tool, free software under the Apache License 2.0, developed in
public. It has its own graph-visualisation backend on the open-source JGraph library.
We have written a second backend on yFiles for Java (Swing) 3.6.0.1: a unit of about
twenty classes in our own package that calls the yFiles API and plugs into GROOVE
through a service interface. GROOVE itself contains no yFiles code and runs without
the library.

We intend to distribute this as follows:

- The standard GROOVE release (zips and installers, Apache 2.0) contains nothing of
  yFiles.
- A separate *add-on* zip, published as a download on the same GitHub release page,
  contains the yFiles library obfuscated with yGuard as in your deployment demo, our
  backend jar (unobfuscated except for its references to the renamed library
  classes), and a notice stating that the add-on is licensed for non-commercial use
  only, may not be extracted, de-obfuscated or repackaged, and that GROOVE with the
  add-on installed is restricted in the same way.
- GROOVE loads the add-on from a user-level extension directory. On first start it
  offers to download and install the add-on, showing the notice, and the user can
  decline.
- The add-on is built by our release workflow on GitHub Actions, which takes the
  plain library jar and the licence file from a private repository that only I and
  the workflow can read.

**The correction.** The source of the backend unit has been in GROOVE's public
repository since we wrote it, under Apache 2.0. Re-reading the SLA I concluded that
§2.4 ("any software application developed under an Academic License may not be
licensed ... to a third party being a commercial institution") and §1 do not permit
that. We have removed the unit from the public branches and moved it to the private
repository mentioned above. The commits that added it are still in the public
history.

**The questions.**

(a) What is the status of our Subscription, and which generation of the library was
delivered under it? Our code is written against 3.6.0.1.

(b) §2.1(c): is the obfuscation as above sufficient? yGuard renames every library
name except your annotated exclusions and the methods our backend overrides. Our
backend jar necessarily references the renamed names of the API subset it uses, so
in principle the mapping of that subset can be read off it.

(c) May the development licence file we received ship inside the add-on as the
runtime licence (it is the file the library loads), or do you issue a separate
deployment licence for this?

(d) §2.1(c) and §2.4: is an add-on distributed separately from GROOVE, as a public
download with the non-commercial notice, still one of "your own software
applications", and acceptable under the academic restriction?

(e) §2.4: is the rest of GROOVE, which contains no yFiles code and runs without the
library, unaffected by the restriction, so that it can stay under Apache 2.0 and be
used commercially without the add-on? And is the backend source acceptable in a
private repository readable by the licensed developer and the build automation only?

(f) Do you require the commits that added the backend source to be purged from the
public history of the GROOVE repository, or does removal from the current branches
suffice? A purge is possible but rewrites the repository's history, so we would
rather know that it is needed.

(g) §2.1(d): the add-on jar necessarily contains public classes (the service
provider and the canvases GROOVE instantiates). They are documented as internal and
expose nothing of the yFiles API by type, but are callable by anyone who puts the
jar on their class path. Is that acceptable, or must the surface be reduced further?

(h) Attached is the wording of the notice that ships in the add-on and of the
question GROOVE shows before installing it. Would you like anything changed?

Best regards,
Arend Rensink

---

Attachments for (h): `release/yfiles/include/YFILES-ADDON.md` and the message text
in `gui.AddOnInstaller.confirmInstall` (paste the rendered text, not the Java).
