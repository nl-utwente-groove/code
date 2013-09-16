<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start-2">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>579 104 51 33</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>456 30 68 31</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>441 116 51 33</string>
            </attr>
        </node>
        <node id="n9">
            <attr name="layout">
                <string>212 171 74 31</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>185 262 61 46</string>
            </attr>
        </node>
        <node id="n10">
            <attr name="layout">
                <string>337 142 61 46</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>130 99 55 33</string>
            </attr>
        </node>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>int:2</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>Numbers</string>
            </attr>
        </edge>
        <edge to="n4" from="n6">
            <attr name="label">
                <string>number</string>
            </attr>
        </edge>
        <edge to="n0" from="n6">
            <attr name="label">
                <string>number</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>int:1</string>
            </attr>
        </edge>
        <edge to="n9" from="n9">
            <attr name="label">
                <string>Scheduler</string>
            </attr>
        </edge>
        <edge to="n1" from="n9">
            <attr name="label">
                <string>init</string>
            </attr>
        </edge>
        <edge to="n10" from="n9">
            <attr name="label">
                <string>init</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>Process</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>active</string>
            </attr>
        </edge>
        <edge to="n10" from="n1">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n5" from="n1">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n10" from="n10">
            <attr name="label">
                <string>Process</string>
            </attr>
        </edge>
        <edge to="n10" from="n10">
            <attr name="label">
                <string>active</string>
            </attr>
        </edge>
        <edge to="n5" from="n10">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge to="n1" from="n10">
            <attr name="label">
                <string>next</string>
            </attr>
            <attr name="layout">
                <string>454 -16 362 183 362 284 241 284 12</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>int:-1</string>
            </attr>
        </edge>
    </graph>
</gxl>
