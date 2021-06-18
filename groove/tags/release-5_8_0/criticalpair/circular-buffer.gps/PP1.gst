<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="PP1">
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
                <string>196 267 38 18</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>194 156 23 18</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>286 150 34 54</string>
            </attr>
        </node>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>type:Buffer</string>
            </attr>
        </edge>
        <edge from="n1" to="n3">
            <attr name="label">
                <string>last</string>
            </attr>
        </edge>
        <edge from="n1" to="n0">
            <attr name="label">
                <string>last</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>type:Cell</string>
            </attr>
        </edge>
        <edge from="n0" to="n3">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>type:Cell</string>
            </attr>
        </edge>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>flag:empty</string>
            </attr>
        </edge>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
    </graph>
</gxl>
