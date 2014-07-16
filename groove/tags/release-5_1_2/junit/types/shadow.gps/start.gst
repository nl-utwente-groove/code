<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n1">
            <attr name="layout">
                <string>88 191 37 31</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>89 104 36 31</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>213 192 36 31</string>
            </attr>
        </node>
        <edge to="n2" from="n1">
            <attr name="label">
                <string>a</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:A1</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:C1</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:B1</string>
            </attr>
        </edge>
    </graph>
</gxl>
