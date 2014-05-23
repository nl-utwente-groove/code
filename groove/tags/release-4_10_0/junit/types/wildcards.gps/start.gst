<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <attr name="transitionLabel">
            <string></string>
        </attr>
        <attr name="enabled">
            <string>true</string>
        </attr>
        <attr name="priority">
            <string>0</string>
        </attr>
        <attr name="printFormat">
            <string></string>
        </attr>
        <attr name="remark">
            <string></string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n4">
            <attr name="layout">
                <string>209 301 17 15</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>59 106 9 15</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>195 106 8 15</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>114 205 17 45</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>250 201 9 15</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>279 109 19 19</string>
            </attr>
        </node>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:A1</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>aToB</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:A2</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>flag:a</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>flag:a2</string>
            </attr>
        </edge>
        <edge to="n1" from="n2">
            <attr name="label">
                <string>a2ToB</string>
            </attr>
        </edge>
        <edge to="n1" from="n2">
            <attr name="label">
                <string>aToB</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>type:Aalt</string>
            </attr>
        </edge>
    </graph>
</gxl>
