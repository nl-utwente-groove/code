<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n2">
            <attr name="layout">
                <string>334 139 29 31</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>420 138 29 31</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>266 139 29 31</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>156 235 31 31</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>412 65 27 31</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>152 138 29 31</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>252 59 27 33</string>
            </attr>
        </node>
        <edge to="n1" from="n2">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
        <edge to="n6" from="n5">
            <attr name="label">
                <string>min</string>
            </attr>
        </edge>
        <edge to="n3" from="n2">
            <attr name="label">
                <string>min</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>int:1</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string></string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>int:0</string>
            </attr>
        </edge>
        <edge to="n3" from="n0">
            <attr name="label">
                <string>min</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
        <edge to="n5" from="n2">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
        <edge to="n3" from="n4">
            <attr name="label">
                <string>min</string>
            </attr>
        </edge>
    </graph>
</gxl>
