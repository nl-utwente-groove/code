<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>306 373 61 16</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>100 370 61 16</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>87 450 33 16</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>300 119 33 16</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>187 209 61 16</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>364 180 36 16</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>49 175 36 16</string>
            </attr>
        </node>
        <node id="n7">
            <attr name="layout">
                <string>391 433 33 16</string>
            </attr>
        </node>
        <node id="n8">
            <attr name="layout">
                <string>127 279 33 16</string>
            </attr>
        </node>
        <node id="n9">
            <attr name="layout">
                <string>298 49 61 16</string>
            </attr>
        </node>
        <node id="n10">
            <attr name="layout">
                <string>114 123 33 16</string>
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
        <edge from="n0" to="n7">
            <attr name="label">
                <string>out</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>transition</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>transition</string>
            </attr>
        </edge>
        <edge from="n1" to="n2">
            <attr name="label">
                <string>out</string>
            </attr>
        </edge>
        <edge from="n2" to="n12">
            <attr name="label">
                <string>in</string>
            </attr>
            <attr name="layout">
                <string>500 0 95 450 14 384 14 126 142 50 11</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>place</string>
            </attr>
        </edge>
        <edge from="n3" to="n4">
            <attr name="label">
                <string>in</string>
            </attr>
        </edge>
        <edge from="n3" to="n5">
            <attr name="label">
                <string>mark</string>
            </attr>
        </edge>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>place</string>
            </attr>
        </edge>
        <edge from="n4" to="n11">
            <attr name="label">
                <string>out</string>
            </attr>
        </edge>
        <edge from="n4" to="n4">
            <attr name="label">
                <string>transition</string>
            </attr>
        </edge>
        <edge from="n4" to="n8">
            <attr name="label">
                <string>out</string>
            </attr>
        </edge>
        <edge from="n5" to="n5">
            <attr name="label">
                <string>token</string>
            </attr>
        </edge>
        <edge from="n6" to="n6">
            <attr name="label">
                <string>token</string>
            </attr>
        </edge>
        <edge from="n7" to="n9">
            <attr name="label">
                <string>in</string>
            </attr>
            <attr name="layout">
                <string>500 0 407 441 514 374 514 102 328 57 11</string>
            </attr>
        </edge>
        <edge from="n7" to="n7">
            <attr name="label">
                <string>place</string>
            </attr>
        </edge>
        <edge from="n8" to="n1">
            <attr name="label">
                <string>in</string>
            </attr>
        </edge>
        <edge from="n8" to="n8">
            <attr name="label">
                <string>place</string>
            </attr>
        </edge>
        <edge from="n9" to="n9">
            <attr name="label">
                <string>transition</string>
            </attr>
        </edge>
        <edge from="n9" to="n3">
            <attr name="label">
                <string>out</string>
            </attr>
        </edge>
        <edge from="n10" to="n6">
            <attr name="label">
                <string>mark</string>
            </attr>
            <attr name="layout">
                <string>714 8 135 134 72 186 11</string>
            </attr>
        </edge>
        <edge from="n10" to="n4">
            <attr name="label">
                <string>in</string>
            </attr>
        </edge>
        <edge from="n10" to="n10">
            <attr name="label">
                <string>place</string>
            </attr>
        </edge>
        <edge from="n11" to="n0">
            <attr name="label">
                <string>in</string>
            </attr>
        </edge>
        <edge from="n11" to="n11">
            <attr name="label">
                <string>place</string>
            </attr>
        </edge>
        <edge from="n12" to="n12">
            <attr name="label">
                <string>transition</string>
            </attr>
        </edge>
        <edge from="n12" to="n10">
            <attr name="label">
                <string>out</string>
            </attr>
        </edge>
    </graph>
</gxl>
