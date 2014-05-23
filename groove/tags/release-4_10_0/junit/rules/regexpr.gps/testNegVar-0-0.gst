<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="testNegVar-0-0">
        <node id="n0">
            <attr name="layout">
                <string>99 76 29 31</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>223 228 28 31</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>97 158 37 31</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>229 77 28 31</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>218 157 28 31</string>
            </attr>
        </node>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n2" from="n0">
            <attr name="label">
                <string>aToB</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:A2</string>
            </attr>
        </edge>
        <edge to="n2" from="n1">
            <attr name="label">
                <string>a1ToB</string>
            </attr>
        </edge>
        <edge to="n3" from="n1">
            <attr name="label">
                <string>aToB</string>
            </attr>
        </edge>
        <edge to="n4" from="n1">
            <attr name="label">
                <string>a1ToB</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
    </graph>
</gxl>
