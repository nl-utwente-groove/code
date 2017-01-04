<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="materialisation-test-9">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n1">
            <attr name="layout">
                <string>121 254 36 48</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>119 118 36 48</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>285 293 36 48</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>291 87 36 48</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>292 193 36 48</string>
            </attr>
        </node>
        <edge to="n3" from="n0">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n2" from="n0">
            <attr name="label">
                <string>c</string>
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
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n4" from="n1">
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
    </graph>
</gxl>
