<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="eraseOverlap-0">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n1">
            <attr name="layout">
                <string>155 177 29 31</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>251 174 28 31</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>151 107 29 31</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>243 106 28 31</string>
            </attr>
        </node>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n3" from="n0">
            <attr name="label">
                <string>a</string>
            </attr>
        </edge>
        <edge to="n2" from="n1">
            <attr name="label">
                <string>a</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
    </graph>
</gxl>
