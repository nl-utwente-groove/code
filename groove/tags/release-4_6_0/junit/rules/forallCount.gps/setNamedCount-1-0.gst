<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="setNamedCount-1-0">
        <node id="n0">
            <attr name="layout">
                <string>95 63 46 46</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>158 62 46 46</string>
            </attr>
        </node>
        <node id="n5"/>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>flag:mark</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>flag:mark</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>let:count = 2</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>type:Count</string>
            </attr>
        </edge>
    </graph>
</gxl>
