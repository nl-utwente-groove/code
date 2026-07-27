# Ecore example repository

The fixtures in this directory are the worked examples of the Ecore porter
(`nl.utwente.groove.io.external.format.ecore`): together they exercise every
Ecore feature the porter supports, and every approximation it makes. The
encoding they illustrate is specified in
[`claude/ecore-porter-design.md`](../../claude/ecore-porter-design.md); this
file records what the porter *actually* produces for these inputs, so that the
two can be compared. Every label below is copied from a real import.

Run the covering tests with

```
mvn test -Dtest=EcoreTest
```

Each example is a pair: a `.ecore` meta-model and a `.xmi` instance of it.
Importing the `.ecore` yields a type graph; importing the `.xmi` yields both a
host graph *and* the regenerated type graph, so an instance import exercises the
meta-model encoding as well. `broken` is the exception: it is the error-path
example and does not round-trip.

## Reading the labels

The labels are written as GROOVE parses them, with the aspect prefixes intact.
In the listings below, a line `name  label label …` gives the *self-loops* of
one node — everything the node declares about itself — and a line with an arrow
gives a binary edge.

| label form | meaning |
|---|---|
| `type:C` | the node is of node type `C` |
| `abs:` | the node type is abstract (an abstract class, an interface, or an enum) |
| `sub:` | subtype edge, from the subtype to the supertype |
| `int:a`, `real:a`, `bool:a`, `string:a` | attribute `a` of the given sort |
| `out=lo..hi:` | multiplicity annotation of an edge (omitted when it is `0..*`) |
| `part:` | containment edge |
| `edge:"r"`, `int:index` | the intermediate node of a nodified feature `r` under `index` ordering |
| `id:x` | node identifier, from the `xmi:id` of the object |
| `let:a=v` | attribute assignment in a host graph |

Two things are easy to misread. First, an attribute over a *data type* becomes a
self-loop (`int:code`), but an attribute over an *enum* becomes an ordinary edge
— to the enum's type node in the type graph, to the literal's node in an
instance — because enums are node types, not sorts. Second, a reference from a
class to itself is a self-loop too, so `out=0..1:next` among a node's own labels
is a reference, not an attribute.

## `shop` — the tour of the encoding

**Purpose.** The original fixture: one small metamodel touching most of the
encoding at once. Everything the other examples isolate can be seen here in
context.

**Features.** Classes, an abstract class, single inheritance, attributes of
three sorts, a custom `EDataType`, an enum, containment, a cross-reference, an
`eOpposite` pair, explicit multiplicities, a many-valued attribute, a
sub-package with a colliding classifier name.

| Ecore | GROOVE |
|---|---|
| `EClass Shop` | `type:Shop` |
| `EClass Item` (abstract) | `type:shop$Item` + `abs:` |
| `Book eSuperTypes Item` | `Book -sub:-> shop$Item` |
| `Shop.name : EString [1..1]` | `string:name` (the lower bound is not encoded) |
| `Item.price : EDouble` | `real:price` |
| `Book.isbn : Isbn` (custom data type) | `string:isbn`, with `Isbn` recorded in the metadata |
| `Shop.items : Item [1..*]` containment, opposite of `Item.shop` | `Shop -out=1..*:part:items-> shop$Item` |
| `Item.shop : Shop [1..1]` | `shop$Item -out=1:shop-> Shop` |
| `Customer.favourites : Item [0..*]` | `Customer -favourites-> shop$Item` (no annotation: `0..*` is the default) |
| `EEnum Category` | `type:Category` + `abs:`, with `Category$FICTION -sub:-> Category` per literal |
| `Book.category : Category` | `Book -out=0..1:category-> Category` |
| `catalog.Item` (sub-package) | `type:catalog$Item`, colliding with `type:shop$Item` |

In the instance, single-valued attributes become assignments and many-valued
ones edges to shared constant nodes:

```
theShop  type:Shop  id:theShop  let:name="Books&Co"  let:open=true
book1    type:Book  id:book1    let:code=1  let:price=9.99  let:isbn="978-1"
book1 -tags-> string:"fiction"      book1 -tags-> string:"classic"
book1 -category-> Category$FICTION  alice -favourites-> book1
```

The `eOpposite` pairing is not structural; it is recorded as
`ecoreOpposites = Shop.items|shop$Item.shop`.

**Covering tests.** `testMetamodel`, `testMetadata`, `testCompilation`,
`testInstance`, `testUseIdentifiers`, `testOrderingSetSemantics`,
`testMetamodelExport`, `testInstanceExport`, `testManyValuedAttribute`.

