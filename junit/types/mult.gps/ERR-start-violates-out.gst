<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="ERR-start-violates-out">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>138 83 9 15</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>261 83 8 15</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>262 155 8 15</string>
            </attr>
        </node>
        <edge from="n0" to="n1">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge from="n0" to="n2">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
    </graph>
</gxl>
