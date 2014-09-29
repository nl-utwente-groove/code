<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n3">
            <attr name="layout">
                <string>223 128 64 46</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>220 51 64 46</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>102 129 64 46</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>96 53 64 46</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>217 199 64 46</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>96 200 64 46</string>
            </attr>
        </node>
        <node id="n7">
            <attr name="layout">
                <string>215 263 64 46</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>94 264 64 46</string>
            </attr>
        </node>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>flag:available</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>flag:available</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>flag:available</string>
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
        <edge to="n1" from="n1">
            <attr name="label">
                <string>flag:available</string>
            </attr>
        </edge>
        <edge to="n2" from="n1">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>flag:available</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>flag:available</string>
            </attr>
        </edge>
        <edge to="n5" from="n4">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>type:D</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>flag:available</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>type:D</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>flag:available</string>
            </attr>
        </edge>
        <edge to="n7" from="n6">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
    </graph>
</gxl>
