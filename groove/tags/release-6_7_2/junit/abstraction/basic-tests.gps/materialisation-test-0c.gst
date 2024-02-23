<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="materialisation-test-0c">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n1">
            <attr name="layout">
                <string>260 199 30 32</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>263 109 30 32</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>397 194 28 32</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>398 110 28 32</string>
            </attr>
        </node>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n3" from="n1">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n2" from="n0">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
    </graph>
</gxl>
