<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start-violates-in">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>129 62 14 30</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>128 151 14 30</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>258 75 14 30</string>
            </attr>
        </node>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>y</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n1" from="n3">
            <attr name="label">
                <string>y</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
    </graph>
</gxl>
