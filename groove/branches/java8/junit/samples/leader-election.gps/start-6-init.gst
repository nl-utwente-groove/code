<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start-6-init">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n6">
            <attr name="layout">
                <string>511 124 22 14</string>
            </attr>
        </node>
        <node id="n9">
            <attr name="layout">
                <string>200 60 56 70</string>
            </attr>
        </node>
        <node id="n8">
            <attr name="layout">
                <string>252 356 56 70</string>
            </attr>
        </node>
        <node id="n10">
            <attr name="layout">
                <string>618 168 22 14</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>202 210 47 14</string>
            </attr>
        </node>
        <node id="n13">
            <attr name="layout">
                <string>22 254 26 14</string>
            </attr>
        </node>
        <node id="n14">
            <attr name="layout">
                <string>456 126 22 14</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>456 176 22 14</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>102 360 56 70</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>470 39 42 14</string>
            </attr>
        </node>
        <node id="n12">
            <attr name="layout">
                <string>668 112 22 14</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>92 154 56 70</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>353 307 56 70</string>
            </attr>
        </node>
        <node id="n7">
            <attr name="layout">
                <string>516 186 22 14</string>
            </attr>
        </node>
        <node id="n11">
            <attr name="layout">
                <string>305 151 56 70</string>
            </attr>
        </node>
        <edge to="n13" from="n5">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n10" from="n9">
            <attr name="label">
                <string>max</string>
            </attr>
        </edge>
        <edge to="n11" from="n11">
            <attr name="label">
                <string>type:Process</string>
            </attr>
        </edge>
        <edge to="n5" from="n2">
            <attr name="label">
                <string>init</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>type:Process</string>
            </attr>
        </edge>
        <edge to="n9" from="n2">
            <attr name="label">
                <string>init</string>
            </attr>
        </edge>
        <edge to="n13" from="n8">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n6" from="n1">
            <attr name="label">
                <string>max</string>
            </attr>
        </edge>
        <edge to="n10" from="n9">
            <attr name="label">
                <string>number</string>
            </attr>
        </edge>
        <edge to="n12" from="n8">
            <attr name="label">
                <string>number</string>
            </attr>
        </edge>
        <edge to="n8" from="n3">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:Process</string>
            </attr>
        </edge>
        <edge to="n13" from="n3">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n12" from="n12">
            <attr name="label">
                <string>int:6</string>
            </attr>
        </edge>
        <edge to="n11" from="n11">
            <attr name="label">
                <string>flag:active</string>
            </attr>
        </edge>
        <edge to="n12" from="n8">
            <attr name="label">
                <string>max</string>
            </attr>
        </edge>
        <edge to="n7" from="n3">
            <attr name="label">
                <string>number</string>
            </attr>
        </edge>
        <edge to="n9" from="n9">
            <attr name="label">
                <string>type:Process</string>
            </attr>
        </edge>
        <edge to="n11" from="n5">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n1" from="n9">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n6" from="n1">
            <attr name="label">
                <string>number</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>flag:active</string>
            </attr>
        </edge>
        <edge to="n14" from="n14">
            <attr name="label">
                <string>int:1</string>
            </attr>
        </edge>
        <edge to="n0" from="n11">
            <attr name="label">
                <string>max</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:Process</string>
            </attr>
        </edge>
        <edge to="n3" from="n1">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>int:3</string>
            </attr>
        </edge>
        <edge to="n3" from="n2">
            <attr name="label">
                <string>init</string>
            </attr>
        </edge>
        <edge to="n8" from="n8">
            <attr name="label">
                <string>type:Process</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>int:2</string>
            </attr>
        </edge>
        <edge to="n13" from="n9">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:Numbers</string>
            </attr>
        </edge>
        <edge to="n1" from="n2">
            <attr name="label">
                <string>init</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>flag:active</string>
            </attr>
        </edge>
        <edge to="n13" from="n1">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n8" from="n8">
            <attr name="label">
                <string>flag:active</string>
            </attr>
        </edge>
        <edge to="n8" from="n2">
            <attr name="label">
                <string>init</string>
            </attr>
        </edge>
        <edge to="n13" from="n11">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n11" from="n2">
            <attr name="label">
                <string>init</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>flag:active</string>
            </attr>
        </edge>
        <edge to="n9" from="n11">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n9" from="n9">
            <attr name="label">
                <string>flag:active</string>
            </attr>
        </edge>
        <edge to="n13" from="n13">
            <attr name="label">
                <string>int:-1</string>
            </attr>
        </edge>
        <edge to="n14" from="n5">
            <attr name="label">
                <string>number</string>
            </attr>
        </edge>
        <edge to="n0" from="n11">
            <attr name="label">
                <string>number</string>
            </attr>
        </edge>
        <edge to="n7" from="n3">
            <attr name="label">
                <string>max</string>
            </attr>
        </edge>
        <edge to="n14" from="n5">
            <attr name="label">
                <string>max</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:Scheduler</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>int:4</string>
            </attr>
        </edge>
        <edge to="n10" from="n10">
            <attr name="label">
                <string>int:5</string>
            </attr>
        </edge>
        <edge to="n5" from="n8">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
    </graph>
</gxl>
