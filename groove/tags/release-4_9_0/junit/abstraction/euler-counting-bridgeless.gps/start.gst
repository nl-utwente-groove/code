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
                <string>72 127 48 31</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>70 41 50 31</string>
            </attr>
        </node>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>flag:start</string>
            </attr>
        </edge>
        <edge to="n0" from="n1">
            <attr name="label">
                <string>here</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:Area</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:Euler</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>flag:even</string>
            </attr>
        </edge>
    </graph>
</gxl>
