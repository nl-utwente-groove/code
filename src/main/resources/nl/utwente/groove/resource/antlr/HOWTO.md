How to generate the parser and checker files from a `?.g` grammar
---------

- Assume the JVM is on the path

- Open a terminal in this directory (`src\main\resources\nl\utwente\groove\resource\antlr`)

- Invoke (for the specific cases of `Ctrl.g` and `CtrlChecker.g`)

	```
	java -jar ..\..\..\..\..\..\..\..\lib\antlr-complete-3.5.2.jar Ctrl.g
	java -jar ..\..\..\..\..\..\..\..\lib\antlr-complete-3.5.2.jar CtrlChecker.g
	copy *.java ..\..\..\..\..\..\java\nl\utwente\groove\control\parse
	rm *.java
	```

Mutatis mutandis if this is not on Windows
