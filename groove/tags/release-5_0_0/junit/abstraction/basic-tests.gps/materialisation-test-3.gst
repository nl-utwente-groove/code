<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n1">
            <attr name="layout">
                <string>187 315 29 32</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>182 88 29 32</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>334 140 29 32</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>330 244 29 32</string>
            </attr>
        </node>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>l</string>
            </attr>
            <attr name="layout">
                <string>500 0 197 104 139 218 202 331 12</string>
            </attr>
        </edge>
        <edge to="n2" from="n0">
            <attr name="label">
                <string>n</string>
            </attr>
        </edge>
        <edge to="n0" from="n1">
            <attr name="label">
                <string>n</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n1" from="n3">
            <attr name="label">
                <string>n</string>
            </attr>
        </edge>
        <edge to="n3" from="n2">
            <attr name="label">
                <string>n</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
    </graph>
</gxl>
