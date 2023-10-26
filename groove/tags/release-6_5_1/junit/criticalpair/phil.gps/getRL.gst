<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="getRL">
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
        <node id="n1">
            <attr name="layout">
                <string>184 220 48 54</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>197 97 27 36</string>
            </attr>
        </node>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>type:Phil</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>flag:hasLeft</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>flag:hungry</string>
            </attr>
        </edge>
        <edge from="n1" to="n0">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge from="n1" to="n0">
            <attr name="label">
                <string>right</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>flag:free</string>
            </attr>
        </edge>
    </graph>
</gxl>
