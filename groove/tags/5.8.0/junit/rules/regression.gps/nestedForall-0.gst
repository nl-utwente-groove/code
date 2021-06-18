<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="nestedForall-0">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n1">
            <attr name="layout">
                <string>320 200 29 31</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>200 280 28 31</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>205 192 29 31</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>381 284 28 31</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>323 284 28 31</string>
            </attr>
        </node>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n4" from="n0">
            <attr name="label">
                <string>a</string>
            </attr>
        </edge>
        <edge to="n2" from="n1">
            <attr name="label">
                <string>a</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n3" from="n1">
            <attr name="label">
                <string>a</string>
            </attr>
        </edge>
    </graph>
</gxl>
