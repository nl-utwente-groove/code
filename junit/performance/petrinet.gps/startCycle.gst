<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="startCycle">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>298 125 33 16</string>
            </attr>
        </node>
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
        <edge from="n0" to="n2">
            <attr name="label">
                <string>mark</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>place</string>
            </attr>
        </edge>
        <edge from="n0" to="n1">
            <attr name="label">
                <string>in</string>
            </attr>
            <attr name="layout">
                <string>500 0 310 125 270 77 219 124 11</string>
            </attr>
        </edge>
        <edge from="n1" to="n0">
            <attr name="label">
                <string>out</string>
            </attr>
            <attr name="layout">
                <string>500 0 202 132 267 208 314 133 11</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>transition</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>token</string>
            </attr>
        </edge>
    </graph>
</gxl>
