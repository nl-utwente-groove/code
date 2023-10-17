<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="figure">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n9">
            <attr name="layout">
                <string>90 30 31 51</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>320 70 31 31</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>230 70 41 31</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>390 60 31 51</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>160 70 31 31</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>90 110 31 51</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>450 20 31 51</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>490 60 31 51</string>
            </attr>
        </node>
        <node id="n7">
            <attr name="layout">
                <string>450 100 31 51</string>
            </attr>
        </node>
        <node id="n8">
            <attr name="layout">
                <string>20 30 31 51</string>
            </attr>
        </node>
        <node id="n10">
            <attr name="layout">
                <string>20 90 31 51</string>
            </attr>
        </node>
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
        <edge to="n4" from="n9">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:IF</string>
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
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:IF</string>
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
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:P</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>flag:s</string>
            </attr>
        </edge>
        <edge to="n6" from="n3">
            <attr name="label">
                <string>at</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>type:P</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>flag:u</string>
            </attr>
        </edge>
        <edge to="n6" from="n5">
            <attr name="label">
                <string>at</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>type:P</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>flag:u</string>
            </attr>
        </edge>
        <edge to="n6" from="n7">
            <attr name="label">
                <string>at</string>
            </attr>
        </edge>
        <edge to="n8" from="n8">
            <attr name="label">
                <string>type:P</string>
            </attr>
        </edge>
        <edge to="n8" from="n8">
            <attr name="label">
                <string>flag:s</string>
            </attr>
        </edge>
        <edge to="n9" from="n8">
            <attr name="label">
                <string>at</string>
            </attr>
        </edge>
        <edge to="n10" from="n10">
            <attr name="label">
                <string>type:P</string>
            </attr>
        </edge>
        <edge to="n10" from="n10">
            <attr name="label">
                <string>flag:s</string>
            </attr>
        </edge>
        <edge to="n9" from="n10">
            <attr name="label">
                <string>at</string>
            </attr>
        </edge>
    </graph>
</gxl>
