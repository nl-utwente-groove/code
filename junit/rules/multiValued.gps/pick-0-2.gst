<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="pick-0-2">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="a">
            <attr name="layout">
                <string>80 80 60 30</string>
            </attr>
        </node>
        <node id="b">
            <attr name="layout">
                <string>250 80 60 30</string>
            </attr>
        </node>
        <edge from="a" to="a">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge from="a" to="a">
            <attr name="label">
                <string>let:n = 3</string>
            </attr>
        </edge>
        <edge from="b" to="b">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge from="b" to="b">
            <attr name="label">
                <string>let:x = 2</string>
            </attr>
        </edge>
    </graph>
</gxl>
