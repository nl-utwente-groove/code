<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>511 124 22 14</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>46 140 37 28</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>470 39 42 14</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>456 126 22 14</string>
            </attr>
        </node>
        <node id="n9">
            <attr name="layout">
                <string>226 180 47 14</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>197 271 37 28</string>
            </attr>
        </node>
        <node id="n8">
            <attr name="layout">
                <string>203 31 37 28</string>
            </attr>
        </node>
        <node id="n10">
            <attr name="layout">
                <string>349 151 37 28</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>516 186 22 14</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>201 149 26 14</string>
            </attr>
        </node>
        <node id="n7">
            <attr name="layout">
                <string>456 176 22 14</string>
            </attr>
        </node>
        <edge to="n3" from="n9">
            <attr name="label">
                <string>init</string>
            </attr>
        </edge>
        <edge to="n9" from="n9">
            <attr name="label">
                <string>type:Scheduler</string>
            </attr>
        </edge>
        <edge to="n5" from="n1">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>int:-1</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>int:1</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:Process</string>
            </attr>
        </edge>
        <edge to="n1" from="n9">
            <attr name="label">
                <string>init</string>
            </attr>
        </edge>
        <edge to="n3" from="n8">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n2" from="n6">
            <attr name="label">
                <string>number</string>
            </attr>
        </edge>
        <edge to="n7" from="n6">
            <attr name="label">
                <string>number</string>
            </attr>
        </edge>
        <edge to="n8" from="n8">
            <attr name="label">
                <string>type:Process</string>
            </attr>
        </edge>
        <edge to="n10" from="n9">
            <attr name="label">
                <string>init</string>
            </attr>
        </edge>
        <edge to="n10" from="n10">
            <attr name="label">
                <string>flag:active</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>flag:active</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>int:3</string>
            </attr>
        </edge>
        <edge to="n8" from="n8">
            <attr name="label">
                <string>flag:active</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>flag:active</string>
            </attr>
        </edge>
        <edge to="n10" from="n1">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:Process</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>int:4</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>int:2</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>type:Numbers</string>
            </attr>
        </edge>
        <edge to="n0" from="n6">
            <attr name="label">
                <string>number</string>
            </attr>
        </edge>
        <edge to="n5" from="n3">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n1" from="n3">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n8" from="n9">
            <attr name="label">
                <string>init</string>
            </attr>
        </edge>
        <edge to="n10" from="n10">
            <attr name="label">
                <string>type:Process</string>
            </attr>
        </edge>
        <edge to="n5" from="n10">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n4" from="n6">
            <attr name="label">
                <string>number</string>
            </attr>
        </edge>
        <edge to="n5" from="n8">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n8" from="n10">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
    </graph>
</gxl>
