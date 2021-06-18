<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start3">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n7">
            <attr name="layout">
                <string>180 221 59 46</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>55 218 59 46</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>61 138 39 46</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>117 218 59 46</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>193 142 39 46</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>124 139 39 46</string>
            </attr>
        </node>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>type:Secret</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:Secret</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>type:Girl</string>
            </attr>
        </edge>
        <edge to="n1" from="n6">
            <attr name="label">
                <string>knows</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:Secret</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:Girl</string>
            </attr>
        </edge>
        <edge to="n7" from="n3">
            <attr name="label">
                <string>knows</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:Girl</string>
            </attr>
        </edge>
        <edge to="n4" from="n0">
            <attr name="label">
                <string>knows</string>
            </attr>
        </edge>
    </graph>
</gxl>
