<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start-5">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n5">
            <attr name="layout">
                <string>371 109 28 32</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>130 79 31 32</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>371 29 28 32</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>211 79 39 32</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>51 119 28 32</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>290 69 31 32</string>
            </attr>
        </node>
        <node id="n9">
            <attr name="layout">
                <string>51 29 28 32</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>341 181 28 48</string>
            </attr>
        </node>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>type:L</string>
            </attr>
        </edge>
        <edge to="n2" from="n5">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:IF</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>type:L</string>
            </attr>
        </edge>
        <edge to="n2" from="n6">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:FW</string>
            </attr>
        </edge>
        <edge to="n2" from="n0">
            <attr name="label">
                <string>out</string>
            </attr>
        </edge>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>in</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:L</string>
            </attr>
        </edge>
        <edge to="n1" from="n4">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:IF</string>
            </attr>
        </edge>
        <edge to="n9" from="n9">
            <attr name="label">
                <string>type:L</string>
            </attr>
        </edge>
        <edge to="n1" from="n9">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:L</string>
            </attr>
        </edge>
        <edge to="n2" from="n3">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
    </graph>
</gxl>
