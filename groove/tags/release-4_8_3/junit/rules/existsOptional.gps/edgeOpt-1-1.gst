<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="edgeOpt-1-1">
        <node id="n1">
            <attr name="layout">
                <string>199 262 34 46</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>195 170 34 46</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>327 126 46 61</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>325 218 34 46</string>
            </attr>
        </node>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>flag:mark</string>
            </attr>
        </edge>
        <edge to="n2" from="n0">
            <attr name="label">
                <string>a</string>
            </attr>
        </edge>
        <edge to="n3" from="n0">
            <attr name="label">
                <string>a</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n3" from="n1">
            <attr name="label">
                <string>a</string>
            </attr>
        </edge>
    </graph>
</gxl>
