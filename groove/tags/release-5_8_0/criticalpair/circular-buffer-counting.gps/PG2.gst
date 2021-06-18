<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="PG2">
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
        <node id="n0">
            <attr name="layout">
                <string>328 210 23 18</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>444 142 14 18</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>414 217 23 18</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>322 146 38 18</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>432 106 40 18</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>324 61 23 18</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>448 63 23 18</string>
            </attr>
        </node>
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
        <edge from="n1" to="n1">
            <attr name="label">
                <string>int:88</string>
            </attr>
        </edge>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>type:Cell</string>
            </attr>
        </edge>
        <edge from="n4" to="n4">
            <attr name="label">
                <string>type:Buffer</string>
            </attr>
        </edge>
        <edge from="n4" to="n1">
            <attr name="label">
                <string>empty</string>
            </attr>
        </edge>
        <edge from="n4" to="n0">
            <attr name="label">
                <string>last</string>
            </attr>
            <attr name="layout">
                <string>350 0 339 167 339 207 11</string>
            </attr>
        </edge>
        <edge from="n4" to="n2">
            <attr name="label">
                <string>first</string>
            </attr>
        </edge>
        <edge from="n5" to="n5">
            <attr name="label">
                <string>type:Object</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>type:Cell</string>
            </attr>
        </edge>
        <edge from="n2" to="n5">
            <attr name="label">
                <string>val</string>
            </attr>
        </edge>
        <edge from="n2" to="n6">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n6" to="n6">
            <attr name="label">
                <string>type:Cell</string>
            </attr>
        </edge>
    </graph>
</gxl>
