<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="startCycle">
        <node id="n1">
            <attr name="layout">
                <string>172 124 61 16</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>354 76 36 16</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>298 125 33 16</string>
            </attr>
        </node>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>token</string>
            </attr>
        </edge>
        <edge to="n0" from="n1">
            <attr name="label">
                <string>out</string>
            </attr>
            <attr name="layout">
                <string>500 0 216 146 267 208 311 147 11</string>
            </attr>
        </edge>
        <edge to="n2" from="n0">
            <attr name="label">
                <string>mark</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>place</string>
            </attr>
        </edge>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>in</string>
            </attr>
            <attr name="layout">
                <string>500 0 310 125 270 77 219 124 11</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>transition</string>
            </attr>
        </edge>
    </graph>
</gxl>
