<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <node id="n1">
            <attr name="layout">
                <string>100 370 61 16</string>
            </attr>
        </node>
        <node id="n8">
            <attr name="layout">
                <string>127 279 33 16</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>187 209 61 16</string>
            </attr>
        </node>
        <node id="n10">
            <attr name="layout">
                <string>114 123 33 16</string>
            </attr>
        </node>
        <node id="n9">
            <attr name="layout">
                <string>298 49 61 16</string>
            </attr>
        </node>
        <node id="n7">
            <attr name="layout">
                <string>391 433 33 16</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>49 175 36 16</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>87 450 33 16</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>306 373 61 16</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>364 180 36 16</string>
            </attr>
        </node>
        <node id="n11">
            <attr name="layout">
                <string>235 274 33 16</string>
            </attr>
        </node>
        <node id="n12">
            <attr name="layout">
                <string>112 42 61 16</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>300 119 33 16</string>
            </attr>
        </node>
        <edge to="n9" from="n7">
            <attr name="label">
                <string>in</string>
            </attr>
            <attr name="layout">
                <string>500 0 428 433 514 374 514 102 369 68 11</string>
            </attr>
        </edge>
        <edge to="n6" from="n10">
            <attr name="label">
                <string>mark</string>
            </attr>
            <attr name="layout">
                <string>714 8 135 134 72 186 11</string>
            </attr>
        </edge>
        <edge to="n9" from="n9">
            <attr name="label">
                <string>transition</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>transition</string>
            </attr>
        </edge>
        <edge to="n2" from="n1">
            <attr name="label">
                <string>out</string>
            </attr>
        </edge>
        <edge to="n4" from="n3">
            <attr name="label">
                <string>in</string>
            </attr>
        </edge>
        <edge to="n11" from="n4">
            <attr name="label">
                <string>out</string>
            </attr>
        </edge>
        <edge to="n1" from="n8">
            <attr name="label">
                <string>in</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>place</string>
            </attr>
        </edge>
        <edge to="n0" from="n11">
            <attr name="label">
                <string>in</string>
            </attr>
        </edge>
        <edge to="n12" from="n2">
            <attr name="label">
                <string>in</string>
            </attr>
            <attr name="layout">
                <string>500 0 95 450 14 384 14 126 127 64 11</string>
            </attr>
        </edge>
        <edge to="n5" from="n3">
            <attr name="label">
                <string>mark</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>transition</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>token</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>place</string>
            </attr>
        </edge>
        <edge to="n8" from="n4">
            <attr name="label">
                <string>out</string>
            </attr>
        </edge>
        <edge to="n4" from="n10">
            <attr name="label">
                <string>in</string>
            </attr>
        </edge>
        <edge to="n7" from="n0">
            <attr name="label">
                <string>out</string>
            </attr>
        </edge>
        <edge to="n10" from="n10">
            <attr name="label">
                <string>place</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>token</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>transition</string>
            </attr>
        </edge>
        <edge to="n3" from="n9">
            <attr name="label">
                <string>out</string>
            </attr>
        </edge>
        <edge to="n12" from="n12">
            <attr name="label">
                <string>transition</string>
            </attr>
        </edge>
        <edge to="n8" from="n8">
            <attr name="label">
                <string>place</string>
            </attr>
        </edge>
        <edge to="n10" from="n12">
            <attr name="label">
                <string>out</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>place</string>
            </attr>
        </edge>
        <edge to="n11" from="n11">
            <attr name="label">
                <string>place</string>
            </attr>
        </edge>
    </graph>
</gxl>
