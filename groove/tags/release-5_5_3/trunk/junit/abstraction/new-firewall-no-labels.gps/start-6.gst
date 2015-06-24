<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start-6">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n5">
            <attr name="layout">
                <string>440 70 31 51</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>130 80 31 31</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>380 20 31 51</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>210 80 41 31</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>50 150 31 51</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>290 70 31 31</string>
            </attr>
        </node>
        <node id="n9">
            <attr name="layout">
                <string>50 20 31 51</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>380 120 31 51</string>
            </attr>
        </node>
        <node id="n7">
            <attr name="layout">
                <string>10 80 31 51</string>
            </attr>
        </node>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>type:L</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>flag:o</string>
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
        <edge to="n6" from="n6">
            <attr name="label">
                <string>flag:o</string>
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
        <edge to="n4" from="n4">
            <attr name="label">
                <string>flag:i</string>
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
        <edge to="n9" from="n9">
            <attr name="label">
                <string>flag:i</string>
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
        <edge to="n3" from="n3">
            <attr name="label">
                <string>flag:o</string>
            </attr>
        </edge>
        <edge to="n2" from="n3">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>type:L</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>flag:i</string>
            </attr>
        </edge>
        <edge to="n1" from="n7">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
    </graph>
</gxl>