## `ordered` — the two ordering modes

**Purpose.** The smallest model that the `ordering` option can tell apart: an
ordered containment and a *non-unique* many-valued attribute.

**Features.** `ordered="true"` (the Ecore default) and `unique="false"`.

Under `ordering=none` the order is dropped and duplicates collapse — silently,
because that is what the mode selects. The three `labels` values `x y x` become
two constant nodes:

```
List     type:List     string:labels
Element  type:Element  string:name
List -part:elements-> Element

theList -elements-> first    theList -elements-> second    theList -elements-> third
theList -labels-> string:"x"                theList -labels-> string:"y"
```

Under `ordering=index` both features are nodified:

```
List$elements   type:List$elements  edge:"elements"  int:index
List$labels     type:List$labels    edge:"labels"    int:index  string:val
List -in=1:elements-> List$elements       List$elements -out=1:part:val-> Element
List -in=1:labels-> List$labels
```

and the instance keeps both the order and the duplicate, since every occurrence
gets its own intermediate node:

```
theList -elements-> List$elements#1 -val-> first
theList -labels-> List$labels#1 -val-> string:"x"
theList -labels-> List$labels#3 -val-> string:"x"
```

Note that `shop` deliberately does *not* change between the modes: all its
many-valued features are declared `ordered="false"` and are unique, which is
exactly the set semantics that direct edges already express.

**Covering tests.** `testOrderingNone`, `testOrderingIndex`,
`testOrderingExport`.

## `broken` — the error path

**Purpose.** The one thing the instance encoding refuses to approximate. Unlike
the mappings the encoding makes by design, `NaN` has no GROOVE counterpart at
all, so it is a format error rather than a substituted zero.

`broken.xmi` holds a single `Measurement` with `value="NaN"`. The import
succeeds, but the host graph carries

```
Value 'NaN' of attribute 'value' has no GROOVE representation
```

and the offending value is simply absent: the node is `type:Measurement id:m1`
and nothing else. Because any error keeps a resource from compiling, this
example is *not* round-tripped.

**Covering test.** `testUnrepresentableValue`.

## `datatypes` — one attribute per EMF data type

**Purpose.** The complete data type table, with the edge cases of the values.

**Features.** Every standard EMF data type the porter maps, a custom
`EDataType`, and a many-valued `EString` attribute.

| attribute | Ecore type | type graph | host graph |
|---|---|---|---|
| `booleanValue` | `EBoolean` | `bool:booleanValue` | `let:booleanValue=true` |
| `booleanObject` | `EBooleanObject` | `bool:booleanObject` | `let:booleanObject=false` |
| `byteValue` | `EByte` | `int:byteValue` | `let:byteValue=-128` |
| `byteObject` | `EByteObject` | `int:byteObject` | `let:byteObject=127` |
| `shortValue` | `EShort` | `int:shortValue` | `let:shortValue=-32768` |
| `shortObject` | `EShortObject` | `int:shortObject` | `let:shortObject=32767` |
| `intValue` | `EInt` | `int:intValue` | `let:intValue=-2147483648` |
| `integerObject` | `EIntegerObject` | `int:integerObject` | `let:integerObject=2147483647` |
| `longValue` | `ELong` | `int:longValue` | `let:longValue=-9223372036854775808` |
| `longObject` | `ELongObject` | `int:longObject` | `let:longObject=9223372036854775807` |
| `bigInteger` | `EBigInteger` | `int:bigInteger` | `let:bigInteger=170141183460469231731687303715884105727` |
| `floatValue` | `EFloat` | `real:floatValue` | `let:floatValue=-1.5` |
| `floatObject` | `EFloatObject` | `real:floatObject` | `let:floatObject=0.25` |
| `doubleValue` | `EDouble` | `real:doubleValue` | `let:doubleValue=-0.5` |
| `doubleObject` | `EDoubleObject` | `real:doubleObject` | `let:doubleObject=0.001` |
| `bigDecimal` | `EBigDecimal` | `real:bigDecimal` | `let:bigDecimal=-123.456` |
| `stringValue` | `EString` | `string:stringValue` | `let:stringValue="He said \"hi\", path C:\temp"` |
| `charValue` | `EChar` | `string:charValue` | `let:charValue="65"` |
| `characterObject` | `ECharacterObject` | `string:characterObject` | `let:characterObject="122"` |
| `dateValue` | `EDate` | `string:dateValue` | `let:dateValue="2026-03-01T12:00:00.000+0100"` |
| `customValue` | `Colour` (custom) | `string:customValue` | `let:customValue="#ff8800"` |
| `aliases` | `EString [0..*]` | `string:aliases` | edges to `string:"plain"` and `string:"quo\"ted"` |

