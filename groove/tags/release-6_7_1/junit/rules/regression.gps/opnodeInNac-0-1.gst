<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="opnodeInNac-0-1">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n2">
            <attr name="layout">
                <string>210 102 30 30</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>287 105 30 15</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>123 99 30 30</string>
            </attr>
        </node>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>flag:mark</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>let:x = 1</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>let:x = 0</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>let:x = 2</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>flag:mark</string>
            </attr>
        </edge>
    </graph>
</gxl>
