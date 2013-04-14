<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="materialisation-test-1c">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n1">
            <attr name="layout">
                <string>412 105 28 32</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>266 137 30 32</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>414 217 28 32</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>413 158 28 32</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>266 188 30 32</string>
            </attr>
        </node>
        <edge to="n1" from="n3">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n2" from="n0">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n2" from="n3">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n4" from="n3">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n4" from="n0">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
    </graph>
</gxl>
