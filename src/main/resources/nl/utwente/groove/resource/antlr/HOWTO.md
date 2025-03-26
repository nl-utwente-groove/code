How to generate the parser and checker files from a `?.g` grammar
---------

- Assume the JVM is on the path

- Open a terminal in this directory (`src\main\resources\nl\utwente\groove\resource\antlr`)

- Invoke

```
java -jar ..\..\..\..\..\..\..\..\lib\antlr-complete-3.5.2.jar ?.g
move ?.java ..\..\..\..\..\..\java\nl\utwente\groove\control\parse
```

Mutatis mutandis if this is not on Windows
