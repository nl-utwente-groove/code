<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <node id="n1">
            <attr name="layout">
                <string>136 102 15 15</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>215 220 15 15</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>270 102 15 15</string>
            </attr>
        </node>
        <edge to="n2" from="n0">
            <attr name="label">
                <string>b</string>
            </attr>
        </edge>
        <edge to="n0" from="n1">
            <attr name="label">
                <string>a</string>
            </attr>
        </edge>
        <edge to="n1" from="n2">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
    </graph>
</gxl>
