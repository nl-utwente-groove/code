<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="materialisation-test-4">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n1">
            <attr name="layout">
                <string>285 315 29 32</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>285 181 36 48</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>109 308 36 48</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>436 311 29 32</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>438 190 29 32</string>
            </attr>
        </node>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n0" from="n3">
            <attr name="label">
                <string>n</string>
            </attr>
        </edge>
        <edge to="n2" from="n1">
            <attr name="label">
                <string>n</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n1" from="n4">
            <attr name="label">
                <string>f</string>
            </attr>
        </edge>
        <edge to="n3" from="n2">
            <attr name="label">
                <string>n</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
    </graph>
</gxl>
