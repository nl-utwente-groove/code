<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n1">
            <attr name="layout">
                <string>437 155 7 15</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>215 252 7 15</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>325 129 24 15</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>246 149 8 15</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>330 66 55 15</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>420 260 7 15</string>
            </attr>
        </node>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>B</string>
            </attr>
        </edge>
        <edge to="n3" from="n1">
            <attr name="label">
                <string>score</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>int:0</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>bool:false</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>A</string>
            </attr>
        </edge>
        <edge to="n2" from="n0">
            <attr name="label">
                <string>score</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>Properties</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>let:intGravity = 0</string>
            </attr>
        </edge>
        <edge to="n5" from="n4">
            <attr name="label">
                <string>finished</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>int:0</string>
            </attr>
        </edge>
    </graph>
</gxl>
