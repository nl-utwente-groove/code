<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="createNotANode-0-1">
        <node id="n4">
            <attr name="layout">
                <string>48 174 28 31</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>175 72 28 31</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>199 168 37 31</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>116 167 37 31</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>39 75 29 31</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>74 250 37 46</string>
            </attr>
        </node>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:A2</string>
            </attr>
        </edge>
        <edge to="n2" from="n3">
            <attr name="label">
                <string>aToB</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:A1</string>
            </attr>
        </edge>
        <edge to="n2" from="n1">
            <attr name="label">
                <string>aToB</string>
            </attr>
        </edge>
        <edge to="n4" from="n1">
            <attr name="label">
                <string>aToB</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n4" from="n0">
            <attr name="label">
                <string>aToB</string>
            </attr>
        </edge>
        <edge to="n2" from="n0">
            <attr name="label">
                <string>aToB</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>type:A1</string>
            </attr>
        </edge>
        <edge to="n4" from="n6">
            <attr name="label">
                <string>aToB</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>flag:a</string>
            </attr>
        </edge>
    </graph>
</gxl>
