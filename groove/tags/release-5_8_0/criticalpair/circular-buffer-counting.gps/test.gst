<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="test">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>356 111 38 18</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>363 198 23 18</string>
            </attr>
        </node>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>type:Buffer</string>
            </attr>
        </edge>
        <edge from="n0" to="n1">
            <attr name="label">
                <string>first</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>type:Cell</string>
            </attr>
        </edge>
    </graph>
</gxl>
