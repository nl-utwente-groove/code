<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>101 99 63 31</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>239 87 38 31</string>
            </attr>
        </node>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>counter</string>
            </attr>
        </edge>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>this</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>int:0</string>
            </attr>
        </edge>
    </graph>
</gxl>
