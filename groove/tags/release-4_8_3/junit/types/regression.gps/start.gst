<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>104 49 29 31</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>214 48 29 31</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>106 128 28 31</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>215 129 28 31</string>
            </attr>
        </node>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n3" from="n0">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n1" from="n2">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n3" from="n2">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
    </graph>
</gxl>