Three of these rows deserve a second look.

* **Boundary values survive the graph, not the algebra.** GROOVE's integer
  constants are `BigInteger`-based, so `ELong` and `EBigInteger` values outside
  the Java `int` range arrive in the host graph unchanged, compile without
  complaint and round-trip exactly. They are narrowed only when the graph is
  evaluated with the default algebra family, which uses Java `int`.
* **Characters arrive as numbers.** EMF serialises an `EChar` as the decimal
  code of the character, and reads it back the same way, so `A` becomes `"65"`
  and `z` becomes `"122"`. The round trip is exact; the label is just not what
  one expects.
* **Dates are local.** EMF renders an `EDate` in the time zone of the machine
  reading it. The label above is what a CET machine produces for the fixture's
  `2026-03-01T12:00:00.000+01:00`; elsewhere the rendering differs, which is why
  the test only pins down its shape.

String values are quoted GROOVE-style: a `"` inside the value is escaped, a
backslash is not (`\` only escapes a following quote, so it does not need it).
`EDate` and the custom `Colour` are both approximated by strings; the declared
type is kept in the metadata (`Values|dateValue|EDate|...`,
`Values|customValue|Colour|...`), so an export puts it back.

Under `ordering=index` only the many-valued `aliases` changes, to
`Values$aliases` with `edge:"aliases"`, `int:index`, `string:val`.

**Covering tests.** `testDatatypes`, `testDatatypesIndexed`.

## `hierarchy` — classification

**Purpose.** Everything the type graph expresses about *kinds* of node.

**Features.** An interface, an abstract class, multiple inheritance, an
inheritance chain of four levels, an enum with a literal whose name is not a
Java identifier.

```
Named       type:Named       abs:  string:name        (interface)
Trackable   type:Trackable   abs:                     (interface)
Element     type:Element     abs:  int:rank           (abstract class)
Task        type:Task                                 (concrete)
Subtask     type:Subtask     int:depth
Project     type:Project
Status      type:Status      abs:                     (enum)

Element -sub:-> Named         Task -sub:-> Element     Task -sub:-> Trackable
Subtask -sub:-> Task          Project -sub:-> Named
Project -part:tasks-> Task    Trackable -out=0..1:status-> Status

Status$NEW -sub:-> Status     Status$IN_HYPH_PROGRESS -sub:-> Status
Status$DONE -sub:-> Status
```

An interface is encoded exactly like an abstract class — `abs:` and nothing
else. What tells the two apart on the way back out is the metadata, which
records `Named|hierarchy|Named|interface` against `Element|hierarchy|Element|class`.
Multiple inheritance needs no special encoding: `Task` simply has two `sub:`
edges.

The enum literal `IN-PROGRESS` is not a Java identifier, so it is repaired:
`type:Status$IN_HYPH_PROGRESS`, with the original name kept in the metadata
(`Status$IN_HYPH_PROGRESS|hierarchy|IN-PROGRESS|literal`). In the instance only
the literals that are actually used get a node — `Status$NEW` has none.

Under `ordering=index` the ordered containment `tasks` is nodified, and the
containment moves to the value edge:

```
Project -in=1:tasks-> Project$tasks -out=1:part:val-> Task
```

**Covering tests.** `testHierarchy`, `testHierarchyIndexed`.

## `network` — references among themselves

**Purpose.** References that point back into the same class, and the
multiplicities that constrain them.

**Features.** A self-referential class, a mutually opposite reference pair on
that one class, an explicitly bounded multiplicity, and a class that is both
contained and cross-referenced.

```
Network   type:Network
Station   type:Station  string:name  out=0..1:next  out=0..1:previous  route

Network -out=2..4:part:stations-> Station
```

`next`, `previous` and `route` are all loops of `Station`, since a station's
references target stations. `stations` is the bounded one: `lowerBound="2"
upperBound="4"` becomes `out=2..4:`, and the instance has three stations, so it
type-checks.

The opposite pair is `Station.next` / `Station.previous`. Both directions become
ordinary edges — the encoding does not enforce the pairing — and only the
metadata knows they belong together:

```
ecoreOpposites = Station.next|Station.previous

north -next-> middle      middle -previous-> north
middle -next-> south      south -previous-> middle
```

Under `ordering=index` the ordered cross-reference `route` is nodified and its
order becomes explicit; `stations`, being `ordered="false"` and unique, keeps
its direct (and bounded) encoding:

```
north -route-> Station$route#1 -val-> south
north -route-> Station$route#2 -val-> middle
```

The instance declares the `next` references *backwards*, to a station read
earlier in the file. This is an EMF loading quirk rather than a GROOVE one: a
single-valued reference whose `eOpposite` is single-valued too is silently
dropped when its target has not been read yet. It does not affect exported
files, since an export writes both directions of every pair.

**Covering tests.** `testNetwork`, `testNetworkIndexed`.

## `packages` — names

**Purpose.** The naming policy: qualification of colliding classifier names, and
repair of names that GROOVE cannot use as identifiers.

**Features.** Two levels of sub-package, a three-way name collision across them,
a class name that is not a Java identifier, two attribute names that are not,
and an attribute named after a GROOVE keyword.

```
packages          Item                     ->  type:packages$Item    string:name
packages.core     Item                     ->  type:core$Item        int:code
packages.core     Line-Item                ->  type:Line_HYPH_Item
packages.core.detail  Item                 ->  type:detail$Item      string:note

packages$Item -part:entries-> core$Item
core$Item -part:details-> detail$Item
Line_HYPH_Item -sub:-> core$Item
```

Three classifiers called `Item` collide, so each is qualified with one segment
of its package path — enough to separate them, so the policy stops there. Note
that `Line-Item` is *not* qualified: its simple name never collided.

Classifier and feature names are repaired under different rules, which is why
the same character can fare differently:

| Ecore name | kind | GROOVE label | why |
|---|---|---|---|
| `Line-Item` | classifier | `Line_HYPH_Item` | a type label must be a Java identifier |
| `unit-price` | attribute | `unit_price` | a hyphen is mapped to an underscore |
| `unit.price` | attribute | `unit_UNKN_price` | `.` has no dedicated replacement |
| `self` | attribute | `_self_` | `self` is a reserved GROOVE keyword |

A feature label has to be usable as an *attribute field name*, which is a Java
identifier, so it is repaired more strictly than a type label: a hyphen would be
legal in a GROOVE identifier, but `real:unit-price` does not parse. Mapping it
to an underscore rather than to the `_HYPH_` a classifier gets keeps the label
readable, at the price of not being reversible by rule — which does not matter,
since the name is recorded (see below). References are repaired by the same rule
as attributes, although a hyphen would do them no harm: one rule is easier to
predict than two.

So the instance reads

```
line   type:Line_HYPH_Item  id:line  let:code=2
       let:unit_UNKN_price=9.5  let:unit_price=7.25  let:_self_="own"
```

The metadata carries the package paths, the classifier names, and the name of
every feature whose label does not reproduce it, so an export reconstructs the
package tree and puts all four names back:

```
ecorePackages = packages|http://groove.utwente.nl/ecore/packages|packages;
                packages.core|http://groove.utwente.nl/ecore/packages/core|core;
                packages.core.detail|http://groove.utwente.nl/ecore/packages/core/detail|detail
ecoreTypes    = packages$Item|packages|Item|class;core$Item|packages.core|Item|class;
                Line_HYPH_Item|packages.core|Line-Item|class;
                detail$Item|packages.core.detail|Item|class
ecoreFeatures = packages$Item|entries||false|true|0|-1|;
                core$Item|details||true|true|0|-1|;
                Line_HYPH_Item|_self_||true|true|0|1|self;
                Line_HYPH_Item|unit_UNKN_price||true|true|0|1|unit.price;
                Line_HYPH_Item|unit_price||true|true|0|1|unit-price
```

(the line breaks are for readability; the recorded values have none). A feature
record is `owner|feature|declaredType|ordered|unique|lower|upper|originalName`,
with the last field empty — as in the first two records above — whenever the
label already is the Ecore name.

Under `ordering=index` the intermediate node of `core$Item.details` is named
after the *already qualified* owner label: `type:core$Item$details`.

**Covering tests.** `testPackages`, `testPackagesNames`, `testPackagesIndexed`.

## Known gaps

Things these examples deliberately stay clear of, because the porter does not
handle them today:

* **A string value ending in a backslash breaks the graph.** The value is quoted
  by escaping the quote character only, so a trailing `\` escapes the closing
  quote.
